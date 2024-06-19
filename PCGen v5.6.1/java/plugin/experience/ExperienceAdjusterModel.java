package plugin.experience;

import gmgen.io.ReadXML;
import gmgen.plugin.Combatant;
import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.util.LogUtilities;
import java.io.File;
import java.util.Observable;
import pcgen.util.Logging;
import pcgen.core.SettingsHandler;
import plugin.experience.gui.PreferencesExperiencePanel;

/**
 * This <code>class</code> holds all the necessary data in order to have
 * functionality for the experience adjuster.<br>
 * Created on February 19, 2003<br>
 * Updated on February 26, 2003
 * @author Expires 2003
 * @version 2.10
 */
public class ExperienceAdjusterModel extends Observable {

	/** The value of experience gotten from a group. */
	protected int partyExperience;
	protected double multiplier = 1.0;

	protected String dir;

	protected InitHolderList combat;
	protected ExperienceList party = new ExperienceList();
	protected ExperienceList enemies = new ExperienceList();

	/**
	 * Class constructor for ExperienceAdjusterView taking a
	 * parent directory.  This will call the
	 * function <code>initComponents</code> to initialise all the GUI
	 * components on the <code>JPanel</code>.
	 * @param parentDir the directory this is running out of
	 */
	public ExperienceAdjusterModel(String parentDir) {
		dir = parentDir;
	}

	public void setCombat(InitHolderList combat) {
		this.combat = combat;
	}
	/**
	 * Adds experience to a certain character.
	 * @param gm the character.
	 * @param experience the value to add to the character.
	 */
	public void addExperienceToCharacter(ExperienceListItem item, int experience) {
		Combatant cbt = item.getCombatant();
		cbt.setXP(cbt.getXP() + experience);
		LogUtilities.inst().logMessage(ExperienceAdjusterPlugin.LOG_NAME, cbt.getName() + " Awarded " + experience + " Experience");
	}

	/**
	 * Adds experience to a group of combatants.
	 * @param groupNumber the selected group to add to.
	 */
	public void addExperienceToParty() {
		int expType = SettingsHandler.getGMGenOption(ExperienceAdjusterPlugin.LOG_NAME + ".ExperienceType", PreferencesExperiencePanel.EXPERIENCE_35);
		LogUtilities.inst().logMessage(ExperienceAdjusterPlugin.LOG_NAME, "Party Awarded " + getPartyExperience() + " Total Experience Split as:");

		for (int i = 0; i < party.size(); i++) {
			Combatant cbt = ((ExperienceListItem) party.get(i)).getCombatant();
			if (expType == PreferencesExperiencePanel.EXPERIENCE_3) {
				cbt.setXP(cbt.getXP() + (getPartyTotalExperience() / party.size()));
				LogUtilities.inst().logMessage(ExperienceAdjusterPlugin.LOG_NAME,
				 cbt.getName() + ": " + (getPartyTotalExperience() / party.size()));
			}
			else {
				cbt.setXP(cbt.getXP() + getCombatantExperience(cbt));
				LogUtilities.inst().logMessage(ExperienceAdjusterPlugin.LOG_NAME,
				 cbt.getName() + ": " + getCombatantExperience(cbt));
			}
		}
	}

	public int getCombatantExperience(Combatant cbt) {
		float enemyCR;
		int tableCR;
		int experience = 0;
		File experienceFile = new File(dir + File.separator + "experience_tables/7_1.xml");
		ReadXML experienceTable = new ReadXML(experienceFile);

		for (int i = 0; i < enemies.size(); i++) {
			ExperienceListItem item = (ExperienceListItem) enemies.get(i);
			enemyCR = item.getCombatant().getCR();

			if (enemyCR < 1) {
				tableCR = 1;
			}
			else {
				tableCR = (int) enemyCR;
			}

			String xp = (String) experienceTable.getTable().crossReference(Integer.toString((int)cbt.getCR()), Integer.toString(tableCR));

			try {
				if (enemyCR < 1) {
					experience += (int) (Float.parseFloat(xp) * enemyCR);
				}
				else {
					experience += Integer.parseInt(xp);
				}
			}
			catch (Exception e) {
				Logging.errorPrint("Experience Value: '" + xp + "' Not a number");
				Logging.errorPrint(e.getMessage(), e);
			}
		}

		return new Double((experience * multiplier) / party.size()).intValue();
	}

