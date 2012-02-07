package uk.ac.ox.oucs.vidaas.session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import uk.ac.ox.oucs.vidaas.concurrency.CreateWebApplicationThread;
import uk.ac.ox.oucs.vidaas.concurrency.DatabaseTablesCreatorThread;
import uk.ac.ox.oucs.vidaas.concurrency.MDBParserThread;
import uk.ac.ox.oucs.vidaas.concurrency.ParseCreateLoaderThread;
import uk.ac.ox.oucs.vidaas.concurrency.CreateBerkeleyXMLDatabaseThread;
import uk.ac.ox.oucs.vidaas.create.CreateBerkeleyXMLDatabase;
import uk.ac.ox.oucs.vidaas.create.CreateWebApplication;
import uk.ac.ox.oucs.vidaas.dao.ProjectDatabaseHome;
import uk.ac.ox.oucs.vidaas.dao.ProjectHome;
import uk.ac.ox.oucs.vidaas.dao.UserProjectHome;
import uk.ac.ox.oucs.vidaas.dao.DataspaceHome;
import uk.ac.ox.oucs.vidaas.dao.DatabaseStructureHome;
import uk.ac.ox.oucs.vidaas.dao.WebApplicationHome;
import uk.ac.ox.oucs.vidaas.dao.UsersHome;
import uk.ac.ox.oucs.vidaas.dao.XMLFilesHome;
import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;
import uk.ac.ox.oucs.vidaas.delete.DeleteDatabase;
import uk.ac.ox.oucs.vidaas.delete.DeleteWebApplication;
import uk.ac.ox.oucs.vidaas.delete.DeleteXMLFileFromDatabase;
import uk.ac.ox.oucs.vidaas.download.DownloadDatabase;

import uk.ac.ox.oucs.vidaas.manager.ConnectionManager;
import uk.ac.ox.oucs.vidaas.session.NavigationController;
import uk.ac.ox.oucs.vidaas.utility.LoadXMLContainer;

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
import uk.ac.ox.oucs.vidaas.entity.XMLFiles;

