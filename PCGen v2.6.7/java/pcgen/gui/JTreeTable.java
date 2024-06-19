/*
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
 */
package pcgen.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
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
import pcgen.core.character.CharacterSpell;

/**
 * This example shows how to create a simple JTreeTable component,
 * by using a JTree as a renderer (and editor) for the cells in a
 * particular column in the JTable.
 *
 * @version 1.2 10/27/98
 *
 * @author Philip Milne
 * @author Scott Violet
 */
public class JTreeTable extends JTableEx
{
	/** A subclass of JTree. */
	protected TreeTableCellRenderer tree;

	public JTreeTable(TreeTableModel treeTableModel)
	{
		super();

		/*
		 JTreeTable's event handling assumes bad things about mouse pressed/released
		 that are not true on MacOS X.  For example, one gets NPEs thrown when the mouse is
		 hit because the event manager is waiting for released, and one never gets the
		 release.

		 It turns out that the MetalLAF handles this happily, and thus we can use that to
		 get appropriate line styles, without knackering Mac support.

		 Fix done by LeeAnn Rucker, formerly at Apple for Javasoft.  Put in pcgen by Scott Ellsworth
		 */
		UIManager.put("TreeTableUI", "javax.swing.plaf.metal.MetalTreeUI");
		UIManager.put("Tree.leftChildIndent", new Integer(3));
		UIManager.put("Tree.rightChildIndent", new Integer(8));

		// Create the tree. It will be used as a renderer and editor.

		tree = new TreeTableCellRenderer(treeTableModel);

// Install a tableModel representing the visible rows in the tree.
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

// Force the JTable and JTree to share their row selection models.
		ListToTreeSelectionModelWrapper selectionWrapper = new
			ListToTreeSelectionModelWrapper();
		tree.setSelectionModel(selectionWrapper);
		setSelectionModel(selectionWrapper.getListSelectionModel());

// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, tree);
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

// No grid.
		setShowGrid(false);

// No intercell spacing
		setIntercellSpacing(new Dimension(0, 0));

// And update the height of the trees row to match that of
// the table.
		if (tree.getRowHeight() < 1)
		{
			// Metal looks better like this.
			setRowHeight(18);
		}
		else
		{
			// If the UI has specified a rowHeight, we'd better all be using the same one!
			setRowHeight(tree.getRowHeight());
		}
	}

	/**
	 * Overridden to message super and forward the method to the tree.
	 * Since the tree is not actually in the component hieachy it will
	 * never receive this unless we forward it in this manner.
	 */
	public void updateUI()
	{
		super.updateUI();
		if (tree != null)
		{
			tree.updateUI();
		}
// Use the tree's default foreground and background colors in the
// table.
		LookAndFeel.installColorsAndFont(this, "Tree.background",
			"Tree.foreground", "Tree.font");
	}

	/* Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to
	 * paint the renderers and editors and overriding setBounds() below
	 * is not the right thing to do for an editor. Returning -1 for the
	 * editing row in this case, ensures the editor is never painted.
	 */
	public int getEditingRow()
	{
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 :
			editingRow;
	}

	/**
	 * Overridden to pass the new rowHeight to the tree.
	 */
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
	 */
	public JTree getTree()
	{
		return tree;
	}

	public int getMinSelectionRow()
	{
		int iRow = -1;
		if (tree != null)
		{
			iRow = tree.getMinSelectionRow();
		}
		return iRow;
	}

	public void setSelectionRow(int iRow)
	{
		if (tree != null)
		{
			tree.setSelectionRow(iRow);
		}
	}

	//
	// Returns an array of int of currently expanded rows
	//
	public int[] getExpandedRows()
	{
		int iCount = 0;
		if (tree != null)
		{
			for (int i = 0; i < tree.getRowCount(); i++)
			{
				if (tree.isExpanded(i))
				{
					iCount += 1;
				}
			}
		}
		if (iCount == 0)
		{
			return null;
		}

		int[] expandedRow = new int[iCount];

		iCount = 0;
		for (int i = 0; i < tree.getRowCount(); i++)
		{
			if (tree.isExpanded(i))
			{
				expandedRow[iCount++] = i;
			}
		}

		return expandedRow;
	}

	public void setExpandedRows(int[] expandedRows)
	{
		if ((tree != null) && (expandedRows != null))
		{
			for (int i = 0; i < expandedRows.length; i++)
			{
				final int iRow = expandedRows[i];
				final TreePath tp = tree.getPathForRow(iRow);
				try
				{
					tree.fireTreeWillExpand(tp);
					tree.expandRow(iRow);
					tree.fireTreeExpanded(tp);
				}
				catch (javax.swing.tree.ExpandVetoException veto)
				{
				}
			}
		}
	}


