package uk.ac.ox.oucs.vidaas.session;

import uk.ac.ox.oucs.vidaas.entity.Logins;
import uk.ac.ox.oucs.vidaas.entity.Users;
import uk.ac.ox.oucs.vidaas.dao.LoginsHome;
import uk.ac.ox.oucs.vidaas.dao.UsersHome;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

@Name("authenticator")
public class Authenticator
{
    @Logger private Log log;

    @In Identity identity;
    @In Credentials credentials;
    
    Users user;
    
    @In (create = true)
    LoginsHome loginsHome;
    
    @In (create = true)
    @Out (required = true)
    UsersHome usersHome;

    private boolean loginAttemptedAndSuccessful = false;
    private boolean disableLogin = false;
    
    private String loginFailed = "";
    
    
    public boolean isLoginAttemptedAndSuccessful() {
		return loginAttemptedAndSuccessful;
	}


	public void setLoginAttemptedAndSuccessful(boolean loginAttemptedAndSuccessful) {
		this.loginAttemptedAndSuccessful = loginAttemptedAndSuccessful;
	}	

	public String getLoginFailed() {
		return loginFailed;
	}

	public void setLoginFailed(String loginFailed) {
		this.loginFailed = loginFailed;
	}

	public boolean isDisableLogin() {
		return disableLogin;
	}


	public void setDisableLogin(boolean disableLogin) {
		this.disableLogin = disableLogin;
	}


	public boolean authenticate()
    {
		disableLogin = true;
		loginFailed = "";
		loginAttemptedAndSuccessful = false;
		
        log.info("authenticating {0}", credentials.getUsername());
        log.info("authenticating {0}", credentials.getPassword());
        
        loginsHome.setLoginsUserName(credentials.getUsername());
    	Logins login = loginsHome.getInstance();
    	
    	if(login != null)
    		System.out.println (login.getPassword() + " == " + credentials.getPassword());
    	
        try{
        	Thread.sleep(1000);
        }catch (Exception e){
        	
        }
        
        if (login.getPassword().equals(credentials.getPassword()))
        {
        	user = login.getUsers();
    		identity.addRole(user.getPosition());
    		
    		usersHome.setId(user.getUserId());
    		usersHome.getInstance().setUserId(user.getUserId());
    		// Adding userMain in the context
    		
    		Contexts.getSessionContext().set("userMain", user);
    		Contexts.getSessionContext().set("loginMain", login);
    		
    		System.out.println(user.getFirstName());
    		
            loginAttemptedAndSuccessful = false;
            return true;
        } else {
        	loginAttemptedAndSuccessful = true;
        	loginFailed = "Login Failed Try Again";
        }
        //disableLogin = false;
        return false;
    }

}