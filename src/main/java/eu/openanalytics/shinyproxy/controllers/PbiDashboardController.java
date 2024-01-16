package eu.openanalytics.shinyproxy.controllers;

import eu.openanalytics.containerproxy.auth.IAuthenticationBackend;
import eu.openanalytics.containerproxy.event.PbiClickEvent;
import eu.openanalytics.containerproxy.service.UserService;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PbiDashboardController extends BaseController {
   private static final Logger log = LogManager.getLogger(PbiTokenController.class);
   @Inject
   UserService userService;
   @Inject
   Environment environment;
   @Inject
   IAuthenticationBackend authenticationBackend;
   @Inject
   private ApplicationEventPublisher applicationEventPublisher;

   @RequestMapping({"/pbi/{dashId}"})
   private Object pbi(@PathVariable String dashId, ModelMap map, HttpServletRequest request) {
      map.addAttribute("dashId", dashId);
      log.info(String.format("Dashboard activated [id: %s]", dashId));
      this.applicationEventPublisher.publishEvent(new PbiClickEvent(this, dashId, this.userService.getCurrentUserId()));
      return "pbi";
   }
}
