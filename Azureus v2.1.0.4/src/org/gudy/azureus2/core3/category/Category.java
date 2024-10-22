/*
 * File    : Category.java
 * Created : 09 feb. 2004
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

package org.gudy.azureus2.core3.category;

import java.util.List;
import org.gudy.azureus2.core3.download.DownloadManager;

/** A Category for grouping torrents (DownloadManagers)
 * @author TuxPaper
 */
public interface Category {
  /** User created Category */  
  public static final int TYPE_USER = 0;
  /** Category which contains all DownloadManagers */  
  public static final int TYPE_ALL  = 1;
  /** Category which contains DownloadManagers that do not have a category assigned to
   * them.
   *
   * Currently not used.
   */  
  public static final int TYPE_UNCATEGORIZED = 2;

  /** Add a Category Listener
   * @param l Listener to add
   */  
  public void addCategoryListener(CategoryListener l);

  /** Remove a Category Listener
   * @param l Listener to remove
   */  
  public void removeCategoryListener(CategoryListener l);

  /** Retrieve the name of the category.  All category names are unique.
   * @return If type is TYPE_USER, returns name of the category
   *         Otherwise, returns ID in MessageBundle.
   */  
  public String getName();
  
  /** Retrieves what type of Category this is
   * @return TYPE_* constant
   */  
  public int getType();
  
  /** Retrieve a list of DownloadManagers for this category
   * @return DownloadManager List
   */  
  public List getDownloadManagers();
  
  /** Add a DownloadManager to this category.
   *
   * Used by DownloadManager.  You should not have to add a DownloadManager object
   * manually. If you wish to change a DownloadManager's category, use
   * DownloadManager.setCategory(..) instead.
   * @param manager DownloadManager object to add to Category
   */  
  public void addManager(DownloadManager manager);

  /** Remove a DownloadManager object from this Category.
   *
   * Used by DownloadManager.  You should not have to add a DownloadManager object
   * manually.  If you wish to change a DownloadManager's category, use
   * DownloadManager.setCategory(..) instead.
   * @param manager DownloadManager object to remove from Category
   */  
  public void removeManager(DownloadManager manager);

  // Other things like stats, settings, etc?
}
