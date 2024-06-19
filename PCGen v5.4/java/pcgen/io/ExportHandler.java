/*
 * ExportHandler.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on March 07, 2002, 8:30 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:19:07 $
 *
 */

package pcgen.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.StringTokenizer;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.NoteItem;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.SystemCollections;
import pcgen.core.WeaponProf;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellInfo;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.GuiFacade;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * <code>ExportHandler</code>.
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public final class ExportHandler
{
	private PlayerCharacter aPC;

	private File templateFile;

	private String csheetTag2 = "\\";
	private final Map loopVariables = new HashMap();

	private static boolean existsOnly = false;
	private static boolean noMoreItems = false;
	private static boolean skipMath = false;
	private boolean canWrite = true;
	private boolean checkBefore = false;
	private boolean inLabel = false;
	private static final boolean doMathMode = true;

	/**
	 * Constructor.
	 * @param templateFile the template to use while exporting.
	 *
	 * <br>author: Thomas Behr 12-04-02
	 **/
	public ExportHandler(File templateFile)
	{
		setTemplateFile(templateFile);
	}

	/**
	 * Sets the template to use for export.<br>
	 * Use this method to reset this handler, if it should be used
	 * to export to different/multiple templates
	 * @param templateFile the template to use while exporting.
	 *
	 * <br>author: Thomas Behr 12-04-02
	 *
	 * @param templateFile
	 **/
	private void setTemplateFile(File templateFile)
	{
		this.templateFile = templateFile;
	}

	/**
	 * Exports the contents of the given PlayerCharacter to a Writer
	 * according to the handler's template
	 *
	 * <br>author: Thomas Behr 12-04-02
	 *
	 * @param argPC	  the PlayerCharacter to write
	 * @param out	the Writer to be written to
	 **/
	public void write(PlayerCharacter argPC, BufferedWriter out)
	{
		this.aPC = argPC;

		// Get the EquipSet used for output and calculations
		// possibly include equipment from temporary bonuses
		argPC.setCalcEquipmentList(argPC.getUseTempMods());

		// Make sure spell lists are setup
		argPC.getSpellList();

		FileAccess.setCurrentOutputFilter(templateFile.getName());

		argPC.getAllSkillList(true); //force refresh of skills
		argPC.populateSkills(SettingsHandler.getIncludeSkills());
		for (Iterator e = argPC.getClassList().iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass) e.next();
			aClass.sortCharacterSpellList();
		}
		argPC.determinePrimaryOffWeapon();
		argPC.modFromArmorOnWeaponRolls();
		argPC.adjustMoveRates();
		argPC.calcActiveBonuses();

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile), "UTF-8"));
			String aString;
			final StringBuffer inputLine = new StringBuffer();
			while ((aString = br.readLine()) != null)
			{
				if (aString.length() == 0)
				{
					inputLine.append(' ').append(Constants.s_LINE_SEP);
				}
				else if (aString.indexOf("||") < 0)
				{
					inputLine.append(aString).append(Constants.s_LINE_SEP);
				}
				else
				{
					// Adjacent separators get merged by StringTokenizer, so we break them up here
					int dblBarPos = aString.indexOf("||");
					while (dblBarPos >= 0)
					{
						inputLine.append(aString.substring(0, dblBarPos)).append("| |");
						aString = aString.substring(dblBarPos + 2);
						dblBarPos = aString.indexOf("||");
					}
					if (aString.length() > 0)
					{
						inputLine.append(aString);
					}
					inputLine.append(Constants.s_LINE_SEP);
				}
			}

			aString = inputLine.toString();
			final StringTokenizer aTok = new StringTokenizer(aString, "\r\n", false);

			final FileAccess fa = new FileAccess();

			// parse the template for and pre-process all the
			// FOR loops and IIF statements
			//
			final FORNode root = parseFORs(aTok);
			loopVariables.put(null, "0");
			existsOnly = false;
			noMoreItems = false;
			//
			// now actualy process the (new) template file
			//
			loopFOR(root, 0, 0, 1, out, fa);
			loopVariables.clear();

		}
		catch (IOException exc)
		{
			Logging.errorPrint("Error in ExportHandler::write", exc);
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
                    //TODO: If this should be ignored, add a comment here describing why. XXX
				}
			}
			if (out != null)
			{
				try
				{
					out.flush();
				}
				catch (IOException e)
				{
                    //TODO: If this should be ignored, add a comment here describing why. XXX
				}
			}
		}

		csheetTag2 = "\\";

		// reset the EquipmentList without Temporary Bonus equipment
		argPC.setCalcEquipmentList(false);

		this.aPC = null;
	}

	/**
	 * Exports a PlayerCharacter-Party to a Writer
	 * according to the handler's template
	 *
	 * <br>author: Thomas Behr 13-11-02
	 *
	 * @param PCs	the Collection of PlayerCharacter instances which compromises the Party to write
	 * @param out	the Writer to be written to
	 **/
	public void write(Collection PCs, BufferedWriter out)
	{
		write((PlayerCharacter[]) PCs.toArray(new PlayerCharacter[PCs.size()]), out);
	}

	/**
	 * Exports a PlayerCharacter-Party to a Writer
	 * according to the handler's template
	 *
	 * <br>author: Thomas Behr 13-11-02
	 *
	 * @param PCs	the PlayerCharacter[] which compromises the Party to write
	 * @param out	the Writer to be written to
	 **/
	private void write(PlayerCharacter[] PCs, BufferedWriter out)
	{
		FileAccess.setCurrentOutputFilter(templateFile.getName());
		final PlayerCharacter holdPC = Globals.getCurrentPC();

		BufferedReader br = null;
		try
		{
			//br = new BufferedReader(new FileReader(templateFile));
			br = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile), "UTF-8"));
			boolean flag;
			boolean inPipe = false;
			StringBuffer tokString = new StringBuffer();
			int charNum;

			String aLine;
			while ((aLine = br.readLine()) != null)
			{
				if (!inPipe && aLine.lastIndexOf('|') < 0)
				{
					FileAccess.write(out, aLine);
					FileAccess.newLine(out);
				}
				else if ((inPipe && aLine.lastIndexOf('|') < 0) || (!inPipe && aLine.lastIndexOf('|') == 0))
				{
					tokString.append(aLine.substring(aLine.lastIndexOf('|') + 1));
					inPipe = true;
				}
				else
				{
					if (!inPipe && aLine.charAt(0) == '|')
					{
						inPipe = true;
					}
					final StringTokenizer bTok = new StringTokenizer(aLine, "|", false);
					flag = bTok.countTokens() == 1;
					String bString;
					while (bTok.hasMoreTokens())
					{
						bString = bTok.nextToken();
						if (!inPipe)
						{
							FileAccess.write(out, bString);
						}
						else
						{
							if (bTok.hasMoreTokens() || flag)
							{
								int i;

								String aString = tokString.toString() + bString;
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
									boolean _existsOnly = false;
									boolean _noMoreItems = false;
									while (pTok.hasMoreTokens())
									{
										bString = pTok.nextToken();
										switch (j++)
										{
											case 0:
												cMin = Delta.decode(bString);
												break;
											case 1:
												cMax = Delta.decode(bString);
												break;
											case 2:
												cStep = Delta.decode(bString);
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
												_existsOnly = !("0".equals(bString));
												break;
											default:
												Logging.errorPrint("In Party.print there is an unhandled case in a switch (the value is " + j + ".");
												break;
										}
									}
									if ((cMax.intValue() >= PCs.length) && _existsOnly)
									{
										cMax = new Integer(PCs.length);
									}
									for (int k = cMin.intValue(); k < cMax.intValue(); k++)
									{
										if (x++ == 0)
										{
											FileAccess.write(out, cStartLineString);
										}
										String dString = cString;
										String eString;
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
											if ("".equals(eString))
											{
												eString = dString;
												dString = "";
											}
											if (eString.startsWith("%."))
											{
												charNum = k;
												if ((charNum >= 0) && (charNum < PCs.length))
												{
													this.aPC = PCs[charNum];
													Globals.setCurrentPC(this.aPC);
													if (aPC != null)
													{
														replaceToken(eString.substring(2), out);
													}
													else
													{
														_noMoreItems = true;
													}

												}
												else
												{
													_noMoreItems = true;
												}
											}
											else
											{
												FileAccess.write(out, eString);
											}
										}
										if (x == cStep.intValue() || (_existsOnly == _noMoreItems))
										{
											FileAccess.write(out, cEndLineString);
											FileAccess.newLine(out);
											x = 0;
											if (_existsOnly == _noMoreItems)
											{
												break;
											}
										}
									}
								}
								else
								{
									charNum = -1;
									for (i = 0; i < aString.length(); i++)
									{
										if (aString.charAt(i) < '0' || aString.charAt(i) > '9')
										{
											break;
										}
									}
									if (i > 0)
									{
										charNum = Delta.parseInt(aString.substring(0, i));
									}
									if (charNum >= 0 && charNum < Globals.getPCList().size())
									{
										this.aPC = PCs[charNum];
										Globals.setCurrentPC(this.aPC);
										replaceToken(aString, out);
									}
								}
								tokString = new StringBuffer("");
							}
							else
							{
								tokString.append(bString);
							}
						}
						if (bTok.hasMoreTokens() || flag)
						{
							inPipe = !inPipe;
						}
					}
					if (inPipe && aLine.charAt(aLine.length() - 1) == '|')
					{
						inPipe = false;
					}
				}
				if (!inPipe)
				{
					FileAccess.newLine(out);
				}
			}
		}
		catch (IOException exc)
		{
			//TODO: If this should be ignored, add a comment here describing why. XXX
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException ignore)
				{
					// nothing to do about it
				}
			}
		}
		Globals.setCurrentPC(holdPC);
		this.aPC = null;
	}

	private FORNode parseFORs(StringTokenizer tokens)
	{
		final FORNode root = new FORNode(null, "0", "0", "1", true);
		String line;
		while (tokens.hasMoreTokens())
		{
			line = tokens.nextToken();
			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");
				if (newFor.countTokens() > 1)
				{
					newFor.nextToken();
					if (newFor.nextToken().startsWith("%"))
					{
						root.addChild(parseFORs(line, tokens));
					}
					else
					{
						root.addChild(line);
					}
				}
				else
				{
					root.addChild(line);
				}
			}
			else if (line.startsWith("|IIF(") && line.lastIndexOf(',') < 0)
			{
				String expr = line.substring(5, line.lastIndexOf(')'));
				root.addChild(parseIIFs(expr, tokens));
			}
			else
			{
				root.addChild(line);
			}
		}
		return root;
	}

	private FORNode parseFORs(String forLine, StringTokenizer tokens)
	{
		final StringTokenizer forVars = new StringTokenizer(forLine, ",");
		forVars.nextToken();
		final String var = forVars.nextToken();
		final String min = forVars.nextToken();
		final String max = forVars.nextToken();
		final String step = forVars.nextToken();
		final String eTest = forVars.nextToken();
		boolean exists = false;
		if ((eTest.length() > 0 && eTest.charAt(0) == '1') || (eTest.length() > 0 && eTest.charAt(0) == '2'))
		{
			exists = true;
		}
		final FORNode node = new FORNode(var, min, max, step, exists);
		String line;
		while (tokens.hasMoreTokens())
		{
			line = tokens.nextToken();
			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");
				newFor.nextToken();
				if (newFor.nextToken().startsWith("%"))
				{
					node.addChild(parseFORs(line, tokens));
				}
				else
				{
					node.addChild(line);
				}
			}
			else if (line.startsWith("|IIF(") && line.lastIndexOf(',') < 0)
			{
				String expr = line.substring(5, line.lastIndexOf(')'));
				node.addChild(parseIIFs(expr, tokens));
			}
			else if (line.startsWith("|ENDFOR|"))
			{
				return node;
			}
			else
			{
				node.addChild(line);
			}

		}
		return node;
	}

	private IIFNode parseIIFs(String expr, StringTokenizer tokens)
	{
		final IIFNode node = new IIFNode(expr);
		String line;
		boolean childrenType = true;
		while (tokens.hasMoreTokens())
		{
			line = tokens.nextToken();
			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");
				newFor.nextToken();
				if (newFor.nextToken().startsWith("%"))
				{
					if (childrenType)
					{
						node.addTrueChild(parseFORs(line, tokens));
					}
					else
					{
						node.addFalseChild(parseFORs(line, tokens));
					}
				}
				else
				{
					if (childrenType)
					{
						node.addTrueChild(line);
					}
					else
					{
						node.addFalseChild(line);
					}
				}
			}
			else if (line.startsWith("|IIF(") && line.lastIndexOf(',') < 0)
			{
				String newExpr = line.substring(5, line.lastIndexOf(')'));
				if (childrenType)
				{
					node.addTrueChild(parseIIFs(newExpr, tokens));
				}
				else
				{
					node.addFalseChild(parseIIFs(newExpr, tokens));
				}
			}
			else if (line.startsWith("|ELSE|"))
			{
				childrenType = false;
			}
			else if (line.startsWith("|ENDIF|"))
			{
				return node;
			}
			else
			{
				if (childrenType)
				{
					node.addTrueChild(line);
				}
				else
				{
					node.addFalseChild(line);
				}
			}
		}
		return node;
	}

	private int getVarValue(String var)
	{
		char chC;
		for (int idx = -1; ;)
		{
			idx = var.indexOf("COUNT[EQ", idx + 1);
			if (idx < 0)
			{
				break;
			}
			chC = var.charAt(idx + 8);
			if ((chC == '.') || ((chC >= '0') && (chC <= '9')))
			{
				final int i = var.indexOf(']', idx + 8);
				if (i >= 0)
				{
					String aString = var.substring(idx + 6, i);
					aString = replaceTokenEq(aString);
					var = var.substring(0, idx) + aString + var.substring(i + 1);
				}
			}
		}
		for (int idx = -1; ;)
		{
			idx = var.indexOf("STRLEN[", idx + 1);
			if (idx < 0)
			{
				break;
			}
			final int i = var.indexOf(']', idx + 7);
			if (i >= 0)
			{
				String aString = var.substring(idx + 7, i);
				StringWriter sWriter = new StringWriter();
				BufferedWriter aWriter = new BufferedWriter(sWriter);
				replaceToken(aString, aWriter);
				sWriter.flush();
				try
				{
					aWriter.flush();
				}
				catch (IOException e)
				{
					//TODO: If this should be ignored, add a comment here describing why. XXX
				}
				aString = sWriter.toString();
				var = var.substring(0, idx) + aString.length() + var.substring(i + 1);
			}
		}
		return aPC.getVariableValue(var, "").intValue();
	}

	private void loopFOR(FORNode node, int min, int max, int step, BufferedWriter output, FileAccess fa)
	{
		for (int x = min; x <= max; x += step)
		{
			loopVariables.put(node.var(), new Integer(x));
			for (int y = 0; y < node.children().size(); ++y)
			{
				if (node.children().get(y) instanceof FORNode)
				{
					FORNode nextFor = (FORNode) node.children().get(y);
					loopVariables.put(nextFor.var(), new Integer(0));
					existsOnly = nextFor.exists();
					String minString = nextFor.min();
					String maxString = nextFor.max();
					String stepString = nextFor.step();
					String fString;
					String rString;
					for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
					{
						Object anObject = ivar.next();
						if (anObject == null)
						{
							continue;
						}
						fString = anObject.toString();
						rString = loopVariables.get(fString).toString();
						minString = Utility.replaceAll(minString, fString, rString);
						maxString = Utility.replaceAll(maxString, fString, rString);
						stepString = Utility.replaceAll(stepString, fString, rString);
					}
					final int varMin = getVarValue(minString);
					final int varMax = getVarValue(maxString);
					final int varStep = getVarValue(stepString);
					loopFOR(nextFor, varMin, varMax, varStep, output, fa);
					existsOnly = node.exists();
					loopVariables.remove(nextFor.var());
				}
				else if (node.children().get(y) instanceof IIFNode)
				{
					evaluateIIF((IIFNode) node.children().get(y), output, fa);
				}
				else
				{
					String lineString = (String) node.children().get(y);
					for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
					{
						Object anObject = ivar.next();
						if (anObject == null)
						{
							continue;
						}
						String fString = anObject.toString();
						String rString = loopVariables.get(fString).toString();
						lineString = Utility.replaceAll(lineString, fString, rString);
					}

					noMoreItems = false;
					replaceLine(lineString, output);

					// output a newline at the end of each
					// loop (only if output is allowed)
					if (canWrite)
					{
						FileAccess.newLine(output);
					}

					// break out of loop if no more items
					if (existsOnly && noMoreItems)
					{
						x = max + 1;
					}
				}
			}
		}
	}

	private void evaluateIIF(IIFNode node, BufferedWriter output, FileAccess fa)
	{
		if (evaluateExpression(node.expr()))
		{
			evaluateIIFChildren(node.trueChildren(), output, fa);
		}
		else
		{
			evaluateIIFChildren(node.falseChildren(), output, fa);
		}
	}

	private void evaluateIIFChildren(final List children, BufferedWriter output, FileAccess fa)
	{
		for (int y = 0; y < children.size(); ++y)
		{
			if (children.get(y) instanceof FORNode)
			{
				FORNode nextFor = (FORNode) children.get(y);
				loopVariables.put(nextFor.var(), new Integer(0));
				existsOnly = nextFor.exists();
				String minString = nextFor.min();
				String maxString = nextFor.max();
				String stepString = nextFor.step();
				String fString;
				String rString;
				for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
				{
					Object anObject = ivar.next();
					if (anObject == null)
					{
						continue;
					}
					fString = anObject.toString();
					rString = loopVariables.get(fString).toString();
					minString = Utility.replaceAll(minString, fString, rString);
					maxString = Utility.replaceAll(maxString, fString, rString);
					stepString = Utility.replaceAll(stepString, fString, rString);
				}
				loopFOR(nextFor,
					getVarValue(minString),
					getVarValue(maxString),
					getVarValue(stepString), output, fa);
				existsOnly = nextFor.exists();
				loopVariables.remove(nextFor.var());
			}
			else if (children.get(y) instanceof IIFNode)
			{
				evaluateIIF((IIFNode) children.get(y), output, fa);
			}
			else
			{
				String lineString = (String) children.get(y);
				for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
				{
					Object anObject = ivar.next();
					if (anObject == null)
					{
						continue;
					}
					String fString = anObject.toString();
					String rString = loopVariables.get(fString).toString();
					lineString = Utility.replaceAll(lineString, fString, rString);
				}
				replaceLine(lineString, output);

				// output a newline if output is allowed
				if (canWrite)
				{
					FileAccess.newLine(output);
				}
			}
		}
	}

	private boolean evaluateExpression(String expr)
	{
		if (expr.indexOf(".AND.") > 0)
		{
			String part1 = expr.substring(0, expr.indexOf(".AND."));
			String part2 = expr.substring(expr.indexOf(".AND.") + 5);
			return (evaluateExpression(part1) && evaluateExpression(part2));
		}
		if (expr.indexOf(".OR.") > 0)
		{
			String part1 = expr.substring(0, expr.indexOf(".OR."));
			String part2 = expr.substring(expr.indexOf(".OR.") + 4);
			return (evaluateExpression(part1) || evaluateExpression(part2));
		}
		for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
		{
			Object anObject = ivar.next();
			if (anObject == null)
			{
				continue;
			}
			String fString = anObject.toString();
			String rString = loopVariables.get(fString).toString();
			expr = Utility.replaceAll(expr, fString, rString);
		}
		if (expr.startsWith("HASVAR:"))
		{
			expr = expr.substring(7).trim();
			return (aPC.getVariableValue(expr, "").intValue() > 0);
		}
		if (expr.startsWith("HASFEAT:"))
		{
			expr = expr.substring(8).trim();
			return (aPC.getFeatNamed(expr) != null);
		}
		if (expr.startsWith("HASSA:"))
		{
			expr = expr.substring(6).trim();
			return (aPC.hasSpecialAbility(expr));
		}
		if (expr.startsWith("HASEQUIP:"))
		{
			expr = expr.substring(9).trim();
			return (aPC.getEquipmentNamed(expr) != null);
		}
		if (expr.startsWith("SPELLCASTER:"))
		{
			// Could look like one of the following:
			// Arcane
			// Chaos
			// Divine
			// EleMage
			// Psionic
			// Wizard
			// Prepare

			// !Prepare
			// 0=Wizard    (%classNum=className)
			// 0=Divine    (%classNum=spell_type)
			// 0=Prepare   (%classNum=preparation_type)
			final String fString = expr.substring(12).trim();
			if (fString.indexOf('=') >= 0)
			{
				final StringTokenizer aTok = new StringTokenizer(fString, "=", false);
				final int i = Integer.parseInt(aTok.nextToken());
				final String cs = aTok.nextToken();
				final List cList = aPC.getClassList();
				if (i >= cList.size())
				{
					return false;
				}
				final PCClass aClass = (PCClass) cList.get(i);
				if (cs.equalsIgnoreCase(aClass.getSpellType()))
				{
					return true;
				}
				if (cs.equalsIgnoreCase(aClass.getName()))
				{
					return true;
				}
				if (cs.equalsIgnoreCase(aClass.getCastAs()))
				{
					return true;
				}
				if ("!Prepare".equalsIgnoreCase(cs) && aClass.getMemorizeSpells())
				{
					return true;
				}
				if ("Prepare".equalsIgnoreCase(cs) && (!aClass.getMemorizeSpells()))
				{
					return true;
				}
			}
			else
			{
				for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
				{
					PCClass aClass = (PCClass) e.next();
					if (fString.equalsIgnoreCase(aClass.getSpellType()))
					{
						return true;
					}
					if (fString.equalsIgnoreCase(aClass.getName()))
					{
						return true;
					}
					if (fString.equalsIgnoreCase(aClass.getCastAs()))
					{
						return true;
					}
					if ("!Prepare".equalsIgnoreCase(fString) && aClass.getMemorizeSpells())
					{
						return true;
					}
					if ("Prepare".equalsIgnoreCase(fString) && (!aClass.getMemorizeSpells()))
					{
						return true;
					}
				}
			}
		}
		if (expr.startsWith("EVEN:"))
		{
			int i = 0;
			try
			{
				i = Integer.parseInt(expr.substring(5).trim());
			}
			catch (NumberFormatException exc)
			{
				Logging.errorPrint("EVEN:" + i);
				return true;
			}
			return (i % 2 == 0);
		}
		if (expr.endsWith("UNTRAINED"))
		{
			final StringTokenizer aTok = new StringTokenizer(expr, ".");
			final String fString = aTok.nextToken();
			Skill aSkill = null;
			if (fString.length() > 5)
			{
				final int i = Integer.parseInt(fString.substring(5));
				final List pcSkills = aPC.getSkillListInOutputOrder();
				if (i <= pcSkills.size() - 1)
				{
					aSkill = (Skill) pcSkills.get(i);
				}
			}
			if (aSkill == null)
			{
				return false;
			}
			else if (aSkill.getUntrained().length() > 0 && aSkill.getUntrained().charAt(0) == 'Y')
			{
				return true;
			}
			return false;
		}
		// Before returning false, let's see if this is a valid token, like this:
		//
		// |IIF(WEAPON%weap.CATEGORY:Ranged)|
		// something 1
		// |ELSE|
		// something 2
		// |END IF|
		// It can theorically be used with any valid token, doing an equal compare
		// (integer or string equalities are valid)

		StringTokenizer aTok = new StringTokenizer(expr, ":");
		final String token;
		final String equals;
		if (aTok.countTokens() == 1)
		{
			token = expr;
			equals = "TRUE";
		}
		else if (aTok.countTokens() != 2)
		{
			Logging.errorPrint("evaluateExpression: Incorrect syntax (missing parameter)");
			return false;
		}
		else
		{
			token = aTok.nextToken();
			equals = aTok.nextToken().toUpperCase();
		}
		StringWriter sWriter = new StringWriter();
		BufferedWriter aWriter = new BufferedWriter(sWriter);
		replaceToken(token, aWriter);
		sWriter.flush();
		try
		{
			aWriter.flush();
		}
		catch (IOException e)
		{
			; // Don't have anything to do in this case
		}
		try
		{
			// integer values
			final int i = Integer.parseInt(sWriter.toString());
			return (i != Integer.parseInt(equals)) ? false : true;
		}
		catch (NumberFormatException e)
		{
			// String values
			return (sWriter.toString().toUpperCase().indexOf(equals) < 0) ? false : true;
		}
	}

	private void replaceLine(String aLine, BufferedWriter output)
	{
		boolean inPipe = false;
		boolean flag;
		StringBuffer tokString = new StringBuffer("");

		if (!inPipe && aLine.lastIndexOf('|') < 0)
		{
			if (aLine.length() > 0)
			{
				outputNonToken(aLine, output);
			}
		}
		else if ((inPipe && aLine.lastIndexOf('|') < 0) || (!inPipe && aLine.lastIndexOf('|') == 0))
		{
			tokString.append(aLine.substring(aLine.lastIndexOf('|') + 1));
			inPipe = true;
		}
		else
		{
			if (!inPipe && aLine.charAt(0) == '|')
			{
				inPipe = true;
			}
			final StringTokenizer bTok = new StringTokenizer(aLine, "|", false);
			flag = bTok.countTokens() == 1;
			while (bTok.hasMoreTokens())
			{
				String bString = bTok.nextToken();
				if (!inPipe)
				{
					outputNonToken(bString, output);
				}
				else
				{
					if (bTok.hasMoreTokens() || flag || (inPipe && !bTok.hasMoreTokens() && aLine.charAt(aLine.length() - 1) == '|'))
					{
						replaceToken(tokString.toString() + bString, output);
						tokString = new StringBuffer("");
					}
					else
					{
						tokString.append(bString);
					}
				}
				if (bTok.hasMoreTokens() || flag)
				{
					inPipe = !inPipe;
				}
			}

			if (inPipe && aLine.charAt(aLine.length() - 1) == '|')
			{
				inPipe = false;
			}
		}
	}

	private void outputNonToken(String aString, BufferedWriter output)
	{

		//If something shouldn't be output, return.
		if (!canWrite)
		{
			return;
		}

		if (aString.length() > 0)
		{
			FileAccess.write(output, aString);
		}
	}

	/** Discovers if a string is a attack routine. It must begin with a sign (+-),
	 * it must have only digits, and must have a delimiter
	 * May be optimized via Stringtokenizer, instead of a for
	 */
	private static boolean isAttackRoutine(String aString)
	{
		final String signs = "+-";
		final String delimiter = "/";
		int typeBefore = 0; // 0=delimiter, 1=sign, 2=digit
		for (int i = 0; i < aString.length(); ++i)
		{
			if (signs.indexOf(aString.charAt(i)) > -1)
			{
				if (typeBefore != 0)
				{
					return false;
				}
				typeBefore = 1;
			}
			else if (delimiter.indexOf(aString.charAt(i)) > -1)
			{
				if (typeBefore != 2)
				{
					return false;
				}
				typeBefore = 0;
			}
			else if (aString.charAt(i) >= '0' && aString.charAt(i) <= '9')
			{
				if (typeBefore != 1 && typeBefore != 2)
				{
					return false;
				}
				typeBefore = 2;
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	/** Math Mode - Most of the code logic was copied from PlayerCharacter.getVariableValue
	 * included a treatment for math with attack routines e.g. +6/+1 - 2 = +4/-1
	 */
	private String mathMode(String aString)
	{
		Float total = new Float(0.0);
		while (aString.lastIndexOf('(') >= 0)
		{
			int x = Utility.innerMostStringStart(aString);
			int y = Utility.innerMostStringEnd(aString);
			if (y < x)
			{
// This was breaking some homebrew sheets. [Felipe - 13-may-03]
//				Logging.errorPrint("Missing closing parenthesis: " + aString);
//				return total.toString();
				break;
			}
			String bString = aString.substring(x + 1, y);
			// This will treat Knowledge (xx) kind of token
			if (x > 0 && aString.charAt(x - 1) == ' ' && (aString.charAt(y + 1) == '.' || (y == aString.length() - 1)))
			{
				aString = aString.substring(0, x) + "[" + bString + "]" + aString.substring(y + 1);
			}
			else
			{
				aString = aString.substring(0, x) + mathMode(bString) + aString.substring(y + 1);
			}
		}
		aString = Utility.replaceAll(aString, "[", "(");
		aString = Utility.replaceAll(aString, "]", ")");
		final String delimiter = "+-/*";
		String valString = "";
		final int ADDITION_MODE = 0;
		final int SUBTRACTION_MODE = 1;
		final int MULTIPLICATION_MODE = 2;
		final int DIVISION_MODE = 3;
		int mode = ADDITION_MODE;
		int nextMode = 0;
		final int REGULAR_MODE = 0;
		final int INTVAL_MODE = 1;
		final int SIGN_MODE = 2;
		final int NO_ZERO_MODE = 3;
		int endMode = REGULAR_MODE;
		boolean attackRoutine = false;
		String attackData = "";

		for (int i = 0; i < aString.length(); ++i)
		{
			valString += aString.substring(i, i + 1);
			if (i == aString.length() - 1 ||
				(delimiter.lastIndexOf(aString.charAt(i)) > -1 && i > 0 && aString.charAt(i - 1) != '.'))
			{
				if (delimiter.lastIndexOf(aString.charAt(i)) > -1)
				{
					valString = valString.substring(0, valString.length() - 1);
				}
				if (i < aString.length())
				{
					if (valString.endsWith(".TRUNC"))
					{
						if (attackRoutine)
						{
							Logging.errorPrint("Math Mode Error: Using .TRUNC in Attack Mode.");
						}
						else
						{
							valString = String.valueOf(Float.valueOf(mathMode(valString.substring(0, valString.length() - 6))).intValue());
						}
					}
					if (valString.endsWith(".INTVAL"))
					{
						if (attackRoutine)
						{
							Logging.errorPrint("Math Mode Error: Using .INTVAL in Attack Mode.");
						}
						else
						{
							valString = mathMode(valString.substring(0, valString.length() - 7));
						}
						endMode = INTVAL_MODE;
					}
					if (valString.endsWith(".SIGN"))
					{
						valString = mathMode(valString.substring(0, valString.length() - 5));
						endMode = SIGN_MODE;
					}
					if (valString.endsWith(".NOZERO"))
					{
						valString = mathMode(valString.substring(0, valString.length() - 7));
						endMode = NO_ZERO_MODE;
					}
					if (aString.length() > 0 && aString.charAt(i) == '+')
					{
						nextMode = ADDITION_MODE;
					}
					else if (aString.length() > 0 && aString.charAt(i) == '-')
					{
						nextMode = SUBTRACTION_MODE;
					}
					else if (aString.length() > 0 && aString.charAt(i) == '*')
					{
						nextMode = MULTIPLICATION_MODE;
					}
					else if (aString.length() > 0 && aString.charAt(i) == '/')
					{
						nextMode = DIVISION_MODE;
					}
					StringWriter sWriter = new StringWriter();
					BufferedWriter aWriter = new BufferedWriter(sWriter);
					replaceTokenSkipMath(aPC, valString, aWriter);
					sWriter.flush();
					try
					{
						aWriter.flush();
					}
					catch (IOException e)
					{
						//TODO: Really ignore this? If so, explain why in a comment here. XXX
					}
					final String bString = sWriter.toString();
					try
					{
						// Float values
						valString = String.valueOf(Float.parseFloat(bString));
					}
					catch (NumberFormatException e)
					{
						// String values
						valString = bString;
					}
					if ((!attackRoutine) && isAttackRoutine(valString))
					{
						attackRoutine = true;
						attackData = valString;
						valString = "";
					}
				}
				try
				{
					if (valString.length() > 0)
					{
						if (attackRoutine)
						{
							StringTokenizer bTok = new StringTokenizer(attackData, "/");
							String newAttackData = "";
							if (bTok.countTokens() > 0)
							{
								while (bTok.hasMoreTokens())
								{
									final String bString = bTok.nextToken();
									switch (mode)
									{
										case ADDITION_MODE:
											newAttackData += "/+" + Integer.toString(new Float(Float.parseFloat(bString) + Float.parseFloat(valString)).intValue());
											break;
										case SUBTRACTION_MODE:
											newAttackData += "/+" + Integer.toString(new Float(Float.parseFloat(bString) - Float.parseFloat(valString)).intValue());
											break;
										case MULTIPLICATION_MODE:
											newAttackData += "/+" + Integer.toString(new Float(Float.parseFloat(bString) * Float.parseFloat(valString)).intValue());
											break;
										case DIVISION_MODE:
											newAttackData += "/+" + Integer.toString(new Float(Float.parseFloat(bString) / Float.parseFloat(valString)).intValue());
											break;
										default:
											Logging.errorPrint("In mathMode the mode " + mode + " is unsupported.");
											break;
									}
								}
								attackData = Utility.replaceAll(newAttackData.substring(1), "+-", "-");
							}
						}
						else
						{
							switch (mode)
							{
								case ADDITION_MODE:
									total = new Float(total.doubleValue() + Double.parseDouble(valString));
									break;
								case SUBTRACTION_MODE:
									total = new Float(total.doubleValue() - Double.parseDouble(valString));
									break;
								case MULTIPLICATION_MODE:
									total = new Float(total.doubleValue() * Double.parseDouble(valString));
									break;
								case DIVISION_MODE:
									total = new Float(total.doubleValue() / Double.parseDouble(valString));
									break;
								default:
									Logging.errorPrint("In mathMode the mode " + mode + " is unsupported.");
									break;
							}
						}
					}
				}
				catch (NumberFormatException exc)
				{
//					GuiFacade.showMessageDialog(null, "Math error determining value for " + aString + " " + attackData + "(" + valString + ")", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
					StringWriter sWriter = new StringWriter();
					BufferedWriter aWriter = new BufferedWriter(sWriter);
					replaceTokenSkipMath(aPC, aString, aWriter);
					sWriter.flush();
					try
					{
						aWriter.flush();
					}
					catch (IOException e)
					{
						//TODO: Really ignore this? If so, explain why in a comment here. XXX
					}
					return sWriter.toString();
				}
				mode = nextMode;
				nextMode = ADDITION_MODE;
				valString = "";
			}
		}
		if (attackRoutine)
		{
			return attackData;
		}
		else
		{
			if (endMode == INTVAL_MODE)
			{
				return Integer.toString(total.intValue());
			}
			if (endMode == SIGN_MODE)
			{
				final Integer valInt = new Integer(total.intValue());
				return Delta.toString(valInt);
			}
			if (endMode == NO_ZERO_MODE)
			{
				final Integer valInt = new Integer(total.intValue());
				if (valInt.intValue() == 0)
				{
					return "";
				}
				return Delta.toString(valInt);
			}
			return total.toString();
		}
	}

	public void replaceTokenSkipMath(PlayerCharacter argPC, String aString, BufferedWriter output)
	{
		aPC = argPC;
		final boolean oldSkipMath = skipMath;
		skipMath = true;
		replaceToken(aString, output);
		skipMath = oldSkipMath;
	}

	private int replaceToken(String aString, BufferedWriter output)
	{
		try
		{
			int len = 1;
			if (!canWrite &&
				(aString.length() > 0) && (aString.charAt(0) != '%'))
			{
				return 0;
			}
			if ("%".equals(aString))
			{
				inLabel = false;
				canWrite = true;
				return 0;
			}

			FileAccess.maxLength(-1);

			//
			// Start the |%blah| token section
			//
			if (aString.length() > 0 && aString.charAt(0) == '%' && aString.length() > 1 &&
				aString.lastIndexOf('<') < 0 && aString.lastIndexOf('>') < 0)
			{
				boolean found = false;
				canWrite = true;

				// check to see how we are merging equipment
				int merge = Constants.MERGE_ALL;
				if (aString.indexOf("MERGENONE") > 0)
				{
					merge = Constants.MERGE_NONE;
				}
				if (aString.indexOf("MERGELOC") > 0)
				{
					merge = Constants.MERGE_LOCATION;
				}

				if (aString.substring(1).startsWith("GAMEMODE:"))
				{
					if (aString.substring(10).endsWith(SettingsHandler.getGame().getName()))
					{
						canWrite = false;
					}
					return 0;
				}
				if ("REGION".equals(aString.substring(1)))
				{
					if (aPC.getRegion().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					return 0;
				}
				if ("NOTES".equals(aString.substring(1)))
				{
					if (aPC.getNotesList().size() <= 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("SKILLPOINTS".equals(aString.substring(1)))
				{
					if (getTokenSkillPoints("SKILLPOINTS.UNUSED") == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if (aString.substring(1).startsWith("TEMPLATE"))
				{
					// New token syntax |%TEMPLATE.x| instead of |%TEMPLATEx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					final List tList = aPC.getTemplateList();
					String fString = aTok.nextToken();
					final int index;
					if (aTok.hasMoreTokens())
					{
						index = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						// When removing old syntax, remove the else and leave the if
						if ("TEMPLATE".equals(fString))
						{
							if (tList.isEmpty())
							{
								canWrite = false;
							}
							return 0;
						}
						else
						{
							Logging.errorPrint("Old syntax %TEMPLATEx will be replaced for %TEMPLATE.x");

							index = Integer.parseInt(aString.substring(9));
						}
					}

					if (index >= tList.size())
					{
						canWrite = false;
						return 0;
					}
					if (((PCTemplate) tList.get(index)).isVisible() != 1 && ((PCTemplate) tList.get(index)).isVisible() != 2)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("FOLLOWER".equals(aString.substring(1)))
				{
					if (aPC.getFollowerList().isEmpty())
					{
						canWrite = false;
					}
					return 0;
				}
				if ("FOLLOWEROF".equals(aString.substring(1)))
				{
					if (aPC.getMasterPC() == null)
					{
						canWrite = false;
					}
					return 0;
				}
				if (aString.substring(1).startsWith("FOLLOWERTYPE."))
				{
					List aList = new ArrayList();
					for (Iterator iter = aPC.getFollowerList().iterator(); iter.hasNext();)
					{
						Follower aFollower = (Follower) iter.next();
						// only allow followers that
						// are currently loaded
						// Otherwise the stats a zero
						for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
						{
							PlayerCharacter lPC = (PlayerCharacter) p.next();
							if (lPC.getFileName().equals(aFollower.getFileName()))
							{
								aList.add(aFollower);
							}
						}
					}
					StringTokenizer aTok = new StringTokenizer(aString, ".");
					aTok.nextToken();	// FOLLOWERTYPE
					String typeString = aTok.nextToken();
					for (int i = aList.size() - 1; i >= 0; --i)
					{
						final Follower fol = (Follower) aList.get(i);
						if (!fol.getType().equalsIgnoreCase(typeString))
						{
							aList.remove(i);
						}
					}
					if (aList.isEmpty())
					{
						canWrite = false;
					}
					return 0;
				}
				if ("PROHIBITEDLIST".equals(aString.substring(1)))
				{
					for (Iterator iter = aPC.getClassList().iterator(); iter.hasNext();)
					{
						PCClass aClass = (PCClass) iter.next();
						if (aClass.getLevel() > 0)
						{
							if (!aClass.getProhibitedString().equals(Constants.s_NONE))
							{
								return 0;
							}
						}
					}
					canWrite = false;
					return 0;
				}
				if ("CATCHPHRASE".equals(aString.substring(1)))
				{
					if (aPC.getCatchPhrase().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getCatchPhrase()).trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("LOCATION".equals(aString.substring(1)))
				{
					if (aPC.getLocation().equals(Constants.s_NONE))

					{
						canWrite = false;
					}
					else if ((aPC.getLocation()).trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("RESIDENCE".equals(aString.substring(1)))
				{
					if (aPC.getResidence().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getResidence()).trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("PHOBIAS".equals(aString.substring(1)))
				{
					if (aPC.getPhobias().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getPhobias()).trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("INTERESTS".equals(aString.substring(1)))
				{
					if (aPC.getInterests().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getInterests()).trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("SPEECHTENDENCY".equals(aString.substring(1)))
				{
					if (aPC.getSpeechTendency().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getSpeechTendency()).trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("PERSONALITY1".equals(aString.substring(1)))
				{
					if (aPC.getTrait1().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getTrait1()).trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("PERSONALITY2".equals(aString.substring(1)))
				{
					if (aPC.getTrait2().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if ((aPC.getTrait2()).trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("MISC.FUNDS".equals(aString.substring(1)))
				{
					if (aPC.getMiscList().get(0).equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (((String) aPC.getMiscList().get(0)).trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("COMPANIONS".equals(aString.substring(1)) ||
					"MISC.COMPANIONS".equals(aString.substring(1)))
				{
					if (aPC.getMiscList().get(1).equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (((String) aPC.getMiscList().get(1)).trim().length() == 0)
					{
						canWrite = false;
					}

					return 0;
				}
				if ("MISC.MAGIC".equals(aString.substring(1)))
				{
					if (aPC.getMiscList().get(2).equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (((String) aPC.getMiscList().get(2)).trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("DESC".equals(aString.substring(1)))
				{
					if (aPC.getDescription().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (aPC.getDescription().trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("BIO".equals(aString.substring(1)))
				{
					if (aPC.getBio().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					else if (aPC.getBio().trim().length() == 0)
					{
						canWrite = false;
					}
					return 0;
				}
				if ("SUBREGION".equals(aString.substring(1)))
				{
					if (aPC.getSubRegion().equals(Constants.s_NONE))
					{
						canWrite = false;
					}
					return 0;
				}
				if (aString.substring(1).startsWith("TEMPBONUS."))
				{
					StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					int index = -1;
					aTok.nextToken(); // discard first one
					if (aTok.hasMoreTokens())
					{
						index = Integer.parseInt(aTok.nextToken());
					}
					if (index > aPC.getNamedTempBonusList().size())
					{
						canWrite = false;
						return 0;
					}
					if (aPC.getUseTempMods())
					{
						canWrite = true;
						return 1;
					}
				}
				if (aString.substring(1).startsWith("ARMOR.ITEM"))
				{
					// New token syntax |%ARMOR.ITEM.x| instead of |%ARMOR.ITEMx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					aTok.nextToken(); // ARMOR
					String fString = aTok.nextToken();
					final int count;
					final List aArrayList = new ArrayList();
					for (Iterator e = aPC.getEquipmentListInOutputOrder().iterator(); e.hasNext();)
					{
						Equipment eq = (Equipment) e.next();

						if (eq.getBonusListString("AC") &&
							(!eq.isArmor() && !eq.isShield()))
						{
							aArrayList.add(eq);
						}
					}

					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						count = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging.errorPrint("Old syntax %ARMOR.ITEMx will be replaced for %ARMOR.ITEM.x");

						count = Integer.parseInt(fString.substring(fString.length() - 1));
					}
					if (count > aArrayList.size())
					{
						canWrite = false;
					}
					return 0;
				}
				if (aString.substring(1).startsWith("ARMOR.SHIELD"))
				{
					// New token syntax |%ARMOR.SHIELD.x| instead of |%ARMOR.SHIELDx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					aTok.nextToken(); // ARMOR
					String fString = aTok.nextToken();
					final int count;
					final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("SHIELD", 3);
					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						count = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging.errorPrint("Old syntax %ARMOR.SHIELDx will be replaced for %ARMOR.SHIELD.x");

						count = Integer.parseInt(fString.substring(fString.length() - 1));
					}
					if (count > aArrayList.size())
					{
						canWrite = false;
					}
					return 0;
				}
				if (aString.substring(1).startsWith("ARMOR"))
				{
					// New token syntax |%ARMOR.x| instead of |%ARMORx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					String fString = aTok.nextToken();
					final int count;
					final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("ARMOR", 3);
					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						count = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging.errorPrint("Old syntax %ARMORx will be replaced for %ARMOR.x");

						count = Integer.parseInt(fString.substring(fString.length() - 1));
					}
					if (count > aArrayList.size())
					{
						canWrite = false;
					}
					return 0;
				}
				if ("WEAPONPROF".equals(aString.substring(1)))
				{
					if (!SettingsHandler.getWeaponProfPrintout())
					{
						canWrite = false;
					}
					return 0;
				}
				if (aString.substring(1).startsWith("WEAPON"))
				{
					// New token syntax |%WEAPON.x| instead of |%WEAPONx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					String fString = aTok.nextToken();
					int count = 0;
					final List aArrayList = aPC.getExpandedWeapons(merge);
					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						count = Integer.parseInt(aTok.nextToken());

					}
					else
					{
						Logging.errorPrint("Old syntax %WEAPONx will be replaced for %WEAPON.x");

						count = Integer.parseInt(fString.substring(fString.length() - 1));
					}
					if (count >= aArrayList.size())
					{
						canWrite = false;
					}
					return 0;
				}
				if (aString.substring(1).startsWith("DOMAIN"))
				{
					// New token syntax |%DOMAIN.x| instead of |%DOMAINx|
					final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ".");
					String fString = aTok.nextToken();
					final int index;
					// When removing old syntax, remove the else and leave the if
					if (aTok.hasMoreTokens())
					{
						index = Integer.parseInt(aTok.nextToken());
					}
					else
					{
						Logging.errorPrint("Old syntax %DOMAINx will be replaced for %DOMAIN.x");

						index = Integer.parseInt(fString.substring(6));
					}

					canWrite = (index <= aPC.getCharacterDomainList().size());
					return 0;
				}
				if (aString.substring(1).startsWith("SPELLLISTBOOK"))
				{
					if (SettingsHandler.getPrintSpellsWithPC())
					{
						// New token syntax |%SPELLLISTBOOK.x| instead of |%SPELLLISTBOOKx|
						// To remove old syntax, keep the if and remove the else
						if (aString.charAt(14) == '.')
						{
							aString = aString.substring(15);
						}
						else
						{
							aString = aString.substring(14);
						}
						return replaceTokenSpellListBook(aString);
					}
					else
					{
						canWrite = false;
						return 0;
					}
				}
				if (aString.substring(1).startsWith("VAR."))
				{
					replaceTokenVar(aString);
					return 0;
				}
				if (aString.substring(1).startsWith("COUNT["))
				{
					if (getVarValue(aString.substring(1)) > 0)
					{
						canWrite = true;
						return 1;
					}
					canWrite = false;
					return 0;
				}

				// finaly, check for classes
				final StringTokenizer aTok = new StringTokenizer(aString.substring(1), ",", false);
				while (aTok.hasMoreTokens())
				{
					String cString = aTok.nextToken();
					StringTokenizer bTok = new StringTokenizer(cString, "=", false);
					String bString = bTok.nextToken();
					int i = 0;
					if (bTok.hasMoreTokens())
					{
						i = Integer.parseInt(bTok.nextToken());
					}

					PCClass aClass = aPC.getClassNamed(bString);
					PCClass bClass = Globals.getClassNamed(bString);
					found = bClass != null;
					if (bClass != null && aClass != null)
					{
						canWrite = (aClass.getLevel() >= i);
					}
					else if (bClass != null && aClass == null)
					{
						canWrite = false;
					}
					else if (bString.startsWith("SPELLLISTCLASS"))
					{
						// New token syntax |%SPELLLISTCLASS.x| instead of |%SPELLLISTCLASSx|
						// To remove old syntax, keep the if and remove the else
						if (bString.charAt(14) == '.')
						{
							bString = bString.substring(15);
						}
						else
						{
							bString = bString.substring(14);
						}
						found = true;
						PObject aObject = aPC.getSpellClassAtIndex(Integer.parseInt(bString));
						canWrite = (aObject != null);
					}
				}
				if (found)
				{
					inLabel = true;
					return 0;
				}
				else
				{
					canWrite = false;
					inLabel = true;
					return 0;
				}
			}
			// done with |%blah| tokens


			// now check for max length tokens
			// eg: |SUB10.ARMOR.AC|
			if ((aString.indexOf("SUB") == 0) && (aString.indexOf(".") > 3))
			{
				int iEnd = aString.indexOf(".");
				int maxLength = -1;
				try
				{
					maxLength = Integer.parseInt(aString.substring(3, iEnd));
				}
				catch (NumberFormatException ex)
				{
					// Hmm, no number?
					Logging.errorPrint("Number format error: " + aString);
					maxLength = -1;
				}
				if (maxLength > 0)
				{
					aString = aString.substring(iEnd + 1);
					FileAccess.maxLength(maxLength);
				}
			}

			//
			// now check for the rest of the tokens
			//
			if (aString.startsWith("FOR.") || aString.startsWith("DFOR."))
			{
				FileAccess.maxLength(-1);
				existsOnly = false;
				noMoreItems = false;
				checkBefore = false;
//				skipMath = true;
				replaceTokenForDfor(aString, output);
//				skipMath = false;
				existsOnly = false;
				noMoreItems = false;
				return 0;
			}
			else if (aString.startsWith("OIF("))
			{
				replaceTokenIIF(aString, output);
			}
			else if (((aString.indexOf('(') >= 0) ||
				(aString.indexOf('+') >= 0) ||
				(aString.indexOf('-') >= 0) ||
				(aString.indexOf(".INTVAL") >= 0) ||
				(aString.indexOf(".SIGN") >= 0) ||
				(aString.indexOf(".NOZERO") >= 0) ||
				(aString.indexOf(".TRUNC") >= 0) ||
				(aString.indexOf('*') >= 0) ||
				(aString.indexOf('/') >= 0)) &&
				(!skipMath) && (doMathMode))
			{
				FileAccess.maxLength(-1);
				FileAccess.write(output, mathMode(aString));
				return 0;
			}
			else if (aString.startsWith("EQSET.START") || aString.startsWith("EQSET.END"))
			{
				FileAccess.maxLength(-1);
				return 0;
			}
			else if (aString.startsWith("TEMPBONUS."))
			{
				replaceTokenTempBonus(aString, output);
			}
			else if (aString.startsWith("CSHEETTAG2."))
			{
				csheetTag2 = aString.substring(11, 12);
				FileAccess.maxLength(-1);
				return 0;
			}
			else if (aString.startsWith("STAT"))
			{
				len = replaceTokenStat(aString, output);
			}
			else if (aString.startsWith("BONUS."))
			{
				FileAccess.write(output, calcBonusValue(aPC, aString));
			}
			else if (aString.startsWith("SKILLPOINTS"))
			{
				FileAccess.write(output, String.valueOf(getTokenSkillPoints(aString)));
			}
			else if ("EXPORT.DATE".equals(aString))
			{
				FileAccess.write(output, DateFormat.getDateInstance().format(new Date()));
			}
			else if ("EXPORT.TIME".equals(aString))
			{
				FileAccess.write(output, DateFormat.getTimeInstance().format(new Date()));
			}
			else if ("EXPORT.VERSION".equals(aString))
			{
				try
				{
					final ResourceBundle d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
					FileAccess.write(output, d_properties.getString("VersionNumber"));
				}
				catch (MissingResourceException mre)
				{
					//Should this be ignored?
				}
			}
			else if ("EQSET.NAME".equals(aString))
			{
				FileAccess.write(output, aPC.getCurrentEquipSetName());
			}
			else if (aString.startsWith("PAPERINFO."))
			{
				replaceTokenPaperInfo(aString, output);
			}
			else if ("GAMEMODE".equals(aString))
			{
				FileAccess.write(output, SettingsHandler.getGame().getName());

			}
			else if (aString.startsWith("BIO"))
			{
				final List stringList = getLineForBio();
				aString = replaceTokenCommaSeparatedList(aString, stringList, output);
			}
			else if (aString.startsWith("DESC"))
			{
				final List stringList = getLineForDesc();
				aString = replaceTokenCommaSeparatedList(aString, stringList, output);
			}
			else if ("NAME".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getName());
			}
			else if (aString.startsWith("NOTE."))
			{
				replaceTokenNote(aString, output);
			}
			else if (aString.startsWith("HPROLL"))
			{
				replaceTokenHPRoll(aString, output);
			}
			// When removing old syntax, remove this option
			else if (aString.startsWith("NOTE"))
			{
				_replaceTokenNote(aString, output);
			}
			else if ("ECL".equals(aString))
			{
				// simplified 30 July 2003 -- sage_sam
				FileAccess.write(output, Integer.toString(aPC.getECL()));
			}
			else if ("RACE".equals(aString))
			{
				String tempRaceName = aPC.getRace().getDisplayName();
				if (tempRaceName.equals(Constants.s_NONE))
				{
					//tempRaceName = aPC.getRace().getName();
					tempRaceName = aPC.getRace().getOutputName();
				}

				StringBuffer extraRaceInfo = new StringBuffer(40);

				if (!aPC.getSubRace().equals(Constants.s_NONE))
				{
					extraRaceInfo.append(aPC.getSubRace());
				}
				if (SettingsHandler.hideMonsterClasses())
				{
					final String monsterClass = aPC.getRace().getMonsterClass(false);
					if (monsterClass != null)
					{
						final PCClass aClass = aPC.getClassNamed(monsterClass);
						if (aClass != null)
						{
							int minHD = aPC.getRace().hitDice() + aPC.getRace().getMonsterClassLevels();
							int monsterHD = aPC.getRace().hitDice() + aClass.getLevel();
							if (monsterHD != minHD)
							{
								if (extraRaceInfo.length() != 0)
								{
									extraRaceInfo.append(' ');
								}
								extraRaceInfo.append(monsterHD).append("HD");
							}
						}
					}
				}
				FileAccess.encodeWrite(output, tempRaceName);
				if (extraRaceInfo.length() != 0)
				{
					FileAccess.encodeWrite(output, " (" + extraRaceInfo.toString() + ')');
				}
			}
			else if (aString.startsWith("AC."))
			{
				FileAccess.encodeWrite(output, Integer.toString(aPC.calcACOfType(aString.substring(3))));
			}
			else if ("AGE".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.getAge()));
			}
			else if ("HEIGHT".equals(aString))
			{
				//if (Globals.useMetric())
				//{
				//	FileAccess.encodeWrite(output, Integer.toString(aPC.getHeight()) + " " + Globals.getHeightDisplay());
				//}
				//else
				//{
				//	FileAccess.encodeWrite(output, Integer.toString(aPC.getHeight() / 12) + "' " + Integer.toString(aPC.getHeight() % 12) + "\"");
				//}
				if ("in".equals(Globals.getHeightDisplay()))
				{
					FileAccess.encodeWrite(output, Integer.toString(aPC.getHeight() / 12) + "' " + Integer.toString(aPC.getHeight() % 12) + "\"");
				}
				else
				{
					FileAccess.encodeWrite(output, Integer.toString(aPC.getHeight()) + " " + Globals.getHeightDisplay());
				}
			}
			else if ("HEIGHT.FOOTPART".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.getHeight() / 12));
			}
			else if ("HEIGHT.INCHPART".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.getHeight() % 12));
			}
			else if ("WEIGHT".equals(aString))
			{
				FileAccess.encodeWrite(output, Integer.toString(aPC.getWeight()) + " " + Globals.getWeightDisplay());
			}
			else if ("WEIGHT.NOUNIT".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.getWeight()));
			}
			else if ("COLOR.EYE".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getEyeColor());
			}
			else if ("COLOR.HAIR".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getHairColor());
			}
			else if ("COLOR.SKIN".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getSkinColor());
			}
			else if ("LENGTH.HAIR".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getHairStyle());
			}
			else if ("PERSONALITY1".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getTrait1());
			}
			else if ("PERSONALITY2".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getTrait2());
			}
			else if ("SPEECHTENDENCY".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getSpeechTendency());
			}
			else if ("CATCHPHRASE".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getCatchPhrase());
			}
			else if ("RESIDENCE".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getResidence());
			}
			else if ("LOCATION".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getLocation());
			}
			else if ("BIRTHPLACE".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getBirthplace());
			}
			else if ("REGION".equals(aString) || "SUBREGION".equals(aString))
			{
				final String tempRegName = aPC.getRegion();

				if (aPC.getSubRegion().equals(Constants.s_NONE))
				{
					FileAccess.encodeWrite(output, tempRegName);
				}
				else
				{
					FileAccess.encodeWrite(output, tempRegName + " (" + aPC.getSubRegion() + ")");
				}
			}
			//			else if (aString.equals("SUBREGION"))
			//				FileAccess.encodeWrite(output, aPC.getSubRegion());
			else if ("PORTRAIT".equals(aString))
			{
				FileAccess.write(output, aPC.getPortraitPath());
			}
			else if ("PHOBIAS".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getPhobias());
			}
			else if ("INTERESTS".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getInterests());
			}
			else if ("TOTALLEVELS".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.getTotalLevels()));
			}
			else if ("CR".equals(aString))
			{
				int CR = aPC.calcCR();
				if (CR < 0)
				{
					FileAccess.write(output, "1/");
					CR = -CR;
				}
				FileAccess.write(output, Integer.toString(CR));
			}
			else if ("FACE".equals(aString))
			{
				FileAccess.write(output, aPC.getRace().getFace());
			}
			else if ("REACH".equals(aString))
			{
				FileAccess.write(output, Integer.toString(getPCReach(aPC)));
			}
			else if ("SR".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.getSR()));
			}
			else if ("DR".equals(aString))
			{
				FileAccess.write(output, aPC.calcDR());
			}
			else if ("ALIGNMENT".equals(aString))
			{
				replaceTokenAlignment(output);
			}
			else if ("ALIGNMENT.SHORT".equals(aString))
			{
				replaceTokenAlignmentShort(output);
			}
			else if (aString.startsWith("BONUSLIST."))
			{
				replaceTokenBonusList(aString, output);
			}
			else if ("GENDER".equals(aString) || "GENDER.SHORT".equals(aString))
			{
				FileAccess.write(output, aPC.getGender());
			}
			else if ("GENDER.LONG".equals(aString))
			{
				if ("M".equals(aPC.getGender()))
				{
					FileAccess.write(output, "Male");
				}
				else if ("F".equals(aPC.getGender()))
				{
					FileAccess.write(output, "Female");
				}
				else
				{
					FileAccess.write(output, aPC.getGender());
				}
			}
			else if ("HANDED".equals(aString))
			{
				FileAccess.write(output, aPC.getHanded());
			}
			else if ("PROHIBITEDLIST".equals(aString))
			{
				int i;
				final int k = aString.lastIndexOf(',');
				if (k >= 0)
				{
					aString = aString.substring(k + 1);
				}
				else
				{
					aString = ", ";
				}

				final List stringList = new ArrayList();
				for (Iterator iter = aPC.getClassList().iterator(); iter.hasNext();)
				{
					PCClass aClass = (PCClass) iter.next();
					if (aClass.getLevel() > 0)
					{
						if (!aClass.getProhibitedString().equals(Constants.s_NONE))
						{
							stringList.add(aClass.getProhibitedString());
						}
					}
				}
				for (i = 0; i < stringList.size(); ++i)
				{
					FileAccess.write(output, (String) stringList.get(i));
					if (i < stringList.size() - 1)
					{
						FileAccess.write(output, aString);
					}

				}
			}
			else if ("PIPE".equals(aString))
			{
				FileAccess.write(output, "|");
			}
			else if (aString.startsWith("TEMPLATE"))
			{
				replaceTokenTemplate(aString, output);
			}
			else if ("FOLLOWEROF".equals(aString))
			{
				final PlayerCharacter masterPC = aPC.getMasterPC();
				String outputString = "";
				if (masterPC != null)
				{
					for (Iterator e = masterPC.getFollowerList().iterator(); e.hasNext();)
					{
						Follower aFollower = (Follower) e.next();
						if (aFollower.getFileName().equals(aPC.getFileName()))
						{
							outputString = aFollower.getType() + " of " + masterPC.getName();
						}
					}
				}
				FileAccess.write(output, outputString);
			}
			else if (aString.startsWith("FOLLOWER"))
			{
				replaceTokenFollowers(aString, output);
			}
			else if (aString.startsWith("CLASS"))
			{
				replaceTokenClass(aString, output);
			}
			else if ("HITDICE".equals(aString))
			{
				replaceTokenHitDice(output);
			}
			else if ("EXP.CURRENT".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.getXP()));
			}
			else if ("EXP.NEXT".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.minXPForNextECL()));
				FileAccess.write(output, "");
			}

			else if ("EXP.FACTOR".equals(aString))
			{
				/*				Float aFloat = new Float(aPC.multiclassXPMultiplier().floatValue() * 100.0);
				 Integer aInt = new Integer(aFloat.intValue());
				 FileAccess.encodeWrite(output, aInt.toString() + "%");
				 */
				final StringBuffer xpFactor = new StringBuffer(5);
				xpFactor.append((int) (aPC.multiclassXPMultiplier() * 100.0));
				xpFactor.append('%');
				FileAccess.encodeWrite(output, xpFactor.toString());

			}
			else if ("EXP.PENALTY".equals(aString))
			{
				/*				Float aFloat = new Float(aPC.multiclassXPMultiplier().floatValue() * 100.0);
				 Integer aInt = new Integer(100 - aFloat.intValue());
				 FileAccess.encodeWrite(output, aInt.toString() + "%");
				 */
				final StringBuffer xpFactor = new StringBuffer(5);
				xpFactor.append(100 - (int) (aPC.multiclassXPMultiplier() * 100.0));
				xpFactor.append('%');
				FileAccess.encodeWrite(output, xpFactor.toString());
			}
			else if ("FAVOREDLIST".equals(aString))
			{
				final int favoredSize = aPC.getFavoredClasses().size();

				if (favoredSize <= 0 && existsOnly)
				{
					noMoreItems = true;
					FileAccess.maxLength(-1);
					return 0;
				}
				boolean firstPass = true;

				for (Iterator e = aPC.getFavoredClasses().iterator(); e.hasNext();)
				{
					// separator only on second and beyond iterations
					if (!firstPass)
					{
						FileAccess.write(output, ", ");
					}
					final String favoredString = (String) e.next();
					FileAccess.write(output, favoredString);
					firstPass = false;
				}
			}
			else if (aString.startsWith("CHECK"))
			{
				replaceTokenCheck(aString, output);
			}
			else if ("SIZEMOD".equals(aString))
			{
				final int sizeMod = (int) aPC.getSizeAdjustmentBonusTo("COMBAT", "AC");
				writeToken(sizeMod, output);
			}
			else if ("MAXDEX".equals(aString))
			{
				final int mod = aPC.modToFromEquipment(aString);
				if (mod != Constants.MAX_MAXDEX)
				{
					FileAccess.write(output, Delta.toString(mod));
				}
			}
			else if ("ACCHECK".equals(aString))
			{
				final int mod = aPC.modToFromEquipment(aString);
				FileAccess.write(output, Delta.toString(mod));
			}
			else if ("SPELLFAILURE".equals(aString))
			{
				final int mod = aPC.modToFromEquipment(aString);
				FileAccess.write(output, Integer.toString(mod));
			}
			else if ("INITIATIVEMOD".equals(aString))
			{
				final int initiativeMod = aPC.initiativeMod();
				writeToken(initiativeMod, output);
			}
			else if ("INITIATIVEMISC".equals(aString) || "INITIATIVEBONUS".equals(aString))
			{
				final int initiativeMisc = aPC.initiativeMod() - aPC.getVariableValue("INITCOMP", "").intValue();
				writeToken(initiativeMisc, output);
			}
			else if (aString.startsWith("MOVEMENT"))
			{
				replaceTokenMovement(aString, output);
			}
			else if (aString.startsWith("BASEMOVEMENT"))
			{
				replaceTokenBaseMovement(aString, output);
			}
			else if (aString.startsWith("MOVE")) /* format : MOVE.% prints out movename/move pair. MOVE.%.NAME and MOVE.%.RATE produce the appropriate parts.*/
			{
				replaceTokenMove(aString, output);
			}
			else if ("SIZE".equals(aString))
			{
				FileAccess.write(output, aPC.getSize());
			}
			else if ("SIZELONG".equals(aString))
			{
				FileAccess.write(output, SystemCollections.getSizeAdjustmentAtIndex(aPC.sizeInt()).getName());
			}
			else if ("TYPE".equals(aString))
			{
				FileAccess.write(output, aPC.getCritterType());
			}
			else if (aString.startsWith("FEATALLLIST"))
			{
//				printFeatList(aString.substring(11), aPC.aggregateFeatList(), output);
				printFeatList(aString.substring(11), aPC.aggregateVisibleFeatList(), output);
			}
			else if (aString.startsWith("FEATAUTOLIST"))
			{
				printFeatList(aString.substring(12), aPC.featAutoList(), output);
			}
			else if (aString.startsWith("FEATLIST"))
			{
				printFeatList(aString.substring(8), aPC.getFeatList(), output);
			}
			else if (aString.startsWith("VFEATLIST"))
			{
				printFeatList(aString.substring(9), aPC.vFeatList(), output);
			}
			else if (aString.startsWith("FEATALL"))
			{
				printFeat(7, aString, aPC.aggregateFeatList(), output);
//				printFeat(7, aString, aPC.aggregateVisibleFeatList(), output);
			}
			else if (aString.startsWith("FEATAUTO"))
			{
				printFeat(8, aString, aPC.featAutoList(), output);
			}
			//Added 11/27, prints total number of feats left unless zero.
			else if (aString.startsWith("FEATPOINTS"))
			{
				printFeatPoints(aPC.getFeats(), output);
			}
			else if (aString.startsWith("FEAT"))
			{
				printFeat(4, aString, aPC.getFeatList(), output);
			}
			else if (aString.startsWith("VFEAT"))
			{
				printFeat(5, aString, aPC.vFeatList(), output);
			}
			else if ("MAXSKILLLEVEL".equals(aString))
			{
				BigDecimal maxSkillLevel = PlayerCharacter.maxClassSkillForLevel(aPC.getTotalLevels());
				FileAccess.encodeWrite(output, maxSkillLevel.toString());
			}
			else if ("MAXCCSKILLLEVEL".equals(aString))
			{
				BigDecimal maxCCSkillLevel = PlayerCharacter.maxCrossClassSkillForLevel(aPC.getTotalLevels());
				FileAccess.encodeWrite(output, maxCCSkillLevel.toString());
			}
			else if ("SKILLLISTMODS".equals(aString))
			{
				replaceTokenSkillListMods(output);
			}
			else if (aString.startsWith("SKILL"))
			{
				len = replaceTokenSkill(aString, output);
			}
			else if (aString.startsWith("DEITY"))
			{
				if (aPC.getDeity() != null)
				{
//					FileAccess.encodeWrite(output, aPC.getDeity().getOutputName());
					replaceTokenDeity(aString, output);
				}
				else
				{
					len = 0;
				}
			}
			else if (aString.startsWith("DOMAIN"))
			{
				final StringTokenizer aTok = new StringTokenizer(aString, ".");
				final int domainIndex;
				final boolean flag;
				Domain aDomain = null;
				String bString = aTok.nextToken();
				// New Token Syntax - DOMAIN.x and DOMAIN.x.POWER
				// When removing old syntax, remove the else
				if ("DOMAIN".equals(bString))
				{
					// DOMAINS are a 0-based index, so DOMAIN1 token should get the 0th element
					domainIndex = Math.max(0, Integer.parseInt(aTok.nextToken()) - 1);
					if (aTok.hasMoreTokens())
					{
						bString = aTok.nextToken();
						flag = "POWER".equals(bString);
					}
					else
					{
						flag = false;
					}
				}
				else
				{
					flag = aString.endsWith("POWER");
					bString = aString.substring(6);
					if (flag)
					{
						bString = bString.substring(0, bString.indexOf('.'));
					}
					// DOMAINS are a 0-based index, so DOMAIN1 token should get the 0th element
					domainIndex = Math.max(0, Integer.parseInt(bString) - 1);
				}
				if (aPC.getCharacterDomainList().size() > domainIndex)
				{
					aDomain = ((CharacterDomain) aPC.getCharacterDomainList().get(domainIndex)).getDomain();
				}
				if (aDomain == null)
				{
					if (existsOnly)
					{
						noMoreItems = true;
					}
					FileAccess.maxLength(-1);
					return 0;
				}
				else if (flag)
				{
					FileAccess.write(output, aDomain.piDescString());
				}
				else
				{
					//FileAccess.write(output, aDomain.getName());
					FileAccess.encodeWrite(output, aDomain.getOutputName());
				}
			}
			else if (aString.startsWith("SPECIALLIST"))
			{
				len = replaceTokenSpecialList(aString, output);
			}
			else if (aString.startsWith("SPECIALABILITY"))
			{
				len = replaceTokenSpecialAbility(aString, output);
			}
			else if ("ATTACK.GRAPPLE".equals(aString))
			{
				FileAccess.write(output, aPC.getAttackString(Constants.ATTACKSTRING_MELEE));
			}
			else if ("ATTACK.GRAPPLE.BASE".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.baseAttackBonus()));
			}
			else if ("ATTACK.GRAPPLE.MISC".equals(aString))
			{
				final int miscBonus =
					(int) aPC.getTotalBonusTo("COMBAT", "TOHIT") +
					(int) aPC.getTotalBonusTo("TOHIT", "TOHIT") +
					(int) aPC.getTotalBonusTo("TOHIT", "TYPE.GRAPPLE") -
					(int) aPC.getStatBonusTo("TOHIT", "TYPE.GRAPPLE") -
					(int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
				FileAccess.write(output, Delta.toString(miscBonus));
			}
			else if ("ATTACK.GRAPPLE.SIZE".equals(aString))
			{
				FileAccess.write(output, Delta.toString((int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT")));
			}
			else if ("ATTACK.GRAPPLE.STAT".equals(aString))
			{
				FileAccess.write(output, Delta.toString((int) aPC.getStatBonusTo("TOHIT", "TYPE.GRAPPLE")));
			}
			else if ("ATTACK.GRAPPLE.TOTAL".equals(aString))
			{
				FileAccess.write(output, aPC.getAttackString(Constants.ATTACKSTRING_MELEE,
					(int) aPC.getTotalBonusTo("TOHIT", "TOHIT") +
					(int) aPC.getTotalBonusTo("COMBAT", "TOHIT") +
					(int) aPC.getTotalBonusTo("TOHIT", "TYPE.GRAPPLE")));
			}
			else if ("ATTACK.MELEE".equals(aString))
			{
				FileAccess.write(output, aPC.getAttackString(Constants.ATTACKSTRING_MELEE));
			}
			else if ("ATTACK.MELEE.BASE".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.baseAttackBonus()));
			}
			else if ("ATTACK.MELEE.MISC".equals(aString))
			{
				final int miscBonus =
					(int) aPC.getTotalBonusTo("COMBAT", "TOHIT") +
					(int) aPC.getTotalBonusTo("TOHIT", "TOHIT") +
					(int) aPC.getTotalBonusTo("TOHIT", "TYPE.MELEE") -
					(int) aPC.getStatBonusTo("TOHIT", "TYPE.MELEE") -
					(int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
				FileAccess.write(output, Delta.toString(miscBonus));
			}
			else if ("ATTACK.MELEE.SIZE".equals(aString))
			{
				FileAccess.write(output, Delta.toString((int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT")));
			}
			else if ("ATTACK.MELEE.STAT".equals(aString))
			{
				FileAccess.write(output, Delta.toString((int) aPC.getStatBonusTo("TOHIT", "TYPE.MELEE")));
			}
			else if ("ATTACK.MELEE.TOTAL".equals(aString))
			{
				FileAccess.write(output, aPC.getAttackString(Constants.ATTACKSTRING_MELEE,
					(int) aPC.getTotalBonusTo("TOHIT", "TOHIT") +
					(int) aPC.getTotalBonusTo("COMBAT", "TOHIT") +
					(int) aPC.getTotalBonusTo("TOHIT", "TYPE.MELEE")));
			}
			else if ("ATTACK.RANGED".equals(aString))
			{
				FileAccess.write(output, aPC.getAttackString(Constants.ATTACKSTRING_RANGED));
			}
			else if ("ATTACK.RANGED.BASE".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.baseAttackBonus()));
			}
			else if ("ATTACK.RANGED.MISC".equals(aString))
			{
				final int miscBonus =
					(int) aPC.getTotalBonusTo("COMBAT", "TOHIT") +
					(int) aPC.getTotalBonusTo("TOHIT", "TOHIT") +
					(int) aPC.getTotalBonusTo("TOHIT", "TYPE.RANGED") -
					(int) aPC.getStatBonusTo("TOHIT", "TYPE.RANGED") -
					(int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
				FileAccess.write(output, Delta.toString(miscBonus));
			}
			else if ("ATTACK.RANGED.SIZE".equals(aString))
			{
				FileAccess.write(output, Delta.toString((int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT")));
			}
			else if ("ATTACK.RANGED.STAT".equals(aString))
			{
				FileAccess.write(output, Delta.toString((int) aPC.getStatBonusTo("TOHIT", "TYPE.RANGED")));
			}
			else if ("ATTACK.RANGED.TOTAL".equals(aString))
			{
				FileAccess.write(output, aPC.getAttackString(Constants.ATTACKSTRING_RANGED,
					(int) aPC.getTotalBonusTo("TOHIT", "TOHIT") +
					(int) aPC.getTotalBonusTo("COMBAT", "TOHIT") +
					(int) aPC.getTotalBonusTo("TOHIT", "TYPE.RANGED")));
			}
			else if ("ATTACK.UNARMED".equals(aString))
			{
				FileAccess.write(output, aPC.getAttackString(Constants.ATTACKSTRING_UNARMED));
			}
			else if ("ATTACK.UNARMED.BASE".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.baseAttackBonus()));
			}
			else if ("ATTACK.UNARMED.SIZE".equals(aString))
			{
				FileAccess.write(output, Integer.toString((int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT")));
			}
			else if ("ATTACK.UNARMED.TOTAL".equals(aString))
			{
				FileAccess.write(output, aPC.getAttackString(Constants.ATTACKSTRING_UNARMED,
					(int) aPC.getTotalBonusTo("COMBAT", "TOHIT") +
					(int) aPC.getTotalBonusTo("TOHIT", "TOHIT") +
					(int) aPC.getStatBonusTo("TOHIT", "TYPE.MELEE")));
			}
			else if (aString.startsWith("DAMAGE.UNARMED"))
			{
				FileAccess.write(output, aPC.getUnarmedDamageString(false, true));
			}
			// SPELLMEM.x.x.x.x.LABEL classNum.bookNum.level.spellnumber
			// LABEL is TIMES,NAME,RANGE,etc. if not supplied it defaults to NAME
			else if (aString.startsWith("SPELLMEM"))
			{
				replaceTokenSpellMem(aString, output);
			}
			else if (aString.startsWith("SPELLBOOKNAME"))
			{
				// New Token Syntax SPELLBOOKNAME.x
				// When removing old syntax, remove the else
				final StringTokenizer aTok = new StringTokenizer(aString, ".");
				String bString = aTok.nextToken();
				final int bookNum;
				if ("SPELLBOOKNAME".equals(bString))
				{
					bookNum = Integer.parseInt(aTok.nextToken());
				}
				else
				{
					Logging.errorPrint("Old syntax SPELLBOOKNAMEx will be replaced for SPELLBOOKNAME.x");

					bookNum = Integer.parseInt(aString.substring(13));
				}

				FileAccess.write(output, (String) aPC.getSpellBooks().get(bookNum));
			}
			else if ("SPELLPOINTS".equals(aString))
			{
				FileAccess.write(output, Globals.getSpellPoints());
			}
			else if (aString.startsWith("SPELLLIST"))
			//SPELLLISTCAST0.0 KNOWN0.0 BOOK0.0 TYPE0
			{
				replaceTokenSpellList(aString, output);
			}
			else if ("HP".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.hitPoints()));
			}
			else if ("ALTHP".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.altHP()));
			}
			else if (aString.startsWith("LANGUAGES"))
			{
				replaceTokenLanguages(aString, output);
			}
			else if ("WEAPONPROFS".equals(aString))
			{
				if (SettingsHandler.getWeaponProfPrintout())
				{
					int i;
					final int k = aString.lastIndexOf(',');
					if (k >= 0)
					{
						aString = aString.substring(k + 1);
					}
					else
					{
						aString = ", ";
					}

					final SortedSet stringList = aPC.getWeaponProfList();

					for (i = 0; i < stringList.size(); ++i)
					{
						FileAccess.write(output, (String) stringList.toArray()[i]);
						if (i < stringList.size() - 1)
						{
							FileAccess.write(output, aString);
						}
					}
				}
			}
			else if (aString.startsWith("ARMOR"))
			{
				len = replaceTokenArmor(aString, len, output);
			}
			else if (aString.startsWith("WEAPON"))
			{
				replaceTokenWeapon(aString, output);
			}
			else if (aString.startsWith("EQCONTAINER"))
			{
				replaceTokenEqContainer(aString, output);
			}
			else if (aString.startsWith("EQ"))
			{
				aString = replaceTokenEq(aString);
				// I had to remove encodeWrite from here,
				// because i can write HTML or XML tokens
				FileAccess.write(output, aString);
			}
			else if ("TOTAL.WEIGHT".equals(aString))
			{
				final Float totalWeight = aPC.totalWeight();
				FileAccess.encodeWrite(output, totalWeight.toString() + " " + Globals.getWeightDisplay());
			}
			else if ("TOTAL.VALUE".equals(aString))
			{
				FileAccess.encodeWrite(output, BigDecimalHelper.trimZeros(aPC.totalValue()) + " " + Globals.getCurrencyDisplay());
			}
			else if ("TOTAL.CAPACITY".equals(aString))
			{
				FileAccess.write(output, Globals.maxLoadForLoadScore(aPC.getVariableValue("LOADSCORE", "").intValue()).toString());
			}
			else if ("TOTAL.LOAD".equals(aString))
			{
				replaceTokenTotalLoad(output);
			}
			else if (aString.startsWith("MISC."))
			{
				int i = -1;
				if (aString.substring(5).startsWith("FUNDS"))
				{
					i = 0;
				}
				else if (aString.substring(5).startsWith("COMPANIONS"))
				{
					i = 1;
				}
				else if (aString.substring(5).startsWith("MAGIC"))
				{
					i = 2;
				}

				// This is what:
				// for tags like the following in FOR loops
				// will add after the ',' at end of each line
				// |MISC.MAGIC,</fo:block><fo:block font-size="7pt">|
				final int k = aString.lastIndexOf(',');
				if (k >= 0)
				{
					aString = aString.substring(k + 1);
				}
				else
				{
					aString = "";
				}

				if (i >= 0)
				{
					final List stringList = getLineForMiscList(i);
					for (i = 0; i < stringList.size(); ++i)
					{
						FileAccess.encodeWrite(output, (String) stringList.get(i));
						FileAccess.write(output, aString);
					}
				}
			}
			else if ("GOLD".equals(aString))
			{
				FileAccess.write(output, aPC.getGold().toString());
			}
//*********************************************
//rework of defense tokens by Arknight 01-02-03
//*********************************************
			else if ("DEFENSE.TOTAL".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.getACTotal()));
			}
			else if ("DEFENSE.FLATFOOTED".equals(aString))
			{
				FileAccess.write(output, aPC.flatfootedAC());
			}
			else if ("DEFENSE.TOUCH".equals(aString))
			{
				FileAccess.write(output, aPC.touchAC());
			}
			else if ("DEFENSE.BASE".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.baseAC()));
			}
			else if ("DEFENSE.ABILITY".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.abilityAC()));
			}
			else if ("DEFENSE.CLASS".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.classAC()));
			}
			else if ("DEFENSE.DODGE".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.dodgeAC()));
			}
			else if ("DEFENSE.EQUIPMENT".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.equipmentAC()));
			}
			else if ("DEFENSE.MISC".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.miscAC()));
			}
			else if ("DEFENSE.NATURAL".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.naturalAC()));
			}
			else if ("DEFENSE.SIZE".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.sizeAC()));
			}
			else if ("POOL.CURRENT".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.getPoolAmount()));
			}
			else if ("POOL.COST".equals(aString))
			{
				FileAccess.write(output, Integer.toString(aPC.getCostPool()));
			}
			else if ("TABNAME".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getTabName());
			}
			else if ("PLAYERNAME".equals(aString))
			{
				FileAccess.encodeWrite(output, aPC.getPlayersName());
			}
			else if ("VISION".equals(aString))
			{
				//				aPC.doVision(aPC.getVision(), 2);
				// A little redundant but seems needed based on some errors I was seeing.
				FileAccess.encodeWrite(output, aPC.getVision());
			}
			else if (aString.startsWith("WEIGHT."))
			{
				replaceTokenWeight(aString, output);
			}
			else if ("RACE.ABILITYLIST".equals(aString))
			{
				replaceTokenRaceAbilityList(output);
			}
			else if (aString.startsWith("VAR."))
			{
				replaceTokenVar(aString, output);
			}
			else if (aString.startsWith("DIR."))
			{
				replaceTokenDir(aString, output);
			}
			else
			{
				len = aString.trim().length();
				if (aString.length() > 0)
				{
					FileAccess.write(output, aString);
				}
			}
			FileAccess.maxLength(-1);
			return len;
		}
		catch (Exception exc)
		{
			Logging.errorPrint("Error replacing " + aString, exc);

			GuiFacade.showMessageDialog(null,
				"Error replacing " + aString,
				Constants.s_APPNAME,
				GuiFacade.ERROR_MESSAGE);
			return 0;
		}
	}

	private void replaceTokenVar(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString.substring(5), ".", false);
		final String varName = aTok.nextToken();
		String bString = "EQ";
		if (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
		}
		String value = "0";
		if (aTok.hasMoreTokens())
		{
			value = aTok.nextToken();
		}
		final Float varval = aPC.getVariable(varName, true, true, "", "");
		final Float valval = aPC.getVariableValue(value, "");
		if ("GTEQ".equals(bString))
		{
			canWrite = varval.doubleValue() >= valval.doubleValue();
		}
		else if ("GT".equals(bString))
		{
			canWrite = varval.doubleValue() > valval.doubleValue();
		}
		else if ("LTEQ".equals(bString))
		{
			canWrite = varval.doubleValue() <= valval.doubleValue();
		}
		else if ("LT".equals(bString))
		{
			canWrite = varval.doubleValue() < valval.doubleValue();
		}
		else if ("NEQ".equals(bString))
		{
			canWrite = !Utility.doublesEqual(varval.doubleValue(), valval.doubleValue());
		}
		else
		{
			canWrite = !Utility.doublesEqual(varval.doubleValue(), valval.doubleValue());
		}
	}

	private void replaceTokenMove(String aString, BufferedWriter output)
	{
		if ((aPC.getRace() != null) && !aPC.getRace().equals(Globals.s_EMPTYRACE))
		{
			final StringTokenizer aTok = new StringTokenizer(aString, ".");
			String fString = aTok.nextToken();
			int moveIndex;
			// New syntax MOVE.x instead of MOVEx
			// When removing old syntax, remove the try and leave the catch
			try
			{
				moveIndex = Integer.parseInt(fString.substring(4));

				Logging.errorPrint("Old syntax MOVEx will be replaced for MOVE.x");
			}
			catch (NumberFormatException exc)
			{
				moveIndex = Integer.parseInt(aTok.nextToken());
			}

			if (aTok.hasMoreTokens())
			{
				fString = aTok.nextToken();
				if ("NAME".equals(fString))
				{
					FileAccess.write(output, aPC.getMovementType(moveIndex));
				}
				else if ("RATE".equals(fString))
				{
					FileAccess.encodeWrite(output, aPC.movement(moveIndex) + Globals.getAbbrMovementDisplay());
				}
			}
			else
			{
				FileAccess.encodeWrite(output, aPC.getMovementType(moveIndex) + " " + aPC.movement(moveIndex) + Globals.getAbbrMovementDisplay());
			}
		}
	}

	private static String replaceTokenCommaSeparatedList(String aString, List stringList, BufferedWriter output)
	{
		int i;
		final int k = aString.lastIndexOf(',');
		if (k >= 0)
		{
			aString = aString.substring(k + 1);
		}
		else
		{
			aString = "";
		}

		for (i = 0; i < stringList.size(); ++i)
		{
			FileAccess.encodeWrite(output, (String) stringList.get(i));
			if (i < stringList.size() - 1)
			{
				FileAccess.write(output, aString);
			}

		}
		return aString;
	}

	private void replaceTokenIIF(String aString, BufferedWriter output)
	{
		int iParenCount = 0;
		final String[] aT = new String[3];
		int i;
		int iParamCount = 0;
		int iStart = 4;

		// OIF(expr,truepart,falsepart)
		// {|OIF(HASFEAT:Armor Prof (Light), <b>Yes</b>, <b>No</b>)|}

		for (i = iStart; i < aString.length(); ++i)
		{
			if (iParamCount == 3)
			{
				break;
			}

			switch (aString.charAt(i))
			{
				case '(':
					iParenCount += 1;
					break;

				case ')':
					iParenCount -= 1;
					if (iParenCount == -1)
					{
						if (iParamCount == 2)
						{
							aT[iParamCount++] = aString.substring(iStart, i).trim();
							iStart = i + 1;
						}
						else
						{
							Logging.errorPrint("IIF: not enough parameters");
						}
					}
					break;

				case ',':
					if (iParenCount == 0)
					{

						if (iParamCount < 2)
						{
							aT[iParamCount] = aString.substring(iStart, i).trim();
							iStart = i + 1;
						}
						else
						{
							Logging.errorPrint("IIF: too many parameters");
						}
						iParamCount += 1;
					}
					break;

				default:
					break;
			}
		}

		if (iParamCount != 3)
		{
			Logging.errorPrint("IIF: invalid parameter count: " + iParamCount);
		}
		else
		{
			aString = aString.substring(iStart);
			iStart = 2;
			if (evaluateExpression(aT[0]))
			{
				iStart = 1;
			}
			FileAccess.write(output, aT[iStart]);
		}

		if (aString.length() > 0)
		{
			Logging.errorPrint("IIF: extra characters on line: " + aString);
			FileAccess.write(output, aString);
		}
	}

	private void replaceTokenVar(String aString, BufferedWriter output)
	{
		final boolean isMin = aString.lastIndexOf(".MINVAL") >= 0;
		int index = aString.length();
		if (aString.lastIndexOf(".INTVAL") >= 0)
		{
			index = aString.lastIndexOf(".INTVAL");
		}
		if (aString.lastIndexOf(".MINVAL") >= 0)
		{
			index = Math.min(index, aString.lastIndexOf(".MINVAL"));
		}
		final Float val = aPC.getVariable(aString.substring(4, index), !isMin, true, "", "");
		if ((aString.lastIndexOf(".NOSIGN") < 0) && (val.doubleValue() > 0.0))
		{
			FileAccess.write(output, "+");
		}
		if (aString.lastIndexOf(".INTVAL") >= 0)
		{
			final int pos = val.toString().lastIndexOf('.');
			FileAccess.write(output, val.toString().substring(0, pos));
		}
		else
		{
			FileAccess.write(output, val.toString());
		}
	}

	private int getTokenSkillPoints(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		String bString;

		bString = aTok.nextToken();
		if (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
		}
		if (bString.startsWith("SKILLPOINTS"))
		{
			bString = "TOTAL";
		}

		PCClass aPCClass;
		float aTotalSkillPoints = 0;

		if ("TOTAL".equals(bString) || "UNUSED".equals(bString))
		{
			for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
			{
				aPCClass = (PCClass) it.next();

				if (aPCClass.getSkillPool().intValue() > 0)
				{
					aTotalSkillPoints += aPCClass.getSkillPool().intValue();
				}
			}
		}

		if ("TOTAL".equals(bString) || "USED".equals(bString))
		{
			Skill aSkill;
			for (Iterator it = aPC.getSkillList().iterator(); it.hasNext();)
			{
				aSkill = (Skill) it.next();

				if ((aSkill.getRank().doubleValue() > 0) ||
					(aSkill.getOutputIndex() != 0))
				{
					float ranks;
					String className;
					String classRanks;
					for (Iterator it2 = aSkill.getRankList().iterator(); it2.hasNext();)
					{
						classRanks = (String) it2.next();

						int index = classRanks.indexOf(':');
						className = classRanks.substring(0, index);
						ranks = Float.valueOf(classRanks.substring(index + 1)).floatValue();

						aPCClass = aPC.getClassKeyed(className);

						aTotalSkillPoints += ranks * aSkill.costForPCClass(aPCClass).intValue();
					}
				}
			}
		}
		return (int) aTotalSkillPoints;
	}

	private void replaceTokenRaceAbilityList(BufferedWriter output)
	{
		int i = 0;
		String bString;
		final List aList = aPC.getRace().getSpecialAbilityList();
		if (aList == null || aList.isEmpty())
		{
			return;
		}
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			bString = ((SpecialAbility) e.next()).getName();
			if (i++ > 0)
			{
				FileAccess.write(output, ", ");
			}
			FileAccess.encodeWrite(output, bString);
		}
	}

	private void replaceTokenBonusList(String aString, BufferedWriter output)
	{
		final StringTokenizer bTok = new StringTokenizer(aString.substring(10), ".", false);
		String bonusString = "";
		String substring = "";
		String typeSeparator = " ";
		String delim = ", ";
		if (bTok.hasMoreTokens())
		{
			bonusString = bTok.nextToken();
		}
		if (bTok.hasMoreTokens())
		{
			substring = bTok.nextToken();
		}
		if (bTok.hasMoreTokens())
		{
			typeSeparator = bTok.nextToken();
		}
		if (bTok.hasMoreTokens())
		{
			delim = bTok.nextToken();
		}
		final int typeLen = bonusString.length() + substring.length() + 2;
		if (substring.length() > 0 && bonusString.length() > 0)
		{
			final int total = (int) aPC.getTotalBonusTo(bonusString, substring);
			if ("TOTAL".equals(typeSeparator))
			{
				FileAccess.write(output, String.valueOf(total));
				return;
			}
			boolean needDelim = false;
			final String prefix = bonusString + "." + substring + ".";
			for (Iterator bi = aPC.getActiveBonusMap().keySet().iterator(); bi.hasNext();)
			{
				String aKey = bi.next().toString();
				if (aKey.startsWith(prefix))
				{
					if (needDelim)
					{
						FileAccess.write(output, delim);
					}
					if (aKey.length() > typeLen)
					{
						FileAccess.write(output, aKey.substring(typeLen));
					}
					else
					{
						FileAccess.write(output, "None");
					}
					FileAccess.write(output, typeSeparator);
					FileAccess.write(output, (String) aPC.getActiveBonusMap().get(aKey));
					needDelim = true;
				}
			}
		}
	}

	private void replaceTokenClass(String aString, BufferedWriter output)
	{
		final int TOKEN_CLASS = 0;
		final int TOKEN_CLASSLIST = 1;
		final int TOKEN_CLASSABB = 2;
		final int TOKEN_CLASSLEVEL = 3;
		final int TOKEN_CLASSSALIST = 4;
		int i = 0;
		int y = 0;
		int cmp = TOKEN_CLASS;
		// New token syntax
		// CLASS.x and CLASSABB.x
		// When removing old syntax, remove the else
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		String fString = aTok.nextToken();
		if ("CLASSLIST".equals(fString) ||
			"CLASSABB".equals(fString) ||
			"CLASS".equals(fString))
		{
			if ("CLASSLIST".equals(fString))
			{
				cmp = TOKEN_CLASSLIST;
			}
			else if ("CLASSABB".equals(fString))
			{
				cmp = TOKEN_CLASSABB;
			}
			if (aTok.hasMoreTokens())
			{
				i = Integer.parseInt(aTok.nextToken());
			}
			if (aTok.hasMoreTokens())
			{
				final String bString = aTok.nextToken();
				if ("LEVEL".equals(bString))
				{
					cmp = TOKEN_CLASSLEVEL;
				}
				else if ("SALIST".equals(bString))
				{
					cmp = TOKEN_CLASSSALIST;
				}
			}
		}
		else
		{
			Logging.errorPrint("Old syntax CLASSx will be replaced for CLASS.x");

			if ("CLASSLIST".equals(aString))
			{
				cmp = TOKEN_CLASSLIST;
			}
			else if (aString.lastIndexOf("ABB") >= 0)
			{
				i = Integer.parseInt(fString.substring(8));
				//i = Integer.valueOf(fString.substring(8)).intValue();
				cmp = TOKEN_CLASSABB;
			}
			else
			{
				i = (int) fString.charAt(5) - 48;
			}
			if (aString.endsWith("LEVEL"))
			{
				cmp = TOKEN_CLASSLEVEL;
			}
			if (aString.endsWith("SALIST"))
			{
				cmp = TOKEN_CLASSSALIST;
			}
		}
		final int classSize = aPC.getClassList().size();
		if (i >= classSize && existsOnly)
		{
			noMoreItems = true;
			return;
		}
		for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass) e.next();
			//
			// Don't show monster levels
			//
			if (SettingsHandler.hideMonsterClasses() && aClass.isMonster())
			{
				continue;
			}

			if ((cmp == TOKEN_CLASSLIST) && (y++ > 0))
			{
				FileAccess.write(output, " ");
			}
			if (aClass.getLevel() > 0)
			{
				--i;
			}
			if ((i == -1) || (cmp == TOKEN_CLASSLIST))
			{
				if (cmp < TOKEN_CLASSABB)
				{
					if (Constants.s_NONE.equals(aClass.getSubClassName()) || "".equals(aClass.getSubClassName()))
					{
						//FileAccess.encodeWrite(output, aClass.getName());
						FileAccess.encodeWrite(output, aClass.getOutputName());
					}
					else
					{
						FileAccess.encodeWrite(output, aClass.getSubClassName());
					}
				}
				if ((cmp == TOKEN_CLASSLIST) || (cmp == TOKEN_CLASSLEVEL))
				{
					FileAccess.write(output, Integer.toString(aClass.getLevel()));
				}
				else if (cmp == TOKEN_CLASSABB)
				{
					//
					// Check for subclasses
					//
					final String subClassName = aClass.getDisplayClassName();
					if (!aClass.getName().equals(subClassName))
					{
						final PCClass subClass = aClass.getSubClassNamed(subClassName);
						if (subClass != null)
						{
							aClass = subClass;
						}
					}
					FileAccess.encodeWrite(output, aClass.getAbbrev());
				}
				else if (cmp == TOKEN_CLASSSALIST)
				{
					final List saList = aClass.getClassSpecialAbilityList();
					int x = 0;
					for (Iterator e1 = saList.iterator(); e1.hasNext();)
					{
						if (x++ > 0)
						{
							FileAccess.write(output, ", ");
						}
						FileAccess.write(output, e1.next().toString());
					}
				}
				else if (cmp != TOKEN_CLASSLIST)
				{
					break;
				}
			}
		}
	}

	private static void replaceTokenDir(String aString, BufferedWriter output)
	{
		if (aString.endsWith("PCGEN"))
		{
			FileAccess.write(output, SettingsHandler.getPcgenSystemDir().getAbsolutePath());
		}
		else if (aString.endsWith("TEMPLATES"))
		{
			FileAccess.write(output, SettingsHandler.getPcgenOutputSheetDir().getAbsolutePath());
		}
		else if (aString.endsWith("PCG"))
		{
			FileAccess.write(output, SettingsHandler.getPcgPath().getAbsolutePath());
		}
		else if (aString.endsWith("HTML"))
		{
			FileAccess.write(output, SettingsHandler.getHTMLOutputSheetPath());
		}
		else if (aString.endsWith("TEMP"))
		{
			FileAccess.write(output, SettingsHandler.getTempPath().getAbsolutePath());
		}
		else
		{
			Logging.errorPrint("DIR: Unknown Dir: " + aString);

			FileAccess.write(output, aString);
		}
	}

	private void replaceTokenHitDice(BufferedWriter output)
	{
		String del = "";
		if (aPC.getRace().hitDice() > 0)
		{
			FileAccess.write(output, "(" + Integer.toString(aPC.getRace().hitDice()) + "d" +
				Integer.toString(aPC.getRace().getHitDiceSize()) + ")");
			del = "+";
		}

		PCClass aClass;
		String aaClassLevel;
		Integer aClassHitDie;
		String aaCLassHitDie;

		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{

			aClass = (PCClass) it.next();
			aaClassLevel = Integer.toString(aClass.getLevel());
			aClassHitDie = new Integer(aClass.getHitDie());
			aaCLassHitDie = aClassHitDie.toString();

			FileAccess.write(output, del + "(" + aaClassLevel + "d" + aaCLassHitDie + ")");
			del = "+";
		}

		//
		// Get CON bonus contribution to hitpoint total
		//
		int temp = (int) aPC.getStatBonusTo("HP", "BONUS");
		temp *= (aPC.getTotalLevels() + aPC.getRace().hitDice());
		//
		// Add in feat bonus
		//
		temp += (int) aPC.getTotalBonusTo("HP", "CURRENTMAX");
		if (temp != 0)
		{
			FileAccess.write(output, Delta.toString(temp));
		}

	}

	/**
	 * BASEMOVEMENT related stuff
	 * possible tokens are
	 *   BASEMOVEMENT.type.load.flag
	 * where
	 *   type    := "WALK" and other Movement Types|a numeric value
	 *				so 0 is the first movement type etc.
	 *   load	 := "LIGHT"|"MEDIUM"|"HEAVY"|"OVERLOAD"
	 *   flag	 := "TRUE"|"FALSE"
	 *		TRUE = Add Movement Measurement type to String.
	 *				FALSE = Dont Add Movement Measurement type to String
	 *   del     := "."
	 *
	 * i.e. BASEMOVEMENT.0.LIGHT.TRUE
	 *  Would output 30' for a normal human
	 * and	BASEMOVEMENT.0.LIGHT.FALSE
	 *  Would output 30 for the same human.
	 *
	 * author: Tim Evans 24-07-02
	 */
	private void replaceTokenBaseMovement(String aString, BufferedWriter output)
	{
		if ((aPC.getRace() != null) && !aPC.getRace().equals(Globals.s_EMPTYRACE))
		{
			if (aString.length() > 13)
			{
				aString = aString.substring(13);
				final StringTokenizer aTok = new StringTokenizer(aString, ".", false);
				final String moveType = aTok.nextToken();
				int iLoad = Constants.LIGHT_LOAD;
				boolean bFlag = false;
				String sMeasure;
				String sType;
				int iPos;

				aString = "LIGHT";
				sMeasure = "";
				sType = "";
				try
				{
					iPos = Integer.parseInt(moveType);
				}
				catch (NumberFormatException e)
				{
					iPos = -1;
				}
				if (aTok.hasMoreTokens())
				{
					aString = (aTok.nextToken()).toUpperCase();
				}
				if (aTok.hasMoreTokens())
				{
					bFlag = ("TRUE".equals((aTok.nextToken()).toUpperCase()));
					if (bFlag)
					{
						sMeasure = Globals.getAbbrMovementDisplay();
					}
				}
				if ("MEDIUM".equals(aString))
				{
					iLoad = Constants.MEDIUM_LOAD;
				}
				else
				{
					if ("HEAVY".equals(aString))
					{
						iLoad = Constants.HEAVY_LOAD;
					}
					else
					{
						if ("OVERLOAD".equals(aString))
						{
							iLoad = Constants.OVER_LOAD;
						}
					}
				}
				if (iPos == -1)
				{
					for (int x = 0; x < aPC.getNumberOfMovements(); ++x)
					{
						if (aPC.getMovementType(x).toUpperCase().equals(moveType.toUpperCase()))
						{
							if (bFlag)
							{
								sType = aPC.getMovementType(x) + " ";
							}
							// Output choices for Move types contained in here, only RATE currently Defined
							FileAccess.encodeWrite(output, sType + aPC.basemovement(x, iLoad) + sMeasure);
						}
					}
				}
				else
				{
					if (iPos < aPC.getNumberOfMovements())
					{
						if (bFlag)
						{
							sType = aPC.getMovementType(iPos) + " ";
						}
						// Output choices for Move types contained in here, only RATE currently Defined
						FileAccess.encodeWrite(output, sType + aPC.basemovement(iPos, iLoad) + sMeasure);
					}
				}
			}
			else
			{
				FileAccess.encodeWrite(output, aPC.getMovementType(0) + " " + aPC.basemovement(0, 0) + Globals.getAbbrMovementDisplay());
				for (int x = 1; x < aPC.getNumberOfMovements(); ++x)
				{
					FileAccess.encodeWrite(output, ", " + aPC.getMovementType(x) + " " + aPC.basemovement(x, 0) + Globals.getAbbrMovementDisplay());
				}
			}
		}
	}

	private void replaceTokenMovement(String aString, BufferedWriter output)
	{
		if ((aPC.getRace() != null) && !aPC.getRace().equals(Globals.s_EMPTYRACE))
		{
			if (aString.length() > 9)
			{
				aString = aString.substring(9);
				final StringTokenizer aTok = new StringTokenizer(aString, ".", false);
				final String moveType = aTok.nextToken();
				aString = "RATE";
				if (aTok.hasMoreTokens())
				{
					aString = (aTok.nextToken()).toUpperCase();
				}

				for (int x = 0; x < aPC.getNumberOfMovements(); ++x)
				{
					if (aPC.getMovementType(x).toUpperCase().equals(moveType.toUpperCase()))
					{
						// Output choices for Move types contained in here, only RATE currently Defined
						if ("RATE".equals(aString))
						{
							FileAccess.encodeWrite(output, "" + aPC.movement(x) + Globals.getAbbrMovementDisplay());
						}
					}
				}
			}
			else
			{
				FileAccess.encodeWrite(output, aPC.getMovementType(0) + " " + aPC.movement(0) + Globals.getAbbrMovementDisplay());
				for (int x = 1; x < aPC.getNumberOfMovements(); ++x)
				{
					FileAccess.encodeWrite(output, ", " + aPC.getMovementType(x) + " " + aPC.movement(x) + Globals.getAbbrMovementDisplay());
				}
			}
		}
	}

	private static void replaceTokenPaperInfo(String aString, BufferedWriter output)
	{
		String oString = aString;
		aString = aString.substring(10);
		int infoType = -1;
		if (aString.startsWith("NAME"))
		{
			infoType = Constants.PAPERINFO_NAME;
		}
		else if (aString.startsWith("HEIGHT"))
		{
			infoType = Constants.PAPERINFO_HEIGHT;
		}
		else if (aString.startsWith("WIDTH"))
		{
			infoType = Constants.PAPERINFO_WIDTH;
		}
		else if (aString.startsWith("MARGIN"))
		{
			aString = aString.substring(6);
			if (aString.startsWith("TOP"))
			{
				infoType = Constants.PAPERINFO_TOPMARGIN;
			}
			else if (aString.startsWith("BOTTOM"))
			{
				infoType = Constants.PAPERINFO_BOTTOMMARGIN;
			}
			else if (aString.startsWith("LEFT"))
			{
				infoType = Constants.PAPERINFO_LEFTMARGIN;
			}
			else if (aString.startsWith("RIGHT"))
			{
				infoType = Constants.PAPERINFO_RIGHTMARGIN;
			}
		}
		if (infoType >= 0)
		{
			final int offs = aString.indexOf('=');
			final String info = Globals.getPaperInfo(infoType);
			if (info == null)
			{
				if (offs >= 0)
				{
					oString = aString.substring(offs + 1);
				}
			}
			else
			{
				oString = info;
			}
		}
		FileAccess.write(output, oString);
	}

	/**
	 * select notes
	 * possible tokens are:
	 *
	 * NOTEx.NAME
	 * NOTEx.VALUE
	 * NOTEx.VALUE.lineprefix.linesuffix
	 *
	 * <br>author: Thomas Behr 06-09-02

	 * When removing old syntax, this function can be removed
	 */
	private void _replaceTokenNote(String aString, BufferedWriter output)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final String[] tokens = new String[aTok.countTokens()];

		for (int i = 0; i < tokens.length; ++i)
		{
			tokens[i] = aTok.nextToken();
		}

		if (tokens.length > 1)
		{

			NoteItem aNoteItem = null;
			final int index = Integer.parseInt(tokens[0].substring(4));
			if (index < aPC.getNotesList().size())
			{
				aNoteItem = (NoteItem) aPC.getNotesList().get(index);
			}

			if (aNoteItem != null)
			{

				if ("NAME".equals(tokens[1]))
				{
					FileAccess.encodeWrite(output, aNoteItem.getName());
				}
				else if ("VALUE".equals(tokens[1]))
				{

					final String prefix = (tokens.length > 2) ? tokens[2] : "";
					final String suffix = (tokens.length > 3) ? tokens[3] : "";

					final StringTokenizer bTok = new StringTokenizer(aNoteItem.getValue(), "\r\n");
					while (bTok.hasMoreTokens())
					{
						FileAccess.write(output, prefix);
						FileAccess.encodeWrite(output, bTok.nextToken());
						FileAccess.write(output, suffix);
					}
				}
			}
		}
	}

	//
	// HPROLL.x
	// HPROLL.x.ROLL
	// HPROLL.x.STAT
	// HPROLL.x.TOTAL
	//
	private void replaceTokenHPRoll(String aString, BufferedWriter output)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		String bString;
		int aHpRoll = 0;
		int aHpBonus;

		bString = aTok.nextToken();
		// New token syntax HPROLL.x instead of HPROLLx
		// When removing old syntax, remove the else
		final int aLevelOffset;
		if ("HPROLL".equals(bString))
		{
			aLevelOffset = Integer.valueOf(aTok.nextToken()).intValue() - 1;
		}
		else
		{
			Logging.errorPrint("Old syntax HPROLLx will be replaced for HPROLL.x");

			aLevelOffset = Integer.valueOf(bString.substring(6)).intValue() - 1;
		}
		if (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
		}
		if (bString.startsWith("HPROLL"))
		{
			bString = "ROLL";
		}
		if ((aLevelOffset >= aPC.getLevelInfoSize()) || (aLevelOffset < 0))
		{
			FileAccess.write(output, "0");
			return;
		}

		final int aClassLevel = aPC.getLevelInfoClassLevel(aLevelOffset) - 1;
		final PCClass aClass = aPC.getClassKeyed(aPC.getLevelInfoClassKeyName(aLevelOffset));
		if (aClass != null)
		{
			aHpRoll = aClass.getHitPoint(aClassLevel).intValue();
		}

		aHpBonus = (int) aPC.getStatBonusTo("HP", "BONUS");

		if ("ROLL".equals(bString))
		{
			FileAccess.write(output, Integer.toString(aHpRoll));
		}
		else if ("STAT".equals(bString))
		{
			FileAccess.write(output, Integer.toString(aHpBonus));
		}
		else if ("TOTAL".equals(bString))
		{
			FileAccess.write(output, Integer.toString(aHpRoll + aHpBonus));
		}
	}

	/**
	 * New Token syntax. This function will treat all NOTEx -> NOTE.x tokens
	 * Those were treated before by _replaceTokenNote
	 * @param aString
	 * @param output
	 */
	private void replaceTokenNote(String aString, BufferedWriter output)
	{
		final StringTokenizer bTok = new StringTokenizer(aString, ".", false);
		bTok.nextToken(); // should be NOTE
		String name = bTok.nextToken();
		List aList;
		if ("ALL".equals(name))
		{
			aList = (ArrayList) aPC.getNotesList().clone();
		}
		else
		{
			try
			{
				final int x = Integer.parseInt(name);
				aList = new ArrayList();
				if (x >= 0 || x < aList.size())
				{
					aList.add(aPC.getNotesList().get(x));
				}
			}
			catch (NumberFormatException e)
			{
				aList = (ArrayList) aPC.getNotesList().clone();
				for (int x = aList.size() - 1; x >= 0; --x)
				{
					final NoteItem ni = (NoteItem) aList.get(x);
					if (!ni.getName().equalsIgnoreCase(name))
					{
						aList.remove(x);
					}
				}
			}
		}
		String beforeHeader = "<b>";
		String afterHeader = "</b><br>";
		String beforeValue = "";
		String afterValue = "<br>";
		String token = "ALL";

		//TODO: Is this right? Given that *every* part of the if below contains a break, this loop will execute exactly once. XXX
		while (bTok.hasMoreTokens())
		{
			name = bTok.nextToken();
			if ("NAME".equals(name))
			{
				token = "NAME";
				beforeHeader = afterHeader = beforeValue = afterValue = "";
				break;
			}
			else if ("VALUE".equals(name))
			{
				token = "VALUE";
				beforeHeader = afterHeader = beforeValue = afterValue = "";
				break;
			}
			else
			{
				beforeHeader = name;
				break;
			}
		}
		if (bTok.hasMoreTokens() && !"ALL".equals(token))
		{
			beforeHeader = bTok.nextToken();
		}
		if (bTok.hasMoreTokens())
		{
			afterHeader = bTok.nextToken();
		}
		if (bTok.hasMoreTokens())
		{
			beforeValue = bTok.nextToken();
		}
		if (bTok.hasMoreTokens())
		{
			afterValue = bTok.nextToken();
		}
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			NoteItem ni = (NoteItem) i.next();
			if ("ALL".equals(token))
			{
				FileAccess.write(output, ni.getExportString(beforeHeader, afterHeader, beforeValue, afterValue));
			}
			else if ("NAME".equals(token))
			{
				FileAccess.encodeWrite(output, ni.getName());
			}
			else if ("VALUE".equals(token))
			{
				final StringTokenizer cTok = new StringTokenizer(ni.getValue(), "\r\n");
				while (cTok.hasMoreTokens())
				{
					FileAccess.write(output, beforeHeader);
					FileAccess.encodeWrite(output, cTok.nextToken());
					FileAccess.write(output, afterHeader);
				}
			}
		}
	}

	private void replaceTokenFollowers(String aString, BufferedWriter output)
	{
		/* syndaryl 24/07/2002 12:51PM: kitbashed an output format for followers, much like the FEATLIST tag */
		/* Will also need to cover COUNT[FOLLOWERS], not done yet. */

		final List followers = aPC.getFollowerList();
		if (!followers.isEmpty())
		{ /* if it's empty, do nothing */
			if ("FOLLOWERLIST".equals(aString))
			{
				int i;
				boolean lastflag = false;
				for (i = 0; i < followers.size(); ++i)
				{
					if (followers.get(i) instanceof Follower)
					{
						Follower aF = (Follower) followers.get(i);
						for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
						{
							PlayerCharacter nPC = (PlayerCharacter) p.next();
							if (aF.getFileName().equals(nPC.getFileName()))
							{
								if (lastflag)
								{
									FileAccess.write(output, ", ");
								}
								FileAccess.encodeWrite(output, nPC.getName());
								lastflag = true;
							}
						}
					}
				}
			}
			else if (aString.startsWith("FOLLOWERTYPE."))
			{
				// Handle FOLLOWERTYPE.<type>x.subtag stuff
				// New token syntax FOLLOWERTYPE.<type>.x instead of FOLLOWERTYPE.<type>x
				StringTokenizer aTok = new StringTokenizer(aString, ".");
				aTok.nextToken();	// FOLLOWERTYPE
				String typeString = aTok.nextToken();
				String restString = "";
				int followerIndex = -1;
				if (aTok.hasMoreTokens())
				{
					restString = aTok.nextToken();
					// When removing old token syntax, remove the catch code
					try
					{
						followerIndex = Integer.parseInt(restString);
						restString = "";
					}
					catch (NumberFormatException exc)
					{
						// Error, not debug.  We want users to report
						// use of the deprecated syntax so we can fix
						// them as they are found.
						Logging.errorPrint("Old syntax FOLLOWERTYPEx will be replaced for FOLLOWERTYPE.x");

						int numCharToRemove = 0;

						for (int i = typeString.length() - 1; i > 0; i--)
						{
							if (typeString.charAt(i) >= '0' && typeString.charAt(i) <= '9')
							{
								followerIndex = Integer.parseInt(typeString.substring(i));
								numCharToRemove++;
							}
							else
							{
								i = 0;
							}
						}
						if (numCharToRemove > 0)
						{
							typeString = typeString.substring(0, typeString.length() - numCharToRemove);
						}
					}
					while (aTok.hasMoreTokens())
					{
						restString = restString + "." + aTok.nextToken();
					}
					if (restString.indexOf(".") == 0)
					{
						restString = restString.substring(1);
					}
				}
				List aList = new ArrayList();
				for (int i = followers.size() - 1; i >= 0; --i)
				{
					final Follower fol = (Follower) followers.get(i);
					if (fol.getType().equalsIgnoreCase(typeString))
					{
						aList.add(fol);
					}
				}
				if (followerIndex < aList.size())
				{
					if (aList.get(followerIndex) instanceof Follower)
					{
						final Follower aF = (Follower) aList.get(followerIndex);
						PlayerCharacter newPC;
						for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
						{
							PlayerCharacter nPC = (PlayerCharacter) p.next();
							if (aF.getFileName().equals(nPC.getFileName()))
							{
								newPC = nPC;
								if (restString.equals(""))
								{
									restString = "NAME";
								}
								nPC = aPC;
								aPC = newPC;
								Globals.setCurrentPC(aPC);
								replaceToken(restString, output);
								aPC = nPC;
								Globals.setCurrentPC(aPC);
							}
						}
					}
				}
			}
			else
			{
				/* FOLLOWER%.subtag stuff handled in here*/
				// New token syntax FOLLOWER.x instead of FOLLOWERx
				StringTokenizer aTok = new StringTokenizer(aString, ".");
				String fString = aTok.nextToken(); // FOLLOWER
				final int i;
				if ("FOLLOWER".equals(fString))
				{
					i = Integer.parseInt(aTok.nextToken());
				}
				else
				{
					Logging.errorPrint("Old syntax FOLLOWERx will be replaced for FOLLOWER.x");

					i = Integer.parseInt(aString.substring(8, aString.indexOf('.')));
				}

				if (i < followers.size())
				{
					if (followers.get(i) instanceof Follower)
					{
						final Follower aF = (Follower) followers.get(i);
						PlayerCharacter newPC;
						for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
						{
							PlayerCharacter nPC = (PlayerCharacter) p.next();
							if (aF.getFileName().equals(nPC.getFileName()))
							{
								newPC = nPC;
								String aLabel;
								if (aTok.hasMoreTokens())
								{
									aLabel = aTok.nextToken();
								}
								else
								{
									aLabel = "NAME";
								}
								nPC = aPC;
								aPC = newPC;
								Globals.setCurrentPC(aPC);
								replaceToken(aLabel, output);
								aPC = nPC;
								Globals.setCurrentPC(aPC);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Replaces the "TEMPBONUS" tokesn on output sheets
	 **/
	private void replaceTokenTempBonus(String aString, BufferedWriter output)
	{
		final List nameList = aPC.getNamedTempBonusList();
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		String fString = aTok.nextToken(); // discard first token
		int index = -1;

		if (aTok.hasMoreTokens())
		{
			index = Integer.parseInt(aTok.nextToken());
		}
		fString = (aTok.hasMoreTokens()) ? aTok.nextToken() : "NAME";

		if ((index > -1) && (index < nameList.size()))
		{
			if ("NAME".equals(fString))
			{
				fString = (String) nameList.get(index);
				FileAccess.write(output, fString);
			}
		}
	}

	/**
	 * Replaces "TEMPLATE*" tokens on output sheets.
	 *
	 * Method updated to work on visible templates only.
	 * author: Thomas Behr 07-01-03
	 */
	private void replaceTokenTemplate(String aString, BufferedWriter output)
	{
		final List tl = new ArrayList();

		PCTemplate aTemplate;
		for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
		{
			aTemplate = (PCTemplate) it.next();
			if (aTemplate.isVisible() == 1 || aTemplate.isVisible() == 2)
			{
				tl.add(aTemplate);
			}
		}

		if ("TEMPLATELIST".equals(aString))
		{
			String del = "";
			for (Iterator it = tl.iterator(); it.hasNext();)
			{
				FileAccess.write(output, del + it.next().toString());
				del = ", ";
			}
		}
		else
		{
			// New token syntax TEMPLATE.x instead of TEMPLATEx
//			final StringTokenizer aTok = new StringTokenizer(aString.substring(8), ".");
			final StringTokenizer aTok = new StringTokenizer(aString, ".");
			// When removing old syntax, remove fString and leave only the IF code
			final String fString = aTok.nextToken();
			final int indexOfTemplate;
			if ("TEMPLATE".equals(fString))
			{
				indexOfTemplate = Integer.parseInt(aTok.nextToken());
			}
			else
			{
				Logging.errorPrint("Old syntax TEMPLATEx will be replaced for TEMPLATE.x");

				indexOfTemplate = Integer.parseInt(fString.substring(8));
			}
			final String aLabel = (aTok.hasMoreTokens()) ? aTok.nextToken() : "NAME";

			if ((indexOfTemplate > -1) && (indexOfTemplate < tl.size()))
			{
				aTemplate = (PCTemplate) tl.get(indexOfTemplate);

				if ("NAME".equals(aLabel))
				{
					FileAccess.write(output, aTemplate.toString());
				}
				else if ("OUTPUTNAME".equals(aLabel))
				{
					FileAccess.write(output, aTemplate.getOutputName());
				}
				else if ("SA".equals(aLabel))
				{
					final List saList = aTemplate.getSpecialAbilityList(aPC.getTotalLevels(), aPC.totalHitDice());
					int x = 0;
					if (saList == null)
					{
						return;
					}
					for (Iterator e = saList.iterator(); e.hasNext();)
					{
						if (x++ > 0)
						{
							FileAccess.write(output, ", ");
						}
						FileAccess.write(output, e.next().toString());
					}
				}
				else if ("FEAT".equals(aLabel))
				{
					final List fList = aTemplate.feats(aPC.getTotalLevels(), aPC.totalHitDice());
					int x = 0;
					for (Iterator e = fList.iterator(); e.hasNext();)
					{
						if (x++ > 0)
						{
							FileAccess.write(output, ", ");
						}
						FileAccess.write(output, e.next().toString());
					}
				}
				else if ("SR".equals(aLabel))
				{
					FileAccess.write(output,
						Integer.toString(aTemplate.getSR(aPC.getTotalLevels(),
							aPC.totalHitDice()
						)));
				}
				else if ("CR".equals(aLabel))
				{
					FileAccess.write(output,
						Integer.toString(aTemplate.getCR(aPC.getTotalLevels(),
							aPC.totalHitDice()
						)));
				}
				else if ("DR".equals(aLabel))
				{
					FileAccess.write(output, aTemplate.getDR(aPC.getTotalLevels(), aPC.totalHitDice()));
				}
				else
				{
					for (int iMod = 0; iMod < Globals.s_ATTRIBSHORT.length; ++iMod)
					{
						final String modName = Globals.s_ATTRIBSHORT[iMod] + "MOD";
						if (aLabel.equals(modName))
						{
							if (aTemplate.isNonAbility(iMod))
							{
								FileAccess.write(output, "*");
							}
							else
							{
								FileAccess.write(output, Integer.toString(aTemplate.getStatMod(iMod)));
							}
							break;
						}
					}
				}
				/* TODO: DR subtag ... */
			}
		}
	}

	private void replaceTokenWeight(String aString, BufferedWriter output)
	{
		int i = 1;
		if (aString.endsWith("MEDIUM"))
		{
			i = 2;
		}
		else if (aString.endsWith("HEAVY"))
		{
			i = 3;
		}
		FileAccess.write(output, new Float(i * Globals.maxLoadForLoadScore(aPC.getVariableValue("LOADSCORE", "").intValue()).intValue() / 3).toString());
	}

// --Recycle Bin START (10/15/02 9:05 PM):
//	private void replaceTokenDefenseCtotal(BufferedWriter output)
//	{
//		int j = -1;
//		int total = 0;
//		int x = -1;
//		String myString = null;
//		PCClass myClass = null;
//		for (j = 0; j < aPC.getClassList().size(); ++j)
//		{
//			myClass = (PCClass)aPC.getClassList().get(j);
//			myString = myClass.defense(j).toString();
//			x = Integer.parseInt(myString);
//			total += x;
//		}
//		FileAccess.write(output, String.valueOf(total));
//	}
// --Recycle Bin STOP (10/15/02 9:05 PM)

	private void replaceTokenTotalLoad(BufferedWriter output)
	{
		final int load = Globals.loadTypeForLoadScore(aPC.getVariableValue("LOADSCORE", "").intValue(), aPC.totalWeight());
		switch (load)
		{
			case Constants.LIGHT_LOAD:
				FileAccess.write(output, "Light");
				return;
			case Constants.MEDIUM_LOAD:
				FileAccess.write(output, "Medium");
				return;
			case Constants.HEAVY_LOAD:
				FileAccess.write(output, "Heavy");
				return;
			case Constants.OVER_LOAD:
				FileAccess.write(output, "Overload");
				return;
			default:
				FileAccess.write(output, "Unknown");
				Logging.errorPrint("Unknown load constant detected in PlayerCharacter.replaceTokenTotalLoad, the constant was " + load + ".");
		}
	}

	/**
	 * Returns a replaced eq token. Should be refactored away completely.
	 * @param pc
	 * @param aString
	 * @return
	 */

	public static String returnReplacedTokenEq(PlayerCharacter pc, String aString)
	{
		return replaceTokenEq(aString, pc, false);
	}

	private String replaceTokenEq(String aString)
	{
		return ExportHandler.replaceTokenEq(aString, aPC, existsOnly);
	}

	private static String getTokenResultFromEq(Equipment eq, String tempString, StringTokenizer aTok, PlayerCharacter argPC)
	{
		String retString = "";
		if ("LONGNAME".equals(tempString))
		{
			retString = eq.longName();
		}
		else if ("NAME".equals(tempString))
		{
			//retString = eq.getName();
			retString = eq.getOutputName();
		}
		else if ("OUTPUTNAME".equals(tempString))
		{
			retString = eq.getOutputName();
		}
		else if ("NOTE".equals(tempString))
		{
			retString = eq.getNote();
		}
		else if ("WT".equals(tempString))
		{
			retString = BigDecimalHelper.trimZeros(eq.getWeight().toString());
		}
		else if ("TOTALWT".equals(tempString))
		{
			retString = BigDecimalHelper.trimZeros(Double.toString(eq.qty() * eq.getWeightAsDouble()));
		}
		else if ("TOTALWEIGHT".equals(tempString))
		{
			if (eq.getChildCount() == 0)
			{
				retString = BigDecimalHelper.trimZeros(eq.getWeight().toString());
			}
			else
			{
				retString = BigDecimalHelper.trimZeros((new Float(eq.getContainedWeight().floatValue() + eq.getWeightAsDouble())).toString());
			}
		}
		else if ("ITEMWEIGHT".equals(tempString))
		{
			retString = BigDecimalHelper.trimZeros(eq.getWeight().toString());
		}
		else if ("ISTYPE".equals(tempString))
		{
			retString = (eq.isType(aTok.nextToken())) ? "TRUE" : "FALSE";
		}
		else if ("CONTENTWEIGHT".equals(tempString))
		{
			if (eq.getChildCount() == 0)
			{
				retString = "0";
			}
			else
			{
				retString = BigDecimalHelper.trimZeros((new Float(eq.getContainedWeight(true).floatValue())).toString());
			}
		}
		else if ("COST".equals(tempString))
		{
			retString = BigDecimalHelper.trimZeros(eq.getCost());
		}
		else if ("QTY".equals(tempString))
		{
			retString = BigDecimalHelper.trimZeros(Double.toString(eq.qty()));
		}
		else if ("EQUIPPED".equals(tempString) && eq.isEquipped())
		{
			retString = ("Y");
		}
		else if ("EQUIPPED".equals(tempString) && !eq.isEquipped())
		{
			retString = ("N");
		}
		else if ("CARRIED".equals(tempString))
		{
			retString = (String.valueOf(eq.numberCarried()));
		}
		else if ("CONTENTS".equals(tempString))
		{
			if (aTok.hasMoreTokens())
			{
				final String bType = aTok.nextToken();
				String aSubTag = "NAME";
				if (aTok.hasMoreTokens())
				{
					aSubTag = aTok.nextToken();
				}
				int contentsIndex;
				try
				{
					contentsIndex = Integer.parseInt(bType);
					while (aTok.hasMoreTokens())
					{
						aSubTag = aSubTag + "." + aTok.nextToken();
					}
					retString = getTokenResultFromEq(eq.getContainedByIndex(contentsIndex), aSubTag, aTok, argPC);
				}
				catch (NumberFormatException e)
				{
					retString = (eq.getContainerByType(bType, aSubTag));
				}
			}
			else
			{
				retString = (eq.getContainerContentsString());
			}
		}
		else if ("CONTENTSNUM".equals(tempString))
		{
			retString = String.valueOf(eq.getContents().size());
		}
		else if ("LOCATION".equals(tempString))
		{
			retString = (eq.getParentName());
		}
		else if ("ACMOD".equals(tempString))
		{
			retString = (eq.getACMod().toString());
		}
		else if ("MAXDEX".equals(tempString))
		{
			retString = (eq.getMaxDex().toString());
		}
		else if ("ACCHECK".equals(tempString))
		{
			retString = (eq.acCheck().toString());
		}
		else if ("EDR".equals(tempString))
		{
			retString = (eq.eDR().toString());
		}
		else if ("MOVE".equals(tempString))
		{
			retString = (eq.moveString());
		}
		else if ("TYPE".equals(tempString))
		{
			retString = (eq.getType());
		}
		else if (tempString.startsWith("TYPE") && tempString.length() > 4)
		{
			final int x = Integer.parseInt(tempString.substring(4));
			retString = (eq.typeIndex(x));
		}
		else if ("SPELLFAILURE".equals(tempString))
		{
			retString = (eq.spellFailure().toString());
		}
		else if ("SIZE".equals(tempString))
		{
			retString = eq.getSize();
		}
		else if ("SIZELONG".equals(tempString))
		{
			retString = SystemCollections.getSizeAdjustmentAtIndex(Globals.sizeInt(eq.getSize())).getName();
		}
		else if ("DAMAGE".equals(tempString))
		{
			retString = getDamage(argPC, eq);
		}
		else if ("CRITRANGE".equals(tempString))
		{
			retString = eq.getCritRange();
		}
		else if ("CRITMULT".equals(tempString))
		{
			retString = eq.getCritMult();
		}
		else if ("ALTDAMAGE".equals(tempString))
		{
			retString = eq.getAltDamage();
		}
		else if ("ALTCRIT".equals(tempString))
		{
			retString = eq.getAltCritMult();
		}
		else if ("ALTCRITMULT".equals(tempString))
		{
			retString = eq.getAltCritMult();
		}
		else if ("ALTCRITRANGE".equals(tempString))
		{
			retString = eq.getAltCritRange();
		}
		else if ("RANGE".equals(tempString))
		{
			retString = eq.getRange().toString();
		}
		else if ("ATTACKS".equals(tempString))
		{
			retString = String.valueOf(eq.bonusTo("COMBAT", "ATTACKS", true));
		}
		else if ("PROF".equals(tempString))
		{
			retString = eq.profName();
		}
		else if ("SPROP".equals(tempString))
		{
			retString = eq.getSpecialProperties();
		}
		else if ("CHARGES".equals(tempString))
		{
			final int charges = eq.getRemainingCharges();
			if (charges >= 0)
			{
				retString = Integer.toString(charges);
			}
		}
		else if ("CHARGESUSED".equals(tempString))
		{
			final int charges = eq.getUsedCharges();
			if (charges >= 0)
			{
				retString = Integer.toString(charges);
			}
		}
		else if ("MAXCHARGES".equals(tempString))
		{
			final int charges = eq.getMaxCharges();
			if (charges > 0)
			{
				retString = Integer.toString(charges);
			}
		}

		return retString;
	}

	/**
	 * Replaces equipment tokens.
	 * @param argToken
	 * @return
	 */
	private static String replaceTokenEq(String argToken, PlayerCharacter argPC, boolean argExistsOnly)
	{

		List aList = new ArrayList();
		String retString = "";
		Equipment eq;
		StringTokenizer aTok;
		String aType;
		int temp = -1;

		// Starting EQ.%.NAME.MAGIC,befTrue,aftTrue,befFalse,aftFalse reading
		String bFilter = "", befTrue = "", aftTrue = "", befFalse = "", aftFalse = "";
		StringTokenizer bTok = new StringTokenizer(argToken, "~");
		if (bTok.countTokens() == 5 || bTok.countTokens() == 3)
		{
			bFilter = bTok.nextToken();
			befTrue = bTok.nextToken();
			aftTrue = bTok.nextToken();
			if (bTok.hasMoreTokens())
			{
				befFalse = bTok.nextToken();
				aftFalse = bTok.nextToken();
			}
			argToken = argToken.substring(0, bFilter.lastIndexOf('.'));
		}
		bTok = new StringTokenizer(bFilter, ".");
		boolean if_detected = false;
		while (bTok.hasMoreTokens())
		{
			String bString = bTok.nextToken();
			if ("IF".equals(bString))
			{
				if_detected = true;
			}
			else
			{
				if (if_detected)
				{
					bFilter = bFilter + "." + bString;
				}
				else
				{
					bFilter = bString;
				}
			}
		}

		if (argToken.startsWith("EQTYPE"))
		{
			aTok = new StringTokenizer(argToken.substring(7), ".", false);
			aType = aTok.nextToken();

			// check to see how we are merging equipment
			int merge = Constants.MERGE_ALL;
			if (aType.indexOf("MERGE") >= 0)
			{
				merge = returnMergeType(aType);
				aType = aTok.nextToken();
			}

			if ("Container".equals(aType))
			{
				aList.clear();
				Equipment anEquip;
				for (Iterator e = argPC.getEquipmentListInOutputOrder(merge).iterator(); e.hasNext();)
				{
					anEquip = (Equipment) e.next();
					if (anEquip.acceptsChildren())
					{
						aList.add(anEquip);
					}
				}

			}
			else
			{
				aList = argPC.getEquipmentOfTypeInOutputOrder(aType, 3, merge);
			}
		}
		else
		{
			// EQ.blah.blah
			aTok = new StringTokenizer(argToken.substring(3), ".", false);
			aType = aTok.nextToken();

			int merge = Constants.MERGE_ALL;
			if (aType.indexOf("MERGE") >= 0)
			{
				merge = returnMergeType(aType);
				aType = aTok.nextToken();
			}

			for (Iterator e = argPC.getEquipmentListInOutputOrder(merge).iterator(); e.hasNext();)
			{
				eq = (Equipment) e.next();
				aList.add(eq);
			}
		}

		//Begin Not code...
		while (aTok.hasMoreTokens())
		{
			if ("NOT".equalsIgnoreCase(aType))
			{
				aList = new ArrayList(PlayerCharacter.removeEqType(aList, aTok.nextToken()));
			}
			else if ("ADD".equalsIgnoreCase(aType))
			{
				aList = new ArrayList(argPC.addEqType(aList, aTok.nextToken()));
			}
			else if ("IS".equalsIgnoreCase(aType))
			{
				aList = new ArrayList(PlayerCharacter.removeNotEqType(aList, aTok.nextToken()));
			}
			else
			{
				// In the end of the above, bString would
				// be valid token, that should go into temp.
				try
				{
					temp = Integer.parseInt(aType);
				}
				catch (NumberFormatException exc)
				{
					// not an error!
				}
			}
			if (temp >= 0)
			{
				break;
			}
			else
			{
				aType = aTok.nextToken();
			}
		}
		//End Not code...
		// you add a NOT.type into either the EQTYPE or EQ tokens...
		// Thus to get all EQ except coins:
		//  EQ.NOT.Coin.%.LONGNAME
		// You can use more than 1 NOT, but each needs to be prepended
		// by a NOT, ie:
		//  NOT.Coin.NOT.Gem
		// Using the ADD:Type keyword adds that type into the list:
		//  EQTYPE.Coin.ADD.Gem.%.Longname
		// includes all Coins and all Gems.
		// WARNING: stuff added in will not be in alphabetical order

		final String tempString = aTok.nextToken();
		eq = null;
		Iterator setIter;
		if (temp >= 0 && temp < aList.size())
		{
			setIter = aList.iterator();
			for (int count = temp; count > 0; --count, setIter.next())
			{
				//Deliberately empty body
			}
			eq = (Equipment) setIter.next();
		}
		if (argExistsOnly && (temp < 0 || temp >= aList.size() - 1))
		{
			noMoreItems = true;
		}

		if (eq != null)
		{
			retString = getTokenResultFromEq(eq, tempString, aTok, argPC);
			// Starting EQ.%.NAME.MAGIC,befTrue,aftTrue,befFalse,aftFalse treatment
			if (!"".equals(bFilter))
			{
				aTok = new StringTokenizer(bFilter, ".");
				boolean result = false;
				boolean and_operation = false;
				while (aTok.hasMoreTokens())
				{
					String bString = aTok.nextToken();
					if ("AND".equals(bString))
					{
						and_operation = true;
					}
					else if ("OR".equals(bString))
					{
						and_operation = false;
					}
					else
					{
						if (and_operation)
						{
							result = (result && eq.isType(bString));
						}
						else
						{
							result = (result || eq.isType(bString));
						}
					}

				}

				if (result)
				{
					retString = befTrue + retString + aftTrue;
				}
				else
				{
					retString = befFalse + retString + aftFalse;
				}
			}
		}
		return retString;
	}

	/**
	 * Check to see how we are merging equipment.
	 * @param aType
	 * @return
	 */
	private static int returnMergeType(String aType)
	{
		int merge = Constants.MERGE_ALL;
		if ("MERGENONE".equals(aType))
		{
			merge = Constants.MERGE_NONE;
		}
		else if ("MERGELOC".equals(aType))
		{
			merge = Constants.MERGE_LOCATION;
		}
		else if ("MERGEALL".equals(aType))
		{
			merge = Constants.MERGE_ALL;
		}
		return merge;
	}

	private void replaceTokenLanguages(String aString, BufferedWriter output)
	{
		//New token syntax - LANGUAGES.x instead of LANGUAGESx
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
//		String fString = aTok.nextToken(); //TODO: Should this really be thrown away?
		aTok.nextToken(); // Throwing LANGUAGES away
		int languageNo;
		if (aTok.hasMoreTokens())
		{
			languageNo = Integer.parseInt(aTok.nextToken());
		}
		else
		{
			//When removing old syntax, remove the if and leave the else
			if (aString.length() > 9)
			{
				Logging.errorPrint("Old syntax LANGUAGESx will be replaced for LANGUAGES.x");

				languageNo = Integer.parseInt(aString.substring(9));
			}
			else
			{
				int c = 0;
				for (Iterator setIter = aPC.getLanguagesList().iterator(); setIter.hasNext();)
				{
					if (c > 0)
					{
						FileAccess.write(output, ", ");
					}
					FileAccess.encodeWrite(output, setIter.next().toString());
					++c;
				}
				// When removing old syntax, this return can be removed.
				return;
			}
		}
		// When removing old syntax, this code to the end can be moved to inside the above if
		final SortedSet aSet = aPC.getLanguagesList();

		if (languageNo >= 0 && languageNo < aSet.size())
		{
			FileAccess.encodeWrite(output, aSet.toArray()[languageNo].toString());
		}
		else if (existsOnly)
		{
			noMoreItems = true;
		}
	}

	private int replaceTokenSpecialAbility(String aString, BufferedWriter output)
	{
		final int len;
		int specialability = -1;
		String sDelim = "\r\n";
		//New token syntax - SPECIALABILITY.x instead of SPECIALABILITYx
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		String fString = aTok.nextToken();
		String subToken = "";
		// When removing old syntax, remove the else part
		if ("SPECIALABILITY".equals(fString))
		{
			while (aTok.hasMoreTokens())
			{
				if (specialability == -1)
				{
					specialability = Integer.parseInt(aTok.nextToken());
				}
				else if ("".equals(subToken))
				{
					subToken = aTok.nextToken();
				}
				else
				{
					sDelim = aTok.nextToken();
				}
			}
		}
		else
		{
			Logging.errorPrint("Old syntax SPECIALABILITYx will be replaced for SPECIALABILITY.x");

			if (aString.indexOf(".DESCRIPTION.") >= 0)
			{
				sDelim = aString.substring(aString.indexOf(".DESCRIPTION.") + 13);
			}

			if (aString.indexOf(".DESCRIPTION") >= 0)
			{
				subToken = "DESCRIPTION";
				specialability = Integer.parseInt(aString.substring(14, aString.indexOf(".DESCRIPTION")));
			}
			else
			{
				specialability = Integer.parseInt(aString.substring(14, aString.length()));
			}
		}

		// Is there a reason for this? Why don't set nomoreitens?
		if (specialability >= aPC.getSpecialAbilityTimesList().size() && existsOnly)
		{
		}
		len = aPC.getSpecialAbilityTimesList().size();
		if (specialability >= 0 && specialability < len)
		{
//			if (aString.indexOf(".DESCRIPTION") >= 0)
			if ("DESCRIPTION".equals(subToken))
			{
				replaceWithDelimiter(output, getItemDescription("SA", aPC.getSpecialAbilityTimesList().get(specialability).toString(), ""), sDelim);
			}
			else
			{
				FileAccess.encodeWrite(output, aPC.getSpecialAbilityTimesList().get(specialability).toString());
			}
		}
		return len;
	}

	private int replaceTokenSpecialList(String aString, BufferedWriter output)
	{
		final int len;
		String delim = aString.substring(11);
		if ("".equals(delim))
		{
			delim = ", ";
		}
		int i = 0;
		len = aPC.getSpecialAbilityTimesList().size();
		for (Iterator e = aPC.getSpecialAbilityTimesList().iterator(); e.hasNext();)
		{
			if (i++ > 0)
			{
				FileAccess.write(output, delim);
			}
			FileAccess.write(output, (String) e.next());
		}
		return len;
	}

	private void replaceTokenSkillListMods(BufferedWriter output)
	{
		int i = 0;
		for (Iterator e = aPC.getSkillListInOutputOrder().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill) e.next();
			int modSkill = -1;
			if (aSkill.getKeyStat().compareToIgnoreCase(Constants.s_NONE) != 0)
			{
				modSkill = aSkill.modifier().intValue() - aPC.getStatList().getStatModFor(aSkill.getKeyStat());
			}
			if (aSkill.getTotalRank().intValue() > 0 || modSkill > 0)
			{
				final int temp = aSkill.modifier().intValue() + aSkill.getTotalRank().intValue();
				if (i > 0)
				{
					FileAccess.write(output, ", ");
				}
				//FileAccess.write(output, aSkill.getName() + " +" + Integer.toString(temp));
				FileAccess.encodeWrite(output, aSkill.getOutputName() + " +" + Integer.toString(temp));
				++i;
			}
		}
	}

	private void replaceTokenDeity(String aString, BufferedWriter output)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".", false);
		String subTag = "OUTPUTNAME";
		Deity aDeity = aPC.getDeity();
		if (aTok.countTokens() > 1)
		{
			aTok.nextToken();
			subTag = aTok.nextToken();
		}
		if ("NAME".equals(subTag))
		{
			FileAccess.encodeWrite(output, aDeity.getName());
		}
		else if ("OUTPUTNAME".equals(subTag))
		{
			FileAccess.encodeWrite(output, aDeity.getOutputName());
		}
		else if ("DOMAINLIST".equals(subTag))
		{
			final List dList = aDeity.getDomainList();
			int x = 0;
			for (Iterator e = dList.iterator(); e.hasNext();)
			{
				if (x++ > 0)
				{
					FileAccess.write(output, ", ");
				}
				FileAccess.write(output, ((Domain) e.next()).getName());
			}
		}
		else if ("FOLLOWERALIGNMENT".equals(subTag))
		{
			final String fAlignment = aDeity.getFollowerAlignments();
			for (int i = 0; i < fAlignment.length(); ++i)
			{
				if (i > 0)
				{
					FileAccess.write(output, ", ");
				}
				FileAccess.encodeWrite(output, SystemCollections.getShortAlignmentAtIndex((int) fAlignment.charAt(i) - 48));
			}
		}
		else if ("ALIGNMENT".equals(subTag))
		{
			FileAccess.encodeWrite(output, aDeity.getAlignment());
		}
		else if ("DESCRIPTION".equals(subTag))
		{
			FileAccess.encodeWrite(output, aDeity.getDescription());
		}
		else if ("HOLYITEM".equals(subTag))
		{
			FileAccess.encodeWrite(output, aDeity.getHolyItem());
		}
		else if ("FAVOREDWEAPON".equals(subTag))
		{
			FileAccess.encodeWrite(output, aDeity.getFavoredWeapon());
		}
		else if ("PANTHEONLIST".equals(subTag))
		{
			FileAccess.encodeWrite(output, Utility.join(aDeity.getPantheonList(), ", "));
		}
		else if ("SOURCE".equals(subTag))
		{
			FileAccess.encodeWrite(output, aDeity.getSource());
		}
		else if ("SA".equals(subTag))
		{
			if (aDeity.getSpecialAbilityList() != null)
			{
				FileAccess.encodeWrite(output, Utility.join(aDeity.getSpecialAbilityList(), ", "));
			}
		}

	}

	private void replaceTokenCheck(String aString, BufferedWriter output)
	{
		String tString = "";
		aString = aString.substring(6);
		final StringTokenizer aTok = new StringTokenizer(aString, ".", false);
		String bString = aTok.nextToken(); // name of the Check
		try
		{
			final int i = Integer.parseInt(bString);
			if (i >= 0 && i < SystemCollections.getUnmodifiableCheckList().size())
			{
				bString = SystemCollections.getUnmodifiableCheckList().get(i).toString();
			}
		}
		catch (NumberFormatException e)
		{
			// just means it's a name, not a number
		}
		while (aTok.hasMoreTokens())
		{
			if (tString.length() > 0)
			{
				tString += ".";
			}
			tString += aTok.nextToken();
		}
		if ("".equals(tString))
		{
			tString = "TOTAL";
		}
		if ("NAME".equals(tString))
		{
			FileAccess.write(output, bString);
		}
		else
		{
			FileAccess.write(output,
				Delta.toString(aPC.calculateSaveBonus(1, bString, tString)));
		}
	}

	private void replaceTokenForDfor(String aString, BufferedWriter output)
	{
		int x = 0;
		int i = 0;
		StringTokenizer aTok;
		if (aString.startsWith("DFOR."))
		{
			aTok = new StringTokenizer(aString.substring(5), ",", false);
		}
		else
		{
			aTok = new StringTokenizer(aString.substring(4), ",", false);
		}
		int cMin = 0;
		int cMax = 100;
		int cStep = 1;
		int cStepLine = 1;
		int cStepLineMax = 0;
		String bString;
		String cString = "";
		String cStartLineString = "";
		String cEndLineString = "";
		boolean isDFor = false;
		while (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
			switch (i++)
			{
				case 0:
					cMin = getVarValue(bString);
					break;
				case 1:
					cMax = getVarValue(bString);
					break;
				case 2:
					cStep = getVarValue(bString);
					if (aString.startsWith("DFOR."))
					{
						isDFor = true;
						bString = aTok.nextToken();
						cStepLineMax = getVarValue(bString);
						bString = aTok.nextToken();
						cStepLine = getVarValue(bString);
					}
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
					existsOnly = (!"0".equals(bString));
					if ("2".equals(bString))
					{
						checkBefore = true;
					}
					break;
				default:
					Logging.errorPrint("ExportHandler.replaceTokenForDfor can't handle token number " + i);
					break;
			}
		}

		if ("COMMA".equals(cStartLineString))
		{
			cStartLineString = ",";
		}
		if ("COMMA".equals(cEndLineString))
		{
			cEndLineString = ",";
		}
		if ("NONE".equals(cStartLineString))
		{
			cStartLineString = "";
		}
		if ("NONE".equals(cEndLineString))
		{
			cEndLineString = "";
		}
		if ("CRLF".equals(cStartLineString))
		{
			cStartLineString = Constants.s_LINE_SEP;
		}
		if ("CRLF".equals(cEndLineString))
		{
			cEndLineString = Constants.s_LINE_SEP;
		}
		int iStart = cMin;
		int iNow;
		while (iStart < cMax)
		{
			if (x++ == 0)
			{
				FileAccess.write(output, cStartLineString);
			}
			iNow = iStart;
			if (!isDFor)
			{
				cStepLineMax = iNow + cStep;
			}
			if ((cStepLineMax > cMax) && !isDFor)
			{
				cStepLineMax = cMax;
			}
			while (iNow < cStepLineMax || (isDFor && iNow < cMax))
			{
				aTok = new StringTokenizer(cString, csheetTag2, false);
				int j = 0;
				while (aTok.hasMoreTokens())
				{
					String eString = aTok.nextToken();
					String fString;
					String gString = "";
					String hString = eString;
					int index = 0;
					while (hString.indexOf('%', index) > 0)
					{
						index = hString.indexOf('%', index);
						if (index == -1)
						{
							break;
						}
						if (index < hString.length() - 1 && hString.charAt(index + 1) != '.')
						{
							index++;
							continue;
						}
						fString = hString.substring(0, index);
						if (index + 1 < eString.length())
						{
							gString = hString.substring(index + 1);
						}
						hString = fString + Integer.toString(iNow) + gString;
					}
// Modified to allow multiple uses of %
//					int index = eString.lastIndexOf('%');
//					if (index < eString.length() - 1 && eString.charAt(index + 1) != '.')
//					{
//						index = -1;
//					}
//					if (index > -1)
//					{
//						fString = eString.substring(0, index);
//						if (index + 1 < eString.length())
//						{
//							gString = eString.substring(index + 1);
//						}
//						hString = fString + Integer.toString(iNow) + gString;
//					}
//
					if ("%0".equals(eString) || "%1".equals(eString))
					{
						final int cInt = iNow + Integer.parseInt(eString.substring(1));
						FileAccess.write(output, Integer.toString(cInt));
					}
					else
					{
						replaceToken(hString, output);
					}
					if (checkBefore && noMoreItems)
					{
						iNow = cMax;
						iStart = cMax;
						if (j == 0)
						{
							existsOnly = false;
						}
						break;
					}
					++j;
				}
				iNow += cStepLine;
				if (cStepLine == 0)
				{
					break;
				}
			}
			if ((cStepLine > 0) || (cStepLine == 0 && x == cStep) || (existsOnly == noMoreItems))
			{
				FileAccess.write(output, cEndLineString);
				x = 0;
				if (existsOnly && noMoreItems)
				{
					return;
				}
			}
			iStart += cStep;
		}
	}

	private void replaceTokenEqContainer(String aString, BufferedWriter output)
	{
		final List aList = new ArrayList();
		String indentSymbol = "\t";
		final StringTokenizer aTok = new StringTokenizer(aString, ".", false);
		String tempString = aTok.nextToken();
		//New token syntax - EQCONTAINER.x instead of EQCONTAINERx
		// When removing old syntax, remove the else
		if ("EQCONTAINER".equals(tempString) ||
			"EQCONTAINERS".equals(tempString) ||
			"EQCONTAINERW".equals(tempString))
		{
			if (tempString.equals("EQCONTAINERW"))
			{
				indentSymbol = "&nbsp&nbsp";
			}
			tempString = aTok.nextToken();
		}
		else
		{
			Logging.errorPrint("Old syntax EQCONTAINERx will be replaced for EQCONTAINER.x");

			if (aString.startsWith("EQCONTAINERW"))
			{
				indentSymbol = "&nbsp&nbsp";
				tempString = tempString.substring(12);
			}
			else if (aString.startsWith("EQCONTAINERS"))
			{
				tempString = tempString.substring(12);
			}
			else
			{
				tempString = tempString.substring(11);
			}
		}

		for (Iterator e = aPC.getEquipmentListInOutputOrder().iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.getChildCount() > 0 && eq.getUberParent().equals(eq))
			{
				aList.add(eq);
				generateContainerList(aList);
			}
		}
		final int eqcontainer = Integer.parseInt(tempString);
		tempString = aTok.nextToken();
		Equipment eq = null;
		if (eqcontainer >= 0 && eqcontainer < aList.size())
		{
			final Iterator setIter = aList.iterator();
			for (int count = eqcontainer; count > 0; --count, setIter.next())
			{
				//Deliberately empty body
			}
			eq = (Equipment) setIter.next();
		}
		if (existsOnly && (eqcontainer < 0 || eqcontainer >= aList.size() - 1))
		{
			noMoreItems = true;
		}
		if (eq != null)
		{
			if ("LONGNAME".equals(tempString))
			{
				int depth = eq.itemDepth();
				while (depth > 0)
				{
					FileAccess.write(output, indentSymbol);
					--depth;
				}
				FileAccess.encodeWrite(output, eq.longName());
			}
			else if ("NAME".equals(tempString))
			{
				//FileAccess.encodeWrite(output, eq.getName());
				FileAccess.encodeWrite(output, eq.getOutputName());
			}
			else if ("OUTPUTNAME".equals(tempString))
			{
				FileAccess.encodeWrite(output, eq.getOutputName());
			}
			else if ("WT".equals(tempString) || "TOTALWEIGHT".equals(tempString))
			{
				if (eq.getChildCount() == 0)
				{
					FileAccess.write(output, BigDecimalHelper.trimZeros(eq.getWeight().toString()));
				}
				else
				{
					FileAccess.write(output, BigDecimalHelper.trimZeros((new Float(eq.getContainedWeight().floatValue() + eq.getWeightAsDouble())).toString()));
				}
			}
			else if ("ITEMWEIGHT".equals(tempString))
			{
				FileAccess.write(output, BigDecimalHelper.trimZeros(eq.getWeight().toString()));
			}
			else if ("CONTENTWEIGHT".equals(tempString))
			{
				if (eq.getChildCount() == 0)
				{
					FileAccess.write(output, "0");
				}
				else
				{
					FileAccess.write(output, BigDecimalHelper.trimZeros((new Float(eq.getContainedWeight(true).floatValue())).toString()));
				}
			}
			else if ("COST".equals(tempString))
			{
				FileAccess.write(output, BigDecimalHelper.trimZeros(eq.getCost()));
			}
			else if ("QTY".equals(tempString))
			{
				FileAccess.write(output, BigDecimalHelper.trimZeros(Double.toString((eq.qty()))));
			}
			else if ("EQUIPPED".equals(tempString) && eq.isEquipped())
			{
				FileAccess.write(output, "Y");
			}
			else if ("EQUIPPED".equals(tempString) && !eq.isEquipped())
			{
				FileAccess.write(output, "N");
			}
			else if ("CARRIED".equals(tempString))
			{
				FileAccess.write(output, String.valueOf(eq.numberCarried()));
			}
			else if ("CONTENTS".equals(tempString))
			{
				if (aTok.hasMoreTokens())
				{
					final String aType = aTok.nextToken();
					String aSubTag = "NAME";
					if (aTok.hasMoreTokens())
					{
						aSubTag = aTok.nextToken();
					}
					FileAccess.encodeWrite(output, eq.getContainerByType(aType, aSubTag));
				}
				else
				{
					FileAccess.encodeWrite(output, eq.getContainerContentsString());
				}
			}
			else if ("LOCATION".equals(tempString))
			{
				FileAccess.write(output, eq.getParentName());
			}
			else if ("ACMOD".equals(tempString))
			{
				FileAccess.write(output, eq.getACMod().toString());
			}
			else if ("MAXDEX".equals(tempString))
			{
				FileAccess.write(output, eq.getMaxDex().toString());
			}
			else if ("ACCHECK".equals(tempString))
			{
				FileAccess.write(output, eq.acCheck().toString());
			}
			else if ("EDR".equals(tempString))
			{
				FileAccess.write(output, eq.eDR().toString());
			}
			else if ("MOVE".equals(tempString))
			{
				FileAccess.write(output, eq.moveString());
			}
			else if ("TYPE".equals(tempString))
			{
				if (aTok.hasMoreTokens())
				{
					final int x = Integer.parseInt(aTok.nextToken());
					FileAccess.write(output, eq.typeIndex(x));
				}
				else
				{
					FileAccess.write(output, eq.getType());
				}
			}
			// When removing old syntax, remove this option
			else if (tempString.startsWith("TYPE") && tempString.length() > 4)
			{
				final int x = Integer.parseInt(tempString.substring(4));
				FileAccess.write(output, eq.typeIndex(x));
			}
			else if ("SPELLFAILURE".equals(tempString))
			{
				FileAccess.write(output, eq.spellFailure().toString());
			}
			else if ("SIZE".equals(tempString))
			{
				FileAccess.write(output, eq.getSize());
			}
			else if ("DAMAGE".equals(tempString))
			{
				FileAccess.write(output, getDamage(aPC, eq));
			}
			else if ("CRITRANGE".equals(tempString))
			{
				FileAccess.write(output, eq.getCritRange());
			}
			else if ("CRITMULT".equals(tempString))
			{
				FileAccess.write(output, eq.getCritMult());
			}
			else if ("ALTDAMAGE".equals(tempString))
			{
				FileAccess.write(output, eq.getAltDamage());
			}
			else if ("ALTCRIT".equals(tempString))
			{
				FileAccess.write(output, eq.getAltCritMult());
			}
			else if ("RANGE".equals(tempString))
			{
				FileAccess.write(output, eq.getRange().toString());
			}
			else if ("ATTACKS".equals(tempString))
			{
				FileAccess.write(output, String.valueOf(eq.bonusTo("COMBAT", "ATTACKS", true)));
			}
			else if ("PROF".equals(tempString))
			{
				FileAccess.encodeWrite(output, eq.profName());
			}
			else if ("SPROP".equals(tempString))
			{
				FileAccess.encodeWrite(output, eq.getSpecialProperties());
			}
		}
	}

	/**
	 * select weapons
	 * possible tokens are:
	 *
	 * WEAPONPx.property
	 * WEAPONOx.property
	 * WEAPONHx.property
	 *
	 * WEAPONx.property
	 * WEAPON.x.property
	 * WEAPON.ALLx.property
	 * WEAPON.EQUIPPEDx.property
	 * WEAPON.NOT_EQUIPPEDx.property
	 * WEAPON.CARRIEDx.property
	 * WEAPON.NOT_CARRIEDx.property

	 * Merge Token Syntax:
	 * .MERGENONE
	 * .MERGELOC
	 * .MERGEALL
	 * merge tokens must be added after the WEAPON.
	 * ie: WEAPON.MERGENONE.EQUIPPED.X

	 * Token Syntax:
	 * <WEAPON DEFINITION>.<PROPERTY>
	 *
	 * <WEAPON DEFINITION>
	 * WEAPONP
	 * WEAPONO
	 * WEAPONH
	 * WEAPON.X
	 * WEAPON.ALL.X
	 * WEAPON.EQUIPPED.X
	 * WEAPON.NOT_EQUIPPED.X
	 * WEAPON.CARRIED.X
	 * WEAPON.NOT_CARRIED.X
	 *
	 * <PROPERTY>
	 * <NULL> = NAME
	 * AMMUNITION
	 * AMMUNITION.x.<PROPERTY>
	 * CONTENTS
	 * CONTENTS.x.<PROPERTY>
	 * RANGELIST.x.<PROPERTY>
	 */
	private void replaceTokenWeapon(String aString, BufferedWriter output)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".", false);

		final int ALL = 0;
		final int EQUIPPED = 1;
		final int NOT_EQUIPPED = 2;
		final int CARRIED = 3;
		final int NOT_CARRIED = 4;

		int equipped = ALL;

		int merge = Constants.MERGE_ALL;

		final int PRIMARY = -1;
		final int OFFHAND = -2;
		final int UNARMED = -3;
		int weapon;

		aString = aTok.nextToken();
		if ("WEAPONP".equals(aString))
		{
			weapon = PRIMARY; // primary
		}
		else if ("WEAPONO".equals(aString))
		{
			weapon = OFFHAND; // off-hand
		}
		else if ("WEAPONH".equals(aString))
		{
			weapon = UNARMED; // unarmed
		}
		else if ("WEAPON".equals(aString))
		{
			aString = aTok.nextToken();

			// First check to see if there is a MERGE token
			if (aString.equals("MERGENONE"))
			{
				merge = Constants.MERGE_NONE;
				aString = aTok.nextToken();
			}
			else if (aString.equals("MERGELOC"))
			{
				merge = Constants.MERGE_LOCATION;
				aString = aTok.nextToken();
			}
			else if (aString.equals("MERGEALL"))
			{
				merge = Constants.MERGE_ALL;
				aString = aTok.nextToken();
			}

			// New token syntax - WEAPON.x instead of WEAPONx
			// All of these IFs need to be changed to equals when removing old token syntax
			if (aString.startsWith("ALL"))
			{
				// When removing old syntax, remove the try and leave the catch part
				try
				{
					weapon = Integer.parseInt(aString.substring(3));

					Logging.errorPrint("Old syntax ALLx will be replaced for ALL.x");
				}
				catch (NumberFormatException exc)
				{
					weapon = Integer.parseInt(aTok.nextToken());
				}
				equipped = ALL;
			}
			else if (aString.startsWith("EQUIPPED"))
			{
				// When removing old syntax, remove the try and leave the catch part
				try
				{
					weapon = Integer.parseInt(aString.substring(8));

					Logging.errorPrint("Old syntax EQUIPPEDx will be replaced for EQUIPPED.x");
				}
				catch (NumberFormatException exc)
				{
					weapon = Integer.parseInt(aTok.nextToken());
				}
				equipped = EQUIPPED;
			}
			else if (aString.startsWith("NOT_EQUIPPED"))
			{
				// When removing old syntax, remove the try and leave the catch part
				try
				{
					weapon = Integer.parseInt(aString.substring(12));

					Logging.errorPrint("Old syntax NOT_EQUIPPEDx will be replaced for NOT_EQUIPPED.x");
				}
				catch (NumberFormatException exc)
				{
					weapon = Integer.parseInt(aTok.nextToken());
				}
				equipped = NOT_EQUIPPED;
			}
			else if (aString.startsWith("CARRIED"))
			{
				// When removing old syntax, remove the try and leave the catch part
				try
				{
					weapon = Integer.parseInt(aString.substring(7));

					Logging.errorPrint("Old syntax CARRIEDx will be replaced for CARRIED.x");
				}
				catch (NumberFormatException exc)
				{
					weapon = Integer.parseInt(aTok.nextToken());
				}
				equipped = CARRIED;
			}
			else if (aString.startsWith("NOT_CARRIED"))
			{
				// When removing old syntax, remove the try and leave the catch part
				try
				{
					weapon = Integer.parseInt(aString.substring(11));

					Logging.errorPrint("Old syntax NOT_CARRIEDx will be replaced for NOT_CARRIED.x");
				}
				catch (NumberFormatException exc)
				{
					weapon = Integer.parseInt(aTok.nextToken());
				}
				equipped = NOT_CARRIED;
			}
			else
			{
				weapon = Integer.parseInt(aString);
			}
		}
		// When removing old syntax, remove this else code and treat as appropriate WEAPON.NAME kind of stuff.
		else
		{
			weapon = Integer.parseInt(aString.substring(6));

			Logging.errorPrint("Old syntax WEAPONx will be replaced for WEAPON.x");
		}
		aString = aTok.nextToken();
		int aRange = -1;
		//New token syntax - RANGELIST.x instead of RANGELISTx
		// This IF needs to be changed to equals when removing old token syntax
		if (aString.startsWith("RANGELIST"))
		{
			// When removing old syntax, remove the try and leave the catch part
			try
			{
				aRange = Integer.parseInt(aString.substring(9));

				Logging.errorPrint("Old syntax RANGELISTx will be replaced for RANGELIST.x");
			}
			catch (NumberFormatException exc)
			{
				aRange = Integer.parseInt(aTok.nextToken());
			}
			if (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
			}
			else
			{
				aString = "RANGELIST";
			}
		}
		int aContent = -1;
		//New token syntax - CONTENTS.x instead of CONTENTSx
		// This IF needs to be changed to equals when removing old token syntax
		if (aString.startsWith("CONTENTS"))
		{
			if (aTok.hasMoreTokens())
			{
				// When removing old syntax, remove the try and leave the catch part
				try
				{
					aContent = Integer.parseInt(aString.substring(8));

					Logging.errorPrint("Old syntax CONTENTSx will be replaced for CONTENTS.x");
				}
				catch (NumberFormatException exc)
				{
					aContent = Integer.parseInt(aTok.nextToken());
				}
				// Does not make much sense now, but when the above TRY code is removed,
				// this will be needed
				if (aTok.hasMoreTokens())
				{
					aString = aTok.nextToken();
				}
				else
				{
					aString = "CONTENTS";
				}
			}
			else
			{
				// When removing old syntax, remove the else and leave the if part
				if ("CONTENTS".equals(aString))
				{
					aString = "CONTENTSCOUNT";
				}
				else
				{
					try
					{
						aContent = Integer.parseInt(aString.substring(8));

						Logging.errorPrint("Old syntax CONTENTSx will be replaced for CONTENTS.x");
					}
					catch (NumberFormatException exc)
					{
						aContent = Integer.parseInt(aTok.nextToken());
					}
					aString = "CONTENTS";
				}
			}
		}
		int aAmmo = -1;
		//New token syntax - AMMUNITION.x instead of AMMUNITIONx
		// This IF needs to be changed to equals when removing old token syntax
		if (aString.startsWith("AMMUNITION"))
		{
			if (aTok.hasMoreTokens())
			{
				// When removing old syntax, remove the try and leave the catch part
				try
				{
					aAmmo = Integer.parseInt(aString.substring(10));

					Logging.errorPrint("Old syntax AMMUNITIONx will be replaced for AMMUNITION.x");
				}
				catch (NumberFormatException exc)
				{
					aAmmo = Integer.parseInt(aTok.nextToken());
				}
				// Does not make much sense now, but when the above TRY code is removed,
				// this will be needed
				if (aTok.hasMoreTokens())
				{
					aString = aTok.nextToken();
				}
				else
				{
					aString = "AMMUNITION";
				}
			}
			else
			{
				// When removing old syntax, remove the else and leave the if part
				if ("AMMUNITION".equals(aString))
				{
					aString = "AMMUNITIONCOUNT";
				}
				else
				{
					try
					{
						aAmmo = Integer.parseInt(aString.substring(10));

						Logging.errorPrint("Old syntax AMMUNITIONx will be replaced for AMMUNITION.x");
					}
					catch (NumberFormatException exc)
					{
						aAmmo = Integer.parseInt(aTok.nextToken());
					}
					aString = "AMMUNITION";
				}
			}
		}

		Equipment eq = null;
		if (weapon == PRIMARY)
		{
			//eq = primaryWeapon;
			if (!aPC.getPrimaryWeapons().isEmpty())
			{
				eq = (Equipment) aPC.getPrimaryWeapons().get(0);
			}
		}
		else if (weapon == OFFHAND)
		{
			//eq = secondaryWeapon[0];
			if (!aPC.getSecondaryWeapons().isEmpty())
			{
				eq = (Equipment) aPC.getSecondaryWeapons().get(0);
			}
		}
		else if (weapon == UNARMED)
		{
			// Creating a fake Unarmed Strike equipment so we don't need it on the lst anymore.
			eq = new Equipment();
			eq.setName("Unarmed Strike");
			eq.setOutputName("Unarmed Strike");
			eq.setTypeInfo("Weapon.Unarmed.Melee.Bludgeoning.Standard.Monk");
			eq.setCost("0", true);
			eq.setWeight("0");
			eq.setDamage("1d1");
			eq.setModifiersAllowed(false);
			eq.setModifiersRequired(false);
			eq.setSize("T", true);
		}
		else
		{
			final List aArrayList = aPC.getExpandedWeapons(merge);

			if (equipped == EQUIPPED)
			{
				// remove all weapons which are not equipped from list
				for (Iterator it = aArrayList.iterator(); it.hasNext();)
				{
					if (!((Equipment) it.next()).isEquipped())
					{
						it.remove();
					}
				}
			}
			else if (equipped == NOT_EQUIPPED)
			{
				// remove all weapons which are equipped from list
				for (Iterator it = aArrayList.iterator(); it.hasNext();)
				{
					if (((Equipment) it.next()).isEquipped())
					{
						it.remove();
					}
				}
			}
			else if (equipped == CARRIED)
			{
				// remove all weapons which are not carried from list
				for (Iterator it = aArrayList.iterator(); it.hasNext();)
				{
					if (((Equipment) it.next()).numberCarried().intValue() == 0)
					{
						it.remove();
					}
				}
			}
			else if (equipped == NOT_CARRIED)
			{
				// remove all weapons which are carried from list
				for (Iterator it = aArrayList.iterator(); it.hasNext();)
				{
					if (((Equipment) it.next()).numberCarried().intValue() > 0)
					{
						it.remove();
					}
				}
			}

			if (weapon < aArrayList.size())
			{
				eq = (Equipment) aArrayList.get(weapon);
			}
			if (weapon == aArrayList.size() - 1 && existsOnly)
			{
				noMoreItems = true;
			}
		}
		if (eq != null)
		{
			boolean isDouble = (eq.isDouble() && eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS);
			boolean isDoubleSplit = (eq.isType("Head1") || eq.isType("Head2"));
			int baseBonus;

			String profName = eq.profName();
			WeaponProf wp = Globals.getWeaponProfNamed(profName);
			if ((wp == null) && eq.isNatural())
			{
				final int idx = profName.indexOf('(');
				if (idx > 0)
				{
					profName = profName.substring(0, idx).trim();
					wp = Globals.getWeaponProfNamed(profName);
				}
			}
			if (aString.startsWith("NAME"))
			{
				boolean noStar = false;
				if (aTok.hasMoreTokens())
				{
					final String subToken = aTok.nextToken();
					if ("NOSTAR".equals(subToken))
					{
						noStar = true;
					}
				}
				if (eq.isEquipped() && !noStar)
				{
					FileAccess.write(output, "*");
				}
				//FileAccess.encodeWrite(output, eq.getName());
				FileAccess.encodeWrite(output, eq.getOutputName());
				FileAccess.encodeWrite(output, eq.getAppliedName());
			}
			else if (aString.startsWith("OUTPUTNAME"))
			{
				if (eq.isEquipped())
				{
					FileAccess.write(output, "*");
				}
				FileAccess.encodeWrite(output, eq.getOutputName());
				FileAccess.encodeWrite(output, eq.getAppliedName());
			}
			else if (aString.startsWith("LONGNAME"))
			{
				if (eq.isEquipped())
				{
					FileAccess.write(output, "*");
				}
				FileAccess.encodeWrite(output, eq.longName());
				FileAccess.encodeWrite(output, eq.getAppliedName());
			}
			else if (aString.startsWith("ATTACKS"))
			{
				FileAccess.write(output, String.valueOf(eq.bonusTo("WEAPON", "ATTACKS", true)));
			}
			else if (aString.startsWith("AMMUNITIONCOUNT"))
			{
				int ammoCount = 0;
				Equipment anEquip;
				final String containerCapacity = eq.getContainerCapacityString();
				for (Iterator e = aPC.getEquipmentListInOutputOrder().iterator(); e.hasNext();)
				{
					anEquip = (Equipment) e.next();
					for (Iterator e2 = anEquip.typeList().iterator(); e2.hasNext();)
					{
						if (containerCapacity.indexOf(e2.next().toString()) >= 0)
						{
							++ammoCount;
							break;
						}
					}
				}
				FileAccess.write(output, Integer.toString(ammoCount));
			}
			else if (aString.startsWith("AMMUNITION"))
			{
				final Equipment ammoUser = returnAmmoUser(eq, aAmmo);
				if (ammoUser != null)
				{
					FileAccess.write(output, ammoUser.getName());
				}
			}
			else if (aString.startsWith("CONTENTSCOUNT"))
			{
				FileAccess.write(output, Integer.toString(eq.getContainedEquipmentCount()));
			}
			else if (aString.startsWith("CONTENTS"))
			{
				if (aContent > -1)
				{
					if (aContent < eq.getContainedEquipmentCount())
					{
						FileAccess.write(output, eq.getContainedEquipment(aContent).getName());
					}
				}
			}
			else if (aString.startsWith("NUMATTACKS"))
			{
				replaceTokenWeaponNumAttacks(eq, output);
			}
			else if (aString.startsWith("HEFT"))
			{
				if (aPC.sizeInt() > Globals.sizeInt(eq.getSize()))
				{
					FileAccess.write(output, "LIGHT");
				}
				else if (aPC.sizeInt() == Globals.sizeInt(eq.getSize()))
				{
					FileAccess.write(output, "MEDIUM");
				}
				else
				{
					FileAccess.write(output, "HEAVY");
				}
			}
			else if (aString.startsWith("ISTYPE"))
			{
				FileAccess.write(output, (eq.isType(aTok.nextToken())) ? "TRUE" : "FALSE");
			}
			else if (aString.startsWith("CRIT"))
			{
				replaceTokenWeaponCrit(eq, output, profName, isDouble);
			}
			else if (aString.startsWith("MULT"))
			{
				final int mult = (int) aPC.getTotalBonusTo("WEAPONPROF=" + profName, "CRITMULTADD");
				int critMult;
				try
				{
					critMult = Integer.parseInt(eq.getCritMult().substring(1));
				}
				catch (NumberFormatException e)
				{
					critMult = 0;
				}

				FileAccess.write(output, String.valueOf((critMult + mult)));
				final int altCrit = eq.getAltCritMultiplier();
				if (isDouble && (altCrit > 0))
				{
					FileAccess.write(output, "/" + String.valueOf(altCrit + mult));
				}
			}
			else if (aString.startsWith("RANGELIST"))
			{
				// Doing the same acces twice. Easy rework
				final List rangeList = eq.getRangeList(true);
				if (aRange < rangeList.size())
				{
					FileAccess.encodeWrite(output, rangeList.get(aRange).toString());
				}
//				final int rangeSize = eq.getRangeList(true).size();
//				if (aRange < rangeSize)
//				{
//					FileAccess.encodeWrite(output, eq.getRangeList(true).get(aRange).toString());
//				}
			}
			else if (aString.startsWith("RANGE"))
			{
				FileAccess.encodeWrite(output, eq.getRange().toString());
				if (!aString.endsWith("NOUNITS"))
				{
					FileAccess.encodeWrite(output, Globals.getAbbrMovementDisplay());
				}
			}
			else if (aString.startsWith("SIZEMOD"))
			{
				FileAccess.encodeWrite(output, Delta.toString((int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT")));
			}
			else if (aString.startsWith("TYPE"))
			{
				FileAccess.encodeWrite(output, weaponTypes(eq));
			}
			else if (aString.startsWith("HIT") ||
				aString.startsWith("TOTALHIT") ||
				aString.startsWith("BASEHIT") ||
				aString.startsWith("TWPHITH") ||
				aString.startsWith("TWPHITL") ||
				aString.startsWith("TWOHIT") ||
				aString.startsWith("OHHIT") ||
				aString.startsWith("THHIT"))
			{
				// This defines if I should return the values based on weapon's location
				// or not. 1,2,3 and 4 overrides the actual location of the weapon
				// and calculates all data with this setting
				final int HITMODE_TOTALHIT = 0;
				final int HITMODE_BASEHIT = 1; // Assuming it's the only weapon, on primary hand
				final int HITMODE_TWPHITH = 2; // Assuming there are two weapons, this is primary and the other is not light
				final int HITMODE_TWPHITL = 3; // Assuming there are two weapons, this is primary and the other is light
				final int HITMODE_TWOHIT = 4;  // Assuming there are two weapons, this is off-hand
				final int HITMODE_OHHIT = 5;  // Assuming there is only one weapon, off-hand
				final int HITMODE_THHIT = 6;  // Assuming there is only one weapon, off-hand

				int hitMode = HITMODE_TOTALHIT;
				if (aString.startsWith("BASEHIT"))
				{
					hitMode = HITMODE_BASEHIT;
				}
				else if (aString.startsWith("TWPHITH"))
				{
					hitMode = HITMODE_TWPHITH;
				}
				else if (aString.startsWith("TWPHITL"))
				{
					hitMode = HITMODE_TWPHITL;
				}
				else if (aString.startsWith("TWOHIT"))
				{
					hitMode = HITMODE_TWOHIT;
				}
				else if (aString.startsWith("OHHIT"))
				{
					hitMode = HITMODE_OHHIT;
				}
				else if (aString.startsWith("THHIT"))
				{
					hitMode = HITMODE_THHIT;
				}

				if ((hitMode != HITMODE_TOTALHIT) && (hitMode != HITMODE_THHIT) && (Globals.isWeaponTwoHanded(aPC, eq, wp)))
				{
					FileAccess.write(output, "N/A");
					return;
				}
				String mString = aPC.getAttackString(Constants.ATTACKSTRING_MELEE, 0);
				String rString = aPC.getAttackString(Constants.ATTACKSTRING_MELEE, 0);
				if (eq.isMonk())
				{
					final String m1String = aPC.getAttackString(Constants.ATTACKSTRING_UNARMED, 0);
					if (m1String.length() > mString.length())
					{
						mString = m1String;
					}
					else if (m1String.length() == mString.length() && !mString.equals(m1String))
					{
						final StringTokenizer mTok = new StringTokenizer(mString, "+/", false);
						final StringTokenizer m1Tok = new StringTokenizer(m1String, "+/", false);
						final String msString = mTok.nextToken();
						final String m1sString = m1Tok.nextToken();
						if (Integer.parseInt(m1sString) >= Integer.parseInt(msString))
						{
							mString = m1String;
						}
					}
				}
				baseBonus = 0;
				int secondaryBonus = 0;
				int primaryBonus = 0;
				if (eq.isNatural() && (eq.getLocation() == Equipment.EQUIPPED_SECONDARY))
				{
					// all secondary natural weapons
					// attack at -5
					baseBonus = -5;

					// Unless the creature has bonus feats etc. to add to improve
					// secondary attacks, i.e. MultiAttack -- Bug #586332
					// sage_sam 26 Nov 2002
					baseBonus += aPC.getTotalBonusTo("COMBAT", "TOHIT-SECONDARY");
				}
				else if (eq.isNatural() && (eq.getLocation() == Equipment.EQUIPPED_PRIMARY))
				{
					// Primary Natural Weapons have no bonus
					// or penalty associated with secondary
					// weapons/attacks
					baseBonus = 0;
				}
				else if ((eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS) || aPC.isPrimaryWeapon(eq) || aPC.isSecondaryWeapon(eq) || (hitMode >= HITMODE_BASEHIT) || (eq.getLocation() == Equipment.EQUIPPED_TEMPBONUS))
				{
					if (isDoubleSplit && (hitMode == HITMODE_TOTALHIT))
					{
						baseBonus = 0;
					}
					else if (hitMode == HITMODE_BASEHIT || hitMode == HITMODE_THHIT)
					{
						baseBonus = 0;
					}
					else if (hitMode == HITMODE_TWPHITH)
					{
						baseBonus = -6;
					}
					else if (hitMode == HITMODE_TWPHITL || hitMode == HITMODE_OHHIT)
					{
						baseBonus = -4;
					}
					else if (hitMode == HITMODE_TWOHIT)
					{
						baseBonus = -10;
					}
					else if ((eq.getLocation() != Equipment.EQUIPPED_TWO_HANDS) && aPC.isSecondaryWeapon(eq) && !aPC.getPrimaryWeapons().isEmpty())
					{
						baseBonus = -10;
					}
					else if (aPC.isSecondaryWeapon(eq) && aPC.getPrimaryWeapons().isEmpty())
					{
						baseBonus = -4;
					}
					else if ((eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS) || !aPC.getSecondaryWeapons().isEmpty())
					{
						baseBonus = -6;
					}
					if (!isDoubleSplit && (hitMode == HITMODE_TOTALHIT) &&
						(
						(isDouble) ||
						(
						(eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS) &&
						Globals.isWeaponLightForPC(aPC, eq)
						) ||
						(
						!aPC.getPrimaryWeapons().isEmpty() &&
						!aPC.getSecondaryWeapons().isEmpty() &&
						Globals.isWeaponLightForPC(aPC, (Equipment) aPC.getSecondaryWeapons().get(0))
						)
						)
					)
					{
						baseBonus += 2;
					}
					if ((hitMode == HITMODE_TWOHIT) &&
						(isDouble || Globals.isWeaponLightForPC(aPC, eq)))
					{
						baseBonus += 2;
					}

					if ((hitMode == HITMODE_TWOHIT) ||
						(hitMode == HITMODE_OHHIT) ||
						(
						(hitMode == HITMODE_TOTALHIT) &&
						!isDoubleSplit &&
						(eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS || (!aPC.getPrimaryWeapons().isEmpty() && aPC.isSecondaryWeapon(eq)))
						)
					)
					{
						secondaryBonus = (int) aPC.getTotalBonusTo("COMBAT", "TOHIT-SECONDARY");
						if (eq.isRanged())
						{
							secondaryBonus -= (int) aPC.getTotalBonusTo("COMBAT", "TOHIT-SECONDARY.NOTRANGED");
						}
						if (hitMode == HITMODE_OHHIT)
						{
							// If only using one weapon, Two-weapon FIghting Bonus does not apply
							// If you have TWF, you have both TOHIT-P and TOHIT-S, so remove TOHIT-P
							// TODO: Rework on this code and/or on the lst, because it "sounds" wrong
							// Felipe Diniz - 12/Feb/2003
							secondaryBonus -= (int) aPC.getTotalBonusTo("COMBAT", "TOHIT-PRIMARY");
						}
					}
					if ((hitMode == HITMODE_TWPHITH || hitMode == HITMODE_TWPHITL) ||
						(hitMode == HITMODE_TOTALHIT && !isDoubleSplit &&
						(eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS || (!aPC.getSecondaryWeapons().isEmpty() && aPC.isPrimaryWeapon(eq)))))
					{
						primaryBonus = (int) aPC.getTotalBonusTo("COMBAT", "TOHIT-PRIMARY");
						if (eq.isRanged())
						{
							primaryBonus -= (int) aPC.getTotalBonusTo("COMBAT", "TOHIT-PRIMARY.NOTRANGED");
						}
					}
				}

				// If normally cannot wield weapon 1-handed,
				// but for some reason they can (Monkey Grip)
				// then check for TOHIT modifier
				if ((eq.getLocation() == Equipment.EQUIPPED_PRIMARY) ||
					(eq.getLocation() == Equipment.EQUIPPED_SECONDARY) ||
					(eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS))
				{
					if (Globals.isWeaponOneHanded(aPC, eq, wp, false) != Globals.isWeaponOneHanded(aPC, eq, wp, true))
					{
						baseBonus += (int) aPC.getTotalBonusTo("WEAPONPROF=" + profName, "TOHITOVERSIZE");
					}
				}

				if ((hitMode != HITMODE_TWOHIT) &&
					(hitMode != HITMODE_OHHIT) &&
					(hitMode != HITMODE_BASEHIT))
				{
					baseBonus += primaryBonus;
				}

				if ((hitMode == HITMODE_TWOHIT) ||
					(hitMode == HITMODE_OHHIT) ||
					(eq.getLocation() != Equipment.EQUIPPED_TWO_HANDS && hitMode == HITMODE_TOTALHIT))
				{
					baseBonus += secondaryBonus;
				}

				boolean hasBoth = (eq.isRanged() && eq.isMelee());
				for (Iterator ei = eq.typeList().iterator(); ei.hasNext();)
				{
					final String tString = ei.next().toString();
					if (!hasBoth || !"RANGED".equalsIgnoreCase(tString))
					{
						baseBonus += (int) aPC.getTotalBonusTo("TOHIT", "TYPE." + tString);
					}
				}

				//
				// This fixes Weapon Finesse breaking on thrown weapons
				// BONUS:WEAPONPROF=%LIST|TOHIT|(STRMAXDEX+SHIELDACHECK)-STR|TYPE.NotRanged
				//
				// Dagger yields following:
				// WEAPONPROF=DAGGER.TOHIT.NOTRANGED
				//
				if ((wp != null) && eq.isRanged())
				{
					baseBonus -= (int) aPC.getTotalBonusTo("WEAPONPROF=" + profName.toUpperCase(), "TOHIT.NOTRANGED");
				}

				if (!eq.isNatural() && ((wp == null) || !aPC.hasWeaponProfNamed(profName)))
				{
					baseBonus += aPC.getNonProficiencyPenalty(); // non-proficiency penalty
				}

				baseBonus += (int) aPC.getTotalBonusTo("WEAPONPROF=" + profName, "TOHIT");

				if (aRange > -1)
				{
					final int rangeSize = eq.getRangeList(true).size();
					// at short range, add SHORTRANGE bonus
					if (aRange < rangeSize && Integer.parseInt(eq.getRangeList(true).get(aRange).toString()) <= SettingsHandler.getGame().getShortRangeDistance())
					{
						baseBonus += (int) aPC.getTotalBonusTo("COMBAT", "TOHIT-SHORTRANGE");
						baseBonus += (int) aPC.getTotalBonusTo("TOHIT", "SHORTRANGE");
						baseBonus += (int) aPC.getTotalBonusTo("WEAPONPROF=" + profName, "TOHIT-SHORTRANGE");
						baseBonus += (int) eq.bonusTo("WEAPON", "TOHIT-SHORTRANGE", true);
					}
					// Long Range To-Hit Modifier
					if (aRange < rangeSize)
					{
						final int defaultRange = Integer.parseInt(eq.getRange().toString());
						final int thisRange = Integer.parseInt(eq.getRangeList(true).get(aRange).toString());
						baseBonus -= 2 * (int) Math.max(Math.ceil(((float) thisRange / defaultRange)) - 1, 0);
					}
				}

				//Ammunition & Contents Modifier
				if (aContent > -1)
				{
					if (aContent < eq.getContainedEquipmentCount())
					{
						baseBonus += eq.getContainedEquipment(aContent).getBonusToHit(true);
					}
				}

				final Equipment ammoUser = returnAmmoUser(eq, aAmmo);
				if (ammoUser != null)
				{
					baseBonus += ammoUser.getBonusToHit(true);
				}

				// do NOT include the size bonus/penalty since
				// it is call in aPC.getTotalBonusTo()

				// include players TOHIT bonuses
				baseBonus += (int) aPC.getTotalBonusTo("TOHIT", "TOHIT");
				baseBonus += (int) aPC.getTotalBonusTo("COMBAT", "TOHIT");
				// subtract Armor and Shield non-proficiency
				baseBonus += aPC.modFromArmorOnWeaponRolls();

				// include bonuses from Item itself
				baseBonus += eq.getBonusToHit(true);

				///////////////////////////////////////
				//    all done with attack bonuses   //
				///////////////////////////////////////


				int numInt = -1;

				// When removing old syntax, remove the else from this
				if (("TOTALHIT".equals(aString) ||
					"BASEHIT".equals(aString) ||
					"TWPHITH".equals(aString) ||
					"OHHIT".equals(aString) ||
					"THHIT".equals(aString) ||
					"TWPHITL".equals(aString) ||
					"TWOHIT".equals(aString)) && aTok.hasMoreTokens())
				{
					try
					{
						numInt = Integer.parseInt(aTok.nextToken());
					}
					catch (NumberFormatException exc)
					{
						Logging.errorPrint("Wrong syntax on " + aString + ". Should be a number.");
					}
				}
				else if ((aString.startsWith("TOTALHIT") ||
					aString.startsWith("BASEHIT") ||
					aString.startsWith("TWPHITH") ||
					aString.startsWith("OHHIT") ||
					aString.startsWith("THHIT") ||
					aString.startsWith("TWPHITL") ||
					aString.startsWith("TWOHIT")) && weapon > -1)
				{

					if (aString.startsWith("TOTALHIT") && !aString.endsWith("TOTALHIT"))
					{
						Logging.errorPrint("Old syntax TOTALHITx will be replaced for TOTALHIT.x");
					}
					if (aString.startsWith("BASEHIT") && !aString.endsWith("BASEHIT"))
					{
						numInt = Integer.parseInt(aString.substring(7));
					}
					if (aString.startsWith("TWPHITH") && !aString.endsWith("TWPHITH"))
					{
						numInt = Integer.parseInt(aString.substring(7));
					}
					if (aString.startsWith("TWPHITL") && !aString.endsWith("TWPHITL"))
					{
						numInt = Integer.parseInt(aString.substring(7));
					}
					if (aString.startsWith("OHHIT") && !aString.endsWith("OHHIT"))
					{
						numInt = Integer.parseInt(aString.substring(5));
					}
					if (aString.startsWith("THHIT") && !aString.endsWith("THHIT"))
					{
						numInt = Integer.parseInt(aString.substring(5));
					}
					if (aString.startsWith("TWOHIT") && !aString.endsWith("TWOHIT"))
					{
						numInt = Integer.parseInt(aString.substring(6));
					}
					if (aString.startsWith("TOTALHIT") && !aString.endsWith("TOTALHIT"))
					{
						numInt = Integer.parseInt(aString.substring(8));
					}
				}

				//
				// BONUS:COMBAT|ATTACKS|#
				// represent extra attacks at BaB
				// such as from a weapon of 'Speed'
				int extra_attacks = (int) eq.bonusTo("WEAPON", "ATTACKS", true);
				// or possibly the "Haste" spell cast on PC
				extra_attacks += (int) aPC.getTotalBonusTo("COMBAT", "ATTACKS");

				StringTokenizer zTok = null;

				// Moved the addition of "Speed" attack to here to avoid the TOTALHITy bug
				if (eq.isMelee())
				{
					int tempExtraAttacks = extra_attacks;
					while (tempExtraAttacks-- > 0)
					{
						final StringTokenizer bTok = new StringTokenizer(mString, "/");
						mString = bTok.nextToken() + "/" + mString;
					}
					zTok = new StringTokenizer(mString, "+/", false);
				}
				else if (eq.isRanged())
				{
					int tempExtraAttacks = extra_attacks;
					while (tempExtraAttacks-- > 0)
					{
						final StringTokenizer bTok = new StringTokenizer(rString, "/");
						rString = bTok.nextToken() + "/" + rString;
					}
					zTok = new StringTokenizer(rString, "+/", false);
				}
				int x = 0;
				int max = 1 + (int) aPC.getTotalBonusTo("COMBAT", "SECONDARYATTACKS");

				if (aPC.getPrimaryWeapons().isEmpty() && hitMode == HITMODE_TOTALHIT)
				{
					max = 100;
				}
				if (!eq.isAttacksProgress() || (eq.bonusTo("WEAPON", "ATTACKSPROGRESS", true) > 0))
				{
					numInt = 0;
				}

				//
				// Trap this to avoid infinite loop
				//
				if (!eq.isMelee() && !eq.isRanged())
				{
					FileAccess.write(output, "???");
					return;
				}

				final StringBuffer primaryAttack = new StringBuffer(20);
				final StringBuffer secondaryAttack = new StringBuffer(20);
				do
				{
					int iBAB = 0;

					if ((hitMode == HITMODE_TWOHIT) && (x >= max))
					{
						break;
					}
					if (hitMode == HITMODE_TOTALHIT &&
						((eq.getLocation() != Equipment.EQUIPPED_TWO_HANDS) && aPC.isSecondaryWeapon(eq) && (x >= max)))
					{
						break;
					}

					if (zTok != null)
					{
						if (zTok.hasMoreTokens())
						{
							// base attack progress
							iBAB = Integer.parseInt(zTok.nextToken());
						}
						else
						{
							break;
						}
					}
					--numInt;
					//
					// Found the correct attack, then output the attack's "to hit"
					//
					if (numInt < 0)
					{
						final int iAtt = iBAB + baseBonus;
						if (primaryAttack.length() != 0)
						{
							primaryAttack.append('/');
						}
						primaryAttack.append(Delta.toString(iAtt));

						//
						// Here we handle extra attacks without a BAB progression
						//
						//if (eq.isNatural())
						if (!eq.isAttacksProgress() || (eq.bonusTo("WEAPON", "ATTACKSPROGRESS", true) > 0))
						{
							while (extra_attacks-- > 0 && aString.endsWith("TOTALHIT"))
							{
								primaryAttack.append('/').append(Delta.toString(iAtt));
							}
							break;
						}
						if ((x < max) && isDouble && (eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS))
						{
							if (secondaryAttack.length() != 0)
							{
								secondaryAttack.append('/');
							}
							final int iAtt2 = iBAB + baseBonus;
							secondaryAttack.append(Delta.toString(iAtt2));
						}
					}
					if (hitMode == HITMODE_TWOHIT && x == (max - 1))
					{
						break;
					}
					if (numInt < -1)
					{
						numInt = -1;
					}
					else if (numInt == -1)
					{
						numInt = -2;
					}
					++x;
					//
					// Just in case we are looping forever
					//
					if (x > 100)
					{
						break;
					}
				}
				while (numInt >= -1);

				FileAccess.write(output, primaryAttack.toString());
				if (secondaryAttack.length() != 0 && hitMode == HITMODE_TOTALHIT)
				{
					FileAccess.write(output, ';' + secondaryAttack.toString());
				}

				if (weapon == PRIMARY &&
					!aPC.getSecondaryWeapons().isEmpty() &&
					!aPC.getPrimaryWeapons().isEmpty() &&
					aPC.getPrimaryWeapons().get(0).equals(aPC.getSecondaryWeapons().get(0)))
				{
					int i = 0;
					if ("TOTALHIT".equals(aString))
					{
						StringTokenizer bTok = null;
						if (eq.isMelee())
						{
							bTok = new StringTokenizer(mString, "/", false);
						}
						else if (eq.isRanged())
						{
							bTok = new StringTokenizer(rString, "/", false);
						}
						if (bTok != null)
						{
							i += Integer.parseInt(bTok.nextToken());
						}
					}
					FileAccess.write(output, '/' + Delta.toString(i));
				}

			}
			else if (aString.startsWith("CATEGORY"))
			{
				FileAccess.write(output, weaponCategories(eq));
				FileAccess.write(output, "-");
				if (eq.isNatural())
				{
					FileAccess.write(output, "Natural");
				}
				else if (eq.isType("Both"))
				{
					if (eq.isMelee())
					{
						FileAccess.write(output, "Both (Melee)");
					}
					else if (eq.isRanged())
					{
						FileAccess.write(output, "Both (Ranged)");
					}
				}
				else if (eq.isMelee())
				{
					FileAccess.write(output, "Melee");
				}
				else if (eq.isRanged())
				{
					FileAccess.write(output, "Ranged");
				}
				else
				{
					FileAccess.write(output, "Non-Standard");
				}
			}
			else if (aString.startsWith("HAND"))
			{
				// updated 16 Dec 2002 to update locations and
				// display only key information -- sage_sam
				String location = Equipment.getLocationName(eq.getLocation());
				final int start = location.indexOf('(') + 1; // move past the paren
				if (start > 0)
				{
					final int end = location.indexOf(')', start);
					if (end > 0)
					{
						location = location.substring(start, end);
					}
				}
				FileAccess.write(output, location);
			}
			else if (aString.startsWith("MAGICDAMAGE"))
			{
				final int magicdamage = eq.getBonusToDamage(true) + (int) eq.bonusTo("WEAPONPROF=" + profName, "DAMAGE");
				FileAccess.write(output, Delta.toString(magicdamage));
			}
			else if (aString.startsWith("MAGICHIT"))
			{
				final int magichit = eq.getBonusToHit(true) + (int) eq.bonusTo("WEAPONPROF=" + profName, "TOHIT");
				FileAccess.write(output, Delta.toString(magichit));
			}
			else if (aString.startsWith("MISC"))
			{
				final int miscBonus =
					(int) aPC.getTotalBonusTo("WEAPONPROF=" + profName, "TOHIT") -
					(int) aPC.getStatBonusTo("TOHIT", "TYPE.MELEE") -
					(int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
				FileAccess.write(output, Delta.toString(miscBonus));
			}
			else if (aString.startsWith("FEATDAMAGE"))
			{
				final int featBonus = (int) aPC.getFeatBonusTo("WEAPON", "DAMAGE", true) -
					(int) aPC.getFeatBonusTo("WEAPON", "DAMAGE-SHORTRANGE", true) +
					(int) aPC.getFeatBonusTo("WEAPONPROF=" + profName, "DAMAGE", true);
				FileAccess.write(output, Delta.toString(featBonus));
			}
			else if (aString.startsWith("FEATHIT"))
			{
				final int featBonus = (int) aPC.getFeatBonusTo("WEAPON", "TOHIT", true) +
					(int) aPC.getFeatBonusTo("WEAPONPROF=" + profName, "TOHIT", true);
				FileAccess.write(output, Delta.toString(featBonus));
			}
			else if (aString.startsWith("TEMPLATEDAMAGE"))
			{
				final int templateBonus = (int) aPC.getTemplateBonusTo("WEAPON", "DAMAGE", true) +
					(int) aPC.getTemplateBonusTo("WEAPONPROF=" + profName, "DAMAGE", true);
				FileAccess.write(output, Delta.toString(templateBonus));
			}
			else if (aString.startsWith("TEMPLATEHIT"))
			{
				final int templateBonus = (int) aPC.getTemplateBonusTo("WEAPON", "TOHIT", true) +
					(int) aPC.getTemplateBonusTo("WEAPONPROF=" + profName, "TOHIT", true);
				FileAccess.write(output, Delta.toString(templateBonus));
			}
			else if (aString.endsWith("DAMAGE") || aString.endsWith("DAMAGEBONUS"))
			{
				replaceTokenWeaponDamage(aString, eq, isDouble, wp, profName, aRange, aContent, aAmmo, output);
			}
			else if (aString.startsWith("SIZE"))
			{
				FileAccess.write(output, eq.getSize());
			}
			else if (aString.startsWith("SPROP"))
			{
				String aSprop = eq.getSpecialProperties();
				//Ammunition & Contents Modifier
				if (aContent > -1)
				{
					aSprop = "";
					if (aContent < eq.getContainedEquipmentCount() && !"".equals(eq.getContainedEquipment(aContent).getSpecialProperties()))
					{
						aSprop = eq.getContainedEquipment(aContent).getSpecialProperties();
					}
				}
				int ammoCount = 0;
				Equipment anEquip = null;
				final String containerCapacity = eq.getContainerCapacityString();
				for (Iterator e = aPC.getEquipmentListInOutputOrder().iterator(); e.hasNext() && aAmmo >= 0;)
				{
					aSprop = "";
					anEquip = (Equipment) e.next();
					for (Iterator e2 = anEquip.typeList().iterator(); e2.hasNext();)
					{
						if (containerCapacity.indexOf(e2.next().toString()) >= 0)
						{
							++ammoCount;
							break;
						}
					}
					if (ammoCount == aAmmo + 1)
					{
						break;
					}
				}
				if (anEquip != null && ammoCount > 0 && !"".equals(anEquip.getSpecialProperties()))
				{
					aSprop = anEquip.getSpecialProperties();
				}

				if (aSprop.startsWith(", "))
				{
					aSprop = aSprop.substring(2);
				}
				FileAccess.encodeWrite(output, aSprop);
			}
			else if (aString.startsWith("REACH"))
			{
				FileAccess.write(output, Integer.toString(getPCReach(aPC) + eq.getReach()));
			}
			else if (aString.startsWith("WT"))
			{
				FileAccess.write(output, BigDecimalHelper.trimZeros(eq.getWeight().toString()));
			}
			else if (aString.startsWith("RATEOFFIRE"))
			{
				FileAccess.encodeWrite(output, eq.getRateOfFire());
			}
/*			else if (aString.startsWith("TECHLEVEL"))
			{
				FileAccess.encodeWrite(output, eq.getTechLevel());
			}*/
		}
		else if (existsOnly)
		{
			noMoreItems = true;
		}
	}

	private void replaceTokenWeaponNumAttacks(Equipment eq, BufferedWriter output)
	{
		final String mString = aPC.getAttackString(Constants.ATTACKSTRING_MELEE, 0);
		final String rString = aPC.getAttackString(Constants.ATTACKSTRING_RANGED, 0);
		final String uString = aPC.getAttackString(Constants.ATTACKSTRING_UNARMED, 0);
		String weaponString = mString;
		if (eq.isRanged())
		{
			weaponString = rString;
		}
		if (eq.isMonk())
		{
			if (uString.length() > mString.length())
			{
				weaponString = uString;
			}
			else if (uString.length() == mString.length() && !mString.equals(uString))
			{
				final StringTokenizer mTok = new StringTokenizer(mString, "+/", false);
				final StringTokenizer uTok = new StringTokenizer(uString, "+/", false);
				final String msString = mTok.nextToken();
				final String usString = uTok.nextToken();
				if (Integer.parseInt(usString) >= Integer.parseInt(msString))
				{
					weaponString = uString;
				}
			}
		}

		final StringTokenizer bTok = new StringTokenizer(weaponString, "/");
		int extra_attacks = (int) eq.bonusTo("WEAPON", "ATTACKS", true);
		FileAccess.write(output, Integer.toString(bTok.countTokens() + extra_attacks));
	}

	private void replaceTokenWeaponCrit(Equipment eq, BufferedWriter output, String profName, boolean aDouble)
	{
		final int rawCritRange = eq.getRawCritRange();

		// see if the weapon has any crit range
		if (rawCritRange == 0)
		{
			// no crit range!
			FileAccess.write(output, "none");
		}
		else
		{
			final int dbl = (int) aPC.getTotalBonusTo("WEAPONPROF=" + profName, "CRITRANGEDOUBLE");
			final int iAdd = (int) aPC.getTotalBonusTo("WEAPONPROF=" + profName, "CRITRANGEADD");

			int eqDbl = eq.getCritRangeDouble(true) + dbl;
			int critrange = eq.getRawCritRange() * (eqDbl + 1);
			critrange = 21 - (critrange + iAdd + eq.getCritRangeAdd(true));

			FileAccess.write(output, String.valueOf(critrange));
			if (critrange < 20)
			{
				FileAccess.write(output, "-20");
			}

			if (aDouble && eq.getAltCritRange().length() > 0)
			{
				eqDbl = eq.getCritRangeDouble(false) + dbl;
				int altCritRange = eq.getRawCritRange(false) * (eqDbl + 1);
				altCritRange = 21 - (altCritRange + iAdd + eq.getCritRangeAdd(false));

				if (altCritRange != critrange)
				{
					FileAccess.write(output, "/" + String.valueOf(altCritRange));
					if (altCritRange < 20)
					{
						FileAccess.write(output, "-20");
					}
				}
			}
		}
	}

	private void replaceTokenWeaponDamage(String aString, Equipment eq, boolean aDouble, WeaponProf wp, String profName, int aRange, int aContent, int aAmmo, BufferedWriter output)
	{
		int index;
		// This defines if I should return the values based on weapon's location
		// or not. 1,2 and 3 overrides the actual location of the weapon
		// and calculates all data with this setting
		final int DAMAGEMODE_NORMALDAMAGE = 0;
		final int DAMAGEMODE_BASICDAMAGE = 1; // Assuming the weapon is on the primary hand
		final int DAMAGEMODE_OHDAMAGE = 2;    // Assuming the weapon is on the off-hand
		final int DAMAGEMODE_THDAMAGE = 3;    // Assuming the weapon is on two hands

		boolean isDoubleSplit = (eq.isType("Head1") || eq.isType("Head2"));
		final boolean isOnlyBonus = aString.endsWith("BONUS");
		if (isOnlyBonus)
		{
			aString = aString.substring(0,aString.length()-5);
		}

		int damageMode = DAMAGEMODE_NORMALDAMAGE;
		if ("BASICDAMAGE".equals(aString))
		{
			damageMode = DAMAGEMODE_BASICDAMAGE;
		}
		else if ("OHDAMAGE".equals(aString))
		{
			damageMode = DAMAGEMODE_OHDAMAGE;
		}
		else if ("THDAMAGE".equals(aString))
		{
			damageMode = DAMAGEMODE_THDAMAGE;
		}

		if (damageMode != DAMAGEMODE_NORMALDAMAGE && damageMode != DAMAGEMODE_THDAMAGE && Globals.isWeaponTwoHanded(aPC, eq, wp))
		{
			FileAccess.write(output, "N/A");
			return;
		}

		String bString = getDamage(aPC, eq);
		int bonus = 0;
		int meleeDamageStatBonus = (int) aPC.getStatBonusTo("DAMAGE", "TYPE.MELEE");
		int weaponProfBonus = 0;
		int eqbonus = 0;
		if (eq.isMonk() && eq.isUnarmed())
		{
			final String cString = aPC.getUnarmedDamageString(false, false);
			StringTokenizer bTok = new StringTokenizer(bString, " d+-", false);
			bTok.nextToken();
			final String b1String = bTok.nextToken();
			bTok = new StringTokenizer(cString, " d+-", false);
			bTok.nextToken();
			final String c1String = bTok.nextToken();
			if (Integer.parseInt(b1String) < Integer.parseInt(c1String))
			{
				bString = cString;
			}
		}
		int bInt = 0;
		if (!aString.startsWith("BASE"))
		{
			if (isDoubleSplit && damageMode == DAMAGEMODE_NORMALDAMAGE)
			{
				if (Globals.isWeaponTwoHanded(aPC, eq, wp))
				{
					damageMode = DAMAGEMODE_THDAMAGE;
				}
			}
			for (index = 0; index < bString.length(); ++index)
			{
				if ((bString.charAt(index) == '+') || (bString.charAt(index) == '-'))
				{
					bInt = Delta.decode(bString.substring(index)).intValue();
					break;
				}
			}

			if ((meleeDamageStatBonus > 0) && eq.isNatural() && (eq.getLocation() == Equipment.EQUIPPED_SECONDARY))
			{
				bInt -= meleeDamageStatBonus - doOffhandMod(aPC, meleeDamageStatBonus);
			}

			if (eq.isMelee() || eq.isThrown())
			{
				if ((aPC.isSecondaryWeapon(eq) && (aPC.getPrimaryWeapons().indexOf(eq) < 0) && (meleeDamageStatBonus > 0) && (damageMode == DAMAGEMODE_NORMALDAMAGE)) || (damageMode == DAMAGEMODE_OHDAMAGE))
				{
					bInt -= meleeDamageStatBonus - doOffhandMod(aPC, meleeDamageStatBonus);
				}
			}
			eqbonus = eq.getBonusToDamage(true);

			//Ammunition & Contents Modifier
			if (aContent > -1)
			{
				if (aContent < eq.getContainedEquipmentCount())
				{
					eqbonus += eq.getContainedEquipment(aContent).getBonusToDamage(true);
				}
			}

			final Equipment ammoUser = returnAmmoUser(eq, aAmmo);
			if (ammoUser != null)
			{
				eqbonus += ammoUser.getBonusToDamage(true);
			}

			for (Iterator ei = eq.typeList().iterator(); ei.hasNext();)
			{
				bonus += (int) aPC.getTotalBonusTo("DAMAGE", "TYPE." + ei.next().toString());
			}

			if (
				!aDouble &&
				eq.isMelee() &&
				(wp != null) &&
				(meleeDamageStatBonus > 0) &&
				(
				(
				(eq.getLocation() == Equipment.EQUIPPED_BOTH) &&
				damageMode == DAMAGEMODE_NORMALDAMAGE
				)
				|| (damageMode == DAMAGEMODE_THDAMAGE)
				)
			)
			{
				// Add extra damage if wielding a (non-light) 1-handed weapon with 2 hands
				if (Globals.isWeaponOneHanded(aPC, eq, wp) && !Globals.isWeaponLightForPC(aPC, eq))
				{
					bonus += doOffhandMod(aPC, meleeDamageStatBonus);
				}
				if (Globals.isWeaponTwoHanded(aPC, eq, wp))
				{
					bonus += doOffhandMod(aPC, meleeDamageStatBonus);
				}
			}
			if (wp != null && (meleeDamageStatBonus > 0) && eq.isMelee())
			{
				if (eq.isNatural() &&
					eq.isOnlyNaturalWeapon() &&
					((eq.getLocation() == Equipment.EQUIPPED_PRIMARY) || (eq.getLocation() == Equipment.EQUIPPED_BOTH)))
				{
					bonus += doOffhandMod(aPC, meleeDamageStatBonus);
				}
			}
			weaponProfBonus = (int) aPC.getTotalBonusTo("WEAPONPROF=" + profName, "DAMAGE");
			if (eq.isRanged())
			{
				weaponProfBonus -= (int) aPC.getTotalBonusTo("WEAPONPROF=" + profName.toUpperCase(), "DAMAGE.NOTRANGED");
			}
			// If at short range, add SHORTRANGE bonus
			if (aRange > -1)
			{
				final int rangeSize = eq.getRangeList(true).size();
				if (aRange < rangeSize && Integer.parseInt(eq.getRangeList(true).get(aRange).toString()) <= SettingsHandler.getGame().getShortRangeDistance())
				{
					bonus += (int) eq.bonusTo("WEAPON", "DAMAGE-SHORTRANGE", true);
					bonus += (int) aPC.getTotalBonusTo("DAMAGE", "SHORTRANGE");
					bonus += (int) aPC.getTotalBonusTo("COMBAT", "DAMAGE-SHORTRANGE");
					weaponProfBonus += (int) aPC.getTotalBonusTo("WEAPONPROF=" + profName, "DAMAGE-SHORTRANGE");
				}
			}
			bInt += bonus + weaponProfBonus + eqbonus;
			bString = bString.substring(0, index);
		}
		if (!"0d0".equalsIgnoreCase(bString))
		{
			if (!isOnlyBonus)
			{
				FileAccess.write(output, bString);
			}
			if (bInt != 0 || isOnlyBonus)
			{
				FileAccess.write(output, Delta.toString(bInt));
			}
		}
		else
		{
			FileAccess.write(output, "0");
		}

		if ((damageMode == DAMAGEMODE_NORMALDAMAGE) && (aDouble || (eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS)) && (!isDoubleSplit))
		{
			bInt -= eqbonus;
			/*
			 * eq.getBonusToDamage(false) returns the eq bonus for the secondary head,
			 * which for Double weapons is the right thing to do here, but for two-weapons
			 * mode we still want to use the primary eqbonus (which is already set properly)
			 */
			if (aDouble)
			{
				eqbonus = eq.getBonusToDamage(false);
			}
			if (aDouble && eq.getAltDamage().length() > 0)
			{
				bInt = 0;
				bString = eq.getAltDamage();
				if (bString.lastIndexOf('-') >= 0)
				{
					bInt = Integer.parseInt(bString.substring(bString.lastIndexOf('-')));
					bString = bString.substring(0, bString.lastIndexOf('-'));
				}
				else if (bString.lastIndexOf('+') >= 0)
				{
					bInt = Integer.parseInt(bString.substring(bString.lastIndexOf('+') + 1));
					bString = bString.substring(0, bString.lastIndexOf('+'));
				}
			}
			else
			{
				weaponProfBonus = 0;
				bonus = 0;
			}

			if (meleeDamageStatBonus > 0)
			{
				bonus += doOffhandMod(aPC, meleeDamageStatBonus) - meleeDamageStatBonus;
			}

			bInt += bonus + weaponProfBonus + eqbonus;
			FileAccess.write(output, "/");
			if (!"0d0".equalsIgnoreCase(bString))
			{
				if (!isOnlyBonus)
				{
					FileAccess.write(output, bString);
				}
				if (bInt != 0 || isOnlyBonus)
				{
					FileAccess.write(output, Delta.toString(bInt));
				}
			}
			else
			{
				FileAccess.write(output, "0");
			}
		}
	}

	/**
	 * I've refactored cut-n-paste code to bring about this method,
	 * which is still abominably funky
	 * TODO Definitely needs fixing up
	 *
	 * Just to explain, this code returns the nth equipment
	 * that can be contained inside the especific container (parameter eq)
	 * I iterate on the equipment list, to check, for each equipment,
	 * if it can be contained on the parameter eq
	 * To check this, i iterate again on the equipment's type list, and
	 * check it against the parameter's container capacity
	 * The looping and breaking logic might get a little more work.
	 * @param eq
	 * @param aAmmo
	 * @return
	 **/
	private Equipment returnAmmoUser(Equipment eq, int aAmmo)
	{
		Equipment anEquip;
		int ammoCount = 0;
		final String containerCapacity = eq.getContainerCapacityString();
		for (Iterator e = aPC.getEquipmentListInOutputOrder().iterator(); e.hasNext() && aAmmo >= 0;)
		{
			anEquip = (Equipment) e.next();
			for (Iterator e2 = anEquip.typeList().iterator(); e2.hasNext();)
			{
				if (containerCapacity.indexOf(e2.next().toString()) >= 0)
				{
					++ammoCount;
					break;
				}
			}

			//What does this do?
			//This ends the search, because we already found the nth equipment
			if (ammoCount == aAmmo + 1)
			{
				return anEquip;
//				break;
			}
		}
		//If it has no ammo capacity, don't bother returning it
//		if (ammoCount <= 0)
//		{
//			anEquip = null;
//		}
//		return anEquip;
		// If we reach here, we did not find the equipment we need
		return null;
	}

	/**
	 * STAT.x
	 * STAT.x.STAT
	 * STAT.x.BASE
	 * STAT.x.MOD
	 * STAT.x.BASEMOD
	 * STAT.x.NAME
	 * STAT.x.LONGNAME
	 * STAT.x.LEVELy[.NOEQUIP][.NOPOST]
	 **/
	private int replaceTokenStat(String aString, BufferedWriter output)
	{
		final int len = 1;
		int i;
		StringTokenizer aTok;

		String target;
		//New token syntax - STAT.x instead of STATx
		// for removal of old syntax, remove the if and leave the else
		if (aString.charAt(4) != '.')
		{
			Logging.errorPrint("Old syntax STATx will be replaced for STAT.x");

			i = (int) aString.charAt(4) - '0';
			aTok = new StringTokenizer(aString, ".", false);
			target = aTok.nextToken();
		}
		else
		{
			aTok = new StringTokenizer(aString, ".", false);
			target = aTok.nextToken(); //STAT
			try
			{
				i = Integer.parseInt(aTok.nextToken());
			}
			catch (NumberFormatException exc)
			{
				Logging.errorPrint("Error replacing " + aString, exc);

				return len;
			}
		}
		if ((i < 0) || (i >= aPC.getStatList().getStats().size()))
		{
			return len;
		}
		//This should be removed on the removal of the old TOKEN# syntax
		if (aTok.hasMoreTokens())
		{
			target = aTok.nextToken();
		}
		final PCStat aStat = (PCStat) aPC.getStatList().getStats().get(i);

		//Should be equals instead of startsWith after removal of old syntax
		if (target.startsWith("STAT"))
		{
			if (aPC.isNonAbility(i))
			{
				FileAccess.write(output, "*");
			}
			else
			{
				FileAccess.write(output, Integer.toString(aPC.getStatList().getTotalStatFor(aStat.getAbb())));
			}
		}
		else if ("BASE".equals(target))
		{
			if (aPC.isNonAbility(i))
			{
				FileAccess.write(output, "*");
			}
			else
			{
				FileAccess.write(output, Integer.toString(aPC.getStatList().getBaseStatFor(aStat.getAbb())));
			}
		}
		else if ("MOD".equals(target))
		{
			if (aPC.isNonAbility(i))
			{
				FileAccess.write(output, "+0");
			}
			else
			{
				final int temp = aPC.getStatList().getStatModFor(aStat.getAbb());
				FileAccess.write(output, Delta.toString(temp));
			}
		}
		else if ("BASEMOD".equals(target))
		{
			if (aPC.isNonAbility(i))
			{
				FileAccess.write(output, "+0");
			}
			else
			{
				final int temp = aPC.getStatList().getBaseStatModFor(aStat.getAbb());
				FileAccess.write(output, Delta.toString(temp));
			}
		}
		else if ("NOTEMP".equals(target))
		{
			if (aPC.isNonAbility(i))
			{
				FileAccess.write(output, "*");
			}
			else
			{
				FileAccess.write(output, Integer.toString(aPC.getStatList().getTotalStatFor(aStat.getAbb(), false)));
			}
		}
		else if ("NOTEMPMOD".equals(target))
		{
			if (aPC.isNonAbility(i))
			{
				FileAccess.write(output, "+0");
			}
			else
			{
				final int aNum = aPC.getStatList().getTotalStatFor(aStat.getAbb(), false);
				final int temp = aPC.getStatList().getModForNumber(aNum);
				FileAccess.write(output, Delta.toString(temp));
			}
		}
		else if ("NAME".equals(target))
		{
			FileAccess.write(output, Globals.s_ATTRIBSHORT[i]);
		}
		else if ("LONGNAME".equals(target))
		{
			FileAccess.write(output, Globals.s_ATTRIBLONG[i]);
		}
		else if (target.startsWith("LEVEL"))
		{
			//New token syntax - LEVEL.x instead of LEVELx
			// for removal of old syntax, remove the if and leave the else
			// and modify the above if to be equals instead of startsWith
			int iLevel;
			if (!"LEVEL".equals(target))
			{
				Logging.errorPrint("Old syntax STATx.LEVELy will be replaced for STAT.x.LEVEL.Y");

				try
				{
					iLevel = Integer.parseInt(target.substring(5));
				}
				catch (NumberFormatException e)
				{
					iLevel = Integer.MAX_VALUE;
				}
			}
			else
			{
				try
				{
					iLevel = Integer.parseInt(aTok.nextToken());
				}
				catch (NumberFormatException exc)
				{
					Logging.errorPrint("Error replacing " + aString, exc);

					return 0;
				}
			}

			if (iLevel >= 0)
			{
				//
				// TODO: at some point could possible see at which level (if any) stat became
				// a non-ability and return value accordingly
				//
				if (aPC.isNonAbility(i))
				{
					FileAccess.write(output, "*");
				}
				else
				{
					boolean includeEq = true;
					boolean includePost = true;
					while (aTok.hasMoreTokens())
					{
						final String bString = aTok.nextToken();
						if (bString.equals("NOEQUIP"))
						{
							includeEq = false;
						}
						else if (bString.equals("NOPOST"))
						{
							includePost = false;
						}
					}

					FileAccess.write(output, Integer.toString(aPC.getTotalStatAtLevel(aStat.getAbb(), iLevel, includePost, includeEq)));
				}
			}
		}
		return len;
	}

	private void _writeArmorProperty(Equipment eq, String property, BufferedWriter output)
	{
		if (property.startsWith("NAME"))
		{
			if (eq.isEquipped())
			{
				FileAccess.write(output, "*");
			}
			//FileAccess.encodeWrite(output, eq.getName());
			FileAccess.encodeWrite(output, eq.getOutputName());
			FileAccess.encodeWrite(output, eq.getAppliedName());
		}
		else if (property.startsWith("OUTPUTNAME"))
		{
			if (eq.isEquipped())
			{
				FileAccess.write(output, "*");
			}
			FileAccess.encodeWrite(output, eq.getOutputName());
			FileAccess.encodeWrite(output, eq.getAppliedName());
		}
		else if (property.startsWith("TOTALAC"))
		{
			// adjustments for new equipment modifier
			// EQMARMOR|AC|x|TYPE=ENHANCEMENT changed to COMBAT|AC|x|TYPE=Armor.ENHANCEMENT
//			FileAccess.write(output, Delta.toString(eq.getACMod()));
			FileAccess.write(output, Delta.toString((int) eq.bonusTo("COMBAT", "AC", true)));
		}
		else if (property.startsWith("BASEAC"))
		{
			// adjustments for new equipment modifier
			// EQMARMOR|AC|x|TYPE=ENHANCEMENT changed to COMBAT|AC|x|TYPE=Armor.ENHANCEMENT
//			FileAccess.write(output, Delta.toString(eq.getACMod()));
			FileAccess.write(output, Delta.toString((int) eq.bonusTo("COMBAT", "AC")));
		}
		else if (property.startsWith("ACBONUS"))
		{
			FileAccess.write(output, Delta.toString((int) eq.bonusTo("COMBAT", "AC", true)));
		}
		else if (property.startsWith("MAXDEX"))
		{
			final int iMax = eq.getMaxDex().intValue();
			if (iMax != Constants.MAX_MAXDEX)
			{
				FileAccess.write(output, Delta.toString(iMax));
			}
		}
		else if (property.startsWith("ACCHECK"))
		{
			FileAccess.write(output, Delta.toString(eq.acCheck()));
		}
		else if (property.startsWith("EDR"))
		{
			FileAccess.write(output, Delta.toString(eq.eDR()));
		}
		else if (property.startsWith("ISTYPE"))
		{
			FileAccess.write(output, (eq.isType(property.substring(property.indexOf(".") + 1))) ? "TRUE" : "FALSE");
		}
		else if (property.startsWith("SPELLFAIL"))
		{
			FileAccess.write(output, eq.spellFailure().toString());
		}
		else if (property.startsWith("MOVE"))
		{
			final StringTokenizer aTok = new StringTokenizer(eq.moveString(), ",", false);
			String tempString = "";
			if (("M".equals(aPC.getSize()) || "S".equals(aPC.getSize())) &&
				aTok.countTokens() > 0)
			{
				tempString = aTok.nextToken();
				if ("S".equals(aPC.getSize()) && aTok.countTokens() > 1)
				{
					tempString = aTok.nextToken();
				}
			}
			FileAccess.write(output, tempString);
		}
		else if (property.startsWith("SPROP"))
		{
			FileAccess.encodeWrite(output, eq.getSpecialProperties());
		}
		else if (property.startsWith("TYPE"))
		{
			String typeString = "";
			if (eq.isLight())
			{
				typeString = "Light";
			}
			else if (eq.isMedium())
			{
				typeString = "Medium";
			}
			else if (eq.isHeavy())
			{
				typeString = "Heavy";
			}
			else if (eq.isShield())
			{
				typeString = "Shield";
			}
			else if (eq.isExtra())
			{
				typeString = "Extra";
			}
			FileAccess.write(output, typeString);
		}
		else if (property.startsWith("WT"))
		{
			FileAccess.write(output, BigDecimalHelper.trimZeros(eq.getWeight().toString()));
		}
	}

	/**
	 * select various stuff, that improves AC
	 **/
	private int _replaceTokenArmorVarious(int index, String type, String subtype, String property, int equipped, int len, BufferedWriter output, int merge)
	{
		Equipment eq;
		final List aArrayList = new ArrayList();
		for (Iterator mapIter = aPC.getEquipmentOfTypeInOutputOrder(type, subtype, equipped, merge).iterator(); mapIter.hasNext();)
		{
			eq = (Equipment) mapIter.next();
			if (eq.getACMod().intValue() > 0)
			{
				aArrayList.add(eq);
			}
			else if (eq.getBonusListString("AC"))
			{
				aArrayList.add(eq);
			}
		}

		if (index >= aArrayList.size() - 1 && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}
		if (index < aArrayList.size())
		{
			eq = (Equipment) aArrayList.get(index);
			_writeArmorProperty(eq, property, output);
		}
		return len;
	}

	/**
	 * select items, which improve AC but are not type ARMOR
	 **/
	private int _replaceTokenArmorItem(int item, String subtype, String property, int equipped, int len, BufferedWriter output, int merge)
	{

		// select all pieces of equipment of status==equipped
		// filter all AC relevant stuff
		final List aArrayList = new ArrayList();
		for (Iterator e = aPC.getEquipmentListInOutputOrder(merge).iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();

			if (("".equals(subtype) || eq.isType(subtype)) &&
				((equipped == 3) ||
				(equipped == 2 && !eq.isEquipped()) ||
				(equipped == 1 && eq.isEquipped())))
			{
				if (eq.getBonusListString("AC") &&
					!eq.isArmor() && !eq.isShield())
				{
					aArrayList.add(eq);
				}
			}
		}

		if (item >= aArrayList.size())
		{
			len = 0;
			noMoreItems = true;
		}
		if (item < aArrayList.size())
		{
			final Equipment eq = (Equipment) aArrayList.get(item);
			_writeArmorProperty(eq, property, output);
		}

		return len;
	}

	/**
	 * select shields
	 **/
	private int _replaceTokenArmorShield(int shield, String subtype, String property, int equipped, int len, BufferedWriter output, int merge)
	{
		final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Shield", subtype, equipped, merge);
		if (shield >= aArrayList.size() - 1 && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}
		if (shield < aArrayList.size())
		{
			final Equipment eq = (Equipment) aArrayList.get(shield);
			_writeArmorProperty(eq, property, output);
		}
		return len;
	}

	/**
	 * select shirts
	 **/
	private int _replaceTokenArmorShirt(int shirt, String subtype, String property, int equipped, int len, BufferedWriter output, int merge)
	{
		final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Shirt", subtype, equipped, merge);
		if (shirt >= aArrayList.size() - 1 && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}
		if (shirt < aArrayList.size())
		{
			final Equipment eq = (Equipment) aArrayList.get(shirt);
			_writeArmorProperty(eq, property, output);
		}
		return len;
	}

	/**
	 * select suits
	 **/
	private int _replaceTokenArmorSuit(int suit, String subtype, String property, int equipped, int len, BufferedWriter output, int merge)
	{
		final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Suit", subtype, equipped, merge);
		//
		// Temporary hack until someone gets around to fixing it properly
		//
//		aArrayList.addAll(aPC.getEquipmentOfTypeInOutputOrder("Shirt", subtype, equipped));
		if (suit >= aArrayList.size() - 1 && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}
		if (suit < aArrayList.size())
		{
			final Equipment eq = (Equipment) aArrayList.get(suit);
			_writeArmorProperty(eq, property, output);
		}
		return len;
	}

	/**
	 * select suits + shields
	 **/
	private int _replaceTokenArmor(int armor, String property, int equipped, int len, BufferedWriter output, int merge)
	{
		final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Armor", equipped, merge);
		final List bArrayList = aPC.getEquipmentOfTypeInOutputOrder("Shield", equipped, merge);
		for (Iterator e = bArrayList.iterator(); e.hasNext();)
		{
			aArrayList.add(e.next());
		}

		if (armor >= aArrayList.size() - 1 && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}
		if (armor < aArrayList.size())
		{
			final Equipment eq = (Equipment) aArrayList.get(armor);
			_writeArmorProperty(eq, property, output);
		}
		return len;
	}

	/**
	 * Token Syntax
	 * <armor definition>.<property>
	 *
	 * <ARMOR DEFINITION>
	 * ARMOR.<TYPE>.<SUBTYPE>.<MERGE>.<EQUIPPED_STATUS>.x
	 *   <TYPE> = OPTIONAL
	 *     SUIT, SHIRT, SHIELD, ITEM, User-Defined
	 *   <SUBTYPE> = OPTIONAL, if TYPE exists
	 *     Only User-Defined
	 *   <MERGE> = OPTIONAL
	 *     MERGENONE, MERGEALL, MERGELOC
	 *   <EQUIPPED_STATUS> = OPTIONAL
	 *     ALL (Default), EQUIPPED, NOT_EQUIPPED
	 *
	 * <PROPERTY>
	 * NAME
	 * OUTPUTNAME
	 * TOTALAC
	 * BASEAC
	 * ACBONUS
	 * MAXDEX
	 * ACCHECK
	 * EDR
	 * SPELLFAIL
	 * MOVE
	 * SPROP
	 * TYPE (Light, Medium, Heavy, Sheild, Extra)
	 * WT
	 **/
	private int replaceTokenArmor(String aString, int len, BufferedWriter output)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final String[] tokens = new String[aTok.countTokens()];
		for (int i = 0; aTok.hasMoreTokens(); ++i)
		{
			tokens[i] = aTok.nextToken();
		}

		String property = "";
		// When removing old syntax, this if should be removed
		if (tokens.length > 0)
		{
			property = tokens[tokens.length - 1];
		}

		int equipped = 3;
		int index = 0;
		String type = "";
		String subtype = "";
		int merge = Constants.MERGE_ALL;

		for (int i = 0; i < tokens.length; ++i)
		{
			if ("ARMOR".equals(tokens[i]))
			{
				continue;
			}
			// When removing old syntax, delete this if
			else if (tokens[i].startsWith("ARMOR"))
			{
				Logging.errorPrint("Old syntax ARMORx will be replaced for ARMOR.x");

				index = Integer.parseInt(tokens[i].substring(5));
			}
			else if ("ALL".equals(tokens[i]))
			{
				equipped = 3;
			}
			// When removing old syntax, delete this if
			else if (tokens[i].startsWith("ALL"))
			{
				Logging.errorPrint("Old syntax ALLx will be replaced for ALL.x");

				index = Integer.parseInt(tokens[i].substring(3));
				equipped = 3;
			}
			else if ("EQUIPPED".equals(tokens[i]))
			{
				equipped = 1;
			}
			// When removing old syntax, delete this if
			else if (tokens[i].startsWith("EQUIPPED"))
			{
				Logging.errorPrint("Old syntax EQUIPPEDx will be replaced for EQUIPPED.x");

				index = Integer.parseInt(tokens[i].substring(8));
				equipped = 1;
			}
			else if ("NOT_EQUIPPED".equals(tokens[i]))
			{
				equipped = 2;
			}
			// When removing old syntax, delete this if
			else if (tokens[i].startsWith("NOT_EQUIPPED"))
			{
				Logging.errorPrint("Old syntax NOT_EQUIPPEDx will be replaced for NOT_EQUIPPED.x");

				index = Integer.parseInt(tokens[i].substring(12));
				equipped = 2;
			}
			else if (tokens[i].equals("MERGENONE"))
			{
				merge = Constants.MERGE_NONE;
			}
			else if (tokens[i].equals("MERGELOC"))
			{
				merge = Constants.MERGE_LOCATION;
			}
			else if (tokens[i].equals("MERGEALL"))
			{
				merge = Constants.MERGE_ALL;
			}
			else if (tokens[i].equals("ISTYPE"))
			{
				property = tokens[i] + "." + tokens[i + 1];
				break;
			}
			else if (i < tokens.length - 1)
			{
				try
				{
					index = Integer.parseInt(tokens[i]);
				}
				catch (NumberFormatException exc)
				{
					if ("".equals(type))
					{
						type = tokens[i];
					}
					else
					{
						subtype = tokens[i];
					}
				}
			}
			else
			{
				property = tokens[i];
			}
		}
		if ("".equals(type))
		{
			return _replaceTokenArmor(index, property, equipped, len, output, merge);
		}
		else if ("SUIT".equals(type))
		{
			return _replaceTokenArmorSuit(index, subtype, property, equipped, len, output, merge);
		}
		else if ("SHIRT".equals(type))
		{
			return _replaceTokenArmorShirt(index, subtype, property, equipped, len, output, merge);
		}
		else if ("SHIELD".equals(type))
		{
			return _replaceTokenArmorShield(index, subtype, property, equipped, len, output, merge);
		}
		else if ("ITEM".equals(type) || "ACITEM".equals(type))
		{
			return _replaceTokenArmorItem(index, subtype, property, equipped, len, output, merge);
		}
		else
		{
			return _replaceTokenArmorVarious(index, type, subtype, property, equipped, len, output, merge);
		}

	}

	private static final int SPELLTAG_UNKNOWN = -1;
	private static final int SPELLTAG_CAST = 0;
	private static final int SPELLTAG_KNOWN = 1;
	private static final int SPELLTAG_BOOK = 2;
	private static final int SPELLTAG_TYPE = 3;
	private static final int SPELLTAG_CLASS = 4;
	private static final int SPELLTAG_DC = 5;
	private static final int SPELLTAG_DCSTAT = 6;

	private void replaceTokenSpellList(String aString, BufferedWriter output)
	{
		int level = 0;
		int bookNum = 0;
		int tagType, i;

		// New token syntax SPELLLISTBLAH.x instead of SPELLLISTBLAHx
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		String fString = aTok.nextToken();
		// When removing old syntax, remove the if and leave the else
		if (fString.charAt(fString.length() - 1) >= '0' && fString.charAt(fString.length() - 1) <= '9')
		{
			Logging.errorPrint("Old syntax SPELLLISTblahx will be replaced for SPELLLISTblah.x");

			//SPELLLISTCAST0.0 KNOWN0.0 BOOK0.0 TYPE0
			if (aString.regionMatches(9, "TYPE", 0, 4))
			{
				tagType = SPELLTAG_TYPE;
			}
			else if (aString.regionMatches(9, "BOOK", 0, 4))
			{
				tagType = SPELLTAG_BOOK;
			}
			else if (aString.regionMatches(9, "KNOWN", 0, 5))
			{
				tagType = SPELLTAG_KNOWN;
			}
			else if (aString.regionMatches(9, "CAST", 0, 4))
			{
				tagType = SPELLTAG_CAST;
			}
			else if (aString.regionMatches(9, "CLASS", 0, 5))
			{
				tagType = SPELLTAG_CLASS;
			}
			else if (aString.regionMatches(9, "DCSTAT", 0, 6))
			{
				tagType = SPELLTAG_DCSTAT;
			}
			else if (aString.regionMatches(9, "DC", 0, 2))
			{
				tagType = SPELLTAG_DC;
			}
			else
			{
				tagType = SPELLTAG_UNKNOWN;
			}

			//
			// Get start of number based on length of tag string
			//
			i = 13;
			if ((tagType == SPELLTAG_KNOWN) || (tagType == SPELLTAG_CLASS))
			{
				i = 14;
			}
			else if (tagType == SPELLTAG_DC)
			{
				i = 11;
			}
			else if (tagType == SPELLTAG_DCSTAT)
			{
				i = 15;
			}

			if ((tagType != SPELLTAG_TYPE) && (tagType != SPELLTAG_CLASS))
			{
				level = Integer.parseInt(aString.substring(i + 2, i + 3));
			}
			if (tagType == SPELLTAG_BOOK)
			{
				bookNum = Integer.parseInt(aString.substring(i + 4, i + 5));
			}
			i = Integer.parseInt(aString.substring(i, i + 1)); // class index
		}
		else
		{
			if ("SPELLLISTTYPE".equals(fString))
			{
				tagType = SPELLTAG_TYPE;
			}
			else if ("SPELLLISTBOOK".equals(fString))
			{
				tagType = SPELLTAG_BOOK;
			}
			else if ("SPELLLISTKNOWN".equals(fString))
			{
				tagType = SPELLTAG_KNOWN;
			}
			else if ("SPELLLISTCAST".equals(fString))
			{
				tagType = SPELLTAG_CAST;
			}
			else if ("SPELLLISTCLASS".equals(fString))
			{
				tagType = SPELLTAG_CLASS;
			}
			else if ("SPELLLISTDCSTAT".equals(fString))
			{
				tagType = SPELLTAG_DCSTAT;
			}
			else if ("SPELLLISTDC".equals(fString))
			{
				tagType = SPELLTAG_DC;
			}
			else
			{
				tagType = SPELLTAG_UNKNOWN;
			}
			i = Integer.parseInt(aTok.nextToken());
			if (aTok.hasMoreTokens())
			{
				level = Integer.parseInt(aTok.nextToken());
			}
			if (aTok.hasMoreTokens())
			{
				bookNum = Integer.parseInt(aTok.nextToken());
			}
		}

		int y = 0;
		final PObject aObject = aPC.getSpellClassAtIndex(i);
		if (aObject != null)
		{
			String castNum = "--";
			int knownNum = 0;
			PCClass aClass = null;
			if (aObject instanceof PCClass)
			{
				aClass = (PCClass) aObject;
				castNum = String.valueOf(aClass.getCastForLevel(aClass.getLevel(),
					level, Globals.getDefaultSpellBook())) +
					aClass.getBonusCastForLevelString(aClass.getLevel(),
						level, Globals.getDefaultSpellBook());
				knownNum = aClass.getKnownForLevel(aClass.getLevel(), level);
			}

			switch (tagType)
			{
				case SPELLTAG_CAST:
					FileAccess.write(output, castNum);
					break;

				case SPELLTAG_KNOWN:
					FileAccess.write(output, Integer.toString(knownNum));
					break;

				case SPELLTAG_BOOK:
					CharacterSpell cs;
					String bookName = Globals.getDefaultSpellBook();
					if (bookNum > 0)
					{
						bookName = (String) aPC.getSpellBooks().get(bookNum);
					}
					final List spells = aObject.getCharacterSpell(null, bookName, level);
//					final List spells = aObject.getCharacterSpell(null, Globals.getDefaultSpellBook(), level);
					for (Iterator se = spells.iterator(); se.hasNext();)
					{
						cs = (CharacterSpell) se.next();
						if (y++ > 0)
						{
							FileAccess.write(output, ", ");
						}
						//FileAccess.encodeWrite(output, cs.getSpell().getName());
						FileAccess.encodeWrite(output, cs.getSpell().getOutputName());
					}
					if ((y == 0) && existsOnly)
					{
						noMoreItems = true;
					}
					break;

				case SPELLTAG_TYPE:
					if (aClass != null)
					{
						FileAccess.encodeWrite(output, aClass.getSpellType());
					}
					break;

				case SPELLTAG_CLASS:
					if ((aClass != null) && aString.endsWith("LEVEL"))
					{
						FileAccess.write(output, String.valueOf(aClass.getLevel() + (int) aPC.getTotalBonusTo("PCLEVEL", aClass.getName())));
					}
					else
					{
						//FileAccess.encodeWrite(output, aObject.getName());
						FileAccess.encodeWrite(output, aObject.getOutputName());
					}
					break;

				case SPELLTAG_DC:
					Spell aSpell = new Spell();
					int DC = aSpell.getDCForPlayerCharacter(aPC, null, aClass, level);
					FileAccess.write(output, Integer.toString(DC));
					break;

				case SPELLTAG_DCSTAT:
					if (aClass != null)
					{
						FileAccess.write(output, aClass.getSpellBaseStat());
					}
					break;

				default:
					Logging.errorPrint("In ExportHandler.replaceTokenSpellList the tagType value " + tagType + " is not handled.");
					break;
			}
		}
	}

	private void replaceTokenSpellMem(String aString, BufferedWriter output)
	{
		// New Token syntax - SPELLMEM.x instead of SPELLMEMx
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		// When removing old syntax, remove fString and leave only the IF code
		final String fString = aTok.nextToken();
		final int classNum;
		if ("SPELLMEM".equals(fString))
		{
			classNum = Integer.parseInt(aTok.nextToken());
		}
		else
		{
			Logging.errorPrint("Old syntax SPELLMEMx will be replaced for SPELLMEM.x");

			classNum = Integer.parseInt(fString.substring(8));
		}
		final int bookNum = Integer.parseInt(aTok.nextToken());
		final int spellLevel = Integer.parseInt(aTok.nextToken());
		final int spellNumber = Integer.parseInt(aTok.nextToken());
		boolean found = false;
		String aLabel = "NAME";
		if (aTok.hasMoreTokens())
		{
			aLabel = aTok.nextToken();
		}
		String altLabel = "";
		if (aTok.hasMoreTokens())
		{
			altLabel = aTok.nextToken();
		}
		final PObject aObject = aPC.getSpellClassAtIndex(classNum);

		if (aObject == null && existsOnly && classNum != -1)
		{
			noMoreItems = true;
		}
		String bookName = Globals.getDefaultSpellBook();
		if (bookNum > 0)
		{
			bookName = (String) aPC.getSpellBooks().get(bookNum);
		}

		if (aObject != null || classNum == -1)
		{
			if (classNum == -1)
			{
				bookName = Globals.getDefaultSpellBook();
			}

			CharacterSpell cs = null;
			if (!"".equals(bookName))
			{
				Spell aSpell = null;
				if (classNum == -1)
				{
					final List charSpellList = new ArrayList();
					for (Iterator iClass = aPC.getClassList().iterator(); iClass.hasNext();)
					{
						final PCClass aClass = (PCClass) iClass.next();
						List aList = aClass.getCharacterSpell(null, bookName, spellLevel);
						for (Iterator ai = aList.iterator(); ai.hasNext();)
						{
							cs = (CharacterSpell) ai.next();
							if (!charSpellList.contains(cs))
							{
								charSpellList.add(cs);
							}
						}
					}
					Collections.sort(charSpellList);
					if (spellNumber < charSpellList.size())
					{
						cs = (CharacterSpell) charSpellList.get(spellNumber);
						aSpell = cs.getSpell();
						found = true;
					}

				}
				else if (aObject != null)
				{
					final List charSpells = aObject.getCharacterSpell(null, bookName, spellLevel);
					if (spellNumber < charSpells.size())
					{
						cs = (CharacterSpell) charSpells.get(spellNumber);
						aSpell = cs.getSpell();
						found = true;

					}
				}
				else if (inLabel && checkBefore)
				{
					canWrite = false;
				}
				if (cs == null)
				{
					if (existsOnly)
					{
						noMoreItems = true;
					}
					return;
				}
				final SpellInfo si = cs.getSpellInfoFor(bookName, spellLevel, -1);
				if (found && (aSpell != null) && (si != null))
				{
					if ("NAME".equals(aLabel))
					{
						FileAccess.write(output, aSpell.getOutputName() + si.toString());
					}
					else if ("OUTPUTNAME".equals(aLabel))
					{
						FileAccess.write(output, aSpell.getOutputName() + si.toString());
					}
					else if ("TIMES".equals(aLabel))
					{
						if (si.getTimes() == -1)
						{
							FileAccess.write(output, "At Will");
						}
						else
						{
							FileAccess.write(output, String.valueOf(si.getTimes()));
						}
					}
					else if (aSpell != null)
					{
						if ("RANGE".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aPC.getSpellRange(aSpell, cs.getOwner().getName(), si));
						}
						else if ("COMPONENTS".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aSpell.getComponentList());
						}
						else if ("CASTINGTIME".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aSpell.getCastingTime());
						}
						else if ("DC".equals(aLabel))
						{
							int dc = aSpell.getDCForPlayerCharacter(aPC, si);
							FileAccess.encodeWrite(output, String.valueOf(dc));
						}
						else if ("DURATION".equals(aLabel))
						{
							String mString = aPC.parseSpellString(aSpell.getDuration(), cs.getOwner().getName());
							FileAccess.encodeWrite(output, mString);
						}
						else if ("DESC".equals(aLabel) || "EFFECT".equals(aLabel))
						{
							String mString = aPC.parseSpellString(aSpell.getDescription(), cs.getOwner().getName());
							FileAccess.encodeWrite(output, mString);
						}
						else if ("TARGET".equals(aLabel) || "EFFECTYPE".equals(aLabel))
						{
							String mString = aPC.parseSpellString(aSpell.getTarget(), cs.getOwner().getName());
							FileAccess.encodeWrite(output, mString);
						}
						else if ("SAVEINFO".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aSpell.getSaveInfo());
						}
						else if ("SCHOOL".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aSpell.getSchool());
						}
						else if ("SOURCELEVEL".equals(aLabel))
						{
							replaceTokenSpellMemSourceLevel(aSpell, output);
						}
						else if ("SOURCE".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aSpell.getSource());
						}
						else if ("SOURCESHORT".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aSpell.getSourceShort(8));
						}
						else if ("SOURCEPAGE".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aSpell.getSourcePage());
						}
						else if ("SUBSCHOOL".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aSpell.getSubschool());
						}
						else if ("DESCRIPTOR".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aSpell.descriptor());
						}
						else if ("FULLSCHOOL".equals(aLabel))
						{
							String aTemp = aSpell.getSchool();
							if ((aSpell.getSubschool().length() > 0) && (!"NONE".equals(aSpell.getSubschool().trim().toUpperCase())))
							{
								aTemp += " (" + aSpell.getSubschool() + ')';
							}
							if (aSpell.descriptor().length() > 0)
							{
								aTemp += " [" + aSpell.descriptor() + ']';
							}
							FileAccess.encodeWrite(output, aTemp);
						}
						else if ("SR".equals(aLabel))
						{
							FileAccess.encodeWrite(output, aSpell.getSpellResistance());
						}
						else if (aLabel.startsWith("DESCRIPTION"))
						{
							final String sString = getItemDescription("SPELL", aSpell.getName(), aSpell.getDescription());
							if (altLabel.length() > 0)
							{
								replaceWithDelimiter(output, sString, altLabel);
							}
							else
							{
								FileAccess.encodeWrite(output, sString);
							}
						}
						else if (aLabel.startsWith("BONUSSPELL"))
						{
							final List dList = new ArrayList();
							String sString = "*";
							if (aLabel.length() > 10)
							{
								sString = aLabel.substring(10);
							}
							if (aObject != null && cs != null && cs.isSpecialtySpell() && (aObject instanceof PCClass))
							{
								for (Iterator ip = aPC.getCharacterDomainList().iterator(); ip.hasNext();)
								{
									final CharacterDomain aCD = (CharacterDomain) ip.next();
									if (aCD != null && aCD.getDomain() != null && aCD.isFromPCClass(aObject.getName()))
									{
										dList.add(aCD.getDomain().getName());
									}
								}
								final List charSpells = aObject.getCharacterSpell(aSpell, bookName, spellLevel);
								boolean isDomainOnly = true;
								for (Iterator e = charSpells.iterator(); e.hasNext();)
								{
									final CharacterSpell cSpell = (CharacterSpell) e.next();
									if (!cSpell.isSpecialtySpell())
									{
										isDomainOnly = false;
										break;
									}
								}
								if (isDomainOnly)
								{
									FileAccess.write(output, sString + sString);
								}
								else
								{
									FileAccess.write(output, sString);
								}
							}
							else
							{
								FileAccess.write(output, altLabel);
							}
						}
					}
				}
				else if (existsOnly)
				{
					noMoreItems = true;
				}
			}
			else if (existsOnly)
			{
				noMoreItems = true;
			}
		}
	}

	private static void replaceTokenSpellMemSourceLevel(Spell aSpell, BufferedWriter output)
	{
		final Map tempHash = aSpell.getLevelInfo();
		StringBuffer tempSource = new StringBuffer();
		for (Iterator e = tempHash.keySet().iterator(); e.hasNext();)
		{
			String className = (String) e.next();
			Integer classLevel = (Integer) tempHash.get(className);
			if (className.startsWith("CLASS|"))
			{
				className = className.substring(6);
				className = Globals.getClassNamed(className).getAbbrev();
			}
			else
			{
				className = className.substring(7);
			}
			if (tempSource.length() > 0)
			{
				tempSource.append(", ");
			}
			tempSource.append(className).append(classLevel.toString());
		}
		FileAccess.write(output, tempSource.toString());
	}

	private int replaceTokenSkill(String aString, BufferedWriter output)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		String fString = "";
		String property = "";
		Skill aSkill = null;

		final String[] tokens = new String[aTok.countTokens()];
		for (int i = 0; aTok.hasMoreTokens(); ++i)
		{
			tokens[i] = aTok.nextToken();
		}

		if (tokens.length > 0)
		{
			fString = tokens[0];
			//This allows only for one property
			//Should be modified to get all tokens except the first two (<token>.<num>.<property>)
			// but can only be done after the support for TOKEN# is removed
			property = tokens[tokens.length - 1];
		}

		if (fString.startsWith("SKILLSUBSET"))	// Matches skills known which start with the second dotted field ie. SKILLSUBSET%.Read Language.NAME
		{
			//New token syntax - SKILLSUBSET.x instead of SKILLSUBSETx
			// for removal of old syntax, remove the if and leave the else
			// and modify the above if to be equals instead of startsWith
			int i;
			if (!"SKILLSUBSET".equals(fString))
			{
				Logging.errorPrint("Old syntax SKILLSUBSETx will be replaced for SKILLSUBSET.x");

				i = Integer.parseInt(fString.substring(11));
				fString = tokens[1];
			}
			else
			{
				try
				{
					i = Integer.parseInt(tokens[1]);
				}
				catch (NumberFormatException exc)
				{
					Logging.errorPrint("Error replacing " + aString, exc);

					return 0;
				}
				fString = tokens[2];
			}
			final List skillSubset = new ArrayList();

			for (Iterator iter = aPC.getSkillListInOutputOrder().iterator(); iter.hasNext();)
			{
				Skill bSkill = (Skill) iter.next();
				if (bSkill.getName().toUpperCase().startsWith(fString.toUpperCase()))
				{
					skillSubset.add(bSkill);
				}
			}

			if (i >= skillSubset.size() - 1 && existsOnly)
			{
				noMoreItems = true;
			}
			if (i > skillSubset.size() - 1)
			{
				//len = 0;
			}
			else
			{
				aSkill = (Skill) skillSubset.get(i);
			}

		}
		if (fString.startsWith("SKILLTYPE"))	// Matches skills known which start with the second dotted field ie. SKILLSUBSET%.Read Language.NAME
		{
			//New token syntax - SKILLTYPE.x instead of SKILLTYPEx
			// for removal of old syntax, remove the if and leave the else
			// and modify the above if to be equals instead of startsWith
			int i;
			if (!"SKILLTYPE".equals(fString))
			{
				Logging.errorPrint("Old syntax SKILLTYPEx will be replaced for SKILLTYPE.x");

				i = Integer.parseInt(fString.substring(9));
				fString = tokens[1];
			}
			else
			{
				try
				{
					i = Integer.parseInt(tokens[1]);
				}
				catch (NumberFormatException exc)
				{
					Logging.errorPrint("Error replacing " + aString, exc);

					return 0;
				}
				fString = tokens[2];
			}
			final List skillSubset = new ArrayList();

			for (Iterator iter = aPC.getSkillListInOutputOrder().iterator(); iter.hasNext();)
			{
				final Skill bSkill = (Skill) iter.next();
				if (bSkill.isType(fString))
				{
					skillSubset.add(bSkill);
				}
			}

			if (i >= skillSubset.size() - 1 && existsOnly)
			{
				noMoreItems = true;
			}
			if (i > skillSubset.size() - 1)
			{
				//len = 0;
			}
			else
			{
				aSkill = (Skill) skillSubset.get(i);
			}

		}
		else if (fString.equals("SKILLLEVEL"))
		{
			if ("TOTAL".equals(property))
			{
				final int aLevelOffset;
				try
				{
					aLevelOffset = Integer.parseInt(tokens[1]) - 1;
					if ((aLevelOffset >= aPC.getLevelInfoSize()) || (aLevelOffset < 0))
					{
						FileAccess.write(output, "0");
						return 0;
					}

					final PCLevelInfo wLevelInfo = (PCLevelInfo) aPC.getLevelInfo().get(aLevelOffset);
					final int wOutput = wLevelInfo.getSkillPointsGained();
					FileAccess.write(output, Integer.toString(wOutput));
					return wOutput;
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Error replacing " + aString, nfe);
					return 0;
				}
			}
		}
		//This is the SKILL# support should be removed entirely
		// when definitely moved to SKILL.# syntax
		else if ((fString.length() > 5) && (fString.length() < 8))
		{
			Logging.errorPrint("Old syntax SKILLx will be replaced for SKILL.x");

			int i = 999;
			try
			{
				i = Integer.parseInt(fString.substring(5));
			}
			catch (NumberFormatException nfe)
			{
				return 0;
			}
			final List pcSkills = aPC.getSkillListInOutputOrder();
			if (i >= pcSkills.size() - 1 && existsOnly)
			{
				noMoreItems = true;
			}
			if (i > pcSkills.size() - 1)
			{
				return 0;
			}
			else
			{
				aSkill = (Skill) pcSkills.get(i);
			}
		}
		else
		{
			//New token syntax - SKILL.x instead of SKILLx
			try
			{
				final int i = Integer.parseInt(tokens[1]);
				final List pcSkills = aPC.getSkillListInOutputOrder();
				if (i >= pcSkills.size() - 1 && existsOnly)
				{
					noMoreItems = true;
				}
				if (i < pcSkills.size())
				{
					aSkill = (Skill) pcSkills.get(i);
				}
			}
			catch (NumberFormatException exc)
			{
				//Allowing SKILL.Spot.<subtoken>
				fString = tokens[1];

				aSkill = aPC.getSkillNamed(fString);
				if (aSkill == null)
				{
					aSkill = Globals.getSkillNamed(fString);
				}
			}
		}

		return _writeSkillProperty(aSkill, property, output);
	}

	/*
	 * This writes the Skill info out to the chseet
	 */
	private int _writeSkillProperty(Skill aSkill, String property, BufferedWriter output)
	{
		final int len = 0;
		int cmp = 0;

		if ("NAME".equalsIgnoreCase(property))
		{
			cmp = 0;
		}
		else if ("TOTAL".equalsIgnoreCase(property))
		{
			cmp = 1;
		}
		else if ("RANK".equalsIgnoreCase(property))
		{
			cmp = 2;
		}
		else if ("MOD".equalsIgnoreCase(property))
		{
			cmp = 3;
		}
		else if ("ABILITY".equalsIgnoreCase(property))
		{
			cmp = 4;
		}
		else if ("ABMOD".equalsIgnoreCase(property))
		{
			cmp = 5;
		}
		else if ("MISC".equalsIgnoreCase(property))
		{
			cmp = 6;
		}
		else if ("UNTRAINED".equalsIgnoreCase(property))
		{
			cmp = 7;
		}
		else if ("EXCLUSIVE".equalsIgnoreCase(property))
		{
			cmp = 8;
		}
		else if (property.toUpperCase().startsWith("UNTRAINED"))
		{
			cmp = 9;
		}
		else if (property.toUpperCase().startsWith("ACP"))
		{
			cmp = 10;
		}
		else if ("EXCLUSIVE_TOTAL".equalsIgnoreCase(property))
		{
			cmp = 11;
		}
		else if ("TRAINED_TOTAL".equalsIgnoreCase(property))
		{
			cmp = 12;
		}

		if (aSkill != null)
		{
			if ((cmp == 5 || cmp == 6) && aSkill.getKeyStat().equals(Constants.s_NONE))
			{
				FileAccess.write(output, "n/a");
			}
			else
			{
				switch (cmp)
				{
					case 0:
						FileAccess.write(output, aSkill.qualifiedName());
						return len;
					case 1:
						FileAccess.write(output, Integer.toString(aSkill.getTotalRank().intValue() + aSkill.modifier().intValue()));
						return len;
					case 2:
						FileAccess.write(output, aSkill.getTotalRank().toString());
						return len;
					case 3:
						FileAccess.write(output, aSkill.modifier().toString());
						return len;
					case 4:
						FileAccess.write(output, aSkill.getKeyStat());
						return len;
					case 5:
						FileAccess.write(output, Integer.toString(aPC.getStatList().getStatModFor(aSkill.getKeyStat())));
						return len;
					case 6:
						FileAccess.write(output, Integer.toString(aSkill.modifier().intValue() - aPC.getStatList().getStatModFor(aSkill.getKeyStat())));
						return len;
					case 7:
						FileAccess.write(output, aSkill.getUntrained());
						return len;
					case 8:
						FileAccess.write(output, aSkill.getExclusive());
						return len;
					case 9:
						{
							/*
								if (property.toUpperCase().startsWith("TRAINED"))

								Syntax: SKILL%.UNTRAINEDfoo,bar
								where foo and bar are optional strings of unfixed length.

								Behavior: prints out foo if the skill is usable untrained,
								bar if not usable untrained.
								if bar is not supplied, nothing is printed if untrained. If neither foo
								nor bar are supplied, why are you using this tag?


							*/
							final StringTokenizer aTok = new StringTokenizer(property.substring(9), ",");
							String untrained_tok, trained_tok;
							if (aTok.hasMoreTokens())
							{
								untrained_tok = aTok.nextToken();
							}
							else
							{
								untrained_tok = "";
							}
							if (aTok.hasMoreTokens())
							{
								trained_tok = aTok.nextToken();
							}
							else
							{
								trained_tok = "";
							}
							if (aSkill.getUntrained().equalsIgnoreCase("Y"))
							{
								FileAccess.write(output, untrained_tok);
							}
							else
							{
								FileAccess.write(output, trained_tok);
							}

							return len;
						}
					case 10:
						{
							/*
								if (property.toUpperCase().startsWith("ACP"))

								Syntax: SKILL%.ACPfoo,bar,baz,bot
								where foo, bar, baz, and bot are strings of unfixed length.

								Behavior: tests for armor check penalty interaction with this skill.
								foo is printed if the skill is not affected by ACP.
								bar is printed if the skill is affected by ACP.
								baz is printed if the skill is only affected by ACP if the user is untrained
								bot is printed if the skill has the special weight penalty (like Swim)
							*/
							final StringTokenizer aTok = new StringTokenizer(property.substring(3), ",");
							String acp_yes, acp_none, acp_nonprof, acp_weight;
							if (aTok.hasMoreTokens())
							{
								acp_none = aTok.nextToken();
							}
							else
							{
								acp_none = "";
							}
							if (aTok.hasMoreTokens())
							{
								acp_yes = aTok.nextToken();
							}
							else
							{
								acp_yes = "";
							}
							if (aTok.hasMoreTokens())
							{
								acp_nonprof = aTok.nextToken();
							}
							else
							{
								acp_nonprof = "";
							}
							if (aTok.hasMoreTokens())
							{
								acp_weight = aTok.nextToken();
							}
							else
							{
								acp_weight = "";
							}
							if (aSkill.getACheck() == 0)
							{
								FileAccess.write(output, acp_none);
							}
							else if (aSkill.getACheck() == 1)
							{
								FileAccess.write(output, acp_yes);
							}
							else if (aSkill.getACheck() == 2)
							{
								FileAccess.write(output, acp_nonprof);
							}
							else if (aSkill.getACheck() == 3)
							{
								FileAccess.write(output, acp_weight);
							}

							return len;
						}
					case 11:
						FileAccess.write(output, Integer.toString(((aSkill.getExclusive().equalsIgnoreCase("Y") || aSkill.getUntrained().equalsIgnoreCase("NO")) && aSkill.getTotalRank().intValue() == 0) ? 0 : aSkill.getTotalRank().intValue() + aSkill.modifier().intValue()));
						return len;
					case 12:
						FileAccess.write(output, Integer.toString((aSkill.getUntrained().equalsIgnoreCase("NO") && aSkill.getTotalRank().intValue() == 0) ? 0 : aSkill.getTotalRank().intValue() + aSkill.modifier().intValue()));
						return len;
					default:
						Logging.errorPrint("In ExportHandler._writeSkillProperty the cmp value " + cmp + " is not handled.");
						break;
				}
			}
		}
		return len;
	}

	private int replaceTokenSpellListBook(String aString)
	{
		int sbookNum = 0;

		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int levelNum = Integer.parseInt(aTok.nextToken());
		if (aTok.hasMoreTokens())
		{
			sbookNum = Integer.parseInt(aTok.nextToken());
		}

		String bookName = Globals.getDefaultSpellBook();
		if (sbookNum > 0)
		{
			bookName = (String) aPC.getSpellBooks().get(sbookNum);
		}

		canWrite = false;

		final PObject aObject = aPC.getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			final List aList = aObject.getCharacterSpell(null, bookName, levelNum);
			canWrite = !aList.isEmpty();
		}
		return 0;
	}

	private void replaceTokenAlignmentShort(BufferedWriter output)
	{
		final String alString = SystemCollections.getShortAlignmentAtIndex(aPC.getAlignment());
		FileAccess.encodeWrite(output, alString);
	}

	private void replaceTokenAlignment(BufferedWriter output)
	{
		final String alString = SystemCollections.getLongAlignmentAtIndex(aPC.getAlignment());
		FileAccess.encodeWrite(output, alString);
	}

	private static void replaceWithDelimiter(BufferedWriter output, String sString, String sDelim)
	{
		final StringTokenizer bTok = new StringTokenizer(sString, "\r\n", false);
		while (bTok.hasMoreTokens())
		{
			FileAccess.encodeWrite(output, bTok.nextToken());
			if (bTok.hasMoreTokens())
			{
				FileAccess.write(output, sDelim);
			}
		}
	}

	/*
	 * ####################################################################
	 * various print methods
	 * ####################################################################
	 */


	private static void printFeat(int numberPos, String aString, List anArrayList, BufferedWriter output)
	{
		final int FEAT_DEFAULT = 0;
		final int FEAT_VISIBLE = 1;
		final int FEAT_HIDDEN = 2;
		final int FEAT_ALL = 3;
		// New token syntax: FEAT.x instead of FEATx
		// When removing old syntax, the numberPos parameter can be removed

		String typeStr = "";
		int visibility = FEAT_DEFAULT;
		String featType = null;

//		final int len = anArrayList.size();
		int i;	//i holds the number of the feat we want, is decremented as we iterate through the list.
		//is only decremented if the current feat matches the same pattern as the desired feat.

		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final String fString = aTok.nextToken();
		// When removing old syntax, remove the else and leave only the if code
		if ("FEAT".equals(fString) ||
			"VFEAT".equals(fString) ||
			"FEATALL".equals(fString) ||
			"FEATAUTO".equals(fString))
		{
			i = -1;
			while (aTok.hasMoreTokens())
			{
				final String bString = aTok.nextToken();
				try
				{
					i = Integer.parseInt(bString);
					break;
				}
				catch (NumberFormatException exc)
				{
					if (visibility == FEAT_DEFAULT)
					{
						if (bString.equals("VISIBLE"))
						{
							visibility = FEAT_VISIBLE;
							continue;
						}
						else if (bString.equals("HIDDEN"))
						{
							visibility = FEAT_HIDDEN;
							continue;
						}
						else if (bString.equals("ALL"))
						{
							visibility = FEAT_ALL;
							continue;
						}
						else
						{
							featType = bString;
						}
					}
					else
					{
						featType = bString;
					}
				}
			}
			//i = Integer.parseInt(aTok.nextToken());
			if (aTok.hasMoreTokens())
			{
				typeStr = aTok.nextToken();
				if (!(typeStr.startsWith("TYPE=") || typeStr.startsWith("!TYPE=")))
				{
					typeStr = "";
				}
			}
		}
		else
		{
			Logging.errorPrint("Old syntax FEATx will be replaced for FEAT.x");

			// When removing old syntax, remove dotpos and the if
			int dotpos = aString.indexOf('.');
			if (dotpos < 0)
			{
				i = Integer.parseInt(aString.substring(numberPos));
			}
			else
			{
				i = Integer.parseInt(aString.substring(numberPos, dotpos));
			}
			if (aString.indexOf("TYPE") > 0)
			{
				if (aString.endsWith(".DESC"))
				{
					typeStr = aString.substring(dotpos, aString.lastIndexOf('.'));
				}
				else
				{
					typeStr = aString.substring(dotpos);
				}
			}
		}

//		if (len <= i && existsOnly)
//		{
//			noMoreItems = true;
//		}
		List aList = new ArrayList();
		Globals.sortPObjectList(anArrayList);
		Feat aFeat;
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{
			aFeat = (Feat) e.next();
			boolean matchTypeDef = false;
			boolean matchVisibilityDef = false;

			if (featType != null)
			{
				if (aFeat.isType(featType))
				{
					matchTypeDef = true;
				}
			}
			else
			{
				matchTypeDef = true;
			}
			if (aString.indexOf(".!TYPE=") >= 0)
			{
				if (!aFeat.isType(typeStr.substring(7)))
				{
					matchTypeDef = true;
				}
			}
			else if (aString.indexOf(".TYPE=") >= 0)
			{
				if (aFeat.isType(typeStr.substring(6)))
				{
					matchTypeDef = true;
				}
			}

			if ((visibility == FEAT_DEFAULT || visibility == FEAT_VISIBLE) &&
				(aFeat.isVisible() == Feat.VISIBILITY_DEFAULT || aFeat.isVisible() == Feat.VISIBILITY_OUTPUT_ONLY))
			{
				matchVisibilityDef = true;
			}
			else if (visibility == FEAT_ALL)
			{
				matchVisibilityDef = true;
			}
			else if ((visibility == FEAT_HIDDEN) &&
				aFeat.isVisible() != Feat.VISIBILITY_DEFAULT &&
				aFeat.isVisible() != Feat.VISIBILITY_OUTPUT_ONLY)
			{
				matchVisibilityDef = true;
			}

			if (matchTypeDef && matchVisibilityDef)
			{
				aList.add(aFeat);
			}
		}
		if (i < aList.size())
		{
			aFeat = (Feat) aList.get(i);
			if (aString.endsWith(".DESC"))
			{
				FileAccess.encodeWrite(output, aFeat.getDescription());
			}
			else if (aString.endsWith(".TYPE"))
			{
				FileAccess.encodeWrite(output, aFeat.getType());
			}
			else if (aString.endsWith(".ASSOCIATED"))
			{
				StringBuffer buf = new StringBuffer();
				for (int j = 0; j < aFeat.getAssociatedCount(); j++)
				{
					if (j != 0)
					{
						buf.append(",");
					}
					buf.append(aFeat.getAssociated(j));
				}
				FileAccess.encodeWrite(output, buf.toString());
			}
			else if (aString.endsWith(".ASSOCIATEDCOUNT"))
			{
				FileAccess.encodeWrite(output, new Integer(aFeat.getAssociatedCount()).toString());
			}
			else
			{
				FileAccess.encodeWrite(output, aFeat.qualifiedName());
			}
		}
		else if (existsOnly)
		{
			noMoreItems = true;
		}
	}

	private static void printFeatList(String delim, List aArrayList, BufferedWriter output)
	{
		if (delim == null || "".equals(delim))
		{
			delim = ", ";
		}
		String aString = "";
		if (delim.lastIndexOf('.') >= 0)
		{
			try
			{
				aString = delim.substring(delim.lastIndexOf('.'));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				return;
			}
		}
		delim = ", ";
		int i = 0;
		int dotpos = aString.indexOf('.');
		String typeStr = "";
		if (aString.indexOf("TYPE") > 0)
		{
			typeStr = aString.substring(dotpos);
		}

		Globals.sortPObjectList(aArrayList);
		Feat aFeat;
		for (Iterator e = aArrayList.iterator(); e.hasNext();)
		{
			aFeat = (Feat) e.next();

			String test;   //will hold token we are testing against
			StringTokenizer st = null;
			int clusive = 0;  //clusive 0 = no type, 1 = inclusive(.TYPE), 2 = exclusive(.!TYPE=)
			int match = 0;	  //match  0 = does not match type, 1 = feat matches type

			//Test for inclusive/exclusive
			if (aString.indexOf(".TYPE=") >= 0)
			{
				clusive = 1;
//				st = new StringTokenizer(typeStr, ".TYPE=");
				st = new StringTokenizer(typeStr.substring(5), "=");
			}
			else if (aString.indexOf(".!TYPE=") >= 0)
			{
				clusive = 2;
//				st = new StringTokenizer(typeStr, ".!TYPE=");
				st = new StringTokenizer(typeStr.substring(6), "=");
			}
			if (clusive == 1 || clusive == 2)
			{
				while (st.hasMoreTokens())
				{
					test = st.nextToken();
					if (aFeat.isType(test))
					{
						match = 1;
					}
				}
			}
			int doIprint = 1;
			if ((clusive == 1 && match == 0) || (clusive == 2 && match == 1))
			{
				doIprint = 0;
			}
			if (doIprint == 1)
			{
				if ((aFeat.isVisible() == Feat.VISIBILITY_DEFAULT) || (aFeat.isVisible() == Feat.VISIBILITY_OUTPUT_ONLY))
				{
					if (i > 0)
					{
						FileAccess.write(output, delim);
					}
					FileAccess.encodeWrite(output, aFeat.qualifiedName());
					++i;
				}
			}
		}
	}

	private static void printFeatPoints(final double numFeats, BufferedWriter output)
	{
		if (!Utility.doublesEqual(numFeats, 0))
		{
			FileAccess.encodeWrite(output, Double.toString(numFeats));
		}
	}
	/*
	 * ##########################################################################
	 * various helper methods
	 * ##########################################################################
	 */

	private static void writeToken(final int value, BufferedWriter output)
	{
		FileAccess.write(output, Delta.toString(value));
	}

	private List getLineForBio()
	{
		final List aArrayList = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer(aPC.getBio(), "\r\n", false);
		while (aTok.hasMoreTokens())
		{
			aArrayList.add(aTok.nextToken());
		}
		return aArrayList;
	}

	private List getLineForDesc()
	{
		final List aArrayList = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer(aPC.getDescription(), "\r\n", false);
		while (aTok.hasMoreTokens())
		{
			aArrayList.add(aTok.nextToken());
		}
		return aArrayList;
	}

	private List getLineForMiscList(int index)
	{
		final List aArrayList = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer((String) aPC.getMiscList().get(index), "\r\n", false);
		while (aTok.hasMoreTokens())
		{
			aArrayList.add(aTok.nextToken());
		}
		return aArrayList;
	}

	private void generateContainerList(List anArray)
	{
		final int equipmentLocation = anArray.size() - 1;
		final Equipment anEquip = (Equipment) anArray.get(equipmentLocation);
		Equipment eq;
		for (Iterator e = aPC.getEquipmentListInOutputOrder().iterator(); e.hasNext();)
		{
			eq = (Equipment) e.next();
			if (anEquip.containsContainedEquipment(eq))
			{
				anArray.add(eq);
				if (eq.getChildCount() > 0)
				{
					generateContainerList(anArray);
				}
			}
		}
	}

	private String getItemDescription(String sType, String sKey, String sAlt)
	{
		if (SettingsHandler.isROG())
		{
			if ("EMPTY".equals(aPC.getDescriptionLst()))
			{
				aPC.loadDescriptionFilesInDirectory("descriptions");
			}
			String aDescription = sAlt;
			final String aSearch = sType.toUpperCase() + ":" + sKey + Constants.s_LINE_SEP;
			final int pos = aPC.getDescriptionLst().indexOf(aSearch);
			if (pos >= 0)
			{
				aDescription = aPC.getDescriptionLst().substring(pos + aSearch.length());
				aDescription = aDescription.substring(0, aDescription.indexOf("####") - 1).trim();
			}
			return aDescription;
		}
		else
		{
			return sAlt;
		}
	}

	private static int doOffhandMod(PlayerCharacter aPC, int myMod)
	{
		if (myMod <= 0)
		{
			return myMod;
		}

		final int div = aPC.getTwoHandDamageDivisor();
		if ((myMod % div) == 0)
		{
			return ((myMod / div) + (int) aPC.getTotalBonusTo("COMBAT", "SECONDARYDAMAGE"));
		}
		else
		{
			if ((int) aPC.getTotalBonusTo("COMBAT", "SECONDARYDAMAGE") == 0)
			{
				return (myMod / div);
			}
			else
			{
				return ((myMod / div) + (int) aPC.getTotalBonusTo("COMBAT", "SECONDARYDAMAGE") + 1);
			}
		}
	}

	/*
	 * ##########################################################################
	 * inner classes
	 * ##########################################################################
	 */

	/**
	 * <code>PStringTokenizer</code>
	 *
	 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
	 * @version $Revision: 1.1 $
	 */
	private static final class PStringTokenizer
	{
		private String _forThisString = "";
		private String _delimiter = "";
		private String _ignoreBetweenThis = "";
		private String _andThat = "";

		public String nextToken()
		{
			String aString;
			int ignores = 0;
			if (_forThisString.lastIndexOf(_delimiter) == -1)
			{
				aString = _forThisString;
				_forThisString = "";
			}
			else
			{
				int i;
				final StringBuffer b = new StringBuffer();
				for (i = 0; i < _forThisString.length(); i++)
				{
					if (_forThisString.substring(i).startsWith(_delimiter) && ignores == 0)
					{
						break;
					}
					if (_forThisString.substring(i).startsWith(_ignoreBetweenThis) && ignores == 0)
					{
						ignores = 1;
					}
					else if (_forThisString.substring(i).startsWith(_andThat))
					{
						ignores = 0;
					}
					b.append(_forThisString.substring(i, i + 1));
				}
				aString = b.toString();
				_forThisString = _forThisString.substring(i + 1);
			}
			return aString;
		}

		public boolean hasMoreTokens()
		{
			return (_forThisString.length() > 0);
		}

		PStringTokenizer(String forThisString, String delimiter, String ignoreBetweenThis, String andThat)
		{
			_forThisString = forThisString;
			_delimiter = delimiter;
			_ignoreBetweenThis = ignoreBetweenThis;
			_andThat = andThat;
		}
	}

	private static String weaponTypes(Equipment eq, boolean primary)
	{
		StringBuffer wt = new StringBuffer(10);
		StringTokenizer aTok = new StringTokenizer(SettingsHandler.getGame().getWeaponTypes(), "|", false);
		while (aTok.countTokens() >= 2)
		{
			final String aType = aTok.nextToken();
			final String abbrev = aTok.nextToken();
			if (eq.isType(aType, primary))
			{
				wt.append(abbrev);
			}
		}
		return wt.toString();
	}

	private static String weaponTypes(Equipment eq)
	{
		String types = weaponTypes(eq, true);
		if (eq.isDouble())
		{
			types += '/' + weaponTypes(eq, false);
		}
		return types;
	}

	private static String weaponCategories(Equipment eq)
	{
		StringBuffer wc = new StringBuffer(10);
		StringTokenizer aTok = new StringTokenizer(SettingsHandler.getGame().getWeaponCategories(), "|", false);
		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();
			if (eq.isType(aType, true))
			{
				if (wc.length() != 0)
				{
					wc.append('/');
				}
				wc.append(aType);
			}
		}
		if (wc.length() == 0)
		{
			wc.append("Non-Standard");
		}
		return wc.toString();
	}

	//
	// Adjust damage of natural weapons based on size increase (MM p.14)
	//
	private static String getDamage(final PlayerCharacter argPC, final Equipment eq)
	{
		String retString = eq.getDamage();
		if ((argPC != null) && (eq.isNatural()))
		{
			retString = Globals.adjustDamage(retString, Globals.sizeInt(argPC.getRace().getSize()), argPC.sizeInt());
		}
		return retString;
	}

	private static int getPCReach(final PlayerCharacter aPC)
	{
		if (aPC.getRace() != null)
		{
			return aPC.getRace().getReach() + (int) aPC.getTotalBonusTo("COMBAT", "REACH");
		}
		return 0;
	}

	public static String calcBonusValue(final PlayerCharacter aPC, final String aString)
	{
		final StringTokenizer bonusTok = new StringTokenizer(aString, ".", false);
		bonusTok.nextToken(); // should be BONUS
		final String aType = bonusTok.nextToken(); // type of bonus
		final String aName = bonusTok.nextToken(); // name of bonus
		double total = 0;
		int decimals = 0;
		double lastValue = 0;
		int signIt = 1;

		while (bonusTok.hasMoreTokens())
		{
			String bucket = bonusTok.nextToken();
			if (Utility.doublesEqual(total, 0.0) && "LISTING".equals(bucket))
			{
				return aPC.listBonusesFor(aType + "." + aName);
			}
			if ((bucket.startsWith("PRE") || bucket.startsWith("!PRE")) && bucket.indexOf(':') >= 0)
			{
				PObject a = new PObject();
				a.addPreReq(bucket);
				if (!a.passesPreReqToGain())
				{
					total -= lastValue * signIt;
					lastValue = 0;
				}
				continue;
			}
			if (bucket.startsWith("MIN="))
			{
				double x = Float.parseFloat(bucket.substring(4));
				if (lastValue < x)
				{
					total -= lastValue - x;
				}
				continue;
			}
			else if (bucket.startsWith("MAX="))
			{
				double x = Float.parseFloat(bucket.substring(4));
				x = Math.min(x, lastValue);
				total -= lastValue - x;
				lastValue = 0;
				continue;
			}
			signIt = 1;
			if ((bucket.length() > 0) && (bucket.charAt(0) == '!'))
			{
				signIt = -1;
				bucket = bucket.substring(1);
			}
			if (bucket.equals("EQTYPE") && bonusTok.hasMoreTokens())
			{
				bucket += "." + bonusTok.nextToken();
			}
			if ("TOTAL".equals(bucket))
			{
				lastValue = aPC.getTotalBonusTo(aType, aName);
			}
			else if (bucket.startsWith("DEC="))
			{
				decimals = Integer.parseInt(bucket.substring(4));
			}
			else if (bucket.startsWith("TYPE=") || bucket.startsWith("EQTYPE."))
			{
				lastValue = 0;
				String restOfBucket;
				if (bucket.startsWith("TYPE="))
				{
					restOfBucket = bucket.substring(5);
				}
				else
				{
					restOfBucket = bucket.substring(7);
				}
				List aList = aPC.getEquipmentOfType(restOfBucket, "", 1);
				if (!aList.isEmpty())
				{
					for (Iterator e = aList.iterator(); e.hasNext();)
					{
						Equipment eq = (Equipment) e.next();
						lastValue += eq.bonusTo(aType, aName, true);
					}
				}
			}
			else
			{
				lastValue = aPC.getBonusDueToType(aType, aName, bucket);
			}
			total += lastValue * signIt;
		}
		return String.valueOf((int) (total * Math.pow(10, decimals)) / (int) Math.pow(10, decimals));
	}
}
