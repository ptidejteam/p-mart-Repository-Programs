/*
 * Created on 07-May-2004
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

package org.gudy.azureus2.pluginsimpl.local.update;

/**
 * @author parg
 *
 */

import java.util.*;

import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.plugins.update.*;

import org.gudy.azureus2.plugins.utils.resourcedownloader.*;

public class 
UpdateImpl 
	implements Update
{
	private UpdateCheckInstanceImpl	instance;
	private String					name;
	private String[]				description;
	private String					new_version;
	private ResourceDownloader[]	downloaders;
	private boolean					mandatory;
	private int						restart_required;

	private Object					user_object;
	
	private List					listeners = new ArrayList();
	
	protected
	UpdateImpl(
		UpdateCheckInstanceImpl	_instance,
		String					_name,
		String[]				_desc,
		String					_new_version,
		ResourceDownloader[]	_downloaders,
		boolean					_mandatory,
		int						_restart_required )
	{
		instance			= _instance;
		name				= _name;
		description			= _desc;
		new_version			= _new_version;
		downloaders			= _downloaders;
		mandatory			= _mandatory;
		restart_required	= _restart_required;
		
		/*
		System.out.println( "Update:" + name + "/" + new_version + ", mand=" + mandatory + ", restart = " + restart_required  );
		
		for (int i=0;i<description.length;i++){
			
			System.out.println( description[i]);
		}
		
		for (int i=0;i<downloaders.length;i++){
			
			try{
				System.out.println( "  size:" + downloaders[i].getSize());
			}catch( Throwable e ){
				
				e.printStackTrace();
			}
		}
		*/
	}
	
	public UpdateCheckInstance
	getCheckInstance()
	{
		return( instance );
	}
	
	public String
	getName()
	{
		return( name );
	}

	public String[]
	getDescription()
	{
		return( description );
	}
	
	public String
	getNewVersion()
	{
		return( new_version );
	}
	
	public ResourceDownloader[]
	getDownloaders()
	{
		return( downloaders );
	}
	
	public boolean
	isMandatory()
	{
		return( mandatory );
	}
	
	public void
	setRestartRequired(
		int	_restart_required )
	{
		restart_required	= _restart_required;
	}
	
	public int
	getRestartRequired()
	{
		return( restart_required );
	}
	
	public void
	setUserObject(
		Object		obj )
	{
		user_object	= obj;
	}
	
	public Object
	getUserObject()
	{
		return( user_object );
	}
	
	public void
	cancel()
	{
		for (int i=0;i<downloaders.length;i++){
			
			try{
				downloaders[i].cancel();
				
			}catch( Throwable e ){
				
				Debug.printStackTrace( e );
			}
		}
	}
	
	public void
	complete()
	{
		for (int i=0;i<listeners.size();i++){
			
			try{
				((UpdateListener)listeners.get(i)).complete( this );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
		}
	}
	
	public Object
	getDecision(
		int			decision_type,
		String		decision_name,
		String		decision_description,
		Object		decision_data )
	{
		return( instance.getDecision( 
				this, decision_type, decision_name, decision_description, decision_data ));
	}
	
	public void
	addListener(
		UpdateListener	l )
	{
		listeners.add( l );
	}
	
	public void
	removeListener(
		UpdateListener	l )
	{
		listeners.remove(l);
	}
}
