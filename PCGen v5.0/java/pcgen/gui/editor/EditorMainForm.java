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
 * @(#) $Id: EditorMainForm.java,v 1.1 2006/02/21 01:07:44 vauchers Exp $

 */

package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
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
import pcgen.core.BioSet;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.Utility;
import pcgen.core.Variable;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;
import pcgen.util.PropertyFactory;

/**
 * <code>EditorMainForm</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

public final class EditorMainForm extends JDialog
{
	private JButton btnAddAdvanced;
	private JButton btnCancel;
	private JButton btnRemoveAdvanced;
	private JButton btnSave;
	private JComboBox cmbAdvancedTag;
	private JLabel lblAdvancedHeader;
	private JLabel lblAdvancedSelected;
	private JLabel lblAdvancedTag;
	private JLabel lblAdvancedTagValue;
	private JList lstAdvancedSelected;
	private JPanel pnlAdvanced;
	private JPanel pnlAdvancedAvailable;
	private JPanel pnlAdvancedButtons;
	private JPanel pnlAdvancedHeader;
	private JPanel pnlAdvancedSelected;
	private JPanel pnlAdvancedTag;
	private JPanel pnlAdvancedTagValue;
	private AgePanel pnlAge;
	private AppearancePanel pnlAppearance;
	private JPanel pnlBase2;
	private JPanel pnlButtons;
	private AvailableSelectedPanel pnlBonusLang;
	private AvailableSelectedPanel pnlDomains;
	private QualifiedAvailableSelectedPanel pnlQDomains;
	private AvailableSelectedPanel pnlFeats;
	private QualifiedAvailableSelectedPanel pnlFollowers;
	private AvailableSelectedPanel pnlLanguages;
	private LevelAbilitiesPanel pnlLevelAbilities;
	private MovementPanel pnlMovement;
	private VisionPanel pnlVision;
	private NaturalAttacksPanel pnlNaturalAttacks;
	private JPanel pnlMainDialog;
	private EditorBasePanel pnlMainTab;
	private AvailableSelectedPanel pnlRaces;
	private AvailableSelectedPanel pnlSkills;
	private JPanel pnlTabs;
	private AvailableSelectedPanel pnlTemplates;
	private AvailableSelectedPanel pnlVFeats;
	private AvailableSelectedPanel pnlWeapons;
	private JPanel pnllstAdvancedSelected;
	private JPanel pnllstAdvancedTagValue;
	private JScrollPane scpAdvancedSelected;
	private JScrollPane scpAdvancedTagValue;
	private JTabbedPane jTabbedPane1;
	private JTextArea txtAdvancedTagValue;
	private ClassAbilityPanel pnlClassAbility;
	private ClassLevelPanel pnlClassLevel;
	private SourceFilesPanel pnlFileTypes;

	//
	// Skills
	//
	private AvailableSelectedPanel pnlClasses;
	private QualifiedAvailableSelectedPanel pnlSynergy;
	private QualifiedAvailableSelectedPanel pnlQClasses;

	private PObject thisPObject = null;
	private boolean wasCancelled = true;

//
// tags from PObject:
// ADD, AUTO
// BONUS
// CCSKILL, CSKILL
// CHOOSE
// DEFINE, DESC, DESCISPI, DR
// KEY, KIT
// LANGAUTO
// NAME, NAMEISPI
// OUTPUTNAME
// PRExxx
// REGION, RESTRICT
// SA, SPELL, SR
// TYPE
// UDAM, UMULT
// VISION
// WEAPONAUTO
//
// tags from Class:
// ABB
// ADDDOMAINS
// ATTACKCYCLE
// BAB
// CAST
// CASTAS
// DEF
// DEITY
// DOMAIN
// EXCHANGELEVEL
// EXCLASS
// FEAT
// FEATAUTO
// HASSUBCLASS
// HD
// ITEMCREATE
// KNOWN
// KNOWNSPELLS
// KNOWNSPELLSFROMSPECIALTY
// LANGBONUS
// LEVELSPERFEAT
// MAXLEVEL
// MEMORIZE
// MODTOSKILLS
// MULTIPREREQS
// PROHIBITED
// QUALIFY
// REP
// SKILLLIST
// SPECIALS
// SPECIALTYKNOWN
// SPELLBOOK
// SPELLLIST
// SPELLSTAT
// SPELLTYPE
// STARTSKILLPTS
// SUBCLASS
// TEMPLATE
// UATT
// VFEAT
// VISIBLE
// WEAPONBONUS
// XTRAFEATS
// MONSKILL
// PRERACETYPE
//
// tags from Deity:
// ALIGN
// DEITYWEAP
// DOMAINS
// FOLLOWERALIGN
// PANTHEON
// QUALIFY
// RACE
// SYMBOL
//
// tags from Domain:
// FEAT
// QUALIFY
//
// tags from Feat:
// ADD
// COST
// MULT
// QUALIFY
// REP
// STACK
// VISIBLE
//
// tags from Language:
// --none--
//
// tags from Race:
// AC
// BAB
// CHOOSE:LANGAUTO
// CR
// FACE
// FAVCLASS
// FEAT
// HANDS
// HITDICE
// HITDIE
// HITDICEADVANCEMENT
// INIT
// LANGBONUS
// LANGNUM
// LEGS
// LEVELADJUSTMENT
// MFEAT
// MONSTERCLASS
// MOVE
// NATURALATTACKS
// PROF
// QUALIFY
// RACENAME
// REACH
// SIZE
// SKILL
// SKILLMULT
// STARTFEATS
// TEMPLATE
// VFEAT
// WEAPONBONUS
// XTRASKILLPTSPERLVL
//
// tags from Skill:
// ACHECK
// CLASSES
// EXCLUSIVE
// KEYSTAT
// QUALIFY
// REQ
// ROOT
// SYNERGY
// USEUNTRAINED
//
// tags from Spell:
// CASTTIME
// CLASSES
// COMPS
// COST
// DOMAINS
// EFFECTS (deprecated use DESC)
// EFFECTTYPE (deprecated use TARGETAREA)
// CT
// DESCRIPTOR
// DURATION
// ITEM
// LVLRANGE (Wheel of Time)
// QUALIFY
// RANGE
// SAVEINFO
// SCHOOL
// SPELLLEVEL (deprecated use CLASSES or DOMAINS)
// SPELLRES
// SUBSCHOOL
// TARGETAREA
// STAT
// VARIANTS
// XPCOST
//
//
	private static final String[] qualifiers = new String[]{"(None)", "VARDEFINED"};
	private static final String[] tags = new String[]{
		"ADD", "AUTO",
		"BONUS",
		"CHOOSE",
		"DEFINE", "DR",
		"KEY",
		"PANTHEON", "PREALIGN", "PREARMORTYPE", "PREATT", "PRECHECK", "PRECHECKBASE", "PRECLASS", "PRECLASSLEVELMAX", "PREDEFAULTMONSTER", "PREDEITY",
		"PREDEITYALIGN", "PREDEITYDOMAIN", "PREDOMAIN", "PREDSIDEPTS", "PREEQUIP", "PREEQUIPBOTH", "PREEQUIPPRIMARY", "PREEQUIPSECONDARY", "PREEQUIPTWOWEAPON",
		"PREFEAT", "PREFORCEPTS", "PREGENDER", "PREHANDSEQ", "PREHANDSGT", "PREHANDSGTEQ", "PREHANDSLT", "PREHANDSLTEQ", "PREHANDSNEQ", "PREHD", "PREHP",
		"PREITEM", "PRELANG", "PRELEVEL", "PRELEVELMAX", "PREMOVE", "PRERACE", "PREREPUTATION", "PREREPUTATIONLTEQ", "PRESA", "PRESIZEEQ", "PRESIZELT",
		"PRESIZELTEQ", "PRESIZEGT", "PRESIZEGTEQ", "PRESIZENEQ", "PRESKILL", "PRESKILLMULT", "PRESKILLTOT", "PRESPELL", "PRESPELLCAST", "PRESPELLSCHOOL",
		"PRESPELLSCHOOLSUB", "PRESPELLTYPE", "PRESTAT", "PRETEMPLATE", "PRETYPE", "PREUATT", "PREVAR", "PREWEAPONPROF",
		"QUALIFY",
		/*"RESTRICT",*/
		"SA", "SPELL", "SR",
		"UMDAM", "UMULT",
		"VISION"};

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
		initComponentContents();
		setLocationRelativeTo(parent);	// centre on parent
	}

	private void initComponentContents()
	{
		Iterator e;
		String aString;
		String bString;
		StringTokenizer aTok;
		ArrayList availableList = new ArrayList();
		ArrayList selectedList = new ArrayList();
		ArrayList selectedList2 = new ArrayList();
		ArrayList aList;
		String[] movementTypes;
		Integer[] movements;
		Integer[] movementMult;
		String[] movementMultOp;
		ArrayList movementValues;
		ArrayList visionValues;
		HashMap vision;
		ArrayList naturalAttacks;

		pnlMainTab.setNameText(thisPObject.getName());
		pnlMainTab.setProductIdentity(thisPObject.getNameIsPI());
		pnlMainTab.setSourceText(thisPObject.getSourcePage());

		pnlMainTab.updateView(thisPObject);

		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				//pnlMainTab.setDescriptionText(thisPObject.getDescription());	// don't want PI here
				//pnlMainTab.setDescIsPI(thisPObject.getDescIsPI());
				break;

			case EditorConstants.EDIT_DEITY:

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

			case EditorConstants.EDIT_DOMAIN:
				break;

			case EditorConstants.EDIT_FEAT:
				break;

			case EditorConstants.EDIT_LANGUAGE:
				break;

			case EditorConstants.EDIT_RACE:
				//
				// Populate the templates available list and selected lists
				//
				availableList.clear();
				selectedList.clear();
				selectedList2.clear();
				for (e = Globals.getTemplateList().iterator(); e.hasNext();)
				{
					final PCTemplate aTemplate = (PCTemplate) e.next();
					aString = aTemplate.getName();
					if (!availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}
				//
				// remove this race's granted templates from the available list and place into selected list
				//
				aList = ((Race) thisPObject).getTemplateList();
				for (e = aList.iterator(); e.hasNext();)
				{
					aString = (String) e.next();
					if (aString.startsWith("CHOOSE:"))
					{
						aTok = new StringTokenizer(aString.substring(7), "|", false);
						while (aTok.hasMoreTokens())
						{
							String chooseTemplate = aTok.nextToken();
							if (!selectedList.contains(chooseTemplate))
							{
								selectedList2.add(chooseTemplate);
							}
						}
					}
					else
					{
						selectedList.add(aString);
						availableList.remove(aString);
					}
				}
				pnlTemplates.setAvailableList(availableList, true);
				pnlTemplates.setSelectedList(selectedList, true);
				pnlTemplates.setSelectedList2(selectedList2, true);

				//
				// Populate the favoured classes available list and selected lists
				//
				availableList.clear();
				selectedList.clear();
				for (e = Globals.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass) e.next();
					availableList.add(aClass.getName());
				}
				availableList.add("Any");
				aString = ((Race) thisPObject).getFavoredClass();
				aTok = new StringTokenizer(aString, "|", false);
				while (aTok.hasMoreTokens())
				{
					String favouredClass = aTok.nextToken();
					if (!selectedList.contains(favouredClass))
					{
						final int idx = availableList.indexOf(favouredClass);
						if (idx < 0)
						{
							Globals.errorPrint("Unknown class: " + favouredClass);
							continue;
						}
						availableList.remove(idx);
						selectedList.add(favouredClass);
					}
				}
				pnlClasses.setAvailableList(availableList, true);
				pnlClasses.setSelectedList(selectedList, true);

				//
				// Populate the feats available list and selected lists
				//
				availableList.clear();
				selectedList.clear();
				selectedList2.clear();
				for (e = Globals.getFeatList().iterator(); e.hasNext();)
				{
					final Feat aFeat = (Feat) e.next();
					availableList.add(aFeat.getName());
				}
				aString = ((Race) thisPObject).getFeatList(false);
				aTok = new StringTokenizer(aString, "|", false);
				while (aTok.hasMoreTokens())
				{
					String featName = aTok.nextToken();
					if (!selectedList.contains(featName))
					{
						availableList.remove(featName);
						selectedList.add(featName);
					}
				}
				aString = ((Race) thisPObject).getMFeatList();
				aTok = new StringTokenizer(aString, "|", false);
				while (aTok.hasMoreTokens())
				{
					String featName = aTok.nextToken();
					if (!selectedList2.contains(featName))
					{
						availableList.remove(featName);
						selectedList2.add(featName);
					}
				}

				pnlFeats.setAvailableList(availableList, true);
				pnlFeats.setSelectedList(selectedList, true);
				pnlFeats.setSelectedList2(selectedList2, true);

				//
				// Populate the virtual feats available list and selected list
				//
				availableList.clear();
				selectedList.clear();
				for (e = Globals.getFeatList().iterator(); e.hasNext();)
				{
					final Feat aFeat = (Feat) e.next();
					availableList.add(aFeat.getName());
				}
				aString = ((Race) thisPObject).getVFeatList();
				aTok = new StringTokenizer(aString, "|", false);
				while (aTok.hasMoreTokens())
				{
					String featName = aTok.nextToken();
					if (!selectedList.contains(featName))
					{
						availableList.remove(featName);
						selectedList.add(featName);
					}
				}

				pnlVFeats.setAvailableList(availableList, true);
				pnlVFeats.setSelectedList(selectedList, true);

				//
				// Populate the bonus languages available list and selected lists
				//
				selectedList.clear();
				availableList.clear();
				aList = ((Race) thisPObject).getLanguageBonus();
				for (e = Globals.getLanguageList().iterator(); e.hasNext();)
				{
					final Language aLang = (Language) e.next();
					if (aList.contains(aLang))
					{
						selectedList.add(aLang);
					}
					else
					{
						availableList.add(aLang);
					}
				}
				pnlBonusLang.setAvailableList(availableList, true);
				pnlBonusLang.setSelectedList(selectedList, true);



				//
				// Populate the movement panel
				//
				movementTypes = ((Race) thisPObject).getMovementTypes();
				movements = ((Race) thisPObject).getMovements();

				movementValues = new ArrayList();
				if (movementTypes != null)
				{
					for (int index = 0; index < movementTypes.length; index++)
					{
						final String aMove = MovementPanel.makeMoveString(movementTypes[index], movements[index], null, null);
						movementValues.add(aMove);
					}
				}
				pnlMovement.setSelectedList(movementValues);

				//
				// Populate the vision panel
				//

				vision = ((Race) thisPObject).getVisionTable();

				visionValues = new ArrayList();
				if (vision != null)
				{
					for (Iterator i = vision.keySet().iterator(); i.hasNext();)
					{
						final StringBuffer vis = new StringBuffer(25);
						final String aKey = i.next().toString();
						final String aVal = vision.get(aKey).toString();

						vis.append(aKey);
						vis.append(',').append(aVal);

						visionValues.add(vis.toString());
					}
				}

				pnlVision.setSelectedList(visionValues);

				//
				// Populate the natural attacks panel
				//
				naturalAttacks = ((Race) thisPObject).getNaturalWeapons();
				pnlNaturalAttacks.setSelectedList(naturalAttacks);

				//
				// Populate the appearance panel
				//
				ArrayList eyeColorList = new ArrayList();
				ArrayList hairColorList = new ArrayList();
				ArrayList skinToneList = new ArrayList();
				for (e = Globals.getRaceMap().keySet().iterator(); e.hasNext();)
				{
					final String raceName = (String) e.next();
					final Race aRace = (Race) Globals.getRaceMap().get(raceName);
					aString = aRace.getRegionString();
					if (aString == null)
					{
						aString = Constants.s_NONE;
					}

					bString = BioSet.getTagForRace(aString, raceName, "HAIR");
					if (bString != null)
					{
						availableList = Utility.split(bString, '|');
						for (Iterator colIt = availableList.iterator(); colIt.hasNext();)
						{
							String color = (String) colIt.next();
							if (!hairColorList.contains(color))
							{
								hairColorList.add(color);
							}
						}
					}

					bString = BioSet.getTagForRace(aString, raceName, "EYES");
					if (bString != null)
					{
						availableList = Utility.split(bString, '|');
						for (Iterator colIt = availableList.iterator(); colIt.hasNext();)
						{
							String color = (String) colIt.next();
							if (!eyeColorList.contains(color))
							{
								eyeColorList.add(color);
							}
						}
					}

					bString = BioSet.getTagForRace(aString, raceName, "SKINTONE");
					if (bString != null)
					{
						availableList = Utility.split(bString, '|');
						for (Iterator colIt = availableList.iterator(); colIt.hasNext();)
						{
							String color = (String) colIt.next();
							if (!skinToneList.contains(color))
							{
								skinToneList.add(color);
							}
						}
					}
				}
				pnlAppearance.setEyeColorAvailableList(eyeColorList, true);
				pnlAppearance.setHairColorAvailableList(hairColorList, true);
				pnlAppearance.setSkinToneAvailableList(skinToneList, true);
				pnlAppearance.updateView(thisPObject);

				//
				// Populate the age panel
				//
				pnlAge.updateView(thisPObject);

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
							Globals.errorPrint("Unknown class: " + aString);
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
				break;

			case EditorConstants.EDIT_SPELL:
				((SpellBasePanel2) pnlBase2).updateView(thisPObject);

				HashMap lvlInfo = ((Spell) thisPObject).getLevelInfo();
				//
				// Initialize the contents of the available and selected domains lists
				//
				int iCount = 0;
				selectedList.clear();
				availableList.clear();
				for (e = Globals.getDomainList().iterator(); e.hasNext();)
				{
					final Domain aDomain = (Domain) e.next();
					Integer lvl = null;
					if (lvlInfo != null)
					{
						lvl = (Integer) lvlInfo.get("DOMAIN|" + aDomain.getName());
					}
					if (lvl != null)
					{
						selectedList.add(encodeDomainEntry(aDomain.getName(), lvl.toString()));
						++iCount;
					}
					else
					{
						availableList.add(aDomain.getName());
					}
				}
				pnlQDomains.setAvailableList(availableList, true);
				pnlQDomains.setSelectedList(selectedList, true);

				//
				// Initialize the contents of the available and selected classes lists
				//
				selectedList.clear();
				availableList.clear();
				for (e = Globals.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass) e.next();
					Integer lvl = null;
					if (lvlInfo != null)
					{
						lvl = (Integer) lvlInfo.get("CLASS|" + aClass.getName());
					}
					if (lvl != null)
					{
						selectedList.add(encodeDomainEntry(aClass.getName(), lvl.toString()));
						++iCount;
					}
					else
					{
						availableList.add(aClass.getName());
					}
				}
				pnlQClasses.setAvailableList(availableList, true);
				pnlQClasses.setSelectedList(selectedList, true);

				//
				// Inform the user if there is a domain/class defined for the spell that was not found
				//
				if ((lvlInfo != null) && (lvlInfo.size() != iCount))
				{
					Globals.errorPrint(Integer.toString(iCount) + " classes and domains found. Should have been " + Integer.toString(lvlInfo.size()) + "\n" + lvlInfo);
				}

				break;

			case EditorConstants.EDIT_TEMPLATE:
				//
				// Populate the templates available list and selected lists
				//
				availableList.clear();
				selectedList.clear();
				selectedList2.clear();
				for (e = Globals.getTemplateList().iterator(); e.hasNext();)
				{
					final PCTemplate aTemplate = (PCTemplate) e.next();
					aString = aTemplate.getName();
					if (!availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}
				//
				// remove this template's granted templates from the available list and place into selected list
				//
				ArrayList templateList = ((PCTemplate) thisPObject).getTemplateList();
				for (e = templateList.iterator(); e.hasNext();)
				{
					aString = (String) e.next();
					if (aString.startsWith("CHOOSE:"))
					{
						aTok = new StringTokenizer(aString.substring(7), "|", false);
						while (aTok.hasMoreTokens())
						{
							String chooseTemplate = aTok.nextToken();
							if (!selectedList.contains(chooseTemplate))
							{
								selectedList2.add(chooseTemplate);
							}
						}
					}
					else
					{
						selectedList.add(aString);
						availableList.remove(aString);
					}
				}
				pnlTemplates.setAvailableList(availableList, true);
				pnlTemplates.setSelectedList(selectedList, true);
				pnlTemplates.setSelectedList2(selectedList2, true);

				//
				// Populate the favoured classes available list and selected lists
				//
				availableList.clear();
				selectedList.clear();
				for (e = Globals.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass) e.next();
					availableList.add(aClass.getName());
				}
				availableList.add("Any");
				aString = ((PCTemplate) thisPObject).getFavoredClass();
				aTok = new StringTokenizer(aString, "|", false);
				while (aTok.hasMoreTokens())
				{
					String favouredClass = aTok.nextToken();
					if (!selectedList.contains(favouredClass))
					{
						final int idx = availableList.indexOf(favouredClass);
						if (idx < 0)
						{
							Globals.errorPrint("Unknown class: " + favouredClass);
							continue;
						}
						availableList.remove(idx);
						selectedList.add(favouredClass);
					}
				}
				pnlClasses.setAvailableList(availableList, true);
				pnlClasses.setSelectedList(selectedList, true);

				//
				// Populate the feats available list and selected lists
				//
				availableList.clear();
				selectedList.clear();
				for (e = Globals.getFeatList().iterator(); e.hasNext();)
				{
					final Feat aFeat = (Feat) e.next();
					availableList.add(aFeat.getName());
				}
				ArrayList featList = ((PCTemplate) thisPObject).feats(-1, -1);
				for (e = featList.iterator(); e.hasNext();)
				{
					aString = (String) e.next();
					if (!selectedList.contains(aString))
					{
						availableList.remove(aString);
						selectedList.add(aString);
					}
				}
				pnlFeats.setAvailableList(availableList, true);
				pnlFeats.setSelectedList(selectedList, true);

				//
				// Populate the movement panel
				//
				movementTypes = ((PCTemplate) thisPObject).getMovementTypes();
				movements = ((PCTemplate) thisPObject).getMovements();
				movementMult = ((PCTemplate) thisPObject).getMovementMult();
				movementMultOp = ((PCTemplate) thisPObject).getMovementMultOp();

				movementValues = new ArrayList();
				if (movementTypes != null)
				{
					for (int index = 0; index < movementTypes.length; index++)
					{
						final String aMove = MovementPanel.makeMoveString(movementTypes[index], movements[index], movementMult[index], movementMultOp[index]);
						movementValues.add(aMove);
					}
				}
				pnlMovement.setSelectedList(movementValues);
				pnlMovement.setMoveRateType(((PCTemplate) thisPObject).getMoveRatesFlag());

				//
				// Populate the abilities panel
				//
				selectedList.clear();
				ArrayList abilitiesList = ((PCTemplate) thisPObject).getLevelStrings();
				if (abilitiesList != null)
				{
					for (e = abilitiesList.iterator(); e.hasNext();)
					{
						aString = (String) e.next();
						selectedList.add("LEVEL:" + aString);
					}
				}
				abilitiesList = ((PCTemplate) thisPObject).getHitDiceStrings();
				if (abilitiesList != null)
				{
					for (e = abilitiesList.iterator(); e.hasNext();)
					{
						aString = (String) e.next();
						selectedList.add("HD:" + aString);
					}
				}
				pnlLevelAbilities.setSelectedList(selectedList);

				//
				// Populate the bonus languages available list and selected lists
				//
				selectedList.clear();
				availableList.clear();
				final Set aSet = ((PCTemplate) thisPObject).getLanguageBonus();
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
				pnlBonusLang.setAvailableList(availableList, true);
				pnlBonusLang.setSelectedList(selectedList, true);
				break;

			case EditorConstants.EDIT_CAMPAIGN:
				pnlFileTypes.updateView(thisPObject);
				break;

			default:
				break;
		}


		//
		// Initialize the contents of the available and selected languages lists
		//
		if (pnlLanguages != null)
		{
			selectedList.clear();
			selectedList2.clear();
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
			if (editType == EditorConstants.EDIT_TEMPLATE || editType == EditorConstants.EDIT_RACE)
			{
				if (editType == EditorConstants.EDIT_TEMPLATE)
				{
					aString = ((PCTemplate) thisPObject).getChooseLanguageAutos();
				}
				else
				{
					aString = ((Race) thisPObject).getChooseLanguageAutos();
				}
				aTok = new StringTokenizer(aString, "|", false);
				while (aTok.hasMoreTokens())
				{
					final Language aLang = Globals.getLanguageNamed(aTok.nextToken());
					if (aLang != null)
					{
						selectedList2.add(aLang);
						availableList.remove(aLang);
					}
				}
				pnlLanguages.setSelectedList2(selectedList2, true);
			}
			pnlLanguages.setAvailableList(availableList, true);
			pnlLanguages.setSelectedList(selectedList, true);
		}

		//
		// Initialize the contents of the available and selected weapon prof lists
		//
		if (pnlWeapons != null)
		{
			selectedList.clear();
			availableList = Globals.getWeaponProfArrayCopy();
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
			if (editType == EditorConstants.EDIT_CLASS)
			{
				e = ((PCClass) thisPObject).getWeaponProfBonus().iterator();
			}
			else if (editType == EditorConstants.EDIT_RACE)
			{
				e = ((Race) thisPObject).getWeaponProfBonus().iterator();
			}
			else
			{
				e = null;
			}
			if (e != null)
			{
				selectedList2.clear();
				while (e.hasNext())
				{
					final WeaponProf wp = Globals.getWeaponProfNamed((String) e.next());
					if (wp != null)
					{
						selectedList2.add(wp);
						availableList.remove(wp);
					}
				}
				pnlWeapons.setSelectedList2(selectedList2, true);
				pnlWeapons.setLblSelectedText(PropertyFactory.getString("in_demAllGranted"));
				pnlWeapons.setLblSelected2Text(PropertyFactory.getString("in_demChoiceGranted"));
			}
			pnlWeapons.setAvailableList(availableList, true);
			pnlWeapons.setSelectedList(selectedList, true);
		}

		String[] values;
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				//
				// initialize the Qualifier combo on the Followers tab
				//
				pnlFollowers.setQualifierModel(new DefaultComboBoxModel(qualifiers));

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
				pnlFollowers.setVariableModel(new DefaultComboBoxModel(availableList.toArray()));
				break;

			case EditorConstants.EDIT_DOMAIN:
				break;

			case EditorConstants.EDIT_FEAT:
				break;

			case EditorConstants.EDIT_LANGUAGE:
				break;

			case EditorConstants.EDIT_RACE:
				break;

			case EditorConstants.EDIT_SKILL:
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
							Globals.errorPrint("Badly formed synergy element: " + aString);
							continue;
						}
						final String skill = aTok.nextToken();
						final String ranks = aTok.nextToken();
						final String bonus = aTok.nextToken();
						availableList.remove(skill);
						selectedList.add(encodeSynergyEntry(skill, ranks, bonus));
					}
				}

				//
				// BONUS:SKILL|Ride|2|PRESKILL:1,Handle Animal=5|TYPE=Synergy.STACK
				//
				for (e = thisPObject.getBonusList().iterator(); e.hasNext();)
				{
					parseSynergyBonus((String) e.next(), availableList, selectedList);
				}

				pnlSynergy.setAvailableList(availableList, true);
				pnlSynergy.setSelectedList(selectedList, true);

				//
				// initialize the Qualifier and Variables combos on the Synergy tab
				//
				values = new String[30];
				for (int i = 0; i < values.length; ++i)
				{
					values[i] = String.valueOf(i + 1);
				}
				pnlSynergy.setQualifierModel(new DefaultComboBoxModel(values));
				pnlSynergy.setVariableModel(new DefaultComboBoxModel(values));
				pnlSynergy.setQualifierSelectedIndex(4);	// should be 5
				pnlSynergy.setVariableSelectedIndex(1);	// should be 2
				break;

			case EditorConstants.EDIT_SPELL:
				//
				// Domains allow levels 1 to 9
				//
				availableList.clear();
				for (int i = 1; i <= 9; ++i)
				{
					availableList.add(String.valueOf(i));
				}
				pnlQDomains.setQualifierModel(new DefaultComboBoxModel(availableList.toArray()));
				pnlQDomains.setQualifierSelectedIndex(0);

				//
				// Classes allow levels 0-9
				//
				availableList.add(0, "0");
				pnlQClasses.setQualifierModel(new DefaultComboBoxModel(availableList.toArray()));
				pnlQClasses.setQualifierSelectedIndex(0);
				break;

			case EditorConstants.EDIT_TEMPLATE:
				break;

			default:
				break;
		}

		//
		// Initialize the contents of the available and selected class/cross-class lists
		//
		if (pnlSkills != null)
		{
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
		}

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
				cmbAdvancedTag.removeItem("ADD");
				cmbAdvancedTag.removeItem("CHOOSE");
				for (e = ((Deity) thisPObject).getPantheonList().iterator(); e.hasNext();)
				{
					selectedList.add("PANTHEON:" + (String) e.next());
				}
				break;

			case EditorConstants.EDIT_LANGUAGE:
			case EditorConstants.EDIT_SPELL:
				cmbAdvancedTag.removeItem("ADD");
				cmbAdvancedTag.removeItem("CHOOSE");
				cmbAdvancedTag.removeItem("PANTHEON");
				break;

			case EditorConstants.EDIT_DOMAIN:
				cmbAdvancedTag.removeItem("ADD");
				cmbAdvancedTag.removeItem("PANTHEON");
				if (thisPObject.getChoiceString().length() != 0)
				{
					selectedList.add("CHOOSE:" + thisPObject.getChoiceString());
				}
				break;

			case EditorConstants.EDIT_FEAT:
				//cmbAdvancedTag.removeItem("CHOOSE");
				cmbAdvancedTag.removeItem("PANTHEON");
				if (thisPObject.getChoiceString().length() != 0)
				{
					selectedList.add("CHOOSE:" + thisPObject.getChoiceString());
				}
				break;

			case EditorConstants.EDIT_RACE:
				cmbAdvancedTag.removeItem("ADD");
				cmbAdvancedTag.removeItem("PANTHEON");
				break;

			case EditorConstants.EDIT_SKILL:
				cmbAdvancedTag.removeItem("ADD");
				cmbAdvancedTag.removeItem("PANTHEON");
				if (((Skill) thisPObject).getChoiceList() != null)
				{
					for (e = ((Skill) thisPObject).getChoiceList().iterator(); e.hasNext();)
					{
						selectedList.add("CHOOSE:" + (String) e.next());
					}
				}
				break;

			case EditorConstants.EDIT_TEMPLATE:
				cmbAdvancedTag.removeItem("ADD");
				cmbAdvancedTag.removeItem("PANTHEON");
				break;

			default:
				break;
		}

		final ArrayList autoList = thisPObject.getAutoArray();
		if (autoList != null)
		{
			for (e = autoList.iterator(); e.hasNext();)
			{
				selectedList.add("AUTO:" + e.next().toString());
			}
		}

		for (e = thisPObject.getBonusList().iterator(); e.hasNext();)
		{
			aString = (String) e.next();
			if (!parseSynergyBonus(aString, null, null))
			{
				selectedList.add("BONUS:" + aString);
			}
		}
		for (int i = 0, x = thisPObject.getPreReqCount(); i < x; ++i)
		{
			selectedList.add(thisPObject.getPreReq(i));
		}
		for (int i = 0, x = thisPObject.getVariableCount(); i < x; ++i)
		{
			aString = thisPObject.getVariableDefinition(i);
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
				Object specialAbility = e.next();
				String saSource = ((SpecialAbility) specialAbility).getSource();
				String saLevel = saSource.substring(saSource.indexOf("|") + 1);
				String saTxt = specialAbility.toString();
				if (saLevel.length() > 0)
				{
					saLevel += "|";
				}

				selectedList.add("SA:" + saLevel + saTxt);
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

		if (editType != EditorConstants.EDIT_CLASS)
		{
			for (int idx = 0; ; ++idx)
			{
				aString = thisPObject.getSpellListItemAsString(idx);
				if (aString == null)
				{
					break;
				}
				selectedList.add("SPELL:" + aString);
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
						parseAlignment(availableList, selectedList, aTok.nextToken(), " [" + qualifierType + ":" + variableName + ']');
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
				PObject pobj = (PObject) obj;
				for (Iterator i = pobj.getVariableIterator(); i.hasNext();)
				{
					Variable var = (Variable) i.next();
					if (!var.getUpperName().startsWith("LOCK.") && !availableList.contains(var.getName()))
					{
						availableList.add(var.getName());
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

		//
		// There's got to be a better/easier way to do this...
		//
		try
		{
			btnAddAdvanced = new JButton(new ImageIcon(Globals.getRootFrame().getClass().getResource("resource/Forward16.gif")));
			btnRemoveAdvanced = new JButton(new ImageIcon(Globals.getRootFrame().getClass().getResource("resource/Back16.gif")));
		}
		catch (Exception exc)
		{
			btnAddAdvanced = new JButton(">");
			btnRemoveAdvanced = new JButton("<");
		}
		btnCancel = new JButton();
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
		pnlMainDialog = new JPanel();
		pnlTabs = new JPanel();
		pnllstAdvancedSelected = new JPanel();
		pnllstAdvancedTagValue = new JPanel();
		scpAdvancedSelected = new JScrollPane();
		scpAdvancedTagValue = new JScrollPane();
		txtAdvancedTagValue = new JTextArea();

		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				pnlLanguages = new AvailableSelectedPanel();
				pnlSkills = new AvailableSelectedPanel(true);
				pnlWeapons = new AvailableSelectedPanel(true);
				pnlClassAbility = new ClassAbilityPanel();
				pnlClassAbility.updateView(thisPObject);
				pnlClassLevel = new ClassLevelPanel();
				pnlClassLevel.updateView(thisPObject);
				break;

			case EditorConstants.EDIT_DEITY:
				pnlLanguages = new AvailableSelectedPanel();
				pnlSkills = new AvailableSelectedPanel(true);
				pnlWeapons = new AvailableSelectedPanel();
				//cmbQualifier = new JComboBox();
				//cmbVariable = new JComboBox();
				//lblQualifier = new JLabel();
				//lblVariable = new JLabel();
				pnlDomains = new AvailableSelectedPanel();
				pnlFollowers = new QualifiedAvailableSelectedPanel("in_demQualifier", "in_demVariable",
					new EditorAddFilter()
					{
						public Object encode(Object anObj)
						{
							return encodeFollowerEntry((String) anObj);
						}

						public Object decode(Object anObj)
						{
							return decodeFollowerEntry((String) anObj);
						}
					},
					new ItemListener()
					{
						public void itemStateChanged(ItemEvent evt)
						{
							cmbQualifierItemStateChanged();
						}
					});
				pnlRaces = new AvailableSelectedPanel();
				break;

			case EditorConstants.EDIT_DOMAIN:
				pnlLanguages = new AvailableSelectedPanel();
				pnlSkills = new AvailableSelectedPanel(true);
				pnlWeapons = new AvailableSelectedPanel();
				break;

			case EditorConstants.EDIT_FEAT:
				pnlLanguages = new AvailableSelectedPanel();
				pnlSkills = new AvailableSelectedPanel(true);
				pnlWeapons = new AvailableSelectedPanel();
				break;

			case EditorConstants.EDIT_LANGUAGE:
				break;

			case EditorConstants.EDIT_RACE:
				pnlMovement = new MovementPanel(true);
				pnlVision = new VisionPanel();
				pnlNaturalAttacks = new NaturalAttacksPanel();
				pnlLanguages = new AvailableSelectedPanel(true);
				pnlSkills = new AvailableSelectedPanel(true);
				pnlTemplates = new AvailableSelectedPanel(true);
				pnlWeapons = new AvailableSelectedPanel(true);
				pnlClasses = new AvailableSelectedPanel();
				pnlFeats = new AvailableSelectedPanel(true);
				pnlVFeats = new AvailableSelectedPanel();
				pnlBonusLang = new AvailableSelectedPanel();
				pnlAppearance = new AppearancePanel();
				pnlAge = new AgePanel();
				break;

			case EditorConstants.EDIT_SKILL:
				pnlLanguages = new AvailableSelectedPanel();
				pnlSkills = new AvailableSelectedPanel(true);
				pnlWeapons = new AvailableSelectedPanel();
				//cmbQualifier = new JComboBox();
				//cmbVariable = new JComboBox();
				//lblQualifier = new JLabel();
				//lblVariable = new JLabel();
				pnlClasses = new AvailableSelectedPanel(true);
				pnlSynergy = new QualifiedAvailableSelectedPanel("in_demSkillRank", "in_demSynergyBonus", new EditorAddFilter()
				{
					public Object encode(Object anObj)
					{
						return encodeSynergyEntry((String) anObj);
					}

					public Object decode(Object anObj)
					{
						return decodeSynergyEntry((String) anObj);
					}
				},
					null);
				break;

			case EditorConstants.EDIT_SPELL:
				pnlBase2 = new SpellBasePanel2();
				pnlQClasses = new QualifiedAvailableSelectedPanel("in_demLevel", null, new EditorAddFilter()
				{
					public Object encode(Object anObj)
					{
						return encodeDomainEntry(pnlQClasses, (String) anObj);
					}

					public Object decode(Object anObj)
					{
						return decodeDomainEntry((String) anObj);
					}
				},
					null);
				pnlQDomains = new QualifiedAvailableSelectedPanel("in_demLevel", null, new EditorAddFilter()
				{
					public Object encode(Object anObj)
					{
						return encodeDomainEntry(pnlQDomains, (String) anObj);
					}

					public Object decode(Object anObj)
					{
						return decodeDomainEntry((String) anObj);
					}
				},
					null);
				break;

			case EditorConstants.EDIT_TEMPLATE:
				pnlMovement = new MovementPanel(false);
				pnlLevelAbilities = new LevelAbilitiesPanel();
				pnlLanguages = new AvailableSelectedPanel(true);
				pnlSkills = new AvailableSelectedPanel(true);
				pnlTemplates = new AvailableSelectedPanel(true);
				pnlWeapons = new AvailableSelectedPanel();
				pnlClasses = new AvailableSelectedPanel();
				pnlFeats = new AvailableSelectedPanel();
				pnlBonusLang = new AvailableSelectedPanel();
				break;

			case EditorConstants.EDIT_CAMPAIGN:
				pnlFileTypes = new SourceFilesPanel();
				break;

			default:
				break;
		}

		getContentPane().setLayout(new GridBagLayout());

		String ttl = "";
		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				ttl = "Class";
				break;

			case EditorConstants.EDIT_DEITY:
				ttl = "Deity";
				break;

			case EditorConstants.EDIT_DOMAIN:
				ttl = "Domain";
				break;

			case EditorConstants.EDIT_FEAT:
				ttl = "Feat";
				break;

			case EditorConstants.EDIT_LANGUAGE:
				ttl = "Language";
				break;

			case EditorConstants.EDIT_RACE:
				ttl = "Race";
				break;

			case EditorConstants.EDIT_SKILL:
				ttl = "Skill";
				break;

			case EditorConstants.EDIT_SPELL:
				ttl = "Spell";
				break;

			case EditorConstants.EDIT_TEMPLATE:
				ttl = "Template";
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
				closeDialog();
			}
		});

		pnlMainDialog.setLayout(new GridBagLayout());

		pnlMainDialog.setPreferredSize(new Dimension(640, 460));
		pnlTabs.setLayout(new BorderLayout());

		pnlTabs.setMinimumSize(new Dimension(128, 88));
		pnlTabs.setPreferredSize(new Dimension(640, 440));
		jTabbedPane1.setName(PropertyFactory.getString("in_demDeitytab"));
		pnlMainTab = new EditorBasePanel(editType);

		jTabbedPane1.addTab(PropertyFactory.getString("in_demBase"), pnlMainTab);

		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				jTabbedPane1.addTab(PropertyFactory.getString("in_classability"), pnlClassAbility);
				jTabbedPane1.addTab(PropertyFactory.getString("in_classlevel"), pnlClassLevel);
				break;

			case EditorConstants.EDIT_DEITY:
				pnlDomains.setHeader(PropertyFactory.getString("in_demGrantDom"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_domains"), pnlDomains);

				//buildFollowersTab();
				jTabbedPane1.addTab(PropertyFactory.getString("in_demFollowers"), pnlFollowers);

				pnlRaces.setHeader(PropertyFactory.getString("in_demRacWors"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_race"), pnlRaces);
				break;

			case EditorConstants.EDIT_DOMAIN:
				break;

			case EditorConstants.EDIT_FEAT:
				break;

			case EditorConstants.EDIT_LANGUAGE:
				break;

			case EditorConstants.EDIT_RACE:
				pnlClasses.setHeader(PropertyFactory.getString("in_demFavoredClasses"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_demClasses"), pnlClasses);
				pnlTemplates.setLblSelectedText(PropertyFactory.getString("in_demAllGranted"));
				pnlTemplates.setLblSelected2Text(PropertyFactory.getString("in_demChoiceGranted"));
				pnlLanguages.setLblSelectedText(PropertyFactory.getString("in_demAllGranted"));
				pnlLanguages.setLblSelected2Text(PropertyFactory.getString("in_demChoiceGranted"));
				pnlBonusLang.setHeader(PropertyFactory.getString("in_demBonusLang"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_demBonusLangAbbrev"), pnlBonusLang);
				pnlFeats.setLblSelectedText(PropertyFactory.getString("in_demSelFeats"));
				pnlFeats.setLblSelected2Text(PropertyFactory.getString("in_demSelMFeats"));
				break;

			case EditorConstants.EDIT_SKILL:
				//buildSynergyTab();
				pnlSynergy.setHeader(" ");
				jTabbedPane1.addTab(PropertyFactory.getString("in_demSynergy"), pnlSynergy);

				pnlClasses.setHeader(" ");
				jTabbedPane1.addTab(PropertyFactory.getString("in_demClasses"), pnlClasses);
				break;

			case EditorConstants.EDIT_SPELL:
				jTabbedPane1.addTab("?Base2?", pnlBase2);
				jTabbedPane1.addTab(PropertyFactory.getString("in_demClasses"), pnlQClasses);
				jTabbedPane1.addTab(PropertyFactory.getString("in_domains"), pnlQDomains);
				break;

			case EditorConstants.EDIT_TEMPLATE:
				pnlClasses.setHeader(PropertyFactory.getString("in_demFavoredClasses"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_demClasses"), pnlClasses);
				pnlTemplates.setLblSelectedText(PropertyFactory.getString("in_demAllGranted"));
				pnlTemplates.setLblSelected2Text(PropertyFactory.getString("in_demChoiceGranted"));
				pnlLanguages.setLblSelectedText(PropertyFactory.getString("in_demAllGranted"));
				pnlLanguages.setLblSelected2Text(PropertyFactory.getString("in_demChoiceGranted"));
				pnlBonusLang.setHeader(PropertyFactory.getString("in_demBonusLang"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_demBonusLangAbbrev"), pnlBonusLang);
				break;

			case EditorConstants.EDIT_CAMPAIGN:
				jTabbedPane1.addTab(PropertyFactory.getString("in_fileTypes"), pnlFileTypes);
				break;

			default:
				break;
		}

		if (pnlLanguages != null)
		{
			pnlLanguages.setHeader(PropertyFactory.getString("in_demGrantLang"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_languages"), pnlLanguages);

		}

		if (pnlWeapons != null)
		{
			pnlWeapons.setHeader(PropertyFactory.getString("in_demGraWeaPro"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_weapon"), pnlWeapons);
		}

		if (pnlSkills != null)
		{
			pnlSkills.setHeader(PropertyFactory.getString("in_demGraSkil"));
			pnlSkills.setLblSelectedText(PropertyFactory.getString("in_demSelClaSkil"));
			pnlSkills.setLblSelected2Text(PropertyFactory.getString("in_demSelCroCla"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_skills"), pnlSkills);
		}

		if (pnlLevelAbilities != null)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_abilities"), pnlLevelAbilities);
		}

		if (pnlMovement != null)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_movement"), pnlMovement);
		}

		if (pnlTemplates != null)
		{
			pnlTemplates.setHeader(PropertyFactory.getString("in_demGraTemp"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_templates"), pnlTemplates);
		}

		if (pnlVision != null)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_demVision"), pnlVision);
		}

		if (pnlAge != null)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_demAge"), pnlAge);
		}

		if (pnlAppearance != null)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_demAppearance"), pnlAppearance);
		}

		if (pnlNaturalAttacks != null)
		{
			jTabbedPane1.addTab("Natural Weapons", pnlNaturalAttacks);
		}

		if (pnlFeats != null)
		{
			pnlFeats.setHeader(PropertyFactory.getString("in_demGraFeat"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_feats"), pnlFeats);
		}

		if (pnlVFeats != null)
		{
			pnlVFeats.setHeader(PropertyFactory.getString("in_demGraVFeat"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_demVFeats"), pnlVFeats);
		}

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
			public void keyReleased(KeyEvent evt)
			{
				super.keyReleased(evt);
				txtAdvancedTagValueKeyReleased();
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

		//btnAddAdvanced.setMnemonic(PropertyFactory.getMnemonic("in_mn_add"));
		//btnAddAdvanced.setText(PropertyFactory.getString("in_add"));
		//btnAddAdvanced.setPreferredSize(new Dimension(81, 26));
		btnAddAdvanced.setEnabled(false);
		btnAddAdvanced.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddAdvancedActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlAdvancedButtons.add(btnAddAdvanced, gridBagConstraints);

		//btnRemoveAdvanced.setMnemonic(PropertyFactory.getMnemonic("in_mn_remove"));
		//btnRemoveAdvanced.setText(PropertyFactory.getString("in_remove"));
		//btnRemoveAdvanced.setPreferredSize(new Dimension(81, 26));
		btnRemoveAdvanced.setEnabled(false);
		btnRemoveAdvanced.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveAdvancedActionPerformed();
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

		btnCancel.setMnemonic(PropertyFactory.getMnemonic("in_mn_cancel"));
		btnCancel.setText(PropertyFactory.getString("in_cancel"));
		btnCancel.setPreferredSize(new Dimension(80, 26));
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnCancelActionPerformed();
			}
		});

		pnlButtons.add(btnCancel);

		btnSave.setMnemonic(PropertyFactory.getMnemonic("in_mn_save"));
		btnSave.setText(PropertyFactory.getString("in_save"));
		btnSave.setPreferredSize(new Dimension(80, 26));
		btnSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnSaveActionPerformed();
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
	}


	///////////////////////
	// Advanced tab
	//

	private void txtAdvancedTagValueKeyReleased()
	{
		btnAddAdvanced.setEnabled(txtAdvancedTagValue.getText().trim().length() != 0);
	}

	private void lstAdvancedSelectedMouseClicked(MouseEvent evt)
	{
		if (EditUtil.isDoubleClick(evt, lstAdvancedSelected, btnRemoveAdvanced))
		{
			btnRemoveAdvancedActionPerformed();
		}
	}

	private void btnRemoveAdvancedActionPerformed()
	{
		btnRemoveAdvanced.setEnabled(false);

		final JListModel lms = (JListModel) lstAdvancedSelected.getModel();
		final Object[] x = lstAdvancedSelected.getSelectedValues();
		for (int i = 0; i < x.length; ++i)
		{
			String entry = (String) x[i];
			final int idx = entry.indexOf(':');
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

	private void btnAddAdvancedActionPerformed()
	{
		btnAddAdvanced.setEnabled(false);

		String newEntry = (String) cmbAdvancedTag.getSelectedItem() + ":" + txtAdvancedTagValue.getText().trim();
		final JListModel lmd = (JListModel) lstAdvancedSelected.getModel();
		lmd.addElement(newEntry);
	}

	///////////////////////
	// Followers tab
	//

	private String decodeFollowerEntry(String entry)
	{
		int idx = -1;
		if (entry.indexOf('[') >= 0)
		{
			for (int j = 0; j < pnlFollowers.getQualifierItemCount(); ++j)
			{
				final String qualifier = " [" + (String) pnlFollowers.getQualifierItemAt(j) + ":";
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
		final String qualifier = (String) pnlFollowers.getQualifierSelectedItem();
		if ((qualifier != null) && !"(None)".equalsIgnoreCase(qualifier))
		{
			condition = " [" + qualifier + ":" + (String) pnlFollowers.getVariableSelectedItem() + ']';
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
	private void cmbQualifierItemStateChanged()
	{
		final String qualifier = (String) pnlFollowers.getQualifierSelectedItem();
		if ((qualifier != null) && !"(None)".equalsIgnoreCase(qualifier))
		{
			pnlFollowers.setVariableEnabled(true);
		}
		else
		{
			pnlFollowers.setVariableEnabled(false);
		}
	}

	///////////////////////
	// Synergy tab
	//
/*	private void buildSynergyTab()
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
		});
	}
*/
	private String decodeSynergyEntry(String entry)
	{
		int idx = -1;
		if (entry.indexOf('=') >= 0)
		{
			for (int j = 0; j < pnlSynergy.getQualifierItemCount(); ++j)
			{
				final String qualifier = "=" + (String) pnlSynergy.getQualifierItemAt(j) + "=";
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
		if (pnlSynergy.getQualifierSelectedIndex() >= 0)
		{
			qualifier = pnlSynergy.getQualifierSelectedItem().toString();
			if (pnlSynergy.getVariableSelectedIndex() >= 0)
			{
				condition = pnlSynergy.getVariableSelectedItem().toString();
			}
		}
		return encodeSynergyEntry(newEntry, qualifier, condition);
	}

	private static String encodeSynergyEntry(String newEntry, String qualifier, String condition)
	{
		if ((qualifier != null) && (condition != null))
		{
			newEntry = newEntry + "=" + qualifier + "=" + condition;
		}
		return newEntry;
	}

	//////////////////////////////////////////

	private void btnSaveActionPerformed()
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

		pnlMainTab.updateData(thisPObject);

		thisPObject.getBonusList().clear();
		thisPObject.clearVariableList();
		thisPObject.setDR(".CLEAR");
		thisPObject.addPreReq("PRE:.CLEAR");
		thisPObject.setSpecialAbilityList(".CLEAR", -9);
		thisPObject.setSR(".CLEAR");
		thisPObject.clearSpellList();
		thisPObject.clearAutoList();

		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
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
					aString = EditUtil.delimitArray(sel, ',');
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
				aString = EditUtil.delimitArray(sel, '|');
				((Deity) thisPObject).setRaceList(aString);
				break;

			case EditorConstants.EDIT_DOMAIN:
				break;

			case EditorConstants.EDIT_FEAT:
				break;

			case EditorConstants.EDIT_LANGUAGE:
				break;

			case EditorConstants.EDIT_RACE:
				Race thisRace = (Race) thisPObject;
				//thisRace.setMovements(pnlMovement.getMoveRates());
				thisRace.setMoveRates(pnlMovement.getMoveValues());
				thisRace.setMovementTypes(pnlMovement.getMoveTypes());
				thisRace.setVisionTable(pnlVision.getVision());
				thisRace.setNaturalWeapons(pnlNaturalAttacks.getNaturalWeapons());
				pnlAppearance.updateData(thisPObject);
				pnlAge.updateData(thisPObject);

				//
				// Save granted templates
				//
				thisRace.addTemplate(".CLEAR");
				sel = pnlTemplates.getSelectedList();
				for (int index = 0; index < sel.length; index++)
				{
					thisRace.addTemplate((String) sel[index]);
				}

				//
				// Save choice of templates
				//
				sel = pnlTemplates.getSelectedList2();
				aString = EditUtil.delimitArray(sel, '|');
				if (aString != null && aString.length() > 0)
				{
					thisRace.addTemplate("CHOOSE:" + aString);
				}


				//
				// Save favoured classes
				//
				sel = pnlClasses.getSelectedList();
				aString = EditUtil.delimitArray(sel, '|');
				thisRace.setFavoredClass(aString);

				//
				// Save choice of auto languages
				//
				sel = pnlLanguages.getSelectedList2();
				aString = EditUtil.delimitArray(sel, '|');
				thisRace.setChooseLanguageAutos(aString);

				//
				// Save feats
				//
				sel = pnlFeats.getSelectedList();
				aString = EditUtil.delimitArray(sel, '|');
				thisRace.setFeatList(aString);
				sel = pnlFeats.getSelectedList2();
				aString = EditUtil.delimitArray(sel, '|');
				thisRace.setMFeatList(aString);

				//
				// Save virtual feats
				//
				sel = pnlVFeats.getSelectedList();
				aString = EditUtil.delimitArray(sel, '|');
				thisRace.setVFeatList(aString);


				//
				// Save bonus languages
				//
				thisRace.setLanguageBonus(".CLEAR");
				sel = pnlBonusLang.getSelectedList();
				aString = EditUtil.delimitArray(sel, ',');
				thisRace.setLanguageBonus(aString);
				break;

			case EditorConstants.EDIT_SKILL:
				//
				// synergyList method has been deprecated. Translate into BONUS instead
				//
				((Skill) thisPObject).getSynergyList().clear();
				sel = pnlSynergy.getSelectedList();
				// BONUS:SKILL|Ride|2|PRESKILL:1,Handle Animal=5|TYPE=Synergy.STACK
				StringTokenizer aTok;
				for (int i = 0; i < sel.length; ++i)
				{
					//((Skill) thisPObject).addSynergyList(sel[i].toString());
					aTok = new StringTokenizer(sel[i].toString(), "=");
					if (aTok.countTokens() == 3)
					{
						final String skillName = aTok.nextToken();
						final String skillRank = aTok.nextToken();
						StringBuffer sb = new StringBuffer(50);
						sb.append("SKILL|").append(thisPObject.getName());
						sb.append('|').append(aTok.nextToken());
						sb.append("|PRESKILL:1,").append(skillName).append('=').append(skillRank);
						sb.append("|TYPE=Synergy.STACK");
						thisPObject.addBonusList(sb.toString());
					}
					else
					{
						Globals.errorPrint("Synergy has invalid format: " + sel[i].toString());
					}
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

			case EditorConstants.EDIT_SPELL:
				((SpellBasePanel2) pnlBase2).updateData(thisPObject);

				((Spell) thisPObject).setLevelInfo(".CLEAR", 0);
				sel = pnlQClasses.getSelectedList();
				for (int i = 0; i < sel.length; ++i)
				{
					aString = sel[i].toString();
					final int idx = aString.indexOf('=');
					if (idx > 0)
					{
						((Spell) thisPObject).setLevelInfo("CLASS|" + aString.substring(0, idx), aString.substring(idx + 1));
					}
				}

				sel = pnlQDomains.getSelectedList();
				for (int i = 0; i < sel.length; ++i)
				{
					aString = sel[i].toString();
					final int idx = aString.indexOf('=');
					if (idx > 0)
					{
						((Spell) thisPObject).setLevelInfo("DOMAIN|" + aString.substring(0, idx), aString.substring(idx + 1));
					}
				}
				break;

			case EditorConstants.EDIT_TEMPLATE:
				PCTemplate thisPCTemplate = (PCTemplate) thisPObject;
				thisPCTemplate.setMoveRates(pnlMovement.getMoveValues());
				thisPCTemplate.setMoveRatesFlag(pnlMovement.getMoveRateType());
				//
				// Save granted templates
				//
				thisPCTemplate.addTemplate(".CLEAR");
				sel = pnlTemplates.getSelectedList();
				for (int index = 0; index < sel.length; index++)
				{
					thisPCTemplate.addTemplate((String) sel[index]);
				}

				//
				// Save choice of templates
				//
				sel = pnlTemplates.getSelectedList2();
				aString = EditUtil.delimitArray(sel, '|');
				if (aString != null && aString.length() > 0)
				{
					thisPCTemplate.addTemplate("CHOOSE:" + aString);
				}


				//
				// Save favoured classes
				//
				sel = pnlClasses.getSelectedList();
				aString = EditUtil.delimitArray(sel, '|');
				thisPCTemplate.setFavoredClass(aString);

				//
				// Save choice of auto languages
				//
				sel = pnlLanguages.getSelectedList2();
				aString = EditUtil.delimitArray(sel, '|');
				thisPCTemplate.setChooseLanguageAutos(aString);

				//
				// Save feats
				//
				thisPCTemplate.addFeatString(".CLEAR");
				sel = pnlFeats.getSelectedList();
				aString = EditUtil.delimitArray(sel, '|');
				thisPCTemplate.addFeatString(aString);

				//
				// Save bonus languages
				//
				thisPCTemplate.setLanguageBonus(".CLEAR");
				sel = pnlBonusLang.getSelectedList();
				aString = EditUtil.delimitArray(sel, ',');
				thisPCTemplate.setLanguageBonus(aString);

				//
				// Save level and hit dice abilities
				//
				thisPCTemplate.addHitDiceString(".CLEAR");
				thisPCTemplate.addLevelString(".CLEAR");
				sel = pnlLevelAbilities.getSelectedList();
				for (int index = 0; index < sel.length; index++)
				{
					aString = (String) sel[index];
					if (aString.startsWith("HD:"))
					{
						thisPCTemplate.addHitDiceString(aString.substring(3));
					}
					else if (aString.startsWith("LEVEL:"))
					{
						thisPCTemplate.addLevelString(aString.substring(6));
					}
				}

				break;

			case EditorConstants.EDIT_CAMPAIGN:
				pnlFileTypes.updateData(thisPObject);
				break;

			default:
				break;
		}

		//
		// Save granted languages
		//
		if (pnlLanguages != null)
		{
			thisPObject.addLanguageAutos(".CLEAR");
			sel = pnlLanguages.getSelectedList();
			aString = EditUtil.delimitArray(sel, ',');
			thisPObject.addLanguageAutos(aString);
		}

		//
		// Save auto weapon proficiencies
		//
		if (pnlWeapons != null)
		{
			thisPObject.setWeaponProfAutos(".CLEAR");
			sel = pnlWeapons.getSelectedList();
			aString = EditUtil.delimitArray(sel, '|');
			thisPObject.setWeaponProfAutos(aString);

			sel = pnlWeapons.getSelectedList2();
			if (sel != null)
			{
				aString = EditUtil.delimitArray(sel, '|');
				if (editType == EditorConstants.EDIT_CLASS)
				{
					((PCClass) thisPObject).setWeaponProfBonus(aString);
				}
				else if (editType == EditorConstants.EDIT_RACE)
				{
					((Race) thisPObject).setWeaponProfBonus(aString);
				}
			}
		}

// TODO: check if all skills of one type are selected...maybe change to TYPE.blah?

		if (pnlSkills != null)
		{
			//
			// Save granted class skills
			//
			thisPObject.setCSkillList(".CLEAR");
			sel = pnlSkills.getSelectedList();
			aString = EditUtil.delimitArray(sel, '|');
			thisPObject.setCSkillList(aString);

			//
			// Save granted cross class skills
			//
			thisPObject.setCcSkillList(".CLEAR");
			sel = pnlSkills.getSelectedList2();
			aString = EditUtil.delimitArray(sel, '|');
			thisPObject.setCcSkillList(aString);
		}

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

			case EditorConstants.EDIT_DOMAIN:
				break;

			case EditorConstants.EDIT_FEAT:
				break;

			case EditorConstants.EDIT_LANGUAGE:
				break;

			case EditorConstants.EDIT_RACE:
				break;

			case EditorConstants.EDIT_SKILL:
				((Skill) thisPObject).addChoiceList(".CLEAR");
				break;

			case EditorConstants.EDIT_SPELL:
				break;

			case EditorConstants.EDIT_TEMPLATE:
				break;

			default:
				break;
		}
		if (editType == EditorConstants.EDIT_SKILL)
		{
			((Skill) thisPObject).addChoiceList(".CLEAR");
		}
		else if ((editType == EditorConstants.EDIT_DOMAIN) || (editType == EditorConstants.EDIT_FEAT))
		{
			thisPObject.setChoiceString("");
		}

		sel = ((JListModel) lstAdvancedSelected.getModel()).getElements();
		if (editType == EditorConstants.EDIT_CLASS)
		{
			pnlClassAbility.updateData(thisPObject);
			pnlClassLevel.updateData(thisPObject);
		}
		for (int i = 0; i < sel.length; ++i)
		{
			aString = (String) sel[i];
			if ((editType == EditorConstants.EDIT_FEAT) && aString.startsWith("ADD:"))
			{
			}
			if (aString.startsWith("AUTO:"))
			{
				thisPObject.addAutoArray(aString.substring(5));
			}
			else if (aString.startsWith("BONUS:"))
			{
				thisPObject.addBonusList(aString.substring(6));
			}
			else if (aString.startsWith("CHOOSE:"))
			{
				if (editType == EditorConstants.EDIT_SKILL)
				{
					((Skill) thisPObject).addChoiceList(aString.substring(7));
				}
				else if ((editType == EditorConstants.EDIT_DOMAIN) || (editType == EditorConstants.EDIT_FEAT))
				{
					thisPObject.setChoiceString(aString.substring(7));
				}
			}
			else if (aString.startsWith("DEFINE:"))
			{
//				thisPObject.addVariable("-9|" + aString.substring(7));
				thisPObject.addVariable(aString.substring(7));
			}
			else if (aString.startsWith("DR:"))
			{
				thisPObject.setDR(aString.substring(3));
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
				final int pipePos = aString.indexOf("|");
				int lvl = -9;
				int argPos = 3;
				if (pipePos > 0)
				// Need to check if the tag has level info
				{
					// Check for a numberic as the first argument after SA:
					try
					{

						lvl = Integer.parseInt(aString.substring(3, pipePos));
						// If we get here, then this is a number, so this is a level dependent tag
						// Set the position for the rest of the information after the level
						argPos = pipePos + 1;
					}
					catch (NumberFormatException exc)
					{
						// Not a number, so this is a level independent tag
						lvl = -9;
						// There is no level information, so set the argument position after the SA:
						argPos = 3;
					}

				}

				thisPObject.setSpecialAbilityList(aString.substring(argPos), lvl);
			}
			else if (aString.startsWith("SPELL:"))
			{
				if (editType == EditorConstants.EDIT_CLASS)
				{
				}
				else
				{
					thisPObject.addSpells(-9, aString.substring(6));
				}
			}
			else if (aString.startsWith("SR:"))
			{
				thisPObject.setSR(aString.substring(3));
			}
			else
			{
				Globals.errorPrint("Unknown advanced setting: " + aString);
			}
		}

		thisPObject.setTypeInfo(Constants.s_CUSTOM);

		wasCancelled = false;
		closeDialog();
	}

/*	private static String EditUtil.delimitArray(Object[] objArray, char delim)
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
*/
	private void btnCancelActionPerformed()
	{
		wasCancelled = true;
		closeDialog();
	}

	/** Closes the dialog */
	private void closeDialog()
	{
		hide();
		dispose();
	}

	public boolean wasCancelled()
	{
		return wasCancelled;
	}

	///////////////////////
	// Spells--Classes and Domains tabs
	//
	private static String decodeDomainEntry(String entry)
	{
		final int idx = entry.indexOf('=');
		if (idx >= 0)
		{
			entry = entry.substring(0, idx);
		}
		return entry;
	}

	private String encodeDomainEntry(QualifiedAvailableSelectedPanel pnl, String newEntry)
	{
		String qualifier = null;
		if (pnl.getQualifierSelectedIndex() >= 0)
		{
			qualifier = pnl.getQualifierSelectedItem().toString();
		}
		return encodeDomainEntry(newEntry, qualifier);
	}

	private static String encodeDomainEntry(String newEntry, final String qualifier)
	{
		if (qualifier != null)
		{
			newEntry = newEntry + "=" + qualifier;
		}
		return newEntry;
	}

	private static boolean parseSynergyBonus(final String aString, ArrayList availableList, ArrayList selectedList)
	{
		if (aString.startsWith("SKILL|"))
		{
			final ArrayList bonusParts = Utility.split(aString, '|');
			//
			// Should probably check if bonusParts(1) == <skill name>, but
			// if the user has just copied a skill with a synergy bonus, the
			// skill name will be incorrect. If we don't check the name, then
			// we can correct the naming when we save
			//
			if ((bonusParts.size() == 5) &&
				((String) bonusParts.get(3)).startsWith("PRESKILL:1,") &&
				((String) bonusParts.get(4)).equalsIgnoreCase("TYPE=Synergy.STACK")
			)
			{
				final String bonus = (String) bonusParts.get(2);
				String skill = ((String) bonusParts.get(3)).substring(11);
				final int idx = skill.indexOf('=');
				if (idx > 0)
				{
					final String ranks = skill.substring(idx + 1);
					skill = skill.substring(0, idx);
					if (availableList != null)
					{
						availableList.remove(skill);
					}
					if (selectedList != null)
					{
						selectedList.add(encodeSynergyEntry(skill, ranks, bonus));
					}
					return true;
				}
			}
		}
		return false;
	}

}
