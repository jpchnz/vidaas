package uk.ac.ox.oucs.iam.interfaces.utilities;


/**
 * Global variables to help with system wise settings
 * 
 * @author oucs0153
 *
 */
public class SystemVars {
	/**
	 * The full address of the IAM role serlvet
	 */
	//public static final String ADDRESS_OF_IAM_WEBAPP = "http://129.67.241.38/iam/ProjectRoleServlet";
	public static final String ADDRESS_OF_IAM_WEBAPP = "http://129.67.241.38/iam/ProjectRoleServlet";
	
	
	/**
	 * The address of the servlet that can receive secure POST data, validate it and 
	 * pass on to the destination
	 */
	public static final String ADDRESS_OF_IAM_WEBAPP_RECEIVER = "http://localhost:8080/iam/ReceivePost";;//"http://129.67.241.38/iam/ReceivePost";
	
	/**
	 * The address of the billing webapp
	 */
	public static final String ADDRESS_OF_VIDAAS_BILLING_APP = "http://localhost:8080/vidaasBilling/BillingServlet";//"http://129.67.103.124:8081/vidaasBilling";
	
	
	/*
	 * Here follows some standard constants used for posting and receiving data, so that, for example, we should never
	 * have the problem where a sender might send a parameter key of "projectuser" and the receiver expect "projectUser"
	 */
	public static final String POST_COMMAND_PROVIDE_UUID_OF_PUBLIC_KEY = "provideUuid";
	public static final String POST_COMMAND_PROVIDE_PUBLIC_KEY = "providePublicKey";
	public static final String POST_COMMAND_EMAIL_TOKEN = "u";
	public static final String POST_COMMAND_COMMAND_TOKEN = "c";
	public static final String POST_COMMAND_PROJECTNAME_TOKEN = "projectName";
	public static final String POST_COMMAND_PROJECTSPACE_TOKEN = "space";
	public static final String POST_COMMAND_BILLINGFREQUENCY_TOKEN = "billingFrequency";
	public static final String POST_COMMAND_PROJECTID_TOKEN = "projectId";
	public static final String POST_COMMAND_NEW_BILL = "bill";
	public static final String POST_COMMAND_NEW_PROJECT = "newProject";
	public static final String POST_COMMAND_UPDATE_PROJECT = "updateProject";
	public static final String POST_COMMAND_GENERATE_INVOICES = "generateInvoices";
	public static final String POST_COMMAND_NEW_DATA_AVAILABLE = "comeAndGetit";
	
	
	/**
	 * Final location of the keystore. This is the folder where keys, as files, are placed.
	 * TODO
	 * There is a work in progress to have public keys placed in a proper keystore and
	 * public keys added to a local database.
	 */
	public static final String locationOfKeyStore = "/User/dave/keyStore/";
	
	
	public static final boolean useMysql = false; // If true, use mySql to store public keys
	public static final String temporaryKeystorePassword = "This is a password";
	public static final String keyStoreAlias = "iamKeyStore";
	public static final String keyStoreFileName = ".iamKeyStore";
}
