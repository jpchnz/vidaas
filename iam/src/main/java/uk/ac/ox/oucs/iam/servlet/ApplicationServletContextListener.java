package uk.ac.ox.oucs.iam.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.xml.DOMConfigurator;

public class ApplicationServletContextListener implements ServletContextListener {
	public void contextInitialized(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();

		String prefix = ctx.getRealPath("/");
		String file = "WEB-INF" + System.getProperty("file.separator") + "classes"
				+ System.getProperty("file.separator") + "log4j.xml";

		DOMConfigurator.configure(prefix + file);
	}

	public void contextDestroyed(ServletContextEvent event) {

	}

}