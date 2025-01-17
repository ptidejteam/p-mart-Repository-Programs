/*
 * Created on 2 juil. 2003
 *
 */
package org.gudy.azureus2.ui.swt.views;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.ipfilter.IpFilter;
import org.gudy.azureus2.core3.ipfilter.IpRange;
import org.gudy.azureus2.core3.stats.StatsWriterPeriodic;
import org.gudy.azureus2.core3.tracker.host.TRHost;
import org.gudy.azureus2.core3.util.FileUtil;
import org.gudy.azureus2.ui.swt.ImageRepository;
import org.gudy.azureus2.ui.swt.MainWindow;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.Utils;
import org.gudy.azureus2.ui.swt.config.*;
import org.gudy.azureus2.ui.swt.ipchecker.IpCheckerWizard;
import org.gudy.azureus2.ui.swt.ipchecker.IpSetterCallBack;

/**
 * @author Olivier
 * 
 */
public class ConfigView extends AbstractIView {

  private static final int upRates[] =
    {
      0,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      20,
      25,
      30,
      35,
      40,
      45,
      50,
      60,
      70,
      80,
      90,
      100,
      150,
      200,
      250,
      300,
      350,
      400,
      450,
      500,
      600,
      700,
      800,
      900,
      1000 };

  private static final int statsPeriods[] =
	  {
	  	1, 2, 3, 4, 5, 10, 15, 20, 25, 30, 40, 50,
	  	60, 120, 180, 240, 300, 360, 420, 480, 540, 600, 
	  	900, 1200, 1800, 2400, 3000, 3600, 
	  	7200, 10800, 14400, 21600, 43200, 86400,
	  };

  IpFilter filter;

  Composite cConfig;
  //CTabFolder ctfConfig;
  TabFolder tfConfig;
  Table table;
  boolean noChange;
  Label passwordMatch;

  public ConfigView() {
    filter = IpFilter.getInstance();
  }

  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.IView#initialize(org.eclipse.swt.widgets.Composite)
   */
  public void initialize(Composite composite) {
    cConfig = new Composite(composite, SWT.NONE);
    GridLayout configLayout = new GridLayout();
    configLayout.marginHeight = 0;
    configLayout.marginWidth = 0;
    configLayout.numColumns = 2;
    cConfig.setLayout(configLayout);

    tfConfig = new TabFolder(cConfig, SWT.TOP | SWT.FLAT);
    //ctfConfig = new CTabFolder(cConfig, SWT.TOP | SWT.FLAT);
    //ctfConfig.setSelectionBackground(new Color[] { MainWindow.white }, new int[0]);
    
    TabItem itemFile = initGroupFile();
    //CTabItem itemFile = initGroupFile();
    
    initGroupServer();
    initGroupDownloads();
    initGroupTransfer();
    initGroupDisplay();
    initGroupIrc();
    initGroupFilter();
    initStats();
    initStyle();
    initTracker();
    
    initSaveButton(); 
    TabItem[] items = {itemFile};
    tfConfig.setSelection(items);
    Utils.changeBackgroundComposite(cConfig,MainWindow.getWindow().getBackground());
  }

  private void initSaveButton() {
    GridData gridData;
    Button save = new Button(cConfig, SWT.PUSH);
    Messages.setLanguageText(save, "ConfigView.button.save"); //$NON-NLS-1$
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_END);
    gridData.widthHint = 80;
    save.setLayoutData(gridData);

