package uk.ac.ox.oucs.vidaas.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.ox.oucs.vidaas.create.CreateDatabase;
import uk.ac.ox.oucs.vidaas.manager.ConnectionManager;
import uk.ac.ox.oucs.vidaas.utility.DatabaseUtilities;

public class DeleteDatabase {

	private String databaseName = null;

	private static Connection connection = null;
	private Statement statement = null;

	public DeleteDatabase(String databaseNameValue) {
		this.databaseName = databaseNameValue;
	}

	public void deleteDatabase() {
		if (databaseName == null) {
			return;
		}

		connection = new ConnectionManager().getConnection();

		if (DatabaseUtilities.doesDatabaseExist(databaseName, connection)) {
			try {
				statement = connection.createStatement();
				statement.executeQuery("SELECT pg_terminate_backend(procpid) FROM pg_stat_activity WHERE datname = '"+ databaseName + "'");
				
				statement.executeUpdate("DROP DATABASE " + databaseName);
				// connection.close();
			} catch (SQLException ex) {
				Logger.getLogger(DeleteDatabase.class.getName()).log(
						Level.SEVERE, null, ex);

			}
			try {
				if (!connection.isClosed()) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
}
