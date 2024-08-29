/**
 * ShinyProxy
 *
 * Copyright (C) 2016-2021 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
package eu.openanalytics.shinyproxy.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.openanalytics.shinyproxy.PbiProperties;
import eu.openanalytics.shinyproxy.PbiProperties.Dashboard;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import jakarta.servlet.ServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@RestController
public class PbiTokenController extends BaseController {
	
	private final static Logger log = LogManager.getLogger(PbiTokenController.class);
	
	@Value("${TENANT_ID}")
	private String tenant_id;
	@Value("${CLIENT_ID}")
	private String client_id;
	@Value("${CLIENT_SECRET}")
	private String client_secret;

	// @Autowired
	// private Environment env;
	// private String tenant_id = env.getProperty("pbi.defaults.tenant-id");
	// private String client_id = env.getProperty("pbi.defaults.client-id");
	// private String client_secret = env.getProperty("pbi.defaults.client-secret");

	private static final String CONTENT_TYPE_WWW = "application/x-www-form-urlencoded";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String CONTENT_TYPE_JSON = "application/json";

	@Inject
	PbiProperties  pbiProperties;


	public String sendRequest(final String url, final String requestBody, final String bearerToken, final String contentType, final String httpMethod) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(httpMethod);
			con.setRequestProperty("Content-Type", contentType);
			if (bearerToken != null) {
				con.setRequestProperty("Authorization", MessageFormat.format("Bearer {0}", bearerToken));
			}
			con.setDoOutput(true);
			
			byte[] postDataBytes = requestBody.getBytes("UTF-8");
			con.getOutputStream().write(postDataBytes);
	
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			log.debug(MessageFormat.format("Response status code: {0}",con.getResponseCode()));
			in.close();
	
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<String> getResults(final String response, final String tokenValueKey, final String expirationDateKey) {
		List<String> parsedResponse = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
            JsonNode rootNode = objectMapper.readTree(response);
            parsedResponse.add(rootNode.get(tokenValueKey).asText());
			parsedResponse.add(rootNode.get(expirationDateKey).asText());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
		return parsedResponse;
	}

    private List<String> getBearerToken(final String url, final String requestBody) {
		String bearerToken = null;
        log.debug("Bearer token requested");
		String response = sendRequest(url, requestBody, bearerToken, CONTENT_TYPE_WWW, HTTP_METHOD_POST);
		List<String> parsedResponse = getResults(response,"access_token","expires_in");
        return parsedResponse;
    }

			
    private List<String> getEmbedToken(final String url, final String bearerToken) {
        String requestBody = "{}";
		log.debug("Embed token requested");
        String response = sendRequest(url, requestBody, bearerToken, CONTENT_TYPE_JSON, HTTP_METHOD_POST);
		List<String> parsedResponse = getResults(response,"token","expiration");
		return parsedResponse;
    }

	@RequestMapping(value = "/generate-token/pbi/{dashId}", method = RequestMethod.GET)
	public String getBPI(@PathVariable String dashId, ServletRequest request) {
		
		// String reportId = request.getParameter("reportId");
		// String groupId = request.getParameter("groupId");
		log.info(MessageFormat.format("get token for Dashboard ID: {0}",dashId));
		
		// log.info(MessageFormat.format("Request host: {0}:{1}",request.getServerName(),String.valueOf(request.getServerPort())));

		Dashboard dashboard = null;
		Map<String, Dashboard> dashboards = pbiProperties.getDashboards();

        for (Dashboard dash: dashboards.values()){
            if (dash.getAppId().equals(dashId)){
                dashboard = dash;
            }
        }
		String reportId = dashboard.getReportId();
		String groupId = dashboard.getGroupId();
		
		// shift checks to pbi class propeties 
		if (reportId == null || reportId.isEmpty() || groupId == null || groupId.isEmpty() ) {
			log.warn("ReportID and/or GroupID are not properly set");
			return ""; // Return value tbd
		} 
		
		log.info(MessageFormat.format("Request Token for reportId {0} in groupId {1}",reportId,groupId));

		String requestBody = MessageFormat.format("client_id={0}&grant_type=client_credentials&scope=openid profile email https://analysis.windows.net/powerbi/api/.default&client_secret={1}", client_id, client_secret);

		List<String>  bearerResponse = getBearerToken(MessageFormat.format("https://login.microsoftonline.com/{0}/oauth2/v2.0/token",tenant_id),requestBody);

		List<String> embedResponse = getEmbedToken(MessageFormat.format("https://api.powerbi.com/v1.0/myorg/groups/{0}/reports/{1}/GenerateToken",groupId,reportId),bearerResponse.get(0));

		return embedResponse.get(0);

	}
}