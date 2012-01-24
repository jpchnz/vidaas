package uk.ac.ox.oucs.iam.security.utilities;

import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

/**
 * <p>
 * This class generates an asymmetric key-pair (public key cryptography). The
 * current time is used as a seed to the secure random algorithm which is used
 * to generate the key bytes. Note that the algorithm used is SHA1PRNG, which is
 * a cryptographically strong, Pseudo-Random Number Generator (PRNG) based on
 * the SHA-1 message digest algorithm.
 * </p>
 * <p>
 * The key length in bits can be specified as a parameter. The main method
 * generates a key pair and writes the pair to disk under the files
 * <code>private-key.bin</code> and <code>public-key.bin</code> to the current
 * working directory.
 * </p>
 * 
 * @author Ants
 */
public class KeyGenerator {
	String publicKey, privateKey;
	
	
	public void writeKeyPairToDisk(int keyLength) {
		if (keyLength == 0) {
			keyLength = 1024;
		}
		
		try {
			KeyPair keyPair = new KeyGenerator("key1").generateKeyPair(keyLength);
//			System.out.println("\nKey pair with key-length of " + keyLength
//					+ " successfully generated.\n");
			// Write the private key.
			FileOutputStream output = new FileOutputStream(privateKey);
			output.write(keyPair.getPrivate().getEncoded());
			output.close();
			// Write the public key.
			output = new FileOutputStream(publicKey);
			output.write(keyPair.getPublic().getEncoded());
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private KeyGenerator() {
	    super();
	  }

	public KeyGenerator(String keyName) {
		super();
		
		publicKey = keyName + ".pub";
		privateKey = keyName + ".priv";
	}

	/**
	 * Generate the keypair using the given key length in bits.
	 * 
	 * @param keyBitSize
	 *            the number of bits for the key length. e.g. 512,1024
	 * @return the key pair
	 * @throws java.security.GeneralSecurityException
	 *             on any errors during the generation
	 */
	public KeyPair generateKeyPair(int keyBitSize)
			throws GeneralSecurityException {
		// Use a digital signature algorithm generator.
		KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
		// Random algorithm is SHA-1 with pseudo-random number generator.
		SecureRandom randomAlg = SecureRandom.getInstance("SHA1PRNG", "SUN");
		randomAlg.setSeed(System.currentTimeMillis());
		generator.initialize(keyBitSize, randomAlg);
		return generator.generateKeyPair();
	}
	
	
	/**
	   * Run the key generator and store the key pair in the files private-key.bin 
	   * and public-key.bin
	   * 
	   * @param args the command line arguments
	   */
	  public static void main(String[] args) {
	    int keyLength = 1024;
	    if ( args.length > 0) {
	      try {
	        keyLength = Integer.parseInt( args[0]);
	      }
	      catch ( NumberFormatException e) {
	        printCmdParameters();
	        System.exit( 1);
	      }
	    }
	    try {
	      KeyPair keyPair = new KeyGenerator().generateKeyPair( keyLength);
	      System.out.println( "\nKey pair with key-length of " + keyLength +
	          " successfully generated.\n");
	      // Write the private key.
	      FileOutputStream output = new FileOutputStream( "private-key.bin");
	      output.write( keyPair.getPrivate().getEncoded());
	      output.close();
	      // Write the public key.
	      output = new FileOutputStream( "public-key.bin");
	      output.write( keyPair.getPublic().getEncoded());
	      output.close();
	    }
	    catch ( Exception e) {
	      e.printStackTrace();
	      System.exit( 1);
	    }
	  }
	   
	  private static void printCmdParameters() {
	    System.out.println( "\n\nUsage: key-gen.bat [key-length]" +
	        "\n\nkey-length defaults to 1024\n\n");
	  }
}