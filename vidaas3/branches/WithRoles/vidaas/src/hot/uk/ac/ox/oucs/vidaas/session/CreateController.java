package uk.ac.ox.oucs.vidaas.session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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

import uk.ac.ox.oucs.iam.interfaces.roles.IAMRoleManager;
import uk.ac.ox.oucs.vidaas.concurrency.CreateBerkeleyXMLDatabaseThread;
import uk.ac.ox.oucs.vidaas.concurrency.CreateWebApplicationThread;
import uk.ac.ox.oucs.vidaas.concurrency.ParseCreateLoaderThread;
import uk.ac.ox.oucs.vidaas.dao.DatabaseStructureHome;
import uk.ac.ox.oucs.vidaas.dao.DataspaceHome;
import uk.ac.ox.oucs.vidaas.dao.ProjectDatabaseHome;
import uk.ac.ox.oucs.vidaas.dao.ProjectHome;
import uk.ac.ox.oucs.vidaas.dao.UserProjectHome;
import uk.ac.ox.oucs.vidaas.dao.UsersHome;
import uk.ac.ox.oucs.vidaas.dao.WebApplicationHome;
import uk.ac.ox.oucs.vidaas.dao.XMLFilesHome;
import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;
import uk.ac.ox.oucs.vidaas.delete.DeleteDatabase;
import uk.ac.ox.oucs.vidaas.delete.DeleteWebApplication;
import uk.ac.ox.oucs.vidaas.delete.DeleteXMLFileFromDatabase;
import uk.ac.ox.oucs.vidaas.download.DownloadDatabase;
import uk.ac.ox.oucs.vidaas.entity.DatabaseStructure;
import uk.ac.ox.oucs.vidaas.entity.Dataspace;
import uk.ac.ox.oucs.vidaas.entity.Logins;
import uk.ac.ox.oucs.vidaas.entity.Project;
import uk.ac.ox.oucs.vidaas.entity.ProjectDatabase;
import uk.ac.ox.oucs.vidaas.entity.UserProject;
import uk.ac.ox.oucs.vidaas.entity.UserProjectId;
import uk.ac.ox.oucs.vidaas.entity.Users;
import uk.ac.ox.oucs.vidaas.entity.WebApplication;
import uk.ac.ox.oucs.vidaas.entity.XMLFiles;
import uk.ac.ox.oucs.vidaas.manager.ConnectionManager;
import uk.ac.ox.oucs.vidaas.utility.DirectoryUtilities;
import uk.ac.ox.oucs.vidaas.utility.SystemVars;

@Name("createController")
@Scope(ScopeType.SESSION)
@SuppressWarnings("unused")
public class CreateController {
	private String requiredRole = "undefined";

	public String testString = "Test String";
	
	private final String rootStorageDirectory = System.getProperty("VIDaaSDataLocation");

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
	private String deleteProjectConfirmationMessage = "";
	private String editProjectConfirmationMessage = "";
	private String createProjectDataspaceConfirmationMessage = "";
	private String deleteProjectDataspaceConfirmationMessage = "";
	private String editProjectDataspaceConfirmationMessage = "";
	private String createDatabaseConfirmationMessage = "";
	private String createDatabaseConfirmationLinkText = "";
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

	public String getDeleteProjectConfirmationMessage() {
		return deleteProjectConfirmationMessage;
	}

	public void setDeleteProjectConfirmationMessage(
			String deleteProjectConfirmationMessage) {
		this.deleteProjectConfirmationMessage = deleteProjectConfirmationMessage;
	}

	public String getCreateProjectDataspaceConfirmationMessage() {
		return createProjectDataspaceConfirmationMessage;
	}

	public void setCreateProjectDataspaceConfirmationMessage(
			String createProjectDataspaceConfirmationMessage) {
		this.createProjectDataspaceConfirmationMessage = createProjectDataspaceConfirmationMessage;
	}

	public String getDeleteProjectDataspaceConfirmationMessage() {
		return deleteProjectDataspaceConfirmationMessage;
	}

