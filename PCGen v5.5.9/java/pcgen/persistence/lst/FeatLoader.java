/*
 * FeatLoader.java
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
 * Last Edited: $Date: 2006/02/21 01:28:23 $
 *
 */

package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class FeatLoader extends LstObjectFileLoader
{
	private boolean defaultFeatsLoaded = false;

	/** Creates a new instance of FeatLoader */
	public FeatLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public PObject parseLine(
		PObject target,
		String lstLine,
		CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		Feat feat = (Feat) target;
		if (feat == null)
		{
			feat = new Feat();
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int col = 0;
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int len = colString.length();
			if (col == 0)
			{
				feat.setName(colString);
				feat.setSourceCampaign(source.getCampaign());
				feat.setSourceFile(source.getFile());
			}
			//
			// moved this after name assignment so feats named
			// PRExxx don't parse the name as a prerequisite
			//
			else if (PObjectLoader.parseTag(feat, colString))
			{
				continue;
			}
			else if (colString.startsWith("ADD:"))
			{
				feat.setAddString(colString.substring(4));
			}
			else if ((len > 14) && colString.startsWith("ADDSPELLLEVEL:"))
			{
				try
				{
					feat.setAddSpellLevel(Delta.parseInt(colString.substring(14)));
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Bad addSpellLevel " + colString);
				}
			}
			else if (colString.startsWith("BENEFIT:"))
			{
				feat.setBenefit(colString.substring(8));
			}
			else if (colString.startsWith("COST:"))
			{
				feat.setCost(colString.substring(5));
			}
			else if (colString.startsWith("MULT:"))
			{
				feat.setMultiples(colString.substring(5));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				feat.setQualifyString(colString.substring(8));
			}
			else if (colString.startsWith("REP:"))
			{
				try
				{
					feat.setLevelsPerRepIncrease(Delta.decode(colString.substring(4)));
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Bad level per value " + colString);
				}
			}
			else if (colString.startsWith("STACK:"))
			{
				feat.setStacks(colString.substring(6));
			}
			else if (colString.startsWith("VISIBLE:"))
			{
				final String visType = colString.substring(8).toUpperCase();
				if (visType.startsWith("EXPORT"))
				{
					feat.setVisible(Feat.VISIBILITY_OUTPUT_ONLY);
				}
				else if (visType.startsWith("NO"))
				{
					feat.setVisible(Feat.VISIBILITY_HIDDEN);
				}
				else if (visType.startsWith("DISPLAY"))
				{
					feat.setVisible(Feat.VISIBILITY_DISPLAY_ONLY);
				}
				else
				{
					feat.setVisible(Feat.VISIBILITY_DEFAULT);
				}
			}
			else
			{
				Logging.errorPrint("Unknown tag '" + colString + "' in "
					+ source.getFile() );
			}
			++col;
		}
		finishObject(feat);
		return null;
	}
	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	protected void finishObject(PObject target)
	{
		if( includeObject(target))
		{
			final Feat aFeat = Globals.getFeatNamed(target.getKeyName());
			if (aFeat == null)
			{
				Globals.getFeatList().add(target);
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#loadLstFile(pcgen.persistence.lst.CampaignSourceEntry)
	 */
	protected void loadLstFile(CampaignSourceEntry sourceEntry)
		throws PersistenceLayerException
	{
		super.loadLstFile(sourceEntry);
		loadDefaultFeats(sourceEntry);
	}

	/**
	 * This method loads the default feats with the first feat source.
	 * @param firstSource CampaignSourceEntry first loaded by this loader
	 */
	private void loadDefaultFeats(CampaignSourceEntry firstSource) throws
		PersistenceLayerException
	{
		if( (!defaultFeatsLoaded) 
			&& (Globals.getFeatNamed(Constants.s_INTERNAL_WEAPON_PROF)==null) )
		{
			//
			// Add catch-all feat for weapon proficiencies that cannot be granted as part of a Feat
			// eg. Simple weapons should normally be applied to the Simple Weapon Proficiency feat, but
			// it does not allow multiples (either all or nothing). So monk class weapons will get dumped
			// into this bucket.
			//
			String aLine = Constants.s_INTERNAL_WEAPON_PROF + "\tOUTPUTNAME:Weapon Proficiency\tTYPE:General\tVISIBLE:NO\tMULT:YES\tSTACK:YES\tDESC:You attack with this specific weapon normally, non-proficiency incurs a -4 to hit penalty.\tSOURCE:PCGen Internal";
			parseLine(null, aLine, firstSource);
			defaultFeatsLoaded = true;			
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectNamed(String baseName)
	{
		return Globals.getFeatNamed(baseName);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
		throws PersistenceLayerException
	{
		Globals.getFeatList().remove(objToForget);
	}

}
