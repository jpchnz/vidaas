package uk.ac.ox.oucs.vidaas.session;

import java.sql.Connection;
import java.util.List;

import uk.ac.ox.oucs.vidaas.concurrency.DatabaseTablesCreatorThread;
import uk.ac.ox.oucs.vidaas.concurrency.MDBParserThread;
import uk.ac.ox.oucs.vidaas.concurrency.ParseCreateLoaderThread;
import uk.ac.ox.oucs.vidaas.create.CreateWebApplication;
import uk.ac.ox.oucs.vidaas.dao.ProjectDatabaseHome;
import uk.ac.ox.oucs.vidaas.dao.ProjectHome;
import uk.ac.ox.oucs.vidaas.dao.UserProjectHome;
import uk.ac.ox.oucs.vidaas.dao.DataspaceHome;
import uk.ac.ox.oucs.vidaas.dao.DatabaseStructureHome;
import uk.ac.ox.oucs.vidaas.dao.WebApplicationHome;
import uk.ac.ox.oucs.vidaas.dao.UsersHome;
import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;
import uk.ac.ox.oucs.vidaas.delete.DeleteDatabase;

import uk.ac.ox.oucs.vidaas.manager.ConnectionManager;
import uk.ac.ox.oucs.vidaas.session.NavigationController;
//import uk.ac.ox.oucs.vidaas.utility.LoadXMLContainer;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
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

import java.util.regex.Pattern;

@Name("createController")
@Scope(ScopeType.SESSION)
@SuppressWarnings("unused")
public class CreateController {

	public String testString = "Test String";

	public String getTestString() {
		return testString;
	}

	public void setTestString(String testString) {
		this.testString = testString;
	}

	private Users userMain;
	private Logins loginMain;

	@Logger
	private Log log;

	@In(required = true)
	DataHolder dataHolder;

	private ParseCreateLoaderThread parseCreateLoaderThread = null;

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

	private String validationError = "";

	private boolean statusPanelOKButtonDisabled = true;
	private String databaseSchemaFormStatus = "";
	private String databaseSchemaShortStatus = "";

	private Integer currentProjectID;
	private Integer currentDataspaceID;
	private Integer currentDatabaseID;

