/*
 * UIFactory.java
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
 * Created on xxxx xx, xxxx, xx:xx PM
 */

package pcgen.gui;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.SkinManager;
import pcgen.util.Logging;
import pcgen.util.SkinLFResourceChecker;

/**
 * <code>UIFactory</code>.
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public final class UIFactory
{
	private static String[][] lafData;

	private static final int NAME = 0;
	private static final int CLASSNAME = 1;
	private static final int TOOLTIP = 2;

	private static final int systemIndex = 0;
	private static final int crossPlatformIndex = 1;

	private static final boolean windowsPlatform = System.getProperty("os.name").startsWith("Windows ");

	static
	{
		// Add the Kunststoff L&F before asking the UIManager.
		UIManager.installLookAndFeel("Kunststoff", "com.incors.plaf.kunststoff.KunststoffLookAndFeel");

		try
		{
			if (SkinLFResourceChecker.getMissingResourceCount() == 0)
			{
				SkinManager.loadLinuxSkin();
			}
		}
		catch (Exception e)
		{
			//TODO: Really ignore?
		}

		UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();

		lafData = new String[lafInfo.length][3];

		lafData[0][NAME] = "System";
		lafData[0][CLASSNAME] = UIManager.getSystemLookAndFeelClassName();
		lafData[0][TOOLTIP] = "Sets the look to that of the System you are using";
		int j = 1;
		if (!lafData[0][CLASSNAME].equals(UIManager.getCrossPlatformLookAndFeelClassName()))
		{
			lafData[1][NAME] = "Java";
			lafData[1][CLASSNAME] = UIManager.getCrossPlatformLookAndFeelClassName();
			lafData[1][TOOLTIP] = "Sets the look to that of Java's cross platform look";
			j++;
		}

		for (int i = 0; i < lafInfo.length && j < lafData.length; i++)
		{
			lafData[j][CLASSNAME] = lafInfo[i].getClassName();
			if (!lafData[j][CLASSNAME].equals(UIManager.getSystemLookAndFeelClassName()) && !lafData[j][CLASSNAME].equals(UIManager.getCrossPlatformLookAndFeelClassName()))
			{
				lafData[j][NAME] = lafInfo[i].getName();
				lafData[j][TOOLTIP] = "Sets the look to " + lafData[j][NAME] + " look";
				j++;
			}
		}

		if (!isWindowsPlatform())
		{
			// Replace the broken Windows L&F which will
			// only run on M$ platforms with one that will
			// run everywhere.  No difference otherwise.
			for (int i = 0; i < lafInfo.length; ++i)
			{
				if (lafInfo[i].getClassName().endsWith("WindowsLookAndFeel"))
				{
					lafInfo[i] = new UIManager.LookAndFeelInfo(lafInfo[i].getName(), "pcgen.gui.FakeWindowsLookAndFeel");
					break;
				}

			}
		}
		UIManager.setInstalledLookAndFeels(lafInfo);

		if (!isWindowsPlatform())
		{
			for (int i = 0; i < lafData.length; ++i)
			{
				if (lafData[i][1].endsWith("WindowsLookAndFeel"))
				{
					lafData[i][1] = "pcgen.gui.FakeWindowsLookAndFeel";
					break;
				}
			}
		}
	}

	public static void initLookAndFeel()
	{
		if (SettingsHandler.getLookAndFeel() < lafData.length)
		{
			setLookAndFeel(new Integer(SettingsHandler.getLookAndFeel()));
		}
		else if (SettingsHandler.getLookAndFeel() == lafData.length)
		{
			try
			{
				//to get this case you should have already had skinlf.jar installed...
				if (SkinLFResourceChecker.getMissingResourceCount() == 0)
				{
					SkinManager.applySkin();
					//but just to be safe...
				}
				else
				{
					Logging.errorPrint(SkinLFResourceChecker.getMissingResourceMessage());
					setLookAndFeel(lafData[crossPlatformIndex][CLASSNAME]);
				}
			}
			catch (Exception e)
			{
				SettingsHandler.setLookAndFeel(0);
				setLookAndFeel(lafData[crossPlatformIndex][CLASSNAME]);
				GuiFacade.showMessageDialog(null, "There was a problem setting the skinned look and feel.\n" + "The look and feel has been reset to cross-platform.\nError: " + e.toString(), "PCGen", GuiFacade.ERROR_MESSAGE);
			}
		}
		else
		{
			SettingsHandler.setLookAndFeel(0);
			setLookAndFeel(lafData[crossPlatformIndex][CLASSNAME]);
		}
	}

	public static int indexOfCrossPlatformLookAndFeel()
	{
		return crossPlatformIndex;
	}

	public static int getLookAndFeelCount()
	{
		return lafData.length;
	}

	public static String getLookAndFeelName(int index)
	{
		if (index == lafData.length)
		{
			return "Skinned";
		}
		else
		{
			return lafData[index][NAME];
		}
	}

	public static String getLookAndFeelTooltip(int index)
	{
		if (index == lafData.length)
		{
			return "Sets the look to skinned";
		}
		else
		{
			return lafData[index][TOOLTIP];
		}
	}

	private static boolean isWindowsPlatform()
	{
		return windowsPlatform;
	}

	public static boolean isWindowsUI()
	{
		final String lnfName = getLookAndFeelName(SettingsHandler.getLookAndFeel());
		return (lnfName.equals("Windows") || lnfName.equals("System") && isWindowsPlatform());
	}

	private static void setLookAndFeel(Object looknfeel)
	{
		try
		{
			if (looknfeel instanceof String)
			{
				UIManager.setLookAndFeel((String) looknfeel);
			}
			else if (looknfeel instanceof LookAndFeel)
			{
				UIManager.setLookAndFeel((LookAndFeel) looknfeel);
			}
			else if (looknfeel instanceof Integer)
			{
				UIManager.setLookAndFeel(lafData[((Integer) looknfeel).intValue()][CLASSNAME]);
			}

			// Fix colors; themes which inherit from
			// MetalTheme change the colors because it's a
			// static member of MetalTheme (!), so when you
			// change back & forth, colors get wonked.
			final LookAndFeel laf = UIManager.getLookAndFeel();
			if (laf instanceof MetalLookAndFeel)
			{
				MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
			}

			// Expect exception for updating helpMenu before
			// it exists.
			PCGen_Frame1.getInst().menuBar.separateHelpMenu(!isWindowsUI());

		}
		catch (Exception e)
		{
			//Hardly a fatal error, and quite unlikely at that...
			Logging.errorPrint("Exception in UIFactory::setLookAndFeel", e);
		}
		SwingUtilities.updateComponentTreeUI(Globals.getRootFrame());
	}

	public static void setLookAndFeel(int looknfeel)
	{
		setLookAndFeel(new Integer(looknfeel));
	}

}
