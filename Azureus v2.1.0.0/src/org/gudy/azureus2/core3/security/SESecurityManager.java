/*
 * File    : SESecurityManager.java
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

package org.gudy.azureus2.core3.security;

/**
 * @author parg
 *
 */

import java.net.URL;
import java.net.PasswordAuthentication;

import javax.net.ssl.*;

import org.gudy.azureus2.core3.security.impl.*;

public class 
SESecurityManager
{
	// SSL client defaults
	
	public static final String SSL_CERTS 		= ".certs";
	public static final String SSL_KEYS			= ".keystore";
	public static final String SSL_PASSWORD 	= "changeit";
	
	
	public static void
	initialise()
	{
		SESecurityManagerImpl.initialise();
	}
	
	public static SSLServerSocketFactory
	getSSLServerSocketFactory()
	
		throws Exception
	{
		return( SESecurityManagerImpl.getSSLServerSocketFactory());
	}
	
	public static boolean
	installServerCertificates(
		URL		https_url )
	{
		return( SESecurityManagerImpl.installServerCertificates(https_url));
	}
	
	public static PasswordAuthentication
	getPasswordAuthentication(
		String		realm,
		URL			tracker )
	{
		return( SESecurityManagerImpl.getPasswordAuthentication(realm, tracker));	
	}
	
	public static void
	setPasswordAuthenticationOutcome(
		String		realm,
		URL			tracker,
		boolean		success )
	{
		SESecurityManagerImpl.setPasswordAuthenticationOutcome(realm, tracker, success);	
	}
		
	
	public static synchronized void
	addPasswordListener(
		SEPasswordListener	l )
	{
		SESecurityManagerImpl.addPasswordListener(l);	
	}	
	
	public static synchronized void
	removePasswordListener(
		SEPasswordListener	l )
	{
		SESecurityManagerImpl.removePasswordListener(l);	
	}
	
	public static void
	addCertificateListener(
			SECertificateListener	l )
	{
		SESecurityManagerImpl.addCertificateListener(l);
	}	
	
	public static void
	removeCertificateListener(
		SECertificateListener	l )
	{
		SESecurityManagerImpl.removeCertificateListener(l);
	}
}