/*
 * InfoClasses.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Created on Feb 16, 2002 11:15 AM
 */


/**
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.1 $
 */
package pcgen.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;

public class InfoClasses extends FilterAdapterPanel
{
	final private JLabel avaLabel = new JLabel("Available");
	final private JLabel selLabel = new JLabel("Selected");
	private JButton leftButton;
	private JButton rightButton;
	JButton adjXP = new JButton("Adj XP");
	JScrollPane cScroll = new JScrollPane();
	/*
	 * initializing the editor pane with default HTML tags;
	 * this fixes a bug which causes NPEs to be thrown on updateUI()
	 * with no HTML tags present
	 *
	 * author: Thomas Behr 13-03-03
	 */
	JEditorPane infoLabel = new JEditorPane("text/html", "<html></html>");
	JPanel center = new JPanel();
	Border etched;
	TitledBorder titled;
	JSplitPane splitPane;
	JSplitPane bsplit;
	JSplitPane asplit;
	protected ClassModel availableModel = null;  // Model for the JTreeTable.
	protected ClassModel selectedModel = null;   // Model for the JTreeTable.
	protected JTreeTable availableTable;  // the available Class
	protected JTreeTable selectedTable;   // the selected Class
	private PCClass lastClass = null; //keep track of which PCClass was last selected from either table
	private boolean needsUpdate = true;
	static protected PlayerCharacter aPC = null;
	private int origLocation = 0;
	static int viewMode = 0; // keep track of what view mode we're in for Available
	static int viewSelectMode = 0; // keep track of what view mode we're in for Selected. defaults to "Name"
	protected JComboBox viewComboBox = new JComboBox();
	protected JComboBox viewSelectComboBox = new JComboBox();
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;

	private boolean d_shown = false;

	private static final int COL_NAME = 0;
	private static final int COL_REQ_LEVEL = 1;
	private static final int COL_SRC = 2;

	// Right-click inventory item
	private int selRow;
	private TreePath selPath;
	private boolean hasBeenSized = false;

	private JPanel jPanel1 = new JPanel();
	private JPanel jPanel2 = new JPanel();
	private JLabel lblFort = new JLabel();
	private JLabel lblReflex = new JLabel();
	private JLabel lblWill = new JLabel();
	private JLabel lblFeats = new JLabel();
	private JLabel lblSkills = new JLabel();
	private JLabel lblBAB = new JLabel();
	private JLabel lReflex = new JLabel();
	private JLabel lWill = new JLabel();
	private JLabel featCount = new JLabel();
	private JLabel skillCount = new JLabel();
	private JLabel lBAB = new JLabel();
	private JLabel lblHp = new JLabel();
	private JLabel lFortitude = new JLabel();
	private JLabel lblExperience = new JLabel();
	private WholeNumberField experience = new WholeNumberField();
	private JButton jButtonHP = new JButton();
	private JLabel lblAlignment = new JLabel("Alignment:");
	private JComboBox alignment = new JComboBox();
	private JLabel lblWoundPoints = new JLabel();
	private JLabel lWP = new JLabel();
	private JLabel lblReputation = new JLabel();
	private JLabel lReputation = new JLabel();
	private JLabel lblDefense = new JLabel();
	private JLabel lDefense = new JLabel();
	/** Instantiated popup frame {@link HPFrame}. */
	HPFrame hpFrame = null;

	private JLabel lMaxHPPct = new JLabel();
	private WholeNumberField tMaxHPPct = new WholeNumberField();
	private JCheckBox levelCap = new JCheckBox("Ignore Level Cap");

	public static PObjectNode typeRoot;

