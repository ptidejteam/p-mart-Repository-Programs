/*
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
 
package org.gudy.azureus2.plugins.ui.tables;

/** 
 * This interface provides access to an Azureus table column.
 */
public interface TableColumn {
  /** The cells in this column display textual information. */
  public static final int TYPE_TEXT = 1;
  /** The graphic type, providing access to graphic specific functions in 
   * {@link TableCell}.
   */
  public static final int TYPE_GRAPHIC = 2;
  /**
   * The cells in this column display only textual information, and do not
   * set any other visible properties of cell (background, foreground, icon, 
   * etc).
   * 
   * Using this type allows azureus to call refresh less, and saves on CPU.
   */
  public static final int TYPE_TEXT_ONLY = 3;
  
  /** leading alignment */
  public static final int ALIGN_LEAD = 1;
  /** trailing alignment */
  public static final int ALIGN_TRAIL = 2;
  /** center alignment */
  public static final int ALIGN_CENTER = 3;

  /** For {@link #setPosition(int)}. Make column invisible initially. */
  public static final int POSITION_INVISIBLE = -1;
  /** For {@link #setPosition(int)}. Make column the last column initially. */
  public static final int POSITION_LAST = -2;
  
  /** Trigger refresh listeners every time a graphic cycle occurs (set by user) */
  public static final int INTERVAL_GRAPHIC = -1;
  /** Trigger refresh listeners every time a GUI update cycle occurs (set by user) */
  public static final int INTERVAL_LIVE = -2;
  /** Trigger refresh only when the cell/row becomes invalid */
  public static final int INTERVAL_INVALID_ONLY = -3;
  
  /** Initialize a group of variables all at once.  Saves on individual setXxx.
   *
   * @param iAlignment See {@link #setAlignment(int)}
   * @param iPosition See {@link #setPosition(int)}
   * @param iWidth See {@link #setWidth(int)}
   * @param iInterval See {@link #setRefreshInterval(int)}
   */
  public void initialize(int iAlignment, int iPosition, 
                         int iWidth, int iInterval);


  /** Initialize a group of variables all at once.  Saves on individual setXxx.
   *
   * @param iAlignment See {@link #setAlignment(int)}
   * @param iPosition See {@link #setPosition(int)}
   * @param iWidth See {@link #setWidth(int)}
   */
  public void initialize(int iAlignment, int iPosition, int iWidth);
  
  /**
   * The logical name of the column. This was set via
   * {@link TableManager#createColumn} and can not be changed.
   *
   * @return the column name (identification)
   */
  public String getName();

  /** Which table the column will be visible in.  This was set via
   * {@link TableManager#createColumn} and can not be changed.
   *
   * @return {@link TableManager}.TABLE_* constant(s)
   */
  public String getTableID();

  /** The type of the contained data.<br>
   * Current supported types are long, string, and graphic.
   * <P>
   * NOTE: This MUST be set BEFORE adding the column to a table.
   * <br>
   * The default type is {@link #TYPE_TEXT_ONLY}.
   *
   * @param type {@link #TYPE_TEXT}, {@link #TYPE_TEXT_ONLY}, {@link #TYPE_GRAPHIC}
   */
  public void setType(int type);

  /** Returns the type of the contained data.
   *
   * @return type TYPE_TEXT, or TYPE_GRAPHIC
   */
  public int getType();
  
  /** The column size.
   * <P>
   * NOTE: This MUST be set BEFORE adding the column to a table.
   *
   * @param width the size in pixels
   */
  public void setWidth(int width);
  
  /** Returns the column's size
   *
   * @return width in pixels
   */
  public int getWidth();
  
