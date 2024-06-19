package plugin.doomsdaybook;

import gmgen.GMGenSystemView;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import plugin.doomsdaybook.gui.NameGenPanel;

/**
 * The <code>ExperienceAdjusterController</code> handles the functionality of
 * the Adjusting of experience.  This class is called by the <code>GMGenSystem
 * </code> and will have it's own model and view.<br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class RandomNamePlugin extends GMBPlugin {
	/** The plugin menu item in the tools menu. */
	private JMenuItem nameToolsItem = new JMenuItem();

	/** The user interface that this class will be using. */
	private NameGenPanel theView;

	/** The English name of the plugin. */
	private String name = "Random Names";

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00";

	public static final String LOG_NAME = "Random Name Generator";

	/**
	 * Creates a new instance of TreasurePlugin
	 */
	public RandomNamePlugin() {
		theView = new NameGenPanel(new File(getDataDir()));
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
	 * @returns name
	 */
	public String getName() { return name; }

	/**
	 * Accessor for version
	 * @returns version
	 */
	public String getVersion() { return version; }

	public void initMenus() {
		nameToolsItem.setMnemonic('R');
		nameToolsItem.setText("Random Name Generator");
		nameToolsItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, nameToolsItem));
	}

	public void toolMenuItem(ActionEvent evt) {
		JTabbedPane tp = GMGenSystemView.getTabPane();
		for(int i = 0; i < tp.getTabCount(); i++) {
			if(tp.getComponentAt(i) instanceof NameGenPanel) {
				tp.setSelectedIndex(i);
			}
		}
	}

	/**
	 * Gets the view that this class is using.
	 * @return the view.
	 */
	public Component getView() {
		return theView;
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message) {
		if(message instanceof StateChangedMessage) {
			if(GMGenSystemView.getTabPane().getSelectedComponent() instanceof NameGenPanel) {
				nameToolsItem.setEnabled(false);
			}
			else {
				nameToolsItem.setEnabled(true);
			}
		}
	}
}

