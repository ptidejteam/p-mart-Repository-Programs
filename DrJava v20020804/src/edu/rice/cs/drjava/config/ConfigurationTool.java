/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is a part of DrJava. Current versions of this project are available
 * at http://sourceforge.net/projects/drjava
 *
 * Copyright (C) 2001-2002 JavaPLT group at Rice University (javaplt@rice.edu)
 * 
 * DrJava is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * DrJava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * or see http://www.gnu.org/licenses/gpl.html
 *
 * In addition, as a special exception, the JavaPLT group at Rice University
 * (javaplt@rice.edu) gives permission to link the code of DrJava with
 * the classes in the gj.util package, even if they are provided in binary-only
 * form, and distribute linked combinations including the DrJava and the
 * gj.util package. You must obey the GNU General Public License in all
 * respects for all of the code used other than these classes in the gj.util
 * package: Dictionary, HashtableEntry, ValueEnumerator, Enumeration,
 * KeyEnumerator, Vector, Hashtable, Stack, VectorEnumerator.
 *
 * If you modify this file, you may extend this exception to your version of the
 * file, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version. (However, the
 * present version of DrJava depends on these classes, so you'd want to
 * remove the dependency first!)
 *
 END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.config;
import java.io.File;
import java.io.IOException;
import gj.util.Vector;
import java.awt.Color;
import edu.rice.cs.drjava.DrJava;

/**
 * Interface which sets up the global configuration object at runtime.
 * @version $Id: ConfigurationTool.java,v 1.1 2005/03/03 19:29:45 toeivesb Exp $
 */
public interface ConfigurationTool {
  
  // STATIC VARIABLES    
  
  /**
   * The ".drjava" file in the user's home directory.
   */
  public static final File PROPERTIES_FILE =  
    new File(System.getProperty("user.home"), ".drjava");  
  
  /**
   * The global Configuration object to use for all configurable options.
   */
  public static final FileConfiguration CONFIG = new DefaultFileConfig().evaluate();
}

/**
 * Generate the CONFIG object separately to appease the almighty Javadoc.
 * (It didn't like anonymous inner classes with generics in interfaces in Java 1.3.)
 */
class DefaultFileConfig {
  public FileConfiguration evaluate() {
    try { 
      ConfigurationTool.PROPERTIES_FILE.createNewFile(); 
      // be nice and ensure a config file 
    } catch(IOException e) { // IOException occurred 
    } 
    FileConfiguration config = 
      new FileConfiguration(ConfigurationTool.PROPERTIES_FILE); 
    try { 
      config.loadConfiguration(); 
    }
    catch (Exception e) {
      // problem parsing the config file.
      // Use defaults and remember what happened (for the UI)
      config.resetToDefaults();
      config.storeStartupException(e);
    }
    return config;
  }
}


