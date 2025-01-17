/*
 * File    : SECertificateHandlerImpl.java
 * Created : 29-Dec-2003
 * By      : parg
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.gudy.azureus2.core3.security.impl;

/**
 * @author parg
 *
 */

import java.util.*;
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import javax.net.ssl.*;

import java.security.*;
import java.security.cert.*;

import org.bouncycastle.jce.*;
import org.bouncycastle.asn1.x509.X509Name;

import org.gudy.azureus2.core3.logging.LGLogger;
import org.gudy.azureus2.core3.security.*;
import org.gudy.azureus2.core3.util.*;

public class 
SESecurityManagerImpl 
{
	protected static SESecurityManagerImpl	singleton = new SESecurityManagerImpl();
	
	protected String	keystore;
	protected String	truststore;
	
	protected List	certificate_listeners 	= new ArrayList();
	protected List	password_listeners 		= new ArrayList();
	
	protected Map	password_handlers		= new HashMap();
	
	public static SESecurityManagerImpl
	getSingleton()
	{
		return( singleton );
	}
	
	public void
	initialise()
	{
		// 	keytool -genkey -keystore %home%\.keystore -keypass changeit -storepass changeit -keyalg rsa -alias azureus

		// 	keytool -export -keystore %home%\.keystore -keypass changeit -storepass changeit -alias azureus -file azureus.cer

		// 	keytool -import -keystore %home%\.certs -alias azureus -file azureus.cer			
	
		// debug SSL with -Djavax.net.debug=ssl
	
		keystore 	= FileUtil.getUserFile(SESecurityManager.SSL_KEYS).getAbsolutePath();
		truststore 	= FileUtil.getUserFile(SESecurityManager.SSL_CERTS).getAbsolutePath();
		
		System.setProperty( "javax.net.ssl.trustStore", truststore );
	
		System.setProperty( "javax.net.ssl.trustStorePassword", SESecurityManager.SSL_PASSWORD );
		
		
		installAuthenticator();
		
		try{
			Security.addProvider((java.security.Provider)
				Class.forName("com.sun.net.ssl.internal.ssl.Provider").newInstance());
			
		}catch( Throwable e ){
			
			e.printStackTrace();
		}
		
		try{
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			
		}catch( Throwable e ){
			
			e.printStackTrace();
		}
	}
	
	public void
	installAuthenticator()
	{
		Authenticator.setDefault(
				new Authenticator()
				{
					protected synchronized PasswordAuthentication
					getPasswordAuthentication()
					{					
						PasswordAuthentication	res =  
							getAuthentication( 
									getRequestingPrompt(),
									getRequestingProtocol(),
									getRequestingHost(),
									getRequestingPort());
						
						/*
						System.out.println( "Authenticator:getPasswordAuth: res = " + res );
						
						if ( res != null ){
							
							System.out.println( "    user = '" + res.getUserName() + "', pw = '" + new String(res.getPassword()) + "'" );
						}
						*/
						
						return( res );
					}
				});
	}
	
	public synchronized PasswordAuthentication
	getAuthentication(
		String		realm,
		String		protocol,
		String		host,
		int			port )
	{
		try{
			URL	tracker_url = new URL( protocol + "://" + host + ":" + port + "/" );
		
			return( getPasswordAuthentication( realm, tracker_url ));
			
		}catch( MalformedURLException e ){
			
			e.printStackTrace();
			
			return( null );
		}
	}
	
	protected boolean
	checkKeyStoreHasEntry()
	{
		File	f  = new File(keystore);
		
		if ( !f.exists()){
			
			LGLogger.logAlertUsingResource( 
					LGLogger.AT_ERROR,
					"Security.keystore.empty",
					new String[]{ keystore });
			
			return( false );
		}
		
		try{
			KeyStore key_store = loadKeyStore();
			
			Enumeration enum = key_store.aliases();
			
			if ( !enum.hasMoreElements()){
				
				LGLogger.logAlertUsingResource( 
						LGLogger.AT_ERROR,
						"Security.keystore.empty",
						new String[]{ keystore });
				
				return( false );			
			}
			
		}catch( Throwable e ){
		
			LGLogger.logAlertUsingResource( 
					LGLogger.AT_ERROR,
					"Security.keystore.corrupt",
					new String[]{ keystore });
			
			return( false );			
		}
		
		return( true );
	}
	
	protected KeyStore
	loadKeyStore()
	
		throws Exception
	{
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		
		return( loadKeyStore( keyManagerFactory ));
	}
	
	protected KeyStore
	loadKeyStore(
		KeyManagerFactory	keyManagerFactory )
		
		throws Exception
	{
		KeyStore key_store = KeyStore.getInstance("JKS");
		
		if ( !new File(keystore).exists()){
			
			key_store.load(null,null);
			
		}else{
			
			InputStream kis = null;
			
			try{
				kis = new FileInputStream(keystore);
			
				key_store.load(kis, SESecurityManager.SSL_PASSWORD.toCharArray());
				
			}finally{
				
				if ( kis != null ){
					
					kis.close();
				}
			}
		}
		
		keyManagerFactory.init(key_store, SESecurityManager.SSL_PASSWORD.toCharArray());
		
		return( key_store );
	}
	
	public SSLServerSocketFactory
	getSSLServerSocketFactory()
	
		throws Exception
	{
		if ( !checkKeyStoreHasEntry()){
			
			return( null );
		}
		
		SSLContext context = SSLContext.getInstance( "SSL" );
		
		// Create the key manager factory used to extract the server key
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		
		KeyStore key_store = loadKeyStore(keyManagerFactory);
		
		// Initialize the context with the key managers
		
		context.init(  	
				keyManagerFactory.getKeyManagers(), 
				null,
				new java.security.SecureRandom());
		
		SSLServerSocketFactory factory = context.getServerSocketFactory();
		
		return( factory );
	}
	
	public SEKeyDetails
	getKeyDetails(
		String		alias )
	
		throws Exception
	{
		// Create the key manager factory used to extract the server key
				
		KeyStore key_store = loadKeyStore();
		
		final Key key = key_store.getKey( alias, SESecurityManager.SSL_PASSWORD.toCharArray());
		
		if ( key == null ){
			
			return( null );
		}
		
		java.security.cert.Certificate[]	chain = key_store.getCertificateChain( alias );

		final X509Certificate[]	res = new X509Certificate[chain.length];
		
		for (int i=0;i<chain.length;i++){
			
			if ( !( chain[i] instanceof X509Certificate )){
				
				throw( new Exception( "Certificate chain must be comprised of X509Certificate entries"));
			}
			
			res[i] = (X509Certificate)chain[i];
		}
		
		return( new SEKeyDetails()
				{
					public Key
					getKey()
					{
						return( key );
					}
					
					public X509Certificate[]
					getCertificateChain()
					{
						return( res );
					}
				});
	}
	
	public void
	createSelfSignedCertificate(
		String		alias,
		String		cert_dn,
		int			strength )
	
		throws Exception
	{
		KeyPairGenerator	kg = KeyPairGenerator.getInstance( "RSA" );
		
		kg.initialize(strength, new SecureRandom());

		KeyPair pair = kg.generateKeyPair();
					
		X509V3CertificateGenerator certificateGenerator = 
			new X509V3CertificateGenerator();
		
		certificateGenerator.setSignatureAlgorithm( "MD5WithRSAEncryption" );
		
		certificateGenerator.setSerialNumber( new BigInteger( ""+System.currentTimeMillis()));
					
		X509Name	issuer_dn = new X509Name(true,cert_dn);
		
		certificateGenerator.setIssuerDN(issuer_dn);
		
		X509Name	subject_dn = new X509Name(true,cert_dn);
		
		certificateGenerator.setSubjectDN(subject_dn);
		
		Calendar	not_after = Calendar.getInstance();
		
		not_after.add(Calendar.YEAR, 1);
		
		certificateGenerator.setNotAfter( not_after.getTime());
		
		certificateGenerator.setNotBefore(Calendar.getInstance().getTime());
		
		certificateGenerator.setPublicKey( pair.getPublic());
		
		X509Certificate certificate = certificateGenerator.generateX509Certificate(pair.getPrivate());
		
		java.security.cert.Certificate[] certChain = {(java.security.cert.Certificate) certificate };

		addCertToKeyStore( alias, pair.getPrivate(), certChain );
	}
	
	public synchronized boolean
	installServerCertificates(
		URL		https_url )
	{
		String	host	= https_url.getHost();
		int		port	= https_url.getPort();
		
		if ( port == -1 ){
			port = 443;
		}
		
		SSLSocket	socket = null;
		
		try{
	
				// to get the server certs we have to use an "all trusting" trust manager
			
			TrustManager[] trustAllCerts = new TrustManager[]{
						new X509TrustManager() {
							public java.security.cert.X509Certificate[] getAcceptedIssuers() {
								return null;
							}
							public void checkClientTrusted(
									java.security.cert.X509Certificate[] certs, String authType) {
							}
							public void checkServerTrusted(
									java.security.cert.X509Certificate[] certs, String authType) {
							}
						}
					};
			
			SSLContext sc = SSLContext.getInstance("SSL");
			
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			
			SSLSocketFactory factory = sc.getSocketFactory();
					
			socket = (SSLSocket)factory.createSocket(host, port);
		
			socket.startHandshake();
			
			java.security.cert.Certificate[] serverCerts = socket.getSession().getPeerCertificates();
			
			if ( serverCerts.length == 0 ){
								
				return( false );
			}
			
			java.security.cert.Certificate	cert = serverCerts[0];
						
			java.security.cert.X509Certificate x509_cert;
			
			if ( cert instanceof java.security.cert.X509Certificate ){
				
				x509_cert = (java.security.cert.X509Certificate)cert;
				
			}else{
				
				java.security.cert.CertificateFactory cf = java.security.cert.CertificateFactory.getInstance("X.509");
				
				x509_cert = (java.security.cert.X509Certificate)cf.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
			}
				
			String	resource = https_url.toString();
			
			int	param_pos = resource.indexOf("?");
			
			if ( param_pos != -1 ){
				
				resource = resource.substring(0,param_pos);
			}
			
			for (int i=0;i<certificate_listeners.size();i++){
				
				if (((SECertificateListener)certificate_listeners.get(i)).trustCertificate( resource, x509_cert )){
					
					String	alias = host.concat(":").concat(String.valueOf(port));
			
					addCertToTrustStore( alias, cert );
			
					return( true );
				}
			}
			
			return( false );
			
		}catch( Throwable e ){
			
			e.printStackTrace();
			
			return( false );
			
		}finally{
			
			if ( socket != null ){
				
				try{
					socket.close();
					
				}catch( Throwable e ){
					
					e.printStackTrace();
				}
			}
		}
	}
	
	protected synchronized void
	addCertToKeyStore(
		String								alias,
		Key									public_key,
		java.security.cert.Certificate[] 	certChain )
	
		throws Exception
	{
		KeyStore key_store = loadKeyStore();
		
		if( key_store.containsAlias( alias )){
			
			key_store.deleteEntry( alias );
		}
		
		key_store.setKeyEntry( alias, public_key, SESecurityManager.SSL_PASSWORD.toCharArray(), certChain );
		
		FileOutputStream	out = null;
		
		try{
			out = new FileOutputStream(keystore);
		
			key_store.store(out, SESecurityManager.SSL_PASSWORD.toCharArray());
			
		}catch( Throwable e ){
			
			e.printStackTrace();
			
		}finally{
			
			if ( out != null ){
				
				out.close();
			}
		}
	}
	
	protected synchronized void
	addCertToTrustStore(
		String							alias,
		java.security.cert.Certificate	cert )
	
		throws Exception
	{
		KeyStore keystore = KeyStore.getInstance("JKS");
		
		if ( !new File(truststore).exists()){
	
			keystore.load(null,null);
			
		}else{
		
			FileInputStream		in 	= null;

			try{
				in = new FileInputStream(truststore);
		
				keystore.load(in, SESecurityManager.SSL_PASSWORD.toCharArray());
				
			}finally{
				
				if ( in != null ){
					
					in.close();
				}
			}
		}
		
		if ( cert != null ){
			
			if ( keystore.containsAlias( alias )){
			
				keystore.deleteEntry( alias );
			}
						
			keystore.setCertificateEntry(alias, cert);

			FileOutputStream	out = null;
			
			try{
				out = new FileOutputStream(truststore);
		
				keystore.store(out, SESecurityManager.SSL_PASSWORD.toCharArray());
		
			}finally{
				
				if ( out != null ){
					
					out.close();
				}						
			}
		}
		
			// pick up the changed trust store
		
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		
		tmf.init(keystore);
		
		SSLContext ctx = SSLContext.getInstance("SSL");
		
		ctx.init(null, tmf.getTrustManagers(), null);
					
		HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
	}
	
	public PasswordAuthentication
	getPasswordAuthentication(
		String		realm,
		URL			tracker )
	{
		Object[]	handler = (Object[])password_handlers.get(tracker.toString());
		
		if ( handler != null ){
			
			return(((SEPasswordListener)handler[0]).getAuthentication( realm, (URL)handler[1] ));
		}
		
		for (int i=0;i<password_listeners.size();i++){
			
			PasswordAuthentication res = ((SEPasswordListener)password_listeners.get(i)).getAuthentication( realm, tracker );
			
			if ( res != null ){
				
				return( res );
			}
		}
		
		return( null );
	}
	
	public void
	setPasswordAuthenticationOutcome(
		String		realm,
		URL			tracker,
		boolean		success )
	{
		for (int i=0;i<password_listeners.size();i++){
			
			((SEPasswordListener)password_listeners.get(i)).setAuthenticationOutcome( realm, tracker, success );
		}
	}
		
	public synchronized void
	addPasswordListener(
		SEPasswordListener	l )
	{
		password_listeners.add(l);
	}	
	
	public synchronized void
	removePasswordListener(
		SEPasswordListener	l )
	{
		password_listeners.remove(l);
	}
	
	public void
	addPasswordHandler(
		URL						url,
		SEPasswordListener		l )
	{
		String url_s	= url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/";
		
		password_handlers.put( url_s, new Object[]{ l, url });
	}
	
	public void
	removePasswordHandler(
		URL						url,
		SEPasswordListener		l )
	{
		String url_s	= url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/";
		
		password_handlers.remove( url_s );
	}
	
	public synchronized void
	addCertificateListener(
		SECertificateListener	l )
	{
		certificate_listeners.add(l);
	}	
	
	public synchronized void
	removeCertificateListener(
		SECertificateListener	l )
	{
		certificate_listeners.remove(l);
	}
	
	public static void
	main(
		String[]	args )
	{
		SESecurityManagerImpl man = SESecurityManagerImpl.getSingleton();
		
		man.initialise();
		
		try{
			man.createSelfSignedCertificate( "SomeAlias", "CN=fred,OU=wap,O=wip,L=here,ST=there,C=GB", 1000 );
			
		}catch( Throwable e ){
			
			e.printStackTrace();
		}
	}
}
