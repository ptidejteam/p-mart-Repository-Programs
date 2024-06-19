/*
 * PurchaseModeFrame.java
 * Copyright 2002 (C) Chris Ryan
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
 */

package pcgen.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Constants;
import pcgen.core.PointBuyMethod;
import pcgen.core.SettingsHandler;

/**
 * @author Chris Ryan
 * @version    $Revision: 1.1 $
 */
final class PurchaseModeFrame extends JDialog
{
	private static String s_TITLE = "Purchase Mode Configuration";

	private JPanel jPanel3;
	private JPanel jPanel1;
	private JScrollPane jScrollPane1;
	private JButton okButton;
	private JTable abilityScoreCostTable;
/////////////////////////////////////////////////
// Yanked for WotC compliance
	private JPanel jPanel2;
	private JLabel purchaseScoreMaxLabel;
	private JTextField purchaseScoreMaxEdit;
	private JLabel purchaseScoreMinLabel;
	private JTextField purchaseScoreMinEdit;
	private JButton resetButton;
//	private JButton defaultButton;
	private JButton purchaseScoreMinIncreaseButton;
	private JButton purchaseScoreMinDecreaseButton;
	private JButton purchaseScoreMaxIncreaseButton;
	private JButton purchaseScoreMaxDecreaseButton;
	private JButton cancelButton;
	private JLabel statusBar;
/////////////////////////////////////////////////
	private JPanel purchaseMethodPanel;
	private JPanel purchaseMethodNamePanel;
	private JPanel purchaseMethodPointsPanel;
	private JPanel purchaseMethodButtonPanel;
	private JLabel savedMethodLabel = null;
	private JLabel methodPointsLabel = null;
	private JComboBox currentPurchaseMethods = null;
	private JTextField purchaseMethodPointsEdit;
	private PurchaseModel purchaseModel = null;
	private JButton addMethodButton = null;
	private JButton removeMethodButton = null;

	/** Creates new form PurchaseModeFrame */
	public PurchaseModeFrame()
	{
		initComponents();
	}

	/** Creates new form PurchaseModeFrame */
	public PurchaseModeFrame(JDialog parent)
	{
		super(parent);

		initComponents();
	}

	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		jPanel1 = new JPanel();
/////////////////////////////////////////////////
// Yanked for WotC compliance
		purchaseScoreMinIncreaseButton = new JButton();
		purchaseScoreMinDecreaseButton = new JButton();
		purchaseScoreMaxIncreaseButton = new JButton();
		purchaseScoreMaxDecreaseButton = new JButton();
		cancelButton = new JButton();
		resetButton = new JButton();
//		defaultButton = new JButton();
		purchaseScoreMinLabel = new JLabel();
		purchaseScoreMinEdit = new JTextField();
		purchaseScoreMaxLabel = new JLabel();
		purchaseScoreMaxEdit = new JTextField();
		statusBar = new JLabel();
		jPanel2 = new JPanel();
/////////////////////////////////////////////////
		currentPurchaseMethods = new JComboBox();
		savedMethodLabel = new JLabel();
		methodPointsLabel = new JLabel();
		purchaseMethodPointsEdit = new JTextField();
		purchaseMethodPanel = new JPanel();
		purchaseMethodNamePanel = new JPanel();
		purchaseMethodPointsPanel = new JPanel();
		purchaseMethodButtonPanel = new JPanel();
		addMethodButton = new JButton();
		removeMethodButton = new JButton();

		jPanel3 = new JPanel();
		okButton = new JButton();
		jScrollPane1 = new JScrollPane();
		abilityScoreCostTable = new JTable();

