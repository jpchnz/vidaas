package uk.ac.ox.oucs.vidaas.session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.jboss.seam.log.Log;

import uk.ac.ox.oucs.vidaas.create.CreateUser;
import uk.ac.ox.oucs.vidaas.entity.DatabaseStructure;
import uk.ac.ox.oucs.vidaas.entity.Dataspace;
import uk.ac.ox.oucs.vidaas.entity.Logins;
import uk.ac.ox.oucs.vidaas.entity.ProjectDatabase;
import uk.ac.ox.oucs.vidaas.entity.Users;
import uk.ac.ox.oucs.vidaas.entity.WebApplication;

public class CreateDatabaseController {
	
	private final String rootStorageDirectory = "/opt/VIDaaSData/";
	
	public void createDatabaseStructure(Integer projectID, String databaseName, DatabaseStructure tempDatabaseStructure,
			Log logger) {
		logger.info("createDatabaseStructure() called");
		
		logger.info(Level.INFO,"tempDatabaseStructure.getFile(): "
				+ tempDatabaseStructure.getFile());
		
		logger.info("Upload Type {0}", tempDatabaseStructure.getUploadType());
		
		String projectName = "project_"	+ projectID;
		
		// databaseDirectory = /opt/vidaasData/project_2/db_test/
		String databaseDirectory = rootStorageDirectory + projectName + "/"
				+ stringValidation(databaseName) + "/";
		
		// schemaDirectory = /opt/vidaasData/project_2/db_test/
		String schemaRootDirectory = databaseDirectory;
		
		// /opt/vidaasData/project_2/db_test/oxrep2003.mdb
		String databaseMDBFile = schemaRootDirectory
				+ tempDatabaseStructure.getFile();
		
		// SQL directory will contain Individual create table SQL
		// can be avoided by clever logic to parse long stream of data
		// Not used Yet ... can be useful in future
		// databaseSchemaDirectory =
		// /opt/vidaasData/project_2/db_test/ddl/
		String databaseSchemaDirectory = schemaRootDirectory + "ddl/";

		// data directory will contain Data used by insert type SQL statements
		// databaseDataDirectory = /opt/vidaasData/project_2/db_test/Dummy/data/
		String databaseDataDirectory = schemaRootDirectory + "data/";

		// Data is CSV format
		// databaseCSVDirectory =
		// /opt/vidaasData/project_2/db_test/data/csv/
		String databaseCSVDataDirectory = databaseDataDirectory + "csv/";
		
		// DATA in insert SQL statement Format ....
		// databaseDataDirectory =
		// /opt/vidaasData/project_2/db_test/data/sql
		String databaseSQLDataDirectory = databaseDataDirectory + "sql/";

		// One Single CSV for whole database doesn't work ...!
		// String databaseCVSFile = databaseCVSDirectory +
		// databaseMDBFileWithoutExtension + ".csv";

		//this.databaseSchemaFormStatus = "Creating Directories";

		if (!(new File(databaseDirectory).exists())) {
			new File(databaseDirectory).mkdirs();
		}

		new File(schemaRootDirectory).mkdirs();
		new File(databaseSchemaDirectory).mkdirs();
		new File(databaseCSVDataDirectory).mkdirs();
		new File(databaseSQLDataDirectory).mkdirs();
		
		try {
			//this.databaseSchemaFormStatus = "Saving Uploaded Database";
			FileOutputStream fileOutputStream = new FileOutputStream(
					databaseMDBFile);
			fileOutputStream.write(tempDatabaseStructure.getData());
			/**/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tempDatabaseStructure.setCreationDate(new Date());
		
		tempDatabaseStructure.setDatabaseDirectory(schemaRootDirectory);
		tempDatabaseStructure.setCsvDirectory(databaseCSVDataDirectory);
		tempDatabaseStructure.setSqlDirectory(databaseSchemaDirectory);
		// tempDatabaseStructure.

		tempDatabaseStructure.setStatus(new String("Uploaded"));
		tempDatabaseStructure.setData(new String("Uploaded").getBytes());
		tempDatabaseStructure.setSchemaType(tempDatabaseStructure.getUploadType());

		//tempDatabaseStructure.setProjectDatabase(tempProjectDatabase);
		//tempDatabaseStructure.setUsers(userMain);
	}
	
	public void createDatabase(Dataspace tempDataSpace, DatabaseStructure tempDatabaseStructure, WebApplication tempWebApplication, Logins loginMain, String projectTitle, ProjectDatabase tempDatabase,
			Log logger) {
		
		String tempProjectTitleNew = stringValidation(projectTitle);
		String modifiedDatabaseName = tempProjectTitleNew + "_" + stringValidation(tempDataSpace.getDataspaceName());
		
		tempDatabase.setDatabaseName(modifiedDatabaseName);
		

		try {
			/* */
			uk.ac.ox.oucs.vidaas.create.CreateDatabase createDatabase = new uk.ac.ox.oucs.vidaas.create.CreateDatabase(
					modifiedDatabaseName.toLowerCase());
			String connectionString[] = createDatabase.createDatabase();
			logger.info(Level.INFO, "*********** connectionString   " + connectionString[1]);
					
			/*
			 * Database doesn't have connection String .. 
			 * It is schema which will have connection String
			tempDatabase.setConnectionString(connectionString[0]
					+ connectionString[1]);*/

			// Database may be existing and CreateDatabase may change its name
			tempDatabase.setDatabaseName(connectionString[1].toLowerCase());
			
			/* Database will have user name based on the User. 
			 * One user will have same DB userName for each DB.
			*/
			//tempDatabase.setUserName(tempDatabase.getUserName().toLowerCase());
			CreateUser createUser = new CreateUser();
			boolean userExists = createUser.userExist(loginMain.getUserName());
			
			logger.info(Level.INFO, " ++++++++++++++++++++++++++++++++++++++++++++++++++++   User Exists: " + userExists);
			logger.info(Level.INFO, " ++++++++++++++++++++++++++++++++++++++++++++++++++++   Database Name: " + tempDatabase.getDatabaseName());
			/* */
			if(userExists == false){
				createUser = new CreateUser(loginMain.getUserName()
						.toLowerCase(), loginMain.getPassword(),
						tempDatabase.getDatabaseName());
				createUser.createUser(true, false);
			} else {
				createUser.grantDatabaseAdmin(tempDatabase.getDatabaseName(), loginMain.getUserName().toLowerCase());
				createUser.grantDatabaseOwnership(tempDatabase.getDatabaseName(), loginMain.getUserName().toLowerCase());
			}
			
			tempDatabase.setDataspace(tempDataSpace);
			tempDatabase.setDatabaseStructure(tempDatabaseStructure);
			tempDatabase.setWebApplication(tempWebApplication);
			tempDatabase.setDatabaseType(tempDataSpace.getDatabaseType());
			tempDatabase.setCreationDate(new Date());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	private String stringValidation(String input) {
		Pattern escaper = Pattern.compile("(^[\\d]*)");
		Pattern escaper2 = Pattern.compile("[^a-zA-z0-9]");

		String newString = escaper.matcher(input).replaceAll("");
		String newString2 = escaper2.matcher(newString).replaceAll("");

		return newString2;

	}

}
