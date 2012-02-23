package uk.ac.ox.oucs.iam.interfaces.roles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;



public class IAMRoleManager {
	private RolePoster rolePoster;	
	private DatabaseAuthentication databaseAuthentication;
	private WebAppAuthentication webAppAuthentication;
	private ProjectAuthentication projectAuthentication;
	
	
	private static IAMRoleManager instance = null;

	public static IAMRoleManager getInstance() throws MalformedURLException {
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
		init(new URL(SystemVars.ADDRESS_OF_IAM_WEBAPP));
	}
	private IAMRoleManager(String urlString) throws MalformedURLException {
		init(new URL(urlString));
	}
	private IAMRoleManager(URL url) throws Exception {
		init(url);
	}
	
	private void init(URL url) {
		rolePoster = new RolePoster(url);
		databaseAuthentication = new DatabaseAuthentication(url);
		webAppAuthentication = new WebAppAuthentication(url);
		projectAuthentication = new ProjectAuthentication(url);
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
	
	
	public String getOwnerRole() throws IOException {
		String ret =  rolePoster.sendPost("getOwnerRole=true");
		System.out.println("TTTTT:" + ret + ":");
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