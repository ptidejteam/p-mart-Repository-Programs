/*
 * File    : BlockedIpsWindow.java
 * Created : 17 d�c. 2003}
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
package org.gudy.azureus2.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.ipfilter.*;
import org.gudy.azureus2.core3.util.DisplayFormatters;

/**
 * @author Olivier
 *
 */
public class BlockedIpsWindow {
  
  public static void show(Display display,String ipsBlocked,String ipsBanned) {
    final Shell window = new Shell(display,SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
    Messages.setLanguageText(window,"ConfigView.section.ipfilter.list.title");
    window.setImage(ImageRepository.getImage("azureus"));
    
    FormLayout layout = new FormLayout();
    try {
      layout.spacing = 5;
    } catch (NoSuchFieldError e) {
      /* Ignore for Pre 3.0 SWT.. */
    }
    layout.marginHeight = 5;
    layout.marginWidth = 5;
    window.setLayout(layout);
    FormData formData;
    
    	// text blocked area
    
    final StyledText textBlocked = new StyledText(window,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    Button btnClear = new Button(window,SWT.PUSH);
    textBlocked.setEditable(false);
    
    final StyledText textBanned = new StyledText(window,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    Button btnOk = new Button(window,SWT.PUSH);
    Button btnReset = new Button(window,SWT.PUSH);
    textBanned.setEditable(false);
    
            
    formData = new FormData();
    formData.left = new FormAttachment(0,0);
    formData.right = new FormAttachment(100,0);
    formData.top = new FormAttachment(0,0);   
    formData.bottom = new FormAttachment(40,0);   
    textBlocked.setLayoutData(formData);
    textBlocked.setText(ipsBlocked);
    
    
    // label blocked area
    
    Label	blockedInfo = new Label(window, SWT.NULL);
    Messages.setLanguageText(blockedInfo,"ConfigView.section.ipfilter.blockedinfo");
    formData = new FormData();
    formData.top = new FormAttachment(textBlocked);    
    formData.right = new FormAttachment(btnClear);    
    formData.left = new FormAttachment(0,0);    
    blockedInfo.setLayoutData( formData );
 
    	// clear button
    
    
    Messages.setLanguageText(btnClear,"Button.clear");
    formData = new FormData();
    formData.top = new FormAttachment(textBlocked);    
    formData.right = new FormAttachment(95,0 );    
    //formData.bottom = new FormAttachment(textBanned);
    formData.width = 70;
    btnClear.setLayoutData(formData);
    btnClear.addListener(SWT.Selection,new Listener() {

    public void handleEvent(Event e) {
     
    	IpFilter.getInstance().clearBlockedIPs();
    	
    	textBlocked.setText( "" );
    }
    });
    
    
    	// text banned area
    formData = new FormData();
    formData.left = new FormAttachment(0,0);
    formData.right = new FormAttachment(100,0);
    formData.top = new FormAttachment(btnClear);   
    formData.bottom = new FormAttachment(btnOk);   
    textBanned.setLayoutData(formData);
    textBanned.setText(ipsBanned);
    
    	// label banned area
    
    Label	bannedInfo = new Label(window, SWT.NULL);
    Messages.setLanguageText(bannedInfo,"ConfigView.section.ipfilter.bannedinfo");
    formData = new FormData();
    formData.right = new FormAttachment(btnReset);    
    formData.left = new FormAttachment(0,0);    
    formData.bottom = new FormAttachment(100,0);  
    bannedInfo.setLayoutData( formData );
    
    	// reset button
    
    Messages.setLanguageText(btnReset,"Button.reset");
    formData = new FormData();
    formData.right = new FormAttachment(btnOk);    
    formData.bottom = new FormAttachment(100,0);    
    formData.width = 70;
    btnReset.setLayoutData(formData);
    btnReset.addListener(SWT.Selection,new Listener() {

    public void handleEvent(Event e) {
      	IpFilter.getInstance().clearBannedIps();
    	BadIps.getInstance().clearBadIps();
		
    	textBanned.setText( "" );    
    	}
    });
    	// ok button
    
    Messages.setLanguageText(btnOk,"Button.ok");
    formData = new FormData();
    formData.right = new FormAttachment(95,0);    
    formData.bottom = new FormAttachment(100,0);    
    formData.width = 70;
    btnOk.setLayoutData(formData);
    btnOk.addListener(SWT.Selection,new Listener() {

    public void handleEvent(Event e) {
      window.dispose();
    }
    });
        
    window.setDefaultButton( btnOk );
    
    window.addListener(SWT.Traverse, new Listener() {	
		public void handleEvent(Event e) {
			if ( e.character == SWT.ESC){
			     window.dispose();
			 }
		}
    });
    
    window.setSize(620,450);
    window.layout();
    window.open();    
  }
  
  public static void showBlockedIps(Shell mainWindow) {
    StringBuffer sbBlocked = new StringBuffer();
    StringBuffer sbBanned = new StringBuffer();
    BlockedIp[] blocked = IpFilter.getInstance().getBlockedIps();
    String inRange = MessageText.getString("ConfigView.section.ipfilter.list.inrange");
    String notInRange = MessageText.getString("ConfigView.section.ipfilter.list.notinrange");   
    String bannedMessage = MessageText.getString( "ConfigView.section.ipfilter.list.banned" );
    String badDataMessage = MessageText.getString( "ConfigView.section.ipfilter.list.baddata" );
    
    for(int i=0;i<blocked.length;i++){
      BlockedIp bIp = blocked[i];
      sbBlocked.append(DisplayFormatters.formatTimeStamp(bIp.getBlockedTime()));
      sbBlocked.append("\t[");
      sbBlocked.append( bIp.getTorrentName() );
      sbBlocked.append("] \t");
      sbBlocked.append(bIp.getBlockedIp());
      IpRange range = bIp.getBlockingRange();
      if(range == null) {
        sbBlocked.append(' ');
        sbBlocked.append(notInRange);
        sbBlocked.append('\n');
      } else {
        sbBlocked.append(' ');
        sbBlocked.append(inRange);
        sbBlocked.append(range.toString());
        sbBlocked.append('\n');
      }
    }  
    
    BannedIp[]	banned_ips = IpFilter.getInstance().getBannedIps();    
    
    for(int i=0;i<banned_ips.length;i++){
    	BannedIp bIp = banned_ips[i];
      sbBanned.append(DisplayFormatters.formatTimeStamp(bIp.getBanningTime()));
      sbBanned.append("\t[");
      sbBanned.append( bIp.getTorrentName() );
      sbBanned.append("] \t" );
      sbBanned.append( bIp.getIp());
      sbBanned.append( " " );
      sbBanned.append( bannedMessage );
      sbBanned.append( "\n");
    }
    
    BadIp[]	bad_ips = BadIps.getInstance().getBadIps();
    for(int i=0;i<bad_ips.length;i++){
    	BadIp bIp = bad_ips[i];
        sbBanned.append(DisplayFormatters.formatTimeStamp(bIp.getLastTime()));
        sbBanned.append( "\t" );
        sbBanned.append( bIp.getIp());
        sbBanned.append( " " );
        sbBanned.append( badDataMessage );
        sbBanned.append( " " );
        sbBanned.append( bIp.getNumberOfWarnings());
        sbBanned.append( "\n" );
    }
    
    if(mainWindow == null || mainWindow.isDisposed())
      return;
    BlockedIpsWindow.show(mainWindow.getDisplay(),sbBlocked.toString(),sbBanned.toString());
  }
}
