/*
 * NameGui.java
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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.core.Globals;
import pcgen.core.NameRule;
import pcgen.core.Names;
import pcgen.core.PlayerCharacter;
import pcgen.core.RollingMethods;


/**
 * <code>NameGui</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public class NameGui extends JFrame
{
	private JList list;
	private DefaultListModel listModel;
	private JButton acceptButton;
	private JButton redoButton;
	private JTextField randName;
	private JLabel info;
	private ArrayList bArrayList = null;

	public NameGui()
	{
		super("Random Name Generator");
		ClassLoader loader = getClass().getClassLoader();
		Toolkit kit = Toolkit.getDefaultToolkit();
		// according to the API, the following should *ALWAYS* use '/'
		Image img = kit.getImage(loader.getResource("pcgen/gui/PcgenIcon.gif"));
		loader = null;
		this.setIconImage(img);
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		// center frame in screen
		setSize(screenWidth / 2, screenHeight / 2);
		setLocation(screenWidth / 4, screenHeight / 4);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		/**
		 * Set up the list of Name files
		 */
		listModel = new DefaultListModel();
		Iterator iter = Globals.getNameList().iterator();
		while (iter.hasNext())
		{
			listModel.addElement(iter.next());
		}

		//Create list and put it in a scroll pane
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(-1);
		list.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting() == false)
				{
					if (list.getSelectedIndex() == -1)
					{
						requestFocus();
						//No selection, disable redo button.
						redoButton.setEnabled(false);
						randName.setText("");
					}
					else
					{
						//Selection, update text field.
						redoButton.setEnabled(true);
						String name = list.getSelectedValue().toString();
						//Must call for the following to load the nam file for the choice.
						ArrayList aArrayList = Names.initNames(System.getProperty("user.dir") + "\\system\\bio\\names" + File.separator + name + ".nam", 0, new ArrayList());
						String aName = randomName();
						randName.setText(aName);
					}
				}
			}
		});
		JScrollPane listScrollPane = new JScrollPane(list);

		acceptButton = new JButton("Accept");
		acceptButton.setMnemonic('A');
		acceptButton.setActionCommand("Accept");
		acceptButton.addActionListener(new AcceptListener());

		redoButton = new JButton("Roll");
		redoButton.setMnemonic('R');
		redoButton.setActionCommand("Roll");
		redoButton.addActionListener(new ReRollListener());

		randName = new JTextField(15);
		randName.setEditable(false);

		info = new JLabel("Select a Name File and roll");

		//Create a panel that uses FlowLayout (the default).
		//JPanel buttonPane = new JPanel(new java.awt.GridLayout(1, 3));
		JPanel buttonPane = new JPanel();
		buttonPane.add(randName);
		buttonPane.add(redoButton);
		buttonPane.add(acceptButton);

		Container contentPane = getContentPane();
		contentPane.add(info, BorderLayout.NORTH);
		contentPane.add(listScrollPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
	}

	/* This function is called when the "Random" button is clicked and
 * randomly generates names */
	private static String randomName()
	{
		int x = 0;
		int y = 0;
		int roll = 0;
		Iterator iter = null;
		String aString = "";
		String finalString = "";
		String newlinedelim = new String("\t");
		ArrayList rules = new ArrayList();
		//Get Rules
		if (Names.getRuleList().size() > 0)
		{
			iter = Names.getRuleList().iterator();
			while (iter.hasNext())
			{
				StringTokenizer newlineStr = new StringTokenizer(iter.next().toString(), newlinedelim, false);
				NameRule newRule = new NameRule();
				newRule.chance = Integer.parseInt(newlineStr.nextToken());
				while (newlineStr.hasMoreTokens())
				{
					newRule.rule.add(newlineStr.nextToken());
				}
				rules.add(newRule);
			}
		}

		NameRule ruleToUse = null;

		//Figure which rule to use.
		roll = RollingMethods.roll(1, 100);
		for (y = 0; y < rules.size(); y++)
		{
			if (roll <= ((NameRule)rules.get(y)).chance)
			{
				ruleToUse = (NameRule)rules.get(y);
				break;
			}
		}
		if (ruleToUse == null)
		{
			System.out.println("Couldn't find a name rule to use.");
			return "No random name available. Try again.";
		}
		iter = ruleToUse.rule.iterator();
		while (iter.hasNext())
		{
			String temp = iter.next().toString();
			if (temp.equals("[TITLE]"))
			{
				aString = getTitleName();
			}
			else if (temp.equals("[FULL]"))
			{
				aString = getFullName();
			}
			else if (temp.equals("[SYL1]"))
			{
				aString = getSyl1Name();
			}
			else if (temp.equals("[SYL2]"))
			{
				aString = getSyl2Name();
			}
			else if (temp.equals("[SYL3]"))
			{
				aString = getSyl3Name();
			}
			finalString = finalString + aString;
		}
		return finalString;
	}

	//for calling a random name from elsewhere.
	public static String getRandomName()
	{
		return randomName();
	}

	public static String getFullName()
	{
		String fullString = null;
		int roll = 0;
		Iterator iter = null;
		int i = 0;
		if (Names.getFullList().size() > 0)
		{
			int size = Names.getFullList().size();
			roll = RollingMethods.roll(1, size);
			iter = Names.getFullList().iterator();
			//System.out.println(i + " start " + roll);
			while (iter.hasNext())
			{
				i++;
				if (i == roll)
				{
					fullString = iter.next().toString();
					i = 0;
					break;
				}
				iter.next();
			}
		}
		else
		{
			return null;
		}
		return fullString;
	}

	public static String getTitleName()
	{
		String titleString = null;
		int roll = 0;
		Iterator iter = null;
		int i = 0;
		if (Names.getTitleList().size() > 0)
		{
			roll = RollingMethods.roll(1, Names.getTitleList().size());
			iter = Names.getTitleList().iterator();
			while (iter.hasNext())
			{
				i++;
				if (i == roll)
				{
					titleString = iter.next().toString();
					i = 0;
					break;
				}
				iter.next();
			}
		}
		else
		{
			return null;
		}
		return titleString;
	}

	public static String getSyl1Name()
	{
		String syl1String = null;
		int roll = 0;
		Iterator iter = null;
		int i = 0;
		if (Names.getSyl1List().size() > 0)
		{
			int roll1 = RollingMethods.roll(1, Names.getSyl1List().size());
			iter = Names.getSyl1List().iterator();
			while (iter.hasNext())
			{
				i++;
				if (i == roll1)
				{
					syl1String = iter.next().toString();
					i = 0;
					break;
				}
				iter.next();
			}
		}
		else
		{
			return null;
		}
		return syl1String;
	}

	public static String getSyl2Name()
	{
		String syl2String = null;
		int roll = 0;
		Iterator iter = null;
		int i = 0;
		if (Names.getSyl2List().size() > 0)
		{
			int roll2 = RollingMethods.roll(1, Names.getSyl2List().size());
			iter = Names.getSyl2List().iterator();
			while (iter.hasNext())
			{
				i++;
				if (i == roll2)
				{
					syl2String = iter.next().toString();
					i = 0;
					break;
				}
				iter.next();
			}
		}
		else
		{
			return null;
		}
		return syl2String;
	}

	public static String getSyl3Name()
	{
		String syl3String = null;
		int roll = 0;
		Iterator iter = null;
		int i = 0;
		if (Names.getSyl3List().size() > 0)
		{
			int roll3 = RollingMethods.roll(1, Names.getSyl3List().size());
			iter = Names.getSyl3List().iterator();
			while (iter.hasNext())
			{
				i++;
				if (i == roll3)
				{
					syl3String = iter.next().toString();
					i = 0;
					break;
				}
				iter.next();
			}
		}
		else
		{
			return null;
		}
		return syl3String;
	}

	class ReRollListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			randName.setText(randomName());
		}
	}


	class AcceptListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String aName = randName.getText().toString();
			//User didn't roll a name...
			if (randName.getText().equals(""))
			{
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC != null)
			{
				PCGen_Frame1.setTabName(aName);
				aPC.setName(aName);
				/**
                                 * added this to address bug #495558
                                 *
				 * author: Thomas Behr 20-12-01
				 */
				PCGen_Frame1.getCurrentCharacterInfo().setTxtName(aName);
			}
			else
			{
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			NameGui.this.dispose();
		}
	}

}
