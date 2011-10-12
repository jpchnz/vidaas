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

public class DeleteDatabase {

	private String databaseName = null;

	private static Connection connection = null;
	private Statement statement = null;

	public DeleteDatabase(String databaseNameValue) {
		this.databaseName = databaseNameValue;
	}

	public void DeleteDatabase() {
		if (databaseName == null)
			return;

		connection = new ConnectionManager().getConnection();

		if (databaseExist(databaseName)) {
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
}
