package uk.ac.ox.oucs.vidaas.session;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.oucs.vidaas.dao.*;

import uk.ac.ox.oucs.vidaas.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

import uk.ac.ox.oucs.iam.interfaces.roles.IAMRoleManager;
import uk.ac.ox.oucs.vidaas.dao.DataspaceHome;
import uk.ac.ox.oucs.vidaas.dao.ProjectDatabaseHome;
import uk.ac.ox.oucs.vidaas.dao.ProjectHome;
import uk.ac.ox.oucs.vidaas.dao.UserProjectHome;
import uk.ac.ox.oucs.vidaas.dao.XMLFilesHome;
import uk.ac.ox.oucs.vidaas.entity.Dataspace;
import uk.ac.ox.oucs.vidaas.entity.Logins;
import uk.ac.ox.oucs.vidaas.entity.Project;
import uk.ac.ox.oucs.vidaas.entity.ProjectDatabase;
import uk.ac.ox.oucs.vidaas.entity.UserProject;
import uk.ac.ox.oucs.vidaas.entity.UserProjectId;
import uk.ac.ox.oucs.vidaas.entity.Users;
import uk.ac.ox.oucs.vidaas.entity.XMLFiles;
import uk.ac.ox.oucs.vidaas.utility.SystemVars;

@Name("navigationController")
@Scope(ScopeType.SESSION)
public class NavigationController {

	@Logger
	private Log log;

	@In(create = true)
	@Out(required = true)
	private UserProjectHome userProjectHome;

	@In(create = true)
	@Out(required = true)
	private ProjectHome projectHome;

	@In(create = true)
	@Out(required = true)
	private ProjectDatabaseHome projectDatabaseHome;
	

	private String notAuthMsg = "";

	@In(create = true)
	@Out(required = true)
	DataspaceHome dataspaceHome;

	@In(create = true)
	@Out(required = true)
	XMLFilesHome xmlFilesHome;

	private Users userMain;
	private Logins loginMain;

	private Project currentProject;
	private Dataspace currentDataspace;
	private ProjectDatabase currentProjectDatabase;
	private UserProject currentUserProject;

	private static String currentRole;
	private UserRolesWorker userRolesWorker = UserRolesWorker.getInstance();
	public Map<String, Object> getRolesListMap() {
		return userRolesWorker.getRolesListMap();
	}

	@RequestParameter("projectIDValue")
	private Integer projectIDValue;

	@RequestParameter("dataspaceIDValue")
	private Integer dataspaceIDValue;

	@RequestParameter("databaseIDValue")
	private Integer databaseIDValue;

	@RequestParameter("userIDValue")
	private Integer userIDValue;

	/*
	 * @RequestParameter("dataspaceIDValue") private Integer
	 * projectDatabaseIDValue;
	 */

	private String homePageMainBodyNavigation = "homeWelcome.xhtml";
	private String homePageNavigation = "empty.xhtml";

	private String createProjectFormInclude = "/popup/createProjectForm.xhtml";
	private boolean createProjectFormRender = false;

	private String createDataSpaceInclude = "/popup/createDataSpaceForm.xhtml";
	private boolean createDataSpaceFormRender = false;

	private String createDatabaseInclude = "/popup/createDatabaseForm.xhtml";
	private boolean databaseFormRender = false;

	private String createXMLDatabaseInclude = "/popup/createXMLDatabaseForm.xhtml";
	private boolean databaseXMLFormRender = false;

	private String dropDatabaseInclude = "/popup/dropDatabaseForm.xhtml";
	private boolean dropDatabaseFormRender = false;

	private String downloadDatabaseInclude = "/popup/downloadDatabaseForm.xhtml";
	private boolean downloadDatabaseFormRender = false;

	private String parseDatabaseInclude = "/popup/parseDatabaseForm.xhtml";
	private boolean parseDatabaseFormRender = false;

	private String addProjectMemberInclude = "/popup/addProjectMemberForm.xhtml";
	private boolean projectMemberFormRender = false;

	private String backupDatabaseInclude = "/popup/backupDatabaseForm.xhtml";
	private boolean backupDatabaseFormRender = false;

	private String testDatabaseInclude = "/popup/testDatabaseForm.xhtml";
	private boolean testDatabaseFormRender = false;

	private String restoreDatabaseInclude = "/popup/restoreDatabaseForm.xhtml";
	private boolean restoreDatabaseFormRender = false;

	private String testXMLDatabaseInclude = "/popup/testXMLDatabaseForm.xhtml";
	private boolean testXMLDatabaseFormRender = false;

	private String createWebApplicationInclude = "/popup/createWebApplicationForm.xhtml";
	private boolean createWebApplicationFormRender = false;

	private String removeWebApplicationInclude = "/popup/removeWebApplicationForm.xhtml";
	private boolean removeWebApplicationFormRender = false;

	private String editProjectMemberInclude = "/popup/editProjectMemberForm.xhtml";
	private boolean editProjectMemberFormRender = false;

	private String editProjectInclude = "/popup/editProjectForm.xhtml";
	private boolean editProjectFormRender = false;

	private String deleteProjectInclude = "/popup/deleteProjectForm.xhtml";
	private boolean deleteProjectFormRender = false;

	private String editDataspaceInclude = "/popup/editDataspaceForm.xhtml";
	private boolean editDataspaceFormRender = false;

