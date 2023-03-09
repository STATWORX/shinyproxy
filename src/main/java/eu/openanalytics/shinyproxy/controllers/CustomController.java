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

import eu.openanalytics.shinyproxy.ShinyProxySpecProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomController extends BaseController {

	@Inject
	ShinyProxySpecProvider shinyProxySpecProvider;

	@RequestMapping("/tou")
    private Object tou(ModelMap map, HttpServletRequest request) {

		prepareMap(map, request);
		
		// operator specific
		map.put("operatorShowTransferMessage", operatorService.showTransferMessageOnMainPage());

		return "tou";
    }

	@RequestMapping("/dps")
    private Object dps(ModelMap map, HttpServletRequest request) {

		prepareMap(map, request);
		
		// operator specific
		map.put("operatorShowTransferMessage", operatorService.showTransferMessageOnMainPage());

		return "dps";
    }

}
