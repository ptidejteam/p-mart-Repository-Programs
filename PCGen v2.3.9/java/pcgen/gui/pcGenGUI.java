/*
 * pcGenGUI.java
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

package pcgen.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import pcgen.core.Globals;
import pcgen.util.SkinLFResourceChecker;


/**
 * <code>pcGenGUI</code> is the Main-Class for the application.
 * It creates an unreferenced copy of itself, basically so that
 * the constructor code is run.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class pcGenGUI
{
	/**
	 * Unknown. Doesn't appear to have a useful function. Kind of a
	 * debug argument, this decides whether the main frame is
	 * packed or validated. The value is only set to false, so
	 * this means the choice is always validate.
	 */
	private boolean packFrame = false;
	private static SplashScreen splash;


	/**
	 * Initialises the application and loads the main
	 * screen. It uses some system properties for parameters, and calls
	 * {@link pcgen.core.Globals#initFile Globals.initFile} to load the
	 * required campaign and configuration files. Finally the main
	 * screen of the application is created,
	 * {@link pcgen.gui.PCGen_Frame1 PCGen_Frame1}.
	 * <p>
	 * Some of the logic of the program initialisation should probably
	 * be refactored into the core package.
	 */
	public pcGenGUI()
	{
		Dimension d = Globals.readOptionsProperties();

		String pccLocation = Globals.getPccFilesLocation();
		loadPCCFilesInDirectory(Globals.getPccFilesLocation());
		String homeDir = System.getProperty("user.dir");
		Globals.initFile(homeDir + "\\system" + File.separator + "schools.lst", Globals.SCHOOLS_TYPE, new ArrayList());
		Globals.initFile(homeDir + "\\system\\bio" + File.separator + "color.lst", Globals.COLOR_TYPE, new ArrayList());
		Globals.initFile(homeDir + "\\system\\bio" + File.separator + "trait.lst", Globals.TRAIT_TYPE, new ArrayList());
		Globals.initFile(homeDir + "\\system\\specials" + File.separator + "specials.lst", Globals.SPECIAL_TYPE, new ArrayList());
		Globals.initFile(homeDir + "\\system" + File.separator + "load.lst", Globals.LOAD_TYPE, new ArrayList());
		Globals.initFile(homeDir + "\\system" + File.separator + "XP.lst", Globals.XP_TYPE, new ArrayList());
		Globals.initFile(homeDir + "\\system\\bio\\names" + File.separator + "names.lst", Globals.NAME_TYPE, new ArrayList());
		Globals.initFile(homeDir + "\\system\\specials" + File.separator + "bonusstacks.lst", Globals.BONUS_TYPE, new ArrayList());
		Globals.initFile(homeDir + "\\system" + File.separator + "sizeAdjustment.lst", Globals.SIZEADJUSTMENT_TYPE, new ArrayList());
    Globals.initFile(homeDir + "\\system\\bio" + File.separator + "phobia.lst", Globals.PHOBIA_TYPE, new ArrayList());
    Globals.initFile(homeDir + "\\system\\bio" + File.separator + "location.lst", Globals.LOCATION_TYPE, new ArrayList());
    Globals.initFile(homeDir + "\\system\\bio" + File.separator + "interests.lst", Globals.INTERESTS_TYPE, new ArrayList());
    Globals.initFile(homeDir + "\\system\\bio" + File.separator + "phrase.lst", Globals.PHRASE_TYPE, new ArrayList());
    Globals.initFile(homeDir + "\\system\\bio" + File.separator + "hairStyle.lst", Globals.HAIRSTYLE_TYPE, new ArrayList());
    Globals.initFile(homeDir + "\\system\\bio" + File.separator + "speech.lst", Globals.SPEECH_TYPE, new ArrayList());

		Globals.setCurrentFile("");
		ArrayList anArrayList = new ArrayList();
		Globals.sortPObjectList(Globals.getCampaignList());

		PCGen_Frame1 frame = new PCGen_Frame1();
		//Validate frames that have preset sizes
		//Pack frames that have useful preferred size info, e.g. from their layout
		if (packFrame)
		{
			frame.pack();
		}
		else
		{
			frame.validate();
		}

		int x = -11;
		int y = -11;
		if (Globals.getLeftUpperCorner() != null)
		{
			x = (int)Globals.getLeftUpperCorner().getX();
			y = (int)Globals.getLeftUpperCorner().getY();
		}

		if (x < -10 || y < -10 || d.height == 0 || d.width == 0)
		{
			//Center the window
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension frameSize = frame.getSize();
			if (frameSize.height > screenSize.height)
			{
				frameSize.height = screenSize.height;
			}
			if (frameSize.width > screenSize.width)
			{
				frameSize.width = screenSize.width;
			}
			frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
			if (anArrayList.size() == 4)
			{
				x = Integer.parseInt(anArrayList.get(0).toString());
				y = Integer.parseInt(anArrayList.get(1).toString());
//				final int height = Integer.parseInt(anArrayList.get(2).toString());
//				final int width = Integer.parseInt(anArrayList.get(3).toString());
				frame.setLocation(x, y);
			}
		}
		else
		{
			frame.setLocation(x, y);
			frame.setSize(d);
		}
		hideSplashScreen();
		Globals.setRootFrame(frame);
                
                UIFactory.initLookAndFeel();                

		frame.setVisible(true);
	}

	private boolean loadPCCFilesInDirectory(String aDirectory)
	{
		new File(aDirectory).list(new FilenameFilter()
		{
			public boolean accept(File aFile, String aString)
			{
				if (aString.endsWith(Globals.s_PCGEN_CAMPAIGN_EXTENSION))
					Globals.initFile(aFile.getPath() + File.separator + aString, Globals.CAMPAIGN_TYPE, new ArrayList());
				else if (aFile.isDirectory())
					loadPCCFilesInDirectory(aFile.getPath() + File.separator + aString);
				return false;
			}
		});
		return false;
	}

	/**
	 * Instantiates itself after setting look & feel, and
	 * opening splash screen.
	 *
	 * @param "-j" If first command line parameter is -j then the cross
	 *             platform look and feel is used. Otherwise the current
	 *             system is used (i.e. native L&F). This is a hidden
	 *              option :-)
	 */
	public static void main(String[] args)
	{

                //
                // print error messages / stack traces to a file
                //
                try {
                        System.setErr( new java.io.PrintStream(
                                new java.io.FileOutputStream( "stderr.txt" ) , true ) );
                } catch ( Exception ex ) {
                        System.err.println( ex.toString() );
                }

		//
		// Ensure we are using the correct version of the run-time environment.
		// If not, inform the user, but still allow him to use the program
		//
		// Might want to be able to turn this message off at some point.
		// i.e. Don't show this again checkbox
		//
		try
		{
			String sVersion = System.getProperty("java.version");
			if (Double.valueOf(sVersion.substring(0, 3)).doubleValue() < 1.3)
			{
				JOptionPane.showMessageDialog(null, "PCGen requires Java 2 v1.3.\nYour version of java is currently " + sVersion + ".\n" + "To be able to run PCGen properly you will need:\n" + " * The Java 2 v1.3 runtime environment available from\n" + "   http://java.sun.com/j2se/1.3/jre/\n\n" + "You'll need to pick the version of java appropriate for your\n" + "OS (the choices are Solaris/SPARC, Linux and Windows).", "PCGen", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch (Exception e)
		{
		}


		try
		{
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			if (args.length > 0 && args[0].equals("-j"))
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			else
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			//Hardly a fatal error, and quite unlikely at that...
			e.printStackTrace();
		}
		showSplashScreen();
		new pcGenGUI();
	}

	/**
	 * Ensures that the splash screen is not visible. This should be
	 * called before displaying any dialog boxes or windows at
	 * startup.
	 */
	public static void hideSplashScreen()
	{
		if (splash != null)
		{
			splash.dispose();
			splash = null;
		}
	}

	public static void showSplashScreen()
	{
		splash = new SplashScreen();
	}

	public static void advanceSplashProgress()
	{
		if (splash != null)
			splash.advance();
	}

}
