/*
 * PCGVer1Parser.java
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
 * Created on March 22, 2002, 12:15 AM
 */

package pcgen.io;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.NoteItem;
import pcgen.core.PlayerCharacter;

/**
 * <code>PCGVer1Parser</code><br>
 * @author Thomas Behr 22-03-02
 * @version $Revision: 1.1 $
 */

class PCGVer1Parser implements IOConstants
{
	private List warnings = new ArrayList();
	private PlayerCharacter aPC;

	/**
	 * Constructor
	 */
	public PCGVer1Parser(PlayerCharacter aPC)
	{
		this.aPC = aPC;
	}

	/**
	 * Selector
	 *
	 * <br>author: Thomas Behr 22-03-02
	 *
	 * @return a list of warning messages
	 */
	public List getWarnings()
	{
		return warnings;
	}

	/**
	 * parse a String in PCG format
	 *
	 * <br>author: Thomas Behr 22-03-02
	 *
	 * @param s   the String to parse
	 */
	public void parsePCG(String s) throws PCGParseException
	{
		// TODO
	}


	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */

	/*
	 * ###############################################################
	 * System Information methods
	 * ###############################################################
	 */

	private void parsePoolPointsLine(String line)
	{
		try
		{
			aPC.setPoolAmount(Integer.parseInt(line.substring(TAG_POOLPOINTS.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Pool Points line ignored: " + line;
			warnings.add(message);
		}
	}

	/*
	 * ###############################################################
	 * Character Bio methods
	 * ###############################################################
	 */

	private void parseCharacterNameLine(String line)
	{
		aPC.setName(line.substring(TAG_CHARACTERNAME.length() + 1));
	}

	private void parseTabNameLine(String line)
	{
		aPC.setTabName(line.substring(TAG_TABNAME.length() + 1));
	}

	private void parsePlayerNameLine(String line)
	{
		aPC.setPlayersName(line.substring(TAG_PLAYERNAME.length() + 1));
	}

	private void parseHeightLine(String line)
	{
		try
		{
			aPC.setHeight(Integer.parseInt(line.substring(TAG_HEIGHT.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Height line ignored: " + line;
			warnings.add(message);
		}
	}

	private void parseWeightLine(String line)
	{
		try
		{
			aPC.setWeight(Integer.parseInt(line.substring(TAG_WEIGHT.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Weight line ignored: " + line;
			warnings.add(message);
		}
	}

	private void parseAgeLine(String line)
	{
		try
		{
			aPC.setAge(Integer.parseInt(line.substring(TAG_AGE.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Age line ignored: " + line;
			warnings.add(message);
		}
	}

	private void parseGenderLine(String line)
	{
		aPC.setGender(line.substring(TAG_GENDER.length() + 1));
	}

	private void parseHandedLine(String line)
	{
		aPC.setHanded(line.substring(TAG_HANDED.length() + 1));
	}

	private void parseSkinColorLine(String line)
	{
		aPC.setSkinColor(line.substring(TAG_SKINCOLOR.length() + 1));
	}

	private void parseEyeColorLine(String line)
	{
		aPC.setEyeColor(line.substring(TAG_EYECOLOR.length() + 1));
	}

	private void parseHairColorLine(String line)
	{
		aPC.setHairColor(line.substring(TAG_HAIRCOLOR.length() + 1));
	}

	private void parseHairStyleLine(String line)
	{
		aPC.setHairStyle(decodeChars(line.substring(TAG_HAIRSTYLE.length() + 1)));
	}

	private void parseLocationLine(String line)
	{
		aPC.setLocation(line.substring(TAG_LOCATION.length() + 1));
	}

	private void parseResidenceLine(String line)
	{
		aPC.setLocation(line.substring(TAG_RESIDENCE.length() + 1));
	}

	private void parsePersonalityTrait1Line(String line)
	{
		aPC.setTrait1(decodeChars(line.substring(TAG_PERSONALITYTRAIT1.length() + 1)));
	}

	private void parsePersonalityTrait2Line(String line)
	{
		aPC.setTrait2(decodeChars(line.substring(TAG_PERSONALITYTRAIT2.length() + 1)));
	}

	private void parseSpeechPatternLine(String line)
	{
		aPC.setSpeechTendency(decodeChars(line.substring(TAG_SPEECHPATTERN.length() + 1)));
	}

	private void parsePhobiasLine(String line)
	{
		aPC.setPhobias(decodeChars(line.substring(TAG_PHOBIAS.length() + 1)));
	}

	private void parseInterestsLine(String line)
	{
		aPC.setInterests(decodeChars(line.substring(TAG_INTERESTS.length() + 1)));
	}

	private void parseCatchPhraseLine(String line)
	{
		aPC.setCatchPhrase(decodeChars(line.substring(TAG_CATCHPHRASE.length() + 1)));
	}

	private void parsePortraitLine(String line)
	{
		aPC.setPortraitPath(line.substring(TAG_PORTRAIT.length() + 1));
	}

	/*
	 * ###############################################################
         * Character Attributes methods
	 * ###############################################################
	 */

	private void parseStatLine(String line)
	{
		// TODO
	}

	private void parseAlignmentLine(String line)
	{
		// TODO
	}

	private void parseRaceLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Class(es) methods
	 * ###############################################################
	 */

	private void parseClassLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Templates methods
	 * ###############################################################
	 */

	private void parseTemplateLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Skills methods
	 * ###############################################################
	 */

	private void parseSkillLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Feats methods
	 * ###############################################################
	 */

	private void parseFeatLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Equipment methods
	 * ###############################################################
	 */

	private void parseEquipmentLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Deity/Domain methods
	 * ###############################################################
	 */

	private void parseDeityLine(String line)
	{
		// TODO
	}

	private void parseDomainLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Spells Information methods
	 * ###############################################################
	 */

	private void parseSpellLine(String line)
	{
		// TODO
	}

	/*
	 * ###############################################################
         * Character Description/Bio/History methods
	 * ###############################################################
	 */

	private void parseCharacterBioLine(String line)
	{
		aPC.setBio(decodeChars(line.substring(TAG_CHARACTERBIO.length() + 1)));
	}

	private void parseCharacterDescLine(String line)
	{
		aPC.setDescription(decodeChars(line.substring(TAG_CHARACTERDESC.length() + 1)));
	}

	/*
	 * ###############################################################
         * Character Notes Tab methods
	 * ###############################################################
	 */

	private void parseNoteLine(String line)
	{
		final NoteItem ni = new NoteItem(-1, -1, "", "");
		final StringTokenizer tokens = new StringTokenizer("|");

		String tag;
		String data;
		String cToken;
		while (tokens.hasMoreTokens())
		{
			cToken = tokens.nextToken();

			int index = cToken.indexOf(":");
			tag = cToken.substring(0, index);
			data = cToken.substring(index + 1);

			if (tag.equals(TAG_NOTE))
			{
				ni.setName(data);
			}
			else if (tag.equals(TAG_ID))
			{
				try
				{
					ni.setIdValue(Integer.parseInt(data));
				}
				catch (NumberFormatException nfe)
				{
					ni.setIdValue(-1);

					final String message = "Illegal Notes line ignored: " + line;
					warnings.add(message);

					break;
				}
			}
			else if (tag.equals(TAG_PARENTID))
			{
				try
				{
					ni.setParentId(Integer.parseInt(data));
				}
				catch (NumberFormatException nfe)
				{
					ni.setIdValue(-1);

					final String message = "Illegal Notes line ignored: " + line;
					warnings.add(message);

					break;
				}
			}
			else if (tag.equals(TAG_VALUE))
			{
				ni.setValue(decodeChars(data));
			}
			else
			{
			}
		}

		if (ni.getId() > -1)
		{
			aPC.addNotesItem(ni);
		}
	}

	/*
	 * ###############################################################
         * Miscellaneous methods
	 * ###############################################################
	 */

	/**
	 * Convenience Method
	 *
	 * <br>author: Thomas Behr 28-04-02
	 *
	 * @param line
	 */
	private boolean isComment(String line)
	{
		return line.trim().startsWith("#");
	}

	/**
	 * decode characters
	 * "\\" <- "\\\\"
	 * "\n" <- "\\n"
	 * "\r" <- "\\r"
	 * "\f" <- "\\f"
	 *
	 * <br>author: Thomas Behr 19-03-02
	 *
	 * @param s   the String to decode
	 * @return the decoded String
	 */
	private String decodeChars(String s)
	{
		// TODO
		return s;
	}
}
