/*
 * Party.java
 * Copyright 2001 (C) Bryan McRoberts
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.util.StringTokenizer;

/**
 * <code>Party</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Party extends PObject
{
	public static boolean print(File aFile, BufferedWriter output)
	{
		Globals.getPcList().trimToSize();
		FileInputStream aStream = null;
		final PlayerCharacter holdPC = Globals.getCurrentPC();
		try
		{
			aStream = new FileInputStream(aFile);
			final int length = (int)aFile.length();
			byte[] inputLine = new byte[length];
			aStream.read(inputLine, 0, length);
			String lineString = new String(inputLine);
			StringTokenizer aTok = new StringTokenizer(lineString, "\r\n", false);
			FileAccess fa = new FileAccess();
			boolean flag = true;
			boolean inPipe = false;
			String tokString = "";
			int charNum = -1;
			String aLine = null;
			while (aTok.hasMoreTokens())
			{
				aLine = aTok.nextToken();
				if (!inPipe && aLine.lastIndexOf("|") == -1)
				{
					fa.write(output, aLine);
					fa.newLine(output);
				}
				else if ((inPipe && aLine.lastIndexOf("|") == -1) || (!inPipe && aLine.lastIndexOf("|") == 0))
				{
					tokString = tokString + aLine.substring(aLine.lastIndexOf("|") + 1);
					inPipe = true;
				}
				else
				{
					if (inPipe == false && aLine.charAt(0) == '|')
						inPipe = true;
					final StringTokenizer bTok = new StringTokenizer(aLine, "|", false);
					flag = bTok.countTokens() == 1;
					final int count = bTok.countTokens();
					String bString = null;
					while (bTok.hasMoreTokens())
					{
						bString = bTok.nextToken();
						if (!inPipe)
							fa.write(output, bString);
						else
						{
							if (bTok.hasMoreTokens() || flag)
							{
								int i = 0;
								String aString = tokString + bString;
								if (aString.startsWith("FOR."))
								{
									int x = 0;
									int j = 0;
									final PStringTokenizer pTok = new PStringTokenizer(aString.substring(4), ",", "\\\\", "\\\\");
									Integer cMin = new Integer(0);
									Integer cMax = new Integer(100);
									Integer cStep = new Integer(1);
									String cString = "";
									String cStartLineString = "";
									String cEndLineString = "";
									bString = null;
									boolean existsOnly = false;
									boolean noMoreItems = false;
									while (pTok.hasMoreTokens())
									{
										bString = pTok.nextToken();
										switch (j++)
										{
											case 0:
												cMin = pcgen.util.Delta.decode(bString);
												break;
											case 1:
												cMax = pcgen.util.Delta.decode(bString);
												break;
											case 2:
												cStep = pcgen.util.Delta.decode(bString);
												break;
											case 3:
												cString = bString;
												break;
											case 4:
												cStartLineString = bString;
												break;
											case 5:
												cEndLineString = bString;
												break;
											case 6:
												existsOnly = !bString.equals("0");
												break;
										}
									}
									if (cMax.intValue() >= Globals.getPcList().size() && existsOnly == true)
										cMax = new Integer(Globals.getPcList().size());
									for (int k = cMin.intValue(); k < cMax.intValue(); k++)
									{
										if (x++ == 0)
											fa.write(output, cStartLineString);
										String dString = cString;
										String eString = null;
										while (dString.length() > 0)
										{
											eString = "";
											for (int l = 0; l < dString.length() - 1; l++)
											{
												if (dString.charAt(l) == '\\' && dString.charAt(l + 1) == '\\')
												{
													eString = dString.substring(0, l);
													dString = dString.substring(l + 2);
													break;
												}
											}
											if (eString.equals(""))
											{
												eString = dString;
												dString = "";
											}
											if (eString.startsWith("%."))
											{
												charNum = k;
												if (charNum >= 0 && charNum < Globals.getPcList().size())
												{
													final PlayerCharacter aPC = (PlayerCharacter)Globals.getPcList().get(charNum);
													Globals.setCurrentPC(aPC);
													if (aPC != null)
														aPC.replaceToken(eString.substring(2), output);
													else
														noMoreItems = true;
												}
												else
													noMoreItems = true;
											}
											else
												fa.write(output, eString);
										}
										if (x == cStep.intValue() || (existsOnly == noMoreItems == true))
										{
											fa.write(output, cEndLineString);
											fa.newLine(output);
											x = 0;
											if (existsOnly == noMoreItems == true)
												break;
										}
									}
								}
								else
								{
									charNum = -1;
									for (i = 0; i < aString.length(); i++)
										if (aString.charAt(i) < '0' || aString.charAt(i) > '9')
											break;
									if (i > 0)
										charNum = pcgen.util.Delta.parseInt(aString.substring(0, i));
									if (charNum >= 0 && charNum < Globals.getPcList().size())
									{
										final PlayerCharacter aPC = (PlayerCharacter)Globals.getPcList().get(charNum);
										aPC.replaceToken(aString, output);
									}
								}
								tokString = "";
							}
							else
								tokString = tokString + bString;
						}
						if (bTok.hasMoreTokens() || flag)
							inPipe = !inPipe;
					}
					if (inPipe && aLine.charAt(aLine.length() - 1) == '|')
						inPipe = false;
				}
				if (!inPipe)
					fa.newLine(output);
			}
			aStream.close();
		}
		catch (Exception exc)
		{
		}
		Globals.setCurrentPC(holdPC);
		return true;
	}

}
