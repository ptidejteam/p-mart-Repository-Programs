/*
 * PrereqHandler.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:27:55 $
 *
 */

package pcgen.core.prereq;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SystemCollections;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.GuiFacade;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PrereqHandler {
	public static final int COMPARETYPE_UNKNOWN = -1;
	public static final int COMPARETYPE_GT = 0;
	public static final int COMPARETYPE_GTEQ = 1;
	public static final int COMPARETYPE_LT = 2;
	public static final int COMPARETYPE_LTEQ = 3;
	public static final int COMPARETYPE_NEQ = 4;
	public static final int COMPARETYPE_EQ = 5;

	public static boolean passesPreReqToGainForList(PObject caller, PlayerCharacter aPC, PObject aObj, List argList)
	{
		return passesPreReqForList(caller, aPC, aObj, argList, new PreReqTestsToGain());
	}

	public static boolean passesPreReqToUseForList(PObject caller, PlayerCharacter aPC, PObject aObj, List argList)
	{
		return passesPreReqForList(caller, aPC, aObj, argList, new PreReqTestsToUse());
	}
	
	/**
	 * misc prereq tags:
	 * <ul>
	 * <li> RESTRICT
	 * </ul>
	 *
	 * possible PRExxx tags:
	 *
	 * <ul>
	 * <li> PREALIGN
	 * <li> PREAPPLY
	 * <li> PREATT
	 * <li> PREUATT
	 * <li> PREARMORPROF
	 * <li> PREARMORTYPE
	 *
	 * <li> PREBIRTHPLACE
	 *
	 * <li> PRECHECK
	 * <li> PRECHECKBASE
	 * <li> PRECLASS
	 * <li> PRESUBCLASS
	 * <li> PRECLASSLEVELMAX
	 * <li> PRECITY
	 *
	 * <li> PREDEITY
	 * <li> PREDEITYALIGN
	 * <li> PREDEITYDOMAIN
	 * <li> PREDOMAIN
	 *
	 * <li> PREEQUIP
	 * <li> PREEQUIPPRIMARY
	 * <li> PREEQUIPSECONDARY
	 * <li> PREEQUIPBOTH
	 * <li> PREEQUIPTWOWEAPON
	 *
	 * <li> PREFEAT
	 *
	 * <li> PREGENDER
	 *
	 * <li> PREHD
	 * <li> PREHP
	 *
	 * <li> PREHANDSEQ
	 * <li> PREHANDSGT
	 * <li> PREHANDSGTEQ
	 * <li> PREHANDSLT
	 * <li> PREHANDSLTEQ
	 * <li> PREHANDSNEQ
	 *
	 * <li> PREITEM
	 *
	 * <li> PRELANG
	 * <li> PRELEVEL
	 * <li> PRELEVELMAX
	 *
	 * <li> PRELEGSEQ
	 * <li> PRELEGSGT
	 * <li> PRELEGSGTEQ
	 * <li> PRELEGSLT
	 * <li> PRELEGSLTEQ
	 * <li> PRELEGSNEQ
	 *
	 * <li> PREMOVE
	 *
	 * <li> PRERACE
	 * <li> PREREGION
	 *
	 * <li> PRESIZEEQ
	 * <li> PRESIZEGT
	 * <li> PRESIZEGTEQ
	 * <li> PRESIZELT
	 * <li> PRESIZELTEQ
	 * <li> PRESIZENEQ
	 * <li> PREBASESIZEEQ
	 * <li> PREBASESIZEGT
	 * <li> PREBASESIZEGTEQ
	 * <li> PREBASESIZELT
	 * <li> PREBASESIZELTEQ
	 * <li> PREBASESIZENEQ
	 *
	 * <li> PRESKILL
	 * <li> PRESKILLMULT
	 * <li> PRESKILLTOT
	 *
	 * <li> PRESPELL
	 * <li> PRESPELLCAST
	 * <li> PRESPELLBOOK
	 * <li> PRESPELLTYPE
	 * <li> PRESPELLSCHOOL
	 * <li> PRESPELLSCHOOLSUB
	 *
	 * <li> PRESTAT
	 * <li> PRESTATEQ
	 * <li> PRESTATGT
	 * <li> PRESTATGTEQ
	 * <li> PRESTATLT
	 * <li> PRESTATLTEQ
	 * <li> PRESTATNEQ
	 *
	 * <li> PRESA
	 *
	 * <li> PRESREQ
	 * <li> PRESRGT
	 * <li> PRESRGTEQ
	 * <li> PRESRLT
	 * <li> PRESRLTEQ
	 * <li> PRESRNEQ
	 *
	 * <li> PRETEMPLATE
	 * <li> PRETYPE
	 *
	 * <li> PREVAR
	 *
	 * <li> PREARMORPROF
	 * <li> PREWEAPONPROF
	 * <li> PRESHIELDPROF
	 *
	 * </ul>
	 */
	private static  boolean passesPreReqForList(PObject caller, PlayerCharacter aPC, PObject aObj, List argList, PreReqTests preReqTests)
	{
		boolean qValue = false; // Qualify overide testing.
		boolean qualifyValue = false; // Qualify overide testing.

		if ((argList == null) || argList.isEmpty())
		{
			return true;
		}

		boolean flag = false;
		boolean invertFlag; // Invert return value for !PRExxx tags


		for (Iterator e = argList.iterator(); e.hasNext();)
		{
			flag = false;
			invertFlag = false;

			PreReqState preReqState = new PreReqState( ((String) e.next()).toUpperCase() );
			preReqState.setTheObj( caller );
			
			
			final StringTokenizer aaTok = new StringTokenizer(preReqState.toString(), ":");
			preReqState.setKind( aaTok.nextToken() );
			if (aaTok.hasMoreTokens())
			{
				preReqState.setParameterString( aaTok.nextToken() );
			}
			else
			{
				preReqState.setParameterString("");
			}

			//check for inverse prereqs.  They start with a "!"
			if (preReqState.getKind().length() > 0 && preReqState.getKind().charAt(0) == '!')
			{
				invertFlag = true;
				preReqState.setKind( preReqState.getKind().substring(1) );
			}

			// The PRE is stripped of in the 
			//   PreReqTestsToGain  or  PreReqTestsToUse
			// procedures, so don't do it here

			// This adds 'Q' onto the PRExxx syntax, which
			// overrides QUALIFY. This allows selected prereqs
			// to be over-ridden but not all.
			// Which is needed for things like regional feats.
			if ("Q".equals(preReqState.getParameterString()))
			{
				qValue = true;
				if (aaTok.hasMoreTokens())
				{
					preReqState.setParameterString(aaTok.nextToken());
				}
			}

			if (aPC != null)
			{
				if (aPC.checkQualifyList(caller.getName()) && !qValue)
				{
					qualifyValue = true;
				}
				flag = preReqTests.passesTests(aPC, aObj, argList, preReqState);
			}
			else if (aObj != null)
			{
				flag = preReqTests.passesTests(aPC, aObj, argList, preReqState);
			}

			if (qValue)
			{
				qualifyValue = false;
			}
			qValue = false;

			if (invertFlag)
			{
				flag = !flag;
			}
			if (!flag && !qualifyValue)
			{
				return flag;
			}
		}
		if (qualifyValue)
		{
			flag = true;
		}
		return flag;
	}



	public static final String preReqStringsForList(List anArrayList)
	{
		if (anArrayList.isEmpty())
		{
			return "";
		}

		final StringBuffer pString = new StringBuffer(anArrayList.size() * 20);

		StringTokenizer aTok;
		String aType;
		String aList;
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{

			String aString = (String) e.next();
			aTok = new StringTokenizer(aString, ":");
			aType = aTok.nextToken();
			aList = aTok.nextToken();

			int i = 0;
			if (pString.length() > 0)
			{
				pString.append("  ");
			}
			if (aType.length() > 0 && aType.charAt(0) == '!')
			{
				pString.append('!');
				aType = aType.substring(1);
			}
			if ("PRECLASS".equals(aType))
			{
				aTok = new StringTokenizer(aList, ",");
				pString.append("CLASS:");
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
					{
						pString.append(',');
					}
					pString.append(aTok.nextToken());
				}
			}
			else if ("PREATT".equals(aType))
			{
				pString.append("ATT=");
				pString.append(aList);
			}
			else if ("PREUATT".equals(aType))
			{
				pString.append("UATT=");
				pString.append(aList);
			}
			//else if ("PRESTAT".equals(aType))
			else if (aType.startsWith("PRESTAT"))
			{
				String comp = aType.substring(7);
				if (comp.length() == 0)
				{
					comp = "GTEQ";
				}
				switch (PrereqHandler.getComparisonType(comp))
				{
					case PrereqHandler.COMPARETYPE_EQ:
						comp = "=";
						break;

					case PrereqHandler.COMPARETYPE_LT:
						comp = "<";
						break;

					case PrereqHandler.COMPARETYPE_LTEQ:
						comp = "<=";
						break;

					case PrereqHandler.COMPARETYPE_GT:
						comp = ">";
						break;

					case PrereqHandler.COMPARETYPE_GTEQ:
						comp = ">=";
						break;

					case PrereqHandler.COMPARETYPE_NEQ:
						comp = "!=";
						break;

					case PrereqHandler.COMPARETYPE_UNKNOWN:
					default:
						comp = "??";
						break;
				}
				pString.append("Stat:");
				aTok = new StringTokenizer(aList, "=", true);
				while (aTok.hasMoreTokens())
				{
					final String t = aTok.nextToken();
					if (t.equals("="))
					{
						pString.append(comp);
					}
					else
					{
						pString.append(t);
					}
				}
			}
			else if ("PREDEITYDOMAIN".equals(aType))
			{
				pString.append("Deity Domain=");
				pString.append(aList);
			}
			else if ("PREMULT".equals(aType))
			{
				pString.append("Multiple reqs.=");
				pString.append(aList.substring(0, aList.indexOf(",")));

				while (aTok.hasMoreTokens())
				{
					aList = aList + ":" + aTok.nextToken();
				}
				aList = aList.substring(aList.indexOf(",") + 1);
				for (int j = 0, nesting = 0, start = 0; j < aList.length(); j++)
				{
					if (aList.charAt(j) == '[')
					{
						++nesting;
					}
					else if (aList.charAt(j) == ']')
					{
						--nesting;
					}
					if (nesting == 0 && aList.charAt(j) == ',')
					{
						List preList = new ArrayList();
						preList.add(aList.substring(start + 1, j - 1));
						pString.append("[" + preReqStringsForList(preList) + "]");
						start = j + 1;
					}
					if (nesting == 0 && j == aList.length() - 1)
					{
						List preList = new ArrayList();
						preList.add(aList.substring(start + 1, aList.length() - 1));
						pString.append("[" + preReqStringsForList(preList) + "]");
					}
					if (nesting < 0 || (nesting > 0 && j == aList.length() - 1))
					{
						Logging.errorPrint("PREMULT Incorrect [] nesting.");
						break;
					}
				}
			}
			else if ("PREDEITYALIGN".equals(aType))
			{
				aTok = new StringTokenizer(aList, ",");
				pString.append("Deity Alignment:");
				while (aTok.hasMoreTokens())
				{

					String tok = aTok.nextToken();
					int raceNumber;
					try
					{
						raceNumber = Integer.parseInt(tok);
					}
					catch (NumberFormatException nfe)
					{
						Logging.errorPrint("Badly formed PREDEITYALIGN attribute: " + tok);
						raceNumber = -1;
					}

					if ((raceNumber >= 0) && (raceNumber < SystemCollections.getUnmodifiableAlignmentList().size()))
					{
						if (i++ > 0)
						{
							pString.append(',');
						}
						pString.append(SystemCollections.getShortAlignmentAtIndex(raceNumber));
					}
					else
					{
						GuiFacade.showMessageDialog(null, "Invalid alignment: " + raceNumber, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
					}
				}
			}
			else if ("PREALIGN".equals(aType))
			{
				aTok = new StringTokenizer(aList, ",");
				pString.append("Alignment:");
				while (aTok.hasMoreTokens())
				{

					int alignNumber;
					int idx = -1;
					String preAlign = aTok.nextToken();

					//
					// Check for [blah=blah=#],#,#,#,#
					//
					try
					{
						if (preAlign.length() > 0 && preAlign.charAt(0) == '[' && preAlign.endsWith("]"))
						{
							idx = preAlign.lastIndexOf('=');
							try
							{
								alignNumber = Integer.parseInt(preAlign.substring(idx + 1, preAlign.length() - 1));
							}
							catch (NumberFormatException nfe)
							{
								Logging.errorPrint("Badly formed PREALIGN/alignNumber attribute: " + preAlign.substring(idx + 1, preAlign.length() - 1));
								alignNumber = -1;
							}

						}
						else
						{
							try
							{
								alignNumber = Integer.parseInt(preAlign);
							}
							catch (NumberFormatException nfe)
							{
								Logging.errorPrint("Badly formed PREALIGN/alignNumber attribute: " + preAlign);
								alignNumber = -1;
							}

						}
						if ((alignNumber >= 0) && (alignNumber < SystemCollections.getUnmodifiableAlignmentList().size()))
						{
							if (i++ > 0)
							{
								pString.append(',');
							}
							if (idx >= 0)
							{
								pString.append(preAlign.substring(0, idx + 1));
							}
							pString.append(SystemCollections.getShortAlignmentAtIndex(alignNumber));
							if (idx >= 0)
							{
								pString.append(']');
							}
						}
						else
						{

							String msg = "Invalid alignment: " + alignNumber;
							Logging.errorPrint(msg);
							GuiFacade.showMessageDialog(null, msg, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
						}
					}
					catch (Exception exc)
					{

						String msg = "Invalid alignment: " + preAlign;
						Logging.errorPrint(msg, exc);
						GuiFacade.showMessageDialog(null, msg, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
					}
				}
			}
			else
			{
				pString.append(aType.substring(3)).append(':');
				aTok = new StringTokenizer(aList, ",");
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
					{
						pString.append(',');
					}
					pString.append(aTok.nextToken());
				}
			}
		}
		return pString.toString();
	}
	



	public static boolean passesPreVar(String aList, String aType, PlayerCharacter aPC)
	{
		return passesPreVar(aList, aType, aPC, null);
	}

	public static boolean passesPreVar(String aList, String aType, PlayerCharacter aPC, Equipment aEq)
	{
		boolean flag;

		final StringTokenizer aTok = new StringTokenizer(aList, ",");
		flag = true;

		int i = 0;
		if (aType.endsWith("GT"))
		{
			i = 0;
		}
		else if (aType.endsWith("GTEQ"))
		{
			i = 1;
		}
		else if (aType.endsWith("LT"))
		{
			i = 2;
		}
		else if (aType.endsWith("LTEQ"))
		{
			i = 3;
		}
		else if (aType.endsWith("NEQ"))
		{
			i = 4;
		}
		else if (aType.endsWith("EQ"))
		{
			i = 5;
		}
		while (aTok.hasMoreTokens() && flag)
		{
			final String varName = aTok.nextToken();
			String valString = "0";
			if (aTok.hasMoreTokens())
			{
				valString = aTok.nextToken();
			}

			final float aTarget;
			final float aVar;
			if (aEq == null)
			{
				aTarget = aPC.getVariableValue(valString, "").floatValue();
				aVar = aPC.getVariable(varName, true, true, "", "").floatValue();
			}
			else
			{
				String eqVar = "EQ:" + aEq.profName(0);
				aTarget = aEq.getVariableValue(valString, eqVar, "").floatValue();
				aVar = aEq.getVariableValue(varName, eqVar, "").floatValue();
			}
			switch (i)
			{

				case 0:
					flag = (aVar > aTarget);
					break;

				case 1:
					flag = (aVar >= aTarget);
					break;

				case 2:
					flag = (aVar < aTarget);
					break;

				case 3:
					flag = (aVar <= aTarget);
					break;

				case 4:
					flag = !(Utility.doublesEqual(aVar, aTarget));
					break;

				case 5:
					flag = (Utility.doublesEqual(aVar, aTarget));
					break;

				default:
					Logging.errorPrint("In PObject.passesPreTestForLists the prevar type " + i + " is unsupported.");
					break;
			}
		}
		return flag;
	}













	/**
	 * Check to see if passes TYPE test
	 * Format is:
	 * PRETYPE:#,type1,type2
	 * Where the number represents how many of the types have to match
	 *
	 * @param aList is the string of pretypes
	 * @param aObj  is the object being tested
	 * @return false if it doesn't pass, true if it does
	 */
	public static boolean passesPreType(String aList, PObject aObj)
	{
		if (aObj == null)
		{
			return true;
		}

		int aNum = 1;
		final StringTokenizer aTok = new StringTokenizer(aList, ",");
		String aString = aTok.nextToken();
		try
		{
			aNum = Integer.parseInt(aString);
		}
		catch (NumberFormatException nfe)
		{
			// First token was not a number,
			// must be old style syntax.
			return passesOldPreType(aList, aObj);
		}
		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();
			if (aObj instanceof Equipment)
			{
				if (((Equipment) aObj).isPreType(aString))
				{
					--aNum;
				}
			}
			else
			{
				if (aObj.isType(aString))
				{
					--aNum;
				}
			}
		}
		return (aNum <= 0);
	}

	public static boolean passesOldPreType(String aList, PObject aObj)
	{
		// This one uses:
		//  PRETYPE:type1,type2|type3

		final StringTokenizer aTok = new StringTokenizer(aList, ",|", true);
		int iLogicType = 0; // AND
		boolean aFlag = true;
		if (aObj != null)
		{
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (",".equals(aString))
				{
					// AND
					iLogicType = 0;
				}
				else if ("|".equals(aString))
				{
					// OR
					iLogicType = 1;
				}
				else
				{
					boolean bIsType;
					boolean bInvert = false;
					if (aString.length() > 0 && aString.charAt(0) == '[' && aString.endsWith("]"))
					{
						aString = aString.substring(1, aString.length() - 1);
						bInvert = true;
					}

					if (aObj instanceof Equipment)
					{
						bIsType = ((Equipment) aObj).isPreType(aString);
					}
					else
					{
						bIsType = aObj.isType(aString);
					}
					if (bInvert)
					{
						bIsType = !bIsType;
					}

					if (iLogicType == 0)
					{
						aFlag &= bIsType;
					}
					else
					{
						aFlag |= bIsType;
					}
				}
			}
		}
		return aFlag;
	}

	public static boolean passesRestrict(String aList, boolean flag, PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, ",");
		while (aTok.hasMoreTokens() && !flag)
		{

			final String aString = aTok.nextToken();
			final PCClass aClass = aPC.getClassNamed(aString);
			flag = (aClass == null);
		}
		return flag;
	}

	public static final int getComparisonType(final String aString)
	{
		if ("EQ".equals(aString))
		{
			return COMPARETYPE_EQ;
		}
		else if ("LT".equals(aString))
		{
			return COMPARETYPE_LT;
		}
		else if ("LTEQ".equals(aString))
		{
			return COMPARETYPE_LTEQ;
		}
		else if ("GT".equals(aString))
		{
			return COMPARETYPE_GT;
		}
		else if ("GTEQ".equals(aString))
		{
			return COMPARETYPE_GTEQ;
		}
		else if ("NEQ".equals(aString))
		{
			return COMPARETYPE_NEQ;
		}
		return COMPARETYPE_UNKNOWN;
	}



	public static boolean doComparison(String comparison, int iVal1, int iVal2)
	{
		return PrereqHandler.doComparison(comparison, iVal1, iVal2, null, null);
	}



	public static boolean doComparison(String comparison, int iVal1, int iVal2, PlayerCharacter aPC, String tag)
	{
		if ((aPC != null) && (tag != null))
		{
			iVal1 += (int) aPC.getTotalBonusTo("SLOTS", tag);
		}

		switch (getComparisonType(comparison))
		{
			case COMPARETYPE_EQ:
				return iVal1 == iVal2;

			case COMPARETYPE_LT:
				return iVal1 < iVal2;

			case COMPARETYPE_LTEQ:
				return iVal1 <= iVal2;

			case COMPARETYPE_GT:
				return iVal1 > iVal2;

			case COMPARETYPE_GTEQ:
				return iVal1 >= iVal2;

			case COMPARETYPE_NEQ:
				return iVal1 != iVal2;

			default:
				break;
		}
		Logging.errorPrint("Prereq failed, unknown comparison: " + comparison);
		return false;
	}


	//PREMULT:#,[PRExxx],[PRExxx],[!PRExxx].....
	public static boolean passesPreMult(PObject caller, PlayerCharacter aPC, PObject aObj, String paramString)
	{
		StringTokenizer aTok = new StringTokenizer(paramString, ",");
		int qtdMult;
		try
		{
			qtdMult = Integer.parseInt(aTok.nextToken());
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Incorrect PREMULT parameter. Assuming 1");
			qtdMult = 1;
		}
		int aIndex = paramString.indexOf(",");
		if (aIndex < 0)
		{
			Logging.errorPrint("PREMULT formatted incorrectly.");
			return false;
		}
		String aList = paramString.substring(aIndex + 1);
		List preList = new ArrayList();
		int qtdPreList = 1;
		for (int i = 0, nesting = 0, start = 0; i < aList.length(); i++)
		{
			if (aList.charAt(i) == '[')
			{
				++nesting;
			}
			else if (aList.charAt(i) == ']')
			{
				--nesting;
			}
			if (nesting == 0 && aList.charAt(i) == ',')
			{
				++qtdPreList;
				preList.add(aList.substring(start, i));
				start = i + 1;
			}
			if (nesting == 0 && i == aList.length() - 1)
			{
				preList.add(aList.substring(start));
			}
			if (nesting < 0 || (nesting > 0 && i == aList.length() - 1))
			{
				Logging.errorPrint("PREMULT Incorrect [] nesting.");
				return false;
			}
		}
		for (Iterator i = preList.iterator(); i.hasNext();)
		{
			String aString = (String) i.next();
			aString = aString.substring(1, aString.length() - 1);
			final List argList = new ArrayList();
			argList.add(aString);
			if (passesPreReqToGainForList(caller, aPC, aObj, argList))
			{
				--qtdMult;
			}
			--qtdPreList;
			if (qtdMult == 0)
			{
				return true;
			}
			if (qtdMult > qtdPreList)
			{
				return false;
			}
		}
		return false;
	}

}
