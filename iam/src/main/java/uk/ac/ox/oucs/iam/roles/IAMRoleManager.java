package uk.ac.ox.oucs.iam.roles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;


public class IAMRoleManager implements IAMRoleManagerInterface {
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
	
	/* (non-Javadoc)
	 * @see uk.ac.ox.oucs.iam.roles.IAMRoleManagerInterface#checkIsOwner(java.lang.String)
	 */
	public boolean checkIsOwner(String role) throws IOException {
		String result = sendPost("checkIsOwner=" + role);
		return (result.startsWith("true"));
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ox.oucs.iam.roles.IAMRoleManagerInterface#checkIsAllowedToCreateDatabaseByRole(java.lang.String)
	 */
	public boolean checkIsAllowedToCreateDatabaseByRole(String role) throws IOException {
		String result = sendPost("checkIsAllowedToCreateDatabaseByRole=" + role);
		return (result.startsWith("true"));
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ox.oucs.iam.roles.IAMRoleManagerInterface#checkIsAllowedToAlterOtherUsersRole(java.lang.String)
	 */
	public boolean checkIsAllowedToAlterOtherUsersRole(String role) throws IOException {
		String result = sendPost("checkIsAllowedToAlterOtherUsersRole=" + role);
		return (result.startsWith("true"));
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ox.oucs.iam.roles.IAMRoleManagerInterface#getRoles()
	 */
	public String getRoles() throws IOException {
		return sendPost("getRoles=true");
	}
	
	public static void main(String[] args) {
		try {
			IAMRoleManagerInterface iamPost = new IAMRoleManager();
			
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
