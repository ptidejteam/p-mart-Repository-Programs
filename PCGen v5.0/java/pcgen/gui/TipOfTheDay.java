/*
 * TipOfTheDay.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 17, 2001, 12:43 PM
 *
 * $Id: TipOfTheDay.java,v 1.1 2006/02/21 01:07:48 vauchers Exp $
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.util.PropertyFactory;

/**
 *
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
final class TipOfTheDay extends JFrame implements ActionListener
{

	private ArrayList tipList = null;

	// the pane to display the text
	private JLabelPane tipText;
	private int lastNumber = -1;
	private JCheckBox chkShowTips;

	private static final String NEXT = "next";

	private static final String HTML_START = "<html><body style=\"margin-left: 5px;margin-right: 5px;margin-top: 5px\">";
	private static final String HTML_END = "</body></html>";

	/** Creates new TipOfTheDay */
	TipOfTheDay()
	{
		super();

		Utility.maybeSetIcon(this, "TipOfTheDay16.gif");

		setTitle(PropertyFactory.getString("in_tod_title"));

		// initialize the interface
		initUI();

		// load tips
		loadTips();

		pack();

		Utility.centerFrame(this, false);
		lastNumber = SettingsHandler.getLastTipShown();
		showNextTip();
	}

	//
	// initialize the dialog
	//
	private void initUI()
	{
		final JPanel panel = new JPanel(new BorderLayout(2, 2));
		panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		JLabel iconLabel;
		final Icon icon = Utility.getImageIcon("TipOfTheDay24.gif");
		if (icon != null)
		{
			iconLabel = new JLabel(icon);
		}
		else
		{
			iconLabel = new JLabel("TipOfTheDay24.gif");
		}
		iconLabel.setOpaque(true);
		panel.add(iconLabel, BorderLayout.WEST);

		final JLabel lblDidYouKnow = new JLabel("    " + PropertyFactory.getString("in_tod_didyouknow"));
		final Font old = lblDidYouKnow.getFont();
		lblDidYouKnow.setFont(old.deriveFont(old.getStyle() | Font.ITALIC, 18f));
		lblDidYouKnow.setOpaque(true);

		tipText = new JLabelPane();
		tipText.setBorder(null);
		tipText.addHyperlinkListener(new pcgen.util.Hyperactive());

		final JScrollPane pane = new JScrollPane(tipText);
		pane.setBorder(null);

		final JPanel content = new JPanel(new BorderLayout(0, 2));
		content.add(lblDidYouKnow, BorderLayout.NORTH);
		content.add(pane, BorderLayout.CENTER);
		//content.setPreferredSize(new Dimension(300, 200));
		content.setPreferredSize(new Dimension(585, 230));

		panel.add(content, BorderLayout.CENTER);

		chkShowTips = new JCheckBox(PropertyFactory.getString("in_tod_showTips"), SettingsHandler.getShowTipOfTheDay());

		final JButton btnClose = new JButton(PropertyFactory.getString("in_close"));
		btnClose.setMnemonic(PropertyFactory.getMnemonic("in_mn_close"));
		btnClose.addActionListener(this);

		final JButton btnNextTip = new JButton(PropertyFactory.getString("in_tod_nextTip"));
		btnNextTip.setMnemonic(PropertyFactory.getMnemonic("in_mn_tod_nextTip"));
		btnNextTip.addActionListener(this);
		btnNextTip.setActionCommand(NEXT);

		final JPanel actions = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);
		actions.add(chkShowTips, c);

		final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(btnNextTip);
		buttons.add(btnClose);
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		actions.add(buttons, c);

		panel.add(actions, BorderLayout.SOUTH);
		setContentPane(panel);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				quit();
			}
		});
		addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					quit();
				}
			}
		});
	}

	private void loadTips()
	{
		tipList = new ArrayList(20);
		final String tipsFilePath = SettingsHandler.getPcgenSystemDir() + File.separator + "tips.lst";
		try
		{
			final File tipsFile = new File(tipsFilePath);
			//final BufferedReader tipsReader = new BufferedReader(new FileReader(tipsFile));
			final BufferedReader tipsReader = new BufferedReader(new InputStreamReader(new FileInputStream(tipsFile), "UTF-8"));
			final int length = (int) tipsFile.length();
			final char[] inputLine = new char[length];
			tipsReader.read(inputLine, 0, length);
			tipsReader.close();
			final StringTokenizer aTok = new StringTokenizer(new String(inputLine), "\r\n", false);
			while (aTok.hasMoreTokens())
			{
				tipList.add(aTok.nextToken());
			}
		}
		catch (FileNotFoundException e)
		{
			Globals.errorPrint("Could not find tips.lst at " + tipsFilePath, e);
		}
		catch (IOException e)
		{
			Globals.errorPrint("Could not find tips.lst at " + tipsFilePath, e);
		}
	}

	private boolean hasTips()
	{
		return (tipList != null) && (tipList.size() > 0);
	}

	private void showNextTip()
	{
		if (hasTips())
		{
			if (++lastNumber >= tipList.size())
			{
				lastNumber = 0;
			}
			final String tip = (String) tipList.get(lastNumber);

			try
			{
				tipText.setText(HTML_START + "<b>Tip#" + Integer.toString(lastNumber + 1) + "</b><br>" + tip + HTML_END);
				repaint();
			}
			catch (Exception exc)
			{
				exc.printStackTrace(System.err);
			}
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if (NEXT.equals(e.getActionCommand()))
		{
			showNextTip();
			return;
		}
		quit();
	}

	// close the dialog and save the settings
	private void quit()
	{
		setVisible(false);

		SettingsHandler.setLastTipShown(lastNumber);
		SettingsHandler.setShowTipOfTheDay(chkShowTips.isSelected());

		dispose();
	}

}
