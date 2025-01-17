/*
 * NewPurchaseMethodDialog.java
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
 * Created on August 20, 2002, 1:57 PM
 *
 * $Id: NewPurchaseMethodDialog.java,v 1.1 2006/02/21 01:16:23 vauchers Exp $
 */

package pcgen.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import pcgen.core.Constants;
import pcgen.gui.utils.GuiFacade;

/**
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

class NewPurchaseMethodDialog extends JDialog
{
	static final long serialVersionUID = -5321303573914291162L;
	private JPanel buttonPanel;
	private JPanel jPanel2;
	private JPanel jPanel1;
	private JButton okButton;
	private JButton cancelButton;
	private JTextField nameEdit;
	private JTextField pointsEdit;
	private JLabel jLabel2;
	private JLabel jLabel1;

	private boolean wasCancelled = true;

	/** Creates new form JDialog */
	private NewPurchaseMethodDialog(Frame parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
		setLocationRelativeTo(parent);	// centre on parent
	}

	/** Creates new form JDialog */
	public NewPurchaseMethodDialog(JDialog parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
		setLocationRelativeTo(parent);	// centre on parent
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		jPanel1 = new JPanel();
		jLabel1 = new JLabel();
		nameEdit = new JTextField();
		jPanel2 = new JPanel();
		jLabel2 = new JLabel();
		pointsEdit = new JTextField();
		buttonPanel = new JPanel();
		cancelButton = new JButton();
		okButton = new JButton();

		getContentPane().setLayout(new GridBagLayout());

		setTitle("Enter name and points for Purchase Method");
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				closeDialog();
			}
		});

		jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel1.setText("Name:");
		jLabel1.setPreferredSize(new Dimension(140, 15));
		jPanel1.add(jLabel1);

		nameEdit.setPreferredSize(new Dimension(140, 20));
		jPanel1.add(nameEdit);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(jPanel1, gridBagConstraints);

		jPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel2.setText("Points:");
		jLabel2.setPreferredSize(new Dimension(140, 15));
		jPanel2.add(jLabel2);

		pointsEdit.setPreferredSize(new Dimension(30, 20));
		jPanel2.add(pointsEdit);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(jPanel2, gridBagConstraints);

		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		cancelButton.setMnemonic('C');
		cancelButton.setText("Cancel");
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				cancelButtonActionPerformed();
			}
		});

		okButton.setMnemonic('O');
		okButton.setText("OK");
		buttonPanel.add(okButton);
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				okButtonActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(buttonPanel, gridBagConstraints);

		pack();
	}

	/** Closes the dialog */
	private void closeDialog()
	{
		setVisible(false);
		dispose();
	}

	private void cancelButtonActionPerformed()
	{
		wasCancelled = true;
		setVisible(false);
		this.dispose();
	}

	private void okButtonActionPerformed()
	{
		if (getEnteredName().length() == 0)
		{
			GuiFacade.showMessageDialog(null, "Please enter a name for this method.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
		if (getEnteredPoints() <= 0)
		{
			GuiFacade.showMessageDialog(null, "Invalid points value. Please try again.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
		wasCancelled = false;
		setVisible(false);
		this.dispose();
	}

	public boolean getWasCancelled()
	{
		return wasCancelled;
	}

	public String getEnteredName()
	{
		return nameEdit.getText().trim();
	}

	public int getEnteredPoints()
	{
		try
		{
			final int points = Integer.parseInt(pointsEdit.getText());
			return points;
		}
		catch (Exception exc)
		{
			//TODO Really ignore?
		}
		return -1;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		new NewPurchaseMethodDialog(new JFrame(), true).show();
	}
}
