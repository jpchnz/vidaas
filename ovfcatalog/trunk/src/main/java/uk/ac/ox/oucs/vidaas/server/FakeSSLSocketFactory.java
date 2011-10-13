/*
 * *******************************************************
 * Copyright VMware, Inc. 2009-2011.  All Rights Reserved.
 * *******************************************************
 */
package uk.ac.ox.oucs.vidaas.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

/**
 * Helper class to accept self-signed certificate.
 */
public class FakeSSLSocketFactory implements SecureProtocolSocketFactory {

	private SSLContext sslContext;

	public FakeSSLSocketFactory() throws NoSuchAlgorithmException,
			KeyManagementException {
		sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, new TrustManager[] { new X509TrustManager() {

			public void checkClientTrusted(X509Certificate[] ax509certificate,
					String s) throws CertificateException {
				// Allow.
			}

			public void checkServerTrusted(X509Certificate[] ax509certificate,
					String s) throws CertificateException {
				// Allow.
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} }, null);
	}

	public Socket createSocket(String s, int i) throws IOException,
			UnknownHostException {
		return sslContext.getSocketFactory().createSocket(s, i);
	}

	public Socket createSocket(String s, int i, InetAddress inetaddress, int j)
			throws IOException, UnknownHostException {
		return sslContext.getSocketFactory().createSocket(s, i, inetaddress, j);
	}

	public Socket createSocket(String s, int i, InetAddress inetaddress, int j,
			HttpConnectionParams httpconnectionparams) throws IOException,
			UnknownHostException, ConnectTimeoutException {
		return sslContext.getSocketFactory().createSocket(s, i, inetaddress, j);
	}

	public Socket createSocket(Socket socket, String s, int i, boolean flag)
			throws IOException, UnknownHostException {
		return sslContext.getSocketFactory().createSocket(socket, s, i, flag);
	}

}
