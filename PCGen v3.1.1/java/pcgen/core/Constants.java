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
 * $Id: Constants.java,v 1.1 2006/02/21 00:05:26 vauchers Exp $
 */
package pcgen.core;

/**
 * This interface holds all global constants. (The reason for an interface rather than a class is that an interface
 * uses a little less memory.)
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

	/** What to display as the application's name */
	String s_APPNAME = "PCGen";

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

	// Alignment constants
	int ALIGNMENT_LG = 0;
	int ALIGNMENT_LN = 1;
	int ALIGNMENT_LE = 2;
	int ALIGNMENT_NG = 3;
	int ALIGNMENT_TN = 4;
	int ALIGNMENT_NE = 5;
	int ALIGNMENT_CG = 6;
	int ALIGNMENT_CN = 7;
	int ALIGNMENT_CE = 8;
	int ALIGNMENT_NONE = 9;
	int ALIGNMENT_DEITY = 10;

	/** Short alignment strings */
	String[] s_ALIGNSHORT = {
		"LG",
		"LN",
		"LE",
		"NG",
		"TN",
		"NE",
		"CG",
		"CN",
		"CE",
		"None",
		"Deity"
	};
	/** Long alignment strings */
	String[] s_ALIGNLONG = new String[]{
		"Lawful Good",
		"Lawful Neutral",
		"Lawful Evil",
		"Neutral Good",
		"Neutral",
		"Neutral Evil",
		"Chaotic Good",
		"Chaotic Neutral",
		"Chaotic Evil",
		Constants.s_NONESELECTED,
		"Deity's"
	};

	/** Size constants */
	int SIZE_F = 0;
	int SIZE_D = 1;
	int SIZE_T = 2;
	int SIZE_S = 3;
	int SIZE_M = 4;
	int SIZE_L = 5;
	int SIZE_H = 6;
	int SIZE_G = 7;
	int SIZE_C = 8;

	/** */
	String s_TAG_TYPE = "TYPE:";

	/** What equipment to autogenerate */
	int AUTOGEN_RACIAL = 1;
	int AUTOGEN_MASTERWORK = 2;
	int AUTOGEN_MAGIC = 3;
	int AUTOGEN_EXOTICMATERIAL = 4;

	/** Size related constants */
	String[] s_SIZELONG = {
		"Fine"
		, "Diminutive"
		, "Tiny"
		, "Small"
		, "Medium"
		, "Large"
		, "Huge"
		, "Gigantic"
		, "Colossal"
	};
	String[] s_SIZESHORT = {
		"F"
		, "D"
		, "T"
		, "S"
		, "M"
		, "L"
		, "H"
		, "G"
		, "C"
	};
	char[] s_SIZESHORTCHAR = {
		'F'
		, 'D'
		, 'T'
		, 'S'
		, 'M'
		, 'L'
		, 'H'
		, 'G'
		, 'C'
	};

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
}
