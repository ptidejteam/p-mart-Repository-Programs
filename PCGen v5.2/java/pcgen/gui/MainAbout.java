/*
 * MainAbout.java
 * Copyright 2001 (C) Tom Epperly <tomepperly@home.com>
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
 * Created on April 26, 2001, 10:47 PM
 */

package pcgen.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import pcgen.gui.utils.BrowserLauncher;
import pcgen.util.Logging;

/**
 * Create a simple panel to identify the program and those who contributed
 * to it.
 *
 * @author  Tom Epperly <tomepperly@home.com>
 * @version $Revision: 1.1 $
 * Modified 4/8/02 by W Robert Reed III (Mynex)
 * Adds List Monkeys Display area
 * Cleaned up naming schema
 */

final class MainAbout extends JPanel
{
  static final long serialVersionUID = -423796320641536943L;
	private JLabel d_versionLabel;
	private JLabel d_dateLabel;
	private JLabel d_leaderLabel;
	private JLabel d_helperLabel;
	private JLabel d_wwwLink;
	private JLabel d_emailLabel;
	private JTextField d_version;
	private JTextField d_releaseDate;
	private JTextField d_projectLead;
	private JButton d_wwwSite;
	private JButton d_mailingList;
	private JLabel d_licenseLabel;
	private JScrollPane d_developerList;
	private JTextArea d_developerListArea;
	private JScrollPane d_testersList;
	private JTextArea d_testMonkeysArea;
	private JScrollPane d_listMonkeys;
	private JTextArea d_listMonkeysArea;
	private JScrollPane d_engMonkeys;
	private JTextArea d_engMonkeysArea;
	private JScrollPane d_license;
	private JTextArea d_LGPLArea;
	private ResourceBundle d_properties;
	private JTabbedPane creditsPane; // no friggin d_

	/** Creates new form MainAbout */
	MainAbout()
	{
		try
		{
			try
			{
				d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			}
			catch (MissingResourceException mre)
			{
				d_properties = null;
			}
			initComponents();
		}
		finally
		{
			d_properties = null;
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		d_versionLabel = new JLabel();
		d_dateLabel = new JLabel();
		d_leaderLabel = new JLabel();
		d_helperLabel = new JLabel();
		d_wwwLink = new JLabel();
		d_emailLabel = new JLabel();
		d_version = new JTextField();
		d_releaseDate = new JTextField();
		d_projectLead = new JTextField();
		d_wwwSite = new JButton();
		d_mailingList = new JButton();
		d_licenseLabel = new JLabel();
		d_developerList = new JScrollPane();
		d_developerListArea = new JTextArea();
		d_testersList = new JScrollPane();
		d_testMonkeysArea = new JTextArea();
		d_listMonkeys = new JScrollPane();
		d_listMonkeysArea = new JTextArea();
		d_engMonkeys = new JScrollPane();
		d_engMonkeysArea = new JTextArea();
		d_license = new JScrollPane();
		d_LGPLArea = new JTextArea();
		creditsPane = new JTabbedPane();

		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints1;

		d_versionLabel.setText("PCGen version:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		add(d_versionLabel, gridBagConstraints1);

		d_dateLabel.setText("Release date:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		add(d_dateLabel, gridBagConstraints1);

		d_leaderLabel.setText("Chief Code Monkey:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		add(d_leaderLabel, gridBagConstraints1);

		d_helperLabel.setText("Monkeys:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 5;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.NORTHWEST;
		add(d_helperLabel, gridBagConstraints1);

		d_wwwLink.setText("WWW site:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 3;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		add(d_wwwLink, gridBagConstraints1);

		d_emailLabel.setText("Email list:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 4;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		add(d_emailLabel, gridBagConstraints1);

		d_version.setEditable(false);
		d_version.setText("3.0.0");
		d_version.setBorder(null);
		d_version.setOpaque(false);
		if (d_properties != null)
		{
			try
			{
				d_version.setText(d_properties.getString("VersionNumber"));
			}
			catch (MissingResourceException mre)
			{
				d_version.setText("Missing VersionNumber property");
			}
		}
		else
		{
			d_version.setText("Unknown VersionNumber");
		}
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		add(d_version, gridBagConstraints1);

		d_releaseDate.setEditable(false);
		d_releaseDate.setText("Unpublished");
		d_releaseDate.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
		d_releaseDate.setOpaque(false);
		if (d_properties != null)
		{
			try
			{
				d_releaseDate.setText(d_properties.getString("ReleaseDate"));
			}
			catch (MissingResourceException mre)
			{
				d_releaseDate.setText("Missing ReleaseDate property");
			}
		}
		else
		{
			d_releaseDate.setText("Missing ReleaseDate");
		}
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		add(d_releaseDate, gridBagConstraints1);

		d_projectLead.setEditable(false);
		d_projectLead.setText("Bryan McRoberts");
		d_projectLead.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
		d_projectLead.setOpaque(false);
		if (d_properties != null)
		{
			try
			{
				d_projectLead.setText(d_properties.getString("HeadCodeMonkey"));
			}
			catch (MissingResourceException mre)
			{
				d_projectLead.setText("Bryan McRoberts");
			}
		}
		else
		{
			d_projectLead.setText("Bryan McRoberts");
		}
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		add(d_projectLead, gridBagConstraints1);

		d_wwwSite.setText("http://pcgen.sourceforge.net/");
		if (d_properties != null)
		{
			try
			{
				d_wwwSite.setText(d_properties.getString("WWWHome"));
			}
			catch (MissingResourceException mre)
			{
				d_wwwSite.setText("http://pcgen.sourceforge.net/");
			}
		}
		else
		{
			d_wwwSite.setText("http://pcgen.sourceforge.net/");
		}
		d_wwwSite.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					BrowserLauncher.openURL(d_wwwSite.getText());
				}
				catch (IOException ioe)
				{
					Logging.errorPrint("Couldn't launch browser", ioe);
				}
			}
		});
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 3;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		add(d_wwwSite, gridBagConstraints1);

		d_mailingList.setText("http://groups.yahoo.com/group/pcgen");
		if (d_properties != null)
		{
			try
			{
				d_mailingList.setText(d_properties.getString("MailingList"));
			}
			catch (MissingResourceException mre)
			{
				d_mailingList.setText("http://groups.yahoo.com/group/pcgen");
			}
		}
		else
		{
			d_mailingList.setText("http://groups.yahoo.com/group/pcgen");
		}
		d_mailingList.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					BrowserLauncher.openURL(d_mailingList.getText());
				}
				catch (IOException ioe)
				{
					Logging.errorPrint("Couldn't launch browser", ioe);
				}
			}
		});
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 4;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		add(d_mailingList, gridBagConstraints1);

