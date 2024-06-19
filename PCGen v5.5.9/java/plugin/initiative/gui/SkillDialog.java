/*
 *  pcgen
 *  Copyright (C) 2003 Ross M. Lodge
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
 *  SkillDialog.java
 *
 *  Created on Nov 5, 2003, 3:37:59 PM
 */
package plugin.initiative.gui;

import com.electronicmuse.djep.JEP;
import com.electronicmuse.djep.function.Roll;
import gmgen.GMGenSystem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;
import plugin.initiative.SkillModel;

/**
 * <p>
 * This dialog class manages a skill roll.
 * </p>
 *
 * @author Ross M. Lodge
 */
public class SkillDialog extends JDialog
{
	/** The skill model for this dialog. */
	SkillModel m_model = null;
	/** Label to display the result of the check */
	private JLabel m_result;
	/** Formatted text field for the dc */
	private JFormattedTextField m_dc;
	/** Text field for the skill roll expression */
	private JTextField m_skillRoll;
	/** Button to exit the dialog */
	private JButton m_ok;
	/** Button to roll the skill checks */
	private JButton m_roll;
	/** dJEP instance to do the calculationss */
	private JEP m_jep = new JEP();

	/**
	 * <p>Construct a dialog for the specified roll.</p>
	 *
	 * @throws java.awt.HeadlessException
	 */
	public SkillDialog(SkillModel model) throws HeadlessException
	{
		super();
		m_model = model;
		m_jep.addStandardFunctions();
		initComponents();
	}

	/**
	 * <p>
	 * Initializes the dialog components, sizes and positions the dialog.
	 * </p>
	 */
	private void initComponents()
	{
		/*
		 * Dialog will consist of
		 *  Skill Roll: [                 ]
		 *  DC:         [      ]
		 *  Result:     [                 ]
		 *                  [ Roll ] [ Ok ]
		 */
		//Set basic properties
		setTitle("Skill: " + m_model);
		//Create fields
		if (m_model.getBonus() >= 0)
		{
			m_skillRoll = new JTextField("1d20+" + m_model.getBonus());
		}
		else
		{
			m_skillRoll = new JTextField("1d20" + m_model.getBonus());
		}
		NumberFormatter formatter = new NumberFormatter(new DecimalFormat("##"));
		formatter.setValueClass(Integer.class);
		m_dc = new JFormattedTextField(formatter);
		m_dc.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		m_dc.setValue(new Integer(15));
		m_result = new JLabel("<html><body><b>-</b></body></html>");
		m_result.setMinimumSize(new Dimension(100,(int)m_result.getMinimumSize().getWidth()));
		m_result.setPreferredSize(new Dimension(100,(int)m_result.getPreferredSize().getWidth()));
		//Create panel for labels
		JPanel labelPanel = new JPanel(new GridLayout(0,1));
		JLabel label = new JLabel("Skill Roll:");
		label.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
		labelPanel.add(label);
		label = new JLabel("DC:");
		label.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
		labelPanel.add(label);
		label = new JLabel("Result:");
		label.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
		labelPanel.add(label);
		//Create panel for text fields
		JPanel fieldPanel = new JPanel(new GridLayout(0,1));
		fieldPanel.add(m_skillRoll);
		fieldPanel.add(m_dc);
		fieldPanel.add(m_result);
		//Create main panel; add sub-panels
		JPanel mainPanel = new JPanel(new BorderLayout(5,5));
		mainPanel.add(labelPanel,BorderLayout.CENTER);
		mainPanel.add(fieldPanel,BorderLayout.EAST);
		//Add main panel to content pane
		getContentPane().add(mainPanel,BorderLayout.CENTER);
		//Create button panel
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		buttons.add(Box.createHorizontalGlue());
		m_roll = new JButton("Roll");
		buttons.add(m_roll);
		buttons.add(m_roll);
		buttons.add(Box.createHorizontalStrut(10));
		m_ok = new JButton("Ok");
		buttons.add(m_ok);
		getContentPane().add(buttons,BorderLayout.SOUTH);
		//Initialize listeners
		m_roll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				handleRoll(e);
			}
		});
		m_ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				handleOk(e);
			}
		});
		//Size and position the dialog
		pack();
		setLocationRelativeTo(GMGenSystem.inst);
	}

	/**
	 * <p>
	 * Exits the dialog.
	 * </p>
	 *
	 * @param e Event which fired this handler
	 */
	protected void handleOk(ActionEvent e)
	{
		hide();
	}

	/**
	 * <p>
	 * Rolls the skill roll.
	 * </p>
	 *
	 * @param e Event which fired this handler
	 */
	protected void handleRoll(ActionEvent e)
	{
		boolean logging = Roll.isLogging();
		Roll.setLogging(false);
		m_jep.parseExpression(m_skillRoll.getText());
		int rollResult = (int)Math.floor(m_jep.getValue());
		m_result.setText("<html><body><b>"
				+ rollResult
				+ ((rollResult >= ((Integer)m_dc.getValue()).intValue()) ? " (passed)" : "")
				+ "</b></body></html>");
		Roll.setLogging(logging);
	}

}
