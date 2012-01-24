package uk.ac.ox.oucs.iam.roles;

import java.io.IOException;

public interface IAMRoleManagerInterface {

	/**
	 * Check if the role supplied denotes the user as an owner of the project
	 * @param role the role to test
	 * @return true of the role denotes the user is owner, false otherwise
	 * @throws IOException
	 */
	public abstract boolean checkIsOwner(String role) throws IOException;

	/**
	 * Check if the user with the defined role is allowed to create a database against the project
	 * @param role the role to test
	 * @return true if the user is allowed to create a database in the project, else false
	 * @throws IOException
	 */
	public abstract boolean checkIsAllowedToCreateDatabaseByRole(String role)
			throws IOException;
	
	/**
	 * Check if the user with the defined role is allowed to alter the roles of other users
	 * within a project, or remove them from the project altogether
	 * @param role the role to test
	 * @return true if the user is allowed to create a database in the project, else false
	 * @throws IOException
	 */
	public abstract boolean checkIsAllowedToAlterOtherUsersRole(String role)
			throws IOException;

	/**
	 * Get a list of all roles available
	 * @return A String object containing all roles, each separated by a newline
	 * @throws IOException
	 */
	public abstract String getRoles() throws IOException;

}