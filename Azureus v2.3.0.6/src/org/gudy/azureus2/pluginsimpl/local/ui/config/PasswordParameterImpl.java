/*
 * Created on 10-Jun-2004
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

package org.gudy.azureus2.pluginsimpl.local.ui.config;

/**
 * @author parg
 *
 */
import java.security.MessageDigest;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.SHA1Hasher;
import org.gudy.azureus2.plugins.PluginConfig;
import org.gudy.azureus2.plugins.ui.config.PasswordParameter;

public class 
PasswordParameterImpl 
	extends 	ParameterImpl 
	implements 	PasswordParameter
{
	protected  	byte[] 	defaultValue;
	protected 	int		encoding_type;
	
	public 
	PasswordParameterImpl(
		PluginConfig 	config,
		String 			key, 
		String 			label,
		int				_encoding_type,
		byte[] 			_defaultValue)
	{ 
		super(config,key, label);
		
		if ( _defaultValue == null ){
			
			defaultValue = new byte[0];
			
		}else{
			
			defaultValue = _defaultValue;

			if ( _encoding_type == ET_SHA1 ){
				
		        SHA1Hasher hasher = new SHA1Hasher();
		        
		        defaultValue = hasher.calculateHash(defaultValue);
		        
			}else if ( _encoding_type == ET_MD5 ){
				
				try{
					defaultValue = MessageDigest.getInstance( "md5").digest( defaultValue );
					
				}catch( Throwable e ){
					
					Debug.printStackTrace(e);
				}
			}
		}
		
		COConfigurationManager.setByteDefault( getKey(), defaultValue );

		encoding_type	= _encoding_type;
	}
	
	public byte[] getDefaultValue()
	{
		return defaultValue;
	}
	
	public int
	getEncodingType()
	{
		return( encoding_type );
	}
	
	public byte[]
	getValue()
	{
		return( config.getByteParameter( getKey(), getDefaultValue()));
	}
}
