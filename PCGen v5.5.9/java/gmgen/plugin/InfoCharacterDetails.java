package gmgen.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.StatList;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.io.ExportHandler;
import pcgen.util.Logging;
import plugin.initiative.InitiativePlugin;

/**
 * This class is a helper for the Combat Tracker.  This class helps display
 * all the statistics of a character.
 * @author Expires 2003
 * @version $Revision: 1.1 $
 *
 * <p>Current Ver: $Revision: 1.1 $</p>
 * <p>Last Editor: $Author: vauchers $</p>
 * <p>Last Edited: $Date: 2006/02/21 01:27:55 $</p>
 */
public class InfoCharacterDetails {

	/** A Character class to have access to all the stats. */
	private PlayerCharacter aPC;

	/** The specialised pane that holds all the stats. */
	private JTextPane mainOutput;

	private String titleFontOpen = "<b><font size='+1'>";
	private String titleFontClose = "</font></b>";
	private String typeFontOpen = "<b><font color='#555555'>";
	private String typeFontClose = "</font></b>";
	private String highlightFontOpen = "<font color='#FF0000'>";
	private String highlightFontClose = "</font>";


	/**
	 * Creates an instance of this class, creating a new character and pane.
	 */
	public InfoCharacterDetails() {
		this(new PlayerCharacter(), new JTextPane());
	}

	/**
	 * Creates an instance of this class taking in a character and a pane.
	 * @param aPC the player character that needs it's stats displayed.
	 * @param mainOutput the pane that the stats will be displayed on.
	 */
	public InfoCharacterDetails(PlayerCharacter aPC, JTextPane mainOutput){
		setPC(aPC);
		setPane(mainOutput);
		setStatText();
	}

	/**
	 * Gets the pane that is used.
	 * @return the pane that is used.
	 */
	public JTextPane getPane() {
		return mainOutput;
	}

	public JScrollPane getScrollPane() {
		JScrollPane scrollPane = new javax.swing.JScrollPane();
		mainOutput.setCaretPosition(0);
		scrollPane.setViewportView(mainOutput);
		return scrollPane;
	}

	/**
	 * Gets the default PlayerCharacter object used by this class.
	 * @return the character being used.
	 */
	public PlayerCharacter getPC(){
		return this.aPC;
	}

	/**
	 * Sets the pane field of this class.
	 * @param o the new pane that will be used.
	 */
	public void setPane(JTextPane o){
		mainOutput = o;
	}

	/**
	 * Sets the default <code>PlayerCharacter</code> object used by this class.
	 * @param aPC the new default character.
	 */
	public void setPC(PlayerCharacter aPC){
		this.aPC = aPC;
	}

	/**
	 * Calls the <code>setStatText</code> and passes it the pane that is used
	 * for displaying.
	 */
	public void setStatText() {
		setStatText(getPC(), getPane());
	}

	/**
	 * Sets the HTML text used to display calculated stats such as AC, BAB,
	 * saves, etc.
	 * @param pc the character you want to populate the stat pane with.
	 */
	public void setStatText(PlayerCharacter pc){
		setStatText(pc, getPane());
	}

