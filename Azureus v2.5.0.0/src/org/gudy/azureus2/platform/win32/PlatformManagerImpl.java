/*
 * Created on 18-Apr-2004
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

package org.gudy.azureus2.platform.win32;

/**
 * @author parg
 *
 */

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.util.AEMonitor;
import org.gudy.azureus2.core3.util.Constants;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.SystemProperties;
import org.gudy.azureus2.platform.PlatformManager;
import org.gudy.azureus2.platform.PlatformManagerCapabilities;
import org.gudy.azureus2.platform.PlatformManagerListener;
import org.gudy.azureus2.platform.win32.access.AEWin32Access;
import org.gudy.azureus2.platform.win32.access.AEWin32AccessListener;
import org.gudy.azureus2.platform.win32.access.AEWin32Manager;
import org.gudy.azureus2.plugins.platform.PlatformManagerException;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class 
PlatformManagerImpl
	implements PlatformManager, AEWin32AccessListener
{
	public static final int			RT_NONE		= 0;
	public static final int			RT_AZ 		= 1;
	public static final int			RT_OTHER 	= 2;
	
	public static final String					DLL_NAME = "aereg";
	
	public static final String				NEW_MAIN_ASSOC	= "Azureus";
	public static final String				OLD_MAIN_ASS0C	= "BitTorrent";
	
	private static boolean					initialising;
	private static boolean					init_tried;
	
	private static PlatformManagerImpl		singleton;
	private static AEMonitor				class_mon	= new AEMonitor( "PlatformManager");

	private final Set capabilitySet = new HashSet();

	private List	listeners = new ArrayList();
	
	public static PlatformManagerImpl
	getSingleton()
	
		throws PlatformManagerException	
	{
		try{
			class_mon.enter();
		
			if ( singleton != null ){
				
				return( singleton );
			}
			
			try{	
				if ( initialising ){
					
					System.err.println( "PlatformManager: recursive entry during initialisation" );
				}
				
				initialising	= true;
				
				if ( !init_tried ){
					
					init_tried	= true;
					
					try{
						singleton	= new PlatformManagerImpl();
						
							// gotta separate this so that a recursive call due to config access during
							// patching finds the singleton 
						
						singleton.applyPatches();
						
					}catch( PlatformManagerException e ){
						
						throw( e );
						
					}catch( Throwable e ){
												
						if ( e instanceof PlatformManagerException ){
							
							throw((PlatformManagerException)e);
						}
						
						throw( new PlatformManagerException( "Win32Platform: failed to initialise", e ));
					}
				}
			}finally{
				
				initialising	= false;
			}
			
			return( singleton );
			
		}finally{
			
			class_mon.exit();
		}
	}
	
	protected AEWin32Access		access;

	protected String			app_exe_name;
	protected File				az_exe;
	protected boolean			az_exe_checked;

	protected
	PlatformManagerImpl()
	
		throws PlatformManagerException
	{
		access	= AEWin32Manager.getAccessor( true );
		
		access.addListener( this );
		
		app_exe_name	= SystemProperties.getApplicationName() + ".exe";
		
        initializeCapabilities();
	}

    private void
    initializeCapabilities()
    {
    	if ( access.isEnabled()){
    		
	        capabilitySet.add(PlatformManagerCapabilities.CreateCommandLineProcess);
	        capabilitySet.add(PlatformManagerCapabilities.GetUserDataDirectory);
	        capabilitySet.add(PlatformManagerCapabilities.RecoverableFileDelete);
	        capabilitySet.add(PlatformManagerCapabilities.RegisterFileAssociations);
	        capabilitySet.add(PlatformManagerCapabilities.ShowFileInBrowser);
	        capabilitySet.add(PlatformManagerCapabilities.GetVersion);
	        capabilitySet.add(PlatformManagerCapabilities.SetTCPTOSEnabled);
	        
	        
	        if ( 	Constants.compareVersions( access.getVersion(), "1.11" ) >= 0 &&
	        		!Constants.isWindows9598ME ){
	        	
	            capabilitySet.add(PlatformManagerCapabilities.CopyFilePermissions);
	            
	        }
	        
	        if ( 	Constants.compareVersions( access.getVersion(), "1.12" ) >= 0 ){
	        	
	            capabilitySet.add(PlatformManagerCapabilities.TestNativeAvailability);
	        }
	        
    	}else{
    		
    			// disabled -> only available capability is that to get the version
    			// therefore allowing upgrade
    		
	        capabilitySet.add(PlatformManagerCapabilities.GetVersion);
    	}
    }

    protected void
	applyPatches()
	{
		try{
			File	exe_loc = getApplicationEXELocation();
			
			String	az_exe_string = exe_loc.toString();
			
			//int	icon_index = getIconIndex();
			
			String	current = 
				access.readStringValue(
					AEWin32Access.HKEY_CLASSES_ROOT,
					NEW_MAIN_ASSOC + "\\DefaultIcon",
					"" );

			//System.out.println( "current = " + current );
			
			String	target = az_exe_string + "," + getIconIndex();
			
			//System.out.println( "target = " + target );
			
				// only patch if Azureus.exe in there
			
			if ( current.indexOf( app_exe_name ) != -1 && !current.equals(target)){
				
				access.writeStringValue( 	
						AEWin32Access.HKEY_CLASSES_ROOT,
						NEW_MAIN_ASSOC + "\\DefaultIcon",
						"",
						target );
			}
		}catch( Throwable e ){
			
			//e.printStackTrace();
		}
		
			// one off fix of permissions in app dir
		
		if ( 	hasCapability( PlatformManagerCapabilities.CopyFilePermissions ) &&
				!COConfigurationManager.getBooleanParameter( "platform.win32.permfixdone2", false )){

			try{
				
				String	str = SystemProperties.getApplicationPath();
				
				if ( str.endsWith(File.separator)){
					
					str = str.substring(0,str.length()-1);
				}
				
				fixPermissions( new File( str ), new File( str ));
				
			}catch( Throwable e ){
				
			}finally{
				
				COConfigurationManager.setParameter( "platform.win32.permfixdone2", true );
			}
		}
	}
	
    protected void
    fixPermissions(
    	File		parent,
    	File		dir )
    
    	throws PlatformManagerException
    {
    	File[]	files = dir.listFiles();
    	
    	if ( files == null ){
    		
    		return;
    	}
    	
    	for (int i=0;i<files.length;i++){
    		
    		File	file = files[i];
    		   	   		
    		if ( file.isFile()){
    			
    			copyFilePermissions( parent.getAbsolutePath(), file.getAbsolutePath());
    		}
    	}
    }
    
	protected int
	getIconIndex()
	
		throws PlatformManagerException
	{
		/*
		File	exe_loc = getAureusEXELocation();
		
		long	size = exe_loc.length();
		
		boolean	old_exe = size < 250000;
		
		return( old_exe?0:1);
		*/
		
		// weird, seems like it should be 0 for old and new
		
		return( 0 );
	}
	
	public String
	getVersion()
	{
		return( access.getVersion());
	}
	
	protected File
	getApplicationEXELocation()
		throws PlatformManagerException
	{
		if ( az_exe == null ){
			
			try{
			
				String az_home;
				
				try{
					az_home = access.getApplicationInstallDir( SystemProperties.getApplicationName());
					
					az_exe = new File( az_home + File.separator + app_exe_name ).getAbsoluteFile();
	
					if ( !az_exe.exists()){
						
						throw( new PlatformManagerException( app_exe_name + " not found in " + az_home + ", please re-install"));
					}
				}catch( Throwable e ){
					
						//hmmm, well let's try the app dir
					
					az_home = SystemProperties.getApplicationPath();		
					
					az_exe = new File( az_home + File.separator + app_exe_name ).getAbsoluteFile();
				}
				
				if ( !az_exe.exists()){
					
					String	msg = app_exe_name + " not found in " + az_home + " - can't check file associations. Please re-install " + SystemProperties.getApplicationName();
					
					az_exe = null;
					
					if (!az_exe_checked){
					
						Logger.log(new LogAlert(LogAlert.UNREPEATABLE, LogAlert.AT_WARNING,
								msg));
					}
					
					throw( new PlatformManagerException( msg ));
				}
			}finally{
				
				az_exe_checked	= true;
			}
		}
		
		return( az_exe );
	}
	
	public int
	getPlatformType()
	{
		return( PT_WINDOWS );
	}
	
	public String
	getUserDataDirectory()
	
		throws PlatformManagerException
	{
		try{
			return( access.getUserAppData());
			
		}catch( Throwable e ){
			
			throw( new PlatformManagerException( "Failed to read registry details", e ));
		}		
	}
	
	public File
	getLocation(
		long	location_id )
	
		throws PlatformManagerException
	{
	    if ( location_id == LOC_USER_DATA ){
	    	
	    	return(new File(getUserDataDirectory()));
	    	
	    }else if ( location_id == LOC_MUSIC ){
	    	
	    	try{
		    	return( new File(
		    		access.readStringValue(
		    			AEWin32Access.HKEY_CURRENT_USER,
		    			"software\\microsoft\\windows\\currentversion\\explorer\\shell folders",
		    			"My Music" )));
		    	
	    	}catch( Throwable e ){
	    		
				throw( new PlatformManagerException( "Failed to read registry details", e ));
	    	}
	    }else{
	    	
	    	return( null );
	    }
	}
	
	public String
	getApplicationCommandLine()
	{
		try{
			return( getApplicationEXELocation().toString());
			
		}catch( Throwable e ){
			
			return( null );
		}
	}
	
	public boolean
	isApplicationRegistered()
	
		throws PlatformManagerException
	{
			// all this stuff needs the exe location so bail out early if unavailable
		
		getApplicationEXELocation();
		
		try{
				// always trigger magnet reg here if not owned so old users get it...
			
			if ( getAdditionalFileTypeRegistrationDetails( "Magnet", ".magnet" ) == RT_NONE ){
		
				registerMagnet();
			}
		}catch( Throwable e ){
			
			Debug.printStackTrace(e);
		}
		
		if ( isAdditionalFileTypeRegistered( OLD_MAIN_ASS0C, ".torrent" )){
			
			unregisterAdditionalFileType( OLD_MAIN_ASS0C, ".torrent" );
			
			registerAdditionalFileType( NEW_MAIN_ASSOC, "Azureus Torrent", ".torrent", "application/x-bittorrent" );
		}
		
		boolean	reg = isAdditionalFileTypeRegistered( NEW_MAIN_ASSOC, ".torrent" );
		
			// one off auto registration on new install
		
		if ( !reg && !COConfigurationManager.getBooleanParameter( "platform.win32.autoregdone", false )){
			
			registerAdditionalFileType( NEW_MAIN_ASSOC, "Azureus Torrent", ".torrent", "application/x-bittorrent" );

			COConfigurationManager.setParameter( "platform.win32.autoregdone", true );
			
			reg	= true;
		}
		
		return( reg );
	}
	
	public boolean
	isAdditionalFileTypeRegistered(
		String		name,
		String		type )
	
		throws PlatformManagerException
	{
		return( getAdditionalFileTypeRegistrationDetails( name, type ) == RT_AZ );
	}
	
	public int
	getAdditionalFileTypeRegistrationDetails(
		String		name,
		String		type )
	
		throws PlatformManagerException
	{

		String	az_exe_str;
		
		try{
			az_exe_str = getApplicationEXELocation().toString();
		
		}catch( Throwable e ){
			
			return( RT_NONE );
		}
		
		try{
			String	test1 = 
				access.readStringValue( 	
					AEWin32Access.HKEY_CLASSES_ROOT,
					name + "\\shell\\open\\command",
					"" );
			
			if ( !test1.equals( "\"" + az_exe_str + "\" \"%1\"" )){
				
				return( test1.length() ==0?RT_NONE:RT_OTHER );
			}
			
				// MRU list is just that, to remove the "always open with" we need to kill
				// the "application" entry, if it exists
			
			try{
				String	always_open_with = 
					access.readStringValue( 
						AEWin32Access.HKEY_CURRENT_USER,
						"Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\" + type,
						"Application" );
				
				//System.out.println( "mru_list = " + mru_list );

				if ( always_open_with.length() > 0 ){
				
					// AZ is default so if this entry exists it denotes another (non-AZ) app
					
					return( RT_OTHER );
				}
			}catch( Throwable e ){
				
				// e.printStackTrace();
				
				// failure means things are OK
			}
			
			/*
			try{
				String	mru_list = 
					access.readStringValue( 
						AEWin32Access.HKEY_CURRENT_USER,
						"Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\.torrent\\OpenWithList",
						"MRUList" );
				
				//System.out.println( "mru_list = " + mru_list );

				if ( mru_list.length() > 0 ){
				
					String	mru = 
						access.readStringValue( 
							AEWin32Access.HKEY_CURRENT_USER,
							"Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\.torrent\\OpenWithList",
							"" + mru_list.charAt(0) );
					
					//System.out.println( "mru = " + mru );
					
					return( mru.equalsIgnoreCase(app_exe_name));
				}
			}catch( Throwable e ){
				
				// e.printStackTrace();
				
				// failure means things are OK
			}
			*/
			
			return( RT_AZ );
			
		}catch( Throwable e ){
			
			if ( 	e.getMessage() == null || 
					e.getMessage().indexOf("RegOpenKey failed") == -1 ){
				
				Debug.printStackTrace( e );
			}

			return( RT_NONE );
		}
	}
	
	public void
	registerApplication()
	
		throws PlatformManagerException
	{
		registerMagnet();
		
		registerAdditionalFileType( NEW_MAIN_ASSOC, "Azureus Torrent", ".torrent", "application/x-bittorrent" );
	}
	
	protected void
	registerMagnet()
	{
		try{
			registerAdditionalFileType( 
				"Magnet", 
				"Magnet URI", 
				".magnet", 
				"application/x-magnet",
				true );
			
		}catch( Throwable e ){
			
			Debug.printStackTrace(e);
		}
	}
	public void
	registerAdditionalFileType(
		String		name,				// e.g. "Azureus"
		String		description,		// e.g. "BitTorrent File"
		String		type,				// e.g. ".torrent"
		String		content_type )		// e.g. "application/x-bittorrent"
		
		throws PlatformManagerException
	{
		registerAdditionalFileType( name, description, type, content_type, false );
	}
	
	public void
	registerAdditionalFileType(
		String		name,				
		String		description,		
		String		type,				
		String		content_type,
		boolean		url_protocol)		
		
		throws PlatformManagerException
	{
		// 	WriteRegStr HKCR ".torrent" "" "Azureus"
		// 	WriteRegStr HKCR "Azureus" "" "Azureus Torrent"
		// 	WriteRegStr HKCR "Azureus\shell" "" "open"
		// 	WriteRegStr HKCR "Azureus\DefaultIcon" "" $INSTDIR\Azureus.exe,1
		// 	WriteRegStr HKCR "Azureus\shell\open\command" "" '"$INSTDIR\Azureus.exe" "%1"'
		// 	WriteRegStr HKCR "Azureus\Content Type" "" "application/x-bittorrent"
		

		try{
			String	az_exe_string	= getApplicationEXELocation().toString();
			
			unregisterAdditionalFileType( name, type );

			access.writeStringValue( 	
					AEWin32Access.HKEY_CLASSES_ROOT,
					type,
					"",
					name );
		
			access.writeStringValue( 	
					AEWin32Access.HKEY_CLASSES_ROOT,
					name,
					"",
					description );
			
			access.writeStringValue( 	
					AEWin32Access.HKEY_CLASSES_ROOT,
					name + "\\shell",
					"",
					"open" );
			
			access.writeStringValue( 	
					AEWin32Access.HKEY_CLASSES_ROOT,
					name + "\\DefaultIcon",
					"",
					az_exe_string + "," + getIconIndex());
			
			access.writeStringValue( 	
					AEWin32Access.HKEY_CLASSES_ROOT,
					name + "\\shell\\open\\command",
					"",
					"\"" + az_exe_string + "\" \"%1\"" );
					
			access.writeStringValue( 	
					AEWin32Access.HKEY_CLASSES_ROOT,
					name + "\\Content Type" ,
					"",
					content_type );
			
			if ( url_protocol ){
				
				access.writeStringValue( 	
						AEWin32Access.HKEY_CLASSES_ROOT,
						name,
						"URL Protocol",
						"" );
			}
			
		}catch( PlatformManagerException e ){
			
			throw(e );
			
		}catch( Throwable e ){
			
			throw( new PlatformManagerException( "Failed to write registry details", e ));
		}
	}
	
	public void
	unregisterAdditionalFileType(
		String		name,				// e.g. "Azureus"
		String		type )				// e.g. ".torrent"
		
		throws PlatformManagerException
	{
		try{
			try{
		
				access.deleteValue( 	
					AEWin32Access.HKEY_CURRENT_USER,
					"Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\" + type,
					"Application" );
				
			}catch( Throwable e ){
				
				// e.printStackTrace();
			}
			
			try{
				access.deleteKey( 	
					AEWin32Access.HKEY_CLASSES_ROOT,
					type );
				
			}catch( Throwable e ){
				
				// Debug.printStackTrace( e );
			}
			
			try{
				access.deleteKey( 	
					AEWin32Access.HKEY_CLASSES_ROOT,
					name,
					true );
				
			}catch( Throwable e ){
				
				// Debug.printStackTrace( e );
			}
			
		}catch( Throwable e ){
			
			throw( new PlatformManagerException( "Failed to delete registry details", e ));
		}
	}
	
	public void
	createProcess(
		String	command_line,
		boolean	inherit_handles )
	
		throws PlatformManagerException
	{
		try{
			access.createProcess( command_line, inherit_handles );
			
		}catch( Throwable e ){
			
			throw( new PlatformManagerException( "Failed to create process", e ));
		}	
	}
	
	public void
    performRecoverableFileDelete(
		String	file_name )
	
		throws PlatformManagerException
	{
		try{
			access.moveToRecycleBin( file_name );
			
		}catch( Throwable e ){
			
			throw( new PlatformManagerException( "Failed to move file", e ));
		}
	}

	public void
	setTCPTOSEnabled(
		boolean		enabled )
		
		throws PlatformManagerException
	{
		try{
			access.writeWordValue( 	
					AEWin32Access.HKEY_LOCAL_MACHINE,
					"System\\CurrentControlSet\\Services\\Tcpip\\Parameters",
					"DisableUserTOSSetting",
					enabled?0:1);
			
		}catch( Throwable e ){
			
			throw( new PlatformManagerException( "Failed to write registry details", e ));
		}		
	}

	public void
    copyFilePermissions(
		String	from_file_name,
		String	to_file_name )
	
		throws PlatformManagerException
	{
		try{
			access.copyFilePermissions( from_file_name, to_file_name );
			
		}catch( Throwable e ){
			
			throw( new PlatformManagerException( "Failed to copy file permissions", e ));
		}		
	}
	
    /**
     * {@inheritDoc}
     */
    public void showFile(String file_name)

            throws PlatformManagerException
    {
        try
        {
        	File file = new File(file_name);
        	Runtime.getRuntime().exec(
        			new String[] { "explorer.exe",
        					file.isDirectory() ? "/e," : "/e,/select,",
        							"\"" + file_name + "\"" });
        }
        catch (IOException e)
        {
            throw new PlatformManagerException("Failed to show file " + file_name, e);
        }
    }

	public boolean
	testNativeAvailability(
		String	name )
	
		throws PlatformManagerException
	{
		if ( !hasCapability( PlatformManagerCapabilities.TestNativeAvailability )){
			
			throw new PlatformManagerException("Unsupported capability called on platform manager");
		}
		
		try{
			return( access.testNativeAvailability( name ));
			
		}catch( Throwable e ){
			
			throw( new PlatformManagerException( "Failed to test availability", e ));
		}
	}
	
    /**
     * {@inheritDoc}
     */
    public boolean
    hasCapability(
            PlatformManagerCapabilities capability)
    {
        return capabilitySet.contains(capability);
    }

    /**
     * Does nothing
     */
    public void dispose()
    {
    }
    
	public void
	eventOccurred(
		int		type )
	{
		int	t_type;
		
		if ( type == AEWin32AccessListener.ET_SHUTDOWN ){
			
			t_type = PlatformManagerListener.ET_SHUTDOWN;
			
		}else{
			
			return;
		}
		
		if ( t_type != -1 ){
			
			for (int i=0;i<listeners.size();i++){
				
				try{
					((PlatformManagerListener)listeners.get(i)).eventOccurred( t_type );
					
				}catch( Throwable e ){
					
					Debug.printStackTrace(e);
				}
			}
		}
	}
	
    public void
    addListener(
    	PlatformManagerListener		listener )
    {
    	listeners.add( listener );
    }
    
    public void
    removeListener(
    	PlatformManagerListener		listener )
    {
    	listeners.remove( listener );
    }
}
