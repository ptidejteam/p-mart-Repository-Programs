/*
 * Created on 15-Jun-2004
 * Created by Paul Gardner
 * Copyright (C) 2004, 2005, 2006 Aelitis, All Rights Reserved.
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

package com.aelitis.net.upnp.impl.services;

/**
 * @author parg
 *
 */

import java.net.*;
import java.util.*;

import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;

import com.aelitis.net.upnp.*;
import com.aelitis.net.upnp.impl.device.UPnPDeviceImpl;
import com.aelitis.net.upnp.services.UPnPSpecificService;

public class 
UPnPServiceImpl
	implements 	UPnPService
{
	protected UPnPDeviceImpl	device;
	
	protected String			service_type;
	protected String			desc_url;
	protected String			control_url;
	
	protected List				actions;
	protected List				state_vars;
	
	public
	UPnPServiceImpl(
		UPnPDeviceImpl					_device,
		String							indent,
		SimpleXMLParserDocumentNode		service_node )
	{
		device		= _device;
		
		service_type 		= service_node.getChild("ServiceType").getValue().trim();
		
		desc_url	= device.getAbsoluteURL(service_node.getChild("SCPDURL").getValue());
		
		control_url	= device.getAbsoluteURL(service_node.getChild("controlURL").getValue());
		
		device.getUPnP().log( indent + "desc=" + desc_url + ", control=" + control_url );
	}
	
	public UPnPDevice
	getDevice()
	{
		return( device );
	}
	
	public String
	getServiceType()
	{
		return( service_type );
	}
	
	public UPnPAction[]
	getActions()
	
		throws UPnPException
	{
		if ( actions == null ){
			
			loadDescription();
		}
		
		UPnPAction[]	res = new UPnPAction[actions.size()];
		
		actions.toArray( res );
		
		return( res );
	}
	
	public UPnPAction
	getAction(
		String	name )
	
		throws UPnPException
	{
		UPnPAction[]	my_actions = getActions();
		
		for (int i=0;i<my_actions.length;i++){
			
			if ( my_actions[i].getName().equalsIgnoreCase( name )){
				
				return( my_actions[i] );
			}
		}
		
		return( null );
	}
	
	public UPnPStateVariable[]
	getStateVariables()
	
		throws UPnPException
	{
		if ( state_vars == null ){
			
			loadDescription();
		}
		
		UPnPStateVariable[]	res = new UPnPStateVariable[state_vars.size()];
		
		state_vars.toArray( res );
		
		return( res );		
	}
	
	public UPnPStateVariable
	getStateVariable(
		String	name )
	
		throws UPnPException
	{
		UPnPStateVariable[]	vars = getStateVariables();
		
		for (int i=0;i<vars.length;i++){
			
			if ( vars[i].getName().equalsIgnoreCase( name )){
				
				return( vars[i] );
			}
		}
		
		return( null );
	}
		
	public URL
	getDescriptionURL()
	
		throws UPnPException
	{
		return( getURL( desc_url ));
	}
	
	public URL
	getControlURL()
	
		throws UPnPException
	{
		return( getURL( control_url ));
	}
	
	protected URL
	getURL(
		String	basis )
	
		throws UPnPException
	{
		try{
			URL	target;
			
			if ( basis.toLowerCase().startsWith( "http" )){
				
					// absolute
				
				target = new URL( basis );
				
			}else{
				
					// relative
				
				URL	root_location = device.getRootDevice().getLocation();
				
				target = new URL( root_location.getProtocol() + "://" +
									root_location.getHost() + 
									(root_location.getPort() == -1?"":":" + root_location.getPort()) + 
									(basis.startsWith( "/" )?"":"/") + basis );
				
			}
			
			return( target );
			
		}catch( MalformedURLException e ){
			
			throw( new UPnPException( "Malformed URL", e ));
		}
	}
	
	protected void
	loadDescription()
	
		throws UPnPException
	{		
		SimpleXMLParserDocument	doc = device.getUPnP().downloadXML( getDescriptionURL());

		parseActions( doc.getChild( "ActionList" ));
				
		parseStateVars( doc.getChild( "ServiceStateTable"));
	}
	
	protected void
	parseActions(
		SimpleXMLParserDocumentNode	action_list )
	{
		actions	= new ArrayList();
		
		SimpleXMLParserDocumentNode[]	kids = action_list.getChildren();
		
		for (int i=0;i<kids.length;i++){
			
			actions.add( new UPnPActionImpl( this, kids[i] ));
		}
	}
	
	protected void
	parseStateVars(
		SimpleXMLParserDocumentNode	action_list )
	{
		state_vars	= new ArrayList();
		
		SimpleXMLParserDocumentNode[]	kids = action_list.getChildren();
		
		for (int i=0;i<kids.length;i++){
			
			state_vars.add( new UPnPStateVariableImpl( this, kids[i] ));
		}
	}

	public UPnPSpecificService
	getSpecificService()
	{
		if ( service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANIPConnection:1")){
			
			return( new UPnPSSWANIPConnectionImpl( this ));
			
		}else if ( service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANPPPConnection:1")){
			
			return( new UPnPSSWANPPPConnectionImpl( this ));
			
		}else if ( service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1")){
			
			return( new UPnPSSWANCommonInterfaceConfigImpl( this ));
			
		}else{
			
			return( null );
		}
	}
}
