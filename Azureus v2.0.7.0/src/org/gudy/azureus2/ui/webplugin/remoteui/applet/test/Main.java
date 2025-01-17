/*
 * File    : Main.java
 * Created : 28-Jan-2004
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

package org.gudy.azureus2.ui.webplugin.remoteui.applet.test;

/**
 * @author parg
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.gudy.azureus2.plugins.*;

import org.gudy.azureus2.ui.webplugin.remoteui.applet.*;


public class 
Main
	implements Plugin
{
	public void 
	initialize(
		PluginInterface _plugin_interface )
	
		throws PluginException
	{	
		try{
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		}catch( Exception e ){
			
			e.printStackTrace();
		}
		
		final JFrame	frame = new JFrame( "Azureus Swing UI" );

		frame.addWindowListener(
			new WindowAdapter()
			{
				public void
				windowClosing(
					WindowEvent	ev )
				{
					System.exit(0);
				}
			});
		
		frame.setSize(600,300);

		Container cont = frame.getContentPane();
		
		cont.setLayout( new BorderLayout());
		
		RemoteUIMainPanel	panel = new RemoteUIMainPanel( _plugin_interface.getDownloadManager());
		
		cont.add( panel );	
		
		panel.addListener(
				new RemoteUIMainPanelListener()
				{
					public void
					refresh()
					{
					}
					
					public void
					error(
						final Throwable 		e )
					{
						SwingUtilities.invokeLater(
								new Runnable()
								{
									public void
									run()
									{
										JOptionPane.showMessageDialog( 
												frame, 
												e.toString(),
												"Error Occurred",  
												JOptionPane.ERROR_MESSAGE );
									}
								});
					}
				});
		frame.setVisible(true);
	}		
}