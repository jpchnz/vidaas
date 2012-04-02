package uk.ac.ox.oucs.iam.interfaces.roles;

import java.io.IOException;
import java.net.MalformedURLException;


/**
 * A class to process role based queries for database operations. 
 * Currently, the only database operation is to add or remove database data.
 * 
 * @author dave
 *
 */
public class DatabaseAuthentication {
	private RolePoster rolePoster;

	public DatabaseAuthentication() throws MalformedURLException {
		rolePoster = RolePoster.getInstance();
	}

	/**
	 * Check if the user with the defined role is allowed to add, edit or remove data in a database for a project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToAddEditOrRemoveDBData(String role) throws IOException {
		return rolePoster.sendPost("isAllowedToAddEditOrRemoveDatabaseDataByRole=" + role).startsWith("true");
	}
}
