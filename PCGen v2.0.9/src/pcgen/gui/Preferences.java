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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import pcgen.core.Globals;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
public class Preferences extends JMenu
{
    private JCheckBoxMenuItem campLoad = new JCheckBoxMenuItem("Load Campaigns");
	  private JMenuItem browserPath = new JMenuItem("Browser Path");
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
    campLoad.setToolTipText("Load selected campaigns on start");
    campLoad.addActionListener(checkBoxHandler);
    campLoad.setSelected(Globals.loadCampaignsAtStart);
		this.add(browserPath);
		browserPath.setToolTipText("Change external browser path");
		browserPath.addActionListener(checkBoxHandler);
    //		browserPath.setSelected(Globals.getBrowserPath());
    this.add(tabPlacement);
    tabPlacement.setToolTipText("Select where the tabs are");
    tabPlacement.add(topTab);
    topTab.addActionListener(checkBoxHandler);
    tabPlacement.add(bottomTab);
    bottomTab.addActionListener(checkBoxHandler);
    tabPlacement.add(leftTab);
    leftTab.addActionListener(checkBoxHandler);
    switch (Globals.tabPlacement)
    {
      case 0: topTab.setSelected(true); break;
      case 1: leftTab.setSelected(true); break;
      case 2: bottomTab.setSelected(true); break;
    }
    /*this.add(chaTabPlacement);
    chaTabPlacement.setToolTipText("Select where the tabs are");
    chaTabPlacement.add(topTab2);
    topTab2.addActionListener(checkBoxHandler);
    chaTabPlacement.add(bottomTab2);
    bottomTab2.addActionListener(checkBoxHandler);
    chaTabPlacement.add(leftTab2);
    leftTab2.addActionListener(checkBoxHandler);
    if (Globals.chaTabPlacement == 0)
    {
      topTab2.setSelected(true);
    }
    else if (Globals.chaTabPlacement == 1)
    {
      leftTab2.setSelected(true);
    }
    else if (Globals.chaTabPlacement == 2)
    {
      bottomTab2.setSelected(true);
    }*/

    this.addSeparator();
    this.add(loknfel);
    loknfel.setEnabled(false);
    loknfel.add(systemlok);
    loknfel.add(crossPlat);
    systemlok.setToolTipText("Sets the look to that of the System your using");
    crossPlat.setToolTipText("Sets the look to that of Java's cross platform look");
    systemlok.addActionListener(checkBoxHandler);
    crossPlat.addActionListener(checkBoxHandler);
    if (Globals.looknFeel == 0)
    {
      crossPlat.setSelected(true);
    }
    else if (Globals.looknFeel == 1)
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
        Globals.tabPlacement = 2;
        leftTab.setSelected(false);
        topTab.setSelected(false);
        PCGen_Frame1.baseTabbedPanel.setTabPlacement(JTabbedPane.BOTTOM);
        updateUI();
      }
      else if (source == leftTab)
      {
        leftTab.requestFocus();
        Globals.tabPlacement = 1;
        bottomTab.setSelected(false);
        topTab.setSelected(false);
        PCGen_Frame1.baseTabbedPanel.setTabPlacement(JTabbedPane.LEFT);
        updateUI();
      }
      else if (source == topTab)
      {
        topTab.requestFocus();
        Globals.tabPlacement = 0;
        bottomTab.setSelected(false);
        leftTab.setSelected(false);
        PCGen_Frame1.baseTabbedPanel.setTabPlacement(JTabbedPane.TOP);
        updateUI();
      }
      else if (source == campLoad)
      {
        Globals.loadCampaignsAtStart = campLoad.isSelected();
      }
      else if (source == browserPath)
      {
        BrowserPathFrame frame = new BrowserPathFrame();
      }
      else if (source == bottomTab2)
      {
        bottomTab2.requestFocus();
        Globals.chaTabPlacement = 2;
        leftTab2.setSelected(false);
        topTab2.setSelected(false);
        CharacterInfo.setTabPlacement(JTabbedPane.BOTTOM);
        updateUI();
      }
      else if (source == leftTab2)
      {
        leftTab2.requestFocus();
        Globals.chaTabPlacement = 1;
        bottomTab2.setSelected(false);
        topTab2.setSelected(false);
        CharacterInfo.setTabPlacement(JTabbedPane.LEFT);
        updateUI();
      }
      else if (source == topTab2)
      {
        topTab2.requestFocus();
        Globals.chaTabPlacement = 0;
        bottomTab2.setSelected(false);
        leftTab2.setSelected(false);
        CharacterInfo.setTabPlacement(JTabbedPane.TOP);
        updateUI();
      }
      else if (source == systemlok)
      {
        systemlok.requestFocus();
        Globals.looknFeel = 0;
        try
        {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
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
        Globals.looknFeel = 1;
        try
        {
          UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch(Exception e)
        {
          //Hardly a fatal error, and quite unlikely at that...
          e.printStackTrace();
        }
        systemlok.setSelected(false);
        updateUI();
        
      }
    }
  }

	private class BrowserPathFrame extends JFrame
	{
    // Entry Area
    // Browse Button
    //    JLabel lblBrowserPathInfo = new JLabel();
    JLabel lblPath = new JLabel();
    JTextField txtPath = new JTextField();
    JButton btnBrowse = new JButton();
    final JFileChooser fc = new JFileChooser();

		public BrowserPathFrame()
		{
			super("Browse Path");
			ClassLoader loader = getClass().getClassLoader();
			Toolkit kit = Toolkit.getDefaultToolkit();
			// according to the API, the following should *ALWAYS* use '/'
			Image img =
				kit.getImage(loader.getResource("pcgen/gui/PcgenIcon.gif"));
			loader = null;
			this.setIconImage(img);
			Dimension screenSize = kit.getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;
			// center frame in screen
			setSize(600, 50);
			setLocation(screenWidth / 5, screenHeight / 3);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
      // Entry and Button
      //      panel.add(lblBrowserPathInfo, BorderLayout.NORTH);
      panel.add(lblPath, BorderLayout.WEST);
      panel.add(txtPath, BorderLayout.CENTER);
      panel.add(btnBrowse, BorderLayout.EAST);
      //      lblBrowserPathInfo.setText("Select or type the path to the external browser you wish to use.");
      lblPath.setText("Browser Path:");
      txtPath.setMinimumSize(new Dimension(500, 21));
      txtPath.setPreferredSize(new Dimension(500, 21));
      txtPath.setBackground(Color.white);
      txtPath.setText(Globals.getBrowserPath().getAbsolutePath());

      btnBrowse.setText("...");
      btnBrowse.setPreferredSize(new Dimension(50, 25));
      btnBrowse.setMaximumSize(new Dimension(70, 25));
      btnBrowse.setMargin(new Insets(2, 5, 2, 5));
      btnBrowse.setMinimumSize(new Dimension(50, 25));
      btnBrowse.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          if(Globals.getBrowserPath().getAbsolutePath() == "")
            fc.setCurrentDirectory(Globals.getPcgPath());
          else
            fc.setCurrentDirectory(Globals.getBrowserPath());
          int returnVal = fc.showOpenDialog(Preferences.this);
          if (returnVal == JFileChooser.APPROVE_OPTION)
          {
            File file = fc.getSelectedFile();
            Globals.setBrowserPath(file);
            txtPath.setText(Globals.getBrowserPath().getAbsolutePath());
          }
        }
      });
			Container contentPane = getContentPane();
			contentPane.add(panel);
			setVisible(true);
		}
	}

}
