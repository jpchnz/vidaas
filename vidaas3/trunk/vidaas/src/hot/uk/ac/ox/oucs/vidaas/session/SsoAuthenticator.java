package uk.ac.ox.oucs.vidaas.session;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jboss.seam.contexts.Contexts;

import uk.ac.ox.oucs.vidaas.utility.SystemVars;

public class SsoAuthenticator {
	private String targettedId, allDetails;
	private String email, surname;

	public SsoAuthenticator() {
	}

	public String getAllDetails() {
		setupShibbolethVariables();
		// allDetails = "\nTargeted id:" + targettedId;
		allDetails = "\nEmail:" + email;
		// allDetails += "\nSurname:" + surname;
		
		return allDetails;
	}

	public String getTargettedId() {
		setupShibbolethVariables();
		return targettedId;
	}

	public boolean isTargettedIdSet() {
		setupShibbolethVariables();
		return (!((targettedId == null) || (targettedId.length() == 0)));
	}

	/**
	 * Check the current header value of AJP_targeted-id
	 * 
	 * @return the header value, or "" if not present
	 */
	public void setupShibbolethVariables() {
		boolean printAllHeaderValues = false;

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		Map<String, String> headers = ec.getRequestHeaderMap();
		if (SystemVars.USE_SSO_IF_AVAILABLE) {
			targettedId = headers.get("AJP_targeted-id");
			if (targettedId == null) {
				targettedId = "";
			}
			email = headers.get("AJP_Shib-InetOrgPerson-mail");
			if (email == null) {
				email = "";
			}
			surname = headers.get("AJP_Shib-Person-surname");
			if (surname == null) {
				surname = "";
			}

			if (printAllHeaderValues) {
				System.out.println("Print header values");
				for (String h : headers.keySet()) {
					if ((headers.get(h) != null) && (headers.get(h).length() != 0)) {
						System.out.println("Header: " + h + " - value: <" + headers.get(h) + ">");
					}
				}
			}
		}
	}
	
	
	
	protected String getEmailAddress() {
		setupShibbolethVariables();
		return getEmail();
	}

	public String getEmail() {
		System.out.println("Returning " + email);
		return email;
	}
}