	private String deleteDataspaceInclude = "/popup/deleteDataspaceForm.xhtml";
	private boolean deleteDataspaceFormRender = false;

	private String editProjectMemberErrorMessage = "Failed to change the user role.";
	private String addProjectMemberErrorMessage = "Failed to add new user.";
	
	private String createDatabaseFromSchemaErrorMessage = "You are not authorised to add a database here.";
	private String createDatabaseFromSchemaInclude = "/popup/editProjectMemberError.xhtml";
	private boolean createDatabaseFromSchemaFormRender = false;
	
	public String getHomePageMainBodyNavigation() {
		return homePageMainBodyNavigation;
	}

	public void setHomePageMainBodyNavigation(String homePageMainBodyNavigation) {
		this.homePageMainBodyNavigation = homePageMainBodyNavigation;
	}

	public String getHomePageNavigation() {
		return homePageNavigation;
	}

	public void setHomePageNavigation(String homePageNavigation) {
		this.homePageNavigation = homePageNavigation;
	}

	public Integer getProjectIDValue() {
		log.info("getProjectIDValue {0}", projectIDValue);
		return projectIDValue;
	}

	public void setProjectIDValue(Integer projectIDValue) {
		log.info("setProjectIDValue {0}", projectIDValue);
		this.projectIDValue = projectIDValue;
	}

	public Integer getDataspaceIDValue() {
		return dataspaceIDValue;
	}

	public void setDataspaceIDValue(Integer dataspaceIDValue) {
		this.dataspaceIDValue = dataspaceIDValue;
	}

	public Integer getDatabaseIDValue() {
		return databaseIDValue;
	}

	public void setDatabaseIDValue(Integer databaseIDValue) {
		log.info("setDatabaseIDValue {0}", databaseIDValue);

		this.databaseIDValue = databaseIDValue;

		projectDatabaseHome.setId(databaseIDValue);
		this.currentProjectDatabase = projectDatabaseHome.getInstance();
		Contexts.getSessionContext().set("currentProjectDatabase",
				this.currentProjectDatabase);
	}

	public Integer getUserIDValue() {
		return userIDValue;
	}

	public void setUserIDValue(Integer userIDValue) {
		this.userIDValue = userIDValue;
	}

	public String getCreateProjectFormInclude() {
		return createProjectFormInclude;
	}

	public void setCreateProjectFormInclude(String createProjectFormInclude) {
		this.createProjectFormInclude = createProjectFormInclude;
	}

	public boolean isCreateProjectFormRender() {
		return createProjectFormRender;
	}

	public void setCreateProjectFormRender(boolean createProjectFormRender) {
		this.createProjectFormRender = createProjectFormRender;
	}

	public String getCreateDataSpaceInclude() {
		return createDataSpaceInclude;
	}

	public void setCreateDataSpaceInclude(String createDataSpaceInclude) {
		this.createDataSpaceInclude = createDataSpaceInclude;
	}

	public boolean isCreateDataSpaceFormRender() {
		return createDataSpaceFormRender;
	}

	public void setCreateDataSpaceFormRender(boolean createDataSpaceFormRender) {
		this.createDataSpaceFormRender = createDataSpaceFormRender;
	}

	public String getCreateDatabaseInclude() {
		return createDatabaseInclude;
	}

	public void setCreateDatabaseInclude(String createDatabaseInclude) {
		this.createDatabaseInclude = createDatabaseInclude;
	}

	public boolean isDatabaseFormRender() {
		return databaseFormRender;
	}

	public void setDatabaseFormRender(boolean databaseFormRender) {
		this.databaseFormRender = databaseFormRender;
	}

	public String getParseDatabaseInclude() {
		return parseDatabaseInclude;
	}

	public void setParseDatabaseInclude(String parseDatabaseInclude) {
		this.parseDatabaseInclude = parseDatabaseInclude;
	}

	public boolean isParseDatabaseFormRender() {
		return parseDatabaseFormRender;
	}

	public void setParseDatabaseFormRender(boolean parseDatabaseFormRender) {
		this.parseDatabaseFormRender = parseDatabaseFormRender;
	}

	public String getAddProjectMemberInclude() {
		return addProjectMemberInclude;
	}

	public void setAddProjectMemberInclude(String addProjectMemberInclude) {
		this.addProjectMemberInclude = addProjectMemberInclude;
	}

	public boolean isProjectMemberFormRender() {
		return projectMemberFormRender;
	}

	public void setProjectMemberFormRender(boolean projectMemberFormRender) {
		this.projectMemberFormRender = projectMemberFormRender;
	}

	public String getDropDatabaseInclude() {
		return dropDatabaseInclude;
	}

	public void setDropDatabaseInclude(String dropDatabaseInclude) {
		this.dropDatabaseInclude = dropDatabaseInclude;
	}

	public boolean isDropDatabaseFormRender() {
		return dropDatabaseFormRender;
	}

	public void setDropDatabaseFormRender(boolean dropDatabaseFormRender) {
		this.dropDatabaseFormRender = dropDatabaseFormRender;
	}

	public String getDownloadDatabaseInclude() {
		return downloadDatabaseInclude;
	}

	public void setDownloadDatabaseInclude(String downloadDatabaseInclude) {
		this.downloadDatabaseInclude = downloadDatabaseInclude;
	}

