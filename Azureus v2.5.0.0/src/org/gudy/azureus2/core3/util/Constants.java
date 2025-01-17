/*
 * Created on 16 juin 2003
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
package org.gudy.azureus2.core3.util;

import java.nio.charset.Charset;
import java.util.StringTokenizer;

/**
 *  
 * @author Olivier
 *
 */

public class 
Constants 
{
  public static final String SF_WEB_SITE		= "http://azureus.sourceforge.net/";
  public static final String AELITIS_WEB_SITE   = "http://azureus.aelitis.com/";
  
  public static final String AELITIS_TORRENTS	= "http://torrents.aelitis.com:88/torrents/";
  public static final String AZUREUS_WIKI = AELITIS_WEB_SITE + "wiki/index.php/";
  
  
  public static final String DEFAULT_ENCODING 	= "UTF8";
  public static final String BYTE_ENCODING 		= "ISO-8859-1";
  public static Charset	BYTE_CHARSET;
  public static Charset	DEFAULT_CHARSET;

  static{
	  try{
	  	BYTE_CHARSET 	= Charset.forName( Constants.BYTE_ENCODING );
	 	DEFAULT_CHARSET = Charset.forName( Constants.DEFAULT_ENCODING );

	}catch( Throwable e ){
		
		e.printStackTrace();
	}
  }
  
  public static final String INFINITY_STRING	= "\u221E"; // "oo";
  public static final int    INFINITY_AS_INT = 31536000; // seconds (365days)
  
  	// keep the CVS style constant coz version checkers depend on it!
  	// e.g. 2.0.8.3
    //      2.0.8.3_CVS
    //      2.0.8.3_Bnn       // incremental build
  
  public static final String AZUREUS_NAME	  = "Azureus";
  public static final String AZUREUS_VERSION  = "2.5.0.0";  //2.5.0.1_CVS
  public static final byte[] VERSION_ID       = ("-" + "AZ" + "2500" + "-").getBytes();  //MUST be 8 chars long!
  
  
  public static final String  OSName = System.getProperty("os.name");
  
  public static final boolean isOSX				= OSName.toLowerCase().startsWith("mac os");
  public static final boolean isLinux			= OSName.equalsIgnoreCase("Linux");
  public static final boolean isSolaris			= OSName.equalsIgnoreCase("SunOS");
  public static final boolean isFreeBSD			= OSName.equalsIgnoreCase("FreeBSD");
  public static final boolean isWindowsXP		= OSName.equalsIgnoreCase("Windows XP");
  public static final boolean isWindows95		= OSName.equalsIgnoreCase("Windows 95");
  public static final boolean isWindows98		= OSName.equalsIgnoreCase("Windows 98");
  public static final boolean isWindowsME		= OSName.equalsIgnoreCase("Windows ME");
  public static final boolean isWindows9598ME	= isWindows95 || isWindows98 || isWindowsME;

  public static final boolean isWindows	= OSName.toLowerCase().startsWith("windows");
  // If it isn't windows or osx, it's most likely an unix flavor
  public static final boolean isUnix = !isWindows && !isOSX;
 
  public static final String	JAVA_VERSION = System.getProperty("java.version");
  
  public static final String	FILE_WILDCARD = isWindows?"*.*":"*";
  
  	/**
  	 * Gets the current version, or if a CVS version, the one on which it is based 
  	 * @return
  	 */
  
  public static String
  getBaseVersion()
  {
  	return( getBaseVersion( AZUREUS_VERSION ));
  }
  
  public static String
  getBaseVersion(
  	String	version )
  {
  	int	p1 = version.indexOf("_");	// _CVS or _Bnn
  	
  	if ( p1 == -1 ){
  		
  		return( version );
  	}
  	
  	return( version.substring(0,p1));
  }
  
  	/**
  	 * is this a formal build or CVS/incremental 
  	 * @return
  	 */
  
  public static boolean
  isCVSVersion()
  {
  	return( isCVSVersion( AZUREUS_VERSION )); 
  }
  
  public static boolean
  isCVSVersion(
  	String	version )
  {
  	return( version.indexOf("_") != -1 );  
  }
  
  	/**
  	 * For CVS builds this returns the incremental build number. For people running their own
  	 * builds this returns -1 
  	 * @return
  	 */
  
  public static int
  getIncrementalBuild()
  {
  	return( getIncrementalBuild( AZUREUS_VERSION ));
  }
  
  public static int
  getIncrementalBuild(
  	String	version )
  {
  	if ( !isCVSVersion(version)){
  		
  		return( 0 );
  	}
  	
  	int	p1 = version.indexOf( "_B" );
  	
  	if ( p1 == -1 ){
  		
  		return( -1 );
  	}
  	
  	try{
  		return( Integer.parseInt( version.substring(p1+2)));
  		
  	}catch( Throwable e ){
  		
  		System.out.println("can't parse version");
  		
  		return( -1 );
  	}
  }
  
		/**
		 * compare two version strings of form n.n.n.n (e.g. 1.2.3.4)
		 * @param version_1	
		 * @param version_2
		 * @return -ve -> version_1 lower, 0 = same, +ve -> version_1 higher
		 */
	
	public static int
	compareVersions(
		String		version_1,
		String		version_2 )
	{	
		try{
			if ( version_1.startsWith("." )){
				version_1 = "0" + version_1;
			}
			if ( version_2.startsWith("." )){
				version_2 = "0" + version_2;
			}
			
			StringTokenizer	tok1 = new StringTokenizer(version_1,".");
			StringTokenizer	tok2 = new StringTokenizer(version_2,".");
			
			while( true ){
				if ( tok1.hasMoreTokens() && tok2.hasMoreTokens()){
				
					int	i1 = Integer.parseInt(tok1.nextToken());
					int	i2 = Integer.parseInt(tok2.nextToken());
				
					if ( i1 != i2 ){
						
						return( i1 - i2 );
					}
				}else if ( tok1.hasMoreTokens()){
					
					int	i1 = Integer.parseInt(tok1.nextToken());
	
					if ( i1 != 0 ){
						
						return( 1 );
					}
				}else if ( tok2.hasMoreTokens()){
					
					int	i2 = Integer.parseInt(tok2.nextToken());
	
					if ( i2 != 0 ){
						
						return( -1 );
					}
				}else{
					return( 0 );
				}
			}
		}catch( Throwable e ){
			
			Debug.printStackTrace(e);
			
			return( 0 );
		}
	}
}
