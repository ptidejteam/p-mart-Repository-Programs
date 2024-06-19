/*
 * SourceBasePanel
 * Copyright 2003 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com >
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
 * Created on January 8, 2003, 8:15 PM
 *
 * @(#) $Id: SourceBasePanel.java,v 1.1 2006/02/21 01:18:42 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.WholeNumberField;

/**
 * <code>SourceBasePanel</code>
 *
 * @author  Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 */

class SourceBasePanel extends BasePanel
{
	static final long serialVersionUID = -8057486950329356072L;
	private JTextField game;
	private JTextField infoText;
	private WholeNumberField rank;
	private JTextField pubNameLong;
	private JTextField pubNameShort;
	private JTextField pubNameWeb;
	private JTextField setting;
	private JTextField destination;
	private JTextField genre;
	private JComboBoxEx bookType;
	private JCheckBox isOGL;
	private JCheckBox isD20;
	private JCheckBox showInMenu;
	private JCheckBox isLicensed;
	private JScrollPane scrollPane;
	private JTableEx sourceTable;
	private SourceTableModel sourceModel;
	private Campaign theCampaign = null;

	/** Creates new form SourceBasePanel */

	public SourceBasePanel()
	{
		initComponents();
		intComponentContents();
	}

	private void intComponentContents()
	{
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		JLabel tempLabel;

		scrollPane = new JScrollPane();
		sourceTable = new JTableEx();
		sourceModel = new SourceTableModel();

		game = new JTextField();
		infoText = new JTextField();
		pubNameWeb = new JTextField();
		pubNameShort = new JTextField();
		pubNameLong = new JTextField();
		setting = new JTextField();
		destination = new JTextField();
		genre = new JTextField();

		isOGL = new JCheckBox();
		isD20 = new JCheckBox();
		showInMenu = new JCheckBox();
		isLicensed = new JCheckBox();
		rank = new WholeNumberField(5, 3);
		bookType = new JComboBoxEx();
		bookType.setEditable(true);
		bookType.addItem("Campaign Setting");
		bookType.addItem("Core Rulebook ");
		bookType.addItem("Magazine");
		bookType.addItem("Module");
		bookType.addItem("Sourcebook");
		bookType.addItem("Supplement");
		bookType.addItem("Web Enhancement");
		bookType.setSelectedIndex(0);

		setLayout(new BorderLayout());

		tempLabel = new JLabel("Game Mode:");
		JPanel aPanel = new JPanel();
		aPanel.setLayout(new GridBagLayout());
		GridBagConstraints gb = new GridBagConstraints();
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.anchor = GridBagConstraints.WEST;
		gb = buildConstraints(gb, 0, 0, true);
		gb.weightx = .1;
		aPanel.add(tempLabel, gb);
		gb = buildConstraints(gb, 1, 0, true);
		gb.weightx = .4;
		aPanel.add(game, gb);
		gb = buildConstraints(gb, 2, 0, true);
		gb.weightx = .1;
		tempLabel = new JLabel("Info Text:");
		aPanel.add(tempLabel, gb);
		gb = buildConstraints(gb, 3, 0, true);
		gb.weightx = .4;
		aPanel.add(infoText, gb);
		gb.weightx = 0;

		tempLabel = new JLabel("Pub Name Short:");
		gb = buildConstraints(gb, 0, 1, true);
		aPanel.add(tempLabel, gb);
		gb = buildConstraints(gb, 1, 1, true);
		aPanel.add(pubNameShort, gb);

		JPanel bPanel = new JPanel();
		tempLabel = new JLabel("OGL:");
		bPanel.add(tempLabel);
		bPanel.add(isOGL);
		tempLabel = new JLabel("D20:");
		bPanel.add(tempLabel);
		bPanel.add(isD20);
		gb = buildConstraints(gb, 2, 1, true);
		aPanel.add(bPanel, gb);

		bPanel = new JPanel();
		bPanel.setLayout(new BorderLayout());
		tempLabel = new JLabel("Setting:");
		bPanel.add(tempLabel, BorderLayout.WEST);
		bPanel.add(setting, BorderLayout.CENTER);
		gb = buildConstraints(gb, 3, 1, true);
		aPanel.add(bPanel, gb);

		tempLabel = new JLabel("Pub Name Long:");
		gb = buildConstraints(gb, 0, 2, true);
		aPanel.add(tempLabel, gb);
		gb = buildConstraints(gb, 1, 2, true);
		aPanel.add(pubNameLong, gb);

		tempLabel = new JLabel("In Menu:");
		gb = buildConstraints(gb, 2, 2, true);
		bPanel = new JPanel();
		bPanel.add(tempLabel);
		bPanel.add(showInMenu);
		tempLabel = new JLabel("Licensed:");
		bPanel.add(tempLabel);
		bPanel.add(isLicensed);
		aPanel.add(bPanel, gb);

		bPanel = new JPanel();
		bPanel.setLayout(new BorderLayout());
		tempLabel = new JLabel("Genre:");
		bPanel.add(tempLabel, BorderLayout.WEST);
		bPanel.add(genre, BorderLayout.CENTER);
		gb = buildConstraints(gb, 3, 2, true);
		aPanel.add(bPanel, gb);

		tempLabel = new JLabel("Pub Name Web:");
		gb = buildConstraints(gb, 0, 3, true);
		aPanel.add(tempLabel, gb);
		gb = buildConstraints(gb, 1, 3, true);
		aPanel.add(pubNameWeb, gb);

		bPanel = new JPanel();
		tempLabel = new JLabel("Rank:");
		bPanel.add(tempLabel);
		bPanel.add(rank);
		gb = buildConstraints(gb, 2, 3, true);
		aPanel.add(bPanel, gb);

		tempLabel = new JLabel("Book Type");
		gb = buildConstraints(gb, 3, 3, true);
		bPanel = new JPanel();
		bPanel.add(tempLabel);
		bPanel.add(bookType);
		aPanel.add(bPanel, gb);

		tempLabel = new JLabel("Destination of File:");
		gb = buildConstraints(gb, 0, 4, true);
		aPanel.add(tempLabel, gb);
		gb = buildConstraints(gb, 1, 4, true);
		gb.gridwidth = 2;
		aPanel.add(destination, gb);
		JButton bButton = new JButton("Browse...");
		bButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				String loc = "";
				String d = null;
				if (theCampaign != null)
				{
					d = theCampaign.getDestination();
				}
				if (d == null || d.equals(""))
				{
					d = SettingsHandler.getPccFilesLocation().toString();
				}
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(d));
				int returnVal = fc.showOpenDialog(SourceBasePanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					loc = fc.getSelectedFile().toString();
					if (loc.startsWith(SettingsHandler.getPccFilesLocation().toString()))
					{
						loc = loc.substring(SettingsHandler.getPccFilesLocation().toString().length() + 1);
					}
					else if (loc.startsWith(theCampaign.getSourceFile()))
					{
						loc = loc.substring(theCampaign.getSourceFile().length() + 1);
					}
				}
				destination.setText(loc);
			}
		});
		gb = buildConstraints(gb, 3, 4, true);
		gb.gridwidth = 1;
		aPanel.add(bButton, gb);

		add(aPanel, BorderLayout.NORTH);

		sourceTable.setModel(sourceModel);
		sourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceTable.setDoubleBuffered(false);
		scrollPane.setViewportView(sourceTable);
		add(scrollPane, BorderLayout.CENTER);

		aPanel = new JPanel();
		JButton aButton = new JButton("OPTION");
		aButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addOption();
				sourceTable.updateUI();
			}
		});
		aPanel.add(aButton);
		aButton = new JButton("COPYRIGHT");
		aPanel.add(aButton);
		aButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addCopyright();
				sourceTable.updateUI();
			}
		});
		aButton = new JButton("LICENSE");
		aButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addLicense();
				sourceTable.updateUI();
			}
		});
		aPanel.add(aButton);
		aButton = new JButton("REMOVE");
		aButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeLine();
				sourceTable.updateUI();
			}
		});
		aPanel.add(aButton);

		add(aPanel, BorderLayout.SOUTH);
	}

	private GridBagConstraints buildConstraints(GridBagConstraints gridBagConstraints, int gridx, int gridy, boolean useInsets)
	{
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		if (useInsets)
		{
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		}
		return gridBagConstraints;
	}

	private void addOption()
	{
		sourceModel.addOption();
	}

	private void addCopyright()
	{
		sourceModel.addCopyright();
	}

	private void addLicense()
	{
		sourceModel.addLicense();
	}

	private void removeLine()
	{
		sourceModel.removeLine(sourceTable.getSelectedRow());
	}

	public void updateView(PObject thisPObject)
	{
		if (!(thisPObject instanceof Campaign))
		{
			return;
		}
		theCampaign = (Campaign) thisPObject;
		sourceModel.setLists(theCampaign.getOptionsList(), theCampaign.getLicenseAsList(), theCampaign.getSection15AsList());
		rank.setText(theCampaign.getRank().toString());
		game.setText(theCampaign.getGame());
		pubNameLong.setText(theCampaign.getPubNameLong());
		pubNameShort.setText(theCampaign.getPubNameShort());
		pubNameWeb.setText(theCampaign.getPubNameWeb());
		isOGL.setSelected(theCampaign.isOGL());
		isD20.setSelected(theCampaign.isD20());
		isLicensed.setSelected(theCampaign.isLicensed());
		showInMenu.setSelected(theCampaign.canShowInMenu());
		infoText.setText(theCampaign.getInfoText());
		bookType.setSelectedItem(theCampaign.getBookType());
		setting.setText(theCampaign.getSetting());
		genre.setText(theCampaign.getGenre());
		String a = theCampaign.getDestination();
		if (a.equals(""))
		{
			a = theCampaign.getSourceFile();
			if (a.startsWith("file:"))
			{
				a = a.substring(6);
			}
			a = a.replace('\\', File.separator.charAt(0));
			a = a.replace('/', File.separator.charAt(0));
			String b = SettingsHandler.getPccFilesLocation().toString();
			b = b.replace('\\', File.separator.charAt(0));
			b = b.replace('/', File.separator.charAt(0));
			if (a.startsWith(b))
			{
				a = a.substring(b.length() + 1);
			}
			destination.setText(a);
		}
		else
		{
			destination.setText(theCampaign.getDestination());
		}
	}

	public void updateData(PObject thisPObject)
	{
		theCampaign.setRank(new Integer(rank.getText().trim()));
		theCampaign.setGame(game.getText().trim());
		theCampaign.setPubNameLong(pubNameLong.getText().trim());
		theCampaign.setPubNameShort(pubNameShort.getText().trim());
		theCampaign.setPubNameWeb(pubNameWeb.getText().trim());
		theCampaign.setIsOGL(isOGL.getSelectedObjects() != null);
		theCampaign.setIsD20(isD20.getSelectedObjects() != null);
		theCampaign.setShowInMenu(showInMenu.getSelectedObjects() != null);
		theCampaign.setIsLicensed(isLicensed.getSelectedObjects() != null);
		theCampaign.setInfoText(infoText.getText().trim());
		theCampaign.setBookType(bookType.getSelectedItem().toString());
		theCampaign.setDestination(destination.getText().trim());
		theCampaign.addLicense(".CLEAR");
		theCampaign.addSection15Info(".CLEAR");
		theCampaign.setSetting(setting.getText().trim());
		theCampaign.setGenre(genre.getText().trim());
		Properties options = new Properties();
		for (int i = 0; i < sourceModel.getOptionList().size(); i++)
		{
			options.setProperty(sourceModel.getOptionList().get(i).toString(),
				sourceModel.getOptionValues().get(i).toString());
		}
		theCampaign.setOptions(options);
		for (Iterator i = sourceModel.getLicenseList().iterator(); i.hasNext();)
		{
			theCampaign.addLicense((String) i.next());
		}
		for (Iterator i = sourceModel.getCopyrightList().iterator(); i.hasNext();)
		{
			theCampaign.addSection15Info((String) i.next());
		}

	}

	final class SourceTableModel extends AbstractTableModel
	{
		List optionList = null;
		List optionValues = null;
		List licenseList = null;
		List copyrightList = null;

		public int getColumnCount()
		{
			return 2;
		}

		public List getOptionList()
		{
			return optionList;
		}

		public List getOptionValues()
		{
			return optionValues;
		}

		public List getLicenseList()
		{
			return licenseList;
		}

		public List getCopyrightList()
		{
			return copyrightList;
		}

		public void setLists(List optList, List licList, List copyList)
		{
			optionList = (optList == null) ? new ArrayList() : optList;
			optionValues = new ArrayList();
			if (optionList != null)
			{
				for (Iterator i = optionList.iterator(); i.hasNext();)
				{
					String aString = (String) i.next();
					String val = SourceBasePanel.this.theCampaign.getOptions().getProperty(aString);
					optionValues.add(val);
				}
			}
			licenseList = (licList == null) ? new ArrayList() : licList;
			copyrightList = (copyList == null) ? new ArrayList() : copyList;
		}

		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		public int getRowCount()
		{
			if (SourceBasePanel.this.theCampaign == null)
			{
				return 0;
			}
			return optionList.size() + licenseList.size() + copyrightList.size();
		}

		public boolean isCellEditable(int rowIndex, int colIndex)
		{
			return (rowIndex < optionList.size() || colIndex == 1);
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Label";
				case 1:
					return "Value";
				default:
					break;
			}
			return "Out Of Bounds";
		}

		public void addLicense()
		{
			licenseList.add("");
		}

		public void addCopyright()
		{
			copyrightList.add("");
		}

		public void addOption()
		{
			optionList.add("");
			optionValues.add("");
		}

		public void removeLine(int row)
		{
			if (row < 0)
			{
				return;
			}
			if (row < optionList.size())
			{
				optionList.remove(row);
				optionValues.remove(row);
				return;
			}
			row -= optionList.size();
			if (row < copyrightList.size())
			{
				copyrightList.remove(row);
				return;
			}
			row -= copyrightList.size();
			if (row < licenseList.size())
			{
				licenseList.remove(row);
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (rowIndex < optionList.size())
			{
				String propertyKey = optionList.get(rowIndex).toString();
				if (columnIndex == 0)
				{
					return "OPTION:" + propertyKey;
				}
				return optionValues.get(rowIndex).toString();
			}
			rowIndex -= optionList.size();
			if (rowIndex < copyrightList.size())
			{
				if (columnIndex == 0)
				{
					return "COPYRIGHT:";
				}
				return copyrightList.get(rowIndex).toString();
			}
			rowIndex -= copyrightList.size();
			if (rowIndex < licenseList.size())
			{
				if (columnIndex == 0)
				{
					return "LICENSE:";
				}
				return licenseList.get(rowIndex).toString();
			}
			return "";
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (rowIndex < optionList.size())
			{
				String vString = (String) aValue;
				if (columnIndex == 0 && vString.startsWith("OPTION:"))
				{
					vString = vString.substring(7);
				}
				if (columnIndex == 0)
				{
					optionList.set(rowIndex, vString);
				}
				else if (columnIndex == 1)
				{
					optionValues.set(rowIndex, vString);
				}
				return;
			}
			rowIndex -= optionList.size();
			if (rowIndex < copyrightList.size())
			{
				copyrightList.set(rowIndex, aValue);
				return;
			}
			rowIndex -= copyrightList.size();
			if (rowIndex < licenseList.size())
			{
				licenseList.set(rowIndex, aValue);
				return;
			}
		}
	}

}

