/*
 * File    : LGLoggerImpl.java
 * Created : 16-Oct-2003
 * By      : stuff
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

package org.gudy.azureus2.core3.logging.impl;

/**
 * @author parg
 *
 */

import java.util.*;
import java.io.*;

import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.config.*;

public class 
LGLoggerImpl
{
	public static final String		LOG_FILE_NAME	= "az.log";
	public static final String		BAK_FILE_NAME	= "az.log.bak";
	
	public static final String		NL = "\r\n";
	
	private static boolean			initialised = false;
	
	private static ILoggerListener listener;

	private static boolean			log_to_file		= false;
	private static String			log_dir			= "";
	private static int				log_file_max	= 1;		// MB
	
	public static synchronized void
	initialise()
	{
		if ( !initialised ){
			
			initialised	= true;
			
			COConfigurationManager.addListener(
				new COConfigurationListener()
				{
					public void
					configurationSaved()
					{
						checkLoggingConfig();
					}
				});
				
			checkLoggingConfig();
			
			doRedirects();
			
			LGLogger.log( "**** Logging starts ****" );
		}
	}
	
	protected static void
	doRedirects()
	{
		try{
		
			System.setOut(new PrintStream(new redirectorOutputStream( System.out )));
			
			System.setErr(new PrintStream(new redirectorOutputStream( System.err )));
			
		}catch( Throwable e ){
			
			e.printStackTrace();
		}
	}
	
	protected static void
	checkLoggingConfig()
	{
		log_to_file 	= COConfigurationManager.getBooleanParameter("Logging Enable", false );
		
		log_dir			= COConfigurationManager.getStringParameter("Logging Dir", "" );
		
		log_file_max	= COConfigurationManager.getIntParameter("Logging Max Size", 1 );
	}

	public static synchronized void 
	log(
		int componentId, 
		int event, 
		int color, 
		String text) 
	{
		if ( log_to_file ){
			
			logToFile( "{".concat(String.valueOf(componentId)).concat(":").concat(String.valueOf(event)).concat(":").concat(String.valueOf(color)).concat("}  ").concat(text).concat(NL) );
		}
		
		if( listener !=  null ){
		
			listener.log(componentId,event,color,text);
		}    
	}
  
	public static boolean
	isLoggingOn()
	{
		return( listener != null || log_to_file );
	}
	
	public static synchronized void 
	setListener(ILoggerListener _listener) {
	  listener = _listener;
	}
  
	public static synchronized void 
	removeListener() {
	  listener = null;
	}
	
	protected static synchronized void
	logToFile(
		String	str )
	{
		if ( log_to_file ){
			Calendar now = GregorianCalendar.getInstance();
			        
			 String timeStamp =
			   "[".concat(String.valueOf(now.get(Calendar.HOUR_OF_DAY))).concat(":").concat(format(now.get(Calendar.MINUTE))).concat(":").concat(format(now.get(Calendar.SECOND))).concat("]  "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$        
	
			str = timeStamp.concat(str);
			
			PrintWriter	pw = null;
			
			File	file_name = new File( log_dir.concat(File.separator).concat(LOG_FILE_NAME) );
			
			try{		
						
				pw = new PrintWriter(new FileWriter( file_name, true ));
			
				pw.print( str );
				
			}catch( Throwable e ){
				
				// can't log this as go recursive!!!!
				
			}finally{
				
				if ( pw != null ){
					
					try{
					
						pw.close();
						
					}catch( Throwable e ){
						
						// can't log as go recursive!!!!
					}
					
					long	max_bytes = (log_file_max*1024*1024)/2;	// two files so half
					
					if ( file_name.length() > max_bytes ){
						
						File	back_name = new File( log_dir.concat(File.separator).concat(BAK_FILE_NAME) );
						
						if ( (!back_name.exists()) || back_name.delete()){
						
							if ( !file_name.renameTo( back_name )){
								
								file_name.delete();
							}
							
						}else{
							
							file_name.delete();
						}
					}
				}
			}
		}
	}
	
	private static String format(int n) {
	   if(n < 10) return "0".concat(String.valueOf(n)); //$NON-NLS-1$
	   return String.valueOf(n); //$NON-NLS-1$
	 }  
	 
	protected static class
	redirectorOutputStream
		extends OutputStream
	{
		protected PrintStream		ps;
		
		protected StringBuffer	buffer = new StringBuffer(1024);
		
		protected
		redirectorOutputStream(
			PrintStream		_ps )
		{
			ps	= _ps;
		}
			
		public void
		write(
			int		data )
		{
			char	c = (char)data;
			
			buffer.append(c);
			
			if ( c == '\n'){
			
				ps.print(buffer);
			
				logToFile(buffer.toString());
				
				buffer.setLength(0);
			}
		}
	}
}
