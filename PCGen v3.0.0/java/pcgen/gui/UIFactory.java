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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.util.SkinLFResourceChecker;

/**
 * <code>UIFactory</code>.
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public class UIFactory
{
	private static String lafData[][];

	public static final int NAME = 0;
	public static final int CLASSNAME = 1;
	public static final int TOOLTIP = 2;

	private static int systemIndex = 0;
	private static int crossPlatformIndex = 1;

	private static boolean windowsPlatform = System.getProperty("os.name").startsWith("Windows ");

	static
	{
		UIManager.LookAndFeelInfo lafInfo[] = UIManager.getInstalledLookAndFeels();
		/*
		 * do not do this here already.
		 * wait for lafData to be initialized correctly/fully.
		 * author: Thomas Behr 07-03-02
		 */
//  		// Replace the broken Windows L&F which will only run
//  		// on M$ platforms with one that will run everywhere.
//  		// No difference otherwise.
//  		for (int i = 0; i < lafInfo.length; ++i)
//  		{
//  			if (!lafInfo[i].getName().equals("Windows"))
//  				continue;
//  			lafInfo[i] = new UIManager.LookAndFeelInfo("Windows", "pcgen.gui.FakeWindowsLookAndFeel");
//  			UIManager.setInstalledLookAndFeels(lafInfo);
//  			break;
//  		}

		lafData = new String[lafInfo.length][
			3];

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
			if (!lafData[j][CLASSNAME].equals(UIManager.getSystemLookAndFeelClassName()) &&
				!lafData[j][CLASSNAME].equals(UIManager.getCrossPlatformLookAndFeelClassName()))
			{
				lafData[j][NAME] = lafInfo[i].getName();
				lafData[j][TOOLTIP] = "Sets the look to " + lafData[j][NAME] + " look";
				j++;
			}
		}

		//
		// Don't replace on Windows boxes as there are several missing .gif's that won't pop up, which include:
		// pcgen.gui.FakeWindowsLookAndFeel/icons/Question.gif
		// pcgen.gui.FakeWindowsLookAndFeel/icons/Inform.gif
		// pcgen.gui.FakeWindowsLookAndFeel/icons/TreeLeaf.gif
		// pcgen.gui.FakeWindowsLookAndFeel/icons/TreeClosed.gif
		// pcgen.gui.FakeWindowsLookAndFeel/icons/TreeOpen.gif
		//
		if (!isWindowsPlatform())
		{
			// Replace the broken Windows L&F which will only run
			// on M$ platforms with one that will run everywhere.
			// No difference otherwise.
			for (int i = 0; i < lafInfo.length; i++)
			{
				if (lafInfo[i].getClassName().endsWith("WindowsLookAndFeel"))
				{
					lafInfo[i] = new javax.swing.UIManager.LookAndFeelInfo(lafInfo[i].getName(),
						"pcgen.gui.FakeWindowsLookAndFeel");
					break;
				}

			}
		}
		UIManager.setInstalledLookAndFeels(lafInfo);

		if (!isWindowsPlatform())
		{
			for (int i = 0; i < lafData.length; i++)
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
		if (SettingsHandler.getLooknFeel() < lafData.length)
		{
			setLookAndFeel(new Integer(SettingsHandler.getLooknFeel()));
		}
		else if (SettingsHandler.getLooknFeel() == lafData.length)
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
					Globals.errorPrint(SkinLFResourceChecker.getMissingResourceMessage());
					setLookAndFeel(lafData[crossPlatformIndex][CLASSNAME]);
				}
			}
			catch (Exception e)
			{
				SettingsHandler.setLooknFeel(0);
				setLookAndFeel(lafData[crossPlatformIndex][CLASSNAME]);
				JOptionPane.showMessageDialog(null, "There was a problem setting the skinned look and feel.\n" +
						"The look and feel has been reset to cross-platform.\nError: " + e.toString(),
						"PCGen", JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			SettingsHandler.setLooknFeel(0);
			setLookAndFeel(lafData[crossPlatformIndex][CLASSNAME]);
		}
	}

	public static int indexOfSystemLnF()
	{
		return systemIndex;
	}

	public static int indexOfCrossPlatformLnF()
	{
		return crossPlatformIndex;
	}

	public static int getLnFCount()
	{
		return lafData.length;
	}

	public static String getLnFName(int index)
	{
		if (index == lafData.length)
			return "Skinned";
		else
			return lafData[index][NAME];
	}

	public static String getLnFTooltip(int index)
	{
		if (index == lafData.length)
			return "Sets the look to skinned";
		else
			return lafData[index][TOOLTIP];
	}

	public static boolean isWindowsPlatform()
	{
		return windowsPlatform;
	}

	public static boolean isWindowsUI()
	{
		String lnfName = getLnFName(SettingsHandler.getLooknFeel());
		return (lnfName.equals("Windows")
			|| lnfName.equals("System")
			&& isWindowsPlatform());
	}

	public static void setLookAndFeel(Object looknfeel)
	{
		try
		{
			if (looknfeel instanceof String)
				UIManager.setLookAndFeel((String)looknfeel);
			else if (looknfeel instanceof javax.swing.LookAndFeel)
				UIManager.setLookAndFeel((javax.swing.LookAndFeel)looknfeel);
			else if (looknfeel instanceof Integer)
				UIManager.setLookAndFeel(lafData[((Integer)looknfeel).intValue()][CLASSNAME]);

			// Expect exception for updating helpMenu before
			// it exists.
			Globals.getRootFrame().menuBar.separateHelpMenu(!isWindowsUI());

		}
		catch (Exception e)
		{
			//Hardly a fatal error, and quite unlikely at that...
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(Globals.getRootFrame());
	}

	public static void setLookAndFeel(int looknfeel)
	{
		setLookAndFeel(new Integer(looknfeel));
	}

}
