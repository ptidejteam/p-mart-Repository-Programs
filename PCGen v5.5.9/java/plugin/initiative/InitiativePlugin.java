package plugin.initiative;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.io.SimpleFileFilter;
import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.CombatRequestMessage;
import gmgen.pluginmgr.messages.FileOpenMessage;
import gmgen.pluginmgr.messages.HelpMenuItemAddMessage;
import gmgen.pluginmgr.messages.InitHolderListSendMessage;
import gmgen.pluginmgr.messages.OpenPCGRequestMessage;
import gmgen.pluginmgr.messages.PCClosedMessage;
import gmgen.pluginmgr.messages.PCLoadedMessage;
import gmgen.pluginmgr.messages.PreferencesPanelAddMessage;
import gmgen.pluginmgr.messages.SaveMessage;
import gmgen.pluginmgr.messages.SavePCGRequestMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import gmgen.pluginmgr.messages.WindowClosedMessage;
import gmgen.util.LogUtilities;
import gmgen.util.MiscUtilities;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import plugin.initiative.gui.Initiative;
import plugin.initiative.gui.PreferencesDamagePanel;
import plugin.initiative.gui.PreferencesMassiveDamagePanel;

/**
 * The <code>ExperienceAdjusterController</code> handles the functionality of
 * the Adjusting of experience.  This class is called by the <code>GMGenSystem
 * </code> and will have it's own model and view.<br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class InitiativePlugin extends GMBPlugin {
	/** The plugin menu item in the help menu. */
	private JMenuItem initHelpItem = new JMenuItem();

	/** The plugin menu item in the tools menu. */
	private JMenuItem initToolsItem = new JMenuItem();

	/** The user interface that this class will be using. */
	private Initiative theView;

	/** The English name of the plugin. */
	private String name = "Initiative";

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00";

	public static final String LOG_NAME = "Initiative";

	/**
	 * Creates a new instance of TreasurePlugin
	 */
	public InitiativePlugin() {
		theView = new Initiative(GMGenSystem.inst);
		GMBus.send(new PreferencesPanelAddMessage(this, name, new PreferencesDamagePanel()));
		GMBus.send(new PreferencesPanelAddMessage(this, name, new PreferencesMassiveDamagePanel()));
		//GMBus.send(new PreferencesPanelAddMessage(this, name, new PreferencesTrackingPanel(theView)));
		//GMBus.send(new PreferencesPanelAddMessage(this, name, new PreferencesMiscPanel(theView)));
		theView.setLog(LogUtilities.inst());
	}

	/**
	 * Starts the plugin, registering itself with the <code>TabAddMessage</code>.
	 */
	public void start() {
		GMBus.send(new TabAddMessage(this, name, getView()));
		initMenus();
	}

	/**
	 * Accessor for name
	 * @return name
	 */
	public String getName() { return name; }

	/**
	 * Accessor for version
	 * @return version
	 */
	public String getVersion() { return version; }

	public void initMenus() {
		initToolsItem.setMnemonic('I');
		initToolsItem.setText("Initiative");
		initToolsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				initMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, initToolsItem));

		initHelpItem.setMnemonic('I');
		initHelpItem.setText("Initiative");
		initHelpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				helpMenuItem(evt);
			}
		});
		GMBus.send(new HelpMenuItemAddMessage(this, initHelpItem));
	}

	public void initMenuItem(ActionEvent evt) {
		JTabbedPane tp = GMGenSystemView.getTabPane();
		for(int i = 0; i < tp.getTabCount(); i++) {
			if(tp.getComponentAt(i) instanceof Initiative) {
				tp.setSelectedIndex(i);
			}
		}
	}

	public void helpMenuItem(ActionEvent evt) {
	}

	/**
	 * Gets the view that this class is using.
	 * @return the view.
	 */
	public Component getView() {
		return theView;
	}

		/**
	 * Handles the clicking of the <b>Add</b> button on the GUI.
	 */
	public void handleAddButton() {
		JFileChooser chooser = new JFileChooser();
		File defaultFile = SettingsHandler.getPcgPath();
		if(defaultFile.exists()) {
			chooser.setCurrentDirectory(defaultFile);
		}
		String[] pcgs = new String[] {"pcg", "pcp"};
		String[] init = new String[] {"gmi", "init"};
		SimpleFileFilter ff = new SimpleFileFilter(pcgs, "PCGen File");
		chooser.addChoosableFileFilter(ff);
		chooser.addChoosableFileFilter(new SimpleFileFilter(init, "Initiative Export"));
		chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
		chooser.setFileFilter(ff);
		chooser.setMultiSelectionEnabled(true);
		Cursor saveCursor = MiscUtilities.setBusyCursor(theView);
		int option = chooser.showOpenDialog(theView);
		if(option == JFileChooser.APPROVE_OPTION) {
			File [] pcFiles = chooser.getSelectedFiles();
			for(int i = 0; i < pcFiles.length; i++) {
				SettingsHandler.setPcgPath(pcFiles[i].getParentFile());
				if(pcFiles[i].toString().endsWith(".pcg") || pcFiles[i].toString().endsWith(".pcp")) {
					GMBus.send(new OpenPCGRequestMessage(this, pcFiles[i], false));
					//loadPCG(pcFiles[i]);
				}
				else if(pcFiles[i].toString().endsWith(".init") || pcFiles[i].toString().endsWith(".gmi")) {
					loadINIT(pcFiles[i]);
				}
			}  /* loop through selected files */
			theView.refreshTable();
		}
		else {	/* this means the file is invalid */
		}
		MiscUtilities.setCursor(theView, saveCursor);
	}

	public void handleSaveButton() {
		for (int i = 0; i < theView.initList.size(); i++) {
			InitHolder iH = (InitHolder) theView.initList.get(i);
			if(iH instanceof PcgCombatant) {
				PcgCombatant pcgcbt = (PcgCombatant) iH;
				GMBus.send(new SavePCGRequestMessage(this, pcgcbt.getPC()));
			}
		}
		theView.saveToFile();
	}

	public void loadINIT(File initFile) {
		theView.loadINIT(initFile, this);
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message) {
		if(message instanceof FileOpenMessage) {
			handleFileOpenMessage((FileOpenMessage)message);
		}
		else if(message instanceof SaveMessage) {
			handleSaveMessage((SaveMessage)message);
		}
		else if(message instanceof InitHolderListSendMessage) {
			handleInitHolderListSendMessage((InitHolderListSendMessage)message);
		}
		else if(message instanceof PCLoadedMessage) {
			handlePCLoadedMessage((PCLoadedMessage)message);
		}
		else if(message instanceof PCClosedMessage) {
			handlePCClosedMessage((PCClosedMessage)message);
		}
		else if (message instanceof WindowClosedMessage) {
			handleWindowClosedMessage((WindowClosedMessage)message);
		}
		else if(message instanceof StateChangedMessage) {
			handleStateChangedMessage((StateChangedMessage)message);
		}
		else if(message instanceof CombatRequestMessage) {
			handleCombatRequestMessage((CombatRequestMessage)message);
		}
	}

	public void handleInitHolderListSendMessage(InitHolderListSendMessage message) {
		if(message.getSource() != this) {
			InitHolderList cl = message.getInitHolderList();
			for(int i= 0; i < cl.size(); i++) {
				InitHolder iH = (InitHolder)cl.get(i);
				theView.addInitHolder(iH);
			}
			theView.refreshTable();
		}
	}

	public void handleCombatRequestMessage(CombatRequestMessage message) {
		message.setCombat(theView.initList);
	}

	public void handleFileOpenMessage(FileOpenMessage message) {
		if(GMGenSystemView.getTabPane().getSelectedComponent() instanceof Initiative) {
				handleAddButton();
		}
	}

	public void handleSaveMessage(SaveMessage message) {
		handleSaveButton();
	}


	public void handlePCClosedMessage(PCClosedMessage message) {
		theView.removePcgCombatant(message.getPC());
		theView.refreshTable();
	}

	public void handlePCLoadedMessage(PCLoadedMessage message) {
		if(!message.isIgnored(this)) {
			PlayerCharacter pc = message.getPC();
			String type = "PC";
			String player = pc.getPlayersName();

			//Based on the Player's name, auto set the combatant's type
			if(player.equalsIgnoreCase("Ally")) {
				type = "Ally";
			}
			else if(player.equalsIgnoreCase("GM") || player.equalsIgnoreCase("DM") || player.equalsIgnoreCase("Enemy")) {
				type = "Enemy";
			}
			else if(player.equals("-")) {
				type = "-";
			}

			theView.addPcgCombatant(pc, type);
			theView.refreshTable();
		}
	}

	public void handleStateChangedMessage(StateChangedMessage message) {
		if(GMGenSystemView.getTabPane().getSelectedComponent() instanceof Initiative) {
			initHelpItem.setEnabled(true);
			initToolsItem.setEnabled(false);
			GMGenSystem.inst.openFileItem.setEnabled(true);
			GMGenSystem.inst.saveFileItem.setEnabled(true);
			theView.refreshTable();
			theView.refreshTabs();
		}
		else {
			initHelpItem.setEnabled(false);
			initToolsItem.setEnabled(true);
		}
	}

	public void handleWindowClosedMessage(WindowClosedMessage message) {
		theView.setExitPrefs();
	}
}
