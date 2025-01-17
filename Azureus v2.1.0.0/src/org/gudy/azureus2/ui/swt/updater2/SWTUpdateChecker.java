/*
 * Created on 20 mai 2004
 * Created by Olivier Chalouhi
 * 
 * Copyright (C) 2004 Aelitis SARL, All rights Reserved
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
 * 
 * AELITIS, SARL au capital de 30,000 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package org.gudy.azureus2.ui.swt.updater2;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.gudy.azureus2.core3.logging.LGLogger;
import org.gudy.azureus2.core3.util.Constants;
import org.gudy.azureus2.plugins.update.UpdatableComponent;
import org.gudy.azureus2.plugins.update.Update;
import org.gudy.azureus2.plugins.update.UpdateChecker;
import org.gudy.azureus2.plugins.update.UpdateInstaller;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;

/**
 * @author Olivier Chalouhi
 *
 */
public class SWTUpdateChecker implements UpdatableComponent
{
  public static void
  initialize()
  {
    PluginInitializer.getDefaultInterface().getUpdateManager().registerUpdatableComponent(new SWTUpdateChecker(),true);
  }
  
  public SWTUpdateChecker() {    
  }
  
  public void checkForUpdate(final UpdateChecker checker) {
  	try{
	    SWTVersionGetter versionGetter = new SWTVersionGetter();
	    if( versionGetter.needsUpdate() && System.getProperty("azureus.skipSWTcheck") == null ) {
        
        String[] mirrors = versionGetter.getMirrors();
	      
	      ResourceDownloader swtDownloader = null;
	      
          ResourceDownloaderFactory factory = ResourceDownloaderFactoryImpl.getSingleton();
          List downloaders =  new ArrayList();
          for(int i = 0 ; i < mirrors.length ; i++) {
            try {
              downloaders.add(factory.getSuffixBasedDownloader(factory.create(new URL(mirrors[i]))));
            } catch(MalformedURLException e) {
              //Do nothing
              LGLogger.log("Cannot use URL " + mirrors[i] + " (not valid)");
            }
          }
          ResourceDownloader[] resourceDownloaders = 
            (ResourceDownloader[]) 
            downloaders.toArray(new ResourceDownloader[downloaders.size()]);
          
          swtDownloader = factory.getRandomDownloader(resourceDownloaders);
          	      
	      swtDownloader.addListener(new ResourceDownloaderAdapter() {
	        
	        public boolean completed(ResourceDownloader downloader, InputStream data) {
	          //On completion, process the InputStream to store temp files
	          return processData(checker,data);
	        }
	      });
	      
	      	// get the size so its cached up
	      
	      try{
	      	swtDownloader.getSize();
	      	
	      }catch( ResourceDownloaderException e ){
	      
	      	e.printStackTrace();
	      }
	      
	      checker.addUpdate("SWT Libray for " + versionGetter.getPlatform(),
	          new String[] {"SWT is the graphical library used by Azureus"},
	          "" + versionGetter.getLatestVersion(),
	          swtDownloader,
	          Update.RESTART_REQUIRED_YES
	          );      
	      
	    }
  	}catch( Throwable e ){
  		
  		LGLogger.log( "SWT Version check failed", e );
  		
  		checker.failed();
  		
  	}finally{
  		
  		checker.completed();
  	}
    
  }
  
  private boolean processData(UpdateChecker checker,InputStream data) {
    try {
      UpdateInstaller installer = checker.createInstaller();    
      ZipInputStream zip = new ZipInputStream(data);
      ZipEntry entry = null;
      while((entry = zip.getNextEntry()) != null) {
        String name = entry.getName();
        
        //swt.jar on all platforms ...
        if(name.equals("swt.jar")) {
          installer.addResource(name,zip,false);
          if(Constants.isOSX) {
            installer.addMoveAction(name,installer.getInstallDir() + "/Azureus.app/Contents/Resources/Java/" + name);
          } else {
            installer.addMoveAction(name,installer.getInstallDir() + File.separator + name);
          }
          continue;
        }
        
        //swt-pi.jar on OSX and on Linux
        if(name.equals("swt-pi.jar")) {
          installer.addResource(name,zip,false);
          if(Constants.isOSX) {
            installer.addMoveAction(name,installer.getInstallDir() + "/Azureus.app/Contents/Resources/Java/" + name);
          } else {
            installer.addMoveAction(name,installer.getInstallDir() + File.separator + name);
          }
          continue;
        }
        
        //on OS X, any .jnilib
        if(name.endsWith(".jnilib") && Constants.isOSX) {
          installer.addResource(name,zip,false);
          installer.addMoveAction(name,installer.getInstallDir() + "/Azureus.app/Contents/Resources/Java/dll/" + name);
          continue;
        }
        
        //on OS X, java_swt (the launcher to start SWT applications)
        if(name.equals("java_swt")) {
          installer.addResource(name,zip,false);
          installer.addMoveAction(name,installer.getInstallDir() + "/Azureus.app/Contents/MacOS/" + name);
          installer.addChangeRightsAction("755",installer.getInstallDir() + "/Azureus.app/Contents/MacOS/" + name);
          continue;
        }
        
        //on windows, swt-win32-XXXX.dll
        if(name.startsWith("swt-win32-") && name.endsWith(".dll")) {
          installer.addResource(name,zip,false);
          installer.addMoveAction(name,installer.getInstallDir() + "\\" + name);
          continue;
        }
        
        //on linux, all .jar an .so ;)
        if(Constants.isLinux) {
          
          if(name.endsWith(".jar")) {
            installer.addResource(name,zip,false);
            installer.addMoveAction(name,installer.getInstallDir() + "/" + name);
            continue;
          }
          
          if(name.endsWith(".so")) {
            installer.addResource(name,zip,false);
            installer.addMoveAction(name,installer.getInstallDir() + "/" + name);
            continue;
          }
        }
      }
      zip.close();      
    } catch(Exception e) {
      e.printStackTrace();
      return false;
    }
        
    return true;
  }
  
  public String
  getName()
  {
    return( "SWT library" );
  }
  
  public int
  getMaximumCheckTime()
  {
    return( 30 ); // !!!! TODO: fix this
  } 
}
