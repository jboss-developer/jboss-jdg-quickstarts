package org.jboss.as.quickstarts.datagrid.spring.session;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infinispan.commons.api.BasicCache;
import org.infinispan.spring.provider.SpringEmbeddedCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

@WebServlet("/session")
public class SessionServlet extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      BasicCache sessionCache = getSessionCache();
      PrintWriter writer = resp.getWriter();
      String sessionId = req.getSession(true).getId();
      resp.getWriter().println("Creating sessions: " + sessionId);
      resp.getWriter().println("Active Sessions: " + sessionCache.keySet());
      writer.close();
   }

   private BasicCache getSessionCache() {
      ApplicationContext ac = (ApplicationContext) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
      SpringEmbeddedCacheManager cacheManager = ac.getBean(SpringEmbeddedCacheManager.class);
      return cacheManager.getCache("sessions").getNativeCache();
   }
}
