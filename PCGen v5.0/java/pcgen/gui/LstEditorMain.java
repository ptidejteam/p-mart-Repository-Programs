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
 * @(#) $Id: LstEditorMain.java,v 1.1 2006/02/21 01:07:48 vauchers Exp $
 */

package pcgen.gui;

import java.awt.BorderLayout;
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
import java.util.Iterator;
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
import pcgen.core.BioSet;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.CustomData;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.spell.Spell;
import pcgen.gui.editor.EditorConstants;
import pcgen.gui.editor.EditorMainForm;
import pcgen.util.PropertyFactory;

/**
 * <code>LstEditorMain</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
final class LstEditorMain extends JFrame
{
	private static final String s_EDITTYPE_CLASS = "Class";
	private static final String s_EDITTYPE_DEITY = "Deity";
	private static final String s_EDITTYPE_DOMAIN = "Domain";
	private static final String s_EDITTYPE_FEAT = "Feat";
	private static final String s_EDITTYPE_LANGUAGE = "Language";
	private static final String s_EDITTYPE_RACE = "Race";
	private static final String s_EDITTYPE_SKILL = "Skill";
	private static final String s_EDITTYPE_SPELL = "Spell";
	private static final String s_EDITTYPE_TEMPLATE = "Template";
	private static final String s_EDITTYPE_SOURCE = "Source";

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
	private static final String[] supportedLsts = {
		s_EDITTYPE_CLASS
		, s_EDITTYPE_DEITY
		, s_EDITTYPE_DOMAIN
		, s_EDITTYPE_FEAT
		, s_EDITTYPE_LANGUAGE
		, s_EDITTYPE_RACE
		, s_EDITTYPE_SKILL
		, s_EDITTYPE_SPELL
		, s_EDITTYPE_TEMPLATE
		, s_EDITTYPE_SOURCE};

	/** Creates new form LstEditorMain */
	LstEditorMain()
	{
		initComponents();

		pcgen.gui.Utility.maybeSetIcon(this, "PcgenIcon.gif");
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

		setTitle("LST Editors");

		getContentPane().setLayout(new GridBagLayout());

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				btnDoneActionPerformed();
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
				lstLstFileTypesValueChanged();
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
				lstLstFileContentValueChanged();
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
		btnNew.setMnemonic(PropertyFactory.getMnemonic("in_mn_new"));
		btnNew.setEnabled(false);
		btnNew.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnNewActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnNew, gridBagConstraints);

		btnEdit.setText(PropertyFactory.getString("in_edit"));
		btnEdit.setMnemonic(PropertyFactory.getMnemonic("in_mn_edit"));
		btnEdit.setEnabled(false);
		btnEdit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnEditActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnEdit, gridBagConstraints);

		btnDelete.setText(PropertyFactory.getString("in_delete"));
		btnDelete.setMnemonic(PropertyFactory.getMnemonic("in_mn_delete"));
		btnDelete.setEnabled(false);
		btnDelete.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnDeleteActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnDelete, gridBagConstraints);

		btnCopy.setText(PropertyFactory.getString("in_copy"));
		btnCopy.setMnemonic(PropertyFactory.getMnemonic("in_mn_copy"));
		btnCopy.setEnabled(false);
		btnCopy.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnCopyActionPerformed();
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
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0.9;
		pnlButtons.add(jPanel1, gridBagConstraints);

		btnDone.setText(PropertyFactory.getString("in_close"));
		btnDone.setMnemonic(PropertyFactory.getMnemonic("in_mn_close"));
		btnDone.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnDoneActionPerformed();
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

	private void lstLstFileContentValueChanged()
	{
		final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();
		if (lstItem != null)
		{
			boolean canEdit = lstItem.isType(Constants.s_CUSTOM);
			final String lstType = (String) lstLstFileTypes.getSelectedValue();
			if (lstType.equalsIgnoreCase(s_EDITTYPE_SOURCE))
			{
				canEdit = true;
			}
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
			btnEditActionPerformed();
		}
	}

	private void lstLstFileTypesValueChanged()
	{
		final String lstType = (String) lstLstFileTypes.getSelectedValue();
		ArrayList possibleSelections = null;
		if (lstType != null)
		{
			if (lstType.equalsIgnoreCase(s_EDITTYPE_CLASS))
			{
				possibleSelections = Globals.getClassList();
				editType = EditorConstants.EDIT_CLASS;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_DEITY))
			{
				possibleSelections = Globals.getDeityList();
				editType = EditorConstants.EDIT_DEITY;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_DOMAIN))
			{
				possibleSelections = Globals.getDomainList();
				editType = EditorConstants.EDIT_DOMAIN;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_FEAT))
			{
				possibleSelections = Globals.getFeatList();
				editType = EditorConstants.EDIT_FEAT;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_LANGUAGE))
			{
				possibleSelections = Globals.getLanguageList();
				editType = EditorConstants.EDIT_LANGUAGE;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_RACE))
			{
				possibleSelections = new ArrayList(Globals.getRaceMap().values());
				editType = EditorConstants.EDIT_RACE;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_SKILL))
			{
				possibleSelections = Globals.getSkillList();
				editType = EditorConstants.EDIT_SKILL;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_SPELL))
			{
				possibleSelections = new ArrayList(Globals.getSpellMap().values().size());
				for (Iterator e = Globals.getSpellMap().values().iterator(); e.hasNext();)
				{
					final Object obj = e.next();
					if (obj instanceof Spell)
					{
						possibleSelections.add(obj);
					}
					else
					{
						possibleSelections.addAll((ArrayList) obj);
					}
				}
				editType = EditorConstants.EDIT_SPELL;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_TEMPLATE))
			{
				possibleSelections = Globals.getTemplateList();
				editType = EditorConstants.EDIT_TEMPLATE;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_SOURCE))
			{
				possibleSelections = Globals.getCampaignList();
				editType = EditorConstants.EDIT_CAMPAIGN;
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

	private void btnDoneActionPerformed()
	{
		hide();
		dispose();
	}

	private void btnCopyActionPerformed()
	{
		final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();
		if (lstItem != null)
		{
			try
			{
				final PObject newItem = (PObject) lstItem.clone();
				final String nameEnding = " of " + lstItem.getName();
				//
				// Check for a pre-existing item named "Copy of blah". Generate "Copy# of blah" until we find
				// one that's not in use
				//
				for (int idx = 1; ; ++idx)
				{
					String newName = "Copy" + ((idx > 1) ? Integer.toString(idx) : "") + nameEnding;
					if (findObject(newName) == null)
					{
						newItem.setName(newName);
						if (lstItem instanceof Race)
						{
							String region = lstItem.getRegionString();
							if (region == null)
							{
								region = Constants.s_NONE;
							}
							BioSet.copyRaceTags(region, lstItem.getName(), region, newItem.getName());
						}
						editIt(newItem);
						break;
					}
				}
			}
			catch (Exception e)
			{
			}
		}
	}

	private void btnDeleteActionPerformed()
	{
		//
		// Popup "Are you sure?"
		//
		if (JOptionPane.showConfirmDialog(null, "Are you sure?", Constants.s_APPNAME, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.NO_OPTION)
		{
			final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();
			if (removeObject(lstItem))
			{
				lstLstFileTypesValueChanged();
			}
		}
	}

	private void btnEditActionPerformed()
	{
		final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();
		if (lstItem != null)
		{
			editIt(lstItem);
		}
	}

	private void btnNewActionPerformed()
	{
		editIt(null);
	}

	void editIt(PObject editItem, int argEditType)
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
			lstLstFileTypesValueChanged();
		}
		catch (Exception ignored)
		{
			Globals.errorPrint("Error", ignored);
		}
	}

	private PObject newObject()
	{
		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				return new PCClass();

			case EditorConstants.EDIT_DEITY:
				return new Deity();

			case EditorConstants.EDIT_DOMAIN:
				return new Domain();

			case EditorConstants.EDIT_FEAT:
				return new Feat();

			case EditorConstants.EDIT_LANGUAGE:
				return new Language();

			case EditorConstants.EDIT_RACE:
				return new Race();

			case EditorConstants.EDIT_SKILL:
				return new Skill();

			case EditorConstants.EDIT_SPELL:
				return new Spell();

			case EditorConstants.EDIT_TEMPLATE:
				return new PCTemplate();

			case EditorConstants.EDIT_CAMPAIGN:
				return new Campaign();

			default:
				break;
		}
		return null;
	}

	private PObject findObject(final String aName)
	{
		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				return Globals.getClassNamed(aName);

			case EditorConstants.EDIT_DEITY:
				return Globals.getDeityNamed(aName);

			case EditorConstants.EDIT_DOMAIN:
				return Globals.getDomainNamed(aName);

			case EditorConstants.EDIT_FEAT:
				return Globals.getFeatNamed(aName);

			case EditorConstants.EDIT_LANGUAGE:
				return Globals.getLanguageNamed(aName);

			case EditorConstants.EDIT_RACE:
				return Globals.getRaceNamed(aName);

			case EditorConstants.EDIT_SKILL:
				return Globals.getSkillNamed(aName);

			case EditorConstants.EDIT_SPELL:
				return Globals.getSpellNamed(aName);		// will return 1st entry in ArrayList

			case EditorConstants.EDIT_TEMPLATE:
				return Globals.getTemplateNamed(aName);

			case EditorConstants.EDIT_CAMPAIGN:
				return Globals.getCampaignNamed(aName);

			default:
				break;
		}
		return null;
	}

	private void addObject(final PObject editObject)
	{
		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				Globals.getClassList().add(editObject);
				Globals.sortPObjectList(Globals.getClassList());
				break;

			case EditorConstants.EDIT_DEITY:
				Globals.getDeityList().add(editObject);
				Globals.sortPObjectList(Globals.getDeityList());
				break;

			case EditorConstants.EDIT_DOMAIN:
				Globals.addDomain((Domain) editObject);
				Globals.sortPObjectList(Globals.getDomainList());
				break;

			case EditorConstants.EDIT_FEAT:
				Globals.getFeatList().add(editObject);
				Globals.sortPObjectList(Globals.getFeatList());
				break;

			case EditorConstants.EDIT_LANGUAGE:
				Globals.getLanguageList().add(editObject);
				Globals.sortPObjectList(Globals.getLanguageList());
				break;

			case EditorConstants.EDIT_RACE:
				Globals.getRaceMap().put(editObject.getKeyName(), editObject);
				break;

			case EditorConstants.EDIT_SKILL:
				Globals.getSkillList().add(editObject);
				Globals.sortPObjectList(Globals.getSkillList());
				break;

			case EditorConstants.EDIT_SPELL:
				Globals.getSpellMap().put(editObject.getName(), editObject);
				//Globals.sortPObjectList(Globals.getSpellList());
				break;

			case EditorConstants.EDIT_TEMPLATE:
				Globals.getTemplateList().add(editObject);
				Globals.sortPObjectList(Globals.getTemplateList());
				break;

			case EditorConstants.EDIT_CAMPAIGN:
				Globals.getCampaignList().add(editObject);
				Globals.sortPObjectList(Globals.getCampaignList());
				break;

			default:
				break;
		}
		CustomData.writeCustomFiles();
	}

	private boolean removeObject(final PObject editObject)
	{
		if (editObject != null)
		{
			switch (editType)
			{
				case EditorConstants.EDIT_CLASS:
					return Globals.getClassList().remove(editObject);

				case EditorConstants.EDIT_DEITY:
					return Globals.getDeityList().remove(editObject);

				case EditorConstants.EDIT_DOMAIN:
					if (Globals.getDomainMap().remove(editObject.getKeyName()) != null)
					{
						return Globals.getDomainList().remove(editObject);
					}
					return false;

				case EditorConstants.EDIT_FEAT:
					return Globals.getFeatList().remove(editObject);

				case EditorConstants.EDIT_LANGUAGE:
					return Globals.getLanguageList().remove(editObject);

				case EditorConstants.EDIT_RACE:
					return Globals.getRaceMap().remove(editObject.getKeyName()) != null;

				case EditorConstants.EDIT_SKILL:
					return Globals.getSkillList().remove(editObject);

				case EditorConstants.EDIT_SPELL:
					Object obj = Globals.getSpellMap().get(editObject.getName());
					if (obj instanceof ArrayList)
					{
						return ((ArrayList) obj).remove(editObject);
					}
					return Globals.getSpellMap().remove(editObject.getName()) != null;

				case EditorConstants.EDIT_TEMPLATE:
					return Globals.getTemplateList().remove(editObject);

				case EditorConstants.EDIT_CAMPAIGN:
					return Globals.getCampaignList().remove(editObject);

				default:
					break;
			}
		}
		return false;
	}

}
