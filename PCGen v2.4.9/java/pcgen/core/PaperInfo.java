/*
 * PaperInfo.java
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
 * Created on February 25, 2002, 10:15 PM
 *
 * $Id: PaperInfo.java,v 1.1 2006/02/20 23:52:29 vauchers Exp $
 */

package pcgen.core;


public class PaperInfo
{
	private String[] paperInfo = new String[7];

	private boolean validIndex(int index)
	{
		switch (index)
		{
			case Constants.PAPERINFO_NAME:
			case Constants.PAPERINFO_HEIGHT:
			case Constants.PAPERINFO_WIDTH:
			case Constants.PAPERINFO_TOPMARGIN:
			case Constants.PAPERINFO_BOTTOMMARGIN:
			case Constants.PAPERINFO_LEFTMARGIN:
			case Constants.PAPERINFO_RIGHTMARGIN:
				break;
			default:
				return false;
		}
		return true;
	}

	public String getName()
	{
		return getPaperInfo(Constants.PAPERINFO_NAME);
	}

	public String getPaperInfo(int infoType)
	{
		if (!validIndex(infoType))
		{
			return null;
		}
		return paperInfo[infoType];
	}

	public void setPaperInfo(int infoType, String info) throws IndexOutOfBoundsException
	{
		if (!validIndex(infoType)) throw new IndexOutOfBoundsException("invalid index: " + infoType);

		paperInfo[infoType] = info;
	}

}
