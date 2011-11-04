package uk.ac.ox.oucs.vidaas.utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.ox.oucs.vidaas.manager.ConnectionManager;

/**
 * General database utilities to promote code reuse.
 * 
 * 
 * @author Dave Paine
 *
 */
public class DatabaseUtilities {
	private static Logger logger = Logger.getLogger(DatabaseUtilities.class.getName());
	
	/**
	 * 
	 * @param databaseNameVal name of the database to check
	 * @param connection (Optional) db connection string. If null then one will be created.
	 * @return true if the named database exists
	 */
	public static boolean doesDatabaseExist(String databaseName, Connection connection) {
		logger.fine("doesDatabaseExist: " + databaseName);
		if (connection == null) {
			connection = new ConnectionManager().getConnection();
			if (connection == null) {
				logger.severe("Unable to get database connection - this is bad");
				/*
				 * TODO
				 * Things are now in bad shape. We should ideally throw an
				 * exception to let the caller know things are bad.
				 */
			}
		}
		PreparedStatement statement;
		try {
			statement = connection
					.prepareStatement("SELECT count(*) FROM pg_catalog.pg_database WHERE datname = ?");
			statement.setString(1, databaseName);
			ResultSet rs = statement.executeQuery();
			// System.out.println("                                                                              Before If    ");
			if (rs.next()) {
				logger.fine("Result set returned");
				/*
				 * postgres=# SELECT count(*) FROM pg_catalog.pg_database WHERE
				 * datname = 'testdb'; count ------- 1 (1 row)
				 */
				int rowsReturned = Integer.parseInt(rs.getString(1));
				logger.info("Rows returned: " + rowsReturned);
				if (rowsReturned > 0) {
					return true;
				}
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		return false;
	}
}
