package uk.ac.ox.oucs.vidaas.session;

import java.sql.Connection;
import java.util.List;


import uk.ac.ox.oucs.vidaas.concurrency.DatabaseTablesCreatorThread;
import uk.ac.ox.oucs.vidaas.concurrency.MDBParserThread;
import uk.ac.ox.oucs.vidaas.concurrency.ParseCreateLoaderThread;
import uk.ac.ox.oucs.vidaas.dao.ProjectDatabaseHome;
import uk.ac.ox.oucs.vidaas.dao.ProjectHome;
import uk.ac.ox.oucs.vidaas.dao.UserProjectHome;
import uk.ac.ox.oucs.vidaas.dao.DataspaceHome;
import uk.ac.ox.oucs.vidaas.dao.DatabaseStructureHome;
import uk.ac.ox.oucs.vidaas.dao.WebApplicationHome;
import uk.ac.ox.oucs.vidaas.dao.UsersHome;
import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;

import uk.ac.ox.oucs.vidaas.manager.ConnectionManager;
import uk.ac.ox.oucs.vidaas.session.NavigationController;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

import uk.ac.ox.oucs.vidaas.entity.Logins;
import uk.ac.ox.oucs.vidaas.entity.ProjectDatabase;
import uk.ac.ox.oucs.vidaas.entity.UserProject;
import uk.ac.ox.oucs.vidaas.entity.Users;
import uk.ac.ox.oucs.vidaas.entity.Dataspace;
import uk.ac.ox.oucs.vidaas.entity.Project;
import uk.ac.ox.oucs.vidaas.entity.DatabaseStructure;
import uk.ac.ox.oucs.vidaas.entity.WebApplication;
import uk.ac.ox.oucs.vidaas.entity.UserProjectId;

@Name("createController")
@Scope(ScopeType.SESSION)
@SuppressWarnings("unused")
public class CreateController {

	private Users userMain;
	private Logins loginMain;

	@Logger
	private Log log;

	@In (required = true)
	DataHolder dataHolder;
	
	@In(create = true)
	@Out(required = true)
	UsersHome usersHome;
	
	@In(create = true)
	@Out(required = true)
	ProjectHome projectHome;

	@In(create = true)
	@Out(required = true)
	DataspaceHome dataspaceHome;

	@In(create = true)
	@Out(required = true)
	UserProjectHome userProjectHome;

	@In(create = true)
	@Out(required = true)
	DatabaseStructureHome databaseStructureHome;
	
	@In(create = true)
	@Out(required = true)
	WebApplicationHome webApplicationHome;
	
	@In(create = true)
	@Out(required = true)
	ProjectDatabaseHome projectDatabaseHome;
	
	private boolean statusPanelOKButtonDisabled = true;
	private String databaseSchemaFormStatus = "";
	private String databaseSchemaShortStatus = "";

	private Integer currentProjectID;

	private String createProjectConfirmationMessage = "";
	private String createProjectDataspaceConfirmationMessage = "";
	private String createDatabaseConfirmationMessage = "";
	private String addProjectMemberConfirmationMessage = "";

	public String getCreateProjectConfirmationMessage() {
		return createProjectConfirmationMessage;
	}

	public void setCreateProjectConfirmationMessage(
			String createProjectConfirmationMessage) {
		this.createProjectConfirmationMessage = createProjectConfirmationMessage;
	}

	public String getCreateProjectDataspaceConfirmationMessage() {
		return createProjectDataspaceConfirmationMessage;
	}

	public void setCreateProjectDataspaceConfirmationMessage(
			String createProjectDataspaceConfirmationMessage) {
		this.createProjectDataspaceConfirmationMessage = createProjectDataspaceConfirmationMessage;
	}
	
	public String getCreateDatabaseConfirmationMessage() {
		return createDatabaseConfirmationMessage;
	}

	public void setCreateDatabaseConfirmationMessage(
			String createDatabaseConfirmationMessage) {
		this.createDatabaseConfirmationMessage = createDatabaseConfirmationMessage;
	}
	
	public boolean isStatusPanelOKButtonDisabled() {
		System.out.println("isStatusPanelOKButtonDisabled(): " + dataHolder.isOkButton());
		return dataHolder.isOkButton();
	}

	public void setStatusPanelOKButtonDisabled(boolean statusPanelOKButtonDisabled) {
		this.statusPanelOKButtonDisabled = statusPanelOKButtonDisabled;
	}