		getContentPane().setLayout(new GridBagLayout());

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(s_TITLE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				exitForm(evt);
			}
		});

		jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 5));

		purchaseScoreMinLabel.setText("Purchase Score Min:");
		purchaseScoreMinLabel.setToolTipText("The ability score can not go below this value");
		purchaseScoreMinLabel.setPreferredSize(new Dimension(140, 15));
		jPanel1.add(purchaseScoreMinLabel);

		purchaseScoreMinEdit.setHorizontalAlignment(JTextField.RIGHT);
		//purchaseScoreMinEdit.setText("10");
		purchaseScoreMinEdit.setPreferredSize(new Dimension(30, 20));
		jPanel1.add(purchaseScoreMinEdit);

		purchaseScoreMinIncreaseButton.setText("+");
		purchaseScoreMinIncreaseButton.setToolTipText("Increase score minimum");
		purchaseScoreMinIncreaseButton.setMargin(new Insets(2, 2, 2, 2));
		purchaseScoreMinIncreaseButton.setPreferredSize(new Dimension(30, 20));
		purchaseScoreMinIncreaseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				purchaseScoreMinIncreaseButtonActionPerformed(evt);
			}
		});

		jPanel1.add(purchaseScoreMinIncreaseButton);

		purchaseScoreMinDecreaseButton.setText("-");
		purchaseScoreMinDecreaseButton.setToolTipText("Decrease score minimum");
		purchaseScoreMinDecreaseButton.setMargin(new Insets(2, 2, 2, 2));
		purchaseScoreMinDecreaseButton.setPreferredSize(new Dimension(30, 20));
		purchaseScoreMinDecreaseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				purchaseScoreMinDecreaseButtonActionPerformed(evt);
			}
		});

		jPanel1.add(purchaseScoreMinDecreaseButton);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(jPanel1, gridBagConstraints);

		jPanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 5));

		purchaseScoreMaxLabel.setText("Purchase Score Max:");
		purchaseScoreMaxLabel.setToolTipText("The ability score can not go above this value");
		purchaseScoreMaxLabel.setPreferredSize(new Dimension(140, 15));
		jPanel2.add(purchaseScoreMaxLabel);

		purchaseScoreMaxEdit.setHorizontalAlignment(JTextField.RIGHT);
		//purchaseScoreMaxEdit.setText("10");
		purchaseScoreMaxEdit.setPreferredSize(new Dimension(30, 20));
		jPanel2.add(purchaseScoreMaxEdit);

		purchaseScoreMaxIncreaseButton.setText("+");
		purchaseScoreMaxIncreaseButton.setToolTipText("Increase score maximum");
		purchaseScoreMaxIncreaseButton.setMargin(new Insets(2, 2, 2, 2));
		purchaseScoreMaxIncreaseButton.setPreferredSize(new Dimension(30, 20));
		purchaseScoreMaxIncreaseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				purchaseScoreMaxIncreaseButtonActionPerformed(evt);
			}
		});

		jPanel2.add(purchaseScoreMaxIncreaseButton);

		purchaseScoreMaxDecreaseButton.setText("-");
		purchaseScoreMaxDecreaseButton.setToolTipText("Decrease score maximum");
		purchaseScoreMaxDecreaseButton.setMargin(new Insets(2, 2, 2, 2));
		purchaseScoreMaxDecreaseButton.setPreferredSize(new Dimension(30, 20));
		purchaseScoreMaxDecreaseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				purchaseScoreMaxDecreaseButtonActionPerformed(evt);
			}
		});

		jPanel2.add(purchaseScoreMaxDecreaseButton);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(jPanel2, gridBagConstraints);

		purchaseMethodPanel.setLayout(new GridBagLayout());
		purchaseMethodPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(purchaseMethodPanel, gridBagConstraints);

		purchaseMethodNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 5));
		savedMethodLabel.setText("Saved Methods:");
		savedMethodLabel.setPreferredSize(new Dimension(140, 15));
		purchaseMethodNamePanel.add(savedMethodLabel);
		purchaseMethodNamePanel.add(currentPurchaseMethods);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(purchaseMethodNamePanel, gridBagConstraints);
		purchaseMethodPanel.add(purchaseMethodNamePanel, gridBagConstraints);

		purchaseMethodPointsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 5));
		methodPointsLabel.setText("Points:");
		methodPointsLabel.setPreferredSize(new Dimension(140, 15));
		purchaseMethodPointsPanel.add(methodPointsLabel);
		purchaseMethodPointsEdit.setHorizontalAlignment(JTextField.RIGHT);
		purchaseMethodPointsEdit.setEditable(false);

		//purchaseMethodPointsEdit.setText("10");
		purchaseMethodPointsEdit.setPreferredSize(new Dimension(30, 20));
		purchaseMethodPointsPanel.add(purchaseMethodPointsEdit);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
