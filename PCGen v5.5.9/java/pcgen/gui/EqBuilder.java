/*
 * EqBuilder.java
 * @(#) $Id: EqBuilder.java,v 1.1 2006/02/21 01:28:08 vauchers Exp $
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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:28:08 $
 *
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.EquipmentList;
import pcgen.core.spell.Spell;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.TableSorter;
import pcgen.gui.utils.Utility;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Popup frame with export options
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

final class EqBuilder extends JPanel
{
	static final long serialVersionUID = -369105812700996734L;
	private JPanel jPanel1;
	private JPanel jPanelOkCancel;
	private JPanel jPanel3;
	private JPanel jPanel6;
	private JLabel jLabelSize;
	private JButton jButtonName;
	private JButton jButtonSProp;
	private JButton jButtonCost;
	private JButton jButtonWeight;
	private JComboBoxEx jComboBoxSize;
	private JPanel jPanel4;
	private JButton jButtonCancel;
	private JButton jButtonOk;
	private JButton jButtonPurchase;
	private FlippingSplitPane jSplitPane2;
	private JPanel jPanel2;
	private JScrollPane jScrollPane2;
	private JTextPane jItemDesc;
	private JPanel jPanelModifiers;
	private FlippingSplitPane jSplitPane3;
	private JPanel jPanelAvailables;
	private JScrollPane jScroll_ListAvailable;
	private JTableEx jListAvailable;
	private JPanel jPanel5;
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
	private ArrayList[] newTypeList = {null, null};

	static String[] validEqTypes = {"potion", "scroll", "wand", "ring"};
	public static final int EQTYPE_NONE = -1;
	public static final int EQTYPE_POTION = 0;
	public static final int EQTYPE_SCROLL = 1;
	public static final int EQTYPE_WAND = 2;
	public static final int EQTYPE_RING = 3;
	private int eqType = EQTYPE_NONE;

	private String customName = "";

	private List displayModifiers = new ArrayList();

	/**
	 * Creates new form EqBuilder
	 */
	EqBuilder()
	{
		initComponents();
	}

	private static void setGuiTextInfo(Object obj, String in_String)
	{
		Utility.setGuiTextInfo(obj, "in_EqBuilder_" + in_String);
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		listModel1 = new DefaultListModel();
		listModel2 = new DefaultListModel();
		jPanel1 = new JPanel();
		jPanelOkCancel = new JPanel();
		jPanel3 = new JPanel();
		jPanel6 = new JPanel();
		jLabelSize = new JLabel(/*"Size"*/);
		jButtonName = new JButton(/*"Name"*/);
		jButtonSProp = new JButton(/*"SProp"*/);
		jButtonCost = new JButton(/*"Cost"*/);
		jButtonWeight = new JButton(/*"Weight"*/);
		jComboBoxSize = new JComboBoxEx();
		jPanel4 = new JPanel();
		jButtonCancel = new JButton(/*"Cancel"*/);
		jButtonOk = new JButton(/*"Ok"*/);
		jButtonPurchase = new JButton(/*"Purchase"*/);
		jSplitPane2 = new FlippingSplitPane();
		jPanel2 = new JPanel();
		jScrollPane2 = new JScrollPane();
		jItemDesc = new JTextPane();
		jPanelModifiers = new JPanel();
		jSplitPane3 = new FlippingSplitPane();
		jPanelAvailables = new JPanel();
		jScroll_ListAvailable = new JScrollPane();

		setGuiTextInfo(jLabelSize, "Size");

		sorter = new TableSorter(dataModel);
		jListAvailable = new JTableEx(sorter);
		jListAvailable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sorter.addMouseListenerToHeaderInTable(jListAvailable);
		jPanel5 = new JPanel();
		jPanelSelections = new JPanel();
		jPanelSelected1 = new JPanel();
		jPanel20 = new JPanel();
		jPanelButtons1 = new JPanel();
		jButtonAdd1 = new JButton(/*"Add"*/);
		jButtonRemove1 = new JButton(/*"Remove"*/);
		jPanel21 = new JPanel();
		jScroll_ListSelected1 = new JScrollPane();
		jListSelected1 = new JList(listModel1);
		jPanelSelected2 = new JPanel();
		jPanel22 = new JPanel();
		jPanelButtons2 = new JPanel();
		jButtonAdd2 = new JButton(/*"Add"*/);
		jButtonRemove2 = new JButton(/*"Remove"*/);
		jPanel23 = new JPanel();
		jScroll_ListSelected2 = new JScrollPane();
		jListSelected2 = new JList(listModel2);

		setLayout(new BorderLayout());

		setPreferredSize(new Dimension(640, 480));
		jPanel1.setLayout(new BorderLayout());

		jPanel1.setPreferredSize(new Dimension(640, 480));
		jPanelOkCancel.setLayout(new BoxLayout(jPanelOkCancel, BoxLayout.X_AXIS));

		jPanel3.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints1;

		GridBagLayout gridbag = new GridBagLayout();
		jPanel6.setLayout(gridbag);

		jPanel6.add(jLabelSize);
		jComboBoxSize.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				jComboBoxSizeActionPerformed();
			}
		});
		jPanel6.add(jComboBoxSize);

		setGuiTextInfo(jButtonName, "Name");
		jButtonName.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonNameActionPerformed();
			}
		});
		jButtonName.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonNameActionPerformed();
			}
		});
		jPanel6.add(jButtonName);

		setGuiTextInfo(jButtonSProp, "SProp");
		jButtonSProp.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonSPropActionPerformed();
			}
		});
		jButtonSProp.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonSPropActionPerformed();
			}
		});
		jPanel6.add(jButtonSProp);

		setGuiTextInfo(jButtonCost, "Cost");
		jButtonCost.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonCostActionPerformed();
			}
		});
		jButtonCost.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonCostActionPerformed();
			}
		});
		jPanel6.add(jButtonCost);

		setGuiTextInfo(jButtonWeight, "Weight");
		jButtonWeight.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonWeightActionPerformed();
			}
		});
		jButtonWeight.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonWeightActionPerformed();
			}
		});
		jPanel6.add(jButtonWeight);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		jPanel3.add(jPanel6, gridBagConstraints1);

		jPanelOkCancel.add(jPanel3);

		jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.X_AXIS));

		setGuiTextInfo(jButtonCancel, "Cancel");
		jButtonCancel.setPreferredSize(new Dimension(81, 27));
		jButtonCancel.setMaximumSize(new Dimension(81, 27));
		jButtonCancel.setMinimumSize(new Dimension(81, 27));
		jButtonCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonCancelActionPerformed();
			}
		});
		jButtonCancel.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonCancelActionPerformed();
			}
		});

		jPanel4.add(jButtonCancel);

		setGuiTextInfo(jButtonOk, "Ok");
		jButtonOk.setPreferredSize(new Dimension(81, 27));
		jButtonOk.setMaximumSize(new Dimension(81, 27));
		jButtonOk.setMinimumSize(new Dimension(81, 27));
		jButtonOk.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonOkActionPerformed(false);
			}
		});
		jButtonOk.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonOkActionPerformed(false);
			}
		});

		jPanel4.add(jButtonOk);

		setGuiTextInfo(jButtonPurchase, "Purchase");
		jButtonPurchase.setPreferredSize(new Dimension(81, 27));
		jButtonPurchase.setMaximumSize(new Dimension(81, 27));
		jButtonPurchase.setMinimumSize(new Dimension(81, 27));
		jButtonPurchase.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonOkActionPerformed(true);
			}
		});
		jButtonPurchase.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonOkActionPerformed(true);
			}
		});

		jPanel4.add(jButtonPurchase);

		jPanelOkCancel.add(jPanel4);

		jPanel1.add(jPanelOkCancel, BorderLayout.SOUTH);

		jSplitPane2.setDividerSize(5);
		jSplitPane2.setOrientation(FlippingSplitPane.VERTICAL_SPLIT);
		jPanel2.setLayout(new BorderLayout());

		jPanel2.setPreferredSize(new Dimension(44, 70));
		jItemDesc.setEditable(false);
		jItemDesc.setBackground(jPanel1.getBackground());
		jScrollPane2.setViewportView(jItemDesc);

		jPanel2.add(jScrollPane2, BorderLayout.CENTER);

		jSplitPane2.setLeftComponent(jPanel2);

		jPanelModifiers.setLayout(new BoxLayout(jPanelModifiers, BoxLayout.X_AXIS));

		jPanelModifiers.setPreferredSize(new Dimension(640, 300));
		jSplitPane3.setDividerSize(5);
		jSplitPane3.setPreferredSize(new Dimension(640, 407));
		jPanelAvailables.setLayout(new BorderLayout());

		jPanelAvailables.setPreferredSize(new Dimension(340, 403));
		jListAvailable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListAvailable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jListAvailableMouseClicked(evt);
			}
		});
		jScroll_ListAvailable.setViewportView(jListAvailable);

		jPanelAvailables.add(jScroll_ListAvailable, BorderLayout.CENTER);

		jSplitPane3.setLeftComponent(jPanelAvailables);

		jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.Y_AXIS));

		jPanel5.setPreferredSize(new Dimension(200, 400));
		jPanelSelections.setLayout(new BoxLayout(jPanelSelections, BoxLayout.Y_AXIS));

		jPanelSelected1.setLayout(new BoxLayout(jPanelSelected1, BoxLayout.X_AXIS));

		jPanelButtons1.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints2;

		setGuiTextInfo(jButtonAdd1, "Add1");
		jButtonAdd1.setPreferredSize(new Dimension(81, 27));
		jButtonAdd1.setMaximumSize(new Dimension(81, 27));
		jButtonAdd1.setMinimumSize(new Dimension(81, 27));
		jButtonAdd1.setEnabled(false);
		jButtonAdd1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonAdd1ActionPerformed();
			}
		});
		jButtonAdd1.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonAdd1ActionPerformed();
			}
		});

		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
		jPanelButtons1.add(jButtonAdd1, gridBagConstraints2);

		setGuiTextInfo(jButtonRemove1, "Remove1");
		jButtonRemove1.setPreferredSize(new Dimension(81, 27));
		jButtonRemove1.setEnabled(false);
		jButtonRemove1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonRemove1ActionPerformed();
			}
		});
		jButtonRemove1.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonRemove1ActionPerformed();
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

		setGuiTextInfo(jButtonAdd2, "Add2");
		jButtonAdd2.setPreferredSize(new Dimension(81, 27));
		jButtonAdd2.setMaximumSize(new Dimension(81, 27));
		jButtonAdd2.setMinimumSize(new Dimension(81, 27));
		jButtonAdd2.setEnabled(false);
		jButtonAdd2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonAdd2ActionPerformed();
			}
		});
		jButtonAdd2.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonAdd2ActionPerformed();
			}
		});

		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
		jPanelButtons2.add(jButtonAdd2, gridBagConstraints3);

		setGuiTextInfo(jButtonRemove2, "Remove2");
		jButtonRemove2.setPreferredSize(new Dimension(81, 27));
		jButtonRemove2.setEnabled(false);
		jButtonRemove2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				jButtonRemove2ActionPerformed();
			}
		});
		jButtonRemove2.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				jButtonRemove2ActionPerformed();
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
		jComboBoxSize.setModel(new DefaultComboBoxModel(populateSizeModel()));

		jSplitPane2.setDividerLocation(SettingsHandler.getCustomizerSplit1());
		jSplitPane3.setDividerLocation(SettingsHandler.getCustomizerSplit2());

		//
		// Want to resize the width of the last column to fill the empty space (if any) created when
		// this is resized
		jScroll_ListAvailable.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				final Dimension dimMax = jScroll_ListAvailable.getSize();
				Dimension dimCur = jListAvailable.getSize();
				if (dimCur.getWidth() < dimMax.getWidth())
				{
					jListAvailable.setSize((int) dimMax.getWidth(), (int) dimCur.getHeight());
					final int[] cols = {jListAvailable.getColumnCount() - 1};
					jListAvailable.setOptimalColumnWidths(cols);
				}
			}
		});
	}

	private static String[] populateSizeModel()
	{
		String[] sizeStrings = new String[SystemCollections.getSizeAdjustmentListSize()];
		for (int i = 0; i < SystemCollections.getSizeAdjustmentListSize(); i++)
		{
			sizeStrings[i] = SystemCollections.getSizeAdjustmentAtIndex(i).getName();
		}
		return sizeStrings;
	}

	private void jButtonNameActionPerformed()
	{
		Object selectedValue = GuiFacade.showInputDialog(null, "Enter the new name", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE, null, null, customName);
		if (selectedValue != null)
		{
			String aString = ((String) selectedValue).trim();
			if ((aString.indexOf('|') >= 0) || (aString.indexOf(':') >= 0) || (aString.indexOf(';') >= 0))
			{
				GuiFacade.showMessageDialog(null, "Invalid character in string! You cannot use '|', ':' or ';' in this entry", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			}
			else
			{
				customName = aString;
				StringBuffer oldName = new StringBuffer("(").append(aNewEq.getItemNameFromModifiers()).append(")");
				//
				// Replace illegal characters in old name
				//
				for (int i = 0; i < oldName.length(); i++)
				{
					switch (oldName.charAt(i))
					{
						case ';':
						case ':':
						case '|':
							oldName.setCharAt(i, '@');
							break;
						default:
							break;
					}
				}
				if (!oldName.toString().toUpperCase().startsWith(Constants.s_GENERIC_ITEM.toUpperCase()))
				{
					aNewEq.setSpecialProperties(oldName.toString());
				}
				final String aSize = SystemCollections.getSizeAdjustmentAtIndex(getItemSize()).getAbbreviation();
				aNewEq.resizeItem(aSize);
				showItemInfo();
			}
		}
	}

	private void jButtonWeightActionPerformed()
	{
		Object selectedValue = GuiFacade.showInputDialog(null, "Enter Item's New Weight", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE, null, null, aNewEq.getWeight().toString());

		if (selectedValue != null)
		{
			String aString = ((String) selectedValue).trim();
			try
			{
				BigDecimal newWeight = new BigDecimal(aString);
				if (newWeight.doubleValue() < 0)
				{
					GuiFacade.showMessageDialog(null, "Weight cannot be negative!", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
					return;
				}
				aNewEq.setWeightMod("0");
				newWeight = newWeight.subtract(new BigDecimal(aNewEq.getWeightAsDouble()));
				aNewEq.setWeightMod(newWeight.toString());
				showItemInfo();
			}
			catch (Exception e)
			{
				GuiFacade.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			}
		}
	}

	private void jButtonCostActionPerformed()
	{
		Object selectedValue = GuiFacade.showInputDialog(null, "Enter Item's New Cost", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE, null, null, aNewEq.getCost().toString());

		if (selectedValue != null)
		{
			String aString = ((String) selectedValue).trim();
			try
			{
				BigDecimal newCost = new BigDecimal(aString);
				if (newCost.doubleValue() < 0)
				{
					GuiFacade.showMessageDialog(null, "Cost cannot be negative!", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
					return;
				}
				aNewEq.setCostMod("0");
				aNewEq.setCostMod(newCost.subtract(aNewEq.getCost()));
				showItemInfo();
			}
			catch (Exception e)
			{
				GuiFacade.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			}
		}
	}

	private void jButtonSPropActionPerformed()
	{
		Object selectedValue = GuiFacade.showInputDialog(null, "Enter Special Property", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE, null, null, aNewEq.getRawSpecialProperties());
		if (selectedValue != null)
		{
			String aString = ((String) selectedValue).trim();
			if ((aString.indexOf('|') >= 0) || (aString.indexOf(':') >= 0) || (aString.indexOf(';') >= 0))
			{
				GuiFacade.showMessageDialog(null, "Invalid character in string! You cannot use '|', ':' or ';' in this entry", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			}
			else
			{
				aNewEq.setSpecialProperties(aString);
				final String aSize = SystemCollections.getSizeAdjustmentAtIndex(getItemSize()).getAbbreviation();
				aNewEq.resizeItem(aSize);
				showItemInfo();
			}
		}
	}

	private void jButtonSpellActionPerformed(EquipmentModifier eqMod, String extraInfo)
	{
		List classList = null;
		List levelList = null;
		boolean metaAllowed = true;
		int spellBooks = 0;
		if (extraInfo.length() != 0)
		{
			//
			// CLASS=Wizard|CLASS=Sorcerer|Metamagic=0|LEVEL=1|LEVEL=2|SPELLBOOKS=Y
			final StringTokenizer aTok = new StringTokenizer(extraInfo, "|", false);
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (aString.startsWith("CLASS="))
				{
					if (classList == null)
					{
						classList = new ArrayList();
					}
					classList.add(aString.substring(6));
				}
				else if (aString.startsWith("LEVEL="))
				{
					if (levelList == null)
					{
						levelList = new ArrayList();
					}
					levelList.add(aString.substring(6));
				}
				else if (aString.startsWith("SPELLBOOKS="))
				{
					switch (aString.charAt(11))
					{
						case 'Y':
							spellBooks = 1;
							break;

						case 'N':
							spellBooks = -1;
							break;

						default:
							spellBooks = 0;
							break;
					}
				}
				else if (aString.equals("METAMAGIC=N"))
				{
					metaAllowed = false;
				}
			}
		}
		ChooseSpellDialog csd = new ChooseSpellDialog((JFrame) Utility.getParentNamed(getParent(), "pcgen.gui.EQFrame"), eqType, metaAllowed, classList, levelList, spellBooks, eqMod.getChoiceString());
		csd.show();
		if (!csd.getWasCancelled())
		{
			Object castingClass = csd.getCastingClass();
			Spell theSpell = csd.getSpell();
			String variant = csd.getVariant();
			String spellType = csd.getSpellType();
			int baseSpellLevel = csd.getBaseSpellLevel();
			int casterLevel = csd.getCasterLevel();
			Object[] metamagicFeats = csd.getMetamagicFeats();

			int charges = -1;
			if (eqMod.getMinCharges() > 0)
			{
				for (; ;)
				{
					Object selectedValue = GuiFacade.showInputDialog(null, "Enter Number of Charges (" + Integer.toString(eqMod.getMinCharges()) + "-" + Integer.toString(eqMod.getMaxCharges()) + ")", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE, null, null, Integer.toString(eqMod.getMaxCharges()));

					if (selectedValue != null)
					{
						try
						{
							final String aString = ((String) selectedValue).trim();
							charges = Integer.parseInt(aString);
							if (charges < eqMod.getMinCharges())
							{
								continue;
							}
							if (charges > eqMod.getMaxCharges())
							{
								continue;
							}
							break;
						}
						catch (Exception exc)
						{
							//TODO: Should we really ignore this?
						}
					}
				}
			}

			final EquipmentModifier existingEqMod = aNewEq.getEqModifierKeyed(eqMod.getKeyName(), true);
			if (existingEqMod != null)
			{
				existingEqMod.setSpellInfo((PObject) castingClass, theSpell, variant, spellType, baseSpellLevel, casterLevel, metamagicFeats, charges);
			}
			else
			{
				eqMod = (EquipmentModifier) eqMod.clone();
				eqMod.setSpellInfo((PObject) castingClass, theSpell, variant, spellType, baseSpellLevel, casterLevel, metamagicFeats, charges);

				aNewEq.addEqModifier(eqMod, true);
			}
			updateDisplay(true);
		}
	}

	private void addButton(boolean bPrimary)
	{
		jButtonAdd1.setEnabled(false);
		jButtonAdd2.setEnabled(false);

		//
		// Trash the cost modifications
		//
		aNewEq.setCostMod("0");

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
				final EquipmentModifier eqMod = (EquipmentModifier) displayModifiers.get(iSelected);
				if (eqMod.getChoiceString().startsWith("EQBUILDER.SPELL"))
				{
					jButtonSpellActionPerformed(eqMod, eqMod.getChoiceString().substring(15));
					return;
				}
				aNewEq.addEqModifier(eqMod, bPrimary);
				setEquipment(aNewEq, true);
				updateDisplay(bPrimary);
				if (aNewEq.isDouble() && eqMod.getAssignToAll())
				{
					aNewEq.addEqModifier(eqMod, !bPrimary);
					updateDisplay(!bPrimary);
				}
				//
				// Get focus in case the chooser popped up
				//
				getRootPane().getParent().requestFocus();
			}
		}
	}

	private void removeButton(DefaultListModel lm, int idx, boolean bPrimary)
	{
		removeElement(lm, idx, bPrimary);
	}

	private void jButtonAdd1ActionPerformed()
	{
		addButton(true);
	}

	private void jButtonAdd2ActionPerformed()
	{
		addButton(false);
	}

	private void jButtonRemove1ActionPerformed()
	{
		jButtonRemove1.setEnabled(false);
		removeButton(listModel1, jListSelected1.getSelectedIndex(), true);
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
				final String aSize = SystemCollections.getSizeAdjustmentAtIndex(getItemSize()).getAbbreviation();
				aNewEq.resizeItem(aSize);
				showItemInfo();
			}
		}
	}

	private void loadScreenInfo()
	{
		final String tbl = "EqBuilder" + String.valueOf((char) ('A' + dataModel.getTableType()));
		int i = jListAvailable.getColumnCount();
		int width = 0;
		for (; i > 0; i--)
		{
			final TableColumn col = jListAvailable.getColumnModel().getColumn(i - 1);
			width = Globals.getCustColumnWidth(tbl, i - 1);
			if (width == 0)
			{
				break;
			}
			col.setPreferredWidth(width);
		}
		if (width == 0)
		{
			//
			// Resize the columns for optimal display
			//
			i = jListAvailable.getColumnCount();
			int[] cols = new int[i];
			for (; i > 0; i--)
			{
				cols[i - 1] = i - 1;
			}
			jListAvailable.setOptimalColumnWidths(cols);
		}
	}

	private void saveScreenInfo()
	{
		final String tbl = "EqBuilder" + String.valueOf((char) ('A' + dataModel.getTableType()));

		SettingsHandler.setCustomizerSplit1(jSplitPane2.getDividerLocation());
		SettingsHandler.setCustomizerSplit2(jSplitPane3.getDividerLocation());

		int i = jListAvailable.getColumnCount();
		for (; i > 0; i--)
		{
			final TableColumn col = jListAvailable.getColumnModel().getColumn(i - 1);
			Globals.setCustColumnWidth(tbl, i - 1, col.getWidth());
		}
	}

	private void jButtonCancelActionPerformed()
	{
		doCleanUp();
		((EQFrame) getRootPane().getParent()).exitItem_actionPerformed(true);
	}

	private void doCleanUp()
	{
		aNewEq = null;
		newTypeList[0] = null;
		newTypeList[1] = null;
		saveScreenInfo();
	}

	private void jButtonOkActionPerformed(boolean bPurchase)
	{
		String sName = aNewEq.getKeyName();
		if (customName.length() != 0)
		{
			sName = customName;
		}

		if (aNewEq.isWeapon() && !aNewEq.isMelee() && !aNewEq.isRanged())
		{
			GuiFacade.showMessageDialog(null, "Weapons must either be Melee or Ranged", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
			return;
		}

		if (sName.toUpperCase().startsWith(Constants.s_GENERIC_ITEM.toUpperCase()))
		{
			GuiFacade.showMessageDialog(null, "You must rename this item!", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
			return;
		}

		if (EquipmentList.getEquipmentKeyed(sName) != null)
		{
			GuiFacade.showMessageDialog(null, "There is already an existing item: " + sName, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
			return;
		}

		aNewEq.setName(sName);
		aNewEq.removeType("AUTO_GEN");
		aNewEq.removeType("STANDARD");

		//
		// Need to change this so that we can customize it again
		//
		if (aNewEq.getBaseItemName().toUpperCase().startsWith(Constants.s_GENERIC_ITEM.toUpperCase()))
		{
			aNewEq.setBaseItem("");
		}

		if (!EquipmentList.addEquipment(aNewEq))
		{
			GuiFacade.showMessageDialog(null, "Error adding item to list.", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
			return;
		}
		PCGen_Frame1.getInst().eqList_Changed(aNewEq, bPurchase);

		doCleanUp();
		((EQFrame) getRootPane().getParent()).exitItem_actionPerformed(false);
	}

	/**
	 * User has clicked on the "available" list. Enable/disable
	 * buttons if qualified/not qualified.
	 * If double click then send to AddButton if only 1 enabled.
	 *
	 * @param evt
	 */
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
					jButtonAdd1ActionPerformed();
				}
				else if (!jButtonAdd1.isEnabled() && jButtonAdd2.isEnabled())
				{
					jButtonAdd2ActionPerformed();
				}
			}
		}
	}

	/**
	 * User has clicked/double clicked on the "selected 1" list
	 * Enable the "Remove1" button on 1st click
	 * If double click send to RemoveButton1
	 *
	 * @param evt
	 */
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

	/**
	 * User has clicked/double clicked on the "selected 2" list
	 * Enable the "Remove2" button on 1st click
	 * If double click send to RemoveButton2
	 *
	 * @param evt
	 */
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

	/**
	 * **********************************************************************************************
	 */

	public boolean setEquipment(Equipment aEq)
	{
		return setEquipment(aEq, false);
	}

	private boolean setEquipment(Equipment aEq, boolean bReloading)
	{
		listModel1.clear();
		listModel2.clear();

		final String sBaseKey = aEq.getBaseItemName();
		if (!bReloading)
		{
			customName = "";
			//
			// If item has a base item name that differs
			// from its item name, then get it for the base
			//
			if (!sBaseKey.equals(aEq.getName()))
			{
				baseEquipment = EquipmentList.getEquipmentKeyed(sBaseKey);
			}
			else
			{
				baseEquipment = aEq;
			}
		}

		// Translate string into numerical type
		//
		eqType = EQTYPE_NONE;
		for (int idx = 0; idx < validEqTypes.length; ++idx)
		{
			if (aEq.isType(validEqTypes[idx]))
			{
				eqType = idx;
				break;
			}
		}

		// If there are no modifiers attached, make sure
		// the item has no types assigned by any modifiers
		//
		if ((aEq.getEqModifierList(true).size() == 0) && (aEq.getEqModifierList(false).size() == 0))
		{
			for (Iterator e = EquipmentList.getModifierList().iterator(); e.hasNext();)
			{
				final EquipmentModifier eqMod = (EquipmentModifier) e.next();
				if (!eqMod.getName().startsWith("EXCLUDEEQ"))
				{
					continue;
				}
				final List typeList = eqMod.getItemType();
				for (Iterator e2 = typeList.iterator(); e2.hasNext();)
				{
					final String type = (String) e2.next();
					if (aEq.isEitherType(type.toUpperCase()))
					{
						GuiFacade.showMessageDialog(null, "This item already has type: " + type + ". Select the base item and modify it instead.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
						return false;
					}
				}
			}
		}

		// Bail out if couldn't find base item
		//
		if (baseEquipment == null)
		{
			Logging.errorPrint("No base equipment found: " + aEq.getName() + ", aborting EqBuilder:" + sBaseKey);
			return false;
		}

		if (bReloading)
		{
			if (aEq.isWeapon() && aEq.isDouble())
			{
				iListCount = 2;
			}
			else
			{
				iListCount = 1;
			}
		}
		else
		{
			if (baseEquipment.isWeapon() && baseEquipment.isDouble())
			{
				iListCount = 2;
			}
			else
			{
				iListCount = 1;
			}
		}

		jListSelected1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListSelected1.setModel(listModel1);

		jPanelButtons1.setVisible(true);

		// Setup modifier filter based on the item's properties
		if (bReloading)
		{
			dataModel.setFilter(aEq, iListCount);
		}
		else
		{
			dataModel.setFilter(baseEquipment, iListCount);
		}

		// Only show size combo if there is a resizing method
		boolean showSize = Globals.canResizeHaveEffect(aEq, aEq.typeList());
		jLabelSize.setVisible(showSize);
		jComboBoxSize.setVisible(showSize);

		// Set item size
		final String eqSize = aEq.getSize();
		final int iSize = Globals.sizeInt(eqSize);
		setItemSize(SystemCollections.getSizeAdjustmentAtIndex(iSize).getName());

		if (!bReloading)
		{
			// Start with a clean copy
			aNewEq = (Equipment) aEq.clone();
		}

		// If not a two-headed weapon, then hide the controls
		// pertaining to the second head
		//
		jPanelSelected2.setVisible(iListCount > 1);

		loadScreenInfo();

		updateDisplay(true);	// update primary list
		updateDisplay(false);	// update secondary list
		return true;
	}

	public void toFront()
	{
		switch (eqType)
		{
			case EQTYPE_POTION:
			case EQTYPE_WAND:
			case EQTYPE_SCROLL:
				for (int idx = 0; idx < displayModifiers.size(); idx++)
				{
					final EquipmentModifier eqMod = (EquipmentModifier) displayModifiers.get(idx);
					if (eqMod.getKeyName().startsWith("SE_") && (eqMod.getMyTypeCount() == 1))
					{
						idx = sorter.translateRow(idx);
						jListAvailable.setRowSelectionInterval(idx, idx);
						jButtonAdd1ActionPerformed();
						break;
					}
				}
				break;

			default:
				break;
		}
	}

	private void setItemSize(String aNewSize)
	{
		for (int i = 0; i < jComboBoxSize.getItemCount(); i++)
		{
			final String aSize = (String) jComboBoxSize.getItemAt(i);
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
			String aSize = (String) jComboBoxSize.getItemAt(idx);
			for (int i = 0; i <= SystemCollections.getSizeAdjustmentListSize() - 1; i++)
			{
				if (SystemCollections.getSizeAdjustmentAtIndex(i).getName().equalsIgnoreCase(aSize))
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
		List eqModList = aNewEq.getEqModifierList(bPrimary);
		DefaultListModel lm;
		if (bPrimary)
		{
			lm = listModel1;
		}
		else
		{
			lm = listModel2;
		}

		lm.clear();
		for (Iterator e = eqModList.iterator(); e.hasNext();)
		{
			final EquipmentModifier eqMod = (EquipmentModifier) e.next();
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
			EquipmentModifier eqMod = (EquipmentModifier) lm.elementAt(idx);
			if (baseEquipment.getEqModifierList(bPrimary).contains(eqMod))
			{
				GuiFacade.showMessageDialog(null, "That modifier is part of the base item. You cannot remove it.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
				return;
			}

			//
			// Trash the cost modifications
			//
			aNewEq.setCostMod("0");

			aNewEq.removeEqModifier(eqMod, bPrimary);
			setEquipment(aNewEq, true);
			updateDisplay(bPrimary);
			if (aNewEq.isDouble() && eqMod.getAssignToAll())
			{
				aNewEq.removeEqModifier(eqMod, !bPrimary);
				updateDisplay(!bPrimary);
			}
			//
			// Get focus in case the chooser popped up
			//
			getRootPane().getParent().requestFocus();
		}
	}

	private boolean needRebuild(boolean bPrimary)
	{
		boolean bRebuild = false;
		final int idx = bPrimary ? 0 : 1;
		ArrayList newTypes = null;
		List oldTypes = newTypeList[idx];

		final EquipmentModifier aEqMod = aNewEq.getEqModifierKeyed("ADDTYPE", bPrimary);
		if (aEqMod != null)
		{
			newTypes = new ArrayList();
			aEqMod.addAssociatedTo(newTypes);
		}

		if (((oldTypes == null) && (aEqMod != null)) || ((oldTypes != null) && (aEqMod == null)))
		{
			bRebuild = true;
		}
		else if ((oldTypes != null) && (newTypes != null))
		{
			if (oldTypes.size() != newTypes.size())
			{
				bRebuild = true;
			}
			else
			{
				for (int i = 0; i < newTypes.size(); i++)
				{
					if (!oldTypes.contains(newTypes.get(i)))
					{
						bRebuild = true;
						break;
					}
				}
			}
		}
		if (newTypes != null)
		{
			newTypeList[idx] = (ArrayList) newTypes.clone();
		}
		else
		{
			newTypeList[idx] = null;
		}
		return bRebuild;
	}

	private void removeAddedType(String addedType)
	{
		final EquipmentModifier eqMod = aNewEq.getEqModifierKeyed("ADDTYPE", true);
		if (eqMod != null)
		{
			if (eqMod.removeAssociated(addedType))
			{
				if (eqMod.getAssociatedCount() == 0)
				{
					aNewEq.getEqModifierList(true).remove(eqMod);
				}
			}
		}
	}

	private void showItemInfo()
	{
		//
		// Show base item name
		//
		StringBuffer aInfo = new StringBuffer(140);
		aInfo.append("Base Item: ").append(baseEquipment.getName()).append(Constants.s_LINE_SEP);
		if (customName.length() != 0)
		{
			aInfo.append("Name: ").append(customName).append(Constants.s_LINE_SEP);
		}
		String sprop = "";

		// If we've got types added, check to see if they've changed.
		// If they have, we need to rebuild the filtered list
		if (needRebuild(true) || needRebuild(false))
		{
			if (aNewEq.isContainer())
			{
				//TODO: What is this? XXX
			}

			if (aNewEq.isWeapon())
			{
				if (aNewEq.getDamage().length() == 0)
				{
					NewWeaponInfoDialog nwid = new NewWeaponInfoDialog((JFrame) Utility.getParentNamed(getParent(), "pcgen.gui.EQFrame"));
					nwid.show();
					if (!nwid.getWasCancelled())
					{
						StringBuffer modString = new StringBuffer("PCGENi_WEAPON");
						modString.append("|DAMAGE=").append(nwid.getDamage());
						modString.append("|CRITRANGE=").append(nwid.getCritRange());
						modString.append("|CRITMULT=").append(nwid.getCritMultiplier());
						aNewEq.addEqModifiers(modString.toString(), true);
					}
					else
					{
						removeAddedType("WEAPON");
					}
				}
			}

			final String modString = "PCGENi_WEAPON|RANGE=";
			if (aNewEq.isWeapon() && aNewEq.isRanged())
			{
				while (aNewEq.getRange().intValue() == 0)
				{
					Object selectedValue = GuiFacade.showInputDialog(null, "Enter the range", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE, null, null, null);
					if (selectedValue != null)
					{
						final String aString = ((String) selectedValue).trim();
						if (Delta.decode(aString).intValue() > 0)
						{
							aNewEq.removeEqModifiers(modString, true);
							aNewEq.addEqModifiers(modString + aString, true);
						}
					}
					else
					{
						removeAddedType("RANGED");
						break;
					}
				}
			}

			if (!aNewEq.isWeapon() || !aNewEq.isRanged())
			{
				removeAddedType("RANGED");
				aNewEq.removeEqModifiers(modString, true);
			}

			if (aNewEq.isArmor())
			{
				if (aNewEq.getMaxDex().intValue() == Constants.MAX_MAXDEX)
				{
					//TODO: What is this?
				}
			}
			//
			// Need to change this so that we can customize it again
			//
			if (aNewEq.getBaseItemName().toUpperCase().startsWith(Constants.s_GENERIC_ITEM.toUpperCase()))
			{
				aNewEq.setBaseItem("");
			}
			setEquipment(aNewEq, true);
			return;
		}

		try
		{
			if (aNewEq != null)
			{
				final int itemPluses = aNewEq.calcPlusForCosting();

				aInfo.append("New Item: ").append(aNewEq.nameItemFromModifiers()).append(Constants.s_LINE_SEP);
				aInfo.append("Cost: ").append(aNewEq.getCost().toString());
				if (itemPluses != 0)
				{
					aInfo.append(" (plus: ").append(itemPluses).append(')');
				}
				aInfo.append(", Weight: ").append(aNewEq.getWeight().toString());
				if (aNewEq.isArmor() || aNewEq.isShield())
				{
					aInfo.append(", AC: ").append(aNewEq.getACMod().toString());
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
						int altCritRange = 21;
						final String aCrit = aNewEq.getAltCritRange();
						if (aCrit.length() > 0)
						{
							altCritRange = 21 - Integer.parseInt(aCrit);
						}
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

					if (aNewEq.isRanged())
					{
						aInfo.append(", Range: ").append(aNewEq.getRange().toString());
					}
				}

				sprop = aNewEq.getSpecialProperties();
				if (sprop.length() != 0)
				{
					aInfo.append(Constants.s_LINE_SEP).append("SPROP: ").append(sprop);
				}
			}
			jItemDesc.setText(aInfo.toString());
		}
		catch (Exception e)
		{
			String x = "ERROR: Exception type:" + e.getClass().getName() + Constants.s_LINE_SEP + "Message:" + e.getMessage();
			GuiFacade.showMessageDialog(null, x, Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			Logging.errorPrint(aInfo.toString(), e);
		}
	}

	/**
	 * This internal class is provides the equipment table
	 * with the data it needs to operate
	 * It has column header names, column widths,
	 * the row count, the and the column count
	 * For the actual data, this class relies on the global
	 * equipment modifier list from <code>Globals</code>.
	 */
	private final class EquipmentModModel extends AbstractTableModel
	{
		static final long serialVersionUID = -369105812700996734L;
		public static final int TABLETYPE_EQMOD2 = 0;
		public static final int TABLETYPE_EQMOD1 = 2;

		private int lastRow = -1;
		private Object[] lastColValue = new Object[6];
		private int tableType = TABLETYPE_EQMOD1;

		/** Store the column heading names here. */
		private static final int COLUMN_TYPE = 0; // array index
		private static final int COLUMN_NAME = 1; // array index
		private final Object[][] s_columnTypeNames_eqMod2 = new Object[][]{{String.class, "Q1"}, {String.class, "Q2"}, {String.class, "Name"}, {String.class, "PreReqs"}, {String.class, "Cost"}, {String.class, "SA"}, {String.class, "Source"}};

		private final Object[][] s_columnTypeNames_eqMod1 = new Object[][]{{String.class, "Q"}, {String.class, "Name"}, {String.class, "PreReqs"}, {String.class, "Cost"}, {String.class, "SA"}, {String.class, "Source"}};

		/**
		 * Return the number of columns in the table.
		 *
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			switch (tableType)
			{
				case TABLETYPE_EQMOD1:
					return s_columnTypeNames_eqMod1.length;

				case TABLETYPE_EQMOD2:
					return s_columnTypeNames_eqMod2.length;

				default:
					Logging.errorPrint("In EqBuilder.getColumnCount the table type " + tableType + " is not handled.");
					break;
			}
			return 0;
		}

		/**
		 * Return the column name.
		 *
		 * @return the name of the column
		 */
		public String getColumnName(int column)
		{
			switch (tableType)
			{
				case TABLETYPE_EQMOD1:
					return (String) s_columnTypeNames_eqMod1[column][COLUMN_NAME];

				case TABLETYPE_EQMOD2:
					return (String) s_columnTypeNames_eqMod2[column][COLUMN_NAME];

				default:
					Logging.errorPrint("In EqBuilder.getColumnName the table type " + tableType + " is not handled.");
					break;
			}
			return null;
		}

		/**
		 *
		 */
		public Class getColumnClass(int column)
		{
			switch (tableType)
			{
				case TABLETYPE_EQMOD1:
					return (Class) s_columnTypeNames_eqMod1[column][COLUMN_TYPE];

				case TABLETYPE_EQMOD2:
					return (Class) s_columnTypeNames_eqMod2[column][COLUMN_TYPE];

				default:
					Logging.errorPrint("In EqBuilder.getColumnClass the table type " + tableType + " is not handled.");
					break;
			}
			return null;
		}

		/**
		 * Return the current number of rows in the table
		 * based on the value from the global equipment list.
		 *
		 * @return the number of rows
		 */
		public int getRowCount()
		{
			return displayModifiers.size();
		}

		private int getTableType()
		{
			return tableType;
		}

		private void setFilter(Equipment anEq, int listCount)
		{
			List aFilter = anEq.typeList();
			int currentRowCount = getRowCount();
			displayModifiers.clear();
			if (currentRowCount > 0)
			{
				fireTableRowsDeleted(0, currentRowCount - 1);
			}

			for (Iterator it = EquipmentList.getModifierList().iterator(); it.hasNext();)
			{
				final EquipmentModifier aEqMod = (EquipmentModifier) it.next();
				if (anEq.isVisible(aEqMod))
				{
					if (aEqMod.isType("ALL"))
					{
						displayModifiers.add(aEqMod);
					}
					else
					{
						for (Iterator e = aFilter.iterator(); e.hasNext();)
						{
							final String aType = (String) e.next();
							if (aEqMod.isType(aType))
							{
								displayModifiers.add(aEqMod);
								break;
							}
						}
					}
				}
			}
			Globals.sortPObjectListByName(displayModifiers);
			lastRow = -1;
			if (listCount == 2)
			{
				if (tableType != TABLETYPE_EQMOD2)
				{
					tableType = TABLETYPE_EQMOD2;
					fireTableStructureChanged();
				}
			}
			else
			{
				if (tableType != TABLETYPE_EQMOD1)
				{
					tableType = TABLETYPE_EQMOD1;
					fireTableStructureChanged();
				}
			}
			fireTableDataChanged();
		}

		/**
		 * Change the value of a grid cell.
		 */
		public Object getValueAt(int row, int column)
		{
			if ((column < 0) || (column >= getColumnCount()))
			{
				return "Out of Bounds";
			}
			if (row >= getRowCount())
			{
				return null;
			}

			if (row != lastRow)
			{
				lastColValue = new String[getColumnCount()];
				lastRow = row;
			}
			else if (lastColValue[column] != null)
			{
				return lastColValue[column];
			}

			Object sRet;
			switch (tableType)
			{
				case TABLETYPE_EQMOD2:
					sRet = getEqModTableValueAt(row, column, true);
					break;

				default:
					sRet = getEqModTableValueAt(row, column, false);
					break;
			}
			try
			{
				lastColValue[column] = sRet;
			}
			catch (Exception exc)
			{
				//TODO: If we really should ignore this, add a note explaining why. XXX
			}
			return sRet;
		}

		private Object getEqModTableValueAt(int row, int column, boolean twoLists)
		{
			EquipmentModifier e;
			try
			{
				e = (EquipmentModifier) displayModifiers.get(row);
			}
			catch (Exception exc)
			{
				return null;
			}

			if (!twoLists && (column > 0))
			{
				column += 1;
			}
			Object sRet = "";
			switch (column)
			{
				case 0:
					if ((aNewEq != null) && aNewEq.canAddModifier(e, true))
					{
						sRet = "Y";
					}
					else
					{
						sRet = "N";
					}
					break;

				case 1:
					if ((aNewEq != null) && aNewEq.canAddModifier(e, false))
					{
						sRet = "Y";
					}
					else
					{
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
					int iPlus = e.getPlus();
					StringBuffer eCost = new StringBuffer(20);
					if (iPlus != 0)
					{
						eCost.append("Plus:").append(iPlus);
					}
					String sCost = e.getPreCost();
					if (!"0".equals(sCost))
					{
						if (eCost.length() != 0)
						{
							eCost.append(", ");
						}
						eCost.append("Precost:").append(sCost);
					}
					sCost = e.getCost();
					if (!"0".equals(sCost))
					{
						if (eCost.length() != 0)
						{
							eCost.append(", ");
						}
						eCost.append("Cost:").append(sCost);
					}
					sRet = eCost.toString();
					break;

				case 5:
					final List aSA = e.getSpecialProperties();
					StringBuffer aBuf = new StringBuffer(aSA.size() * 50);
					for (Iterator e2 = aSA.iterator(); e2.hasNext();)
					{
						if (aBuf.length() > 0)
						{
							aBuf.append(", ");
						}
						aBuf.append((String) e2.next());
					}

					sRet = aBuf.toString();
					break;

				case 6:
					sRet = e.getSource();
					break;

				default:
					Logging.errorPrint("In EqBuilder.getEqModTableValueAt the column " + column + " is not handled.");
					break;

			}
			return sRet;
		}
	}
}