  /** Location to put the column.  When set before being added to the UI
   * (see {@link TableManager#addColumn}), the supplied value will be used
   * as the default position.  If the user has moved the column previously,
   * the new position will be used, and the default position will be ignored.
   *
   * This function cannot be called after you have added the column to a UI 
   * table.  In the future, setting the position after adding the column to the
   * UI table will result in the column being moved.
   *
   * @param position Column Number (0 based), POSITION_INVISIBLE or POSITION_LAST
   */
  public void setPosition(int position);
  
  
  /** Returns the position of the column
   *
   * @return Column Number (0 based), POSITION_INVISIBLE or POSITION_LAST
   */
  public int getPosition();

  /** Orientation of the columns text and header.
   * <P>
   * NOTE: This MUST be set BEFORE adding the column to a table.
   *
   * @param alignment ALIGN_TRAIL, ALIGN_LEAD, or ALIGN_CENTER
   */
  public void setAlignment(int alignment);

  /** Returns the alignment of the column 
   *
   * @return ALIGN_TRAIL, ALIGN_LEAD, or ALIGN_CENTER
   */
  public int getAlignment();
  
  /** Set how often the cell receives a refresh() trigger
   *
   * @param interval INTERVAL_GRAPHIC, INTERVAL_LIVE, INTERVAL_INVALID_ONLY
   *                 constants, or an integer based on the user-configurable
   *                 "GUI refresh interval".  For example, specifying 4 will 
   *                 result in a refresh trigger every 4 "GUI refresh intervals"
   */
  public void setRefreshInterval(int interval);

  /** Returns the refresh interval of the column.
   * The default is INTERVAL_INVALID_ONLY
   *
   * @return INTERVAL_* constant, or a number representing the # of GUI refresh
   *         cycles between each cell refresh call.
   */
  public int getRefreshInterval();

  /** Adds a listener that triggers when a TableCell that belongs to this column
   * needs refreshing.
   *
   * @param listener Listener Object to be called when refresh is needed.
   */
  public void addCellRefreshListener(TableCellRefreshListener listener);
  /** Removed a previously added TableCellRefreshListener
   *
   * @param listener Previously added listener
   */
  public void removeCellRefreshListener(TableCellRefreshListener listener);


  /** Adds a listener that triggers when a TableCell that belongs to this column
   * is being added.
   *
   * @param listener Listener Object to be called when refresh is needed.
   */
  public void addCellAddedListener(TableCellAddedListener listener);
  public void removeCellAddedListener(TableCellAddedListener listener);

  /** Adds a listener that triggers when a TableCell that belongs to this column
   * is being disposed.
   *
   * @param listener Listener Object to be called when refresh is needed.
   */
  public void addCellDisposeListener(TableCellDisposeListener listener);
  public void removeCellDisposeListener(TableCellDisposeListener listener);

  /** Adds a listener that triggers when a TableCell that belongs to this column
   * has a tooltip action
   *
   * @param listener Listener Object to be called when refresh is needed.
   */
  public void addCellToolTipListener(TableCellToolTipListener listener);
  public void removeCellToolTipListener(TableCellToolTipListener listener);

  /**
   * Adds a listener that triggers when a TableCell that belongs to this column
   * has a mouse event.
   * 
   * @param listener
   * 
   * @since 2.3.0.7
   */
  public void addCellMouseListener(TableCellMouseListener listener);
  /** Remove a previously added TableCellMouseListener
  *
  * @param listener Previously added listener
   * @since 2.3.0.7
  */
  public void removeCellMouseListener(TableCellMouseListener listener);

  /**
   * A listener is added for every type of cell listener the supplied object 
   * implements
   *  
   * @param listenerObject Object implementing some cell listeneters
   */
  public void addListeners(Object listenerObject);

  
  /** Invalidate all cells in this column.  The cells will be forced to
   * update on the next refresh.
   */
  public void invalidateCells();


  /** Adds a Context Menu item to the "This Column" sub menu
   *
   * @param resourceKey ID of the context menu, which is also used to retreieve
   *                    the textual name from the plugin language file.
   *
   * @return a newly created menu item
   */
  public TableContextMenuItem addContextMenuItem(String resourceKey);
}