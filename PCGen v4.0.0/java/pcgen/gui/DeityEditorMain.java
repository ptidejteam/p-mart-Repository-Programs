/*
 * DeityEditorMain.java
 *
 * @(#) $Id: DeityEditorMain.java,v 1.1 2006/02/21 00:47:12 vauchers Exp $
 *
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
 * Created on August 27, 2002, 3:14 PM
 *
 * @version $Revision: 1.1 $
 */

/**
 *
 * @author  Greg Bingleman
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
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
import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.WeaponProf;

public class DeityEditorMain extends JFrame
{
	private Deity thisDeity = null;
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
// tags from Domain:
// CHOOSE
// DESC
// FEAT
// QUALIFY
//
	private static String[] qualifiers = new String[] {"(None)", "VARDEFINED"};
	private static String[] tags = new String[]
	 {"BONUS", "DEFINE", "DR", "KEY", "PANTHEON",
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
	  "QUALIFY", /*"RESTRICT",*/ "SA", "SPELL", "SR", "UMDAM", "UMULT", "VISION" };


    /** Creates new form DeityEditorMain */
	public DeityEditorMain(Deity argDeity)
	{
		super();
		if (argDeity == null)
		{
			thisDeity = new Deity();
		}
		else
		{
			thisDeity = (Deity)argDeity.clone();
		}
		initComponents();
		intComponentContents();

		Utility.centerFrame((JFrame)this, false);

	}

	private void intComponentContents()
	{
		Utility.maybeSetIcon((Frame)this/*getParent()*/, "PcgenIcon.gif");

 		txtDeityName.setText(thisDeity.getName());
		txtHolyItem.setText(thisDeity.getHolyItem());
		txtDescription.setText(thisDeity.getDescription());
		chkProductIdentity.setSelected(thisDeity.getNameIsPI());

		//
		// Initialize the contents of the deity's alignment combo
		//
		final String deityAlignment = thisDeity.getDeityAlignment();
		int alignmentIdx = -1;
		String alignments[] = new String[9];
		for (int i = 0; i < 9; ++i)
		{
			alignments[i] = Constants.s_ALIGNLONG[i];
			if (deityAlignment.equals(Constants.s_ALIGNSHORT[i]))
			{
				alignmentIdx = i;
			}
		}
		cmbDeityAlignment.setModel(new DefaultComboBoxModel(alignments));
		cmbDeityAlignment.setSelectedIndex(alignmentIdx);

		Iterator e;
		String aString;

		txtSource.setText(thisDeity.getSourcePage());

		//
		// Initialize the contents of the available and selected domains lists
		//
		ArrayList selectedList = new ArrayList();
		ArrayList availableList = new ArrayList();
		for (e = Globals.getDomainList().iterator(); e.hasNext(); )
		{
			final Domain aDomain = (Domain)e.next();
			if (thisDeity.hasDomainNamed(aDomain.getName()))
			{
				selectedList.add(aDomain);
			}
			else
			{
				availableList.add(aDomain);
			}
		}
		lstDomainsAvailable.setModel(new JListModel(availableList, true));
		lstDomainsSelected.setModel(new JListModel(selectedList, true));

		//
		// Initialize the contents of the available and selected languages lists
		//
		selectedList.clear();
		availableList.clear();
		final Set aSet = thisDeity.getAutoLanguages();
		for (e = Globals.getLanguageList().iterator(); e.hasNext(); )
		{
			final Language aLang = (Language)e.next();
			if (aSet.contains(aLang))
			{
				selectedList.add(aLang);
			}
			else
			{
				availableList.add(aLang);
			}
		}
		lstLanguagesAvailable.setModel(new JListModel(availableList, true));
		lstLanguagesSelected.setModel(new JListModel(selectedList, true));

		//
		// Initialize the contents of the available and selected races list
		//
		selectedList.clear();
		availableList.clear();
		final ArrayList raceList = thisDeity.getRaceList();
		for (e = Globals.getRaceMap().values().iterator(); e.hasNext(); )
		{
			final Race aRace = (Race)e.next();
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
		lstRacesAvailable.setModel(new JListModel(availableList, true));
		lstRacesSelected.setModel(new JListModel(selectedList, true));

		//
		// Initialize the contents of the available and selected weapon prof lists
		//
		selectedList.clear();
		availableList = (ArrayList)Globals.getWeaponProfList().clone();
		final ArrayList autoWeap = thisDeity.getWeaponProfAutos();
		if (autoWeap != null)
		{
			for (e = autoWeap.iterator(); e.hasNext(); )
			{
				final WeaponProf wp = Globals.getWeaponProfNamed((String)e.next());
				if (wp != null)
				{
					selectedList.add(wp);
					availableList.remove(wp);
				}
			}
		}
		lstWeaponsAvailable.setModel(new JListModel(availableList, true));
		lstWeaponsSelected.setModel(new JListModel(selectedList, true));

		//
		// Initialize the contents of the available and selected favored weapons lists
		//
		selectedList.clear();
		availableList = (ArrayList)Globals.getWeaponProfList().clone();
		StringTokenizer aTok = new StringTokenizer(thisDeity.getFavoredWeapon(), "|", false);
		while (aTok.hasMoreTokens())
		{
			String deityWeap = aTok.nextToken();
			if (deityWeap.equalsIgnoreCase("ALL") || deityWeap.equalsIgnoreCase("ANY"))
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
		lstFavoredWeaponAvailable.setModel(new JListModel(availableList, true));
		lstFavoredWeaponSelected.setModel(new JListModel(selectedList, true));

		//
		// Initialize the contents of the available and selected class/cross-class lists
		//
		selectedList.clear();
//		availableList = (ArrayList)Globals.getSkillList().clone();
		availableList.clear();
		for (e = Globals.getSkillList().iterator(); e.hasNext(); )
		{
			final Skill aSkill = (Skill)e.next();
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

		ArrayList skills = thisDeity.getCSkillList();
		if (skills != null)
		{
			for (e = skills.iterator(); e.hasNext(); )
			{
				aString = (String)e.next();
				selectedList.add(aString);
				if (availableList.contains(aString))
				{
					availableList.remove(aString);
				}
			}
		}
		lstSkillsSelectedClass.setModel(new JListModel(selectedList, true));

		selectedList.clear();
		skills = thisDeity.getCcSkillList();
		if (skills != null)
		{
			for (e = skills.iterator(); e.hasNext(); )
			{
				aString = (String)e.next();
				selectedList.add(aString);
				if (availableList.contains(aString))
				{
					availableList.remove(aString);
				}
			}
		}
		lstSkillsSelectedCrossClass.setModel(new JListModel(selectedList, true));

		lstSkillsAvailable.setModel(new JListModel(availableList, true));

		//
		// Initialize the lists of available and selected follower alignments
		//
		availableList.clear();
		selectedList.clear();
		for (int i = 0; i < alignments.length; ++i)
		{
			availableList.add(alignments[i]);
		}
		final String followerAlignments = thisDeity.getFollowerAlignments();
		parseAlignment(availableList, selectedList, followerAlignments, null);

		lstFollowersAvailable.setModel(new JListModel(availableList, true));
		lstFollowersSelected.setModel(new JListModel(selectedList, true));

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

		//
		// Tags on advanced tag
		//
		cmbAdvancedTag.setModel(new DefaultComboBoxModel(tags));
		cmbAdvancedTag.setSelectedIndex(0);

		//
		// Initialize the list of advanced items in the selected list
		//
		selectedList.clear();
		for (e = thisDeity.getPantheonList().iterator(); e.hasNext(); )
		{
			selectedList.add("PANTHEON:" + (String)e.next());
		}
		for (e = thisDeity.getBonusList().iterator(); e.hasNext(); )
		{
			selectedList.add("BONUS:" + (String)e.next());
		}
		for (int i = 0, x = thisDeity.getPreReqCount(); i < x; ++i)
		{
			selectedList.add(thisDeity.getPreReq(i));
		}
		for (int i = 0, x = thisDeity.getVariableCount(); i < x; ++i)
		{
			aString = thisDeity.getVariable(i);
			if (aString.startsWith("-9|"))
			{
				aString = aString.substring(3);
			}
			selectedList.add("DEFINE:" + aString);
		}
		final ArrayList saList = thisDeity.getSpecialAbilityList();
		if ((saList != null) && (saList.size() != 0))
		{
			for (e = saList.iterator(); e.hasNext(); )
			{
				selectedList.add("SA:" + ((SpecialAbility)e.next()).toString());
			}
		}
		aString = thisDeity.getDR();
		if (aString != null)
		{
			aTok = new StringTokenizer(aString, "|", false);
			while (aTok.hasMoreTokens())
			{
				selectedList.add("DR:" + aTok.nextToken());
			}
		}
		aString = thisDeity.getSRFormula();
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
						final String qualifierType  = aTok.nextToken();
						final String variableName   = aTok.nextToken();
						parseAlignment(availableList, selectedList, aTok.nextToken(), " [" + qualifierType + ":" + variableName + "]");
					}
					i = idx;
				}
			}
			else if ((alignmentChar >= '0') && (alignmentChar <= '9'))
			{
				final int idx = (int)alignmentChar - '0';
				availableList.remove(Constants.s_ALIGNLONG[idx]);
				selectedList.add(Constants.s_ALIGNLONG[idx] + (qualifier == null ? "" : qualifier));
			}
		}
	}

	private void addVariables(ArrayList availableList, Collection objList)
	{
		for (Iterator e = objList.iterator(); e.hasNext(); )
		{
			final Object obj = e.next();
			if (obj instanceof PObject)
			{
				final int varCount = ((PObject)obj).getVariableCount();
				if (varCount != 0)
				{
					for (int i = 0; i < varCount; ++i)
					{
						final StringTokenizer aTok = new StringTokenizer(((PObject)obj).getVariable(i), "|", false);
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
				Globals.errorPrint("Unsupported type in addVariables: " + obj.getClass().getName());
			}
		}
	}


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
		GridBagConstraints gridBagConstraints;

		btnAddAdvanced = new JButton();
		btnAddClassSkill = new JButton();
		btnAddCrossClassSkill = new JButton();
		btnAddDomain = new JButton();
		btnAddFavoredWeapon = new JButton();
		btnAddFollower = new JButton();
		btnAddLanguage = new JButton();
		btnAddRace = new JButton();
		btnAddWeapon = new JButton();
		btnCancel = new JButton();
		btnRemoveAdvanced = new JButton();
		btnRemoveClassSkill = new JButton();
		btnRemoveCrossClassSkill = new JButton();
		btnRemoveDomain = new JButton();
		btnRemoveFavoredWeapon = new JButton();
		btnRemoveFollower = new JButton();
		btnRemoveLanguage = new JButton();
		btnRemoveRace = new JButton();
		btnRemoveWeapon = new JButton();
		btnSave = new JButton();
		chkProductIdentity = new JCheckBox();
		cmbAdvancedTag = new JComboBox();
		cmbDeityAlignment = new JComboBox();
		cmbQualifier = new JComboBox();
		cmbVariable = new JComboBox();
		jTabbedPane1 = new JTabbedPane();
		lbFollowersAvailable = new JLabel();
		lblAdvancedHeader = new JLabel();
		lblAdvancedSelected = new JLabel();
		lblAdvancedTag = new JLabel();
		lblAdvancedTagValue = new JLabel();
		lblDeityAlignment = new JLabel();
		lblDeityFavoredWeapon = new JLabel();
		lblDeityName = new JLabel();
		lblDescription = new JLabel();
		lblDomainsAvailable = new JLabel();
		lblDomainsHeader = new JLabel();
		lblDomainsSelected = new JLabel();
		lblFavoredWeaponAvailable = new JLabel();
		lblFavoredWeaponSelected = new JLabel();
		lblFollowersHeader = new JLabel();
		lblFollowersSelected = new JLabel();
		lblHolyItem = new JLabel();
		lblLanguagesAvailable = new JLabel();
		lblLanguagesHeader = new JLabel();
		lblLanguagesSelected = new JLabel();
		lblQualifier = new JLabel();
		lblRacesAvailable = new JLabel();
		lblRacesHeader = new JLabel();
		lblRacesSelected = new JLabel();
		lblSkillsAvailable = new JLabel();
		lblSkillsHeader = new JLabel();
		lblSkillsSelectedClass = new JLabel();
		lblSkillsSelectedCrossClass = new JLabel();
		lblSource = new JLabel();
		lblVariable = new JLabel();
		lblWeaponsAvailable = new JLabel();
		lblWeaponsHeader = new JLabel();
		lblWeaponsSelected = new JLabel();
		lstAdvancedSelected = new JList();
		lstDomainsAvailable = new JList();
		lstDomainsSelected = new JList();
		lstFavoredWeaponAvailable = new JList();
		lstFavoredWeaponSelected = new JList();
		lstFollowersAvailable = new JList();
		lstFollowersSelected = new JList();
		lstLanguagesAvailable = new JList();
		lstLanguagesSelected = new JList();
		lstRacesAvailable = new JList();
		lstRacesSelected = new JList();
		lstSkillsAvailable = new JList();
		lstSkillsSelectedClass = new JList();
		lstSkillsSelectedCrossClass = new JList();
		lstWeaponsAvailable = new JList();
		lstWeaponsSelected = new JList();
		pnlAdvanced = new JPanel();
		pnlAdvancedAvailable = new JPanel();
		pnlAdvancedButtons = new JPanel();
		pnlAdvancedHeader = new JPanel();
		pnlAdvancedSelected = new JPanel();
		pnlAdvancedTag = new JPanel();
		pnlAdvancedTagValue = new JPanel();
		pnlButtons = new JPanel();
		pnlDeityAlignment = new JPanel();
		pnlDeityName = new JPanel();
		pnlDescription = new JPanel();
		pnlDescriptionText = new JPanel();
		pnlDomains = new JPanel();
		pnlDomainsAvailable = new JPanel();
		pnlDomainsButtons = new JPanel();
		pnlDomainsHeader = new JPanel();
		pnlDomainsSelected = new JPanel();
		pnlFavoredWeapon = new JPanel();
		pnlFavoredWeaponAvailable = new JPanel();
		pnlFavoredWeaponHeader = new JPanel();
		pnlFavoredWeaponSelected = new JPanel();
		pnlFavoredWeaponsButtons = new JPanel();
		pnlFollowers = new JPanel();
		pnlFollowersAvailable = new JPanel();
		pnlFollowersButtons = new JPanel();
		pnlFollowersHeader = new JPanel();
		pnlFollowersSelected = new JPanel();
		pnlHolyItem = new JPanel();
		pnlLanguages = new JPanel();
		pnlLanguagesAvailable = new JPanel();
		pnlLanguagesButtons = new JPanel();
		pnlLanguagesHeader = new JPanel();
		pnlLanguagesSelected = new JPanel();
		pnlMainDialog = new JPanel();
		pnlMainTab = new JPanel();
		pnlProductIdentity = new JPanel();
		pnlQualifier = new JPanel();
		pnlRaces = new JPanel();
		pnlRacesAvailable = new JPanel();
		pnlRacesButtons = new JPanel();
		pnlRacesHeader = new JPanel();
		pnlRacesSelected = new JPanel();
		pnlSkills = new JPanel();
		pnlSkillsAvailable = new JPanel();
		pnlSkillsButtonsClass = new JPanel();
		pnlSkillsButtonsCrossClass = new JPanel();
		pnlSkillsHeader = new JPanel();
		pnlSkillsSelectedClass = new JPanel();
		pnlSkillsSelectedCrossClass = new JPanel();
		pnlSource = new JPanel();
		pnlTabs = new JPanel();
		pnlVariable = new JPanel();
		pnlVariableQualifier = new JPanel();
		pnlWeapons = new JPanel();
		pnlWeaponsAvailable = new JPanel();
		pnlWeaponsButtons = new JPanel();
		pnlWeaponsHeader = new JPanel();
		pnlWeaponsSelected = new JPanel();
		pnllstAdvancedSelected = new JPanel();
		pnllstAdvancedTagValue = new JPanel();
		pnllstDomainsAvailable = new JPanel();
		pnllstDomainsSelected = new JPanel();
		pnllstFavoredWeaponAvailable = new JPanel();
		pnllstFavoredWeaponSelected = new JPanel();
		pnllstFollowersAvailable = new JPanel();
		pnllstFollowersSelected = new JPanel();
		pnllstLanguagesAvailable = new JPanel();
		pnllstLanguagesSelected = new JPanel();
		pnllstRacesAvailable = new JPanel();
		pnllstRacesSelected = new JPanel();
		pnllstSkillsAvailable = new JPanel();
		pnllstSkillsSelectedClass = new JPanel();
		pnllstSkillsSelectedCrossClass = new JPanel();
		pnllstWeaponsAvailable = new JPanel();
		pnllstWeaponsSelected = new JPanel();
		scpAdvancedSelected = new JScrollPane();
		scpAdvancedTagValue = new JScrollPane();
		scpDescription = new JScrollPane();
		scpDomainsAvailable = new JScrollPane();
		scpDomainsSelected = new JScrollPane();
		scpFavoredWeaponAvailable = new JScrollPane();
		scpFavoredWeaponSelected = new JScrollPane();
		scpFollowersAvailable = new JScrollPane();
		scpFollowersSelected = new JScrollPane();
		scpLanguagesAvailable = new JScrollPane();
		scpLanguagesSelected = new JScrollPane();
		scpRacesAvailable = new JScrollPane();
		scpRacesSelected = new JScrollPane();
		scpSkillsAvailable = new JScrollPane();
		scpSkillsSelectedClass = new JScrollPane();
		scpSkillsSelectedCrossClass = new JScrollPane();
		scpWeaponsAvailable = new JScrollPane();
		scpWeaponsSelected = new JScrollPane();
		txtAdvancedTagValue = new JTextArea();
		txtDeityName = new JTextField();
		txtDescription = new JTextArea();
		txtHolyItem = new JTextField();
		txtSource = new JTextField();

		getContentPane().setLayout(new GridBagLayout());

		setTitle("Deity Editor");
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				closeDialog(evt);
			}
		});

		pnlMainDialog.setLayout(new GridBagLayout());

		pnlMainDialog.setPreferredSize(new Dimension(640, 440));
		pnlTabs.setLayout(new BorderLayout());

		pnlTabs.setMinimumSize(new Dimension(128, 88));
		pnlTabs.setPreferredSize(new Dimension(640, 440));
		jTabbedPane1.setName("deitytab");
		pnlMainTab.setLayout(new GridBagLayout());

		pnlDeityName.setLayout(new GridBagLayout());

		lblDeityName.setText("Deity Name");
		lblDeityName.setDisplayedMnemonic(78);
		lblDeityName.setLabelFor(txtDeityName);
		lblDeityName.setMaximumSize(new Dimension(70, 16));
		lblDeityName.setMinimumSize(new Dimension(70, 16));
		lblDeityName.setPreferredSize(new Dimension(70, 16));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlDeityName.add(lblDeityName, gridBagConstraints);

		txtDeityName.setPreferredSize(new Dimension(280, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlDeityName.add(txtDeityName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		pnlMainTab.add(pnlDeityName, gridBagConstraints);

		pnlHolyItem.setLayout(new GridBagLayout());

		lblHolyItem.setText("Holy Item");
		lblHolyItem.setDisplayedMnemonic(72);
		lblHolyItem.setLabelFor(txtHolyItem);
		lblHolyItem.setMaximumSize(new Dimension(70, 16));
		lblHolyItem.setMinimumSize(new Dimension(70, 16));
		lblHolyItem.setPreferredSize(new Dimension(70, 16));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlHolyItem.add(lblHolyItem, gridBagConstraints);

		txtHolyItem.setPreferredSize(new Dimension(280, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlHolyItem.add(txtHolyItem, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		pnlMainTab.add(pnlHolyItem, gridBagConstraints);

		pnlDeityAlignment.setLayout(new GridBagLayout());

		lblDeityAlignment.setText("Deity's Alignment");
		lblDeityAlignment.setDisplayedMnemonic(108);
		lblDeityAlignment.setLabelFor(cmbDeityAlignment);
		lblDeityAlignment.setPreferredSize(new Dimension(108, 16));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlDeityAlignment.add(lblDeityAlignment, gridBagConstraints);

		cmbDeityAlignment.setPreferredSize(new Dimension(180, 25));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlDeityAlignment.add(cmbDeityAlignment, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		pnlMainTab.add(pnlDeityAlignment, gridBagConstraints);

		pnlDescription.setLayout(new GridBagLayout());

		lblDescription.setText("Description");
		lblDescription.setDisplayedMnemonic(68);
		lblDescription.setLabelFor(txtDescription);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		pnlDescription.add(lblDescription, gridBagConstraints);

		pnlDescriptionText.setLayout(new BorderLayout());

		txtDescription.setLineWrap(true);
		txtDescription.setWrapStyleWord(true);
		scpDescription.setViewportView(txtDescription);

		pnlDescriptionText.add(scpDescription, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.8;
		pnlDescription.add(pnlDescriptionText, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlMainTab.add(pnlDescription, gridBagConstraints);

		pnlFavoredWeapon.setLayout(new GridBagLayout());

		pnlFavoredWeaponAvailable.setLayout(new GridBagLayout());

		pnllstFavoredWeaponAvailable.setLayout(new BorderLayout());

		lstFavoredWeaponAvailable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstFavoredWeaponAvailableMouseClicked(evt);
			}
		});

		scpFavoredWeaponAvailable.setViewportView(lstFavoredWeaponAvailable);

		pnllstFavoredWeaponAvailable.add(scpFavoredWeaponAvailable, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlFavoredWeaponAvailable.add(pnllstFavoredWeaponAvailable, gridBagConstraints);

		lblFavoredWeaponAvailable.setText("Available");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlFavoredWeaponAvailable.add(lblFavoredWeaponAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlFavoredWeapon.add(pnlFavoredWeaponAvailable, gridBagConstraints);

		pnlFavoredWeaponsButtons.setLayout(new GridBagLayout());

		btnAddFavoredWeapon.setMnemonic('A');
		btnAddFavoredWeapon.setText("Add");
		btnAddFavoredWeapon.setMaximumSize(new Dimension(81, 26));
		btnAddFavoredWeapon.setMinimumSize(new Dimension(81, 26));
		btnAddFavoredWeapon.setPreferredSize(new Dimension(81, 26));
		btnAddFavoredWeapon.setEnabled(false);
		btnAddFavoredWeapon.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddFavoredWeaponActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlFavoredWeaponsButtons.add(btnAddFavoredWeapon, gridBagConstraints);

		btnRemoveFavoredWeapon.setMnemonic('R');
		btnRemoveFavoredWeapon.setText("Remove");
		btnRemoveFavoredWeapon.setMaximumSize(new Dimension(81, 26));
		btnRemoveFavoredWeapon.setMinimumSize(new Dimension(81, 26));
		btnRemoveFavoredWeapon.setEnabled(false);
		btnRemoveFavoredWeapon.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveFavoredWeaponActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlFavoredWeaponsButtons.add(btnRemoveFavoredWeapon, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 0.2;
		pnlFavoredWeapon.add(pnlFavoredWeaponsButtons, gridBagConstraints);

		lblDeityFavoredWeapon.setText("Deity's Favored Weapons");
		pnlFavoredWeaponHeader.add(lblDeityFavoredWeapon);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlFavoredWeapon.add(pnlFavoredWeaponHeader, gridBagConstraints);

		pnlFavoredWeaponSelected.setLayout(new GridBagLayout());

		pnllstFavoredWeaponSelected.setLayout(new BorderLayout());

		lstFavoredWeaponSelected.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstFavoredWeaponSelectedMouseClicked(evt);
			}
		});

		scpFavoredWeaponSelected.setViewportView(lstFavoredWeaponSelected);

		pnllstFavoredWeaponSelected.add(scpFavoredWeaponSelected, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlFavoredWeaponSelected.add(pnllstFavoredWeaponSelected, gridBagConstraints);

		lblFavoredWeaponSelected.setText("Selected");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlFavoredWeaponSelected.add(lblFavoredWeaponSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlFavoredWeapon.add(pnlFavoredWeaponSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlMainTab.add(pnlFavoredWeapon, gridBagConstraints);

		pnlProductIdentity.setLayout(new FlowLayout(FlowLayout.RIGHT));

		chkProductIdentity.setMnemonic('P');
		chkProductIdentity.setText("Product Identity");
		chkProductIdentity.setHorizontalTextPosition(SwingConstants.LEADING);
		pnlProductIdentity.add(chkProductIdentity);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlMainTab.add(pnlProductIdentity, gridBagConstraints);

		pnlSource.setLayout(new GridBagLayout());

		lblSource.setText("Source");
		lblSource.setDisplayedMnemonic(117);
		lblSource.setLabelFor(txtSource);
		lblSource.setPreferredSize(new Dimension(70, 16));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlSource.add(lblSource, gridBagConstraints);

		txtSource.setPreferredSize(new Dimension(280, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.9;
		pnlSource.add(txtSource, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlMainTab.add(pnlSource, gridBagConstraints);

		jTabbedPane1.addTab("Base", pnlMainTab);

		pnlDomains.setLayout(new GridBagLayout());

		pnlDomains.setName("deityLanguageTab");
		pnlDomainsAvailable.setLayout(new GridBagLayout());

		lblDomainsAvailable.setText("Available");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlDomainsAvailable.add(lblDomainsAvailable, gridBagConstraints);

		pnllstDomainsAvailable.setLayout(new BorderLayout());

		lstDomainsAvailable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstDomainsAvailableMouseClicked(evt);
			}
		});

		scpDomainsAvailable.setViewportView(lstDomainsAvailable);

		pnllstDomainsAvailable.add(scpDomainsAvailable, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlDomainsAvailable.add(pnllstDomainsAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlDomains.add(pnlDomainsAvailable, gridBagConstraints);

		pnlDomainsButtons.setLayout(new GridBagLayout());

		btnAddDomain.setMnemonic('A');
		btnAddDomain.setText("Add");
		btnAddDomain.setPreferredSize(new Dimension(81, 26));
		btnAddDomain.setEnabled(false);
		btnAddDomain.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddDomainActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlDomainsButtons.add(btnAddDomain, gridBagConstraints);

		btnRemoveDomain.setMnemonic('R');
		btnRemoveDomain.setText("Remove");
		btnRemoveDomain.setPreferredSize(new Dimension(81, 26));
		btnRemoveDomain.setEnabled(false);
		btnRemoveDomain.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveDomainActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlDomainsButtons.add(btnRemoveDomain, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.weighty = 1.0;
		pnlDomains.add(pnlDomainsButtons, gridBagConstraints);

		pnlDomainsSelected.setLayout(new GridBagLayout());

		pnlDomainsSelected.setPreferredSize(new Dimension(259, 147));
		lblDomainsSelected.setText("Selected");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlDomainsSelected.add(lblDomainsSelected, gridBagConstraints);

		pnllstDomainsSelected.setLayout(new BorderLayout());

		lstDomainsSelected.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstDomainsSelectedMouseClicked(evt);
			}
		});

		scpDomainsSelected.setViewportView(lstDomainsSelected);

		pnllstDomainsSelected.add(scpDomainsSelected, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlDomainsSelected.add(pnllstDomainsSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlDomains.add(pnlDomainsSelected, gridBagConstraints);

		lblDomainsHeader.setText("Granted Domains");
		pnlDomainsHeader.add(lblDomainsHeader);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		pnlDomains.add(pnlDomainsHeader, gridBagConstraints);

		jTabbedPane1.addTab("Domains", pnlDomains);

		pnlFollowers.setLayout(new GridBagLayout());

		pnlFollowers.setName("deityLanguageTab");
		pnlFollowersAvailable.setLayout(new GridBagLayout());

		lbFollowersAvailable.setText("Available");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlFollowersAvailable.add(lbFollowersAvailable, gridBagConstraints);

		pnllstFollowersAvailable.setLayout(new BorderLayout());

		lstFollowersAvailable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstFollowersAvailableMouseClicked(evt);
			}
		});

		scpFollowersAvailable.setViewportView(lstFollowersAvailable);

		pnllstFollowersAvailable.add(scpFollowersAvailable, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.8;
		pnlFollowersAvailable.add(pnllstFollowersAvailable, gridBagConstraints);

		pnlVariableQualifier.setLayout(new GridBagLayout());

		lblQualifier.setText("Qualifier");
		lblQualifier.setLabelFor(cmbQualifier);
		pnlQualifier.add(lblQualifier);

		cmbQualifier.setPreferredSize(new Dimension(180, 25));
		cmbQualifier.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				cmbQualifierItemStateChanged(evt);
			}
		});

		pnlQualifier.add(cmbQualifier);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		pnlVariableQualifier.add(pnlQualifier, gridBagConstraints);

		lblVariable.setText("Variable");
		lblVariable.setLabelFor(cmbVariable);
		lblVariable.setEnabled(false);
		pnlVariable.add(lblVariable);

		cmbVariable.setEditable(true);
		cmbVariable.setPreferredSize(new Dimension(180, 25));
		cmbVariable.setEnabled(false);
		pnlVariable.add(cmbVariable);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		pnlVariableQualifier.add(pnlVariable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.2;
		pnlFollowersAvailable.add(pnlVariableQualifier, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlFollowers.add(pnlFollowersAvailable, gridBagConstraints);

		pnlFollowersButtons.setLayout(new GridBagLayout());

		btnAddFollower.setMnemonic('A');
		btnAddFollower.setText("Add");
		btnAddFollower.setPreferredSize(new Dimension(81, 26));
		btnAddFollower.setEnabled(false);
		btnAddFollower.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddFollowerActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlFollowersButtons.add(btnAddFollower, gridBagConstraints);

		btnRemoveFollower.setMnemonic('R');
		btnRemoveFollower.setText("Remove");
		btnRemoveFollower.setPreferredSize(new Dimension(81, 26));
		btnRemoveFollower.setEnabled(false);
		btnRemoveFollower.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
			btnRemoveFollowerActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlFollowersButtons.add(btnRemoveFollower, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.weighty = 1.0;
		pnlFollowers.add(pnlFollowersButtons, gridBagConstraints);

		pnlFollowersSelected.setLayout(new GridBagLayout());

		lblFollowersSelected.setText("Selected");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlFollowersSelected.add(lblFollowersSelected, gridBagConstraints);

		pnllstFollowersSelected.setLayout(new BorderLayout());

		lstFollowersSelected.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstFollowersSelectedMouseClicked(evt);
			}
		});

		scpFollowersSelected.setViewportView(lstFollowersSelected);

		pnllstFollowersSelected.add(scpFollowersSelected, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlFollowersSelected.add(pnllstFollowersSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlFollowers.add(pnlFollowersSelected, gridBagConstraints);

		lblFollowersHeader.setText("Allowed Worshipper Alignment");
		pnlFollowersHeader.add(lblFollowersHeader);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		pnlFollowers.add(pnlFollowersHeader, gridBagConstraints);

		jTabbedPane1.addTab("Followers", pnlFollowers);

		pnlLanguages.setLayout(new GridBagLayout());

		pnlLanguages.setName("deityLanguageTab");
		pnlLanguagesAvailable.setLayout(new GridBagLayout());

		lblLanguagesAvailable.setText("Available");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlLanguagesAvailable.add(lblLanguagesAvailable, gridBagConstraints);

		pnllstLanguagesAvailable.setLayout(new BorderLayout());

		lstLanguagesAvailable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstLanguagesAvailableMouseClicked(evt);
			}
		});

		scpLanguagesAvailable.setViewportView(lstLanguagesAvailable);

		pnllstLanguagesAvailable.add(scpLanguagesAvailable, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlLanguagesAvailable.add(pnllstLanguagesAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlLanguages.add(pnlLanguagesAvailable, gridBagConstraints);

		pnlLanguagesButtons.setLayout(new GridBagLayout());

		btnAddLanguage.setMnemonic('A');
		btnAddLanguage.setText("Add");
		btnAddLanguage.setPreferredSize(new Dimension(81, 26));
		btnAddLanguage.setEnabled(false);
		btnAddLanguage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddLanguageActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlLanguagesButtons.add(btnAddLanguage, gridBagConstraints);

		btnRemoveLanguage.setMnemonic('R');
		btnRemoveLanguage.setText("Remove");
		btnRemoveLanguage.setPreferredSize(new Dimension(81, 26));
		btnRemoveLanguage.setEnabled(false);
		btnRemoveLanguage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveLanguageActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlLanguagesButtons.add(btnRemoveLanguage, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.weighty = 1.0;
		pnlLanguages.add(pnlLanguagesButtons, gridBagConstraints);

		pnlLanguagesSelected.setLayout(new GridBagLayout());

		pnlLanguagesSelected.setPreferredSize(new Dimension(259, 147));
		lblLanguagesSelected.setText("Selected");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlLanguagesSelected.add(lblLanguagesSelected, gridBagConstraints);

		pnllstLanguagesSelected.setLayout(new BorderLayout());

		lstLanguagesSelected.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstLanguagesSelectedMouseClicked(evt);
			}
		});

		scpLanguagesSelected.setViewportView(lstLanguagesSelected);

		pnllstLanguagesSelected.add(scpLanguagesSelected, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlLanguagesSelected.add(pnllstLanguagesSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlLanguages.add(pnlLanguagesSelected, gridBagConstraints);

		lblLanguagesHeader.setText("Granted Languages");
		pnlLanguagesHeader.add(lblLanguagesHeader);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		pnlLanguages.add(pnlLanguagesHeader, gridBagConstraints);

		jTabbedPane1.addTab("Languages", pnlLanguages);

		pnlRaces.setLayout(new GridBagLayout());

		pnlRaces.setName("deityLanguageTab");
		pnlRacesAvailable.setLayout(new GridBagLayout());

		pnlRacesAvailable.setPreferredSize(new Dimension(259, 147));
		lblRacesAvailable.setText("Available");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlRacesAvailable.add(lblRacesAvailable, gridBagConstraints);

		pnllstRacesAvailable.setLayout(new BorderLayout());

		pnllstRacesAvailable.setPreferredSize(new Dimension(259, 131));
		lstRacesAvailable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstRacesAvailableMouseClicked(evt);
			}
		});

		scpRacesAvailable.setViewportView(lstRacesAvailable);

		pnllstRacesAvailable.add(scpRacesAvailable, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlRacesAvailable.add(pnllstRacesAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlRaces.add(pnlRacesAvailable, gridBagConstraints);

		pnlRacesButtons.setLayout(new GridBagLayout());

		btnAddRace.setMnemonic('A');
		btnAddRace.setText("Add");
		btnAddRace.setPreferredSize(new Dimension(81, 26));
		btnAddRace.setEnabled(false);
		btnAddRace.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddRaceActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlRacesButtons.add(btnAddRace, gridBagConstraints);

		btnRemoveRace.setMnemonic('R');
		btnRemoveRace.setText("Remove");
		btnRemoveRace.setPreferredSize(new Dimension(81, 26));
		btnRemoveRace.setEnabled(false);
		btnRemoveRace.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveRaceActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlRacesButtons.add(btnRemoveRace, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.weighty = 1.0;
		pnlRaces.add(pnlRacesButtons, gridBagConstraints);

		pnlRacesSelected.setLayout(new GridBagLayout());

		pnlRacesSelected.setPreferredSize(new Dimension(259, 147));
		lblRacesSelected.setText("Selected");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlRacesSelected.add(lblRacesSelected, gridBagConstraints);

		pnllstRacesSelected.setLayout(new BorderLayout());

		pnllstRacesSelected.setPreferredSize(new Dimension(259, 131));
		lstRacesSelected.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstRacesSelectedMouseClicked(evt);
			}
		});

		scpRacesSelected.setViewportView(lstRacesSelected);

		pnllstRacesSelected.add(scpRacesSelected, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlRacesSelected.add(pnllstRacesSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlRaces.add(pnlRacesSelected, gridBagConstraints);

		lblRacesHeader.setText("Racial Worshippers");
		pnlRacesHeader.add(lblRacesHeader);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		pnlRaces.add(pnlRacesHeader, gridBagConstraints);

		jTabbedPane1.addTab("Races", pnlRaces);

		pnlWeapons.setLayout(new GridBagLayout());

		pnlWeapons.setName("deityLanguageTab");
		pnlWeaponsAvailable.setLayout(new GridBagLayout());

		pnlWeaponsAvailable.setPreferredSize(new Dimension(259, 147));
		lblWeaponsAvailable.setText("Available");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlWeaponsAvailable.add(lblWeaponsAvailable, gridBagConstraints);

		pnllstWeaponsAvailable.setLayout(new BorderLayout());

		pnllstWeaponsAvailable.setPreferredSize(new Dimension(259, 131));
		lstWeaponsAvailable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstWeaponsAvailableMouseClicked(evt);
			}
		});

		scpWeaponsAvailable.setViewportView(lstWeaponsAvailable);

		pnllstWeaponsAvailable.add(scpWeaponsAvailable, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlWeaponsAvailable.add(pnllstWeaponsAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlWeapons.add(pnlWeaponsAvailable, gridBagConstraints);

		pnlWeaponsButtons.setLayout(new GridBagLayout());

		btnAddWeapon.setMnemonic('A');
		btnAddWeapon.setText("Add");
		btnAddWeapon.setPreferredSize(new Dimension(81, 26));
		btnAddWeapon.setEnabled(false);
		btnAddWeapon.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddWeaponActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlWeaponsButtons.add(btnAddWeapon, gridBagConstraints);

		btnRemoveWeapon.setMnemonic('R');
		btnRemoveWeapon.setText("Remove");
		btnRemoveWeapon.setPreferredSize(new Dimension(81, 26));
		btnRemoveWeapon.setEnabled(false);
		btnRemoveWeapon.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveWeaponActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlWeaponsButtons.add(btnRemoveWeapon, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.weighty = 1.0;
		pnlWeapons.add(pnlWeaponsButtons, gridBagConstraints);

		pnlWeaponsSelected.setLayout(new GridBagLayout());

		pnlWeaponsSelected.setPreferredSize(new Dimension(259, 147));
		lblWeaponsSelected.setText("Selected");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlWeaponsSelected.add(lblWeaponsSelected, gridBagConstraints);

		pnllstWeaponsSelected.setLayout(new BorderLayout());

		pnllstWeaponsSelected.setPreferredSize(new Dimension(259, 131));
		lstWeaponsSelected.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstWeaponsSelectedMouseClicked(evt);
			}
		});

		scpWeaponsSelected.setViewportView(lstWeaponsSelected);

		pnllstWeaponsSelected.add(scpWeaponsSelected, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlWeaponsSelected.add(pnllstWeaponsSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlWeapons.add(pnlWeaponsSelected, gridBagConstraints);

		lblWeaponsHeader.setText("Granted Weapon Proficiencies");
		pnlWeaponsHeader.add(lblWeaponsHeader);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		pnlWeapons.add(pnlWeaponsHeader, gridBagConstraints);

		jTabbedPane1.addTab("Weapons", pnlWeapons);

		pnlSkills.setLayout(new GridBagLayout());

		pnlSkills.setName("deityLanguageTab");
		pnlSkillsAvailable.setLayout(new GridBagLayout());

		lblSkillsAvailable.setText("Available");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlSkillsAvailable.add(lblSkillsAvailable, gridBagConstraints);

		pnllstSkillsAvailable.setLayout(new BorderLayout());

		lstSkillsAvailable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstSkillsAvailableMouseClicked(evt);
			}
		});

		scpSkillsAvailable.setViewportView(lstSkillsAvailable);

		pnllstSkillsAvailable.add(scpSkillsAvailable, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlSkillsAvailable.add(pnllstSkillsAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlSkills.add(pnlSkillsAvailable, gridBagConstraints);

		pnlSkillsButtonsClass.setLayout(new GridBagLayout());

		btnAddClassSkill.setMnemonic('A');
		btnAddClassSkill.setText("Add");
		btnAddClassSkill.setPreferredSize(new Dimension(81, 26));
		btnAddClassSkill.setEnabled(false);
		btnAddClassSkill.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddClassSkillActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlSkillsButtonsClass.add(btnAddClassSkill, gridBagConstraints);

		btnRemoveClassSkill.setMnemonic('R');
		btnRemoveClassSkill.setText("Remove");
		btnRemoveClassSkill.setPreferredSize(new Dimension(81, 26));
		btnRemoveClassSkill.setEnabled(false);
		btnRemoveClassSkill.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveClassSkillActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlSkillsButtonsClass.add(btnRemoveClassSkill, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.weighty = 0.5;
		pnlSkills.add(pnlSkillsButtonsClass, gridBagConstraints);

		pnlSkillsSelectedClass.setLayout(new GridBagLayout());

		lblSkillsSelectedClass.setText("Selected Class Skills");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlSkillsSelectedClass.add(lblSkillsSelectedClass, gridBagConstraints);

		pnllstSkillsSelectedClass.setLayout(new BorderLayout());

		lstSkillsSelectedClass.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstSkillsSelectedClassMouseClicked(evt);
			}
		});

		scpSkillsSelectedClass.setViewportView(lstSkillsSelectedClass);

		pnllstSkillsSelectedClass.add(scpSkillsSelectedClass, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlSkillsSelectedClass.add(pnllstSkillsSelectedClass, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlSkills.add(pnlSkillsSelectedClass, gridBagConstraints);

		lblSkillsHeader.setText("Granted Skills");
		pnlSkillsHeader.add(lblSkillsHeader);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		pnlSkills.add(pnlSkillsHeader, gridBagConstraints);

		pnlSkillsButtonsCrossClass.setLayout(new GridBagLayout());

		btnAddCrossClassSkill.setMnemonic('d');
		btnAddCrossClassSkill.setText("Add");
		btnAddCrossClassSkill.setPreferredSize(new Dimension(81, 26));
		btnAddCrossClassSkill.setEnabled(false);
		btnAddCrossClassSkill.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddCrossClassSkillActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlSkillsButtonsCrossClass.add(btnAddCrossClassSkill, gridBagConstraints);

		btnRemoveCrossClassSkill.setMnemonic('e');
		btnRemoveCrossClassSkill.setText("Remove");
		btnRemoveCrossClassSkill.setPreferredSize(new Dimension(81, 26));
		btnRemoveCrossClassSkill.setEnabled(false);
		btnRemoveCrossClassSkill.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveCrossClassSkillActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlSkillsButtonsCrossClass.add(btnRemoveCrossClassSkill, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.weighty = 0.5;
		pnlSkills.add(pnlSkillsButtonsCrossClass, gridBagConstraints);

		pnlSkillsSelectedCrossClass.setLayout(new GridBagLayout());

		lblSkillsSelectedCrossClass.setText("Selected Cross Class Skills");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlSkillsSelectedCrossClass.add(lblSkillsSelectedCrossClass, gridBagConstraints);

		pnllstSkillsSelectedCrossClass.setLayout(new BorderLayout());

		lstSkillsSelectedCrossClass.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				lstSkillsSelectedCrossClassMouseClicked(evt);
			}
		});

		scpSkillsSelectedCrossClass.setViewportView(lstSkillsSelectedCrossClass);

		pnllstSkillsSelectedCrossClass.add(scpSkillsSelectedCrossClass, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlSkillsSelectedCrossClass.add(pnllstSkillsSelectedCrossClass, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		pnlSkills.add(pnlSkillsSelectedCrossClass, gridBagConstraints);

		jTabbedPane1.addTab("Skills", pnlSkills);

		pnlAdvanced.setLayout(new GridBagLayout());

		pnlAdvanced.setName("deityLanguageTab");
		pnlAdvancedAvailable.setLayout(new GridBagLayout());

		pnlAdvancedAvailable.setPreferredSize(new Dimension(259, 147));
		pnlAdvancedTag.setLayout(new GridBagLayout());

		lblAdvancedTag.setText("Tag");
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

		lblAdvancedTagValue.setText("Tag Value");
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
		});

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

		btnAddAdvanced.setMnemonic('A');
		btnAddAdvanced.setText("Add");
		btnAddAdvanced.setPreferredSize(new Dimension(81, 26));
		btnAddAdvanced.setEnabled(false);
		btnAddAdvanced.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddAdvancedActionPerformed(evt);
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlAdvancedButtons.add(btnAddAdvanced, gridBagConstraints);

		btnRemoveAdvanced.setMnemonic('R');
		btnRemoveAdvanced.setText("Remove");
		btnRemoveAdvanced.setPreferredSize(new Dimension(81, 26));
		btnRemoveAdvanced.setEnabled(false);
		btnRemoveAdvanced.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveAdvancedActionPerformed(evt);
			}
		});

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

		lblAdvancedSelected.setText("Selected");
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
		});

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

		lblAdvancedHeader.setText("Miscellaneous Tags");
		pnlAdvancedHeader.add(lblAdvancedHeader);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		pnlAdvanced.add(pnlAdvancedHeader, gridBagConstraints);

		jTabbedPane1.addTab("Advanced", pnlAdvanced);

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

		btnCancel.setMnemonic('C');
		btnCancel.setText("Cancel");
		btnCancel.setPreferredSize(new Dimension(80, 26));
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnCancelActionPerformed(evt);
			}
		});

		pnlButtons.add(btnCancel);

		btnSave.setMnemonic('S');
		btnSave.setText("Save");
		btnSave.setPreferredSize(new Dimension(80, 26));
		//btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnSaveActionPerformed(evt);
			}
		});

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
	}//GEN-END:initComponents


	///////////////////////
	// Advanced tab
	//

	private void txtAdvancedTagValueKeyTyped(KeyEvent evt)//GEN-FIRST:event_txtAdvancedTagValueKeyTyped
	{
		//
		// TODO: the contents of the JTextArea have not yet had the effects of the key applied...
		//
//System.out.println("[" + txtAdvancedTagValue.getText().trim() + "] " + evt);
		btnAddAdvanced.setEnabled(txtAdvancedTagValue.getText().trim().length() != 0);
	}//GEN-LAST:event_txtAdvancedTagValueKeyTyped

	private void lstAdvancedSelectedMouseClicked(MouseEvent evt)//GEN-FIRST:event_lstAdvancedSelectedMouseClicked
	{
		if (isDoubleClick(evt, lstAdvancedSelected, btnRemoveAdvanced))
		{
			btnRemoveAdvancedActionPerformed(null);
		}
	}//GEN-LAST:event_lstAdvancedSelectedMouseClicked

	private void btnRemoveAdvancedActionPerformed(ActionEvent evt)//GEN-FIRST:event_btnRemoveAdvancedActionPerformed
	{
		btnRemoveAdvanced.setEnabled(false);

		final JListModel lms = (JListModel)lstAdvancedSelected.getModel();
		final Object x[] = lstAdvancedSelected.getSelectedValues();
		for (int i = 0; i < x.length; ++i)
		{
			String entry = (String)x[i];
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
	}//GEN-LAST:event_btnRemoveAdvancedActionPerformed

	private void btnAddAdvancedActionPerformed(ActionEvent evt)//GEN-FIRST:event_btnAddAdvancedActionPerformed
	{
		btnAddAdvanced.setEnabled(false);

		String newEntry = (String)cmbAdvancedTag.getSelectedItem() + ":" + txtAdvancedTagValue.getText().trim();
		final JListModel lmd = (JListModel)lstAdvancedSelected.getModel();
		lmd.addElement(newEntry);
	}//GEN-LAST:event_btnAddAdvancedActionPerformed

    	///////////////////////
	// Followers tab
	//

	private void lstFollowersSelectedMouseClicked(MouseEvent evt)//GEN-FIRST:event_lstFollowersSelectedMouseClicked
	{
		if (isDoubleClick(evt, lstFollowersSelected, btnRemoveFollower))
		{
			btnRemoveFollowerActionPerformed(null);
		}
	}//GEN-LAST:event_lstFollowersSelectedMouseClicked

	private void lstFollowersAvailableMouseClicked(MouseEvent evt)//GEN-FIRST:event_lstFollowersAvailableMouseClicked
	{
		if (isDoubleClick(evt, lstFollowersAvailable, btnAddFollower))
		{
			btnAddFollowerActionPerformed(null);
		}
	}//GEN-LAST:event_lstFollowersAvailableMouseClicked

	private void btnRemoveFollowerActionPerformed(ActionEvent evt)//GEN-FIRST:event_btnRemoveFollowerActionPerformed
	{
		btnRemoveFollower.setEnabled(false);

		final JListModel lms = (JListModel)lstFollowersSelected.getModel();
		final JListModel lmd = (JListModel)lstFollowersAvailable.getModel();
		final Object x[] = lstFollowersSelected.getSelectedValues();
		for (int i = 0; i < x.length; ++i)
		{
			String entry = (String)x[i];
			int idx = -1;
			if (entry.indexOf('[') >= 0)
			{
				for (int j = 0; j < cmbQualifier.getItemCount(); ++j)
				{
					final String qualifier = " [" + (String)cmbQualifier.getItemAt(j) + ":";
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
			lms.removeElement(x[i]);
			lmd.addElement(entry);
		}
	}//GEN-LAST:event_btnRemoveFollowerActionPerformed

	private void btnAddFollowerActionPerformed(ActionEvent evt)//GEN-FIRST:event_btnAddFollowerActionPerformed
	{
		btnAddFollower.setEnabled(false);

		String condition = null;
		final String qualifier = (String)cmbQualifier.getSelectedItem();
		if ((qualifier != null) && !qualifier.equalsIgnoreCase("(None)"))
		{
			condition = " [" + qualifier + ":" + (String)cmbVariable.getSelectedItem() + "]";
		}

		final JListModel lms = (JListModel)lstFollowersAvailable.getModel();
		final JListModel lmd = (JListModel)lstFollowersSelected.getModel();
		final Object x[] = lstFollowersAvailable.getSelectedValues();
		for (int i = 0; i < x.length; ++i)
		{
			String newEntry = (String)x[i];
			if (condition != null)
			{
				newEntry += condition;
			}
			lmd.addElement(newEntry);
			lms.removeElement(x[i]);
		}
	}//GEN-LAST:event_btnAddFollowerActionPerformed

	//
	// User has changed the selection in the qualifier combo. If they've selected "(None)" then disable choosing from the variable name
	// combo. Otherwise enable it
	//
	private void cmbQualifierItemStateChanged(ItemEvent evt)//GEN-FIRST:event_cmbQualifierItemStateChanged
	{
		final String qualifier = (String)cmbQualifier.getSelectedItem();
		if ((qualifier != null) && !qualifier.equalsIgnoreCase("(None)"))
		{
			cmbVariable.setEnabled(true);
			lblVariable.setEnabled(true);
		}
		else
		{
			cmbVariable.setEnabled(false);
			lblVariable.setEnabled(false);
		}
	}//GEN-LAST:event_cmbQualifierItemStateChanged

	///////////////////////
	// Base tab
	//

	//
	// User clicked on Selected Favored Weapon
	//
	private void lstFavoredWeaponSelectedMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstFavoredWeaponSelectedMouseClicked
		if (isDoubleClick(evt, lstFavoredWeaponSelected, btnRemoveFavoredWeapon))
		{
			btnRemoveFavoredWeaponActionPerformed(null);
		}
	}//GEN-LAST:event_lstFavoredWeaponSelectedMouseClicked

	//
	// User clicked on Available Favored Weapon
	//
	private void lstFavoredWeaponAvailableMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstFavoredWeaponAvailableMouseClicked
		if (isDoubleClick(evt, lstFavoredWeaponAvailable, btnAddFavoredWeapon))
		{
			btnAddFavoredWeaponActionPerformed(null);
		}
	}//GEN-LAST:event_lstFavoredWeaponAvailableMouseClicked


	//
	// User clicked the Add Favored weapon button
	//
	private void btnAddFavoredWeaponActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddFavoredWeaponActionPerformed
		swapEntries(lstFavoredWeaponSelected, lstFavoredWeaponAvailable, btnAddFavoredWeapon);
	}//GEN-LAST:event_btnAddFavoredWeaponActionPerformed

	//
	// User clicked the Remove Favored weapon button
	//
	private void btnRemoveFavoredWeaponActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveFavoredWeaponActionPerformed
		swapEntries(lstFavoredWeaponAvailable, lstFavoredWeaponSelected, btnRemoveFavoredWeapon);
	}//GEN-LAST:event_btnRemoveFavoredWeaponActionPerformed


	//////////////////////////////////////////
	// Weapons tab
	//
	private void btnAddWeaponActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddWeaponActionPerformed
		swapEntries(lstWeaponsSelected, lstWeaponsAvailable, btnAddWeapon);
	}//GEN-LAST:event_btnAddWeaponActionPerformed

	private void btnRemoveWeaponActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveWeaponActionPerformed
		swapEntries(lstWeaponsAvailable, lstWeaponsSelected, btnRemoveWeapon);
	}//GEN-LAST:event_btnRemoveWeaponActionPerformed

	private void lstWeaponsAvailableMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstWeaponsAvailableMouseClicked
		if (isDoubleClick(evt, lstWeaponsAvailable, btnAddWeapon))
		{
			btnAddWeaponActionPerformed(null);
		}
	}//GEN-LAST:event_lstWeaponsAvailableMouseClicked

	private void lstWeaponsSelectedMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstWeaponsSelectedMouseClicked
		if (isDoubleClick(evt, lstWeaponsSelected, btnRemoveWeapon))
		{
			btnRemoveWeaponActionPerformed(null);
		}
	}//GEN-LAST:event_lstWeaponsSelectedMouseClicked


	//////////////////////////////////////////
	// Skills tab
	//
	private void btnAddCrossClassSkillActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddCrossClassSkillActionPerformed
		btnAddClassSkill.setEnabled(false);
		swapEntries(lstSkillsSelectedCrossClass, lstSkillsAvailable, btnAddCrossClassSkill);
	}//GEN-LAST:event_btnAddCrossClassSkillActionPerformed

	private void btnRemoveCrossClassSkillActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveCrossClassSkillActionPerformed
		swapEntries(lstSkillsAvailable, lstSkillsSelectedCrossClass, btnRemoveCrossClassSkill);
	}//GEN-LAST:event_btnRemoveCrossClassSkillActionPerformed

	private void btnAddClassSkillActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddClassSkillActionPerformed
		btnAddCrossClassSkill.setEnabled(false);
		swapEntries(lstSkillsSelectedClass, lstSkillsAvailable, btnAddClassSkill);
	}//GEN-LAST:event_btnAddClassSkillActionPerformed

	private void btnRemoveClassSkillActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveClassSkillActionPerformed
		swapEntries(lstSkillsAvailable, lstSkillsSelectedClass, btnRemoveClassSkill);
	}//GEN-LAST:event_btnRemoveClassSkillActionPerformed

	private void lstSkillsAvailableMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstSkillsAvailableMouseClicked
		isDoubleClick(evt, lstSkillsAvailable, btnAddClassSkill);
		isDoubleClick(evt, lstSkillsAvailable, btnAddCrossClassSkill);
	}//GEN-LAST:event_lstSkillsAvailableMouseClicked

	private void lstSkillsSelectedCrossClassMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstSkillsSelectedCrossClassMouseClicked
		if (isDoubleClick(evt, lstSkillsSelectedCrossClass, btnRemoveCrossClassSkill))
		{
			btnRemoveCrossClassSkillActionPerformed(null);
		}
	}//GEN-LAST:event_lstSkillsSelectedCrossClassMouseClicked

	private void lstSkillsSelectedClassMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstSkillsSelectedClassMouseClicked
		if (isDoubleClick(evt, lstSkillsSelectedClass, btnRemoveClassSkill))
		{
			btnRemoveClassSkillActionPerformed(null);
		}
	}//GEN-LAST:event_lstSkillsSelectedClassMouseClicked


	//////////////////////////////////////////
	// Domains tab
	//
	private void btnAddDomainActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddDomainActionPerformed
		swapEntries(lstDomainsSelected, lstDomainsAvailable, btnAddDomain);
	}//GEN-LAST:event_btnAddDomainActionPerformed

	private void btnRemoveDomainActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveDomainActionPerformed
		swapEntries(lstDomainsAvailable, lstDomainsSelected, btnRemoveDomain);
	}//GEN-LAST:event_btnRemoveDomainActionPerformed

	private void lstDomainsAvailableMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstDomainsAvailableMouseClicked
		if (isDoubleClick(evt, lstDomainsAvailable, btnAddDomain))
		{
			btnAddDomainActionPerformed(null);
		}
	}//GEN-LAST:event_lstDomainsAvailableMouseClicked

	private void lstDomainsSelectedMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstDomainsSelectedMouseClicked
		if (isDoubleClick(evt, lstDomainsSelected, btnRemoveDomain))
		{
			btnRemoveDomainActionPerformed(null);
		}
	}//GEN-LAST:event_lstDomainsSelectedMouseClicked


	//////////////////////////////////////////
	// Races tab
	//
	private void btnAddRaceActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddRaceActionPerformed
		swapEntries(lstRacesSelected, lstRacesAvailable, btnAddRace);
	}//GEN-LAST:event_btnAddRaceActionPerformed

	private void btnRemoveRaceActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveRaceActionPerformed
		swapEntries(lstRacesAvailable, lstRacesSelected, btnRemoveRace);
	}//GEN-LAST:event_btnRemoveRaceActionPerformed

	private void lstRacesAvailableMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstRacesAvailableMouseClicked
		if (isDoubleClick(evt, lstRacesAvailable, btnAddRace))
		{
			btnAddRaceActionPerformed(null);
		}
	}//GEN-LAST:event_lstRacesAvailableMouseClicked

	private void lstRacesSelectedMouseClicked(MouseEvent evt) {//GEN-FIRST:event_lstRacesSelectedMouseClicked
		if (isDoubleClick(evt, lstRacesSelected, btnRemoveRace))
		{
			btnRemoveRaceActionPerformed(null);
		}
	}//GEN-LAST:event_lstRacesSelectedMouseClicked


	//////////////////////////////////////////
	// Languages tab
	//
	private void btnAddLanguageActionPerformed(ActionEvent evt)//GEN-FIRST:event_btnAddLanguageActionPerformed
	{
		swapEntries(lstLanguagesSelected, lstLanguagesAvailable, btnAddLanguage);
	}//GEN-LAST:event_btnAddLanguageActionPerformed

	private void btnRemoveLanguageActionPerformed(ActionEvent evt)//GEN-FIRST:event_btnRemoveLanguageActionPerformed
	{
		swapEntries(lstLanguagesAvailable, lstLanguagesSelected, btnRemoveLanguage);
	}//GEN-LAST:event_btnRemoveLanguageActionPerformed

	private void lstLanguagesAvailableMouseClicked(MouseEvent evt)//GEN-FIRST:event_lstLanguagesAvailableMouseClicked
	{
		if (isDoubleClick(evt, lstLanguagesAvailable, btnAddLanguage))
		{
			btnAddLanguageActionPerformed(null);
		}
	}//GEN-LAST:event_lstLanguagesAvailableMouseClicked

	private void lstLanguagesSelectedMouseClicked(MouseEvent evt)//GEN-FIRST:event_lstLanguagesSelectedMouseClicked
	{
		if (isDoubleClick(evt, lstLanguagesSelected, btnRemoveLanguage))
		{
			btnRemoveLanguageActionPerformed(null);
		}
	}//GEN-LAST:event_lstLanguagesSelectedMouseClicked


	//////////////////////////////////////////

	private void btnSaveActionPerformed(ActionEvent evt)//GEN-FIRST:event_btnSaveActionPerformed
	{
		String aString = txtDeityName.getText().trim();
		if (aString.length() == 0)
		{
			JOptionPane.showMessageDialog(null, "You must enter a name for the deity.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		final Deity existingDeity = Globals.getDeityNamed(aString);
//System.out.println("deityName: " + aString);
		if (existingDeity != null)
 		{
//System.out.println("TYPE:" + existingDeity.getType());
			if (!existingDeity.isType("CUSTOM"))
			{
				JOptionPane.showMessageDialog(null, "You currently cannot overwrite a non-custom deity.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			final int overwrite = JOptionPane.showConfirmDialog(null, "Deity already exists. Overwrite?", Constants.s_APPNAME, JOptionPane.OK_CANCEL_OPTION);
			if (overwrite != JOptionPane.OK_OPTION)
			{
				return;
			}
		}

		thisDeity.setName(aString);
		thisDeity.setHolyItem(txtHolyItem.getText().trim());

		aString = (String)cmbDeityAlignment.getSelectedItem();
		if (aString != null)
		{
			for (int i = 0; i < Constants.s_ALIGNLONG.length; ++i)
			{
				if (aString.equals(Constants.s_ALIGNLONG[i]))
				{
					thisDeity.setDeityAlignment(Constants.s_ALIGNSHORT[i]);
					break;
				}
			}
		}
		thisDeity.setDescription(txtDescription.getText().trim());

		//
		// Save source info
		//
		thisDeity.setSource(".CLEAR");
		aString = txtSource.getText().trim();
		if (aString.length() != 0)
		{
			thisDeity.setSource("SOURCEPAGE:" + aString);
		}

		//
		// Save favored weapon(s)
		//
		Object sel[];
		if (lstFavoredWeaponAvailable.getModel().getSize() == 0)
		{
			aString = "Any";
		}
		else
		{
			sel = ((JListModel)lstFavoredWeaponSelected.getModel()).getElements();
			aString = delimitArray(sel, '|');
		}
		thisDeity.setFavoredWeapon(aString);
//System.out.println("setFavoredWeapon:" + aString);

		//
		// Save P.I. flag
		//
		thisDeity.setNameIsPI(chkProductIdentity.isSelected());
//System.out.println("setNameIsPI:" + chkProductIdentity.isSelected());

		//
		// Save granted domains
		//
		if (lstDomainsAvailable.getModel().getSize() == 0)
		{
			aString = "ALL";
		}
		else
		{
			sel = ((JListModel)lstDomainsSelected.getModel()).getElements();
			aString = delimitArray(sel, ',');
		}
		thisDeity.setDomainList(aString);
//System.out.println("setDomainList:" + aString);

		//
		// Save follower alignments
		//
		sel = ((JListModel)lstFollowersSelected.getModel()).getElements();
		StringBuffer tbuf = new StringBuffer(100);
		for (int i = 0; i < sel.length; ++i)
		{
			String qualifier = null;
			aString = (String)sel[i];
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
			for (int align = 0; align < Constants.s_ALIGNLONG.length; ++align)
			{
				if (aString.equals(Constants.s_ALIGNLONG[align]))
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
		thisDeity.setFollowerAlignments(tbuf.toString());
//System.out.println("setFollowerAlignments:" + tbuf.toString());

		//
		// Save granted languages
		//
		thisDeity.setLanguageAutos(".CLEAR");
		sel = ((JListModel)lstLanguagesSelected.getModel()).getElements();
		aString = delimitArray(sel, ',');
		thisDeity.setLanguageAutos(aString);
//System.out.println("setLanguageAutos:" + aString);

		//
		// Save racial worshippers (no need to explicitly clear)
		//
		sel = ((JListModel)lstRacesSelected.getModel()).getElements();
		aString = delimitArray(sel, '|');
		thisDeity.setRaceList(aString);
//System.out.println("setRaceList:" + aString);

		//
		// Save auto weapon proficiencies
		//
		thisDeity.setWeaponProfAutos(".CLEAR");
		sel = ((JListModel)lstWeaponsSelected.getModel()).getElements();
		aString = delimitArray(sel, '|');
		thisDeity.setWeaponProfAutos(aString);
//System.out.println("setWeaponProfAutos:" + aString);

// TODO: check if all skills of one type are selected...maybe change to TYPE.blah?

		//
		// Save granted class skills
		//
		thisDeity.setCSkillList(".CLEAR");
		sel = ((JListModel)lstSkillsSelectedClass.getModel()).getElements();
		aString = delimitArray(sel, '|');
		thisDeity.setCSkillList(aString);
//System.out.println("setCSkillList:" + aString);

		//
		// Save granted cross class skills
		//
		thisDeity.setCcSkillList(".CLEAR");
		sel = ((JListModel)lstSkillsSelectedCrossClass.getModel()).getElements();
		aString = delimitArray(sel, '|');
		thisDeity.setCcSkillList(aString);
//System.out.println("setCcSkillList:" + aString);


		//
		// Save advanced tab info
		//
		//
		// Make sure the lists are all empty to start
		//
		thisDeity.getBonusList().clear();
		thisDeity.clearVariableList();
		thisDeity.setDR(".CLEAR");
		thisDeity.addPreReq("PRE:.CLEAR");
		thisDeity.setSpecialAbilityList(".CLEAR", -9);
		thisDeity.setSR(null);

		sel = ((JListModel)lstAdvancedSelected.getModel()).getElements();
		for (int i = 0; i < sel.length; ++i)
		{
			aString = (String)sel[i];
			if (aString.startsWith("BONUS:"))
			{
				thisDeity.addBonusList(aString.substring(6));
			}
			else if (aString.startsWith("DEFINE:"))
			{
				thisDeity.addVariable("-9|" + aString.substring(7));
			}
			else if (aString.startsWith("DR:"))
			{
				thisDeity.setDR(aString.substring(3));
			}
			else if (aString.startsWith("PRE") || aString.startsWith("!PRE"))
			{
				thisDeity.addPreReq(aString);
			}
			else if (aString.startsWith("SA:"))
			{
				thisDeity.setSpecialAbilityList(aString.substring(3), -9);
			}
			else if (aString.startsWith("SR:"))
			{
				thisDeity.setSR(aString.substring(3));
			}
		}

		thisDeity.addType("CUSTOM");


		if (existingDeity != null)
		{
			if (existingDeity.isType("CUSTOM"))
			{
				Globals.getDeityList().remove(existingDeity);
			}
			else
			{
			}
		}


		//
		// Add to deity list and sort
		//
		Globals.getDeityList().add(thisDeity);
		Globals.sortPObjectList(Globals.getDeityList());

		closeDialog(null);
	}//GEN-LAST:event_btnSaveActionPerformed

	private String delimitArray(Object objArray[], char delim)
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

	private void btnCancelActionPerformed(ActionEvent evt)//GEN-FIRST:event_btnCancelActionPerformed
	{
		closeDialog(null);
	}//GEN-LAST:event_btnCancelActionPerformed

	/** Closes the dialog */
	private void closeDialog(WindowEvent evt)//GEN-FIRST:event_closeDialog
	{
		setVisible(false);
		dispose();
	}//GEN-LAST:event_closeDialog


	//
	// Remove selected objects from src and insert it in dst
	//
	private void swapEntries(JList dst, JList src, JButton btn)
	{
		btn.setEnabled(false);

		final JListModel lms = (JListModel)src.getModel();
		final JListModel lmd = (JListModel)dst.getModel();
		final Object x[] = src.getSelectedValues();
		for (int i = 0; i < x.length; ++i)
		{
			lmd.addElement(x[i]);
			lms.removeElement(x[i]);
		}
	}

	private boolean isDoubleClick(MouseEvent evt, JList lst, JButton btn)
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

    /**
     * @param args the command line arguments
     */
	public static void main(String args[])
	{
		new DeityEditorMain(null).show();
	}


	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JButton btnAddAdvanced;
	private JButton btnAddClassSkill;
	private JButton btnAddCrossClassSkill;
	private JButton btnAddDomain;
	private JButton btnAddFavoredWeapon;
	private JButton btnAddFollower;
	private JButton btnAddLanguage;
	private JButton btnAddRace;
	private JButton btnAddWeapon;
	private JButton btnCancel;
	private JButton btnRemoveAdvanced;
	private JButton btnRemoveClassSkill;
	private JButton btnRemoveCrossClassSkill;
	private JButton btnRemoveDomain;
	private JButton btnRemoveFavoredWeapon;
	private JButton btnRemoveFollower;
	private JButton btnRemoveLanguage;
	private JButton btnRemoveRace;
	private JButton btnRemoveWeapon;
	private JButton btnSave;
	private JCheckBox chkProductIdentity;
	private JComboBox cmbAdvancedTag;
	private JComboBox cmbDeityAlignment;
	private JComboBox cmbQualifier;
	private JComboBox cmbVariable;
	private JLabel lbFollowersAvailable;
	private JLabel lblAdvancedHeader;
	private JLabel lblAdvancedSelected;
	private JLabel lblAdvancedTag;
	private JLabel lblAdvancedTagValue;
	private JLabel lblDeityAlignment;
	private JLabel lblDeityFavoredWeapon;
	private JLabel lblDeityName;
	private JLabel lblDescription;
	private JLabel lblDomainsAvailable;
	private JLabel lblDomainsHeader;
	private JLabel lblDomainsSelected;
	private JLabel lblFavoredWeaponAvailable;
	private JLabel lblFavoredWeaponSelected;
	private JLabel lblFollowersHeader;
	private JLabel lblFollowersSelected;
	private JLabel lblHolyItem;
	private JLabel lblLanguagesAvailable;
	private JLabel lblLanguagesHeader;
	private JLabel lblLanguagesSelected;
	private JLabel lblQualifier;
	private JLabel lblRacesAvailable;
	private JLabel lblRacesHeader;
	private JLabel lblRacesSelected;
	private JLabel lblSkillsAvailable;
	private JLabel lblSkillsHeader;
	private JLabel lblSkillsSelectedClass;
	private JLabel lblSkillsSelectedCrossClass;
	private JLabel lblSource;
	private JLabel lblVariable;
	private JLabel lblWeaponsAvailable;
	private JLabel lblWeaponsHeader;
	private JLabel lblWeaponsSelected;
	private JList lstAdvancedSelected;
	private JList lstDomainsAvailable;
	private JList lstDomainsSelected;
	private JList lstFavoredWeaponAvailable;
	private JList lstFavoredWeaponSelected;
	private JList lstFollowersAvailable;
	private JList lstFollowersSelected;
	private JList lstLanguagesAvailable;
	private JList lstLanguagesSelected;
	private JList lstRacesAvailable;
	private JList lstRacesSelected;
	private JList lstSkillsAvailable;
	private JList lstSkillsSelectedClass;
	private JList lstSkillsSelectedCrossClass;
	private JList lstWeaponsAvailable;
	private JList lstWeaponsSelected;
	private JPanel pnlAdvanced;
	private JPanel pnlAdvancedAvailable;
	private JPanel pnlAdvancedButtons;
	private JPanel pnlAdvancedHeader;
	private JPanel pnlAdvancedSelected;
	private JPanel pnlAdvancedTag;
	private JPanel pnlAdvancedTagValue;
	private JPanel pnlButtons;
	private JPanel pnlDeityAlignment;
	private JPanel pnlDeityName;
	private JPanel pnlDescription;
	private JPanel pnlDescriptionText;
	private JPanel pnlDomains;
	private JPanel pnlDomainsAvailable;
	private JPanel pnlDomainsButtons;
	private JPanel pnlDomainsHeader;
	private JPanel pnlDomainsSelected;
	private JPanel pnlFavoredWeapon;
	private JPanel pnlFavoredWeaponAvailable;
	private JPanel pnlFavoredWeaponHeader;
	private JPanel pnlFavoredWeaponSelected;
	private JPanel pnlFavoredWeaponsButtons;
	private JPanel pnlFollowers;
	private JPanel pnlFollowersAvailable;
	private JPanel pnlFollowersButtons;
	private JPanel pnlFollowersHeader;
	private JPanel pnlFollowersSelected;
	private JPanel pnlHolyItem;
	private JPanel pnlLanguages;
	private JPanel pnlLanguagesAvailable;
	private JPanel pnlLanguagesButtons;
	private JPanel pnlLanguagesHeader;
	private JPanel pnlLanguagesSelected;
	private JPanel pnlMainDialog;
	private JPanel pnlMainTab;
	private JPanel pnlProductIdentity;
	private JPanel pnlQualifier;
	private JPanel pnlRaces;
	private JPanel pnlRacesAvailable;
	private JPanel pnlRacesButtons;
	private JPanel pnlRacesHeader;
	private JPanel pnlRacesSelected;
	private JPanel pnlSkills;
	private JPanel pnlSkillsAvailable;
	private JPanel pnlSkillsButtonsClass;
	private JPanel pnlSkillsButtonsCrossClass;
	private JPanel pnlSkillsHeader;
	private JPanel pnlSkillsSelectedClass;
	private JPanel pnlSkillsSelectedCrossClass;
	private JPanel pnlSource;
	private JPanel pnlTabs;
	private JPanel pnlVariable;
	private JPanel pnlVariableQualifier;
	private JPanel pnlWeapons;
	private JPanel pnlWeaponsAvailable;
	private JPanel pnlWeaponsButtons;
	private JPanel pnlWeaponsHeader;
	private JPanel pnlWeaponsSelected;
	private JPanel pnllstAdvancedSelected;
	private JPanel pnllstAdvancedTagValue;
	private JPanel pnllstDomainsAvailable;
	private JPanel pnllstDomainsSelected;
	private JPanel pnllstFavoredWeaponAvailable;
	private JPanel pnllstFavoredWeaponSelected;
	private JPanel pnllstFollowersAvailable;
	private JPanel pnllstFollowersSelected;
	private JPanel pnllstLanguagesAvailable;
	private JPanel pnllstLanguagesSelected;
	private JPanel pnllstRacesAvailable;
	private JPanel pnllstRacesSelected;
	private JPanel pnllstSkillsAvailable;
	private JPanel pnllstSkillsSelectedClass;
	private JPanel pnllstSkillsSelectedCrossClass;
	private JPanel pnllstWeaponsAvailable;
	private JPanel pnllstWeaponsSelected;
	private JScrollPane scpAdvancedSelected;
	private JScrollPane scpAdvancedTagValue;
	private JScrollPane scpDescription;
	private JScrollPane scpDomainsAvailable;
	private JScrollPane scpDomainsSelected;
	private JScrollPane scpFavoredWeaponAvailable;
	private JScrollPane scpFavoredWeaponSelected;
	private JScrollPane scpFollowersAvailable;
	private JScrollPane scpFollowersSelected;
	private JScrollPane scpLanguagesAvailable;
	private JScrollPane scpLanguagesSelected;
	private JScrollPane scpRacesAvailable;
	private JScrollPane scpRacesSelected;
	private JScrollPane scpSkillsAvailable;
	private JScrollPane scpSkillsSelectedClass;
	private JScrollPane scpSkillsSelectedCrossClass;
	private JScrollPane scpWeaponsAvailable;
	private JScrollPane scpWeaponsSelected;
	private JTabbedPane jTabbedPane1;
	private JTextArea txtAdvancedTagValue;
	private JTextArea txtDescription;
	private JTextField txtDeityName;
	private JTextField txtHolyItem;
	private JTextField txtSource;
	// End of variables declaration//GEN-END:variables


	private class JListModel extends AbstractListModel
	{
		private ArrayList listData = null;
		private boolean sort = false;

		public JListModel(ArrayList listdata)
		{
			initModel(listdata, false);
		}

		public JListModel(ArrayList listdata, boolean argSort)
		{
			initModel(listdata, argSort);
		}

		private void initModel(ArrayList listdata, boolean argSort)
		{
			listData = (ArrayList)listdata.clone();

			sort = argSort;
			if (sort)
			{
				Collections.sort(listData);
			}
		}


		public int getSize()
		{
			if (listData != null)
			{
				return listData.size();
			}
			return 0;
		}

		public Object[] getElements()
		{
			return listData.toArray();
		}

		public Object getElementAt(int i)
		{
			if ((listData != null) && (i < listData.size()))
			{
				return listData.get(i);
			}
			return null;
		}

		public void addElement(Object obj)
		{
			listData.add(obj);
			if (sort)
			{
				Collections.sort(listData);
			}
			fireIntervalAdded(this, 0, listData.size());
		}

		public boolean removeElement(Object obj)
		{
			final int idx = listData.indexOf(obj);
			listData.remove(idx);
			fireIntervalRemoved(this, idx, idx);
			return idx >= 0;
		}
	}


}