//		getContentPane().add(purchaseMethodPointsPanel, gridBagConstraints);
		purchaseMethodPanel.add(purchaseMethodPointsPanel, gridBagConstraints);

		currentPurchaseMethods.setPreferredSize(new Dimension(140, 21));
		currentPurchaseMethods.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				currentPurchaseMethodsActionPerformed();
			}
		});

		purchaseMethodButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		addMethodButton.setText("New");
		addMethodButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addMethodButtonActionPerformed(evt);
			}
		});
		purchaseMethodButtonPanel.add(addMethodButton);
		removeMethodButton.setText("Remove");
		removeMethodButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeMethodButtonActionPerformed(evt);
			}
		});
		purchaseMethodButtonPanel.add(removeMethodButton);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		purchaseMethodPanel.add(purchaseMethodButtonPanel, gridBagConstraints);

		statusBar.setText("Set the cost for each ability score");
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 1;
		gridBagConstraints.ipady = 1;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(statusBar, gridBagConstraints);

		jPanel3.setLayout(new FlowLayout(FlowLayout.RIGHT));

/////////////////////////////////////////////////
// Added for WotC compliance
//		jPanel3.setBorder(new BevelBorder(BevelBorder.LOWERED));
/////////////////////////////////////////////////
		okButton.setText("OK");
		okButton.setToolTipText("Accept these values");
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				okButtonActionPerformed(evt);
			}
		});

		jPanel3.add(okButton);

/////////////////////////////////////////////////
// Yanked for WotC compliance
//		resetButton.setText(s_TITLE);		// An attempt to make the popup wide enough to get the entire title shown (gets hidden after pack())
		resetButton.setText("Reset");
		resetButton.setToolTipText("Reset to saved values");
		resetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				resetButtonActionPerformed(evt);
			}
		});

		jPanel3.add(resetButton);
//
//		defaultButton.setText("Default");
//		defaultButton.setToolTipText("Set to default values");
//		defaultButton.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent evt)
//			{
//				defaultButtonActionPerformed(evt);
//			}
//		});
//
//		jPanel3.add(defaultButton);
//
		cancelButton.setText("Cancel");
		cancelButton.setToolTipText("Cancel Purchase Mode Configuration");
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				cancelButtonActionPerformed(evt);
			}
		});

		jPanel3.add(cancelButton);
