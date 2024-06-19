/*
 * Options.java
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
 * Created on April 24, 2001, 10:06 PM
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Globals;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
public class Options extends JMenu
{
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();
	private JMenuItem purchaseMode = new JMenuItem("Purchase Mode...");
	private JMenu rollMethods = new JMenu("Stat Roll Methods");
	private JCheckBoxMenuItem method1 = new JCheckBoxMenuItem("4d6 drop lowest");
	private JCheckBoxMenuItem method2 = new JCheckBoxMenuItem("3d6");
	private JCheckBoxMenuItem method3 = new JCheckBoxMenuItem("5d6 drop two lowest");
	private JCheckBoxMenuItem method4 = new JCheckBoxMenuItem("4d6 drop lowest, reroll 1's");
	private JCheckBoxMenuItem method5 = new JCheckBoxMenuItem("4d6 drop lowest, reroll 1's and 2's");
	private JCheckBoxMenuItem method6 = new JCheckBoxMenuItem("3d6 + 5");
	private JCheckBoxMenuItem maxHpAtFirstLevel = new JCheckBoxMenuItem("Max Hp at first level");
	private JMenu hpRollMethods = new JMenu("HP Roll Methods");
	private JCheckBoxMenuItem hpStandard = new JCheckBoxMenuItem("Standard");
	private JCheckBoxMenuItem hpAutomax = new JCheckBoxMenuItem("Always max");
	private JCheckBoxMenuItem hpPercentage = new JCheckBoxMenuItem("Percentage");
	private JCheckBoxMenuItem hpRpga = new JCheckBoxMenuItem("RPGA");
	private JCheckBoxMenuItem toolTipTextShown = new JCheckBoxMenuItem("Show Tooltip text");
	private JCheckBoxMenuItem previewTabShown = new JCheckBoxMenuItem("Show Preview Tab");

	/** Creates new form Options */
	public Options()
	{
		setText("Options");
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception
	{
		this.add(purchaseMode);
		purchaseMode.setToolTipText("Change stat costs for point system");
		purchaseMode.addActionListener(checkBoxHandler);
		purchaseMode.setSelected(Globals.isPurchaseStatMode());
		purchaseMode.setMnemonic('P');
		this.add(rollMethods);
		rollMethods.setMnemonic('S');
		rollMethods.add(method1);
		method1.setToolTipText("Roll 4d6 drop lowest");
		method1.addActionListener(checkBoxHandler);
		rollMethods.add(method2);
		method2.setToolTipText("Roll 3d6");
		method2.addActionListener(checkBoxHandler);
		rollMethods.add(method3);
		method3.setToolTipText("Roll 5d6 drop two lowest");
		method3.addActionListener(checkBoxHandler);
		method3.setEnabled(false);
		rollMethods.add(method4);
		method4.setToolTipText("Roll 4d6 drop lowest and reroll 1's");
		method4.addActionListener(checkBoxHandler);
		rollMethods.add(method5);
		method5.setToolTipText("Roll 4d6 drop lowest reroll 1's and 2's");
		method5.addActionListener(checkBoxHandler);
		rollMethods.add(method6);
		method6.setToolTipText("Roll 3d6 + 5");
		method6.addActionListener(checkBoxHandler);
		switch (Globals.getRollMethod())
		{
			case 0:
				return;
			case 1:
				method1.setSelected(true);
				break;
			case 2:
				method2.setSelected(true);
				break;
			case 3:
				method3.setSelected(true);
				break;
			case 4:
				method4.setSelected(true);
				break;
			case 5:
				method5.setSelected(true);
				break;
			case 6:
				method6.setSelected(true);
				break;
		}

		this.add(maxHpAtFirstLevel);
		maxHpAtFirstLevel.setToolTipText("Set whether the first level should have max hitpoints");
		maxHpAtFirstLevel.addActionListener(checkBoxHandler);
		maxHpAtFirstLevel.setSelected(Globals.isHpMaxAtFirstLevel());
		maxHpAtFirstLevel.setMnemonic('M');

		this.add(hpRollMethods);
		hpRollMethods.setMnemonic('H');
		hpRollMethods.add(hpStandard);
		hpStandard.setToolTipText("Standard");
		hpStandard.addActionListener(checkBoxHandler);
		hpRollMethods.add(hpAutomax);
		hpAutomax.setToolTipText("Always max");
		hpAutomax.addActionListener(checkBoxHandler);
		hpRollMethods.add(hpPercentage);
		hpPercentage.setToolTipText("Percentage");
		hpPercentage.addActionListener(checkBoxHandler);
		hpRollMethods.add(hpRpga);
		hpRpga.setToolTipText("RPGA");
		hpRpga.addActionListener(checkBoxHandler);

		this.add(toolTipTextShown);
		toolTipTextShown.setToolTipText("Show tooltips over tables");
		toolTipTextShown.addActionListener(checkBoxHandler);
		toolTipTextShown.setSelected(Globals.isToolTipTextShown());
		toolTipTextShown.setMnemonic('T');
		this.add(previewTabShown);
		previewTabShown.setToolTipText("Show Preview tab in character's tabs");
		previewTabShown.addActionListener(checkBoxHandler);
		previewTabShown.setSelected(Globals.isPreviewTabShown());
		previewTabShown.setMnemonic('r');


		switch (Globals.getHpRollMethod())
		{
			case Globals.s_HP_STANDARD:
				hpStandard.setSelected(true);
				break;
			case Globals.s_HP_AUTOMAX:
				hpAutomax.setSelected(true);
				break;
			case Globals.s_HP_PERCENTAGE:
				hpPercentage.setSelected(true);
				break;
			case Globals.s_HP_RPGA:
				hpRpga.setSelected(true);
				break;
		}
	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 */
	private final class CheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			Object source = actionEvent.getSource();
			if (source == purchaseMode)
			{
				PurchaseModeStats frame = new PurchaseModeStats();
			}
			else if (source == method1)
			{
				method2.setSelected(false);
				method3.setSelected(false);
				method4.setSelected(false);
				method5.setSelected(false);
				method6.setSelected(false);
				Globals.setRollMethod(1);
			}
			else if (source == method2)
			{
				method1.setSelected(false);
				method3.setSelected(false);
				method4.setSelected(false);
				method5.setSelected(false);
				method6.setSelected(false);
				Globals.setRollMethod(2);
			}
			else if (source == method3)
			{
				method2.setSelected(false);
				method1.setSelected(false);
				method4.setSelected(false);
				method5.setSelected(false);
				method6.setSelected(false);
				Globals.setRollMethod(3);
			}
			else if (source == method4)
			{
				method2.setSelected(false);
				method3.setSelected(false);
				method1.setSelected(false);
				method5.setSelected(false);
				method6.setSelected(false);
				Globals.setRollMethod(4);
			}
			else if (source == method5)
			{
				method2.setSelected(false);
				method3.setSelected(false);
				method4.setSelected(false);
				method1.setSelected(false);
				method6.setSelected(false);
				Globals.setRollMethod(5);
			}
			else if (source == method6)
			{
				method2.setSelected(false);
				method3.setSelected(false);
				method4.setSelected(false);
				method5.setSelected(false);
				method1.setSelected(false);
				Globals.setRollMethod(6);
			}
			else if (source == maxHpAtFirstLevel)
			{
				Globals.setHpMaxAtFirstLevel(maxHpAtFirstLevel.isSelected());
			}
			else if (source == hpStandard)
			{
				hpAutomax.setSelected(false);
				hpPercentage.setSelected(false);
				hpRpga.setSelected(false);
				Globals.setHpRollMethod(Globals.s_HP_STANDARD);
			}
			else if (source == hpAutomax)
			{
				hpStandard.setSelected(false);
				hpPercentage.setSelected(false);
				hpRpga.setSelected(false);
				Globals.setHpRollMethod(Globals.s_HP_AUTOMAX);
			}
			else if (source == hpPercentage)
			{
				hpStandard.setSelected(false);
				hpAutomax.setSelected(false);
				hpRpga.setSelected(false);
				Globals.setHpRollMethod(Globals.s_HP_PERCENTAGE);
			}
			else if (source == hpRpga)
			{
				hpStandard.setSelected(false);
				hpAutomax.setSelected(false);
				hpPercentage.setSelected(false);
				Globals.setHpRollMethod(Globals.s_HP_RPGA);
			}
			else if (source == toolTipTextShown)
			{
				Globals.setToolTipTextShown(toolTipTextShown.isSelected());
			}
			else if (source == previewTabShown)
			{
				Globals.setPreviewTabShown(previewTabShown.isSelected());
			}
		}
	}

	private class PurchaseModeStats extends JFrame
	{
		JScrollPane statCostArea;
		JTableEx statCostTable = new JTableEx();
		StatTableModel statTableModel = new StatTableModel();

		public PurchaseModeStats()
		{
			super("Purchase Mode");
			ClassLoader loader = getClass().getClassLoader();
			Toolkit kit = Toolkit.getDefaultToolkit();
			// according to the API, the following should *ALWAYS* use '/'
			Image img =
				kit.getImage(loader.getResource("pcgen/gui/PcgenIcon.gif"));
			loader = null;
			this.setIconImage(img);
			Dimension screenSize = kit.getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;
			// center frame in screen
			setSize(screenWidth / 4, screenHeight / 4);
			setLocation(screenWidth / 6, screenHeight / 6);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			statCostTable.setModel(statTableModel);
			statCostArea = new JScrollPane(statCostTable);
			statCostArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			statCostArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			statCostArea.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
			statCostArea.setBackground(new Color(255, 255, 255));
			panel.add(statCostArea, BorderLayout.CENTER);
			Container contentPane = getContentPane();
			contentPane.add(panel);
			setVisible(true);
		}
	}

	/**
	 * This class is the model for the stat cost table.
	 */
	private final class StatTableModel extends AbstractTableModel
	{
		private final String[] d_columnNames = new String[]
		{
			"Cost", "Stat"
		};
		private final Class[] d_types = new Class[]
		{
			Integer.class, Integer.class
		};
		private final boolean[] d_canEdit = new boolean[]
		{
			true, false
		};

		public String getColumnName(int columnIndex)
		{
			return d_columnNames[columnIndex];
		}

		public Class getColumnClass(int columnIndex)
		{
			return d_types[columnIndex];
		}

		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return d_canEdit[columnIndex];
		}

		public final int getRowCount()
		{
			return Globals.getStatCost().length;
		}

		public final int getColumnCount()
		{
			return d_columnNames.length;
		}

		public final Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
				return new Integer(Globals.getStatCost()[rowIndex]);
			else if (columnIndex == 1)
				return new Integer(9 + rowIndex);
			else
				throw new ArrayIndexOutOfBoundsException(columnIndex);
		}

		public final void setValueAt(Object newValue,
			int rowIndex,
			int columnIndex)
		{
			if (newValue instanceof Integer)
			{
				if (columnIndex == 0)
				{
					Globals.getStatCost()[rowIndex] = ((Integer)newValue).intValue();
					fireTableCellUpdated(rowIndex, columnIndex);
				}
				else
				{
					throw new ArrayIndexOutOfBoundsException(columnIndex);
				}
			}
		}
	}
}