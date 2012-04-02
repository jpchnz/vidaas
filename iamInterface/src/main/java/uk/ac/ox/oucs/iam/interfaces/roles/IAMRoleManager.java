package uk.ac.ox.oucs.iam.interfaces.roles;

import java.io.IOException;
import java.net.MalformedURLException;


/**
 * The gateway to the role management functions.
 * 
 * @author dave
 *
 */
public class IAMRoleManager {
	private RolePoster rolePoster;	
	private DatabaseAuthentication databaseAuthentication;
	private WebAppAuthentication webAppAuthentication;
	private ProjectAuthentication projectAuthentication;
	
	
	private static IAMRoleManager instance = null;

	/**
	 * Provides an instance of the IAMRoleManager class, instantiating it
	 * if it has not yet been created.
	 * @return
	 * @throws MalformedURLException
	 */
	public synchronized static IAMRoleManager getInstance() throws MalformedURLException {
		if (instance == null) {
			instance = new IAMRoleManager();
		}
		return instance;
	}
	
	
	/**
	 * Testing only - create a Role Manager object to communicate with
	 * "http://localhost:8080/iam/ProjectRoleServlet"
	 * 
	 * @throws MalformedURLException 
	 */
	private IAMRoleManager() throws MalformedURLException {
		init();
	}

	
	private void init() throws MalformedURLException {
		rolePoster = RolePoster.getInstance();
		databaseAuthentication = new DatabaseAuthentication();
		webAppAuthentication = new WebAppAuthentication();
		projectAuthentication = new ProjectAuthentication();
	}
	
	
	
	
	
	/**
	 * Get a list of all roles available
	 * @return A String object containing all roles, each separated by a newline
	 * @throws IOException
	 */
	public String getRoles() throws IOException {
		String ret = rolePoster.sendPost("getRoles=true");
		if (ret.compareTo("true") == 0) {
			ret = "Owner\nSystem Administrator\nContributor\nViewer";
		}
		return ret;
	}
	
	
	/**
	 * Allows the caller to deretmine which role has been designated project owner
	 * @return the name of the project owner role - typically "Owner"
	 * @throws IOException
	 */
	public String getOwnerRole() throws IOException {
		String ret =  rolePoster.sendPost("getOwnerRole=true").replaceAll("\n", "");
		if (ret.compareTo("true") == 0) {
			ret = "Owner";
		}
		return ret;
	}
	
	/**
	 * Get a list of all roles available with the exception of the owner role
	 * @return A String object containing all roles, each separated by a newline
	 * @throws IOException
	 */
	public String getRolesWithoutOwner() throws IOException {
		String ret =  rolePoster.sendPost("getRolesWithoutOwner=true");
		if (ret.compareTo("true") == 0) {
			ret = "System Administrator\nContributor\nViewer";
		}
		return ret;
	}
	
	
	
	
	/**
	 * Check if the role supplied denotes the user as an owner of the project.
	 * 
	 * Note. This is back end processing. It assumes the project is known by the caller.
	 * So the caller can simply get the relevant data string from the database for the project and
	 * verify its value against this function.
	 * @param role the role to test
	 * @return true of the role denotes the user is owner, false otherwise
	 * @throws IOException
	 */
	public boolean isOwner(String role) throws IOException {
		String ret = rolePoster.sendPost("isOwner=" + role);
		if (ret.compareTo("true") == 0) {
			return true;
		}
		return (ret.startsWith("true"));
	}
	
	
	
	
	
	public static void main(String[] args) {
		try {
			IAMRoleManager iamPost = new IAMRoleManager();
			
			System.out.println("All roles are:" + iamPost.getRoles());
			System.out.println("Checking is owner ...");
			System.out.println(iamPost.isOwner("OWNER"));
			System.out.println(iamPost.isOwner("Admin"));
			System.out.println("Checking is owner allowed to create project ...");
			System.out.println(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("owner"));
			System.out.println("Checking is admin allowed to create project ...");
			System.out.println(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("Project Administrator"));
			System.out.println("Checking is contributor allowed to create project ...");
			System.out.println(iamPost.getProjectAuthentication().isAllowedToCreateDatabaseByRole("contributor"));
			
			System.out.println("Checking is contributor allowed to create webapp ...");
			System.out.println(iamPost.getWebAppAuthentication().isAllowedToCreateWebAppByRole("contributor"));
			System.out.println("Checking is contributor allowed to delete webapp ...");
			System.out.println(iamPost.getWebAppAuthentication().isAllowedToDeleteWebAppByRole("contributor"));

//			System.out.println(iamPost.getRoles());
			System.out.println("Finished!");
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public WebAppAuthentication getWebAppAuthentication() {
		return webAppAuthentication;
	}
	public DatabaseAuthentication getDatabaseAuthentication() {
		return databaseAuthentication;
	}
	public ProjectAuthentication getProjectAuthentication() {
		return projectAuthentication;
	}
}