	/** This function starts a recursive search of all PObjectNodes of this JTreeTable,
	 * expanding all occurances of PObjects with a given name
	 */
	public void expandByPObjectName(String name)
	{
		expandByPObjectName((PObjectNode)this.getTree().getModel().getRoot(), name);
	}

	/** This function recursively searches all PObjectNodes of this JTreeTable,
	 * expanding all occurances of PObjects with a given name
	 * The initial call should always come from expandByPObjectName(String)
	 */
	private void expandByPObjectName(PObjectNode root, String name)
	{
		PObjectNode[] p1 = root.getChildren();
		if (p1==null) return;

		for (int counter = 0; counter < p1.length; counter++)
		{
			//recurse for all this node's subnodes...
			if (!p1[counter].isLeaf())
			{
				expandByPObjectName(p1[counter], name);
			}
			//...but look at all the terminal nodes (actual PObjects)
			else
			{
				final Object theObj = p1[counter].getItem();
				if (theObj instanceof PObject)
				{
					if (((PObject)theObj).getName().equals(name))
					{
						//expand that node
						ArrayList path = new ArrayList();
						PObjectNode pon = p1[counter];
						while (pon.getParent() != null)
						{
							path.add(0, pon.getParent()); //pop this entry onto the "front" of the list since it's a parent
							pon = pon.getParent();
						}
						this.getTree().expandPath(new TreePath(path.toArray()));
						//would like to .scrollPathToVisible, but it doesn't seem to work
					}
				}
				else if (theObj instanceof CharacterSpell)
				{
					if (((CharacterSpell)theObj).toString().equals(name))
					{
						//expand that node
						ArrayList path = new ArrayList();
						PObjectNode pon = p1[counter];
						while (pon.getParent() != null)
						{
							path.add(0, pon.getParent()); //pop this entry onto the "front" of the list since it's a parent
							pon = pon.getParent();
						}
						this.getTree().expandPath(new TreePath(path.toArray()));
						//would like to .scrollPathToVisible, but it doesn't seem to work
					}
				}
			}
		}
	}

