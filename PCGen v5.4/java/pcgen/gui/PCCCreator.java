/*
 * PCCCreator.java -- A custom PCC file generator for use with PCGen
 * Copyright (C) 2002 Ryan Koppenhaver <rlkoppenhaver@yahoo.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.Logging;

/**
 * PCCCreator is the main class of the PCCCreator application.  It scans PCGen's data
 *  directory, building a tree based on the directory structure contained therein, with
 *  .lst files as the leaves.  It then presents this tree to the user, allowing her to
 *  select items, and generate .pcc files based on those selections.
 * <p>
 * The TODO list is as follows (in no particular order):
 * <ul>
 *   <li> Remember location of PCGen data dir.
 *   <li> Include icons to indicate selection status?
 *   <li> Fix color not changing on combobox selection bug
 * </ul>
 * </p>
 *
 * @author	Ryan Koppenhaver <rlkoppenhaver@yahoo.com>
 * @version $Revision: 1.1 $
 *
 * @see		<a href="http://pcgen.sourceforge.net">PCGen -- http://pcgen.sourceforge.net</a>
 *
 */
final class PCCCreator extends JFrame
{
	static final long serialVersionUID = 923830359956243549L;

	////////////////////////////////////////////////////////////////////////////////
	// Nested Classes //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * TODO: documentation
	 */
	final class SourceNode extends DefaultMutableTreeNode
	{
		public static final int NONE = 0;
		public static final int ALL = 1;
		public static final int MIXED = 2;

		public String nodeName;
		public File nodeFile;
		JComboBoxEx combo;
		public JPanel panel;
		public JLabel label;

		public int getSelectedState()
		{
			if (combo != null)
			{
				return combo.getSelectedIndex();
				//Defaults...
			}
			else if (isLeaf())
			{
				return ALL;
			}
			else
			{
				return NONE;
			}
		}

		public SourceNode(File f)
		{
			super(f);
			nodeFile = f;
			nodeName = f.getName();
			//getSelectedState() = NONE;

		}

		public JPanel getPanel()
		{
			if (panel == null)
			{
				panel = new JPanel();
				label = new JLabel(nodeName);
				combo = new JComboBoxEx()
				{
					protected void fireItemStateChanged(ItemEvent e)
					{
						super.fireItemStateChanged(e);
					}
				};

				//NOTE: Order of insertion is significant.
				if (nodeFile.isDirectory())
				{
					combo.addItem("INCLUDE NONE");
					combo.addItem("INCLUDE ALL");
					combo.addItem("MIXED");
					if (isRoot())
					{
						combo.setSelectedIndex(MIXED);
					}
					else
					{
						combo.setSelectedIndex(NONE);
					}
				}
				else
				{
					combo.addItem("EXCLUDE");
					combo.addItem("INCLUDE");
					combo.setSelectedIndex(ALL);
				}

				//combo.setBackground(Color.white);
				//panel.setBackground(Color.white);

				panel.setOpaque(false);

				panel.add(combo);
				panel.add(label);
			}
			if (isRelevant())
			{
				combo.setEnabled(true);
				label.setForeground(getSelectedState() == MIXED ? Color.black : (getSelectedState() == ALL ? Color.green : Color.red));
			}
			else
			{
				combo.setEnabled(false);
				label.setForeground(Color.gray);
			}
			return panel;
		}

		public boolean isRelevant()
		{
			/*
			* jikes says:
			*   "Ambiguous reference to member named 'getParent' inherited
			*    from type 'java/awt/Component' but also declared or
			*    inherited in the enclosing type 'pcgen/gui/PCCCreator$SourceNode'.
			*    Explicit qualification is required."
			* Well, let's do what jikes wants us to do ;-)
			*
			* author: Thomas Behr 15-04-02
			*/
			TreeNode t = SourceNode.this.getParent();
			if (t == null)
			{
				return true;
			}
			else if (((SourceNode) t).isRelevant())
			{
				return (((SourceNode) t).getSelectedState() == MIXED);
			}
			else
			{
				return false;
			}
		}

		public String toString()
		{
			return nodeName;
		}
	}

	static final class SourceNodeTreeCellEditorAndRenderer extends DefaultCellEditor implements TreeCellRenderer, TreeCellEditor
	{

		public SourceNodeTreeCellEditorAndRenderer()
		{
			super(new JComboBoxEx());	//Have to feed it something
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			SourceNode sNode = (SourceNode) value;
			//if (sNode.isRelevant()) {
			return sNode.getPanel();
			//} else {
			//	return sNode.getLabel();
			//}
		}

		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
		{
			SourceNode sNode = (SourceNode) value;
			//if (sNode.isRelevant()) {
			return sNode.getPanel();
			//} else {
			//	return sNode.getLabel();
			//}
		}
	}



	////////////////////////////////////////////////////////////////////////////////
	// Fields //////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	/**
	 * The root node of the tree, located at PCGen's data directory.
	 */
	private SourceNode root;

	/**
	 * A mapping of lst files to their types, obtained by scanning pcc files
	 */
	private Map lstTypes = new HashMap();

	/**
	 * The main panel of our GUI
	 */
	private JPanel mainPanel = new JPanel(new BorderLayout());

	private MainSource mSrc;

