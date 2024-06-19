/*
 * PObjectLoader.java
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
 * Created on February 22, 2002, 10:29 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:18:47 $
 *
 */

package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.PCSpell;
import pcgen.core.PObject;

/**
 * Made public 11 Aug 2003 by sage_sam for the LST editors
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public final class PObjectLoader
{
	/** 
	 * Creates a new instance of PObjectLoader 
	 * (Private since instances need never be created--API methods are public and static.)
	 */
	private PObjectLoader()
	{
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @return boolean true if the tag is parsed; else false.
	 */
	public static boolean parseTag(PObject obj, String aTag)
	{
		return parseTagLevel(obj, aTag, -9);
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	public static boolean parseTagLevel(PObject obj, String aTag, int anInt)
	{
		if ((obj == null) || (aTag.length() < 1))
		{
			return false;
		}

		obj.setNewItem(false);
		final char firstLetter = aTag.charAt(0);

		boolean result = false;
		
		switch( firstLetter )
		{
			case 'A':
				result = parseATagLevel(obj, aTag, anInt);
				break;

			case 'B':
				result = parseBTagLevel(obj, aTag, anInt);
				break;

			case 'C':
				result = parseCTagLevel(obj, aTag, anInt);
				break;

			case 'D':
				result = parseDTagLevel(obj, aTag, anInt);
				break;

			case 'K':
				result = parseKTagLevel(obj, aTag, anInt);
				break;

			case 'L':
				result = parseLTagLevel(obj, aTag, anInt);
				break;

			case 'M':
				result = parseMTagLevel(obj, aTag, anInt);
				break;

			case 'N':
				result = parseNTagLevel(obj, aTag, anInt);
				break;

			case 'O':
				result = parseOTagLevel(obj, aTag, anInt);
				break;

			case 'P':
			case 'R': // Restrict -- a PRE tag (fall through)
			case '!': // as in !PRE etc (continue fall-through)
				result = parsePTagLevel(obj, aTag, anInt);
				break;

			case 'S':
				result = parseSTagLevel(obj, aTag, anInt);
				break;

			case 'T':
				result = parseTTagLevel(obj, aTag, anInt);
				break;

			case 'U':
				result = parseUTagLevel(obj, aTag, anInt);
				break;

			case 'V':
				result = parseVTagLevel(obj, aTag, anInt);
				break;

			case 'W':
				result = parseWTagLevel(obj, aTag, anInt);
				break;

			default:
				result = false;
		}
		
		return result;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'A'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseATagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("ADD:"))
		{
			obj.addAddList(anInt, aTag.substring(4));
		}
		else if (aTag.startsWith("AUTO:"))
		{
			obj.addAutoArray(aTag.substring(5));
		}
		else
		{
			return false;
		}
		return true;
	}
	
	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'B'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseBTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("BONUS:"))
		{
			if (anInt > -9)
			{
				obj.addBonusList(anInt + "|" + aTag.substring(6), obj);
			}
			else
			{
				obj.addBonusList(aTag.substring(6), obj);
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'C'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseCTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("CAMPAIGN:") && !(obj instanceof Campaign))
		{
			// blank intentionally
		}
		else if (aTag.startsWith("CSKILL:"))
		{
			obj.setCSkillList(aTag.substring(7));
		}
		else if (aTag.startsWith("CCSKILL:"))
		{
			obj.setCcSkillList(aTag.substring(8));
		}
		else if (aTag.startsWith("CHOOSE:") && !aTag.startsWith("CHOOSE:LANGAUTO"))
		{
			obj.setChoiceString(aTag.substring(7));
		}
		else
		{
			return false;
		}
		return true;
	}
	
	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'D'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseDTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("DEFINE:"))
		{
// This was removed because the use of -9 and 0 are not the same; -9 is for level-independent stuff, 0 is to support apprentice classes
//			if (anInt > -9)
//			{
			obj.addVariable(anInt + "|" + aTag.substring(7));
//			}
//			else
//			{
//				obj.addVariable("0|" + aTag.substring(7));
//			}
		}
		else if (aTag.startsWith("DESC:"))
		{
			obj.setDescription(pcgen.io.EntityEncoder.decode(aTag.substring(5)));
		}
		else if (aTag.startsWith("DESCISPI:"))
		{
			obj.setDescIsPI(aTag.substring(9).startsWith("Y"));
		}
		else if (aTag.startsWith("DR:"))
		{
			if (anInt > -9)
			{
				obj.setDR(anInt + "|" + aTag.substring(3));
			}
			else
			{
				obj.setDR(aTag.substring(3));
			}
		}
		else
		{
			return false;
		}
		return true;
	}
	
	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'K'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseKTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("KEY:"))
		{
			obj.setKeyName(aTag.substring(4));
		}
		else if (aTag.startsWith("KIT:"))
		{
			if (anInt > -9)
			{
				obj.setKitString(anInt + "|" + aTag.substring(4));
			}
			else
			{
				obj.setKitString("0|" + aTag.substring(4));
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'L'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseLTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("LANGAUTO:"))
		{
			obj.addLanguageAutos(aTag.substring(9));
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'M'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseMTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("MOVE:"))
		{
			if (obj instanceof Equipment)
			{
				return false;
			}
			else
			{
				obj.setMoveRates(aTag.substring(5));
				obj.setMoveRatesFlag(0);
			}
		}
		else if (aTag.startsWith("MOVEA:"))
		{
			obj.setMoveRates(aTag.substring(6));
			obj.setMoveRatesFlag(1);
		}
		else if (aTag.startsWith("MOVECLONE:"))
		{
			obj.setMoveRates(aTag.substring(10));
			obj.setMoveRatesFlag(2);
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'N'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseNTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("NAME:"))
		{
			obj.setName(aTag.substring(5));
		}
		else if (aTag.startsWith("NAMEISPI:"))
		{
			obj.setNameIsPI(aTag.substring(9).startsWith("Y"));
		}
		else if (aTag.startsWith("NATURALATTACKS:"))
		{
			// first entry is primary, others are secondary
			// lets try the format:
			// NATURALATTACKS:primary weapon name,num attacks,damage|secondary1 weapon name,num attacks,damage|secondary2.....
			// damage will be of the form XdY+Z or XdY-Z
			if (anInt > -9)
			{
				obj.setNaturalAttacks(obj, anInt + "|" + aTag.substring(15));
			}
			else
			{
				obj.setNaturalAttacks(obj, aTag.substring(15));
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'O'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseOTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("OUTPUTNAME:"))
		{
			obj.setOutputName(aTag.substring(11));
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'P'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parsePTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("PRE") || aTag.startsWith("!PRE") || aTag.startsWith("RESTRICT:"))
		{
			obj.addPreReq(aTag);
		}
		else if (aTag.startsWith("REGION:"))
		{
			if (anInt > -9)
			{
				obj.setRegionString(anInt + "|" + aTag.substring(7));
			}
			else
			{
				obj.setRegionString("0|" + aTag.substring(7));
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'S'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseSTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("SOURCE"))
		{
			obj.setSource(aTag);
		}
		else if (aTag.startsWith("SA:"))
		{
			obj.setSpecialAbilityList(aTag.substring(3), anInt);
		}
		else if (aTag.startsWith("SPELL:") && !(obj instanceof Campaign))
		{
			obj.addSpells( anInt, createSpellList( aTag.substring(6) ) );
		}
		else if (aTag.startsWith("SPELLLEVEL:") && !(obj instanceof Campaign))
		{
			obj.addSpellLevel(aTag.substring(11));
		}
		else if (aTag.startsWith("SR:"))
		{
			if (anInt > -9)
			{
				obj.setSR(anInt + "|" + aTag.substring(3));
			}
			else
			{
				obj.setSR(aTag.substring(3));
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'T'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseTTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith(Constants.s_TAG_TYPE))
		{
			obj.setTypeInfo(aTag.substring(Constants.s_TAG_TYPE.length()));
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'U'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseUTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("UDAM:"))
		{
			obj.addUdamList(aTag.substring(5));
		}
		else if (aTag.startsWith("UMULT:"))
		{
			if (anInt > -9)
			{
				obj.addUmult(anInt + "|" + aTag.substring(6));
			}
			else
			{
				obj.addUmult(aTag.substring(6));
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'V'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseVTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("VISION:"))
		{
			if (anInt > -9)
			{
				obj.setVision(anInt + "|" + aTag.substring(7));
			}
			else
			{
				obj.setVision(aTag.substring(7));
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method parses a Tag and its value from an LST formatted file (or other
	 * source, such as an LST editor).  It applies the value of the tag to the
	 * provided PObject.  If a level is given, the tag value is applied to the
	 * object at the specified level [as appropriate for the tag].  A level of
	 * -9 or lower is treated as "at all levels."
	 * <p>
	 * This method handles ONLY tags starting with the letter 'W'.
	 * 
	 * @param obj PObject which the tag will be applied to
	 * @param aTag String tag and value to parse
	 * @param anInt int character level at which the tag becomes effective
	 * @return boolean true if the tag is parsed; else false.
	 */
	private static boolean parseWTagLevel(PObject obj, String aTag, int anInt)
	{
		if (aTag.startsWith("WEAPONAUTO:"))
		{
			obj.setWeaponProfAutos(aTag.substring(11));
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * This method creates a List of properly formatted SPELL tags.
	 * @since 12 Aug 2003 by sage_sam -- spell refactoring.
	 * @param level int level the spell is available
	 * @param sourceLine String containing the original spell source
	 * @return List of individual SPELL tags with pre-reqs applied to each
	 */
	private static List createSpellList(final int level, final String sourceLine)
	{
		return createSpellList(sourceLine);
	}
	
	private static List createSpellList(final String sourceLine)
	{
		List spellList=new ArrayList();
		
		// Accept the base line by default
		String spellSrc = sourceLine;

		// Search for pre-reqs denoted by square braces
		String preTag = null;
		final int i = sourceLine.lastIndexOf('[');
		if (i >= 0)
		{
			int j = sourceLine.lastIndexOf(']');
			if (j < i)
			{
				j = sourceLine.length();
			}
			preTag = sourceLine.substring(i + 1, j);
			spellSrc = sourceLine.substring(0, i);
		}

		final StringTokenizer aTok = new StringTokenizer(spellSrc, "|");
		StringBuffer spellBuf;
		while (aTok.hasMoreTokens())
		{
			PCSpell spell = new PCSpell();

			// Get the name/key out of the string
			spell.setName(aTok.nextToken());
			spell.setKeyName(spell.getName());

			// Get the number of times per day, if present (default is 1)
			if(aTok.hasMoreTokens())
			{
				spell.setTimesPerDay(aTok.nextToken());
			}
			else
			{
				spell.setTimesPerDay("1");
			}
						
			// Get the spellbook, if present (default is Innate)
			if(aTok.hasMoreTokens())
			{
				spell.setSpellbook(aTok.nextToken());
			}
			else
			{
				spell.setSpellbook("Innate");
			}

			// Set the pre-reqs if needed
			if( preTag != null )
			{
				StringTokenizer preTok=new StringTokenizer(preTag, "|");
				while( preTok.hasMoreTokens() )
				{
					spell.addPreReq(preTok.nextToken());
				}
			}

			// add the spell to the list
			spellList.add(spell);
		}

		// return the list of spells
		return spellList;
	}
}