	/**
	 * A TreeCellRenderer that displays a JTree.
	 */
	public class TreeTableCellRenderer extends JTree implements
		TableCellRenderer
	{
		// Fix to bad event handling on MacOS X - created by Lee Ann Rucker, added to pcgen by Scott Ellsworth
		public String getUIClassID()
		{
			return "TreeTableUI";
		}

		/** Last table/tree row asked to renderer. */
		protected int visibleRow;

		public TreeTableCellRenderer(TreeModel model)
		{
			super(model);
		}

		/**
		 * updateUI is overridden to set the colors of the Tree's renderer
		 * to match that of the table.
		 */
		public void updateUI()
		{
			super.updateUI();
			// Make the tree's cell renderer use the table's cell selection
			// colors.
			TreeCellRenderer tcr = getCellRenderer();
			if (tcr instanceof DefaultTreeCellRenderer)
			{
				DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer)tcr);
				// For 1.1 uncomment this, 1.2 has a bug that will cause an
				// exception to be thrown if the border selection color is
				// null.
				// dtcr.setBorderSelectionColor(null);
				dtcr.setTextSelectionColor(UIManager.getColor
					("Table.selectionForeground"));
				dtcr.setBackgroundSelectionColor(UIManager.getColor
					("Table.selectionBackground"));
			}
		}

		/**
		 * Sets the row height of the tree, and forwards the row height to
		 * the table.
		 */
		public void setRowHeight(int rowHeight)
		{
			if (rowHeight > 0)
			{
				super.setRowHeight(rowHeight);
				if (JTreeTable.this != null &&
					JTreeTable.this.getRowHeight() != rowHeight)
				{
					JTreeTable.this.setRowHeight(JTreeTable.this.getRowHeight());
				}
			}
		}

		/**
		 * This is overridden to set the height to match that of the JTable.
		 */
		public void setBounds(int x, int y, int w, int h)
		{
			super.setBounds(x, 0, w, JTreeTable.this.getHeight());
		}

		/**
		 * Sublcassed to translate the graphics such that the last visible
		 * row will be drawn at 0,0.
		 */
		public void paint(Graphics g)
		{
			g.translate(0, -visibleRow * JTreeTable.this.getRowHeight());
			super.paint(g);
		}

		/**
		 * TreeCellRenderer method. Overridden to update the visible row.
		 */
		public Component getTableCellRendererComponent(JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row, int column)
		{
			if (isSelected)
				this.setBackground(table.getSelectionBackground());
			else
				this.setBackground(table.getBackground());

			visibleRow = row;
			return this;
		}
	}


	/**
	 * TreeTableCellEditor implementation. Component returned is the
	 * JTree.
	 */
	public class TreeTableCellEditor extends AbstractCellEditor implements
		TableCellEditor
	{
		public Component getTableCellEditorComponent(JTable table,
			Object value,
			boolean isSelected,
			int r, int c)
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
						MouseEvent me = (MouseEvent)e;
						MouseEvent newME = new MouseEvent(tree, me.getID(),
							me.getWhen(), me.getModifiers(),
							me.getX() - getCellRect(0, counter, true).x,
							me.getY(), me.getClickCount(),
							me.isPopupTrigger());
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
	 */
	class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
	{
		/** Set to true when we are updating the ListSelectionModel. */
		protected boolean updatingListSelectionModel;

		public ListToTreeSelectionModelWrapper()
		{
			super();
			getListSelectionModel().addListSelectionListener
				(createListSelectionListener());
		}

		/**
		 * Returns the list selection model. ListToTreeSelectionModelWrapper
		 * listens for changes to this model and updates the selected paths
		 * accordingly.
		 */
		ListSelectionModel getListSelectionModel()
		{
			return listSelectionModel;
		}

		/**
		 * This is overridden to set <code>updatingListSelectionModel</code>
		 * and message super. This is the only place DefaultTreeSelectionModel
		 * alters the ListSelectionModel.
		 */
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
		 */
		protected ListSelectionListener createListSelectionListener()
		{
			return new ListSelectionHandler();
		}

		/**
		 * If <code>updatingListSelectionModel</code> is false, this will
		 * reset the selected paths from the selected rows in the list
		 * selection model.
		 */
		protected void updateSelectedPathsFromSelectedRows()
		{
			if (!updatingListSelectionModel)
			{
				updatingListSelectionModel = true;
				try
				{
					// This is way expensive, ListSelectionModel needs an
					// enumerator for iterating.
					int min = listSelectionModel.getMinSelectionIndex();
					int max = listSelectionModel.getMaxSelectionIndex();

					this.clearSelection();
					if (min != -1 && max != -1)
					{
						for (int counter = min; counter <= max; counter++)
						{
							if (listSelectionModel.isSelectedIndex(counter))
							{
								TreePath selPath = tree.getPathForRow
									(counter);

								if (selPath != null)
								{
									addSelectionPath(selPath);
								}
							}
						}
					}
				}
				finally
				{
					updatingListSelectionModel = false;
				}
			}
		}

		/**
		 * Class responsible for calling updateSelectedPathsFromSelectedRows
		 * when the selection of the list changse.
		 */
		class ListSelectionHandler implements ListSelectionListener
		{
			public void valueChanged(ListSelectionEvent e)
			{
				updateSelectedPathsFromSelectedRows();
			}
		}
	}

	public TreePath search(String name, boolean expand)
	{
		final PObjectNode rootNode = (PObjectNode)tree.getModel().getRoot();
		return search(rootNode, name.toLowerCase(), expand);
	}

	public TreePath search(PObjectNode root, String name, boolean expand)
	{
		PObjectNode[] p1 = root.getChildren();
		if (p1 != null)
		{
			for (int counter = 0; counter < p1.length; counter++)
			{
				//recurse for all this node's subnodes...
				if (!p1[counter].isLeaf())
				{
					TreePath tp = search(p1[counter], name, expand);
					if (tp != null)
					{
						return tp;
					}
				}
				//...but look at all the terminal nodes (actual PObjects)
				else
				{
					final Object theObj = p1[counter].getItem();
					if (theObj instanceof PObject)
					{
						if (((PObject)theObj).getName().toLowerCase().startsWith(name))
						{
							//expand that node
							ArrayList path = new ArrayList();
							PObjectNode pon = p1[counter];
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

							path.add(p1[counter]);
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
	 * Makes sure all the path components in path are expanded (except
	 * for the last path component) and scrolls so that the
	 * node identified by the path is displayed. Only works when this
	 * <code>JTree</code> is contained in a <code>JScrollPane</code>.
	 *
	 * @param path  the <code>TreePath</code> identifying the node to
	 * 		bring into view
	 */
	public void scrollPathToVisible(TreePath path)
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
	 * @see JViewport
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

			((JComponent)parent).scrollRectToVisible(aRect);
			aRect.x -= dx;
			aRect.y -= dy;
		}
	}
}
