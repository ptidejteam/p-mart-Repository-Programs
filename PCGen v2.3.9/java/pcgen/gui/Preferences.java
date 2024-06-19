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
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import pcgen.core.Globals;
import pcgen.util.SkinLFResourceChecker;

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
	private JMenuItem clearBrowserPath = new JMenuItem("Clear Browser Path");
	private JMenuItem templateDefault = new JMenuItem("Default Export Template...");
	private JMenu mainTabPlacement = new JMenu("Main Tab Placement");
	private JMenu chaTabPlacement = new JMenu("Character Tab Placement");
	private JCheckBoxMenuItem mainTabTop = new JCheckBoxMenuItem("Top");
	private JCheckBoxMenuItem mainTabBottom = new JCheckBoxMenuItem("Bottom");
	private JCheckBoxMenuItem mainTabLeft = new JCheckBoxMenuItem("Left");
	private JCheckBoxMenuItem mainTabRight = new JCheckBoxMenuItem("Right");
	private JCheckBoxMenuItem chaTabTop = new JCheckBoxMenuItem("Top");
	private JCheckBoxMenuItem chaTabBottom = new JCheckBoxMenuItem("Bottom");
	private JCheckBoxMenuItem chaTabLeft = new JCheckBoxMenuItem("Left");
	private JCheckBoxMenuItem chaTabRight = new JCheckBoxMenuItem("Right");
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();
	private ChaTabCheckBoxListener chaTabCheckBoxHandler = new ChaTabCheckBoxListener();
	private MainTabCheckBoxListener mainTabCheckBoxHandler = new MainTabCheckBoxListener();
	private LookFeelCheckBoxListener lookFeelCheckBoxHandler = new LookFeelCheckBoxListener();
	private JMenu looknfeel = new JMenu("GUI Look and Feel");
	private JCheckBoxMenuItem laf[];
	private JCheckBoxMenuItem skinnedLookFeel = new JCheckBoxMenuItem("Skinned");
	private JMenuItem themepack = new JMenuItem("Choose Skin Themepack...");

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
		this.add(clearBrowserPath);
		clearBrowserPath.setToolTipText("Reset the browser path to the system default");
		clearBrowserPath.addActionListener(checkBoxHandler);
		clearBrowserPath.setMnemonic('C');
		this.add(templateDefault);
		templateDefault.setToolTipText("Change default PC export template");
		templateDefault.addActionListener(checkBoxHandler);
		templateDefault.setMnemonic('D');
		this.add(mainTabPlacement);
		mainTabPlacement.setToolTipText("Select where the tabs should be");
		mainTabPlacement.setMnemonic('M');
		mainTabPlacement.add(mainTabTop);
		mainTabTop.addActionListener(mainTabCheckBoxHandler);
		mainTabPlacement.add(mainTabBottom);
		mainTabBottom.addActionListener(mainTabCheckBoxHandler);
		mainTabPlacement.add(mainTabLeft);
		mainTabLeft.addActionListener(mainTabCheckBoxHandler);
		mainTabPlacement.add(mainTabRight);
		mainTabRight.addActionListener(mainTabCheckBoxHandler);
		mainTabLeft.setMnemonic('L');
		mainTabRight.setMnemonic('R');
		mainTabTop.setMnemonic('T');
		mainTabBottom.setMnemonic('B');

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

		this.add(chaTabPlacement);
		chaTabPlacement.setToolTipText("Select where the character specific tabs should be (won't affect currently loaded characters)");
		chaTabPlacement.setMnemonic('C');
		chaTabPlacement.add(chaTabTop);
		chaTabTop.addActionListener(chaTabCheckBoxHandler);
		chaTabPlacement.add(chaTabBottom);
		chaTabBottom.addActionListener(chaTabCheckBoxHandler);
		chaTabPlacement.add(chaTabLeft);
		chaTabLeft.addActionListener(chaTabCheckBoxHandler);
		chaTabPlacement.add(chaTabRight);
		chaTabRight.addActionListener(chaTabCheckBoxHandler);
		chaTabLeft.setMnemonic('L');
		chaTabRight.setMnemonic('R');
		chaTabTop.setMnemonic('T');
		chaTabBottom.setMnemonic('B');

		switch (Globals.getChaTabPlacement())
		{
			case 0:
				chaTabTop.setSelected(true);
				break;
			case 1:
				chaTabLeft.setSelected(true);
				break;
			case 2:
				chaTabBottom.setSelected(true);
				break;
			case 3:
				chaTabRight.setSelected(true);
				break;
		}

                /**
                 * author: Thomas Behr 06-01-02
                 */
		this.addSeparator();
		this.add(looknfeel);
		looknfeel.setEnabled(true);
                laf = new JCheckBoxMenuItem[ UIFactory.getLnFCount() ];
                for ( int i = 0; i < laf.length; i++ ) {
                        laf[ i ] = new JCheckBoxMenuItem();
                        laf[ i ].setText( UIFactory.getLnFName( i ) );
                        laf[ i ].setToolTipText( UIFactory.getLnFTooltip( i ) );
//                          laf[ i ].setMnemonic('');
                        laf[ i ].addActionListener(lookFeelCheckBoxHandler);
                        looknfeel.add( laf[ i ] );
                }
                int crossIndex = UIFactory.indexOfCrossPlatformLnF();

		looknfeel.add(skinnedLookFeel);
		skinnedLookFeel.setToolTipText("Sets the look to that of the select themepack (see next option), or to cross platform if there is no themepack selected. Probably needs a restart of pcgen to work correctly.");
		skinnedLookFeel.setMnemonic('K');
		skinnedLookFeel.addActionListener(lookFeelCheckBoxHandler);
		if (Globals.getLooknFeel() < laf.length)
		{
			laf[ Globals.getLooknFeel() ].setSelected(true);
		}
		else if (Globals.getLooknFeel() == laf.length)
		{
			if ((SkinLFResourceChecker.getMissingResourceCount() == 0))
				skinnedLookFeel.setSelected(true);
			else
				laf[ crossIndex ].setSelected(true);
		} else {
                        laf[ crossIndex ].setSelected(true);
                }
                
		looknfeel.add(themepack);
		themepack.setToolTipText("Change skin themepack (get some at http://javootoo.l2fprod.com/plaf/skinlf/index.php)");
		themepack.addActionListener(checkBoxHandler);
		themepack.setMnemonic('T');

	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 */
	private final class CheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();
			if (source == campLoad)
			{
				Globals.setLoadCampaignsAtStart(campLoad.isSelected());
			}
			else if (source == browserPath)
			{
				final JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Find and select your preferred html browser.");
				if (System.getProperty("os.name").startsWith("Mac OS"))
				{
					// On MacOS X, do not traverse file bundles
					fc.putClientProperty("JFileChooser.appBundleIsTraversable", "never");
				}

				if (Globals.getBrowserPath() == null)
				{
					//No action, as we have no idea what a good default would be...
				}
				else
				{
					fc.setCurrentDirectory(Globals.getBrowserPath());
				}
				final int returnVal = fc.showOpenDialog(getParent().getParent()); //ugly, but it works
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					final File file = fc.getSelectedFile();
					Globals.setBrowserPath(file);
				}
			}
			else if (source == clearBrowserPath)
			{
				// If none is set, there is nothing to clear
				if (Globals.getBrowserPath() == null) return;
				final int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the default browser?", "Clear default browser", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION)
				{
					Globals.setBrowserPath(null);
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
			else if (source == themepack)
			{
				final JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Find and select your default l2fprod themepack.");
				fc.setCurrentDirectory(new File(Globals.getSkinLFThemePack()));
				fc.setSelectedFile(new File(Globals.getSkinLFThemePack()));
				if (fc.showOpenDialog(getParent().getParent()) == JFileChooser.APPROVE_OPTION) //ugly, but it works
				{
					File newTheme = fc.getSelectedFile();
					if (newTheme.isDirectory() || (!newTheme.getName().endsWith("themepack.zip")))
					{
						JOptionPane.showMessageDialog(null, "Item selected does not appear to be a themepack file.  Please try again.", "PCGen", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						if (newTheme.getName().endsWith("themepack.zip"))
						{
							Globals.setSkinLFThemePack(newTheme.getAbsolutePath());
						}
					}
				}
			}
		}
	}

	private final class ChaTabCheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();

			chaTabLeft.setSelected(false);
			chaTabRight.setSelected(false);
			chaTabTop.setSelected(false);
			chaTabBottom.setSelected(false);

			if (source == chaTabBottom)
			{
				chaTabBottom.requestFocus();
				Globals.setChaTabPlacement(2);
				chaTabBottom.setSelected(true);
			}
			else if (source == chaTabLeft)
			{
				chaTabLeft.requestFocus();
				Globals.setChaTabPlacement(1);
				chaTabLeft.setSelected(true);
			}
			else if (source == chaTabRight)
			{
				chaTabRight.requestFocus();
				Globals.setChaTabPlacement(3);
				chaTabRight.setSelected(true);
			}
			else if (source == chaTabTop)
			{
				chaTabTop.requestFocus();
				Globals.setChaTabPlacement(0);
				chaTabTop.setSelected(true);
			}
			updateUI();
		}
	}

	private final class LookFeelCheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();
                        int sourceIndex = laf.length;
                        
			// grab these states here so we can put them back
                        // if the user pick Skinned L&F without skinlf.jar installed

                        /**
                         * author: Thomas Behr 06-01-02
                         */
                        boolean restore[] = new boolean[ laf.length ];
                        for ( int i = 0; i < laf.length; i++ ) {
                                restore[ i ] = laf[ i ].getState();
                                laf[ i ].setSelected(false);
                                if ( source.equals( laf[ i ] ) ) {
                                        sourceIndex = i;
                                }
                        }
			skinnedLookFeel.setSelected(false);

			if (sourceIndex < laf.length)
			{
				laf[ sourceIndex ].requestFocus();
				laf[ sourceIndex ].setSelected(true);
				Globals.setLooknFeel( sourceIndex );
				UIFactory.setLookAndFeel( sourceIndex );
			}
			else if (source == skinnedLookFeel)
			{
				skinnedLookFeel.requestFocus();
				if ((SkinLFResourceChecker.getMissingResourceCount() == 0))
				{
					skinnedLookFeel.setSelected(true);
					Globals.setLooknFeel(laf.length);
					try
					{
						SkinManager.applySkin();
					}
					catch (Exception e)
					{
						//I can't think of anything better to do.
						UIFactory.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					}
				}
				else
				{
					System.err.println(SkinLFResourceChecker.getMissingResourceMessage());
					String missingLibMsg;
					ResourceBundle d_properties;
					try
					{
						d_properties = ResourceBundle.getBundle("pcgen/gui/PCGenProp");
						missingLibMsg = d_properties.getString("MissingLibMessage");
					}
					catch (MissingResourceException mrex)
					{
						missingLibMsg = "This feature requires the download of the above mentiond file(s) " +
							"(http://sourceforge.net/projects/pcgen/).\n" +
							"Please download and place in the \"lib\" sub-directory of your PCGen installation.\n" +
							"You must then restart PCGen for full functionality.";
						mrex.printStackTrace();
					}
					finally
					{
						d_properties = null;
					}
					missingLibMsg = missingLibMsg.replace('|', '\n');
					JOptionPane.showMessageDialog(null,
						SkinLFResourceChecker.getMissingResourceMessage() +
						"\n" + missingLibMsg,
						"PCGen",
						JOptionPane.WARNING_MESSAGE);

                                        for ( int i = 0; i < laf.length; i++ ) {
                                                laf[ i ].setSelected( restore[ i ] );
                                        }
				}
			}

			updateUI();
		}
	}

	private final class MainTabCheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();

			mainTabBottom.setSelected(false);
			mainTabLeft.setSelected(false);
			mainTabRight.setSelected(false);
			mainTabTop.setSelected(false);
			if (source == mainTabBottom)
			{
				mainTabBottom.requestFocus();
				Globals.setTabPlacement(2);
				mainTabBottom.setSelected(true);
				PCGen_Frame1.getBaseTabbedPanel().setTabPlacement(JTabbedPane.BOTTOM);
			}
			else if (source == mainTabLeft)
			{
				mainTabLeft.requestFocus();
				Globals.setTabPlacement(1);
				mainTabLeft.setSelected(true);
				PCGen_Frame1.getBaseTabbedPanel().setTabPlacement(JTabbedPane.LEFT);
			}
			else if (source == mainTabRight)
			{
				mainTabRight.requestFocus();
				Globals.setTabPlacement(3);
				mainTabRight.setSelected(true);
				PCGen_Frame1.getBaseTabbedPanel().setTabPlacement(JTabbedPane.RIGHT);
			}
			else if (source == mainTabTop)
			{
				mainTabTop.requestFocus();
				Globals.setTabPlacement(0);
				mainTabTop.setSelected(true);
				PCGen_Frame1.getBaseTabbedPanel().setTabPlacement(JTabbedPane.TOP);
			}
			updateUI();
		}
	}
}
