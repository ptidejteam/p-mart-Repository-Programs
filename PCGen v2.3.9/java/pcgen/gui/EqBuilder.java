/*
 * EqBuilder.java
 * @(#) $Id: EqBuilder.java,v 1.1 2006/02/20 23:47:11 vauchers Exp $
 *
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
 * Created on November 23, 2001, 7:04 PM
 *
 * @version $Revision: 1.1 $
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.util.Delta;


public class EqBuilder extends JPanel
{

	/** Creates new form EqBuilder */
	public EqBuilder()
	{
		initComponents();
	}

	//Sets up all constraints for GridBags
//	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
//	{
//		gbc.gridx = gx;
//		gbc.gridy = gy;
//		gbc.gridwidth = gw;
//		gbc.gridheight = gh;
//		gbc.weightx = wx;
//		gbc.weighty = wy;
//	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		listModel1 = new DefaultListModel();
		listModel2 = new DefaultListModel();
		jPanel1 = new JPanel();
		jPanelOkayCancel = new JPanel();
		jPanel3 = new JPanel();
		jPanel6 = new JPanel();
		jLabelSize = new JLabel();
//		jLabelName = new JLabel();
//		jTextName = new JTextField();
		jComboBoxSize = new JComboBox();
		jPanel4 = new JPanel();
		jButtonCancel = new JButton();
		jButtonOkay = new JButton();
		jSplitPane2 = new JSplitPane();
		jPanel2 = new JPanel();
		jScrollPane2 = new JScrollPane();
		jItemDesc = new JTextPane();
		jPanelModifiers = new JPanel();
		jSplitPane3 = new JSplitPane();
		jPanelAvailables = new JPanel();
		jScroll_ListAvailable = new JScrollPane();
		sorter = new TableSorter(dataModel);
		jListAvailable = new JTableEx(sorter);
		jListAvailable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sorter.addMouseListenerToHeaderInTable(jListAvailable);
//		jListAvailable = new JTable();
		jPanel5 = new JPanel();
		jPanel7 = new JPanel();
		jPanelSelections = new JPanel();
		jPanelSelected1 = new JPanel();
		jPanel20 = new JPanel();
		jPanelButtons1 = new JPanel();
		jButtonAdd1 = new JButton();
		jButtonRemove1 = new JButton();
		jPanel21 = new JPanel();
		jScroll_ListSelected1 = new JScrollPane();
		jListSelected1 = new JList(listModel1);
		jPanelSelected2 = new JPanel();
		jPanel22 = new JPanel();
		jPanelButtons2 = new JPanel();
		jButtonAdd2 = new JButton();
		jButtonRemove2 = new JButton();
		jPanel23 = new JPanel();
		jScroll_ListSelected2 = new JScrollPane();
		jListSelected2 = new JList(listModel2);

		setLayout(new BorderLayout());

		setPreferredSize(new Dimension(640, 480));
		jPanel1.setLayout(new BorderLayout());

		jPanel1.setPreferredSize(new Dimension(640, 480));
		jPanelOkayCancel.setLayout(new BoxLayout(jPanelOkayCancel, BoxLayout.X_AXIS));

		jPanel3.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints1;

		GridBagLayout gridbag = new GridBagLayout();
		jPanel6.setLayout(gridbag);

		jLabelSize.setText("Size");
//        	jLabelSize.setMnemonic('S');
		jLabelSize.setForeground(Color.black);
		jPanel6.add(jLabelSize);

//        	jComboBoxSize.setMnemonic('S');
		jComboBoxSize.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				jComboBoxSizeMouseClicked(evt);
			}
		});

		jPanel6.add(jComboBoxSize);

//		jLabelName.setText("Name");
//		jLabelName.setForeground(Color.black);
//		jPanel6.add(jLabelName);

