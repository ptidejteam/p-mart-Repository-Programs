/*
 * FileAccess.java
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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JOptionPane;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.Utility;
import pcgen.util.Delta;
import pcgen.util.GuiFacade;

/**
 * <code>FileAccess</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class FileAccess
{
	private static String outputFilterName = "";
	private static Hashtable outputFilter = null;

	public static String getCurrentOutputFilter()
	{
		return outputFilterName;
	}

	public static void setCurrentOutputFilter(String filterName)
	{
		int idx = filterName.lastIndexOf('.');
		if (idx >= 0)
		{
			filterName = filterName.substring(idx + 1);
		}

		filterName = filterName.toLowerCase();
		if (filterName.equals(outputFilterName))
		{
			return;
		}

		outputFilter = null;

		filterName = Globals.getDefaultPath() + File.separator + "system" + File.separator + "outputFilters" + File.separator + filterName + ".lst";
		File aFile = new File(filterName);
		try
		{
			if (aFile.canRead() && aFile.isFile())
			{
				BufferedReader br = new BufferedReader(new FileReader(aFile));
				if (br != null)
				{
					outputFilterName = filterName;
					outputFilter = new Hashtable();
					for (; ;)
					{
						String aLine = br.readLine();
						if (aLine == null)
						{
							break;
						}
						ArrayList filterEntry = Utility.split(aLine, '\t');
						if (filterEntry.size() >= 2)
						{
							try
							{
								Integer key = Delta.decode((String)filterEntry.get(0));
								outputFilter.put(key, (String)filterEntry.get(1));
							}
							catch (NullPointerException e)
							{
								Globals.errorPrint("Exception in setCurrentOutputFilter", e);
							}
						}
					}
					br.close();
				}
			}
		}
		catch (IOException e)
		{
			//Should this be ignored?
		}
	}

	public static void encodeWrite(Writer output, String aString)
	{
		if ((outputFilter != null) && (outputFilter.size() != 0))
		{
			StringBuffer xlatedString = new StringBuffer(aString.length());
			for (int i = 0; i < aString.length(); i++)
			{
				final char c = aString.charAt(i);
				final String xlation = (String)outputFilter.get(new Integer(c));
				if (xlation != null)
				{
					xlatedString.append(xlation);
				}
				else
				{
					xlatedString.append(c);
				}
			}
			aString = xlatedString.toString();
		}
		write(output, aString);
	}

	public static void write(Writer output, String aString)
	{
		try
		{
			output.write(aString);
		}
		catch (IOException exception)
		{
			GuiFacade.showMessageDialog(null, exception.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void newLine(BufferedWriter output)
	{
		try
		{
			output.newLine();
		}
		catch (IOException exception)
		{
			GuiFacade.showMessageDialog(null, exception.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
	}

	public static String readLine(BufferedReader input)
	{
		try
		{
			return input.readLine();
		}
		catch (IOException exception)
		{
			GuiFacade.showMessageDialog(null, exception.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	public static String readWholeLine(BufferedReader input)
	{
		try
		{
			char[] c = new char[5000];
			for (int i = 0; i < 5000; i++)
			{
				final char d = (char)input.read();
				c[i] = d;
				if (d == '\r' || d == '\n')
					break;
			}
			return new String(c);
		}
		catch (IOException exception)
		{
			GuiFacade.showMessageDialog(null, exception.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	public static int read(BufferedReader input)
	{
		try
		{
			return input.read();
		}
		catch (IOException exception)
		{
			GuiFacade.showMessageDialog(null, exception.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		return 0;
	}
}
