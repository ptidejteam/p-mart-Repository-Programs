/*
 * Created on 17-Jun-2004
 * Created by Paul Gardner
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package org.gudy.azureus2.pluginsimpl.local.utils.security;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import org.gudy.azureus2.core3.util.AERunnable;
import org.gudy.azureus2.core3.util.SHA1Hasher;

/**
 * @author parg
 *
 */

import org.gudy.azureus2.core3.security.*;
import org.gudy.azureus2.plugins.utils.security.CertificateListener;
import org.gudy.azureus2.plugins.utils.security.PasswordListener;


public class 
SESecurityManagerImpl 
	implements org.gudy.azureus2.plugins.utils.security.SESecurityManager
{
	private Map	password_listeners		= new HashMap();
	private Map	certificate_listeners	= new HashMap();
	
	public byte[]
	calculateSHA1(
		byte[]		data_in )
	{
		if (data_in == null ){
			
			data_in = new byte[0];
		}
		
        SHA1Hasher hasher = new SHA1Hasher();
        
        return( hasher.calculateHash(data_in));	
	}
	
	public void
	runWithAuthenticator(
		Authenticator	authenticator,
		Runnable		target )
	{
		try{
			Authenticator.setDefault( authenticator );
			
			target.run();
			
		}finally{
			
			SESecurityManager.installAuthenticator();
		}
	}
	
	public void
	addPasswordListener(
		final PasswordListener	listener )
	{
		SEPasswordListener	sepl = 
			new SEPasswordListener()
			{
				public PasswordAuthentication
				getAuthentication(
					String		realm,
					URL			tracker )
				{
					return( listener.getAuthentication( realm, tracker ));
				}
				
				public void
				setAuthenticationOutcome(
					String		realm,
					URL			tracker,
					boolean		success )
				{
					listener.setAuthenticationOutcome( realm, tracker, success );
				}
			};
			
		password_listeners.put( listener, sepl );
		
		SESecurityManager.addPasswordListener( sepl );
	}
		
	public void
	removePasswordListener(
		PasswordListener	listener )
	{
		SEPasswordListener	sepl = (SEPasswordListener)password_listeners.get( listener );
		
		if ( sepl != null ){
			
			SESecurityManager.removePasswordListener( sepl );
		}
	}
	
	public void
	addCertificateListener(
		final CertificateListener	listener )
	{
		SECertificateListener	sepl = 
			new SECertificateListener()
			{
			public boolean
			trustCertificate(
				String			resource,
				X509Certificate	cert )
			{
				return( listener.trustCertificate( resource, cert ));
			}
			};
			
		certificate_listeners.put( listener, sepl );
		
		SESecurityManager.addCertificateListener( sepl );
	}
		
	public void
	removeCertificateListener(
			CertificateListener	listener )
	{
		SECertificateListener	sepl = (SECertificateListener)certificate_listeners.get( listener );
		
		if ( sepl != null ){
			
			SESecurityManager.removeCertificateListener( sepl );
		}
	}
}
