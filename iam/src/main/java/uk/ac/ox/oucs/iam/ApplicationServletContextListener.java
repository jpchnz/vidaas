package uk.ac.ox.oucs.iam;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

public class ApplicationServletContextListener implements ServletContextListener {
	public void contextInitialized(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();

		String prefix = ctx.getRealPath("/");
		String file = "WEB-INF" + System.getProperty("file.separator") + "classes"
				+ System.getProperty("file.separator") + "log4j.xml";

		if (file != null) {
			DOMConfigurator.configure(prefix + file);
//			PropertyConfigurator.configure(prefix + file);
			System.out.println("Log4J Logging started for application: " + prefix + file);
		}
		else {
			System.out.println("Log4J Is not configured for application Application: " + prefix + file);
		}

	}

	public void contextDestroyed(ServletContextEvent event) {

	}

}