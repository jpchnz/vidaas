package uk.ac.ox.oucs.vidaas.utility;


/**
 * A series of routines to help with access management features of VIDaaS. 
 * @author oucs0153
 *
 */
public class AccessManagement {
	/**
	 * Define the possible roles associated with a user
	 * 
	 * @author oucs0153
	 *
	 */
	public enum UserRoles {
		OWNER("Owner"), ADMIN("Project Administrator"), CONTRUBUTOR("Contributor"), VIEWER("Viewer");

		private String role;
		private UserRoles(String role) {
			this.role = role;
		}
		
		public String getRole() {
			return role;
		}
	}
	
	
	
	/**
	 * Determine if the role of the current user is suitable to allow that user to
	 * add a dataspace to the current project
	 * @param usersRole The role of the user
	 * @return true if the user has authority to add a dataspace, else false
	 */
	public static boolean isAllowedToAddDataspace(String usersRole) {
		boolean ret = false;
		if ( (usersRole.equals(UserRoles.ADMIN.getRole())) ||
				(usersRole.equals(UserRoles.OWNER.getRole())) ) {
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


