package uk.ac.ox.oucs.iam.interfaces.roles;

import java.io.IOException;
import java.net.URL;

public class ProjectAuthentication  {
	private RolePoster rolePoster;

	public ProjectAuthentication(URL url) {
		rolePoster = new RolePoster(url);
	}


	/**
	 * Check if the user with the defined role is allowed to alter the roles of other users
	 * within a project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToAlterOtherUsersRole(String role) throws IOException {
		String result = rolePoster.sendPost("isAllowedToAlterOtherUsersRole=" + role);
		return (result.startsWith("true"));
	}
	
	
	/**
	 * Check if the user with the defined role is allowed to view project data
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isViewProjectData(String role) throws IOException {
		String result = rolePoster.sendPost("isViewProjectData=" + role);
		return (result.startsWith("true"));
	}
	
	/**
	 * Check if the user with the defined role is allowed to create public views of project data
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isCreatePublicViewsOfData(String role) throws IOException {
		String result = rolePoster.sendPost("isCreatePublicViewsOfData=" + role);
		return (result.startsWith("true"));
	}
	
	
	/**
	 * Check if the user with the defined role is allowed to add other users
	 * to or from a project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToAddOthersForProject(String role) throws IOException {
		String result = rolePoster.sendPost("isAllowedToAddOthersForProject=" + role);
		System.out.println("Result:" + result);
		return (result.startsWith("true"));
	}
	
	
	/**
	 * Check if the user with the defined role is allowed to remove other users
	 * to or from a project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToRemoveOthersForProject(String role) throws IOException {
		String result = rolePoster.sendPost("isAllowedToRemoveOthersForProject=" + role);
		return (result.startsWith("true"));
	}
	
	
	/**
	 * Check if the user with the defined role is allowed to create a database against the project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToCreateDatabaseByRole(String role) throws IOException {
		String result = rolePoster.sendPost("isAllowedToCreateDatabaseByRole=" + role);
		return (result.startsWith("true"));
	}
	
	
	/**
	 * Check if the user with the defined role is allowed to edit a database against the project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToEditDatabaseByRole(String role) throws IOException {
		String result = rolePoster.sendPost("isAllowedToEditDatabaseByRole=" + role);
		return (result.startsWith("true"));
	}
	
	/**
	 * Check if the user with the defined role is allowed to delete a database against the project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToDeleteDatabaseByRole(String role) throws IOException {
		String result = rolePoster.sendPost("isAllowedToDeleteDatabaseByRole=" + role);
		return (result.startsWith("true"));
	}
	
	
	/**
	 * Check if the user with the defined role is allowed to remove the project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToRemoveProject(String role) throws IOException {
		String result = rolePoster.sendPost("isAllowedToRemoveProjectByRole=" + role);
		return (result.startsWith("true"));
	}
	
	/**
	 * Check if the user with the defined role is allowed to edit the project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true if the user is allowed to, else false
	 * @throws IOException
	 */
	public boolean isAllowedToEditProject(String role) throws IOException {
		String result = rolePoster.sendPost("isAllowedToEditProjectByRole=" + role);
		return (result.startsWith("true"));
	}
}
