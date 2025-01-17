/*
 * File    : COConfigurationManager.java
 * Created : 5 Oct. 2003
 * By      : Parg 
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
 
package org.gudy.azureus2.core3.config;

import java.io.IOException;

import org.gudy.azureus2.core3.config.impl.*;

public class 
COConfigurationManager 
{
	public static void
	checkConfiguration()
	{
		ConfigurationChecker.checkConfiguration();
	}
	
	public static String
	getStringParameter(
		String		_name )
	{
		return( ConfigurationManager.getInstance().getStringParameter( _name ));
	}
	
	public static String
	getStringParameter(
		String		_name,
		String		_default )
	{
		return( ConfigurationManager.getInstance().getStringParameter( _name, _default ));
	}
	
	public static void 
	setParameter(String parameter, String value) 
	{
		ConfigurationManager.getInstance().setParameter( parameter, value );
	}

	public static boolean
	getBooleanParameter(
		String		_name )
	{
		return( ConfigurationManager.getInstance().getBooleanParameter( _name ));
	}
	
	public static boolean
	getBooleanParameter(
		String		_name,
		boolean		_default )
	{
		return( ConfigurationManager.getInstance().getBooleanParameter( _name, _default ));
	}
	
	public static void 
	setParameter(String parameter, boolean value) 
	{
		ConfigurationManager.getInstance().setParameter( parameter, value );
	}
	
	public static int
	getIntParameter(
		String		_name )
	{
		return( ConfigurationManager.getInstance().getIntParameter( _name ));
	}
	
	public static int
	getIntParameter(
		String		_name,
		int		_default )
	{
		return( ConfigurationManager.getInstance().getIntParameter( _name, _default ));
	}
	
	public static void 
	setParameter(String parameter, int value) 
	{
		ConfigurationManager.getInstance().setParameter( parameter, value );
	}
	
	public static byte[]
	getByteParameter(
		String		_name,
		byte[]		_default )
	{
		return( ConfigurationManager.getInstance().getByteParameter( _name, _default ));
	}
	
	public static void 
	setParameter(String parameter, byte[] value) 
	{
		ConfigurationManager.getInstance().setParameter( parameter, value );
	}
	
	public static String
	getDirectoryParameter(
		String		_name )		
		throws IOException
	{
		return( ConfigurationManager.getInstance().getDirectoryParameter( _name ));
	}

	public static void
	save()
	{
		ConfigurationManager.getInstance().save();
	}
}
