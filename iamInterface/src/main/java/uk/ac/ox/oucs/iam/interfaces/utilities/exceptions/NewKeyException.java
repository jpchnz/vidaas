package uk.ac.ox.oucs.iam.interfaces.utilities.exceptions;

import uk.ac.ox.oucs.iam.interfaces.security.keys.KeyServices;

@SuppressWarnings("serial")
public class NewKeyException extends Exception {
	public NewKeyException(String keyname) {
		super(
				"No key pair was present on the client. A new key pair has now been "
						+ "created and is called "
						+ keyname
						+ ". The *public* portion of this"
						+ " (i.e. "
						+ keyname
						+ KeyServices.publicKeyNameExtension
						+ ") should be copied to the destination machine in the predefined key location. Once this has been done,"
						+ " the method may be called again.");
	}
}
