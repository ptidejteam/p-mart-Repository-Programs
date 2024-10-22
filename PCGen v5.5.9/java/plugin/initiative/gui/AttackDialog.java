/*
 *  AttackDialog -- for GMGen
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 * AttackDialog.java
 *
 * Created on Oct 8, 2003
 */
package plugin.initiative.gui;

import com.electronicmuse.djep.JEP;
import com.electronicmuse.djep.function.Roll;
import gmgen.GMGenSystem;
import gmgen.plugin.PcgCombatant;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatter;
import plugin.initiative.AttackModel;

/**
 * @author Ross M. Lodge
 *
 * <p>This class resolves an attack as described by AttackModel.</p>
 * <p>The dialog presents a table which holds the details of the attacks.  Certain cells
 * are editable (notably fudge bonus, range value, hit/crit checkboxes, damage dice).</p>
 * <p>User can enter an armor class and roll attacks.  If a vector of combatants is passed the
 * dialog displays a <code>JComboBox</code> which displays attackable combatants.  Changing
 * selections changes the AC value and re-calculates the attack rolls.</p>
 *
 */
public class AttackDialog extends JDialog
{
	/**
	 * @author Ross M. Lodge
	 *
	 * <p>A table model for the dialog.  Defines columns, column data types, editable values, etc.</p>
	 * <p>Provides methods for rolling attacks and re-calculating data values.</p>
	 */
	public class AttackTableModel extends DefaultTableModel
	{
		/** Key strings for accessing column data.  (Allows column names to be loaded from resource files
		 * if necessary)
		 */
		static final String COLUMN_KEY_BONUS = "BONUS";
		static final String COLUMN_KEY_FUDGE = "FUDGE";
		static final String COLUMN_KEY_INCREMENT = "INCREMENT";
		static final String COLUMN_KEY_RANGE = "RANGE";
		static final String COLUMN_KEY_ROLL = "ROLL";
		static final String COLUMN_KEY_TOTAL = "TOTAL";
		static final String COLUMN_KEY_CRITROLL = "CRITROLL";
		static final String COLUMN_KEY_CRITTOTAL = "CRITTOTAL";
		static final String COLUMN_KEY_HIT = "HIT";
		static final String COLUMN_KEY_CRIT = "CRIT";
		static final String COLUMN_KEY_DMG = "DMG";
		static final String COLUMN_KEY_DMGTOT = "DMGTOT";
		static final String COLUMN_KEY_TARGET = "TARGET";
		static final String COLUMN_KEY_AC = "ARMORCLASS";

		/**
		 * Integers for use when accessing values in the columns array
		 */
		static final int COLUMN_INDEX_NAME = 0;
		static final int COLUMN_INDEX_CLASS = 1;
		static final int COLUMN_INDEX_DEFAULT = 2;
		static final int COLUMN_INDEX_EDITABLE = 3;
		static final int COLUMN_INDEX_KEY = 4;

		/**
		 * Provides a way to access column data without repetitive hard-coding.  Probably not
		 * efficient but saves me a lot of typing.  Use fields COLUMN_INDEX_XYZ as the second index.
		 * You can use columnFromKey(COLUMN_KEY_XYZ) to get the first (column number) index.
		 * This array is used to initialize the names of the columns and for returning values from
		 * <code>getColumClass</code> and <code>isCellEditable</code>.
		 */
		private Object[][] columns = {
			{"Bonus",Integer.class,null,new Boolean(false),COLUMN_KEY_BONUS}
			,{"Fudge",Integer.class,new Integer(0),new Boolean(true),COLUMN_KEY_FUDGE}
			,{"Increment",Integer.class,new Integer(m_attack.getRangeAsInt()),new Boolean(false),COLUMN_KEY_INCREMENT}
			,{"Range",Integer.class,null,new Boolean(true),COLUMN_KEY_RANGE}
			,{"Roll",Integer.class,null,new Boolean(false),COLUMN_KEY_ROLL}
			,{"Total",Integer.class,null,new Boolean(false),COLUMN_KEY_TOTAL}
			,{"Target",PcgCombatant.class,null,new Boolean(true),COLUMN_KEY_TARGET}
			,{"AC",Integer.class,null,new Boolean(true),COLUMN_KEY_AC}
			,{"Crit Roll",Integer.class,null,new Boolean(false),COLUMN_KEY_CRITROLL}
			,{"Crit Total",Integer.class,null,new Boolean(false),COLUMN_KEY_CRITTOTAL}
			,{"Hit",Boolean.class,new Boolean(false),new Boolean(true),COLUMN_KEY_HIT}
			,{"Crit",Boolean.class,new Boolean(false),new Boolean(true),COLUMN_KEY_CRIT}
			,{"Dmg",String.class,null,new Boolean(true),COLUMN_KEY_DMG}
			,{"Dmg Tot",Integer.class,null,new Boolean(true),COLUMN_KEY_DMGTOT}
		};

