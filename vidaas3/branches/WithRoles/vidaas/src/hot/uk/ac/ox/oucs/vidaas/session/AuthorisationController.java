package uk.ac.ox.oucs.vidaas.session;

import java.io.IOException;
import java.net.MalformedURLException;

import uk.ac.ox.oucs.iam.interfaces.roles.IAMRoleManager;
import uk.ac.ox.oucs.vidaas.utility.SystemVars;

public class AuthorisationController {
	public static String authorisedToDeleteDataspace(String currentRole) {
		boolean actionAuthorised = false;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData(currentRole)
					|| SystemVars.treatAdminAsOwner(currentRole);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		catch (IOException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		
		if (actionAuthorised) {
			return "deleteDataspacePanel";
		}
		return "notAuthorisedPanel";
	}
	
	
	public static String authorisedToUpdateDataspace(String currentRole) {
		boolean actionAuthorised = false;
		System.out.println("Check authorisation for role " + currentRole);
		try {
			actionAuthorised = IAMRoleManager.getInstance().getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData(currentRole)
					|| SystemVars.treatAdminAsOwner(currentRole);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		catch (IOException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		
		if (actionAuthorised) {
			System.out.println("Returning true");
			return "editDataspacePanel";
		}
		return "notAuthorisedPanel";
	}
	
	
	public static String authorisedToCreateDatabase(String currentRole) {
		System.out.println("authorisedToCreateDatabase");
		
		System.out
				.println(String.format(
						"Check if the user is authorised to create a database from schema when they have the role <%s>",
						currentRole));
		boolean authorised = false;
		try {
			authorised = IAMRoleManager.getInstance().getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData(currentRole)
					|| SystemVars.treatAdminAsOwner(currentRole);
		}
		catch (MalformedURLException e) {
			System.out.println("Malformed exception");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("IO Exception");
			e.printStackTrace();
		}
		if (authorised) {
			System.out.println("Returning true");
			return "createDatabasePanel";
		}
		return "notAuthorisedPanel";
	}
	
	
	public static String authorisedToDropDatabase(String currentRole) {
		boolean actionAuthorised = true;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData(currentRole)
					|| SystemVars.treatAdminAsOwner(currentRole);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		catch (IOException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		
		if (actionAuthorised) {
			System.out.println("Returning true");
			return "dropDatabasePanel";
		}
		return "notAuthorisedPanel";
	}
	
	public static String authorisedToCreateWebApp(String currentRole) {
		System.out.println("authorisedToCreateWebApp:" + currentRole);
		boolean actionAuthorised = true;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getWebAppAuthentication().isAllowedToCreateWebAppByRole(currentRole)
					|| SystemVars.treatAdminAsOwner(currentRole);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		catch (IOException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		
		if (actionAuthorised) {
			System.out.println("Returning true");
			return "createWebApplicationPanel";
		}
		System.out.println("Returning false");
		return "notAuthorisedPanel";
	}
	
	public static String authorisedToRemoveWebApp(String currentRole) {
		System.out.println("authorisedToCreateWebApp:" + currentRole);
		boolean actionAuthorised = true;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getWebAppAuthentication().isAllowedToDeleteWebAppByRole(currentRole)
					|| SystemVars.treatAdminAsOwner(currentRole);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		catch (IOException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		
		if (actionAuthorised) {
			System.out.println("Returning true");
			return "removeWebApplicationPanel";
		}
		System.out.println("Returning false");
		return "notAuthorisedPanel";
	}
	
	public static String authorisedToCreateBackupDatabase(String currentRole) {
		System.out.println("authorisedToCreateBackupDatabase:" + currentRole);
		boolean actionAuthorised = true;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData(currentRole)
					|| SystemVars.treatAdminAsOwner(currentRole);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		catch (IOException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		
		if (actionAuthorised) {
			System.out.println("Returning true");
			return "backupDatabasePanel";
		}
		return "notAuthorisedPanel";
	}
	
	public static String authorisedToCreateTestDatabase(String currentRole) {
		System.out.println("authorisedToCreateTestDatabase:" + currentRole);
		boolean actionAuthorised = true;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData(currentRole)
					|| SystemVars.treatAdminAsOwner(currentRole);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		catch (IOException e) {
			e.printStackTrace();
			actionAuthorised = false;
		}
		
		if (actionAuthorised) {
			System.out.println("Returning true");
			return "testDatabasePanel";
		}
		return "notAuthorisedPanel";
	}
}