	public void setDeleteProjectDataspaceConfirmationMessage(
			String deleteProjectDataspaceConfirmationMessage) {
		this.deleteProjectDataspaceConfirmationMessage = deleteProjectDataspaceConfirmationMessage;
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

	public void updateProject() {
		Project tempProject = ((Project) Contexts.getSessionContext().get(
				"currentProject"));
		log.info("updateProject {0} {1}", tempProject.getProjectId(),
				tempProject.getName());
		
		String currentRole = ((String) Contexts.getSessionContext().get("currentRole"));
		boolean actionAuthorised = false;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getProjectAuthentication().isAllowedToEditProject(currentRole)
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
			System.out.println("User authorised to do that");
			projectHome.setId(tempProject.getProjectId());
	
			Project tempProjectNew = projectHome.find();
	
			tempProjectNew.setName(tempProject.getName());
			tempProjectNew.setStartDate(tempProject.getStartDate());
			tempProjectNew.setEndDate(tempProject.getEndDate());
			tempProjectNew.setDescription(tempProject.getDescription());
	
			projectHome.persist();
		}
		else {
			System.out.println("User not authorised");
			editProjectConfirmationMessage = "You are not authorised to edit this project.";
			((NavigationController) Contexts.getSessionContext().get(
					"navigationController")).editProjectConfirmation();
		}
	}

	public void deleteProject(Integer projectIDValue) {
		Project tempProject = ((Project) Contexts.getSessionContext().get(
				"currentProject"));

		String projectName = tempProject.getName();
		Integer projectId = tempProject.getProjectId();
		
		log.info("deleteProject {0} {1}", tempProject.getProjectId(),
				tempProject.getName());

		projectHome.setId(tempProject.getProjectId());

		Project tempProjectNew = projectHome.find();
		
		String currentRole = ((String) Contexts.getSessionContext().get("currentRole"));
		System.out.println(String.format("Check authorisation for role <%s>", currentRole));
		boolean actionAuthorised = false;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getProjectAuthentication().isAllowedToRemoveProject(currentRole)
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
			Set<Dataspace> dataspaces = tempProjectNew.getDataspaces();
			if (dataspaces.size() == 0) {
				List<UserProject> userProjectList = userProjectHome
						.findByProjectID(tempProject.getProjectId());
				for (int i = 0; i < userProjectList.size(); i++) {
					UserProject tempUserProject = userProjectList.get(i);
					userProjectHome.setId(tempUserProject.getId());
	
					userProjectHome.find();
	
					userProjectHome.remove();
				}
				projectHome.remove();
				new DirectoryUtilities().removeProjectDir(rootStorageDirectory + "/project_" + projectId);
				deleteProjectConfirmationMessage = "Project '" + projectName
						+ "' successfully deleted.";
			} else {
				boolean moreThanOneDatabase = (dataspaces.size() > 1);
				deleteProjectConfirmationMessage = String.format("The project has %s active database%s. Please delete %s database before deleting the project.",
						moreThanOneDatabase ? "some" : "an",
						moreThanOneDatabase ? "s" : "",
						moreThanOneDatabase ? "each" : "the");
			}
		}
		else {
			deleteProjectConfirmationMessage = "You are not authorised to delete this project.";
		}

		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).deleteProjectConfirmation();
	}

	public void finishDeleteProject() {
		log.info("finishDeleteProject");
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).deleteProjectInitial();
	}
	
	
	public void finishEditProject() {
		log.info("finishEditProject");
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).editProjectInitial();
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

			if (projectsList.size() > 1) {
				log.error("Too many projects found - internal problem.");
			}
			log.info("projectsList.size() {0}", projectsList.size());
			log.info("projectsList.get(0).getUserRole() {0}",
					projectsList.get(0).getUserRole());

			boolean actionAuthorised = false;
			try {
				System.out.println("Check authorisation");
				actionAuthorised = IAMRoleManager.getInstance().getProjectAuthentication().isAllowedToCreateDatabaseByRole(projectsList.get(0).getUserRole())
						|| SystemVars.treatAdminAsOwner(projectsList.get(0).getUserRole());
				System.out.println(String.format("Role <%s> is %sauthorised to create a dataspace here", projectsList.get(0).getUserRole(), actionAuthorised ? "" : "not "));
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
				System.out.println("Authorised to perform action");
				new CreateDataSpaceController().createDataSpace(getUserMain(),
						projectsList.get(0).getProject(), dataspaceHome, today,
						log);
			

				createProjectDataspaceConfirmationMessage = "Database '"
						+ dataspaceHome.getInstance().getDataspaceName()
						+ "' for the Project: '"
						+ projectsList.get(0).getProject().getTitle()
						+ "' has been successfully created.";
			}
			else {
				System.out.println("Not authorised to perform action");
				createProjectDataspaceConfirmationMessage = "You are not authorised to create a project database for this project";	
			}
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

	
	public void updateDataSpace() {
		System.out.println("updateDataSpace");
		Dataspace tempDataspace = ((Dataspace) Contexts.getSessionContext()
				.get("currentDataspace"));
		
		Project currentProject = ((Project) Contexts.getSessionContext().get("currentProject"));
		String currentRole = NavigationController.setAndGetUserRoleByEmail(currentProject.getUserProjects(), currentProject.getProjectId());
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
			System.out.println("Authorised");
			dataspaceHome.setId(tempDataspace.getDataSpaceId());
	
			Dataspace tempDataspaceNew = dataspaceHome.find();
	
			tempDataspaceNew.setDataspaceUserFriendlyName(tempDataspace
					.getDataspaceUserFriendlyName());
			tempDataspaceNew.setDatabaseBackupPolicy(tempDataspace
					.getDatabaseBackupPolicy());
			tempDataspaceNew.setCreationDate(tempDataspace.getCreationDate());
			tempDataspaceNew.setDatabaseDescription(tempDataspace
					.getDatabaseDescription());
			tempDataspaceNew.setDatabaseExpandablePolicy(tempDataspace
					.getDatabaseExpandablePolicy());
			tempDataspaceNew.setDatabaseSize(tempDataspace.getDatabaseSize());
	
			dataspaceHome.persist();
			editProjectDataspaceConfirmationMessage = "Your changes have been made.";
		}
		else {
			System.out.println("Not authorised");
			editProjectDataspaceConfirmationMessage = "You are not authorised to edit this database. Your changes will not be kept.";
		}
		

