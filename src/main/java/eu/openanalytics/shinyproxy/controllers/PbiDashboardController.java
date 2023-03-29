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

import eu.openanalytics.containerproxy.auth.IAuthenticationBackend;
import eu.openanalytics.containerproxy.service.UserService;
import eu.openanalytics.shinyproxy.ShinyProxySpecProvider;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.MessageFormat;
import java.util.Arrays;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Controller
public class PbiDashboardController extends BaseController {

    private final static Logger log = LogManager.getLogger(PbiTokenController.class);
    
	@Inject
	UserService userService;
	
	@Inject
	Environment environment;

    @Inject
	IAuthenticationBackend authenticationBackend;
    
    @RequestMapping("/pbi/{dashId}")
    private Object pbi(@PathVariable String dashId, ModelMap map, HttpServletRequest request) {
    map.addAttribute("dashId", dashId);

    log.debug(MessageFormat.format("Dashboard ID: {0}",dashId));

    return "pbi"; // "http://localhost:8081/index";

    }
}
