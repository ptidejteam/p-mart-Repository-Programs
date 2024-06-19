/*
 * IOConstants.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on March 19, 2002, 5:15 PM
 */
package pcgen.io;

/**
 * <code>IOConstants</code>
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

interface IOConstants
{

	String TAG_PCGVERSION = "PCGVERSION";

	// System information
	String TAG_CAMPAIGN = "CAMPAIGN";
	String TAG_CAMPAIGNS = "CAMPAIGNS";
	String TAG_VERSION = "VERSION";
	String TAG_ROLLMETHOD = "ROLLMETHOD";
	String TAG_EXPRESSION = "EXPRESSION";
	String TAG_PURCHASEPOINTS = "PURCHASEPOINTS";
	String TAG_UNLIMITEDPOOLCHECKED = "UNLIMITEDPOOLCHECKED";
	String TAG_POOLPOINTS = "POOLPOINTS";
	String TAG_GAMEMODE = "GAMEMODE";
	String TAG_TABLABEL = "TABLABEL";
	String TAG_AUTOSPELLS = "AUTOSPELLS";

	// Character bio
	String TAG_CHARACTERNAME = "CHARACTERNAME";
	String TAG_TABNAME = "TABNAME";
	String TAG_PLAYERNAME = "PLAYERNAME";
	String TAG_HEIGHT = "HEIGHT";
	String TAG_WEIGHT = "WEIGHT";
	String TAG_AGE = "AGE";
	String TAG_GENDER = "GENDER";
	String TAG_HANDED = "HANDED";
	String TAG_SKINCOLOR = "SKINCOLOR";
	String TAG_EYECOLOR = "EYECOLOR";
	String TAG_HAIRCOLOR = "HAIRCOLOR";
	String TAG_HAIRSTYLE = "HAIRSTYLE";
	String TAG_LOCATION = "LOCATION";
	String TAG_RESIDENCE = "RESIDENCE";
	String TAG_CITY = "CITY";
	String TAG_BIRTHPLACE = "BIRTHPLACE";
	String TAG_PERSONALITYTRAIT1 = "PERSONALITYTRAIT1";
	String TAG_PERSONALITYTRAIT2 = "PERSONALITYTRAIT2";
	String TAG_SPEECHPATTERN = "SPEECHPATTERN";
	String TAG_PHOBIAS = "PHOBIAS";
	String TAG_INTERESTS = "INTERESTS";
	String TAG_CATCHPHRASE = "CATCHPHRASE";
	String TAG_PORTRAIT = "PORTRAIT";

	// Character attributes
	String TAG_STAT = "STAT";
	String TAG_SCORE = "SCORE";
	String TAG_ALIGNMENT = "ALIGN";
	String TAG_RACE = "RACE";

	// Character experience
	String TAG_EXPERIENCE = "EXPERIENCE";

	// Character class(es)
	String TAG_CLASS = "CLASS";
	String TAG_SUBCLASS = "SUBCLASS";
	String TAG_CLASSLEVEL = "CLASSLEVEL";
	String TAG_LEVEL = "LEVEL";
	String TAG_CLASSABILITIESLEVEL = "CLASSABILITIESLEVEL";
	String TAG_HITPOINTS = "HITPOINTS";
	String TAG_SPELLBASE = "SPELLBASE";
	String TAG_PROHIBITED = "PROHIBITED";
	String TAG_SAVES = "SAVES";
	String TAG_SA = "SA";
	String TAG_SPECIALABILITIES = "SPECIALABILITIES";
	String TAG_SPECIALTY = "SPECIALTY";
	String TAG_SPECIALTIES = "SPECIALTIES";

	// Character templates
	String TAG_TEMPLATESAPPLIED = "TEMPLATESAPPLIED";
	String TAG_TEMPLATE = "TEMPLATE";
	String TAG_REGION = "REGION";

	// Character skills
	String TAG_CLASSBOUGHT = "CLASSBOUGHT";
	String TAG_CLASSSKILL = "CLASSSKILL";
	String TAG_SKILL = "SKILL";
	String TAG_SKILLPOOL = "SKILLPOOL";
	String TAG_CROSSCLASS = "CROSSCLASS";
	String TAG_RANKS = "RANKS";
	String TAG_COST = "COST";
	String TAG_SYNERGY = "SYNERGY";
	String TAG_OUTPUTORDER = "OUTPUTORDER";
	String TAG_ASSOCIATEDDATA = "ASSOCIATEDDATA";

	// Character languages
	String TAG_LANGUAGE = "LANGUAGE";

	// Character feats
	String TAG_FEAT = "FEAT";
	String TAG_TYPE = "TYPE";
	String TAG_DESC = "DESC";
	String TAG_APPLIEDTO = "APPLIEDTO";
	String TAG_FEATPOOL = "FEATPOOL";
	String TAG_MULTISELECT = "MULTISELECT";

	// Character weapon proficiencies
	String TAG_WEAPONPROF = "WEAPONPROF";
	String TAG_ARMORPROF = "ARMORPROF";

	// Character equipment
	String TAG_EQUIPMENT = "EQUIPMENT";
	String TAG_MONEY = "MONEY";
	String TAG_EQUIPNAME = "EQUIPNAME";
	String TAG_QUANTITY = "QUANTITY";
	String TAG_CARRIED = "CARRIED";
	String TAG_EQUIPPED = "EQUIPPED";
	String TAG_CONTAINS = "CONTAINS";
	String TAG_TOTALWT = "TOTALWT";
	String TAG_WT = "WT";
	String TAG_MISC = "MISC";
	String TAG_CUSTOMIZATION = "CUSTOMIZATION";
	String TAG_BASEITEM = "BASEITEM";
	String TAG_DATA = "DATA";
	String TAG_EQUIPSET = "EQUIPSET";

	// Character deity/domain
	String TAG_DEITY = "DEITY";
	String TAG_DEITYDOMAINS = "DEITYDOMAINS";
	String TAG_ALIGNALLOW = "ALIGNALLOW";
	String TAG_HOLYITEM = "HOLYITEM";
	String TAG_SYMBOL = "SYMBOL";
	String TAG_DEITYFAVWEAP = "DEITYFAVWEAP";
	String TAG_DEITYALIGN = "DEITYALIGN";
	String TAG_DOMAIN = "DOMAIN";
	String TAG_DOMAINFEATS = "DOMAINFEATS";
	String TAG_DOMAINGRANTS = "DOMAINGRANTS";
	String TAG_DOMAINSKILLS = "DOMAINSKILLS";
	String TAG_DOMAINSPECIALS = "DOMAINSPECIALS";
	String TAG_DOMAINSPELLS = "DOMAINSPELLS";
	String TAG_NAME = "NAME";
	String TAG_SOURCE = "SOURCE";
	String TAG_DEFINED = "DEFINED";
	String TAG_SPELL = "SPELL";
	String TAG_SPELLLIST = "SPELLLIST";
	String TAG_WEAPON = "WEAPON";

	// Character spells information
	String TAG_CANCASTPERDAY = "CANCASTPERDAY";
	String TAG_SPELLNAME = "SPELLNAME";
	String TAG_SPELLLEVEL = "SPELLLEVEL";
	String TAG_POWERNAME = "POWERNAME";
	String TAG_SCHOOL = "SCHOOL";
	String TAG_SUBSCHOOL = "SUBSCHOOL";
	String TAG_COMP = "COMP";
	String TAG_CT = "CT";
	String TAG_RANGE = "RANGE";
	String TAG_EFFECT = "EFFECT";
	String TAG_EFFECTTYPE = "EFFECTTYPE";
	String TAG_DURATION = "DURATION";
	String TAG_SAVE = "SAVE";
	String TAG_SR = "SR";
	String TAG_DESCRIPTOR = "DESCRIPTOR";
	String TAG_TIMES = "TIMES";
	String TAG_SPELLBOOK = "BOOK";
	String TAG_FEATLIST = "FEATLIST";

	// Character description/bio/history
	String TAG_CHARACTERBIO = "CHARACTERBIO";
	String TAG_CHARACTERDESC = "CHARACTERDESC";
	String TAG_CHARACTERCOMP = "CHARACTERCOMP";
	String TAG_CHARACTERASSET = "CHARACTERASSET";
	String TAG_CHARACTERMAGIC = "CHARACTERMAGIC";
	String TAG_NOTE = "NOTE";
	String TAG_ID = "ID";
	String TAG_PARENTID = "PARENTID";
	String TAG_VALUE = "VALUE";

	// Character follower
	String TAG_FOLLOWER = "FOLLOWER";
	String TAG_MASTER = "MASTER";
	String TAG_HITDICE = "HITDICE";
	String TAG_FILE = "FILE";

	// Kits
	String TAG_KIT = "KIT";
}