//		((NavigationController) Contexts.getSessionContext().get(
//				"navigationController"))
//				.setHomePageMainBodyNavigation("/custom/singleDataspaceByProject.xhtml");
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).editDataspaceConfirmation();
	}
	
	

	public void deleteDataSpace(Integer DataSpaceIdValue) {
		System.out.println("deleteDataSpace:" + DataSpaceIdValue);

		Dataspace workingDataspace = ((Dataspace) Contexts.getSessionContext()
				.get("currentDataspace"));
		dataspaceHome.setId(workingDataspace.getDataSpaceId());
		
		Dataspace tempDataspaceNew = dataspaceHome.find();
		Project currentProject = ((Project) Contexts.getSessionContext().get("currentProject"));
		String currentRole = NavigationController.setAndGetUserRoleByEmail(currentProject.getUserProjects(), currentProject.getProjectId());
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
			String dataspaceUserFriendlyName = workingDataspace
					.getDataspaceUserFriendlyName();
	
			
			System.out.println("Check size");
			Set<ProjectDatabase> projectDatabases = tempDataspaceNew
					.getProjectDatabases();
			if (projectDatabases.size() == 0) {
				dataspaceHome.remove();
				deleteProjectDataspaceConfirmationMessage = "Database '"
						+ dataspaceUserFriendlyName + "'" + "successfully deleted.";
				((NavigationController) Contexts.getSessionContext().get(
						"navigationController"))
						.setHomePageMainBodyNavigation("/custom/projectByUserList.xhtml");
			} else {
				deleteProjectDataspaceConfirmationMessage = 
						String.format("Database '%s' has %s active version%s. You need to delete each version before deleting database",
								dataspaceUserFriendlyName, (projectDatabases.size() > 1 ? "some" : "an"),
								(projectDatabases.size() > 1 ? "s" : ""));
			}
		}
		else {
			deleteProjectDataspaceConfirmationMessage = "You are not authorised to delete a project database for this project";
		}

		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).deleteDataspaceConfirmation();
	}
	
	public void finishEditDataSpace() {
		log.info("finishEditDataSpace");
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).editDataspaceInitial();

	}

	public void finishDeleteDataSpace() {
		log.info("finishDeleteDataSpace");
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).deleteDataspaceInitial();

	}

	public void createDatabaseFromSchema() {
		// log.info("createDatabase {0} {1}", projectIDValue, dataspaceIDValue);
		System.out.println("createDatabaseFromSchema");
		dataHolder.setOkButton(true);
		Project currentProject = ((Project) Contexts.getSessionContext().get("currentProject"));
		String currentRole = NavigationController.setAndGetUserRoleByEmail(currentProject.getUserProjects(), currentProject.getProjectId());
		
		System.out
				.println(String.format(
						"Check if the user is authorised to create a database from schema when they have the role <%s>",
						currentRole));

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
			((NavigationController) Contexts.getSessionContext().get(
					"navigationController")).setParseDatabaseFormRender(false);
			System.out.println("Yes, the user is authorised");
			Dataspace currentDataspace = ((Dataspace) Contexts.getSessionContext()
					.get("currentDataspace"));
	
			log.info("createDatabase {0} {1}", currentProject.getProjectId(),
					currentDataspace.getDataSpaceId());
			/**/
			DatabaseStructure tempDatabaseStructure = databaseStructureHome
					.getInstance();
			tempDatabaseStructure.setCreationDate(today);
	
			new CreateDatabaseController().createDatabaseStructure(
					currentProject.getProjectId(), currentDataspace.getDataspaceName(),
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
			new CreateDatabaseController().createDatabase(currentDataspace,
					tempDatabaseStructure, tempWebApplication, getLoginsMain(),
					currentProject.getTitle(), tempProjectDatabase, "main", log);
	
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
			createDatabaseConfirmationLinkText = "Parse Database";
			
		}
		else {
			System.out.println("No, the user is not authorised");
			createDatabaseConfirmationMessage = "You are not authorised to create a database when your role is " + currentRole;
			createDatabaseConfirmationLinkText = "Return";
		}
		/**/
//		((NavigationController) Contexts.getSessionContext().get(
//				"navigationController")).setParseDatabaseFormRender(!authorised);
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

	private boolean authorised;
	public void parseDatabase() {
		System.out.println("parseDatabase");
		if (authorised) {
			System.out.println("The user is authorised to do this");
			
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
			if (tempDatabaseStructure == null) {
				System.out.println("Problem: working database structre is null");
			}
			else {
				System.out.println("Working database structure obtained");
			}
	
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
		else {
			System.out.println("Not authorised!!");
			((NavigationController) Contexts.getSessionContext().get(
					"navigationController"))
					.setHomePageMainBodyNavigation("/custom/singleDataspaceByProject.xhtml");
		}
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).setParseDatabaseFormRender(true);
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).createDatabaseInitial();
	}

	public void finishParseDatabase() {
		System.out.println("finishParseDatabase");
		
		if (authorised) {
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
			((NavigationController) Contexts.getSessionContext().get(
					"navigationController"))
					.setCurrentDataspace(tempProjectDatabase.getDataspace()
							.getDataSpaceId());
		}

		

		((NavigationController) Contexts.getSessionContext().get(
				"navigationController"))
				.setHomePageMainBodyNavigation("/custom/singleDataspaceByProject.xhtml");
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).setParseDatabaseFormRender(false);
	}

	public void createWebApplication(Integer projectDatabaseIDValue) {
		dataHolder.setOkButton(true);
		dataHolder.currentStatus = "";
		log.info("createWebApplication() called " + "" + projectDatabaseIDValue);
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
		// dataHolder.setOkButton(false);
	}

	public void finishCreateWebApplication() {
		log.info("finishCreateWebApplication() called " + ""
				+ this.currentDatabaseID);
		((NavigationController) Contexts.getSessionContext().get(
				"navigationController")).createWebApplicationInitial();

		log.info("finishCreateWebApplication() "
				+ createWebApplicationThread.isCreateStatus());

		projectDatabaseHome.setId(this.currentDatabaseID);
		ProjectDatabase tempProjectDatabase = projectDatabaseHome.find();

		projectDatabaseHome.setInstance(tempProjectDatabase);

		webApplicationHome.setId(tempProjectDatabase.getWebApplication()
				.getWebId());

		WebApplication tempWebApplication = webApplicationHome.find();

		webApplicationHome.setInstance(tempWebApplication);

		if (createWebApplicationThread.isCreateStatus() == true) {
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

	public void editProjectMember() {
		UserProject userProject = ((UserProject) Contexts.getSessionContext()
				.get("currentUserProject"));

		userProjectHome.setId(userProject.getId());

		UserProject userProjectNew = userProjectHome.find();

		System.out.println(String.format("Setting user's role to be <%s>", requiredRole));
		userProjectNew.setUserRole(requiredRole);

		userProjectHome.persist();
	}
	
	public void deleteProjectMember() {
		System.out.println("createController:deleteProjectMember");
		
		UserProject userProject = ((UserProject) Contexts.getSessionContext()
				.get("currentUserProject"));

		userProjectHome.setId(userProject.getId());

		UserProject userProjectNew = userProjectHome.find();
		userProjectHome.remove();
	}

	public void createProjectMember() {
		System.out.println("createProjectMember");

		addProjectMemberConfirmationMessage = "";
		/*
		 * ((NavigationController) Contexts.getSessionContext().get(
		 * "navigationController")).homePageWelcome();
		 */

		Project currentProject = ((Project) Contexts.getSessionContext().get("currentProject"));
		log.info("currentProject description: " + currentProject.getDescription());

		/*
		 * First we need to determine if this user has authority to add another
		 * user to the project!
		 */
		String currentRole = ((String) Contexts.getSessionContext().get("currentRole"));
		System.out.println(String.format("Check if the user is authorised to create a project member when they have the role <%s>", currentRole));
		boolean actionAuthorised = false;
		try {
			actionAuthorised = IAMRoleManager.getInstance().getProjectAuthentication().isAllowedToAddOthersForProject(currentRole)
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

		if (actionAuthorised) {
			// String currentRole = ((UserRoles)
			// Contexts.getSessionContext().get(
			// "userRoles")).getCurrentRole();
			System.out.println("User is authorised to add member!");
			Users userToAdd = usersHome.getInstance();
			List<Users> userList = usersHome.findUserByEmail(userToAdd.getEmail());

			if (log.isDebugEnabled()) {
				log.debug("About to try to add user " + userToAdd.getEmail());
			}
			System.out.println("userList.size(): " + userList.size());

			if (userList.size() == 0) {
				// save the user from the form

				// I should just return .. if user doesn't exist
				// usersHome.persist();
				addProjectMemberConfirmationMessage = userToAdd.getFirstName() + " " + userToAdd.getLastName()
						+ " is not a registered member";
				// return;
			}
			else {
				if (userList.size() > 1) {
					/*
					 * FIXME This should not happen. For now, we should log the
					 * problem and continue.
					 */
					log.error("There are too many users defined for email address " + userToAdd.getEmail());
				}
				userToAdd = userList.get(0);

				if (log.isDebugEnabled()) {
					log.debug("Confirmed that user is registered");
					log.debug(String.format("Setting user id %d and project id %d", userToAdd.getUserId(),
							currentProject.getProjectId()));
				}

				UserProjectId userProjectID = new UserProjectId();
				userProjectID.setUserId(userToAdd.getUserId());
				userProjectID.setProjectId(currentProject.getProjectId());

				System.out.println("Temp project ID:" + currentProject.getProjectId() + "  Project ID:"
						+ projectHome.getProjectProjectId() + "  User ID:" + getUserMain().getUserId());
				UserProject userProject = userProjectHome.getInstance();
				userProject.setId(userProjectID);
				userProject.setProject(currentProject);
				userProject.setUsers(userToAdd);
				System.out.println(String.format("Setting user's role to be <%s>", requiredRole));
				userProject.setUserRole(requiredRole);

				try {
					if (log.isDebugEnabled()) {
						log.debug("Set instance ...");
						log.debug("userid:" + userProject.getId().getUserId());
						log.debug("projectid:" + userProject.getId().getProjectId());
						log.debug("project name:" + userProject.getProject().getName());
						log.debug("project id:" + userProject.getProject().getProjectId());
						log.debug(userProject.getUserRole());
					}
					userProjectHome.setInstance(userProject);
					userProjectHome.persist();
					addProjectMemberConfirmationMessage = userToAdd.getFirstName() + " " + userToAdd.getLastName()
							+ " has been added as a " + userProject.getUserRole() + " for this project.";
				}
				catch (Exception e) {
					e.printStackTrace();
					// org.hibernate.exception.ConstraintViolationException
					addProjectMemberConfirmationMessage = userToAdd.getFirstName()
							+ " "
							+ userToAdd.getLastName()
							+ " is already project member. If you believe this to be in error then please contact support.";
				}
			}
		}
		else {
			// TODO Audit this
			System.out.println("User is not authorised to add member!");
			addProjectMemberConfirmationMessage = "You are not authorised to add a user to this project.";
		}
		((NavigationController) Contexts.getSessionContext().get("navigationController"))
				.addProjectMemberConfirmation();
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

	
	

	public String getEditProjectConfirmationMessage() {
		return editProjectConfirmationMessage;
	}

	public void setEditProjectConfirmationMessage(String editProjectConfirmationMessage) {
		this.editProjectConfirmationMessage = editProjectConfirmationMessage;
	}
	
	public String getRequiredRole() {
		return requiredRole;
	}

	public void setRequiredRole(String requiredRole) {
		this.requiredRole = requiredRole;
	}

	public boolean isAuthorised() {
		return authorised;
	}

	public void setAuthorised(boolean authorised) {
		this.authorised = authorised;
	}

	public String getCreateDatabaseConfirmationLinkText() {
		return createDatabaseConfirmationLinkText;
	}

	public void setCreateDatabaseConfirmationLinkText(String createDatabaseConfirmationLinkText) {
		this.createDatabaseConfirmationLinkText = createDatabaseConfirmationLinkText;
	}

	public String getEditProjectDataspaceConfirmationMessage() {
		return editProjectDataspaceConfirmationMessage;
	}

	public void setEditProjectDataspaceConfirmationMessage(String editProjectDataspaceConfirmationMessage) {
		this.editProjectDataspaceConfirmationMessage = editProjectDataspaceConfirmationMessage;
	}
}
