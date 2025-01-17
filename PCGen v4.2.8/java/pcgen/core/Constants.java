/*
 * Constants.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: Constants.java,v 1.1 2006/02/21 01:00:27 vauchers Exp $
 */
package pcgen.core;

/**
 * This interface holds all global constants.
 *
 * (The reason for an interface rather than a class
 * is that an interface uses a little less memory.)
 *
 * @author     Jonas Karlsson
 * @version    $Revision: 1.1 $
 */
public interface Constants
{
	/** The extension for a campaign file   */
	String s_PCGEN_CAMPAIGN_EXTENSION = ".pcc";
	/** The extension for a character file   */
	String s_PCGEN_CHARACTER_EXTENSION = ".pcg";
	/** The extension for a party file   */
	String s_PCGEN_PARTY_EXTENSION = ".pcp";
	/** What a party template file name starts with.    */
	String s_PARTY_TEMPLATE_START = "psheet";
	/** What a character template file name starts with.    */
	String s_CHARACTER_TEMPLATE_START = "csheet";
	/** What a character template file name starts with.    */
	String s_EQSET_TEMPLATE_START = "eqsheet";
	/** What to show when no race or alignment is selected. */
	String s_NONE = "None";
	String s_NONESELECTED = "<none selected>";
	String html_NONESELECTED = "<html>none selected</html>";

	// Name of Shield Proficiency Feat
	String s_ShieldProficiency = "Shield Proficiency";

	/** What to display as the application's name */
	String s_APPNAME = "PCGen";

	String s_TempFileName = "currentPC";

	/** How to roll hitpoints. */
	int s_HP_STANDARD = 0;
	int s_HP_AUTOMAX = 1;
/////////////////////////////////////////////////
// Yanked for WotC compliance
//	int s_HP_PERCENTAGE = 2;
//	int s_HP_LIVING_GREYHAWK = 3;
//	int s_HP_LIVING_CITY = 4;
/////////////////////////////////////////////////

	/** SOURCE Display options */
	int SOURCELONG = 0;
	int SOURCESHORT = 1;
	int SOURCEPAGE = 2;
	int SOURCEWEB = 3;

	/** Encumbrance Constants */
	int LIGHT_LOAD = 0;
	int MEDIUM_LOAD = 1;
	int HEAVY_LOAD = 2;
	int OVER_LOAD = 3;

	/**
	 * HackMaster attributes
	 */
	int COMELINESS = 6;
	int HONOR = 7;

	/** */
	String s_TAG_TYPE = "TYPE:";

	/** What equipment to autogenerate */
	int AUTOGEN_RACIAL = 1;
	int AUTOGEN_MASTERWORK = 2;
	int AUTOGEN_MAGIC = 3;
	int AUTOGEN_EXOTICMATERIAL = 4;

	/**
	 * Game mode constants
	 */
	String DEADLANDS_MODE = "Deadlands";
	String DND_MODE = "DnD";
	String FADINGSUNSD20_MODE = "FadingSunsD20";
	//String HACKMASTER_MODE = "HackMaster";
	String HWNMNBSOL_MODE = "CallOfCthulhuD20";
	String L5R_MODE = "L5R";
	String SIDEWINDER_MODE = "Sidewinder";
	String SOVEREIGNSTONED20_MODE = "SovereignStoneD20";
	String STARWARS_MODE = "StarWars";
	String SPYCRAFT_MODE = "Spycraft";
	String WEIRDWARS_MODE = "WeirdWars";
	String WHEELOFTIME_MODE = "WheelOfTime";

	int ATTACKSTRING_MELEE = 0;
	int ATTACKSTRING_RANGED = 1;
	int ATTACKSTRING_UNARMED = 2;

	String s_CUSTOM = "CUSTOM";
	String s_GENERIC_ITEM = "Generic Item";

	int PAPERINFO_NAME = 0;
	int PAPERINFO_HEIGHT = 1;
	int PAPERINFO_WIDTH = 2;
	int PAPERINFO_TOPMARGIN = 3;
	int PAPERINFO_BOTTOMMARGIN = 4;
	int PAPERINFO_LEFTMARGIN = 5;
	int PAPERINFO_RIGHTMARGIN = 6;

	int DISPLAY_STYLE_NAME = 0;
	int DISPLAY_STYLE_NAME_CLASS = 1;
	int DISPLAY_STYLE_NAME_RACE = 2;
	int DISPLAY_STYLE_NAME_RACE_CLASS = 3;
	int DISPLAY_STYLE_NAME_CUSTOM = 4; // needs work!

	int MAX_OPEN_RECENT_ENTRIES = 5;

	int ROLLINGMETHOD_STANDARD = 1;
	int ROLLINGMETHOD_ALLSAME = 8;
	int ROLLINGMETHOD_PURCHASE = 9;

	/**
	 * Character panel tab constants
	 */
	int CHARACTER_TAB_SUMMARY = 0;
	int CHARACTER_TAB_RACE = 1;
	int CHARACTER_TAB_ABILITIES = 2;
	int CHARACTER_TAB_CLASSES = 3;
	int CHARACTER_TAB_SKILLS = 4;
	int CHARACTER_TAB_FEATS = 5;
	int CHARACTER_TAB_DOMAINS = 6;
	int CHARACTER_TAB_SPELLS = 7;
	int CHARACTER_TAB_INVENTORY = 8;
	int CHARACTER_TAB_DESCRIPTION = 9;

	String s_LINE_SEP = System.getProperty("line.separator");

	String s_standard_outputsheet_directory = "htmlxml";
	String s_pdf_outputsheet_directory = "pdf";

	int CHOOSER_SINGLECHOICEMETHOD_NONE = 0;	// do nothing
	int CHOOSER_SINGLECHOICEMETHOD_SELECT = 1;	// add single choice to selected list
	int CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT = 2;	// add single choice to selected list and then close

	boolean PRINTOUT_WEAPONPROF = true;

        // Static definitions of Equipment location strings
        String S_BOTH = "Both Hands";
        String S_CARRIED = "Carried";
        String S_DOUBLE = "Double Weapon";
        String S_EQUIPPED = "Equipped";
        String S_FINGERS = "Fingers,";
        String S_NATURAL_PRIMARY = "Natural-Primary";
        String S_NATURAL_SECONDARY = "Natural-Secondary";
        String S_NOTCARRIED = "Not Carried";
        String S_PRIMARY = "Primary Hand";
        String S_RAPIDSHOT = "Rapid Shot";
        String S_SECONDARY = "Secondary Hand";
        String S_SHIELD = "Shield";
        String S_UNARMED = "Unarmed";
	String S_TWOWEAPONS = "Two Weapons";
}
