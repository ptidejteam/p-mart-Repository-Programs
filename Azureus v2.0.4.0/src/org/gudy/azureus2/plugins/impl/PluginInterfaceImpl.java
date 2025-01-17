/*
 * File    : PluginInterfaceImpl.java
 * Created : 12 nov. 2003
 * By      : Olivier
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
 
package org.gudy.azureus2.plugins.impl;

import java.util.Properties;

import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.PluginView;
import org.gudy.azureus2.ui.swt.FileDownloadWindow;
import org.gudy.azureus2.ui.swt.MainWindow;

/**
 * @author Olivier
 *
 */
public class PluginInterfaceImpl implements PluginInterface {
  
    Properties props;
    String pluginDir;
  
    public PluginInterfaceImpl(
        Properties props,
        String pluginDir) {
      this.props = props;
      this.pluginDir = pluginDir;
    }
  
    public void addView(PluginView view)
    {
      MainWindow window = MainWindow.getWindow();
      if(window != null) {
        window.addPluginView(view);
      }
    } 

    public void openTorrentFile(String fileName) {
      MainWindow.getWindow().openTorrent(fileName);
    }

    public void openTorrentURL(String url) {
      new FileDownloadWindow(MainWindow.getWindow().getDisplay(),url);
    }
        
    public Properties getPluginProperties() {
      return props;
    }
    
    public String getPluginDirectoryName() {
      return pluginDir;
    }
}
