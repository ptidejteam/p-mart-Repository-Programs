/*
 * Created on 28-Nov-2004
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

package org.gudy.azureus2.pluginsimpl.local.installer;

/**
 * @author parg
 *
 */

import java.util.List;

import org.gudy.azureus2.core3.html.HTMLUtils;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.plugins.installer.*;
import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
import org.gudy.azureus2.pluginsimpl.update.PluginUpdatePlugin;
import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetails;

public class 
StandardPluginImpl 
	implements StandardPlugin, InstallablePluginImpl
{
	private PluginInstallerImpl	installer;
	private SFPluginDetails		details;
	private String				version;
	
	protected
	StandardPluginImpl(
		PluginInstallerImpl	_installer,
		SFPluginDetails		_details,
		String				_version )
	{
		installer	= _installer;
		details		= _details;
		version		= _version==null?"":_version;
	}
	
	public String
	getId()
	{
		return( details.getId());
	}
	
	public String
	getVersion()
	{
		return( version );
	}
	
	public String
	getName()
	{
		return( details.getName());
	}
	
	public String
	getDescription()
	{
		try{
			List lines = HTMLUtils.convertHTMLToText("", details.getDescription());
			
			String	res = "";
			
			for (int i=0;i<lines.size();i++){
				res += (i==0?"":"\n") + lines.get(i);
			}
			
			return( res );
			
		}catch( Throwable e ){
			
			return( Debug.getNestedExceptionMessage( e ));
		}
	}
	
		/**
		 * Returns the plugin's interface if already installed, null if it isn't
		 * @return
		 */
	
	public PluginInterface
	getAlreadyInstalledPlugin()
	{
		return( installer.getAlreadyInstalledPlugin( getId()));
	}
	
	public void
	install(
		boolean		shared )
	
		throws PluginException
	{
		installer.install( this, shared );
	}
	
	public void
	uninstall()
	
		throws PluginException
	{
		installer.uninstall( this );
	}
	
	public PluginInstaller
	getInstaller()
	{
		return( installer );
	}
	
	public void
	addUpdate(
			UpdateCheckInstance	inst,
			PluginUpdatePlugin	plugin_update_plugin,
			Plugin				plugin,
			PluginInterface		plugin_interface )
	{
		inst.addUpdatableComponent(
				plugin_update_plugin.getCustomUpdateableComponent( getId(), false), false );
	}
}
