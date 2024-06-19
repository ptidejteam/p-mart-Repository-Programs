/*
 * LstEditorMain.java
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
 * Created on November 4, 2002, 9:19 AM
 *
 * @(#) $Id: LstEditorMain.java,v 1.1 2006/02/21 00:57:39 vauchers Exp $
 */

/**
 * <code>LstEditorMain</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.gui.editor.EditorConstants;
import pcgen.gui.editor.EditorMainForm;
import pcgen.util.PropertyFactory;

public final class LstEditorMain extends JFrame
{
	private JList lstLstFileContent;
	private JList lstLstFileTypes;
	private JPanel pnlButtons;
	private JButton btnCopy;
	private JButton btnDelete;
	private JLabel lblLstFileContent;
	private JLabel lblLstFileTypes;
	private JPanel pnlLstEditorMain;
	private JButton btnNew;
	private JPanel pnlLstFileContent;
	private JPanel pnlLstFileTypes;
	private JScrollPane scpLstFileContent;
	private JScrollPane scpLstFileTypes;
	private JPanel pnllstLstFileContent;
	private JPanel pnllstLstFileTypes;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JButton btnDone;
	private JButton btnEdit;

	private int editType = EditorConstants.EDIT_NONE;
	private static final String[] supportedLsts = {"Deity", "Skill"};

	/** Creates new form LstEditorMain */
	public LstEditorMain()
	{
		initComponents();

		pcgen.gui.Utility.maybeSetIcon((Frame) this, "PcgenIcon.gif");
		pcgen.gui.Utility.centerFrame(this, false);
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		pnlLstEditorMain = new JPanel();
		pnlLstFileTypes = new JPanel();
		lblLstFileTypes = new JLabel();
		pnllstLstFileTypes = new JPanel();
		scpLstFileTypes = new JScrollPane();
		lstLstFileTypes = new JList(supportedLsts);
		pnlLstFileContent = new JPanel();
		lblLstFileContent = new JLabel();
		pnllstLstFileContent = new JPanel();
		scpLstFileContent = new JScrollPane();
		lstLstFileContent = new JList();
		pnlButtons = new JPanel();
		btnNew = new JButton();
		btnEdit = new JButton();
		btnDelete = new JButton();
		btnCopy = new JButton();
		btnDone = new JButton();
		jPanel1 = new JPanel();
		jPanel2 = new JPanel();

		getContentPane().setLayout(new GridBagLayout());

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				btnDoneActionPerformed(null);
			}
		});

		pnlLstEditorMain.setLayout(new GridBagLayout());

		pnlLstFileTypes.setLayout(new GridBagLayout());

		lblLstFileTypes.setText("File Types");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlLstFileTypes.add(lblLstFileTypes, gridBagConstraints);

		pnllstLstFileTypes.setLayout(new BorderLayout());

		lstLstFileTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstLstFileTypes.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				lstLstFileTypesValueChanged(evt);
			}
		});

		scpLstFileTypes.setPreferredSize(new Dimension(90, 20));
		scpLstFileTypes.setViewportView(lstLstFileTypes);

		pnllstLstFileTypes.add(scpLstFileTypes, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlLstFileTypes.add(pnllstLstFileTypes, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.3;
		gridBagConstraints.weighty = 1.0;
		pnlLstEditorMain.add(pnlLstFileTypes, gridBagConstraints);

		pnlLstFileContent.setLayout(new GridBagLayout());

		lblLstFileContent.setText("Content");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlLstFileContent.add(lblLstFileContent, gridBagConstraints);

		pnllstLstFileContent.setLayout(new BorderLayout());

		lstLstFileContent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstLstFileContent.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				lstLstFileContentValueChanged(evt);
			}
		});
		lstLstFileContent.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstLstFileContentMouseClicked(evt);
			}
		});

		scpLstFileContent.setPreferredSize(new Dimension(90, 20));
		scpLstFileContent.setViewportView(lstLstFileContent);

		pnllstLstFileContent.add(scpLstFileContent, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlLstFileContent.add(pnllstLstFileContent, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.7;
		gridBagConstraints.weighty = 1.0;
		pnlLstEditorMain.add(pnlLstFileContent, gridBagConstraints);

		pnlButtons.setLayout(new GridBagLayout());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0.1;
		pnlButtons.add(jPanel2, gridBagConstraints);

		btnNew.setText(PropertyFactory.getString("in_new"));
		btnNew.setMnemonic(PropertyFactory.getMnemonic("in_mn_new", 0));
		btnNew.setEnabled(false);
		btnNew.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnNewActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnNew, gridBagConstraints);

		btnEdit.setText(PropertyFactory.getString("in_edit"));
		btnEdit.setMnemonic(PropertyFactory.getMnemonic("in_mn_edit", 0));
		btnEdit.setEnabled(false);
		btnEdit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnEditActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnEdit, gridBagConstraints);

		btnDelete.setText(PropertyFactory.getString("in_delete"));
		btnDelete.setMnemonic(PropertyFactory.getMnemonic("in_mn_delete", 0));
		btnDelete.setEnabled(false);
		btnDelete.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnDeleteActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnDelete, gridBagConstraints);

		btnCopy.setText(PropertyFactory.getString("in_copy"));
		btnCopy.setMnemonic(PropertyFactory.getMnemonic("in_mn_copy", 0));
		btnCopy.setEnabled(false);
		btnCopy.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnCopyActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnCopy, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0.9;
		pnlButtons.add(jPanel1, gridBagConstraints);

		btnDone.setText(PropertyFactory.getString("in_close"));
		btnDone.setMnemonic(PropertyFactory.getMnemonic("in_mn_close", 0));
		btnDone.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnDoneActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		gridBagConstraints.anchor = GridBagConstraints.SOUTH;
		pnlButtons.add(btnDone, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.weighty = 1.0;
		pnlLstEditorMain.add(pnlButtons, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		getContentPane().add(pnlLstEditorMain, gridBagConstraints);

		setSize(new Dimension(640, 470));
	}

	private void lstLstFileContentValueChanged(ListSelectionEvent evt)
	{
		final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();
		if (lstItem != null)
		{
			boolean canEdit = lstItem.isType(Constants.s_CUSTOM);
			btnDelete.setEnabled(canEdit);
			btnEdit.setEnabled(canEdit);
			btnCopy.setEnabled(true);
			return;
		}
		btnEdit.setEnabled(false);
		btnCopy.setEnabled(false);
		btnDelete.setEnabled(false);
	}

	private void lstLstFileContentMouseClicked(MouseEvent evt)
	{
		if (btnEdit.isEnabled() && (evt.getClickCount() == 2))
		{
			btnEditActionPerformed(null);
		}
	}

	private void lstLstFileTypesValueChanged(ListSelectionEvent evt)
	{
		final String lstType = (String) lstLstFileTypes.getSelectedValue();
		ArrayList possibleSelections = null;
		if (lstType != null)
		{
			if (lstType.equalsIgnoreCase("Deity"))
			{
				possibleSelections = Globals.getDeityList();
				editType = EditorConstants.EDIT_DEITY;
			}
			else if (lstType.equalsIgnoreCase("Skill"))
			{
				possibleSelections = Globals.getSkillList();
				editType = EditorConstants.EDIT_SKILL;
			}
		}
		if (possibleSelections == null)
		{
			possibleSelections = new ArrayList();
			editType = EditorConstants.EDIT_NONE;
		}
		lstLstFileContent.setListData(possibleSelections.toArray());
		btnNew.setEnabled(possibleSelections.size() != 0);
		btnEdit.setEnabled(false);
		btnCopy.setEnabled(false);
		btnDelete.setEnabled(false);
	}

	private void btnDoneActionPerformed(ActionEvent evt)
	{
		hide();
		dispose();
	}

	private void btnCopyActionPerformed(ActionEvent evt)
	{
		final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();
		if (lstItem != null)
		{
			try
			{
				PObject newItem = (PObject) lstItem.clone();
				newItem.setName("Copy of " + newItem.getName());
				editIt(newItem);
			}
			catch (Exception e)
			{
			}
		}
	}

	private void btnDeleteActionPerformed(ActionEvent evt)
	{
		final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();
		if (lstItem != null)
		{
			switch (editType)
			{
				case EditorConstants.EDIT_DEITY:
					Globals.getDeityList().remove(lstItem);
					break;

				case EditorConstants.EDIT_SKILL:
					Globals.getSkillList().remove(lstItem);
					break;

				default:
					return;
			}
			lstLstFileTypesValueChanged(null);
		}
	}

	private void btnEditActionPerformed(ActionEvent evt)
	{
		final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();
		if (lstItem != null)
		{
			editIt(lstItem);
		}
	}

	private void btnNewActionPerformed(ActionEvent evt)
	{
		editIt(null);
	}

	public void editIt(PObject editItem, int argEditType)
	{
		editType = argEditType;
		editIt(editItem);
	}

	private void editIt(PObject editItem)
	{
		try
		{
			PObject oldObject;
			if (editItem == null)
			{
				oldObject = null;
				editItem = newObject();
			}
			else
			{
				oldObject = findObject(editItem.getName());
				//
				// Remove the pre-existing object (so renaming won't mess us up)
				//
				if (oldObject != null)
				{
					removeObject(oldObject);
					editItem = (PObject) oldObject.clone();
				}
			}

			final EditorMainForm emf = new EditorMainForm(this, editItem, editType);
			for (; ;)
			{
				emf.show();
				if (emf.wasCancelled())
				{
					//
					// Need to add pre-existing object back in
					//
					if (oldObject != null)
					{
						addObject(oldObject);
					}
					return;
				}

				//
				// Make sure we aren't over-writing a pre-existing element
				//
				if (findObject(editItem.getName()) != null)
				{
					JOptionPane.showMessageDialog(null, "Cannot save; already exists.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					continue;
				}
				break;
			}

			addObject(editItem);
			lstLstFileTypesValueChanged(null);
		}
		catch (Exception ignored)
		{
			ignored.printStackTrace();
		}
	}

	private PObject newObject()
	{
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				return new Deity();

			case EditorConstants.EDIT_SKILL:
				return new Skill();

			default:
				break;
		}
		return null;
	}

	private PObject findObject(final String aName)
	{
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				return Globals.getDeityNamed(aName);

			case EditorConstants.EDIT_SKILL:
				return Globals.getSkillNamed(aName);

			default:
				break;
		}
		return null;
	}

	private void addObject(final PObject editObject)
	{
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				Globals.getDeityList().add(editObject);
				Globals.sortPObjectList(Globals.getDeityList());
				break;

			case EditorConstants.EDIT_SKILL:
				Globals.getSkillList().add(editObject);
				Globals.sortPObjectList(Globals.getSkillList());
				break;

			default:
				break;
		}
	}

	private boolean removeObject(final PObject editObject)
	{
		if (editObject != null)
		{
			switch (editType)
			{
				case EditorConstants.EDIT_DEITY:
					return Globals.getDeityList().remove(editObject);

				case EditorConstants.EDIT_SKILL:
					return Globals.getSkillList().remove(editObject);

				default:
					break;
			}
		}
		return false;
	}

}
