/*
 * File    : UtilitiesImpl.java
 * Created : 24-Mar-2004
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

package org.gudy.azureus2.pluginsimpl.local.utils;

/**
 * @author parg
 *
 */

import java.io.InputStream;
import java.io.File;
import java.nio.ByteBuffer;

import org.gudy.azureus2.platform.PlatformManager;
import org.gudy.azureus2.platform.PlatformManagerFactory;
import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.plugins.utils.*;
import org.gudy.azureus2.plugins.utils.resourcedownloader.*;
import org.gudy.azureus2.plugins.utils.security.SESecurityManager;
import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentFactory;
import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.*;
import org.gudy.azureus2.pluginsimpl.local.utils.security.*;
import org.gudy.azureus2.pluginsimpl.local.utils.xml.simpleparser.*;

import org.gudy.azureus2.core3.util.AEThread;
import org.gudy.azureus2.core3.util.Constants;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.DirectByteBuffer;
import org.gudy.azureus2.core3.util.SystemProperties;
import org.gudy.azureus2.core3.util.DirectByteBufferPool;
import org.gudy.azureus2.ui.common.UIImageRepository;

public class 
UtilitiesImpl
	implements Utilities
{
	protected PluginInterface			pi;
	
	public
	UtilitiesImpl(
		PluginInterface		_pi )
	{
		pi		= _pi;
	}
	
	public String
	getAzureusUserDir()
	{
		String	res = SystemProperties.getUserPath();
		
		if ( res.endsWith(File.separator )){
			
			res = res.substring(0,res.length()-1);
		}
		
		return( res );
	}
	
	public boolean
	isCVSVersion()
	{
		return( Constants.isCVSVersion());
	}
	
	public InputStream
	getImageAsStream(
		String	image_name )
	{
		return( UIImageRepository.getImageAsStream( image_name ));
	}
	
	public Semaphore
	getSemaphore()
	{
		return( new SemaphoreImpl());
	}
	
	public ByteBuffer
	allocateDirectByteBuffer(
		int		size )
	{
		return( DirectByteBufferPool.getBuffer( DirectByteBuffer.AL_EXTERNAL,size ).getBuffer(DirectByteBuffer.SS_EXTERNAL));
	}
	
	public void
	freeDirectByteBuffer(
		ByteBuffer	buffer )
	{
    
		//DirectByteBufferPool.freeBuffer( buffer );
	}
	
	public Formatters
	getFormatters()
	{
		return( new FormattersImpl());
	}
	
	public LocaleUtilities
	getLocaleUtilities()
	{
		return( new LocaleUtilitiesImpl( pi ));
	}
	
	public UTTimer
	createTimer(
		String		name )
	{
		return( new UTTimerImpl( pi, name ));
	}
	
	public void
	createThread(
		String			name,
		final Runnable	target )
	{
		Thread t = 
			new AEThread( pi.getPluginName() + "::" + name )
			{
				public void
				runSupport()
				{
					target.run();
				}
			};
	
		t.setDaemon(true);
		
		t.start();
	}
	
	public void
	createProcess(
		String		command_line )
	
		throws PluginException
	{
	    try{
	    		// we need to spawn without inheriting handles
	    	
	    	PlatformManager pm = PlatformManagerFactory.getPlatformManager();
	    	
	    	pm.createProcess( command_line, false );
	    	    	
	    }catch(Throwable e) {
	    	
	        Debug.printStackTrace(e);
	        
	        try{
	        	Runtime.getRuntime().exec( command_line );
	        	
	        }catch( Throwable f ){
	        	
	        	throw( new PluginException("Failed to create process", f ));
	        }
	    }
	}
	
	public ResourceDownloaderFactory
	getResourceDownloaderFactory()
	{
		return( ResourceDownloaderFactoryImpl.getSingleton());
	}
	
	public SESecurityManager
	getSecurityManager()
	{
		return( new SESecurityManagerImpl());
	}
	
	public SimpleXMLParserDocumentFactory
	getSimpleXMLParserDocumentFactory()
	{
		return( new SimpleXMLParserDocumentFactoryImpl());
	}
}
