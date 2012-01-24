package uk.ac.ox.oucs.iam.security.utilities.exceptions;

@SuppressWarnings("serial")
public class NoEncodingException extends Exception {
	public NoEncodingException() {
		super("This algorithm doesn't support encoding");
	}
} 
