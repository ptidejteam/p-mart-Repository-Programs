/*
 *  Initiative - A role playing utility to track turns
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
 *  PreferencesTrackingPanel.java
 *
 *  Created on August 29, 2002, 2:41 PM
 */
package plugin.initiative.gui;

import javax.swing.JCheckBox;

import pcgen.core.SettingsHandler;
import plugin.initiative.InitiativePlugin;

/**
 *  Panel that tracks the misc preferences
 *
 *@author     devon
 *@created    April 7, 2003
 */
public class PreferencesPerformancePanel extends gmgen.gui.PreferencesPanel
{
	// End of variables declaration//GEN-END:variables
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel jLabel4;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private JCheckBox refreshOnStateChange;

	/**  Creates new form PreferencesMiscPanel */
	public PreferencesPerformancePanel()
	{
		initComponents();
	}

	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".refreshOnStateChange", getRefreshOnStateChange());
	}

	public void initPreferences()
	{
		setRefreshOnStateChange(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".refreshOnStateChange", true));
	}

	/**
	 * <p>
	 * Turns on or off refresh on state cange
	 * </p>
	 * 
	 * @param b
	 */
	private void setRefreshOnStateChange(boolean b)
	{
		refreshOnStateChange.setSelected(b);
	}

	/**
	 * <p>
	 * Gets current setting of refresh on state change
	 * </p>
	 * 
	 * @param b
	 */
	private boolean getRefreshOnStateChange()
	{
		return refreshOnStateChange.isSelected();
	}

	public String toString()
	{
		return "Performance";
	}

	private void initComponents()
	{

		java.awt.GridBagConstraints gridBagConstraints;

		jSeparator1 = new javax.swing.JSeparator();
		jLabel2 = new javax.swing.JLabel();
		jSeparator2 = new javax.swing.JSeparator();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		refreshOnStateChange = new JCheckBox();

		setLayout(new java.awt.GridBagLayout());

		jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 8;
		gridBagConstraints.ipady = 260;
		gridBagConstraints.insets = new java.awt.Insets(8, 17, 0, 0);
		add(jSeparator1, gridBagConstraints);

		jLabel2.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel2.setText("Performance");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
		add(jLabel2, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 6;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 400;
		gridBagConstraints.ipady = 8;
		gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
		add(jSeparator2, gridBagConstraints);

		jLabel3.setText("Refresh on state chage");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
		add(jLabel3, gridBagConstraints);

		refreshOnStateChange.setMinimumSize(new java.awt.Dimension(20, 20));
		refreshOnStateChange.setPreferredSize(new java.awt.Dimension(20, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
		add(refreshOnStateChange, gridBagConstraints);

	}
}
