/*
 * File    : ProgressPanel.java
 * Created : 7 oct. 2003 13:01:42
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

package org.gudy.azureus2.ui.swt.maketorrent;

import java.io.File;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.DownloadManagerState;
import org.gudy.azureus2.core3.download.DownloadManagerStateFactory;
import org.gudy.azureus2.core3.internat.LocaleUtil;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.torrent.*;
import org.gudy.azureus2.core3.tracker.host.TRHostException;
import org.gudy.azureus2.core3.util.AERunnable;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.TrackersUtil;
import org.gudy.azureus2.core3.util.TorrentUtils;
import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.ui.swt.wizard.*;
import org.gudy.azureus2.core3.util.AEThread;

/**
 * @author Olivier
 * 
 */
public class ProgressPanel extends AbstractWizardPanel implements TOTorrentProgressListener {

  Text tasks;
  ProgressBar progress;
  Display display;

  public ProgressPanel(NewTorrentWizard _wizard, IWizardPanel _previousPanel) {
    super(_wizard, _previousPanel);
  }
  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.maketorrent.IWizardPanel#show()
   */
  public void show() {
    display = wizard.getDisplay();
    wizard.setTitle(MessageText.getString("wizard.progresstitle"));
    wizard.setCurrentInfo("");
    wizard.setPreviousEnabled(false);
    Composite rootPanel = wizard.getPanel();
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    rootPanel.setLayout(layout);

    Composite panel = new Composite(rootPanel, SWT.NULL);
    GridData gridData = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
    panel.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 1;
    panel.setLayout(layout);

    tasks = new Text(panel, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY);
    tasks.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.heightHint = 120;
    tasks.setLayoutData(gridData);

    progress = new ProgressBar(panel, SWT.NULL);
    progress.setMinimum(0);
    progress.setMaximum(0);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    progress.setLayoutData(gridData);
  }

  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.maketorrent.IWizardPanel#finish()
   */
  public void finish() {
    Thread t = new AEThread("Torrent Maker") {
      public void runSupport() {
        makeTorrent();
      }
    };
    t.setPriority(Thread.MIN_PRIORITY);
    t.setDaemon(true);
    t.start();
  }
  
  public void makeTorrent() {
  	NewTorrentWizard _wizard = (NewTorrentWizard)wizard;
  	
    if(_wizard.tracker_type == NewTorrentWizard.TT_EXTERNAL ){
    	
      TrackersUtil.getInstance().addTracker(_wizard.trackerURL);
    }
    
    File f;
    
    if (_wizard.create_from_dir) {
      f = new File(_wizard.directoryPath);
    }
    else {
      f = new File(_wizard.singlePath);
    }

    try {
      URL url = new URL(_wizard.trackerURL);
      
      final TOTorrent torrent;
      
      if ( _wizard.getPieceSizeComputed()){
      	
        _wizard.creator = 
      		TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(
      					f, url, _wizard.getAddOtherHashes());
      	
        _wizard.creator.addListener( this );
      	
      	torrent = _wizard.creator.create();
      	
      }else{
      	TOTorrentCreator c = 
      		TOTorrentFactory.createFromFileOrDirWithFixedPieceLength(
      					f, url, _wizard.getAddOtherHashes(), _wizard.getPieceSizeManual());
      	
    	c.addListener( this );
      	
      	torrent = c.create();
      }
      
      if ( _wizard.tracker_type == NewTorrentWizard.TT_DECENTRAL ){
      	
      	TorrentUtils.setDecentralised( torrent );
      }
	  
      torrent.setComment(_wizard.getComment());
 
      TorrentUtils.setDHTBackupEnabled( torrent, _wizard.permitDHT );
	  
      TorrentUtils.setPrivate( torrent, _wizard.privateTorrent );
      
	  LocaleUtil.getSingleton().setDefaultTorrentEncoding( torrent );
      
      	// mark this newly created torrent as complete to avoid rechecking on open
      
      final File save_dir;
      
      if (_wizard.create_from_dir){
      	
      	save_dir = f;
      	
      }else{
      	
      	save_dir = f.getParentFile();
      }
      
	  DownloadManagerState	download_manager_state = 
			DownloadManagerStateFactory.getDownloadState( torrent ); 

	  TorrentUtils.setResumeDataCompletelyValid( download_manager_state );

	  download_manager_state.save();
     
      if(_wizard.useMultiTracker) {
        this.reportCurrentTask(MessageText.getString("wizard.addingmt"));
        TorrentUtils.listToAnnounceGroups(((NewTorrentWizard)wizard).trackers, torrent);
       }
      this.reportCurrentTask(MessageText.getString("wizard.savingfile"));
      
      final File torrent_file = new File(((NewTorrentWizard)wizard).savePath);
      
      torrent.serialiseToBEncodedFile(torrent_file);
      this.reportCurrentTask(MessageText.getString("wizard.filesaved"));
	  wizard.switchToClose();
	  
	  if ( ((NewTorrentWizard)wizard).autoOpen ){
	  	
        new AEThread("TorrentOpener::openTorrent") 
		{
            public void
			runSupport() 
            {
             	boolean	default_start_stopped = COConfigurationManager.getBooleanParameter( "Default Start Torrents Stopped" );

                ((NewTorrentWizard)wizard).getAzureusCore().getGlobalManager().addDownloadManager(
                		torrent_file.toString(), 
                		save_dir.toString(), 
						default_start_stopped 	? DownloadManager.STATE_STOPPED 
												: DownloadManager.STATE_QUEUED,
						true,	// persistent 
						true );	// for seeding
                
                if ( ((NewTorrentWizard)wizard).autoHost &&  ((NewTorrentWizard)wizard).tracker_type != NewTorrentWizard.TT_EXTERNAL ){
                	
                	try{
                		((NewTorrentWizard)wizard).getAzureusCore().getTrackerHost().hostTorrent( torrent, true, false );
                		
                	}catch( TRHostException e ){
                		
                		LGLogger.logRepeatableAlert( "Host operation fails", e );
                	}
                }

            }
		}.start();
	  }
	}
    catch (Exception e) {
      if ( 	e instanceof TOTorrentException && 
      		((TOTorrentException)e).getReason() == TOTorrentException.RT_CANCELLED ){
      	
      		//expected failure, don't log exception
     	
      }else{
      	Debug.printStackTrace( e );
        reportCurrentTask(MessageText.getString("wizard.operationfailed"));
        reportCurrentTask(LGLogger.exceptionToString(e));
      }
      
 	  wizard.switchToClose();
    }
  }

  /* (non-Javadoc)
   * @see org.gudy.azureus2.core3.torrent.TOTorrentProgressListener#reportCurrentTask(java.lang.String)
   */
  public void reportCurrentTask(final String task_description) {
    if (display != null && !display.isDisposed()) {
      display.asyncExec(new AERunnable(){
        public void runSupport() {
          if (tasks != null && !tasks.isDisposed()) {
            tasks.append(task_description + Text.DELIMITER);
          }
        }
      });
    }
  }

  /* (non-Javadoc)
   * @see org.gudy.azureus2.core3.torrent.TOTorrentProgressListener#reportProgress(int)
   */
  public void reportProgress(final int percent_complete) {
    if (display != null && !display.isDisposed()) {
      display.asyncExec(new AERunnable() {
        public void runSupport() {
          if (progress != null && !progress.isDisposed()) {
            progress.setSelection(percent_complete);
          }

        }
      });
    }
  }

}
