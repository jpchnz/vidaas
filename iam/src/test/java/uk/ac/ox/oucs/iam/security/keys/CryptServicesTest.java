package uk.ac.ox.oucs.iam.security.keys;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.crypto.BadPaddingException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.KeyNotFoundException;



/**
 * Inputs: cryptkey.source - symmetric key generated on a remote machine
 * encString0-5 - encrypted messages from the remote machine that we need to decrypt
 * permanentSymKey - a file created during the first run of the tests and then used in subsequent runs
 * 
 * @author oucs0153
 *
 */
public class CryptServicesTest {
	private static String[] msg;
	private static int numberOfStrings = 0;
	private static File permanentKey, remoteKey, localKey;

	@BeforeClass
	public static void initialise() {
		msg = new String[10];
		msg[numberOfStrings++] = "If you can read this then this is working!";
		msg[numberOfStrings++] = "!\"£$%^&*()_+";
		msg[numberOfStrings++] = "|\\@'~#:;";
		msg[numberOfStrings++] = "`¬-={}[]";
		msg[numberOfStrings++] = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		msg[numberOfStrings++] = "";

		permanentKey = new File("permanentSymKey");
		remoteKey = new File("testfiles" + File.separator + "cryptkey.source");
		localKey = new File("localKey");
	}
	
	
	/**
	 * Check we can decrypt remotely prepared encrypted strings
	 */
	@Test
	public void decryptRemoteStrings() {
		int counter = 0;
		try {
			CryptServices cs = new CryptServices(
					remoteKey.getAbsolutePath(), false);
			for (String s : msg) {
				if (s.length() == 0) {
					break;
				}
				String toTest = (String) GeneralUtils
						.readObjectFromFile("testfiles" + File.separator + "encString" + counter);
				assertTrue(cs.decrypt(toTest).equals(msg[counter]));
				counter++;
			}
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Check we cannot decrypt remotely prepared encrypted strings
	 * with a newly created key 
	 */
	@Test
	public void decryptRemoteStringsWithLocalKey() {
		int counter = 0;
		try {
			CryptServices cs = new CryptServices(
					localKey.getAbsolutePath(), true);
			for (String s : msg) {
				if (s.length() == 0) {
					break;
				}
				String toTest = (String) GeneralUtils
						.readObjectFromFile("testfiles" + File.separator + "encString" + counter);
				assertFalse(cs.decrypt(toTest).equals(msg[counter]));
				counter++;
			}
		} catch (BadPaddingException e) {
			// This is to be expected
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	/**
	 * When executed for the first time, this will encrypt message strings and
	 * write them to file, then create a permanent key file and assert false.
	 * The next time through, it will use that key file, read the strings and
	 * use the permanent key file to decrypt the strings to make sure they can
	 * be persisted.
	 */
	@Test
	public void checkPersistance() {
		System.out.println("checkPersistance");
		try {
			CryptServices cs = new CryptServices(
					permanentKey.getAbsolutePath(), false);
			int count = 0;
			for (String s : msg) {
				if (s == null) {
					break;
				}
				String toTest = (String) GeneralUtils
						.readObjectFromFile("./symEnc" + count++);
				assertTrue(cs.decrypt(toTest).equals(s));
				System.out.println("\ttested string");
			}
		} catch (KeyNotFoundException e) {
			setupTestingFiles();
			assertFalse(true);
		} catch (IOException e) {
			setupTestingFiles();
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
		System.out.println("\tsuccess");
	}
	
	private void setupTestingFiles() {
		try {
			/*
			 * This is the first time through - set up persistance strings
			 * to file
			 */
			CryptServices cs = new CryptServices(
					permanentKey.getAbsolutePath(), true);
			int count = 0;
			for (String s : msg) {
				if (s == null) {
					break;
				}
				String enc = cs.encrypt(s);
				GeneralUtils.writeObject("./symEnc" + count++, enc);
			}
			System.out
					.println("\tIt appears this is the first tme through this test - a permanent key has been\n"
							+ "\tcreated. Please now re-run the test. If you see this message again then there is a problem!");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	
	/**
	 * 
	 */
	@Test
	public void basicEncryptionTests() {
		System.out.println("basicEncryptionTests");

		CryptServices cs;
		try {
			cs = new CryptServices(true);

			for (String s : msg) {
				if (s == null) {
					break;
				}
				String enc = cs.encrypt(s);
				assertFalse(s.equals(enc));
				assertTrue(cs.decrypt(enc).equals(s));
				System.out.println("\ttested string");
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	
	/**
	 * Remove files generated for the tests
	 */
	@AfterClass
	public static void finish() {
		new File("localKey").getAbsoluteFile().delete();
		new File("cryptKey").getAbsoluteFile().delete();
	}
}
