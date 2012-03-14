package uk.ac.ox.oucs.iam.security.keys;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.NoEncodingException;
import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;

/**
 * NOTE: Currently the class this tests is not needed, so tests disabled
 * 
 * Inputs: cryptkey.source - symmetric key generated on a remote machine
 * encString0-5 - encrypted messages from the remote machine that we need to
 * decrypt permanentSymKey - a file created during the first run of the tests
 * and then used in subsequent runs
 * 
 * @author oucs0153
 * 
 */
public class CryptServicesTest {
	private static boolean disabled = true;
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
	 * Check we cannot decrypt remotely prepared encrypted strings with a newly
	 * created key
	 * 
	 * @throws NoEncodingException
	 * @throws KeyNotFoundException
	 * @throws IOException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	@Test(expected = BadPaddingException.class)
	public void decryptRemoteStringsWithLocalKey() throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IOException, KeyNotFoundException, NoEncodingException, IllegalBlockSizeException,
			BadPaddingException {
		if (disabled) {
			throw new BadPaddingException();
		}
		System.out.println("decryptRemoteStringsWithLocalKey");

		int counter = 0;
		CryptServices cs = new CryptServices(localKey.getAbsolutePath(), true);
		for (String s : msg) {
			if (s.length() == 0) {
				System.out.println("\tTests finished fine");
				break;
			}
			String fileName = "testfiles" + File.separator + "encString" + counter;
			System.out.println("\tCheck file:" + new File(fileName).getAbsolutePath());
			String toTest = (String) GeneralUtils.readObjectFromFile(fileName);
			System.out.println("\tTest if <" + toTest + "> decrypts to <" + msg[counter] + "> (it should not)");
			assertFalse(cs.decrypt(toTest).equals(msg[counter]));

			counter++;
		}
	}

	/**
	 * When executed for the first time, this will encrypt message strings and
	 * write them to file, then create a permanent key file and assert false.
	 * The next time through, it will use that key file, read the strings and
	 * use the permanent key file to decrypt the strings to make sure they can
	 * be persisted.
	 * 
	 * @throws NoEncodingException
	 * @throws KeyNotFoundException
	 * @throws IOException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	@Test
	public void checkPersistance() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IOException, KeyNotFoundException, NoEncodingException, IllegalBlockSizeException, BadPaddingException {
		if (disabled) {
			return;
		}
		System.out.println("checkPersistance");
		CryptServices cs = new CryptServices(permanentKey.getAbsolutePath(), false);
		int count = 0;
		for (String s : msg) {
			if ((s == null) || (s.length() == 0)) {
				System.out.println("\tTests finished fine");
				break;
			}
			String fileToTest = "./symEnc" + count;
			System.out.println("\tTest file " + fileToTest);
			String toTest = (String) GeneralUtils.readObjectFromFile(fileToTest);
			count++;
			assertTrue(cs.decrypt(toTest).equals(s));
			System.out.println("\ttested string");
		}
		System.out.println("\tsuccess");
	}

	private void setupTestingFiles() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IOException, KeyNotFoundException, NoEncodingException, IllegalBlockSizeException, BadPaddingException {
		if (disabled) {
			return;
		}
		/*
		 * This is the first time through - set up persistance strings to file
		 */
		CryptServices cs = new CryptServices(permanentKey.getAbsolutePath(), true);
		int count = 0;
		for (String s : msg) {
			if (s == null) {
				break;
			}
			String enc = cs.encrypt(s);
			GeneralUtils.writeObject("./symEnc" + count++, enc);
		}
		System.out.println("\tIt appears this is the first tme through this test - a permanent key has been\n"
				+ "\tcreated. Please now re-run the test. If you see this message again then there is a problem!");
	}

	/**
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws NoEncodingException
	 * @throws KeyNotFoundException
	 * @throws IOException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * 
	 */
	@Test
	public void basicEncryptionTests() throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IOException, KeyNotFoundException, NoEncodingException {
		if (disabled) {
			return;
		}
		System.out.println("basicEncryptionTests");

		CryptServices cs;
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
	}

	/**
	 * Remove files generated for the tests
	 */
	@AfterClass
	public static void finish() {
		if (disabled) {
			return;
		}
		new File("localKey").getAbsoluteFile().delete();
		new File("cryptKey").getAbsoluteFile().delete();
	}
}
