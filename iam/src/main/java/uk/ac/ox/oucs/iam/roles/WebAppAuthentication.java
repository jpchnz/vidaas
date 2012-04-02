package uk.ac.ox.oucs.iam.roles;

import java.io.IOException;
import java.net.URL;


/**
 * A class to process role based queries for web application operations.
 * Currently, the only operations here are to create or delete web applications.
 * @author dave
 *
 */
public class WebAppAuthentication  {
	private RolePoster rolePoster;

	public WebAppAuthentication(URL url) {
		rolePoster = new RolePoster(url);
	}

	/**
	 * Check if the user with the defined role is allowed to create a webapp for a project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToCreateWebAppByRole(String role) throws IOException {
		return rolePoster.sendPost("isAllowedToCreateWebAppByRole=" + role).startsWith("true");
	}
	
	/**
	 * Check if the user with the defined role is allowed to delete a webapp for a project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToDeleteWebAppByRole(String role) throws IOException {
		return rolePoster.sendPost("isAllowedToDeleteWebAppByRole=" + role).startsWith("true");
	}
}