	public boolean isDownloadDatabaseFormRender() {
		return downloadDatabaseFormRender;
	}

	public void setDownloadDatabaseFormRender(boolean downloadDatabaseFormRender) {
		this.downloadDatabaseFormRender = downloadDatabaseFormRender;
	}

	public String getBackupDatabaseInclude() {
		return backupDatabaseInclude;
	}

	public void setBackupDatabaseInclude(String backupDatabaseInclude) {
		this.backupDatabaseInclude = backupDatabaseInclude;
	}

	public boolean isBackupDatabaseFormRender() {
		return backupDatabaseFormRender;
	}

	public void setBackupDatabaseFormRender(boolean backupDatabaseFormRender) {
		this.backupDatabaseFormRender = backupDatabaseFormRender;
	}

	public String getTestDatabaseInclude() {
		return testDatabaseInclude;
	}

	public void setTestDatabaseInclude(String testDatabaseInclude) {
		this.testDatabaseInclude = testDatabaseInclude;
	}

	public boolean isTestDatabaseFormRender() {
		return testDatabaseFormRender;
	}

	public void setTestDatabaseFormRender(boolean testDatabaseFormRender) {
		this.testDatabaseFormRender = testDatabaseFormRender;
	}

	public String getRestoreDatabaseInclude() {
		return restoreDatabaseInclude;
	}

	public void setRestoreDatabaseInclude(String restoreDatabaseInclude) {
		this.restoreDatabaseInclude = restoreDatabaseInclude;
	}

	public boolean isRestoreDatabaseFormRender() {
		return restoreDatabaseFormRender;
	}

	public void setRestoreDatabaseFormRender(boolean restoreDatabaseFormRender) {
		this.restoreDatabaseFormRender = restoreDatabaseFormRender;
	}

	public String getTestXMLDatabaseInclude() {
		return testXMLDatabaseInclude;
	}

	public void setTestXMLDatabaseInclude(String testXMLDatabaseInclude) {
		this.testXMLDatabaseInclude = testXMLDatabaseInclude;
	}

	public boolean isTestXMLDatabaseFormRender() {
		return testXMLDatabaseFormRender;
	}

	public void setTestXMLDatabaseFormRender(boolean testXMLDatabaseFormRender) {
		this.testXMLDatabaseFormRender = testXMLDatabaseFormRender;
	}

	public String getCreateXMLDatabaseInclude() {
		return createXMLDatabaseInclude;
	}

	public void setCreateXMLDatabaseInclude(String createXMLDatabaseInclude) {
		this.createXMLDatabaseInclude = createXMLDatabaseInclude;
	}

	public boolean isDatabaseXMLFormRender() {
		return databaseXMLFormRender;
	}

	public void setDatabaseXMLFormRender(boolean databaseXMLFormRender) {
		this.databaseXMLFormRender = databaseXMLFormRender;
	}

	public String getCreateWebApplicationInclude() {
		return createWebApplicationInclude;
	}

	public void setCreateWebApplicationInclude(
			String createWebApplicationInclude) {
		this.createWebApplicationInclude = createWebApplicationInclude;
	}

	public boolean isCreateWebApplicationFormRender() {
		return createWebApplicationFormRender;
	}

	public void setCreateWebApplicationFormRender(
			boolean createWebApplicationFormRender) {
		this.createWebApplicationFormRender = createWebApplicationFormRender;
	}

	public String getRemoveWebApplicationInclude() {
		return removeWebApplicationInclude;
	}

	public void setRemoveWebApplicationInclude(
			String removeWebApplicationInclude) {
		this.removeWebApplicationInclude = removeWebApplicationInclude;
	}

	public boolean isRemoveWebApplicationFormRender() {
		return removeWebApplicationFormRender;
	}

	public void setRemoveWebApplicationFormRender(
			boolean removeWebApplicationFormRender) {
		this.removeWebApplicationFormRender = removeWebApplicationFormRender;
	}

	public String getEditProjectMemberInclude() {
		return editProjectMemberInclude;
	}

	public void setEditProjectMemberInclude(String editProjectMemberInclude) {
		this.editProjectMemberInclude = editProjectMemberInclude;
	}

	public boolean isEditProjectMemberFormRender() {
		return editProjectMemberFormRender;
	}

	public void setEditProjectMemberFormRender(
			boolean editProjectMemberFormRender) {
		this.editProjectMemberFormRender = editProjectMemberFormRender;
	}

	public String getEditProjectInclude() {
		return editProjectInclude;
	}

	public void setEditProjectInclude(String editProjectInclude) {
		this.editProjectInclude = editProjectInclude;
	}

	public boolean isEditProjectFormRender() {
		return editProjectFormRender;
	}

	public void setEditProjectFormRender(boolean editProjectFormRender) {
		this.editProjectFormRender = editProjectFormRender;
	}

	public String getDeleteProjectInclude() {
		return deleteProjectInclude;
	}

	public void setDeleteProjectInclude(String deleteProjectInclude) {
		this.deleteProjectInclude = deleteProjectInclude;
	}

	public boolean isDeleteProjectFormRender() {
		return deleteProjectFormRender;
	}

	public void setDeleteProjectFormRender(boolean deleteProjectFormRender) {
		this.deleteProjectFormRender = deleteProjectFormRender;
	}

