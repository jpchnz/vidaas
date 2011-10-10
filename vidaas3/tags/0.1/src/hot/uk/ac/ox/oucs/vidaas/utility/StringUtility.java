package uk.ac.ox.oucs.vidaas.utility;

import java.util.regex.Pattern;

public class StringUtility {
	
	public static String stringValidation(String input) {
		Pattern escaper = Pattern.compile("(^[\\d]*)");
		Pattern escaper2 = Pattern.compile("[^a-zA-z0-9]");

		String newString = escaper.matcher(input).replaceAll("");
		String newString2 = escaper2.matcher(newString).replaceAll("");

		return newString2;

	}

}
