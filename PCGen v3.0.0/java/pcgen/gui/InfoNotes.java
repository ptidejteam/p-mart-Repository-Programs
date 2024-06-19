/*
 * InfoNotes.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Created on March 16, 2002, 5:57 PM
 */


package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.NoteItem;
import pcgen.core.PlayerCharacter;

/**
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.1 $
 */

public class InfoNotes extends JPanel
{
	private JScrollPane notesScroll;
	private JTree notesTree;
	private JButton addButton;
	private JButton deleteButton;
	private JButton renameButton;
	private JButton revertButton;
	private JButton moveButton;
	private JScrollPane dataScroll;
	private JEditorPane dataText;
	private static boolean needsUpdate = true;
	protected static PlayerCharacter aPC = null;
	private JPanel buttonPanel;
	private DefaultTreeModel notesModel;
	private NoteTreeNode rootTreeNode;
	private ArrayList remainingList;
	private JSplitPane splitPane;
	private NoteItem currentItem = null;
	private NoteItem lastItem = null;
	private boolean textIsDirty = false;

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
			//qty should remain unused by this derived class
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


	private void hookupPopupMenu(JTree tree)
	{
		tree.addMouseListener(new NotePopupListener(tree, new NotePopupMenu()));
	}


	public InfoNotes()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Notes");
		aPC = Globals.getCurrentPC();

		initComponents();
		initActionListeners();
	}

	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	public void initComponents()
	{
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
		add(buttonPanel, BorderLayout.SOUTH);

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
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, notesScroll, dataScroll);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(100);

		add(splitPane, BorderLayout.CENTER);
	}

	private void establishTreeNodes(NoteTreeNode aNode, NoteItem note)
	{
		int index = -1;
		if (aNode == null)
		{
			rootTreeNode = new NoteTreeNode(null);
			aNode = rootTreeNode;
			remainingList = (ArrayList)aPC.getNotesList().clone(); // unplaced NoteItem
		}
		else
		{
			index = note.getId();
		}
		ArrayList aList = new ArrayList();

		for (int x = 0; x < remainingList.size(); x++)
		{
			NoteItem ni = (NoteItem)remainingList.get(x);
			if (ni.getParentId() == index)
			{
				NoteTreeNode dNode = new NoteTreeNode(ni);
				aNode.add(dNode);
				remainingList.remove(x);
				x--;
				aList.add(ni);
			}
		}
		for (int i = 0; i < aNode.getChildCount(); i++)
		{
			establishTreeNodes((NoteTreeNode)aNode.getChildAt(i), (NoteItem)aList.get(i));
		}
	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
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
				int parent = -1;
				int newNodeId = 0;

				final TreePath selPath = notesTree.getSelectionPath();
				if (selPath == null)
					return;

				Object anObject = selPath.getLastPathComponent();
				if (anObject != null && ((NoteTreeNode)anObject).getItem() != null)
					parent = ((NoteTreeNode)anObject).getItem().getId();
				Iterator allNotes = aPC.getNotesList().iterator();
				while (allNotes.hasNext())
				{
					final NoteItem currItem = (NoteItem)allNotes.next();
					if (currItem.getId() > newNodeId)
						newNodeId = currItem.getId();
				}
				++newNodeId;
				NoteItem a = new NoteItem(newNodeId, parent, "New Item", "New Value");
				NoteTreeNode aNode = new NoteTreeNode(a);
				if (anObject != null)
					((NoteTreeNode)anObject).add(aNode);
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
				if (selPath == null)
					return;
				Object anObject = selPath.getLastPathComponent();
				if (anObject == null || ((NoteTreeNode)anObject).getItem() == null)
					return;
				NoteTreeNode aNode = (NoteTreeNode)anObject;
				Enumeration allChildren = aNode.breadthFirstEnumeration();
				while (allChildren.hasMoreElements())
				{
					NoteTreeNode ancestorNode = (NoteTreeNode)allChildren.nextElement();
					if (ancestorNode != aNode)
						numChildren++;
				}
				reallyDelete = JOptionPane.showConfirmDialog(null,
					"Are you sure you wish to delete the note " + aNode.toString() + (numChildren > 0?" and its " + (numChildren) + " children":"") + "?",
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
				if (selPath == null)
					return;
				Object anObject = selPath.getLastPathComponent();
				if (anObject == null || ((NoteTreeNode)anObject).getItem() == null)
					return;
				NoteTreeNode aNode = (NoteTreeNode)anObject;
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
		hookupPopupMenu(notesTree);

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
			textIsDirty = false;
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText(""); //just clear it till there's something worth putting here
		updateCharacterInfo();
		buttonPanel.setPreferredSize(new Dimension((int)(this.getSize().getWidth()), 40));
	}

	public void updateCharacterInfo()
	{
		stopListeners();
		// First off store the existing value
		if (aPC != null && currentItem != null)
			updateNoteItem();

		aPC = Globals.getCurrentPC();
		currentItem = null;
		dataText.setText("");

		if (aPC == null)
		{
			startListeners();
			return;
		}

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


	public void startListeners()
	{
		dataText.getDocument().addDocumentListener(noteChangeListener);
	}


	public void stopListeners()
	{
		dataText.getDocument().removeDocumentListener(noteChangeListener);
	}

	class NoteTreeNode extends DefaultMutableTreeNode
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
