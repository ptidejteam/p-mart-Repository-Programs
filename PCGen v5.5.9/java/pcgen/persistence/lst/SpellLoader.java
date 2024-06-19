/*
 * SpellLoader.java
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
import java.util.ArrayList;
import java.util.Iterator;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class SpellLoader extends LstObjectFileLoader
{
	/** Creates a new instance of SpellLoader */
	public SpellLoader()
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
		Spell obj = (Spell) target;
		if (obj == null)
		{
			obj = new Spell();
		}

		int i = 0;
		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreElements())
		{
			final String colString = colToken.nextToken().trim();
			final int aLen = colString.length();

			// The very first one is the Name
			if (i == 0)
			{
				if( (!colString.equals(obj.getName())) 
					&& (colString.indexOf(".MOD")<0))
				{
					finishObject(obj);
					obj = new Spell();
					obj.setName(colString);
					obj.setSourceCampaign(source.getCampaign());
					obj.setSourceFile(source.getFile());
				}
				i++;
				continue;
			}

			if (colString.startsWith("CASTTIME:"))
			{
				obj.setCastingTime(colString.substring(9));
			}
			else if (colString.startsWith("CLASSES:"))
			{
				obj.setLevelList("CLASS", colString.substring(8));
			}
			else if (colString.startsWith("COMPS:"))
			{
				obj.setComponentList(colString.substring(6));
			}
			else if (colString.startsWith("COST:"))
			{
				obj.setCost(colString.substring(5));
			}
			//else if (colString.startsWith("DESC:"))
			//{
			//	obj.setDescription(colString.substring(5));
			//}
			else if (colString.startsWith("DOMAINS:"))
			{
				obj.setLevelList("DOMAIN", colString.substring(8));
			}
			else if (colString.startsWith("EFFECTS:"))
			{
				Logging.errorPrint("EFFECTS: tag deprecated - use DESC: instead in " + source.getFile());
				obj.setDescription(colString.substring(8));
			}
			else if (colString.startsWith("EFFECTTYPE:"))
			{
				Logging.errorPrint("EFFECTTYPE: tag deprecated - use TARGETAREA: instead in " + source.getFile());
				obj.setTarget(colString.substring(11));
			}
			else if (colString.startsWith("CT:"))
			{
				obj.setCastingThreshold(Integer.parseInt(colString.substring(3)));
			}
			else if ((aLen > 11) && colString.startsWith("DESCRIPTOR"))
			{
				obj.addDescriptors(colString.substring(11));
			}
			else if (colString.startsWith("DURATION:"))
			{
				obj.setDuration(colString.substring(9));
			}
			else if (colString.startsWith("ITEM:"))
			{
				obj.setCreatableItem(colString.substring(5));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				obj.setQualifyString(colString.substring(8));
			}
			else if (colString.startsWith("RANGE:"))
			{
				obj.setRange(colString.substring(6));
			}
			else if (colString.startsWith("SAVEINFO:"))
			{
				obj.setSaveInfo(colString.substring(9));
			}
			else if (colString.startsWith("SCHOOL:"))
			{
				obj.setSchool(colString.substring(7));
			}
			else if (colString.startsWith("SPELLLEVEL:"))
			{
				Logging.errorPrint("Warning: tag 'SPELLLEVEL' has been deprecated. Use CLASSES or DOMAINS tag instead.");
				final StringTokenizer slTok = new StringTokenizer(colString.substring(11), "|");
				while (slTok.countTokens() >= 3)
				{
					final String typeString = slTok.nextToken();
					final String mainString = slTok.nextToken();
					obj.setLevelInfo(typeString + "|" + mainString, slTok.nextToken());
				}
			}
			else if (colString.startsWith("SPELLRES:"))
			{
				obj.setSpellResistance(colString.substring(9));
			}
			else if (colString.startsWith("STAT:"))
			{
				obj.setStat(colString.substring(5));
			}
			else if (colString.startsWith("SUBSCHOOL:"))
			{
				obj.setSubschool(colString.substring(10));
			}
			else if (colString.startsWith("TARGETAREA:"))
			{
				obj.setTarget(colString.substring(11));
			}
			else if (colString.startsWith("VARIANTS:"))
			{
				obj.setVariants(colString.substring(9));
			}
			else if (colString.startsWith("XPCOST:"))
			{
				obj.setXPCost(colString.substring(7));
			}
			else if (PObjectLoader.parseTag(obj, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal spell info '" + colString + "' in " + source.getFile() );
			}
		}
		
		return obj;
	}
	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	protected void finishObject(PObject target)
	{
		if( includeObject(target) )
		{
			Object obj = Globals.getSpellMap().get(target.getName());
			if (obj == null)
			{
				Globals.getSpellMap().put(target.getName(), target);
			}
			else
			{
				ArrayList aList;
				if (obj instanceof ArrayList)
					aList = (ArrayList)obj;
				else
				{
					aList = new ArrayList();
					aList.add(obj);
				}
				boolean match = false;
				for (Iterator i = aList.iterator(); i.hasNext();)
				{
					Spell aSpell = (Spell)i.next();
					Object a = aSpell.getLevelInfo();
					Object b = ((Spell)target).getLevelInfo();
					if ((a==null && b==null) || (a!=null && a.equals(b)))
					{
						match = true;
					}
				}
				if (!match)
				{
					aList.add(target);
					Globals.getSpellMap().put(target.getName(), aList);
				}
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectNamed(String baseName)
	{
		return Globals.getSpellNamed(baseName);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
		throws PersistenceLayerException
	{
		Globals.getSpellMap().remove(objToForget.getName());
	}

}
