/*
 * File    : ConfigPanel*.java
 * Created : 11 mar. 2004
 * By      : TuxPaper
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

package org.gudy.azureus2.ui.swt.views.configsections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.program.Program;

import com.aelitis.azureus.core.*;

import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.ui.swt.config.*;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.tracker.host.TRHost;
import org.gudy.azureus2.core3.tracker.server.TRTrackerServer;
import org.gudy.azureus2.core3.util.AENetworkClassifier;
import org.gudy.azureus2.core3.util.AERunnable;
import org.gudy.azureus2.ui.swt.ipchecker.IpCheckerWizard;
import org.gudy.azureus2.ui.swt.ipchecker.IpSetterCallBack;
import org.gudy.azureus2.ui.swt.mainwindow.Colors;
import org.gudy.azureus2.ui.swt.mainwindow.Cursors;
import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
import org.gudy.azureus2.ui.swt.auth.*;

public class 
ConfigSectionTrackerServer 
	implements UISWTConfigSection 
{
	
	protected	AzureusCore	azureus_core;
	
	public
	ConfigSectionTrackerServer(
		AzureusCore		_azureus_core )
	{
		azureus_core	= _azureus_core;
	}
	
  public String configSectionGetParentSection() {
    return ConfigSection.SECTION_TRACKER;
  }

	public String configSectionGetName() {
		return "tracker.server";
	}

  public void configSectionSave() {
  }

  public void configSectionDelete() {
  }
  

  public Composite configSectionCreate(final Composite parent) {
    GridData gridData;
    GridLayout layout;
    Label label;
    int userMode = COConfigurationManager.getIntParameter("User Mode");
    
    // main tab set up
    Composite gMainTab = new Composite(parent, SWT.NULL);

    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gMainTab.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 4;
    gMainTab.setLayout(layout);
    
      // MAIN TAB DATA

   	// row
    
    if(userMode>0) { // XXX

    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.ip");

    final StringParameter tracker_ip = new StringParameter(gMainTab, "Tracker IP", "" );

    gridData = new GridData();
    gridData.widthHint = 80;
    tracker_ip.setLayoutData( gridData );

    Button check_button = new Button(gMainTab, SWT.PUSH);
    gridData = new GridData();
    gridData.horizontalSpan = 2;
    check_button.setLayoutData(gridData);
    
    Messages.setLanguageText(check_button, "ConfigView.section.tracker.checkip"); //$NON-NLS-1$

    final Display display = gMainTab.getDisplay();

    check_button.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event event) {
          IpCheckerWizard wizard = new IpCheckerWizard(azureus_core, display);
          wizard.setIpSetterCallBack(new IpSetterCallBack() {
              public void setIp(final String ip) {
                if(display == null || display.isDisposed())
                  return;
                  display.asyncExec(new AERunnable(){
                  public void runSupport() {
                    if(tracker_ip != null)
                      tracker_ip.setValue(ip);
                  }
                });
              }
           }); // setIPSetterCallback
         }
    });


    // row

    final BooleanParameter nonsslEnable = 
        new BooleanParameter(gMainTab, "Tracker Port Enable", false, 
                             "ConfigView.section.tracker.port");

    IntParameter tracker_port = new IntParameter(gMainTab, "Tracker Port", TRHost.DEFAULT_PORT, false );

    gridData = new GridData();
    gridData.widthHint = 50;
    tracker_port.setLayoutData( gridData );

    final StringParameter tracker_port_backup = new StringParameter(gMainTab, "Tracker Port Backups", "" );

    gridData = new GridData();
    gridData.widthHint = 100;
    tracker_port_backup.setLayoutData( gridData );
    
    Label tracker_port_backup_label = new Label(gMainTab, SWT.NULL );
    Messages.setLanguageText(tracker_port_backup_label, "ConfigView.section.tracker.portbackup");

    Control[] non_ssl_controls = new Control[3];
    non_ssl_controls[0] = tracker_port.getControl();
    non_ssl_controls[1] = tracker_port_backup.getControl();
    non_ssl_controls[2] = tracker_port_backup_label;

    nonsslEnable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer( non_ssl_controls ));

 
    // row

    final BooleanParameter sslEnable = 
        new BooleanParameter(gMainTab, "Tracker Port SSL Enable", false,
                             "ConfigView.section.tracker.sslport");

    IntParameter tracker_port_ssl = 
        new IntParameter(gMainTab, "Tracker Port SSL", TRHost.DEFAULT_PORT_SSL, false);
    gridData = new GridData();
    gridData.widthHint = 50;
    tracker_port_ssl.setLayoutData( gridData );

    final StringParameter tracker_port_ssl_backup = new StringParameter(gMainTab, "Tracker Port SSL Backups", "" );

    gridData = new GridData();
    gridData.widthHint = 100;
    tracker_port_ssl_backup.setLayoutData( gridData );
    
    Label tracker_port_ssl_backup_label = new Label(gMainTab, SWT.NULL );
    Messages.setLanguageText(tracker_port_ssl_backup_label, "ConfigView.section.tracker.portbackup");

    	// create cert row

    Label cert_label = new Label(gMainTab, SWT.NULL );
    Messages.setLanguageText(cert_label, "ConfigView.section.tracker.createcert");

    Button cert_button = new Button(gMainTab, SWT.PUSH);

    Messages.setLanguageText(cert_button, "ConfigView.section.tracker.createbutton");

    cert_button.addListener(SWT.Selection, 
    		new Listener() 
			{
		        public void 
				handleEvent(Event event) 
		        {
		        	new CertificateCreatorWindow();
		        }
		    });
    
    Label ssl_faq_label = new Label(gMainTab, SWT.NULL);
    gridData = new GridData();
    gridData.horizontalSpan = 2;
    ssl_faq_label.setLayoutData(gridData);
    Messages.setLanguageText(ssl_faq_label, "ConfigView.section.tracker.sslport.info");
    final String linkFAQ = "http://azureus.sourceforge.net/faq.php#19";
    ssl_faq_label.setCursor(Cursors.handCursor);
    ssl_faq_label.setForeground(Colors.blue);
    ssl_faq_label.addMouseListener(new MouseAdapter() {
       public void mouseDoubleClick(MouseEvent arg0) {
         Program.launch(linkFAQ);
       }
       public void mouseDown(MouseEvent arg0) {
         Program.launch(linkFAQ);
       }
    });
    
    Control[] ssl_controls = { 	
    		tracker_port_ssl.getControl(),
    		tracker_port_ssl_backup.getControl(),
    		tracker_port_ssl_backup_label,
    		ssl_faq_label,
    		cert_label, 
    		cert_button };
 

    sslEnable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer( ssl_controls ));

    
    // row

    gridData = new GridData();
    gridData.horizontalSpan = 1;
    new BooleanParameter(gMainTab, "Tracker Public Enable", false,
                         "ConfigView.section.tracker.publicenable").setLayoutData( gridData );

    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.publicenable.info");
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    label.setLayoutData(gridData);
    
    // row
    
    BooleanParameter forcePortDetails = 
        new BooleanParameter(gMainTab,  "Tracker Port Force External", false, 
                             "ConfigView.section.tracker.forceport");

    label = new Label(gMainTab, SWT.NULL);
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    label.setLayoutData(gridData);
    
    
    Control[] f_controls = new Control[1];
    f_controls[0] = forcePortDetails.getControl();

    IAdditionalActionPerformer f_enabler =
      new GenericActionPerformer(f_controls) {
        public void performAction()
        {
          boolean selected =  nonsslEnable.isSelected() ||
          sslEnable.isSelected();
    
          controls[0].setEnabled( selected );
        }
      };

    nonsslEnable.setAdditionalActionPerformer(f_enabler);
    sslEnable.setAdditionalActionPerformer(f_enabler);
    
    // row

    gridData = new GridData();
    gridData.horizontalSpan = 1;
    final BooleanParameter passwordEnableWeb = 
        new BooleanParameter(gMainTab, "Tracker Password Enable Web", false, 
                             "ConfigView.section.tracker.passwordenableweb");
    passwordEnableWeb.setLayoutData( gridData );
    
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    final BooleanParameter passwordWebHTTPSOnly = 
        new BooleanParameter(gMainTab, "Tracker Password Web HTTPS Only", false, 
                             "ConfigView.section.tracker.passwordwebhttpsonly");
    passwordWebHTTPSOnly.setLayoutData( gridData );

    IAdditionalActionPerformer web_https_enabler =
        new GenericActionPerformer(passwordWebHTTPSOnly.getControls())
            {
	            public void performAction()
	            {
	              boolean selected =  	passwordEnableWeb.isSelected() &&
				  						sslEnable.isSelected();
	
	              for (int i=0;i<controls.length;i++){
	              	
	              	controls[i].setEnabled( selected );
	              }
	            }
            };

    passwordEnableWeb.setAdditionalActionPerformer(web_https_enabler);
    sslEnable.setAdditionalActionPerformer(web_https_enabler);

    // row

     final BooleanParameter passwordEnableTorrent = 
      new BooleanParameter(gMainTab, "Tracker Password Enable Torrent", false, 
                           "ConfigView.section.tracker.passwordenabletorrent");
 
    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.passwordenabletorrent.info");
    gridData = new GridData();
    gridData.horizontalSpan = 3;
    label.setLayoutData( gridData );

     // row

    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.username");

    final StringParameter tracker_username = new StringParameter(gMainTab, "Tracker Username", "" );

    gridData = new GridData();
    gridData.widthHint = 100;
    tracker_username.setLayoutData( gridData );

    label = new Label(gMainTab, SWT.NULL);
    label = new Label(gMainTab, SWT.NULL);
     // row

    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.password");

    final PasswordParameter tracker_password = new PasswordParameter(gMainTab, "Tracker Password" );

    gridData = new GridData();
    gridData.widthHint = 100;
    tracker_password.setLayoutData( gridData );

    label = new Label(gMainTab, SWT.NULL);
    label = new Label(gMainTab, SWT.NULL);

    Control[] x_controls = new Control[2];
    x_controls[0] = tracker_username.getControl();
    x_controls[1] = tracker_password.getControl();

    IAdditionalActionPerformer enabler =
        new GenericActionPerformer(x_controls)
            {
            public void performAction()
            {
              boolean selected =  passwordEnableWeb.isSelected() ||
                        passwordEnableTorrent.isSelected();

              for (int i=0;i<controls.length;i++){

                controls[i].setEnabled( selected );
              }
            }
            };

    passwordEnableWeb.setAdditionalActionPerformer(enabler);
    passwordEnableTorrent.setAdditionalActionPerformer(enabler);
    

    
    	// Poll Group //
    
    Group gPollStuff = new Group(gMainTab, SWT.NULL);
    Messages.setLanguageText(gPollStuff, "ConfigView.section.tracker.pollinterval");
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gridData.horizontalSpan = 4;
    gPollStuff.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 4;
    gPollStuff.setLayout(layout);

    label = new Label(gPollStuff, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalmin");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter pollIntervalMin = new IntParameter(gPollStuff, "Tracker Poll Interval Min", TRHost.DEFAULT_MIN_RETRY_DELAY );

    gridData = new GridData();
    gridData.widthHint = 30;
    pollIntervalMin.setLayoutData( gridData );

    label = new Label(gPollStuff, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalmax");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter pollIntervalMax = new IntParameter(gPollStuff, "Tracker Poll Interval Max", TRHost.DEFAULT_MAX_RETRY_DELAY );

    gridData = new GridData();
    gridData.widthHint = 30;
    pollIntervalMax.setLayoutData( gridData );

    // row

    label = new Label(gPollStuff, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalincby");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter pollIntervalIncBy = new IntParameter(gPollStuff, "Tracker Poll Inc By", TRHost.DEFAULT_INC_BY );

    gridData = new GridData();
    gridData.widthHint = 30;
    pollIntervalIncBy.setLayoutData( gridData );

    label = new Label(gPollStuff, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalincper");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter pollIntervalIncPer = new IntParameter(gPollStuff, "Tracker Poll Inc Per", TRHost.DEFAULT_INC_PER );

    gridData = new GridData();
    gridData.widthHint = 30;
    pollIntervalIncPer.setLayoutData( gridData );

    
    // scrape + cache group

    Group gScrapeCache = new Group(gMainTab, SWT.NULL);
    Messages.setLanguageText(gScrapeCache, "ConfigView.section.tracker.scrapeandcache");
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gridData.horizontalSpan = 4;
    gScrapeCache.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 4;
    gScrapeCache.setLayout(layout);
    
    // row
    
    label = new Label(gScrapeCache, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.announcescrapepercentage");

    IntParameter scrapeannouncepercentage = new IntParameter(gScrapeCache, "Tracker Scrape Retry Percentage", TRHost.DEFAULT_SCRAPE_RETRY_PERCENTAGE );

    gridData = new GridData();
    gridData.widthHint = 30;
    scrapeannouncepercentage.setLayoutData( gridData );
    
    label = new Label(gScrapeCache, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.scrapecacheperiod");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter scrapeCachePeriod = new IntParameter(gScrapeCache, "Tracker Scrape Cache", TRHost.DEFAULT_SCRAPE_CACHE_PERIOD );

    gridData = new GridData();
    gridData.widthHint = 30;
    scrapeCachePeriod.setLayoutData( gridData );
    
 
    // row

    label = new Label(gScrapeCache, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.announcecacheminpeers");

    IntParameter announceCacheMinPeers = new IntParameter(gScrapeCache, "Tracker Announce Cache Min Peers", TRHost.DEFAULT_ANNOUNCE_CACHE_PEER_THRESHOLD );

    gridData = new GridData();
    gridData.widthHint = 30;
    announceCacheMinPeers.setLayoutData( gridData );
    
    label = new Label(gScrapeCache, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.announcecacheperiod");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter announceCachePeriod = new IntParameter(gScrapeCache, "Tracker Announce Cache", TRHost.DEFAULT_ANNOUNCE_CACHE_PERIOD );

    gridData = new GridData();
    gridData.widthHint = 30;
    announceCachePeriod.setLayoutData( gridData );

    
    // main tab again
    // row

    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.maxpeersreturned");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter maxPeersReturned = new IntParameter(gMainTab, "Tracker Max Peers Returned", 100 );

    gridData = new GridData();
    gridData.widthHint = 50;
    maxPeersReturned.setLayoutData( gridData );

    label = new Label(gMainTab, SWT.NULL);
    label = new Label(gMainTab, SWT.NULL);

    	// seed retention limit
    
    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.seedretention");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter seedRetentionLimit = new IntParameter(gMainTab, "Tracker Max Seeds Retained");

    gridData = new GridData();
    gridData.widthHint = 50;
    seedRetentionLimit.setLayoutData( gridData );

    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.seedretention.info");
    gridData = new GridData();
    gridData.horizontalSpan = 2;
    label.setLayoutData( gridData );

    	// row

    gridData = new GridData();
    gridData.horizontalSpan = 2;
    new BooleanParameter(gMainTab, "Tracker NAT Check Enable", true, 
                         "ConfigView.section.tracker.natcheckenable").setLayoutData( gridData );
    
    Composite gNATDetails = new Composite(gMainTab, SWT.NULL);
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gridData.horizontalSpan = 2;
    gNATDetails.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 2;
    layout.marginHeight=0;
    layout.marginWidth=0;
    gNATDetails.setLayout(layout);
    
    	// row
    
    label = new Label(gNATDetails, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.natchecktimeout");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter NATTimeout = new IntParameter(gNATDetails, "Tracker NAT Check Timeout", TRTrackerServer.DEFAULT_NAT_CHECK_SECS );

    gridData = new GridData();
    gridData.widthHint = 50;
    NATTimeout.setLayoutData( gridData );

 
    // row
    
    gridData = new GridData();
    gridData.horizontalSpan = 4;
 
    new BooleanParameter(gMainTab, "Tracker Send Peer IDs", true, 
                         "ConfigView.section.tracker.sendpeerids").setLayoutData(gridData);
    
    // row
 
    gridData = new GridData();
    gridData.horizontalSpan = 4;
 
    BooleanParameter	enable_udp = 
    	new BooleanParameter(gMainTab, "Tracker Port UDP Enable", false, 
                         "ConfigView.section.tracker.enableudp");

    enable_udp.setLayoutData(gridData);
    
    // row
    
    Label udp_version_label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(udp_version_label,  "ConfigView.section.tracker.udpversion");
    gridData = new GridData();
    gridData.widthHint = 40;
    IntParameter	udp_version = new IntParameter(gMainTab, "Tracker Port UDP Version", 2);
    udp_version.setLayoutData(gridData);
    label = new Label(gMainTab, SWT.NULL);
    label = new Label(gMainTab, SWT.NULL);

    enable_udp.setAdditionalActionPerformer(
    		new ChangeSelectionActionPerformer( new Control[]{ udp_version_label, udp_version.getControl() }));

    // row
    
    gridData = new GridData();
    gridData.horizontalSpan = 4;
 
    new BooleanParameter(gMainTab, "Tracker Compact Enable", true,
                         "ConfigView.section.tracker.enablecompact").setLayoutData(gridData);
    
    // row

    gridData = new GridData();
    gridData.horizontalSpan = 4;
    BooleanParameter log_enable = 
    	new BooleanParameter(gMainTab, "Tracker Log Enable", false, 
                         "ConfigView.section.tracker.logenable");
    log_enable.setLayoutData( gridData );
    
    if(userMode>1) { // XXX
    
    // row

    gridData = new GridData();
    gridData.horizontalSpan = 4;
 
    new BooleanParameter(gMainTab, "Tracker Key Enable Server", true,
                         "ConfigView.section.tracker.enablekey").setLayoutData(gridData);

    // Networks Group //
    
    Group networks_group = new Group( gMainTab, SWT.NULL );
    Messages.setLanguageText( networks_group, "ConfigView.section.tracker.server.group.networks" );
    GridData    networks_layout = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    networks_layout.horizontalSpan = 4;
    networks_group.setLayoutData( networks_layout );
    layout = new GridLayout();
    layout.numColumns = 2;
    networks_group.setLayout(layout);
        
    label = new Label(networks_group, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.server.group.networks.info");
    GridData grid_data = new GridData();
    grid_data.horizontalSpan = 2;
    label.setLayoutData( grid_data );
    
    for (int i=0;i<AENetworkClassifier.AT_NETWORKS.length;i++){
		
		String	nn = AENetworkClassifier.AT_NETWORKS[i];
	
		String	config_name = "Tracker Network Selection Default." + nn;
		String	msg_text	= "ConfigView.section.connection.networks." + nn;
		 
		BooleanParameter network = new BooleanParameter(networks_group, config_name, msg_text );
				
	    grid_data = new GridData();
	    grid_data.horizontalSpan = 2;
	    network.setLayoutData( grid_data );
	}
    
    // processing limits group //

    Group gProcessing = new Group(gMainTab, SWT.NULL);
    Messages.setLanguageText(gProcessing, "ConfigView.section.tracker.processinglimits");
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gridData.horizontalSpan = 4;
    gProcessing.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 3;
    gProcessing.setLayout(layout);
    
    	// row annouce/scrape max process time
    
    label = new Label(gProcessing, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.maxgettime");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter maxGetTime = new IntParameter(gProcessing, "Tracker Max GET Time", 20 );
 
    gridData = new GridData();
    gridData.widthHint = 50;
    maxGetTime.setLayoutData( gridData );

    label = new Label(gProcessing, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.maxgettime.info");
   
  	// row post multiplier
    
    label = new Label(gProcessing, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.maxposttimemultiplier");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter maxPostTimeMultiplier = new IntParameter(gProcessing, "Tracker Max POST Time Multiplier", 1 );

    gridData = new GridData();
    gridData.widthHint = 50;
    maxPostTimeMultiplier.setLayoutData( gridData );

    label = new Label(gProcessing, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.maxposttimemultiplier.info");
   
   	// row max threads
    
    label = new Label(gProcessing, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.maxthreads");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter maxThreadsTime = new IntParameter(gProcessing, "Tracker Max Threads", 48 );
    maxThreadsTime.setMinimumValue(1);
    maxThreadsTime.setMaximumValue(4096);
    gridData = new GridData();
    gridData.widthHint = 50;
    maxThreadsTime.setLayoutData( gridData );

    label = new Label(gProcessing, SWT.NULL);
    
    
  	// non-blocking tracker group //
    
    Group gNBTracker = new Group(gMainTab, SWT.NULL);
    Messages.setLanguageText(gNBTracker, "ConfigView.section.tracker.nonblocking");
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gridData.horizontalSpan = 4;
    gNBTracker.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 3;
    gNBTracker.setLayout(layout);
    
    	// row

    gridData = new GridData();
    gridData.horizontalSpan = 3;
 
    BooleanParameter nb_enable =
    	new BooleanParameter(gNBTracker, "Tracker TCP NonBlocking", false,
                         "ConfigView.section.tracker.tcpnonblocking");
    nb_enable.setLayoutData(gridData);

 	// row max conc connections
    
    label = new Label(gNBTracker, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.section.tracker.nonblockingconcmax");
    gridData = new GridData();
    label.setLayoutData( gridData );

    IntParameter maxConcConn = new IntParameter(gNBTracker, "Tracker TCP NonBlocking Conc Max" );
    gridData = new GridData();
    gridData.widthHint = 50;
    maxConcConn.setLayoutData( gridData );

    label = new Label(gNBTracker, SWT.NULL);
    
    nb_enable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer( maxConcConn.getControls() ));
    
    }
  }
    
    return gMainTab;
  }
}
