/*
 * Names.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;


/**
 * <code>Names</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public class Names
{
	private int fullName;
	private int sylFirst;
	private int sylSecond;
	private int sylThrid;
	private static int switchNum;
	private String name;
	public static ArrayList ruleList = new ArrayList();
	public static ArrayList fullList = new ArrayList();
	public static ArrayList syl1List = new ArrayList();
	public static ArrayList syl2List = new ArrayList();
	public static ArrayList syl3List = new ArrayList();
	public static ArrayList titleList = new ArrayList();
	private Iterator i;


	/** Creates new name */
	public Names()
	{
	}

	public static ArrayList initNames(String fileName, int fileType, ArrayList aList)
	{
		ruleList.clear();
		titleList.clear();
		fullList.clear();
		syl1List.clear();
		syl2List.clear();
		syl3List.clear();
		byte[] inputLine = null;
		fileName = fileName.replace('\\', File.separatorChar);
		fileName = fileName.replace('/', File.separatorChar);
		fileName = fileName;
		File aFile = new File(fileName);
		//PObject anObj = null;
		String aString = null;
		String aLine = "";
		//currentFile = fileName;
		int lineNum = 0;
		try
		{
			FileInputStream aStream = new FileInputStream(aFile);
			int length = (int)aFile.length();
			inputLine = new byte[length];
			aStream.read(inputLine, 0, length);
			aString = new String(inputLine);
			String newlinedelim = new String("\r\n");
			StringTokenizer newlineStr = new StringTokenizer(aString, newlinedelim, false);
			while (newlineStr.hasMoreTokens())
			{
				aLine = newlineStr.nextToken();
				lineNum++;
				if (aLine.startsWith("#"))
				{
					continue;
				}
				if (aLine.startsWith("[RULES]"))
				{
					switchNum = 0;
					continue;
				}
				else if (aLine.startsWith("[FULL]"))
				{
					switchNum = 1;
					continue;
				}
				else if (aLine.startsWith("[SYL1]"))
				{
					switchNum = 2;
					continue;
				}
				else if (aLine.startsWith("[SYL2]"))
				{
					switchNum = 3;
					continue;
				}
				else if (aLine.startsWith("[SYL3]"))
				{
					switchNum = 4;
					continue;
				}
				else if (aLine.startsWith("[TITLE]"))
				{
					switchNum = 5;
					continue;
				}
				switch (switchNum)
				{
					case 0:
						ruleList.add(aLine);
						break;
					case 1:
						fullList.add(aLine);
						break;
					case 2:
						syl1List.add(aLine);
						break;
					case 3:
						syl2List.add(aLine);
						break;
					case 4:
						syl3List.add(aLine);
						break;
					case 5:
						titleList.add(aLine);
						break;
				}

			}
			aStream.close();
		}
		catch (Exception exception)
		{
			if (!fileName.equals("pcgen.ini"))
			{
				System.out.println("ERROR:" + fileName + " error " + aLine + " Exception type:" + exception.getClass().getName() + " Message:" + exception.getMessage());
			}
		}

		return null;
	}

	public static ArrayList getRuleList()
	{
		return ruleList;
	}

	public static ArrayList getFullList()
	{
		return fullList;
	}

	public static ArrayList getSyl1List()
	{
		return syl1List;
	}

	public static ArrayList getSyl2List()
	{
		return syl2List;
	}

	public static ArrayList getSyl3List()
	{
		return syl3List;
	}

	public static ArrayList getTitleList()
	{
		return titleList;
	}
}