//		GridBagConstraints c = new GridBagConstraints();
//		c.anchor = GridBagConstraints.EAST;
//		buildConstraints(c, 5, 0, 10, 1, 0, 0);
//		c.fill = GridBagConstraints.HORIZONTAL;
//		gridbag.setConstraints(jTextName, c);
//		jPanel6.add(jTextName);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		jPanel3.add(jPanel6, gridBagConstraints1);

		jPanelOkayCancel.add(jPanel3);

		jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.X_AXIS));

		jButtonCancel.setText("Cancel");
		jButtonCancel.setMnemonic('C');
		jButtonCancel.setPreferredSize(new Dimension(81, 27));
		jButtonCancel.setMaximumSize(new Dimension(81, 27));
		jButtonCancel.setMinimumSize(new Dimension(81, 27));
		jButtonCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonCancelActionPerformed(evt);
			}
		});
		jButtonCancel.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonCancelMouseClicked(evt);
			}
		});

		jPanel4.add(jButtonCancel);

		jButtonOkay.setText("Okay");
		jButtonOkay.setMnemonic('O');
		jButtonOkay.setPreferredSize(new Dimension(81, 27));
		jButtonOkay.setMaximumSize(new Dimension(81, 27));
		jButtonOkay.setMinimumSize(new Dimension(81, 27));
		jButtonOkay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonOkayActionPerformed(evt);
			}
		});
		jButtonOkay.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonOkayMouseClicked(evt);
			}
		});

		jPanel4.add(jButtonOkay);

		jPanelOkayCancel.add(jPanel4);

		jPanel1.add(jPanelOkayCancel, BorderLayout.SOUTH);

		jSplitPane2.setDividerSize(5);
		jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		jPanel2.setLayout(new BorderLayout());

		jPanel2.setPreferredSize(new Dimension(44, 70));
		jItemDesc.setEditable(false);
		jItemDesc.setBackground(new Color(204, 204, 204));
		jScrollPane2.setViewportView(jItemDesc);

		jPanel2.add(jScrollPane2, BorderLayout.CENTER);

		jSplitPane2.setLeftComponent(jPanel2);

		jPanelModifiers.setLayout(new BoxLayout(jPanelModifiers, BoxLayout.X_AXIS));

		jPanelModifiers.setPreferredSize(new Dimension(640, 300));
		jSplitPane3.setDividerSize(5);
		jSplitPane3.setPreferredSize(new Dimension(640, 407));
		jPanelAvailables.setLayout(new BorderLayout());

		jPanelAvailables.setPreferredSize(new Dimension(340, 403));
//		jPanel7.setLayout(new BorderLayout());
		jListAvailable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListAvailable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jListAvailableMouseClicked(evt);
			}
		});
