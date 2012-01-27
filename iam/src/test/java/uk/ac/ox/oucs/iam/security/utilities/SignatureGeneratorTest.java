package uk.ac.ox.oucs.iam.security.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.ox.oucs.iam.security.keys.KeyServices;

public class SignatureGeneratorTest {
	private static String keyFile = "/tmp/key";
	private final static String keyType = "HmacSHA512";
	
	@BeforeClass
	public static void initialise() {
		File privateKey = new File(keyFile+".priv");
		if (!privateKey.exists()) {
			// Create the private key
			try {
				new KeyServices(keyFile, true, keyType);
				assertTrue(privateKey.exists());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertTrue(false);
			}
		}
	}
	
	
	/*
	 * Test data may be signed, encoded and then decoded properly
	 * without a message expiry 
	 */
	@Test
	public void test1() {
		String postData = "user=john&password=fred";
		SignatureGenerator signature;
		try {
			for (int counter = 0; counter < 100; counter++) {
				signature = new SignatureGenerator(keyFile);
				signature.setUseMessageExpiry(false);

				VidaasSignature vSig = signature.signMessageAndEncode(postData + counter);

				SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
				byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
				assertTrue(sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getTimestampedMessage(postData + counter)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	
	/*
	 * Test data may be signed, encoded and then decoded properly
	 * without a message expiry 
	 */
	@Test
	public void test2() {
		String postData = "user=john&password=fred";
		SignatureGenerator signature;
		try {
			for (int counter = 0; counter < 100; counter++) {
				signature = new SignatureGenerator(keyFile);
				signature.setUseMessageExpiry(false);

				VidaasSignature vSig = signature.signMessageAndEncode(postData + counter);

				SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
				byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
				assertTrue(sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getTimestampedMessage(postData + counter)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	
	/*
	 * Test data may be signed, encoded and then decoded properly
	 * with a message expiry 
	 */
	@Test
	public void test3() {
		String postData = "user=john&password=fred";
		SignatureGenerator signature;
		try {
			for (int counter = 0; counter < 100; counter++) {
				signature = new SignatureGenerator(keyFile);
				signature.setUseMessageExpiry(true);
				
				VidaasSignature vSig = signature.signMessageAndEncode(postData + counter);

				SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
				byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
				assertTrue(sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getOriginalMessage()));
				assertTrue(sigVerifier.verifyTimestamp(vSig.getTimestamp()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/*
	 * Test message expiry 
	 */
	@Test
	public void test4() {
		String postData = "user=john&password=fred";
		SignatureGenerator signature;
		try {
			signature = new SignatureGenerator(keyFile);
			signature.setUseMessageExpiry(true);
			
			VidaasSignature vSig = signature.signMessageAndEncode(postData);
			
			Thread.sleep(1002);

			SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
			sigVerifier.setMaxMessageAgeSeconds(1);
			byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
			assertTrue(sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getOriginalMessage()));
			assertFalse(sigVerifier.verifyTimestamp(vSig.getTimestamp()));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
