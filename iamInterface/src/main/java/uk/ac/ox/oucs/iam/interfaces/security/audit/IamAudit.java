package uk.ac.ox.oucs.iam.interfaces.security.audit;

import org.apache.log4j.Logger;

public class IamAudit {
	Logger log = Logger.getLogger(IamAudit.class);
	
//	public IamAudit() {
//		DOMConfigurator.configure("log4j.xml");
//	}
	
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
