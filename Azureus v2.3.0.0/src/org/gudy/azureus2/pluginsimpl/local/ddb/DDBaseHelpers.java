/*
 * Created on 21-Feb-2005
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

package org.gudy.azureus2.pluginsimpl.local.ddb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.gudy.azureus2.core3.util.HashWrapper;
import org.gudy.azureus2.core3.util.SHA1Hasher;
import org.gudy.azureus2.plugins.ddb.DistributedDatabaseException;

/**
 * @author parg
 *
 */

public class 
DDBaseHelpers 
{
	protected static byte[]
	encode(
		Object	obj )
	
		throws DistributedDatabaseException
	{
		byte[]	res;
		
		if ( obj == null ){
			
			throw( new DistributedDatabaseException( "null not supported" ));
			
		}else if ( obj instanceof byte[] ){
			
			res = (byte[])obj;
			
		}else if ( obj instanceof String ){
			
			res = ((String)obj).getBytes();
			
		}else if (	obj instanceof Byte ||
					obj instanceof Short ||
					obj instanceof Integer ||
					obj instanceof Long ||
					obj instanceof Float ||
					obj instanceof Double ||
					obj instanceof Boolean ){
			
			throw( new DistributedDatabaseException( "not supported yet!" ));
					
		}else{
			
			try{
				ByteArrayOutputStream	baos = new ByteArrayOutputStream();
				
				ObjectOutputStream	oos = new ObjectOutputStream( baos );
				
				oos.writeObject( obj );
				
				oos.close();
				
				res = baos.toByteArray();
				
			}catch( Throwable e ){
				
				throw( new DistributedDatabaseException( "encoding fails", e ));
			}
		}
		
		return( res );
	}
	
	protected static Object
	decode(
		Class	target,
		byte[]	data )
	
		throws DistributedDatabaseException
	{
		if ( target == byte[].class ){
			
			return( data );
			
		}else if ( target == String.class ){
			
			return( new String( data ));
			
		}else{
			
			try{
				ObjectInputStream	iis = new ObjectInputStream( new ByteArrayInputStream( data ));
				
				Object	res = iis.readObject();
				
				if ( target.isInstance( res )){
					
					return( res );
					
				}else{
					
					throw( new DistributedDatabaseException( "decoding fails, incompatible type" ));
				}

			}catch( DistributedDatabaseException e ){
				
				throw( e );
				
			}catch( Throwable e ){
				
				throw( new DistributedDatabaseException( "decoding fails", e ));
			}		
		}
	}
	
	protected static HashWrapper
	getKey(
		Class	c )
	
		throws DistributedDatabaseException
	{
		String	name = c.getName();
		
		if ( name == null ){
			
			throw( new DistributedDatabaseException( "name doesn't exist for '" + c.getName() + "'" ));
		}
			
		return( new HashWrapper(new SHA1Hasher().calculateHash(name.getBytes())));
	}
}
