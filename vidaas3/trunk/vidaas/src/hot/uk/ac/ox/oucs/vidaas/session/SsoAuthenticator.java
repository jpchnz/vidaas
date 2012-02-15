package uk.ac.ox.oucs.vidaas.session;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import uk.ac.ox.oucs.vidaas.utility.SystemVars;


/**
 * A helper class to deal with Single Sign On authentication
 * 
 * @author oucs0153
 *
 */
public class SsoAuthenticator {
	private String targettedId, allDetails;
	private String email, surname;


	
	/**
	 * Provide details about the current environment, currently via headers set by Shibboleth
	 * 
	 * @return A String with the users email address in a format suitable for display
	 */
	public String getAllDetails() {
		setupShibbolethVariables();
		// allDetails = "\nTargeted id:" + targettedId;
		allDetails = "\nEmail:" + email;
		// allDetails += "\nSurname:" + surname;
		
		return allDetails;
	}

	
	/**
	 * Get the Shibboleth Targetted Id variable
	 * @return
	 */
	public String getTargettedId() {
		setupShibbolethVariables();
		return targettedId;
	}

	
	/**
	 * Determine if the Shibboleth Targetted Id variable has been set in the headers.
	 * if it has not been set, this will typically indicate an error, like the Apache web server
	 * has not been set up correctly. Used to determine how to display certain menu options in the web site.
	 * 
	 * @return true if the variable has been found and set, else false
	 */
	public boolean isTargettedIdSet() {
		setupShibbolethVariables();
		return (!((targettedId == null) || (targettedId.length() == 0)));
	}

	/**
	 * Check the current header value of AJP_targeted-id, email and surname variables.
	 * This should be extended to forename also.
	 * FIXME
	 * Currently, though I have been told that surname and forename are being supplied 
	 * by the IdP to my Shibboleth SP, I cannot get the information supposedly set - only
	 * the email address seems findable.   This may be an SP configuration error - or maybe the IdP 
	 * is not sending the variables.
	 * 
	 * @return
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
	
	
	/**
	 * Once SSO authenticated, the user's email address should be set in the associated
	 * Apache headers 
	 * 
	 * @return the Shibboleth email address
	 */
	protected String getEmailAddress() {
		setupShibbolethVariables();
		return getEmail();
	}

	public String getEmail() {
		System.out.println("Returning " + email);
		return email;
	}
}
