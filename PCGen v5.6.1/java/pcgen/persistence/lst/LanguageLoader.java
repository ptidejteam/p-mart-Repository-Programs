/*
 * LanguageLoader.java
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
 * $Id: LanguageLoader.java,v 1.1 2006/02/21 01:33:26 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class LanguageLoader extends LstObjectFileLoader
{

	/** Creates a new instance of LanguageLoader */
	public LanguageLoader()
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
		Language obj = (Language) target;
		if (obj == null)
		{
			obj = new Language();
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		int col = 0;

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (col == 0)
			{
				obj.setName(colString);
				obj.setSourceCampaign(source.getCampaign());
				obj.setSourceFile(source.getFile());
			}
			else
			{
				if (PObjectLoader.parseTag(obj, colString))
				{
					continue;
				}
				else
				{
					Logging.errorPrint("Unknown tag '" + colString + "' in "
						+ source.getFile() );
				}
			}
			++col;
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
			final Language lang = Globals.getLanguageNamed(target.getName());
			if (lang == null)
			{
				Globals.getLanguageList().add(target);
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectNamed(String baseName)
	{
		return Globals.getLanguageNamed(baseName);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
		throws PersistenceLayerException
	{
		Globals.getLanguageList().remove(objToForget);
	}

}
