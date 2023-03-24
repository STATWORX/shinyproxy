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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pbi")
public class PbiProperties {
    private Map<String, Object> defaults;
    
	private Map<String, Dashboard> dashboards = new HashMap<>();

    public Map<String, Object> getDefaults() {
        return defaults;
    }

    public void setDefaults(Map<String, Object> defaults) {
        this.defaults = defaults;
    }

    public Map<String, Dashboard> getDashboards() {
        return dashboards;
    }

    public void setDashboards(List<Dashboard> dashboards) {
        this.dashboards.clear();
        for (Dashboard dashboard : dashboards) {
            this.dashboards.put(dashboard.getId(), dashboard);
        }
    }

	public Dashboard getDashboard(String dashId) {
        return dashboards.get(dashId);
    }


	public static class Defaults {

        private String tenantId;

        private String clientId;

        private String secret;

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    public static class Dashboard {

        private String id;

        private String displayName;

        private String description;

        private String reportId;

        private String groupId;

		private List<String> accessGroups;

		
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getReportId() {
            return reportId;
        }

        public void setReportId(String reportId) {
            this.reportId = reportId;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

		public List<String> getAccessGroups() {
			return accessGroups;
		}
	
		public void setAccessGroups(List<String> accessGroups) {
			this.accessGroups = accessGroups;
		}
    }

}