	/**
	 * <p>
	 * This sets the text of the JTextPane for the specified PC. It uses an
	 * output sheet template, specified by the templateName option; it uses
	 * <code>pcgen.io.ExportHandler</code> to transform the template file
	 * into an StringWriter, and then sets the text of the text pane as html.
	 * This allows us easy access to changing the content or format of the stat
	 * block, and also allows us to easily use a different output format if
	 * necessary.
	 * </p>
	 *
	 * @param aPC
	 *            the character you want to populate the stat labelbox with.
	 * @param aPane
	 *            the <code>JLabelPane</code> you want to be populated.
	 */
	public void setStatText(PlayerCharacter aPC, JTextPane aPane) {
		boolean outputSheet = false;
		if(outputSheet) {
			String baseDir = SettingsHandler.getGmgenPluginDir().toString();
			String initiativeDir =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".initiativeDir", "Initiative");
			String templateName = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".templateName", "csheet_gmgen_statblock.htm");
			File template = new File(baseDir + File.separator + initiativeDir + File.separator + templateName);
			ExportHandler export = new ExportHandler(template);
			StringWriter sWriter = new StringWriter();
			export.write(aPC,new BufferedWriter(sWriter));
			aPane.setEditorKit(aPane.getEditorKitForContentType("text/html"));
			aPane.setText(sWriter.toString());
		}
		else {
			int bonus = 0;
			StringBuffer statBuf = new StringBuffer();

			List itemList = aPC.getEquipSet();

			statBuf.append("<html>");
			if (aPC != null){
				Globals.setCurrentPC(aPC);
				statBuf.append(getStatBlockHeader(aPC));
				statBuf.append("<body class='Normal' lang='EN-US'>");
				statBuf.append(getStatBlockTitle(aPC));
				statBuf.append(getStatBlockCore(aPC));
				statBuf.append("<DIV style='MARGIN: 0px 10px'>");
				statBuf.append(getStatBlockLineSkills(aPC));
				statBuf.append(getStatBlockLinePossessions(aPC));
				try {
					statBuf.append(getStatBlockLineSpells(aPC));
				}
				catch(Exception e) {
					Logging.errorPrint(e.getMessage(), e);
				}
				statBuf.append("</DIV>");

				statBuf.append("<br>");
			}

			statBuf.append("</html>");
			aPane.setEditorKit(aPane.getEditorKitForContentType("text/html"));
			aPane.setText(statBuf.toString());
		}
	}

	private String getStatBlockHeader(PlayerCharacter aPC) {
		StringBuffer statBuf = new StringBuffer();
		PlayerCharacterOutput pcOut = new PlayerCharacterOutput(aPC);

		statBuf.append("<head><title>");
		statBuf.append(pcOut.getName());//|NAME|
		statBuf.append(" - ");
		statBuf.append(aPC.getPlayersName());//|PLAYERNAME|
		statBuf.append("(");
		statBuf.append(aPC.getCostPool());//|POOL.COST|
		statBuf.append(" Points) in GMGEN Statblock Format");
		statBuf.append("</title>");
		statBuf.append("<style type='text/css'>");
		statBuf.append("a:link {color: #006699}");
		statBuf.append("a:visited {color: #006699}");
		statBuf.append("a:hover {color: #006699}");
		statBuf.append("a:active {color: #006699}");
		statBuf.append(".type {color:#555555;font-weight:bold}");
		statBuf.append(".highlight {color:#FF0000}");
		statBuf.append(".dialog {color:#006699}");
		statBuf.append("</style></head>\n");

		return statBuf.toString();
	}

	private String getStatBlockTitle(PlayerCharacter aPC) {
		StringBuffer statBuf = new StringBuffer();
		PlayerCharacterOutput pcOut = new PlayerCharacterOutput(aPC);

		statBuf.append("<p class='gork'><font size='+1'><b>");
		statBuf.append(pcOut.getName());//|NAME|
		statBuf.append(", ");
		statBuf.append(pcOut.getGender());//|GENDER|
		statBuf.append(" ");
		statBuf.append(pcOut.getRace());//|RACE|
		String region = pcOut.getRegion();//|REGION|.|%|
		if(region != "" && region != null && region != "None") {
			statBuf.append(" From " + region + " ");
		}
		statBuf.append(pcOut.getClasses() + " ");//|CLASSLIST|
		statBuf.append("</b></font></p>\n");

		return statBuf.toString();
	}

	private String getStatBlockCore(PlayerCharacter aPC) {
		StringBuffer statBuf = new StringBuffer();
		PlayerCharacterOutput pcOut = new PlayerCharacterOutput(aPC);

		statBuf.append("<font class='type'>CR</font> ");
		statBuf.append(pcOut.getCR());//|CR|
		statBuf.append("; ");

		statBuf.append("<font class='type'>Size</font> ");
		statBuf.append(pcOut.getSize());//|SIZE|
		statBuf.append("; ");

		statBuf.append("<font class='type'>Type</font> ");
		statBuf.append(pcOut.getCritterType());//|TYPE|
		statBuf.append("; ");

		statBuf.append("<font class='type'>HD</font> ");
		statBuf.append(pcOut.getHitDice());//|HITDICE|
		statBuf.append("; ");

		statBuf.append("<font class='type'>hp</font> ");
		statBuf.append(pcOut.getHitPoints());//|HP|
		statBuf.append("; ");

		statBuf.append("<font class='type'>Init</font> <font class='highlight'>");
		statBuf.append(pcOut.getInitTotal());//|INITIATIVEMOD|
		statBuf.append("</font> (");
		statBuf.append(pcOut.getInitStatMod());//|STAT.1.MOD|
		statBuf.append("Dex, ");
		statBuf.append(pcOut.getInitMiscMod());//|INITIATIVEMISC|
		statBuf.append("Misc); ");

		statBuf.append("<font class='type'>Spd</font> ");
		statBuf.append(pcOut.getSpeed());//|MOVEMENT|
		statBuf.append("; ");

		statBuf.append("<font class='type'>AC</font> <font class='highlight'>");
		statBuf.append(pcOut.getAC());//|AC.Total|
		statBuf.append("</font> (flatfooted <font class='highlight'>");
		statBuf.append(pcOut.getACFlatFooted());//|AC.Flatfooted|
		statBuf.append("</font>, touch <font class='highlight'>");
		statBuf.append(pcOut.getACTouch());//|AC.Touch|
		statBuf.append("</font>); ");

		statBuf.append("<font class='type'>Melee:</font> <a href='attack:Melee\\");
		statBuf.append(pcOut.getMeleeTotal());//|ATTACK.MELEE.TOTAL|
		statBuf.append("' class='highlight'>");
		statBuf.append(pcOut.getMeleeTotal());//|ATTACK.MELEE.TOTAL|
		statBuf.append("</a>; ");

		statBuf.append("<font class='type'>Ranged:</font> <a href='attack:Ranged\\");
		statBuf.append(pcOut.getRangedTotal());//|ATTACK.RANGED.TOTAL|
		statBuf.append("' class='highlight'>");
		statBuf.append(pcOut.getRangedTotal());//|ATTACK.RANGED.TOTAL|
		statBuf.append("</a>; ");

		statBuf.append("<font class='type'>Weapons:</font>");

		List weaponList = aPC.getExpandedWeapons(Constants.MERGE_ALL);
		boolean firstLine = true;
		for(int i = 0; i < weaponList.size(); i++) {
			Equipment eq = (Equipment) weaponList.get(i);
			statBuf.append("<a href=" + '"' + "attack:");
			statBuf.append(pcOut.getWeaponName(eq));//|WEAPON.%weap.NAME|
			statBuf.append("\\");
			statBuf.append(pcOut.getWeaponToHit(i));//|WEAPON.%weap.TOTALHIT|
			statBuf.append("\\");
			statBuf.append(pcOut.getWeaponRange(eq));//|WEAPON.%weap.RANGE|
			statBuf.append("\\");
			statBuf.append(pcOut.getWeaponType(eq));//|WEAPON.%weap.TYPE|
			statBuf.append("\\");
			statBuf.append(pcOut.getWeaponDamage(i));//|WEAPON.%weap.DAMAGE|
			statBuf.append("\\");
			statBuf.append(pcOut.getWeaponCritRange(i));//|WEAPON.%weap.CRIT|
			statBuf.append("\\");
			statBuf.append(pcOut.getWeaponCritMult(i));//|WEAPON.%weap.MULT|
			statBuf.append("\\");
			statBuf.append(pcOut.getWeaponHand(eq));//|WEAPON.%weap.HAND|
			statBuf.append("\\");
			statBuf.append(pcOut.getWeaponSize(eq));//|WEAPON.%weap.SIZE|
			statBuf.append("\\");
			statBuf.append(pcOut.getWeaponSpecialProperties(eq));//|WEAPON.%weap.SPROP|
			statBuf.append('"' + " class=" + '"' + "dialog" + '"' + "> ");

			statBuf.append(pcOut.getWeaponName(eq));//|WEAPON.%weap.NAME|
			statBuf.append(" ");
			statBuf.append(pcOut.getWeaponToHit(i));//|WEAPON.%weap.TOTALHIT|
			statBuf.append(" ");
			statBuf.append(pcOut.getWeaponRange(eq));//|WEAPON.%weap.RANGE|
			statBuf.append("/");
			statBuf.append(pcOut.getWeaponType(eq));//|WEAPON.%weap.TYPE|
			statBuf.append(" (");
			statBuf.append(pcOut.getWeaponDamage(i));//|WEAPON.%weap.DAMAGE|
			statBuf.append(" ");
			statBuf.append(pcOut.getWeaponCritRange(i));//|WEAPON.%weap.CRIT|
			statBuf.append("/x");
			statBuf.append(pcOut.getWeaponCritMult(i));//|WEAPON.%weap.MULT|
			statBuf.append(" ");
			statBuf.append(pcOut.getWeaponHand(eq));//|WEAPON.%weap.HAND|
			statBuf.append(" ");
			statBuf.append(pcOut.getWeaponSize(eq));//|WEAPON.%weap.SIZE|
			statBuf.append(" ");
			statBuf.append(pcOut.getWeaponSpecialProperties(eq));//|WEAPON.%weap.SPROP|
			statBuf.append(") </a> or ");
		}

		statBuf.append("<a href=" + '"' + "attack:Unarmed\\");
		statBuf.append(pcOut.getExportToken("WEAPONH.TOTALHIT"));//|WEAPONH.TOTALHIT|
		statBuf.append("\\\\B\\");
		statBuf.append(pcOut.getExportToken("WEAPONH.DAMAGE"));//|WEAPONH.DAMAGE|
		statBuf.append("\\");
		statBuf.append(pcOut.getExportToken("WEAPONH.CRIT"));//|WEAPONH.CRIT|
		statBuf.append("\\");
		statBuf.append(pcOut.getExportToken("WEAPONH.MULT"));//|WEAPONH.MULT|
		statBuf.append('"' + " class=" + '"' + "dialog" + '"' + "> ");

		statBuf.append("Unarmed ");
		statBuf.append(pcOut.getExportToken("WEAPONH.TOTALHIT"));//|WEAPONH.TOTALHIT|
		statBuf.append(" (");
		statBuf.append(pcOut.getExportToken("WEAPONH.DAMAGE"));//|WEAPONH.DAMAGE|
		statBuf.append(" ");
		statBuf.append(pcOut.getExportToken("WEAPONH.CRIT"));//|WEAPONH.CRIT|
		statBuf.append("/x");
		statBuf.append(pcOut.getExportToken("WEAPONH.MULT"));//|WEAPONH.MULT|
		statBuf.append(");</a> ");

		statBuf.append("<font class='type'>SA:</font> ");
		statBuf.append(pcOut.getSpecialAbilities());//|SPECIALLIST|
		statBuf.append("; ");

		statBuf.append("<font class='type'>Vision:</font> ");
		statBuf.append(pcOut.getVision());//|VISION|
		statBuf.append(" ");

		statBuf.append("<font class='type'>AL:</font> ");
		statBuf.append(pcOut.getAlignmentShort());//|ALIGNMENT.SHORT|
		statBuf.append("; ");

		statBuf.append("<font class='type'>Sv:</font> Fort <font class='highlight'>");
		statBuf.append(pcOut.getSaveFort());//|CHECK.FORTITUDE.TOTAL|
		statBuf.append("</font>, Ref <font class='highlight'>");
		statBuf.append(pcOut.getSaveRef());//|CHECK.REFLEX.TOTAL|
		statBuf.append("</font>, Will <font class='highlight'>");
		statBuf.append(pcOut.getSaveWill());//|CHECK.WILLPOWER.TOTAL|
		statBuf.append("</font>; ");

		StatList sl = pcOut.getStatList();
		List statList = sl.getStats();
		firstLine = true;
		for(int i = 0; i < statList.size(); i++) {
			PCStat stat = (PCStat) statList.get(i);

			if(aPC.isNonAbility(i)) {
				statBuf.append("<font class='type'>");
				statBuf.append(stat.getAbb());//|STAT.%stat.NAME|
				statBuf.append("</font> ");

				statBuf.append("*");//|STAT.%stat|
				statBuf.append("&nbsp;(");
				statBuf.append("0");//|STAT.%stat.MOD|
				statBuf.append(") ");
			}
			else {
				statBuf.append("<font class='type'>");
				statBuf.append(stat.getAbb());//|STAT.%stat.NAME|
				statBuf.append("</font> ");

				statBuf.append(pcOut.getStat(stat.getAbb()));//|STAT.%stat|
				statBuf.append("&nbsp;(");
				statBuf.append(pcOut.getStatMod(stat.getAbb()));//|STAT.%stat.MOD|
				statBuf.append(") ");
			}
		}

		statBuf.append("</p>\n");

		return statBuf.toString();
	}

	private String getStatBlockLineSkills(PlayerCharacter aPC) {
		StringBuffer statBuf = new StringBuffer();
		PlayerCharacterOutput pcOut = new PlayerCharacterOutput(aPC);

		statBuf.append("<p><font class='type'>Skills and Feats:</font>&nbsp;");

		ArrayList skillList = aPC.getSkillListInOutputOrder();
		boolean firstLine = true;
		for(int i = 0; i < skillList.size(); i++) {
			Skill skill = (Skill) skillList.get(i);
			int modSkill = -1;
			if (skill.getKeyStat().compareToIgnoreCase(Constants.s_NONE) != 0) {
				modSkill = skill.modifier().intValue() - aPC.getStatList().getStatModFor(skill.getKeyStat());
			}
			if (skill.getTotalRank().intValue() > 0 || modSkill > 0) {
				int temp = skill.modifier().intValue() + skill.getTotalRank().intValue();

				statBuf.append("<a href='skill:");
				statBuf.append(skill.getOutputName());//|SKILL.%skill|
				statBuf.append("\\");
				statBuf.append(temp);//|SKILL.%skill.TOTAL|
				statBuf.append("' class='dialog'> ");

				statBuf.append(skill.getOutputName());//|SKILL.%skill|
				statBuf.append(" (");
				statBuf.append(temp);//|SKILL.%skill.TOTAL|
				statBuf.append(")</a>");
				if(!firstLine) {
					statBuf.append(", ");
				}
				firstLine = false;
			}
		}
		statBuf.append("; ");
		statBuf.append(pcOut.getFeatList());//|FEATLIST|
		statBuf.append("</p>\n");

		return statBuf.toString();
	}

	private String getStatBlockLinePossessions(PlayerCharacter aPC) {
		StringBuffer statBuf = new StringBuffer();
		PlayerCharacterOutput pcOut = new PlayerCharacterOutput(aPC);

		statBuf.append("<p><font class='type'>Possessions:</font>&nbsp;");
		statBuf.append(pcOut.getEquipmentList());//|FOR.0,(COUNT[EQUIPMENT]+1),1,&nbsp;\EQ.%.QTY\&nbsp;\EQ.%.NAME\, ,COMMA,1|
		statBuf.append("</p>\n");

		return statBuf.toString();
	}

	private String getStatBlockLineSpells(PlayerCharacter aPC) {
		StringBuffer statBuf = new StringBuffer();
		PlayerCharacterOutput pcOut = new PlayerCharacterOutput(aPC);
		List domainList = aPC.getCharacterDomainList();
		if(domainList.size() > 0) {
			//Domains
			//Deity
			statBuf.append("<p>");
			statBuf.append("<font class='type'>Deity:</font>");
			statBuf.append(pcOut.getDeity());
			statBuf.append("<br>");
			statBuf.append("<font class='type'>Domains:</font>&nbsp;");
			//Domain List with powers
			boolean firstLine = true;
			for(int i = 0; i < domainList.size(); i++) {
				if(!firstLine) {
					statBuf.append(", ");
				}
				firstLine = false;
				Domain dom = ((CharacterDomain)domainList.get(i)).getDomain();
				statBuf.append(pcOut.getDomainName(dom));//|DOMAIN|
				statBuf.append(" (");
				statBuf.append(pcOut.getDomainPower(dom));//|DOMAIN.POWER|
				statBuf.append(")");
			}
			statBuf.append("</p>");
		}
		
		statBuf.append("<p>");
		
		/*
			<p>
			<!-- Start Racial Innate Spells -->
			|FOR,%spellrace,COUNT[SPELLRACE],COUNT[SPELLRACE],1,0|
			|IIF(%spellrace:0)|
			<!-- No innate spells -->
			|ELSE|
			|FOR,%spellbook,1,1,1,1|
			|FOR,%class,0,0,1,1|
			|FOR,%level,0,0,1,1|
			|%SPELLLISTBOOK%class.%level.%spellbook|
			<font class="type">Racial Innate Spells</font>
			<br>
			<!-- Start Racial Innate Spell listing -->
			|FOR,%spell,0,COUNT[SPELLSINBOOK%class.%spellbook.%level]-1,1,0|
			<a href="spell:|SPELLMEM.%class.%spellbook.%level.%spell.NAME|\|SPELLMEM.%class.%spellbook.%level.%spell.DESC|\|SPELLMEM.%class.%spellbook.%level.%spell.RANGE|\|SPELLMEM.%class.%spellbook.%level.%spell.CASTINGTIME|\|SPELLMEM.%class.%spellbook.%level.%spell.SAVEINFO|\|SPELLMEM.%class.%spellbook.%level.%spell.DURATION|\|SPELLMEM.%class.%spellbook.%level.%spell.TARGET|" class="dialog">
			|SPELLMEM.%class.%spellbook.%level.%spell.NAME|
			</a>
			(|SPELLMEM.%class.%spellbook.%level.%spell.TIMES|)(DC:|SPELLMEM.%class.%spellbook.%level.%spell.DC|),
			|ENDFOR|
			|%|
			|ENDFOR|
			|ENDFOR|
			|ENDFOR|
			<!-- End Racial Innate Spells -->
			<!-- Start Other Innate Spells -->
			|FOR,%spellbook,2,COUNT[SPELLBOOKS]-1,1,0|
			<br>
			|FOR,%class,0,0,1,1|
			|FOR,%level,0,0,1,1|
			|%SPELLLISTBOOK%class.%level.%spellbook|
			<br>
			<font class="type">|SPELLBOOKNAME.%spellbook| Innate Spells</font>
			<br>
			|FOR,%spell,0,COUNT[SPELLSINBOOK%class.%spellbook.%level]-1,1,0|
			<a href="spell:|SPELLMEM.%class.%spellbook.%level.%spell.NAME|\|SPELLMEM.%class.%spellbook.%level.%spell.DESC|\|SPELLMEM.%class.%spellbook.%level.%spell.RANGE|\|SPELLMEM.%class.%spellbook.%level.%spell.CASTINGTIME|\|SPELLMEM.%class.%spellbook.%level.%spell.SAVEINFO|\|SPELLMEM.%class.%spellbook.%level.%spell.DURATION|\|SPELLMEM.%class.%spellbook.%level.%spell.TARGET|" class="dialog">
			|SPELLMEM.%class.%spellbook.%level.%spell.NAME|
			</a>
			(|SPELLMEM.%class.%spellbook.%level.%spell.TIMES|)(DC:|SPELLMEM.%class.%spellbook.%level.%spell.DC|),
			|ENDFOR|
			|%|
			|ENDFOR|
			|ENDFOR|
			|ENDFOR|
			<!-- End Other Innate Spells -->
			|ENDIF|
			|ENDFOR|
			<!-- End Innate Spells -->
			|FOR,%spellbook,0,0,1,0|
			|FOR,%class,COUNT[SPELLRACE],COUNT[SPELLRACE]+COUNT[CLASSES]-1,1,1|
			|%SPELLLISTCLASS%class|
			<!-- START Spell list Header Table (Known) -->
			<br>
			<font class="type">|SPELLLISTCLASS.%class|
			|IIF(SPELLLISTCLASS.%class:Psychic Warrior.OR.SPELLLISTCLASS.%class:Psion)|
			Powers
			|ELSE|
			Spells Known
			|ENDIF|
			</font>
			<br>
			<!-- End Spell List Header Table (Known) -->
			<!-- Start Known Spells -->
			|FOR,%level,0,9,1,1|
			|FOR,%spellcount,COUNT[SPELLSINBOOK%class.%spellbook.%level],COUNT[SPELLSINBOOK%class.%spellbook.%level],1,0|
			|IIF(%spellcount:0)|
			|ELSE|
			<br>
			<font class="type">Level %level</font>
			<br>
			|FOR,%spell,0,COUNT[SPELLSINBOOK%class.%spellbook.%level]-1,1,0|
			<a href="spell:|SPELLMEM.%class.%spellbook.%level.%spell.NAME|\|SPELLMEM.%class.%spellbook.%level.%spell.DESC|\|SPELLMEM.%class.%spellbook.%level.%spell.RANGE|\|SPELLMEM.%class.%spellbook.%level.%spell.CASTINGTIME|\|SPELLMEM.%class.%spellbook.%level.%spell.SAVEINFO|\|SPELLMEM.%class.%spellbook.%level.%spell.DURATION|\|SPELLMEM.%class.%spellbook.%level.%spell.TARGET|" class="dialog">
			|SPELLMEM.%class.%spellbook.%level.%spell.NAME|
			</a>,
			|IIF(SPELLLISTCLASS.%class:Psychic Warrior.OR.SPELLLISTCLASS.%class:Psion)|
			|FOR,%ppcost,(%level*2)-1,(%level*2)-1,1,1|
			|IIF(%ppcost:-1)|
			<i>PP:</i> 0/1
			|ELSE|
			<i>PP:</i> %ppcost
			|ENDIF|
			|ENDFOR|
			|ENDIF|
			|ENDFOR|
			|ENDIF|
			|ENDFOR|
			|ENDFOR|
			<br>
			|%|
			|ENDFOR|
			|ENDFOR|
			<!-- End Known Spells -->
			<!-- ================================================================ -->
			<!-- Start Prepared Spells -->
			|FOR,%memorised,COUNT[SPELLRACE]+COUNT[SPELLBOOKS]-2,COUNT[SPELLRACE]+COUNT[SPELLBOOKS]-2,1,0|
			|IIF(%memorised:0)|
			|ELSE|
			<!-- Start Regular Prepared -->
			|FOR,%spellbook,2,COUNT[SPELLBOOKS]-1,1,0|
			|FOR,%foo,COUNT[SPELLRACE],COUNT[SPELLRACE],1,1|
			|FOR,%bar,COUNT[SPELLSINBOOK0.%spellbook.0],COUNT[SPELLSINBOOK0.%spellbook.0],1,1|
			|IIF(%foo:0.OR.%bar:0)|
			<br>
			<font class="type">|SPELLBOOKNAME.%spellbook| Spellbook:</font>
			<br>
			|FOR,%class,COUNT[SPELLRACE],COUNT[SPELLRACE]+COUNT[CLASSES]-1,1,1|
			<br>
			<font class="type">|SPELLLISTCLASS.%class|</font>
			<br>
			|FOR,%level,0,9,1,1|
			|FOR,%spelllevelcount,COUNT[SPELLSINBOOK%class.%spellbook.%level],COUNT[SPELLSINBOOK%class.%spellbook.%level],1,0|
			|IIF(%spelllevelcount:0)|
			<!-- no memorized spells for SPELLSINBOOK%class %spellbook %level -->
			|ELSE|
			<br>
			<font class="type">Level %level</font>
			<br>
			|FOR,%spell,0,COUNT[SPELLSINBOOK%class.%spellbook.%level]-1,1,0|
			<a href="spell:|SPELLMEM.%class.%spellbook.%level.%spell.NAME|\|SPELLMEM.%class.%spellbook.%level.%spell.DESC|\|SPELLMEM.%class.%spellbook.%level.%spell.RANGE|\|SPELLMEM.%class.%spellbook.%level.%spell.CASTINGTIME|\|SPELLMEM.%class.%spellbook.%level.%spell.SAVEINFO|\|SPELLMEM.%class.%spellbook.%level.%spell.DURATION|\|SPELLMEM.%class.%spellbook.%level.%spell.TARGET|" class="dialog">
			|SPELLMEM.%class.%spellbook.%level.%spell.NAME|
			</a>
			(|SPELLMEM.%class.%spellbook.%level.%spell.TIMES|)(DC:|SPELLMEM.%class.%spellbook.%level.%spell.DC|),
			|ENDFOR|
			|ENDIF|
			|ENDFOR|
			<!-- END FOR,%spellcount,COUNT[SPELLSINBOOK%class.%spellbook.0],COUNT[SPELLSINBOOK%class.%spellbook.0],1,0 -->
			|ENDFOR|
			<!-- END SPELLLISTCLASS%class -->
			|%|
			<!-- END FOR,%class,COUNT[SPELLRACE],COUNT[SPELLRACE]+COUNT[CLASSES]-1,1,1 -->
			|ENDFOR|
			|ELSE|
			|ENDIF|
			<!-- END FOR,%bar,COUNT[SPELLSINBOOK0.%spellbook.0],COUNT[SPELLSINBOOK0.%spellbook.0],1,1 -->
			|ENDFOR|
			<!-- END FOR,%foo,COUNT[SPELLRACE],COUNT[SPELLRACE],1,1 -->
			|ENDFOR|
			<!-- END FOR,%spellbook,2,COUNT[SPELLBOOKS]-1,1,0 -->
			|ENDFOR|
			<!-- ### END class Spellbook memorized spells ### -->
			<!-- START FALSE IIF(%memorised:0) -->
			|ENDIF|
			|ENDFOR|
			<!-- ### END MEMORIZED ### -->
			
			<!-- End Prepared Spells -->
		*/
		
		ArrayList classList = aPC.getClassList();
		for(int i = 0; i < classList.size(); i++) {
			PObject aObject = aPC.getSpellClassAtIndex(i);
			PCClass pcclass = (PCClass) aObject;
			if (aObject != null) {
				int level = 0;
				List spellList = aObject.getCharacterSpell(null, Globals.getDefaultSpellBook(), level);
				if(spellList.size() >= 1) {
					statBuf.append("<font class='type'>" + pcclass.getName() + ":</font><br> ");
				}
				while(spellList.size() >= 1) {
					statBuf.append("<font class='type'>Level " + level + ":</font> ");

					boolean firstLine = true;
					for(int j = 0; j < spellList.size(); j++) {
						if(!firstLine) {
							statBuf.append(", ");
						}
						firstLine = false;

						Spell spell = ((CharacterSpell)spellList.get(j)).getSpell();
						CharacterSpell cs = (CharacterSpell)spellList.get(j);
						statBuf.append("<a href=" + '"' + "spell:");
						statBuf.append(spell.getName());
						statBuf.append("\\");
						statBuf.append(aPC.parseSpellString(spell.getDescription(), cs.getOwner()));
						statBuf.append("\\");
						statBuf.append(spell.getRange());
						statBuf.append("\\");
						statBuf.append(spell.getCastingTime());
						statBuf.append("\\");
						statBuf.append(spell.getSaveInfo());
						statBuf.append("\\");
						statBuf.append(aPC.parseSpellString(spell.getDuration(),cs.getOwner()));
						statBuf.append("\\");
						statBuf.append(aPC.parseSpellString(spell.getTarget(),cs.getOwner()));
						statBuf.append('"' + " class=" + '"' + "dialog" + '"' + ">");
						
						statBuf.append(spell.getName());
						statBuf.append("</a>");
					}
					level++;
					statBuf.append("<br>");
					spellList = aObject.getCharacterSpell(null, Globals.getDefaultSpellBook(), level);
				}
			}
		}

		return statBuf.toString();
	}
}
