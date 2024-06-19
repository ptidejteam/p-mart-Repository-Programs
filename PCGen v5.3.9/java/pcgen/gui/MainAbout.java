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
 * 
 * Current Ver: $Revision: 1.1 $ <br>
 * Last Editor: $Author: vauchers $ <br>
 * Last Edited: $Date: 2006/02/21 01:16:22 $ 
 *
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

import javax.swing.border.BevelBorder;
import javax.swing.BorderFactory;
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
	private JTabbedPane creditsPane; // no friggin d_
	private JTextArea d_otherLibrariesField;

	/** Creates new form MainAbout */
	MainAbout()
	{
		initComponents();
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
		d_otherLibrariesField = new JTextArea();

		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints1;

		d_versionLabel.setText("PCGen version:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = .2;
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
		
		d_version.setText( PCGenProp.getVersionNumber() );

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
		
		d_releaseDate.setText( PCGenProp.getReleaseDate() );
		
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
//		gridBagConstraints1.weightx = 1.0;
		add(d_releaseDate, gridBagConstraints1);

		d_projectLead.setEditable(false);
		d_projectLead.setText("Bryan McRoberts");
		d_projectLead.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
		d_projectLead.setOpaque(false);
		
		d_projectLead.setText( PCGenProp.getHeadCodeMonkey() );
		
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
//		gridBagConstraints1.weightx = 1.0;
		add(d_projectLead, gridBagConstraints1);

		d_wwwSite.setText( PCGenProp.getWWWHome() );

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
		gridBagConstraints1.gridwidth = 1;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
//		gridBagConstraints1.weightx = 1.0;
		add(d_wwwSite, gridBagConstraints1);

		d_mailingList.setText( PCGenProp.getMailingList() );

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
		gridBagConstraints1.gridwidth = 1;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
//		gridBagConstraints1.weightx = 1.0;
		add(d_mailingList, gridBagConstraints1);

		d_licenseLabel.setText("Licensing terms:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 8;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		add(d_licenseLabel, gridBagConstraints1);

		d_developerListArea.setWrapStyleWord(true);
		d_developerListArea.setLineWrap(true);
		d_developerListArea.setEditable(false);
		d_developerListArea.setText("Monkeys");
		
		d_developerListArea.setText( PCGenProp.getCodeMonkeys() );

		d_developerList.setViewportView(d_developerListArea);
		d_developerListArea.setCaretPosition(0);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 5;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
//		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;

		add(creditsPane, gridBagConstraints1);
		creditsPane.add("Code", d_developerList);

//Begin additional code to display List Monkeys

		d_listMonkeysArea.setWrapStyleWord(true);
		d_listMonkeysArea.setLineWrap(true);
		d_listMonkeysArea.setEditable(false);
		
		d_listMonkeysArea.setText( PCGenProp.getListMonkeys() );

		d_listMonkeys.setViewportView(d_listMonkeysArea);
		d_listMonkeysArea.setCaretPosition(0);

		creditsPane.add("List", d_listMonkeys);

//End Additional code to display list monkeys


		d_testMonkeysArea.setWrapStyleWord(true);
		d_testMonkeysArea.setLineWrap(true);
		d_testMonkeysArea.setEditable(false);
		
		d_testMonkeysArea.setText(PCGenProp.getTestMonkeys() );
		
		d_testersList.setViewportView(d_testMonkeysArea);
		d_testMonkeysArea.setCaretPosition(0);

		creditsPane.add("Test", d_testersList);
		creditsPane.setToolTipTextAt(2, "Can you find the Easter Egg?  It isn't easy!"); // because there isn't one

		d_engMonkeysArea.setWrapStyleWord(true);
		d_engMonkeysArea.setLineWrap(true);
		d_engMonkeysArea.setEditable(false);
		
		d_engMonkeysArea.setText( PCGenProp.getEngineeringMonkeys() );
		
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
		gridBagConstraints1.gridwidth = 3;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
//		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 3.0;
		add(d_license, gridBagConstraints1);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 2;
		gridBagConstraints1.gridheight = 5;
		gridBagConstraints1.gridwidth = 1;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 2.0;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.anchor = GridBagConstraints.CENTER;
		gridBagConstraints1.insets = new Insets(10, 10, 10, 10);
		d_otherLibrariesField.setText("PCGen includes software developed by the Apache Software Foundation (http://www.apache.org/): Avalon, Batik, FOP, Xalan and Xerces");
		d_otherLibrariesField.setWrapStyleWord(true);
		d_otherLibrariesField.setLineWrap(true);
		d_otherLibrariesField.setEditable(false);
		d_otherLibrariesField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		add(d_otherLibrariesField, gridBagConstraints1);
	}
}
