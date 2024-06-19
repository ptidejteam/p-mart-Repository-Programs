/*
 * CustomData.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on November 23, 2002, 12:53 AM
 *
 * @(#) $Id: CustomData.java,v 1.1 2006/02/21 01:10:50 vauchers Exp $
 */

package pcgen.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.lst.CampaignOutput;
import pcgen.util.Logging;

/**
 * <code>CustomData</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

public final class CustomData
{
	public static void writeCustomFiles()
	{
		writePurchaseModeConfiguration();

		//
		// Don't trash the file if user exits before loading custom items
		//
		if (!PersistenceManager.isCustomItemsLoaded())
		{
			return;
		}
		writeCustomBioSet();
		writeCustomClasses();
		writeCustomDeities();
		writeCustomDomains();
		writeCustomFeats();
		writeCustomItems();
		writeCustomLanguages();
		writeCustomRaces();
		writeCustomSkills();
		writeCustomSpells();
		writeCustomTemplates();
		writeCustomSources();
	}

	public static BufferedReader getCustomEquipmentReader()
	{
		return getReader(customEquipmentFilePath());
	}

	private static BufferedReader getReader(final String path)
	{
		try
		{
			//return new BufferedReader(new FileReader(path));
			return new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		}
		catch (IOException e)
		{
			return null;
		}
	}

	private static BufferedWriter getCustomEquipmentWriter()
	{
		return getWriter(customEquipmentFilePath());
	}

	private static BufferedWriter getPurchaseModeWriter()
	{
		final String modeFile = SettingsHandler.getPcgenSystemDir() + File.separator + "pointbuymethods.lst";
		return getWriter(modeFile);
	}

	private static BufferedWriter getWriter(final String path)
	{
		try
		{
			//return new BufferedWriter(new FileWriter(path));
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
		}
		catch (IOException e)
		{
			return null;
		}
	}

	private static String getCustomPath(final String type, final boolean usePath)
	{
		String aString = "";
		if (usePath)
		{
			aString = SettingsHandler.getPccFilesLocation().getAbsolutePath();
		}

		return aString + File.separator + "customsources" + File.separator + "custom" + type + ".lst";
	}

	public static final String customBioSetFilePath(final boolean usePath)
	{
		return getCustomPath("BioSet", usePath);
	}

	public static final String customClassFilePath(final boolean usePath)
	{
		return getCustomPath("Classes", usePath);
	}

	public static final String customDeityFilePath(final boolean usePath)
	{
		return getCustomPath("Deities", usePath);
	}

	public static final String customDomainFilePath(final boolean usePath)
	{
		return getCustomPath("Domains", usePath);
	}

	private static final String customEquipmentFilePath()
	{
		return getCustomPath("Equipment", true);
	}

	public static final String customFeatFilePath(final boolean usePath)
	{
		return getCustomPath("Feats", usePath);
	}

	public static final String customLanguageFilePath(final boolean usePath)
	{
		return getCustomPath("Languages", usePath);
	}

	public static final String customRaceFilePath(final boolean usePath)
	{
		return getCustomPath("Races", usePath);
	}

	public static final String customSkillFilePath(final boolean usePath)
	{
		return getCustomPath("Skills", usePath);
	}

	public static final String customSpellFilePath(final boolean usePath)
	{
		return getCustomPath("Spells", usePath);
	}

	public static final String customTemplateFilePath(final boolean usePath)
	{
		return getCustomPath("Templates", usePath);
	}

	private static void writeCustomHeader(final BufferedWriter bw) throws IOException
	{
		bw.write("#");
		bw.newLine();
		bw.write("#This file auto-generated by PCGen. Do not edit manually.");
		bw.newLine();
		bw.write("#");
		bw.newLine();
		bw.write("SOURCELONG:Custom|SOURCESHORT:Custom");
		bw.newLine();
	}

	private static void writeCustomPObjects(String filename, Iterator it)
	{
		final BufferedWriter bw = getWriter(filename);
		if (bw == null)
		{
			return;
		}
		try
		{
			writeCustomHeader(bw);

			while(it.hasNext())
			{
				final PObject pobj = (PObject) it.next();
				if (pobj.isType(Constants.s_CUSTOM))
				{
					bw.write(pobj.getPCCText());
					bw.newLine();
				}
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error in writeCustomPObjects", e);
		}
		finally
		{
			try
			{
				if (bw != null)
				{
					bw.close();
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error in writeCustomPObjects while closing", ex);
			}
		}
	}

	private static void writeCustomBioSet()
	{
		final BufferedWriter bw = getWriter(customBioSetFilePath(true));
		if (bw == null)
		{
			return;
		}
		try
		{
			bw.write("#");
			bw.newLine();
			bw.write("#This file auto-generated by PCGen. Do not edit manually.");
			bw.newLine();
			bw.write("#");
			bw.newLine();

			for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext();)
			{
				final PObject pobj = (PObject) it.next();
				if (pobj.isType(Constants.s_CUSTOM))
				{
					String region = pobj.getRegionString();
					if (region == null)
					{
						region = Constants.s_NONE;
					}
					String name = pobj.getName();
					bw.write(BioSet.getRacePCCText(region, name));
					bw.newLine();
				}
			}

			// We are grouping the custom bio sets under the region of custom,
			// rather than types which are used elsewhere.
			// This done as type is not supported for bio sets.
			//bw.write(BioSet.getRegionPCCText("Custom"));
			bw.newLine();
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error in writeCustomBioSet", e);
		}
		finally
		{
			try
			{
				if (bw != null)
				{
					bw.close();
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error in writeCustomBioSet while closing", ex);
			}
		}

	}

	private static void writeCustomClasses()
	{
		writeCustomPObjects(customClassFilePath(true), Globals.getClassList().iterator());
	}

	private static void writeCustomDeities()
	{
		writeCustomPObjects(customDeityFilePath(true), Globals.getDeityList().iterator());
	}

	private static void writeCustomDomains()
	{
		writeCustomPObjects(customDomainFilePath(true), Globals.getDomainList().iterator());
	}

	private static void writeCustomFeats()
	{
		writeCustomPObjects(customFeatFilePath(true), Globals.getFeatList().iterator());
	}

	private static void writeCustomLanguages()
	{
		writeCustomPObjects(customLanguageFilePath(true), Globals.getLanguageList().iterator());
	}

	private static void writeCustomRaces()
	{
		writeCustomPObjects(customRaceFilePath(true), Globals.getRaceMap().values().iterator());
	}

	private static void writeCustomSkills()
	{
		writeCustomPObjects(customSkillFilePath(true), Globals.getSkillList().iterator());
	}

	private static void writeCustomSpells()
	{
		final BufferedWriter bw = getWriter(customSpellFilePath(true));
		if (bw == null)
		{
			return;
		}
		try
		{
			writeCustomHeader(bw);

			Iterator e = Globals.getSpellMap().values().iterator();
			while (e.hasNext())
			{
				final Object obj = e.next();
				if (obj instanceof ArrayList)
				{
					for (Iterator e2 = ((ArrayList) obj).iterator(); e2.hasNext();)
					{
						final Spell aSpell = (Spell) e2.next();
						if (aSpell.isType(Constants.s_CUSTOM))
						{
							bw.write(aSpell.getPCCText());
							bw.newLine();
						}
					}
				}
				else
				{
					if (((Spell) obj).isType(Constants.s_CUSTOM))
					{
						bw.write(((Spell) obj).getPCCText());
						bw.newLine();
					}
				}
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error in writeCustomSpells", e);
		}
		finally
		{
			try
			{
				if (bw != null)
				{
					bw.close();
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error in writeCustomSpells while closing", ex);
			}
		}
	}

	private static void writeCustomTemplates()
	{
		writeCustomPObjects(customTemplateFilePath(true), Globals.getTemplateList().iterator());
	}

	public static void writeCustomItems()
	{
		final BufferedWriter bw = getCustomEquipmentWriter();

		if (bw == null)
		{
			return;
		}

		try
		{
			bw.write("#This file auto-generated by PCGen. Do not edit manually.");
			bw.newLine();

			for (Iterator e = Globals.getEquipmentList().iterator(); e.hasNext();)
			{
				final Equipment aEq = (Equipment) e.next();
				if (aEq.isType(Constants.s_CUSTOM) && !aEq.isType("AUTO_GEN"))
				{
					aEq.save(bw);
				}
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error in writeCustomItems", e);
		}
		finally
		{
			try
			{
				if (bw != null)
				{
					bw.close();
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error in writeCustomItems while closing", ex);
			}
		}
	}

	public static void writePurchaseModeConfiguration()
	{
		final BufferedWriter bw = getPurchaseModeWriter();

		if (bw == null)
		{
			return;
		}

		try
		{
			bw.write("#");
			bw.newLine();
			bw.write("#This file auto-generated by PCGen. Do not edit manually.");
			bw.newLine();
			bw.write("#");
			bw.newLine();
			bw.write("# Point-buy ability score costs");
			bw.newLine();
			bw.write("#");
			bw.newLine();

			final int statMin = SettingsHandler.getPurchaseScoreMin();
			if (statMin >= 0)
			{
				final int statMax = SettingsHandler.getPurchaseScoreMax();
				final int[] statCosts = SettingsHandler.getAbilityScoreCost();
				for (int i = statMin; i <= statMax; ++i)
				{
					bw.write("STAT:" + Integer.toString(i) + "\t\tCOST:" + Integer.toString(statCosts[i - statMin]));
					bw.newLine();
				}
			}

			bw.write("#");
			bw.newLine();
			bw.write("# Point-buy methods");
			bw.newLine();
			bw.write("#");
			bw.newLine();
			for (int i = 0, x = SettingsHandler.getPurchaseMethodCount(); i < x; ++i)
			{
				final PointBuyMethod pbm = SettingsHandler.getPurhaseMethod(i);
				bw.write("METHOD:" + pbm.getMethodName() + "\t\tPOINTS:" + Integer.toString(pbm.getPoints()));
				bw.newLine();
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error in writePurchaseModeConfiguration", e);
		}
		finally
		{
			try
			{
				if (bw != null)
				{
					bw.close();
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error in writePurchaseModeConfiguration while closing", ex);
			}
		}
	}

	private static void writeCustomSources()
	{
		for (Iterator i = Globals.getCampaignList().iterator(); i.hasNext();)
		{
			Campaign c = (Campaign) i.next();
			if (c.getDestination().length() > 0)
			{
				CampaignOutput.output(c);
			}
		}
	}
}