		/**
		 * dJEP instance for rolling dice.
		 */
		private JEP m_jep = new JEP();

		/** AC Type string */
		private String m_acType = "Total";

		/**
		 * Constructor.  Builds values based on the enclosing class's m_attack member.
		 */
		public AttackTableModel()
		{
			super();
			int[] attacks = m_attack.getBonusList();
			Vector values = new Vector(columns.length);
			values.setSize(values.capacity());
			for (int i = 0; i < columns.length; i++)
			{
				addColumn(columns[i][COLUMN_INDEX_NAME]);
				values.add(i, columns[i][COLUMN_INDEX_DEFAULT]);
			}
			for (int i = 0; i < attacks.length; i++)
			{
				values.set(columnFromKey(COLUMN_KEY_BONUS),new Integer(attacks[i]));
				values.set(columnFromKey(COLUMN_KEY_DMG),m_attack.getDamage(i));
				addRow((Vector)values.clone());
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		public Class getColumnClass(int columnIndex)
		{
			return (Class)columns[columnIndex][COLUMN_INDEX_CLASS];
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int column)
		{
			return ((Boolean)columns[column][COLUMN_INDEX_EDITABLE]).booleanValue();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 *
		 * In addition to setting the value this method also calls recalcRow.
		 */
		public void setValueAt(Object aValue, int row, int column)
		{
			super.setValueAt(aValue, row, column);
			recalcRow(row, column);
		}

		/**
		 * Recalculates the data values on the requested row, based on changes to the
		 * column index supplied.  Be aware that this does itself call <code>setValueAt</code>,
		 * so care must be taken to avoid an infinite recursive call.  The <code>if</code> blocks
		 * should all make sure they don't set columns that they in turn react to.
		 *
		 * @param row Row that has changed.
		 * @param column Column that has changed
		 */
		private void recalcRow(int row, int column)
		{
			//COLUMN_KEY_TOTAL
			int attTotal = 0;
			int attackModifier =
				getIntAt(row, columnFromKey(COLUMN_KEY_BONUS))
					+ getIntAt(row, columnFromKey(COLUMN_KEY_FUDGE));
			if (getIntAt(row, columnFromKey(COLUMN_KEY_INCREMENT)) > 0
				&& getIntAt(row, columnFromKey(COLUMN_KEY_RANGE)) > 0)
			{
				attackModifier
					+= ((int) Math
						.floor(
							(double) getIntAt(row,
								columnFromKey(COLUMN_KEY_RANGE))
								/ (double) getIntAt(row,
									columnFromKey(COLUMN_KEY_INCREMENT)))
						*
						- 2);
			}
			int result = getIntAt(row, columnFromKey(COLUMN_KEY_ROLL));
			if (result == 1 || result == 0)
			{
				attTotal = result;
			}
			else
			{
				attTotal = result + attackModifier;
			}
			//COLUMN_KEY_TOTAL
			if (column == columnFromKey(COLUMN_KEY_BONUS)
				|| column == columnFromKey(COLUMN_KEY_FUDGE)
				|| column == columnFromKey(COLUMN_KEY_INCREMENT)
				|| column == columnFromKey(COLUMN_KEY_RANGE)
				|| column == columnFromKey(COLUMN_KEY_ROLL))
			{
				setValueAt(new Integer(attTotal), row, columnFromKey(COLUMN_KEY_TOTAL));
			}
			//COLUMN_KEY_AC
			if (column == columnFromKey(COLUMN_KEY_TARGET))
			{
				if (getValueAt(row, columnFromKey(COLUMN_KEY_TARGET)) instanceof PcgCombatant)
				{
					setValueAt(new Integer(((PcgCombatant)getValueAt(row, columnFromKey(COLUMN_KEY_TARGET))).getPC().calcACOfType(m_acType)),row,columnFromKey(COLUMN_KEY_AC));
				}
			}
			//COLUMN_KEY_HIT
			if (column == columnFromKey(COLUMN_KEY_TOTAL)
				|| column == columnFromKey(COLUMN_KEY_AC))
			{
				if (attTotal >= getIntAt(row, columnFromKey(COLUMN_KEY_AC)) || getIntAt(row, columnFromKey(COLUMN_KEY_ROLL)) >= 20)
				{
					setValueAt(new Boolean(true),row,columnFromKey(COLUMN_KEY_HIT));
				}
				else
				{
					setValueAt(new Boolean(false),row,columnFromKey(COLUMN_KEY_HIT));
				}
			}
			//COLUMN_KEY_BONUS
			//COLUMN_KEY_FUDGE
			//COLUMN_KEY_INCREMENT
			//COLUMN_KEY_RANGE
			//COLUMN_KEY_ROLL
			//COLUMN_KEY_TOTAL
			//COLUMN_KEY_CRITROLL
			//COLUMN_KEY_CRITTOTAL
			if (column == columnFromKey(COLUMN_KEY_CRITROLL)
				|| column == columnFromKey(COLUMN_KEY_AC))
			{
				int critTotal = 0;
				if (getIntAt(row, columnFromKey(COLUMN_KEY_CRITROLL)) > 1)
				{
					critTotal = getIntAt(row, columnFromKey(COLUMN_KEY_CRITROLL)) + attackModifier;
					setValueAt(new Integer(critTotal),row,columnFromKey(COLUMN_KEY_CRITTOTAL));
				}
				else
				{
					setValueAt(null,row,columnFromKey(COLUMN_KEY_CRITTOTAL));
				}
				//COLUMN_KEY_CRIT
				if (critTotal > getIntAt(row, columnFromKey(COLUMN_KEY_AC)) || getIntAt(row, columnFromKey(COLUMN_KEY_CRITROLL)) >= 20)
				{
					setValueAt(new Boolean(true),row,columnFromKey(COLUMN_KEY_CRIT));
				}
				else
				{
					setValueAt(new Boolean(false),row,columnFromKey(COLUMN_KEY_CRIT));
				}
			}
			//COLUMN_KEY_DMG
			//COLUMN_KEY_DMGTOT
			if (column == columnFromKey(COLUMN_KEY_HIT)
				||column == columnFromKey(COLUMN_KEY_CRIT))
			{
				if (((Boolean)getValueAt(row,columnFromKey(COLUMN_KEY_HIT))).booleanValue())
				{
					int numberOfRolls = 1;
					String damageString = (String)getValueAt(row,columnFromKey(COLUMN_KEY_DMG));
					if (damageString.indexOf("/") >= 0)
					{
						StringTokenizer tok = new StringTokenizer(damageString,"/");
						String[] heads = new String[tok.countTokens()];
						for (int i = 0; tok.hasMoreTokens(); i++)
						{
							heads[i] = tok.nextToken();
						}
						damageString =
							(String) JOptionPane.showInputDialog(
								AttackDialog.this,
								"This weapon appears to have more than one possible damage " +
									"die listed.  Please choose one:",
								"Multiple Damage Dice",
								JOptionPane.QUESTION_MESSAGE,
								null,
								heads,
								heads[1]);
						setValueAt(damageString, row, columnFromKey(COLUMN_KEY_DMG));
					}
					if (((Boolean)getValueAt(row,columnFromKey(COLUMN_KEY_CRIT))).booleanValue())
					{
						numberOfRolls = Integer.parseInt(m_attack.getCritMultiple(row));
					}
					int dmg = 0;
					for (int i = 0; i < numberOfRolls; i++)
					{
						dmg += rollDice(damageString);
					}
					setValueAt(new Integer(dmg),row,columnFromKey(COLUMN_KEY_DMGTOT));
				}
				else
				{
					setValueAt(null,row,columnFromKey(COLUMN_KEY_DMGTOT));
				}
			}
		}

		/**
		 * Iterates through all rows in the table and calls <code>rollAttack(row)</code>.
		 * Sets <code>m_armorClass</code> to the passed value.
		 *
		 * @param ac The armor class to roll against
		 */
		public void rollAttacks()
		{
			for (int i = 0; i < getRowCount(); i++)
			{
				rollAttack(i);
			}
		}

		/**
		 * Calculates to-hit and damage rolls for the requested row, rolls critical
		 * if necessary.
		 *
		 * @param row The row to roll an attack on.
		 * @param ac The armor class to roll against
		 */
		private void rollAttack(int row)
		{
			setValueAt(new Integer(rollDice("1d20")), row, columnFromKey(COLUMN_KEY_ROLL));
			if (getIntAt(row, columnFromKey(COLUMN_KEY_ROLL)) >= m_attack.getCritRangeMin(row)
				&& ((Boolean)getValueAt(row,columnFromKey(COLUMN_KEY_HIT))).booleanValue())
			{
				setValueAt(new Integer(rollDice("1d20")), row, columnFromKey(COLUMN_KEY_CRITROLL));
			}
			else
			{
				setValueAt(null, row, columnFromKey(COLUMN_KEY_CRITROLL));
			}
		}

		/**
		 * Shortcut to get the int value for a column with an Integer data type.
		 *
		 * @param row The row to request a value for
		 * @param column The column to request a value for
		 * @return The integer value at the specified column, or 0 if class of that column not Integer
		 */
		private int getIntAt(int row, int column)
		{
			int returnValue = 0;
			if (getValueAt(row,column) != null && getValueAt(row, column) instanceof Integer)
			{
				returnValue = ((Integer)getValueAt(row,column)).intValue();
			}
			return returnValue;
		}

		/**
		 * Shortcut to get the index of a column based on the string
		 * key value.
		 *
		 * @param key The key string (a COLUMN_KEY_XYZ value)
		 * @return The integer index of the column
		 */
		private int columnFromKey(String key)
		{
			int returnValue = -1;
			for (int i = 0; i < columns.length; i++)
			{
				if (columns[i][COLUMN_INDEX_KEY].equals(key))
				{
					returnValue = i;
					break;
				}
			}
			return returnValue;
		}

		/**
		 * Sets the armor class and recalculates the rows in the model
		 * without re-rolling the attacks.
		 *
		 * @param ac Armor class
		 */
		public void setArmorClass(int ac)
		{
			for (int i = 0; i < getRowCount(); i++)
			{
				setValueAt(new Integer(ac),i,columnFromKey(COLUMN_KEY_AC));
			}
		}

		/**
		 * Sets the target and recalculates the rows in the model
		 * without re-rolling the attacks.
		 *
		 * @param target Target
		 */
		public void setTarget(PcgCombatant target)
		{
			for (int i = 0; i < getRowCount(); i++)
			{
				setValueAt(target,i,columnFromKey(COLUMN_KEY_TARGET));
			}
		}

		/**
		 * <p>Executes the requested dice expression and returns the integer value.</p>
		 *
		 * @param expression A dice expression
		 * @return The integer result
		 */
		private int rollDice(String expression)
		{
			boolean logging = Roll.isLogging();
			Roll.setLogging(false);
			m_jep.parseExpression(expression);
			int returnValue = (int)m_jep.getValue();
			Roll.setLogging(logging);
			return returnValue;
		}

		/**
		 * @param string
		 */
		public void setAcType(String string)
		{
			m_acType = string;
			for (int row = 0; row < getRowCount(); row++)
			{
				recalcRow(row, columnFromKey(COLUMN_KEY_TARGET));
			}
		}

	}

	/** <p><code>boolean</code>; whether or not damage is subdual.</p> */
	private boolean m_subdual;
	/** <p><code>JComboBox</code> for combatants.</p> */
	private JComboBox m_targetsCombo;
	/** <p><code>JComboBox</code> for Armor class types.</p> */
	private JComboBox m_acTypeCombo;
	/**
	 * Cell editor for target column
	 */
	private JComboBox m_targets = null;
	/** <p>Total damage for all successful attacks.</p> */
	private int m_totalDmg;
	/** <p>Label to hold the total damage information.</p> */
	private JLabel m_totalDamageLabel;
	/** <p>Text field for the armor class</p> */
	private JFormattedTextField m_field;
	/** <p>This dialog's attack model; that is, the attack object this dialog will resolve.</p> */
	private AttackModel m_attack = null;
	/** <p>The dialog's <code>JTable</code>; holds all attack information for display</p> */
	private JTable m_table = null;
	/** <p>Instance of <code>AttackTableModel</code>.</p> */
	private AttackTableModel m_tableModel = null;

	/** <p>Vector of combatants that are valid targets.</p> */
	private Vector m_combatants = null;
	/** <p>List of resulting damage values, one for each successful attack.</p> */
	private ArrayList m_damageList = null;
	/**
	 * <p>List of targets, one for each successful attack.  Each one matches a damage value in
	 * m_damagelist</p>
	 */
	private ArrayList m_targetList = null;

	/**
	 * <p>Initializes the dialog with the specified model.</p>
	 *
	 * @param model Attack model for this dlg.
	 */
	public AttackDialog(AttackModel model)
	{
		super(GMGenSystem.inst);
		m_attack = model;
		initComponents();
	}

	/**
	 * <p>Initializes the dialog with the specified model and the specified
	 * list of valid targets.  This class ignores combatants that are not
	 * instances of <code>PcgCombatant</code>.  A null or empty vector
	 * will cause the dialgo to display as if <code>AttackDialog(AttackModel model)</code>
	 * had been called.</p>
	 *
	 * @param model Attack model for this dlg.
	 */
	public AttackDialog(AttackModel model, Vector combatants)
	{
		super(GMGenSystem.inst);
		m_attack = model;
		if (combatants != null)
		{
			m_combatants = (Vector)combatants.clone();
		}
		initComponents();
	}

	/**
	 * <p>Initiaizes the dialog components.</p>
	 */
	private void initComponents()
	{
		setTitle("Attack: " + m_attack.toString());
		//Set Layout
		getContentPane().setLayout(new BorderLayout());
		//Build the center panel with JTable and scroll pane
		int[] attacks = m_attack.getBonusList();
		JPanel center = new JPanel(new BorderLayout());
		getContentPane().add(center, BorderLayout.CENTER);
		//This column model auto-sizes the columns based on contents.
		AutoSizingColumnModel columns = new AutoSizingColumnModel();

		m_tableModel = new AttackTableModel();
		m_tableModel.addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				handleTableUpdate(e);
			}
		});
		m_table = new JTable(m_tableModel,columns);

