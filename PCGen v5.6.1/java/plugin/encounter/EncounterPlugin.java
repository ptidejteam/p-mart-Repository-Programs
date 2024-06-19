package plugin.encounter;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.io.ReadXML;
import gmgen.io.VectorTable;
import gmgen.plugin.Dice;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.InitHolderListSendMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SystemCollections;
import pcgen.core.WeaponProf;
import pcgen.core.character.EquipSet;
import pcgen.core.character.EquipSlot;
import pcgen.gui.utils.ChooserFactory;
import pcgen.gui.utils.ChooserRadio;
import pcgen.util.Logging;
import plugin.encounter.gui.EncounterView;

/**
 * The <code>EncounterPlugin</code> controlls the various classes that are
 * involved in the functionality of the Encounter Generator.  This <code>class
 * </code> is a plugin for the <code>GMGenSystem</code>, is called by the
 * <code>PluginLoader</code> and will create a model and a view for this plugin.
 * @author Expires 2003
 * @version 2.10
 */
public class EncounterPlugin extends GMBPlugin implements ActionListener, ItemListener, MouseListener {
	/** The plugin menu item in the tools menu. */
	private JMenuItem encounterToolsItem = new JMenuItem();

	/**
	 * The environment that can be selected for the encounter to take place in,
	 */
	private EnvironmentModel    theEnvironments;

	/** The list of combatants in the game. */
	private InitHolderList      theList;

	/** The model that holds all the data for generating encounters. */
	private EncounterModel      theModel;

	/** The list of races that are available for creatures. */
	private RaceModel           theRaces;

	/** The user interface for the encounter generator. */
	private EncounterView       theView;

	/** The English name of the plugin. */
	private String name = "Encounter";

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00";

	public static final String LOG_NAME = "Encounter";

	/**
	 * Creates an instance of this class creating a new <code>InitHolderList
	 * </code>.
	 */
	public EncounterPlugin(){
		this(new InitHolderList());
	}

