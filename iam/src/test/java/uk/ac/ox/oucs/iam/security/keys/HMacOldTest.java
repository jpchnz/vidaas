package uk.ac.ox.oucs.iam.security.keys;
//package uk.ac.ox.oucs.testsec;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import java.io.File;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.sun.jna.Platform;
//
//
///**
// * Inputs:
// * remote_MacKey - Mac key generated on a remote machine
// * hMacString - string encrypted using this key on the remote machine
// * @author dave
// *
// */
//public class HMacOldTest {
//	private static HMacGenerator[] key;
//	private static File[] fArray;
//	private static File permanentKey, permanentKey2;
//	private static HMacGenerator permanentHMac;
//	private static String permMsg = "This is a wrong permanent message, so think on";
//	private static String correctPermMsg = "This is a secret HMAC message";
//	private static boolean problemWithInit1, problemWithInit2, problemWithInit3;
//	
//	
//	
//	
//
//	@BeforeClass
//	public static void initialise() {
//		System.out.println("HMacKeyTest ...");
//		try {
//		String encryptedString = (String)GeneralUtils.readObjectFromFile("hMacString");
//		HMacGenerator newhMac2 = new HMacGenerator(new File("remote_MacKey").getAbsolutePath(), false, correctPermMsg);
//		String encryptedString2 = (String)GeneralUtils.readObjectFromFile("hMacString2");
//		}
//		catch (Exception e) {
//			int t = 8;
//		}
//		
//		problemWithInit1 = true;
//		problemWithInit2 = true;
//		problemWithInit3 = true;
//		
//		permanentKey = new File("permanentKey");
//		permanentKey2 = new File("permanentKey2");
//		fArray = new File[2];
//		fArray[0] = new File(KeyServicesTest.keyFileNameHMAC + "1");
//		fArray[1] = new File(KeyServicesTest.keyFileNameHMAC + "2");
//		for (File f : fArray) {
//			f.getAbsoluteFile().delete();
//		}
//		key = new HMacGenerator[2];
//
//		try {
//			String g = fArray[0].getAbsolutePath();
//			key[0] = new HMacGenerator(g, false, "Sample message 1");
//		} catch (KeyNotFoundException e) {
//			// Should not be able to find this key since not allowed to create
//			// it
//			problemWithInit1 = false;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		try {
//			key[1] = new HMacGenerator(fArray[1].getAbsolutePath(), true, "Sample message 2");
//			problemWithInit2 = false;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//
//		try {
//			permanentHMac = new HMacGenerator(permanentKey.getAbsolutePath(),
//					true, permMsg);
//			problemWithInit3 = false;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//	}
//	
//	@AfterClass
//	public static void finish() {
//		for (File f : fArray) {
//			f.getAbsoluteFile().delete();
//		}
//		permanentKey2.getAbsoluteFile().delete();
//	}
//	
//	
//	@Test
//	public void testInit() {
//		System.out.println("testInit");
//		assertFalse(problemWithInit1);
//		assertFalse(problemWithInit2);
//		assertFalse(problemWithInit3);
//		System.out.println("\tchecked");
//	}
//	
//	
//	@Test
//	public void checkRemoteMacKey() {
//		try {
//			String encryptedString = (String)GeneralUtils.readObjectFromFile("hMacString");
//			HMacGenerator newhMac2 = new HMacGenerator(new File("remote_MacKey").getAbsolutePath(), false, correctPermMsg);
//			String encryptedString2 = (String)GeneralUtils.readObjectFromFile("hMacString2");
//			byte[] digestB64 = new sun.misc.BASE64Decoder().decodeBuffer(encryptedString);
//			HMacGenerator newhMac = new HMacGenerator(new File("remote_MacKey").getAbsolutePath(), false, permMsg);
//			encryptedString2 = (String)GeneralUtils.readObjectFromFile("hMacString2");
//			HMacGenerator duffHMac = new HMacGenerator(new File("dont_need_this").getAbsolutePath(), true, permMsg);
//			encryptedString2 = (String)GeneralUtils.readObjectFromFile("hMacString2");
//			assertFalse(newhMac.equals(duffHMac));
//			//HMac newhMac2 = new HMac(new File("remote_MacKey").getAbsolutePath(), false, correctPermMsg);
//			assertFalse(newhMac2.equals(duffHMac));
//			//assertTrue(newhMac2.equals(newhMac));
//			String g = newhMac2.gethMacString();
//			
//			assertTrue(newhMac2.gethMacString().equals(encryptedString));
//		} catch (Exception e) {
//			assertTrue(false);
//			e.printStackTrace();
//		} 
//	}
//
//	/**
//	 * Test permanent key integrity. We create a new HMAC and ensure it is the same as
//	 * the old one. Then others and ensure they are not!
//	 */
//	@Test
//	public void checkIntegrityOfPermanentKey() {
//		System.out.println("checkIntegrityOfPermanentKey");
//		
//		try {
//			/*
//			 * TODO
//			 * NOTE
//			 * Another test would be to have the HMAC generated on another machine.
//			 * That key needs to be imported here and tested.
//			 */
//			HMacGenerator newhMac = new HMacGenerator(permanentKey.getAbsolutePath(), false, permMsg);
//			assertTrue(permanentHMac.equals(newhMac));
//			newhMac = new HMacGenerator(permanentKey.getAbsolutePath(), true, permMsg);
//			assertTrue(permanentHMac.equals(newhMac));
//			newhMac = new HMacGenerator(permanentKey.getAbsolutePath(), true, permMsg + "This  text changes things");
//			assertFalse(permanentHMac.equals(newhMac));
//			newhMac = new HMacGenerator(permanentKey2.getAbsolutePath(), true, permMsg);
//			assertFalse(permanentHMac.equals(newhMac));
//			assertFalse(permanentHMac.equals(key[1]));
//			System.out.println("\tsuccess");
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
//		assertFalse(fArray[0].getAbsoluteFile().exists());
//		assertTrue(fArray[1].getAbsoluteFile().exists());
//		if (Platform.isLinux()) {
//			assertTrue(KeyServicesTest.checkFilePermissions(fArray[1]));
//		}
//		System.out.println("\tsuccess");
//	}
//}
