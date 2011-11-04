package uk.ac.ox.oucs.vidaas.session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.jboss.seam.log.Log;

import uk.ac.ox.oucs.vidaas.create.CreateUser;
import uk.ac.ox.oucs.vidaas.entity.DatabaseStructure;
import uk.ac.ox.oucs.vidaas.entity.Dataspace;
import uk.ac.ox.oucs.vidaas.entity.Logins;
import uk.ac.ox.oucs.vidaas.entity.ProjectDatabase;
import uk.ac.ox.oucs.vidaas.entity.WebApplication;

public class CreateDatabaseController {

	private final String rootStorageDirectory = "/opt/VIDaaSData/";

	public void createDatabaseStructure(Integer projectID, String databaseName,
			DatabaseStructure tempDatabaseStructure, String databaseVersion,
			Log logger) {
		logger.info("createDatabaseStructure() called");

		logger.info(Level.INFO, "tempDatabaseStructure.getFile(): "
				+ tempDatabaseStructure.getFile());
		logger.info("Upload Type {0}", tempDatabaseStructure.getUploadType());

		String projectName = "project_" + projectID;

		// databaseDirectory = /opt/vidaasData/project_2/db_test/
		String databaseDirectory = rootStorageDirectory + projectName + "/"
				+ stringValidation(databaseName) + "/" + databaseVersion + "/";

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

		// this.databaseSchemaFormStatus = "Creating Directories";

		boolean ret = false;
		if (!(new File(databaseDirectory).exists())) {
			ret = new File(databaseDirectory).mkdirs();
			if (!ret) {
				logger.error(String.format(
						"Unable to create database folder (%s)",
						databaseDirectory));
			}
		}

		ret = new File(schemaRootDirectory).mkdirs();
		if (!ret) {
			logger.error(String.format("Unable to create schema root (%s)",
					schemaRootDirectory));
		}
		ret = new File(databaseSchemaDirectory).mkdirs();
		if (!ret) {
			logger.error(String.format("Unable to create schema folder (%s)",
					databaseSchemaDirectory));
		}
		ret = new File(databaseCSVDataDirectory).mkdirs();
		if (!ret) {
			logger.error(String.format(
					"Unable to create database csv folder (%s)",
					databaseCSVDataDirectory));
		}
		ret = new File(databaseSQLDataDirectory).mkdirs();
		if (!ret) {
			logger.error(String.format(
					"Unable to create database sql folder (%s)",
					databaseSQLDataDirectory));
		}

		try {
			// this.databaseSchemaFormStatus = "Saving Uploaded Database";
			FileOutputStream fileOutputStream = new FileOutputStream(
					databaseMDBFile);
			fileOutputStream.write(tempDatabaseStructure.getData());
			/**/
		} catch (FileNotFoundException e) {
			logger.error(String.format("File not found:%s", databaseMDBFile), e);
		} catch (IOException e) {
			logger.error("IO Error", e);
			byte [] b = tempDatabaseStructure.getData();
			if (b == null) {
				logger.error("Null data");
			}
			else {
				logger.error(String.format("Data length %d", b.length));
			}
		}

		tempDatabaseStructure.setCreationDate(new Date());

		tempDatabaseStructure.setDatabaseDirectory(schemaRootDirectory);
		tempDatabaseStructure.setCsvDirectory(databaseCSVDataDirectory);
		tempDatabaseStructure.setSqlDirectory(databaseSchemaDirectory);

		tempDatabaseStructure.setStatus(new String("Uploaded"));
		tempDatabaseStructure.setData(new String("Uploaded").getBytes());
		tempDatabaseStructure.setSchemaType(tempDatabaseStructure
				.getUploadType());

		// tempDatabaseStructure.setProjectDatabase(tempProjectDatabase);
		// tempDatabaseStructure.setUsers(userMain);
	}

