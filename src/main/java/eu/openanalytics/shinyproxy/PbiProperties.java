package eu.openanalytics.shinyproxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
   prefix = "pbi"
)
public class PbiProperties {
   private PbiProperties.Defaults defaults;
   private Map<String, PbiProperties.Dashboard> dashboards = new LinkedHashMap();

   public PbiProperties.Defaults getDefaults() {
      return this.defaults;
   }

   public void setDefaults(PbiProperties.Defaults defaults) {
      this.defaults = defaults;
   }

   public static class Defaults {
      private String principalId;
      private String principalSecret;
      private String tenantId;

      public String getPrincipalId() {
         return this.principalId;
      }

      public void setPrincipalId(String principalId) {
         this.principalId = principalId;
      }

      public String getPrincipalSecret() {
         return this.principalSecret;
      }

      public void setPrincipalSecret(String principalSecret) {
         this.principalSecret = principalSecret;
      }

      public String getTenantId() {
         return this.tenantId;
      }

      public void setTenantId(String tenantId) {
         this.tenantId = tenantId;
      }
   }

   public Map<String, PbiProperties.Dashboard> getDashboards() {
      return this.dashboards;
   }

   @PostConstruct
   private void initializeAndResolveDefaults() {
      if (defaults == null || dashboards == null) {
         return;
     }

      String defaultTenantId = defaults.getTenantId();
      String defaultPrincipalId = defaults.getPrincipalId();
      String defaultPrincipalSecret = defaults.getPrincipalSecret();
      List<PbiProperties.Dashboard> dashboardList = new ArrayList<>(dashboards.values());
      for (PbiProperties.Dashboard dashboard : dashboardList) {
         if (dashboard.getTenantId() == null) {
            dashboard.setTenantId(defaultTenantId);
         }
         if (dashboard.getPrincipalId() == null) {
            dashboard.setPrincipalId(defaultPrincipalId);
         }
         if (dashboard.getPrincipalSecret() == null) {
            dashboard.setPrincipalSecret(defaultPrincipalSecret);
         }
      }
   }


   public void setDashboards(List<PbiProperties.Dashboard> dashboards) {
      this.dashboards.clear();
      Iterator var2 = dashboards.iterator();

      while(var2.hasNext()) {
         PbiProperties.Dashboard dashboard = (PbiProperties.Dashboard)var2.next();
         // dashboard.validate();
         this.dashboards.put(dashboard.getAppId(), dashboard);
      }

   }

   public PbiProperties.Dashboard getDashboard(String dashId) {
      return (PbiProperties.Dashboard)this.dashboards.get(dashId);
   }

   public static class Dashboard {
      private String appId;
      private String displayName;
      private String description;
      private String logoUrl;
      private String reportId;
      private String groupId;
      private String primaryApp;
      private List<String> accessGroups;
      private Boolean isCdck;
      private Boolean isHidden;
      private Boolean hasAccess;
      private String principalId;
      private String principalSecret;
      private String tenantId;
      private PbiProperties pbiProperties;

      public void validate() {
         String[] mandatoryFields = new String[]{this.appId, this.reportId, this.groupId};
         String[] var2 = mandatoryFields;
         int var3 = mandatoryFields.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String field = var2[var4];
            if (field == null || field.isEmpty()) {
               throw new IllegalArgumentException("Incomplete dashboard [" + this.appId + "]: A mandatory field is missing.");
            }
         }

      }

      public String getAppId() {
         return this.appId;
      }

      public void setAppId(String appId) {
         if (appId == null) {
            throw new IllegalArgumentException("appId cannot be null.");
         } else {
            this.appId = appId;
         }
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public void setDisplayName(String displayName) {
         this.displayName = displayName;
      }

      public String getDescription() {
         return this.description;
      }

      public void setDescription(String description) {
         this.description = description;
      }

      public String getLogoUrl() {
         return this.logoUrl;
      }

      public void setLogoUrl(String logoUrl) {
         this.logoUrl = logoUrl;
      }

      public String getReportId() {
         return this.reportId;
      }

      public void setReportId(String reportId) {
         this.reportId = reportId;
      }

      public String getGroupId() {
         return this.groupId;
      }

      public void setGroupId(String groupId) {
         this.groupId = groupId;
      }

      public List<String> getAccessGroups() {
         return this.accessGroups;
      }

      public void setAccessGroups(List<String> accessGroups) {
         this.accessGroups = accessGroups;
      }

      public Boolean getIsCdck() {
         return this.isCdck;
      }

      public void setIsCdck(Boolean isCdck) {
         this.isCdck = isCdck;
      }

      public String getPrimaryApp() {
         return this.primaryApp;
      }

      public void setPrimaryApp(String primaryApp) {
         this.primaryApp = primaryApp;
      }

      public Boolean getIsHidden() {
         return this.isHidden;
      }

      public void setIsHidden(Boolean isHidden) {
         this.isHidden = isHidden;
      }

      public Boolean getHasAccess() {
         return this.hasAccess;
      }

      public void setHasAccess(Boolean hasAccess) {
         this.hasAccess = hasAccess;
      }

      public String getPrincipalId() {
         return this.principalId;
      }

      public void setPrincipalId(String principalId) {
         this.principalId = principalId;
      }

   //    public String getPrincipalSecret() {
   //       if (this.principalSecret == null) {
   //          Map<String, Object> defaultValues = pbiProperties.getDefaults();
   //          return defaultValues.get("principalSecret").toString();
   //       }
   //       return this.principalSecret;
   //   }

      public String getPrincipalSecret() {
         return this.principalSecret;
      }

      public void setPrincipalSecret(String principalSecret) {
         this.principalSecret = principalSecret;
      }

      public String getTenantId() {
         return this.tenantId;
      }

      public void setTenantId(String tenantId) {
         this.tenantId = tenantId;
      }

   }
    
}
