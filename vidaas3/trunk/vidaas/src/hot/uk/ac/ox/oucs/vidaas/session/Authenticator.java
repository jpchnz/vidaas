package uk.ac.ox.oucs.vidaas.session;

import java.util.List;
import java.util.Map;

import uk.ac.ox.oucs.vidaas.entity.Logins;
import uk.ac.ox.oucs.vidaas.entity.Users;
import uk.ac.ox.oucs.vidaas.utility.SystemVars;
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
import org.jboss.seam.web.Session;

@Name("authenticator")
public class Authenticator {
        @Logger
        private Log log;

        @In
        Identity identity;
        @In
        Credentials credentials;

        Users user;

        @In(create = true)
        LoginsHome loginsHome;

        @In(create = true)
        @Out(required = true)
        UsersHome usersHome;

        
        private boolean loginAttemptedAndFailed = false;
        private boolean disableLogin = false;

        private String loginFailed = "";

        public boolean isLoginAttemptedAndFailed() {
                return loginAttemptedAndFailed;
        }

        public void setLoginAttemptedAndFailed(
                        boolean loginAttemptedAndFailed) {
                this.loginAttemptedAndFailed = loginAttemptedAndFailed;
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

        
      public boolean authenticateUsingShib() {
  		boolean ret = false;
  		
  		boolean debug = false;
  		if (debug) {
  			// Print vars
  			Map<String, String> env = System.getenv();
  	        for (String envName : env.keySet()) {
  	        	System.out.println("Here we go...");
  	            System.out.format("%s=%s%n",
  	                              envName,
  	                              env.get(envName));
  	        }
  		}
  		
  		/*
  		 * We should <somehow> be able to get at the header variable AJP_targeted-id, passed from Apache2 into
  		 * Seam. I need to find out how. For now, assume we can access it. 
  		 */
  		
  		
  		return ret;
  	}

	

	public boolean authenticate() {
		((NavigationController) Contexts.getSessionContext().get("navigationController")).defaultHomePage();

		disableLogin = true;
		loginFailed = "";
		loginAttemptedAndFailed = true;
		
		String shibTargetedId = checkHeaderForTargetedId(); 

		// log.info("authenticating {0}", credentials.getUsername());
		// log.info("authenticating {0}", credentials.getPassword());
		try {
			/*
			 * First try to understand if the user has already logged in via
			 * their SSO id. If so, we can simply log them in here (if they have
			 * previously registered) or register them and then log them in
			 * here.
			 * If (shibTargetedId == null || shibTargetedId == "")
			 * then 
			 * 		there is no SSO credential. Log them in normally
			 * else
			 * 		if they have a VIDaaS account
			 * 			log them in using it
			 * 		else
			 * 			create the account for them
			 */
			List<Logins> loginViaShibList = loginsHome.findByShibTargetedId(shibTargetedId);
			if (SystemVars.USE_SSO_IF_AVAILABLE && ((loginViaShibList != null) && (loginViaShibList.size() > 0) )) {
				/*
				 * We should have a single entry in loginViaShibList. Otherwise something bad
				 * has happened and we need to report that to admin. We could
				 * simply choose the first entry in the list and use that, but
				 * safer to fail login and report. Note that this circumstance
				 * should never happen.
				 */
				if (loginViaShibList.size() > 1) {
					log.error("Problem with user login attempt - more than one login instance with their credentials");
					loginFailed = "Duplicate internal SSO credentials - please contact admin";
				}
				else {
					setupUsers(loginViaShibList.get(0));
				}
			} 
			else {
				loginsHome.setLoginsUserName(credentials.getUsername());
				Logins login = loginsHome.getInstance();

				// This if condition will never be executed
				// EntityNotFoundException will be thrown ...!
				if (login == null) {
					loginFailed = "Username not found. Try Again";
				}
				else if (login.getPassword().equals(credentials.getPassword())) {
					setupUsers(login);
				} 
				else {
					loginFailed = "Login Failed Try Again";
				}
			}
		} catch (org.jboss.seam.framework.EntityNotFoundException exception) {
			loginFailed = "Username not found. Try Again";
		}
		// disableLogin = false;
		return !loginAttemptedAndFailed;
	}
	
	private void setupUsers(Logins login) {
		user = login.getUsers();
		identity.addRole(user.getPosition());

		usersHome.setId(user.getUserId());
		usersHome.getInstance().setUserId(user.getUserId());
		// Adding userMain in the context

		Contexts.getSessionContext().set("userMain", user);
		Contexts.getSessionContext().set("loginMain", login);

		System.out.println(user.getFirstName());

		loginAttemptedAndFailed = false;
	}
	
	
	/**
	 * Check the current header value of AJP_targeted-id
	 * @return the header value, or null if not present
	 */
	public static String checkHeaderForTargetedId() {
		String targetedId = null;
		
		/*
		 * TODO
		 * This needs to be properly queried
		 * jsf get servlet context
		 */
		if (SystemVars.USE_SSO_IF_AVAILABLE) {
			targetedId = "dpdpdp2";
		}
		 
		return targetedId;
	}

	public void logout() {
		log.info("Authenticator Logout called", "");
		((NavigationController) Contexts.getSessionContext().get("navigationController")).defaultHomePage();
		Session session = Session.instance();
		session.invalidate();
		identity.logout();
		// return result;
	}


}