import java.util.regex.Pattern;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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
	private CreateWebApplicationThread createWebApplicationThread = null;

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

	@In(create = true)
	@Out(required = true)
	XMLFilesHome xmlFilesHome;

	private CreateBerkeleyXMLDatabaseThread createBerkeleyXMLDatabaseThread;

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
	private String restoreDatabaseConfirmationMessage = "";
	private String deleteWebApplicationConfirmationMessage = "";

	private Date today;

	public Date getToday() {
		return new Date();
	}

	public void setToday(Date today) {
		this.today = today;
	}

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
		/*
		 * System.out.println("isStatusPanelOKButtonDisabled(): " +
		 * dataHolder.isOkButton());
		 */
		return dataHolder.isOkButton();
	}

	public void setStatusPanelOKButtonDisabled(
			boolean statusPanelOKButtonDisabled) {
		this.statusPanelOKButtonDisabled = statusPanelOKButtonDisabled;
	}

	public String getDatabaseSchemaFormStatus() {
		// System.out.println("getDatabaseSchemaFormStatus()");
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

	public String getRestoreDatabaseConfirmationMessage() {
		return restoreDatabaseConfirmationMessage;
	}

	public void setRestoreDatabaseConfirmationMessage(
			String restoreDatabaseConfirmationMessage) {
		this.restoreDatabaseConfirmationMessage = restoreDatabaseConfirmationMessage;
	}

	public String getDeleteWebApplicationConfirmationMessage() {
		return deleteWebApplicationConfirmationMessage;
	}

	public void setDeleteWebApplicationConfirmationMessage(
			String deleteWebApplicationConfirmationMessage) {
		this.deleteWebApplicationConfirmationMessage = deleteWebApplicationConfirmationMessage;
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
				+ projectHome.getInstance().getName()
				+ "' created successfully.";

		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).createProjectConfirmation();
	}

	public void createDataSpace(/* Integer projectIDValue */) {
		// if
		// (validateString(dataspaceHome.getInstance().getDataspaceUserFriendlyName()))
		// {
		if (validateString(dataspaceHome.getInstance().getWebApplicationName())) {

			System.out.println(this.currentProjectID + "   "
					+ getUserMain().getUserId());

			List<UserProject> projectsList = userProjectHome
					.findByUserIDAndProjectID(getUserMain().getUserId(),
							this.currentProjectID);

			log.info("projectsList.size() {0}", projectsList.size());
			log.info("projectsList.get(0).getUserRole() {0}",
					projectsList.get(0).getUserRole());

			if (projectsList.get(0).getUserRole().equalsIgnoreCase("admin")
					|| projectsList.get(0).getUserRole()
							.equalsIgnoreCase("Owner")) {
				new CreateDataSpaceController().createDataSpace(getUserMain(),
						projectsList.get(0).getProject(), dataspaceHome, today,
						log);
			}

			createProjectDataspaceConfirmationMessage = "Databace '"
					+ dataspaceHome.getInstance().getDataspaceName()
					+ "' for the Project: '"
					+ projectsList.get(0).getProject().getTitle()
					+ "' has been successfully created.";

			((NavigationController) Contexts.getSessionContext().get(
					"navigationController"))
					.createProjectDataspaceConfirmation();
		} else {
			validationError = "Database interface name should not contain special characters or spaces";
		}
		/*
		 * } else { validationError =
		 * "Databace Name should not contain special character or space"; }
		 */
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
		tempDatabaseStructure.setCreationDate(today);

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
		tempProjectDatabase.setCreationDate(today);
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
		} else if (cloneType.equalsIgnoreCase("test")) {
			((NavigationController) Contexts.getSessionContext().get(
					"navigationController")).testDatabaseConfirmation();
		} else {
			((NavigationController) Contexts.getSessionContext().get(
					"navigationController")).restoreDatabaseConfirmation();
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
		Connection connection = connectionManager.createConnection(
				tempProjectDatabase.getDatabaseName().toLowerCase(),
				getLoginsMain().getUserName().toLowerCase(), getLoginsMain()
						.getPassword());
		dataHolder.setCurrentStatus("\nSuccessfully Connected with Database "
				+ dataHolder.getCurrentStatus());

		try {
			parseCreateLoaderThread = new ParseCreateLoaderThread(
					databaseMDBFile, databaseSchemaFile, sqlDataDirecotry,
					csvDataDirectory, dataHolder, connection);
			Thread parserThread = new Thread(parseCreateLoaderThread);
			parserThread.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
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
		dataHolder.setOkButton(true);
		dataHolder.currentStatus = "";
		log.info("createWebApplication() called "  + "" +  projectDatabaseIDValue);
		this.currentDatabaseID = projectDatabaseIDValue;
		
		String serverURLTemp = System.getProperty("serverURL");

		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).createWebApplicationConfirmation();

		projectDatabaseHome.setId(projectDatabaseIDValue);
		ProjectDatabase tempProjectDatabase = projectDatabaseHome.find();

		projectDatabaseHome.setInstance(tempProjectDatabase);

		String webApplicationName = tempProjectDatabase.getDataspace()
				.getWebApplicationName();

		if (tempProjectDatabase.getDatabaseType().equalsIgnoreCase("old")) {
			webApplicationName = webApplicationName + "_old";
		} else if (tempProjectDatabase.getDatabaseType().equalsIgnoreCase(
				"test")) {
			webApplicationName = webApplicationName + "_test";
		}

		String webApplicationLocation = tempProjectDatabase
				.getDatabaseStructure().getDatabaseDirectory();
		String databaseName = tempProjectDatabase.getDatabaseName();

		String userName = getLoginsMain().getUserName();
		String password = getLoginsMain().getPassword();

		try {
			webApplicationHome.setId(tempProjectDatabase.getWebApplication()
					.getWebId());
			WebApplication tempWebApplication = webApplicationHome.find();

			webApplicationHome.setInstance(tempWebApplication);

			
			tempWebApplication.setWebApplicationName(webApplicationName);

			tempWebApplication.setUrl(serverURLTemp + webApplicationName);
			
			createWebApplicationThread = new CreateWebApplicationThread(
					webApplicationName, webApplicationLocation, databaseName,
					userName.toLowerCase(), password, dataHolder);

			Thread webApplicationCreaterThread = new Thread(
					createWebApplicationThread);
			webApplicationCreaterThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			dataHolder.currentStatus = "Failed to initiate Data Interface creation process";
			dataHolder.setOkButton(false);
		}
		//dataHolder.setOkButton(false);
	}

	public void finishCreateWebApplication() {
		log.info("finishCreateWebApplication() called "  + "" +  this.currentDatabaseID);
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).createWebApplicationInitial();
		
		log.info("finishCreateWebApplication() " + createWebApplicationThread.isCreateStatus());
		
		projectDatabaseHome.setId(this.currentDatabaseID);
		ProjectDatabase tempProjectDatabase = projectDatabaseHome.find();

		projectDatabaseHome.setInstance(tempProjectDatabase);
		
		webApplicationHome.setId(tempProjectDatabase.getWebApplication()
				.getWebId());
		
		WebApplication tempWebApplication = webApplicationHome.find();

		webApplicationHome.setInstance(tempWebApplication);
		
		if(createWebApplicationThread.isCreateStatus() == true){
			tempWebApplication.setStatus("Deployed");
			webApplicationHome.persist();
		} else {
			tempWebApplication.setStatus("NotDeployed");
			webApplicationHome.persist();
		}
		dataHolder.currentStatus = "";
	}

	public void removeWebApplication(Integer projectDatabaseIDValue) {

		projectDatabaseHome.setId(projectDatabaseIDValue);
		ProjectDatabase tempProjectDatabase = projectDatabaseHome.find();

		projectDatabaseHome.setInstance(tempProjectDatabase);

		String webApplicationNameTemp = projectDatabaseHome.getInstance()
				.getDataspace().getWebApplicationName();

		if (new DeleteWebApplication()
				.undeployWebApplication(webApplicationNameTemp)) {

			webApplicationHome.setId(tempProjectDatabase.getWebApplication()
					.getWebId());
			WebApplication tempWebApplication = webApplicationHome.find();

			webApplicationHome.setInstance(tempWebApplication);

			tempWebApplication.setStatus("NotDeployed");

			webApplicationHome.persist();

			deleteWebApplicationConfirmationMessage = "Web Application "
					+ webApplicationNameTemp + " successfully removed";
		} else {
			deleteWebApplicationConfirmationMessage = "Failed to remove Web Application "
					+ webApplicationNameTemp;
		}

		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).removeWebApplicationConfirmation();

	}

	public void finishRemoveWebApplication() {
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).removeWebApplicationInitial();
		dataHolder.currentStatus = "";
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

			try {
				userProjectHome.setInstance(userProject);
				// userProject.
				userProjectHome.persist();
				addProjectMemberConfirmationMessage = tempUser.getLastName()
						+ ", " + tempUser.getFirstName()
						+ " is added as project member";
			} catch (Exception e) {
				// org.hibernate.exception.ConstraintViolationException
				addProjectMemberConfirmationMessage = tempUser.getLastName()
						+ ", " + tempUser.getFirstName()
						+ " is already project member";
			}

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

	public void downloadDatabase(/* Integer currentDatabaseIDValue */) {
		log.info("downloadDatabase( ) Database ID: "
				+ ((NavigationController) Contexts.getSessionContext().get(
						"navigationController")).getDatabaseIDValue());
		log.info("downloadDatabase( ) Project ID: "
				+ ((NavigationController) Contexts.getSessionContext().get(
						"navigationController")).getProjectIDValue());
		log.info("downloadDatabase( ) Dataspace ID: "
				+ ((NavigationController) Contexts.getSessionContext().get(
						"navigationController")).getDataspaceIDValue());

		/*
		 * projectDatabaseHome.setId(currentDatabaseIDValue); ProjectDatabase
		 * tempDatabase = projectDatabaseHome.getInstance();
		 */
		/* */

		Integer tempDatabaseID = ((NavigationController) Contexts
				.getSessionContext().get("navigationController"))
				.getDatabaseIDValue();
		if (tempDatabaseID != null) {
			projectDatabaseHome.setId(tempDatabaseID);

			ProjectDatabase tempDatabase = projectDatabaseHome.getInstance();

			if (tempDatabase != null) {
				log.info("downloadDatabase( ) tempDatabase ID: "
						+ tempDatabase.getDatabaseId());

				Project tempProject = ((Project) Contexts.getSessionContext()
						.get("currentProject"));

				if (tempDatabase != null) {
					log.info("downloadDatabase( ) projectID: "
							+ tempProject.getProjectId());

					Dataspace tempDataspace = ((Dataspace) Contexts
							.getSessionContext().get("currentDataspace"));

					log.info("downloadDatabase( ) tempDataspaceID: "
							+ tempDataspace.getDataSpaceId());

					if (tempDatabase != null) {
						log.info(
								"Project ID {0}, Dataspace ID {1}, Database ID {2}, old database name {3}",
								tempProject.getProjectId(),
								tempDataspace.getDataSpaceId(),
								tempDatabase.getDatabaseId(),
								tempDatabase.getDatabaseName());

						String fileNameWithLocation = new DownloadDatabase()
								.dumpDatabaseForDownload(
										tempProject.getProjectId(),
										tempDataspace.getDataspaceName(),
										tempDatabase.getDatabaseName(),

										tempDatabase.getDatabaseType(), log);

						/* */
						try {
							downloadCSVFile(fileNameWithLocation);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	public void downloadCSVFile(String csvFileName) throws IOException {

		// String csvFileName =
		// "/opt/VIDaaSData/project_1/Rivers/main/romaneconomy_rivers.csv";
		log.info("downloadCSVFile () method called");
		File file = new File(csvFileName);
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		log.info("fileSize: " + length);

		if (length < Integer.MAX_VALUE) {
			byte[] bytes = new byte[(int) length];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;

				if (offset < bytes.length) {
					throw new IOException("Could not completely read file "
							+ file.getName());
				}
				is.close();

				HttpServletResponse response = (HttpServletResponse) FacesContext
						.getCurrentInstance().getExternalContext()
						.getResponse();
				response.setContentType("application/data");
				response.addHeader("Content-Disposition",
						"attachment;filename=\"" + csvFileName + ".csv\"");
				response.addHeader("Cache-Control", "no-cache");
				response.setStatus(200);

				try {
					ServletOutputStream os = response.getOutputStream();
					os.write(bytes);
					os.flush();
					os.close();
					FacesContext.getCurrentInstance().responseComplete();
				} catch (Exception e) {
					log.error("\nFailure : " + e.toString() + "\n");
				}
			}
		}
	}

	public void createXMLDatabase(String fileName, String fileLocation,
			long size) {

		System.out.println(fileName + " " + fileLocation + " " + size);

		List<ProjectDatabase> projectDatabaseList;
		String xmlDatabaseRootDirectory;

		Project tempProject = ((Project) Contexts.getSessionContext().get(
				"currentProject"));

		Dataspace tempDataspace = ((Dataspace) Contexts.getSessionContext()
				.get("currentDataspace"));

		String tempProjectTitleNew = stringValidation(tempProject.getTitle());
		// It is important to save it in lower case ..
		// lower case is used in Create Database controller ...!
		String modifiedDatabaseName = (tempProjectTitleNew + "_" + stringValidation(tempDataspace
				.getDataspaceName())).toLowerCase();
		ProjectDatabase tempProjectDatabase = ((ProjectDatabase) Contexts
				.getSessionContext().get("currentProjectDatabase"));

		// Check if the currentProjectDatabase in session is the one required
		if (tempProjectDatabase != null) {
			if (tempProjectDatabase.getDatabaseName().equalsIgnoreCase(
					modifiedDatabaseName)) {
				// Do update ..
				log.info("Project Database found: {0} in session",
						tempProjectDatabase.getDatabaseName());
			} else {
				log.info(
						"Appropriate Project Database Not found: {0} {1} in session",
						tempProjectDatabase.getDatabaseName(),
						modifiedDatabaseName);
				// If Database is not in session!
				// Find it in the VIDaaS Database

				projectDatabaseList = projectDatabaseHome
						.findByDatabaseName(modifiedDatabaseName);

				// If not found in VIDaaS Database Create it ...!
				if (projectDatabaseList.size() == 0) {
					log.info("Project Database Not found: {0} in Database",
							modifiedDatabaseName);
					DatabaseStructure tempDatabaseStructure = databaseStructureHome
							.getInstance();
					// create Database Structure and persist it.
					new CreateXMLDatabase().createDatabaseStructure(
							tempProject.getProjectId(),
							tempDataspace.getDataspaceName(),
							tempDatabaseStructure, "main", log);

					String databaseStructurePersistString = databaseStructureHome
							.persist();
					log.info("databaseStructurePersistString {0}",
							databaseStructurePersistString);

					WebApplication tempWebApplication = webApplicationHome
							.getInstance();
					tempWebApplication.setStatus("NotDeployed");

					String webApplicationPersistString = webApplicationHome
							.persist();
					log.info("webApplicationPersistString {0}",
							webApplicationPersistString);

					tempProjectDatabase = projectDatabaseHome.getInstance();
					new CreateXMLDatabase().createDatabase(tempDataspace,
							tempDatabaseStructure, tempWebApplication,
							getLoginsMain(), tempProject.getTitle(),
							tempProjectDatabase, "main", log);

					try {
						String projectDatabasePersistString = projectDatabaseHome
								.persist();

						log.info("projectDatabasePersistString {0}",
								projectDatabasePersistString);
						Contexts.getSessionContext().set(
								"currentProjectDatabase", tempProjectDatabase);
					} catch (InvalidStateException ise) {
						InvalidValue[] iv = ise.getInvalidValues();
						for (int i = 0; i < iv.length; i++) {
							System.out.println("Property Name: "
									+ iv[i].getPropertyName());
							System.out.println("Property Name Message: "
									+ iv[i].getMessage());
						}
					}
				} else {
					// Found in VIDaaS Database ... update it
					log.info("Project Database found: {0} in Database",
							modifiedDatabaseName);
					tempProjectDatabase = projectDatabaseHome
							.findByDatabaseName(modifiedDatabaseName).get(0);
				}
			}
		} else {
			log.info("Project Database Not found in session: {0}",
					modifiedDatabaseName);
			projectDatabaseList = projectDatabaseHome
					.findByDatabaseName(modifiedDatabaseName);

			if (projectDatabaseList.size() == 0) {
				DatabaseStructure tempDatabaseStructure = databaseStructureHome
						.getInstance();
				// create Database Structure and persist it.
				new CreateXMLDatabase().createDatabaseStructure(
						tempProject.getProjectId(),
						tempDataspace.getDataspaceName(),
						tempDatabaseStructure, "main", log);

				String databaseStructurePersistString = databaseStructureHome
						.persist();
				log.info("databaseStructurePersistString {0}",
						databaseStructurePersistString);

				WebApplication tempWebApplication = webApplicationHome
						.getInstance();
				tempWebApplication.setStatus("NotDeployed");

				String webApplicationPersistString = webApplicationHome
						.persist();
				log.info("webApplicationPersistString {0}",
						webApplicationPersistString);

				tempProjectDatabase = projectDatabaseHome.getInstance();
				new CreateXMLDatabase().createDatabase(tempDataspace,
						tempDatabaseStructure, tempWebApplication,
						getLoginsMain(), tempProject.getTitle(),
						tempProjectDatabase, "main", log);

				try {
					String projectDatabasePersistString = projectDatabaseHome
							.persist();

					log.info("projectDatabasePersistString {0}",
							projectDatabasePersistString);
					Contexts.getSessionContext().set("currentProjectDatabase",
							tempProjectDatabase);
				} catch (InvalidStateException ise) {
					InvalidValue[] iv = ise.getInvalidValues();
					for (int i = 0; i < iv.length; i++) {
						System.out.println("Property Name: "
								+ iv[i].getPropertyName());
						System.out.println("Property Name Message: "
								+ iv[i].getMessage());
					}
				}
			} else if (projectDatabaseHome
					.findByDatabaseName(modifiedDatabaseName) != null) {
				// Found in VIDaaS Database ... update it
				log.info("Project Database found: {0} in Database",
						modifiedDatabaseName);
				tempProjectDatabase = projectDatabaseHome.findByDatabaseName(
						modifiedDatabaseName).get(0);
			}
		}

		if (tempProjectDatabase != null
				&& tempProjectDatabase.getDatabaseName().equalsIgnoreCase(
						modifiedDatabaseName)) {
			log.info("Project Database ID {0}",
					tempProjectDatabase.getDatabaseId());
			XMLFiles xmlFilesTemp = xmlFilesHome.getInstance();
			xmlFilesTemp.setProjectDatabase(tempProjectDatabase);
			xmlFilesTemp.setUsers(getUserMain());

			xmlFilesTemp.setFileName(fileName);
			xmlFilesTemp.setSize(size);

			xmlFilesTemp.setUploadDate(new Date());

			xmlFilesHome.persist();

			xmlDatabaseRootDirectory = xmlFilesTemp.getProjectDatabase()
					.getDatabaseStructure().getDatabaseDirectory();
			copyFile(fileLocation, xmlDatabaseRootDirectory + fileName);

			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(
						xmlDatabaseRootDirectory + fileName + ".txt"));
				out.write("Start Loading File in BD XML Database" + "\n");

				createBerkeleyXMLDatabaseThread = new CreateBerkeleyXMLDatabaseThread(
						xmlDatabaseRootDirectory + "ddl/",
						modifiedDatabaseName, xmlDatabaseRootDirectory,
						fileName, out);

				Thread xmlBDBThread = new Thread(
						createBerkeleyXMLDatabaseThread);
				xmlBDBThread.start();
			} catch (Exception exp) {

			}

		} else {
			log.info(
					"Project Database found: {0} is not the one required {1} XML Files not created ...!",
					tempProjectDatabase.getDatabaseName(), modifiedDatabaseName);
		}

	}

	/*
	 * public void dropXMLFile(int projectDatabaseIDValue, String fileNameValue)
	 * { log.info("File Name {0}: Database ID: {1}", fileNameValue,
	 * projectDatabaseIDValue);
	 * 
	 * // xmlFilesHome.findByFileNameAndDatabaseID(projectDatabaseIDValue, //
	 * fileNameValue);
	 * 
	 * }
	 */
	public void dropXMLFile(int fileIDValue) {
		log.info("File ID {0}: ", fileIDValue);

		// xmlFilesHome.findByFileID(fileIDValue);
		xmlFilesHome.setId(fileIDValue);

		XMLFiles tempXMLFile = xmlFilesHome.getInstance();
		String fileNameWithPath = tempXMLFile.getProjectDatabase()
				.getDatabaseStructure().getDatabaseDirectory()
				+ tempXMLFile.getFileName();

		String databaseName = tempXMLFile.getProjectDatabase()
				.getDatabaseName();
		String containerPath = tempXMLFile.getProjectDatabase()
				.getDatabaseStructure().getDatabaseDirectory()
				+ "ddl/";

		log.info("File Name {0}: ", fileNameWithPath);

		File fileToDelete = new File(fileNameWithPath);
		boolean success = fileToDelete.delete();
		// xmlFilesHome.remove();

		log.info("databaseName {0}: containerPath {1} ", databaseName,
				containerPath);

		new DeleteXMLFileFromDatabase().deleteXMLFileInContainer(containerPath,
				databaseName + ".dbxml", tempXMLFile.getFileName());
		xmlFilesHome.remove();
	}

	public void dropXMLDatabase(int databaseIDValue) {

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

	private String stringValidation(String input) {
		Pattern escaper = Pattern.compile("(^[\\d]*)");
		Pattern escaper2 = Pattern.compile("[^a-zA-z0-9]");

		String newString = escaper.matcher(input).replaceAll("");
		String newString2 = escaper2.matcher(newString).replaceAll("");

		return newString2;

	}

	private void copyFile(String inputFileString, String outputFileString) {
		File inputFile = new File(inputFileString);
		File outputFile = new File(outputFileString);

		try {
			FileChannel inChannel = (new FileInputStream(inputFile))
					.getChannel();
			FileChannel outChannel = (new FileOutputStream(outputFile))
					.getChannel();
			inChannel.transferTo(0, inputFile.length(), outChannel);
			inChannel.close();
			outChannel.close();

			inputFile.delete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