	public String getEditDataspaceInclude() {
		return editDataspaceInclude;
	}

	public void setEditDataspaceInclude(String editDataspaceInclude) {
		this.editDataspaceInclude = editDataspaceInclude;
	}

	public boolean isEditDataspaceFormRender() {
		return editDataspaceFormRender;
	}

	public void setEditDataspaceFormRender(boolean editDataspaceFormRender) {
		this.editDataspaceFormRender = editDataspaceFormRender;
	}

	public String getDeleteDataspaceInclude() {
		return deleteDataspaceInclude;
	}

	public void setDeleteDataspaceInclude(String deleteDataspaceInclude) {
		this.deleteDataspaceInclude = deleteDataspaceInclude;
	}

	public boolean isDeleteDataspaceFormRender() {
		return deleteDataspaceFormRender;
	}

	public void setDeleteDataspaceFormRender(boolean deleteDataspaceFormRender) {
		this.deleteDataspaceFormRender = deleteDataspaceFormRender;
	}

	public String getEditProjectMemberErrorMessage() {
		return editProjectMemberErrorMessage;
	}

	public void setEditProjectMemberErrorMessage(
			String editProjectMemberErrorMessage) {
		this.editProjectMemberErrorMessage = editProjectMemberErrorMessage;
	}

	public Users getUserMain() {
		if (userMain == null) {
			userMain = ((Users) Contexts.getSessionContext().get("userMain"));
		}
		return userMain;
	}

	public Logins getLoginMain() {
		return loginMain;
	}

	public void defaultHomePage() {
		homePageMainBodyNavigation = "homeWelcome.xhtml";
		homePageNavigation = "empty.xhtml";
	}

	public void welcomePage() {
		homePageMainBodyNavigation = "homeWelcome.xhtml";
		homePageNavigation = "empty.xhtml";
	}

	public void listUserProjects() {
		// log.info("listUserProjects {0}", homePageMainBodyNavigation);
		if (getUserMain() != null) {
			homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
		} else {
			homePageMainBodyNavigation = "homeWelcome.xhtml";
		}
		// log.info("listUserProjects {0}", homePageMainBodyNavigation);
	}

	public List<UserProject> userProjectsList() {
		List<UserProject> userProjectsList = null;
		if (getUserMain() != null) {
			userProjectsList = userProjectHome.findByUserID(getUserMain().getUserId());
		}
		if (userProjectsList == null) {
			userProjectsList = new ArrayList<UserProject>();
		}
		return userProjectsList;
	}

	/*
	 * Projects don't have Databases public List<ProjectDatabase>
	 * listProjectDatabase(Integer projectIDValue){ return
	 * projectDatabaseHome.findByProjectID(projectIDValue); // currentDataspace
	 * }
	 */

	public List<ProjectDatabase> listDatabaseByDataspace() {
		dataspaceHome.setId(currentDataspace.getDataSpaceId());

		currentDataspace = dataspaceHome.find();

		Set<ProjectDatabase> tempProjectDatabaseSet = currentDataspace
				.getProjectDatabases();
		/**/log.info(
				"currentDataspace.getDataSpaceId() {0} Number of Databases: {1}",
				currentDataspace.getDataSpaceId(),
				tempProjectDatabaseSet.size());

		List<ProjectDatabase> list = new ArrayList<ProjectDatabase>(
				tempProjectDatabaseSet);
		
		 Collections.sort(list, new Comparator(){
			 
	            public int compare(Object o1, Object o2) {
	            	ProjectDatabase p1 = (ProjectDatabase) o1;
	            	ProjectDatabase p2 = (ProjectDatabase) o2;
	               return p1.getDatabaseId().compareTo(p2.getDatabaseId());
	            }
	 
	        });
		 
		return list;
		// currentDataspace
	}

	public List<XMLFiles> listXMLFilesByProjectDatabase(int databaseIDValue) {
		List<XMLFiles> xmlFilesList = xmlFilesHome
				.findByDatabaseID(databaseIDValue);
		return xmlFilesList;
	}

	public List<UserProject> listProjectMembers(Integer projectIDValue) {
		List<UserProject> userProject = userProjectHome
				.findByProjectID(projectIDValue);
		// System.out.println("listProjectMembers(Integer " + projectIDValue +
		// ")   " + userProject.size() + "  " +
		// userProject.get(0).getUsers().getFirstName());
		return userProject;
	}

	public List<Dataspace> listProjectDataspaces(Integer projectIDValue) {
		List<Dataspace> dataspacesList = dataspaceHome
				.findByProjectID(projectIDValue);
		/*
		 * log.info(
		 * "listProjectDataspaces ***************************************   dataspaceIDValue:  {0} {1}"
		 * , dataspacesList.size(), projectIDValue);
		 */
		return dataspacesList;
	}

	public void addProjectMemberInitial() {
		projectMemberFormRender = false;
		// log.info("addProjectMemberInitial() ................................................................... addProjectMemberInitial()");
		addProjectMemberInclude = "/popup/addProjectMemberForm.xhtml";
	}

	public void addProjectMemberConfirmation() {
		projectMemberFormRender = true;
		log.info("createDatabaseStructureConfirmation()");
		addProjectMemberInclude = "/popup/addProjectMemberConfirmation.xhtml";
	}

