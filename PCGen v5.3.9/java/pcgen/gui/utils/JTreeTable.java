/**
 * @(#)JTreeTable.java	1.2 98/10/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:16:09 $
 *
 **/
package pcgen.gui.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import pcgen.core.PObject;
import pcgen.core.character.SpellInfo;

/**
 * This example shows how to create a simple JTreeTable component,
 * by using a JTree as a renderer (and editor) for the cells in a
 * particular column in the JTable.
 *
 * @version 1.2 10/27/98
 *
 * @author Philip Milne
 * @author Scott Violet
 **/
public final class JTreeTable extends JTableEx implements KeyListener
{
	static final long serialVersionUID = -3571248405124682593L;
	/** A subclass of JTree. */
	private TreeTableCellRenderer tree;
	// 3 sec delay before reset of search word
	private TimedKeyBuffer keyBuffer = new TimedKeyBuffer(3000);

	public JTreeTable(TreeTableModel treeTableModel)
	{
		super();

		/*
		 JTreeTable's event handling assumes bad things about
		 mouse pressed/released that are not true on MacOS X.
		 For example, one gets NPEs thrown when the mouse is
		 hit because the event manager is waiting for released
		 and one never gets the release.

		 It turns out that the MetalLAF handles this happily and
		 thus we can use that to get appropriate line styles,
		 without knackering Mac support.

		 Fix done by LeeAnn Rucker, formerly at Apple for Javasoft.
		 Added to pcgen by Scott Ellsworth
		 */
		UIManager.put("TreeTableUI", "javax.swing.plaf.metal.MetalTreeUI");
		UIManager.put("Tree.leftChildIndent", new Integer(3));
		UIManager.put("Tree.rightChildIndent", new Integer(8));

		// Create the tree. It will be used as a renderer and editor.

		tree = new TreeTableCellRenderer(treeTableModel);
		addKeyListener(this);


		// Install a tableModel representing the visible rows in tree.
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

		// Force the JTable and JTree to share row selection models.
		ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();
		tree.setSelectionModel(selectionWrapper);
		setSelectionModel(selectionWrapper.getListSelectionModel());

		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, tree);
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

		// No grid.
		setShowGrid(false);

		// No intercell spacing
		setIntercellSpacing(new Dimension(0, 0));

