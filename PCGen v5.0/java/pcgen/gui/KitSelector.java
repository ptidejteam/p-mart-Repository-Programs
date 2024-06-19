/*
 * KitSelector.java
 *
 *
 * @(#) $Id: KitSelector.java,v 1.1 2006/02/21 01:07:48 vauchers Exp $
 *
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 24, 2002, 8:59 PM
 *
 * version $Revision: 1.1 $
 */

/**
 *
 * ???
 *
 * @author  Greg Bingleman
 * @version $Revision: 1.1 $
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.kit.KitFeat;
import pcgen.core.kit.KitGear;
import pcgen.core.kit.KitProf;
import pcgen.core.kit.KitSkill;
import pcgen.core.kit.KitSpells;
import pcgen.gui.panes.FlippingSplitPane;

final class KitSelector extends JFrame
{
	private String filter = "";
	private PlayerCharacter aPC = null;

	// Variables declaration
	private JLabelPane txtInfo;
	private JPanel pnlInfo;
	private JTree lstAvailable;
	private JPanel pnlAvailable;
	private JPanel pnlBottom;
	private FlippingSplitPane spMain;
	private JLabel lblAvailable;
	private JLabel lblSelected;
	private JScrollPane scpInfo;
	private JPanel pnlSelected;
	private JButton btnAdd;
	private JPanel pnlFrame;
	private JScrollPane scpSelected;
	private FlippingSplitPane spChoices;
	private JButton btnOk;
	private JScrollPane scpAvailable;
	private JTree lstSelected;
	private JButton btnRemove;

	private static final int USR_NO = 0;
	private static final int USR_YES = 1;

	private DefaultMutableTreeNode rootNodea;
	private DefaultMutableTreeNode rootNodes;

	/** Creates new form KitSelector */
	KitSelector(PlayerCharacter argPC)
	{
		super();

		aPC = argPC;

		initComponents();
		initComponentContents();

		this.setSize(new Dimension(640, 460));
		Utility.centerFrame(this, false);
	}

	public void setFilter(final String argFilter)
	{
		setFilter(argFilter, true);
	}

	private void setFilter(final String argFilter, boolean updateTree)
	{
		if (argFilter.length() == 0)
		{
			filter = "OTHER";
		}
		else
		{
			filter = argFilter;
		}

		//
		// Be nice and scroll to the selected filter and then
		// expand the region that PC is from (if any)
		//
		if (updateTree)
		{
			expandAll(lstAvailable, false, 2);	// collapse tree

			DefaultMutableTreeNode n = findChild(rootNodea, filter);
			if (n != null)
			{
				final String region = aPC.getRegion();
				if ((region.length() != 0) && !region.equalsIgnoreCase(Constants.s_NONE))
				{
					DefaultMutableTreeNode r = findChild(n, region);
					if (r != null)
					{
						n = r;
					}
				}

				//
				// Expand so the last child is visible
				//
				if (n.getChildCount() != 0)
				{
					n = (DefaultMutableTreeNode) n.getLastChild();
				}
				final TreePath tp = new TreePath(n.getPath());
				lstAvailable.expandPath(tp);
				lstAvailable.scrollPathToVisible(tp);
				lstAvailable.updateUI();
			}
		}
	}

	private void initComponentContents()
	{
		Utility.maybeSetIcon(this, "PcgenIcon.gif");

		//
		// Get a list of all the kit types
		//
		ArrayList kitTypes = new ArrayList();
		kitTypes.add("OTHER");
		for (Iterator e = Globals.getKitInfo().iterator(); e.hasNext();)
		{
			final Kit aKit = (Kit) e.next();
			for (int i = 0, x = aKit.getMyTypeCount(); i < x; ++i)
			{
				final String aString = aKit.getMyType(i);
				if (!kitTypes.contains(aString))
				{
					kitTypes.add(aString);
				}
			}
		}
		Collections.sort(kitTypes);

		//
		// Create the trees and insert the kit types
		//
		rootNodea = new DefaultMutableTreeNode();
		rootNodes = new DefaultMutableTreeNode();
		for (int i = 0; i < kitTypes.size(); ++i)
		{
			rootNodea.add(new DefaultMutableTreeNode(kitTypes.get(i), true));
			rootNodes.add(new DefaultMutableTreeNode(kitTypes.get(i), true));
		}

		lstAvailable = new JTree(rootNodea);
		lstAvailable.setCellRenderer(new MyCellRenderer());
		lstAvailable.setRootVisible(false);
		lstAvailable.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		scpAvailable.setViewportView(lstAvailable);

		final ArrayList pcKitInfo = aPC.getKitInfo();
		addSelections(lstAvailable, Globals.getKitInfo(), pcKitInfo);

		lstSelected = new JTree(rootNodes);
		lstSelected.setCellRenderer(new MyCellRenderer());
		lstSelected.setRootVisible(false);
		lstSelected.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		scpSelected.setViewportView(lstSelected);
		addSelections(lstSelected, pcKitInfo, null);

		for (int i = 0, x = lstAvailable.getRowCount(); i < x; ++i)
		{
			final Object obj = lstAvailable.getPathForRow(i).getLastPathComponent();
			if (obj instanceof DefaultMutableTreeNode)
			{
				final String thisType = obj.toString();
				if (thisType.equalsIgnoreCase(filter))
				{
					lstAvailable.expandRow(i);

					//final String region = aPC.getRegion();
					//if ((region.length() != 0) && !region.equalsIgnoreCase(Constants.s_NONE))
					//{
					//}
					break;
				}
			}
		}

		lstAvailable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstAvailableMouseClicked(evt);
			}
		});
		lstAvailable.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent evt)
			{
				lstAvailableSelectionChanged();
			}
		});
		lstSelected.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstSelectedMouseClicked(evt);
			}
		});
		lstSelected.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent evt)
			{
				lstSelectedSelectionChanged();
			}
		});

		lstAvailable.putClientProperty("JTree.lineStyle", "Angled");
		lstSelected.putClientProperty("JTree.lineStyle", "Angled");

		//javax.swing.UIManager.put("TreeTableUI", "javax.swing.plaf.metal.MetalTreeUI");
		//javax.swing.UIManager.put("Tree.leftChildIndent", new Integer(3));
		//javax.swing.UIManager.put("Tree.rightChildIndent", new Integer(8));
	}

	private static DefaultMutableTreeNode findChild(DefaultMutableTreeNode root, Object nodeToFind)
	{
		if ((root != null) && (root.getChildCount() != 0))
		{
			for (Enumeration en = root.children(); en.hasMoreElements();)
			{
				final DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) en.nextElement();
				final Object childObj = childNode.getUserObject();
				if ((childObj instanceof String) && (nodeToFind instanceof String))
				{
					if (((String) nodeToFind).equalsIgnoreCase((String) childObj))
					{
						return childNode;
					}
				}
				else if ((childObj instanceof Kit) && (nodeToFind instanceof Kit))
				{
				}
			}
		}
		return null;
	}

	//
	// If only 1 type, then stick it in that type's list. Otherwise use "Other"
	//
	private static String getKitType(Kit aKit)
	{
		if (aKit.getMyTypeCount() == 1)
		{
			return aKit.getMyType(0);
		}
		return "OTHER";
	}

	private void addSelections(JTree lst, ArrayList kits, ArrayList excluded)
	{
		if ((kits == null) || (kits.size() == 0))
		{
			return;
		}

		for (Iterator e = kits.iterator(); e.hasNext();)
		{
			final Kit aKit = (Kit) e.next();
			if ((excluded != null) && excluded.contains(aKit))
			{
				continue;
			}

			final String region = aKit.getRegion();
			boolean hasRegion = false;
			if ((region.length() != 0) && !region.equalsIgnoreCase(Constants.s_NONE))
			{
				hasRegion = true;
			}

			final String aType = getKitType(aKit);

			for (int i = 0, x = lst.getRowCount(); i < x; ++i)
			{
				DefaultMutableTreeNode obj = (DefaultMutableTreeNode) lst.getPathForRow(i).getLastPathComponent();
				final String thisType = obj.getUserObject().toString();
				if (aType.equalsIgnoreCase(thisType))
				{
					DefaultMutableTreeNode child;
					//
					// If the kit is associated with a region then create a node with the
					// region's name and make the kit its child
					//
					if (hasRegion)
					{
						child = findChild(obj, region);
						if (child == null)
						{
							child = new DefaultMutableTreeNode(region);
							obj.add(child);
						}
						obj = child;
					}
					//
					// Add the kit
					//
					child = new DefaultMutableTreeNode(aKit);
					obj.add(child);
					break;
				}
			}
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		pnlFrame = new JPanel();
		spMain = new FlippingSplitPane();
		spChoices = new FlippingSplitPane();
		pnlAvailable = new JPanel();
		btnAdd = new JButton(new ImageIcon(getClass().getResource("resource/Forward16.gif")));
		btnRemove = new JButton(new ImageIcon(getClass().getResource("resource/Back16.gif")));
		lblAvailable = new JLabel();
		scpAvailable = new JScrollPane();
		pnlSelected = new JPanel();
		lblSelected = new JLabel();
		scpSelected = new JScrollPane();
		pnlBottom = new JPanel();
		pnlInfo = new JPanel();
		scpInfo = new JScrollPane();
		//txtInfo = new JTextArea();
		txtInfo = new JLabelPane();
		btnOk = new JButton();

		setTitle("Kit Selection");

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				closeDialog();
			}
		});

		pnlFrame.setLayout(new BorderLayout());

		pnlFrame.setBorder(new EmptyBorder(new Insets(3, 3, 3, 1)));
		spMain.setOrientation(FlippingSplitPane.VERTICAL_SPLIT);
		spMain.setResizeWeight(0.5);
		spMain.setOneTouchExpandable(true);
		spMain.setDividerSize(10);
		spChoices.setResizeWeight(0.5);
		spChoices.setOneTouchExpandable(true);
		spChoices.setDividerSize(10);
		pnlAvailable.setLayout(new GridBagLayout());

		pnlAvailable.setBorder(new EmptyBorder(new Insets(1, 3, 1, 3)));
		btnAdd.setEnabled(false);
		btnAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddActionPerformed();
			}
		});

		pnlAvailable.add(btnAdd, new GridBagConstraints());

		lblAvailable.setText("Available");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlAvailable.add(lblAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlAvailable.add(scpAvailable, gridBagConstraints);

		spChoices.setLeftComponent(pnlAvailable);

		pnlSelected.setLayout(new GridBagLayout());

		pnlSelected.setBorder(new EmptyBorder(new Insets(1, 3, 1, 3)));
		btnRemove.setEnabled(false);
		btnRemove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveActionPerformed();
			}
		});

		pnlSelected.add(btnRemove, new GridBagConstraints());

		lblSelected.setText("Selected");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlSelected.add(lblSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlSelected.add(scpSelected, gridBagConstraints);

		spChoices.setRightComponent(pnlSelected);

		spMain.setLeftComponent(spChoices);

		pnlBottom.setLayout(new GridBagLayout());

		pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.X_AXIS));

		pnlInfo.setBorder(new TitledBorder("Kit Info"));
		txtInfo.setEditable(false);
		txtInfo.setBackground(pnlInfo.getBackground());
		scpInfo.setViewportView(txtInfo);

		pnlInfo.add(scpInfo);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlBottom.add(pnlInfo, gridBagConstraints);

		btnOk.setMnemonic('O');
		btnOk.setText("Ok");
		btnOk.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnOkActionPerformed();
			}
		});

		pnlBottom.add(btnOk, new GridBagConstraints());

		spMain.setRightComponent(pnlBottom);

		pnlFrame.add(spMain, BorderLayout.CENTER);

		getContentPane().add(pnlFrame, BorderLayout.CENTER);
	}

	private void showKitInfo(Kit theKit)
	{
		if (theKit == null)
		{
			txtInfo.setText();
			return;
		}

		StringBuffer info = new StringBuffer(150);
		info.append("<html>");
		info.append("<b>").append(theKit.getName()).append("</b> ");

		String aString = getPreReqHTMLStrings(theKit);
		if (aString.length() != 0)
		{
			info.append("  <b>Requirements</b>: ").append(aString);
		}

		//
		// Show the Feats
		//
		ArrayList aList = theKit.getFeats();
		if ((aList != null) && (aList.size() != 0))
		{
			info.append("  <b>Feats</b>: ");
			for (int i = 0, x = aList.size(); i < x; ++i)
			{
				final KitFeat kFeat = (KitFeat) aList.get(i);
				if (i != 0)
				{
					info.append("; ");
				}
				aString = kFeat.toString();

				//
				// TODO: parse the feat's individually and check to see if prereqs met (featName[PRExxx])
				// Display as not met if prereq fail
				//

				if (!theKit.passesPreReqTestsForList(aPC, theKit, kFeat.getPrereqs()))
				{
					info.append(SettingsHandler.getPrereqFailColorAsHtml()).append(aString).append("</font>");
				}
				else
				{
					info.append(aString);
				}
			}
		}

		//
		// Show the equipment
		//
		aList = theKit.getGear();
		if ((aList != null) && (aList.size() != 0))
		{
			info.append("  <b>Gear</b>: ");
			for (int i = 0, x = aList.size(); i < x; ++i)
			{
				final KitGear kGear = (KitGear) aList.get(i);
				if (i != 0)
				{
					info.append("; ");
				}
				aString = kGear.toString();
				if (!theKit.passesPreReqTestsForList(aPC, theKit, kGear.getPrereqs()))
				{
					info.append(SettingsHandler.getPrereqFailColorAsHtml()).append(aString).append("</font>");
				}
				else
				{
					info.append(aString);
				}
			}
		}

		//
		// Show the spells
		//
		aList = theKit.getSpells();
		if ((aList != null) && (aList.size() != 0))
		{
			info.append("  <b>Spells</b>: ");
			for (int i = 0, x = aList.size(); i < x; ++i)
			{
				final KitSpells kSpells = (KitSpells) aList.get(i);
				if (i != 0)
				{
					info.append("; ");
				}
				aString = kSpells.toString();
				if (!theKit.passesPreReqTestsForList(aPC, theKit, kSpells.getPrereqs()))
				{
					info.append(SettingsHandler.getPrereqFailColorAsHtml()).append(aString).append("</font>");
				}
				else
				{
					info.append(aString);
				}
			}
		}

		//
		// Show the skills
		//
		aList = theKit.getSkill();
		if ((aList != null) && (aList.size() != 0))
		{
			info.append("  <b>Skills</b>: ");
			for (int i = 0, x = aList.size(); i < x; ++i)
			{
				final KitSkill kSkill = (KitSkill) aList.get(i);
				if (i != 0)
				{
					info.append("; ");
				}
				aString = kSkill.toString();
				if (!theKit.passesPreReqTestsForList(aPC, theKit, kSkill.getPrereqs()))
				{
					info.append(SettingsHandler.getPrereqFailColorAsHtml()).append(aString).append("</font>");
				}
				else
				{
					info.append(aString);
				}
			}
		}

		//
		// Show the proficiencies
		//
		aList = theKit.getProfs();
		if ((aList != null) && (aList.size() != 0))
		{
			info.append("  <b>Proficiencies</b>: ");
			for (int i = 0, x = aList.size(); i < x; ++i)
			{
				final KitProf kProf = (KitProf) aList.get(i);
				if (i != 0)
				{
					info.append("; ");
				}
				aString = kProf.toString();
				if (!theKit.passesPreReqTestsForList(aPC, theKit, kProf.getPrereqs()))
				{
					info.append(SettingsHandler.getPrereqFailColorAsHtml()).append(aString).append("</font>");
				}
				else
				{
					info.append(aString);
				}
			}
		}

		info.append("  <b>Source</b>: ").append(theKit.getSource());

		info.append("</html>");
		txtInfo.setText(info.toString());
	}

	private void addKit(DefaultMutableTreeNode node, Kit theKit)
	{
		//
		// Make sure pass prereqs
		//
		if ((theKit == null) || !kitPassesPrereqs(theKit))
		{
			return;
		}

		ArrayList thingsToAdd = new ArrayList();
		ArrayList warnings = new ArrayList();
		theKit.addKitFeats(thingsToAdd, warnings);
		theKit.addKitProfs(thingsToAdd, warnings);
		theKit.addKitGear(thingsToAdd, warnings);
		theKit.addKitSpells(thingsToAdd, warnings);
		theKit.addKitSkills(thingsToAdd, warnings);

		//
		// See if user wants to apply the kit even though there were errors
		//
		if ((warnings.size() != 0) && (showWarnings(warnings) == USR_NO))
		{
			return;
		}

		theKit.processKit(thingsToAdd);
		forceTabUpdate();

		TreeNode ptr[] = node.getPath();
		node.removeFromParent();

		DefaultMutableTreeNode n = rootNodes;
		//
		// Can ignore the 1st entry--root
		//
		for (int i = 1; i < ptr.length; ++i)
		{
			final Object userObj = ((DefaultMutableTreeNode) ptr[i]).getUserObject();
			DefaultMutableTreeNode child = findChild(n, userObj);
			if (child == null)
			{
				child = new DefaultMutableTreeNode(userObj);
				n.add(child);
			}
			n = child;
		}
		//
		// Expand the tree so the new node is visible and select it
		//
		final TreePath tp = new TreePath(n.getPath());
		lstSelected.scrollPathToVisible(tp);
		lstSelected.setSelectionPath(tp);
		//
		// Need this or trees don't refresh
		//
		lstSelected.updateUI();
		lstAvailable.updateUI();
	}

	//
	// Listen for when the selection changes.
	//
	private void lstAvailableSelectionChanged()
	{
		Kit aKit = null;
		boolean bEnable = false;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) lstAvailable.getLastSelectedPathComponent();

		if (node != null)
		{
			final Object nodeInfo = node.getUserObject();
			if (nodeInfo instanceof Kit)
			{
				aKit = (Kit) nodeInfo;
				if (kitPassesPrereqs(aKit))
				{
					bEnable = true;
				}
			}
		}
		showKitInfo(aKit);
		btnAdd.setEnabled(bEnable);
	}

	private void lstSelectedSelectionChanged()
	{
		Kit aKit = null;
		boolean bEnable = false;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) lstSelected.getLastSelectedPathComponent();

		if (node != null)
		{
			final Object nodeInfo = node.getUserObject();
			if (nodeInfo instanceof Kit)
			{
				aKit = (Kit) nodeInfo;
				bEnable = true;
			}
		}
		showKitInfo(aKit);
		btnRemove.setEnabled(bEnable);
	}

	private static boolean isDoubleClick(MouseEvent evt, JTree lst, JButton btn)
	{
		if (lst.getSelectionCount() >= 0)
		{
			switch (evt.getClickCount())
			{
				case 1:
					break;

				case 2:
					if (btn.isEnabled())
					{
						return true;
					}
					break;

				default:
					break;
			}
		}
		return false;
	}

	private void lstSelectedMouseClicked(MouseEvent evt)
	{
		if (isDoubleClick(evt, lstSelected, btnRemove))
		{
			btnRemoveActionPerformed();
		}
	}

	private void lstAvailableMouseClicked(MouseEvent evt)
	{
		if (isDoubleClick(evt, lstAvailable, btnAdd))
		{
			btnAddActionPerformed();
		}
	}

	private void btnOkActionPerformed()
	{
		closeDialog();
	}

	private void btnRemoveActionPerformed()
	{
		btnRemove.setEnabled(false);
	}

	private void btnAddActionPerformed()
	{
		btnAdd.setEnabled(false);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) lstAvailable.getLastSelectedPathComponent();

		if (node != null)
		{
			final Object nodeInfo = node.getUserObject();
			if (nodeInfo instanceof Kit)
			{
				addKit(node, (Kit) nodeInfo);
				requestFocus();
			}
		}
	}

	public void closeDialog()
	{
		//
		// TODO: save window size and position of scroll pane dividers
		//
		setVisible(false);
		dispose();
	}

	private boolean kitPassesPrereqs(Kit theKit)
	{
		ArrayList prereqList = theKit.getPreReqList();
		final String region = theKit.getRegion();
		if (!region.equalsIgnoreCase(Constants.s_NONE))
		{
			if (prereqList == null)
			{
				prereqList = new ArrayList();
			}
			prereqList.add("PREREGION:" + region);
		}
		return theKit.passesPreReqTestsForList(aPC, theKit, prereqList);
	}

	private String getPreReqHTMLStrings(Kit theKit)
	{
		ArrayList prereqList = theKit.getPreReqList();
		final String region = theKit.getRegion();
		if (!region.equalsIgnoreCase(Constants.s_NONE))
		{
			if (prereqList == null)
			{
				prereqList = new ArrayList();
			}
			prereqList.add("PREREGION:" + region);
		}
		return theKit.preReqHTMLStringsForList(aPC, theKit, prereqList, false);
	}

	private static void forceTabUpdate()
	{
		Globals.getRootFrame().updateByKludge();
	}

	private int userResponse = 0;

	private int showWarnings(ArrayList warnings)
	{
		userResponse = USR_NO;
		try
		{
			//final JFrame jFrame = new JFrame("Warnings");
			final JDialog aFrame = new JDialog(this, "Warnings", true);
			//Utility.maybeSetIcon(aFrame, "PcgenIcon.gif");

			final JPanel jPanel1 = new JPanel();
			final JPanel jPanel2 = new JPanel();
			final JPanel jPanel3 = new JPanel();
			final JLabel jLabel1 = new JLabel("The following warnings were encountered");
			final JButton jApply = new JButton("Apply");
			final JButton jAbort = new JButton("Abort");


			//jLabel1.setIcon(new ImageIcon(url));
			jPanel1.add(jLabel1);

			jPanel2.setLayout(new BorderLayout());

			aFrame.getContentPane().add(jPanel1, BorderLayout.NORTH);
			aFrame.getContentPane().add(jPanel2, BorderLayout.CENTER);
			aFrame.getContentPane().add(jPanel3, BorderLayout.SOUTH);

			StringBuffer warningInfo = new StringBuffer(100);
			warningInfo.append("<html>");
			for (Iterator e = warnings.iterator(); e.hasNext();)
			{
				warningInfo.append((String) e.next()).append("<br>");
			}
			warningInfo.append("</html>");

			JEditorPane a = new JEditorPane("text/html", warningInfo.toString());
			a.setEditable(false);
			JScrollPane aPane = new JScrollPane();
			aPane.setViewportView(a);
			jPanel2.add(aPane, BorderLayout.CENTER);

			jPanel3.add(jAbort);
			jPanel3.add(jApply);

			jApply.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					userResponse = USR_YES;
					aFrame.dispose();
				}
			});

			jAbort.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					userResponse = USR_NO;
					aFrame.dispose();
				}
			});

			aFrame.setSize(new Dimension(456, 352));
			//Utility.centerFrame(aFrame, false);
			aFrame.setLocationRelativeTo(this);	// centre on parent
			aFrame.setVisible(true);
		}
		catch (Exception e)
		{
		}
		return userResponse;
	}

	//
	// If expand is true, expands all nodes in the tree.
	// Otherwise, collapses all nodes in the tree.
	//
	private void expandAll(JTree tree, boolean expand, int maxDepth)
	{
		TreeNode root = (TreeNode) tree.getModel().getRoot();

		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand, maxDepth);
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand, int maxDepth)
	{
		if (maxDepth < 0)
		{
			return;
		}

		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0)
		{
			for (Enumeration e = node.children(); e.hasMoreElements();)
			{
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand, --maxDepth);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand)
		{
			tree.expandPath(parent);
		}
		else
		{
			tree.collapsePath(parent);
		}
	}

	private class MyCellRenderer extends DefaultTreeCellRenderer
	{
		private MyCellRenderer()
		{
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{

			Object obj = ((DefaultMutableTreeNode) value).getUserObject();
			if (obj instanceof Kit)
			{
				if (!kitPassesPrereqs((Kit) obj))
				{
					value = "<html>" + SettingsHandler.getPrereqFailColorAsHtml() + ((Kit) obj).getName() + "</font></html>";
				}
			}

			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			return this;
		}
	}

}
