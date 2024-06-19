package plugin.pcgtracker;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.io.CharacterReaderWriter;
import gmgen.io.SimpleFileFilter;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.FileOpenMessage;
import gmgen.pluginmgr.messages.HelpMenuItemAddMessage;
import gmgen.pluginmgr.messages.OpenPCGRequestMessage;
import gmgen.pluginmgr.messages.PCClosedMessage;
import gmgen.pluginmgr.messages.PCLoadedMessage;
import gmgen.pluginmgr.messages.SavePCGRequestMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import gmgen.pluginmgr.messages.WindowClosedMessage;
import gmgen.util.MiscUtilities;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import pcgen.core.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.PCGIOHandler;
import pcgen.util.Logging;
import plugin.pcgtracker.gui.PCGTrackerView;

/**
 * The <code>ExperienceAdjusterController</code> handles the functionality of
 * the Adjusting of experience.  This class is called by the <code>GMGenSystem
 * </code> and will have it's own model and view.<br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class PCGTrackerPlugin extends GMBPlugin implements java.awt.event.ActionListener {
	/** The English name of the plugin. */
	private String name = "Characters";

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00";

	private PCGTrackerModel model = new PCGTrackerModel();

	public static final String LOG_NAME = "PCG Tracker";

	/** The plugin menu item in the help menu. */
	private JMenuItem charHelpItem = new JMenuItem();

	/** The plugin menu item in the tools menu. */
	private JMenuItem charToolsItem = new JMenuItem();

	private PCGTrackerView theView;

	/**
	 * Creates a new instance of TreasurePlugin
	 */
	public PCGTrackerPlugin() {
		theView = new PCGTrackerView();
		theView.getLoadedList().setModel(model);
		initListeners();
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

	private void initMenus() {
		charToolsItem.setMnemonic('C');
		charToolsItem.setText("Character Tracker");
		charToolsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, charToolsItem));

		charHelpItem.setMnemonic('C');
		charHelpItem.setText("Character Tracker");
		charHelpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				helpMenuItem(evt);
			}
		});
		GMBus.send(new HelpMenuItemAddMessage(this, charHelpItem));
	}

	public void toolMenuItem(ActionEvent evt) {
		JTabbedPane tp = GMGenSystemView.getTabPane();
		for(int i = 0; i < tp.getTabCount(); i++) {
			if(tp.getComponentAt(i) instanceof PCGTrackerView) {
				tp.setSelectedIndex(i);
			}
		}
	}

	public void helpMenuItem(ActionEvent evt) {
	}

	/**
	 * Registers all the listeners for any actions.
	 */
	public void initListeners() {
		theView.getRemoveButton().addActionListener(this);
		theView.getSaveButton().addActionListener(this);
		theView.getSaveAsButton().addActionListener(this);
		theView.getLoadButton().addActionListener(this);
	}

	/**
	 * Gets the view that this class is using.
	 * @return the view.
	 */
	public Component getView() {
		return theView;
	}

	/**
	 * Checks whether a character can be saved, and if so, calls
	 * it's <code>save</code> method.
	 *
	 * @param aPC The PlayerCharacter to save
	 * @param saveas boolean if <code>true</code>, ask for file name
	 *
	 * @return <code>true</code> if saved; <code>false</code> if saveas cancelled
	 */
	public boolean savePC(PlayerCharacter aPC, boolean saveas) {
		boolean newPC = false;
		File prevFile, file = null;
		String aPCFileName = aPC.getFileName();

		if (aPCFileName.equals("")) {
			prevFile = new File(SettingsHandler.getPcgPath().toString(), aPC.getDisplayName() + Constants.s_PCGEN_CHARACTER_EXTENSION);
			aPCFileName = prevFile.getAbsolutePath();
			newPC = true;
		}
		else {
			prevFile = new File(aPCFileName);
		}

		if (saveas || newPC) {
			JFileChooser fc = new JFileChooser();
			String[] pcgs = new String[] {"pcg"};
			SimpleFileFilter ff = new SimpleFileFilter(pcgs, "PCGen Character");
			fc.setFileFilter(ff);
			fc.setSelectedFile(prevFile);
			FilenameChangeListener listener = new FilenameChangeListener(aPCFileName, fc);

			fc.addPropertyChangeListener(listener);
			int returnVal = fc.showSaveDialog(GMGenSystem.inst);
			fc.removePropertyChangeListener(listener);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();

				if (!file.getName().endsWith(Constants.s_PCGEN_CHARACTER_EXTENSION)) {
					file = new File(file.getParent(), file.getName() + Constants.s_PCGEN_CHARACTER_EXTENSION);
				}

				if (file.isDirectory()) {
					JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a character.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					return false;
				}

				if (file.exists() && (newPC || prevFile == null || !file.getName().equals(prevFile.getName()))) {
					int reallyClose = JOptionPane.showConfirmDialog(GMGenSystem.inst, "The file " + file.getName() + " already exists, are you sure you want to overwrite it?", "Confirm overwriting " + file.getName(), JOptionPane.YES_NO_OPTION);

					if (reallyClose != JOptionPane.YES_OPTION) {
						return false;
					}
				}
				aPC.setFileName(file.getAbsolutePath());
			}
			else { // not saving
				return false;
			}
		}

		else {// simple save
			file = prevFile;
		}

		try {
			(new PCGIOHandler()).write(aPC, file.getAbsolutePath());

			SettingsHandler.setPcgPath(file.getParentFile());
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Could not save " + aPC.getDisplayName(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			Logging.errorPrint("Could not save " + aPC.getDisplayName());
			Logging.errorPrint(ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	public void handleClose() {
		if(model.size() > 0) {
			GMGenSystemView.getTabPane().setSelectedComponent(theView);
		}
		for(int i = 0; i < model.size(); i++) {
			PlayerCharacter pc = (PlayerCharacter) model.get(i);
		}
	}

		/**
	 * Handles the clicking of the <b>Add</b> button on the GUI.
	 */
	public void handleOpen() {
		File defaultFile = SettingsHandler.getPcgPath();
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(defaultFile);
		String[] pcgs = new String[] {"pcg", "pcp"};
		SimpleFileFilter ff = new SimpleFileFilter(pcgs, "PCGen File");
		chooser.addChoosableFileFilter(ff);
		chooser.setFileFilter(ff);
		chooser.setMultiSelectionEnabled(true);
		java.awt.Cursor saveCursor = MiscUtilities.setBusyCursor(GMGenSystem.inst);
		int option = chooser.showOpenDialog(GMGenSystem.inst);
		if(option == JFileChooser.APPROVE_OPTION) {
			File [] pcFiles = chooser.getSelectedFiles();
			for(int i = 0; i < pcFiles.length; i++) {
				SettingsHandler.setPcgPath(pcFiles[i].getParentFile());
				if(pcFiles[i].toString().endsWith(".pcg")) {
					GMBus.send(new OpenPCGRequestMessage(this, pcFiles[i], false));
				}
				else if(pcFiles[i].toString().endsWith(".pcp")) {
					GMBus.send(new OpenPCGRequestMessage(this, pcFiles[i], false));
				}
			} /* loop through selected files */
		}
		else { /* this means the file is invalid */
		}
		MiscUtilities.setCursor(GMGenSystem.inst, saveCursor);
	}

	public PlayerCharacter loadPCG(File pcgFile) {
		PlayerCharacter newPC = new PlayerCharacter();
		newPC.setFileName(pcgFile.getPath());
		CharacterReaderWriter crw = new CharacterReaderWriter( newPC );
		crw.readCharacterFromFile(newPC, pcgFile);
		newPC.setDirty(false);
		return newPC;
	}

	public void loadPCP(File pcpFile) {
		try {
			String path = pcpFile.getParent();
			BufferedReader br = new BufferedReader(new FileReader(pcpFile));
			br.readLine(); //Read and throw away version info. May change to actually use later
			//load character filename data
			String charFiles = br.readLine();
			StringTokenizer fileNames = new StringTokenizer(charFiles, ",");
			while(fileNames.hasMoreTokens()) {
				String fileName = fileNames.nextToken();
				try {
					if(fileName.endsWith(".pcg")) {
						File pcg = new File(path + File.separator + fileName);
						if(pcg.exists()) {
							PlayerCharacter newPC = loadPCG(pcg);
							GMBus.send(new PCLoadedMessage(this, newPC));
						}
						else {
							throw new FileNotFoundException(pcg.getName());
						}
					}
					else if(fileName.endsWith(".pcp")) {
						File pcp = new File(path + File.separator + fileName);
						if(pcp.exists()) {
							loadPCP(pcp);
						}
						else {
							throw new FileNotFoundException(pcp.getName());
						}
					}
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(GMGenSystem.inst, "File load error: " + path + File.separator + fileName);
					Logging.errorPrint("File Load Error: " + path + File.separator + fileName);
					Logging.errorPrint(e.getMessage(), e);
				}
			}
			br.close();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(GMGenSystem.inst, "File load error: " + pcpFile.getName());
			Logging.errorPrint("File Load Error" + pcpFile.getName());
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == theView.getRemoveButton()) {
			removeSelected();
		}
		if (e.getSource() == theView.getSaveButton()) {
			Object[] list = theView.getLoadedList().getSelectedValues();
			for(int i = 0; i < list.length; i++) {
				PlayerCharacter pc = model.get(list[i]);
				savePC(pc, false);
			}
		}
		if (e.getSource() == theView.getSaveAsButton()) {
			Object[] list = theView.getLoadedList().getSelectedValues();
			for(int i = 0; i < list.length; i++) {
				PlayerCharacter pc = model.get(list[i]);
				savePC(pc, true);
			}
		}
		if (e.getSource() == theView.getLoadButton()) {
			handleOpen();
		}
		theView.getLoadedList().repaint();
	}

	public void removeSelected() {
		Object[] list = theView.getLoadedList().getSelectedValues();
		for(int i = 0; i < list.length; i++) {
			PlayerCharacter pc = model.get(list[i]);
			model.removeElement(list[i]);
			GMBus.send(new PCClosedMessage(this, pc));
		}
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message) {
		if(message instanceof FileOpenMessage) {
			if(GMGenSystemView.getTabPane().getSelectedComponent() instanceof PCGTrackerView) {
				handleOpen();
			}
		}
		else if(message instanceof PCLoadedMessage) {
			PCLoadedMessage cmessage = (PCLoadedMessage)message;
			if(!cmessage.isIgnored(this)) {
				model.add(cmessage.getPC());
			}
		}
		else if(message instanceof StateChangedMessage) {
			if(GMGenSystemView.getTabPane().getSelectedComponent() instanceof PCGTrackerView) {
				GMGenSystem.inst.openFileItem.setEnabled(true);
			}
		}
		else if (message instanceof WindowClosedMessage) {
			handleClose();
		}
		else if (message instanceof SavePCGRequestMessage) {
			SavePCGRequestMessage smessage = (SavePCGRequestMessage) message;
			savePC(smessage.getPC(), false);
		}
		else if (message instanceof PCClosedMessage) {
			PCClosedMessage cmessage = (PCClosedMessage)message;
			Object[] list = theView.getLoadedList().getSelectedValues();
			for(int i = 0; i < list.length; i++) {
				PlayerCharacter pc = model.get(list[i]);
				if(pc == cmessage.getPC()) {
					model.removeElement(list[i]);
				}
			}
		}
	}

	/**
	 * Property change listener for the event "selected file
	 * changed".  Ensures that the filename doesn't get changed
	 * when a directory is selected.
	 *
	 * @author Dmitry Jemerov <yole@spb.cityline.ru>
	 */

	static final class FilenameChangeListener implements PropertyChangeListener {
		private String lastSelName;
		private JFileChooser fileChooser;

		FilenameChangeListener(String aFileName, JFileChooser aFileChooser) {
			lastSelName = aFileName;
			fileChooser = aFileChooser;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			String propName = evt.getPropertyName();
			if (propName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
				onSelectedFileChange(evt);
			}
			else if (propName.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
				onDirectoryChange();
			}
		}

		private void onDirectoryChange() {
			fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), lastSelName));
		}

		private void onSelectedFileChange(PropertyChangeEvent evt) {
			File newSelFile = (File) evt.getNewValue();
			if (newSelFile != null && !newSelFile.isDirectory()) {
				lastSelName = newSelFile.getName();
			}
		}
	}
}
