package uk.ac.ox.oucs.iam.security.utilities;

/*
 * Copyright 2006 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * Originally from:
 * http://blogs.sun.com/andreas/resource/InstallCert.java
 * Use:
 * java InstallCert hostname
 * Example:
 *% java InstallCert ecc.fedora.redhat.com
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Class used to add the server's certificate to the KeyStore with your trusted
 * certificates.
 */
public class InstallCert {

	public static void main(String[] args) throws Exception {
		String host;
		int port;
		char[] passphrase;
		if ((args.length == 1) || (args.length == 2)) {
			String[] c = args[0].split(":");
			host = c[0];
			port = (c.length == 1) ? 443 : Integer.parseInt(c[1]);
			String p = (args.length == 1) ? "changeit" : args[1];
			passphrase = p.toCharArray();
		} else {
			System.out.println("Usage: java InstallCert [:port] [passphrase]");
			return;
		}

		InstallCert installCert = new InstallCert(passphrase, host, port);
		installCert.getRemoteCertificate(true);
	}
	
	
	/**
	 * Convert file to a keystore
	 * @param file the keystore file requested
	 * @return a KeyStore object
	 * @throws Exception
	 */
	public static KeyStore getKeyStore(File file) throws Exception {
		//File file = new File("/opt/localTestCerts");
		System.out.println("Loading KeyStore " + file + "...");

		InputStream in = new FileInputStream(file);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, passphrase);
		in.close();
		
		return ks;
	}
	
	
	

	private static char[] passphrase;
	private String host;
	private int port;

	public InstallCert(char[] passphrase, String host, int port) {
		InstallCert.passphrase = passphrase;
		this.host = host;
		this.port = port;
	}

	public InstallCert(char[] passphrase, String host) {
		InstallCert.passphrase = passphrase;
		this.host = host;
		this.port = 443;
	}

	public InstallCert(char[] passphrase) {
		InstallCert.passphrase = passphrase;
		this.host = "localhost";
		this.port = 443;
	}

	
	public static java.security.cert.Certificate getCertFromKeystore(String certName) throws Exception {
		// Load the keystore in the user's home directory
	    FileInputStream is = new FileInputStream("/opt/localTestCerts");

	    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	    keystore.load(is, "karate".toCharArray());

	    // Get certificate
	    return(keystore.getCertificate(certName));
	}
	
	
	
	
	
	
	// Collect a certificate from a remote site and, if it doesn't exist locally, add it to the local keystore
	public java.security.cert.Certificate getRemoteCertificate(boolean writeIfNotThere) throws Exception {
		KeyStore ks = getKeyStore(new File("/opt/localTestCerts"));

		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf
				.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory factory = context.getSocketFactory();

		System.out
				.println("Opening connection to " + host + ":" + port + "...");
		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
		socket.setSoTimeout(10000);
		boolean alreadyHaveCertificate = false;
		try {
			System.out.println("Starting SSL handshake...");
			socket.startHandshake();
			socket.close();
			System.out.println();
			System.out.println("No errors, certificate is already trusted");
			alreadyHaveCertificate = true;
		} catch (SSLException e) {
			System.out.println();
			e.printStackTrace(System.out);
		}

		X509Certificate[] chain = tm.chain;
		if (chain == null) {
			System.out.println("Could not obtain server certificate chain");
			return null;
		}

		System.out.println();
		System.out.println("Server sent " + chain.length + " certificate(s):");
		System.out.println();
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			System.out.println(" " + (i + 1) + " Subject "
					+ cert.getSubjectDN());
			System.out.println("   Issuer  " + cert.getIssuerDN());
			sha1.update(cert.getEncoded());
			System.out.println("   sha1    " + toHexString(sha1.digest()));
			md5.update(cert.getEncoded());
			System.out.println("   md5     " + toHexString(md5.digest()));
			System.out.println();
		}

		int k = 0;
//		System.out
//				.println("Enter certificate to add to trusted keystore or 'q' to quit: [1]");
//		BufferedReader reader = new BufferedReader(new InputStreamReader(
//				System.in));
//		String line = reader.readLine().trim();
//		try {
//			k = (line.length() == 0) ? 0 : Integer.parseInt(line) - 1;
//		} catch (NumberFormatException e) {
//			System.out.println("KeyStore not changed");
//			return null;
//		}

		X509Certificate cert = chain[k];
		String alias = host + "-" + (k + 1);
		ks.setCertificateEntry(alias, cert);

		if ( !alreadyHaveCertificate && writeIfNotThere) {
			OutputStream out = new FileOutputStream("/opt/localTestCerts");
			ks.store(out, passphrase);
			out.close();
		}
		

		System.out.println();
		System.out.println(cert);
		System.out.println();
		System.out
				.println("Added certificate to keystore '/opt/localTestCerts' using alias '"
						+ alias + "'");
		
		return cert;
	}
	
	public static void listAliases(File keystore) throws Exception {
		listAliases(getKeyStore(keystore));
	}

	private static void listAliases(KeyStore keystore) throws KeyStoreException {
		Enumeration<String> e = keystore.aliases();
		System.out.println("Aliases ...");
		for (; e.hasMoreElements();) {
			String alias = (String) e.nextElement();

			// Does alias refer to a private key?
			boolean b = keystore.isKeyEntry(alias);

			// Does alias refer to a trusted certificate?
			boolean b2 = keystore.isCertificateEntry(alias);
			if (b2 == b) {
				System.out.println("Alias:" + alias + " does " + (b ? "" : "not ") + "refer to a private key and does" + (b2 ? "" : " not") + " refer to a trusted certificate");
			}
			else {
				if (b) {
					System.out.println("Alias:" + alias + " refers to a private key");
				}
				else {
					System.out.println("Alias:" + alias + " refers to a trusted certificate");
				}
			}
		}
	}

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}

	private static class SavingTrustManager implements X509TrustManager {

		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			throw new UnsupportedOperationException();
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}
	}
}