	public int getPartyTotalExperience() {
		float enemyCR;
		int tableCR;
		int experience = 0;
		File experienceFile = new File(dir + File.separator + "experience_tables/7_1.xml");
		ReadXML experienceTable = new ReadXML(experienceFile);

		for (int i = 0; i < enemies.size(); i++) {
			ExperienceListItem item = (ExperienceListItem) enemies.get(i);
			enemyCR = item.getCombatant().getCR();

			if (enemyCR < 1) {
				tableCR = 1;
			}
			else {
				tableCR = (int) enemyCR;
			}

			String xp = (String) experienceTable.getTable().crossReference(Integer.toString(party.averageCR()), Integer.toString(tableCR));

			try {
				if (enemyCR < 1) {
					experience += (int) (Float.parseFloat(xp) * enemyCR);
				}
				else {
					experience += Integer.parseInt(xp);
				}
			}
			catch (Exception e) {
				Logging.errorPrint("Experience Value: '" + xp + "' Not a number");
				Logging.errorPrint(e.getMessage(), e);
			}
		}

		return new Double(experience * multiplier).intValue();
	}

	/**
	 * Gets the group experience,
	 * @return the experience for the group.
	 */
	public int getPartyExperience() {
		return partyExperience;
	}

	/**
	 * Updates the value displayed on the GUI for group experience.
	 * @param addTo the group to add to.
	 * @param addFrom the gruop to take experience from.
	 */
	public void updatePartyExperience() {
		int expType = SettingsHandler.getGMGenOption(ExperienceAdjusterPlugin.LOG_NAME + ".ExperienceType", PreferencesExperiencePanel.EXPERIENCE_35);
		if (expType == PreferencesExperiencePanel.EXPERIENCE_3) {
			partyExperience = getPartyTotalExperience();
		}
		else {
			partyExperience = 0;
			for (int i = 0; i < party.size(); i++) {
				Combatant cbt = ((ExperienceListItem) party.get(i)).getCombatant();
				partyExperience += getCombatantExperience(cbt);
			}
		}
	}

	public void setMultiplier(double mult) {
		multiplier = mult;
	}

	public void removeEnemy(ExperienceListItem enemy) {
		combat.remove(enemy.getCombatant());
		enemies.removeElement(enemy);
	}

	public void addEnemy(ExperienceListItem enemy) {
		combat.add(enemy.getCombatant());
		enemies.addElement(enemy);
	}

	public void clearEnemies() {
		for(int i = 0; i < enemies.size(); i++) {
			ExperienceListItem item = (ExperienceListItem)enemies.get(i);
			combat.remove(item.getCombatant());
		}
		enemies.removeAllElements();
	}

	public void populateLists() {
		if(combat != null) {
			party.removeAllElements();
			enemies.removeAllElements();
			for (int i = 0; i < combat.size(); i++) {
				InitHolder iH = (InitHolder) combat.get(i);
				if(iH instanceof Combatant) {
					Combatant cbt = (Combatant)iH;
					if(cbt.getCombatantType().equals("PC")) {
						party.addElement(new ExperienceListItem(cbt));
					}
					else if(cbt.getCombatantType().equals("Enemy")) {
						if(cbt.getStatus().equals("Dead") || cbt.getStatus().equals("Defeated")) {
							enemies.addElement(new ExperienceListItem(cbt));
						}
					}
				}
			}
		}
	}

	public void setParty(ExperienceList party) {
		this.party = party;
	}

	public void setEnemies(ExperienceList enemies) {
		this.enemies = enemies;
	}

	public ExperienceList getParty() {
		return party;
	}

	public ExperienceList getEnemies() {
		return enemies;
	}

}
