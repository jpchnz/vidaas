package uk.ac.ox.oucs.iam.interfaces.utilities.exceptions;

@SuppressWarnings("serial")
public class DuplicateKeyException extends Exception {
	public DuplicateKeyException(int i) {
		super(String.format("More than one private key found in key folder (total of %d)", i));
	}
}
