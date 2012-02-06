package uk.ac.ox.oucs.iam.roles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;


public class IAMRoleManager {
	private URL url;
	private URLConnection connection = null;
	private OutputStreamWriter out;
	
	
	public IAMRoleManager() throws Exception {
		url = new URL("http://localhost:8080/iam/ProjectRoleServlet");
	}
	public IAMRoleManager(String urlString) throws Exception {
		url = new URL(urlString);
	}
	
	private String postData;
	private String sendPost(String postData) throws IOException {
		this.postData = postData;
		sendPost();
		return getResult();
	}
	
	
	private void sendPost() throws IOException {
		connection = url.openConnection();
		connection.setDoOutput(true);
		out = new OutputStreamWriter(connection.getOutputStream());
		out.write(postData);
		out.flush();
		out.close();
	}
	
	
	private String getResult() throws IOException {
		BufferedReader in = null;
		String result = "";

		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String decodedString;

		while ((decodedString = in.readLine()) != null) {
			result += decodedString + "\n";
		}
		in.close();
		
		return result;
	}
	
	/**
	 * Check if the role supplied denotes the user as an owner of the project
	 * @param role the role to test
	 * @return true of the role denotes the user is owner, false otherwise
	 * @throws IOException
	 */
	public boolean checkIsOwner(String role) throws IOException {
		String result = sendPost("checkIsOwner=" + role);
		return (result.startsWith("true"));
	}
	
	/**
	 * Check if the user with the defined role is allowed to create a database against the project
	 * @param role the role to test
	 * @return true if the user is allowed to create a database in the project, else false
	 * @throws IOException
	 */
	public boolean checkIsAllowedToCreateDatabaseByRole(String role) throws IOException {
		String result = sendPost("checkIsAllowedToCreateDatabaseByRole=" + role);
		return (result.startsWith("true"));
	}
	
	/**
	 * Check if the user with the defined role is allowed to alter the roles of other users
	 * within a project, or remove them from the project altogether
	 * @param role the role to test
	 * @return true if the user is allowed to create a database in the project, else false
	 * @throws IOException
	 */
	public boolean checkIsAllowedToAlterOtherUsersRole(String role) throws IOException {
		String result = sendPost("checkIsAllowedToAlterOtherUsersRole=" + role);
		return (result.startsWith("true"));
	}
	
	
	/**
	 * Get a list of all roles available
	 * @return A String object containing all roles, each separated by a newline
	 * @throws IOException
	 */
	public String getRoles() throws IOException {
		return sendPost("getRoles=true");
	}
	
	public static void main(String[] args) {
		try {
			IAMRoleManager iamPost = new IAMRoleManager();
			
			System.out.println("Checking is owner ...");
			System.out.println(iamPost.checkIsOwner("owner"));
			System.out.println(iamPost.checkIsOwner("Admin"));
			System.out.println("Checking is owner allowed to create project ...");
			System.out.println(iamPost.checkIsAllowedToCreateDatabaseByRole("owner"));
			System.out.println("Checking is admin allowed to create project ...");
			System.out.println(iamPost.checkIsAllowedToCreateDatabaseByRole("Project Administrator"));
			System.out.println("Checking is contributor allowed to create project ...");
			System.out.println(iamPost.checkIsAllowedToCreateDatabaseByRole("contributor"));

//			System.out.println(iamPost.getRoles());
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