/////////////////////////////////////////////////

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(jPanel3, gridBagConstraints);

		jScrollPane1.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
		jScrollPane1.setPreferredSize(new Dimension(100, 200));
		abilityScoreCostTable.setBorder(new BevelBorder(BevelBorder.LOWERED));
		purchaseModel = new PurchaseModel();
		abilityScoreCostTable.setModel(purchaseModel);
		abilityScoreCostTable.setToolTipText("Set the cost for each ability score");
		jScrollPane1.setViewportView(abilityScoreCostTable);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		getContentPane().add(jScrollPane1, gridBagConstraints);

		pack();

		initializeCurrentPurchaseMethods();
	}

	private void initializeCurrentPurchaseMethods()
	{
		//
		// Set up the current methods combo's contents
		//
		final int methodCount = SettingsHandler.getPurchaseMethodCount();
		if (methodCount > 0)
		{
			final PointBuyMethod methods[] = new PointBuyMethod[methodCount];
			for (int i = 0; i < methodCount; ++i)
			{
				methods[i] = SettingsHandler.getPurhaseMethod(i);
			}
			currentPurchaseMethods.setModel(new DefaultComboBoxModel(methods));
		}
		currentPurchaseMethodsActionPerformed();		// Get into correct state
	}

	private void purchaseScoreMinDecreaseButtonActionPerformed(ActionEvent evt)
	{

		int oldValue = purchaseModel.getPurchaseScoreMin();

		// get the current value from the edit field
		String valueString = purchaseScoreMinEdit.getText();

		// convert it to an integer
		int value = convertStringToInt(valueString);

		// bad value?
		if (value == -1)
		{

			// set a status message
			statusBar.setText("Bad value for purchase minimum score, fixing...");

		}
		else
		{

			// decrease the value in the model
			if (!purchaseModel.setPurchaseScoreMin(value - 1))
			{

				// set a status message
				statusBar.setText("Purchase Score Minimum value is 0!");

			}

		}

		// ensure the edit value gets updated correctly
		updatePurchaseScoreMin(oldValue);
	}

	private void purchaseScoreMinIncreaseButtonActionPerformed(ActionEvent evt)
	{

		int oldValue = purchaseModel.getPurchaseScoreMin();

		// get the current value from the edit field
		String valueString = purchaseScoreMinEdit.getText();

		// convert it to an integer
		int value = convertStringToInt(valueString);

		// bad value?
		if (value == -1)
		{

			// set a status message
			statusBar.setText("Bad value for purchase minimum score, fixing...");

		}
		else
		{

			// increase the value in the model
			if (!purchaseModel.setPurchaseScoreMin(value + 1))
			{

				// set a status message
				statusBar.setText("Purchase Score Minimum value can not exceed Purchase Score Maximum Value!");

			}

		}

		// ensure the edit value gets updated correctly
		updatePurchaseScoreMin(oldValue);
	}

	private void purchaseScoreMaxDecreaseButtonActionPerformed(ActionEvent evt)
	{

		int oldValue = purchaseModel.getPurchaseScoreMax();

		// get the current value from the edit field
		String valueString = purchaseScoreMaxEdit.getText();

		// convert it to an integer
		int value = convertStringToInt(valueString);

		// bad value?
		if (value == -1)
		{

			// set a status message
			statusBar.setText("Bad value for purchase maximum score, fixing...");

		}
		else
		{

			// decrease the value in the model
			if (!purchaseModel.setPurchaseScoreMax(value - 1))
			{

				// set a status message
				statusBar.setText("Purchase Score Maximum value can no go below Purchase Score Minimum!");

			}

		}

		// ensure the edit value gets updated correctly
		updatePurchaseScoreMax(oldValue);
	}

	private void purchaseScoreMaxIncreaseButtonActionPerformed(ActionEvent evt)
	{

		int oldValue = purchaseModel.getPurchaseScoreMax();

		// get the current value from the edit field
		String valueString = purchaseScoreMaxEdit.getText();

		// convert it to an integer
		int value = convertStringToInt(valueString);

		// bad value?
		if (value == -1)
		{

			// set a status message
			statusBar.setText("Bad value for purchase maximum score, fixing...");

		}
		else
		{

			// increase the value in the model
			purchaseModel.setPurchaseScoreMax(value + 1);

		}

		// ensure the edit value gets updated correctly
		updatePurchaseScoreMax(oldValue);
	}

	private void cancelButtonActionPerformed(ActionEvent evt)
	{
		this.dispose();
	}

	private void okButtonActionPerformed(ActionEvent evt)
	{
		purchaseModel.keepNewValues();
		this.dispose();
	}

	private void resetButtonActionPerformed(ActionEvent evt)
	{
		purchaseModel.copySavedToCurrent();
		updatePurchaseScoreMin(-1);
		updatePurchaseScoreMax(-1);
		purchaseModel.fireTableStructureChanged();

		initializeCurrentPurchaseMethods();
	}

