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
 * This has lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
public class Preferences extends JMenu
{
	private JCheckBoxMenuItem campLoad = new JCheckBoxMenuItem("Auto-load Source Materials");
	private JMenuItem browserPath = new JMenuItem("Browser Path...");
	private JMenuItem templateDefault = new JMenuItem("Default Export Template...");
	private JMenu mainTabPlacement = new JMenu("Main Tab Placement");
	private JMenu chaTabPlacement = new JMenu("Character Tab Placement");
	private JCheckBoxMenuItem mainTabTop = new JCheckBoxMenuItem("Top");
	private JCheckBoxMenuItem mainTabBottom = new JCheckBoxMenuItem("Bottom");
	private JCheckBoxMenuItem mainTabLeft = new JCheckBoxMenuItem("Left");
	private JCheckBoxMenuItem mainTabRight = new JCheckBoxMenuItem("Right");
	private JCheckBoxMenuItem chaTabTop = new JCheckBoxMenuItem("Top");
	private JCheckBoxMenuItem chaTabBottom = new JCheckBoxMenuItem("Bottom");
	private JCheckBoxMenuItem chaTabLeft = new JCheckBoxMenuItem("Right");
	private JCheckBoxMenuItem chaTabRight = new JCheckBoxMenuItem("Right");
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();
	private JMenu loknfel = new JMenu("GUI Look and Feel");
	private JCheckBoxMenuItem systemLookFeel = new JCheckBoxMenuItem("System");
	private JCheckBoxMenuItem crossPlatformLookFeel = new JCheckBoxMenuItem("Cross Platform");

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
		this.add(mainTabPlacement);
		mainTabPlacement.setToolTipText("Select where the tabs are");
		mainTabPlacement.setMnemonic('M');
		mainTabPlacement.add(mainTabTop);
		mainTabTop.addActionListener(checkBoxHandler);
		mainTabPlacement.add(mainTabBottom);
		mainTabBottom.addActionListener(checkBoxHandler);
		mainTabPlacement.add(mainTabLeft);
		mainTabLeft.addActionListener(checkBoxHandler);
		mainTabLeft.setMnemonic('L');
		mainTabPlacement.add(mainTabRight);
		mainTabRight.addActionListener(checkBoxHandler);
		mainTabRight.setMnemonic('R');
		mainTabTop.setMnemonic('T');
		mainTabBottom.setMnemonic('B');
		chaTabLeft.setMnemonic('L');
		chaTabRight.setMnemonic('R');
		chaTabTop.setMnemonic('T');
		chaTabBottom.setMnemonic('B');

		switch (Globals.getTabPlacement())
		{
			case 0:
				mainTabTop.setSelected(true);
				break;
			case 1:
				mainTabLeft.setSelected(true);
				break;
			case 2:
				mainTabBottom.setSelected(true);
				break;
			case 3:
				mainTabRight.setSelected(true);
				break;

		}

		this.addSeparator();
		this.add(loknfel);
		loknfel.setEnabled(false);
		loknfel.add(systemLookFeel);
		loknfel.add(crossPlatformLookFeel);
		systemLookFeel.setToolTipText("Sets the look to that of the System your using");
		crossPlatformLookFeel.setToolTipText("Sets the look to that of Java's cross platform look");
		systemLookFeel.addActionListener(checkBoxHandler);
		crossPlatformLookFeel.addActionListener(checkBoxHandler);
		if (Globals.getLooknFeel() == 0)
		{
			crossPlatformLookFeel.setSelected(true);
		}
		else if (Globals.getLooknFeel() == 1)
		{
			systemLookFeel.setSelected(true);
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
			if (source == mainTabBottom)
			{
				mainTabBottom.requestFocus();
				Globals.setTabPlacement(2);
				mainTabLeft.setSelected(false);
				mainTabRight.setSelected(false);
				mainTabTop.setSelected(false);
				PCGen_Frame1.getBaseTabbedPanel().setTabPlacement(JTabbedPane.BOTTOM);
				updateUI();
			}
			else if (source == mainTabLeft)
			{
				mainTabLeft.requestFocus();
				Globals.setTabPlacement(1);
				mainTabBottom.setSelected(false);
				mainTabRight.setSelected(false);
				mainTabTop.setSelected(false);
				PCGen_Frame1.getBaseTabbedPanel().setTabPlacement(JTabbedPane.LEFT);
				updateUI();
			}
			else if (source == mainTabRight)
			{
				mainTabRight.requestFocus();
				Globals.setTabPlacement(3);
				mainTabBottom.setSelected(false);
				mainTabLeft.setSelected(false);
				mainTabTop.setSelected(false);
				PCGen_Frame1.getBaseTabbedPanel().setTabPlacement(JTabbedPane.RIGHT);
				updateUI();
			}
			else if (source == mainTabTop)
			{
				mainTabTop.requestFocus();
				Globals.setTabPlacement(0);
				mainTabBottom.setSelected(false);
				mainTabLeft.setSelected(false);
				mainTabRight.setSelected(false);
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
			else if (source == chaTabBottom)
			{
				chaTabBottom.requestFocus();
				Globals.setChaTabPlacement(2);
				chaTabLeft.setSelected(false);
				chaTabRight.setSelected(false);
				chaTabTop.setSelected(false);
				CharacterInfo.setTabPlacement(JTabbedPane.BOTTOM);
				updateUI();
			}
			else if (source == chaTabLeft)
			{
				chaTabLeft.requestFocus();
				Globals.setChaTabPlacement(1);
				chaTabBottom.setSelected(false);
				chaTabRight.setSelected(false);
				chaTabTop.setSelected(false);
				CharacterInfo.setTabPlacement(JTabbedPane.LEFT);
				updateUI();
			}
			else if (source == chaTabRight)
			{
				chaTabRight.requestFocus();
				Globals.setChaTabPlacement(3);
				chaTabBottom.setSelected(false);
				chaTabTop.setSelected(false);
				CharacterInfo.setTabPlacement(JTabbedPane.LEFT);
				updateUI();
			}
			else if (source == chaTabTop)
			{
				chaTabTop.requestFocus();
				Globals.setChaTabPlacement(0);
				chaTabBottom.setSelected(false);
				chaTabLeft.setSelected(false);
				chaTabRight.setSelected(false);
				CharacterInfo.setTabPlacement(JTabbedPane.TOP);
				updateUI();
			}
			else if (source == systemLookFeel)
			{
				systemLookFeel.requestFocus();
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
				crossPlatformLookFeel.setSelected(false);
				updateUI();

			}
			else if (source == crossPlatformLookFeel)
			{
				crossPlatformLookFeel.requestFocus();
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
				systemLookFeel.setSelected(false);
				updateUI();

			}
		}
	}
}
