/*
 * Created on 15-Nov-2004
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

package org.gudy.azureus2.core3.download.impl;

import java.util.*;
import java.io.*;

import org.gudy.azureus2.core3.download.*;
import org.gudy.azureus2.core3.logging.LGLogger;
import org.gudy.azureus2.core3.category.*;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentException;
import org.gudy.azureus2.core3.tracker.client.TRTrackerClient;
import org.gudy.azureus2.core3.util.AEMonitor;
import org.gudy.azureus2.core3.util.BEncoder;
import org.gudy.azureus2.core3.util.ByteFormatter;
import org.gudy.azureus2.core3.util.Constants;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.FileUtil;
import org.gudy.azureus2.core3.util.HashWrapper;
import org.gudy.azureus2.core3.util.TorrentUtils;

/**
 * @author parg
 * Overall aim of this is to stop updating the torrent file itself and update something
 * Azureus owns. To this end a file based on torrent hash is created in user-dir/active
 * It is actually just a copy of the torrent file
 */

public class 
DownloadManagerStateImpl
	implements DownloadManagerState
{
	private static final String			RESUME_KEY			= "resume";
	private static final String			TRACKER_CACHE_KEY	= "tracker_cache";
	private static final String			ATTRIBUTE_KEY		= "attributes";
		
	private static final File			ACTIVE_DIR;
	
	static{
	
		ACTIVE_DIR = FileUtil.getUserFile( "active" );
		
		if ( !ACTIVE_DIR.exists()){
			
			ACTIVE_DIR.mkdirs();
		}
	}
	
	private static AEMonitor	class_mon	= new AEMonitor( "DownloadManagerState:class" );
	
	private static Map					state_map = new HashMap();
	
	private DownloadManagerImpl			download_manager;
	
	private TOTorrent					torrent;
	
	private boolean						write_required;
	
	private Map							tracker_response_cache			= new HashMap();
  
		
	private Category 	category;

	private List		listeners	= new ArrayList();
	
	private AEMonitor	this_mon	= new AEMonitor( "DownloadManagerState" );


	private static DownloadManagerState
	getDownloadState(
		DownloadManagerImpl	download_manager,
		TOTorrent			original_torrent,
		TOTorrent			target_torrent )
	
		throws TOTorrentException
	{
		byte[]	hash	= target_torrent.getHash();
		
		DownloadManagerStateImpl	res	= null;
		
		try{
			class_mon.enter();
		
			HashWrapper	hash_wrapper = new HashWrapper( hash );
			
			res = (DownloadManagerStateImpl)state_map.get(hash_wrapper); 
			
			if ( res == null ){
			
				res = new DownloadManagerStateImpl( download_manager, target_torrent );
									
				state_map.put( hash_wrapper, res );
				
			}else{
				
					// if original state was created without a download manager, 
					// bind it to this one
				
				if ( res.getDownloadManager() == null && download_manager != null ){
					
					res.setDownloadManager( download_manager );
				}
				
				if ( original_torrent != null ){
						
					res.mergeTorrentDetails( original_torrent );
				}
			}
		}finally{
			
			class_mon.exit();
		}
				
		return( res );
	}

	
	public static DownloadManagerState
	getDownloadState(
		TOTorrent		original_torrent )
	
		throws TOTorrentException
	{
		byte[]	torrent_hash = original_torrent.getHash();
		
		// System.out.println( "getDownloadState: hash = " + ByteFormatter.encodeString(torrent_hash));
		
		TOTorrent saved_state	= null;
				
		File	saved_file = getStateFile( torrent_hash ); 
		
		if ( saved_file.exists()){
			
			try{
				saved_state = TorrentUtils.readFromFile( saved_file, true );
				
			}catch( Throwable e ){
				
				Debug.out( "Failed to load download state for " + saved_file );
			}
		}
		
			// if saved state not found then recreate from original torrent 
		
		if ( saved_state == null ){
		
			TorrentUtils.copyToFile( original_torrent, saved_file );
			
			saved_state = TorrentUtils.readFromFile( saved_file, true );
		}

		return( getDownloadState( null, original_torrent, saved_state ));
	}
	
	protected static DownloadManagerState
	getDownloadState(
		DownloadManagerImpl	download_manager,
		String				torrent_file,
		byte[]				torrent_hash )
	
		throws TOTorrentException
	{
		// System.out.println( "getDownloadState: hash = " + (torrent_hash==null?"null":ByteFormatter.encodeString(torrent_hash) + ", file = " + torrent_file ));

		TOTorrent	original_torrent	= null;
		TOTorrent 	saved_state			= null;
		
			// first, if we already have the hash then see if we can load the saved state
		
		if ( torrent_hash != null ){
			
			File	saved_file = getStateFile( torrent_hash ); 
		
			if ( saved_file.exists()){
				
				try{
					saved_state = TorrentUtils.readFromFile( saved_file, true );
					
				}catch( Throwable e ){
					
					Debug.out( "Failed to load download state for " + saved_file );
				}
			}
		}
		
			// if saved state not found then recreate from original torrent if required
		
		if ( saved_state == null ){
		
			original_torrent = TorrentUtils.readFromFile( new File(torrent_file), true );
			
			torrent_hash = original_torrent.getHash();
			
			File	saved_file = getStateFile( torrent_hash ); 
			
			if ( saved_file.exists()){
				
				try{
					saved_state = TorrentUtils.readFromFile( saved_file, true );
					
				}catch( Throwable e ){
					
					Debug.out( "Failed to load download state for " + saved_file );
				}
			}
			
			if ( saved_state == null ){
						
					// we must copy the torrent as we want one independent from the
					// original (someone might still have references to the original
					// and do stuff like write it somewhere else which would screw us
					// up)
				
				TorrentUtils.copyToFile( original_torrent, saved_file );
				
				saved_state = TorrentUtils.readFromFile( saved_file, true );
			}
		}

		return( getDownloadState( download_manager, original_torrent, saved_state ));
	}
	
	protected static File
	getStateFile(
		byte[]		torrent_hash )
	{
		return( new File( ACTIVE_DIR, ByteFormatter.encodeString( torrent_hash ) + ".dat" ));
	}
	
	protected
	DownloadManagerStateImpl(
		DownloadManagerImpl	_download_manager,
		TOTorrent			_torrent )
	{
		download_manager	= _download_manager;
		torrent				= _torrent;
		
			// sanity check on additional attribute types
		
		String[]	map_types = { RESUME_KEY, TRACKER_CACHE_KEY, ATTRIBUTE_KEY };
		
		for (int i=0;i<map_types.length;i++){
			
			String	map_type = map_types[i];
			
			Object	attribute_key = torrent.getAdditionalProperty( map_type );
			
			if ( attribute_key != null && !( attribute_key instanceof Map )){
			
				Debug.out( "Invalid state entry type for '" + map_type + "'" );
				
				torrent.removeAdditionalProperty( map_type );
			}
		}
		
			// get initial values
		
		tracker_response_cache	= (Map)torrent.getAdditionalMapProperty( TRACKER_CACHE_KEY );
		
		if ( tracker_response_cache == null ){
			
			tracker_response_cache	= new HashMap();
		}
		
		
        String cat_string = getStringAttribute( AT_CATEGORY );

        if ( cat_string != null ){
        	
        	Category cat = CategoryManager.getCategory( cat_string );
        	
        	if ( cat != null ){
        		
        		setCategory( cat );
        	}
        }
	}
	
	public DownloadManager
	getDownloadManager()
	{
		return( download_manager );
	}
	
	protected void
	setDownloadManager(
		DownloadManagerImpl		dm )
	{
		download_manager	= dm;
	}
	
	public void
	clearTrackerResponseCache()
	{
		setTrackerResponseCache( new HashMap());
	}
	
	public Map
	getTrackerResponseCache()
	{
		return( tracker_response_cache );
	}
	
	public void
	setTrackerResponseCache(
		Map		value )
	{
		try{
			this_mon.enter();
		
			// System.out.println( "setting download state/tracker cache for '" + new String(torrent.getName()));

			boolean	changed = !BEncoder.mapsAreIdentical( value, tracker_response_cache );
		
			if ( changed ){
				
				write_required	= true;
				
				tracker_response_cache	 = value;
		
				torrent.setAdditionalMapProperty( TRACKER_CACHE_KEY, tracker_response_cache );
			}	
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	/*
	protected boolean
	mergeTrackerResponseCache(
		DownloadManagerStateImpl	other_state )
	{
		Map  other_cache	= other_state.getTrackerResponseCache();

		if ( other_cache != null && other_cache.size() > 0 ){
						
			Map merged_cache = TRTrackerUtils.mergeResponseCache( tracker_response_cache, other_cache );
		
			setTrackerResponseCache( merged_cache );
	  		
	  		return( true );
		}
		
		return( false );
	}
	*/
	
	public Map
	getResumeData()
	{
		return( torrent.getAdditionalMapProperty(RESUME_KEY));
	}
	
	public void
	clearResumeData()
	{
		setResumeData( null );
	}
	
	public void
	setResumeData(
		Map	data )
	{
		try{
			this_mon.enter();
		
			// System.out.println( "setting download state/resume data for '" + new String(torrent.getName()));

			if ( data == null ){
				
				torrent.removeAdditionalProperty( RESUME_KEY );
				
			}else{
				
				torrent.setAdditionalMapProperty(RESUME_KEY, data);
			}
			
			write_required	= true;
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	public TOTorrent
	getTorrent()
	{
		return( torrent );
	}
	
	public void
	save()
	{
 		boolean	do_write;
  		
  		try{
  			this_mon.enter();
  		
  			do_write	= write_required;
  			
  			write_required	= false;
  			
  		}finally{
  			
  			this_mon.exit();
  		}
  		
	  	if ( do_write ){
	  				  	
	  		try{
	  			// System.out.println( "writing download state for '" + new String(torrent.getName()));
	  		
	  			LGLogger.log( "Saving state for download '" + TorrentUtils.getLocalisedName( torrent ) + "'" );
				
	  			TorrentUtils.writeToFile( torrent, true );
	  		
	  		}catch( Throwable e ){
	  		
	  			Debug.printStackTrace( e );
	  		}
	  	}else{
	  		
	  		// System.out.println( "not writing download state for '" + new String(torrent.getName()));
	  	}
	}
	
	public void
	delete()
	{
		try{
			class_mon.enter();

			state_map.remove( torrent.getHashWrapper());
			
	        TorrentUtils.delete( torrent );
	        
	    }catch( TOTorrentException e ){
	    	
	    	Debug.printStackTrace( e );
	   
		}finally{
			
			class_mon.exit();
		}
	}
	
	protected void
	mergeTorrentDetails(
		TOTorrent	other_torrent )
	{
		try{		
			boolean	write = TorrentUtils.mergeAnnounceURLs( other_torrent, torrent );
					
			// System.out.println( "DownloadManagerState:mergeTorrentDetails -> " + write );
			
			if ( write ){
				
				save();
				
				if ( download_manager != null ){
					
					TRTrackerClient	client = download_manager.getTrackerClient();

					if ( client != null ){
										
						// pick up any URL changes
					
						client.resetTrackerUrl( false );
					}
				}
			}
		}catch( Throwable e ){
				
			Debug.printStackTrace( e );
		}
	}
	
	
	public void
	setAttribute(
		String		name,
		String		value )
	{
		if ( name.equals( AT_CATEGORY )){
			
			if ( value == null ){
				
				setCategory( null );
				
			}else{
				Category	cat = CategoryManager.getCategory( value );
			
				if ( cat == null ){
				
					cat = CategoryManager.createCategory( value );
					
				}
								
				setCategory( cat );
			}
		}else{
			
			setStringAttribute( name, value );
		}
	}
	
	public String
	getAttribute(
		String		name )
	{
		if ( name.equals( AT_CATEGORY )){
			
			Category	cat = getCategory();
			
			if ( cat == null ){
				
				return( null );
			}
			
			if ( cat == CategoryManager.getCategory( Category.TYPE_UNCATEGORIZED )){
				
				return( null );
			}
			
			return( cat.getName());
		}else{
			
			return( getStringAttribute( name ));
		}
	}
	
	public 
	Category 
	getCategory() 
	{
	    return category;
	}
		
	public void 
	setCategory(
		Category 	cat ) 
	{
		if ( cat == category ){
			
			return;
		}
	  
		if (cat != null && cat.getType() != Category.TYPE_USER){
	    
			cat = null;
		}
		
		Category oldCategory = (category == null)?CategoryManager.getCategory(Category.TYPE_UNCATEGORIZED):category;
				
		category = cat;
	  
		if (oldCategory != null ){
			
			oldCategory.removeManager( this );
  		}
		
		if (category != null ){
			
			category.addManager( this );
		}
  	
		if ( category != null && category.getType() == Category.TYPE_USER ){
			
			setStringAttribute( AT_CATEGORY, category.getName());
			
		}else{
			
			setStringAttribute( AT_CATEGORY, null );
		}
	}
		
	
	protected String
	getStringAttribute(
		String	attribute_name )
	{
		Map	attributes = torrent.getAdditionalMapProperty( ATTRIBUTE_KEY );
		
		if ( attributes == null ){
			
			return( null );
		}
		
		byte[]	bytes = (byte[])attributes.get( attribute_name );
		
		if ( bytes == null ){
			
			return( null );
		}
		
		try{
			return( new String( bytes, Constants.DEFAULT_ENCODING ));
			
		}catch( UnsupportedEncodingException e ){
			
			Debug.printStackTrace(e);
			
			return( null );
		}
	}
	
	protected void
	setStringAttribute(
		final String	attribute_name,
		final String	attribute_value )
	{
		Map	attributes = torrent.getAdditionalMapProperty( ATTRIBUTE_KEY );
		
		if ( attributes == null ){
			
			if ( attribute_value == null ){
			
					// nothing to do, no attributes and we're removing a value
				
				return;
			}
			
			attributes = new HashMap();
			
			torrent.setAdditionalMapProperty( ATTRIBUTE_KEY, attributes );
		}
	
		boolean	changed	= false;
		
		if ( attribute_value == null ){
			
			if ( attributes.containsKey( attribute_name )){
			
				attributes.remove( attribute_name );
			
				changed	= true;
			}
		}else{
		
			try{
				byte[]	existing_bytes = (byte[])attributes.get( attribute_name );
				
				byte[]	new_bytes = attribute_value.getBytes( Constants.DEFAULT_ENCODING );
				
				if ( 	existing_bytes == null || 
						!Arrays.equals( existing_bytes, new_bytes )){
				
					attributes.put( attribute_name, new_bytes );
					
					changed	= true;
				}
				
			}catch( UnsupportedEncodingException e ){
				
				Debug.printStackTrace(e);
			}
		}
		
		if ( changed ){
			
			write_required	= true;
			
			for (int i=0;i<listeners.size();i++){
				
				try{
					((DownloadManagerStateListener)listeners.get(i)).stateChanged(
						this,
						new DownloadManagerStateEvent()
						{
							public int
							getType()
							{
								return( DownloadManagerStateEvent.ET_ATTRIBUTE_CHANGED );
							}
							
							public Object
							getData()
							{
								return( attribute_name );
							}
						});
					
				}catch( Throwable e ){
					
					Debug.printStackTrace(e);
				}
			}
		}
	}
	
	public static DownloadManagerState
	getDownloadState(
		DownloadManager	dm )
	{
		return( new nullState(dm));
	}
	
	public void
	addListener(
		DownloadManagerStateListener	l )
	{
		listeners.add( l );
	}
	
	public void
	removeListener(
		DownloadManagerStateListener	l )
	{
		listeners.remove(l);
	}
	
	protected static class
	nullState
		implements DownloadManagerState
	{
		protected DownloadManager		download_manager;
		
		protected
		nullState(
			DownloadManager	_dm )
		{
			download_manager = _dm;
		}
		
		public TOTorrent
		getTorrent()
		{
			return( null );
		}
		
		public DownloadManager
		getDownloadManager()
		{
			return( download_manager );
		}
		
		public void
		clearResumeData()
		{
		}
		
		public Map
		getResumeData()
		{
			return( new HashMap());
		}
		
		public void
		setResumeData(
			Map	data )
		{
		}
		
		public void
		clearTrackerResponseCache()
		{
		}
		
		public Map
		getTrackerResponseCache()
		{
			return( new HashMap());
		}

		public void
		setTrackerResponseCache(
			Map		value )
		{
		}
		
		public void
		setAttribute(
			String		name,
			String		value )
		{
		}			
		
		public String
		getAttribute(
			String		name )
		{
			return( null );
		}
		
		public Category 
		getCategory()
		{
			return( null );
		}
		
		public void 
		setCategory(
			Category cat )
		{
		}
		
		public void
		save()
		{	
		}
		
		public void
		delete()
		{
		}
		
		public void
		addListener(
			DownloadManagerStateListener	l )
		{}
		
		public void
		removeListener(
			DownloadManagerStateListener	l )
		{}
	}
}