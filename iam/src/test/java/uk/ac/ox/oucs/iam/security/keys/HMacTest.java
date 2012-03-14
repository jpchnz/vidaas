package uk.ac.ox.oucs.iam.security.keys;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Inputs: remote_MacKey - Mac key generated on a remote machine
 * hMacString.source.remote - string encrypted on the remote machine using remote_MacKey
 * 
 * NOTE
 * Currently these tests have been disabled. The underlying code has changed and these tests may need to be rewritten.
 * This will involve generating an HMAC on a remote machine that I currently
 * don't have access to.
 * 
 * @author dave
 * 
 */
public class HMacTest {
//	private static final String CORRECT_PERM_MESSAGE = "This is a secret HMAC message"; // The message used to generate the HMAC key on the remote machine
//	private static String encryptedStringFromSource;
//	private static File remoteMacKey = new File("testfiles" + File.separator + "remote_MacKey");
//
//	/**
//	 * Perform init functions
//	 * @throws IOException 
//	 */
//	@BeforeClass
//	public static void initialise() throws IOException {
//		encryptedStringFromSource = (String) GeneralUtils
//				.readObjectFromFile("testfiles" + File.separator + "hMacString.source.remote");
//	}
	@Test
	public void dummy() {
		assertTrue(true);
	}
//	
//
//	/**
//	 * Test to ensure we can generate a hmac using remote hmac key and correct
//	 * string and get the same message result
//	 */
//	@Test
//	public void checkRemoteMacKeyWithCorrectMessagePasses() {
//		try {
//			HMacReceiver hMacGeneratedLocally = new HMacReceiver(
//					remoteMacKey.getAbsolutePath(),
//					CORRECT_PERM_MESSAGE);
//			assertTrue(encryptedStringFromSource.equals(hMacGeneratedLocally
//					.gethMacString()));
//		} catch (Exception e) {
//			assertTrue(false);
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Test to ensure we can generate a hmac using remote hmac key and incorrect
//	 * string and not get the same message result
//	 */
//	@Test
//	public void checkRemoteMacKeyWithIncorrectMessageFails() {
//		try {
//			HMacReceiver hMacGeneratedLocally = new HMacReceiver(
//					remoteMacKey.getAbsolutePath(),
//					CORRECT_PERM_MESSAGE + ".wrong");
//			assertFalse(encryptedStringFromSource.equals(hMacGeneratedLocally
//					.gethMacString()));
//		} catch (Exception e) {
//			assertTrue(false);
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Test to ensure we can generate a hmac using a local hmac key and correct
//	 * string and not get the same message result
//	 */
//	@Test
//	public void checkLocalMacKeyWithCorrectMessageFails() {
//		try {
//			// Create local Mackey
//			HMacGenerator hMacGeneratedLocally = new HMacGenerator(
//					new File("local_MacKey2").getAbsolutePath(),
//					CORRECT_PERM_MESSAGE);
//			assertFalse(encryptedStringFromSource.equals(hMacGeneratedLocally
//					.gethMacString()));
//		} catch (Exception e) {
//			assertTrue(false);
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Test to ensure we can generate a hmac using a local hmac key and
//	 * incorrect string and not get the same message result
//	 */
//	@Test
//	public void checkLocalMacKeyWithIncorrectMessageFails() {
//		try {
//			// Create local Mackey
//			HMacGenerator hMacGeneratedLocally = new HMacGenerator(
//					new File("local_MacKey1").getAbsolutePath(),
//					CORRECT_PERM_MESSAGE + ".wrong");
//			assertFalse(encryptedStringFromSource.equals(hMacGeneratedLocally
//					.gethMacString()));
//		} catch (Exception e) {
//			assertTrue(false);
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Sanity check on key files
//	 */
//	@Test
//	public void keyFileTest() {
//		System.out.println("keyFileTest");
//
//		/*
//		 * Create a new key - allow it to be created
//		 */
//		try {
//			File f1 = new File("test_key_1");
//			new HMacGenerator(f1.getAbsolutePath(), CORRECT_PERM_MESSAGE + ".wrong");
//			assertTrue(f1.getAbsoluteFile().exists());
//			if (Platform.isLinux()) {
//				assertTrue(KeyServicesTest.checkFilePermissions(f1));
//			}
//		} catch (Exception e) {
//			assertTrue(false);
//		}
//
//		/*
//		 * Create a new key - don't allow it to be created
//		 */
//		try {
//			File f1 = new File("test_key_2");
//			new HMacReceiver(f1.getAbsolutePath(), CORRECT_PERM_MESSAGE + ".wrong");
//			assertFalse(f1.getAbsoluteFile().exists());
//		} catch (KeyNotFoundException e) {
//		} catch (Exception e) {
//			assertTrue(false);
//		}
//		System.out.println("\tsuccess");
//	}
//
//	@AfterClass
//	public static void finish() {
//		new File("test_key_1").getAbsoluteFile().delete();
//		new File("local_MacKey1").getAbsoluteFile().delete();
//		new File("local_MacKey2").getAbsoluteFile().delete();
//	}
}