    save.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        COConfigurationManager.setParameter("updated", 1); //$NON-NLS-1$
        COConfigurationManager.save();
        filter.save();
      }
    });
  }

  private void initGroupFilter() {
    GridData gridData;
    Label label;
    TabItem itemFilter = new TabItem(tfConfig, SWT.NULL);
    //CTabItem itemFilter = new CTabItem(ctfConfig, SWT.NULL);
    Messages.setLanguageText(itemFilter, "ipFilter.shortTitle"); //$NON-NLS-1$

    Group gFilter = new Group(tfConfig, SWT.NULL);
    //Group gFilter = new Group(ctfConfig, SWT.NULL);
    gridData = new GridData(GridData.FILL_BOTH);
    gFilter.setLayoutData(gridData);

    GridLayout layoutFilter = new GridLayout();
    layoutFilter.numColumns = 3;
    gFilter.setLayout(layoutFilter);

    gridData = new GridData(GridData.BEGINNING);
    new BooleanParameter(gFilter, "Ip Filter Enabled",false).setLayoutData(gridData); //$NON-NLS-1$
        
    label = new Label(gFilter, SWT.NULL);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    label.setLayoutData(gridData);
    Messages.setLanguageText(label, "ipFilter.enable"); //$NON-NLS-1$
    
    
    table = new Table(gFilter, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
    String[] headers = { "ipFilter.description", "ipFilter.start", "ipFilter.end" };
    int[] sizes = { 200, 110, 110 };
    int[] aligns = { SWT.LEFT, SWT.CENTER, SWT.CENTER };
    for (int i = 0; i < headers.length; i++) {
      TableColumn tc = new TableColumn(table, aligns[i]);
      tc.setText(headers[i]);
      tc.setWidth(sizes[i]);
      Messages.setLanguageText(tc, headers[i]); //$NON-NLS-1$
    }

    table.setHeaderVisible(true);

    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.heightHint = 200;
    gridData.verticalSpan = 4;
    gridData.horizontalSpan = 2;
    table.setLayoutData(gridData);

    Button add = new Button(gFilter, SWT.PUSH);
    gridData = new GridData(GridData.CENTER);
    gridData.widthHint = 100;
    add.setLayoutData(gridData);
    Messages.setLanguageText(add, "ipFilter.add");
    add.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event arg0) {
        addRange();
      }
    });

    Button remove = new Button(gFilter, SWT.PUSH);
    gridData = new GridData(GridData.CENTER);
    gridData.widthHint = 100;
    remove.setLayoutData(gridData);
    Messages.setLanguageText(remove, "ipFilter.remove");
    remove.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event arg0) {
        TableItem[] selection = table.getSelection();
        if (selection.length == 0)
          return;
        removeRange((IpRange) selection[0].getData());
        table.remove(table.indexOf(selection[0]));
        selection[0].dispose();
      }
    });

    Button edit = new Button(gFilter, SWT.PUSH);
    gridData = new GridData(GridData.CENTER);
    gridData.widthHint = 100;
    edit.setLayoutData(gridData);
    Messages.setLanguageText(edit, "ipFilter.edit");
    edit.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event arg0) {
        TableItem[] selection = table.getSelection();
        if (selection.length == 0)
          return;
        editRange((IpRange) selection[0].getData());
      }
    });

    table.addMouseListener(new MouseAdapter() {
      public void mouseDoubleClick(MouseEvent arg0) {
        TableItem[] selection = table.getSelection();
        if (selection.length == 0)
          return;
        editRange((IpRange) selection[0].getData());
      }
    });

    populateTable();

    itemFilter.setControl(gFilter);
  }

  private void initGroupIrc() {
    GridData gridData;
    GridLayout layout;
    Label label;
    TabItem itemIrc = new TabItem(tfConfig, SWT.NULL);
    //CTabItem itemIrc = new CTabItem(ctfConfig, SWT.NULL);
    Messages.setLanguageText(itemIrc, "ConfigView.section.irc"); //$NON-NLS-1$

    Group gIrc = new Group(tfConfig, SWT.NULL);
    //Group gIrc = new Group(ctfConfig, SWT.NULL);

    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gIrc.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 2;
    gIrc.setLayout(layout);

    label = new Label(gIrc, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.ircserver"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 150;
    new StringParameter(gIrc, "Irc Server", "irc.freenode.net").setLayoutData(gridData); //$NON-NLS-1$

    label = new Label(gIrc, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.ircchannel"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 150;
    new StringParameter(gIrc, "Irc Channel", "#azureus-users").setLayoutData(gridData); //$NON-NLS-1$

    label = new Label(gIrc, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.irclogin"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 150;
    new StringParameter(gIrc, "Irc Login", "").setLayoutData(gridData); //$NON-NLS-1$

    itemIrc.setControl(gIrc);
  }

  private void initGroupDisplay() {
    GridData gridData;
    GridLayout layout;
    Label label;
    TabItem itemDisplay = new TabItem(tfConfig, SWT.NULL);
    //CTabItem itemDisplay = new CTabItem(ctfConfig, SWT.NULL);
    Messages.setLanguageText(itemDisplay, "ConfigView.section.display"); //$NON-NLS-1$

    Group gDisplay = new Group(tfConfig, SWT.NULL);
    //Group gDisplay = new Group(ctfConfig, SWT.NULL);
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gDisplay.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 2;
    gDisplay.setLayout(layout);

    label = new Label(gDisplay, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.opendetails"); //$NON-NLS-1$
    new BooleanParameter(gDisplay, "Open Details", true); //$NON-NLS-1$

    label = new Label(gDisplay, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.openbar"); //$NON-NLS-1$
    new BooleanParameter(gDisplay, "Open Bar", false); //$NON-NLS-1$

    label = new Label(gDisplay, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.closetotray"); //$NON-NLS-1$
    new BooleanParameter(gDisplay, "Close To Tray", true); //$NON-NLS-1$

    label = new Label(gDisplay, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.minimizetotray"); //$NON-NLS-1$
    new BooleanParameter(gDisplay, "Minimize To Tray", false); //$NON-NLS-1$

    label = new Label(gDisplay, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.password"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 150;
    new PasswordParameter(gDisplay, "Password").setLayoutData(gridData); //$NON-NLS-1$

    label = new Label(gDisplay, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.passwordconfirm"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 150;
    new PasswordParameter(gDisplay, "Password Confirm").setLayoutData(gridData); //$NON-NLS-1$

    label = new Label(gDisplay, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.passwordmatch"); //$NON-NLS-1$
    passwordMatch = new Label(gDisplay, SWT.NULL);
    gridData = new GridData();
    gridData.widthHint = 150;
    passwordMatch.setLayoutData(gridData);

    itemDisplay.setControl(gDisplay);

    //CTabItem itemStart = new CTabItem(ctfConfig, SWT.NULL);
    //Messages.setLanguageText(itemStart, "ConfigView.section.start"); //$NON-NLS-1$ //general

    Group gStart = new Group(gDisplay, SWT.NULL);
    Messages.setLanguageText(gStart, "ConfigView.section.start"); //$NON-NLS-1$ //general
    
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gridData.horizontalSpan = 2;
    gStart.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 2;
    gStart.setLayout(layout);

    label = new Label(gStart, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.showsplash"); //$NON-NLS-1$
    new BooleanParameter(gStart, "Show Splash", true); //$NON-NLS-1$

    label = new Label(gStart, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.autoupdate"); //$NON-NLS-1$
    new BooleanParameter(gStart, "Auto Update", true); //$NON-NLS-1$

    label = new Label(gStart, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.openconsole"); //$NON-NLS-1$
    new BooleanParameter(gStart, "Open Console", false); //$NON-NLS-1$

    label = new Label(gStart, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.openconfig"); //$NON-NLS-1$
    new BooleanParameter(gStart, "Open Config", false); //$NON-NLS-1$

    label = new Label(gStart, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.startminimized"); //$NON-NLS-1$
    new BooleanParameter(gStart, "Start Minimized", false); //$NON-NLS-1$

  }

  private void initGroupTransfer() {
    GridData gridData;
    GridLayout layout;
    Label label;
    TabItem itemTransfer = new TabItem(tfConfig, SWT.NULL);
    //CTabItem itemTransfer = new CTabItem(ctfConfig, SWT.NULL);
    Messages.setLanguageText(itemTransfer, "ConfigView.section.transfer"); //$NON-NLS-1$

    Group gTransfer = new Group(tfConfig, SWT.NULL);
    //Group gTransfer = new Group(ctfConfig, SWT.NULL);

    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gTransfer.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 2;
    gTransfer.setLayout(layout);

    label = new Label(gTransfer, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.maxactivetorrents"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 40;
    new IntParameter(gTransfer, "max active torrents", 4).setLayoutData(gridData); //$NON-NLS-1$

    label = new Label(gTransfer, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.maxdownloads"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 40;
    new IntParameter(gTransfer, "max downloads", 4).setLayoutData(gridData); //$NON-NLS-1$

    label = new Label(gTransfer, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.maxclients"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 30;
    new IntParameter(gTransfer, "Max Clients", 0).setLayoutData(gridData); //$NON-NLS-1$
    
    label = new Label(gTransfer, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.slowconnect"); //$NON-NLS-1$
    new BooleanParameter(gTransfer, "Slow Connect", false); //$NON-NLS-1$
    
    label = new Label(gTransfer, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.maxuploads"); //$NON-NLS-1$
    final String upLabels[] = new String[99];
    final int upValues[] = new int[99];
    for (int i = 0; i < 99; i++) {
      upLabels[i] = " " + (i + 2); //$NON-NLS-1$
      upValues[i] = i + 2;
    }
    new IntListParameter(gTransfer, "Max Uploads", 4, upLabels, upValues); //$NON-NLS-1$

    label = new Label(gTransfer, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.maxuploadspeed"); //$NON-NLS-1$
    final String upsLabels[] = new String[upRates.length];
    final int upsValues[] = new int[upRates.length];
    upsLabels[0] = MessageText.getString("ConfigView.unlimited"); //$NON-NLS-1$
    upsValues[0] = 0;
    for (int i = 1; i < upRates.length; i++) {
      upsLabels[i] = " " + upRates[i] + "kB/s"; //$NON-NLS-1$ //$NON-NLS-2$
      upsValues[i] = 1024 * upRates[i];
    }
    new IntListParameter(gTransfer, "Max Upload Speed", 0, upsLabels, upsValues); //$NON-NLS-1$  

    itemTransfer.setControl(gTransfer);
  }

  private void initGroupDownloads() {
    GridData gridData;
    GridLayout layout;
    Label label;
    TabItem itemDownloads = new TabItem(tfConfig, SWT.NULL);
    //CTabItem itemDownloads = new CTabItem(ctfConfig, SWT.NULL);
    Messages.setLanguageText(itemDownloads, "ConfigView.section.seeding"); //$NON-NLS-1$

    Group gDownloads = new Group(tfConfig, SWT.NULL);    
    //Group gDownloads = new Group(ctfConfig, SWT.NULL);
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gDownloads.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 2;
    gDownloads.setLayout(layout);

    label = new Label(gDownloads, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.disconnetseed"); //$NON-NLS-1$
    new BooleanParameter(gDownloads, "Disconnect Seed", false); //$NON-NLS-1$

    label = new Label(gDownloads, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.switchpriority"); //$NON-NLS-1$
    new BooleanParameter(gDownloads, "Switch Priority", false); //$NON-NLS-1$
    
    label = new Label(gDownloads, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.stopRatio"); //$NON-NLS-1$
    final String stopRatioLabels[] = new String[11];
    final int stopRatioValues[] = new int[11];
    stopRatioLabels[0] = MessageText.getString("ConfigView.text.neverStop");
    stopRatioValues[0] = 0;
    for (int i = 1; i < 11; i++) {
      stopRatioLabels[i] = i + ":" + 1; //$NON-NLS-1$
      stopRatioValues[i] = i;
    }
    new IntListParameter(gDownloads, "Stop Ratio", 0, stopRatioLabels, stopRatioValues);
    
    label = new Label(gDownloads, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.stopRatioPeers"); //$NON-NLS-1$    
    final String stopRatioPeersLabels[] = new String[5];
    final int stopRatioPeersValues[] = new int[5];
    stopRatioPeersLabels[0] = MessageText.getString("ConfigView.text.neverStop");
    stopRatioPeersValues[0] = 0;
    String peers = MessageText.getString("ConfigView.text.peers");
    for (int i = 1; i < stopRatioPeersValues.length; i++) {
      stopRatioPeersLabels[i] = i + " " + peers; //$NON-NLS-1$
      stopRatioPeersValues[i] = i;
    }
    gridData = new GridData();
    gridData.verticalSpan = 2;
    new IntListParameter(gDownloads, "Stop Peers Ratio", 0, stopRatioPeersLabels, stopRatioPeersValues).setLayoutData(gridData);
    label = new Label(gDownloads,SWT.NULL);
    //Messages.setLanguageText(label,"ConfigView.label.onlyafter50");
    
    label = new Label(gDownloads, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.startRatioPeers"); //$NON-NLS-1$
    final String startRatioPeersLabels[] = new String[13];
    final int startRatioPeersValues[] = new int[13];
    startRatioPeersLabels[0] = MessageText.getString("ConfigView.text.neverStart");
    startRatioPeersValues[0] = 0;
    for (int i = 1; i < 13; i++) {
      startRatioPeersLabels[i] = (i + 3) + " " + peers; //$NON-NLS-1$
      startRatioPeersValues[i] = i + 3;
    }
    new IntListParameter(gDownloads, "Start Peers Ratio", 0, startRatioPeersLabels, startRatioPeersValues);
    
    String seeds = MessageText.getString("ConfigView.label.seeds");
    label = new Label(gDownloads, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.startNumSeeds"); //$NON-NLS-1$
    final String startNumSeedsLabels[] = new String[16];
    final int startNumSeedsValues[] = new int[16];
    startNumSeedsLabels[0] = MessageText.getString("ConfigView.text.neverStart");
    startNumSeedsValues[0] = 0;
    for (int i = 1; i < 16; i++) {
      startNumSeedsLabels[i] = i + " " + seeds; //$NON-NLS-1$
      startNumSeedsValues[i] = i;
    }
    gridData = new GridData();
    gridData.verticalSpan = 2;
    new IntListParameter(gDownloads, "Start Num Peers", 0, startNumSeedsLabels, startNumSeedsValues).setLayoutData(gridData);    
    label = new Label(gDownloads,SWT.NULL);
    //Messages.setLanguageText(label,"ConfigView.label.override");
    
    label = new Label(gDownloads, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.showpopuponclose"); //$NON-NLS-1$
    new BooleanParameter(gDownloads, "Alert on close", true);
    
    itemDownloads.setControl(gDownloads);
  }

  private void initGroupServer() {
    GridData gridData;
    GridLayout layout;
    Label label;
    TabItem itemServer = new TabItem(tfConfig, SWT.NULL);
    //CTabItem itemServer = new CTabItem(ctfConfig, SWT.NULL);
    Messages.setLanguageText(itemServer, "ConfigView.section.server"); //$NON-NLS-1$

    Group gServer = new Group(tfConfig, SWT.NULL);
    //Group gServer = new Group(ctfConfig, SWT.NULL);
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gServer.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 2;
    gServer.setLayout(layout);

    label = new Label(gServer, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.overrideip"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 100;
    new StringParameter(gServer, "Override Ip", "").setLayoutData(gridData); //$NON-NLS-1$ //$NON-NLS-2$

    label = new Label(gServer, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.bindip"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 100;
    new StringParameter(gServer, "Bind IP", "").setLayoutData(gridData); //$NON-NLS-1$ //$NON-NLS-2$
    
    label = new Label(gServer, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.serverportlow"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 40;
    new IntParameter(gServer, "Low Port", 6881).setLayoutData(gridData); //$NON-NLS-1$

    label = new Label(gServer, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.serverporthigh"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 40;
    new IntParameter(gServer, "High Port", 6889).setLayoutData(gridData); //$NON-NLS-1$

    itemServer.setControl(gServer);
  }

  private TabItem initGroupFile() {
  //private CTabItem initGroupFile() {
    GridData gridData;
    TabItem itemFile = new TabItem(tfConfig, SWT.NULL);
    //CTabItem itemFile = new CTabItem(ctfConfig, SWT.NULL);
    Messages.setLanguageText(itemFile, "ConfigView.section.files"); //$NON-NLS-1$

    Group gFile = new Group(tfConfig, SWT.NULL);
    //Group gFile = new Group(ctfConfig, SWT.NULL);

    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    gFile.setLayout(layout);
    Label label;
    
    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.zeronewfiles"); //$NON-NLS-1$
    BooleanParameter zeroNew = new BooleanParameter(gFile, "Zero New", false); //$NON-NLS-1$
    new Label(gFile, SWT.NULL);

    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.incrementalfile"); //$NON-NLS-1$
    BooleanParameter incremental = new BooleanParameter(gFile, "Enable incremental file creation", false); //$NON-NLS-1$
    new Label(gFile, SWT.NULL);

    //Make the incremental checkbox (button) deselect when zero new is used
    Button[] btnIncremental = {(Button)incremental.getControl()};
    zeroNew.setAdditionalActionPerformer(new ExclusiveSelectionActionPerformer(btnIncremental));
    
    //Make the zero new checkbox(button) deselct when incremental is used
    Button[] btnZeroNew = {(Button)zeroNew.getControl()}; 
    incremental.setAdditionalActionPerformer(new ExclusiveSelectionActionPerformer(btnZeroNew));
    
    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.usefastresume"); //$NON-NLS-1$
    BooleanParameter bpUseResume = new BooleanParameter(gFile, "Use Resume", false); //$NON-NLS-1$
    new Label(gFile, SWT.NULL);

    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.saveresumeinterval"); //$NON-NLS-1$
    final String saveResumeLabels[] = new String[19];
    final int saveResumeValues[] = new int[19];
    for (int i = 2; i < 21; i++) {
      saveResumeLabels[i - 2] = " " + i + " min"; //$NON-NLS-1$ //$NON-NLS-2$
      saveResumeValues[i - 2] = i;
    }
    Control[] controls = new Control[2];
    controls[0] = label;
    controls[1] = new IntListParameter(gFile, "Save Resume Interval", 5, saveResumeLabels, saveResumeValues).getControl(); //$NON-NLS-1$    
    IAdditionalActionPerformer performer = new ChangeSelectionActionPerformer(controls);
    bpUseResume.setAdditionalActionPerformer(performer);    
    new Label(gFile, SWT.NULL);
    
    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.defaultsavepath"); //$NON-NLS-1$

    gridData = new GridData();
    gridData.widthHint = 150;
    final StringParameter pathParameter = new StringParameter(gFile, "Default save path", ""); //$NON-NLS-1$ //$NON-NLS-2$
    pathParameter.setLayoutData(gridData);
    Button browse = new Button(gFile, SWT.PUSH);
    Messages.setLanguageText(browse, "ConfigView.button.browse"); //$NON-NLS-1$
    browse.addListener(SWT.Selection, new Listener() {
      /* (non-Javadoc)
       * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
       */
      public void handleEvent(Event event) {
        DirectoryDialog dialog = new DirectoryDialog(tfConfig.getShell(), SWT.APPLICATION_MODAL);
        //DirectoryDialog dialog = new DirectoryDialog(ctfConfig.getShell(), SWT.APPLICATION_MODAL);
        dialog.setFilterPath(pathParameter.getValue());
        dialog.setText(MessageText.getString("ConfigView.dialog.choosedefaultsavepath")); //$NON-NLS-1$
        String path = dialog.open();
        if (path != null) {
          pathParameter.setValue(path);
        }
      }
    });
    
    
    
    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.savetorrents"); //$NON-NLS-1$
    BooleanParameter saveTorrents = new BooleanParameter(gFile, "Save Torrent Files", true); //$NON-NLS-1$    
    new Label(gFile, SWT.NULL);
    
    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.defaultTorrentPath"); //$NON-NLS-1$

    gridData = new GridData();
    gridData.widthHint = 150;
    final StringParameter torrentPathParameter = new StringParameter(gFile, "General_sDefaultTorrent_Directory", ""); //$NON-NLS-1$ //$NON-NLS-2$
    torrentPathParameter.setLayoutData(gridData);
    Button browse2 = new Button(gFile, SWT.PUSH);
    Messages.setLanguageText(browse2, "ConfigView.button.browse"); //$NON-NLS-1$
    browse2.addListener(SWT.Selection, new Listener() {
      /* (non-Javadoc)
       * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
       */
      public void handleEvent(Event event) {
        DirectoryDialog dialog = new DirectoryDialog(tfConfig.getShell(), SWT.APPLICATION_MODAL);
        //DirectoryDialog dialog = new DirectoryDialog(ctfConfig.getShell(), SWT.APPLICATION_MODAL);
        dialog.setFilterPath(torrentPathParameter.getValue());
        dialog.setText(MessageText.getString("ConfigView.dialog.choosedefaulttorrentpath")); //$NON-NLS-1$
        String path = dialog.open();
        if (path != null) {
          torrentPathParameter.setValue(path);
        }
      }
    });
    
    controls = new Control[2];
    controls[0] = torrentPathParameter.getControl();
    controls[1] = browse2;
    IAdditionalActionPerformer grayPathAndButton1 = new ChangeSelectionActionPerformer(controls);
    saveTorrents.setAdditionalActionPerformer(grayPathAndButton1);
    
    
    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.movecompleted"); //$NON-NLS-1$
    BooleanParameter moveCompleted = new BooleanParameter(gFile, "Move Completed When Done", false); //$NON-NLS-1$    
    new Label(gFile, SWT.NULL);
    
    
    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.moveCompletedPath"); //$NON-NLS-1$

    gridData = new GridData();
    gridData.widthHint = 150;
    final StringParameter movePathParameter = new StringParameter(gFile, "Completed Files Directory", "");
    movePathParameter.setLayoutData(gridData);
    Button browse3 = new Button(gFile, SWT.PUSH);
    Messages.setLanguageText(browse3, "ConfigView.button.browse"); //$NON-NLS-1$
    browse3.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        DirectoryDialog dialog = new DirectoryDialog(tfConfig.getShell(), SWT.APPLICATION_MODAL);
        //DirectoryDialog dialog = new DirectoryDialog(ctfConfig.getShell(), SWT.APPLICATION_MODAL);
        dialog.setFilterPath(movePathParameter.getValue());
        dialog.setText(MessageText.getString("ConfigView.dialog.choosemovepath")); //$NON-NLS-1$
        String path = dialog.open();
        if (path != null) {
          movePathParameter.setValue(path);
        }
      }
    });
    
    controls = new Control[2];
    controls[0] = movePathParameter.getControl();
    controls[1] = browse3;
    IAdditionalActionPerformer grayPathAndButton2 = new ChangeSelectionActionPerformer(controls);
    moveCompleted.setAdditionalActionPerformer(grayPathAndButton2);
    
    
    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.priorityExtensions"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 100;
    new StringParameter(gFile, "priorityExtensions", "").setLayoutData(gridData); //$NON-NLS-1$       
    new Label(gFile, SWT.NULL);
    
    
    label = new Label(gFile, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.checkOncompletion"); //$NON-NLS-1$
    new BooleanParameter(gFile, "Check Pieces on Completion", true);

    itemFile.setControl(gFile);
    return itemFile;
  }
  
  private void initStats() {
	 GridData gridData;
	 GridLayout layout;
	 Label label;
   TabItem itemStats = new TabItem(tfConfig, SWT.NULL);
	 //CTabItem itemStats = new CTabItem(ctfConfig, SWT.NULL);
	 Messages.setLanguageText(itemStats, "ConfigView.section.stats"); //$NON-NLS-1$

   Group gStats = new Group(tfConfig, SWT.NULL);
	 //Group gStats = new Group(ctfConfig, SWT.NULL);
	 gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
	 gStats.setLayoutData(gridData);
	 layout = new GridLayout();
	 layout.numColumns = 3;
	 gStats.setLayout(layout);

		// row
		
	 label = new Label(gStats, SWT.NULL);
	 Messages.setLanguageText(label, "ConfigView.section.stats.enable"); //$NON-NLS-1$
	 BooleanParameter enableStats = new BooleanParameter(gStats, "Stats Enable", false); //$NON-NLS-1$

	 label = new Label(gStats, SWT.NULL);

   Control[] controls = new Control[4];
   
		// row
		
	 label = new Label(gStats, SWT.NULL);
	 Messages.setLanguageText(label, "ConfigView.section.stats.defaultsavepath"); //$NON-NLS-1$

	 gridData = new GridData();
	 gridData.widthHint = 150;
   final StringParameter pathParameter = new StringParameter(gStats, "Stats Dir", ""); //$NON-NLS-1$ //$NON-NLS-2$
   pathParameter.setLayoutData(gridData);
   controls[0] = pathParameter.getControl();
   Button browse = new Button(gStats, SWT.PUSH);
   Messages.setLanguageText(browse, "ConfigView.button.browse"); //$NON-NLS-1$
   controls[1] = browse;
   browse.addListener(SWT.Selection, new Listener() {
	  /* (non-Javadoc)
	   * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	   */
	  public void handleEvent(Event event) {
      DirectoryDialog dialog = new DirectoryDialog(tfConfig.getShell(), SWT.APPLICATION_MODAL);
		//DirectoryDialog dialog = new DirectoryDialog(ctfConfig.getShell(), SWT.APPLICATION_MODAL);
		dialog.setFilterPath(pathParameter.getValue());
		dialog.setText(MessageText.getString("ConfigView.section.stats.choosedefaultsavepath")); //$NON-NLS-1$
		String path = dialog.open();
		if (path != null) {
		  pathParameter.setValue(path);
		}
	  }
	});

	// row
		
	label = new Label(gStats, SWT.NULL);
	Messages.setLanguageText(label, "ConfigView.section.stats.savefile"); //$NON-NLS-1$
	
	gridData = new GridData();
	gridData.widthHint = 150;
	final StringParameter fileParameter = new StringParameter(gStats, "Stats File", StatsWriterPeriodic.DEFAULT_STATS_FILE_NAME ); 
  fileParameter.setLayoutData(gridData);
  controls[2] = fileParameter.getControl();
	label = new Label(gStats, SWT.NULL);

		// row
		
	label = new Label(gStats, SWT.NULL);
		
	Messages.setLanguageText(label, "ConfigView.section.stats.savefreq"); 
	final String spLabels[] = new String[statsPeriods.length];
	final int spValues[] = new int[statsPeriods.length];
	for (int i = 0; i < statsPeriods.length; i++) {
		int	num = statsPeriods[i];
		
		if ( num%3600 ==0 ){
	
			spLabels[i] = " " + (statsPeriods[i]/3600) + " " + MessageText.getString("ConfigView.section.stats.hours" );
		
		}else if ( num%60 ==0 ){
	
			spLabels[i] = " " + (statsPeriods[i]/60) + " " + MessageText.getString("ConfigView.section.stats.minutes" );
		
		}else{
	
			spLabels[i] = " " + statsPeriods[i] + " " + MessageText.getString("ConfigView.section.stats.seconds" );
		}
		
		spValues[i] = statsPeriods[i];
	}
	
  
  controls[3] = new IntListParameter(gStats, "Stats Period", 0, spLabels, spValues).getControl();  
  enableStats.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(controls));
 
	itemStats.setControl(gStats);
   }

  private void initStyle() {
    GridData gridData;
   GridLayout layout;
   Label label;
   TabItem itemStyle = new TabItem(tfConfig, SWT.NULL);
   //CTabItem itemStats = new CTabItem(ctfConfig, SWT.NULL);
   Messages.setLanguageText(itemStyle, "ConfigView.section.style"); //$NON-NLS-1$

   Group gStyle = new Group(tfConfig, SWT.NULL);
   //Group gStats = new Group(ctfConfig, SWT.NULL);
   gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
   gStyle.setLayoutData(gridData);
   layout = new GridLayout();
   layout.numColumns = 2;
   gStyle.setLayout(layout);
   
   label = new Label(gStyle, SWT.NULL);
   Messages.setLanguageText(label, "ConfigView.section.style.useCustomTabs"); //$NON-NLS-1$
   BooleanParameter useCustomTabs = new BooleanParameter(gStyle, "useCustomTab",true); //$NON-NLS-1$
   
   String osName = System.getProperty("os.name");
   if (osName.equals("Windows XP")) {
     label = new Label(gStyle, SWT.NULL);
     Messages.setLanguageText(label, "ConfigView.section.style.enableXPStyle"); //$NON-NLS-1$
     final Button enableXPStyle = new Button(gStyle, SWT.CHECK);
     boolean enabled = false;
     boolean valid = true;
     try {
       File f =
         new File(
           System.getProperty("java.home")
             + "\\bin\\javaw.exe.manifest");
       if (f.exists()) {
         enabled = true;
       }
     } catch (Exception e) {
       e.printStackTrace();
       valid = false;
     }
     enableXPStyle.setEnabled(valid);
     enableXPStyle.setSelection(enabled);
     enableXPStyle.addListener(SWT.Selection, new Listener() {
       public void handleEvent(Event arg0) {
         //In case we enable the XP Style
         if (enableXPStyle.getSelection()) {
           try {
             File fDest =
               new File(
                 System.getProperty("java.home")
                   + "\\bin\\javaw.exe.manifest");
             File fOrigin = new File("javaw.exe.manifest");
             if (!fDest.exists() && fOrigin.exists()) {
               FileUtil.copyFile(fOrigin, fDest);
             }
           } catch (Exception e) {
             e.printStackTrace();
           }
         } else {
           try {
             File fDest =
               new File(
                 System.getProperty("java.home")
                   + "\\bin\\javaw.exe.manifest");
             fDest.delete();
           } catch (Exception e) {
             e.printStackTrace();
           }
         }
       }
     });
   }
   
   label = new Label(gStyle, SWT.NULL);
   Messages.setLanguageText(label, "ConfigView.section.style.colorScheme"); //$NON-NLS-1$
   ColorParameter colorScheme = new ColorParameter(gStyle, "Color Scheme",0,128,255); //$NON-NLS-1$
   gridData = new GridData();
   gridData.widthHint = 50;
   colorScheme.setLayoutData(gridData);
   
   itemStyle.setControl(gStyle);
  }
  
  
  
 	private void 
 	initTracker() 
  	{
		GridData gridData;
		GridLayout layout;
		Label label;
		TabItem itemStats = new TabItem(tfConfig, SWT.NULL);
		Messages.setLanguageText(itemStats, "ConfigView.section.tracker"); //$NON-NLS-1$

		Group gTracker = new Group(tfConfig, SWT.NULL);
		
	    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
	    
		gTracker.setLayoutData(gridData);
		
		layout = new GridLayout();
		
		layout.numColumns = 3;
		
		gTracker.setLayout(layout);

		// row
		
		label = new Label(gTracker, SWT.NULL);
		
		Messages.setLanguageText(label, "ConfigView.section.tracker.pollinterval"); 
		
		label = new Label(gTracker, SWT.NULL);
		
		Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalmin"); 
		
		IntParameter pollIntervalMin = new IntParameter(gTracker, "Tracker Poll Interval Min", TRHost.DEFAULT_MIN_RETRY_DELAY );
	
		gridData = new GridData();
		gridData.widthHint = 30;
		pollIntervalMin.setLayoutData( gridData );
		
		// row
		
		label = new Label(gTracker, SWT.NULL);
				
		label = new Label(gTracker, SWT.NULL);
		
		Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalmax"); 
		
		IntParameter pollIntervalMax = new IntParameter(gTracker, "Tracker Poll Interval Max", TRHost.DEFAULT_MAX_RETRY_DELAY );
	
		gridData = new GridData();
		gridData.widthHint = 30;
		pollIntervalMax.setLayoutData( gridData );
		
		// row
		
		label = new Label(gTracker, SWT.NULL);
				
		label = new Label(gTracker, SWT.NULL);
		
		Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalincby"); 
		
		IntParameter pollIntervalIncBy = new IntParameter(gTracker, "Tracker Poll Inc By", TRHost.DEFAULT_INC_BY );
		
		gridData = new GridData();
		gridData.widthHint = 30;
		pollIntervalIncBy.setLayoutData( gridData );
		
		// row
		
		label = new Label(gTracker, SWT.NULL);
				
		label = new Label(gTracker, SWT.NULL);
		
		Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalincper"); 
		
		IntParameter pollIntervalIncPer = new IntParameter(gTracker, "Tracker Poll Inc Per", TRHost.DEFAULT_INC_PER );
		
		gridData = new GridData();
		gridData.widthHint = 30;
		pollIntervalIncPer.setLayoutData( gridData );
		
	   // row
		
	  label = new Label(gTracker, SWT.NULL);
		
	  Messages.setLanguageText(label, "ConfigView.section.tracker.ip"); 
		
	  final StringParameter tracker_ip = new StringParameter(gTracker, "Tracker IP", "" );
	  
	  gridData = new GridData();
	  gridData.widthHint = 100;

	  tracker_ip.setLayoutData( gridData );
	  
	  Button check_button = new Button(gTracker, SWT.PUSH);
	  
	  Messages.setLanguageText(check_button, "ConfigView.section.tracker.checkip"); //$NON-NLS-1$

      final Display display = gTracker.getDisplay();
    
	  check_button.addListener(SWT.Selection, new Listener() {

 	  public void 
	  handleEvent(Event event) 
	  {
      IpCheckerWizard wizard = new IpCheckerWizard(cConfig.getDisplay());
      wizard.setIpSetterCallBack(new IpSetterCallBack() {
        public void setIp(final String ip) {
          if(display == null || display.isDisposed())
            return;
            display.asyncExec(new Runnable() {
            public void run() {
              if(tracker_ip != null)
                tracker_ip.setValue(ip);
            }
          });
        }
       });

		 }
	   });
		
	  // row
		
	  label = new Label(gTracker, SWT.NULL);
		
	  Messages.setLanguageText(label, "ConfigView.section.tracker.port"); 
		
	  IntParameter tracker_port = new IntParameter(gTracker, "Tracker Port", TRHost.DEFAULT_PORT );

	  gridData = new GridData();
	  gridData.widthHint = 50;

	  tracker_port.setLayoutData( gridData );

	  label = new Label(gTracker, SWT.NULL);
		
			// row
			
		label = new Label(gTracker, SWT.NULL);
		
		Messages.setLanguageText(label, "ConfigView.section.tracker.publishenable"); 
		
		BooleanParameter enablePublish = new BooleanParameter(gTracker, "Tracker Publish Enable", true);

		label = new Label(gTracker, SWT.NULL);


	 	itemStats.setControl(gTracker);
	}
	
  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.IView#getComposite()
   */
  public Composite getComposite() {
    return cConfig;
  }

  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.IView#refresh()
   */
  public void refresh() {
    byte[] password = COConfigurationManager.getByteParameter("Password", "".getBytes());
    COConfigurationManager.setParameter("Password enabled", false);
    if (password.length == 0) {
      passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchnone"));
    }
    else {
      byte[] confirm = COConfigurationManager.getByteParameter("Password Confirm", "".getBytes());
      if (confirm.length == 0) {
        passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchno"));
      }
      else {
        boolean same = true;
        for (int i = 0; i < password.length; i++) {
          if (password[i] != confirm[i])
            same = false;
        }
        if (same) {
          passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchyes"));
          COConfigurationManager.setParameter("Password enabled", true);
        }
        else {
          passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchno"));
        }
      }
    }

    if (table == null || table.isDisposed() || noChange)
      return;
    noChange = true;
    TableItem[] items = table.getItems();
    for (int i = 0; i < items.length; i++) {
      IpRange range = (IpRange) items[i].getData();
      if (items[i] == null || items[i].isDisposed())
        continue;
      String tmp = items[i].getText(0);
      if (range.getDescription() != null && !range.getDescription().equals(tmp))
        items[i].setText(0, range.getDescription());

      tmp = items[i].getText(1);
      if (range.getStartIp() != null && !range.getStartIp().equals(tmp))
        items[i].setText(1, range.getStartIp());

      tmp = items[i].getText(2);
      if (range.getEndIp() != null && !range.getEndIp().equals(tmp))
        items[i].setText(2, range.getEndIp());

    }
  }

  public void updateLanguage() {
    super.updateLanguage();
    tfConfig.setSize(tfConfig.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    //ctfConfig.setSize(ctfConfig.computeSize(SWT.DEFAULT, SWT.DEFAULT));
  }

  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.IView#delete()
   */
  public void delete() {
    MainWindow.getWindow().setConfig(null);
    Utils.disposeComposite(tfConfig);
    //Utils.disposeComposite(ctfConfig);
  }

  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.IView#getFullTitle()
   */
  public String getFullTitle() {
    return MessageText.getString("ConfigView.title.full"); //$NON-NLS-1$
  }

  private void populateTable() {
    List ipRanges = filter.getIpRanges();
    Display display = cConfig.getDisplay();
    if(display == null || display.isDisposed()) {
      return;
    }
    synchronized (ipRanges) {
      Iterator iter = ipRanges.iterator();
      while (iter.hasNext()) {
        final IpRange range = (IpRange) iter.next();
        display.asyncExec(new Runnable() {
          public void run() {
            if(table == null || table.isDisposed())
              return;
            TableItem item = new TableItem(table, SWT.NULL);
            item.setImage(0, ImageRepository.getImage("ipfilter"));
            item.setText(0, range.getDescription());
            item.setText(1, range.getStartIp());
            item.setText(2, range.getEndIp());
            item.setData(range);
          }
        });        
      }
    }
  }

  public void removeRange(IpRange range) {
    List ranges = filter.getIpRanges();
    synchronized (ranges) {
      ranges.remove(range);
    }
    noChange = false;
  }

  public void editRange(IpRange range) {
    new IpFilterEditor(tfConfig.getDisplay(), table, filter.getIpRanges(), range);
    noChange = false;
    //new IpFilterEditor(ctfConfig.getDisplay(), table, filter.getIpRanges(), range);
  }

  public void addRange() {
    new IpFilterEditor(tfConfig.getDisplay(), table, filter.getIpRanges(), null);
    noChange = false;
    //new IpFilterEditor(ctfConfig.getDisplay(), table, filter.getIpRanges(), null);
  }

}
