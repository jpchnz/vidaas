package uk.ac.ox.oucs.vidaas.session;

import java.util.Date;

import org.jboss.seam.log.Log;

import uk.ac.ox.oucs.iam.interfaces.roles.IAMRoleManager;
import uk.ac.ox.oucs.vidaas.dao.ProjectHome;
import uk.ac.ox.oucs.vidaas.dao.UserProjectHome;
import uk.ac.ox.oucs.vidaas.entity.Project;
import uk.ac.ox.oucs.vidaas.entity.UserProject;
import uk.ac.ox.oucs.vidaas.entity.UserProjectId;
import uk.ac.ox.oucs.vidaas.entity.Users;
import uk.ac.ox.oucs.vidaas.utility.StringUtility;

public class CreateProjectController {
	
	public void createProject(Users userMain, ProjectHome projectHome, UserProjectHome userProjectHome, Log log) {
		
		Project tempProject = projectHome.getInstance();
		tempProject.setStartDate(new Date());

		projectHome.getInstance().setTitle(
				StringUtility.stringValidation(projectHome.getInstance().getName()));

		projectHome.persist();

		UserProjectId userProjectID = new UserProjectId();
		userProjectID.setUserId(userMain.getUserId());
		userProjectID.setProjectId(projectHome.getInstance().getProjectId());

		log.info(tempProject.getProjectId() + "  "
				+ projectHome.getProjectProjectId() + "  "
				+ userMain.getUserId());

		UserProject userProject = new UserProject();
		userProject.setId(userProjectID);
		userProject.setProject(projectHome.getInstance());
		userProject.setUsers(userMain);
		try {
			userProject.setUserRole(IAMRoleManager.getInstance().getOwnerRole());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			userProject.setUserRole("Owner");
		}
		

		userProjectHome.setInstance(userProject);
		String persistResultString = userProjectHome.persist();
		
		log.info(persistResultString);

		log.info(projectHome.getInstance().getProjectId() + "  "
				+ userMain.getUserId());
		log.info(userProjectHome.findByUserID(userMain.getUserId())
				.size());
	}

}
