/*
 * File    : RPPluginConfig.java
 * Created : 17-Feb-2004
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

package org.gudy.azureus2.pluginsimpl.remote;

/**
 * @author parg
 *
 */

import java.util.Properties;
import org.gudy.azureus2.plugins.*;

public class 
RPPluginConfig
	extends		RPObject
	implements 	PluginConfig
{
	protected transient PluginConfig		delegate;
	protected transient	Properties			property_cache;
	
		// don't change these field names as they are visible on XML serialisation

	public String[]		cached_property_names;
	public Object[]		cached_property_values;
	
	public static PluginConfig
	create(
		PluginConfig		_delegate )
	{
		RPPluginConfig	res =(RPPluginConfig)_lookupLocal( _delegate );
		
		if ( res == null ){
			
			res = new RPPluginConfig( _delegate );
		}
			
		return( res );
	}
	
	protected
	RPPluginConfig(
		PluginConfig		_delegate )
	{
		super( _delegate );
	}
	
	protected void
	_setDelegate(
		Object		_delegate )
	{
		delegate = (PluginConfig)_delegate;
		
		cached_property_names 	= new String[]{
				CORE_PARAM_INT_MAX_UPLOAD_SPEED_KBYTES_PER_SEC,
				CORE_PARAM_INT_MAX_DOWNLOAD_SPEED_KBYTES_PER_SEC,
				CORE_PARAM_INT_MAX_CONNECTIONS_PER_TORRENT,
				CORE_PARAM_INT_MAX_CONNECTIONS_GLOBAL,
			};
		
		cached_property_values 	= new Object[]{
				new Integer( delegate.getIntParameter( cached_property_names[0] )),
				new Integer( delegate.getIntParameter( cached_property_names[1] )),
				new Integer( delegate.getIntParameter( cached_property_names[2] )),
				new Integer( delegate.getIntParameter( cached_property_names[3] )),
		};		
	}
	
	public Object
	_setLocal()
	
		throws RPException
	{
		return( _fixupLocal());
	}
	
	public void
	_setRemote(
		RPRequestDispatcher		_dispatcher )
	{
		super._setRemote( _dispatcher );
		
		property_cache	= new Properties();
		
		for (int i=0;i<cached_property_names.length;i++){
			
			// System.out.println( "cache:" + cached_property_names[i] + "=" + cached_property_values[i] );
			
			property_cache.put(cached_property_names[i],cached_property_values[i]);
		}
	}
	
	public RPReply
	_process(
		RPRequest	request	)
	{
		String	method = request.getMethod();
		
		Object[] params = (Object[])request.getParams();
		
		if ( method.equals( "getPluginIntParameter[String,int]")){
			
			return( new RPReply( new Integer( delegate.getPluginIntParameter((String)params[0],((Integer)params[1]).intValue()))));
			
		}else if ( method.equals( "getPluginStringParameter[String,String]")){
				
			return( new RPReply( delegate.getPluginStringParameter((String)params[0],(String)params[1])));
		
		}else if ( method.equals( "setPluginParameter[String,int]")){
				
			delegate.setPluginParameter((String)params[0],((Integer)params[1]).intValue());
				
			return( null );
			
		}else if ( 	method.equals( "getIntParameter[String,int]") ||
				 	method.equals( "getParameter[String,int]")){
				
			return( new RPReply( new Integer( delegate.getIntParameter((String)params[0],((Integer)params[1]).intValue()))));
				
		}else if ( method.equals( "setParameter[String,int]")){
					
			delegate.setIntParameter((String)params[0],((Integer)params[1]).intValue());
			
			return( null );
			
		}else if ( method.equals( "save")){
			
			try{ 
				delegate.save();
				
				return( null );
				
			}catch( PluginException e ){
				
				return( new RPReply( e ));
			}
		}			
	
			
		throw( new RPException( "Unknown method: " + method ));
	}

	// ***************************************************

	public String
	getPluginConfigKeyPrefix()
	{
	  	notSupported();
	  	
	  	return(null);
	}
	
    public float getFloatParameter(String key) {
	  	notSupported();
	  	
	  	return(0);
    }

    public int getIntParameter(String key)
	  {
	  	notSupported();
	  	
	  	return(0);
	  }

	  public int getIntParameter(String key, int default_value)
	  {
		Integer	res = (Integer)property_cache.get( key );
		
		if ( res == null ){
			
			res = (Integer)_dispatcher.dispatch( new RPRequest( this, "getIntParameter[String,int]", new Object[]{key,new Integer(default_value)} )).getResponse();
		}
		
		return( res.intValue());
	  }
		
	  public void
	  setIntParameter( 
		String	key, 
		int		value )
	  {
	  	property_cache.put( key, new Integer( value ));
	  	
		_dispatcher.dispatch( new RPRequest( this, "setParameter[String,int]", new Object[]{key,new Integer(value)} )).getResponse();
	  }
	  
	  public String getStringParameter(String key)
	  {
	  	notSupported();
	  	
	  	return(null);
	  }
	  
	  public String getStringParameter(String name, String _default )
	  {
	  	notSupported();
	  	
	  	return(null);
	  }
	  
	  public boolean getBooleanParameter(String key)
	  {	
	  	notSupported();
	  	
	  	return(false);
	  }
	  
	  public boolean getBooleanParameter(String key, boolean _default )
	  {
	  	notSupported();
	  	
	  	return( false );
	  }
	  
	  public byte[] getByteParameter(String name, byte[] _default )
	  {
	  	notSupported();
	  	
	  	return( null );
	  }
	   
	  public int getPluginIntParameter(String key)
	  {	
	  	notSupported();
	  	
	  	return(0);
	  }
	  
	  public int getPluginIntParameter(String key,int defaultValue)
	  {
		Integer	res = (Integer)_dispatcher.dispatch( new RPRequest( this, "getPluginIntParameter[String,int]", new Object[]{key,new Integer(defaultValue)} )).getResponse();
		
		return( res.intValue());
	  }
	  
	  public String getPluginStringParameter(String key)
	  {
	  	notSupported();
	  	
	  	return(null);
	  }
	  
	  public String getPluginStringParameter(String key,String defaultValue)
	  {
		String	res = (String)_dispatcher.dispatch( new RPRequest( this, "getPluginStringParameter[String,String]", new Object[]{key,defaultValue} )).getResponse();
		
		return( res );
	  }
	  
	  public boolean getPluginBooleanParameter(String key)
	  {
	  	notSupported();
	  	
	  	return(false);
	  }
	  
	  public boolean getPluginBooleanParameter(String key,boolean defaultValue)
	  {
	  	notSupported();
	  	
	  	return(false);
	  }
	    
	  public void setPluginParameter(String key,int value)
	  {
		_dispatcher.dispatch( new RPRequest( this, "setPluginParameter[String,int]", new Object[]{key,new Integer(value)} ));
	  }
	  
	  public void setPluginParameter(String key,String value)
	  {
	  	
	  	notSupported();
	  }
	  
	  public void setPluginParameter(String key,boolean value)
	  {  	
	  	notSupported();
	  }
	  
	  public void setPluginParameter(String key,byte[] value)
	  {
	  	notSupported();
	  }
	  
	  public void
	  save()
	  	throws PluginException
	  {
	  	try{
	  		_dispatcher.dispatch( new RPRequest( this, "save", null)).getResponse();
	  		
		}catch( RPException e ){
			
			Throwable cause = e.getCause();
			
			if ( cause instanceof PluginException ){
				
				throw((PluginException)cause);
			}
			
			throw( e );
		}
	  }
    
    
}
