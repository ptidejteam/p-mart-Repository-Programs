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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.core.Globals;
import pcgen.core.Names;


/**
 * <code>NameGui</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public class NameGui extends JFrame
{
	private JTextField generatedName;

	public NameGui()
	{
		super("Random Name Generator");
		// according to the API, the following should *ALWAYS* use '/'
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("pcgen/gui/resource/PcgenIcon.gif")));
		Utility.centerFrame(this, true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		/**
		 * Set up the list of Name files
		 */
		DefaultListModel listModel = new DefaultListModel();
		String[] allNamesFiles = Names.findAllNamesFiles();
		Arrays.sort(allNamesFiles);
		for (int i = 0; i < allNamesFiles.length; i++)
		{
			listModel.addElement(allNamesFiles[i]);
		}

		final JButton generateButton = new JButton("Generate Name");
		generateButton.setMnemonic('G');
		generateButton.setActionCommand("Roll");
		generateButton.addActionListener(new ReRollListener());

		generatedName = new JTextField(15);
		generatedName.setEditable(false);

		//Create list and put it in a scroll pane
		final JList list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(-1);
		list.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					if (list.getSelectedIndex() == -1)
					{
						requestFocus();
						//No selection, disable redo button.
						generateButton.setEnabled(false);
						generatedName.setText("");
					}
					else
					{
						//Selection, update text field.
						generateButton.setEnabled(true);
						String name = list.getSelectedValue().toString();
						//Must call for the following to load the nam file for the choice.
						Names.getInstance().init(name);
						generatedName.setText(Names.getInstance().getRandomName());
					}
				}
			}
		});
		JScrollPane listScrollPane = new JScrollPane(list);

		JButton acceptButton = new JButton("Accept");
		acceptButton.setMnemonic('A');
		acceptButton.setActionCommand("Accept");
		acceptButton.addActionListener(new AcceptListener());

		JLabel info = new JLabel("Select a ruleset for name creation");

		JLabel nameLabel = new JLabel("Name:");
		JPanel namePanel = new JPanel();

		Box nameBox = new Box(BoxLayout.X_AXIS);
		nameBox.add(nameLabel);
		nameBox.add(generatedName);
		namePanel.add(nameBox);

		Box generateButtonBox = new Box(BoxLayout.X_AXIS);
		generateButtonBox.add(Box.createHorizontalGlue());
		generateButtonBox.add(generateButton);
		generateButtonBox.add(Box.createHorizontalGlue());

		Box buttonAndNameBox = new Box(BoxLayout.Y_AXIS);
//    buttonAndNameBox.add(Box.createVerticalGlue());
		buttonAndNameBox.add(namePanel);
		buttonAndNameBox.add(generateButtonBox);
//    buttonAndNameBox.add(Box.createVerticalGlue());

		JPanel namePlusButtonHolderPanel = new JPanel();
		namePlusButtonHolderPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		namePlusButtonHolderPanel.add(buttonAndNameBox);

		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		contentPane.add(info,
			new GridBagConstraints(0, 0, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		contentPane.add(listScrollPane,
			new GridBagConstraints(0, 1, 1, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 0), 0, 0));
		contentPane.add(namePlusButtonHolderPanel,
			new GridBagConstraints(1, 1, 2, 1, 1.3, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		contentPane.add(acceptButton,
			new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
	}

	class ReRollListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				generatedName.setText(Names.getInstance().getRandomName());
			}
			catch (RuntimeException e1)
			{
				//todo: put a message in a status bar or something.
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}


	class AcceptListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String name = generatedName.getText();

			//User didn't roll a name...
			if (name.equals(""))
			{
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			if (Globals.getCurrentPC() != null)
			{
				Globals.getCurrentPC().setName(name);
				Globals.getRootFrame().forceUpdate_PlayerTabs();
				Globals.getRootFrame().getCurrentCharacterInfo().setTxtName(name);
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