	////////////////////////////////////////////////////////////////////////////////
	// Methods /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * The PCCCreator constructor calls methods to scan the data directory and build
	 *  the GUI.
	 */
	public PCCCreator(MainSource ms)
	{
		mSrc = ms;
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.getImage(getClass().getResource(IconUtilitities.RESOURCE_URL + "PcgenIcon.gif"));
		this.setIconImage(img);
		buildTree();
		JButton writeBtn = new JButton("Write .pcc file");
		writeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				writePCCFile();
			}
		});

		mainPanel.add(writeBtn, BorderLayout.SOUTH);
		this.getContentPane().add(mainPanel);

		setSize(400, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Select Sources to include");
		setVisible(true);
	}

	/**
	 * Scans the data directory and builds the JTree.
	 */
	private void buildTree()
	{
		//Prompt for the data dir.
		//Create the root node.
		root = new SourceNode(SettingsHandler.getPccFilesLocation());
		root.nodeName = "All Source Materials";

		//Build the tree data model down from the root
		buildSubTree(root, root.nodeFile);

		//Build the JTree, and set basic properties.
		DefaultTreeModel dtm = new DefaultTreeModel(root);
		final JTree t = new JTree(dtm);
		t.setShowsRootHandles(true);
		t.setEditable(true);
		t.setSelectionModel(null);

		t.setRowHeight(-1);

		t.setCellRenderer(new SourceNodeTreeCellEditorAndRenderer());
		t.setCellEditor(new SourceNodeTreeCellEditorAndRenderer());

		//Add to the window in a scroll box
		JScrollPane s = new JScrollPane(t);
		s.setPreferredSize(new Dimension(400, 800));
		mainPanel.add(s, BorderLayout.CENTER);
	}

	/**
	 * Calls itself recursively to build the the tree of SourceNodes.
	 *
	 * @param	parent	The node to attach children to.
	 * @param	f	A File object representing the directory associated with the "parent" node.
	 *
	 * @return	true if any children were added to the "parent" parameter.  Determines whether the parent
	 *		  gets added to it's parent.
	 */
	private boolean buildSubTree(SourceNode parent, File f)
	{
		boolean addedChildren = false;
		File[] children = f.listFiles();

		//Iterate through subdirectories
		for (int i = 0; i < children.length; i++)
		{
			SourceNode n = new SourceNode(children[i]);
			boolean addThis = false;

			if (children[i].isDirectory())
			{
				//Recurse subdirectory, and add node to tree if subnodes added to node.
				addThis = buildSubTree(n, children[i]) || addThis;
			}
			else
			{
				//Add .lst files to tree
				if (children[i].getName().toLowerCase().endsWith(".lst"))
				{
					addThis = true;
				}
				else if (children[i].getName().toLowerCase().endsWith(".pcc"))
				{
					extractLSTTypes(children[i]);
				}
			}
			if (addThis)
			{
				parent.add(n);
			}
			addedChildren = addedChildren || addThis;
		}
		return addedChildren;
	}

	/**
	 * Scans a pcc file for lst file references, and adds them to {@link #lstTypes lstTypes} for use
	 *  when printing our custom pcc file.
	 */
	private void extractLSTTypes(File f)
	{
		try
		{
			//BufferedReader in = new BufferedReader(new FileReader(f));
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			while (true)
			{
				String line = in.readLine();
				if (line == null)
				{
					return;
				}
				int start = line.indexOf(":");
				if (!line.startsWith("LSTEXCLUDE") && !line.startsWith("PCC") && !line.startsWith("CAMPAIGN") && !line.startsWith("GAME") && !line.startsWith("RANK") && !line.startsWith("SOURCE") && !line.startsWith("#") && (start >= 0))
				{
					String lstType = line.substring(0, start);
					while (start != 0)
					{
						int end = line.indexOf("|", start);

						String lstName;
						if (end == -1)
						{
							lstName = line.substring(start + 1);
						}
						else
						{
							lstName = line.substring(start + 1, end);
						}
						lstName = new File(lstName).getName();
						lstTypes.put(lstName, lstType);
						start = end + 1;
					}
				}
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint("", e);
		}

	}

	/**
	 * Writes a pcc file based on our current selection of nodes.  recurseNodes does most of the work.
	 *
	 * @see #recurseNodes(java.io.PrintStream, PCCCreator.SourceNode, boolean)
	 */
	private void writePCCFile()
	{
		String name = GuiFacade.showInputDialog(this, "Enter PCC filename");
		if (name == null)
		{
			return;
		}
		if (!name.endsWith(".pcc"))
		{
			name += ".pcc";
		}
		try
		{
			FileOutputStream fout = new FileOutputStream(new File(root.nodeFile, name));
			PrintStream pr = new PrintStream(fout);
			pr.println("CAMPAIGN: Custom Source Materials (" + name + ")");
			recurseNodes(pr, root, (root.getSelectedState() == SourceNode.ALL));
			fout.close();
		}
		catch (IOException e)
		{
			Logging.errorPrint("", e);
		}

		mSrc.refreshCampaigns();
	}

	/**
	 * Follows the tree of nodes, finding selected ones and appending them to the output pcc file.
	 */
	private void recurseNodes(PrintStream pr, SourceNode n, boolean force)
	{
		if (!force && (n.getSelectedState() == SourceNode.NONE))
		{
			return;
		}

		for (Enumeration e = n.children(); e.hasMoreElements();)
		{
			SourceNode sNode = (SourceNode) e.nextElement();
			if (sNode.isLeaf())
			{
				String type = (String) lstTypes.get(sNode.nodeName);
				String absPath = sNode.nodeFile.getPath();
				String relPath = absPath.substring(absPath.indexOf(File.separator + "data" + File.separator) + 6);
				if (type == null)
				{
					type = "# *** UNKNOWN TYPE, FIX ME ***";
				}
				if (force || (sNode.getSelectedState() == SourceNode.ALL))
				{
					pr.println(type + ":" + relPath);
				}
			}
			else
			{
				recurseNodes(pr, sNode, (force || sNode.getSelectedState() == SourceNode.ALL));
			}
		}
	}
}