		columns.referenceTable(m_table);
		m_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		m_table.setAutoCreateColumnsFromModel(true);
		m_table.setPreferredScrollableViewportSize(
			new Dimension(
				columns.getTotalPreferredWidth(),
				m_table.getRowHeight() * m_table.getRowCount()));

		center.add(new JScrollPane(m_table),BorderLayout.CENTER);
		//Build panel to contain buttons, controls at bottom
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom,BoxLayout.X_AXIS));


		bottom.add(Box.createRigidArea(new Dimension(10,0)));

		m_totalDamageLabel = new JLabel("<html>Total Damage: <b>0</b></html>");
		bottom.add(m_totalDamageLabel);

		bottom.add(Box.createRigidArea(new Dimension(10,0)));

		JCheckBox checkbox = new JCheckBox("Damage is subdual?");
		checkbox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				handleSubdualAction(e);
			}
		});

		bottom.add(checkbox);

		bottom.add(Box.createRigidArea(new Dimension(10,0)));

		JButton button = null;
		button = new JButton(new AbstractAction("Roll")
		{
			public void actionPerformed(ActionEvent e)
			{
				performRoll();
			}
		});
		bottom.add(button);
		bottom.add(Box.createRigidArea(new Dimension(10,0)));
		button = new JButton(new AbstractAction("Ok")
		{
			public void actionPerformed(ActionEvent e)
			{
				handleOk();
			}
		});
		bottom.add(button);
		bottom.add(Box.createRigidArea(new Dimension(10,0)));
		button = new JButton(new AbstractAction("Cancel")
		{
			public void actionPerformed(ActionEvent e)
			{
				handleCancel();
			}
		});
		bottom.add(button);
		getContentPane().add(bottom,BorderLayout.SOUTH);

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top,BoxLayout.X_AXIS));
		if (m_combatants != null && m_combatants.size() > 0)
		{
			m_targets = new JComboBox(m_combatants);
			m_table.setDefaultEditor(PcgCombatant.class,new DefaultCellEditor(m_targets));
			//If we have combatants, initialize the top panel and populate
			//the JComboBox
			m_targetsCombo = new JComboBox(m_combatants);
			m_targetsCombo.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					handleTargetAction(e);
				}
			});
			top.add(new JLabel("Attack Character: "));
			top.add(m_targetsCombo);
			m_acTypeCombo = new JComboBox();
			m_acTypeCombo.addItem("Total");
			m_acTypeCombo.addItem("Flatfooted");
			m_acTypeCombo.addItem("Touch");
			m_acTypeCombo.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					handleAcTypeAction(e);
				}
			});
			top.add(new JLabel("Use AC Type: "));
			top.add(m_acTypeCombo);
		}
		DefaultFormatter formatter = new DefaultFormatter();
		formatter.setValueClass(Integer.class);
		formatter.setCommitsOnValidEdit(true);
		m_field = new JFormattedTextField(formatter);
		m_field.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		m_field.setPreferredSize(new Dimension(40,m_field.getPreferredSize().height));
		m_field.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName() != null && evt.getPropertyName().equals("value"))
				{
					m_tableModel.setArmorClass(((Integer)m_field.getValue()).intValue());
				}
			}
		});
		top.add(new JLabel("AC"));
		top.add(m_field);
		m_field.setValue(new Integer(15));
		handleTargetAction(null);
		getContentPane().add(top, BorderLayout.NORTH);
		//Pack and locate the dialog
		pack();
		setLocationRelativeTo(GMGenSystem.inst);
	}

	/**
	 * <p>Handles actions on the subdual checkbox (saving data back to m_subdual).</p>
	 * @param e
	 * 		Event which fired this handler.
	 */
	protected void handleSubdualAction(ActionEvent e)
	{
		if (e.getSource() instanceof JCheckBox)
		{
			m_subdual = ((JCheckBox)e.getSource()).isSelected();
		}
	}

	/**
	 * <p>Hides the dialog and makes sure all damage/target lists are null.</p>
	 */
	protected void handleCancel()
	{
		m_damageList = null;
		m_targetList = null;
		hide();
	}

	/**
	 * Handles actions from <code>m_acTypeCombo</code>; changes how armor
	 * class is recalculated
	 *
	 * @param e Event which fired this handler
	 */
	protected void handleAcTypeAction(ActionEvent e)
	{
		m_field.setValue(new Integer(((PcgCombatant)m_targetsCombo.getSelectedItem()).getPC().calcACOfType(m_acTypeCombo.getSelectedItem().toString())));
		m_tableModel.setAcType(m_acTypeCombo.getSelectedItem().toString());
	}

	/**
	 * Handles actions from <code>m_targetsCombo</code>; sets chosen combatant
	 * and value of armor class.
	 *
	 * @param e Event which fired this handler
	 */
	protected void handleTargetAction(ActionEvent e)
	{
		if (m_targetsCombo != null && m_targetsCombo.getSelectedItem() != null && m_targetsCombo.getSelectedItem() instanceof PcgCombatant)
		{
			PcgCombatant combatant = (PcgCombatant)m_targetsCombo.getSelectedItem();
			m_field.setValue(new Integer(combatant.getPC().calcACOfType(m_acTypeCombo.getSelectedItem().toString())));
			m_tableModel.setTarget(combatant);
		}
	}

	/**
	 * Handles actions from the Ok button.  Sets the damage list and hides the dialog.
	 *
	 */
	protected void handleOk()
	{
		m_damageList = new ArrayList(m_tableModel.getRowCount());
		m_targetList = new ArrayList(m_tableModel.getRowCount());
		for (int i = 0; i < m_table.getRowCount(); i++)
		{
			int dmg = m_tableModel.getIntAt(i, m_tableModel.columnFromKey(AttackTableModel.COLUMN_KEY_DMGTOT));
			if (dmg > 0)
			{
				m_damageList.add(new Integer(dmg));
				Object target =
					m_tableModel.getValueAt(
						i,
						m_tableModel.columnFromKey(
							AttackTableModel.COLUMN_KEY_TARGET));
				if (target != null && target instanceof PcgCombatant)
				{
					m_targetList.add(target);
				}
			}
		}
		hide();
	}

	/**
	 * Handles table updates.  If damage column has been updated in any way, re-calculates
	 * total damage and resets text of the total damage label.
	 *
	 * @param e Model event
	 */
	protected void handleTableUpdate(TableModelEvent e)
	{
		int dmgColumn = m_tableModel.columnFromKey(AttackTableModel.COLUMN_KEY_DMGTOT);
		if (dmgColumn == e.getColumn()
			||e.getColumn() == TableModelEvent.ALL_COLUMNS
			||e.getType() == TableModelEvent.DELETE
			||e.getType() == TableModelEvent.INSERT)
		{
			m_totalDmg = 0;
			for (int i = 0; i < m_table.getRowCount(); i++)
			{
				m_totalDmg += m_tableModel.getIntAt(i, dmgColumn);
			}
			m_totalDamageLabel.setText("<html>Total Damage: <b>" + m_totalDmg + "</b></html>");
		}
	}

	/**
	 * Handles actions from the Roll button; calls table model's <code>rollAttacks</code>
	 * method with the value of <code>m_field</code>
	 */
	protected void performRoll()
	{
		m_tableModel.rollAttacks();
	}

	/**
	 * Returns the combatants for the successful attacks.
	 *
	 * @return Chosen combatant.
	 */
	public ArrayList getDamagedCombatants()
	{
		return m_targetList;
	}

	/**
	 * Returns the damage list (may be null or 0-length).
	 *
	 * @return Damage list
	 */
	public ArrayList getDamageList()
	{
		return m_damageList;
	}

	/**
	 * Returns the total damage (may be 0)
	 *
	 * @return Total damage
	 */
	public int getTotalDmg()
	{
		return m_totalDmg;
	}

	/**
	 * @return <code>true</code> if damage is subdual
	 */
	public boolean isSubdual()
	{
		return m_subdual;
	}

}