	private class ClassPopupMenu extends JPopupMenu
	{
		private class ClassActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddClassActionListener extends ClassActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addClass(evt, 1);
			}
		}

		private class RemoveClassActionListener extends ClassActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addClass(evt, -1);
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddClassActionListener(), "add 1", (char)0, accelerator, "Add 1 level", "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveClassActionListener(), "remove 1", (char)0, accelerator, "Remove 1 level", "Remove16.gif", true);
		}

		ClassPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				/*
				 * jikes says:
				 *   "Ambiguous reference to member 'add' inherited from
				 *    type 'javax/swing/JPopupMenu' but also declared or
				 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
				 *    Explicit qualification is required."
				 * Well, let's do what jikes wants us to do ;-)
				 *
				 * author: Thomas Behr 08-02-02
				 *
				 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
				 * get "control PLUS" to function on standard US keyboard with Windows 98
				 */
				ClassPopupMenu.this.add(createAddMenuItem("Add  1", "control EQUALS"));
			}

			else // selectedTable
			{
				/*
				 * jikes says:
				 *   "Ambiguous reference to member 'add' inherited from
				 *    type 'javax/swing/JPopupMenu' but also declared or
				 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
				 *    Explicit qualification is required."
				 * Well, let's do what jikes wants us to do ;-)
				 *
				 * author: Thomas Behr 08-02-02
				 *
				 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
				 * get "control PLUS" to function on standard US keyboard with Windows 98
				 */
				ClassPopupMenu.this.add(createAddMenuItem("Add  1", "control EQUALS"));
				ClassPopupMenu.this.add(createRemoveMenuItem("Remove  1", "control MINUS"));
			}
		}
	}

	private class ClassPopupListener extends MouseAdapter
	{
		private JTree tree;
		private ClassPopupMenu menu;

		ClassPopupListener(JTreeTable treeTable, ClassPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;
			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();
					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final JMenuItem menuItem = (JMenuItem)menu.getComponent(i);
							javax.swing.KeyStroke ks = menuItem.getAccelerator();
							if ((ks != null) && keyStroke.equals(ks))
							{
								selPath = tree.getSelectionPath();
								menuItem.doClick(2);
								return;
							}
						}
					}
					dispatchEvent(e);
				}

				public void keyReleased(KeyEvent e)
				{
					dispatchEvent(e);
				}
			};
			treeTable.addKeyListener(myKeyListener);
		}

		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				selRow = tree.getRowForLocation(evt.getX(), evt.getY());
				if (selRow == -1) return;
				selPath = tree.getPathForLocation(evt.getX(), evt.getY());
				if (selPath == null) return;
				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}


	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new ClassPopupListener(treeTable, new ClassPopupMenu(treeTable)));
	}


	/* typeSubtypeRoot is the base structure used by both the available and selected tables; no
	 * need to generate this same list twice.
	 */

	public InfoClasses()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Classes");

		initComponents();
		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
	}

	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	//Set up GridBag Constraints
	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		typeRoot = new PObjectNode();
		ArrayList tList = new ArrayList();
		for (Iterator i = Globals.getClassList().iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass)i.next();
			ArrayList aList = aClass.typeList();
			for (Iterator ii = aList.iterator(); ii.hasNext();)
			{
				final String aString = (String)ii.next();
				if (!tList.contains(aString))
					tList.add(aString);
			}
		}
		Collections.sort(tList);
		if (!tList.contains("Other"))
			tList.add("Other");
		PObjectNode p[] = new PObjectNode[tList.size()];
		for (int i = 0; i < p.length; i++)
		{
			p[i] = new PObjectNode();
			p[i].setItem(tList.get(i).toString());
			p[i].setParent(typeRoot);
		}
		typeRoot.setChildren(p, false);
		viewComboBox.addItem("Name");
		viewComboBox.addItem("Type/Name");
		viewComboBox.setToolTipText("You can change how the Class in the Tables are listed.");
		viewComboBox.setSelectedIndex(0);       // must be done before createModels call
		viewSelectComboBox.addItem("Name");
		viewSelectComboBox.addItem("Type/Name");
		viewSelectComboBox.setToolTipText("You can change how the Class in the Tables are listed.");
		viewSelectComboBox.setSelectedIndex(0); // must be done before createModels call

		aPC = Globals.getCurrentPC();
		createModels();