		d_licenseLabel.setText("Licensing terms:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 8;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		add(d_licenseLabel, gridBagConstraints1);

		d_developerListArea.setWrapStyleWord(true);
		d_developerListArea.setLineWrap(true);
		d_developerListArea.setEditable(false);
		d_developerListArea.setText("Monkeys");
		if (d_properties != null)
		{
			try
			{
				d_developerListArea.setText(d_properties.getString("CodeMonkeys"));
			}
			catch (MissingResourceException mre)
			{
				// ignore
			}
		}
		d_developerList.setViewportView(d_developerListArea);
		d_developerListArea.setCaretPosition(0);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 5;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;

		add(creditsPane, gridBagConstraints1);
		creditsPane.add("Code", d_developerList);

//Begin additional code to display List Monkeys

		d_listMonkeysArea.setWrapStyleWord(true);
		d_listMonkeysArea.setLineWrap(true);
		d_listMonkeysArea.setEditable(false);
		if (d_properties != null)
		{
			try
			{
				d_listMonkeysArea.setText(d_properties.getString("ListMonkeys"));
			}
			catch (MissingResourceException mre)
			{
				// ignore
			}
		}
		d_listMonkeys.setViewportView(d_listMonkeysArea);
		d_listMonkeysArea.setCaretPosition(0);

		creditsPane.add("List", d_listMonkeys);

//End Additional code to display list monkeys


		d_testMonkeysArea.setWrapStyleWord(true);
		d_testMonkeysArea.setLineWrap(true);
		d_testMonkeysArea.setEditable(false);
		if (d_properties != null)
		{
			try
			{
				d_testMonkeysArea.setText(d_properties.getString("TestMonkeys"));
			}
			catch (MissingResourceException mre)
			{
				// ignore
			}
		}
		d_testersList.setViewportView(d_testMonkeysArea);
		d_testMonkeysArea.setCaretPosition(0);

		creditsPane.add("Test", d_testersList);
		creditsPane.setToolTipTextAt(2, "Can you find the Easter Egg?  It isn't easy!"); // because there isn't one

		d_engMonkeysArea.setWrapStyleWord(true);
		d_engMonkeysArea.setLineWrap(true);
		d_engMonkeysArea.setEditable(false);
		if (d_properties != null)
		{
			try
			{
				d_engMonkeysArea.setText(d_properties.getString("EngineeringMonkeys"));
			}
			catch (MissingResourceException mre)
			{
				// ignore
			}
		}
		d_engMonkeys.setViewportView(d_engMonkeysArea);
		d_engMonkeysArea.setCaretPosition(0);
		creditsPane.add("Engineering", d_engMonkeys);

		d_LGPLArea.setEditable(false);
		InputStream lgpl = ClassLoader.getSystemResourceAsStream("LICENSE");
		if (lgpl != null)
		{
			try
			{
				d_LGPLArea.read(new InputStreamReader(lgpl), "LICENSE");
			}
			catch (IOException ioe)
			{
				d_LGPLArea.setText("Unable to read license text from distribution.");
			}
		}
		else
		{
			d_LGPLArea.setText("Your distribution does not contain the license text.");
		}
		d_license.setViewportView(d_LGPLArea);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 9;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 3.0;
		add(d_license, gridBagConstraints1);

	}
}