	public String getDatabaseSchemaFormStatus() {
		System.out.println("getDatabaseSchemaFormStatus()");
		return dataHolder.getCurrentStatus();
	}

	public void setDatabaseSchemaFormStatus(String databaseSchemaFormStatus) {
		this.databaseSchemaFormStatus = databaseSchemaFormStatus;
	}
	
	public String getDatabaseSchemaShortStatus() {
		return databaseSchemaShortStatus;
	}

	public void setDatabaseSchemaShortStatus(String databaseSchemaShortStatus) {
		this.databaseSchemaShortStatus = databaseSchemaShortStatus;
	}
	
	public String getAddProjectMemberConfirmationMessage() {
		return addProjectMemberConfirmationMessage;
	}

	public void setAddProjectMemberConfirmationMessage(
			String addProjectMemberConfirmationMessage) {
		this.addProjectMemberConfirmationMessage = addProjectMemberConfirmationMessage;
	}

	public void createProject() {
		new CreateProjectController().createProject(getUserMain(), projectHome,
				userProjectHome, log);
		createProjectConfirmationMessage = "Project '"
				+ projectHome.getInstance().getTitle()
				+ "' created successfully.";

		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).createProjectConfirmation();
	}

	public void createDataSpace(/* Integer projectIDValue */) {

		List<UserProject> projectsList = userProjectHome
				.findByUserIDAndProjectID(getUserMain().getUserId(),
						currentProjectID);
		log.info("projectsList.size() {0}", projectsList.size());
		log.info("projectsList.get(0).getUserRole() {0}", projectsList.get(0)
				.getUserRole());
		
		if (projectsList.get(0).getUserRole().equalsIgnoreCase("admin")) {
			new CreateDataSpaceController().createDataSpace(getUserMain(),
					projectsList.get(0).getProject(), dataspaceHome, log);
		}

		createProjectDataspaceConfirmationMessage = "Data Space '"
				+ dataspaceHome.getInstance().getDataspaceName()
				+ "' for Project: '"
				+ projectsList.get(0).getProject().getTitle()
				+ "' is successfully created.";
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).createProjectDataspaceConfirmation();

	}

	public void createDatabaseFromSchema() {
		// log.info("createDatabase {0} {1}", projectIDValue, dataspaceIDValue);
		dataHolder.setOkButton(true);
		Project tempProject = ((Project) Contexts.getSessionContext().get("currentProject"));
		Dataspace tempDataspace = ((Dataspace) Contexts.getSessionContext().get("currentDataspace"));
		log.info("createDatabase {0} {1}", tempProject.getProjectId(), tempDataspace.getDataSpaceId());
/**/
		DatabaseStructure tempDatabaseStructure = databaseStructureHome
				.getInstance();
		
		new CreateDatabaseController().createDatabaseStructure(tempProject.getProjectId(), tempDataspace.getDataspaceName(), tempDatabaseStructure, log);
		
		String databasePersistString = databaseStructureHome.persist();
		log.info("databasePersistString {0}", databasePersistString);
		Contexts.getSessionContext().set("currentDatabaseStructure", tempDatabaseStructure);
		
		WebApplication tempWebApplication = webApplicationHome.getInstance();
		
		String webApplicationPersistString = webApplicationHome.persist();
		log.info("webApplicationPersistString {0}", webApplicationPersistString);
		
		ProjectDatabase tempProjectDatabase = projectDatabaseHome.getInstance();
		new CreateDatabaseController().createDatabase(tempDataspace, tempDatabaseStructure, tempWebApplication, getLoginsMain(), 
				tempProject.getTitle(), tempProjectDatabase, log);
		
		String projectDatabasePersistString = projectDatabaseHome.persist();
		log.info("projectDatabasePersistString {0}", projectDatabasePersistString);
		Contexts.getSessionContext().set("currentProjectDatabase", tempProjectDatabase);
		
		createDatabaseConfirmationMessage = "Database Created";
		/**/
		((NavigationController) Contexts.getSessionContext().get(
		"navigationController")).createDatabaseConfirmation();
		
	}
	
	public void parseDatabase(){
		databaseSchemaShortStatus = "Not Yet Started ...!";
		dataHolder.setOkButton(true);
		((NavigationController) Contexts.getSessionContext().get(
			"navigationController")).createDatabaseInitial();

		
		DatabaseStructure tempDatabaseStructure = (DatabaseStructure) Contexts.getSessionContext().get("currentDatabaseStructure");
		ProjectDatabase tempProjectDatabase = (ProjectDatabase) Contexts.getSessionContext().get("currentProjectDatabase");
		
		databaseStructureHome.setId(tempDatabaseStructure.getStructureId());
		tempDatabaseStructure = databaseStructureHome.getEntityManager().find(DatabaseStructure.class,
				tempDatabaseStructure.getStructureId());
		
		String rootDirectory = tempDatabaseStructure.getDatabaseDirectory();
		String csvDataDirectory = tempDatabaseStructure.getCsvDirectory();
		//String ddlDirecotry = tempDatabaseStructure.getSqlDirectory();		
		String sqlDataDirecotry = rootDirectory + "data/sql/";
		String databaseMDBFile = rootDirectory + tempDatabaseStructure.getFile();
		
		String fileName = tempDatabaseStructure.getFile();
		int index = fileName.indexOf('.');
		
		String databaseMDBFileWithoutExtension = fileName.substring(0, index);
		String databaseSchemaFile = rootDirectory + databaseMDBFileWithoutExtension + ".sql";
		
		databaseSchemaShortStatus = "Starting Parsing: " + databaseMDBFile;
		
		ConnectionManager connectionManager = new ConnectionManager();
		Connection connection = connectionManager.createConnection(
				tempProjectDatabase.getDatabaseName()
						.toLowerCase());
		dataHolder.setCurrentStatus("\nSuccessfully Connected with Database "
				+ dataHolder.getCurrentStatus());
		
		try{
			ParseCreateLoaderThread parseCreateLoaderThread = new ParseCreateLoaderThread(
					databaseMDBFile, databaseSchemaFile, sqlDataDirecotry,
					csvDataDirectory, dataHolder, connection);
			Thread parserThread = new Thread(parseCreateLoaderThread);
			parserThread.start();
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
		tempDatabaseStructure.setStatus(new String("Database_Populated"));
		tempDatabaseStructure.setData(new String("Database_Populated").getBytes());

		// This should be Update
		databaseStructureHome.setInstance(tempDatabaseStructure);
		// databaseStructureHome.update();
		
		databaseStructureHome.persist();
		
		databaseSchemaShortStatus = "Not Yet Started ...!";
	}
	
	public void createProjectMember() {
		addProjectMemberConfirmationMessage = "";
		/*
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).homePageWelcome();
				*/

		Project tempProject = ((Project) Contexts.getSessionContext().get(
				"currentProject"));
		log.info("tempProject.getDescription(): "
				+ tempProject.getDescription());

		Users tempUser = usersHome.getInstance();
		List<Users> userList = usersHome.findUserByEmail(tempUser.getEmail());
		System.out.println("userList.size(): " + userList.size());
		if (userList.size() == 0) {
			// save the user from the form
			
			// I should just return .. if user doesn't exist
			//usersHome.persist();
			addProjectMemberConfirmationMessage = tempUser.getLastName() + ", " + tempUser.getFirstName() + " is not registered member";
			//return;
		} else {
			tempUser = userList.get(0);
		

			UserProjectId userProjectID = new UserProjectId();
			userProjectID.setUserId(tempUser.getUserId());
			userProjectID.setProjectId(tempProject.getProjectId());
	
			System.out.println(tempProject.getProjectId() + "  "
					+ projectHome.getProjectProjectId() + "  "
					+ getUserMain().getUserId());
			UserProject userProject = userProjectHome.getInstance();
			userProject.setId(userProjectID);
			userProject.setProject(tempProject);
			userProject.setUsers(tempUser);
			//userProject.setUserRole("newMemberRole");
	
			addProjectMemberConfirmationMessage = tempUser.getLastName() + ", " + tempUser.getFirstName() + " is added as project member";
			userProjectHome.setInstance(userProject);
			//userProject.
			userProjectHome.persist();
		}
		((NavigationController) Contexts.getSessionContext().get(
			"navigationController")).addProjectMemberConfirmation();
	}

	public Users getUserMain() {
		if (userMain == null) {
			userMain = ((Users) Contexts.getSessionContext().get("userMain"));
		}
		return userMain;
	}
	
	public Logins getLoginsMain(){
		if(loginMain == null){
			loginMain = ((Logins) Contexts.getSessionContext().get("loginMain"));
		}
		return loginMain;
	}

	public void setCurrentProject(Integer currentProjectIDValue) {
		this.currentProjectID = currentProjectIDValue;
	}

}
