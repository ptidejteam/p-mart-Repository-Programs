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

package org.gudy.azureus2.plugins.installer;

import java.io.File;

import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;

/**
 * @author parg
 *
 */

public interface 
PluginInstaller 
{
		/**
		 * Gives access to the list of standard plugins listed on the Azureus website
		 * @return
		 */
	
	public StandardPlugin[]
	getStandardPlugins()
	
		throws PluginException;	
	
		/**
		 * Install one of more plugins in a single operation
		 * @param plugins
		 */
	
	public void
	install(
		InstallablePlugin[]	plugins,
		boolean				shared )
	
		throws PluginException;
	
		/**
		 * Installs a plugin from a file - must be either a ZIP file or a JAR file as per
		 * normal plugin update semantics. Name of file must be of the form:
		 *     <plugin_id> "_" <plugin_version> "." ["jar" | "zip" ].
		 * For example
		 *     myplugin_1.0.jar
		 * @param file
		 * @throws PluginException
		 */
	
	public FilePluginInstaller
	installFromFile(
		File		file )
	
		throws PluginException;
	
	public void
	uninstall(
		PluginInterface		plugin_interface )
	
	
		throws PluginException;
	
	public void
	uninstall(
		PluginInterface[]	plugin_interfaces )
	
	
		throws PluginException;
}
