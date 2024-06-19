/*
 * BaseKit.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
 * Copyright 2003 (C) Jonas Karlson <jujutsunerd@sf.net>
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
 * Created on September 28, 2002, 11:50 PM
 *
 * $Id: BaseKit.java,v 1.1 2006/02/21 01:08:01 vauchers Exp $
 */

package pcgen.core.kit;

import java.util.ArrayList;
import pcgen.core.Globals;

/**
 * Common code for the kits.
 * @author Jonas Karlson <jujutsunerd@sf.net>
 * @version $Revision: 1.1 $
 */

public class BaseKit
{
	int choiceCount = 1;
	private ArrayList prereqs = null;

	public int getChoiceCount()
	{
		return choiceCount;
	}

	public void setChoiceCount(String argChoiceCount)
	{
		try
		{
			choiceCount = Integer.parseInt(argChoiceCount);
		}
		catch (Exception exc)
		{
			Globals.errorPrint("Invalid choice count \"" + argChoiceCount + "\" in BaseKit.setChoiceCount");
		}
	}

	public void addPrereq(String argPrereq)
	{
		if (prereqs == null)
		{
			prereqs = new ArrayList();
		}
		prereqs.add(argPrereq);
	}

	public ArrayList getPrereqs()
	{
		return prereqs;
	}

}
