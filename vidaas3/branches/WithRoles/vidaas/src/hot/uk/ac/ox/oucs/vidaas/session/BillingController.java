package uk.ac.ox.oucs.vidaas.session;

import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.ox.oucs.iam.interfaces.roles.RolePoster;
import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;

public class BillingController {
	private RolePoster rolePoster;
	private static BillingController instance = null;
	
	
	public static BillingController getInstance() throws MalformedURLException {
		if (instance == null) {
			instance = new BillingController();
		}
		return instance;
	}
	
	private BillingController() throws MalformedURLException {
//		rolePoster = new RolePoster(new URL(SystemVars.ADDRESS_OF_IAM_WEBAPP));
	}
	
	public void addNewProjectForBilling(int projectId, int space, String ownerEmail, String projectName) {
//		rolePoster.sendPost(String.format("?u=%s&c=%s&projectName=%s&space=%d", ownerEmail, "newProject", projectName, space));
	}
}
