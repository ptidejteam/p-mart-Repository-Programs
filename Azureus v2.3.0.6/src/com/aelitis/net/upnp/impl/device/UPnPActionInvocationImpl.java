/*
 * Created on 15-Jun-2004
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

package com.aelitis.net.upnp.impl.device;

/**
 * @author parg
 *
 */

import java.util.*;

import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;

import com.aelitis.net.upnp.*;

public class 
UPnPActionInvocationImpl
	implements UPnPActionInvocation
{
	protected UPnPActionImpl		action;
	
	protected List	arg_names	= new ArrayList();
	protected List	arg_values	= new ArrayList();
	
	protected
	UPnPActionInvocationImpl(
		UPnPActionImpl		_action )
	{
		action		= _action;
	}
	
	public void
	addArgument(
		String	name,
		String	value )
	{
		arg_names.add( name );
		
		arg_values.add( value );
	}
	
	public UPnPActionArgument[]
	invoke()
	
		throws UPnPException
	{	
		UPnPService	service = action.getService();
		
		String	soap_action = service.getServiceType() + "#" + action.getName();
		
		try{
			String	request =
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
				"<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
					"<s:Body>";
													
			request += "<u:" + action.getName() + 
							" xmlns:u=\"" + service.getServiceType()+ "\">";
			
			
			for (int i=0;i<arg_names.size();i++){
			
				String	name 	= (String)arg_names.get(i);
				String	value 	= (String)arg_values.get(i);
				
				request += "<" + name + ">" + value + "</" + name + ">";
			}
			
			request += "</u:" + action.getName() + ">";

			request += "</s:Body>"+
						"</s:Envelope>";
							
				// try standard POST
			
			SimpleXMLParserDocument resp_doc	= ((UPnPDeviceImpl)action.getService().getDevice()).getUPnP().performSOAPRequest( service, soap_action, request );
						
			SimpleXMLParserDocumentNode	body = resp_doc.getChild( "Body" );
			
			SimpleXMLParserDocumentNode fault = body.getChild( "Fault" );
			
			if ( fault != null ){
				
				throw( new UPnPException( "Invoke of '" + soap_action + "' fails - fault reported: " + fault.getValue()));
			}
			
			SimpleXMLParserDocumentNode	resp_node = body.getChild( action.getName() + "Response" );
			
			if ( resp_node == null ){
				
				throw( new UPnPException( "Invoke of '" + soap_action + "' fails - response missing: " + body.getValue()));
			}
			
			SimpleXMLParserDocumentNode[]	out_nodes = resp_node.getChildren();
			
			UPnPActionArgument[]	resp = new UPnPActionArgument[out_nodes.length];
			
			for (int i=0;i<out_nodes.length;i++){
				
				resp[i] = new UPnPActionArgumentImpl( out_nodes[i].getName(), out_nodes[i].getValue());
			}
			
			return( resp );
			
		}catch( Throwable e ){
			
			if ( e instanceof UPnPException ){
				
				throw((UPnPException)e);
			}
			
			throw( new UPnPException( "Invoke of '" + soap_action + "' fails: " + e.getMessage(), e ));	
		}
	}
}
