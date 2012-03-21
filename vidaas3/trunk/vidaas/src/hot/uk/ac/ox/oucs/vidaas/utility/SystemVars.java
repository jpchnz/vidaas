package uk.ac.ox.oucs.vidaas.utility;


/**
 * Global variables to help with system wise settings
 * 
 * @author oucs0153
 *
 */
public class SystemVars {
	public static final boolean SHOW_PASSWORD_IN_LOGS_FOR_DEBUGGING_PURPOSES = false;
		
	
	/**
	 * This should be false for the final release. It is currently
	 * required for backwards compatibility with the database, where
	 * "admin" was considered the user's role regardless of who they were.
	 */
	public static final boolean TREAT_ADMIN_AS_OWNER = true;
	public static boolean treatAdminAsOwner(String role) {
		if (role == null) {
			return false;
		}
		return role.equalsIgnoreCase("admin");
	}
	
	/*
	 * Use this flag to determine if SSO functions are used within VIDaaS
	 */
	public static final boolean USE_SSO_IF_AVAILABLE = true;
	
	
	/*
	 * Set this to have user role functions enabled.
	 * Normally this should be set to true, but is currently included
	 * so that new code won't impact other users until fully tested.
	 */
	public static final boolean USE_USER_ROLES = true;
	
	
	/**
	 * Define the possible roles associated with a user
	 * 
	 * @author oucs0153
	 *
	 */
	public enum UserRoles {OWNER("Owner"), ADMIN("Project Administrator"), CONTRUBUTOR("Contributor"), VIEWER("Viewer");
	
		private String role;
		private UserRoles(String role) {
			this.role = role;
		}
		
		public String getRole() {
			return role;
		}
	};
}
