package uk.ac.ox.oucs.vidaas.session;

import javax.security.auth.login.LoginException;

import uk.ac.ox.oucs.vidaas.dao.LoginsHome;
import uk.ac.ox.oucs.vidaas.dao.UsersHome;
import uk.ac.ox.oucs.vidaas.entity.Logins;
import uk.ac.ox.oucs.vidaas.entity.Users;

import org.hibernate.validator.Email;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

@Name("registration")
@Scope(ScopeType.SESSION)
public class RegistrationBean {

	@Logger
	Log log;

	// true means registration required
	// false means registration finished

	private boolean level1Registration = false;
	private boolean level1Registration2 = false;

	public String registrationFormInclude = "/popup/registerForm-1.xhtml";
	public String emailUniqueViolation = "";

	@In(create = true)
	@Out(required = true)
	UsersHome usersHome;

	Users user;

	private int userId;
	private String firstName;
	private String lastName;
	private String postion;
	private String department;
	private String grp;
	private String emailText;

	
	@Email
	private String email;

	@In(create = true)
	@Out(required = true)
	LoginsHome loginsHome;

	@In
	Credentials credentials;

	@In
	Identity identity;

	Logins logins;

	private String userName;
	private String password;
	private String password2;

	private String registrationMessage1 = "";
	private String registrationMessage2 = "";
	
	public RegistrationBean() {
		if (Authenticator.useSso) {
			emailText = "Our records indicate that you have not registered with VIDaaS using your SSO email address. ";
			emailText += "Since the University's SSO system is in use, the email address you must register with has been fixed.";
		}
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPostion() {
		return postion;
	}

	public void setPostion(String postion) {
		this.postion = postion;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getGrp() {
		return grp;
	}

	public void setGrp(String grp) {
		this.grp = grp;
	}
	
	

	public String getEmail() {
		SsoAuthenticator sso = new SsoAuthenticator();
		sso.setupShibbolethVariables();
		String emailField = sso.getEmail();
		if (emailField != null) {
			return emailField;
		}
		return email;
	}
	
	public String getEmailText() {
		return emailText;
	}

	public void setEmailText(String emailText) {
		this.emailText = emailText;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isLevel1Registration() {
		return level1Registration;
	}

	public void setLevel1Registration(boolean level1Registration) {
		this.level1Registration = level1Registration;
	}

	public boolean isLevel1Registration2() {
		return level1Registration2;
	}

	public void setLevel1Registration2(boolean level1Registration2) {
		this.level1Registration2 = level1Registration2;
	}

	public String getRegistrationFormInclude() {
		return registrationFormInclude;
	}

	public void setRegistrationFormInclude(String registrationFormInclude) {
		this.registrationFormInclude = registrationFormInclude;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getRegistrationMessage1() {
		return registrationMessage1;
	}

	public void setRegistrationMessage1(String registrationMessage1) {
		this.registrationMessage1 = registrationMessage1;
	}

	public String getRegistrationMessage2() {
		return registrationMessage2;
	}

	public void setRegistrationMessage2(String registrationMessage2) {
		this.registrationMessage2 = registrationMessage2;
	}

	public String getEmailUniqueViolation() {
		return emailUniqueViolation;
	}

	public void setEmailUniqueViolation(String emailUniqueViolation) {
		this.emailUniqueViolation = emailUniqueViolation;
	}

	public boolean registrationLevel1() {
		level1Registration = true;
		
		((NavigationController) Contexts.getSessionContext().get(
		"navigationController")).defaultHomePage();
		
		if (usersHome.findUserByEmail(email).isEmpty()) {

			user = usersHome.getInstance();

			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setPosition(postion);
			user.setDepartment(department);
			user.setGrp(grp);
			
			if (emailField != null) {
				System.out.println("We shall use:" + emailField);
				email = emailField;
			}
			user.setEmail(email);

			String tempPersistResult = usersHome.persist();
			System.out.println("usersHome.persist(): " + tempPersistResult
					+ user.getPosition() + "  " + this.postion);

			this.userId = user.getUserId();

			if (tempPersistResult.equalsIgnoreCase("persisted")) {
				level1Registration = false;
				level1Registration2 = true;
				registrationMessage1 = "";
				if (Authenticator.useSso) {
					/*
					 * Since we are using single sign on, the user doesn't need their own login
					 * id - it is provided for us.
					 */
					registrationFormInclude = "/popup/registerForm-3.xhtml";
					
					/*
					 * We should automatically create a user entry
					 */
					password = "AKTB1348dhnyt";
					password2 = password;
					userName = email;
					registrationLevel2();
				}
				else {
					registrationFormInclude = "/popup/registerForm-2.xhtml";
				}
				emailUniqueViolation = "";
				
				if (loginsHome.findByUserName(email).isEmpty()) {
					logins = loginsHome.getInstance();
					logins.setUserName(email);
					logins.setPassword("not being used");
					usersHome.setId(this.userId);
					logins.setUsers(usersHome.getInstance());
				}
			}
		} else {
			emailUniqueViolation = "'" + email + "' already registered";
		}
		return level1Registration;
	}

	public boolean registrationLevel2() {
		((NavigationController) Contexts.getSessionContext().get(
		"navigationController")).defaultHomePage();
		
		if (loginsHome.findByUserName(userName).isEmpty()) {
			if (password.equals(password2)) {
				logins = loginsHome.getInstance();

				logins.setUserName(userName.toLowerCase());
				logins.setPassword(password);

				System.out.println("User ID: " + this.userId);
				usersHome.setId(this.userId);

				logins.setUsers(usersHome.getInstance());

				String tempPersistResult = loginsHome.persist();
				/*
				 * System.out.println("usersHome.persist(): " +
				 * tempPersistResult + logins.getUserName() + "  " +
				 * logins.getPassword());
				 */

				credentials.setUsername(logins.getUserName());
				credentials.setPassword(logins.getPassword());

				if (tempPersistResult.equalsIgnoreCase("persisted")) {
					level1Registration2 = false;
					registrationMessage2 = "";
					registrationFormInclude = "/popup/registerForm-3.xhtml";
					// authenticator.authenticate();
					try {
						identity.authenticate();
					} catch (LoginException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				registrationMessage2 = "'Both passwords don't match.' Please Try again.";
			}
		} else {
			registrationMessage2 = "'" + userName + "' already exists";
		}

		return false;
	}
	

	String emailField = null;
	public void setEmailField(String emailField) {
		System.out.println("Setting email field:" + emailField);
		this.emailField = emailField;
		setEmail(emailField);
	}
	
	private void clearFields() {
		firstName = "";
		lastName = "";
		postion = "";
		department = "";
		grp = "";
		email = "";
		userName = "";
		password = "";
		password2 = "";

		registrationMessage1 = "";
		registrationMessage2 = "";
	}
	
	public void registrationSuccessful(){
		clearFields();
    	registrationFormInclude = "/popup/registerForm-1.xhtml";
    }
}