	public void createProjectInitial() {
		createProjectFormRender = false;
		log.info("createProjectInitial()");
		createProjectFormInclude = "/popup/createProjectForm.xhtml";
	}

	public void createProjectConfirmation() {
		createProjectFormRender = true;
		createProjectFormInclude = "/popup/createProjectConfirmation.xhtml";
	}

	public void createProjectDataSpaceInitial() {
		createProjectFormRender = false;
		log.info("createProjectInitial()");
		createDataSpaceInclude = "/popup/createDataSpaceForm.xhtml";
	}

	public void createProjectDataspaceConfirmation() {
		createProjectFormRender = true;
		createDataSpaceInclude = "/popup/createDataSpaceConfirmation.xhtml";
	}

	public void createDatabaseInitial() {
		databaseFormRender = false;
		createDatabaseInclude = "/popup/createDatabaseForm.xhtml";
	}

	public void createDatabaseConfirmation() {
		databaseFormRender = true;
		createDatabaseInclude = "/popup/createDatabaseConfirmation.xhtml";
	}

	public void dropDatabaseInitial() {
		this.dropDatabaseFormRender = false;
		dropDatabaseInclude = "/popup/dropDatabaseForm.xhtml";
		// homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
		homePageMainBodyNavigation = "/custom/singleDataspaceByProject.xhtml";
	}

