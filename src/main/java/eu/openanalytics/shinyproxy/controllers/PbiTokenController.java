package eu.openanalytics.shinyproxy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.openanalytics.shinyproxy.PbiProperties;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PbiTokenController extends BaseController {
   private static final Logger log = LogManager.getLogger(PbiTokenController.class);
   private static final String CONTENT_TYPE_WWW = "application/x-www-form-urlencoded";
   private static final String HTTP_METHOD_POST = "POST";
   private static final String CONTENT_TYPE_JSON = "application/json";
   @Inject
   PbiProperties pbiProperties;

   public String sendRequest(final String url, final String requestBody, final String bearerToken, final String contentType, final String httpMethod) {
      try {
         URL obj = new URL(url);
         HttpURLConnection con = (HttpURLConnection)obj.openConnection();
         con.setRequestMethod(httpMethod);
         con.setRequestProperty("Content-Type", contentType);
         if (bearerToken != null) {
            con.setRequestProperty("Authorization", MessageFormat.format("Bearer {0}", bearerToken));
         }

         con.setDoOutput(true);
         byte[] postDataBytes = requestBody.getBytes("UTF-8");
         con.getOutputStream().write(postDataBytes);
         BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
         StringBuilder response = new StringBuilder();

         String inputLine;
         while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
         }

         log.debug(MessageFormat.format("Response status code: {0}", con.getResponseCode()));
         in.close();
         return response.toString();
      } catch (Exception var12) {
         var12.printStackTrace();
         return null;
      }
   }

   private List<String> getResults(final String response, final String tokenValueKey, final String expirationDateKey) {
      List<String> parsedResponse = new ArrayList();
      ObjectMapper objectMapper = new ObjectMapper();

      try {
         JsonNode rootNode = objectMapper.readTree(response);
         parsedResponse.add(rootNode.get(tokenValueKey).asText());
         parsedResponse.add(rootNode.get(expirationDateKey).asText());
      } catch (JsonProcessingException var7) {
         var7.printStackTrace();
      }

      return parsedResponse;
   }

   private List<String> getBearerToken(final String url, final String requestBody) {
      String bearerToken = null;
      log.debug("Bearer token requested");
      String response = this.sendRequest(url, requestBody, (String)bearerToken, "application/x-www-form-urlencoded", "POST");
      List<String> parsedResponse = this.getResults(response, "access_token", "expires_in");
      return parsedResponse;
   }

   private List<String> getEmbedToken(final String url, final String bearerToken) {
      String requestBody = "{}";
      log.debug("Embed token requested");
      String response = this.sendRequest(url, requestBody, bearerToken, "application/json", "POST");
      List<String> parsedResponse = this.getResults(response, "token", "expiration");
      return parsedResponse;
   }

   @RequestMapping(
      value = {"/generate-token/pbi/{dashId}"},
      method = {RequestMethod.GET}
   )
   public String getBPI(@PathVariable String dashId, ServletRequest request) {
      log.debug(MessageFormat.format("Dashboard ID: {0}", dashId));
      PbiProperties.Dashboard dashboard = this.pbiProperties.getDashboard(dashId);
      String reportId = dashboard.getReportId();
      String groupId = dashboard.getGroupId();
      String principalId = dashboard.getPrincipalId();
      String principalSecret = dashboard.getPrincipalSecret();
      String tenantId = dashboard.getTenantId();
      log.info(MessageFormat.format("Request Token for reportId {0} in groupId {1}", reportId, groupId));
      String requestBody = MessageFormat.format("client_id={0}&grant_type=client_credentials&scope=openid profile email https://analysis.windows.net/powerbi/api/.default&client_secret={1}", principalId, principalSecret);
      List<String> bearerResponse = this.getBearerToken(MessageFormat.format("https://login.microsoftonline.com/{0}/oauth2/v2.0/token", tenantId), requestBody);
      List<String> embedResponse = this.getEmbedToken(MessageFormat.format("https://api.powerbi.com/v1.0/myorg/groups/{0}/reports/{1}/GenerateToken", groupId, reportId), (String)bearerResponse.get(0));
      return (String)embedResponse.get(0);
   }
}
