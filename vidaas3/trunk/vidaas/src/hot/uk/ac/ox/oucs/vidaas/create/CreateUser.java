package uk.ac.ox.oucs.vidaas.create;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.ox.oucs.vidaas.manager.ConnectionManager;

/**
 *
 * @author Asif Akram
 */
public class CreateUser {

    private String userName = null;
    private String userPassword = null;
    private String databaseName = null;
    private static Connection connection = null;
    private Statement statement = null;
    private Logger logger = Logger.getLogger(CreateUser.class.getName());

    public CreateUser(){
    	logger.fine("CreateUser default constructor");
    }
    
    public CreateUser(String userNameValue, String userPasswordValue, String databaseNameValue) {
        this.userName = userNameValue.toLowerCase();
        this.userPassword = userPasswordValue;
        this.databaseName = databaseNameValue.toLowerCase();
        logger.fine(String.format("CreateUser constructor(%s, ..., %s)", userNameValue, databaseNameValue));
    }

    /**
     * Get the value of userPassword
     *
     * @return the value of userPassword
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Set the value of userPassword
     *
     * @param userPassword new value of userPassword
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * Get the value of userName
     *
     * @return the value of userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the value of userName
     *
     * @param userName new value of userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean createUser(boolean owner, boolean admin) {
        connection = new ConnectionManager().createConnection(databaseName);
        
        String tempUserName = userName;
        
        if (this.userExist(tempUserName)) {
        	int randomNumber = (int)((Math.random() * 1000) + 100);
        	tempUserName = userName + randomNumber;
        }

        //System.out.println(this.userExist());
        try {
            statement = connection.createStatement();
            
            if(!this.userExist(tempUserName)){
	            //statement = connection.prepareStatement("CREATE USER " + userName  + " WITH PASSWORD '" + userPassword + "'");
	            statement.executeUpdate("CREATE USER " + tempUserName + " WITH PASSWORD '" + userPassword + "'");
	            
	            if(admin){
	                //statement = connection.prepareStatement("GRANT ALL PRIVILEGES ON DATABASE testdb1 to tom");
	                statement.executeUpdate("GRANT ALL PRIVILEGES ON DATABASE " + databaseName + " to " + tempUserName);
	                //statement.executeUpdate("GRANT ALL PRIVILEGES ON SCHEMA " + databaseName + " to " + userName);
	            } else {
	                // There is no such option ...
	                // have to come with any clever trick
	                //statement.executeUpdate("GRANT SELECT ON  " + databaseName + " to " + userName);
	            }

	            if (owner) {
	                statement.executeUpdate("ALTER DATABASE " + databaseName + " OWNER TO " + tempUserName);
	                //statement.executeUpdate("GRANT ALL PRIVILEGES ON SCHEMA " + databaseName + " to " + userName);
	            }
            } else {
            	createUser(owner, admin);
            }
            
             System.out.println(this.userExist(tempUserName));
             connection.close();
             connection = null;
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex);
            try {
				connection.close();
				connection = null;
			} catch (SQLException e) {			
				e.printStackTrace();
			}
        }
        return false;
    }
    
    public void grantDatabaseOwnership(String databaseNameVal, String userNameVal){
    	connection = new ConnectionManager().createConnection(databaseNameVal);
    	try {
            statement = connection.createStatement();
            statement.executeUpdate("ALTER DATABASE " + databaseNameVal + " OWNER TO " + userNameVal);
    	} catch (SQLException ex) {
    		logger.log(Level.SEVERE, null, ex);
            try {
				connection.close();
				connection = null;
			} catch (SQLException e) {			
				e.printStackTrace();
			}
        }
    }
    
    public void grantDatabaseAdmin(String databaseNameVal, String userNameVal){
    	connection = new ConnectionManager().createConnection(databaseNameVal);
    	try {
            statement = connection.createStatement();
            statement.executeUpdate("GRANT ALL PRIVILEGES ON DATABASE " + databaseNameVal + " TO " + userNameVal);
    	} catch (SQLException ex) {
            Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex);
            try {
				connection.close();
				connection = null;
			} catch (SQLException e) {			
				e.printStackTrace();
			}
        }
    }

    public boolean userExist(String userNameVal){
        if(connection == null){
            connection = new ConnectionManager().getConnection();
        }
        
        try {
            Statement statementTemp = connection.createStatement();

            // on Windows it is not case sensitive ....
            // to avoid complications making everything lower case
            String queryUserStatement = "SELECT count(*) FROM pg_shadow WHERE pg_shadow.usename like '" + userNameVal.toLowerCase() +"'";
           
            ResultSet rs = statementTemp.executeQuery(queryUserStatement);
            if (rs.next()) {
                /*
                 *  postgres=# SELECT count(*) FROM pg_shadow WHERE pg_shadow.usename like 'tom';
                     count
                    -------
                         1
                    (1 row)
                 */
                System.out.println(queryUserStatement + "      " + rs.getString(1));
                int rowsReturned = Integer.parseInt(rs.getString(1));
                if(rowsReturned > 0)
                    return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
}
