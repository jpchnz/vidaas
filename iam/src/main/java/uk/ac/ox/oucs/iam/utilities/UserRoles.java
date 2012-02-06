package uk.ac.ox.oucs.iam.utilities;

import uk.ac.ox.oucs.iam.utilities.UserAuth.FunctonsAvailableEnum;




/**
 * A series of routines to help with access management features of VIDaaS. 
 * @author oucs0153
 *
 */
public class UserRoles {
	private UserAuth userAuth = new UserAuth();
	
	
	/**
	 * Return a list of all roles available
	 * @return
	 */
	public String[] getRolesAsArray() {
		return userAuth.getRolesAvailable();
	}
	
	
	/**
	 * Routine to determine if the role denotes owner
	 * @param usersRole of user to query
	 * @return true if owner, else false
	 */
	public boolean isOwner(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.IsOwner);
	}
	
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * add a database to the current project
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public boolean isAllowedToCreateDatabaseByRole(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.CreateDB);
	}
	
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * edit a database to the current project
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public boolean isAllowedToEditDatabaseByRole(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.EditDB);
	}
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * delete a database to the current project
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public boolean isAllowedToDeleteDatabaseByRole(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.DeleteDB);
	}
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * the role of another user
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public boolean isAllowedToAlterOtherUsersRole(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.EditUsersRightsForProject);
	}
	
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * remove the current project
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public boolean isAllowedToRemoveProject(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.DeleteProject);
	}
	
	
	/**
	 * Check if the user with the defined role is allowed to add or remove other users
	 * to or from a project.
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public boolean isAllowedToAddOthersForProject(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.AddUserToProject);
	}
	
	/**
	 * Check if the user with the defined role is allowed to add or remove other users
	 * to or from a project.
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public boolean isAllowedToRemoveOthersForProject(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.RemoveUserFromProject);
	}
	
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * add, edit or delete data in a database to the current project
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public boolean isAllowedToAddEditOrRemoveDatabaseDataByRole(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.AddRemoveEditDataInDB);
	}
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * create a webapp in the current project
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public boolean isAllowedToCreateWebAppByRole(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.CreateWebApp);
	}
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * delete a webapp in the current project
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public boolean isAllowedToDeleteWebAppByRole(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.DeleteWebApp);
	}
	
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * view project data
	 * @param usersRole The role of the user
	 * @return true if the user has authority, else false
	 */
	public boolean isViewProjectData(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.ViewProjectData);
	}
	
	
	/**
	 * Check if the user with the defined role is allowed to create public views of project data
	 * @param usersRole The role of the user
	 * @return true if the user has authority, else false
	 */
	public boolean isAllowedToRemoveProjectByRole(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.DeleteProject);
	}
	
	
	/**
	 * Check if the user with the defined role is allowed to create public views of project data
	 * @param usersRole The role of the user
	 * @return true if the user has authority, else false
	 */
	public boolean isCreatePublicViewsOfData(String usersRole) {
		return userAuth.isAuthorised(usersRole,FunctonsAvailableEnum.CreatePublicViewsOfData);
	}
}


