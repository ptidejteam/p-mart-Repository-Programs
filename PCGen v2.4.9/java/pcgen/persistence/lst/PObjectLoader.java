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
 * $Id: PObjectLoader.java,v 1.1 2006/02/20 23:52:37 vauchers Exp $
 */

package pcgen.persistence.lst;

import pcgen.core.PObject;
import pcgen.persistence.PersistenceManager;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class PObjectLoader
{

	/** Creates a new instance of PObjectLoader */
	private PObjectLoader()
	{
	}

	public static boolean parseTag(PObject obj, String aTag)
	{
		return parseTagLevel(obj, aTag, -9);
	}

	// return true if tag is parsed here
	public static boolean parseTagLevel(PObject obj, String aTag, int anInt)
	{
		obj.setNewItem(false);
		if (aTag.startsWith("SOURCE"))
		{
			final String src = PersistenceManager.getCurrentSource();
			if (src.length() > 0)
			{
				obj.setSource(PersistenceManager.saveSource(src), aTag);
			}
			else
			{
				obj.setSource(aTag);
			}
			return true;
		}
		if (aTag.startsWith("BONUS:"))
		{
			if (anInt > -9)
				obj.addBonusList(anInt + "|" + aTag.substring(6));
			else
				obj.addBonusList(aTag.substring(6));
			return true;
		}
		return false;
	}


}
