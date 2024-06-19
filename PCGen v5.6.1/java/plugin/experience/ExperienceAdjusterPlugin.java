package plugin.experience;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.plugin.Combatant;
import gmgen.plugin.InitHolderList;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.CombatRequestMessage;
import gmgen.pluginmgr.messages.PreferencesPanelAddMessage;
import gmgen.pluginmgr.messages.SaveMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pcgen.core.utils.Utility;
import pcgen.core.SettingsHandler;
import plugin.experience.gui.AddDefeatedCombatant;
import plugin.experience.gui.ExperienceAdjusterView;
import plugin.experience.gui.PreferencesExperiencePanel;

/**
 * The <code>ExperienceAdjusterController</code> handles the functionality of
 * the Adjusting of experience.  This class is called by the <code>GMGenSystem
 * </code> and will have it's own model and view.<br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class ExperienceAdjusterPlugin extends GMBPlugin implements ActionListener, ChangeListener, KeyListener /*Observer*/ {
	/** The plugin menu item in the tools menu. */
	protected JMenuItem experienceToolsItem = new JMenuItem();

	/** The model that holds all the data for this section. */
	protected ExperienceAdjusterModel eaModel;

	/** The user interface that this class will be using. */
	protected ExperienceAdjusterView  eaView;

	/** The English name of the plugin. */
	protected String name = "Experience";

	/** The version number of the plugin. */
	protected String version = "01.00.99.01.00";

	protected InitHolderList initList;

	public static final String LOG_NAME = "Experience_Adjuster";

	/**
	 * Creates a new instance of ExperienceAdjusterModel taking in a
	 */
	public ExperienceAdjusterPlugin() {
		eaModel = new ExperienceAdjusterModel(getDataDir());
		eaView = new ExperienceAdjusterView();
		GMBus.send(new PreferencesPanelAddMessage(this, name, new PreferencesExperiencePanel()));
		initListeners();
		update();
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
		experienceToolsItem.setMnemonic('E');
		experienceToolsItem.setText("Experience Adjuster");
		experienceToolsItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, experienceToolsItem));
	}

	public void toolMenuItem(java.awt.event.ActionEvent evt) {
		JTabbedPane tp = GMGenSystemView.getTabPane();
		for(int i = 0; i < tp.getTabCount(); i++) {
			if(tp.getComponentAt(i) instanceof ExperienceAdjusterView) {
				tp.setSelectedIndex(i);
			}
		}
	}

	/**
	 * Calls the appropriate methods depending on the source of the action.
	 * @param e the action even that happened.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == eaView.getAddExperienceToCharButton()) {
			handleAddExperienceToCharButton();
		}
		if(e.getSource() == eaView.getAddExperienceToPartyButton()) {
			handleAddExperienceToPartyButton();
		}
		if(e.getSource() == eaView.getAddEnemyButton()) {
			handleAddEnemyButton();
		}
		if(e.getSource() == eaView.getRemoveEnemyButton()) {
			handleRemoveEnemyButton();
		}
		if(e.getSource() == eaView.getAdjustCRButton()) {
			handleAdjustCRButton();
		}
	}

	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == eaView.getExperienceMultSlider()) {
			handleMultiplierSlider();
		}
	}

	/**
	 * Gets the model that this class is using.
	 * @return the model.
	 */
	public ExperienceAdjusterModel getModel() {
		return eaModel;
	}

	/**
	 * Gets the view that this class is using.
	 * @return the view.
	 */
	public JPanel getView() {
		return eaView;
	}

	public void handleAdjustCRButton() {
		if(eaView.getCharacterList().getSelectedIndex() != -1) {
			Object[] list = eaView.getCharacterList().getSelectedValues();
			for(int i = 0; i < list.length; i++) {
				ExperienceListItem item = (ExperienceListItem)list[i];
				Combatant cbt = item.getCombatant();
				adjustCR(cbt);
			}
		}
		if(eaView.getEnemyList().getSelectedIndex() != -1) {
			Object[] list = eaView.getEnemyList().getSelectedValues();
			for(int i = 0; i < list.length; i++) {
				ExperienceListItem item = (ExperienceListItem)list[i];
				Combatant cbt = item.getCombatant();
				adjustCR(cbt);
			}
		}
		update();
	}

	public void adjustCR(Combatant cbt) {
		String inputValue = JOptionPane.showInputDialog(GMGenSystem.inst, "CR", cbt.getCR() + "");
		if (inputValue != null) {
			try {
				Integer intValue = new Integer(inputValue);
				cbt.setCR(intValue.intValue());
			}
			catch (NumberFormatException e) {
				adjustCR(cbt);
			}
		}
	}

	/**
	 * Handles the <b>Add Experience to Character</code> button on the GUI.
	 */
	public void handleAddExperienceToCharButton() {
		int newEXP = 0;
		if(eaView.getCharacterList().getSelectedIndex() != -1) {
			try {
				Object[] list = eaView.getCharacterList().getSelectedValues();
				for(int i = 0; i < list.length; i++) {
					eaModel.addExperienceToCharacter((ExperienceListItem)list[i], Integer.parseInt(eaView.getExperienceField().getText()));
				}
			} catch(NumberFormatException e) {
				eaView.setExperienceToAdd("");
			}
		}
		eaView.getCharacterList().updateUI();
		eaView.getExperienceField().setText( "0" );
	}

	public void handleAddEnemyButton() {
		AddDefeatedCombatant dialog = new AddDefeatedCombatant(GMGenSystem.inst, true, eaModel);
		dialog.setVisible(true);
		handleGroupBox();
		update();
		this.eaView.getEnemyList().updateUI();
	}

	public void handleRemoveEnemyButton() {
		if(eaView.getEnemyList().getSelectedIndex() != -1) {
			Object[] list = eaView.getEnemyList().getSelectedValues();
			for(int i = 0; i < list.length; i++) {
				eaModel.removeEnemy((ExperienceListItem)list[i]);
			}
		}
		handleGroupBox();
		update();
		this.eaView.getEnemyList().updateUI();
	}

	/**
	 * Handles the <b>Add Experience to Group</b> button on the GUI.
	 */
	public void handleAddExperienceToPartyButton() {
		eaModel.addExperienceToParty();
		this.eaView.getCharacterList().updateUI();
		eaModel.clearEnemies();
		handleGroupBox();
	}

	public void handleMultiplierSlider() {
		int value = eaView.getExperienceMultSlider().getValue();
		double realValue = 1.0 + (value * 0.1);
		eaModel.setMultiplier(realValue);
		if(Utility.doublesEqual(realValue, 0.5)) {
			eaView.getExperienceMultNameLabel().setText("Half as Hard");
		}
		else if(realValue <= .7) {
			eaView.getExperienceMultNameLabel().setText("Much Easier");
		}
		else if(realValue > .7 && realValue < 1.5) {
			eaView.getExperienceMultNameLabel().setText("Normal");
		}
		else if(realValue >= 1.5) {
			eaView.getExperienceMultNameLabel().setText("Much Harder");
		}
		if(Utility.doublesEqual(realValue, 2)) {
			eaView.getExperienceMultNameLabel().setText("Twice as Hard");
		}
		java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(1);

		eaView.getExperienceMultLabel().setText(nf.format(realValue) + "X");
		handleGroupBox();
	}

	/**
	 * Handles the <code>Export</code> button or menu option.
	 */
	public void handleExportButton() {
		/*if(c.size() != 0) {
			JFileChooser chooser = new JFileChooser();
			String[] txts = new String[] {"txt"};
			chooser.addChoosableFileFilter(new SimpleFileFilter(txts, "Text Format (*.txt)"));
			chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
			int option = chooser.showSaveDialog(eaView);
			if(option == JFileChooser.APPROVE_OPTION) {
				eaModel.export( chooser.getSelectedFile() );
			} else {
				// this means the file is invalid
			}
		}*/
	}

	/**
	 * Handles the action performed on the Group Box
	 */
	public void handleGroupBox() {
		eaModel.updatePartyExperience();
		eaView.setExperienceFromCombat(eaModel.getPartyExperience());
	}

	/**
	 * Registers all the listeners for any actions.
	 */
	public void initListeners() {
		eaView.getAddExperienceToCharButton().addActionListener(this);
		eaView.getAddExperienceToPartyButton().addActionListener(this);
		eaView.getAdjustCRButton().addActionListener(this);
		eaView.getAddEnemyButton().addActionListener(this);
		eaView.getRemoveEnemyButton().addActionListener(this);
		eaView.getExperienceMultSlider().addChangeListener(this);
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		this.update();
	}

	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Sets the instance of the model for this class to use.
	 * @param theModel the model for this class to use.
	 */
	public void setModel(ExperienceAdjusterModel eaModel) {
		this.eaModel = eaModel;
	}

	/**
	 * Sets the instance of the view for this class to use.
	 * @param theView the <code>JPanel</code> that this class uses.
	 */
	public void setView(ExperienceAdjusterView eaView) {
		this.eaView = eaView;
	}

	/**
	 * Calls all the necessary update functions for the GUI components.
	 *
	 */
	public void update() {
		eaModel.populateLists();
		eaView.setParty(eaModel.getParty());
		eaView.setEnemies(eaModel.getEnemies());
		handleGroupBox();
	}


	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message) {
		if(message instanceof CombatRequestMessage) {
			if(message.getSource() == this) {
				CombatRequestMessage cmessage = (CombatRequestMessage)message;
				if(initList == null) {
					initList = cmessage.getCombat();
				}
				eaModel.setCombat(initList);
			}
			update();
		}
		else if(message instanceof StateChangedMessage) {
			if(GMGenSystemView.getTabPane().getSelectedComponent() instanceof ExperienceAdjusterView) {
				experienceToolsItem.setEnabled(false);
				if(initList == null) {
					GMBus.send(new CombatRequestMessage(this));
				}
				update();
			}
			else {
				experienceToolsItem.setEnabled(true);
			}
		}
		else if(message instanceof SaveMessage) {
			if(GMGenSystemView.getTabPane().getSelectedComponent() instanceof ExperienceAdjusterView) {
				handleExportButton();
			}
		}
	}
}
