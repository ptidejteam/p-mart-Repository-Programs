/*
 * File    : RPRequestHandler.java
 * Created : 15-Mar-2004
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

import java.util.*;

import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.plugins.logging.*;
import org.gudy.azureus2.plugins.ipfilter.*;
import org.gudy.azureus2.pluginsimpl.remote.download.*;

public class 
RPRequestHandler 
{
	protected PluginInterface	plugin_interface;
	
	protected boolean	view_mode;
	
	protected Map	reply_cache	= new HashMap();
	
	public
	RPRequestHandler(
		PluginInterface		_pi )
	{
		plugin_interface	= _pi;
		
		Properties properties				= plugin_interface.getPluginProperties();

		String	mode_str = (String)properties.get("mode");
		
		view_mode = mode_str != null && mode_str.trim().equalsIgnoreCase("view");
	}
	
	public RPReply
	processRequest(
		RPRequest		request )
	{
		Long	connection_id 	= new Long( request.getConnectionId());

		replyCache	cached_reply = connection_id.longValue()==0?null:(replyCache)reply_cache.get(connection_id);
		
		if ( cached_reply != null ){
			
			if ( cached_reply.getId() == request.getRequestId()){
				
				return( cached_reply.getReply());
			}
		}
		
		RPReply	reply = processRequestSupport( request );
		
		reply_cache.put( connection_id, new replyCache( request.getRequestId(), reply ));
		
		return( reply );
	}
	

	protected RPReply
	processRequestSupport(
		RPRequest		request )
	{
		try{
			RPObject		object 	= request.getObject();
			String			method	= request.getMethod();
			
			// System.out.println( "object = " + object + ", method = " + method );
				
			if ( object == null && method.equals("getSingleton")){
				
				RPReply reply = new RPReply( RPPluginInterface.create(plugin_interface));
				
				return( reply );
				
			}else if ( object == null && method.equals( "getDownloads")){
					
					// short cut method for quick access to downloads
					// used by GTS
				
				RPPluginInterface pi = RPPluginInterface.create(plugin_interface);
					
				RPDownloadManager dm = (RPDownloadManager)pi._process( new RPRequest(null, "getDownloadManager", null )).getResponse();
				
				RPReply	rep = dm._process(new RPRequest( null, "getDownloads", null ));
				
				rep.setProperty( "azureus_name", pi.azureus_name );
				
				rep.setProperty( "azureus_version", pi.azureus_version );
				
				return( rep );
				
			}else{
				
				// System.out.println( "Request: con = " + request.getConnectionId() + ", req = " + request.getRequestId() + ", client = " + request.getClientIP());
				
				object = RPObject._lookupLocal( object._getOID());
				
					// _setLocal synchronizes the RP objects with their underlying 
					// plugin objects
				
				object._setLocal();
				
				if ( method.equals( "_refresh" )){
				
					RPReply	reply = new RPReply( object );
				
					return( reply );
					
				}else{

					String	name = object._getName();
							
					if ( view_mode ){
						
							// this really needs fixing up properly (somehow)
						
						// System.out.println( "request: " + name + "/" + method );
						
						if ( name.equals( "Download" )){
							
							if ( 	method.equals( "start" ) ||
									method.equals( "stop" ) ||
									method.equals( "restart" ) ||
									method.equals( "remove" ) ||
									method.startsWith( "set" )){
								
								throw( new RPException( "Access Denied" ));
							}
						}else if ( name.equals( "DownloadManager" )){
							
							if ( 	method.startsWith( "addDownload")){
								
								throw( new RPException( "Access Denied" ));
							}
						}else if ( name.equals( "TorrentManager" )){
							
							if ( 	method.startsWith( "getURLDownloader")){
								
								throw( new RPException( "Access Denied" ));
							}	
						}else if ( name.equals( "PluginConfig" )){
								
							if ( 	method.startsWith( "setParameter")){
									
								throw( new RPException( "Access Denied" ));
							}
							
						}else if ( name.equals( "IPFilter" )){
							
							if ( 	method.startsWith( "set") ||
									method.startsWith( "create" )||
									method.startsWith( "save" )){
								
								throw( new RPException( "Access Denied" ));
							}
						}else if ( name.equals( "IPRange" )){
							
							if ( 	method.startsWith( "delete" )){
								
								throw( new RPException( "Access Denied" ));
							}
						}					
					}
	
					RPReply	reply = object._process( request );
					
					if ( 	name.equals( "IPFilter" ) && 
							method.equals( "setInRangeAddressesAreAllowed[boolean]" ) &&
							request.getClientIP() != null ){
						
						String	client_ip	= request.getClientIP();
						
							// problem here, if someone changes the mode here they'll lose their 
							// connection coz they'll be denied access :)
						
						boolean	b = ((Boolean)request.getParams()[0]).booleanValue();
						
						LoggerChannel[] channels = plugin_interface.getLogger().getChannels();
						
						IPFilter filter = plugin_interface.getIPFilter();
						
						if ( b ){
							
							if ( filter.isInRange( client_ip )){
							
									// we gotta add the client's address range
								
								for (int i=0;i<channels.length;i++){
									
									channels[i].log( 
											LoggerChannel.LT_INFORMATION,
										"Adding range for client '" + client_ip + "' as allow/deny flag changed to allow" );
								}
								
								filter.createAndAddRange(
										"auto-added for remote interface",
										client_ip,
										client_ip,
										false );
								
								filter.save();
								
								plugin_interface.getPluginconfig().save();
							}
							
						}else{
							
							IPRange[]	ranges = filter.getRanges();
							
							for (int i=0;i<ranges.length;i++){
								
								if ( ranges[i].isInRange(client_ip)){
									
									for (int j=0;j<channels.length;j++){
										
										channels[j].log( 
												LoggerChannel.LT_INFORMATION,
												"deleting range '" + ranges[i].getStartIP() + "-" + ranges[i].getEndIP() + "' for client '" + client_ip + "' as allow/deny flag changed to deny" );
									}
									
									ranges[i].delete();
								}
							}
							
							filter.save();
							
							plugin_interface.getPluginconfig().save();
						}
					}
					
					return( reply );
				}
			}
		}catch( RPException e ){
			
			return( new RPReply( e ));
			
		}catch( Throwable e ){
			
			return( new RPReply( new RPException( "server execution fails", e )));
		}
	}
	
	protected static class
	replyCache
	{
		protected long		id;
		protected RPReply	reply;
		
		protected
		replyCache(
			long		_id,
			RPReply		_reply )
		{
			id		= _id;
			reply	= _reply;
		}
		
		protected long
		getId()
		{
			return( id );
		}
		
		protected RPReply
		getReply()
		{
			return( reply );
		}
	}
}