	public void dropDatabaseConfirmation() {
		System.out.println("dropDatabaseConfirmation: ");
		this.dropDatabaseFormRender = true;
		dropDatabaseInclude = "/popup/dropDatabaseConfirmation.xhtml";
		// homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}

	public void backupDatabaseInitial() {
		backupDatabaseFormRender = false;
		backupDatabaseInclude = "/popup/backupDatabaseForm.xhtml";
		// homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}

	public void backupDatabaseConfirmation() {
		backupDatabaseFormRender = true;
		backupDatabaseInclude = "/popup/backupDatabaseConfirmation.xhtml";
	}

	public void restoreDatabaseInitial() {
		restoreDatabaseFormRender = false;
		restoreDatabaseInclude = "/popup/restoreDatabaseForm.xhtml";
		// homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}

	public void restoreDatabaseConfirmation() {
		restoreDatabaseFormRender = true;
		restoreDatabaseInclude = "/popup/restoreDatabaseConfirmation.xhtml";
	}

	public void createWebApplicationInitial() {
		createWebApplicationFormRender = false;
		createWebApplicationInclude = "/popup/createWebApplicationForm.xhtml";
		// homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}

	public void createWebApplicationConfirmation() {
		createWebApplicationFormRender = true;
		createWebApplicationInclude = "/popup/createWebApplicationConfirmation.xhtml";
	}

	public void removeWebApplicationInitial() {
		removeWebApplicationFormRender = false;
		removeWebApplicationInclude = "/popup/removeWebApplicationForm.xhtml";
		// homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}

	public void removeWebApplicationConfirmation() {
		removeWebApplicationFormRender = true;
		removeWebApplicationInclude = "/popup/removeWebApplicationConfirmation.xhtml";
	}

	public void testDatabaseInitial() {
		testDatabaseFormRender = false;
		testDatabaseInclude = "/popup/testDatabaseForm.xhtml";
		// homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}

	public void testDatabaseConfirmation() {
		testDatabaseFormRender = true;
		testDatabaseInclude = "/popup/testDatabaseConfirmation.xhtml";
	}

	public void editProjectMemberInitial() {
		editProjectMemberFormRender = false;
		editProjectMemberInclude = "/popup/editProjectMemberForm.xhtml";
	}

	public void editProjectMemberConfirmation() {
		editProjectMemberFormRender = true;
		editProjectMemberInclude = "/popup/editProjectMemberConfirmation.xhtml";
	}

	public void editProjectInitial() {
		editProjectMemberFormRender = false;
		editProjectMemberInclude = "/popup/editProjectForm.xhtml";
	}

	public void editProjectConfirmation() {
		editProjectMemberFormRender = true;
		editProjectMemberInclude = "/popup/editProjectConfirmation.xhtml";
	}

	public void deleteProjectInitial() {
		deleteProjectFormRender = false;
		deleteProjectInclude = "/popup/deleteProjectForm.xhtml";
	}

	public void deleteProjectConfirmation() {
		deleteProjectFormRender = true;
		deleteProjectInclude = "/popup/deleteProjectConfirmation.xhtml";
	}

	public void deleteDataspaceInitial() {
		deleteDataspaceFormRender = false;
		deleteDataspaceInclude = "/popup/deleteDataspaceForm.xhtml";
	}

	public void deleteDataspaceConfirmation() {
		deleteDataspaceFormRender = true;
		deleteDataspaceInclude = "/popup/deleteDataspaceConfirmation.xhtml";
	}
	
	public void editDataspaceInitial() {
		editDataspaceFormRender = false;
		editDataspaceInclude = "/popup/editDataspaceForm.xhtml";
	}

	public void editDataspaceConfirmation() {
		editDataspaceFormRender = true;
		editDataspaceInclude = "/popup/editDataspaceConfirmation.xhtml";
	}

	public void singleDataspaceDisplayPage() {
		System.out.println("singleDataspaceDisplayPage");
		log.info(
				"singleDatabaseDisplayPage dataspaceIDValue: {0} projectIDValue: {1}",
				dataspaceIDValue, projectIDValue);
		System.out.println(String.format("db id: %d, proj id %d", dataspaceIDValue, projectIDValue));

		projectHome.setId(projectIDValue);
		currentProject = projectHome.getInstance();
		Contexts.getSessionContext().set("currentProject", currentProject);
		System.out.println("current project set to be " + currentProject.getName());

		dataspaceHome.setId(dataspaceIDValue);
		currentDataspace = dataspaceHome.getInstance();
		Contexts.getSessionContext().set("currentDataspace", currentDataspace);


		System.out.println("current dataspace set to be " + currentDataspace.getDataspaceName());
		homePageMainBodyNavigation = "/custom/singleDataspaceByProject.xhtml";
		System.out.println("Return from singleDataspaceDisplayPage");
	}

	public void setCurrentDataspace(Integer currentDataspaceIDValue) {
		log.info("setCurrentDataspace currentDataspaceIDValue: "
				+ currentDataspaceIDValue);
		dataspaceHome.setId(currentDataspaceIDValue);
		currentDataspace = dataspaceHome.getInstance();

		Contexts.getSessionContext().set("currentDataspace", currentDataspace);
		Contexts.getSessionContext().set("currentRole", currentRole);
	}

	
	
	public String authorisedToEditProject() {//editProjectInclude = "/popup/editProjectForm.xhtml"
		System.out.println("authorisedToEditProject with Project ID = " + projectIDValue);
		setAndGetUserRole(userProjectsList(), projectIDValue);
		System.out.println("Current role is now set to " + currentRole);
		return AuthorisationController.authorisedToEditProject(currentRole);
	}
	
	
	public String getPanelForDBDelete() {
		System.out.println("getPanelForDBDelete");
		setAndGetUserRole(userProjectsList(), projectIDValue);
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
		
		String rerenderPanelWhenDeletingDatabase;
		if (actionAuthorised) {
			System.out.println("Authorised");
			rerenderPanelWhenDeletingDatabase = "deleteDataspacePanel";
			notAuthMsg = "";
		}
		else {
			System.out.println("Not authorised!");
			rerenderPanelWhenDeletingDatabase = "notAuthorisedPanel";
			notAuthMsg = "You are not authorised to do this.";
		}
		return rerenderPanelWhenDeletingDatabase;
	}
	
	
	public String getCreateDBPanel() {
		System.out.println("getCreateDBPanel");
		authorisedToPerformOperation = checkAuthorisedToUploadDb();
		if (authorisedToPerformOperation) {
			System.out.println("true");
			notAuthMsg = "";
			return "createDatabasePanel";
		}
		else {
			System.out.println("false");
			notAuthMsg = "You are not authorised to do this.";
			return "createDatabasePanel";
		}
	}
	private boolean authorisedToPerformOperation = false;
	
	
	public boolean checkAuthorisedToUploadDb() {
		System.out.println("checkAuthorisedToUploadDb:" + projectIDValue);
		setAndGetUserRole(userProjectsList(), projectIDValue);
		
		if (currentRole == null) {
			return false;
		}
		System.out
				.println(String.format(
						"Check if the user is authorised to create a database from schema when they have the role <%s>",
						currentRole));
		boolean actionAuthorised = false;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData(currentRole)
					|| SystemVars.treatAdminAsOwner(currentRole);
			System.out.println("Call returned " + actionAuthorised);
		}
		catch (MalformedURLException e) {
			System.out.println("Malformed exception");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("IO Exception");
			e.printStackTrace();
		}
		return actionAuthorised;
	}
	
//	public String getPanelForDBCreate() {
//		System.out.println("getPanelForDBCreate");
//		setAndGetUserRole(userProjectsList(), projectIDValue);
//		boolean actionAuthorised = false;
//		try {
//			actionAuthorised = IAMRoleManager.getInstance().getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData(currentRole)
//					|| SystemVars.treatAdminAsOwner(currentRole);
//		}
//		catch (MalformedURLException e) {
//			e.printStackTrace();
//			actionAuthorised = false;
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//			actionAuthorised = false;
//		}
//		
//		String rerenderPanelWhenCreatingDatabase;
//		if (actionAuthorised) {
//			System.out.println("Authorised");
//			rerenderPanelWhenCreatingDatabase = "createDataSpacePanel";
//			createDataSpaceInclude = "/popup/createDataSpaceForm.xhtml";
//			notAuthMsg = "";
//		}
//		else {
//			System.out.println("Not authorised!");
//			rerenderPanelWhenCreatingDatabase = "notAuthorisedPanel";
//			createDataSpaceInclude = "/popup/editProjectMemberError.xhtml";
//			notAuthMsg = "You are not authorised to do this.";
//		}
//		return rerenderPanelWhenCreatingDatabase;
//	}
		
//	public String getPanelForDBEdit() {
//		System.out.println("getPanelForDBEdit");
//		setAndGetUserRole(userProjectsList(), projectIDValue);
//		boolean actionAuthorised = false;
//		try {
//			actionAuthorised = IAMRoleManager.getInstance().getDatabaseAuthentication().isAllowedToAddEditOrRemoveDBData(currentRole)
//					|| SystemVars.treatAdminAsOwner(currentRole);
//		}
//		catch (MalformedURLException e) {
//			e.printStackTrace();
//			actionAuthorised = false;
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//			actionAuthorised = false;
//		}
//		
//		String rerenderPanelWhenEditingDatabase;
//		if (actionAuthorised) {
//			System.out.println("Authorised");
//			rerenderPanelWhenEditingDatabase = "editDataspacePanel";
//			notAuthMsg = "";
//		}
//		else {
//			System.out.println("Not authorised!");
//			rerenderPanelWhenEditingDatabase = "notAuthorisedPanel";
//			notAuthMsg = "You are not authorised to edit this database.";
//		}
//		return rerenderPanelWhenEditingDatabase;
//	}
	
	
	
	public void createProjectMember() {
		log.info("createProjectMember projectIDValue: " + projectIDValue);
		if (projectIDValue != null) {
			Contexts.getSessionContext().set("projectIDValue", projectIDValue);
		} else {
			projectIDValue = (Integer) Contexts.getSessionContext().get(
					"projectIDValue");
		}

		projectHome.setId(projectIDValue);
		currentProject = projectHome.getInstance();
		
		
		
		/*
		 * We need to determine if this user has authority to add another user to the project!
		 */
		log.debug("Check if the user is authorised");
		setAndGetUserRole(userProjectsList(), projectIDValue);
		
		Contexts.getSessionContext().set("currentProject", currentProject);
		// homePageMainBodyNavigation = "/custom/createUserForm.xhtml";
	}
	
	public void createDatabaseFromSchema() {
		System.out.println("Nonononono");
		createDatabaseFromSchemaErrorMessage = "You are not authorised to add a database here.";
		createDatabaseFromSchemaInclude = "/popup/editProjectMemberError.xhtml";
	}

	public void modifyProjectMember() {
		System.out.println("userIDValue: " + userIDValue);
		
		this.userProjectHome.setId(new UserProjectId(projectIDValue,
				userIDValue));
		currentUserProject = this.userProjectHome.getInstance();

		List<UserProject> userProjectList = userProjectHome
				.findByProjectID(currentUserProject.getId().getProjectId());
		System.out.println("Number of users in project:" + userProjectList.size());
		
		
		boolean actionAuthorised = false;
		boolean attemptToModifyOwner = false;
		
		if (userProjectList.size() > 0) {
			setAndGetUserRole(userProjectsList(), projectIDValue);
			
			System.out.println(String.format("Check if %s is authorised to modify a project member", currentRole));
			actionAuthorised = isAuthorisedToAlterUserRole();
			
			if (actionAuthorised) {
				System.out.println("Yes, the user is authorised.");
				
				/**
				 * Look through each user in the project
				 */
				System.out.println(String.format("There are %d users in this project", userProjectList.size()));
				for (int i = 0; i < userProjectList.size(); i++) {
					UserProject workingUserProject = userProjectList.get(i);
					System.out.println(String.format("Check who we are dealing with. Does user id %d match the current id %d?",
							workingUserProject.getId().getUserId(),currentUserProject.getId().getUserId()));
					System.out.println(String.format("Project Name is %s", workingUserProject.getProject().getName()));
					if (workingUserProject.getId().getUserId() != currentUserProject.getId().getUserId()) {
						System.out.println("No, it doesn't!");
					}
					else {
						System.out.println(String.format("Yes, this is it. So we want to alter the role of user %d who has role of <%s>",
								workingUserProject.getId().getUserId(), workingUserProject.getUserRole()));
						/*
						 * We know the user doing the work is authorised to do it. Now
						 * check if the user to be changed is owner.
						 */
						try {
							attemptToModifyOwner = (IAMRoleManager.getInstance().getOwnerRole().equals(workingUserProject.getUserRole())
									|| SystemVars.treatAdminAsOwner(workingUserProject.getUserRole()));
						}
						catch (MalformedURLException e) {
							e.printStackTrace();
						}
						catch (IOException e) {
							e.printStackTrace();
						}
						if (attemptToModifyOwner) {
							System.out.println("Attempt to modify owner");
							editProjectMemberErrorMessage = "You cannot modify the project owner! Change in role not allowed";
							editProjectMemberInclude = "/popup/editProjectMemberError.xhtml";
						}
						else {
							System.out.println(String.format("Role %s is modifyable - so let's modify!", workingUserProject.getUserRole()));
						}
					}
					
					break;
				}
				if (attemptToModifyOwner) {
					editProjectMemberErrorMessage = "You cannot modify the project owner! Change in role not allowed";
					editProjectMemberInclude = "/popup/editProjectMemberError.xhtml";
				}
				else {
					editProjectMemberInclude = "/popup/editProjectMemberForm.xhtml";
				}
			}
			else {
				setupErrorFields();
			}
		}
		else {
			System.out.println("Error - no users in the project. This should never happen.");
		}
		Contexts.getSessionContext().set("currentUserProject", currentUserProject);
	}
	
	private boolean isAuthorisedToAlterUserRole() {
		boolean actionAuthorised = false;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getProjectAuthentication().isAllowedToAlterOtherUsersRole(currentRole)
					|| SystemVars.treatAdminAsOwner(currentRole);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return actionAuthorised;
	}
	
	private void setupErrorFields() {
		editProjectMemberErrorMessage = "You are not authorised to modify this project member.";
		editProjectMemberInclude = "/popup/editProjectMemberError.xhtml";
	}

	public void setCurrentProject(Integer currentProjectIDValue) {
		System.out.println("Current project:" + currentProjectIDValue);
		log.info("setCurrentProject projectIDValue: " + projectIDValue + "  "
				+ currentProjectIDValue);
		if (projectIDValue != null) {
			Contexts.getSessionContext().set("projectIDValue", projectIDValue);
		} else {
			projectIDValue = (Integer) Contexts.getSessionContext().get(
					"projectIDValue");
		}

		projectHome.setId(projectIDValue);
		currentProject = projectHome.getInstance();
		Contexts.getSessionContext().set("currentProject", currentProject);
		setAndGetUserRole(userProjectsList(), projectIDValue);
	}

	public boolean setCurrentDatabase(Integer currentDatabaseIDValue) {
		Contexts.getSessionContext().set("currentProjectDatabase", null);
		log.info("setCurrentDatabase  currentDatabaseIDValue: "
				+ currentDatabaseIDValue);
		
		this.currentProjectDatabase = null;
		
		projectDatabaseHome.setId(currentDatabaseIDValue);
		this.currentProjectDatabase = projectDatabaseHome.getInstance();

		setCurrentDataspace(this.currentProjectDatabase.getDataspace()
				.getDataSpaceId());
		
		Contexts.getSessionContext().set("currentProjectDatabase",
				this.currentProjectDatabase);
		return true;
	}

	public ProjectDatabase getCurrentProjectDatabase() {
		//log.info(" *******************************************************   getCurrentProjectDatabase()");
		if (currentProjectDatabase != null) {
			log.info("getCurrentProjectDatabase() Database Name: {0}",
					currentProjectDatabase.getDatabaseName());
		}
		return currentProjectDatabase;
	}

	public Project getCurrentProject() {
		if (currentProject != null) {
			log.info("getCurrentProject() Project Name: {0}",
					currentProject.getName());
		}
		return currentProject;
	}

	public void setCurrentProjectDatabase(ProjectDatabase currentProjectDatabase) {
		this.currentProjectDatabase = currentProjectDatabase;
	}

	public void actionTest() {
		log.info("actionTest()");
	}

	public void onCompleteTest() {
		log.info("onCompleteTest()");
	}


	public String getAddProjectMemberErrorMessage() {
		return addProjectMemberErrorMessage;
	}

	public void setAddProjectMemberErrorMessage(String addProjectMemberErrorMessage) {
		this.addProjectMemberErrorMessage = addProjectMemberErrorMessage;
	}

	public String getNotAuthMsg() {
		return notAuthMsg;
	}

	public void setNotAuthMsg(String notAuthMsg) {
		this.notAuthMsg = notAuthMsg;
	}

	public String getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(String currentRole) {
		NavigationController.currentRole = currentRole;
	}
	
	
	
	
	
	
	/**
	 * Roles
	 */
	public String setAndGetUserRoleByEmail(Set<UserProject> userProjects, Integer projectIDValue) {
		List<UserProject> list = new ArrayList<UserProject>(userProjects);
		return setAndGetUserRoleByEmail(list, projectIDValue);
	}
	public String setAndGetUserRoleByEmail(List<UserProject> UserProjects, Integer projectIDValue) {
		System.out.println("setAndGetUserRoleByEmail");
		if (projectIDValue == null) {
			return null;
		}
		System.out.println("About to get the user role for project id:" + projectIDValue);
		System.out.println(String.format("We have %d user project objects", UserProjects.size()));
		
		for (UserProject up : UserProjects) {
			if (up.getUsers().getEmail().equalsIgnoreCase(userMain.getEmail())) {
				currentRole = up.getUserRole();
				System.out.println("Got it!");
				break;
			}
		}
		System.out.println(String.format("Current role is now <%s>", currentRole));
		Contexts.getSessionContext().set("currentRole", currentRole);
		
		return currentRole;
	}
	public void setAndGetUserRole(List<UserProject> UserProjects, Integer projectIDValue) {
		System.out.println("setAndGetUserRole");
		if (projectIDValue == null) {
			return ;
		}
		System.out.println("About to get the user role for project id:" + projectIDValue);
		System.out.println(String.format("We have %d user project objects", UserProjects.size()));
		
		for (UserProject up : UserProjects) {
//			System.out.println("Check project " + up.getProject().getName());
//			System.out.println("Project id is " + up.getProject().getProjectId());
//			System.out.println("Check email " + up.getUsers().getEmail());
//			System.out.println("Role: " + up.getUserRole());
			if (up.getProject().getProjectId().equals(projectIDValue)) {
//				System.out.println("Project name " + up.getProject().getName());
//				System.out.println("Project id is " + up.getProject().getProjectId());
//				System.out.println("Email is " + up.getUsers().getEmail());
				currentRole = up.getUserRole().replaceAll("\n", "");
				break;
			}
		}
		System.out.println(String.format("Setting current role to <%s>", currentRole));
		Contexts.getSessionContext().set("currentRole", currentRole);
		
		return;
	}

	public boolean isAuthorisedToPerformOperation() {
		System.out.println("Returning " + authorisedToPerformOperation);
		return authorisedToPerformOperation;
	}

	public void setAuthorisedToPerformOperation(boolean authorisedToPerformOperation) {
		this.authorisedToPerformOperation = authorisedToPerformOperation;
	}
}