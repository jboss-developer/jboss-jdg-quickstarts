package org.jboss.as.quickstarts.datagrid.spring.session.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.infinispan.spring.embedded.provider.SpringEmbeddedCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionsController {

   @Autowired
   SpringEmbeddedCacheManager cacheManager;

   @RequestMapping("/sessions")
   public Map<String, String> session(HttpServletRequest request) {
      Map<String, String> result = new HashMap<>();
      String sessionId = request.getSession(true).getId();
      result.put("created:", sessionId);
      // By default Infinispan integration for Spring Session will use 'sessions' cache.
      result.put("active:", cacheManager.getCache("sessions").getNativeCache().keySet().toString());
      return result;
   }
}
