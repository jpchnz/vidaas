package uk.ac.ox.oucs.vidaas.session;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import uk.ac.ox.oucs.vidaas.entity.Logins;
import uk.ac.ox.oucs.vidaas.entity.Users;
import uk.ac.ox.oucs.vidaas.dao.LoginsHome;
import uk.ac.ox.oucs.vidaas.dao.UsersHome;

import uk.ac.ox.oucs.vidaas.utility.SystemVars;

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

		public void setLoginAttemptedAndFailed(boolean loginAttemptedAndFailed) {
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
                ((NavigationController) Contexts.getSessionContext().get(
                                "navigationController")).defaultHomePage();

                disableLogin = true;
                loginFailed = "";
                loginAttemptedAndFailed = false;

                // log.info("authenticating {0}", credentials.getUsername());
                // log.info("authenticating {0}", credentials.getPassword());
                try {
                        loginsHome.setLoginsUserName(credentials.getUsername());
                        Logins login = loginsHome.getInstance();

                        // This if condition will never be executed
                        // EntityNotFoundException will be thrown ...!
                        if (login == null) {
                                loginAttemptedAndFailed = true;
                                loginFailed = "Username not found. Please, try Again";
                                return false;
                        } else if (login.getPassword().equals(credentials.getPassword())) {
                                user = login.getUsers();
                                identity.addRole(user.getPosition());

                                usersHome.setId(user.getUserId());
                                usersHome.getInstance().setUserId(user.getUserId());
                                // Adding userMain in the context

                                Contexts.getSessionContext().set("userMain", user);
                                Contexts.getSessionContext().set("loginMain", login);

                                System.out.println(user.getFirstName());

                                loginAttemptedAndFailed = false;
                                return true;
                        } else {
                                loginAttemptedAndFailed = true;
                                loginFailed = "Login Failed Try Again";
                        }
                } catch (org.jboss.seam.framework.EntityNotFoundException exception) {
                        loginAttemptedAndFailed = true;
                        loginFailed = "Username not found. Please, try Again";
                }
                // disableLogin = false;
                return false;
        }
        
        /**
    	 * Check the current header value of AJP_targeted-id
    	 * @return the header value, or "" if not present
    	 */
    	public static String checkHeaderForTargetedId() {
    		String targetedId = "";
    		boolean printAllHeaderValues = false;
    		
    		FacesContext fc = FacesContext.getCurrentInstance();
    		ExternalContext ec = fc.getExternalContext();
    		Map<String, String> headers = ec.getRequestHeaderMap();
    		if (SystemVars.USE_SSO_IF_AVAILABLE) {
    			targetedId = headers.get("AJP_targeted-id");
    			if (targetedId == null) {
    				targetedId = "";
    			}
    			if (printAllHeaderValues) {
    				for (String h : headers.keySet()) {
    					if ( (headers.get(h) != null) && (headers.get(h).length() != 0) ) {
    						System.out.println("Header: " + h + " - value: <" + headers.get(h) + ">");
    					}
    				}
    			}
    		}
    		
    		 
    		return targetedId;
    	}

        public void logout() {
                log.info("Authenticator Logout called", "");
                ((NavigationController) Contexts.getSessionContext().get(
                                "navigationController")).defaultHomePage();
                Session session = Session.instance();
                session.invalidate();
                identity.logout();
                // return result;
        }


}