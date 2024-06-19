/*
 * SkillLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * $Id: SkillLoader.java,v 1.1 2006/02/21 01:33:26 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class SkillLoader extends LstObjectFileLoader
{
	/** Creates a new instance of SkillLoader */
	public SkillLoader()
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
		Skill obj = (Skill) target;
		if (obj == null)
		{
			obj = new Skill();
			obj.setSourceCampaign(source.getCampaign());
			obj.setSourceFile(source.getFile());
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		// first column is the name; after that are LST tags
		obj.setName(colToken.nextToken());

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			if (PObjectLoader.parseTag(obj, colString))
			{
				continue;
			}
			else if (colString.startsWith("ACHECK:"))
			{
				obj.setACheck(colString.substring(7));
			}
			else if (colString.startsWith("CLASSES:"))
			{
				obj.addClassList(colString.substring(8));
			}
			//else if (colString.startsWith(Constants.s_TAG_TYPE))
			//{
			//	obj.setType(colString.substring(Constants.s_TAG_TYPE.length()));
			//}
			else if (colString.startsWith("EXCLUSIVE:"))
			{
				obj.setIsExclusive(colString.charAt(10) == 'Y');
			}
			else if (colString.startsWith("KEYSTAT:"))
			{
				obj.setKeyStat(colString.substring(8));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				obj.setQualifyString(colString.substring(8));
			}
			else if ("REQ".equals(colString))
			{
				obj.setRequired(true);
			}
			else if (colString.startsWith("ROOT:"))
			{
				obj.setRootName(colString.substring(5));
			}
			else if (colString.startsWith("SYNERGY:"))
			{
				Logging.errorPrint("SYNERGY is a deprecated tag in "
					+ source.toString()
					+ ". This functionality is now handled by the BONUS tag.");
			}
			else if (colString.startsWith("USEUNTRAINED:"))
			{
				obj.setUntrained(colString.substring(13));
			}
		}

		finishObject(obj);
		return null;
	}
	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	protected void finishObject(PObject target)
	{
		if( includeObject(target))
		{
			Skill skill = (Skill) target;
			final Skill aSkill = Globals.getSkillKeyed(skill.getKeyName());
			if (aSkill == null)
			{
				Globals.getSkillList().add(skill);
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectNamed(String baseName)
	{
		return Globals.getSkillNamed(baseName);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
		throws PersistenceLayerException
	{
		Globals.getSkillList().remove(objToForget);
	}

}