	/**
	 * Creates an instance of this class taking in a <code>InitHolderList
	 * </code>.
	 * @param l the list of combatants.
	 */
	public EncounterPlugin(InitHolderList l) {
		super();
		theModel = new EncounterModel(getDataDir());
		theView = new EncounterView();
		theRaces = new RaceModel();
		theList = l;
		theEnvironments = new EnvironmentModel(getDataDir());

		theView.getLibraryCreatures().setModel(theRaces);
		theView.getEncounterCreatures().setModel(theModel);
		theView.getEnvironment().setModel(theEnvironments);

		theView.getLibraryCreatures().addMouseListener(this);
		theView.getEncounterCreatures().addMouseListener(this);

		theView.getAddCreature().addActionListener(this);
		theView.getRemoveCreature().addActionListener(this);
		theView.getTransferToTracker().addActionListener(this);
		theView.getGenerateEncounter().addActionListener(this);
		theView.getEnvironment().addItemListener(this);
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
		encounterToolsItem.setMnemonic('n');
		encounterToolsItem.setText("Encounter Generator");
		encounterToolsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, encounterToolsItem));
	}

	public void toolMenuItem(ActionEvent evt) {
		JTabbedPane tp = GMGenSystemView.getTabPane();
		for(int i = 0; i < tp.getTabCount(); i++) {
			if(tp.getComponentAt(i) instanceof EncounterView) {
				tp.setSelectedIndex(i);
			}
		}
	}

	/**
	 * Calls the appropriate methods depending on the source of the event.
	 * @param e the source of the event from the GUI.
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == theView.getAddCreature()) {
			handleAddCreature();
		} else if (e.getSource() == theView.getRemoveCreature()) {
			handleRemoveCreature();
		} else if (e.getSource() == theView.getTransferToTracker()) {
			handleTransferToTracker();
		} else if (e.getSource() == theView.getGenerateEncounter() ) {
			handeGenerateEncounter(theModel);
		} else {
			Logging.errorPrint("Unhandled ActionEvent: " + e.getSource().toString());
		}
		updateUI();
	}

	public void mouseClicked(MouseEvent evt) {
		if (evt.getSource() == theView.getLibraryCreatures()) {
			if(evt.getClickCount() == 2) {
				int index = theView.getLibraryCreatures().locationToIndex(evt.getPoint());
				ListModel dlm = theView.getLibraryCreatures().getModel();
				Object item = dlm.getElementAt(index);
				theView.getLibraryCreatures().ensureIndexIsVisible(index);
				theModel.addElement(item);
				updateUI();
			}
		} else if (evt.getSource() == theView.getEncounterCreatures()) {
			try {
				if(evt.getClickCount() == 2) {
					int index = theView.getEncounterCreatures().locationToIndex(evt.getPoint());
					ListModel dlm = theView.getEncounterCreatures().getModel();
					Object item = dlm.getElementAt(index);
					theView.getEncounterCreatures().ensureIndexIsVisible(index);
					theModel.removeElement(item);
					updateUI();
				}
			}
			catch (Exception e) {
			}
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Generates a creature for an encounter given a specified environment.
	 * @param Environment the environment setting that the encounter will
	 *        take place in.
	 */
	private void generateXfromY(String Environment) {
		Vector critters;
		try {
			critters = getMonsterFromTable(Environment);
		} catch(FileNotFoundException e) {
			Logging.errorPrint(e.getMessage(), e);
			return;
		}
		for(int x=0; x < ((Integer)critters.firstElement()).intValue(); x++) {
			theModel.addElement(
				critters.lastElement().toString()
			);
		}
	}

	/**
	 * Generates creatures for an encounter based on a specified Encounter
	 * Level and number of creatures.
	 * @param size the number of creatures needed for encounter.
	 * @param totalEL total experience level.
	 */
	private void generateXofYEL(String size, String totalEL) {
		java.io.File f;
		ReadXML xml;
		VectorTable table41;
		Random roll = new Random(System.currentTimeMillis());
		Vector critters = new Vector();
		String cr;
		String[] crSplit;
		float crNum;

		if((f = new java.io.File(getDataDir() + File.separator + "encounter_tables" + File.separator + "4_1.xml")) == null) {
			Logging.errorPrint("ACK! No FILE! " + f.toString());
			return;
		}

		xml = new ReadXML(f);
		if((table41 = xml.getTable())==null) {
			Logging.errorPrint("ACK! error getting table41! " + f.toString());
			return;
		}

		xml = null;
		f = null;

		// verrify values on the table.
		cr = (String) table41.crossReference( totalEL, size);

		table41 = null;

		if(cr == null) {
			Logging.errorPrint("Tables do not match the given parameters (" + totalEL + ", " + size + ")");
			return;
		}

		crSplit = cr.split("/");
		try {
			crNum = -1 * Integer.valueOf(crSplit[1]).intValue();
		} catch(Exception e) {
			crNum = Integer.valueOf(cr).intValue();
		}

		// populate critters with a list of matching monsters with the right CR.
		for(Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext();) {
			final Race aRace = (Race) it.next();

			if(aRace.getCR() == crNum) {
				critters.add(aRace);
			}
		}

		int i = roll.nextInt(critters.size());
		for(int x=0; x< Integer.valueOf(size).intValue(); x++) {
			theModel.addElement(
				critters.elementAt(i).toString()
			);
		}
	}

	/**
	 * Gets the model that holds the data for the encounter generator.
	 * @return the <code>EncounterModel</code>.
	 */
	public EncounterModel getModel() {
		return theModel;
	}

	/**
	 * Gets a monster from the table specified.
	 * @param table the table that the creature will come from.
	 * @return the creature(s).
	 * @throws FileNotFoundException an exception if there is a non-existant
	 *         file.
	 */
	private Vector getMonsterFromTable(String table) throws FileNotFoundException {
		String tablePath, tableEntry, numMonsters;

		Random roll = new Random(System.currentTimeMillis());
		if(table.startsWith("[")) {

			tablePath = getDataDir() + File.separator + "encounter_tables" + File.separator + table.substring(1,table.length()-1);
			Logging.errorPrint("subfile " + tablePath.toString());
		} else {
			tablePath = table;
		}
		tablePath = tablePath.concat(".xml");

			/*open file*/
		File monsterFile = new File(tablePath);

		if(monsterFile == null) {
			Logging.errorPrint("could not open " + tablePath);
			return null;
		}

		ReadXML monsterTable = new ReadXML(monsterFile);
		String percent = monsterTable.findPercentageEntry(roll.nextInt(99)+1);

			/*get item type*/
		tableEntry = monsterTable.getTable().crossReference( percent, "Monster").toString();
			/*get amount of items*/
		numMonsters = monsterTable.getTable().crossReference( percent, "Number").toString();

		/*create items and add to list*/
		if ( tableEntry.startsWith("[") ) {
			return getMonsterFromTable( tableEntry.substring(1, tableEntry.length()-1));
		}
		else {
			Integer num;
			try {
				num = Integer.valueOf( numMonsters);
			} catch(Exception e) {
				String[] dice = numMonsters.split("d");
				num = new Integer(0);
				for(int x=0; x < Integer.valueOf(dice[0]).intValue(); x++) {
					num = new Integer(num.intValue() + roll.nextInt(Integer.valueOf(dice[1]).intValue())+1);
				}
			}
			Vector toReturn = new Vector();
			toReturn.addElement(num);
			toReturn.addElement(Globals.getRaceNamed(tableEntry));
			return toReturn;
		}
	}

	/**
	 * Gets the list of races for creatures.
	 * @return the list of races.
	 */
	public RaceModel getRaces() {
		return theRaces;
	}

	/**
	 * Gets the <code>JPanel</code> view associated for this class.
	 * @return the view.
	 */
	public JPanel getView() {
		return theView;
	}

	/**
	 * Handles the <b>Add Creature</b> button.
	 */
	public void handleAddCreature() {
		if(!theView.getLibraryCreatures().isSelectionEmpty()) {
			Object[] values = theView.getLibraryCreatures().getSelectedValues();
			for(int i = 0; i < values.length; i++) {
				theModel.addElement(values[i]);
			}
			updateUI();
		}
	}

	/**
	 * Handles the <b>Generate Encounter</b> button.
	 * @param m the encounter model.
	 */
	public void handeGenerateEncounter(EncounterModel m) {
		File f = new File(getDataDir() + File.separator + "encounter_tables" + File.separator + "environments.xml");
		ReadXML xml;
		if (f != null && f.exists()) {
			xml = new ReadXML(f);
		}
		else {
			Logging.errorPrint("handleGenerateEncounter:");
			Logging.errorPrint(f.toString());
			return;
		}

		VectorTable environments = xml.getTable();

		theModel.clear();
		if (theView.getEnvironment().getSelectedIndex() == 0) {
			generateXofYEL(theView.getNumberOfCreatures().getText(),theView.getTargetEL());
		} else {
			generateXfromY(environments.crossReference(theView.getEnvironment().getSelectedItem().toString(), "File").toString());
		}
		updateUI();
	}

	/**
	 * Handles the <b>Remove Creature</b> button.
	 */
	public void handleRemoveCreature() {
		if(!theView.getEncounterCreatures().isSelectionEmpty()) {
			Object[] values = theView.getEncounterCreatures().getSelectedValues();
			for(int i = 0; i < values.length; i++) {
				theModel.removeElement(values[i]);
			}
			updateUI();
		}
	}

	/**
	 * Handles the <b>Begin Combat</b> button.
	 */
	public void handleTransferToTracker() {
		int i;
		PlayerCharacter aPC;
		JFrame oldRoot = Globals.getRootFrame();
		Globals.setRootFrame(GMGenSystem.inst);
		theModel.setPCs(theModel.size());

		try {
			for(i = 0; i < theModel.size(); i++) {
				aPC = theModel.getPCs()[i];
				Globals.setCurrentPC(aPC);
				aPC.setImporting(false);

				if(!handleRace(aPC, i)) {
					continue;
				}

				PCClass monsterClass = Globals.getClassNamed(aPC.getRace().getMonsterClass(false));
				if (monsterClass != null) {
					handleMonster(aPC, monsterClass);
				}
				else {
					handleNonMonster(aPC);
				}
				handleEquipment(aPC);
				aPC.setPlayersName("Enemy");
				theList.add(new PcgCombatant(aPC, "Enemy"));
			}
			GMBus.send(new InitHolderListSendMessage(this, theList));
			removeAll();
		}
		catch (Throwable e) {
				e.printStackTrace();
		}
		Globals.setRootFrame(oldRoot);
	}

	private void printStats(PlayerCharacter aPC, String append) {
		StringBuffer statBuf = new StringBuffer();
		statBuf.append(append);
		pcgen.core.StatList sl = aPC.getStatList();
		List statList = sl.getStats();
		boolean firstLine = true;
		for(int i = 0; i < statList.size(); i++) {
			if(!firstLine) {
				statBuf.append(", ");
			}
			firstLine = false;
			PCStat stat = (PCStat) statList.get(i);
			statBuf.append(stat.getAbb() + " " + sl.getTotalStatFor(stat.getAbb()));
		}
	}

	private void handleNonMonster(PlayerCharacter aPC) {
		PCClass mclass = Globals.getClassNamed("Warrior");
		if (mclass != null) {
			Logging.debugPrint("Class: " + mclass.getName() + " Level: 1");
			aPC.incrementClassLevel(1, mclass);
			rollHP(aPC);
		}
	}

	private void handleMonster(PlayerCharacter aPC, PCClass monsterClass) {
		Race race = aPC.getRace();
		int racehd = aPC.getRace().hitDice();
		//Use Default monsters for the time being
		if(racehd > 0) {
			int bonus = (int) aPC.getTotalBonusTo("HD", "MIN");
			int size = aPC.getRace().getHitDiceSize();
			for (int x = 0; x < racehd; ++x) {
				aPC.getRace().setHitPoint(x, new Integer(new Dice(1, size, bonus).roll()));
			}
			aPC.setCurrentHP(aPC.hitPoints());
		}
		else {
			//SettingsHandler.setMonsterDefault(false);
			monsterClass.setMonsterFlag("YES");
			int levels = race.getMonsterClassLevels(false);
			Logging.debugPrint("Monster Class: " + monsterClass.getName() + " Level: " + levels);
			aPC.incrementClassLevel(levels, monsterClass);
			rollHP(aPC);
			//SettingsHandler.setMonsterDefault(true);
		}
	}


	private void rollHP(PlayerCharacter aPC) {
		List classList = aPC.getClassList();
		for(int i = 0; i < classList.size(); i++) {
			PCClass pcclass = (PCClass) classList.get(i);
			int bonus = (int) aPC.getTotalBonusTo("HD", "MIN") + (int) aPC.getTotalBonusTo("HD", "MIN;CLASS." + pcclass.getName());
			int size = pcclass.getHitDie();
			for (int j = 0; j < pcclass.getLevel(); j++) {
				pcclass.setHitPoint(j, new Integer(new Dice(1, size, bonus).roll()));
			}
		}
		aPC.setCurrentHP(aPC.hitPoints());
	}

	private void updateRace(PlayerCharacter aPC) {
		Race race = aPC.getRace();
	}

	private void handleEquipment(PlayerCharacter aPC) {
		EquipSet eqSet = createDefaultEquipset(aPC);
		addAllToEquipSet(aPC, eqSet);
		aPC.setCalcEquipSetId(eqSet.getIdPath());
		aPC.setCalcEquipmentList();
	}

	private boolean handleRace(PlayerCharacter aPC, int number) {
		Race race = Globals.getRaceNamed((String) theModel.getElementAt(number));
		if (race == null) {
			return false;
		}

		aPC.setRace(race);
		aPC.setName(race.toString());

		return true;
	}

	private void handleMonsterStats(PlayerCharacter aPC) {
		List statList = aPC.getStatList().getStats();
		for(int i = 0 ; i < statList.size(); i++) {
			PCStat stat = (PCStat) statList.get(i);
			stat.setBaseScore(10);
		}
	}

	private String getNewIdPath(PlayerCharacter aPC, EquipSet eSet) {
		String pid = "0";
		int newID = 0;
		if (eSet != null) {
			pid = eSet.getIdPath();
		}
		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();) {
			EquipSet es = (EquipSet) e.next();
			if (es.getParentIdPath().equals(pid) && es.getId() > newID) {
				newID = es.getId();
			}
		}
		++newID;
		return pid + '.' + newID;
	}

	private EquipSet createDefaultEquipset(PlayerCharacter aPC) {
		EquipSet eSet;
		if (aPC.getEquipSet().size() == 0) {
			String id = getNewIdPath(aPC, null);
			String defaultEquipSet = "Default Set";
			eSet = new EquipSet(id, defaultEquipSet);
			aPC.addEquipSet(eSet);
			Logging.debugPrint("Adding EquipSet: " + defaultEquipSet.toString());
		}
		else {
			eSet = (EquipSet) aPC.getEquipSet().get(0);
		}
		return eSet;
	}

	private void addAllToEquipSet(PlayerCharacter aPC, EquipSet eqSet) {
		List eqList = aPC.getEquipmentList();
		for(int i = 0; i < eqList.size(); i++) {
			Equipment eq = (Equipment) eqList.get(i);
			addEquipToTarget(aPC, eqSet, "", (Equipment)eq.clone(), new Float(1));
		}
	}

	/**
	 * This method gets a list of locations for a weapon
	 **/
	private static ArrayList getWeaponLocationChoices(int hands, String multiHand) {
		ArrayList result = new ArrayList(hands + 2);
		if (hands > 0) {
			result.add(Constants.S_PRIMARY);
			for (int i = 1; i < hands; ++i) {
				if (i > 1) {
					result.add(Constants.S_SECONDARY + " " + i);
				}
				else {
					result.add(Constants.S_SECONDARY);
				}
			}
			if (multiHand.length() > 0) {
				result.add(multiHand);
			}
		}
		return result;
	}

	private final ArrayList locationChoices(PlayerCharacter aPC, Equipment eqI) {
		// Some Equipment locations are based on the number of hands
		int hands = 0;
		final pcgen.core.Race race = aPC.getRace();
		if (race != null) {
			hands = race.getHands();
		}

		ArrayList aList = new ArrayList();

		if (eqI.isWeapon()) {
			if (eqI.isUnarmed()) {
				aList.add(Constants.S_UNARMED);
			}
			else if (Globals.isWeaponLightForPC(aPC, eqI)) {
				aList = getWeaponLocationChoices(hands, "");
				if (eqI.isRanged() && !eqI.isThrown()) {
					aList.add(Constants.S_BOTH);
				}
				if (eqI.isMelee()) {
					aList.add(Constants.S_TWOWEAPONS);
				}
			}
			else {
				String wpSingle = eqI.profName(Equipment.EQUIPPED_PRIMARY);
				WeaponProf wp = Globals.getWeaponProfNamed(wpSingle);
				if (Globals.handsRequired(aPC, eqI, wp) == 1) {
					aList = getWeaponLocationChoices(hands, Constants.S_BOTH);
					if (eqI.isMelee()) {
						if (eqI.isDouble()) {
							aList.add(Constants.S_DOUBLE);
						}
						else {
							aList.add(Constants.S_TWOWEAPONS);
						}
					}
				}
				else {
					aList.add(Constants.S_BOTH);
					if (eqI.isMelee() && eqI.isDouble()) {
						aList.add(Constants.S_DOUBLE);
					}
				}
			}
		}
		else {
			String locName = getSingleLocation(eqI);
			if (locName.length() != 0) {
				aList.add(locName);
			}
			else {
				aList.add(Constants.S_EQUIPPED);
			}
		}
		if (!eqI.isUnarmed()) {
			aList.add(Constants.S_CARRIED);
			aList.add(Constants.S_NOTCARRIED);
		}
		return aList;
	}

	/**
	 * If an item can only go in one location, return the name of that
	 * location to add to an EquipSet
	 **/
	private static String getSingleLocation(Equipment eqI) {
		// Handle natural weapons
		if (eqI.isNatural()) {
			if (eqI.getSlots() == 0) {
				if (eqI.modifiedName().endsWith("Primary")) {
					return Constants.S_NATURAL_PRIMARY;
				}
				else {
					return Constants.S_NATURAL_SECONDARY;
				}
			}
		}
		// Always force weapons to go through the chooser dialog
		/*if (eqI.isWeapon()) {
			return "";
		}*/

		List eqSlotList = SystemCollections.getUnmodifiableEquipSlotList();
		if ((eqSlotList == null) || eqSlotList.isEmpty()) {
			return "";
		}

		for (Iterator eI = eqSlotList.iterator(); eI.hasNext();) {
			EquipSlot es = (EquipSlot) eI.next();

			// see if this EquipSlot can contain this item TYPE
			if (es.canContainType(eqI.getType())) {
				return es.getSlotName();
			}
		}
		return "";
	}

	private String getEquipLocation(PlayerCharacter aPC, EquipSet eSet, String locName, Equipment eqI) {
		if ("".equals(locName) || locName.length() == 0) {
			// get the possible locations for this item
			ArrayList aList = locationChoices(aPC, eqI);
			locName = getSingleLocation(eqI);
			if (!((locName.length() != 0) && canAddEquip(aPC, eSet, locName, eqI))) {
				// let them choose where to put the item
				ChooserRadio c = ChooserFactory.getRadioInstance();
				c.setAvailableList(aList);
				c.setVisible(false);
				c.setTitle(eqI.getName());
				c.setMessageText("Select a location for this item");
				c.show();
				aList = c.getSelectedList();
				if (c.getSelectedList().size() > 0) {
					Object loc = aList.get(0);
					if (loc instanceof String) {
						locName = (String) loc;
					}
				}
			}
		}
		if ("".equals(locName) || locName.length() == 0) {
			return null;
		}

		// make sure we can add item to that slot in this EquipSet
		if (!canAddEquip(aPC, eSet, locName, eqI)) {
			JOptionPane.showMessageDialog(null, "Can not equip " + eqI.getName() + " to " + locName, "GMGen", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return locName;
	}

	private static boolean canAddEquip(PlayerCharacter aPC, EquipSet eSet, String locName, Equipment eqI) {
		String idPath = eSet.getIdPath();

		// If Carried/Equipped/Not Carried slot
		// allow as many as they would like
		if (locName.startsWith(Constants.S_CARRIED) || locName.startsWith(Constants.S_EQUIPPED) || locName.startsWith(Constants.S_NOTCARRIED)) {
			return true;
		}

		// allow as many unarmed items as you'd like
		if (eqI.isUnarmed()) {
			return true;
		}
		// allow many Secondary Natural weapons
		if (locName.equals(Constants.S_NATURAL_SECONDARY)) {
			return true;
		}
		// Don't allow weapons that are too large for PC
		if (Globals.isWeaponTooLargeForPC(aPC, eqI)) {
			return false;
		}

		// make a HashMap to keep track of the number of each
		// item that is already equipped to a slot
		HashMap slotMap = new HashMap();

		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();) {
			EquipSet es = (EquipSet) e.next();
			if (!es.getParentIdPath().startsWith(idPath)) {
				continue;
			}
			// check to see if we already have
			// an item in that particular location
			if (es.getName().equals(locName)) {
				Equipment eItem = es.getItem();
				String nString = (String) slotMap.get(locName);
				int existNum = 0;
				if (nString != null) {
					existNum = Integer.parseInt(nString);
				}
				if (eItem != null) {
					existNum += eItem.getSlots();
				}
				slotMap.put(locName, String.valueOf(existNum));
			}
		}

		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();) {
			EquipSet es = (EquipSet) e.next();
			if (!es.getParentIdPath().startsWith(idPath)) {
				continue;
			}

			// if it's a weapon we have to do some
			// checks for hands already in use
			if (eqI.isWeapon()) {
				// weapons can never occupy the same slot
				if (es.getName().equals(locName)) {
					return false;
				}
				// if Double Weapon or Both Hands, then no
				// other weapon slots can be occupied
				if ((locName.equals(Constants.S_BOTH) || locName.equals(Constants.S_DOUBLE) || locName.equals(Constants.S_TWOWEAPONS)) &&
					(es.getName().equals(Constants.S_PRIMARY) || es.getName().equals(Constants.S_SECONDARY) || es.getName().equals(Constants.S_BOTH) || es.getName().equals(Constants.S_DOUBLE) || es.getName().equals(Constants.S_TWOWEAPONS)))
				{
					return false;
				}
				// inverse of above case
				if ((locName.equals(Constants.S_PRIMARY) || locName.equals(Constants.S_SECONDARY)) &&
					(es.getName().equals(Constants.S_BOTH) || es.getName().equals(Constants.S_DOUBLE) || es.getName().equals(Constants.S_TWOWEAPONS)))
				{
					return false;
				}
			}

			// If we already have an item in that location
			// check to see how many are allowed in that slot
			if (es.getName().equals(locName)) {
				final String nString = (String) slotMap.get(locName);
				int existNum = 0;
				if (nString != null) {
					existNum = Integer.parseInt(nString);
				}
				existNum += eqI.getSlots();

				EquipSlot eSlot = Globals.getEquipSlotByName(locName);
				if (eSlot == null) {
					return true;
				}
				// if the item takes more slots, return false
				if (existNum > eSlot.getSlotCount() + (int) aPC.getTotalBonusTo("SLOTS", eSlot.getContainType())) {
					return false;
				}
				return true;
			}
		}
		return true;
	}

	private EquipSet addEquipToTarget(PlayerCharacter aPC, EquipSet eSet, String locName, Equipment eqI, Float newQty) {
		locName = getEquipLocation(aPC, eSet, locName, eqI);

		// construct the new IdPath
		// new id is one larger than any other id at this path level
		String id = getNewIdPath(aPC, eSet);

		Logging.debugPrint("--addEB-- IdPath:" + id + "  Parent:" + eSet.getIdPath() + " Location:" + locName + " eqName:" + eqI.getName() + "  eSet:" + eSet.getName());

		// now create a new EquipSet to add this Equipment item to
		EquipSet newSet = new EquipSet(id, locName, eqI.getName(), eqI);

		// set the Quantity of equipment
		eqI.setQty(newQty);
		newSet.setQty(newQty);

		aPC.addEquipSet(newSet);
		aPC.setCurrentEquipSetName(eSet.getName());
		List itemList = aPC.getEquipSet();
		return newSet;
	}



	private void removeAll() {
		theModel.removeAllElements();
		theList = new InitHolderList();
		updateUI();
	}


	/**
	 * Enables or diabales items on the GUI depending on the state of the
	 * model.
	 * @see ItemListener#itemStateChanged(ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		if(theView.getEnvironment().getSelectedIndex() == 0) {
			theView.getNumberOfCreatures().setEnabled(true);
			theView.getTargetEncounterLevel().setEnabled(true);
			theView.getNumberLabel().setEnabled(true);
			theView.getTargetLabel().setEnabled(true);
		} else {
			theView.getNumberOfCreatures().setEnabled(false);
			theView.getTargetEncounterLevel().setEnabled(false);
			theView.getNumberLabel().setEnabled(false);
			theView.getTargetLabel().setEnabled(false);
		}
	}

	/**
	 * Sets the instance of the model for the encounter generator.
	 * @param theModel the <code>EncounterModel</code>.
	 */
	public void setModel(EncounterModel theModel) {
		this.theModel = theModel;
	}

	/**
	 * Sets the instance of the view being used for this class.
	 * @param theView the GUI interface.
	 */
	public void setView(EncounterView theView) {
		this.theView = theView;
	}

	/**
	 * Updates all necessary items if there has been a change.
	 */
	public void updateUI() {
		int sel;
		if((sel = theView.getEnvironment().getSelectedIndex()) == -1) {
			sel = 0;
		}
		theRaces.update();
		theEnvironments.update();
		theView.getEnvironment().setSelectedIndex(sel);
		theView.setTotalEncounterLevel(Integer.toString(theModel.getCR()));

		if (theModel.getSize() < 1) {
			theView.getTransferToTracker().setEnabled(false);
		} else {
			theView.getTransferToTracker().setEnabled(true);
		}
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	public void handleMessage(GMBMessage message) {
		if(message instanceof StateChangedMessage) {
			if(GMGenSystemView.getTabPane().getSelectedComponent() instanceof EncounterView) {
				updateUI();
				encounterToolsItem.setEnabled(false);
			}
			else {
				encounterToolsItem.setEnabled(true);
			}
		}
	}
}

