/*
 * Created on Jun 20, 2003
 * Copyright (C) 2003, 2004, 2005, 2006 Aelitis, All Rights Reserved.
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
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */
package org.gudy.azureus2.core3.config.impl;

import java.io.*;
import java.io.IOException;
import java.util.*;


import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.core3.config.*;

/**
 * A singleton used to store configuration into a bencoded file.
 *
 * @author TdC_VgA
 *
 */
public class 
ConfigurationManager 
	implements AEDiagnosticsEvidenceGenerator
{
  private static ConfigurationManager 	config_temp = null;
  private static ConfigurationManager 	config 		= null;
  private static AEMonitor				class_mon	= new AEMonitor( "ConfigMan:class" );
  
  private Map propertiesMap;	// leave this NULL - it picks up errors caused by initialisation sequence errors
  
  private List		listeners 			= new ArrayList();
  private Hashtable parameterListeners 	= new Hashtable();
  
  private AEMonitor	this_mon	= new AEMonitor( "ConfigMan");
  
 
  public static ConfigurationManager getInstance() {
  	try{
  		class_mon.enter();
  	
	  	if ( config == null){
	  		
	  			// this is nasty but I can't see an easy way around it. Unfortunately while reading the config
	  			// we hit other code (logging for example) that needs access to the config data. Things are
	  			// cunningly (?) arranged so that a recursive call here *won't* result in a further (looping)
	  			// recursive call if we attempt to load the config again. Hence this disgusting code that
	  			// goes for a second load attempt
	  		
	  		if ( config_temp == null ){
	  			
	  			config_temp = new ConfigurationManager();
	  		
	  			config_temp.load();
	  			
	  			config_temp.initialise();
	  			
	  		  	config	= config_temp;
	  		  	
	  		}else{
	  			
	  			if ( config_temp.propertiesMap == null ){
	  				
	  				config_temp.load();
	  			}
	  			
	  			return( config_temp );
	  		}
	  	}
	  	
	  	return config;
	  	
  	}finally{
  		class_mon.exit();
  	}
  }
  
  public static ConfigurationManager getInstance(Map data) {
  	try{
  		class_mon.enter();

	  	if (config == null){
	  		
	  		config = new ConfigurationManager(data);
	  	}
	  	
	  	return config;
  	}finally{
  		
  		class_mon.exit();
  	}
  }
  
  
  private 
  ConfigurationManager() 
  {
  }
  
  private 
  ConfigurationManager(
  	Map data ) 
  {
	  	// default state of play for config initialised from map is debug log files off unless already
	  	// specified
	  
	  if ( data.get("Logger.DebugFiles.Enabled") == null ){
		  
		  data.put( "Logger.DebugFiles.Enabled", new Long(0));
	  }
	  
	  propertiesMap	= data;
  }
  
  protected void
  initialise()
  {
		
	  //ConfigurationChecker.migrateConfig();  //removed 2201
	 	
	 ConfigurationChecker.checkConfiguration();

	 ConfigurationChecker.setSystemProperties();
		 	
	 AEDiagnostics.addEvidenceGenerator( this );
  }
  
  public void load(String filename) 
  {
  	Map	data = FileUtil.readResilientConfigFile( filename, false );
  	
  		// horrendous recursive loading going on here due to logger + config depedencies. If already loaded
  		// then use the existing data as it might have already been written to...
  	
  	if ( propertiesMap == null ){
  		
  		propertiesMap	= data;
  	}
  	
/* 
 * Can't do this yet.  Sometimes, there's a default set to x, but the code
 * calls get..Parameter(..., y).  y != x.  When the user sets the the parameter
 * to x, we remove it from the list.  Later, the get..Parameter(.., y) returns
 * y because there is no entry.
 * 
 * The solution is to not allow get..Parameter(.., y) when there's a default
 * value.  Another reason to not allow it is that having two defaults confuses
 * coders.
 *  	
  	// Remove entries that are default.  Saves memory, reduces
  	// file size when saved again
    ConfigurationDefaults def = ConfigurationDefaults.getInstance();
  	Iterator it = new TreeSet(propertiesMap.keySet()).iterator();

		while (it.hasNext()) {
			String key = (String)it.next();
			Object defValue = def.getDefaultValueAsObject(key);
			if (defValue == null)
				continue;

			if (defValue instanceof Long) {
				int iDefValue = ((Long)defValue).intValue();
				int iValue = getIntParameter(key, iDefValue);
				if (iValue == iDefValue)
					propertiesMap.remove(key);
			}
			if (defValue instanceof String) {
				String sDefValue = defValue.toString();
				String sValue = getStringParameter(key, sDefValue);
				if (sValue.compareTo(sDefValue) == 0)
					propertiesMap.remove(key);
			}
		}
*/
  }
  
  public void load() {
    load("azureus.config");
  }
  
  public void save(String filename) 
  {
	if ( propertiesMap == null ){
		
			// nothing to save, initialisation not complete
		
		return;
	}
	
  	FileUtil.writeResilientConfigFile( filename, propertiesMap );
    
  	List	listeners_copy;
  	
    try{
    	this_mon.enter();
    
    	listeners_copy = new ArrayList( listeners );
    	
    }finally{
    	
    	this_mon.exit();
    }
    
	for (int i=0;i<listeners_copy.size();i++){
		
		COConfigurationListener l = (COConfigurationListener)listeners_copy.get(i);
		
		if (l != null){
			
			try{
				l.configurationSaved();
				
			}catch( Throwable e ){
				
				Debug.printStackTrace( e );
			}
		}else{
			
			Debug.out("COConfigurationListener is null");
		}
	}
  }
  
  public void save() {
    save("azureus.config");
  }
  
  public boolean getBooleanParameter(String parameter, boolean defaultValue) {
    int defaultInt = defaultValue ? 1 : 0;
    int result = getIntParameter(parameter, defaultInt);
    return result == 0 ? false : true;
  }
  
  public boolean getBooleanParameter(String parameter) {
    ConfigurationDefaults def = ConfigurationDefaults.getInstance();
    int result;
    try {
      result = getIntParameter(parameter, def.getIntParameter(parameter));
    } catch (ConfigurationParameterNotFoundException e) {
      result = getIntParameter(parameter, def.def_boolean);
    }
    return result == 0 ? false : true;
  }
  
  public boolean setParameter(String parameter, boolean value) {
    return setParameter(parameter, value ? 1 : 0);
  }
  
  private Long getIntParameterRaw(String parameter) {
    try {
      return (Long) propertiesMap.get(parameter);
    } catch (Exception e) {
    	Debug.printStackTrace( e );
      return null;
    }
  }
  
  public int getIntParameter(String parameter, int defaultValue) {
    Long tempValue = getIntParameterRaw(parameter);
    return tempValue != null ? tempValue.intValue() : defaultValue;
  }
  
  public int getIntParameter(String parameter) {
  	ConfigurationDefaults def = ConfigurationDefaults.getInstance();
  	int result;
    try {
      result = getIntParameter(parameter, def.getIntParameter(parameter));
    } catch (ConfigurationParameterNotFoundException e) {
      result = getIntParameter(parameter, def.def_int);
    }
    return result;
  }
  
  private byte[] getByteParameterRaw(String parameter) {
    return (byte[]) propertiesMap.get(parameter);
  }
  
  public byte[] getByteParameter(String parameter, byte[] defaultValue) {
    byte[] tempValue = getByteParameterRaw(parameter);
    return tempValue != null ? tempValue : defaultValue;
  }
  
  private String getStringParameter(String parameter, byte[] defaultValue) {
	  byte[] bp = getByteParameter(parameter, defaultValue);
	  if ( bp == null ){
		  bp = getByteParameter(parameter, null);
	  }
      if (bp == null)
        return null;
      return bytesToString(bp);
  }
  
  public String getStringParameter(String parameter, String defaultValue) {
    String tempValue = getStringParameter(parameter, (byte[]) null);
    return tempValue != null ? tempValue : defaultValue;
  }
  
  public String getStringParameter(String parameter) {
    ConfigurationDefaults def = ConfigurationDefaults.getInstance();
    String result;
    try {
      result = getStringParameter(parameter, def.getStringParameter(parameter));
    } catch (ConfigurationParameterNotFoundException e) {
      result = getStringParameter(parameter, def.def_String);
    }
    return result;
  }
  
  public StringList getStringListParameter(String parameter) {
  	try {  		
  		List rawList = (List) propertiesMap.get(parameter);
  		if(rawList == null)
  			return new StringListImpl();  		
  		return new StringListImpl(rawList);  	
  	} catch(Exception e) {
  		Debug.printStackTrace(e);
  		return new StringListImpl();
  	}
  }
	  

  
  public boolean setParameter(String parameter,StringList value) {
  	try {
  		List	encoded = new ArrayList();
  		
  		List	l = ((StringListImpl)value).getList();
  		
  		for (int i=0;i<l.size();i++){
  			
  			encoded.add( stringToBytes((String)l.get(i)));
  		}
  		propertiesMap.put(parameter,encoded);
  	} catch(Exception e) {
  		Debug.printStackTrace(e);
  		return false;
  	}
  	return true;
  }
   
  public List 
  getListParameter(String parameter, List def) 
  {
  	try {  		
  		List rawList = (List) propertiesMap.get(parameter);
  		if(rawList == null)
  			return def;
  		return rawList;	
  	} catch(Exception e) {
  		Debug.printStackTrace(e);
  		return def;
  	}
  }
  
  public boolean setParameter(String parameter,List value) {
  	try {
  		propertiesMap.put(parameter,value);
  		notifyParameterListeners(parameter);
  	} catch(Exception e) {
  		Debug.printStackTrace(e);
  		return false;
  	}
  	return true;
  }

  public Map 
  getMapParameter(String parameter, Map def) 
  {
  	try {  		
		Map map = (Map) propertiesMap.get(parameter);
  		if(map == null)
  			return def;
  		return map;	
  	} catch(Exception e) {
  		Debug.printStackTrace(e);
  		return def;
  	}
  }
  
  public boolean setParameter(String parameter,Map value) {
  	try {
  		propertiesMap.put(parameter,value);
  		notifyParameterListeners(parameter);
  	} catch(Exception e) {
  		Debug.printStackTrace(e);
  		return false;
  	}
  	return true;
  }
  
  
  public String getDirectoryParameter(String parameter) throws IOException {
    String dir = getStringParameter(parameter);
    
    if( dir.length() > 0 ) {
      File temp = new File(dir);
      if (!temp.exists())
        temp.mkdirs();
      else if (!temp.isDirectory()) {
        throw new IOException("Configuration error. This is not a directory: " + dir);
      }
    }

    return dir;
  }
  
  public float getFloatParameter(String parameter) {
    ConfigurationDefaults def = ConfigurationDefaults.getInstance();
    try {
      Object o = propertiesMap.get(parameter);
      if (o instanceof Number) {
        return ((Number)o).floatValue();
      }
      
      String s = getStringParameter(parameter);
      
      if (!s.equals(def.def_String))
        return Float.parseFloat(s);
    } catch (Exception e) {
    	Debug.printStackTrace( e );
    }
    
    try {
      return def.getFloatParameter(parameter);
    } catch (Exception e2) {
      return def.def_float;
    }
  }

  public boolean setParameter(String parameter, float defaultValue) {
    String newValue = String.valueOf(defaultValue);
    return setParameter(parameter, stringToBytes(newValue));
  }

  public boolean setParameter(String parameter, int defaultValue) {
    Long newValue = new Long(defaultValue);
    Long oldValue = (Long) propertiesMap.put(parameter, newValue);
    return notifyParameterListenersIfChanged(parameter, newValue, oldValue);
  }
  
  public boolean setParameter(String parameter, byte[] defaultValue) {
    byte[] oldValue = (byte[]) propertiesMap.put(parameter, defaultValue);
    return notifyParameterListenersIfChanged(parameter, defaultValue, oldValue);
   }
  
  public boolean setParameter(String parameter, String defaultValue) {
    return setParameter(parameter, stringToBytes(defaultValue));
  }

	public boolean setRGBParameter(String parameter, int red, int green, int blue) {
    boolean bAnyChanged = false;
    bAnyChanged |= setParameter(parameter + ".red", red);
    bAnyChanged |= setParameter(parameter + ".green", green);
    bAnyChanged |= setParameter(parameter + ".blue", blue);
    if (bAnyChanged)
      notifyParameterListeners(parameter);

    return bAnyChanged;
	}
  
  // Sets a parameter back to its default
  public boolean setParameter(String parameter) throws ConfigurationParameterNotFoundException {
    ConfigurationDefaults def = ConfigurationDefaults.getInstance();
    try {
      return setParameter(parameter, def.getIntParameter(parameter));
    } catch (Exception e) {
      return setParameter(parameter, def.getStringParameter(parameter));
    }
  }
  
  
  /**
   * Remove the given configuration parameter completely.
   * @param parameter to remove
   * @return true if found and removed, false if not
   */
  public boolean removeParameter( String parameter ) {
    boolean removed = propertiesMap.remove( parameter ) != null;
    if (removed)
    	notifyParameterListeners(parameter);
    return removed;
  }
  
  public boolean removeRGBParameter(String parameter) {
    boolean bAnyChanged = false;
    bAnyChanged |= removeParameter(parameter + ".red");
    bAnyChanged |= removeParameter(parameter + ".green");
    bAnyChanged |= removeParameter(parameter + ".blue");
    if (bAnyChanged)
      notifyParameterListeners(parameter);

    return bAnyChanged;
  }
  
  /**
   * Does the given parameter exist.
   * @param parameter to check
   * @return true if exists, false if not present
   */
  
  public boolean 
  doesParameterNonDefaultExist( 
  	String parameter ) 
  {
    return propertiesMap.containsKey( parameter );
  }
  
  
  
  private boolean  notifyParameterListenersIfChanged(String parameter, Long newValue, Long oldValue) {
    if(oldValue == null || 0 != newValue.compareTo(oldValue)) {
      notifyParameterListeners(parameter);
      return true;
    }
    return false;
  }

  private boolean notifyParameterListenersIfChanged(String parameter, byte[] newValue, byte[] oldValue) {
    if(oldValue == null || !Arrays.equals(newValue, oldValue)) {
      notifyParameterListeners(parameter);
      return true;
    }
    return false;
  }
    
  private void notifyParameterListeners(String parameter) {
    Vector parameterListener = (Vector) parameterListeners.get(parameter);
    if(parameterListener != null) {
    	try{
    		for (int i=0;i<parameterListener.size();i++){
        
    			ParameterListener	listener = (ParameterListener)parameterListener.get(i);
    			
    			if(listener != null) {
    				listener.parameterChanged(parameter);
    			}
    		}
    	}catch( Throwable e ){
    		
    			// we're not synchronized so possible but unlikely error here
    		
    		Debug.printStackTrace( e );
      }
    }
   }

  public void addParameterListener(String parameter, ParameterListener listener){
  	try{
  		this_mon.enter();
  	
	    if(parameter == null || listener == null)
	      return;
	    Vector parameterListener = (Vector) parameterListeners.get(parameter);
	    if(parameterListener == null) {
	      parameterListeners.put(parameter, parameterListener = new Vector());
	    }
	    if(!parameterListener.contains(listener))
	      parameterListener.add(listener); 
  	}finally{
  		this_mon.exit();
  	}
  }

  public void removeParameterListener(String parameter, ParameterListener listener){
  	try{
  		this_mon.enter();
 
	    if(parameter == null || listener == null)
	      return;
	    Vector parameterListener = (Vector) parameterListeners.get(parameter);
	    if(parameterListener != null) {
	    	parameterListener.remove(listener);
	    }
  	}finally{
  		this_mon.exit();
  	}
  }

  public void addListener(COConfigurationListener listener) {
  	try{
  		this_mon.enter();

  		listeners.add(listener);
  		
  	}finally{
  		
  		this_mon.exit();
  	}
  }

  public void removeListener(COConfigurationListener listener) {
  	try{
  		this_mon.enter();
  	
  		listeners.remove(listener);
  	}finally{
  		
  		this_mon.exit();
  	}
  }
  
	public void
	generate(
		IndentWriter		writer )
	{
		writer.println( "Configuration Details" );
		
		try{
			writer.indent();
		
			writer.println( "System Properties" );
			
			try{
				writer.indent();
			
				Properties props = System.getProperties();
				
				Iterator	it = new TreeSet( props.keySet()).iterator();
				
				while(it.hasNext()){
					
					String	key = (String)it.next();
					
					writer.println( key + "=" + props.get( key ));
				}
			}finally{
				
				writer.exdent();
			}
			
			writer.println( "Azureus Config" );

			try{
				writer.indent();
			
				Iterator it = new TreeSet(propertiesMap.keySet()).iterator();
			
				while( it.hasNext()){
					
					Object	key 	= it.next();
					Object	value	= propertiesMap.get(key);
					boolean bParamExists = ConfigurationDefaults.getInstance().doesParameterDefaultExist(key.toString());
					if (!bParamExists)
						key = "[NoDef] " + key;
					
					if ( value instanceof Long ){
						
						writer.println( key + "=" + value );
						
					}else if ( value instanceof List ){
						
						writer.println( key + "=" + value + "[list]" );
						
					}else if ( value instanceof Map ){
						
						writer.println( key + "=" + value + "[map]" );
						
					}else if ( value instanceof byte[] ){
						
						byte[]	b = (byte[])value;
					
						boolean	hex	= false;
						
						for (int i=0;i<b.length;i++){
							
							char	c = (char)b[i];
							
							if ( !	( 	Character.isLetterOrDigit(c) ||
										"`�\"�$%^&*()-_=+[{]};:'@#~,<.>/?'".indexOf(c) != -1 )){
								
								hex	= true;
								
								break;
							}
						}
						writer.println( key + "=" + (hex?ByteFormatter.nicePrint(b):bytesToString((byte[])value)));
						
					}else{
						
						writer.println( key + "=" + value + "[unknown]" );
					}
				}
			}finally{
				
				writer.exdent();
			}
		}finally{
			
			writer.exdent();
		}
	}
	
	protected static String
	bytesToString(
		byte[]	bytes )
	{
		try{
			return( new String( bytes, Constants.DEFAULT_ENCODING ));
			
		}catch( Throwable e ){
			
			return( new String(bytes));
		}
	}
	
	protected static byte[]
	stringToBytes(
		String	str )
	{
		try{
			return( str.getBytes( Constants.DEFAULT_ENCODING ));
			
		}catch( Throwable e ){
			
			return( str.getBytes());
		}
	}
	
}