/////////////////////////////////////////////////
// Yanked for WotC compliance
//	private void defaultButtonActionPerformed(ActionEvent evt)
//	{
//		purchaseModel.copyDefaultToCurrent();
//		updatePurchaseScoreMin(-1);
//		updatePurchaseScoreMax(-1);
//		purchaseModel.fireTableStructureChanged();
//	}
/////////////////////////////////////////////////

	/** Exit Purchase Mode Frame */
	private void exitForm(WindowEvent evt)
	{
		// TODO
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		new PurchaseModeFrame().show();
	}

	private int convertStringToInt(java.lang.String valueString)
	{
		int value = -1;

		try
		{
			value = Integer.parseInt(valueString);
		}
		catch (NumberFormatException nfe)
		{
			// bad value
			value = -1;
		}

		return value;

	}

	private void updatePurchaseScoreMin(int oldValue)
	{
		int score = purchaseModel.getPurchaseScoreMin();
		purchaseScoreMinEdit.setText(Integer.toString(score));

		if (oldValue != -1 && oldValue != score)
		{
			purchaseModel.prependRows(score - oldValue);
			purchaseModel.fireTableStructureChanged();
		}

	}

	private void updatePurchaseScoreMax(int oldValue)
	{
		int score = purchaseModel.getPurchaseScoreMax();
		purchaseScoreMaxEdit.setText(Integer.toString(score));

		if (oldValue != -1 && oldValue != score)
		{
			purchaseModel.appendRows(score - oldValue);
			purchaseModel.fireTableStructureChanged();
		}

	}

	private class PurchaseModel extends AbstractTableModel
	{
/////////////////////////////////////////////////
// Yanked for WotC compliance
//		private Object[][] defaultValues = new Object[11][2];
/////////////////////////////////////////////////

		private String[] columnHeaders = new String[]
		{
			"Ability Score", "Cost"
		};

		private Class[] types = new Class[]
		{
			java.lang.Integer.class, java.lang.Integer.class
		};

		private boolean[] canEdit = new boolean[]
		{
			false, true
		};

		private Object[][] currentValues = null;

		private Object[][] savedValues = null;

		private int savedPurchaseScoreMin = 0;
		private int savedPurchaseScoreMax = 0;

		private int currentPurchaseScoreMin = 10;		// Start at the average stat
		private int currentPurchaseScoreMax = 10;

/////////////////////////////////////////////////
// Yanked for WotC compliance
//		private int defaultPurchaseScoreMin = 8;
//		private int defaultPurchaseScoreMax = 18;
/////////////////////////////////////////////////

		PurchaseModel()
		{
			super();

			// initialise the saved values
			initValues();

			// copy the saved values to the current values
			copySavedToCurrent();

		}

		public String getColumnName(int param)
		{
			return columnHeaders[param];
		}

		public Class getColumnClass(int columnIndex)
		{
			return types[columnIndex];
		}

		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return canEdit[columnIndex];
		}

		public int getColumnCount()
		{
			return columnHeaders.length;
		}

		public int getRowCount()
		{
			return currentValues.length;
		}

		public Object getValueAt(int row, int column)
		{

			if ((row < 0) || (row >= currentValues.length))
			{
				throw new ArrayIndexOutOfBoundsException("Row index out of bounds: " + row);
			}

			if ((column == 0) || (column == 1))
			{
				return currentValues[row][column];
			}
			else
			{
				throw new ArrayIndexOutOfBoundsException("Column index out of bounds: " + column);
			}

		}

		public void setValueAt(Object obj, int row, int column)
		{

			if ((row < 0) || (row >= currentValues.length))
			{
				throw new ArrayIndexOutOfBoundsException("Row index out of bounds: " + row);
			}

			if ((column == 0) || (column == 1))
			{
				currentValues[row][column] = obj;
				fireTableCellUpdated(row, column);
			}
			else
			{
				throw new ArrayIndexOutOfBoundsException("Column index out of bounds: " + column);
			}

		}

		public void initValues()
		{

/////////////////////////////////////////////////
// Yanked for WotC compliance
//			for (int score = 8; score <= 18; ++score)
//			{
//				defaultValues[score - 8][0] = new Integer(score);
//				defaultValues[score - 8][1] = new Integer(getStandardScoreCost(score));
//			}
/////////////////////////////////////////////////

			// get the ability score costs from settings
			int[] scoreCosts = SettingsHandler.getAbilityScoreCost();
			if (scoreCosts != null)
			{
				// get the save values from the settings
				savedPurchaseScoreMin = SettingsHandler.getPurchaseScoreMin();
				savedPurchaseScoreMax = SettingsHandler.getPurchaseScoreMax();

				savedValues = new Object[scoreCosts.length][2];

				for (int i = savedPurchaseScoreMin, index = 0; i <= savedPurchaseScoreMax; ++i)
				{
					index = i - savedPurchaseScoreMin;
					savedValues[index][0] = new Integer(i);
					savedValues[index][1] = new Integer(scoreCosts[index]);
				}
			}
			else
			{
				savedPurchaseScoreMin = 10;
				savedPurchaseScoreMax = 10;

				scoreCosts = new int[1];
				scoreCosts[0] = 0;

				savedValues = new Object[1][2];
				savedValues[0][0] = new Integer(10);
				savedValues[0][1] = new Integer(0);
			}

			//
			// Make sure the min/max buttons have the correct info
			//
			purchaseScoreMinEdit.setText(Integer.toString(savedPurchaseScoreMin));
			purchaseScoreMaxEdit.setText(Integer.toString(savedPurchaseScoreMax));

		}

		public void copySavedToCurrent()
		{
			if (savedValues != null)
			{
				currentPurchaseScoreMin = savedPurchaseScoreMin;
				currentPurchaseScoreMax = savedPurchaseScoreMax;

				final int nrEntries = currentPurchaseScoreMax - currentPurchaseScoreMin + 1;

				currentValues = new Object[nrEntries][2];

				for (int i = 0; i < nrEntries; ++i)
				{
					currentValues[i][0] = savedValues[i][0];
					currentValues[i][1] = savedValues[i][1];
				}
			}
		}

