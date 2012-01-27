package uk.ac.ox.oucs.iam.audit;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class IamAudit {
	Logger log = Logger.getLogger(IamAudit.class);
	
	public IamAudit() {
		DOMConfigurator.configure("log4j.xml");
	}
	
	public void auditFailure(String message) {
		log.fatal(message);
	}
	
	public void auditSuccess(String message) {
		log.info(message);
	}
}