//		jPanel7.add(jListAvailable,BorderLayout.CENTER);
//		jScroll_ListAvailable.setViewportView(jPanel7);
		jScroll_ListAvailable.setViewportView(jListAvailable);

		jPanelAvailables.add(jScroll_ListAvailable, BorderLayout.CENTER);

		jSplitPane3.setLeftComponent(jPanelAvailables);

		jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.Y_AXIS));

		jPanel5.setPreferredSize(new Dimension(200, 400));
		jPanelSelections.setLayout(new BoxLayout(jPanelSelections, BoxLayout.Y_AXIS));

		jPanelSelected1.setLayout(new BoxLayout(jPanelSelected1, BoxLayout.X_AXIS));

		jPanelButtons1.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints2;

		jButtonAdd1.setText("Add");
		jButtonAdd1.setMnemonic('A');
		jButtonAdd1.setPreferredSize(new Dimension(81, 27));
		jButtonAdd1.setMaximumSize(new Dimension(81, 27));
		jButtonAdd1.setMinimumSize(new Dimension(81, 27));
		jButtonAdd1.setEnabled(false);
		jButtonAdd1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonAdd1ActionPerformed(evt);
			}
		});
		jButtonAdd1.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonAdd1MouseClicked(evt);
			}
		});

		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
		jPanelButtons1.add(jButtonAdd1, gridBagConstraints2);

		jButtonRemove1.setText("Remove");
		jButtonRemove1.setMnemonic('R');
		jButtonRemove1.setEnabled(false);
		jButtonRemove1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonRemove1ActionPerformed(evt);
			}
		});
		jButtonRemove1.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonRemove1MouseClicked(evt);
			}
		});

		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		jPanelButtons1.add(jButtonRemove1, gridBagConstraints2);

		jPanel20.add(jPanelButtons1);

		jPanelSelected1.add(jPanel20);

		jPanel21.setLayout(new BorderLayout());

		jListSelected1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListSelected1.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jListSelected1MouseClicked(evt);
			}
		});
		jScroll_ListSelected1.setViewportView(jListSelected1);

		jPanel21.add(jScroll_ListSelected1, BorderLayout.CENTER);

		jPanelSelected1.add(jPanel21);

		jPanelSelections.add(jPanelSelected1);

		jPanelSelected2.setLayout(new BoxLayout(jPanelSelected2, BoxLayout.X_AXIS));

		jPanelButtons2.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints3;

		jButtonAdd2.setText("Add");
		jButtonAdd2.setMnemonic('d');
		jButtonAdd2.setPreferredSize(new Dimension(81, 27));
		jButtonAdd2.setMaximumSize(new Dimension(81, 27));
		jButtonAdd2.setMinimumSize(new Dimension(81, 27));
		jButtonAdd2.setEnabled(false);
		jButtonAdd2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonAdd2ActionPerformed(evt);
			}
		});
		jButtonAdd2.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonAdd2MouseClicked(evt);
			}
		});

		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
		jPanelButtons2.add(jButtonAdd2, gridBagConstraints3);

		jButtonRemove2.setText("Remove");
		jButtonRemove2.setMnemonic('e');
		jButtonRemove2.setEnabled(false);
		jButtonRemove2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonRemove2ActionPerformed(evt);
			}
		});
		jButtonRemove2.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonRemove2MouseClicked(evt);
			}
		});

		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridy = 1;
		jPanelButtons2.add(jButtonRemove2, gridBagConstraints3);

		jPanel22.add(jPanelButtons2);

		jPanelSelected2.add(jPanel22);

		jPanel23.setLayout(new BorderLayout());

		jListSelected2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListSelected2.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jListSelected2MouseClicked(evt);
			}
		});
		jScroll_ListSelected2.setViewportView(jListSelected2);

		jPanel23.add(jScroll_ListSelected2, BorderLayout.CENTER);

		jPanelSelected2.add(jPanel23);

		jPanelSelections.add(jPanelSelected2);

		jPanel5.add(jPanelSelections);

		jSplitPane3.setRightComponent(jPanel5);

		jPanelModifiers.add(jSplitPane3);

		jSplitPane2.setRightComponent(jPanelModifiers);

		jPanel1.add(jSplitPane2, BorderLayout.CENTER);

		add(jPanel1, BorderLayout.CENTER);

		//
		// Set up the size combo's contents
		//
		jComboBoxSize.setModel(new DefaultComboBoxModel(Globals.s_SIZELONG));

	}

	private void jComboBoxSizeMouseClicked(ItemEvent evt)
	{
		jComboBoxSizeActionPerformed();
	}

	private void jButtonOkayMouseClicked(MouseEvent evt)
	{
		jButtonOkayActionPerformed();
	}

	private void jButtonCancelMouseClicked(MouseEvent evt)
	{
		jButtonCancelActionPerformed();
	}

	private void jButtonRemove2MouseClicked(MouseEvent evt)
	{
		jButtonRemove2ActionPerformed();
	}

	private void jButtonRemove1MouseClicked(MouseEvent evt)
	{
		jButtonRemove1ActionPerformed();
	}

	private void jButtonAdd2MouseClicked(MouseEvent evt)
	{
		jButtonAdd2ActionPerformed();
	}

	private void jButtonAdd1MouseClicked(MouseEvent evt)
	{
		jButtonAdd1ActionPerformed();
	}


	// Variables declaration
	private JPanel jPanel1;
	private JPanel jPanelOkayCancel;
	private JPanel jPanel3;
	private JPanel jPanel6;
	private JLabel jLabelSize;
