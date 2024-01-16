package eu.openanalytics.shinyproxy;

import eu.openanalytics.containerproxy.auth.IAuthenticationBackend;
import eu.openanalytics.containerproxy.service.UserService;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class PbiAccessControlService {
   private static final Logger log = LogManager.getLogger(PbiAccessControlService.class);
   private final IAuthenticationBackend authBackend;
   private final UserService userService;
   private final PbiProperties pbiProperties;

   public PbiAccessControlService(@Lazy IAuthenticationBackend authBackend, UserService userService, PbiProperties pbiProperties) {
      this.authBackend = authBackend;
      this.userService = userService;
      this.pbiProperties = pbiProperties;
   }


   public boolean canAccessDashboard(Authentication authentication, String dashId) {
      log.info(MessageFormat.format("Dashboard ID: {0}", dashId));
      log.info(MessageFormat.format("User groups: {0}", this.userService.getGroups().toString()));
      PbiProperties.Dashboard dashboard = this.pbiProperties.getDashboard(dashId); 
      if (dashboard == null) {
         log.warn("Requested Dashboard not found");
         return false;
      } else if (authentication == null) {
         log.warn("Authentication method not found");
         return false;
      } else if (authentication instanceof AnonymousAuthenticationToken) {
         return !this.authBackend.hasAuthorization();
      } else {
         List<String> accessGroups = dashboard.getAccessGroups();
         if (accessGroups != null && !accessGroups.isEmpty()) {
            Iterator<String> var5 = accessGroups.iterator();

            String group;
            do {
               if (!var5.hasNext()) {
                  log.warn("No permissions could have been matched");
                  return false;
               }

               group = (String)var5.next();
            } while(!this.userService.isMember(authentication, group));

            return true;
         } else {
            log.warn("Access groups not defined for dashboard");
            return false;
         }
      }
   }
}
