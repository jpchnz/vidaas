package uk.ac.ox.oucs.iam.utilities;



/**
 * This class provides key features for user authorisaton.
 * 
 * @author dave
 *
 */
public class UserAuth {
	/*
	 * The following two variables must be in sync, and the first entry needs to be the owner
	 */
	private final int ownerRoleNumber = 0; // Special
	
	/**
	 * A list of all roles available. The owner must always be the first role, as indexed by
	 * int ownerRoleNumber
	 */
	private static String[] rolesAvailable = { "Owner", "Project Administrator", "Contributor", "Viewer" };
	/**
	 * This is a convenience array that will provide a list of all roles excluding the special
	 * "owner" role. I wondered about just using "rolesAvailable" above and building the list on the fly,
	 * or merging with rolesAvailable, but decided on this approach for simplicity.
	 */
	private static String[] rolesAvailableExcludeOwner = { "Project Administrator", "Contributor", "Viewer" };

	/**
	 * A convenience helper enum. The viewer should always be the final entry in this.
	 * @author dave
	 *
	 */
	private static enum RolesAvailableEnum {
		Owner, ProjectAdministrator, Contributor, Viewer
	};

	public static enum FunctonsAvailableEnum {
		IsOwner, AddProject, DeleteProject, EditProject, AddUserToProject,
		RemoveUserFromProject, EditUsersRightsForProject,
		CreateDB, EditDB, DeleteDB, AddRemoveEditDataInDB,
		CreateWebApp, DeleteWebApp, ViewProjectData, CreatePublicViewsOfData
	};

	public String[] getRolesAvailable() {
		return rolesAvailable;
	}
	
	public String[] getRolesAvailableExcludeOwner() {
		return rolesAvailableExcludeOwner;
	}
	
	public String getOwnerRole() {
		return rolesAvailable[ownerRoleNumber];
	}

	
	/**
	 * This is the heart of the roles function. It will check whether a particular user is
	 * authorised to perform a particular function and return true or false as an answer.
	 * The role is case insensitive.
	 * 
	 * @param roleIn the role to test
	 * @param function the function the roles is testing for
	 * @return true if the role is allowed to perform the function, else false
	 */
	public boolean isAuthorised(String roleIn, FunctonsAvailableEnum function) {
		boolean ret = false;
		String role = roleIn.replaceAll(" ", "");

		switch (function) {
		case IsOwner:
			ret = role.equalsIgnoreCase(RolesAvailableEnum.Owner.toString());
			break;
			
		case DeleteProject:
			ret = role.equalsIgnoreCase(RolesAvailableEnum.Owner.toString());
			break;
			
		case CreatePublicViewsOfData:
		case AddProject:
		case AddUserToProject:
		case RemoveUserFromProject:
		case EditUsersRightsForProject:
		case EditProject:
			ret = role.equalsIgnoreCase(RolesAvailableEnum.Owner.toString())
					|| role.equalsIgnoreCase(RolesAvailableEnum.ProjectAdministrator.toString());
			break;
			
		case CreateDB:
		case EditDB:
		case DeleteDB:
		case AddRemoveEditDataInDB:
		case CreateWebApp:
		case DeleteWebApp:
			ret = role.equalsIgnoreCase(RolesAvailableEnum.Owner.toString())
					|| role.equalsIgnoreCase(RolesAvailableEnum.ProjectAdministrator.toString())
					|| role.equalsIgnoreCase(RolesAvailableEnum.Contributor.toString());
			break;

		case ViewProjectData:
			ret = role.equalsIgnoreCase(RolesAvailableEnum.Owner.toString())
					|| role.equalsIgnoreCase(RolesAvailableEnum.ProjectAdministrator.toString())
					|| role.equalsIgnoreCase(RolesAvailableEnum.Contributor.toString())
					|| role.equalsIgnoreCase(RolesAvailableEnum.Viewer.toString());
			break;

		default:
			break;
		}

		return ret;
	}
}
