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
package eu.openanalytics.shinyproxy;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import eu.openanalytics.containerproxy.auth.IAuthenticationBackend;
import eu.openanalytics.containerproxy.service.UserService;
import eu.openanalytics.shinyproxy.PbiProperties.Dashboard;
import eu.openanalytics.containerproxy.model.spec.ProxySpec;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class PbiAccessControlService {
    private final static Logger log = LogManager.getLogger(PbiAccessControlService.class);

    private final IAuthenticationBackend authBackend;

    private final UserService userService;

    private final PbiProperties pbiProperties;
    
    public PbiAccessControlService(@Lazy IAuthenticationBackend authBackend, UserService userService,  PbiProperties pbiProperties) {
        this.authBackend = authBackend;
        this.userService = userService;
        this.pbiProperties = pbiProperties;
    }

    public boolean canAccessDashboard(Authentication authentication, String dashId) {
        // Check if the authenticated user has access to the dashboard with the given dashId
        // Return true if the user has access, false otherwise
        log.debug(MessageFormat.format("Dashboard ID: {0}",dashId));
        log.debug(MessageFormat.format("User´s group: {0}",Arrays.toString(userService.getGroups())));
        
        Dashboard dashboard = pbiProperties.getDashboard(dashId);
        
        if (authentication == null || dashboard == null) {
            log.warn("Dashboard or authentication method not found");
            return false;
        }

        if (authentication instanceof AnonymousAuthenticationToken) {
            // if anonymous -> only allow access if we the backend has no authorization enabled
            return !authBackend.hasAuthorization();
        }
        
        List<String> accessGroups = dashboard.getAccessGroups();
        if (accessGroups == null || accessGroups.isEmpty()) {
            log.warn("Access groups not defined for dashboard");
            return false;
        }

        for (String group : accessGroups) {
            if (userService.isMember(authentication, group)) {
                return true;
            }
        }

        log.warn("No permissions could have been matched");
        return false;

    }

}
