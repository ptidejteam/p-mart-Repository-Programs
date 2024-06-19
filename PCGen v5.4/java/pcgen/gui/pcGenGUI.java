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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.party.Party;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.Hyperactive;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.Utility;
import pcgen.io.ExportHandler;
import pcgen.util.Logging;

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
	/** Instantiated popup frame {@link HPFrame}. */
	private static HPFrame hpFrame = null;

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
			if (SettingsHandler.getFirstRun())
			{
				if (Globals.getUseGUI())
				{
					hideSplashScreen();
					askFileLocation();
				}
			}
			SettingsHandler.readOptionsProperties();
			d = SettingsHandler.getOptionsFromProperties();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			String message = e.getMessage();
			if (message == null || message.length() == 0)
			{
				message = "Unknown error whilst reading options.ini";
			}
			message += "\n\nIt MAY be possible to fix this problem by deleting your options.ini file.";
			GuiFacade.showMessageDialog(null, message, "PCGen - Error processing Options.ini", GuiFacade.ERROR_MESSAGE);
			if (Globals.getUseGUI())
			{
				hideSplashScreen();
			}
			System.exit(0);
		}

		// Fixes for Mac OS X look-and-feel menu problems.
		// sk4p 12 Dec 2002
		if (System.getProperty("os.name").equals("Mac OS X"))
		{
			System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
			System.setProperty("com.apple.mrj.application.live-resize", "false");
			System.setProperty("com.apple.macos.smallTabs", "true");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		if (!Globals.getUseGUI())
		{
			if (partyMode)
			{
				Party party = Party.makePartyFromFile(new File(inFileName));
				if (!party.load(null))
				{
					//todo: i18n these messages
					GuiFacade.showMessageDialog(null, "Problems occurred while loading the party.", "Error", GuiFacade.ERROR_MESSAGE);
				}
			}
			else
			{
				Party party = Party.makeSingleCharacterParty(new File(inFileName));
				if (!party.load(null))
				{
					//todo: i18n these messages
					GuiFacade.showMessageDialog(null, "Unrecoverable problems occurred while loading the character.", "Error", GuiFacade.ERROR_MESSAGE);
				}
			}
			try
			{
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8"));
				final File template = new File(templateName);
				if (partyMode)
				{
					SettingsHandler.setSelectedPartyHTMLOutputSheet(template.getAbsolutePath());
					(new ExportHandler(template)).write(Globals.getPCList(), bw);
				}
				else
				{
					SettingsHandler.setSelectedCharacterHTMLOutputSheet(template.getAbsolutePath());
					(new ExportHandler(template)).write(Globals.getCurrentPC(), bw);
				}
				bw.close();
				Globals.executePostExportCommand(outFileName);
				System.exit(0);
			}
			catch (Exception ex)
			{
				Logging.errorPrint("Exception in writing", ex);
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
			x = (int) SettingsHandler.getLeftUpperCorner().getX();
			y = (int) SettingsHandler.getLeftUpperCorner().getY();
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

		UIFactory.initLookAndFeel();

		processStartupArgs();

		hideSplashScreen();

		// These can't be handled before the main frame exists
		SettingsHandler.readGUIOptionsProperties();
		Utility.handleToolTipShownStateChange();

		PCGen_Frame1.enableDisableMenuItems();
		frame.setVisible(true);
		if (SettingsHandler.getShowTipOfTheDay())
		{
			showTipOfTheDay();
		}
	}

	private boolean processStartupArgs()
	{
		boolean status = true;

		/* Load through the frame instead of this class so that
		   the frame has a chance to update the menubars, etc.
		   */
		for (int i = 0; i < startupArgs.length; ++i)
		{
			if (startupArgs[i].endsWith(".pcg"))
			{
				if (!frame.loadPCFromFile(new File(startupArgs[i])))
				{
					Logging.errorPrint("No such PC file: " + startupArgs[i]);
					status = false;
				}
			}
			else if (startupArgs[i].endsWith(".pcp"))
			{
				if (!frame.loadPartyFromFile(new File(startupArgs[i])))
				{
					Logging.errorPrint("No such Party file: " + startupArgs[i]);
					status = false;
				}
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
			final String sVersion = System.getProperty("java.version");
			if (Double.valueOf(Globals.javaVersion.substring(0, 3)).doubleValue() < 1.3)
			{
				GuiFacade.showMessageDialog(null, "PCGen requires Java 2 v1.3.\nYour version of java is currently " + sVersion + ".\n" + "To be able to run PCGen properly you will need:\n" + " * The Java 2 v1.3 runtime environment available from\n" + "   http://java.sun.com/j2se/1.3/jre/\n\n" + "You'll need to pick the version of java appropriate for your\n" + "OS (the choices are Solaris/SPARC, Linux and Windows).", "PCGen", GuiFacade.INFORMATION_MESSAGE);
			}
		}
		catch (Exception e)
		{
			//Don't care?
		}

		try
		{
			if (Globals.getUseGUI()) // only set L&F if we're going to use a GUI
			{
				if (args.length > 0 && args[0].equals("-j"))
				{
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				}
				else
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
			}
		}
		catch (Exception e)
		{
			//Hardly a fatal error, and quite unlikely at that...
			Logging.errorPrint("Couldn't set look and feel", e);
		}
		if (Globals.getUseGUI())
		{
			showSplashScreen();
		}
		new pcGenGUI();
	}

	public static void showMandatoryD20Info()
	{
		final ImageIcon imgIcon = IconUtilitities.getImageIcon("D20_logo_RGB.jpg");
		if (imgIcon != null)
		{
			final JFrame aFrame = new JFrame("D20 Required Information");
			IconUtilitities.maybeSetIcon(aFrame, "PcgenIcon.gif");

			final JPanel jPanel1 = new JPanel();
			final JPanel jPanel2 = new JPanel();
			final JPanel jPanel3 = new JPanel();
			final JLabel jLabel1 = new JLabel(imgIcon);
			final JCheckBox jCheckBox1 = new JCheckBox("Show on source load");
			final JButton jClose = new JButton("Close");

			jPanel1.add(jLabel1);

			jPanel2.setLayout(new BorderLayout());

			aFrame.getContentPane().add(jPanel1, BorderLayout.NORTH);
			aFrame.getContentPane().add(jPanel2, BorderLayout.CENTER);
			aFrame.getContentPane().add(jPanel3, BorderLayout.SOUTH);

			String d20License = readTextFromFile(SettingsHandler.getPcgenSystemDir() + File.separator + "D20System.htm");
			final JEditorPane a = new JEditorPane("text/html", d20License);
			a.setEditable(false);
			a.addHyperlinkListener(new Hyperactive());
			final JScrollPane aPane = new JScrollPane();
			aPane.setViewportView(a);
			jPanel2.add(aPane, BorderLayout.CENTER);

			jPanel3.add(jCheckBox1);
			jPanel3.add(jClose);
			jCheckBox1.setSelected(SettingsHandler.showD20Info());

			jClose.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					aFrame.dispose();
				}
			});

			jCheckBox1.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent evt)
				{
					SettingsHandler.setShowD20Info(jCheckBox1.isSelected());
				}
			});

			aFrame.setSize(new Dimension(456, 352));
			Utility.centerFrame(aFrame, false);
			aFrame.setVisible(true);
		}
	}

	private void askFileLocation()
	{
		final Object[] oOk = {"OK"};
		final JLabel aLabel = new JLabel("<html>Select a directory to store PCGen options in:<hr><b>PCGen Dir</b>: This is the directory that PCGen is installed into (default)<br><b>Home Dir</b>: This is your home directory<br><b>Select</b>: Select a directory to use <br>If you have an existing options.ini file, then select the directory containing that file<hr>");
		final JPanel aPanel = new JPanel();
		final JPanel bPanel = new JPanel();
		final JPanel cPanel = new JPanel(new BorderLayout());
		final JPanel allPanel = new JPanel(new BorderLayout());

		ButtonGroup rGroup = new ButtonGroup();
		JRadioButton rPButton = new JRadioButton("PCGen Dir", true);
		JRadioButton rUButton = new JRadioButton("Home Dir");
		JRadioButton rSButton = new JRadioButton("Select a directory");
		final JTextField textField = new JTextField(String.valueOf(SettingsHandler.getPcgenFilesDir()));
		textField.setEditable(false);
		textField.setMinimumSize(new Dimension(90, 25));
		final JButton dirButton = new JButton("...");
		dirButton.setEnabled(false);
		rGroup.add(rPButton);
		rGroup.add(rUButton);
		rGroup.add(rSButton);

		aPanel.add(aLabel);
		bPanel.add(rPButton);
		bPanel.add(rUButton);
		cPanel.add(rSButton, BorderLayout.NORTH);
		rPButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				SettingsHandler.setFilePaths("pcgen");
				textField.setText(System.getProperty("user.dir"));
			}
		});
		rUButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				SettingsHandler.setFilePaths("user");
				textField.setText(System.getProperty("user.home") + File.separator + ".pcgen");
			}
		});
		rSButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				SettingsHandler.setFilePaths("select");
				dirButton.setEnabled(true);
			}
		});
		dirButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				File returnFile = SettingsHandler.getPcgenFilesDir();
				JFileChooser fc;
				if (returnFile == null)
				{
					fc = new JFileChooser();
				}
				else
				{
					fc = new JFileChooser(returnFile);
				}
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				final int rVal = fc.showOpenDialog(null);
				if (rVal == JFileChooser.APPROVE_OPTION)
				{
					returnFile = fc.getSelectedFile();
				}
				textField.setText(String.valueOf(returnFile));
				SettingsHandler.setPcgenFilesDir(returnFile);
			}
		});
		cPanel.add(textField, BorderLayout.CENTER);
		cPanel.add(dirButton, BorderLayout.EAST);

		allPanel.setSize(new Dimension(400, 200));
		allPanel.add(aPanel, BorderLayout.NORTH);
		allPanel.add(bPanel, BorderLayout.CENTER);
		allPanel.add(cPanel, BorderLayout.SOUTH);
		final Object[] message = new Object[1];
		message[0] = allPanel;

		GuiFacade.showOptionDialog(null, message, "Directory for options.ini location", GuiFacade.DEFAULT_OPTION, GuiFacade.INFORMATION_MESSAGE, null, oOk, oOk[0]);
	}

	public static void showLicense()
	{
		String aString = " ";
		aString += readTextFromFile(SettingsHandler.getPcgenSystemDir() + File.separator + "opengaminglicense.10a.txt");
		if (Globals.getSection15() != null)
		{
			aString += Globals.getSection15().toString();
		}
		showLicense("OGL License 1.0a", aString);
	}

	public static void showLicense(String title, List fileList)
	{
		for (Iterator i = fileList.iterator(); i.hasNext();)
		{
			String fileText = readTextFromFile(Globals.getDefaultPath() + File.separator + (String) i.next());
			showLicense(title, fileText);
		}
	}

	public static void showLicense(String title, String text)
	{
		if (title == null)
		{
			title = "OGL License 1.0a";
		}
		if (text == null)
		{
			text = "No license information found";
		}

		final JFrame aFrame = new JFrame(title);
		final JButton jClose = new JButton("Close");
		final JPanel jPanel = new JPanel();
		final JCheckBox jCheckBox = new JCheckBox("Show on source load");
		jPanel.add(jCheckBox);
		jCheckBox.setSelected(SettingsHandler.showLicense());
		jCheckBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				SettingsHandler.setShowLicense(jCheckBox.isSelected());
			}
		});
		jPanel.add(jClose);
		jClose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				aFrame.dispose();
			}
		});
		IconUtilitities.maybeSetIcon(aFrame, "PcgenIcon.gif");
		final JEditorPane a = new JEditorPane("text/html", text);
		a.setEditable(false);
		final JScrollPane aPane = new JScrollPane();
		aPane.setViewportView(a);
		aFrame.getContentPane().setLayout(new BorderLayout());
		aFrame.getContentPane().add(aPane, BorderLayout.CENTER);
		aFrame.getContentPane().add(jPanel, BorderLayout.SOUTH);
		aFrame.setSize(new Dimension(700, 500));
		Utility.centerFrame(aFrame, false);
		aFrame.setVisible(true);
	}

	private static String readTextFromFile(String fileName)
	{
		String aString;
		final File aFile = new File(fileName);
		if (!aFile.exists())
		{
			Logging.errorPrint("Could not find license at " + fileName);
			aString = "No license information found";
			return aString;
		}
		try
		{
			BufferedReader theReader = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), "UTF-8"));
			final int length = (int) aFile.length();
			final char[] inputLine = new char[length];
			theReader.read(inputLine, 0, length);
			theReader.close();
			aString = new String(inputLine);
		}
		catch (IOException e)
		{
			Logging.errorPrint("Could not read license at " + fileName, e);
			aString = "No license information found";
		}
		return aString;
	}

	/**
	 * Ensures that the splash screen is not visible. This should be
	 * called before displaying any dialog boxes or windows at
	 * startup.
	 */
	private static void hideSplashScreen()
	{
		if (splash != null)
		{
			splash.dispose();
			splash = null;
		}
	}

	private static void showSplashScreen()
	{
		splash = new SplashScreen();
	}

	private static void initHpFrame()
	{
		if (hpFrame == null)
		{
			hpFrame = new HPFrame();
		}
	}

	public static void showHpFrame()
	{
		if (hpFrame == null)
		{
			initHpFrame();
		}
		hpFrame.setPSize();
		hpFrame.pack();
		hpFrame.setVisible(true);
	}

	public static void showTipOfTheDay()
	{
		new TipOfTheDay().show();
	}

}