/////////////////////////////////////////////////
// Yanked for WotC compliance
//		void copyDefaultToCurrent()
//		{
//
//			currentPurchaseScoreMin = defaultPurchaseScoreMin;
//			currentPurchaseScoreMax = defaultPurchaseScoreMax;
//
//			int nrEntries = currentPurchaseScoreMax - currentPurchaseScoreMin + 1;
//
//			currentValues = new Object[nrEntries][2];
//
//			for (int i = 0; i < nrEntries; ++i)
//			{
//
//				currentValues[i][0] = defaultValues[i][0];
//				currentValues[i][1] = defaultValues[i][1];
//
//			}
//
//		}
/////////////////////////////////////////////////

		void keepNewValues()
		{
			// set the current values into the settings
			SettingsHandler.clearPointBuyStatCosts();
			for (int i = currentPurchaseScoreMin; i <= currentPurchaseScoreMax; ++i)
			{
				SettingsHandler.addPointBuyStatCost(i, ((Integer) currentValues[i - currentPurchaseScoreMin][1]).intValue());
			}

			SettingsHandler.clearPurchaseModeMethods();
			for (int i = 0, x = currentPurchaseMethods.getItemCount(); i < x; ++i)
			{
				final PointBuyMethod pbm = (PointBuyMethod) currentPurchaseMethods.getItemAt(i);
				SettingsHandler.addPurchaseModeMethod(pbm.getMethodName(), pbm.getPoints());
			}
			//
			// Modify the menu to reflect any changes
			//
			//Options.initializePurchaseModeMethods();
			//Options.checkForSelectedStatMode();
		}

