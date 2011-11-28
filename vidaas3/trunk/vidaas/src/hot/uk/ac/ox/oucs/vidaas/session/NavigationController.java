package uk.ac.ox.oucs.vidaas.session;

import java.util.ArrayList;
import java.util.List;
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

	@RequestParameter("projectIDValue")
	private Integer projectIDValue;

	@RequestParameter("dataspaceIDValue")
	private Integer dataspaceIDValue;

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

	public void setCreateWebApplicationInclude(String createWebApplicationInclude) {
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

	public void setRemoveWebApplicationInclude(String removeWebApplicationInclude) {
		this.removeWebApplicationInclude = removeWebApplicationInclude;
	}

	public boolean isRemoveWebApplicationFormRender() {
		return removeWebApplicationFormRender;
	}

	public void setRemoveWebApplicationFormRender(
			boolean removeWebApplicationFormRender) {
		this.removeWebApplicationFormRender = removeWebApplicationFormRender;
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
		log.info("listUserProjects {0}", homePageMainBodyNavigation);
		if (getUserMain() != null) {
			homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
		} else {
			homePageMainBodyNavigation = "homeWelcome.xhtml";
		}
		log.info("listUserProjects {0}", homePageMainBodyNavigation);
	}

	public List<UserProject> userProjectsList() {
		List<UserProject> userProjectsList = null;
		if (getUserMain() != null) {
			userProjectsList = userProjectHome.findByUserID(getUserMain()
					.getUserId());
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
		Set<ProjectDatabase> tempProjectDatabaseSet = currentDataspace
				.getProjectDatabases();
		log.info("currentDataspace.getDataSpaceId() {0} Number of Databases: {1}",
				currentDataspace.getDataSpaceId(),
				tempProjectDatabaseSet.size());

		List<ProjectDatabase> list = new ArrayList<ProjectDatabase>(
				tempProjectDatabaseSet);
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
		log.info(
				"listProjectDataspaces ***************************************   dataspaceIDValue:  {0} {1}",
				dataspacesList.size(), projectIDValue);
				*/
		return dataspacesList;
	}

	public void addProjectMemberInitial() {
		projectMemberFormRender = false;
		//log.info("addProjectMemberInitial() ................................................................... addProjectMemberInitial()");
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
		homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}

	public void dropDatabaseConfirmation() {
		System.out.println("dropDatabaseConfirmation: ");
		this.dropDatabaseFormRender = true;
		dropDatabaseInclude = "/popup/dropDatabaseConfirmation.xhtml";
	}

	public void backupDatabaseInitial() {
		backupDatabaseFormRender = false;
		backupDatabaseInclude = "/popup/backupDatabaseForm.xhtml";
		homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}

	public void backupDatabaseConfirmation() {
		backupDatabaseFormRender = true;
		backupDatabaseInclude = "/popup/backupDatabaseConfirmation.xhtml";
	}
	
	public void restoreDatabaseInitial() {
		restoreDatabaseFormRender = false;
		restoreDatabaseInclude = "/popup/restoreDatabaseForm.xhtml";
		homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}
	
	public void restoreDatabaseConfirmation() {
		restoreDatabaseFormRender = true;
		restoreDatabaseInclude = "/popup/restoreDatabaseConfirmation.xhtml";
	}
	
	public void createWebApplicationInitial() {
		createWebApplicationFormRender = false;
		createWebApplicationInclude = "/popup/createWebApplicationForm.xhtml";
		homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}
	
	public void createWebApplicationConfirmation() {
		createWebApplicationFormRender = true;
		createWebApplicationInclude = "/popup/createWebApplicationConfirmation.xhtml";
	}
	
	public void removeWebApplicationInitial() {
		removeWebApplicationFormRender = false;
		removeWebApplicationInclude = "/popup/removeWebApplicationForm.xhtml";
		homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}
	
	public void removeWebApplicationConfirmation() {
		removeWebApplicationFormRender = true;
		removeWebApplicationInclude = "/popup/removeWebApplicationConfirmation.xhtml";
	}

	public void testDatabaseInitial() {
		testDatabaseFormRender = false;
		testDatabaseInclude = "/popup/testDatabaseForm.xhtml";
		homePageMainBodyNavigation = "/custom/projectByUserList.xhtml";
	}

	public void testDatabaseConfirmation() {
		testDatabaseFormRender = true;
		testDatabaseInclude = "/popup/testDatabaseConfirmation.xhtml";
	}

	public void singleDataspaceDisplayPage() {
		log.info("singleDatabaseDisplayPage dataspaceIDValue: {0} projectIDValue: {1}", dataspaceIDValue, projectIDValue);		

		projectHome.setId(projectIDValue);
		currentProject = projectHome.getInstance();
		Contexts.getSessionContext().set("currentProject", currentProject);

		dataspaceHome.setId(dataspaceIDValue);
		currentDataspace = dataspaceHome.getInstance();
		Contexts.getSessionContext().set("currentDataspace", currentDataspace);

		homePageMainBodyNavigation = "/custom/singleDataspaceByProject.xhtml";
	}

	public void createProjectMember() {
		log.info("createProjectMember projectIDValue: "
				+ projectIDValue);
		if (projectIDValue != null) {
			Contexts.getSessionContext().set("projectIDValue", projectIDValue);
		} else {
			projectIDValue = (Integer) Contexts.getSessionContext().get(
					"projectIDValue");
		}

		projectHome.setId(projectIDValue);
		currentProject = projectHome.getInstance();
		Contexts.getSessionContext().set("currentProject", currentProject);
		// homePageMainBodyNavigation = "/custom/createUserForm.xhtml";
	}

	public boolean setCurrentDatabase(Integer currentDatabaseIDValue) {
		Contexts.getSessionContext().set("currentProjectDatabase",
				null);
		log.info("setCurrentDatabase  currentDatabaseIDValue: "
				+ currentDatabaseIDValue);
		projectDatabaseHome.setId(currentDatabaseIDValue);
		this.currentProjectDatabase = projectDatabaseHome.getInstance();
		//log.info("Database Name: {0}", currentProjectDatabase.getDatabaseName());
/**/
		Contexts.getSessionContext().set("currentProjectDatabase",
				this.currentProjectDatabase);
		return true;
	}

	public ProjectDatabase getCurrentProjectDatabase() {
		log.info(" *******************************************************   getCurrentProjectDatabase()");
		if(currentProjectDatabase != null)
			log.info("getCurrentProjectDatabase() Database Name: {0}", currentProjectDatabase.getDatabaseName());
		return currentProjectDatabase;
	}

	public void setCurrentProjectDatabase(ProjectDatabase currentProjectDatabase) {
		this.currentProjectDatabase = currentProjectDatabase;
	}
	
	public void actionTest(){
		log.info("actionTest()");
	}
	
	public void onCompleteTest(){
		log.info("onCompleteTest()");
	}
	

}
