package uk.ac.ox.oucs.iam.security.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.ox.oucs.iam.interfaces.security.SignatureGenerator;
import uk.ac.ox.oucs.iam.interfaces.security.SignatureVerifier;
import uk.ac.ox.oucs.iam.interfaces.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.interfaces.security.utilities.VidaasSignature;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.NoEncodingException;

public class SignatureGeneratorTest {
	private static String keyFile = "/tmp/key";
	private final static String keyType = "HmacSHA512";
	
	@BeforeClass
	public static void initialise() throws NoSuchAlgorithmException, IOException, KeyNotFoundException, NoEncodingException {
		File privateKey = new File(keyFile+".priv");
		if (!privateKey.exists()) {
			// Create the private key
			new KeyServices(keyFile, true, keyType);
			assertTrue(privateKey.exists());
		}
	}
	
	
	/*
	 * Test data may be signed, encoded and then decoded properly
	 * without a message expiry 
	 */
	@Test
	public void test1() throws IOException, GeneralSecurityException {
		String postData = "user=john&password=fred";
		SignatureGenerator signature;
		for (int counter = 0; counter < 100; counter++) {
			signature = new SignatureGenerator(keyFile);
			signature.setUseMessageExpiry(false);

			VidaasSignature vSig = signature.signMessageAndEncode(postData + counter);

			SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
			byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
			assertTrue(sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getTimestampedMessage(postData + counter)));
		}
	}
	
	
	/*
	 * Test data may be signed, encoded and then decoded properly
	 * without a message expiry 
	 */
	@Test
	public void test2() throws IOException, GeneralSecurityException {
		String postData = "user=john&password=fred";
		SignatureGenerator signature;
		for (int counter = 0; counter < 100; counter++) {
			signature = new SignatureGenerator(keyFile);
			signature.setUseMessageExpiry(false);

			VidaasSignature vSig = signature.signMessageAndEncode(postData + counter);

			SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
			byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
			assertTrue(sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getTimestampedMessage(postData + counter)));
		}
	}
	
	
	/*
	 * Test data may be signed, encoded and then decoded properly
	 * with a message expiry 
	 */
	@Test
	public void test3() throws IOException, GeneralSecurityException {
		String postData = "user=john&password=fred";
		SignatureGenerator signature;
		for (int counter = 0; counter < 100; counter++) {
			signature = new SignatureGenerator(keyFile);
			signature.setUseMessageExpiry(true);
			
			VidaasSignature vSig = signature.signMessageAndEncode(postData + counter);

			SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
			byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
			assertTrue(sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getOriginalMessage()));
			assertTrue(sigVerifier.verifyTimestamp(vSig.getTimestamp()));
		}
	}
	
	/*
	 * Test message expiry 
	 */
	@Test
	public void test4() throws IOException, GeneralSecurityException, InterruptedException {
		String postData = "user=john&password=fred";
		SignatureGenerator signature;
		signature = new SignatureGenerator(keyFile);
		signature.setUseMessageExpiry(true);
		
		VidaasSignature vSig = signature.signMessageAndEncode(postData);
		
		Thread.sleep(1002);

		SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
		sigVerifier.setMaxMessageAgeSeconds(1);
		byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
		assertTrue(sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getOriginalMessage()));
		assertFalse(sigVerifier.verifyTimestamp(vSig.getTimestamp()));
	}
}
