package uk.ac.ox.oucs.vidaas.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.*;
import java.util.Properties;

/**
 *
 * @author Asif Akram
 */
public class ConnectionManager {

    private String adminUserName = null; // "sudamihadmin";
    private String adminUserNamePW = null; // "sudamihAdminPW";
    private String databaseName = null; // "sudamihtestdb";
    private String driverName = null; // "org.postgresql.Driver";
    private String databaseURL = null;
    private Connection connection = null;
    private ResultSet rs = null;
    private Statement st = null;
    private Logger logger = Logger.getLogger(ConnectionManager.class.getName());

    public ConnectionManager() {
    	logger.fine("Default Constructor called");
    }

    private void readPropertiesFile() {
        Properties props = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream("sudamih.properties");
            props.load(in);
            in.close();
            this.adminUserName = props.getProperty("uk.ac.ox.oucs.sudamih.adminUserName");
            this.adminUserNamePW = props.getProperty("uk.ac.ox.oucs.sudamih.adminUserNamePW");
            this.databaseName = props.getProperty("uk.ac.ox.oucs.sudamih.databaseName");
            this.driverName = props.getProperty("uk.ac.ox.oucs.sudamih.driverName");
            this.databaseURL = props.getProperty("uk.ac.ox.oucs.sudamih.databaseURL");
        } catch (IOException ex) {
        	logger.log(Level.SEVERE, "IOException", ex);
        }

        logger.fine(this.adminUserName + " " + this.adminUserNamePW
                + " " + this.databaseName + " " + this.driverName + " " + this.databaseURL);

    }

    private Connection createConnection() {
        Connection connectionTemp= null;
        readPropertiesFile();
        try {
            Class.forName(driverName);    
            connectionTemp = DriverManager.getConnection(databaseURL + databaseName, adminUserName, adminUserNamePW);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "ClassNotFoundException", ex);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQLException", ex);
        }

        return connectionTemp;
    }

    /**/
    public Connection createConnection(String databaseNameValue) {
        Connection connectionTemp= null;
        readPropertiesFile();
        this.databaseName = databaseNameValue;

        logger.fine(this.adminUserName + " " + this.adminUserNamePW
                + " " + this.databaseName + " " + this.driverName + " " + this.databaseURL);
        try {
            Class.forName(driverName);

            // Only for testing purposes ...!
            //con = DriverManager.getConnection("jdbc:postgresql:" + dbName, "postgres", "bna192");

            connectionTemp = DriverManager.getConnection(databaseURL + databaseName, adminUserName, adminUserNamePW);
        } catch (ClassNotFoundException ex) {
        	logger.log(Level.SEVERE, "ClassNotFoundException", ex);
        } catch (SQLException ex) {
        	logger.log(Level.SEVERE, "SQLException", ex);
        }

        return connectionTemp;
    }
    
    public Connection createConnection(String databaseNameValue, String userNameValue, String passwordValue) {
        Connection connectionTemp= null;
        readPropertiesFile();
        //this.databaseName = databaseNameValue;
        /*
        System.out.println(this.adminUserName + " " + this.adminUserNamePW
                + " " + this.databaseName + " " + this.driverName + " " + this.databaseURL); */
        try {
            Class.forName(driverName);

            // Only for testing purposes ...!
            //con = DriverManager.getConnection("jdbc:postgresql:" + dbName, "postgres", "bna192");

            connectionTemp = DriverManager.getConnection(databaseURL + databaseNameValue, userNameValue, passwordValue);
        } catch (ClassNotFoundException ex) {
        	logger.log(Level.SEVERE, "ClassNotFoundException", ex);
        } catch (SQLException ex) {
        	logger.log(Level.SEVERE, "SQLException", ex);
        }

        return connectionTemp;
    }

    public Connection getConnection(){
        if (connection == null){
            connection = createConnection();
        }
        return connection;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }


 
    public static void main(String argv[]) {
    	ConnectionManager cm = new ConnectionManager();
        cm.getConnection();
         if (cm.connection != null) {
             System.out.println("Connection created Successfully ...!");
         }
    }
}
