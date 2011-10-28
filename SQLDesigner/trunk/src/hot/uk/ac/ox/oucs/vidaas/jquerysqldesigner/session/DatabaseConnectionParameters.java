package uk.ac.ox.oucs.vidaas.jquerysqldesigner.session;

import java.io.Serializable;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.security.Identity;

@SuppressWarnings("serial")
@Name("databaseConnectionParameters")
public class DatabaseConnectionParameters {

	private String connectionURL;
	private String userName;
	private String password;

	//@In
	//DBConnection dbConnection;

	@WebRemote
	public String sayHello(DBConnection dbConnectionValue) {
		//System.out.println(userName);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Connection URL, " + dbConnectionValue.getUserName();
	}

	public String getConnectionURL() {
		return connectionURL;
	}

	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		System.out.println("setUserName: " + userName);
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
