/*
 * KitSchool.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 23, 2002, 9:21 PM
 *
 * $Id: KitSchool.java,v 1.1 2006/02/21 01:16:03 vauchers Exp $
 */

package pcgen.core.kit;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>KitSchool</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public final class KitSchool
{
	private String school = "";
	private List prohibited = null;
	private List prereqs = null;

	public KitSchool(String schoolName)
	{
		school = schoolName;
	}

	public void addProhibited(String argProhibited)
	{
		if (prohibited == null)
		{
			prohibited = new ArrayList();
		}
		prohibited.add(argProhibited);
	}

	public void addPreReq(String argPrereq)
	{
		if (prereqs == null)
		{
			prereqs = new ArrayList();
		}
		prereqs.add(argPrereq);
	}

	public String toString()
	{
		//TODO, if useful for debugging could dump values of array
		return "School= " + school;
	}
}
