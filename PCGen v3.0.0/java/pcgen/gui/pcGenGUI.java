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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.Party;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;


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
	private PCGen_Frame1 frame;
	private static String templateName = "";
	private static String inFileName = "";
	private static String outFileName = "";
	private static boolean partyMode = false;

	private static String[] startupArgs = {};

	/**
	 * Initialises the application and loads the main
	 * screen. It uses some system properties for parameters, and calls
	 * {@link pcgen.persistence.PersistenceManager#initialize PersistenceManager.initialize} to load the
	 * required campaign and configuration files. Finally the main
	 * screen of the application is created,
	 * {@link pcgen.gui.PCGen_Frame1 PCGen_Frame1}.
	 * <p>
	 * Some of the logic of the program initialisation should probably
	 * be refactored into the core package.
	 */
	public pcGenGUI()
	{
		Dimension d = null;
		try
		{
			d = SettingsHandler.readOptionsProperties();
		}
		catch (Exception e)
		{
			String message = e.getMessage();
			if (message == null || message.length() == 0)
				message = "Unknown error whilst reading reading options.ini";
			message += "\n\nIt MAY be possible to fix this problem by deleting your options.ini file.";
			JOptionPane.showMessageDialog(null, message, "PCGen - Error processing Options.ini", JOptionPane.ERROR_MESSAGE);
			if (Globals.getUseGUI())
			{
				hideSplashScreen();
			}
			System.exit(0);
		}

		try
		{
			PersistenceManager.initialize();
		}
		catch (PersistenceLayerException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "PCGen", JOptionPane.INFORMATION_MESSAGE);
		}

		if (!Globals.getUseGUI())
		{
			if (partyMode)
				loadPartyFromFile(new File(inFileName), null);
			else
				loadPCFromFile(new File(inFileName));
			try
			{
				final BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName));
				final File template = new File(templateName);
				if (partyMode)
				{
					SettingsHandler.setSelectedPartyTemplate(template.getAbsolutePath());
					Party.print(template, bw);
				}
				else
				{
					SettingsHandler.setSelectedTemplate(template.getAbsolutePath());
					(new pcgen.io.ExportHandler(template)).write(Globals.getCurrentPC(), bw);
//  					final PlayerCharacter aPC = (PlayerCharacter)Globals.getCurrentPC();
//  					aPC.print(template, bw);
				}
				bw.close();
				System.exit(0);
			}
			catch (Exception ex)
			{
				Globals.errorPrint("Exception in writing", ex);
			}
			return;
		}

		frame = new PCGen_Frame1();
		frame.setMainClass(this);
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
		if (SettingsHandler.getLeftUpperCorner() != null)
		{
			x = (int)SettingsHandler.getLeftUpperCorner().getX();
			y = (int)SettingsHandler.getLeftUpperCorner().getY();
		}

		if (x < -10 || y < -10 || d.height == 0 || d.width == 0)
		{
			Utility.centerFrame(frame, false);
		}
		else
		{
			frame.setLocation(x, y);
			frame.setSize(d);
		}

		Globals.setRootFrame(frame);

		UIFactory.initLookAndFeel();

		processStartupArgs();

		hideSplashScreen();

		// These can't be handled before the main frame exists
		SettingsHandler.readGUIOptionsProperties();
		Utility.handleToolTipShownStateChange();
		Utility.updateToolTipTextShownState();

		frame.enableMenuItems();
		frame.setVisible(true);

		// Starting Wizard goes here.  Doesn't do much yet.  XXX
		if (! SettingsHandler.getRanStartingWizard())
		{
			JOptionPane.showMessageDialog(frame, "Hail and well met!\n\nWelcome to PCGen, a program for creating and managing\nD20 characters and parties.  By default, PCGen uses settings\nfor standard D&D 3e campaigns.  To change this and other\nsettings, please use the Settings menu.\n\n(This message will not appear again the next time you run\nPCGen.)", "Welcome to PCGen!", JOptionPane.INFORMATION_MESSAGE);
			SettingsHandler.setRanStartingWizard(true);
		}
	}

	boolean processStartupArgs()
	{
		boolean status = true;

		/* Load through the frame instead of this class so that
		   the frame has a chance to update the menubars, etc.
		   */
		for (int i = 0; i < startupArgs.length; ++i)
			if (startupArgs[i].endsWith(".pcg"))
			{
				if (!frame.loadPCFromFile(new File(startupArgs[i])))
				{
					Globals.errorPrint("No such PC file: " + startupArgs[i]);
					status = false;
				}
			}
			else if (startupArgs[i].endsWith(".pcp"))
			{
				if (!frame.loadPartyFromFile(new File(startupArgs[i])))
				{
					Globals.errorPrint("No such Party file: " + startupArgs[i]);
					status = false;
				}
			}

		return status;
	}

	/**
	 * Instantiates itself after setting look & feel, and
	 * opening splash screen.
	 *
	 * @param args "-j" If first command line parameter is -j then the cross
	 *             platform look and feel is used. Otherwise the current
	 *             system is used (i.e. native L&F). This is a hidden
	 *              option :-)
	 */
	public static void main(String[] args)
	{
		templateName = System.getProperty("pcgen.templatefile");
		inFileName = System.getProperty("pcgen.inputfile");
		outFileName = System.getProperty("pcgen.outputfile");

		startupArgs = args;

		if (inFileName != null && templateName != null && outFileName != null)
		{
			partyMode = inFileName.endsWith(".pcp");
			Globals.setUseGUI(false);
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
			if (Globals.getUseGUI()) // only set L&F if we're going to use a GUI
			{
				if (args.length > 0 && args[0].equals("-j"))
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				else
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		}
		catch (Exception e)
		{
			//Hardly a fatal error, and quite unlikely at that...
			e.printStackTrace();
		}
		if (Globals.getUseGUI())
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

	public boolean loadPartyFromFile(File file, PCGen_Frame1 mainFrame)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			//load version info
			br.readLine(); //Read and throw away version info. May change to actually use later
			//load character filename data
			final String charFiles = br.readLine();
			br.close();

			//we no longer load campaign/source infor from the party file
			// in this space we could check the VERSION tag of versionInfo for whatever we wanted
			// if the String didn't start with VERSION: then we know it's a really old PCP file

			//parse PC data and load the listed PC's
			StringTokenizer pcTok = new StringTokenizer(charFiles, ",");
			while (pcTok.hasMoreTokens())
			{
				File pcFile = new File(pcTok.nextToken());
				if (!pcFile.exists())
				{

					// try using the global pcg path
					pcFile = new File(SettingsHandler.getPcgPath(), pcTok.nextToken());

				}
				if (pcFile.exists())
				{
					// if called from the GUI, then use the GUI's PC loader so that we get the PC tabs built
					if (mainFrame != null)
						mainFrame.loadPCFromFile(pcFile);
					// otherwise, do it the quick-n-dirty way
					else
						loadPCFromFile(pcFile);
				}
			}
			Globals.sortCampaigns();
			return true;
		}
		catch (IOException ex)
		{
			Globals.errorPrint("Error loading party file.", ex);

			if (Globals.getUseGUI())
			{
				JOptionPane.showMessageDialog(null,
					"Could not load party file.",
					"PCGen",
					JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
		return false;
	}

	public boolean loadPCFromFile(File file)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			loadCampaignsForPC(br); //do this, even if we're not loading sources, so that the CAMPAIGN line gets skipped for later on
			if (!Globals.displayListsHappy())
			{
				Globals.errorPrint("Insufficient campaign information to load character file.");
				if (Globals.getUseGUI())
					JOptionPane.showMessageDialog(null,
						"Insufficient campaign information to load character file.",
						"PCGen",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			final PlayerCharacter newPC = new PlayerCharacter();
//  			newPC.load(br);
//  			br.close();
			pcgen.io.PCGIOHandler ioHandler = new pcgen.io.PCGIOHandler();
			ioHandler.read(newPC, file.getAbsolutePath());
			if (Globals.getUseGUI())
			{
				for (Iterator it = ioHandler.getErrors().iterator(); it.hasNext();)
				{
					JOptionPane.showMessageDialog(null,
						"Error: " + (String)it.next(),
						Constants.s_APPNAME,
						JOptionPane.ERROR_MESSAGE);
				}
				for (Iterator it = ioHandler.getWarnings().iterator(); it.hasNext();)
				{
					JOptionPane.showMessageDialog(null,
						"Warning: " + (String)it.next(),
						Constants.s_APPNAME,
						JOptionPane.WARNING_MESSAGE);
				}
			}
			else
			{
				for (Iterator it = ioHandler.getMessages().iterator(); it.hasNext();)
				{
					Globals.errorPrint((String)it.next());
				}
			}

			Globals.getPCList().add(newPC);
			Globals.setCurrentPC(newPC);
			Globals.sortCampaigns();
			return true;
		}
		catch (IOException ex)
		{
			Globals.errorPrint("Error loading character file.", ex);
			if (Globals.getUseGUI())
			{
				JOptionPane.showMessageDialog(null,
					"Could not load character file.",
					"PCGen",
					JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
		return false;
	}

	private void loadCampaignsForPC(BufferedReader br)
	{
		try
		{
			br.mark(1024);  //set a mark so we can reset in the event of an old .pcg file
			String lastLineParsed = br.readLine();
			StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
			String sCamp = aTok.nextToken();
			//if the pcg file starts with CAMPAIGN data then lets process it
			if (sCamp.equals("CAMPAIGNS"))
			{
				if (SettingsHandler.isLoadCampaignsWithPC())
				{
					ArrayList campList = new ArrayList();
					while (aTok.hasMoreTokens())
					{
						Campaign aCamp = Globals.getCampaignNamed(aTok.nextToken());
						if (aCamp != null)
						{
							if (!aCamp.isLoaded())
							{
								campList.add(aCamp);
							}
						}
					}
					if (campList.size() > 0)
					{
						try
						{
							PersistenceManager.loadCampaigns(campList);
						}
						catch (PersistenceLayerException e)
						{
							JOptionPane.showMessageDialog(null, e.getMessage(), "PCGen", JOptionPane.INFORMATION_MESSAGE);
						}
						if (Globals.getUseGUI())
							frame.getMainSource().updateLoadedCampaignsUI();
					}
				}
			}
			else
			{
				//this is an old .pcg file (no campaign data) so just reset the input stream
				br.reset();
			}
		}
		catch (IOException e)
		{
			Globals.errorPrint("Error loading campaign data for character.", e);
			if (Globals.getUseGUI())
			{
				JOptionPane.showMessageDialog(null,
					"Could not load campaign data from character file.",
					"PCGen",
					JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
}
