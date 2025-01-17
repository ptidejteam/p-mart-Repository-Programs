/*
 * InfoDescription.java
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
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.NoteItem;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.util.Delta;
import pcgen.util.PropertyFactory;

/**
 * <code>InfoDescription</code> creates a new tabbed panel.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
class InfoDescription extends JPanel
{
	private static PlayerCharacter aPC = Globals.getCurrentPC();
	static boolean needsUpdate = true;

	private static final int BIO_NOTEID = -2;
	private static final int DESCRIPTION_NOTEID = -3;
	private static final int COMPANION_NOTEID = -4;
	private static final int OTHERASSETS_NOTEID = -5;
	private static final int MAGICITEMS_NOTEID = -6;
	private static final int PORTRAIT_NOTEID = -7;
        
        private static final String in_noPortraitChildrenMessage =
        PropertyFactory.getString("in_noPortraitChildrenMessage");
        private static final String in_noPortraitDeletionMessage =
        PropertyFactory.getString("in_noPortraitDeletionMessage");
        private static final String in_noPortraitRenamingMessage =
        PropertyFactory.getString("in_noPortraitRenamingMessage");

	private JPanel northPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel centerNorthPanel = new JPanel();
	private JPanel centerCenterPanel = new JPanel();

	protected JTextField txtName = new JTextField(aPC.getName());

	private JTextField playerNameText = new JTextField(aPC.getPlayersName());
	private JTextField skinText = new JTextField(aPC.getSkinColor());
	private JTextField hairColorText = new JTextField(aPC.getHairColor());
	private JTextField hairStyleText = new JTextField(aPC.getHairStyle());
	private JTextField eyeColorText = new JTextField(aPC.getEyeColor());
	private JTextField speechPatternText = new JTextField(aPC.getSpeechTendency());
	private JTextField phobiaText = new JTextField(aPC.getPhobias());
	private JTextField interestsText = new JTextField(aPC.getInterests());
	private JTextField catchPhraseText = new JTextField(aPC.getCatchPhrase());
	private JTextField personality1Text = new JTextField(aPC.getTrait1());
	private JTextField personality2Text = new JTextField(aPC.getTrait2());
	private JTextField residenceText = new JTextField(aPC.getResidence());
	private JTextField locationText = new JTextField(aPC.getLocation());

	private JComboBox handedComboBox = new JComboBox();
	private JComboBox genderComboBox = new JComboBox();
	private FocusAdapter ageAdapter = null;
	private FocusAdapter htAdapter = null;
	private FocusAdapter wtAdapter = null;
	private FocusAdapter playerNameAdapter = null;
	private WholeNumberField ageText = new WholeNumberField(0, 0);
	private WholeNumberField wtText = new WholeNumberField(0, 0);
	private WholeNumberField htText = new WholeNumberField(0, 0);
	private JCheckBox ageBox = new JCheckBox();
	private JCheckBox htwtBox = new JCheckBox();
	private JCheckBox skinBox = new JCheckBox();
	private JCheckBox hairColorBox = new JCheckBox();
	private JCheckBox hairStyleBox = new JCheckBox();
	private JCheckBox eyeColorBox = new JCheckBox();
	private JCheckBox speechPatternBox = new JCheckBox();
	private JCheckBox phobiaBox = new JCheckBox();
	private JCheckBox interestsBox = new JCheckBox();
	private JCheckBox catchPhraseBox = new JCheckBox();
	private JCheckBox personality1Box = new JCheckBox();
	private JCheckBox personality2Box = new JCheckBox();
	private JCheckBox residenceBox = new JCheckBox();
	private JCheckBox locationBox = new JCheckBox();
	private JLabel fregion = new JLabel(aPC.getFullRegion());
	private JLabel labelName = null;

	private JButton randName;
	private JButton randAll;
	private JButton checkAll;
	private JButton uncheckAll;

	private NameGui nameFrame = null;
	private Border etched;
	private TitledBorder titled;

	private JScrollPane notesScroll;
	private JTree notesTree;
	private JButton addButton;
	private JButton deleteButton;
	private JButton renameButton;
	private JButton revertButton;
	private JButton moveButton;
        private CardLayout dataLayout;
        private PortraitChooser portrait;
        private JPanel dataPanel;
	private JScrollPane dataScroll;
	private JEditorPane dataText;
	private JPanel buttonPanel;
	private DefaultTreeModel notesModel;
	private NoteTreeNode rootTreeNode;
	private ArrayList nodesToBeAddedList;
	private FlippingSplitPane splitPane;
	private NoteItem currentItem = null;
	private NoteItem lastItem = null;
	private boolean textIsDirty = false;

	private NoteItem bioNote = null;
	private NoteItem descriptionNote = null;
	private NoteItem companionNote = null;
	private NoteItem otherAssetsNote = null;
	private NoteItem magicItemsNote = null;
	private NoteItem portraitNote = null;

	// Combobox event handlers
	private ActionListener al1 = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			if (handedComboBox != null && handedComboBox.getSelectedItem() != null)
			{
				aPC.setDirty(true);
				aPC.setHanded(handedComboBox.getSelectedItem().toString());
			}
		}
	};

	private ActionListener al2 = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			if (genderComboBox != null && genderComboBox.getSelectedItem() != null)
			{
				aPC.setDirty(true);
				aPC.setGender(genderComboBox.getSelectedItem().toString());
			}
		}
	};

	//Notes popup menu
	private class NotePopupMenu extends JPopupMenu
	{
		private class NoteActionListener implements ActionListener
		{
			NoteTreeNode aNode;

			protected NoteActionListener()
			{
				aNode = null;
			}

			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddNoteActionListener extends NoteActionListener
		{
			AddNoteActionListener()
			{
				super();
			}

			public void actionPerformed(ActionEvent evt)
			{
				addButton.doClick();
			}
		}

		private class RenameNoteActionListener extends NoteActionListener
		{
			RenameNoteActionListener()
			{
				super();
			}

			public void actionPerformed(ActionEvent evt)
			{
				renameButton.doClick();
			}
		}

		private class RemoveNoteActionListener extends NoteActionListener
		{
			RemoveNoteActionListener()
			{
				super();
			}

			public void actionPerformed(ActionEvent evt)
			{
				deleteButton.doClick();
			}
		}

		private class MoveNoteActionListener extends NoteActionListener
		{
			MoveNoteActionListener()
			{
				super();
			}

			public void actionPerformed(ActionEvent evt)
			{
				moveButton.doClick();
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddNoteActionListener(), "Add", (char)0, accelerator, "Add", "Add16.gif", true);
		}

		private JMenuItem createRenameMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RenameNoteActionListener(), "Rename", (char)0, accelerator, "Rename", "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveNoteActionListener(), "Delete", (char)0, accelerator, "Delete", "Remove16.gif", true);
		}

		private JMenuItem createMoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new MoveNoteActionListener(), "Move", (char)0, accelerator, "Move", "Add16.gif", true);
		}

		NotePopupMenu()
		{
			NotePopupMenu.this.add(createAddMenuItem("Add", "control EQUALS"));
			NotePopupMenu.this.add(createRemoveMenuItem("Remove", "control MINUS"));
			NotePopupMenu.this.add(createRenameMenuItem("Rename", "alt M"));
			NotePopupMenu.this.add(createMoveMenuItem("Move", "alt Z"));
		}
	}

	// Notes popup menu listener
	private class NotePopupListener extends MouseAdapter
	{
		private JTree tree;
		private NotePopupMenu menu;

		NotePopupListener(JTree atree, NotePopupMenu aMenu)
		{
			tree = atree;
			menu = aMenu;
			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();
					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final JMenuItem menuItem = (JMenuItem)menu.getComponent(i);
							javax.swing.KeyStroke ks = menuItem.getAccelerator();
							if ((ks != null) && keyStroke.equals(ks))
							{
								menuItem.doClick(2);
								return;
							}
						}
					}
					dispatchEvent(e);
				}

				public void keyReleased(KeyEvent e)
				{
					dispatchEvent(e);
				}
			};
			tree.addKeyListener(myKeyListener);
		}

		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				final int selRow = tree.getRowForLocation(evt.getX(), evt.getY());
				if (selRow == -1) return;
				final TreePath selPath = tree.getPathForLocation(evt.getX(), evt.getY());
				if (selPath == null) return;
				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	/**
	 * This listener detects changes in the note text.
	 */
	private DocumentListener noteChangeListener = new DocumentListener()
	{
		public void insertUpdate(DocumentEvent e)
		{
			textIsDirty = true;
		}

		public void removeUpdate(DocumentEvent e)
		{
			textIsDirty = true;
		}

		public void changedUpdate(DocumentEvent e)
		{
			textIsDirty = true;
		}
	};

	//Set up GridBag Constraints
	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	public InfoDescription()
	{
		setName("Description");
		initComponents();
		initNonDataDrivenEventListeners();
		refreshDisplay();
	}

	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	private void initComponents()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		northPanel.setLayout(gridbag);

		buildConstraints(c, 0, 0, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelName = new JLabel(PropertyFactory.getString("in_nameLabel") + ": ");
		gridbag.setConstraints(labelName, c);
		northPanel.add(labelName);

		buildConstraints(c, 1, 0, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(txtName, c);
		northPanel.add(txtName);

		buildConstraints(c, 2, 0, 2, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		randName = new JButton(PropertyFactory.getString("in_randomButton"));
		gridbag.setConstraints(randName, c);
		northPanel.add(randName);

		buildConstraints(c, 0, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		JLabel label = new JLabel(PropertyFactory.getString("in_playerString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 1, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(playerNameText, c);
		northPanel.add(playerNameText);

		buildConstraints(c, 6, 0, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_genderString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 7, 0, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(genderComboBox, c);
		northPanel.add(genderComboBox);

		genderComboBox.addItem(PropertyFactory.getString("in_genderMale"));
		genderComboBox.addItem(PropertyFactory.getString("in_genderFemale"));
		genderComboBox.addItem(PropertyFactory.getString("in_genderNeuter"));
		genderComboBox.addItem(PropertyFactory.getString("in_comboNone"));
		genderComboBox.addItem(PropertyFactory.getString("in_comboOther"));

		buildConstraints(c, 6, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_handString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 7, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(handedComboBox, c);
		northPanel.add(handedComboBox);

		handedComboBox.addItem(PropertyFactory.getString("in_handRight"));
		handedComboBox.addItem(PropertyFactory.getString("in_handLeft"));
		handedComboBox.addItem(PropertyFactory.getString("in_handBoth"));
		handedComboBox.addItem(PropertyFactory.getString("in_comboNone"));
		handedComboBox.addItem(PropertyFactory.getString("in_comboOther"));

		buildConstraints(c, 0, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_heightString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 1, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(htText, c);
		northPanel.add(htText);

		buildConstraints(c, 2, 2, 1, 2, 5, 10);
		c.fill = GridBagConstraints.CENTER;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(htwtBox, c);
		northPanel.add(htwtBox);

		buildConstraints(c, 0, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_weightString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 1, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(wtText, c);
		northPanel.add(wtText);

		buildConstraints(c, 0, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_ageString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 1, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(ageText, c);
		northPanel.add(ageText);

		buildConstraints(c, 2, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(ageBox, c);
		northPanel.add(ageBox);

		buildConstraints(c, 6, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_skinString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 7, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(skinText, c);
		northPanel.add(skinText);

		buildConstraints(c, 8, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(skinBox, c);
		northPanel.add(skinBox);

		buildConstraints(c, 3, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_regionString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 4, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(fregion, c);
		northPanel.add(fregion);

		buildConstraints(c, 3, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_phobiasString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 4, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(phobiaText, c);
		northPanel.add(phobiaText);

		buildConstraints(c, 5, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(phobiaBox, c);
		northPanel.add(phobiaBox);

		buildConstraints(c, 3, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_personalityString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 4, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(personality1Text, c);
		northPanel.add(personality1Text);

		buildConstraints(c, 5, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(personality1Box, c);
		northPanel.add(personality1Box);

		buildConstraints(c, 6, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_eyeString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 7, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(eyeColorText, c);
		northPanel.add(eyeColorText);

		buildConstraints(c, 8, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(eyeColorBox, c);
		northPanel.add(eyeColorBox);

		buildConstraints(c, 3, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_interestString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 4, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(interestsText, c);
		northPanel.add(interestsText);

		buildConstraints(c, 5, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(interestsBox, c);
		northPanel.add(interestsBox);

		buildConstraints(c, 3, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_personalityString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 4, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(personality2Text, c);
		northPanel.add(personality2Text);

		buildConstraints(c, 5, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(personality2Box, c);
		northPanel.add(personality2Box);

		buildConstraints(c, 6, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_hairString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 7, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(hairColorText, c);
		northPanel.add(hairColorText);

		buildConstraints(c, 8, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(hairColorBox, c);
		northPanel.add(hairColorBox);

		buildConstraints(c, 0, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_homeString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

////need to get Region info
		buildConstraints(c, 1, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(residenceText, c);
		northPanel.add(residenceText);

		// buildConstraints(c, 2, 6, 1, 1, 5, 10);
		// c.fill = GridBagConstraints.NONE;
		// c.anchor = GridBagConstraints.WEST;
		// gridbag.setConstraints(residenceBox, c);
		// northPanel.add(residenceBox);

		buildConstraints(c, 6, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_speechString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 7, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(speechPatternText, c);
		northPanel.add(speechPatternText);

		buildConstraints(c, 8, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(speechPatternBox, c);
		northPanel.add(speechPatternBox);

		buildConstraints(c, 6, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_styleString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 7, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(hairStyleText, c);
		northPanel.add(hairStyleText);

		buildConstraints(c, 8, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(hairStyleBox, c);
		northPanel.add(hairStyleBox);

		buildConstraints(c, 0, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_locationString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 1, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(locationText, c);
		northPanel.add(locationText);

		buildConstraints(c, 2, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(locationBox, c);
		northPanel.add(locationBox);

		buildConstraints(c, 3, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_phraseString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 4, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(catchPhraseText, c);
		northPanel.add(catchPhraseText);

		buildConstraints(c, 5, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(catchPhraseBox, c);
		northPanel.add(catchPhraseBox);

		centerNorthPanel.setLayout(new BorderLayout());
		JPanel pane = new JPanel(new FlowLayout());

		checkAll = new JButton(PropertyFactory.getString("in_checkButton"));
		pane.add(checkAll);

		randAll = new JButton(PropertyFactory.getString("in_randomButton"));
		pane.add(randAll);

		uncheckAll = new JButton(PropertyFactory.getString("in_uncheckButton"));
		pane.add(uncheckAll);

		centerNorthPanel.add(pane, BorderLayout.NORTH);

		// Set Sizes of everything
		txtName.setMinimumSize(new Dimension(110, 21));
		txtName.setPreferredSize(new Dimension(200, 21));
		playerNameText.setMinimumSize(new Dimension(110, 21));
		playerNameText.setPreferredSize(new Dimension(200, 21));
		htText.setMinimumSize(new Dimension(110, 21));
		htText.setPreferredSize(new Dimension(200, 21));
		wtText.setMinimumSize(new Dimension(110, 21));
		wtText.setPreferredSize(new Dimension(200, 21));
		ageText.setMinimumSize(new Dimension(110, 21));
		ageText.setPreferredSize(new Dimension(200, 21));
		genderComboBox.setMinimumSize(new Dimension(110, 21));
		genderComboBox.setPreferredSize(new Dimension(200, 21));
		handedComboBox.setMinimumSize(new Dimension(110, 21));
		handedComboBox.setPreferredSize(new Dimension(200, 21));
		skinText.setMinimumSize(new Dimension(110, 21));
		skinText.setPreferredSize(new Dimension(200, 21));
		eyeColorText.setMinimumSize(new Dimension(110, 21));
		eyeColorText.setPreferredSize(new Dimension(200, 21));
		hairColorText.setMinimumSize(new Dimension(110, 21));
		hairColorText.setPreferredSize(new Dimension(200, 21));
		hairStyleText.setMinimumSize(new Dimension(110, 21));
		hairStyleText.setPreferredSize(new Dimension(200, 21));
		speechPatternText.setMinimumSize(new Dimension(110, 21));
		speechPatternText.setPreferredSize(new Dimension(200, 21));
		phobiaText.setMinimumSize(new Dimension(110, 21));
		phobiaText.setPreferredSize(new Dimension(200, 21));
		interestsText.setMinimumSize(new Dimension(110, 21));
		interestsText.setPreferredSize(new Dimension(200, 21));
		catchPhraseText.setMinimumSize(new Dimension(110, 21));
		catchPhraseText.setPreferredSize(new Dimension(200, 21));
		personality1Text.setMinimumSize(new Dimension(110, 21));
		personality1Text.setPreferredSize(new Dimension(200, 21));
		personality2Text.setMinimumSize(new Dimension(110, 21));
		personality2Text.setPreferredSize(new Dimension(200, 21));
		residenceText.setMinimumSize(new Dimension(110, 21));
		residenceText.setPreferredSize(new Dimension(200, 21));
		locationText.setMinimumSize(new Dimension(110, 21));
		locationText.setPreferredSize(new Dimension(200, 21));
		randAll.setMinimumSize(new Dimension(90, 25));
		randAll.setPreferredSize(new Dimension(90, 25));
		checkAll.setMinimumSize(new Dimension(90, 25));
		checkAll.setPreferredSize(new Dimension(90, 25));
		uncheckAll.setMinimumSize(new Dimension(110, 25));
		uncheckAll.setPreferredSize(new Dimension(110, 25));
		randName.setMinimumSize(new Dimension(90, 21));
		randName.setPreferredSize(new Dimension(90, 21));

		// Notes code
		setLayout(new BorderLayout());
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		addButton = new JButton("Add");
		deleteButton = new JButton("Delete");
		renameButton = new JButton("Rename");
		revertButton = new JButton("Revert");
		moveButton = new JButton("Move");

		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(renameButton);
		buttonPanel.add(moveButton);
		buttonPanel.add(revertButton);

		establishTreeNodes(null, null);
		notesModel = new DefaultTreeModel(rootTreeNode);
		notesTree = new JTree(notesModel);
		notesScroll = new JScrollPane(notesTree);
		notesScroll.setViewportView(notesTree);

		dataText = new JTextPane();
		dataText.setEditable(true);
		dataText.setText("");
		dataScroll = new JScrollPane(dataText);
		dataScroll.setViewportView(dataText);

                /*
                 * have a JEditorPane and a PortraitChooser
                 * at the "same" location
                 *
                 * author: Thomas Behr 10-09-02
                 */
                portrait = new PortraitChooser(null);

                dataLayout = new CardLayout();
                dataPanel = new JPanel();
                dataPanel.setLayout(dataLayout);
                dataPanel.add(dataScroll, "notes");
                dataPanel.add(portrait, "portraits");
                
		splitPane = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, notesScroll, dataPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(100);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_notes"));
		title1.setTitleJustification(TitledBorder.LEFT);
		centerCenterPanel.setBorder(title1);

		centerCenterPanel.setLayout(new BorderLayout());
		centerCenterPanel.add(splitPane, BorderLayout.CENTER);
		centerCenterPanel.add(buttonPanel, BorderLayout.SOUTH);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(centerCenterPanel, BorderLayout.CENTER);
		centerPanel.add(centerNorthPanel, BorderLayout.NORTH);
		this.setLayout(new BorderLayout());
		this.add(northPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);

		Utility.setDescription(ageBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(htwtBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(skinBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(hairColorBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(hairStyleBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(eyeColorBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(speechPatternBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(phobiaBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(interestsBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(catchPhraseBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(personality1Box, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(personality2Box, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(residenceBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(locationBox, PropertyFactory.getString("in_randCheckTipString"));
		Utility.setDescription(randName, PropertyFactory.getString("in_randNameTipString"));
		Utility.setDescription(randAll, PropertyFactory.getString("in_randTraitTipString"));
		Utility.setDescription(checkAll, PropertyFactory.getString("in_checkTipString"));
		Utility.setDescription(uncheckAll, PropertyFactory.getString("in_uncheckTipString"));

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});

	}

	/**
	 * Recursively build up the tree of notes.
	 * The tree is built off the root node rootTreeNode
	 */
	private void establishTreeNodes(NoteTreeNode aNode, NoteItem note)
	{
		int index = -1;
		if (aNode == null)
		{
			rootTreeNode = new NoteTreeNode(null);
			aNode = rootTreeNode;
			nodesToBeAddedList = (ArrayList)aPC.getNotesList().clone();

                        int order = 0;
                        portraitNote = new NoteItem(PORTRAIT_NOTEID, -1, "Portrait", "");
			nodesToBeAddedList.add(order++, portraitNote);

			bioNote = new NoteItem(BIO_NOTEID, -1, "Bio", aPC.getBio());
			nodesToBeAddedList.add(order++, bioNote);
			descriptionNote = new NoteItem(DESCRIPTION_NOTEID, -1, "Description", aPC.getDescription());
			nodesToBeAddedList.add(order++, descriptionNote);
			companionNote = new NoteItem(COMPANION_NOTEID, -1, "Companions", (String)aPC.getMiscList().get(1));
			nodesToBeAddedList.add(order++, companionNote);
			otherAssetsNote = new NoteItem(OTHERASSETS_NOTEID, -1, "Other Assets", (String)aPC.getMiscList().get(0));
			nodesToBeAddedList.add(order++, otherAssetsNote);
			magicItemsNote = new NoteItem(MAGICITEMS_NOTEID, -1, "Magic Items", (String)aPC.getMiscList().get(2));
			nodesToBeAddedList.add(order++, magicItemsNote);

		}
		else
		{
			index = note.getId();
		}
		ArrayList aList = new ArrayList();

		for (int x = 0; x < nodesToBeAddedList.size(); x++)
		{
			NoteItem ni = (NoteItem)nodesToBeAddedList.get(x);
			if (ni.getParentId() == index)
			{
				NoteTreeNode dNode = new NoteTreeNode(ni);
				aNode.add(dNode);
				nodesToBeAddedList.remove(x);
				x--;
				aList.add(ni);
			}
		}
		for (int i = 0; i < aNode.getChildCount(); i++)
		{
			establishTreeNodes((NoteTreeNode)aNode.getChildAt(i), (NoteItem)aList.get(i));
		}
	}

	/**
	 * This method creates and registers all of the action listeners for the
	 * description and notes buttons as well as the focus listener for the
	 * notes text control and the mouse click listener for the notes tree.
	 * NB: Handlers for the document change and combo actions are handled
	 * separately as they need to be shut off when we are switching
	 * characters or otherwise updating the text data programatically.
	 */
	private void initNonDataDrivenEventListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});

		txtName.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				txtName_Changed(e);
				labelName.requestFocus();
			}
		});

		txtName.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtName_Changed(evt);
			}
		});

		randName.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (nameFrame == null)
				{
					nameFrame = new NameGui();
				}
				nameFrame.setVisible(true);
			}
		});

		java.awt.event.FocusListener fl = new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				updateTextFields();
			}
		};
		skinText.addFocusListener(fl);
		hairColorText.addFocusListener(fl);
		hairStyleText.addFocusListener(fl);
		eyeColorText.addFocusListener(fl);
		speechPatternText.addFocusListener(fl);
		phobiaText.addFocusListener(fl);
		interestsText.addFocusListener(fl);
		catchPhraseText.addFocusListener(fl);
		personality1Text.addFocusListener(fl);
		personality2Text.addFocusListener(fl);
		residenceText.addFocusListener(fl);
		locationText.addFocusListener(fl);

		checkAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				checkAll_click();
			}
		});

		randAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				randAll_click();
				updateTextFields();
				aPC.setDirty(true);
			}
		});

		uncheckAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				uncheckAll_click();
			}
		});

		ageText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				aPC.setDirty(true);
				aPC.setAge(Delta.parseInt("0" + ageText.getText()));
			}
		});

		htText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				aPC.setDirty(true);
				aPC.setHeight(Delta.parseInt("0" + htText.getText()));
			}
		});

		wtText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				aPC.setDirty(true);
				aPC.setWeight(Delta.parseInt("0" + wtText.getText()));
			}
		});

		playerNameText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				aPC.setPlayersName(playerNameText.getText());
			}
		});

		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = notesTree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = notesTree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && selPath != null)
					{
						selectNotesNode(selRow);
					}
				}
				lastItem = null;
			}
		};
		notesTree.addMouseListener(ml);

		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int parentId = -1;
				int newNodeId = 0;

				final TreePath selPath = notesTree.getSelectionPath();
				if (selPath == null) {
					return;
                                }
                                
				NoteTreeNode parentTreeNode = (NoteTreeNode)selPath.getLastPathComponent();
				if (parentTreeNode != null && parentTreeNode.getItem() != null) {
					parentId = parentTreeNode.getItem().getId();
                                }
                                
                                /*
                                 * The portrait note may not have children
                                 *
                                 * author: Thomas Behr 10-09-02
                                 */
                                if (parentTreeNode.getItem().getId() == PORTRAIT_NOTEID) {
                                        JOptionPane.showMessageDialog(null, in_noPortraitChildrenMessage);
                                        return;
                                }

				Iterator allNotes = aPC.getNotesList().iterator();
				while (allNotes.hasNext())
				{
					final NoteItem currItem = (NoteItem)allNotes.next();
					if (currItem.getId() > newNodeId)
						newNodeId = currItem.getId();
				}
				++newNodeId;
				NoteItem a = new NoteItem(newNodeId, parentId, "New Item", "New Value");
				NoteTreeNode aNode = new NoteTreeNode(a);
				if (parentTreeNode != null) {
					parentTreeNode.add(aNode);
                                }

				aPC.addNotesItem(a);
				aPC.setDirty(true);
				notesTree.expandPath(selPath);
				notesTree.updateUI();
			}
		});
		Utility.setDescription(addButton, "Add an item as a child of the currently selected item.");
		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int numChildren = 0;
				int reallyDelete = 0;

				final TreePath selPath = notesTree.getSelectionPath();
				if (selPath == null) {
					return;
                                }
                                
				Object anObject = selPath.getLastPathComponent();
				if (anObject == null || ((NoteTreeNode)anObject).getItem() == null) {
					return;
                                }

				NoteTreeNode aNode = (NoteTreeNode)anObject;

                                /*
                                 * The portrait note may not be removed
                                 *
                                 * author: Thomas Behr 10-09-02
                                 */
                                if (aNode.getItem().getId() == PORTRAIT_NOTEID) {
                                        JOptionPane.showMessageDialog(null, in_noPortraitDeletionMessage);
                                        return;
                                }
                                
				Enumeration allChildren = aNode.breadthFirstEnumeration();
				while (allChildren.hasMoreElements())
				{
					NoteTreeNode ancestorNode = (NoteTreeNode)allChildren.nextElement();
					if (ancestorNode != aNode) {
						numChildren++;
                                        }
				}

				reallyDelete = JOptionPane.showConfirmDialog(null,
                                                                             "Are you sure you wish to delete the note " +
                                                                             aNode.toString() +
                                                                             (numChildren > 0
                                                                              ? " and its " + (numChildren) + " children"
                                                                              : " ") + "?",
                                                                             "Delete Note",
                                                                             JOptionPane.OK_CANCEL_OPTION);

				if (reallyDelete == JOptionPane.OK_OPTION)
				{
					NoteTreeNode parent = (NoteTreeNode)aNode.getParent();
					if (parent != null)
					{
						allChildren = aNode.breadthFirstEnumeration();
						while (allChildren.hasMoreElements())
						{
							NoteTreeNode ancestorNode = (NoteTreeNode)allChildren.nextElement();
							aPC.getNotesList().remove(ancestorNode.getItem());
						}
						parent.remove(aNode);
						aPC.setDirty(true);
					}
					notesTree.updateUI();
				}
			}
		});
		Utility.setDescription(deleteButton, "Delete the selected item.");
		renameButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				final TreePath selPath = notesTree.getSelectionPath();
				if (selPath == null) {
					return;
                                }
                                
				Object anObject = selPath.getLastPathComponent();
				if (anObject == null || ((NoteTreeNode)anObject).getItem() == null){
					return;
                                }
                                
				NoteTreeNode aNode = (NoteTreeNode)anObject;

                                /*
                                 * The portrait note may not be renamed
                                 *
                                 * author: Thomas Behr 10-09-02
                                 */
                                if (aNode.getItem().getId() == PORTRAIT_NOTEID) {
                                        JOptionPane.showMessageDialog(null, in_noPortraitRenamingMessage);
                                        return;
                                }

				String selectedValue = JOptionPane.showInputDialog(null, "Enter New Name", Constants.s_APPNAME, JOptionPane.QUESTION_MESSAGE);
				if (selectedValue != null && selectedValue.trim().length() > 0)
				{
					aNode.getItem().setName(selectedValue.trim());
					aPC.setDirty(true);
					notesTree.updateUI();
				}
			}
		});
		Utility.setDescription(renameButton, "Rename the currently selected item");
		revertButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		});
		Utility.setDescription(revertButton, "Click on this to lose any note changes since last save.");
		revertButton.setEnabled(false); // not coded yet
		moveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				lastItem = currentItem;
			}
		});
		Utility.setDescription(moveButton, "Click on an item, then on this button, then on another item to switch them");

		dataText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				updateNoteItem();
			}
		});

		notesTree.addMouseListener(new NotePopupListener(notesTree, new NotePopupMenu()));

	}

	private void updateNoteItem()
	{
		if (currentItem != null && textIsDirty)
		{
			int x = aPC.getNotesList().indexOf(currentItem);
			currentItem.setValue(dataText.getText());
			if (x > -1)
			{
				((NoteItem)aPC.getNotesList().get(x)).setValue(dataText.getText());
				aPC.setDirty(true);
			}
			else if (currentItem == bioNote)
			{
				aPC.setBio(dataText.getText());
				aPC.setDirty(true);
			}
			else if (currentItem == descriptionNote)
			{
				aPC.setDescription(dataText.getText());
				aPC.setDirty(true);
			}
			else if (currentItem == companionNote)
			{
				aPC.getMiscList().set(1, dataText.getText());
				aPC.setDirty(true);
			}
			else if (currentItem == otherAssetsNote)
			{
				aPC.getMiscList().set(0, dataText.getText());
				aPC.setDirty(true);
			}
			else if (currentItem == magicItemsNote)
			{
				aPC.getMiscList().set(2, dataText.getText());
				aPC.setDirty(true);
			}
			textIsDirty = false;
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText("");

		updateCharacterInfo();
		//buttonPanel.setPreferredSize(new Dimension((int)(this.getSize().getWidth()), 40)); -- from Notes - don't know if its needed though.

	}

	public void updateCharacterInfo()
	{
		stopListeners();

		// First off store the existing value
		if (aPC != null && currentItem != null)
                {
			updateNoteItem();
                }
                
		aPC = Globals.getCurrentPC();
		currentItem = null;
		dataText.setText("");

		if (aPC == null)
		{
			startListeners();
			return;
		}

		refreshDisplay();

		establishTreeNodes(null, null);
		notesModel.setRoot(rootTreeNode);
		if (aPC.getNotesList().size() == 0)
		{
			NoteItem a = new NoteItem(0, -1, "New Item", "New Value");
			NoteTreeNode aNode = new NoteTreeNode(a);
			rootTreeNode.add(aNode);
			aPC.addNotesItem(a);
		}
		notesTree.updateUI();
		needsUpdate = false;
		startListeners();

		selectNotesNode(1);
	}

	private final void updateTextFields()
	{
		aPC.setSkinColor(skinText.getText());
		aPC.setHairColor(hairColorText.getText());
		aPC.setHairStyle(hairStyleText.getText());
		aPC.setEyeColor(eyeColorText.getText());
		aPC.setSpeechTendency(speechPatternText.getText());
		aPC.setPhobias(phobiaText.getText());
		aPC.setInterests(interestsText.getText());
		aPC.setCatchPhrase(catchPhraseText.getText());
		aPC.setTrait1(personality1Text.getText());
		aPC.setTrait2(personality2Text.getText());
		aPC.setResidence(residenceText.getText());
		aPC.setLocation(locationText.getText());
		aPC.setAge(Delta.parseInt("0" + ageText.getText()));
		aPC.setHeight(Delta.parseInt("0" + htText.getText()));
		aPC.setWeight(Delta.parseInt("0" + wtText.getText()));
	}

	/**
	 *  This method takes the name entered in the txtName field and makes it the
	 *  name of the active tab.
	 *
	 * @param  e  The ActionEvent
	 */
	void txtName_Changed(java.awt.AWTEvent e)
	{
		if (aPC != null)
		{
			aPC.setName(txtName.getText());
			Globals.getRootFrame().forceUpdate_PlayerTabs();
		}
	}

	/**
	 * This function is called when the "Check All" button is clicked.
	 * It sets all of the random checkboxes.
	 */
	private void checkAll_click()
	{
		ageBox.setSelected(true);
		htwtBox.setSelected(true);
		skinBox.setSelected(true);
		hairColorBox.setSelected(true);
		hairStyleBox.setSelected(true);
		eyeColorBox.setSelected(true);
		speechPatternBox.setSelected(true);
		phobiaBox.setSelected(true);
		interestsBox.setSelected(true);
		catchPhraseBox.setSelected(true);
		personality1Box.setSelected(true);
		personality2Box.setSelected(true);
		residenceBox.setSelected(true);
		locationBox.setSelected(true);
	}

	/**
	 * This function is called when the "Uncheck All" button is clicked.
	 * It clears all of the random checkboxes.
	 */
	private void uncheckAll_click()
	{
		ageBox.setSelected(false);
		htwtBox.setSelected(false);
		skinBox.setSelected(false);
		hairColorBox.setSelected(false);
		hairStyleBox.setSelected(false);
		eyeColorBox.setSelected(false);
		speechPatternBox.setSelected(false);
		phobiaBox.setSelected(false);
		interestsBox.setSelected(false);
		catchPhraseBox.setSelected(false);
		personality1Box.setSelected(false);
		personality2Box.setSelected(false);
		residenceBox.setSelected(false);
		locationBox.setSelected(false);
	}

	/**
	 * Build an ArrayList containing valid colors for the specified type
	 * Types are : E for eye, H for hair or S for skin
	 *
	 * @param colorList The list of all colors
	 * @param type The color type to be selected
	 * @return An ArrayList of only those colors valid for the supplied type.
	 */
	private ArrayList getColors(ArrayList colorList, char type)
	{
		ArrayList availColors = new ArrayList();
		for (Iterator e1 = colorList.iterator(); e1.hasNext();)
		{
			final String aString = (String)e1.next();
			if (aString.substring(aString.lastIndexOf('\t')).lastIndexOf(type) >= 0)
			{
				availColors.add(aString.substring(0, aString.indexOf('\t')));
			}
		}
		return availColors;
	}

	/**
	 * This function is called when the "Random" button is clicked and
	 * randomly generates traits/colors for those fields which have the
	 * random checkbox checked. Not all fields can be randomly generated
	 * though.
	 */
	private void randAll_click()
	{
		int roll = 0;
		final ArrayList globalColorList = pcgen.core.Globals.getColorList();
		final ArrayList globalTraitList = pcgen.core.Globals.getTraitList();
		final ArrayList globalPhobiaList = pcgen.core.Globals.getPhobiaList();
		final ArrayList globalLocationList = pcgen.core.Globals.getLocationList();
		final ArrayList globalInterestsList = pcgen.core.Globals.getInterestsList();
		final ArrayList globalPhraseList = pcgen.core.Globals.getPhraseList();
		final ArrayList globalHairStyleList = pcgen.core.Globals.getHairStyleList();
		final ArrayList globalSpeechList = pcgen.core.Globals.getSpeechList();

		final int traitNum = globalTraitList.size();

		if (eyeColorBox.isSelected())
		{
			final ArrayList eyeColors = getColors(globalColorList, 'E');
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % eyeColors.size();
			eyeColorText.setText((String)eyeColors.get(roll));
		}

		if (hairColorBox.isSelected())
		{
			final ArrayList hairColors = getColors(globalColorList, 'H');
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % hairColors.size();
			hairColorText.setText((String)hairColors.get(roll));
		}

		if (skinBox.isSelected())
		{
			final ArrayList skinColors = getColors(globalColorList, 'S');
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % skinColors.size();
			skinText.setText((String)skinColors.get(roll));
		}

		if (personality1Box.isSelected())
		{
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % traitNum;
			personality1Text.setText((String)globalTraitList.get(roll));
		}

		if (personality2Box.isSelected())
		{
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % traitNum;
			personality2Text.setText((String)globalTraitList.get(roll));
		}

		if (phobiaBox.isSelected())
		{
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % globalPhobiaList.size();
			phobiaText.setText((String)globalPhobiaList.get(roll));
		}

		if (locationBox.isSelected())
		{
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % globalLocationList.size();
			locationText.setText((String)globalLocationList.get(roll));
		}

		if (interestsBox.isSelected())
		{
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % globalInterestsList.size();
			interestsText.setText((String)globalInterestsList.get(roll));
		}

		if (catchPhraseBox.isSelected())
		{
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % globalPhraseList.size();
			catchPhraseText.setText((String)globalPhraseList.get(roll));
		}

		if (hairStyleBox.isSelected())
		{
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % globalHairStyleList.size();
			hairStyleText.setText((String)globalHairStyleList.get(roll));
		}

		if (speechPatternBox.isSelected())
		{
			roll = Globals.getRandomInt();
			if (roll < 0)
			{
				roll = -roll;
			}
			roll = roll % globalSpeechList.size();
			speechPatternText.setText((String)globalSpeechList.get(roll));
		}

		if (htwtBox.isSelected())
		{
			if (aPC.getRace() != null)
			{
				aPC.getRace().rollHeightWeight();
				htText.setText(String.valueOf(aPC.getHeight()));
				wtText.setText(String.valueOf(aPC.getWeight()));
			}
		}

		if (ageBox.isSelected())
		{
			if (aPC.getRace() != null)
			{
				if (aPC.getClassList().size() == 0)
				{
					// Without a class, everone is young
					aPC.getRace().rollAgeForAgeSet(0);

				}
				else
				{
					PCClass aClass = (PCClass)aPC.getClassList().get(0);
					aPC.getRace().rollAgeForAgeSet(aClass.getAgeSet());
				}

				ageText.setText(String.valueOf(aPC.getAge()));
			}
		}
	}

	/**
	 * Start the listeners that track changing data. These have to
	 * be stopped when updating data programatically to avoid
	 * spurious setting of dirty flags etc.
	 */
	public void startListeners()
	{
		handedComboBox.addActionListener(al1);
		genderComboBox.addActionListener(al2);
		dataText.getDocument().addDocumentListener(noteChangeListener);
	}

	/**
	 * Stop the listeners that track changing data. These have to
	 * be stopped when updating data programatically to avoid
	 * spurious setting of dirty flags etc.
	 */
	public void stopListeners()
	{
		handedComboBox.removeActionListener(al1);
		genderComboBox.removeActionListener(al2);
		dataText.getDocument().removeDocumentListener(noteChangeListener);
	}

	/**
	 * Refresh the display with the new character info. It is assumed
	 * that the caller has turned off any listeners that may react to
	 * this change in data.
	 */
	public void refreshDisplay()
	{
		final Race pcRace = aPC.getRace();
		if (!pcRace.equals(Globals.s_EMPTYRACE))
		{
			handedComboBox.setSelectedItem(aPC.getHanded());
			genderComboBox.setSelectedItem(aPC.getGender());
			ageText.setText(String.valueOf(aPC.getAge()));
			wtText.setText(String.valueOf(aPC.getWeight()));
			htText.setText(String.valueOf(aPC.getHeight()));
		}
		playerNameText.setText(aPC.getPlayersName());
		txtName.setText(aPC.getName());
		skinText.setText(aPC.getSkinColor());
		fregion.setText(aPC.getFullRegion());
		hairColorText.setText(aPC.getHairColor());
		hairStyleText.setText(aPC.getHairStyle());
		eyeColorText.setText(aPC.getEyeColor());
		speechPatternText.setText(aPC.getSpeechTendency());
		phobiaText.setText(aPC.getPhobias());
		interestsText.setText(aPC.getInterests());
		catchPhraseText.setText(aPC.getCatchPhrase());
		personality1Text.setText(aPC.getTrait1());
		personality2Text.setText(aPC.getTrait2());
		residenceText.setText(aPC.getResidence());
		locationText.setText(aPC.getLocation());

                portrait.refresh();
	}

	/**
	 * Select the notes entry at the specified row in the nodes tree.
	 * Will update the stored value of the currently displayed note
	 * before moving to the specified note. In addition this method
	 * will swap the currently selected node and the specified node,
	 * if the move action has ben requested.
	 *
	 * @param rowNum The row number in the notes tree of the note to be displayed.
	 */
	private void selectNotesNode(int rowNum)
	{
		stopListeners();

		notesTree.requestFocus();
		notesTree.setSelectionRow(rowNum);
		final TreePath path = notesTree.getSelectionPath();
		Object anObj = path.getLastPathComponent();
		if ((anObj != null) && (anObj instanceof NoteTreeNode))
		{
			if (currentItem != null)
				updateNoteItem();

			final NoteItem selectedItem = ((NoteTreeNode)anObj).getItem();
			currentItem = selectedItem;

                        /*
                         * switch cards to display portrait chooser when appropriate
                         *
                         * author: Thomas Behr 10-09-02
                         */
                        if (currentItem.getId() == PORTRAIT_NOTEID) {
                                dataLayout.last(dataPanel);
                        }
                        else {
                                dataLayout.first(dataPanel);
                        }

			if (selectedItem != null)
			{
                                dataText.setText(currentItem.getValue());

				if (lastItem != null) // exchange places
				{
					int oldParent = currentItem.getParentId();
					currentItem.setParentId(lastItem.getParentId());
					lastItem.setParentId(oldParent);
					establishTreeNodes(null, null);
					notesModel.setRoot(rootTreeNode);
					notesTree.updateUI();
				}
				dataText.setEnabled(true);
				dataText.setEditable(true);
			}
			else
			{
				dataText.setText("Select the note you wish to edit.");
				dataText.setEnabled(false);
				dataText.setEditable(false);
			}
			dataText.setCaretPosition(0);
		}
		startListeners();
	}

	/**
	 * A tree node dedicated to storing notes.
	 */
	private class NoteTreeNode extends DefaultMutableTreeNode
	{
		NoteItem item;

		public NoteTreeNode(NoteItem x)
		{
			item = x;
		}

		public String toString()
		{
			if (item != null)
				return item.toString();
			return aPC.getDisplayName();
		}

		public NoteItem getItem()
		{
			return item;
		}
	}
}
