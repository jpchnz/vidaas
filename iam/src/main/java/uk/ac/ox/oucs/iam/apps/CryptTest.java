package uk.ac.ox.oucs.iam.apps;

import uk.ac.ox.oucs.iam.security.keys.CryptServices;
import uk.ac.ox.oucs.iam.security.keys.HMacGenerator;

public class CryptTest {
	public static void main( String[] args )
    {
    	// ... doing its thing
    	// ... now needs to send post message
//    	String message = "command to send";
//    	VidaasSecurityInterface vsi = new Communications();
//    	vsi.sendMessage(message);
    	
    	HMacGenerator test;
		try {			
			CryptServices cs = new CryptServices(true);
			String enc = cs.encrypt("If you can read this then this is working!");
	    	System.out.println("Encrypted: " + enc);
	    	System.out.println("Decrypted: " + cs.decrypt(enc));
	    	
	    	// Now generate some test messages
	    	String[] msg = new String[10];
	    	int stringNumber = 0;
			msg[stringNumber++] = "If you can read this then this is working!";
			msg[stringNumber++] = "!\"£$%^&*()_+";
			msg[stringNumber++] = "|\\@'~#:;";
			msg[stringNumber++] = "`¬-={}[]";
			msg[stringNumber++] = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			msg[stringNumber++] = "";
			stringNumber = 0;
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
    }
}
