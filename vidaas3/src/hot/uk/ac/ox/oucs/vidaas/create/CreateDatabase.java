package uk.ac.ox.oucs.vidaas.create;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.ox.oucs.vidaas.manager.ConnectionManager;

/**
 * 
 * @author Asif Akram
 */
public class CreateDatabase {

	private String databaseName = null;
	private String databasePassword = null;
	private String databaseConnectionString = null;
	private static Connection connection = null;
	private Statement statement = null;

	private int counter = 0;

	public CreateDatabase(String databaseNameValue) {
		this.databaseName = databaseNameValue;
	}

	/*
	 * public String createDatabase(String databaseOwnerNameValue, String
	 * databaseOwnerPWValue) {
	 * 
	 * return databaseConnectionString; }
	 */
	public String[] createDatabase() {
		String tempDatabaseName = databaseName;
		// System.out.println(" createDatabase()    " + tempDatabaseName + " " +
		// databaseExist());

		/**/
		connection = new ConnectionManager().getConnection();

		/**/
		if (databaseExist(tempDatabaseName)) {
			int randomNumber = (int) ((Math.random() * 1000) + 100);
			tempDatabaseName = databaseName + randomNumber;
			// System.out.println("If .... createDatabase()    " +
			// tempDatabaseName + " " + databaseExist());
		}
		try {
			statement = connection.createStatement();

			statement.executeUpdate("CREATE DATABASE " + tempDatabaseName
					+ " TEMPLATE template0");

			databaseConnectionString = "jdbc:postgresql://daas.oucs.ox.ac.uk:5432/"
					+ tempDatabaseName;

			//connection.close();
		} catch (SQLException ex) {
			Logger.getLogger(CreateDatabase.class.getName()).log(Level.SEVERE,
					null, ex);
			int randomNumber = (int) ((Math.random() * 1000) + 100);
			databaseName = databaseName + randomNumber;
			createDatabase();
		} /*finally {
			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			if (!connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/

		return new String[] { "jdbc:postgresql://localhost:5432/",
				tempDatabaseName };
	}

	public String[] cloneDatabase(String newDatabase, String oldDatabase) {
		System.out.println(newDatabase + " Old Database: " + oldDatabase);
		
		String tempDatabaseName = databaseName;
		connection = new ConnectionManager().getConnection();

		try {
			statement = connection.createStatement();

			statement.executeUpdate("CREATE DATABASE " + newDatabase
					+ " WITH TEMPLATE " + oldDatabase);

			databaseConnectionString = "jdbc:postgresql://daas.oucs.ox.ac.uk:5432/"
					+ newDatabase;

			// connection.close();
		} catch (SQLException ex) {
			Logger.getLogger(CreateDatabase.class.getName()).log(Level.SEVERE,
					null, ex);
			int randomNumber = (int) ((Math.random() * 1000) + 100);
			databaseName = databaseName + randomNumber;
			createDatabase();
		}

		return new String[] { "jdbc:postgresql://localhost:5432/",
				tempDatabaseName };
	}

	public boolean databaseExist(String databaseNameVal) {
		// System.out.println("                                                                              databaseExist()    ");
		if (connection == null) {
			connection = new ConnectionManager().getConnection();
		}
		PreparedStatement statementTemp;
		try {
			statementTemp = connection
					.prepareStatement("SELECT count(*) FROM pg_catalog.pg_database WHERE datname = ?");
			statementTemp.setString(1, databaseNameVal);
			ResultSet rs = statementTemp.executeQuery();
			// System.out.println("                                                                              Before If    ");
			if (rs.next()) {
				/*
				 * postgres=# SELECT count(*) FROM pg_catalog.pg_database WHERE
				 * datname = 'testdb'; count ------- 1 (1 row)
				 */
				int rowsReturned = Integer.parseInt(rs.getString(1));
				System.out
						.println("                                                                              rowsReturned    "
								+ rs.getString(1));
				if (rowsReturned > 0)
					return true;
			}
		} catch (SQLException ex) {
			Logger.getLogger(CreateDatabase.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return false;
	}
	
	public void closeConnection(){
		try {
			if (!connection.isClosed()) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		CreateDatabase.connection = connection;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

}
