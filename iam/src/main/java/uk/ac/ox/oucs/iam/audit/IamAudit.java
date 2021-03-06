package uk.ac.ox.oucs.iam.audit;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;


/**
 * Audit class
 * @author dave
 *
 */
public class IamAudit {
	Logger log = Logger.getLogger(IamAudit.class);
	
	public IamAudit() {
		URL url = Loader.getResource("log4j.xml");
		DOMConfigurator.configure(url);
	}
	
	/**
	 * Always audit message
	 * @param message the message to audit
	 */
	public void auditAlways(String message) {
		log.fatal(message);
	}
	
	/**
	 * Only audits this message is the settings require it. Use this for environments
	 * where some messages (i.e. these) may be switched off.
	 * @param message the message to audit
	 */
	public void auditSometimes(String message) {
		log.info(message);
	}
}
