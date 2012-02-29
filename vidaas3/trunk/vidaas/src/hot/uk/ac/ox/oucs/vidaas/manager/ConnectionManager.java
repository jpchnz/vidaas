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

    public ConnectionManager(){
        Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, "Default Constructor called");
    }

    private void readPropertiesFile() {
        //Properties props = new Properties();
        try {
            //InputStream in = this.getClass().getResourceAsStream("sudamih.properties");
            //props.load(in);
            //in.close();
            this.adminUserName = System.getProperty("uk.ac.ox.oucs.sudamih.adminUserName");
            this.adminUserNamePW = System.getProperty("uk.ac.ox.oucs.sudamih.adminUserNamePW");
            this.databaseName = System.getProperty("uk.ac.ox.oucs.sudamih.databaseName");
            this.driverName = System.getProperty("uk.ac.ox.oucs.sudamih.driverName");
            this.databaseURL = System.getProperty("uk.ac.ox.oucs.sudamih.databaseURL");
        } catch (Exception ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(this.adminUserName + " " + this.adminUserNamePW
                + " " + this.databaseName + " " + this.driverName + " " + this.databaseURL);

    }

    private Connection createConnection() {
        Connection connectionTemp= null;
        readPropertiesFile();
        try {
            Class.forName(driverName);    
            connectionTemp = DriverManager.getConnection(databaseURL + databaseName, adminUserName, adminUserNamePW);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        return connectionTemp;
    }

    /**/
    public Connection createConnection(String databaseNameValue) {
        Connection connectionTemp= null;
        readPropertiesFile();
        this.databaseName = databaseNameValue;
        System.out.println(this.adminUserName + " " + this.adminUserNamePW
                + " " + this.databaseName + " " + this.driverName + " " + this.databaseURL);
        try {
            Class.forName(driverName);

            // Only for testing purposes ...!
            //con = DriverManager.getConnection("jdbc:postgresql:" + dbName, "postgres", "bna192");

            connectionTemp = DriverManager.getConnection(databaseURL + databaseName, adminUserName, adminUserNamePW);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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


 
    public String getAdminUserName() {
    	readPropertiesFile();
		return adminUserName;
	}

	public String getAdminUserNamePW() {
		readPropertiesFile();
		return adminUserNamePW;
	}

	public static void main(String argv[]) {
    	ConnectionManager cm = new ConnectionManager();
        cm.getConnection();
         if (cm.connection != null){
             System.out.println("Connection created Successfully ...!");
         }
    }
}