/////////////////////////////////////////////////
// Yanked for WotC compliance
//		public int getStandardScoreCost(int score)
//		{
//			// Extrapolate from DMG
//			int costs[] = {-10, -8, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 8, 10, 13, 16, 20, 24, 29, 34, 40, 46, 53, 60};
//			return ((score >= costs.length || score < 0) ? 0 : costs[score]);
//		}
/////////////////////////////////////////////////

		/** Getter for property purchaseScoreMin.
		 * @return Value of property purchaseScoreMin.
		 */
		int getPurchaseScoreMin()
		{
			return currentPurchaseScoreMin;
		}

		/** Setter for property purchaseScoreMin.
		 * @param purchaseScoreMin New value of property purchaseScoreMin.
		 */
		boolean setPurchaseScoreMin(int purchaseScoreMin)
		{
			if (purchaseScoreMin >= 0 && purchaseScoreMin <= currentPurchaseScoreMax)
			{
				currentPurchaseScoreMin = purchaseScoreMin;
				return true;
			}
			return false;
		}

		/** Getter for property purchaseScoreMax.
		 * @return Value of property purchaseScoreMax.
		 */
		int getPurchaseScoreMax()
		{
			return currentPurchaseScoreMax;
		}

		/** Setter for property purchaseScoreMax.
		 * @param purchaseScoreMax New value of property purchaseScoreMax.
		 */
		boolean setPurchaseScoreMax(int purchaseScoreMax)
		{
			if (purchaseScoreMax >= 0 && purchaseScoreMax >= currentPurchaseScoreMin)
			{
				currentPurchaseScoreMax = purchaseScoreMax;
				return true;
			}
			return false;
		}

		void prependRows(int nrRows)
		{
			final int nrEntries = currentPurchaseScoreMax - currentPurchaseScoreMin + 1;

			Object[][] newValues = new Object[nrEntries][2];

			if (nrRows > 0)
			{
				// removing rows
				System.arraycopy(currentValues, nrRows, newValues, 0, nrEntries);
			}
			else
			{
				// adding rows
				nrRows = Math.abs(nrRows);
				System.arraycopy(currentValues, 0, newValues, nrRows, currentValues.length);
				final int tblStart = ((Integer) currentValues[0][1]).intValue();

				for (int i = 0; i < nrRows; ++i)
				{
					final int score = i + currentPurchaseScoreMin;
					newValues[i][0] = new Integer(score);
					newValues[i][1] = new Integer(tblStart - nrRows + i);
					//newValues[i][1] = new Integer(getStandardScoreCost(score));
				}
			}

			currentValues = newValues;
		}

		void appendRows(int nrRows)
		{
			final int nrEntries = currentPurchaseScoreMax - currentPurchaseScoreMin + 1;

			Object[][] newValues = new Object[nrEntries][2];

			if (nrRows < 0)
			{
				// removing rows
				System.arraycopy(currentValues, 0, newValues, 0, nrEntries);
			}
			else
			{
				// adding rows
				System.arraycopy(currentValues, 0, newValues, 0, currentValues.length);

				final int preLength = currentValues.length;
				for (int i = 0; i < nrRows; ++i)
				{
					final int score = i + currentPurchaseScoreMax - nrRows + 1;
					int preVal = -1;
					newValues[i + preLength][0] = new Integer(score);
					if ((i + preLength) != 0)
					{
						preVal = ((Integer) newValues[i + preLength - 1][1]).intValue();
					}
					newValues[i + preLength][1] = new Integer(preVal + 1);
				}
			}

			currentValues = newValues;
		}
	}

	//
	// Pop up a window to get information about a new purchase method
	//
	public void addMethodButtonActionPerformed(ActionEvent evt)
	{
		NewPurchaseMethodDialog npmd = new NewPurchaseMethodDialog(this, true);
		npmd.show();
		if (!npmd.getWasCancelled())
		{
			final String methodName = npmd.getEnteredName();
			if (SettingsHandler.getPurchaseMethodByName(methodName) == null)
			{
				PointBuyMethod pbm = new PointBuyMethod(methodName, npmd.getEnteredPoints());
				currentPurchaseMethods.addItem(pbm);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Cannot add method. Name already exists.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	//
	// Remove the current selection from the list of purchase methods
	//
	public void removeMethodButtonActionPerformed(ActionEvent evt)
	{
		final PointBuyMethod method = (PointBuyMethod) currentPurchaseMethods.getSelectedItem();
		if (method != null)
		{
			currentPurchaseMethods.removeItem(method);
		}
	}

	//
	// Display info about the selected purchase method
	//
	public void currentPurchaseMethodsActionPerformed()
	{
		final PointBuyMethod method = (PointBuyMethod) currentPurchaseMethods.getSelectedItem();
		if (method == null)
		{
			removeMethodButton.setEnabled(false);
			purchaseMethodPointsEdit.setText("");
		}
		else
		{
			purchaseMethodPointsEdit.setText(Integer.toString(method.getPoints()));
			removeMethodButton.setEnabled(true);
		}
	}
}
