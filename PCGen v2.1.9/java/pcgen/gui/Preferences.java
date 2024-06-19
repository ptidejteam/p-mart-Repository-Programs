/*
 * Preferences.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Created on April 24, 2001, 10:06 PM
 */
package pcgen.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import pcgen.core.Globals;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
public class Preferences extends JMenu
{
	private JCheckBoxMenuItem campLoad = new JCheckBoxMenuItem("Auto-load Source Materials");
	private JMenuItem browserPath = new JMenuItem("Browser Path...");
	private JMenuItem templateDefault = new JMenuItem("Default Export Template...");
	private JMenu tabPlacement = new JMenu("Main Tab Placement");
	private JMenu chaTabPlacement = new JMenu("Character Tab Placement");
	private JCheckBoxMenuItem topTab = new JCheckBoxMenuItem("Top");
	private JCheckBoxMenuItem bottomTab = new JCheckBoxMenuItem("Bottom");
	private JCheckBoxMenuItem leftTab = new JCheckBoxMenuItem("Left");
	private JCheckBoxMenuItem topTab2 = new JCheckBoxMenuItem("Top");
	private JCheckBoxMenuItem bottomTab2 = new JCheckBoxMenuItem("Bottom");
	private JCheckBoxMenuItem leftTab2 = new JCheckBoxMenuItem("Left");
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();
	private JMenu loknfel = new JMenu("GUI Look and Feel");
	private JCheckBoxMenuItem systemlok = new JCheckBoxMenuItem("System");
	private JCheckBoxMenuItem crossPlat = new JCheckBoxMenuItem("Cross Platform");

