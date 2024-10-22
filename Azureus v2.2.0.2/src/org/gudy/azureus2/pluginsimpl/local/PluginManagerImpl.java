/*
 * File    : PluginManagerImpl.java
 * Created : 14-Dec-2003
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

package org.gudy.azureus2.pluginsimpl.local;

/**
 * @author parg
 *
 */

import java.util.*;

import org.gudy.azureus2.core3.util.AEMonitor;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.plugins.installer.PluginInstaller;
import org.gudy.azureus2.pluginsimpl.local.installer.*;

import com.aelitis.azureus.core.*;

public class 
PluginManagerImpl 
	extends PluginManager
{
	protected static boolean	running		= false;
	
	protected static PluginManagerImpl	singleton;
	protected static AEMonitor			class_mon	= new AEMonitor( "PluginManager");

	protected static AzureusCore		azureus_core;
	
	protected static PluginManagerImpl
	getSingleton(
		PluginInitializer	pi )
	{
		try{
			class_mon.enter();
			
			if ( singleton == null ){
				
				singleton = new PluginManagerImpl( pi );
			}
			
			return( singleton );
			
		}finally{
			
			class_mon.exit();
		}
	}
	
	public static PluginManager
	startAzureus(
		int			ui_type,
		Properties	properties )
	{
		try{
			class_mon.enter();
			
			if ( running ){
				
				throw( new RuntimeException( "Azureus is already running"));
			}
			
			running	= true;
			
		}finally{
			
			class_mon.exit();
		}
		
			// there's a small window here when an immediate "stop" wouldn't work coz
			// this code would carry on after the stop and start. However, can't easily
			// fix this here...
		
		if ( ui_type == PluginManager.UI_NONE ){
		
				// can't invoke directly as the ui.common stuff isn't part of the core distribution
				// org.gudy.azureus2.ui.common.Main.main( new String[]{"--ui=console"});
			
			try{
				
				azureus_core = AzureusCoreFactory.create();
				
				azureus_core.addLifecycleListener(
					new AzureusCoreLifecycleAdapter()
					{
						public boolean
						stopRequested(
							AzureusCore		core )
						
							throws AzureusCoreException
						{
							core.stop();
							
							return( true );
						}						
					});
				
				azureus_core.start();
				
			}catch( Throwable e ){
				
				Debug.printStackTrace( e );
				
					// some idiot (me) forgot to add the exception to the i/f and now we
					// can't add it as is stuffs existing plugins...
				
				throw( new RuntimeException( "Azureus failed to start", e ));
			}
		}else if ( ui_type == PluginManager.UI_SWT ){
				
			if ( properties != null ){
				
				String	mi = (String)properties.get( PluginManager.PR_MULTI_INSTANCE );
				
				if ( mi != null && mi.equalsIgnoreCase("true")){
					
					System.setProperty( org.gudy.azureus2.ui.swt.Main.PR_MULTI_INSTANCE, "true" );
				}
			}
			
			org.gudy.azureus2.ui.swt.Main.main(new String[0]);
		}
		
		if ( azureus_core == null ){
			
			throw( new RuntimeException( "Azureus core failed to initialise" ));
		}
		
		return( azureus_core.getPluginManager());
	}
	
	public static void
	stopAzureus()
	
		throws PluginException
	{
		try{
			class_mon.enter();
		
			if ( !running ){
				
				throw( new RuntimeException( "Azureus is not running"));
			}
						
			try{
				azureus_core.requestStop();
				
			}catch( Throwable e ){
								
				throw( new PluginException( "PluginManager: Azureus close action failed", e));
			}
	
			running	= false;
			
		}finally{
			
			class_mon.exit();
		}
	}
	
	public static void
	restartAzureus()
	
		throws PluginException
	{
		if ( !running ){
			
			throw( new RuntimeException( "Azureus is not running"));
		}
		
		try{
			azureus_core.requestRestart();
			
		}catch( Throwable e ){
							
			throw( new PluginException( "PluginManager: Azureus restart action failed", e));
		}
			
		running	= false;
	}	
	
		/**
		 * When AZ is started directly (i.e. not via a plugin) this method is called
		 * so that the running state is correctly understood
		 * @param type
		 */
	
	public static void
	setStartDetails(
		AzureusCore		_core )
	{
		azureus_core	= _core;
		
		running			= true;
	}
	
	public static void
	registerPlugin(
		Class		plugin_class )
	{
		PluginInitializer.queueRegistration( plugin_class );
	}
	
	public static void
	registerPlugin(
		Plugin		plugin,
		String		id )
	{
		PluginInitializer.queueRegistration( plugin, id );
	}
	
	public PluginInterface
	getPluginInterfaceByID(
		String		id )
	{
		PluginInterface[]	p = getPluginInterfaces();
		
		for (int i=0;i<p.length;i++){
			
			if ( p[i].getPluginID().equalsIgnoreCase( id )){
				
				return( p[i]);
			}
		}
		
		return( null );
	}
	
	public PluginInterface
	getPluginInterfaceByClass(
		Class		c )
	{
		PluginInterface[]	p = getPluginInterfaces();
		
		for (int i=0;i<p.length;i++){
			
			if ( p[i].getPlugin().getClass().equals( c )){
				
				return( p[i]);
			}
		}
		
		return( null );
	}
	
	public PluginInterface
	getPluginInterfaceByClass(
		String		class_name  )
	{
		PluginInterface[]	p = getPluginInterfaces();
		
		for (int i=0;i<p.length;i++){
			
			if ( p[i].getPlugin().getClass().getName().equals( class_name )){
				
				return( p[i]);
			}
		}
		
		return( null );
	}
	
	public PluginInterface[]
	getPluginInterfaces()
	{
		List	l = PluginInitializer.getPluginInterfaces();
		
		PluginInterface[]	res = new PluginInterface[l.size()];
		
		l.toArray(res);
		
		return( res );
	}
	
	public PluginInterface
	getDefaultPluginInterface()
	{
		return( PluginInitializer.getDefaultInterface());
	}
	
	protected PluginInitializer		pi;
	
	protected
	PluginManagerImpl(
		PluginInitializer		_pi )
	{
		pi		= _pi;
	}
	
	public PluginInterface[]
	getPlugins()
	{
		return( pi.getPlugins());
	}
	
	public void
	firePluginEvent(
		int	ev )
	{
		PluginInitializer.fireEvent( ev );
	}
	
	public PluginInstaller
	getPluginInstaller()
	{
		return( PluginInstallerImpl.getSingleton(this));
	}
}
