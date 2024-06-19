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
 * @author Jayme Cox <jaymecox@netscape.net>
 * Created on July 10, 2002, 11:26 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:02:35 $
 */

package pcgen.core.character;


/**
 * <code>Follower.java</code>
 * @author Jayme Cox <jaymecox@excite.com>
 * @version $Revision: 1.1 $
*/

public class Follower implements Comparable
{

	/*
	 *
	 * the Structure of each Follower is as follows:
	 *
	 * FOLLOWER:/path/save.pcg:name:type
	 *
	 * String fileName = path and file name
	 * String name = name of the follower
	 * String type = Familliars, Mounts, Followers
	 *
	 */

	private String fileName = "";
	private String name = "";
	private String type = "";
	private int usedHD = 0;

	public Follower(String fName, String aName, String aType)
	{
		fileName = fName;
		name = aName;
		type = aType;
	}

	public int compareTo(Object obj)
	{
		Follower aF = (Follower)obj;
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

	public int getUsedHD()
	{
		return usedHD;
	}

	public void setUsedHD(int x)
	{
		usedHD = x;
	}
}

