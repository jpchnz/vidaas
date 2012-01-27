package uk.ac.ox.oucs.iam.security.keys;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.ox.oucs.iam.security.keys.KeyServices;

import com.sun.jna.Platform;

public class KeyServicesTest {
	public static KeyServices keyServicesBlowfish, keyServicesHMAC;
	public static final String KEY_FILE_NAME_BLOWFISH = "." + File.separator + "blowFishTestkey.priv";
	public static final String KEY_FILE_NAME_HMAC = "." + File.separator + "HMACTestkey.priv";
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
	 */
	@BeforeClass
	public static void initialise() {
		problemWithInit = true;
		fArray = new File[2];
		fArray[0] = new File(KEY_FILE_NAME_BLOWFISH);
		fArray[1] = new File(KEY_FILE_NAME_HMAC);
		
		// Remove any keys
		for (File f : fArray) {
			f.getAbsoluteFile().delete();
		}

		try {
			keyServicesBlowfish = new KeyServices(KEY_FILE_NAME_BLOWFISH, true,
					"Blowfish");
			keyServicesHMAC = new KeyServices(KEY_FILE_NAME_HMAC, true,
					"HmacSHA512");
			problemWithInit = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test the initialisation was successful
	 */
	@Test
	public void testInit() {
		System.out.println("testInit");
		assertTrue(keyServicesBlowfish != null);
		assertTrue(keyServicesHMAC != null);
		assertFalse(problemWithInit);
		System.out.println("\tchecked");
	}

	/**
	 * Check that the generated key exists and has the correct
	 * permissions
	 */
	@Test
	public void keyFileTest() {
		System.out.println("keyFileTest");
		
		for (File f : fArray) {
			System.out.println("Checking file " + f.getAbsolutePath());
			assertTrue(f.getAbsoluteFile().exists());
			assertTrue(checkFilePermissions(f));
			System.out.println("\tfile checked");
		}
	}
	
	/**
	 * Helper routine to ensure the key files have the correct permissions
	 * @param f the file whose permissions are to test
	 * @return true if they do
	 */
	public static boolean checkFilePermissions(File f) {
		if (!Platform.isLinux()) {
			System.out.println("Test not running on Linux - file permission test skipped");
			return true;
		}
		boolean ret = false;

		String[] cmd = new String[] { "ls", "-l", f.getAbsolutePath() };
		String result = "";
		Runtime rt = Runtime.getRuntime();
		Process pr;
		try {
			pr = rt.exec(cmd);
			InputStream in = pr.getInputStream();
			InputStreamReader ir = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(ir);
			String line = null;
			while ((line = br.readLine()) != null) {
				result += line;
			}
			ret = result.contains("-r--------");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