	private String createProjectConfirmationMessage = "";
	private String createProjectDataspaceConfirmationMessage = "";
	private String createDatabaseConfirmationMessage = "";
	private String addProjectMemberConfirmationMessage = "";
	private String dropDatabaseConfirmationMessage = "";
	private String backupDatabaseConfirmationMessage = "";

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
		System.out.println("isStatusPanelOKButtonDisabled(): "
				+ dataHolder.isOkButton());
		return dataHolder.isOkButton();
	}

	public void setStatusPanelOKButtonDisabled(
			boolean statusPanelOKButtonDisabled) {
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

	public String getDropDatabaseConfirmationMessage() {
		return dropDatabaseConfirmationMessage;
	}

	public void setDropDatabaseConfirmationMessage(
			String dropDatabaseConfirmationMessage) {
		this.dropDatabaseConfirmationMessage = dropDatabaseConfirmationMessage;
	}

	public String getBackupDatabaseConfirmationMessage() {
		return backupDatabaseConfirmationMessage;
	}

	public void setBackupDatabaseConfirmationMessage(
			String backupDatabaseConfirmationMessage) {
		this.backupDatabaseConfirmationMessage = backupDatabaseConfirmationMessage;
	}

	public String getValidationError() {
		return validationError;
	}

	public void setValidationError(String validationError) {
		this.validationError = validationError;
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
		if (validateString(dataspaceHome.getInstance().getDataspaceName())) {
			if (validateString(dataspaceHome.getInstance()
					.getWebApplicationName())) {

				System.out.println(this.currentProjectID + "   "
						+ getUserMain().getUserId());

				List<UserProject> projectsList = userProjectHome
						.findByUserIDAndProjectID(getUserMain().getUserId(),
								this.currentProjectID);

				log.info("projectsList.size() {0}", projectsList.size());
				log.info("projectsList.get(0).getUserRole() {0}", projectsList
						.get(0).getUserRole());

				if (projectsList.get(0).getUserRole().equalsIgnoreCase("admin")) {
					new CreateDataSpaceController().createDataSpace(
							getUserMain(), projectsList.get(0).getProject(),
							dataspaceHome, log);
				}

				createProjectDataspaceConfirmationMessage = "Data Space '"
						+ dataspaceHome.getInstance().getDataspaceName()
						+ "' for Project: '"
						+ projectsList.get(0).getProject().getTitle()
						+ "' is successfully created.";
				((NavigationController) Contexts.getSessionContext().get(
						"navigationController"))
						.createProjectDataspaceConfirmation();
			} else {
				validationError = "Web Application Name should not contain special character or space";
			}
		} else {
			validationError = "Dataspace Name should not contain special character or space";
		}
	}

	public void createDatabaseFromSchema() {
		// log.info("createDatabase {0} {1}", projectIDValue, dataspaceIDValue);
		dataHolder.setOkButton(true);
		Project tempProject = ((Project) Contexts.getSessionContext().get(
				"currentProject"));
		Dataspace tempDataspace = ((Dataspace) Contexts.getSessionContext()
				.get("currentDataspace"));

		log.info("createDatabase {0} {1}", tempProject.getProjectId(),
				tempDataspace.getDataSpaceId());
		/**/
		DatabaseStructure tempDatabaseStructure = databaseStructureHome
				.getInstance();

		new CreateDatabaseController().createDatabaseStructure(
				tempProject.getProjectId(), tempDataspace.getDataspaceName(),
				tempDatabaseStructure, "main", log);

		String databaseStructurePersistString = databaseStructureHome.persist();
		log.info("databaseStructurePersistString {0}",
				databaseStructurePersistString);
		Contexts.getSessionContext().set("currentDatabaseStructure",
				tempDatabaseStructure);

		WebApplication tempWebApplication = webApplicationHome.getInstance();
		tempWebApplication.setStatus("NotDeployed");

		String webApplicationPersistString = webApplicationHome.persist();
		log.info("webApplicationPersistString {0}", webApplicationPersistString);

		ProjectDatabase tempProjectDatabase = projectDatabaseHome.getInstance();
		new CreateDatabaseController().createDatabase(tempDataspace,
				tempDatabaseStructure, tempWebApplication, getLoginsMain(),
				tempProject.getTitle(), tempProjectDatabase, "main", log);

		try {
			String projectDatabasePersistString = projectDatabaseHome.persist();

			log.info("projectDatabasePersistString {0}",
					projectDatabasePersistString);
			Contexts.getSessionContext().set("currentProjectDatabase",
					tempProjectDatabase);
		} catch (InvalidStateException ise) {
			InvalidValue[] iv = ise.getInvalidValues();
			for (int i = 0; i < iv.length; i++) {
				System.out.println("Property Name: " + iv[i].getPropertyName());
				System.out.println("Property Name Message: "
						+ iv[i].getMessage());
			}
		}

		createDatabaseConfirmationMessage = "Database Created";
		/**/
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).createDatabaseConfirmation();
	}

	public void cloneDatabase(Integer currentDatabaseIDValue, String cloneType) {

		((NavigationController) Contexts.getSessionContext().get(
				"navigationController"))
				.setCurrentDatabase(currentDatabaseIDValue);

		Project tempProject = ((Project) Contexts.getSessionContext().get(
				"currentProject"));

		Dataspace tempDataspace = ((Dataspace) Contexts.getSessionContext()
				.get("currentDataspace"));

		ProjectDatabase tempOldDatabase = ((ProjectDatabase) Contexts
				.getSessionContext().get("currentProjectDatabase"));

		log.info(
				"Project ID {0}, Dataspace ID {1}, Database ID {2}, old database name {3}",
				tempProject.getProjectId(), tempDataspace.getDataSpaceId(),
				tempOldDatabase.getDatabaseId(),
				tempOldDatabase.getDatabaseName());

		DatabaseStructure tempDatabaseStructure = databaseStructureHome
				.getInstance();

		new CreateDatabaseController().cloneDatabaseStructure(
				tempProject.getProjectId(), tempDataspace.getDataspaceName(),
				tempDatabaseStructure, tempOldDatabase.getDatabaseStructure(),
				cloneType, log);

		String databaseStructurePersistString = databaseStructureHome.persist();
		log.info("databasePersistString {0}", databaseStructurePersistString);
		/*
		 * new CreateDatabaseController().dumpDatabase(
		 * tempDatabaseStructure.getDatabaseDirectory() +
		 * tempOldDatabase.getDatabaseName() +".sql",
		 * tempOldDatabase.getDatabaseName());
		 */

		Contexts.getSessionContext().set("currentDatabaseStructure",
				tempDatabaseStructure);

		WebApplication tempWebApplication = webApplicationHome.getInstance();
		tempWebApplication.setStatus("NotDeployed");

		String webApplicationPersistString = webApplicationHome.persist();
		log.info("webApplicationPersistString {0}", webApplicationPersistString);

		projectDatabaseHome.clearInstance();
		ProjectDatabase newProjectDatabase = projectDatabaseHome.getInstance();
		new CreateDatabaseController().cloneDatabase(tempDataspace,
				tempDatabaseStructure, tempWebApplication, getLoginsMain(),
				tempProject.getTitle(), newProjectDatabase, tempOldDatabase,
				cloneType, log);

		String projectDatabasePersistString = projectDatabaseHome.persist();
		log.info("projectDatabasePersistString {0}",
				projectDatabasePersistString);

		backupDatabaseConfirmationMessage = "'"
				+ tempOldDatabase.getDatabaseName() + "' is mirrored as '"
				+ newProjectDatabase.getDatabaseName()
				+ "'. The new database can be accessed with '"
				+ newProjectDatabase.getConnectionString() + "'";

		if (cloneType.equalsIgnoreCase("old")) {
			((NavigationController) Contexts.getSessionContext().get(
					"navigationController")).backupDatabaseConfirmation();
		} else {
			((NavigationController) Contexts.getSessionContext().get(
					"navigationController")).testDatabaseConfirmation();
		}
		/*		*/
	}

	public void parseDatabase() {
		databaseSchemaShortStatus = "\n Not Yet Started ...!";
		dataHolder.currentStatus = "";
		dataHolder.setOkButton(true);
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).createDatabaseInitial();

		DatabaseStructure tempDatabaseStructure = (DatabaseStructure) Contexts
				.getSessionContext().get("currentDatabaseStructure");
		ProjectDatabase tempProjectDatabase = (ProjectDatabase) Contexts
				.getSessionContext().get("currentProjectDatabase");

		databaseStructureHome.setId(tempDatabaseStructure.getStructureId());
		tempDatabaseStructure = databaseStructureHome.getEntityManager()
				.find(DatabaseStructure.class,
						tempDatabaseStructure.getStructureId());

		String rootDirectory = tempDatabaseStructure.getDatabaseDirectory();
		String csvDataDirectory = tempDatabaseStructure.getCsvDirectory();
		// String ddlDirecotry = tempDatabaseStructure.getSqlDirectory();
		String sqlDataDirecotry = rootDirectory + "data/sql/";
		String databaseMDBFile = rootDirectory
				+ tempDatabaseStructure.getFile();

		String fileName = tempDatabaseStructure.getFile();
		int index = fileName.indexOf('.');

		String databaseMDBFileWithoutExtension = fileName.substring(0, index);
		String databaseSchemaFile = rootDirectory
				+ databaseMDBFileWithoutExtension + ".sql";

		databaseSchemaShortStatus = "Starting Parsing: " + databaseMDBFile;

		ConnectionManager connectionManager = new ConnectionManager();
		Connection connection = connectionManager
				.createConnection(tempProjectDatabase.getDatabaseName()
						.toLowerCase());
		dataHolder.setCurrentStatus("\nSuccessfully Connected with Database "
				+ dataHolder.getCurrentStatus());

		try {
			parseCreateLoaderThread = new ParseCreateLoaderThread(
					databaseMDBFile, databaseSchemaFile, sqlDataDirecotry,
					csvDataDirectory, dataHolder, connection);
			Thread parserThread = new Thread(parseCreateLoaderThread);
			parserThread.start();
			/*
			 * while (parserThread.isAlive()){
			 * 
			 * }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * // if(parseCreateLoaderThread.isParsingStatus() == true){
		 * tempDatabaseStructure.setStatus(new String("Database_Populated"));
		 * tempDatabaseStructure.setData(new String("Database_Populated")
		 * .getBytes());
		 * 
		 * // This should be Update
		 * databaseStructureHome.setInstance(tempDatabaseStructure); //
		 * databaseStructureHome.update();
		 * 
		 * databaseStructureHome.persist(); // } else { //
		 * databaseStructureHome.setInstance(tempDatabaseStructure); //
		 * databaseStructureHome.remove();
		 * 
		 * // dropDatabase(tempProjectDatabase.getDatabaseName());
		 * 
		 * // }
		 * 
		 * databaseSchemaShortStatus = "Not Yet Started ...!";
		 */
	}

	public void finishParseDatabase() {
		DatabaseStructure tempDatabaseStructure = (DatabaseStructure) Contexts
				.getSessionContext().get("currentDatabaseStructure");
		ProjectDatabase tempProjectDatabase = (ProjectDatabase) Contexts
				.getSessionContext().get("currentProjectDatabase");

		if (parseCreateLoaderThread.isParsingStatus() == true) {
			databaseStructureHome.setId(tempDatabaseStructure.getStructureId());
			tempDatabaseStructure = databaseStructureHome.find();

			tempDatabaseStructure.setStatus(new String("Database_Populated"));
			tempDatabaseStructure.setData(new String("Database_Populated")
					.getBytes());

			// This should be Update
			databaseStructureHome.setInstance(tempDatabaseStructure);
			// databaseStructureHome.update();

			databaseStructureHome.persist();
		} else {

			dropDatabase(tempProjectDatabase.getDatabaseName());

		}
	}

	public void createWebApplication(Integer projectDatabaseIDValue) {
		projectDatabaseHome.setId(projectDatabaseIDValue);
		ProjectDatabase tempProjectDatabase = projectDatabaseHome.find();
		
		projectDatabaseHome.setInstance(tempProjectDatabase);
		
		String webApplicationName = tempProjectDatabase.getDataspace().getWebApplicationName();
		String webApplicationLocation = tempProjectDatabase.getDatabaseStructure().getDatabaseDirectory();
		String databaseName = tempProjectDatabase.getDatabaseName();
		
		String userName = getLoginsMain().getUserName();
		String password = getLoginsMain().getPassword();
		
		CreateWebApplication createWebApplication = new CreateWebApplication();
		createWebApplication.createWebApplication(webApplicationName, webApplicationLocation, databaseName, userName, password);
		
		webApplicationHome.setId(tempProjectDatabase.getWebApplication().getWebId());
		WebApplication tempWebApplication = webApplicationHome.find();
		
		webApplicationHome.setInstance(tempWebApplication);
		
		tempWebApplication.setStatus("Deployed");
		
		webApplicationHome.persist();
		
	}

	public void removeWebApplication(Integer projectDatabaseIDValue) {
		/*projectDatabaseHome.setId(projectDatabaseIDValue);
		ProjectDatabase tempProjectDatabase = projectDatabaseHome.find();
		
		projectDatabaseHome.setInstance(tempProjectDatabase);
		
		webApplicationHome.setId(tempProjectDatabase.getWebApplication().getWebId());
		WebApplication tempWebApplication = webApplicationHome.find();
		
		webApplicationHome.setInstance(tempWebApplication);
		
		tempWebApplication.setStatus("NotDeployed");
		
		webApplicationHome.persist();
		*/
		/*
		try {
			new LoadXMLContainer().loadXMLContainer();
		} catch (Throwable e) {
			e.printStackTrace();
		}*/
	}

	public void createProjectMember() {
		addProjectMemberConfirmationMessage = "";
		/*
		 * ((NavigationController) Contexts.getSessionContext().get(
		 * "navigationController")).homePageWelcome();
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
			// usersHome.persist();
			addProjectMemberConfirmationMessage = tempUser.getLastName() + ", "
					+ tempUser.getFirstName() + " is not registered member";
			// return;
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
			// userProject.setUserRole("newMemberRole");

			addProjectMemberConfirmationMessage = tempUser.getLastName() + ", "
					+ tempUser.getFirstName() + " is added as project member";
			userProjectHome.setInstance(userProject);
			// userProject.
			userProjectHome.persist();
		}
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).addProjectMemberConfirmation();
	}

	public void dropDatabase(String databaseName) {
		log.info("dropDatabase {0}", databaseName);
		new DeleteDatabase(databaseName).DeleteDatabase();

		ProjectDatabase tempProjectDatabase = (ProjectDatabase) Contexts
				.getSessionContext().get("currentProjectDatabase");
		DatabaseStructure tempDatabaseStructure = tempProjectDatabase
				.getDatabaseStructure();

		webApplicationHome.setId(tempProjectDatabase.getWebApplication()
				.getWebId());

		projectDatabaseHome.setId(tempProjectDatabase.getDatabaseId());
		projectDatabaseHome.find();
		String projectDatabaseRemoveString = projectDatabaseHome.remove();

		webApplicationHome.find();
		webApplicationHome.remove();

		/**/
		databaseStructureHome.setId(tempDatabaseStructure.getStructureId());
		databaseStructureHome.find();
		String databaseStructureRemoveString = databaseStructureHome.remove();

		System.out.println(projectDatabaseRemoveString + "  "
				+ databaseStructureRemoveString);

		this.dropDatabaseConfirmationMessage = "The database with name '"
				+ databaseName + "' successfully deleted.";

		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).dropDatabaseConfirmation();
	}

	public Users getUserMain() {
		if (userMain == null) {
			userMain = ((Users) Contexts.getSessionContext().get("userMain"));
		}
		return userMain;
	}

	public Logins getLoginsMain() {
		if (loginMain == null) {
			loginMain = ((Logins) Contexts.getSessionContext().get("loginMain"));
		}
		return loginMain;
	}

	public void setCurrentProject(Integer currentProjectIDValue) {
		System.out.println(" ********************     setCurrentProject: "
				+ currentProjectIDValue);
		this.currentProjectID = currentProjectIDValue;
	}

	public void setCurrentDataspace(Integer currentDataspaceIDValue) {
		this.currentDataspaceID = currentDataspaceIDValue;
	}

	public void setCurrentDatabase(Integer currentDatabaseIDValue) {
		this.currentDatabaseID = currentDatabaseIDValue;
	}

	public void setCurrentDataspaceAndDatabase(Integer currentDataspaceIDValue,
			Integer currentDatabaseIDValue) {
		this.currentDataspaceID = currentDataspaceIDValue;
		this.currentDatabaseID = currentDatabaseIDValue;
	}

	public void clearDataspaceMessages() {
		log.info("clearDataspaceMessages()");

		// dataspaceHome.getInstance().setCreationDate(null);
		// dataspaceHome.getInstance().setDataspaceName("");
		dataspaceHome.setInstance(null);
		createProjectDataspaceConfirmationMessage = "";
		validationError = "";
	}

	/**/
	public boolean hasBackupDatabase(Integer currentDataspaceIDValue) {
		projectDatabaseHome.setId(currentDataspaceIDValue);
		try {
			ProjectDatabase tempProjectDB = projectDatabaseHome.getInstance();
			List<ProjectDatabase> tempProjectDBList = projectDatabaseHome
					.findByDataspaceID(tempProjectDB.getDataspace()
							.getDataSpaceId());
			if (tempProjectDBList.size() > 1) {
				for (int i = 0; i < tempProjectDBList.size(); i++) {
					if (tempProjectDBList.get(i).getDatabaseType()
							.equalsIgnoreCase("old"))
						return false;
				}
			}
		} catch (Exception e) {

		}
		return true;
	}

	public boolean hasTestDatabase(Integer currentDataspaceIDValue) {
		projectDatabaseHome.setId(currentDataspaceIDValue);
		try {
			ProjectDatabase tempProjectDB = projectDatabaseHome.getInstance();
			List<ProjectDatabase> tempProjectDBList = projectDatabaseHome
					.findByDataspaceID(tempProjectDB.getDataspace()
							.getDataSpaceId());

			if (tempProjectDBList.size() > 1) {
				for (int i = 0; i < tempProjectDBList.size(); i++) {
					if (tempProjectDBList.get(i).getDatabaseType()
							.equalsIgnoreCase("test"))
						return false;
				}
			}
		} catch (Exception e) {

		}
		return true;
	}

	public boolean addNewDataspaceAllowed(Integer currentProjectIDValue) {
		projectHome.setId(currentProjectIDValue);
		Project tempProject = projectHome.getInstance();
		// log.info("Project ID: {0} Number of Dataspaces: {1}",
		// currentProjectIDValue, tempProject.getDataspaces().size());
		if (tempProject.getDataspaces().size() > 2) {
			return false;
		}
		return true;
	}

	public boolean hasMainDatabase(Integer currentDataspaceIDValue) {
		projectDatabaseHome.setId(currentDataspaceIDValue);
		try {
			ProjectDatabase tempProjectDB = projectDatabaseHome.getInstance();
			List<ProjectDatabase> tempProjectDBList = projectDatabaseHome
					.findByDataspaceID(tempProjectDB.getDataspace()
							.getDataSpaceId());

			if (tempProjectDBList.size() > 1) {
				for (int i = 0; i < tempProjectDBList.size(); i++) {
					if (tempProjectDBList.get(i).getDatabaseType()
							.equalsIgnoreCase("main"))
						return false;
				}
			}
		} catch (Exception e) {

		}
		return true;
	}

	private boolean validateString(String enteredValue) {
		Pattern alphaNumericPattern = Pattern.compile("^[A-Za-z0-9]+$");
		return alphaNumericPattern.matcher(enteredValue).matches();
	}

	int counterTest = 100;

	public void testingString() {
		testString = testString + counterTest++;
	}

}
