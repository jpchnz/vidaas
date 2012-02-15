package uk.ac.ox.oucs.vidaas.session;

import java.util.List;
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
        private SsoAuthenticator ssoAuthenticator;
        private String loginFailed = "";
        public static boolean useSso = false;
        public void setUseSso(boolean useSso) {
			Authenticator.useSso = useSso;
		}


		private boolean performAutomaticRegistration = true;
        
        public Authenticator() {
        	ssoAuthenticator = new SsoAuthenticator();
        }
        

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

        public boolean authenticate() {
        	log.debug("Authenticate with sso == " + useSso);
        	if (useSso) {
        		return authenticateViaSso();
        	}
        	//checkHeaderForTargetedId();
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
	 * Determine the email address that the user will authenticate under in an SSO environemnt.
	 * 
	 * @return null if the user has not registered, else their email address
	 */
	private String checkEmailAddress() {
		String email = ssoAuthenticator.getEmailAddress();
		log.debug(String.format("Email is:%s", email));
		List<Users> userList = usersHome.findUserByEmail(email);
		log.debug(String.format("We have %d entries", userList.size()));
		
		if (userList.size() == 0) {
			return null;
		}
		else if (userList.size() == 1) {
			// We have the user
		}
		else {
			/*
			 * This should surely not happen - more than 1 user with the same email address.
			 * For now, let us log this as an error and continue
			 */
			log.error("More than 1 user registered with email address " + email);
		}
		
		return email;
	}
	
	
	/**
	 * Determine which panel menu.xhtml will show when the user tries to log in. 
	 * If the user has registered themselves with VIDaaS then loginUsingShibPanel
	 * is shown to them, telling them the user email they are about to log in with. 
	 * If they haven't, registrationPanel-1 is shown to them, allowing them to
	 * register themselves.
	 * 
	 * @return the panel they are to be shown when they click on the login link on the web page
	 */
	public String whichPanel() {
		String ret;
		String email = checkEmailAddress();
		if (email == null) {
			ret = "registrationPanel-1";
			((RegistrationBean) Contexts.getSessionContext().get(
                    "registration")).setEmailField(ssoAuthenticator.getEmailAddress());
		}
		else {
			ret = "loginUsingShibPanel";
		}
		log.debug("whichPanel:" + ret);
		return ret;
	}
        
	
	/**
	 * Provides user authentication for environments with SSO enabled
	 * 
	 * @return true if authentication was setup correctly, else false
	 */
	private boolean authenticateViaSso() {
		log.debug("authenticateViaSso");
		
		((NavigationController) Contexts.getSessionContext().get("navigationController")).defaultHomePage();
		
		disableLogin = true;
		loginFailed = "";
		loginAttemptedAndFailed = false;
		
		
		String email = checkEmailAddress();
		if (email == null) {
			log.warn("No email found for this user");
			/*
			 * No entries exist - the user first needs to register themselves.
			 * Or, we could register them ourselves!
			 */
			if (performAutomaticRegistration) {
				log.debug("Creating the user automatically");
				user = usersHome.getInstance();
				user.setEmail(email);
				Logins logins = loginsHome.getInstance();
				usersHome.setId(user.getUserId());
				logins.setUserName(email);
				logins.setUsers(usersHome.getInstance());
				log.debug("User created automatically");
			}
			else {
				log.error("Yet to be implemented");
			}
		}
		
		

		try {
			log.debug(String.format("About to set up system for user %s", email));
			loginsHome.setLoginsUserName(email);
			log.debug("Email set");
			Logins login = loginsHome.getInstance();

			log.debug("We have logins home var set up");
			user = login.getUsers();
			log.debug("User got");
			identity.addRole(user.getPosition());

			usersHome.setId(user.getUserId());
			usersHome.getInstance().setUserId(user.getUserId());
			
			log.debug("Set contexts now");

			Contexts.getSessionContext().set("userMain", user);
			Contexts.getSessionContext().set("loginMain", login);

			log.debug(String.format("User's first name is:", user.getFirstName()));

			loginAttemptedAndFailed = false;
			return true;
		}
		catch (org.jboss.seam.framework.EntityNotFoundException exception) {
			loginAttemptedAndFailed = true;
			loginFailed = "Username not found. Please, try Again";
			log.error("Entity not found");
		}
		// disableLogin = false;
		return false;
	}
	
	
        
	/**
	 * Check the current header value of AJP_targeted-id
	 * 
	 * @return the header value, or "" if not present
	 */
	public static String checkHeaderForTargetedId() {
		String targetedId = "";
		boolean printAllHeaderValues = true;

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
					if ((headers.get(h) != null) && (headers.get(h).length() != 0)) {
						System.out.println("Header: " + h + " - value: <" + headers.get(h) + ">");
					}
				}
			}
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

	public SsoAuthenticator getSsoAuthenticator() {
		return ssoAuthenticator;
	}

	public boolean isUseSso() {
		return useSso;
	}
}