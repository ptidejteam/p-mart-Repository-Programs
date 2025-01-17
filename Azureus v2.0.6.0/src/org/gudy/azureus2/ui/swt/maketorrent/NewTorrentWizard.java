/*
 * File : Wizard.java Created : 12 oct. 2003 14:30:57 By : Olivier
 * 
 * Azureus - a Java Bittorrent client
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details ( see the LICENSE file ).
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.gudy.azureus2.ui.swt.maketorrent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.gudy.azureus2.ui.swt.URLTransfer;
import org.gudy.azureus2.ui.swt.Utils;
import org.gudy.azureus2.ui.swt.wizard.Wizard;

/**
 * @author Olivier
 *  
 */
public class NewTorrentWizard extends Wizard {

  //false : singleMode, true: directory
  boolean mode;
  String singlePath = "";
  String directoryPath = "";
  String savePath = "";
  String comment = "";
  boolean localTracker = true;
  String trackerURL = "http://";
  boolean computed_piece_size = true;
  long	  manual_piece_size;
  
  boolean useMultiTracker = false;
  String multiTrackerConfig = "";
  List trackers = new ArrayList();

  public NewTorrentWizard(Display display) {
    super(display, "wizard.title");
    trackers.add(new ArrayList());
    trackerURL = Utils.getLinkFromClipboard(display);
    ModePanel panel = new ModePanel(this, null);
    this.setFirstPanel(panel);
    createDropTarget(getWizardWindow());
    
  }

  void setComment(String s) {
    comment = s;
  }

  String getComment() {
    return (comment);
  }
  private void createDropTarget(final Control control) {
    DropTarget dropTarget = new DropTarget(control, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
    dropTarget.setTransfer(new Transfer[] { URLTransfer.getInstance(), FileTransfer.getInstance()});
    dropTarget.addDropListener(new DropTargetAdapter() {
      public void dragOver(DropTargetEvent event) {
        if(URLTransfer.getInstance().isSupportedType(event.currentDataType)) {
          event.detail = getCurrentPanel() instanceof ModePanel ? DND.DROP_LINK : DND.DROP_NONE;
        }
      }
      public void drop(DropTargetEvent event) {
        if (event.data instanceof String[]) {
          String[] sourceNames = (String[]) event.data;
          if (sourceNames == null || sourceNames.length != 1)
            event.detail = DND.DROP_NONE;
          if (event.detail == DND.DROP_NONE)
            return;
          File droppedFile = new File(sourceNames[0]);
          if (getCurrentPanel() instanceof ModePanel) {
            if (droppedFile.isFile()) {
              singlePath = droppedFile.getAbsolutePath();
              ((ModePanel) getCurrentPanel()).activateMode(true);
            } else if (droppedFile.isDirectory()) {
              directoryPath = droppedFile.getAbsolutePath();
              ((ModePanel) getCurrentPanel()).activateMode(false);
            }
          } else if (getCurrentPanel() instanceof DirectoryPanel) {
            if (droppedFile.isDirectory())
              ((DirectoryPanel) getCurrentPanel()).setFilename(droppedFile.getAbsolutePath());
          } else if (getCurrentPanel() instanceof SingleFilePanel) {
            if (droppedFile.isFile())
              ((SingleFilePanel) getCurrentPanel()).setFilename(droppedFile.getAbsolutePath());
          }
         } else if (getCurrentPanel() instanceof ModePanel) {
           trackerURL = ((URLTransfer.URLType)event.data).linkURL;
           ((ModePanel) getCurrentPanel()).updateTrackerURL();
         }
       }
    });
  }

  protected void
  setPieceSizeComputed()
  {
  	computed_piece_size = true;
  }
  
  public boolean
  getPieceSizeComputed()
  {
  	return( computed_piece_size );
  }
  
  protected void
  setPieceSizeManual(
  	long	_value )
  {
  	computed_piece_size	= false;
  	manual_piece_size	= _value;
  }
  
  protected long
  getPieceSizeManual()
  {
  	return( manual_piece_size );
  }
}