/*
 * EditorMainForm.java
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
 * Created on November 6, 2002, 9:24 AM
 *
 * @(#) $Id: EditorMainForm.java,v 1.1 2006/02/21 00:57:49 vauchers Exp $
 */

/**
 * <code>EditorMainForm</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.WeaponProf;
import pcgen.util.PropertyFactory;

public final class EditorMainForm extends JDialog
{
	private JButton btnAddAdvanced;
	private JButton btnCancel;
	private JButton btnRemoveAdvanced;
	private JButton btnSave;
	private JComboBox cmbAdvancedTag;
	private JComboBox cmbQualifier;
	private JComboBox cmbVariable;
	private JLabel lblAdvancedHeader;
	private JLabel lblAdvancedSelected;
	private JLabel lblAdvancedTag;
	private JLabel lblAdvancedTagValue;
	private JLabel lblQualifier;
	private JLabel lblVariable;
	private JList lstAdvancedSelected;
	private JPanel pnlAdvanced;
	private JPanel pnlAdvancedAvailable;
	private JPanel pnlAdvancedButtons;
	private JPanel pnlAdvancedHeader;
	private JPanel pnlAdvancedSelected;
	private JPanel pnlAdvancedTag;
	private JPanel pnlAdvancedTagValue;
	private JPanel pnlButtons;
	private AvailableSelectedPanel pnlDomains;
	private AvailableSelectedPanel pnlFollowers;
	private AvailableSelectedPanel pnlLanguages;
	private JPanel pnlMainDialog;
	private EditorBasePanel pnlMainTab;
	private AvailableSelectedPanel pnlRaces;
	private AvailableSelectedPanel pnlSkills;
	private JPanel pnlTabs;
	private AvailableSelectedPanel pnlWeapons;
	private JPanel pnllstAdvancedSelected;
	private JPanel pnllstAdvancedTagValue;
	private JScrollPane scpAdvancedSelected;
	private JScrollPane scpAdvancedTagValue;
	private JTabbedPane jTabbedPane1;
	private JTextArea txtAdvancedTagValue;

	//
	// Skills
	//
	private AvailableSelectedPanel pnlClasses;
	private AvailableSelectedPanel pnlSynergy;

	private PObject thisPObject = null;
	private boolean wasCancelled = true;

//
// tags from PObject:
// BONUS
// CCSKILL, CSKILL
// DEFINE, DR
// KEY
// LANGAUTO
// NAME, NAMEISPI
// PRExxx
// RESTRICT
// SA, SPELL, SR
// UDAM, UMULT
// VISION
// WEAPONAUTO
//
// tags from Deity:
// ALIGN
// DEITYWEAP
// DESC
// DOMAINS
// FOLLOWERALIGN
// PANTHEON
// QUALIFY
// RACE
// SYMBOL
// TYPE
//
// tags from Skill:
// ACHECK
// CHOOSE
// CLASSES
// EXCLUSIVE
// KEYSTAT
// QUALIFY
// REQ
// ROOT
// SYNERGY
// TYPE
// USEUNTRAINED
//
// tags from Domain:
// CHOOSE
// DESC
// FEAT
// QUALIFY
//
	private static final String[] qualifiers = new String[]{"(None)", "VARDEFINED"};
	private static final String[] tags = new String[]
	{"BONUS", "CHOOSE", "DEFINE", "DR", "KEY", "PANTHEON",
	 "PREALIGN", "PREARMORTYPE", "PREATT",
	 "PRECHECK", "PRECHECKBASE", "PRECLASS", "PRECLASSLEVELMAX",
	 "PREDEFAULTMONSTER", "PREDEITY", "PREDEITYALIGN", "PREDEITYDOMAIN", "PREDOMAIN", "PREDSIDEPTS",
	 "PREEQUIP", "PREEQUIPBOTH", "PREEQUIPPRIMARY", "PREEQUIPSECONDARY", "PREEQUIPTWOWEAPON",
	 "PREFEAT", "PREFORCEPTS",
	 "PREGENDER",
	 "PREHANDSEQ", "PREHANDSGT", "PREHANDSGTEQ", "PREHANDSLT", "PREHANDSLTEQ", "PREHANDSNEQ", "PREHD", "PREHP",
	 "PREITEM",
	 "PRELANG", "PRELEVEL", "PRELEVELMAX",
	 "PREMOVE",
	 "PRERACE", "PREREPUTATION", "PREREPUTATIONLTEQ",
	 "PRESA", "PRESIZEEQ", "PRESIZELT", "PRESIZELTEQ", "PRESIZEGT", "PRESIZEGTEQ", "PRESIZENEQ", "PRESKILL", "PRESKILLMULT", "PRESKILLTOT", "PRESPELL", "PRESPELLCAST", "PRESPELLSCHOOL",
	 "PRESPELLSCHOOLSUB", "PRESPELLTYPE", "PRESTAT",
	 "PRETEMPLATE", "PRETYPE",
	 "PREUATT",
	 "PREVAR",
	 "PREWEAPONPROF",
	 "QUALIFY", /*"RESTRICT",*/ "SA", "SPELL", "SR", "UMDAM", "UMULT", "VISION"};

	private int editType = EditorConstants.EDIT_NONE;

	/** Creates new form EditorMainForm */
	public EditorMainForm(JFrame parent, PObject argPObject, int argEditType) throws Exception
	{
		super(parent);

		if (argPObject == null)
		{
			throw new NullPointerException();
		}

		setModal(true);
		editType = argEditType;
		thisPObject = argPObject;
		initComponents();
		intComponentContents();
		setLocationRelativeTo(parent);	// centre on parent
	}

	private void intComponentContents()
	{
		Iterator e;
		String aString;
		StringTokenizer aTok;
		ArrayList availableList = new ArrayList();
		ArrayList selectedList = new ArrayList();
		ArrayList selectedList2 = new ArrayList();

		pnlMainTab.setNameText(thisPObject.getName());
		pnlMainTab.setProductIdentity(thisPObject.getNameIsPI());
		pnlMainTab.setSourceText(thisPObject.getSourcePage());

		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				pnlMainTab.setHolyItemText(((Deity) thisPObject).getHolyItem());
				pnlMainTab.setDescriptionText(((Deity) thisPObject).getDescription());

				//
				// Initialize the contents of the deity's alignment combo
				//
				pnlMainTab.setDeityAlignment(((Deity) thisPObject).getAlignment());

				//
				// Initialize the lists of available and selected follower alignments
				//
				availableList.clear();
				selectedList.clear();
				for (e = Globals.getAlignmentList().iterator(); e.hasNext();)
				{
					final PCAlignment anAlignment = (PCAlignment) e.next();
					if (anAlignment.isValidForFollower())
					{
						availableList.add(anAlignment.getName());
					}
				}
				final String followerAlignments = ((Deity) thisPObject).getFollowerAlignments();
				parseAlignment(availableList, selectedList, followerAlignments, null);

				pnlFollowers.setAvailableList(availableList, true);
				pnlFollowers.setSelectedList(selectedList, true);

				//
				// Initialize the contents of the available and selected domains lists
				//
				selectedList.clear();
				availableList.clear();
				for (e = Globals.getDomainList().iterator(); e.hasNext();)
				{
					final Domain aDomain = (Domain) e.next();
					if (((Deity) thisPObject).hasDomain(aDomain))
					{
						selectedList.add(aDomain);
					}
					else
					{
						availableList.add(aDomain);
					}
				}
				pnlDomains.setAvailableList(availableList, true);
				pnlDomains.setSelectedList(selectedList, true);

				//
				// Initialize the contents of the available and selected races list
				//
				selectedList.clear();
				availableList.clear();
				final ArrayList raceList = ((Deity) thisPObject).getRaceList();
				for (e = Globals.getRaceMap().values().iterator(); e.hasNext();)
				{
					final Race aRace = (Race) e.next();
					final String raceName = aRace.getName();
					if (!raceName.equals(Constants.s_NONESELECTED))
					{
						if (raceList.contains(raceName))
						{
							selectedList.add(aRace);
						}
						else
						{
							availableList.add(aRace);
						}
					}
				}
				pnlRaces.setAvailableList(availableList, true);
				pnlRaces.setSelectedList(selectedList, true);
				break;

			case EditorConstants.EDIT_SKILL:
				availableList.clear();
				selectedList.clear();
				selectedList2.clear();
				for (e = Globals.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass) e.next();
					availableList.add(aClass.getName());
				}
				boolean negate;
				for (e = ((Skill) thisPObject).getClassList().iterator(); e.hasNext();)
				{
					aString = (String) e.next();
					if (aString.length() > 0)
					{
						if (aString.charAt(0) == '!')
						{
							aString = aString.substring(1);
							negate = true;
						}
						else
						{
							negate = false;
						}
						final int idx = availableList.indexOf(aString);
						if (idx < 0)
						{
							System.err.println("Unknown class: " + aString);
							continue;
						}
						availableList.remove(idx);
						if (negate)
						{
							selectedList2.add(aString);
						}
						else
						{
							selectedList.add(aString);
						}
					}
				}
				pnlClasses.setAvailableList(availableList, true);
				pnlClasses.setSelectedList(selectedList, true);
				pnlClasses.setSelectedList2(selectedList2, true);
				pnlClasses.setLblSelectedText("Class Skill");
				pnlClasses.setLblSelected2Text("Not allowed");

				//
				// Populate the types
				//
				availableList.clear();
				selectedList.clear();
				for (e = Globals.getSkillList().iterator(); e.hasNext();)
				{
					final Skill aSkill = (Skill) e.next();
					for (int i = aSkill.getMyTypeCount(); i > 0;)
					{
						aString = aSkill.getMyType(--i);
						if (!aString.equals(Constants.s_CUSTOM))
						{
							if (!availableList.contains(aString))
							{
								availableList.add(aString);
							}
						}
					}
				}
				//
				// remove this skill's type from the available list and place into selected list
				//
				for (int i = ((Skill) thisPObject).getMyTypeCount(); i > 0;)
				{
					aString = ((Skill) thisPObject).getMyType(--i);
					if (!aString.equals(Constants.s_CUSTOM))
					{
						selectedList.add(aString);
						availableList.remove(aString);
					}
				}
				pnlMainTab.setTypesAvailableList(availableList, true);
				pnlMainTab.setTypesSelectedList(selectedList, true);

				pnlMainTab.setKeyStat(((Skill) thisPObject).getKeyStat());
				pnlMainTab.setArmorCheck(((Skill) thisPObject).getACheck());
				pnlMainTab.setIsExclusive(((Skill) thisPObject).isExclusive());
				pnlMainTab.setIsUntrained(((Skill) thisPObject).isUntrained());
				break;

			default:
				break;
		}


		//
		// Initialize the contents of the available and selected languages lists
		//
		selectedList.clear();
		availableList.clear();
		final Set aSet = thisPObject.getAutoLanguages();
		for (e = Globals.getLanguageList().iterator(); e.hasNext();)
		{
			final Language aLang = (Language) e.next();
			if (aSet.contains(aLang))
			{
				selectedList.add(aLang);
			}
			else
			{
				availableList.add(aLang);
			}
		}
		pnlLanguages.setAvailableList(availableList, true);
		pnlLanguages.setSelectedList(selectedList, true);

		//
		// Initialize the contents of the available and selected weapon prof lists
		//
		selectedList.clear();
		availableList = (ArrayList) Globals.getWeaponProfList().clone();
		final ArrayList autoWeap = thisPObject.getWeaponProfAutos();
		if (autoWeap != null)
		{
			for (e = autoWeap.iterator(); e.hasNext();)
			{
				final WeaponProf wp = Globals.getWeaponProfNamed((String) e.next());
				if (wp != null)
				{
					selectedList.add(wp);
					availableList.remove(wp);
				}
			}
		}
		pnlWeapons.setAvailableList(availableList, true);
		pnlWeapons.setSelectedList(selectedList, true);

		if (editType == EditorConstants.EDIT_DEITY)
		{
			//
			// Initialize the contents of the available and selected favored weapons lists
			//
			selectedList.clear();
			availableList = (ArrayList) Globals.getWeaponProfList().clone();
			aTok = new StringTokenizer(((Deity) thisPObject).getFavoredWeapon(), "|", false);
			while (aTok.hasMoreTokens())
			{
				String deityWeap = aTok.nextToken();
				if (deityWeap.equalsIgnoreCase("ALL") || "ANY".equalsIgnoreCase(deityWeap))
				{
					selectedList.addAll(availableList);
					availableList.clear();
					break;
				}
				else
				{
					final WeaponProf wp = Globals.getWeaponProfNamed(deityWeap);
					if (wp != null)
					{
						selectedList.add(wp);
						availableList.remove(wp);
					}
				}
			}
			pnlMainTab.setFavoredWeaponsAvailableList(availableList, true);
			pnlMainTab.setFavoredWeaponsSelectedList(selectedList, true);

			//
			// initialize the Qualifier combo on the Followers tab
			//
			cmbQualifier.setModel(new DefaultComboBoxModel(qualifiers));

			//
			// Initialize the Variable combo with all the variable names we can find
			//
			availableList.clear();
			addVariables(availableList, Globals.getClassList());
			addVariables(availableList, Globals.getFeatList());
			addVariables(availableList, Globals.getRaceMap().values());
			addVariables(availableList, Globals.getSkillList());
			addVariables(availableList, Globals.getModifierList());
			addVariables(availableList, Globals.getTemplateList());
			addVariables(availableList, Globals.getCompanionModList());
			Collections.sort(availableList);
			cmbVariable.setModel(new DefaultComboBoxModel(availableList.toArray()));
		}
		else if (editType == EditorConstants.EDIT_SKILL)
		{
			availableList.clear();
			selectedList.clear();
			for (e = Globals.getSkillList().iterator(); e.hasNext();)
			{
				final Skill aSkill = (Skill) e.next();
				if (!aSkill.getName().equals(thisPObject.getName()))
				{
					availableList.add(aSkill.getName());
				}
			}

			if (((Skill) thisPObject).getSynergyList() != null)
			{
				for (e = ((Skill) thisPObject).getSynergyList().iterator(); e.hasNext();)
				{
					aString = (String) e.next();
					aTok = new StringTokenizer(aString, "=", false);
					if (aTok.countTokens() != 3)
					{
						System.err.println("Badly formed synergy element: " + aString);
						continue;
					}
					final String skill = aTok.nextToken();
					final String ranks = aTok.nextToken();
					final String bonus = aTok.nextToken();
					availableList.remove(skill);
					selectedList.add(encodeSynergyEntry(skill, ranks, bonus));
				}
			}
			pnlSynergy.setAvailableList(availableList, true);
			pnlSynergy.setSelectedList(selectedList, true);

			//
			// initialize the Qualifier and Variables combos on the Synergy tab
			//
			String[] values = new String[30];
			for (int i = 0; i < values.length; ++i)
			{
				values[i] = String.valueOf(i + 1);
			}
			cmbQualifier.setModel(new DefaultComboBoxModel(values));
			cmbVariable.setModel(new DefaultComboBoxModel(values));
			cmbQualifier.setSelectedIndex(4);	// should be 5
			cmbVariable.setSelectedIndex(1);	// should be 2
		}

		//
		// Initialize the contents of the available and selected class/cross-class lists
		//
		selectedList.clear();
		availableList.clear();
		for (e = Globals.getSkillList().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill) e.next();
			aString = aSkill.getName();
			if (!availableList.contains(aString))
			{
				availableList.add(aString);
			}

			for (int i = 0, x = aSkill.getMyTypeCount(); i < x; ++i)
			{
				aString = "TYPE." + aSkill.getMyType(i);
				if (!availableList.contains(aString))
				{
					availableList.add(aString);
				}
			}
		}

		ArrayList skills = thisPObject.getCSkillList();
		if (skills != null)
		{
			for (e = skills.iterator(); e.hasNext();)
			{
				aString = (String) e.next();
				selectedList.add(aString);
				if (availableList.contains(aString))
				{
					availableList.remove(aString);
				}
			}
		}
		pnlSkills.setSelectedList(selectedList, true);

		selectedList.clear();
		skills = thisPObject.getCcSkillList();
		if (skills != null)
		{
			for (e = skills.iterator(); e.hasNext();)
			{
				aString = (String) e.next();
				selectedList.add(aString);
				if (availableList.contains(aString))
				{
					availableList.remove(aString);
				}
			}
		}
		pnlSkills.setSelectedList2(selectedList, true);

		pnlSkills.setAvailableList(availableList, true);

		//
		// Tags on advanced tag
		//
		cmbAdvancedTag.setModel(new DefaultComboBoxModel(tags));
		cmbAdvancedTag.setSelectedIndex(0);

		//
		// Initialize the list of advanced items in the selected list
		//
		selectedList.clear();
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				cmbAdvancedTag.removeItem("CHOOSE");
				for (e = ((Deity) thisPObject).getPantheonList().iterator(); e.hasNext();)
				{
					selectedList.add("PANTHEON:" + (String) e.next());
				}
				break;

			case EditorConstants.EDIT_SKILL:
				cmbAdvancedTag.removeItem("PANTHEON");
				if (((Skill) thisPObject).getChoiceList() != null)
				{
					for (e = ((Skill) thisPObject).getChoiceList().iterator(); e.hasNext();)
					{
						selectedList.add("CHOOSE:" + (String) e.next());
					}
				}
				break;

			default:
				break;
		}
		for (e = thisPObject.getBonusList().iterator(); e.hasNext();)
		{
			selectedList.add("BONUS:" + (String) e.next());
		}
		for (int i = 0, x = thisPObject.getPreReqCount(); i < x; ++i)
		{
			selectedList.add(thisPObject.getPreReq(i));
		}
		for (int i = 0, x = thisPObject.getVariableCount(); i < x; ++i)
		{
			aString = thisPObject.getVariable(i);
			if (aString.startsWith("-9|"))
			{
				aString = aString.substring(3);
			}
			selectedList.add("DEFINE:" + aString);
		}
		final ArrayList saList = thisPObject.getSpecialAbilityList();
		if ((saList != null) && (saList.size() != 0))
		{
			for (e = saList.iterator(); e.hasNext();)
			{
				selectedList.add("SA:" + ((SpecialAbility) e.next()).toString());
			}
		}
		aString = thisPObject.getDR();
		if (aString != null)
		{
			aTok = new StringTokenizer(aString, "|", false);
			while (aTok.hasMoreTokens())
			{
				selectedList.add("DR:" + aTok.nextToken());
			}
		}
		aString = thisPObject.getSRFormula();
		if (aString != null)
		{
			selectedList.add("SR:" + aString);
		}
		lstAdvancedSelected.setModel(new JListModel(selectedList, true));
	}

	private void parseAlignment(ArrayList availableList, ArrayList selectedList, String alignmentString, String qualifier)
	{
		for (int i = 0; i < alignmentString.length(); ++i)
		{
			final char alignmentChar = alignmentString.charAt(i);
			if (alignmentChar == '[')
			{
				int idx = alignmentString.indexOf(']', i);
				if (idx >= 0)
				{
					final StringTokenizer aTok = new StringTokenizer(alignmentString.substring(i + 1, idx), "=", false);
					if (aTok.countTokens() == 3)
					{
						final String qualifierType = aTok.nextToken();
						final String variableName = aTok.nextToken();
						parseAlignment(availableList, selectedList, aTok.nextToken(), " [" + qualifierType + ':' + variableName + ']');
					}
					i = idx;
				}
			}
			else if ((alignmentChar >= '0') && (alignmentChar <= '9'))
			{
				final int idx = (int) alignmentChar - '0';
				availableList.remove(Globals.getLongAlignmentAtIndex(idx));
				selectedList.add(Globals.getLongAlignmentAtIndex(idx) + (qualifier == null ? "" : qualifier));
			}
		}
	}

	private static void addVariables(ArrayList availableList, Collection objList)
	{
		for (Iterator e = objList.iterator(); e.hasNext();)
		{
			final Object obj = e.next();
			if (obj instanceof PObject)
			{
				final int varCount = ((PObject) obj).getVariableCount();
				if (varCount != 0)
				{
					for (int i = 0; i < varCount; ++i)
					{
						final StringTokenizer aTok = new StringTokenizer(((PObject) obj).getVariable(i), "|", false);
						if (aTok.countTokens() > 2)
						{
							String var = aTok.nextToken();
							var = aTok.nextToken();
							if (!var.startsWith("LOCK.") && !availableList.contains(var))
							{
								availableList.add(var);
							}
						}
					}
				}
			}
			else
			{
				Globals.errorPrint(PropertyFactory.getString("in_demEr1") + ": " + obj.getClass().getName());
			}
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		btnAddAdvanced = new JButton();
		btnCancel = new JButton();
		btnRemoveAdvanced = new JButton();
		btnSave = new JButton();
		cmbAdvancedTag = new JComboBox();
		jTabbedPane1 = new JTabbedPane();
		lblAdvancedHeader = new JLabel();
		lblAdvancedSelected = new JLabel();
		lblAdvancedTag = new JLabel();
		lblAdvancedTagValue = new JLabel();
		lstAdvancedSelected = new JList();
		pnlAdvanced = new JPanel();
		pnlAdvancedAvailable = new JPanel();
		pnlAdvancedButtons = new JPanel();
		pnlAdvancedHeader = new JPanel();
		pnlAdvancedSelected = new JPanel();
		pnlAdvancedTag = new JPanel();
		pnlAdvancedTagValue = new JPanel();
		pnlButtons = new JPanel();
		pnlLanguages = new AvailableSelectedPanel();
		pnlMainDialog = new JPanel();
		pnlSkills = new AvailableSelectedPanel(true);
		pnlTabs = new JPanel();
		pnlWeapons = new AvailableSelectedPanel();
		pnllstAdvancedSelected = new JPanel();
		pnllstAdvancedTagValue = new JPanel();
		scpAdvancedSelected = new JScrollPane();
		scpAdvancedTagValue = new JScrollPane();
		txtAdvancedTagValue = new JTextArea();
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				cmbQualifier = new JComboBox();
				cmbVariable = new JComboBox();
				lblQualifier = new JLabel();
				lblVariable = new JLabel();
				pnlDomains = new AvailableSelectedPanel();
				pnlFollowers = new AvailableSelectedPanel();
				pnlRaces = new AvailableSelectedPanel();
				break;

			case EditorConstants.EDIT_SKILL:
				cmbQualifier = new JComboBox();
				cmbVariable = new JComboBox();
				lblQualifier = new JLabel();
				lblVariable = new JLabel();
				pnlClasses = new AvailableSelectedPanel(true);
				pnlSynergy = new AvailableSelectedPanel();
				break;

			default:
				break;
		}

		getContentPane().setLayout(new GridBagLayout());

		String ttl = "";
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				ttl = "Deity";
				break;

			case EditorConstants.EDIT_SKILL:
				ttl = "Skill";
				break;

			default:
				break;
		}
		setTitle(PropertyFactory.getString("in_demTitle" + ttl));

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				wasCancelled = true;
				closeDialog(evt);
			}
		}
		);

		pnlMainDialog.setLayout(new GridBagLayout());

		pnlMainDialog.setPreferredSize(new Dimension(640, 440));
		pnlTabs.setLayout(new BorderLayout());

		pnlTabs.setMinimumSize(new Dimension(128, 88));
		pnlTabs.setPreferredSize(new Dimension(640, 440));
		jTabbedPane1.setName(PropertyFactory.getString("in_demDeitytab"));
		pnlMainTab = new EditorBasePanel(editType);

		jTabbedPane1.addTab(PropertyFactory.getString("in_demBase"), pnlMainTab);

		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				pnlDomains.setHeader(PropertyFactory.getString("in_demGrantDom"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_domains"), pnlDomains);

				buildFollowersTab();
				jTabbedPane1.addTab(PropertyFactory.getString("in_demFollowers"), pnlFollowers);

				pnlRaces.setHeader(PropertyFactory.getString("in_demRacWors"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_race"), pnlRaces);
				break;

			case EditorConstants.EDIT_SKILL:
				buildSynergyTab();
				pnlSynergy.setHeader(" ");
				jTabbedPane1.addTab(PropertyFactory.getString("in_demSynergy"), pnlSynergy);

				pnlClasses.setHeader(" ");
				jTabbedPane1.addTab(PropertyFactory.getString("in_demClasses"), pnlClasses);
				break;

			default:
				break;
		}

		pnlLanguages.setHeader(PropertyFactory.getString("in_demGrantLang"));
		jTabbedPane1.addTab(PropertyFactory.getString("in_languages"), pnlLanguages);

		pnlWeapons.setHeader(PropertyFactory.getString("in_demGraWeaPro"));
		jTabbedPane1.addTab(PropertyFactory.getString("in_weapon"), pnlWeapons);

		pnlSkills.setHeader(PropertyFactory.getString("in_demGraSkil"));
		pnlSkills.setLblSelectedText(PropertyFactory.getString("in_demSelClaSkil"));
		pnlSkills.setLblSelected2Text(PropertyFactory.getString("in_demSelCroCla"));
		jTabbedPane1.addTab(PropertyFactory.getString("in_skills"), pnlSkills);

		pnlAdvanced.setLayout(new GridBagLayout());

		pnlAdvanced.setName(PropertyFactory.getString("in_demLangTab"));
		pnlAdvancedAvailable.setLayout(new GridBagLayout());

		pnlAdvancedAvailable.setPreferredSize(new Dimension(259, 147));
		pnlAdvancedTag.setLayout(new GridBagLayout());

		lblAdvancedTag.setText(PropertyFactory.getString("in_demTag"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		pnlAdvancedTag.add(lblAdvancedTag, gridBagConstraints);

		cmbAdvancedTag.setEditable(true);
		cmbAdvancedTag.setPreferredSize(new Dimension(180, 25));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		pnlAdvancedTag.add(cmbAdvancedTag, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		pnlAdvancedAvailable.add(pnlAdvancedTag, gridBagConstraints);

		pnlAdvancedTagValue.setLayout(new GridBagLayout());

		lblAdvancedTagValue.setText(PropertyFactory.getString("in_demTagVal"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		pnlAdvancedTagValue.add(lblAdvancedTagValue, gridBagConstraints);

		pnllstAdvancedTagValue.setLayout(new BorderLayout());

		pnllstAdvancedTagValue.setPreferredSize(new Dimension(100, 16));
		scpAdvancedTagValue.setPreferredSize(new Dimension(259, 131));
		txtAdvancedTagValue.setLineWrap(true);
		txtAdvancedTagValue.addKeyListener(new KeyAdapter()
		{
			public void keyTyped(KeyEvent evt)
			{
				super.keyTyped(evt);
				txtAdvancedTagValueKeyTyped(evt);
			}
		}
		);

		scpAdvancedTagValue.setViewportView(txtAdvancedTagValue);

		pnllstAdvancedTagValue.add(scpAdvancedTagValue, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0.8;
		pnlAdvancedTagValue.add(pnllstAdvancedTagValue, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridheight = 8;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.8;
		pnlAdvancedAvailable.add(pnlAdvancedTagValue, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlAdvanced.add(pnlAdvancedAvailable, gridBagConstraints);

		pnlAdvancedButtons.setLayout(new GridBagLayout());

		btnAddAdvanced.setMnemonic(PropertyFactory.getMnemonic("in_mn_add", 0));
		btnAddAdvanced.setText(PropertyFactory.getString("in_add"));
		btnAddAdvanced.setPreferredSize(new Dimension(81, 26));
		btnAddAdvanced.setEnabled(false);
		btnAddAdvanced.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddAdvancedActionPerformed(evt);
			}
		}
		);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlAdvancedButtons.add(btnAddAdvanced, gridBagConstraints);

		btnRemoveAdvanced.setMnemonic(PropertyFactory.getMnemonic("in_mn_remove", 0));
		btnRemoveAdvanced.setText(PropertyFactory.getString("in_remove"));
		btnRemoveAdvanced.setPreferredSize(new Dimension(81, 26));
		btnRemoveAdvanced.setEnabled(false);
		btnRemoveAdvanced.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveAdvancedActionPerformed(evt);
			}
		}
		);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlAdvancedButtons.add(btnRemoveAdvanced, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.weighty = 1.0;
		pnlAdvanced.add(pnlAdvancedButtons, gridBagConstraints);

		pnlAdvancedSelected.setLayout(new GridBagLayout());

		lblAdvancedSelected.setText(PropertyFactory.getString("in_selected"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlAdvancedSelected.add(lblAdvancedSelected, gridBagConstraints);

		pnllstAdvancedSelected.setLayout(new BorderLayout());

		lstAdvancedSelected.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstAdvancedSelectedMouseClicked(evt);
			}
		}
		);

		scpAdvancedSelected.setViewportView(lstAdvancedSelected);

		pnllstAdvancedSelected.add(scpAdvancedSelected, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlAdvancedSelected.add(pnllstAdvancedSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlAdvanced.add(pnlAdvancedSelected, gridBagConstraints);

		lblAdvancedHeader.setText(PropertyFactory.getString("in_demMiscTags"));
		pnlAdvancedHeader.add(lblAdvancedHeader);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		pnlAdvanced.add(pnlAdvancedHeader, gridBagConstraints);

		jTabbedPane1.addTab(PropertyFactory.getString("in_demAdv"), pnlAdvanced);

		pnlTabs.add(jTabbedPane1, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 6;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 6.0;
		pnlMainDialog.add(pnlTabs, gridBagConstraints);

		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));

		btnCancel.setMnemonic(PropertyFactory.getMnemonic("in_mn_cancel", 0));
		btnCancel.setText(PropertyFactory.getString("in_cancel"));
		btnCancel.setPreferredSize(new Dimension(80, 26));
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnCancelActionPerformed(evt);
			}
		}
		);

		pnlButtons.add(btnCancel);

		btnSave.setMnemonic(PropertyFactory.getMnemonic("in_mn_save", 0));
		btnSave.setText(PropertyFactory.getString("in_save"));
		btnSave.setPreferredSize(new Dimension(80, 26));
		btnSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnSaveActionPerformed(evt);
			}
		}
		);

		pnlButtons.add(btnSave);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		pnlMainDialog.add(pnlButtons, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		getContentPane().add(pnlMainDialog, gridBagConstraints);

		pack();
	}


	///////////////////////
	// Advanced tab
	//

	private void txtAdvancedTagValueKeyTyped(KeyEvent evt)
	{
		//
		// TODO: the contents of the JTextArea have not yet had the effects of the key applied...
		//
//System.out.println("[" + txtAdvancedTagValue.getText().trim() + "] " + evt);
		btnAddAdvanced.setEnabled(txtAdvancedTagValue.getText().trim().length() != 0);
	}

	private void lstAdvancedSelectedMouseClicked(MouseEvent evt)
	{
		if (isDoubleClick(evt, lstAdvancedSelected, btnRemoveAdvanced))
		{
			btnRemoveAdvancedActionPerformed(null);
		}
	}

	private void btnRemoveAdvancedActionPerformed(ActionEvent evt)
	{
		btnRemoveAdvanced.setEnabled(false);

		final JListModel lms = (JListModel) lstAdvancedSelected.getModel();
		final Object[] x = lstAdvancedSelected.getSelectedValues();
		for (int i = 0; i < x.length; ++i)
		{
			String entry = (String) x[i];
			final int idx = entry.indexOf(":");
			if (idx >= 0)
			{
				final String tag = entry.substring(0, idx);
				cmbAdvancedTag.setSelectedItem(tag);
				entry = entry.substring(idx + 1);
				txtAdvancedTagValue.setText(entry);
			}
			lms.removeElement(x[i]);
		}
	}

	private void btnAddAdvancedActionPerformed(ActionEvent evt)
	{
		btnAddAdvanced.setEnabled(false);

		String newEntry = (String) cmbAdvancedTag.getSelectedItem() + ":" + txtAdvancedTagValue.getText().trim();
		final JListModel lmd = (JListModel) lstAdvancedSelected.getModel();
		lmd.addElement(newEntry);
	}

	///////////////////////
	// Followers tab
	//

	private void buildFollowersTab()
	{
		pnlFollowers.setExtraLayout(new GridBagLayout());

		lblQualifier.setText(PropertyFactory.getString("in_demQualifier"));
		lblQualifier.setLabelFor(cmbQualifier);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.WEST;
		pnlFollowers.addExtra(lblQualifier, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
//		gbc.weightx = 1.0;
		pnlFollowers.addExtra(cmbQualifier, gbc);

		cmbQualifier.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				cmbQualifierItemStateChanged(evt);
			}
		}
		);

		lblVariable.setText(PropertyFactory.getString("in_demVariable"));
		lblVariable.setLabelFor(cmbVariable);
		lblVariable.setEnabled(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.WEST;
		pnlFollowers.addExtra(lblVariable, gbc);

		cmbVariable.setEditable(true);
		cmbVariable.setEnabled(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
//		gbc.weightx = 1.0;
		pnlFollowers.addExtra(cmbVariable, gbc);

		pnlFollowers.setAddFilter(new EditorAddFilter()
		{
			public Object encode(Object anObj)
			{
				return encodeFollowerEntry((String) anObj);
			}

			public Object decode(Object anObj)
			{
				return decodeFollowerEntry((String) anObj);
			}
		}
		);
	}

	private String decodeFollowerEntry(String entry)
	{
		int idx = -1;
		if (entry.indexOf('[') >= 0)
		{
			for (int j = 0; j < cmbQualifier.getItemCount(); ++j)
			{
				final String qualifier = " [" + (String) cmbQualifier.getItemAt(j) + ':';
				idx = entry.indexOf(qualifier);
				if (idx >= 0)
				{
					break;
				}
			}
		}

		if (idx >= 0)
		{
			entry = entry.substring(0, idx);
		}
		return entry;
	}

	private String encodeFollowerEntry(String newEntry)
	{
		String condition = null;
		final String qualifier = (String) cmbQualifier.getSelectedItem();
		if ((qualifier != null) && !"(None)".equalsIgnoreCase(qualifier))
		{
			condition = " [" + qualifier + ':' + (String) cmbVariable.getSelectedItem() + ']';
		}

		if (condition != null)
		{
			newEntry += condition;
		}
		return newEntry;
	}

	//
	// User has changed the selection in the qualifier combo. If they've selected "(None)" then disable choosing from the variable name
	// combo. Otherwise enable it
	//
	private void cmbQualifierItemStateChanged(ItemEvent evt)
	{
		final String qualifier = (String) cmbQualifier.getSelectedItem();
		if ((qualifier != null) && !"(None)".equalsIgnoreCase(qualifier))
		{
			cmbVariable.setEnabled(true);
			lblVariable.setEnabled(true);
		}
		else
		{
			cmbVariable.setEnabled(false);
			lblVariable.setEnabled(false);
		}
	}

	///////////////////////
	// Synergy tab
	//
	private void buildSynergyTab()
	{
		pnlSynergy.setExtraLayout(new GridBagLayout());

		lblQualifier.setText(PropertyFactory.getString("in_demSkillRank"));
		lblQualifier.setLabelFor(cmbQualifier);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.WEST;
		pnlSynergy.addExtra(lblQualifier, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
//		gbc.weightx = 1.0;
		pnlSynergy.addExtra(cmbQualifier, gbc);

		lblVariable.setText(PropertyFactory.getString("in_demSynergyBonus"));
		lblVariable.setLabelFor(cmbVariable);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.WEST;
		pnlSynergy.addExtra(lblVariable, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
//		gbc.weightx = 1.0;
		pnlSynergy.addExtra(cmbVariable, gbc);

		pnlSynergy.setAddFilter(new EditorAddFilter()
		{
			public Object encode(Object anObj)
			{
				return encodeSynergyEntry((String) anObj);
			}

			public Object decode(Object anObj)
			{
				return decodeSynergyEntry((String) anObj);
			}
		}
		);
	}

	private String decodeSynergyEntry(String entry)
	{
		int idx = -1;
		if (entry.indexOf('=') >= 0)
		{
			for (int j = 0; j < cmbQualifier.getItemCount(); ++j)
			{
				final String qualifier = '=' + (String) cmbQualifier.getItemAt(j) + '=';
				idx = entry.indexOf(qualifier);
				if (idx >= 0)
				{
					break;
				}
			}
		}

		if (idx >= 0)
		{
			entry = entry.substring(0, idx);
		}
		return entry;
	}

	private String encodeSynergyEntry(String newEntry)
	{
		String condition = null;
		String qualifier = null;
		if (cmbQualifier.getSelectedIndex() >= 0)
		{
			qualifier = cmbQualifier.getSelectedItem().toString();
			if (cmbVariable.getSelectedIndex() >= 0)
			{
				condition = cmbVariable.getSelectedItem().toString();
			}
		}
		return encodeSynergyEntry(newEntry, qualifier, condition);
	}

	private static String encodeSynergyEntry(String newEntry, String qualifier, String condition)
	{
		if ((qualifier != null) && (condition != null))
		{
			newEntry = newEntry + '=' + qualifier + '=' + condition;
		}
		return newEntry;
	}

	//////////////////////////////////////////

	private void btnSaveActionPerformed(ActionEvent evt)
	{
		String aString = pnlMainTab.getNameText();
		if (aString.length() == 0)
		{
			JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_demMes1"), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Object[] sel;

		thisPObject.setName(aString);
		//
		// Save source info
		//
		thisPObject.setSource(".CLEAR");
		aString = pnlMainTab.getSourceText();
		if (aString.length() != 0)
		{
			thisPObject.setSource("SOURCEPAGE:" + aString);
		}

		//
		// Save P.I. flag
		//
		thisPObject.setNameIsPI(pnlMainTab.getProductIdentity());

		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				((Deity) thisPObject).setHolyItem(pnlMainTab.getHolyItemText());
				((Deity) thisPObject).setAlignment(pnlMainTab.getDeityAlignment());
				((Deity) thisPObject).setDescription(pnlMainTab.getDescriptionText());

				//
				// Save favored weapon(s)
				//
				if (pnlMainTab.getFavoredWeaponsAvailableList().length == 0)
				{
					aString = "Any";
				}
				else
				{
					sel = pnlMainTab.getFavoredWeaponsSelectedList();
					aString = delimitArray(sel, '|');
				}
				((Deity) thisPObject).setFavoredWeapon(aString);

				//
				// Save granted domains
				//
				if (pnlDomains.getAvailableList().length == 0)
				{
					aString = "ALL";
				}
				else
				{
					sel = pnlDomains.getSelectedList();
					aString = delimitArray(sel, ',');
				}
				((Deity) thisPObject).setDomainListString(aString);

				//
				// Save follower alignments
				//
				sel = pnlFollowers.getSelectedList();
				StringBuffer tbuf = new StringBuffer(100);
				for (int i = 0; i < sel.length; ++i)
				{
					String qualifier = null;
					aString = (String) sel[i];
					int idx = aString.indexOf(" [VARDEFINED:");
					if (idx >= 0)
					{
						qualifier = aString.substring(idx + 1);
						if (qualifier.endsWith("]"))
						{
							qualifier = qualifier.substring(0, qualifier.length() - 1);
						}
						qualifier = qualifier.replace(':', '=');
						aString = aString.substring(0, idx);
					}
					for (int align = 0; align < Globals.getAlignmentList().size(); ++align)
					{
						if (aString.equals(Globals.getLongAlignmentAtIndex(align)))
						{
							if (qualifier != null)
							{
								tbuf.append(qualifier).append('=');
							}
							tbuf.append(align);
							if (qualifier != null)
							{
								tbuf.append(']');
							}
						}
					}
				}
				((Deity) thisPObject).setFollowerAlignments(tbuf.toString());

				//
				// Save racial worshippers (no need to explicitly clear)
				//
				sel = pnlRaces.getSelectedList();
				aString = delimitArray(sel, '|');
				((Deity) thisPObject).setRaceList(aString);
				break;

			case EditorConstants.EDIT_SKILL:
				sel = pnlMainTab.getTypesSelectedList();
				((Skill) thisPObject).setType("");
				for (int i = 0; i < sel.length; ++i)
				{
					((Skill) thisPObject).addType(sel[i].toString());
				}
				((Skill) thisPObject).setUntrained(pnlMainTab.getIsUntrained() ? "Y" : "N");
				((Skill) thisPObject).setIsExclusive(pnlMainTab.getIsExclusive());
				((Skill) thisPObject).setKeyStat(pnlMainTab.getKeyStat());
				((Skill) thisPObject).setACheck(pnlMainTab.getArmorCheck());

				((Skill) thisPObject).getSynergyList().clear();
				sel = pnlSynergy.getSelectedList();
				for (int i = 0; i < sel.length; ++i)
				{
					((Skill) thisPObject).addSynergyList(sel[i].toString());
				}

				((Skill) thisPObject).getClassList().clear();
				sel = pnlClasses.getSelectedList2();
				for (int i = 0; i < sel.length; ++i)
				{
					((Skill) thisPObject).addClassList('!' + sel[i].toString());
				}

				sel = pnlClasses.getSelectedList();
				for (int i = 0; i < sel.length; ++i)
				{
					((Skill) thisPObject).addClassList(sel[i].toString());
				}
				break;

			default:
				break;
		}

		//
		// Save granted languages
		//
		thisPObject.setLanguageAutos(".CLEAR");
		sel = pnlLanguages.getSelectedList();
		aString = delimitArray(sel, ',');
		thisPObject.setLanguageAutos(aString);

		//
		// Save auto weapon proficiencies
		//
		thisPObject.setWeaponProfAutos(".CLEAR");
		sel = pnlWeapons.getSelectedList();
		aString = delimitArray(sel, '|');
		thisPObject.setWeaponProfAutos(aString);

// TODO: check if all skills of one type are selected...maybe change to TYPE.blah?

		//
		// Save granted class skills
		//
		thisPObject.setCSkillList(".CLEAR");
		sel = pnlSkills.getSelectedList();
		aString = delimitArray(sel, '|');
		thisPObject.setCSkillList(aString);

		//
		// Save granted cross class skills
		//
		thisPObject.setCcSkillList(".CLEAR");
		sel = pnlSkills.getSelectedList2();
		aString = delimitArray(sel, '|');
		thisPObject.setCcSkillList(aString);

		//
		// Save advanced tab info
		//
		//
		// Make sure the lists are all empty to start
		//
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				((Deity) thisPObject).getPantheonList().clear();
				break;

			case EditorConstants.EDIT_SKILL:
				((Skill) thisPObject).addChoiceList(".CLEAR");
				break;

			default:
				break;
		}
		thisPObject.getBonusList().clear();
		thisPObject.clearVariableList();
		thisPObject.setDR(-9, ".CLEAR");
		thisPObject.addPreReq("PRE:.CLEAR");
		thisPObject.setSpecialAbilityList(".CLEAR", -9);
		thisPObject.setSR(null);

		sel = ((JListModel) lstAdvancedSelected.getModel()).getElements();
		for (int i = 0; i < sel.length; ++i)
		{
			aString = (String) sel[i];
			if (aString.startsWith("BONUS:"))
			{
				thisPObject.addBonusList(aString.substring(6));
			}
			else if ((editType == EditorConstants.EDIT_SKILL) && (aString.startsWith("CHOOSE:")))
			{
				((Skill) thisPObject).addChoiceList(aString.substring(7));
			}
			else if (aString.startsWith("DEFINE:"))
			{
				thisPObject.addVariable("-9|" + aString.substring(7));
			}
			else if (aString.startsWith("DR:"))
			{
				thisPObject.setDR(-9, aString.substring(3));
			}
			else if ((editType == EditorConstants.EDIT_DEITY) && (aString.startsWith("PANTHEON:")))
			{
				((Deity) thisPObject).addPantheonList(aString.substring(9));
			}
			else if (aString.startsWith("PRE") || aString.startsWith("!PRE"))
			{
				thisPObject.addPreReq(aString);
			}
			else if (aString.startsWith("SA:"))
			{
				thisPObject.setSpecialAbilityList(aString.substring(3), -9);
			}
			else if (aString.startsWith("SR:"))
			{
				thisPObject.setSR(aString.substring(3));
			}
			else
			{
				System.err.println("Unknown advanced setting: " + aString);
			}
		}

		thisPObject.addType(Constants.s_CUSTOM);

		wasCancelled = false;
		closeDialog(null);
	}

	private static String delimitArray(Object[] objArray, char delim)
	{
		StringBuffer tbuf = new StringBuffer(100);
		for (int i = 0; i < objArray.length; ++i)
		{
			if (tbuf.length() != 0)
			{
				tbuf.append(delim);
			}
			tbuf.append(objArray[i].toString());
		}
		return tbuf.toString();
	}

	private void btnCancelActionPerformed(ActionEvent evt)
	{
		wasCancelled = true;
		closeDialog(null);
	}

	/** Closes the dialog */
	private void closeDialog(WindowEvent evt)
	{
		hide();
		dispose();
	}

	//
	// Remove selected object(s) from src and insert it/them in dst
	//
	private static void swapEntries(JList dst, JList src, JButton btn)
	{
		btn.setEnabled(false);

		final JListModel lms = (JListModel) src.getModel();
		final JListModel lmd = (JListModel) dst.getModel();
		final Object[] x = src.getSelectedValues();
		for (int i = 0; i < x.length; ++i)
		{
			lmd.addElement(x[i]);
			lms.removeElement(x[i]);
		}
	}

	private static boolean isDoubleClick(MouseEvent evt, JList lst, JButton btn)
	{
		if (lst.getMinSelectionIndex() >= 0)
		{
			switch (evt.getClickCount())
			{
				case 1:
					btn.setEnabled(true);
					break;

				case 2:
					if (btn.isEnabled())
					{
						return true;
					}
					break;

				default:
					break;
			}
		}
		return false;
	}

	public boolean wasCancelled()
	{
		return wasCancelled;
	}
}
