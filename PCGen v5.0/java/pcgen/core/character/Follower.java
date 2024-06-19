/*
 * Follower.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on July 10, 2002, 11:26 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:08:02 $
 *
 */

package pcgen.core.character;

import pcgen.core.SettingsHandler;

/**
 * <code>Follower.java</code>
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 **/

public final class Follower implements Comparable
{

	/*
	 *
	 * the Structure of each Follower is as follows:
	 *
	 * FOLLOWER:name:type:race:HD:/path/to/some.pcg
	 *
	 * String name = name of the follower
	 * String type = Familiars, Mounts, Followers
	 * String race = race of follower
	 * int HD = Number of "used" HD
	 * String fileName = path and file name
	 *
	 */

	private String fileName = "";
	private String name = "";
	private String type = "";
	private String race = "";
	private int usedHD = 0;

	/**
	 * Constructor for Follower.
	 **/
	Follower()
	{
		super();
	}

	public Follower(String fName, String aName, String aType)
	{
		fileName = fName;
		name = aName;
		type = aType;
	}

	public int compareTo(final Object obj)
	{
		final Follower aF = (Follower) obj;
		return this.fileName.compareTo(aF.getFileName());
	}

	public String toString()
	{
		return name;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String x)
	{
		fileName = x;
	}

	// relative to the
	public String getRelativeFileName()
	{
		if (fileName.startsWith(SettingsHandler.getPcgPath().toString()))
		{
			return fileName.substring(SettingsHandler.getPcgPath().toString().length());
		}
		return fileName;
	}

	// String x may be an absolute or relative path
	public void setRelativeFileName(String x)
	{
		if (x.startsWith(SettingsHandler.getPcgPath().toString()))
		{
			fileName = x;
		}
		fileName = SettingsHandler.getPcgPath().toString() + x;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String x)
	{
		name = x;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String x)
	{
		type = x;
	}

	public String getRace()
	{
		return race;
	}

	public void setRace(String x)
	{
		race = x;
	}

	public int getUsedHD()
	{
		return usedHD;
	}

	public void setUsedHD(int x)
	{
		usedHD = x;
	}
}

