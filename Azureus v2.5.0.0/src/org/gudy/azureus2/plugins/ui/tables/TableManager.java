/*
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
 */

package org.gudy.azureus2.plugins.ui.tables;

/** Allows plugins to manage Azureus UI tables.
 *
 * @author TuxPaper
 * @since 2.0.8.5
 */
public interface TableManager
{
  /** Visible for Completed Torrents table */
  public static final String TABLE_MYTORRENTS_COMPLETE   = "MySeeders";
  /** Visible for Incompleted Torrents table */
  public static final String TABLE_MYTORRENTS_INCOMPLETE = "MyTorrents";
  /** Visible for Torrent Peers table */
  public static final String TABLE_TORRENT_PEERS         = "Peers";
  /** Visible for Torrent Pieces table */
  public static final String TABLE_TORRENT_PIECES        = "Pieces";
  /** Visible for Torrent Files table */
  public static final String TABLE_TORRENT_FILES         = "Files";
  /** Visible for My Tracker table */
  public static final String TABLE_MYTRACKER             = "MyTracker";
  /** Visible for My Shares table */
  public static final String TABLE_MYSHARES              = "MyShares";

  /** Creates a column for a UI table.
   * In order for this object to be displayed in an Azureus UI table, the
   * returned object must be added via the {@link #addColumn(TableColumn)}
   * <p>
   * The distinction between creating and adding a column is required because 
   * some TableColumn functions are not available or act differently after the 
   * column had been added.
   * <p>
   * In order to the plugin to display correctly the column name, your are 
   * required to create a key in your language file consisting of the
   * {@link TableManager} Table ID of the table you are adding the column to,
   * plus ".column." plus the logical name of your column. 
   * <p>
   * For example, if you are creating a column named "quality" in the table 
   * TABLE_TORRENT_FILES, youwould have to add the following to your language 
   * file:<br>
   *    <code>Files.column.quality=<i>Column Title</i></code><br>
   * and if you wish to have a short description of the column (visible when
   * the user is setting up columns), create another entry with the same key
   * plus ".info".  For the example above:<br>
   *    <code>Files.column.quality.info=<i>One line description</i></code>
   *
   *
   * @param tableID Which table the column will be visible in. See {@link TableManager}.
   * @param cellID The logical name of the column.
   *
   * @return an interface object allowing modification of the table column.
   */
  public TableColumn createColumn(String tableID, String cellID);

  /** Adds a column to an Azureus UI table.
   *
   * @param tableColumn a column previously created with {@link #createColumn}
   */
  public void addColumn(TableColumn tableColumn);
  
  /** Adds a Context Menu item to the specified table or to all table context menus.
   *
   * @param tableID Which table the menu item will be visible in. See {@link TableManager}.
   *                If null, the menu item will be added to all table context menus.
   * @param resourceKey ID of the context menu, which is also used to retrieve
   *                    the textual name from the plugin language file.
   *
   * @return a newly created menu item for the table's context menu.
   */
  public TableContextMenuItem addContextMenuItem(String tableID, String resourceKey);

  /** Future Implementations:
  public TableContextMenuItem addContextMenuItem(String tableID, String resourceKey,
                                                 String parentKey);

  public Menu addContextMenu(String tableID, String resourceKey);
  public Menu addContextMenu(String tableID, String resourceKey, String parentKey);
  */
}