	/** Creates new form MainOptions */
	public Preferences()
	{
		setText("Preferences");
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception
	{

		this.add(campLoad);
		campLoad.setToolTipText("Load selected source materials on start");
		campLoad.addActionListener(checkBoxHandler);
		campLoad.setSelected(Globals.isLoadCampaignsAtStart());
		campLoad.setMnemonic('S');
		this.add(browserPath);
		browserPath.setToolTipText("Change external browser path");
		browserPath.addActionListener(checkBoxHandler);
		browserPath.setMnemonic('P');
		this.add(templateDefault);
		templateDefault.setToolTipText("Change default PC export template");
		templateDefault.addActionListener(checkBoxHandler);
		templateDefault.setMnemonic('D');
		this.add(tabPlacement);
		tabPlacement.setToolTipText("Select where the tabs are");
		tabPlacement.setMnemonic('M');
		tabPlacement.add(topTab);
		topTab.addActionListener(checkBoxHandler);
		tabPlacement.add(bottomTab);
		bottomTab.addActionListener(checkBoxHandler);
		tabPlacement.add(leftTab);
		leftTab.addActionListener(checkBoxHandler);
		leftTab.setMnemonic('L');
		topTab.setMnemonic('T');
		bottomTab.setMnemonic('B');
		leftTab2.setMnemonic('L');
		topTab2.setMnemonic('T');
		bottomTab2.setMnemonic('B');

		switch (Globals.getTabPlacement())
		{
			case 0:
				topTab.setSelected(true);
				break;
			case 1:
				leftTab.setSelected(true);
				break;
			case 2:
				bottomTab.setSelected(true);
				break;
		}

		this.addSeparator();
		this.add(loknfel);
		loknfel.setEnabled(false);
		loknfel.add(systemlok);
		loknfel.add(crossPlat);
		systemlok.setToolTipText("Sets the look to that of the System your using");
		crossPlat.setToolTipText("Sets the look to that of Java's cross platform look");
		systemlok.addActionListener(checkBoxHandler);
		crossPlat.addActionListener(checkBoxHandler);
		if (Globals.getLooknFeel() == 0)
		{
			crossPlat.setSelected(true);
		}
		else if (Globals.getLooknFeel() == 1)
		{
			systemlok.setSelected(true);
		}

	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 */
	private final class CheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			Object source = actionEvent.getSource();
			if (source == bottomTab)
			{
				bottomTab.requestFocus();
				Globals.setTabPlacement(2);
				leftTab.setSelected(false);
				topTab.setSelected(false);
				PCGen_Frame1.getBaseTabbedPanel().setTabPlacement(JTabbedPane.BOTTOM);
				updateUI();
			}
			else if (source == leftTab)
			{
				leftTab.requestFocus();
				Globals.setTabPlacement(1);
				bottomTab.setSelected(false);
				topTab.setSelected(false);
				PCGen_Frame1.getBaseTabbedPanel().setTabPlacement(JTabbedPane.LEFT);
				updateUI();
			}
			else if (source == topTab)
			{
				topTab.requestFocus();
				Globals.setTabPlacement(0);
				bottomTab.setSelected(false);
				leftTab.setSelected(false);
				PCGen_Frame1.getBaseTabbedPanel().setTabPlacement(JTabbedPane.TOP);
				updateUI();
			}
			else if (source == campLoad)
			{
				Globals.setLoadCampaignsAtStart(campLoad.isSelected());
			}
			else if (source == browserPath)
			{
				final JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Find and select your preferred html browser.");

				if (Globals.getBrowserPath() == null)
				{
					//No action, as we have no idea what a good default would be...
				}
				else
				{
					fc.setCurrentDirectory(Globals.getBrowserPath());
				}
				int returnVal = fc.showOpenDialog(getParent().getParent()); //ugly, but it works
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					Globals.setBrowserPath(file);
				}
			}
			else if (source == templateDefault)
			{
				final JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Find and select your default PC export template.");
				fc.setCurrentDirectory(Globals.getTemplatePath());
				fc.setSelectedFile(new File(Globals.getSelectedTemplate()));
				if (fc.showOpenDialog(getParent().getParent()) == JFileChooser.APPROVE_OPTION) //ugly, but it works
				{
					File newTemplate = fc.getSelectedFile();
					if (newTemplate.isDirectory() || (!newTemplate.getName().startsWith("csheet") && !newTemplate.getName().startsWith("psheet")))
					{
						JOptionPane.showMessageDialog(null, "Item selected does not appear to be a template file.  Please try again.", "PCGen", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						if (newTemplate.getName().startsWith("csheet"))
						{
							Globals.setSelectedTemplate(newTemplate.getAbsolutePath());
							Globals.setTemplatePath(newTemplate.getParentFile());
						}
						else
						{
							//it must be a psheet
							Globals.setSelectedPartyTemplate(newTemplate.getAbsolutePath());
							Globals.setTemplatePath(newTemplate.getParentFile());
						}
					}
				}
			}
			else if (source == bottomTab2)
			{
				bottomTab2.requestFocus();
				Globals.setChaTabPlacement(2);
				leftTab2.setSelected(false);
				topTab2.setSelected(false);
				CharacterInfo.setTabPlacement(JTabbedPane.BOTTOM);
				updateUI();
			}
			else if (source == leftTab2)
			{
				leftTab2.requestFocus();
				Globals.setChaTabPlacement(1);
				bottomTab2.setSelected(false);
				topTab2.setSelected(false);
				CharacterInfo.setTabPlacement(JTabbedPane.LEFT);
				updateUI();
			}
			else if (source == topTab2)
			{
				topTab2.requestFocus();
				Globals.setChaTabPlacement(0);
				bottomTab2.setSelected(false);
				leftTab2.setSelected(false);
				CharacterInfo.setTabPlacement(JTabbedPane.TOP);
				updateUI();
			}
			else if (source == systemlok)
			{
				systemlok.requestFocus();
				Globals.setLooknFeel(0);
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch (Exception e)
				{
					//Hardly a fatal error, and quite unlikely at that...
					e.printStackTrace();
				}
				crossPlat.setSelected(false);
				updateUI();

			}
			else if (source == crossPlat)
			{
				crossPlat.requestFocus();
				Globals.setLooknFeel(1);
				try
				{
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				}
				catch (Exception e)
				{
					//Hardly a fatal error, and quite unlikely at that...
					e.printStackTrace();
				}
				systemlok.setSelected(false);
				updateUI();

			}
		}
	}
}
