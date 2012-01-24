package uk.ac.ox.oucs.iam.security.utilities.exceptions;

@SuppressWarnings("serial")
public class KeyNotFoundException extends Exception {
	public KeyNotFoundException() {
		super("Key not found");
	}
}
