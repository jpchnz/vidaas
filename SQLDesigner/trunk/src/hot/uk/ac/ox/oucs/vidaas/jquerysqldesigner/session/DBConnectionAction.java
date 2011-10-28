package uk.ac.ox.oucs.vidaas.jquerysqldesigner.session;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Database;

import org.apache.ddlutils.io.DatabaseIO;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.remoting.WebRemote;

import java.util.Random;

@Name("dbConnectionAction")
public class DBConnectionAction {

	@WebRemote
	public String getDBMetaData(DBConnection dbConnectionValue) {

		System.out.println("dbConnectionValue.getDatabaseType(): "
				+ dbConnectionValue.getDatabaseType());
		System.out.println("dbConnectionValue.getConnectionURL(): "
				+ dbConnectionValue.getConnectionURL());
		System.out.println("dbConnectionValue.getPassword(): "
				+ dbConnectionValue.getPassword());
		/* "jdbc:mysql://localhost:3306/peersonadb290409", "", "" */
		/* */
		Database db = readDatabase(setupDataSource(dbConnectionValue));

		String fileNameWithLocation = randomFileName();
		System.out.println("fileNameWithLocation: " + fileNameWithLocation);

		writeDatabaseToXML(db,
				"./../server/default/deploy/jQuerySQLDesigner.war/sql/"
						+ fileNameWithLocation);

		return fileNameWithLocation;
	}

	private DataSource setupDataSource(DBConnection dbConnectionValue) {
		BasicDataSource ds = new BasicDataSource();
		/* */

		setDataSourceURL(ds, dbConnectionValue.getDatabaseType(),
				dbConnectionValue.getConnectionURL(),
				dbConnectionValue.getPortNumber(),
				dbConnectionValue.getDatabaseName());

		ds.setUsername(dbConnectionValue.getUserName());
		ds.setPassword(dbConnectionValue.getPassword());
		// ds.setUrl(dbConnectionValue.getConnectionURL());

		// ds.setDriverClassName("com.mysql.jdbc.Driver");
		// ds.setUsername("root");
		// ds.setPassword("bna192");
		// ds.setUrl("jdbc:mysql://localhost:3306/peersonadb290409");

		return ds;
	}

	private Database readDatabase(DataSource dataSource) {
		System.out.println("readDatabase: ");
		Database database = null;
		try {
			Platform platform = PlatformFactory
					.createNewPlatformInstance(dataSource);
			database = platform.readModelFromDatabase("model");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return database;
	}

	private void writeDatabaseToXML(Database db, String fileName) {
		System.out.println("writeDatabaseToXML: " + fileName);
		new DatabaseIO().write(db, fileName);
	}

	private void setDataSourceURL(BasicDataSource dataDource,
			String databaseType, String connectionURI, String portNumber,
			String dbName) {
		if (databaseType.equals("mysql")) {
			dataDource.setDriverClassName("com.mysql.jdbc.Driver");
			String connectionString = "jdbc:mysql://"
					+ createConnectionURI(connectionURI, portNumber, dbName);
			System.out.println(connectionString);
			dataDource.setUrl(connectionString);

		} else if (databaseType.equals("postgresql")) {
			dataDource.setDriverClassName("org.postgresql.Driver"); 
			//jdbc:postgresql
			String connectionString = "jdbc:postgresql://"
				+ createConnectionURI(connectionURI, portNumber, dbName);
			System.out.println(connectionString);
			dataDource.setUrl(connectionString);
		} else if (databaseType.equals("oracle")) {
			dataDource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		} else if (databaseType.equals("mssql")) {
			dataDource
					.setDriverClassName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
		} else if (databaseType.equals("db2")) {
			dataDource
					.setDriverClassName("com.ibm.as400.access.AS400JDBCDriver");
		}
	}

	private String randomFileName() {
		String fileName = "sql_xml";
		Random randomGenerator = new Random();
		fileName = fileName + "_" + randomGenerator.nextInt(10000);
		fileName = fileName + "_" + randomGenerator.nextInt(10000);
		fileName = fileName + ".xml";
		return fileName;
	}

	private String createConnectionURI(String connectionURI, String portNumber,
			String dbName) {
		int firstBackSlashIndex = connectionURI.indexOf("/");
		String tempHostURL = "";

		if (connectionURI.startsWith("http")) {
			tempHostURL = connectionURI.substring(7);
		} else if (connectionURI.startsWith("ftp")) {

		} else if (firstBackSlashIndex == 1) {

		} else {
			tempHostURL = connectionURI;
		}

		tempHostURL = tempHostURL + ":" + portNumber + "/" + dbName;

		System.out.println("tempHostURL: " + tempHostURL);
		return tempHostURL;
	}
}
