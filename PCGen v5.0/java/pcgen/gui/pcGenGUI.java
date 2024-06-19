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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.party.Party;
import pcgen.io.ExportHandler;
import pcgen.util.Hyperactive;

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
			JOptionPane.showMessageDialog(null, message, "PCGen - Error processing Options.ini", JOptionPane.ERROR_MESSAGE);
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
			System.setProperty("com.apple.macos.useScreenMenuBar", "true");
		}

//
// Moved to SettingsHandler
//		try
//		{
//			PersistenceManager.initialize();
//		}
//		catch (PersistenceLayerException e)
//		{
//			JOptionPane.showMessageDialog(null, e.getMessage(), "PCGen", JOptionPane.INFORMATION_MESSAGE);
//		}

		if (!Globals.getUseGUI())
		{
			if (partyMode)
			{
				Party party = Party.makePartyFromFile(new File(inFileName));
				if (!party.load(null))
				{
					//todo: i18n these messages
					JOptionPane.showMessageDialog(null, "Problems occurred while loading the party.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				Party party = Party.makeSingleCharacterParty(new File(inFileName));
				if (!party.load(null))
				{
					//todo: i18n these messages
					JOptionPane.showMessageDialog(null, "Unrecoverable problems occurred while loading the character.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			try
			{
				//final BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8"));
				final File template = new File(templateName);
				if (partyMode)
				{
					SettingsHandler.setSelectedPartyHTMLOutputSheet(template.getAbsolutePath());
					(new ExportHandler(template)).write(Globals.getPCList(), bw);
//					Party.print(template, bw);
				}
				else
				{
					SettingsHandler.setSelectedCharacterHTMLOutputSheet(template.getAbsolutePath());
					(new ExportHandler(template)).write(Globals.getCurrentPC(), bw);
//  					final PlayerCharacter aPC = (PlayerCharacter)Globals.getCurrentPC();
//  					aPC.print(template, bw);
				}
				bw.close();
				Globals.executePostExportCommand(outFileName);
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
		//Utility.updateToolTipTextShownState();

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
				JOptionPane.showMessageDialog(null, "PCGen requires Java 2 v1.3.\nYour version of java is currently " + sVersion + ".\n" + "To be able to run PCGen properly you will need:\n" + " * The Java 2 v1.3 runtime environment available from\n" + "   http://java.sun.com/j2se/1.3/jre/\n\n" + "You'll need to pick the version of java appropriate for your\n" + "OS (the choices are Solaris/SPARC, Linux and Windows).", "PCGen", JOptionPane.INFORMATION_MESSAGE);
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
			Globals.errorPrint("Couldn't set look and feel", e);
		}
		if (Globals.getUseGUI())
		{
			showSplashScreen();
		}
		new pcGenGUI();
	}

	public static void showMandatoryD20Info()
	{
		final ImageIcon imgIcon = Utility.getImageIcon("D20_logo_RGB.jpg");
		if (imgIcon != null)
		{
			final JFrame aFrame = new JFrame("D20 Required Information");
			Utility.maybeSetIcon(aFrame, "PcgenIcon.gif");

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

		//final Object[] message = new Object[6];
		//message[0] = aLabel;
		//message[1] = rPButton;
		//message[2] = rUButton;
		//message[3] = rSButton;
		//message[4] = textField;
		//message[5] = dirButton;

		allPanel.setSize(new Dimension(400, 200));
		allPanel.add(aPanel, BorderLayout.NORTH);
		allPanel.add(bPanel, BorderLayout.CENTER);
		allPanel.add(cPanel, BorderLayout.SOUTH);
		final Object[] message = new Object[1];
		message[0] = allPanel;

		JOptionPane.showOptionDialog(null, message, "Directory for options.ini location", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, oOk, oOk[0]);

		//int results = JOptionPane.showOptionDialog(null, "Choose where to keep your options.ini file", "options.ini location", JOptionPane.DEFAULT_OPTION, null, oOk, oOk[0]);

		//aFrame.setSize(new Dimension(400, 200));
	}

	public static void showLicense()
	{
		showLicense("OGL License 1.0a", readTextFromFile(SettingsHandler.getPcgenSystemDir() + File.separator + "opengaminglicense.10a.txt") + Globals.getSection15().toString());
	}

	public static void showLicense(String title, ArrayList fileList)
	{
		for (Iterator i = fileList.iterator(); i.hasNext();)
		{
			String fileText = readTextFromFile(Globals.getDefaultPath() + File.separator + (String) i.next());
			showLicense(title, fileText);
		}
	}

	public static void showLicense(String title, String text)
	{
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
		Utility.maybeSetIcon(aFrame, "PcgenIcon.gif");
		final JEditorPane a = new JEditorPane("text/html", text);
		a.setEditable(false);
		final JScrollPane aPane = new JScrollPane();
		aPane.setViewportView(a);
		aFrame.getContentPane().setLayout(new BorderLayout());
		aFrame.getContentPane().add(aPane, BorderLayout.CENTER);
		aFrame.getContentPane().add(jPanel, BorderLayout.SOUTH);
		aFrame.setSize(new Dimension(700, 500));
		final Point p = Globals.getRootFrame().getLocationOnScreen();
		aFrame.setLocation((int) p.getX(), (int) p.getY());
		aFrame.setVisible(true);
	}

/*	private static String readD20License()
	{
		String aString = null;
		final String d20LicensePath = SettingsHandler.getPcgenSystemDir() + File.separator + "D20System.htm";
		try
		{
			final File d20LicenseFile = new File(d20LicensePath);
			//final BufferedReader d20InfoReader = new BufferedReader(new FileReader(d20LicenseFile));
			BufferedReader d20InfoReader = new BufferedReader(new InputStreamReader(new FileInputStream(d20LicenseFile), "UTF-8"));
			final int length = (int) d20LicenseFile.length();
			final char[] inputLine = new char[length];
			d20InfoReader.read(inputLine, 0, length);
			d20InfoReader.close();
			aString = new String(inputLine);
		}
		catch (FileNotFoundException e)
		{
			Globals.errorPrint("Could not find D20 license at " + d20LicensePath, e);
			aString = "No license information found";
		}
		catch (IOException e)
		{
			Globals.errorPrint("Could not read license at " + d20LicensePath, e);
			aString = "No license information found";
		}
		return aString;
	}
*/
	private static String readTextFromFile(String fileName)
	{
		String aString = null;
		try
		{
			final File theFile = new File(fileName);
			BufferedReader theReader = new BufferedReader(new InputStreamReader(new FileInputStream(theFile), "UTF-8"));
			final int length = (int) theFile.length();
			final char[] inputLine = new char[length];
			theReader.read(inputLine, 0, length);
			theReader.close();
			aString = new String(inputLine);
		}
		catch (FileNotFoundException e)
		{
			Globals.errorPrint("Could not find license at " + fileName, e);
			aString = "No license information found";
		}
		catch (IOException e)
		{
			Globals.errorPrint("Could not read license at " + fileName, e);
			aString = "No license information found";
		}
		return aString;
	}

/*	private static String readOGL()
	{
		String oglText = null;
		final String oglPathname = SettingsHandler.getPcgenSystemDir() + File.separator + "opengaminglicense.10a.txt";
		final File oglFile = new File(oglPathname);
		try
		{
			//final BufferedReader oglReader = new BufferedReader(new FileReader(oglFile));
			BufferedReader oglReader = new BufferedReader(new InputStreamReader(new FileInputStream(oglFile), "UTF-8"));
			final int length = (int) oglFile.length();
			final char[] inputLine = new char[length];
			oglReader.read(inputLine, 0, length);
			oglText = new String(inputLine);
		}
		catch (FileNotFoundException e)
		{
			Globals.errorPrint("Could not find Open Gaming license at " + oglPathname, e);
			oglText = "No license information found";
		}
		catch (IOException e)
		{
			Globals.errorPrint("Could not read Open Gaming license at " + oglPathname, e);
			oglText = "No license information found";
		}
		return oglText;
	}
*/
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

	public static void advanceSplashProgress()
	{
		if (splash != null)
		{
			splash.advance();
		}
	}

	public static HPFrame getHpFrame()
	{
		return hpFrame;
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
