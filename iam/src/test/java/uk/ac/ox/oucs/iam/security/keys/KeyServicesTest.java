package uk.ac.ox.oucs.iam.security.keys;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.ox.oucs.iam.GlobalTestVars;
import uk.ac.ox.oucs.iam.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.NoEncodingException;

import com.sun.jna.Platform;

public class KeyServicesTest {
	public static KeyServices keyServicesBlowfish, keyServicesHMAC;
	public static final String KEY_FILE_NAME_BLOWFISH = "." + File.separator + "blowFishTestkey";
	public static final String KEY_FILE_NAME_HMAC = "." + File.separator + "HMACTestkey";
	private static File[] fArray;
	private static boolean problemWithInit;
	
	
	/**
	 * Remove generated files
	 */
	@AfterClass
	public static void finish() {
		for (File f : fArray) {
			f.getAbsoluteFile().delete();
		}
	}

	/**
	 * Create some files to test with
	 * @throws NoEncodingException 
	 * @throws KeyNotFoundException 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	@BeforeClass
	public static void initialise() throws NoSuchAlgorithmException, IOException, KeyNotFoundException, NoEncodingException {
		problemWithInit = true;
		fArray = new File[2];
		fArray[0] = new File(KEY_FILE_NAME_BLOWFISH);
		fArray[1] = new File(KEY_FILE_NAME_HMAC);
		
		// Remove any keys
		for (File f : fArray) {
			f.getAbsoluteFile().delete();
		}

		keyServicesBlowfish = new KeyServices(KEY_FILE_NAME_BLOWFISH, true,
				"Blowfish");
		keyServicesHMAC = new KeyServices(KEY_FILE_NAME_HMAC, true,
				"HmacSHA512");
		problemWithInit = false;
	}
	
	/**
	 * Test the initialisation was successful
	 */
	@Test
	public void testInit() {
		if (!GlobalTestVars.runTests) {
			return;
		}
		System.out.println("testInit");
		assertTrue(keyServicesBlowfish != null);
		assertTrue(keyServicesHMAC != null);
		assertFalse(problemWithInit);
		System.out.println("\tchecked");
	}

	/**
	 * Check that the generated key exists and has the correct
	 * permissions
	 * @throws IOException 
	 */
	@Test
	public void keyFileTest() throws IOException {
		if (!GlobalTestVars.runTests) {
			return;
		}
		System.out.println("keyFileTest");
		
		for (File f : fArray) {
			System.out.println("Checking file " + f.getAbsolutePath()+ KeyServices.privateKeyNameExtension);
			assertTrue(new File(f.getAbsolutePath()+ KeyServices.privateKeyNameExtension).getAbsoluteFile().exists());
			assertTrue(checkFilePermissions(new File(f.getAbsolutePath()+ KeyServices.privateKeyNameExtension)));
			assertTrue(new File(f.getAbsolutePath()+ KeyServices.publicKeyNameExtension).getAbsoluteFile().exists());
			assertTrue(checkFilePermissions(new File(f.getAbsolutePath()+ KeyServices.publicKeyNameExtension)));
			
			System.out.println("\tfile checked");
		}
	}
	
	/**
	 * Helper routine to ensure the key files have the correct permissions
	 * @param f the file whose permissions are to test
	 * @return true if they do
	 * @throws IOException 
	 */
	public static boolean checkFilePermissions(File f) throws IOException {
		if (!GlobalTestVars.runTests) {
			return true;
		}
		if (!Platform.isLinux()) {
			System.out.println("Test not running on Linux - file permission test skipped");
			return true;
		}
		boolean ret = false;

		String[] cmd = new String[] { "ls", "-l", f.getAbsolutePath() };
		String result = "";
		Runtime rt = Runtime.getRuntime();
		Process pr;
		pr = rt.exec(cmd);
		InputStream in = pr.getInputStream();
		InputStreamReader ir = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(ir);
		String line = null;
		while ((line = br.readLine()) != null) {
			result += line;
		}
		ret = result.contains("-r--------");
		return ret;
	}
}