	public void cloneDatabaseStructure(Integer projectID, String databaseName,
			DatabaseStructure newDatabaseStructure,
			DatabaseStructure oldDatabaseStructure, String databaseVersion,
			Log logger) {
		logger.info("cloneDatabaseStructure() called");

		String projectName = "project_" + projectID;

		// databaseDirectory = /opt/vidaasData/project_2/db_test/
		String databaseDirectory = rootStorageDirectory + projectName + "/"
				+ stringValidation(databaseName) + "/" + databaseVersion + "/";

		// schemaDirectory = /opt/vidaasData/project_2/db_test/
		String schemaRootDirectory = databaseDirectory;

		// /opt/vidaasData/project_2/db_test/oxrep2003.mdb
		/**/
		String databaseMDBFile = schemaRootDirectory
				+ oldDatabaseStructure.getFile();

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

		// this.databaseSchemaFormStatus = "Creating Directories";

		if (!(new File(databaseDirectory).exists())) {
			new File(databaseDirectory).mkdirs();
		}

		new File(schemaRootDirectory).mkdirs();
		new File(databaseSchemaDirectory).mkdirs();
		new File(databaseCSVDataDirectory).mkdirs();
		new File(databaseSQLDataDirectory).mkdirs();

		try {
			// this.databaseSchemaFormStatus = "Saving Uploaded Database";
			File destination = new File(databaseMDBFile);
			File sourceFile = new File(rootStorageDirectory + projectName + "/"
					+ stringValidation(databaseName) + "/main/"
					+ oldDatabaseStructure.getFile());

			copy(sourceFile, destination);
			/**/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		newDatabaseStructure.setCreationDate(new Date());
		newDatabaseStructure.setFile(oldDatabaseStructure.getFile());

		newDatabaseStructure.setDatabaseDirectory(schemaRootDirectory);
		newDatabaseStructure.setCsvDirectory(databaseCSVDataDirectory);
		newDatabaseStructure.setSqlDirectory(databaseSchemaDirectory);
		// tempDatabaseStructure.

		newDatabaseStructure.setStatus(new String("Copied"));
		newDatabaseStructure.setData(new String("Copied").getBytes());
		newDatabaseStructure
				.setSchemaType(oldDatabaseStructure.getUploadType());

	}

	public void createDatabase(Dataspace tempDataSpace,
			DatabaseStructure tempDatabaseStructure,
			WebApplication tempWebApplication, Logins loginMain,
			String projectTitle, ProjectDatabase tempDatabase,
			String databaseVersion, Log logger) {
		Date todaysDate = new Date();

		String tempProjectTitleNew = stringValidation(projectTitle);
		String modifiedDatabaseName = tempProjectTitleNew + "_"
				+ stringValidation(tempDataSpace.getDataspaceName());

		tempDatabase.setDatabaseName(modifiedDatabaseName.toLowerCase());

		try {
			/* */
			uk.ac.ox.oucs.vidaas.create.CreateDatabase createDatabase = new uk.ac.ox.oucs.vidaas.create.CreateDatabase(
					modifiedDatabaseName.toLowerCase());
			String connectionString[] = createDatabase.createDatabase();
			logger.info(Level.INFO, "*********** connectionString   "
					+ connectionString[1]);
			/*
			 * tempDatabase.setConnectionString(connectionString[0] +
			 * connectionString[1]);
			 */
			// Database may be existing and CreateDatabase may change its name
			// tempDatabase.setDatabaseName(connectionString[1].toLowerCase());

			/*
			 * Database will have user name based on the User. One user will
			 * have same DB userName for each DB.
			 */
			// tempDatabase.setUserName(tempDatabase.getUserName().toLowerCase());
			CreateUser createUser = new CreateUser();
			boolean userExists = createUser.userExist(loginMain.getUserName());

			logger.info(Level.INFO,
					" ++++++++++++++++++++++++++++++++++++++++++++++++++++   User Exists: "
							+ userExists);
			logger.info(Level.INFO,
					" ++++++++++++++++++++++++++++++++++++++++++++++++++++   Database Name: "
							+ tempDatabase.getDatabaseName());
			/* */
			if (userExists == false) {
				createUser = new CreateUser(loginMain.getUserName()
						.toLowerCase(), loginMain.getPassword(),
						tempDatabase.getDatabaseName());
				createUser.createUser(true, false);
			} else {
				createUser.grantDatabaseAdmin(tempDatabase.getDatabaseName(),
						loginMain.getUserName().toLowerCase());
				createUser.grantDatabaseOwnership(tempDatabase
						.getDatabaseName(), loginMain.getUserName()
						.toLowerCase());
			}

			tempDatabase.setDataspace(tempDataSpace);
			tempDatabase.setDatabaseStructure(tempDatabaseStructure);
			tempDatabase.setWebApplication(tempWebApplication);
			tempDatabase.setDatabaseType(databaseVersion);
			// tempDatabase.setCreationDate(todaysDate);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void cloneDatabase(Dataspace tempDataSpace,
			DatabaseStructure tempDatabaseStructure,
			WebApplication tempWebApplication, Logins loginMain,
			String projectTitle, ProjectDatabase newDatabase,
			ProjectDatabase originalDatabase, String databaseVersion, Log logger) {

		logger.info("Old Database Name {0}", originalDatabase.getDatabaseName());

		String tempProjectTitleNew = stringValidation(projectTitle);
		String modifiedDatabaseName = tempProjectTitleNew
				+ "_"
				+ stringValidation(tempDataSpace.getDataspaceName() + "_"
						+ databaseVersion);

		newDatabase.setDatabaseName(modifiedDatabaseName.toLowerCase());

		try {
			uk.ac.ox.oucs.vidaas.create.CreateDatabase createDatabase = new uk.ac.ox.oucs.vidaas.create.CreateDatabase(
					modifiedDatabaseName.toLowerCase());

			String connectionString[] = createDatabase.createDatabase();
			createDatabase.closeConnection();

			dumpDatabase(tempDatabaseStructure.getDatabaseDirectory()
					+ modifiedDatabaseName + ".sql",
					originalDatabase.getDatabaseName());
			restoreDatabase(tempDatabaseStructure.getDatabaseDirectory()
					+ modifiedDatabaseName + ".sql", modifiedDatabaseName);

			/*
			 * String connectionString[] = createDatabase.cloneDatabase(
			 * modifiedDatabaseName.toLowerCase(),
			 * originalDatabase.getDatabaseName());
			 */

			newDatabase.setConnectionString(connectionString[0]
					+ connectionString[1]);

			// Database may be existing and CreateDatabase may change its name
			newDatabase.setDatabaseName(connectionString[1].toLowerCase());

			CreateUser createUser = new CreateUser();
			createUser.grantDatabaseAdmin(newDatabase.getDatabaseName(),
					loginMain.getUserName().toLowerCase());
			createUser.grantDatabaseOwnership(newDatabase.getDatabaseName(),
					loginMain.getUserName().toLowerCase());

			newDatabase.setDataspace(tempDataSpace);
			newDatabase.setDatabaseStructure(tempDatabaseStructure);
			newDatabase.setWebApplication(tempWebApplication);
			newDatabase.setDatabaseType(databaseVersion);
			newDatabase.setCreationDate(new Date());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/**/
	}

	public void dumpDatabase(String dumpFileNameValue, String databaseName) {
		System.out.println(dumpFileNameValue + "  " + databaseName);
		try {
			Process p;
			ProcessBuilder pb;

			List<String> cmds = new ArrayList<String>();
			cmds.add("/usr/bin/pg_dump");
			cmds.add("-i");
			cmds.add("-h");
			cmds.add("localhost");
			cmds.add("-p");
			cmds.add("5432");
			cmds.add("-U");
			cmds.add("sudamihAdmin");
			cmds.add("-F");
			cmds.add("c");
			cmds.add("-b");
			cmds.add("-v");
			cmds.add("-f");
			cmds.add(dumpFileNameValue);
			cmds.add(databaseName);

			pb = new ProcessBuilder(cmds);
			pb.environment().put("PGPASSWORD", "sudamihAdminPW");
			// pb.redirectErrorStream(true);
			p = pb.start();
			try {
				InputStream is = p.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String ll;
				while ((ll = br.readLine()) != null) {
					System.out.println(ll);
				}
				InputStream isE = p.getErrorStream();
				InputStreamReader isrE = new InputStreamReader(isE);
				BufferedReader brE = new BufferedReader(isrE);
				String llE;
				while ((llE = brE.readLine()) != null) {
					System.out.println(llE);
				}
			} catch (IOException e) {
				System.out.println(e);
				// log("ERROR "+e.getMessage());
			}

		} catch (Exception e) {

		}
	}

	public void restoreDatabase(String dumpFileNameValue, String newDatabaseName) {
		System.out.println(dumpFileNameValue + "  " + newDatabaseName);
		try {
			Process p;
			ProcessBuilder pb;

			List<String> cmds = new ArrayList<String>();
			cmds.add("/usr/bin/pg_restore");
			cmds.add("-h");
			cmds.add("localhost");
			cmds.add("-p");
			cmds.add("5432");
			cmds.add("-U");
			cmds.add("sudamihAdmin");
			cmds.add("-d");
			cmds.add(newDatabaseName);
			cmds.add(dumpFileNameValue);
			pb = new ProcessBuilder(cmds);
			pb.environment().put("PGPASSWORD", "sudamihAdminPW");
			// pb.redirectErrorStream(true);
			p = pb.start();
			try {
				InputStream is = p.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String ll;
				while ((ll = br.readLine()) != null) {
					System.out.println(ll);
				}
				InputStream isE = p.getErrorStream();
				InputStreamReader isrE = new InputStreamReader(isE);
				BufferedReader brE = new BufferedReader(isrE);
				String llE;
				while ((llE = brE.readLine()) != null) {
					System.out.println(llE);
				}
			} catch (IOException e) {
				System.out.println(e);
				// log("ERROR "+e.getMessage());
			}

		} catch (Exception e) {

		}
	}

	private String stringValidation(String input) {
		Pattern escaper = Pattern.compile("(^[\\d]*)");
		Pattern escaper2 = Pattern.compile("[^a-zA-z0-9]");

		String newString = escaper.matcher(input).replaceAll("");
		String newString2 = escaper2.matcher(newString).replaceAll("");

		return newString2;

	}

	private void copy(File src, File dst) throws IOException {
		FileChannel in = (new FileInputStream(src)).getChannel();
		FileChannel out = (new FileOutputStream(dst)).getChannel();
		in.transferTo(0, src.length(), out);
		in.close();
		out.close();
	}

}
