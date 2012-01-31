package uk.ac.ox.oucs.iam;




/**
 * A series of routines to help with access management features of VIDaaS. 
 * @author oucs0153
 *
 */
public class UserRoles {
	private enum RolesInOrderOfPower {OWNER, ADMIN, CONTRUBUTOR, VIEWER};
	private static String [] rolesAvailable = {"Owner", "Project Administrator", "Contributor", "Viewer"};
	
	
	
	/**
	 * Return a list of all roles available
	 * @return
	 */
	public static String[] getRolesAsArray() {
		return rolesAvailable;
	}
	
	
	/**
	 * Routine to determine if the role denotes owner
	 * @param usersRole of user to query
	 * @return true if owner, else false
	 */
	public static boolean isOwner(String usersRole) {
		return usersRole.compareToIgnoreCase(rolesAvailable[RolesInOrderOfPower.OWNER.ordinal()]) == 0;
	}
	
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * add a database to the current project
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public static boolean isAllowedToCreateDatabaseByRole(String usersRole) {
		boolean ret = false;
		if ( (usersRole.compareToIgnoreCase(rolesAvailable[RolesInOrderOfPower.ADMIN.ordinal()]) == 0) ||
				(usersRole.compareToIgnoreCase(rolesAvailable[RolesInOrderOfPower.OWNER.ordinal()]) == 0) ) {
			ret = true;
		}
		else if (usersRole.equalsIgnoreCase("admin")) {
			/*
			 * TODO
			 * This has been left in for backwards compatibility with current database entries and should be 
			 * removed once that compatibilty is no longer required
			 */
			ret = true;
		}
		return ret;
	}
}


