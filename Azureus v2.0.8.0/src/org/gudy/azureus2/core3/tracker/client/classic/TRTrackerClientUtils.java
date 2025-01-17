/*
 * File    : TRTrackerClientUtils.java
 * Created : 29-Feb-2004
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

package org.gudy.azureus2.core3.tracker.client.classic;

/**
 * @author parg
 *
 */

import java.net.MalformedURLException;
import java.net.URL;

import org.gudy.azureus2.core3.config.*;

public class 
TRTrackerClientUtils 
{
	protected static URL
	adjustURLForHosting(
		URL		url_in )
	{
		String 	tracker_ip 		= COConfigurationManager.getStringParameter("Tracker IP", "");
		
		if ( tracker_ip.length() > 0 ){
			
			if ( url_in.getHost().equalsIgnoreCase( tracker_ip )){
		
				String bind_ip = COConfigurationManager.getStringParameter("Bind IP", "");

				String	url = url_in.getProtocol() + "://";
		
				if ( bind_ip.length() < 7 ){
						
					url += "127.0.0.1";
						
				}else{
						
					url += bind_ip;
				}		
				
				int	port = url_in.getPort();
				
				if ( port != -1 ){
					
					url += ":" + url_in.getPort();
				}
				
				url += url_in.getPath();
				
				String query = url_in.getQuery();
				
				if ( query != null ){
					
					url += "?" + query;
				}
								
				try{
					return( new URL( url ));
					
				}catch( MalformedURLException e ){
					
					e.printStackTrace();
				}
			}
		}
		
		return( url_in );
	}
}
