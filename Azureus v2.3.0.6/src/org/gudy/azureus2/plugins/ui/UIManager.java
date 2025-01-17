/*
 * Created on 19-Apr-2004
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

package org.gudy.azureus2.plugins.ui;

import java.net.URL;

import org.gudy.azureus2.plugins.PluginView;
import org.gudy.azureus2.plugins.ui.model.*;
import org.gudy.azureus2.plugins.ui.SWT.SWTManager;
import org.gudy.azureus2.plugins.ui.tables.TableManager;

/**
 * Management tools for the User Inferface
 * 
 * @author parg
 */
public interface 
UIManager 
{
		/**
		 * Gets a basic plugin view model that supports simple plugin requirements
		 * After getting the model create the view using createPluginView
		 * @param name name
		 * @return BasicPluginViewModel
		 * @deprecated Use createBasicPluginViewModel 
		 */
	
	public BasicPluginViewModel
	getBasicPluginViewModel(
		String			name );

		/**
		 * Creates a view from the model. It is then necessary to add it to the plugin
		 * as any other PluginView
		 * @param model
		 * @return PluginView
		 * @deprecated Use createBasicPluginViewModel
		 */
	
	public PluginView
	createPluginView(
		PluginViewModel	model );
	
	/**
	 * 
	 * @param section_name
	 * @return BasicPluginConfigModel
	 * @since 2.1.0.0
	 */
	public BasicPluginConfigModel
	createBasicPluginConfigModel(
		String		section_name );
	

	/**
	 * Creates a basic plugin view model and adds it to the plugin in one step.
	 * 
	 * @param parent_section
	 * @param section_name  see {@link org.gudy.azureus2.plugins.ui.config.ConfigSection}.SECTION_*
	 * @return BasicPluginConfigModel
	 * @since 2.1.0.0
	 */
	public BasicPluginConfigModel
	createBasicPluginConfigModel(
		String		parent_section,
		String		section_name );

	/**
	 * Creates a basic plugin view model and adds it to the plugin in one step.
	 * view is placed inside the plugins section of the configuration page.
	 * 
	 * @param name
	 * @return BasicPluginViewModel
	 * @since 2.1.0.2
	 */
	public BasicPluginViewModel
	createBasicPluginViewModel(
		String			name );
	
	/**
	 * 
	 * @param data
	 * @throws UIException
	 * @since 2.1.0.0
	 */
	public void
	copyToClipBoard(
		String		data )
	
		throws UIException;

	/**
	 * Retrieve the Table Manager
	 * 
	 * @return Table management functions
	 * @since 2.1.0.0
	 */
	public TableManager getTableManager();

	/**
	 * 
	 * @param title_resource
	 * @param message_resource
	 * @param contents
	 * @since 2.3.0.5
	 */
	public void
	showTextMessage(
		String		title_resource,
		String		message_resource,
		String		contents );
	
		/**
		 * @since 2.3.0.6
		 * @param url
		 */
	
	public void
	openURL(
		URL		url )
	
		throws UIException;
	
	/** Retrieve a class of SWT specific functions 
	 * 
	 * @deprecated 
	 * @return SWTManager
	 * 
	 * @since 2.1.0.0
	 */
	
	public SWTManager getSWTManager();
  
	  /* Future
	  public MenuManager getMenuManager();
	  In MenuManager..
	  public Menu addMenu(String resourceKey);
	  public Menu addMenu(String resourceKey, String parentKey);
	  public Menu addMenu(String resourceKey, Menu parent);
	  public MenuItem addMenuItem(String resourceKey);
	  public MenuItem addMenuItem(String resourceKey, String parentKey);
	  public MenuItem addMenuItem(String resourceKey, Menu parent);
	  */
  
		/**
		 * UIs should support generic UI-agnostic views such as the basic config model by default. The can also
		 * expose a UI-specific plugin interface to plugins via the UIInstance (see interface for details).
		 * To get access to this it is necessary to use the UIManagerListener 
		 */
	
	/**
	 * attach a new UI
	 *   
	 * @param factory
	 * @throws UIException
	 * 
	 * @since 2.3.0.5
	 */
	
	public void
	attachUI(
		UIInstanceFactory		factory )
	
		throws UIException;
	
	/**
	 * detach a UI - can fail if the UI doesn't support detaching
	 * 
	 * @param factory
	 * @throws UIException
	 * 
	 * @since 2.3.0.5
	 */
	
	public void
	detachUI(
		UIInstanceFactory		factory )
	
		throws UIException;

	/**
	 * 
	 * @param listener
	 * 
	 * @since 2.3.0.5
	 */
  	public void
  	addUIListener(
  		UIManagerListener listener );

	/**
	 * 
	 * @param listener
	 * 
	 * @since 2.3.0.5
	 */
 	public void
  	removeUIListener(
  		UIManagerListener listener );
 	
	/**
	 * 
	 * @param listener
	 * 
	 * @since 2.3.0.5
	 */
 	public void
  	addUIEventListener(
  		UIManagerEventListener listener );
  	
	/**
	 * 
	 * @param listener
	 * 
	 * @since 2.3.0.5
	 */
 	public void
  	removeUIEventListener(
  		UIManagerEventListener listener );
}
