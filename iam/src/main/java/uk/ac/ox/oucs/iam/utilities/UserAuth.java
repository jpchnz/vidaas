package uk.ac.ox.oucs.iam.utilities;

public class UserAuth {
	/*
	 * The following two variables must be in sync
	 */
	protected static String[] rolesAvailable = { "Owner", "Project Administrator", "Contributor", "Viewer" };

	protected static enum RolesAvailableEnum {
		Owner, ProjectAdministrator, Contributor, Viewer
	}; // NOTE: Viewer must be the last entry

	public static enum FunctonsAvailableEnum {
		IsOwner, AddProject, DeleteProject, AddUserToProject,
		RemoveUserFromProject, EditUsersRightsForProject,
		CreateDB, EditDB, DeleteDB, AddRemoveEditDataInDB,
		CreateWebApp, DeleteWebApp, ViewProjectData, CreatePublicViewsOfData
	};

	public String[] getRolesAvailable() {
		return rolesAvailable;
	}

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