		// And update the height of the trees row to match the table
		if (tree.getRowHeight() < 1)
		{
			// Metal looks better like this.
			setRowHeight(18);
		}
		else
		{
			// If the UI has specified a rowHeight,
			// we'd better all be using the same one!
			setRowHeight(tree.getRowHeight());
		}
	}

	/**
	 * Overridden to message super and forward the method to the tree.
	 * Since the tree is not actually in the component hieachy it will
	 * never receive this unless we forward it in this manner.
	 **/
	public void updateUI()
	{
		super.updateUI();
		if (tree != null)
		{
			tree.updateUI();
		}
		// Use the tree's default foreground and background
		// colors in the table
		LookAndFeel.installColorsAndFont(this, "Tree.background", "Tree.foreground", "Tree.font");
	}

	/**
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to
	 * paint the renderers and editors and overriding setBounds() below
	 * is not the right thing to do for an editor. Returning -1 for the
	 * editing row in this case, ensures the editor is never painted.
	 **/
	public int getEditingRow()
	{
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;
	}

	/**
	 * Overridden to pass the new rowHeight to the tree.
	 **/
	public void setRowHeight(int rowHeight)
	{
		super.setRowHeight(rowHeight);
		if (tree != null && tree.getRowHeight() != rowHeight)
		{
			tree.setRowHeight(getRowHeight());
		}
	}

	/**
	 * Returns the tree that is being shared between the model.
	 **/
	public JTree getTree()
	{
		return tree;
	}


	/**
	 * returns a (sorted) List of expanded Tree paths
	 **/
	public List getExpandedPaths()
	{
		int count = 0;
		if (tree == null)
		{
			return null;
		}
		for (int i = 0; i < tree.getRowCount(); i++)
		{
			if (tree.isExpanded(i))
			{
				count++;
			}
		}
		String[] anArray = new String[count];
		count = 0;
		for (int i = 0; i < tree.getRowCount(); i++)
		{
			if (tree.isExpanded(i))
			{
				anArray[count++] = tree.getPathForRow(i).toString();
			}
		}

		List list = Arrays.asList(anArray);
		Collections.sort(list, Collections.reverseOrder());
		return list;
	}

	/**
	 * Expand a List of paths
	 **/
	public void expandPathList(List aList)
	{
		if (aList == null)
		{
			return;
		}
		for (Iterator ap = aList.iterator(); ap.hasNext();)
		{
			String path = (String) ap.next();
			for (int iRow = 0; iRow < getRowCount(); iRow++)
			{
				TreePath iPath = tree.getPathForRow(iRow);
				if ((iPath != null) && iPath.toString().equals(path))
				{
					tree.makeVisible(iPath);
					tree.expandPath(iPath);
				}
			}
		}
	}

	/**
	 * This function starts a recursive search of all PObjectNodes
	 * of this JTreeTable, expanding all occurances of PObjects
	 * with a given name
	 **/
	public void expandByPObjectName(String name)
	{
		expandByPObjectName((PObjectNode) this.getTree().getModel().getRoot(), name);
	}

	/**
	 * This function recursively searches all PObjectNodes
	 * of this JTreeTable, expanding all occurances of PObjects
	 * with a given name
	 * The initial call should always come from expandByPObjectName(String)
	 */
	private void expandByPObjectName(PObjectNode root, String name)
	{
		List p1 = root.getChildren();
		if (p1 == null)
		{
			return;
		}

		for (int counter = 0; counter < p1.size(); counter++)
		{
			PObjectNode node = (PObjectNode) p1.get(counter);
			//recurse for all this node's subnodes...
			if (!node.isLeaf())
			{
				expandByPObjectName(node, name);
			}
			//...but look at all the terminal nodes (actual PObjects)
			else
			{
				final Object theObj = node.getItem();
				if (theObj instanceof PObject)
				{
					if (((PObject) theObj).getName().equals(name))
					{
						//expand that node
						List path = new ArrayList();
						PObjectNode pon = node;
						while (pon.getParent() != null)
						{
							//pop this entry onto the "front" of the list since it's a parent
							path.add(0, pon.getParent());
							pon = pon.getParent();
						}
						this.getTree().expandPath(new TreePath(path.toArray()));
						//would like to .scrollPathToVisible, but it doesn't seem to work
					}
				}
				else if (theObj instanceof SpellInfo)
				{
					if (theObj.toString().equals(name))
					{
						//expand that node
						List path = new ArrayList();
						PObjectNode pon = node;
						while (pon.getParent() != null)
						{
							//pop this entry onto the "front" of the list since it's a parent
							path.add(0, pon.getParent());
							pon = pon.getParent();
						}
						this.getTree().expandPath(new TreePath(path.toArray()));
						//would like to .scrollPathToVisible, but it doesn't seem to work
					}
				}
			}
		}
	}


	//
	// KeyListener implementation to support quick select via keyboard
	//


	/**
	 * @see java.awt.event.KeyListener#keyReleased(KeyEvent)
	 **/
	public void keyReleased(KeyEvent ke)
	{
	}

	/**
	 * Proceses non-unicode keys such as action keys. If the user hits
	 * Escape, then the key buffer is cleared.
	 *
	 * @see java.awt.event.KeyListener#keyPressed(KeyEvent)
	 **/
	public void keyPressed(KeyEvent ke)
	{
		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			keyBuffer.clearBuffer();
		}
	}

	/**
	 * Processes unicode key entry. The keys are added to a timed buffer to
	 * build up the search string. A search is then made for a node in the
	 * current level which has matching name. Only alpha-numeric characters
	 * along with a few symbols will be accepted. No search will take place
	 * if focus is on an editable cell.
	 *
	 * @see java.awt.event.KeyListener#keyTyped(KeyEvent)
	 **/
	public void keyTyped(KeyEvent ke)
	{
		// Need to filter out any escaped characters as they
		// are most likely command shortcuts
		if (ke.getModifiers() != 0 && ke.getModifiers() != KeyEvent.SHIFT_MASK)
		{
			return;
		}
		// Filter out non-standard characters
		char keyChar = ke.getKeyChar();
		if (!Character.isLetterOrDigit(keyChar)
			&& !Character.isWhitespace(keyChar)
			&& !(keyChar == '-'
			|| keyChar == '+'
			|| keyChar == '('
			|| keyChar == ')'
			|| keyChar == '.'
			|| keyChar == ','
			|| keyChar == ':'
			|| keyChar == ';'))
		{
			return;
		}
		// If the current column is editable, then a keypress will be
		// to start editing, and not for us
		if (getSelectedRow() >= 0 && getSelectedColumn() >= 0
			&& isCellEditable(getSelectedRow(), getSelectedColumn())
			&& getColumnClass(getSelectedColumn()) != TreeTableModel.class)
		{
			return;
		}

		// Build the buffer
		keyBuffer.addChar(ke.getKeyChar());
		String buffer = keyBuffer.getString().toLowerCase();

		// Grab the parent of the current node
		TreePath treePath = tree.getSelectionPath();
		PObjectNode current = ((PObjectNode) treePath.getLastPathComponent());
		PObjectNode parent = current.getParent();

		// Check for a expand/contract command
		if (buffer.length() == 1)
		{
			if (!current.isLeaf() && (keyChar == '+' || keyChar == '-' || keyChar == ' '))
			{
				keyBuffer.clearBuffer();
				switch (keyChar)
				{
					case '+':
						//expand the node
						tree.expandPath(treePath);
						tree.setSelectionPath(treePath);
						return;

					case '-':
						// Collapse the node
						tree.collapsePath(treePath);
						tree.setSelectionPath(treePath);
						return;

					case ' ':
						// toggle the node's state
						if (tree.isCollapsed(treePath))
						{
							tree.expandPath(treePath);
						}
						else
						{
							tree.collapsePath(treePath);
						}
						tree.setSelectionPath(treePath);
						return;

					default :
						break;
				}

			}
		}

		// Only search if the current node is not a match
		if (current.getNodeName() == null || !current.getNodeName().toLowerCase().startsWith(buffer))
		{
			// Find a node at the current level that matches the buffer
			searchSingleLevel(parent, buffer, true);
		}
	}

	/**
	 * A TreeCellRenderer that displays a JTree.
	 **/
	final class TreeTableCellRenderer extends JTree implements TableCellRenderer
	{
		// Last table/tree row asked to render
		private int visibleRow;

		TreeTableCellRenderer(TreeModel model)
		{
			super(model);
		}

		/**
		 * Fix to bad event handling on MacOS X
		 **/
		public String getUIClassID()
		{
			return "TreeTableUI";
		}

		/**
		 * updateUI is overridden to set the colors
		 * of the Trees renderer to match that of the table.
		 **/
		public void updateUI()
		{
			super.updateUI();
			// Make the tree's cell renderer use the
			// table's cell selection colors.
			TreeCellRenderer tcr = getCellRenderer();
			if (tcr instanceof DefaultTreeCellRenderer)
			{
				DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
				dtcr.setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
				dtcr.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
			}
		}

		/**
		 * Sets the row height of the tree and forwards
		 * the row height to the table.
		 **/
		public void setRowHeight(int rowHeight)
		{
			if (rowHeight > 0)
			{
				super.setRowHeight(rowHeight);
				if (JTreeTable.this != null && JTreeTable.this.getRowHeight() != rowHeight)
				{
					JTreeTable.this.setRowHeight(JTreeTable.this.getRowHeight());
				}
			}
		}

		/**
		 * This is overridden to set the height
		 * to match that of the JTable.
		 **/
		public void setBounds(int x, int y, int w, int h)
		{
			super.setBounds(x, 0, w, JTreeTable.this.getHeight());
		}

		/**
		 * Sublcassed to translate the graphics such
		 * that the last visible row will be drawn at 0,0.
		 **/
		public void paint(Graphics g)
		{
			g.translate(0, -visibleRow * JTreeTable.this.getRowHeight());
			super.paint(g);
		}

		/**
		 * TreeCellRenderer method.
		 * Overridden to update the visible row.
		 **/
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (isSelected)
			{
				this.setBackground(table.getSelectionBackground());
			}
			else
			{
				this.setBackground(table.getBackground());
			}

			visibleRow = row;
			return this;
		}

	}

	/**
	 * TreeTableCellEditor implementation.
	 * Component returned is the JTree.
	 **/
	private final class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor
	{
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c)
		{
			return tree;
		}

		/**
		 * Overridden to return false, and if the event is a mouse event
		 * it is forwarded to the tree.<p>
		 * The behavior for this is debatable, and should really be offered
		 * as a property. By returning false, all keyboard actions are
		 * implemented in terms of the table. By returning true, the
		 * tree would get a chance to do something with the keyboard
		 * events. For the most part this is ok. But for certain keys,
		 * such as left/right, the tree will expand/collapse where as
		 * the table focus should really move to a different column. Page
		 * up/down should also be implemented in terms of the table.
		 * By returning false this also has the added benefit that clicking
		 * outside of the bounds of the tree node, but still in the tree
		 * column will select the row, whereas if this returned true
		 * that wouldn't be the case.
		 * <p>By returning false we are also enforcing the policy that
		 * the tree will never be editable (at least by a key sequence).
		 */
		public boolean isCellEditable(EventObject e)
		{
			if (e instanceof MouseEvent)
			{
				for (int counter = getColumnCount() - 1; counter >= 0; counter--)
				{
					if (getColumnClass(counter) == TreeTableModel.class)
					{
						MouseEvent me = (MouseEvent) e;
						MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX(), me.getY(), me.getClickCount(), me.isPopupTrigger());
						tree.dispatchEvent(newME);
						break;
					}
				}
			}
			return false;
		}

	}

	/**
	 * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
	 * to listen for changes in the ListSelectionModel it maintains. Once
	 * a change in the ListSelectionModel happens, the paths are updated
	 * in the DefaultTreeSelectionModel.
	 **/
	private final class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
	{
		static final long serialVersionUID = -3571248405124682593L;
		// Set to true when we are updating the ListSelectionModel
		private boolean updatingListSelectionModel;

		private ListToTreeSelectionModelWrapper()
		{
			super();
			getListSelectionModel().addListSelectionListener(createListSelectionListener());
		}

		/**
		 * Returns the list selection model.
		 * ListToTreeSelectionModelWrapper listens for changes
		 * to this model and updates the selected paths accordingly.
		 **/
		private ListSelectionModel getListSelectionModel()
		{
			return listSelectionModel;
		}

		/**
		 * This is overridden to set updatingListSelectionModel
		 * and message super. This is the only place
		 * DefaultTreeSelectionModel alters the ListSelectionModel.
		 **/
		public void resetRowSelection()
		{
			if (!updatingListSelectionModel)
			{
				updatingListSelectionModel = true;
				try
				{
					super.resetRowSelection();
				}
				finally
				{
					updatingListSelectionModel = false;

				}

			}
			// Notice how we don't message super if
			// updatingListSelectionModel is true. If
			// updatingListSelectionModel is true, it implies the
			// ListSelectionModel has already been updated and the
			// paths are the only thing that needs to be updated.
		}

		/**
		 * Creates and returns an instance of ListSelectionHandler.
		 **/
		private ListSelectionListener createListSelectionListener()
		{
			return new ListSelectionHandler();
		}

		/**
		 * If <code>updatingListSelectionModel</code> is false,
		 * this will reset the selected paths from the selected
		 * rows in the list selection model.
		 **/
		private void updateSelectedPathsFromSelectedRows()
		{
			if (!updatingListSelectionModel)
			{
				updatingListSelectionModel = true;
				try
				{
					int sRows[] = getSelectedRows();

					if (sRows == null || sRows.length == 0)
					{
						return;
					}
					int count = 0;

					for (int i = 0; i < sRows.length; i++)
					{
						if (tree.getPathForRow(sRows[i]) != null)
						{
							count++;
						}
					}
					if (count == 0)
					{
						return;
					}
					TreePath tps[] = new TreePath[count];
					count = 0;
					for (int i = 0; i < sRows.length; i++)
					{
						TreePath tp = tree.getPathForRow(sRows[i]);
						if (tp != null)
						{
							tps[count++] = tp;
						}
					}

					// don't ned a clear as we are
					// using setSelectionPaths()
					//clearSelection();

					setSelectionPaths(tps);

				}
				finally
				{
					updatingListSelectionModel = false;
				}
			}
		}

		/**
		 * Class responsible for calling
		 * updateSelectedPathsFromSelectedRows when the
		 * selection of the list changse.
		 **/
		final class ListSelectionHandler implements ListSelectionListener
		{
			public void valueChanged(ListSelectionEvent e)
			{
				updateSelectedPathsFromSelectedRows();
			}
		}
	}

	public TreePath search(String name, boolean expand)
	{
		final PObjectNode rootNode = (PObjectNode) tree.getModel().getRoot();
		return search(rootNode, name.toLowerCase(), expand);
	}

	private TreePath search(PObjectNode root, String name, boolean expand)
	{
		List p1 = root.getChildren();
		if (p1 != null)
		{
			for (int counter = 0; counter < p1.size(); counter++)
			{
				PObjectNode node = (PObjectNode) p1.get(counter);
				//recurse for all this node's subnodes...
				if (!node.isLeaf())
				{
					TreePath tp = search(node, name, expand);
					if (tp != null)
					{
						return tp;
					}
				}
				//...but look at all the terminal nodes (actual PObjects)
				else
				{
					final Object theObj = node.getItem();
					if (theObj instanceof PObject)
					{
						String aString;
						if (pcgen.core.SettingsHandler.guiUsesOutputName())
						{
							aString = ((PObject) theObj).getOutputName();
						}
						else
						{
							aString = ((PObject) theObj).getName();
						}
						if (aString.toLowerCase().startsWith(name))
						{
							//expand that node
							List path = new ArrayList();
							PObjectNode pon = node;
							while (pon.getParent() != null)
							{
								path.add(0, pon.getParent()); //pop this entry onto the "front" of the list since it's a parent
								pon = pon.getParent();
							}
							TreePath tpath = new TreePath(path.toArray());
							if (expand)
							{
								tree.expandPath(tpath);
							}

							path.add(node);
							tpath = new TreePath(path.toArray());

							if (expand)
							{
								scrollPathToVisible(tpath);
								tree.setSelectionPath(tpath);
							}

							return tpath;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Searches the direct children of the supplied node for one with a
	 * name starting with the supplied name. If a node is found, it is
	 * optionally selected.
	 *
	 * @param root The PObjectNode whose children are to be searched
	 * @param name The partial name to look for.
	 * @param select Should we select the node (true) or just pass it back (false)
	 * @return TreePath The path to the first matchign node.
	 *                   Null if thee is no match.
	 */
	private TreePath searchSingleLevel(PObjectNode root, String name, boolean select)
	{
		String lowerName = name.toLowerCase();
		List p1 = root.getChildren();
		if (p1 != null)
		{
			for (int counter = 0; counter < p1.size(); counter++)
			{
				PObjectNode node = (PObjectNode) p1.get(counter);
				// Fetch the name of the node so that we can do a comparison
				String aString = node.getNodeName();
				// Check for a match.
				if (aString.toLowerCase().startsWith(lowerName))
				{
					//select that node
					List path = new ArrayList();
					PObjectNode pon = node;
					while (pon.getParent() != null)
					{
						path.add(0, pon.getParent()); //pop this entry onto the "front" of the list since it's a parent
						pon = pon.getParent();
					}
					path.add(node);
					TreePath tpath = new TreePath(path.toArray());

					if (select)
					{
						scrollPathToVisible(tpath);
						tree.setSelectionPath(tpath);
					}

					return tpath;
				}
			}
		}
		return null;
	}

	/**
	 * Makes sure all the path components in path are expanded (except
	 * for the last path component) and scrolls so that the
	 * node identified by the path is displayed. Only works when this
	 * <code>JTree</code> is contained in a <code>JScrollPane</code>.
	 *
	 * @param path  the <code>TreePath</code> identifying the node to
	 * 		bring into view
	 */
	private void scrollPathToVisible(TreePath path)
	{
		if (path != null)
		{
			tree.makeVisible(path);

			Rectangle bounds = tree.getPathBounds(path);

			if (bounds != null)
			{
				scrollRectToVisible(bounds);
			}
		}
	}

	/**
	 * Forwards the <code>scrollRectToVisible()</code> message to the
	 * <code>JComponent</code>'s parent. Components that can service
	 * the request, such as <code>JViewport</code>,
	 * override this method and perform the scrolling.
	 *
	 * @param aRect the visible <code>Rectangle</code>
	 * @see javax.swing.JViewport
	 */
	public void scrollRectToVisible(Rectangle aRect)
	{
		Container parent;
		int dx = getX();
		int dy = getY();

		for (parent = getParent(); !(parent == null) && !(parent instanceof JComponent) && !(parent instanceof CellRendererPane); parent = parent.getParent())
		{
			final Rectangle bounds = parent.getBounds();

			dx += bounds.x;
			dy += bounds.y;
		}

		if ((parent != null) && !(parent instanceof CellRendererPane))
		{
			aRect.x += dx;
			aRect.y += dy;

			((JComponent) parent).scrollRectToVisible(aRect);
			aRect.x -= dx;
			aRect.y -= dy;
		}
	}

	private static final class TimedKeyBuffer
	{
		private String keyBuffer;
		private int timeToWait;
		private long lastMSecs;

		public TimedKeyBuffer(int msecs)
		{
			timeToWait = msecs;
			lastMSecs = 0;
		}

		public void addChar(char character)
		{
			if (System.currentTimeMillis() > lastMSecs + timeToWait)
			{
				keyBuffer = "";
			}

			keyBuffer += String.valueOf(Character.toLowerCase(character));
			lastMSecs = System.currentTimeMillis();
		}

		public void clearBuffer()
		{
			keyBuffer = "";
		}

		public String getString()
		{
			if (System.currentTimeMillis() > lastMSecs + timeToWait)
			{
				return "";
			}
			else
			{
				return keyBuffer;
			}
		}
	}

}
