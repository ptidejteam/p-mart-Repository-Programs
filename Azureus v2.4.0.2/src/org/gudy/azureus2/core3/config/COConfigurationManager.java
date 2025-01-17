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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;

import org.gudy.azureus2.core3.config.impl.*;
import org.gudy.azureus2.core3.util.Constants;

public class 
COConfigurationManager 
{
	public static final int CONFIG_DEFAULT_MIN_MAX_UPLOAD_SPEED			= 5;
	public static final int CONFIG_DEFAULT_MAX_DOWNLOAD_SPEED			= 0;
	public static final int	CONFIG_DEFAULT_MAX_CONNECTIONS_PER_TORRENT	= 70;
	public static final int	CONFIG_DEFAULT_MAX_CONNECTIONS_GLOBAL		= 300;
	
	public static final int CONFIG_CACHE_SIZE_MAX_MB;
	
	static{
		long max_mem_bytes 	= Runtime.getRuntime().maxMemory();
	    long mb_1			= 1*1024*1024;
	    long mb_32			= 32*mb_1;
	    int size = (int)(( max_mem_bytes - mb_32 )/mb_1);	    
	    if( size > 1024 )  size = 1024;  //safety check
      if( size < 1 )  size = 1;
	    CONFIG_CACHE_SIZE_MAX_MB = size;
	}
	
	private static boolean	pre_initialised;
	
	public static synchronized void
	preInitialise()
	{	  	
		if ( !pre_initialised ){
			
			pre_initialised	= true;
		
			try{
			  	String	handlers = System.getProperty( "java.protocol.handler.pkgs" );
			  	
			  	if ( handlers == null ){
			  		
			  		handlers = "org.gudy.azureus2.core3.util.protocol";
			  		
			  	}else{
			  		
			  		handlers += "|org.gudy.azureus2.core3.util.protocol";
			  	}
			  	
			  	System.setProperty( "java.protocol.handler.pkgs", handlers );
			  	 	
			  		// DNS cache timeouts
			  	
			  	System.setProperty("sun.net.inetaddr.ttl", "60");
			  	System.setProperty("networkaddress.cache.ttl", "60");
		      
			  		// defaults, overridden later if needed
		      
			  	System.setProperty( "sun.net.client.defaultConnectTimeout", "120000" );	
			  	System.setProperty(	"sun.net.client.defaultReadTimeout", "60000" ); 
			  			
			      //see http://developer.apple.com/releasenotes/Java/Java142RN/ResolvedIssues/chapter_3_section_7.html
			      //fixes the osx kernel panic bug caused by Apple's faulty kqueue implementation (as of 10.3.6)
			  	
			    if( Constants.isOSX ) {
			        System.setProperty( "java.nio.preferSelect", "true" );
			    }
			}catch( Throwable e ){
				
					// can happen in applet
				
				e.printStackTrace();
			}
		}
	}
	
	public static void
	initialise()
	{
		preInitialise();
		
		ConfigurationManager.getInstance();
	}
	
	public static void
	initialiseFromMap(
		Map		data )
	{
		preInitialise();
		
		ConfigurationManager.getInstance(data);
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
	
	public static boolean
	setParameter(String parameter, String value) 
	{
		return ConfigurationManager.getInstance().setParameter( parameter, value );
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
	
	public static boolean  
	setParameter(String parameter, boolean value) 
	{
		return ConfigurationManager.getInstance().setParameter( parameter, value );
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
	
	public static boolean 
	setParameter(String parameter, int value) 
	{
		return ConfigurationManager.getInstance().setParameter( parameter, value );
	}
	
	public static byte[]
	getByteParameter(
		String		_name,
		byte[]		_default )
	{
		return( ConfigurationManager.getInstance().getByteParameter( _name, _default ));
	}
	
	public static boolean
	setParameter(String parameter, byte[] value) 
	{
		return ConfigurationManager.getInstance().setParameter( parameter, value );
	}
	
	public static String
	getDirectoryParameter(
		String		_name )		
		throws IOException
	{
		return( ConfigurationManager.getInstance().getDirectoryParameter( _name ));
	}
	
	
	
	/*
	public static boolean
	setParameter(String parameter, Color value) 
	{
		return ConfigurationManager.getInstance().setParameter( parameter, value );
	}

	public static boolean
	setParameter(String parameter, RGB value) 
	{
		return ConfigurationManager.getInstance().setParameter( parameter, value );
	}
	*/
	
	public static boolean
	setRGBParameter(String parameter, int red, int green, int blue) 
	{
		return ConfigurationManager.getInstance().setRGBParameter( parameter, red, green, blue);
	}


	public static float
	getFloatParameter(
		String		_name)
	{
		return( ConfigurationManager.getInstance().getFloatParameter( _name ));
	}
	
	public static boolean 
	setParameter(String parameter, float value)
	{
		return ConfigurationManager.getInstance().setParameter( parameter, value );
	}
	
	public static boolean
	setParameter(String parameter,StringList value) {
		return ConfigurationManager.getInstance().setParameter( parameter, value );
	}
	
	public static StringList
	getStringListParameter(String parameter)
	{
		return( ConfigurationManager.getInstance().getStringListParameter( parameter ));
	}

	public static boolean
	setParameter(String parameter,List value) {
		return ConfigurationManager.getInstance().setParameter( parameter, value );
	}
	
	public static List
	getListParameter(String parameter, List def)
	{
		return( ConfigurationManager.getInstance().getListParameter( parameter, def ));
	}
	
	public static boolean
	setParameter(String parameter,Map value) {
		return ConfigurationManager.getInstance().setParameter( parameter, value );
	}
	
	public static Map
	getMapParameter(String parameter, Map def)
	{
		return( ConfigurationManager.getInstance().getMapParameter( parameter, def ));
	}
	
	public static void
	save()
	{
		ConfigurationManager.getInstance().save();
	}
	
	public static void
	addListener(
		COConfigurationListener		listener )
	{
		ConfigurationManager.getInstance().addListener( listener );
	}

	public static void
	addParameterListener(String parameter, ParameterListener listener)
	{
	  ConfigurationManager.getInstance().addParameterListener(parameter, listener);
	}

	public static void
	addAndFireParameterListener(String parameter, ParameterListener listener)
	{
	  ConfigurationManager.getInstance().addParameterListener(parameter, listener);
	  
	  listener.parameterChanged( parameter );
	}
	
	public static void
	addAndFireParameterListeners(String[] parameters, ParameterListener listener)
	{
		for (int i=0;i<parameters.length;i++){
			ConfigurationManager.getInstance().addParameterListener(parameters[i], listener);
		}
	  
		listener.parameterChanged( null );
	}
	public static void
	removeParameterListener(String parameter, ParameterListener listener)
	{
		ConfigurationManager.getInstance().removeParameterListener(parameter, listener);
	}

	public static void
	removeListener(
		COConfigurationListener		listener )
	{
		ConfigurationManager.getInstance().removeListener( listener );
	}
  
  public static Set
  getAllowedParameters()
  {
  	return ConfigurationDefaults.getInstance().getAllowedParameters();
  }
  
  	/**
  	 * checks if a default is defined for the named parameter
  	 * @param parameter
  	 * @return
  	 */
  
  public static boolean
  doesParameterDefaultExist(
	 String     parameter)
  {
       return ConfigurationDefaults.getInstance().doesParameterDefaultExist(parameter);
  }
  
  	/**
  	 * checks if the user has explicitly set a value for the named parameter
  	 * @param parameter
  	 * @return
  	 */
  
  public static boolean
  doesParameterNonDefaultExist(
	 String     parameter)
  {
       return ConfigurationManager.getInstance().doesParameterNonDefaultExist(parameter);
  }
  public static void
  registerExternalDefaults(
  	Map							addmap)
  {
  	ConfigurationDefaults.getInstance().registerExternalDefaults(addmap);
  }
  
  public static void
  setBooleanDefault(
  	String	parameter,
	boolean	_default )
  {
  	ConfigurationDefaults.getInstance().addParameter( parameter, _default );
  }
  
  public static void
  setIntDefault(
  	String	parameter,
	int	_default )
  {
  	ConfigurationDefaults.getInstance().addParameter( parameter, _default );
  }
  
  public static void
  setStringDefault(
  	String	parameter,
	String	_default )
  {
  	ConfigurationDefaults.getInstance().addParameter( parameter, _default );
  }
  
  public static void
  setByteDefault(
  	String	parameter,
	byte[]	_default )
  {
  	ConfigurationDefaults.getInstance().addParameter( parameter, _default );
  }
  
  public static Object
  getDefault(
	 String parameter )
  {
	return( ConfigurationDefaults.getInstance().getParameter( parameter ));
  }
}
