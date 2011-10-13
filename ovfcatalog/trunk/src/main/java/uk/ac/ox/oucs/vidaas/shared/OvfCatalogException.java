package uk.ac.ox.oucs.vidaas.shared;

/**
 * Simple Exception class
 * 
 * @author chris
 *
 */
public class OvfCatalogException extends Exception {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public OvfCatalogException() {
		super();
	}
	
	/**
	 * Constructor
	 * 
	 * @param message message
	 */
	public OvfCatalogException(String message) {
		super(message);
	}
}
