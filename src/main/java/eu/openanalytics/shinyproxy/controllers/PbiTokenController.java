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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	private static final String CONTENT_TYPE_WWW = "application/x-www-form-urlencoded";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String CONTENT_TYPE_JSON = "application/json";

	// @Autowired
	// private Environment env;
	// private String tenant_id = env.getProperty("TENANT_ID");
	// private String client_id = env.getProperty("CLIENT_ID");
	// private String client_secret = env.getProperty("CLIENT_SECRET");


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
        log.info("Request Bearer token");
		String response = sendRequest(url, requestBody, bearerToken, CONTENT_TYPE_WWW, HTTP_METHOD_POST);
		List<String> parsedResponse = getResults(response,"expires_in","access_token");

        return parsedResponse;
    }

			
    private List<String> getEmbedToken(final String url, final String bearerToken) {
        String requestBody = "{}";
		log.info("Request Embed token");
        String response = sendRequest(url, requestBody, bearerToken, CONTENT_TYPE_JSON, HTTP_METHOD_POST);
		List<String> parsedResponse = getResults(response,"expiration","token");
		return parsedResponse;
    }


	@RequestMapping(value = "/generate-token", method = RequestMethod.GET)
	public String getBPI(ServletRequest request) {
		
		String reportId = request.getParameter("reportId");
		String groupId = request.getParameter("groupId");
		// String reportId = "d6440ccd-6cf3-46b0-a0d0-026708c46819";
		// String groupId = "f3bcc1ec-3b25-463e-b026-12b0d37b1e1b";
		log.info(MessageFormat.format("Request Token for reportId {0} in groupId {1}",reportId,groupId));

		String requestBody = MessageFormat.format("client_id={0}&grant_type=client_credentials&scope=openid profile email https://analysis.windows.net/powerbi/api/.default&client_secret={1}", client_id, client_secret);

		List<String>  bearerResponse = getBearerToken(MessageFormat.format("https://login.microsoftonline.com/{0}/oauth2/v2.0/token",tenant_id),requestBody);
		
		List<String> embedResponse = getEmbedToken(MessageFormat.format("https://api.powerbi.com/v1.0/myorg/groups/{0}/reports/{1}/GenerateToken",groupId,reportId),bearerResponse.get(1));

		return embedResponse.get(1);

	}
}