// create available table of class
		createTreeTables();

		center.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		splitPane = new JSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		center.add(splitPane, BorderLayout.CENTER);

		buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(avaLabel);
		aPanel.add(viewComboBox);
		ImageIcon newImage;
		newImage = new ImageIcon(getClass().getResource("resource/Forward16.gif"));
		rightButton = new JButton(newImage);
		rightButton.setToolTipText("Click to add level from the selected Available class");
		rightButton.setEnabled(false);
		aPanel.add(rightButton);
		leftPane.add(aPanel);
		newImage = new ImageIcon(getClass().getResource("resource/Refresh16.gif"));
		JButton sButton = new JButton(newImage);
		sButton.setToolTipText("Click to change orientation of tables");
		sButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (splitOrientation == JSplitPane.VERTICAL_SPLIT)
					splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
				else
					splitOrientation = JSplitPane.VERTICAL_SPLIT;
				splitPane.setOrientation(splitOrientation);
				splitPane.setDividerLocation(.5);
			}
		});
		aPanel.add(sButton);

		selectedTable.getColumnModel().getColumn(1).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
                selectedTable.getColumnModel().getColumn(1).setPreferredWidth(15);

		buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);


		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		leftButton = new JButton(newImage);
		leftButton.setToolTipText("Click to remove level from the Selected class");
		leftButton.setEnabled(false);
		aPanel.add(leftButton);
		rightPane.add(aPanel);

		buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		rightPane.add(scrollPane);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Class Info");
		title1.setTitleJustification(TitledBorder.CENTER);
		cScroll.setBorder(title1);
		infoLabel.setBackground(rightPane.getBackground());
		cScroll.setViewportView(infoLabel);
		cScroll.setToolTipText("Any requirements you don't meet are in italics.");

		jPanel1.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();


		jButtonHP.setText("HP");
		jButtonHP.setAlignmentY(0.0F);
		jButtonHP.setHorizontalAlignment(SwingConstants.LEFT);

		buildConstraints(gridBagConstraints2, GridBagConstraints.RELATIVE, 0, 1, 1, 3, 10);
		gridBagConstraints2.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jButtonHP, gridBagConstraints2);


		lblFort.setText("Fort");
		lblFort.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 2, 1, 1, 1, 3, 10);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblFort, gridBagConstraints2);

		lblReflex.setText("Reflex");
		lblReflex.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 2, 2, 1, 1, 0, 10);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblReflex, gridBagConstraints2);

		lblWill.setText("Will");
		lblWill.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 2, 3, 1, 1, 0, 10);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblWill, gridBagConstraints2);

		lblReputation.setText("Reputation");
		lblReputation.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 2, 4, 1, 1, 0, 0);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblReputation, gridBagConstraints2);

		lblFeats.setText("Feats");
		lblFeats.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 0, 1, 1, 1, 0, 10);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblFeats, gridBagConstraints2);

		lblSkills.setText("Skills");
		lblSkills.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 0, 2, 1, 1, 0, 10);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblSkills, gridBagConstraints2);

		lblBAB.setText("BAB");
		lblBAB.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 0, 3, 1, 1, 0, 10);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblBAB, gridBagConstraints2);

		lblDefense.setText("Defense");
		lblDefense.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 0, 4, 1, 1, 0, 10);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblDefense, gridBagConstraints2);

		lblWoundPoints.setText("Wound Points");
		lblWoundPoints.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 2, 5, 1, 1, 0, 10);
		gridBagConstraints2.insets = new Insets(0, 10, 0, 0);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblWoundPoints, gridBagConstraints2);
		jPanel1.add(lblAlignment, gridBagConstraints2);

		lReflex.setText("+1");
		lReflex.setForeground(Color.black);
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		buildConstraints(gridBagConstraints2, 3, 2, 1, 1, 0, 0);
		gridBagConstraints2.insets = new Insets(0, 4, 0, 4);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lReflex, gridBagConstraints2);

		lWill.setText("+2");
		lWill.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 3, 3, 1, 1, 0, 0);
		gridBagConstraints2.insets = new Insets(0, 4, 0, 4);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lWill, gridBagConstraints2);

		lFortitude.setText("+0");
		lFortitude.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 3, 1, 1, 1, 2, 0);
		gridBagConstraints2.insets = new Insets(0, 4, 0, 4);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lFortitude, gridBagConstraints2);

		featCount.setText("2");
		featCount.setForeground(Color.black);
		featCount.setHorizontalAlignment(SwingConstants.TRAILING);
		featCount.setPreferredSize(new Dimension(33, 15));
		featCount.setHorizontalTextPosition(SwingConstants.CENTER);
		buildConstraints(gridBagConstraints2, 1, 1, 1, 1, 0, 0);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
		jPanel1.add(featCount, gridBagConstraints2);

		skillCount.setText("8");
		skillCount.setForeground(Color.black);
		skillCount.setHorizontalAlignment(SwingConstants.TRAILING);
		buildConstraints(gridBagConstraints2, 1, 2, 1, 1, 0, 0);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(skillCount, gridBagConstraints2);

		lBAB.setText("+2");
		lBAB.setForeground(Color.black);
		lBAB.setHorizontalAlignment(SwingConstants.TRAILING);
		buildConstraints(gridBagConstraints2, 1, 3, 1, 1, 0, 0);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lBAB, gridBagConstraints2);

		lDefense.setText("4");
		lDefense.setForeground(Color.black);
		lDefense.setHorizontalAlignment(SwingConstants.TRAILING);
		buildConstraints(gridBagConstraints2, 1, 4, 1, 1, 0, 0);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lDefense, gridBagConstraints2);

		lWP.setText("14");
		lWP.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 3, 5, 1, 1, 0, 0);
		gridBagConstraints2.insets = new Insets(0, 4, 0, 4);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lWP, gridBagConstraints2);
		jPanel1.add(alignment, gridBagConstraints2);
		alignment.setModel(new DefaultComboBoxModel(Constants.s_ALIGNLONG));
		alignment.setToolTipText("You must select an alignment.");

		final int align = aPC.getAlignment();
		if (align == Constants.ALIGNMENT_NONE)
			alignment.setForeground(Color.red);
		else
			alignment.setForeground(Color.black);
		if (align > -1)
		{
			alignment.setSelectedIndex(align);
		}
		this.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				updateCharacterInfo();
			}
		});


		lblHp.setText("");
		lblHp.setForeground(Color.black);
		lblHp.setHorizontalAlignment(SwingConstants.TRAILING);
		buildConstraints(gridBagConstraints2, 1, 0, 1, 1, 2, 0);
		gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblHp, gridBagConstraints2);

		lReputation.setText("0");
		lReputation.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 3, 4, 1, 1, 2, 0);
		gridBagConstraints2.insets = new Insets(0, 4, 0, 4);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lReputation, gridBagConstraints2);

		lMaxHPPct.setText("Max HP %");
		lMaxHPPct.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 2, 0, 1, 1, 3, 10);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lMaxHPPct, gridBagConstraints2);

		tMaxHPPct.setHorizontalAlignment(JTextField.TRAILING);
		tMaxHPPct.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				tMaxHPPctFocusLost(evt);
			}
		});

		buildConstraints(gridBagConstraints2, 3, 0, 1, 1, 2, 0);
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(tMaxHPPct, gridBagConstraints2);

		buildConstraints(gridBagConstraints2, 0, 5, 2, 1, 0, 0);
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(levelCap, gridBagConstraints2);
		levelCap.setSelected(Globals.isIgnoreLevelCap());
		levelCap.setToolTipText("Don't stop at level 20");

		lblExperience.setText("Experience");
		buildConstraints(gridBagConstraints2, 0, 6, 1, 1, 0, 0);
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.insets = new Insets(0, 0, 0, 5);
		jPanel1.add(lblExperience, gridBagConstraints2);

		experience.addFocusListener(new FocusAdapter()
		{
			private boolean isProcessing = false;

			public void focusLost(FocusEvent evt)
			{
				//
				// for some reason this gets processed twice, want to ignore the second (and subsequent)
				//
				if (!isProcessing)
				{
					isProcessing = true;
					experienceFocusLost(evt);
					isProcessing = false;
				}
			}
		});

		buildConstraints(gridBagConstraints2, 1, 6, 1, 1, 0, 0);
		gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
		jPanel1.add(experience, gridBagConstraints2);

		buildConstraints(gridBagConstraints2, 2, 6, 1, 1, 0, 0);
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(adjXP, gridBagConstraints2);

		asplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cScroll, jPanel1);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);
	}

	void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(false);
				aPC.setAutomaticFeatsStable(false);
				aPC.setVirtualFeatsStable(false);
				formComponentShown(evt);
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				bsplit.setDividerLocation((int)(InfoClasses.this.getSize().getHeight() - 140));
				asplit.setDividerLocation((int)(InfoClasses.this.getSize().getWidth() - 334));
			}
		});
		leftButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addClass(evt, -1);
			}
		});
		adjXP.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				String selectedValue = JOptionPane.showInputDialog(null, "Enter XP Adjustment", Constants.s_APPNAME, JOptionPane.QUESTION_MESSAGE);
				if (selectedValue != null)
				{
					try
					{
						int x = Integer.parseInt(selectedValue)+aPC.getExperience().intValue();
						Integer xp = new Integer(x);
						aPC.setExperience(xp);
						experience.setText(xp.toString());
						experienceFocusLost(null); // force xp messages as necessary
					}
					catch (Exception e)
					{
						JOptionPane.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		});
		rightButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addClass(evt, 1);
			}
		});
		viewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewComboBoxActionPerformed(evt);
			}
		});
		viewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewSelectComboBoxActionPerformed(evt);
			}
		});
		levelCap.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Globals.setIgnoreLevelCap(levelCap.isSelected());
			}
		});

		alignment.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				alignmentChanged();
			}
		});

		jButtonHP.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (hpFrame == null)
					{
						hpFrame = new HPFrame();
					}
					hpFrame.setPSize();
					hpFrame.pack();
					hpFrame.setVisible(true);
				}
			});
	}

	private void alignmentChanged()
	{
		alignment.setForeground(Color.black);
		final int newAlignment = alignment.getSelectedIndex();
		final int oldAlignment = aPC.getAlignment();
		if (newAlignment == oldAlignment)
			return;


		//
		// Get a list of classes that will become unqualified
		//
		StringBuffer unqualified = new StringBuffer();
		ArrayList classList = aPC.getClassList();
		ArrayList exclassList = new ArrayList();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();

			aPC.setAlignment(oldAlignment, false, true);
			if (aClass.isQualified())
			{
				aPC.setAlignment(newAlignment, false, true);
				if (!aClass.isQualified())
				{
					if (unqualified.length() > 0)
						unqualified.append(", ");
					unqualified.append(aClass.getName());
					exclassList.add(aClass);
				}
			}
		}

		//
		// Give the user a chance to bail
		//
		if (unqualified.length() > 0)
		{
			if (JOptionPane.showConfirmDialog(null, "This will change the following class(es) to ex-class(es):\n" + unqualified.toString(), "PCGen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION)
			{
				aPC.setAlignment(oldAlignment, false, true);
				alignment.setSelectedIndex(oldAlignment);
				return;
			}
		}

		//
		// Convert the class(es)
		//
		for (Iterator e = exclassList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			aPC.makeIntoExClass(aClass);
		}
		aPC.setAlignment(newAlignment, false, true);
		needsUpdate = true;
		updateCharacterInfo();

	}

	private void tMaxHPPctFocusLost(FocusEvent evt)
	{
		Globals.setHpPct(tMaxHPPct.getValue());
	}

	private void experienceFocusLost(FocusEvent evt)
	{
		if (aPC.totalLevels() == 0)
			return;
		aPC.setDirty(true);
		PCClass aClass = (PCClass)aPC.getClassList().get(0);
		Integer min = aClass.minExpForLevel(aPC.totalLevels() + aPC.levelAdjustment());
		Integer anInt = new Integer(experience.getText());
		if (anInt.intValue() < min.intValue())
		{
			JOptionPane.showMessageDialog(null, "To be your level (" + new Integer(aPC.totalLevels()).toString() +
				") you must have at least " + min + " experience", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			experience.setText(min.toString());
			aPC.setExperience(min);
			return;
		}
		min = aClass.minExpForLevel(aPC.totalLevels() + 1 + aPC.levelAdjustment());
		aPC.setExperience(anInt);
		if (anInt.intValue() >= min.intValue())
		{
			JOptionPane.showMessageDialog(null, "You can advance a level with that much experience!", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void buttonHPActionListener(ActionEvent evt)
	{
	}

	private void viewComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			createAvailableModel();
			availableTable.updateUI();
		}
	}

	private void viewSelectComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewSelectComboBox.getSelectedIndex();
		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			createSelectedModel();
			selectedTable.updateUI();
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		//requestDefaultFocus();
		requestFocus();
		PCGen_Frame1.getStatusBar().setText("Classes the character does not qualify for are in Red.");
		updateCharacterInfo();
		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = Globals.getPCGenOption("InfoClasses.splitPane", (int)(this.getSize().getWidth()*7/10));
			t = Globals.getPCGenOption("InfoClasses.bsplit", (int)(this.getSize().getHeight() - 140));
			u = Globals.getPCGenOption("InfoClasses.asplit", (int)(this.getSize().getWidth() - 334));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("ClassSel", i);
				if(width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "ClassSel", i));
			}
			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("ClassAva", i);
				if(width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "ClassAva", i));
			}
		}

		if (s>0)
		{
			splitPane.setDividerLocation(s);
			Globals.setPCGenOption("InfoClasses.splitPane", s);
		}
		if (t>0)
		{
			bsplit.setDividerLocation(t);
			Globals.setPCGenOption("InfoClasses.bsplit", t);
		}
		if (u>0)
		{
			asplit.setDividerLocation(u);
			Globals.setPCGenOption("InfoClasses.asplit", u);
		}
	}

	// This recalculates the states of everything based upon the currently selected
	// character.
	public void updateCharacterInfo()
	{
		if (Globals.isStarWarsMode())
		{
			jButtonHP.setText("VP");
			lMaxHPPct.setText("Max VP %");
			lblDefense.setText("Defense");
			lblWoundPoints.setText("Wound Points");
			lblAlignment.setVisible(false);
			alignment.setVisible(false);
			lblDefense.setVisible(true);
			lDefense.setVisible(true);
			lblWoundPoints.setVisible(true);
			lWP.setVisible(true);
			lblReputation.setVisible(true);
			lReputation.setVisible(true);
		}
		else if (Globals.isSidewinderMode())
		{
			jButtonHP.setText("Vigor");
			lMaxHPPct.setText("Max Vigor %");
			lblDefense.setVisible(true);
			lblDefense.setText("AC");
			lDefense.setVisible(true);
			lblWoundPoints.setVisible(true);
			lblWoundPoints.setText("Body Points");
			lWP.setVisible(true);
			lblReputation.setVisible(false);
			lReputation.setVisible(false);
		}
		else if (Globals.isWheelMode())
		{
			jButtonHP.setText("HP");
			lMaxHPPct.setText("Max HP %");
			lblDefense.setVisible(true);
			lblDefense.setText("Defense");
			lDefense.setVisible(true);
			lblWoundPoints.setVisible(false);
			lWP.setVisible(false);
			lblReputation.setVisible(false);
			lReputation.setVisible(false);
		}
		else
		{
			jButtonHP.setText("HP");
			lMaxHPPct.setText("Max HP %");
			lblAlignment.setVisible(!Globals.isSSd20Mode());
			alignment.setVisible(!Globals.isSSd20Mode());
			lblDefense.setVisible(false);
			lDefense.setVisible(false);
			lblWoundPoints.setVisible(false);
			lWP.setVisible(false);
			lblReputation.setVisible(false);
			lReputation.setVisible(false);
		}
		final PObjectNode a = new PObjectNode();
		a.resetPC();
		aPC = Globals.getCurrentPC();
		if (aPC == null || needsUpdate == false)
			return;
		aPC.setAggregateFeatsStable(false);
		aPC.setAutomaticFeatsStable(false);
		aPC.setVirtualFeatsStable(false);
		createModels();
		availableTable.updateUI();
		selectedTable.updateUI();
		//Calculate the aggregate feat list
		aPC.aggregateFeatList();
		if (aPC != null)
		{
			updateHp();
			featCount.setText(Integer.toString(aPC.getFeats()));
			skillCount.setText(Integer.toString(aPC.getSkillPoints()));
			lBAB.setText(Integer.toString(aPC.baseAttackBonus()));
			lDefense.setText(aPC.defense().toString());
			lFortitude.setText(Integer.toString(aPC.getBonus(1, true)));
			lReflex.setText(Integer.toString(aPC.getBonus(2, true)));
			lWill.setText(Integer.toString(aPC.getBonus(3, true)));
			lWP.setText(aPC.woundPoints().toString());
			experience.setValue(aPC.getExperience().intValue());
			lReputation.setText(aPC.reputation().toString());
		}
		needsUpdate = false;
	}

	private void setInfoLabelText(PCClass aClass)
	{
		lastClass = aClass; //even if that's null
		if (aClass != null)
		{
			StringBuffer b = new StringBuffer();
			b.append("<html><b>").append(aClass.getName()).append("</b>");
			b.append(" &nbsp;<b>TYPE</b>:").append(aClass.getType());
			final String cString = aClass.preReqHTMLStrings(false);
			if (cString.length() > 0)
				b.append(" &nbsp;<b>Requirements</b>:").append(cString);
			String bString = aClass.getSource();
			if (bString.length() > 0)
				b.append(" <b>SOURCE</b>:").append(bString);
			b.append(" <b>BAB:</b>").append(aClass.getAttackBonusType());
			b.append(" <b>HD:</b>1D").append(aClass.getHitDie());
			b.append(" <b>Fortitude:</b>").append(aClass.getFortitudeCheckType());
			b.append(" <b>Reflex:</b>").append(aClass.getReflexCheckType());
			b.append(" <b>Willpower:</b>").append(aClass.getWillCheckType());
			//b.append(" <b>Pre-Requisites:</b>").append(aClass.preReqHTMLStrings(false));
			if (Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isWheelMode())
			{
				b.append(" <b>Defense:</b>").append(aClass.defenseString());
				b.append(" <b>Reputation:</b>").append(aClass.getReputationString());
			}
			else
			{
				b.append(" <b>SPELLTYPE:</b>").append(aClass.getSpellType());
				b.append(" <b>Base Stat:</b>").append(aClass.getSpellBaseStat());
			}

			b.append("</html>");
			infoLabel.setText(b.toString());
			infoLabel.setCaretPosition(0);
		}
	}

	private int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel)e.getSource();
		if (model == null)
		{
			return -1;
		}
		return model.getMinSelectionIndex();
	}

	protected void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					String aString = null;
					/////////////////////////
					// Byngl Feb 20/2002
					// fix bug with displaying incorrect class when use cursor keys to navigate the tree
					//
					//final Object temp = availableTable.getTree().getLastSelectedPathComponent();
					final int idx = getSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					final Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp != null)
					{
						aString = temp.toString();
					}
					else
					{
						lastClass = null;
						JOptionPane.showMessageDialog(null,
							"No class selected. Try again.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						return;
					}

					final PCClass aClass = Globals.getClassNamed(aString.substring(aString.lastIndexOf("|") + 1));
					rightButton.setEnabled(aClass != null);
					setInfoLabelText(aClass);
				}
			}
		});
		final JTree tree = availableTable.getTree();

		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new LabelTreeCellRenderer());


		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = tree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && selPath != null)
					{
						tree.setSelectionPath(selPath);
					}
					else if (e.getClickCount() == 2)
					{
						addClass(null, 1);
					}
				}
			}
		};
		tree.addMouseListener(ml);

		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		final JTree btree = selectedTable.getTree();
		btree.setRootVisible(false);
		btree.setShowsRootHandles(true);
		btree.setCellRenderer(new LabelTreeCellRenderer());
		selectedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					String aString = null;
					/////////////////////////
					// Byngl Feb 20/2002
					// fix bug with displaying incorrect class when use cursor keys to navigate the tree
					//
					//final Object temp = selectedTable.getTree().getLastSelectedPathComponent();
					final int idx = getSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					final Object temp = selectedTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp != null)
					{
						aString = temp.toString();
					}
					else
					{
						lastClass = null;
						/*
						 * this fixes a bug which causes NPEs to be thrown on updateUI()
						 * with no HTML tags present
						 *
						 * author: Thomas Behr 13-03-03
						 */
						infoLabel.setText(Globals.html_NONESELECTED);
						infoLabel.setCaretPosition(0);
						return;
					}

					final PCClass aClass = aPC.getClassNamed(aString.substring(aString.lastIndexOf("|") + 1));
					leftButton.setEnabled(aClass != null);
					setInfoLabelText(aClass);
				}
			}
		});
		ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = btree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = btree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1)
						btree.setSelectionPath(selPath);
					else if (e.getClickCount() == 2)
					{
						addClass(null, -1);
					}
				}
			}
		};
		btree.addMouseListener(ml);

		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private PCClass getSelectedClass()
	{
		if (lastClass == null)
		{
			JOptionPane.showMessageDialog(null,
				"No class selected. Try again.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		return lastClass;
	}

	private void addClass(ActionEvent evt, int levels)
	{
		if ((levels > 0) && (aPC.getAlignment() == Constants.ALIGNMENT_NONE) && Globals.isDndMode())
		{
			JOptionPane.showMessageDialog(null,
				"You must select an Alignment before adding classes.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		aPC.setDirty(true);

		//InfoFeats.needsUpdate = true;
		//InfoDomain.needsUpdate = true;
		//InfoSkills.needsUpdate = true; //more skill points (and maybe classes) have been added
		//InfoSpells.needsUpdate = true; // could have added a spell casting class
		final PCGen_Frame1 rootFrame = (PCGen_Frame1)Globals.getRootFrame();
		rootFrame.forceUpdate_InfoFeats();
		rootFrame.forceUpdate_InfoDomain();
		rootFrame.forceUpdate_InfoSkills();
		rootFrame.forceUpdate_InfoSpells();

		PCClass theClass = getSelectedClass();
		if ((theClass == null) || !theClass.isQualified())
		{
			return;
		}

		final PCClass aClass = aPC.getClassNamed(theClass.getName());
		if (levels < 0 || aClass == null || Globals.isIgnoreLevelCap() || (!Globals.isIgnoreLevelCap() && aClass.getLevel().intValue() < aClass.getMaxLevel()))
		{
			//InfoInventory.needsUpdate = true;
			rootFrame.forceUpdate_InfoInventory();
			aPC.incrementClassLevel(levels, theClass);
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Maximum level reached.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
		// Remember which rows are expanded
		for (int i = 0; i < s.length; i++)
		{
			s[i] = selectedTable.getTree().isExpanded(i);
		}
		boolean a[] = new boolean[availableTable.getTree().getRowCount()];
		// Remember which rows are expanded
		for (int i = 0; i < a.length; i++)
		{
			a[i] = availableTable.getTree().isExpanded(i);
		}


		needsUpdate = true;
		updateCharacterInfo();
		//re-expand the rows (they were cleared out in the createSelectedModel() call)
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
			{
				selectedTable.getTree().expandRow(i);
			}
		}
		for (int i = 0; i < a.length; i++)
		{
			if (a[i])
			{
				availableTable.getTree().expandRow(i);
			}
		}
	}

	/**
	 * Creates the ClassModel that will be used.
	 */
	protected void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	protected void createAvailableModel()
	{
		if (availableModel == null)
			availableModel = new ClassModel(viewMode, true);
		else
			availableModel.resetModel(viewMode, true, false);
	}

	protected void createSelectedModel()
	{
		if (selectedModel == null)
			selectedModel = new ClassModel(viewSelectMode, false);
		else
			selectedModel.resetModel(viewSelectMode, false, false);
	}


	/** The basic idea of the TreeTableModel is that there is a single <code>root</code>
	 *  object.  This root object has a null <code>parent</code>.  All other objects
	 *  have a parent which points to a non-null object.  parent objects contain a list of
	 *  <code>children</code>, which are all the objects that point to it as their parent.
	 *  objects (or <code>nodes</code>) which have 0 children are leafs (the end of that
	 *  linked list).  nodes which have at least 1 child are not leafs. Leafs are like files
	 *  and non-leafs are like directories.
	 */
	public class ClassModel extends AbstractTreeTableModel implements TreeTableModel
	{
		// Types of the columns.
		protected int modelType = 0; // availableModel=0,selectedModel=1

		/**
		 * Creates a ClassModel
		 */
		public ClassModel(int mode, boolean available)
		{
			super(null);
			if (!available)
				modelType = 1;
			resetModel(mode, available, true);
		}

		/**
		 * This assumes the ClassModel exists but needs to be repopulated
		 */
		public void resetModel(int mode, boolean available, boolean newCall)
		{
			Iterator fI;
			if (available == true)
				fI = Globals.getClassList().iterator();
			else
				fI = aPC.getClassList().iterator();
			switch (mode)
			{
				case 0: // Name
					setRoot(new PObjectNode()); // just need a blank one
					for (; fI.hasNext();)
					{
						final PCClass aClass = (PCClass)fI.next();
						// in the availableTable, if filtering out unqualified items
						// ignore any class the PC doesn't qualify for
						if (!shouldDisplayThis(aClass))
							continue;
						PObjectNode aFN = new PObjectNode();
						aFN.setParent((PObjectNode)root);
						aFN.setItem(aClass);
						aFN.setIsValid(aClass.passesPreReqTests());
						((PObjectNode)root).addChild(aFN);
					}
					break;
				case 1: // type/name
					setRoot((PObjectNode)InfoClasses.typeRoot.clone());
					for (; fI.hasNext();)
					{
						final PCClass aClass = (PCClass)fI.next();
						// in the availableTable, if filtering out unqualified items
						// ignore any class the PC doesn't qualify for
						if (!shouldDisplayThis(aClass))
							continue;
						PObjectNode c[] = ((PObjectNode)root).getChildren();
						boolean added = false;
						for (int i = 0; i < c.length; i++)
						{
							if ((added == false && i == c.length - 1) ||
								aClass.isType(c[i].getItem().toString()))
							{
								PObjectNode aFN = new PObjectNode();
								aFN.setParent(c[i]);
								aFN.setItem(aClass);
								aFN.setIsValid(aClass.passesPreReqTests());
								c[i].addChild(aFN);
								added = true;
							}
						}
					}
					break;
			}
			if (!newCall && ((PObjectNode)root).getChildren().length > 0)
				fireTreeNodesChanged(root, ((PObjectNode)root).getChildren(), null, null);
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 */
		private boolean shouldDisplayThis(final PCClass aClass)
		{
			return (modelType == 1 || (aClass.isVisible() && accept(aClass)));
		}

		// "There can be only one!" There must be a root object, though it can be hidden
		// to make it's existence basically a convenient way to keep track of the objects
		public void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}


		public void removeItemFromNodes(PObjectNode p, Object e)
		{
			if (p == null)
				p = (PObjectNode)root;

			PObjectNode c[] = p.getChildren();
			// if no children, remove it and update parent
			if (c.length == 0 && p.getItem().equals(e))
			{
				p.getParent().removeChild(p);
			}
			else
			{
				for (int i = 0; i < c.length; i++)
				{
					removeItemFromNodes(c[i], e);
				}
			}
		}

		/**
		 * Returns int number of children for <code>node</code>.
		 */
		public int getChildCount(Object node)
		{
			Object[] children = getChildren(node);
			return (children == null) ? 0 : children.length;
		}

		/**
		 * Returns Object child for <code>node</code> at index <code>i</code>.
		 */
		public Object getChild(Object node, int i)
		{
			return getChildren(node)[i];
		}

		/**
		 * Returns true if <code>node</node> is a leaf, otherwise false.
		 */
		public boolean isLeaf(Object node)
		{
			return ((PObjectNode)node).isLeaf();
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 */
		public int getColumnCount()
		{
			return 3;
		}

		/**
		 * Returns String name of a column.
		 */
		public String getColumnName(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return "Name";
				case COL_REQ_LEVEL:
					if (modelType == 0)
						return "Pre-Reqs";
					return "Level";
				case COL_SRC:
					return "Source";
			}
			return "";
		}

		/**
		 * Returns Class for the column.
		 */
		public Class getColumnClass(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return TreeTableModel.class;
				case COL_REQ_LEVEL:
					if (modelType == 0)
						return String.class;
					return Integer.class;
				case COL_SRC:
					return String.class;
			}
			return String.class;
		}

		public boolean isCellEditable(Object node, int column)
		{
			return (column == COL_NAME);
		}

		/**
		 * Returns Object value of the column.
		 */
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode)node;
			PCClass aClass = null;
			if (fn != null && (fn.getItem() instanceof PCClass))
				aClass = (PCClass)fn.getItem();

			final Integer c = new Integer(0);
			switch (column)
			{
				case COL_NAME: // Name
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Globals.debugErrorPrint("Somehow we have no active node when doing getValueAt in InfoInventory.");
						return "";
					}
				case COL_REQ_LEVEL: // Cost
					if (modelType == 0)
					{
						if (aClass != null)
							return aClass.preReqHTMLStrings();
						return "";
					}
					if (aClass != null)
						return aClass.getLevel();
					return c;
				case COL_SRC: // Source or Qty
					if (fn != null)
						return fn.getSource();
					return "";
				case -1:
					if (fn != null)
					{
						return fn.getItem();
					}
					else
					{
						Globals.debugErrorPrint("Somehow we have no active node when doing getValueAt in InfoInventory.");
						return null;
					}

			}
			return null;
		}

		protected Object[] getChildren(Object node)
		{
			PObjectNode featNode = ((PObjectNode)node);
			return featNode.getChildren();
		}

		public void addChild(Object aChild, Object aParent)
		{
			PObjectNode aFN = new PObjectNode();
			aFN.setItem(aChild);
			aFN.setParent((PObjectNode)aParent);
			((PObjectNode)aParent).addChild(aFN);
		}
	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllClassFilters(this);
		FilterFactory.registerAllPrereqAlignmentFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		needsUpdate = true;
		updateCharacterInfo();
	}

	/**
	 * specifies wheter the "match any" option should be available
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies wheter the "negate/reverse" option should be available
	 */
	public boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 */
	public int getSelectionMode()
	{
		return MULTI_MULTI_MODE;
	}

	public void updateHp()
	{
		final PlayerCharacter thisPC = Globals.getCurrentPC();
		if (thisPC != null)
		{
			lblHp.setText(new Integer(thisPC.hitPoints()).toString());
		}
	}

}
