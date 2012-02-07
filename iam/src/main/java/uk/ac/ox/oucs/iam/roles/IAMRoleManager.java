package uk.ac.ox.oucs.iam.roles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class IAMRoleManager {
	private RolePoster rolePoster;	
	private DatabaseAuthentication databaseAuthentication;
	private WebAppAuthentication webAppAuthentication;
	private ProjectAuthentication projectAuthentication;
	
	
	/**
	 * Testing only - create a Role Manager object to communicate with
	 * "http://localhost:8080/iam/ProjectRoleServlet"
	 * 
	 * @throws MalformedURLException 
	 */
	protected IAMRoleManager() throws MalformedURLException {
		init(new URL("http://localhost:8081/iam/ProjectRoleServlet"));
	}
	public IAMRoleManager(String urlString) throws MalformedURLException {
		init(new URL(urlString));
	}
	public IAMRoleManager(URL url) throws Exception {
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
		return rolePoster.sendPost("getRoles=true");
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
		return (rolePoster.sendPost("isOwner=" + role).startsWith("true"));
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