//	private JLabel jLabelName;
//	private JTextField jTextName;
	private JComboBox jComboBoxSize;
	private JPanel jPanel4;
	private JButton jButtonCancel;
	private JButton jButtonOkay;
	private JSplitPane jSplitPane2;
	private JPanel jPanel2;
	private JScrollPane jScrollPane2;
	private JTextPane jItemDesc;
	private JPanel jPanelModifiers;
	private JSplitPane jSplitPane3;
	private JPanel jPanelAvailables;
	private JScrollPane jScroll_ListAvailable;
	private JTableEx jListAvailable;
	private JPanel jPanel5;
	private JPanel jPanel7;
	private JPanel jPanelSelections;
	private JPanel jPanelSelected1;
	private JPanel jPanel20;
	private JPanel jPanelButtons1;
	private JButton jButtonAdd1;
	private JButton jButtonRemove1;
	private JPanel jPanel21;
	private JScrollPane jScroll_ListSelected1;
	private JList jListSelected1;
	private JPanel jPanelSelected2;
	private JPanel jPanel22;
	private JPanel jPanelButtons2;
	private JButton jButtonAdd2;
	private JButton jButtonRemove2;
	private JPanel jPanel23;
	private JScrollPane jScroll_ListSelected2;
	private JList jListSelected2;
	private DefaultListModel listModel1;
	private DefaultListModel listModel2;
	private int iListCount = 0;
	private Equipment baseEquipment = null;
	private Equipment aNewEq = null;
	private EquipmentModModel dataModel = new EquipmentModModel();
	private static TableSorter sorter;

	private ArrayList displayModifiers = new ArrayList();
	// End of variables declaration



	private void addButton(DefaultListModel lm, boolean bPrimary)
	{
		jButtonAdd1.setEnabled(false);
		jButtonAdd2.setEnabled(false);

		final ListSelectionModel lsm = jListAvailable.getSelectionModel();
		int iSelected = lsm.getMinSelectionIndex();
		if (iSelected >= 0)
		{
			iSelected = sorter.getRowTranslated(iSelected);
			if (iSelected >= 0)
			{
				//
				// Add to equipment object
				//
				EquipmentModifier eqMod = (EquipmentModifier)displayModifiers.get(iSelected);
				aNewEq.addEqModifier(eqMod, bPrimary);
				updateDisplay(bPrimary);
				if (aNewEq.isDouble() && eqMod.getAssignToAll())
				{
					aNewEq.addEqModifier(eqMod, !bPrimary);
					updateDisplay(!bPrimary);
				}
			}
		}
	}

	private void removeButton(DefaultListModel lm, int idx, boolean bPrimary)
	{
		removeElement(lm, idx, bPrimary);
	}

	private void jButtonAdd1ActionPerformed(ActionEvent evt)
	{
		jButtonAdd1ActionPerformed();
	}

	private void jButtonAdd1ActionPerformed()
	{
		addButton(listModel1, true);
	}

	private void jButtonAdd2ActionPerformed(ActionEvent evt)
	{
		jButtonAdd2ActionPerformed();
	}

	private void jButtonAdd2ActionPerformed()
	{
		addButton(listModel2, false);
	}

	private void jButtonRemove1ActionPerformed(ActionEvent evt)
	{
		jButtonRemove1ActionPerformed();
	}

	private void jButtonRemove1ActionPerformed()
	{
		jButtonRemove1.setEnabled(false);
		removeButton(listModel1, jListSelected1.getSelectedIndex(), true);
	}

	private void jButtonRemove2ActionPerformed(ActionEvent evt)
	{
		jButtonRemove2ActionPerformed();
	}

	private void jButtonRemove2ActionPerformed()
	{
		jButtonRemove2.setEnabled(false);
		removeButton(listModel2, jListSelected2.getSelectedIndex(), false);
	}

	private void jComboBoxSizeActionPerformed()
	{
		if (jComboBoxSize.getSelectedIndex() >= 0)
		{
			if (aNewEq != null)
			{
				final String aSize = Globals.s_SIZESHORT[getItemSize()];
				aNewEq.resizeItem(aSize);
				showItemInfo();
			}
		}
	}

	private void jButtonCancelActionPerformed(ActionEvent evt)
	{
		jButtonCancelActionPerformed();
	}

	private void jButtonCancelActionPerformed()
	{
		aNewEq = null;
		JFrame parentFrame = (JFrame)getParent().getParent().getParent().getParent();  //ugly, but effective...
		parentFrame.dispose();
	}

	private void jButtonOkayActionPerformed(ActionEvent evt)
	{
		jButtonOkayActionPerformed();
	}

	private void jButtonOkayActionPerformed()
	{
//		final String sName = aNewEq.nameItemFromModifiers();
		final String sName = aNewEq.getKeyName();
		if (Globals.getEquipmentKeyed(sName) != null)
		{
			JOptionPane.showMessageDialog(null, "There is already an existing item: " + sName, Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		if (!Globals.addEquipment(aNewEq))
		{
			JOptionPane.showMessageDialog(null, "Error adding item to list.", Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		((PCGen_Frame1)Globals.getRootFrame()).eqList_Changed();

		aNewEq = null;
		JFrame parentFrame = (JFrame)getParent().getParent().getParent().getParent();  //ugly, but effective...
		parentFrame.dispose();
	}

	//
	// User has clicked on the "available" list. Enable/disable
	// buttons if qualified/not qualified.
	// If double click then send to AddButton if only 1 enabled.
	//
	private void jListAvailableMouseClicked(MouseEvent evt)
	{
		final ListSelectionModel lsm = jListAvailable.getSelectionModel();
		final int iSelected = lsm.getMinSelectionIndex();
		if (iSelected >= 0)
		{
			jButtonAdd1.setEnabled(sorter.getValueAt(iSelected, 0).equals("Y"));
			jButtonAdd2.setEnabled(sorter.getValueAt(iSelected, 1).equals("Y"));
			if (evt.getClickCount() == 2)
			{
				if (jButtonAdd1.isEnabled() && !jButtonAdd2.isEnabled())
				{
					jButtonAdd1MouseClicked(evt);
				}
				else if (!jButtonAdd1.isEnabled() && jButtonAdd2.isEnabled())
				{
					jButtonAdd2MouseClicked(evt);
				}
			}
		}
	}

	//
	// User has clicked/double clicked on the "selected 1" list
	// Enable the "Remove1" button on 1st click
	// If double click send to RemoveButton1
	//
	private void jListSelected1MouseClicked(MouseEvent evt)
	{
		if (jListSelected1.getSelectedIndex() != -1)
		{
			jButtonRemove1.setEnabled(true);
			if (evt.getClickCount() == 2)
			{
				jButtonRemove1ActionPerformed();
			}
		}
	}

	//
	// User has clicked/double clicked on the "selected 2" list
	// Enable the "Remove2" button on 1st click
	// If double click send to RemoveButton2
	//
	private void jListSelected2MouseClicked(MouseEvent evt)
	{
		if (jListSelected2.getSelectedIndex() != -1)
		{
			jButtonRemove2.setEnabled(true);
			if (evt.getClickCount() == 2)
			{
				jButtonRemove2ActionPerformed();
			}
		}
	}


	/**************************************************************************************************/

	public boolean setEquipment(Equipment aEq)
	{
		listModel1.clear();
		listModel2.clear();

		//
		// If item has a base item name that differs from its item name, then get it for the base
		//
		final String sBaseKey = aEq.getBaseItemName();
		if (!sBaseKey.equals(aEq.getName()))
		{
			baseEquipment = Globals.getEquipmentKeyed(sBaseKey);
		}
		else
		{
			baseEquipment = aEq;
		}

		//
		// Only works for armor, shields, weapons, and ammunition
		//
		if (!aEq.isArmor() && !aEq.isShield() && !aEq.isWeapon() && !aEq.isAmmunition())
		{
			if (!aEq.isType("RESIZABLE"))
			{
				JOptionPane.showMessageDialog(null, "You can only customize armor, shields, weapons, and ammunition and select goods.", Globals.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		//
		// If there are no modifiers attached, make sure the item has no types assigned by any modifiers
		//
		if ((aEq.getEqModifierList(true).size() == 0) && (aEq.getEqModifierList(false).size() == 0))
		{
			for (Iterator e = Globals.getModifierList().iterator(); e.hasNext();)
			{
				final EquipmentModifier eqMod = (EquipmentModifier)e.next();
				if (!eqMod.getName().startsWith("EXCLUDEEQ"))
				{
					continue;
				}
				final ArrayList typeList = eqMod.getItemType();
				for (Iterator e2 = typeList.iterator(); e2.hasNext();)
				{
					final String type = (String)e2.next();
					if (aEq.isEitherType(type.toUpperCase()))
					{
						JOptionPane.showMessageDialog(null, "This item already has type: " + type + ". Select the base item and modify it instead.", Globals.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}
			}
		}


		//
		// Bail out if couldn't find base item
		//
		if (baseEquipment == null)
		{
			return false;
		}

		if (baseEquipment.isWeapon() && baseEquipment.isDouble())
		{
			iListCount = 2;
		}
		else
		{
			iListCount = 1;
		}


		//
		// Setup modifier filter based on the base item's properties
		//
		dataModel.setFilter(baseEquipment.typeList());

		//
		// Set item size
		//
		final int iSize = Globals.sizeInt(baseEquipment.getSize());
		setItemSize(Globals.s_SIZELONG[iSize]);


		aNewEq = (Equipment)aEq.clone();				// Start with a clean copy

		//
		// If not a two-headed weapon, then hide the controls pertaining
		// to the second head
		//
		jButtonAdd2.setVisible(iListCount > 1);
		jButtonRemove2.setVisible(iListCount > 1);
		jScroll_ListSelected2.setVisible(iListCount > 1);

		final int[] cols = {0, 1, 2, 3};
		jListAvailable.setOptimalColumnWidths(cols);

		updateDisplay(true, false);		// update primary list
		updateDisplay(false, true);		// update secondary list and update item data displayed
		return true;
	}

	private void setItemSize(String aNewSize)
	{
		for (int i = 0; i < jComboBoxSize.getItemCount(); i++)
		{
			final String aSize = (String)jComboBoxSize.getItemAt(i);
			if (aSize.equalsIgnoreCase(aNewSize))
			{
				jComboBoxSize.setSelectedIndex(i);
				return;
			}
		}
	}

	private int getItemSize()
	{
		final int idx = jComboBoxSize.getSelectedIndex();
		if (idx >= 0)
		{
			String aSize = (String)jComboBoxSize.getItemAt(idx);
			for (int i = Globals.SIZE_F; i <= Globals.SIZE_C; i++)
			{
				if (Globals.s_SIZELONG[i].equalsIgnoreCase(aSize))
				{
					return i;
				}
			}
		}
		return -1;
	}


	private void updateDisplay(boolean bPrimary)
	{
		updateDisplay(bPrimary, true);
	}

	private void updateDisplay(boolean bPrimary, boolean bRedraw)
	{
		//
		// Get list of modifiers and update the listbox
		//
		ArrayList eqModList = aNewEq.getEqModifierList(bPrimary);
		DefaultListModel lm;
		if (bPrimary)
			lm = listModel1;
		else
			lm = listModel2;

		lm.clear();
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier)e.next();
			lm.addElement(eqMod);
		}
		if (bRedraw)
		{
			dataModel.fireTableDataChanged();
			showItemInfo();
		}
	}

	private void removeElement(DefaultListModel lm, int idx, boolean bPrimary)
	{
		if ((idx >= 0) && (idx < lm.size()))
		{
			EquipmentModifier eqMod = (EquipmentModifier)lm.elementAt(idx);
			aNewEq.removeEqModifier(eqMod, bPrimary);
			updateDisplay(bPrimary);
			if (aNewEq.isDouble() && eqMod.getAssignToAll())
			{
				aNewEq.removeEqModifier(eqMod, !bPrimary);
				updateDisplay(!bPrimary);
			}
		}
	}

	private void showItemInfo()
	{
		//
		// Show base item name
		//
		StringBuffer aInfo = new StringBuffer(140);
		aInfo.append("Base Item: ").append(baseEquipment.getName()).append("\n");

		try
		{
			if (aNewEq != null)
			{
				aInfo.append("New Item: ").append(aNewEq.nameItemFromModifiers()).append('\n');
				aInfo.append("Cost: ").append(aNewEq.getCost().toString());
				aInfo.append(", Weight: ").append(aNewEq.getWeight().toString());
				if (aNewEq.isArmor() || aNewEq.isShield())
				{
					aInfo.append(", AC: ").append(aNewEq.getAcMod().toString());
					aInfo.append(", ACCheck: ").append(aNewEq.acCheck().toString());
					aInfo.append(", Fail: ").append(aNewEq.spellFailure().toString());
					aInfo.append(", Max Dex: ").append(aNewEq.getMaxDex().toString());
				}
				if (aNewEq.isWeapon())
				{
					aInfo.append(", Damage: ").append(aNewEq.getDamage());
					int i = aNewEq.getBonusToDamage(true);
					if (i != 0)
					{
						aInfo.append(Delta.toString(i));
					}
					i = aNewEq.getBonusToHit(true);
					if (i != 0)
					{
						aInfo.append(" (").append(Delta.toString(i)).append(" to hit)");
					}

					if (aNewEq.isDouble())
					{
						String altDamage = aNewEq.getAltDamage();
						if (altDamage.length() != 0)
						{
							aInfo.append('/').append(altDamage);
							i = aNewEq.getBonusToDamage(false);
							if (i != 0)
							{
								aInfo.append(Delta.toString(i));
							}
							i = aNewEq.getBonusToHit(false);
							if (i != 0)
							{
								aInfo.append(" (").append(Delta.toString(i)).append(" to hit)");
							}
						}
					}

					final int critRange = 21 - Integer.parseInt(aNewEq.getCritRange());
					aInfo.append(" (").append(String.valueOf(critRange));
					if (critRange < 20)
					{
						aInfo.append("-20");
					}
					aInfo.append(' ').append(aNewEq.getCritMult());

					if (aNewEq.isDouble())
					{
						aInfo.append('/');
						final int altCritRange = 21 - Integer.parseInt(aNewEq.getAltCritRange());
						if (altCritRange != critRange)
						{
							aInfo.append(String.valueOf(altCritRange));
							if (altCritRange < 20)
							{
								aInfo.append("-20");
							}
							aInfo.append(' ');
						}
						aInfo.append(aNewEq.getAltCritMult());
					}
					aInfo.append(')');

					aInfo.append(", Range: ").append(aNewEq.getRange().toString());
				}

				final String sprop = aNewEq.getSpecialProperties();
				if (sprop.length() != 0)
				{
					aInfo.append("\nSPROP: ").append(sprop);
				}
			}
			jItemDesc.setText(aInfo.toString());
		}
		catch (Exception e)
		{
			String x = "ERROR: Exception type:" + e.getClass().getName() + "\nMessage:" + e.getMessage();
			JOptionPane.showMessageDialog(null, x, Globals.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			System.out.println(aInfo.toString());
		}
	}


	/**
	 * This internal class is provides the equipment table with the data it needs
	 * to operate. It has column header names, column widths, the row count, the
	 * and the column count. For the actual data, this class relies on the global
	 * equipment modifier list from <code>Globals</code>.
	 *
	 */
	public final class EquipmentModModel extends AbstractTableModel
	{

		private int lastRow = -1;
		private String[] lastColValue = new String[6];

		/**
		 * Store the column heading names here.
		 */
		private final String[] s_columnNames =
			{
				"Q1",
				"Q2",
				"Name",
				"PreReqs",
				"SA",
				"Source"
			};

		public void setFilter(ArrayList aFilter)
		{
			displayModifiers.clear();
			for (Iterator it = Globals.getModifierList().iterator(); it.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier)it.next();
				if (baseEquipment.isVisible(aEqMod))
				{
					for (Iterator e = aFilter.iterator(); e.hasNext();)
					{
						final String aType = (String)e.next();
						if (aEqMod.isType(aType))
						{
							displayModifiers.add(aEqMod);
							break;
						}
					}
				}
			}
			lastRow = -1;
			fireTableDataChanged();
		}


		/**
		 * Return the column name.
		 *
		 * @return the name of the column
		 */
		public String getColumnName(int column)
		{
			return s_columnNames[column];
		}

		/**
		 * Return the number of columns in the table.
		 *
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return s_columnNames.length;
		}

		/**
		 *
		 */
		public Class getColumnClass(int c)
		{
			return String.class;
		}


		/**
		 * Change the value of a grid cell.
		 */
		public Object getValueAt(int row, int column)
		{
			if ((column < 0) || (column > 5))
				return "Out of Bounds";

			if (row != lastRow)
			{
				lastColValue = new String[6];
				lastRow = row;
			}
			else if (lastColValue[column] != null)
			{
				return lastColValue[column];
			}

			EquipmentModifier e = (EquipmentModifier)displayModifiers.get(row);
			String sRet = "";
			switch (column)
			{
				case 0:
					if ((aNewEq != null) && aNewEq.canAddModifier(e, true))
						sRet = "Y";
					else
						sRet = "N";
					break;
				case 1:
					if (iListCount == 2)
					{
						if ((aNewEq != null) && aNewEq.canAddModifier(e, false))
							sRet = "Y";
						else
							sRet = "N";
					}
					break;
				case 2:
					sRet = e.getName();
					if (e.isType("BaseMaterial"))
					{
						sRet = "*" + sRet;
					}
					break;
				case 3:
					sRet = e.preReqHTMLStrings(aNewEq);
					break;
				case 4:
					final ArrayList aSA = e.getSpecialAbilities();
					StringBuffer aBuf = new StringBuffer(aSA.size() * 50);
					for (Iterator e2 = aSA.iterator(); e2.hasNext();)
					{
						if (aBuf.length() > 0)
						{
							aBuf.append(", ");
						}
						aBuf.append((String)e2.next());
					}

					sRet = aBuf.toString();
					break;
				case 5:
					sRet = e.getSource();
					break;
			}
			lastColValue[column] = sRet;
			return sRet;
		}


		/**
		 * Return the current number of rows in the table based on the value from
		 * the global equipment list.
		 *
		 * @return the number of rows
		 */
		public int getRowCount()
		{
			return displayModifiers.size();
		}

	}
}
