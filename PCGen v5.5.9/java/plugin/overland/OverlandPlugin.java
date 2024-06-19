package plugin.overland;

import gmgen.GMGenSystemView;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.HelpMenuItemAddMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import plugin.overland.gui.OverPanel;

/**
 * The <code>Overland Plugin</code> provides a number
 * of useful utilities that help with overland travel <br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class OverlandPlugin extends GMBPlugin {
	/** The plugin menu item in the help menu. */
	private JMenuItem overHelpItem = new JMenuItem();

	/** The plugin menu item in the tools menu. */
	private JMenuItem overToolsItem = new JMenuItem();

	/** The user interface that this class will be using. */
	private OverPanel theView;

	/** The English name of the plugin. */
	private String name = "Overland Travel";

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00";

	public static final String LOG_NAME = "Overland Travel";

	/**
	 * Creates a new instance of OverlandPlugin
	 */
	public OverlandPlugin() {
		String datadir=this.getDataDir();
		theView = new OverPanel(datadir);
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
		overToolsItem.setMnemonic('O');
		overToolsItem.setText("Overland Travel");
		overToolsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, overToolsItem));

		overHelpItem.setMnemonic('O');
		overHelpItem.setText("Overland Travel");
		overHelpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				helpMenuItem(evt);
			}
		});
		GMBus.send(new HelpMenuItemAddMessage(this, overHelpItem));
	}

	public void toolMenuItem(ActionEvent evt) {
		JTabbedPane tp = GMGenSystemView.getTabPane();
		for(int i = 0; i < tp.getTabCount(); i++) {
			if(tp.getComponentAt(i) instanceof OverPanel) {
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
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message) {
		if(message instanceof StateChangedMessage) {
			if(GMGenSystemView.getTabPane().getSelectedComponent() instanceof OverPanel) {
				overHelpItem.setEnabled(false);
				overToolsItem.setEnabled(false);
			}
/*		else if(message instanceof StateChangedMessage) {
				 theView.populateEquipList();
		} */ /*
		else {
				overHelpItem.setEnabled(true);
				overToolsItem.setEnabled(true);
			}
*/		}
	}
}
