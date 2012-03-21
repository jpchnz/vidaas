package uk.ac.ox.oucs.vidaas.session;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.ox.oucs.iam.interfaces.roles.IAMRoleManager;



/**
 * This class exists as a worker to UserRoles as a singleton
 * 
 * @author oucs0153
 *
 */
public class UserRolesWorker {
	private String currentRole = "";
	private IAMRoleManager roleManager;
	private static Map<String, Object> rolesListMap;
	

	private static UserRolesWorker instance = null;

	public static UserRolesWorker getInstance() {
		if (instance == null) {
			instance = new UserRolesWorker();
		}
		return instance;
	}

	protected UserRolesWorker() {
		rolesListMap = new LinkedHashMap<String, Object>();
		try {
			roleManager = IAMRoleManager.getInstance();
			String roles = roleManager.getRolesWithoutOwner();
			System.out.println("Received the following roles:" + roles);
			String[] rolesArray = roles.split("\n");
			for (String s : rolesArray) {
				if (rolesArray == null) {
					System.out.println("Null input");
				}
				else if ( (rolesArray.equals("\n")) || (rolesArray.equals("")) ) {
					System.out.println("Blank input");
				}
				else {
					rolesListMap.put(s, s);
				}
			}
			System.out.println("We have " + rolesListMap.size() + " roles defined");
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(String currentRole) {
		System.out.println("Setting current role to " + currentRole);
		this.currentRole = currentRole;
	}

	public Map<String, Object> getRolesListMap() {
		return rolesListMap;
	}

	
}

