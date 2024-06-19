/*
 * PCTemplateLoader.java
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
 * $Id: PCTemplateLoader.java,v 1.1 2006/02/20 23:52:37 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class PCTemplateLoader
{

	/** Creates a new instance of PCTemplateLoader */
	private PCTemplateLoader()
	{
	}

	public static void parseLine(PCTemplate obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		String templName = "None";
		final int colMax = colToken.countTokens();
		int col = 0;
		if (colMax == 0)
			return;
		for (col = 0; col < colMax; col++)
		{
			final String colString = new String(colToken.nextToken());

			if (PObjectLoader.parseTag(obj, colString))
				continue;
			if (col == 0)
			{
				obj.setName(colString);
				templName = colString;
			}
			else if (colString.startsWith("HITDICESIZE:"))
			{
				int hitDiceSize = Integer.parseInt(colString.substring(12));
				if (hitDiceSize <= 0)
					hitDiceSize = 0;
				obj.setHitDiceSize(hitDiceSize);
			}

			// Dummy functions. I will add soon --- arcady
			else if (colString.startsWith("HEIGHT:"))
			{
				obj.setHeightString(colString.substring(7));
			} // replace racial height
			else if (colString.startsWith("WEIGHT:"))
			{
				obj.setWeightString(colString.substring(7));
			} // replace racial weight
			else if (colString.startsWith("VISION:"))
			{
				StringTokenizer visToken = new StringTokenizer(colString.substring(7), "|", false);

				obj.setVisionFlag(Integer.parseInt(visToken.nextToken()));
				obj.setVision(visToken.nextToken());
			} // replace, add to, or remove from vision type.
			else if (colString.startsWith("AGE:"))
			{
				obj.setAgeString(colString.substring(4));
			} // replaces racial age
			else if (colString.startsWith("LEVELSPERFEAT:"))
			{
				int newLevels = Integer.parseInt(colString.substring(14));
				if (newLevels >= 0)
					obj.setLevelsPerFeat(newLevels);
			} // how many levels per feat.
			else if (colString.startsWith("BONUSSKILLPOINTS:"))
			{
				obj.setBonusSkillsPerLevel(Integer.parseInt(colString.substring(17)));
			} // additional skill points per level
			else if (colString.startsWith("BONUSFEATS:"))
			{
				obj.setBonusInitialFeats(Integer.parseInt(colString.substring(11)));
			} // number of additional feats to spend
			else if (colString.startsWith(Constants.s_TAG_TYPE))
			{
				obj.setType(colString.substring(Constants.s_TAG_TYPE.length()));
			} // What is the 'Type' of this here critter.
			else if (colString.startsWith("POPUPALERT:"))
			{
				// NOTE: This shouldn't be in here... JOptionPane ties this class to Swing
				// JOptionPane.showMessageDialog(null, colString.substring(11), "PCGen", JOptionPane.ERROR_MESSAGE);
			} // pops the message to the screen.


			else if (colString.startsWith("COST:"))
			{
				obj.setCost(Double.parseDouble(colString.substring(5)));
			}

			else if (colString.startsWith("LEVEL:"))
				obj.addLevelString(colString.substring(6));
			else if (colString.startsWith("MOVECLONE:"))
			{
				obj.setMoveRates(colString.substring(10));
				obj.setMoveRatesFlag(2);
			}
			else if (colString.startsWith("MOVEA:"))
			{
				obj.setMoveRates(colString.substring(6));
				obj.setMoveRatesFlag(1);
			}
			else if (colString.startsWith("MOVE:"))
				obj.setMoveRates(colString.substring(5));
			else if (colString.startsWith("GOLD:"))
				obj.setGoldString(colString.substring(5));
			else if (colString.startsWith("HD:"))
				obj.addHitDiceString(colString.substring(3));
			else if (colString.startsWith("SIZE:"))
				obj.setTemplateSize(colString.substring(5));
			else if (colString.startsWith("DR:"))
			{
				String DR = obj.getDR();
				if (!DR.equals(""))
					DR += "," + colString.substring(3);
				else
					DR = colString.substring(3);
				obj.setDR(DR);
			}
			else if (colString.startsWith("SR:"))
				obj.setSR(Integer.parseInt(colString.substring(3)));
			else if (colString.startsWith("CR:"))
				obj.setCR(Integer.parseInt(colString.substring(3)));
			else if (colString.startsWith("LANGAUTO"))
				obj.setLanguageAutos(colString.substring(9));
			else if (colString.startsWith("CHOOSE:LANGAUTO:"))
				obj.setChooseLanguageAutos(colString.substring(16));
			else if (colString.startsWith("LANGBONUS"))
				obj.setLanguageBonus(colString.substring(10));
			else if (colString.startsWith("LEVELADJUSTMENT:"))
				obj.setLevelAdjustment(Integer.parseInt(colString.substring(16)));
			else if (colString.startsWith("SA:"))
				obj.addSA(colString.substring(3));
			else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
				obj.addPreReq(colString);
			else if (colString.startsWith("QUALIFY:"))
				obj.addToQualifyListing(colString.substring(8));
			else if (colString.startsWith("CSKILL:"))
				obj.setCSkillList(colString.substring(7));
			else if (colString.startsWith("CCSKILL:"))
				obj.setCCSkillList(colString.substring(8));
			else if (colString.startsWith("FAVOREDCLASS:"))
			{
				obj.setFavoredClass(colString.substring(13));
			}
			else if (colString.startsWith("FEAT:"))
				obj.addFeatString(colString.substring(5));
			else if (colString.startsWith("VISIBLE:"))
			{
				if (colString.substring(8).startsWith("Export"))
				{
					obj.setVisible(PCTemplate.VISIBILITY_OUTPUT_ONLY);
				}
				else if (colString.substring(8).startsWith("No"))
				{
					obj.setVisible(PCTemplate.VISIBILITY_HIDDEN);
				}
				else if (colString.substring(8).startsWith("Display"))
				{
					obj.setVisible(PCTemplate.VISIBILITY_DISPLAY_ONLY);
				}
				else
				{
					obj.setVisible(PCTemplate.VISIBILITY_DEFAULT);
				}
			}

			else if (colString.startsWith("NATURALARMOR:"))
				obj.setNatAC(Integer.parseInt(colString.substring(13)));
			else if (colString.startsWith("TEMPLATE:"))
				obj.addTemplate(colString.substring(9));
			else if (colString.startsWith("REMOVABLE:"))
			{
				if (colString.substring(10).startsWith("No"))
					obj.setRemovable(false);
			}
			else if (colString.startsWith("SUBRACE:"))
			{
				String subRace = colString.substring(8);
				if (subRace.equals("Yes")) subRace = templName;

				obj.setSubRace(subRace);
			}
			else if (colString.startsWith("REGION:"))
			{
				String region = colString.substring(7);
				if (region.equals("Yes")) region = templName;

				obj.setRegion(region);
			}
			else if (colString.startsWith("KIT:"))
			{
				obj.setKit(colString.substring(4));
			}
			else if (colString.startsWith("SUBREGION:"))
			{
				String subregion = colString.substring(10);
				if (subregion.equals("Yes")) subregion = templName;

				obj.setSubRegion(subregion);
			}
			else if (colString.startsWith("DEFINE"))
				obj.addVariableList("0|" + colString.substring(7));
			else if (colString.startsWith("WEAPONAUTO"))
			{
				obj.setWeaponProfAutos(colString.substring(11));
			}
			else if (colString.startsWith("SOURCE:"))
				obj.setSource(colString.substring(7));
			else
			{
				int iStat;
				for (iStat = 0; iStat < Globals.s_ATTRIBSHORT.length; iStat++)
				{

					final String statName = Globals.s_ATTRIBSHORT[iStat] + ":";
					if (colString.startsWith(statName))
					{
						if (colString.charAt(4) == '*')
						{
							obj.setStatMod(iStat, 0);
							obj.setNonAbility(iStat, true);
						}
						else
						{
							obj.setStatMod(iStat, Integer.parseInt(colString.substring(4)));
						}
						break;
					}
				}
				if (iStat >= Globals.s_ATTRIBSHORT.length)
				{
					throw new PersistenceLayerException("Illegal template info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
	}
}
