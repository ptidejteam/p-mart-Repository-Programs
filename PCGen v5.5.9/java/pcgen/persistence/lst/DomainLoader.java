/*
 * DomainLoader.java
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
 * $Id: DomainLoader.java,v 1.1 2006/02/21 01:28:23 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class DomainLoader extends LstObjectFileLoader
{
	/** Creates a new instance of DomainLoader */
	public DomainLoader()
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
		Domain obj = (Domain) target;
		if (obj == null)
		{
			obj = new Domain();
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int col = 0;

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(obj, colString))
			{
				continue;
			}
			final int aLen = colString.length();
			if (col == 0)
			{
				if( (!colString.equals(obj.getName())) 
					&& (colString.indexOf(".MOD")<0))
				{
					finishObject(obj);
					obj = new Domain();
					obj.setName(colString);
					obj.setSourceCampaign(source.getCampaign());
					obj.setSourceFile(source.getFile());
				}
			}
			else if ((aLen > 5) && colString.startsWith("FEAT:"))
			{
				obj.setFeatList(colString.substring(5));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				obj.setQualifyString(colString.substring(8));
			}
			else if (col == 1)
			{
				obj.setDescription(pcgen.io.EntityEncoder.decode(colString));
			}
			else
			{
				Logging.errorPrint("Illegal obj info '" + colString + "' in " + source.getFile());
			}
			++col;
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
			if( Globals.getDomainNamed(target.getName())==null )
			{
				Globals.addDomain((Domain) target);
			}
		}
	}


	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectNamed(String baseName)
	{
		return Globals.getDomainNamed(baseName);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
		throws PersistenceLayerException
	{
		Globals.getDomainList().remove(objToForget);
	}

}
