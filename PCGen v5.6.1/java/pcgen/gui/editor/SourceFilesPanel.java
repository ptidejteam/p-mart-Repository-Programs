/*
 * SourceFilesPanel.java
 * Copyright 2003 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on March 14, 2003, 2:57:20 PM
 *
 * @(#) $Id: SourceFilesPanel.java,v 1.1 2006/02/21 01:33:33 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JTableEx;
import pcgen.util.Logging;

/**
 * <code>SourceFilesPanel</code>
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision: 1.1 $
 */
final class SourceFilesPanel extends JPanel
{
	static final long serialVersionUID = 2833296242093657468L;
	private JComboBoxEx fileType;
	private JScrollPane scrollPane;
	private JTableEx fileTable;
	private SourceFileModel fileModel;
	private Campaign theCampaign = null;

	/** Creates new form SourceBasePanel */

	public SourceFilesPanel()
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
		setLayout(new BorderLayout());

		scrollPane = new JScrollPane();
		fileTable = new JTableEx();
		fileModel = new SourceFileModel();
		fileType = new JComboBoxEx();
		fileType.addItem("CLASS");
		fileType.addItem("COMPANIONMOD");
		fileType.addItem("COMMENT");
		fileType.addItem("DEITY");
		fileType.addItem("DOMAIN");
		fileType.addItem("EQUIPMENT");
		fileType.addItem("EQUIPMOD");
		fileType.addItem("FEAT");
		fileType.addItem("KIT");
		fileType.addItem("RACE");
		fileType.addItem("REGION");
		fileType.addItem("SKILL");
		fileType.addItem("SPELL");
		fileType.addItem("TEMPLATE");
		fileType.addItem("WEAPONPROF");
		fileType.setSelectedIndex(0);
		JPanel aPanel = new JPanel();
		aPanel.add(fileType);
		JButton aButton = new JButton("Add");
		aButton.addActionListener(new ActionListener()
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
					d = theCampaign.getSourceFile();
					if (d.startsWith("file:/"))
					{
						d = d.substring(6);
					}
				}
				if (d == null || d.equals(""))
				{
					d = SettingsHandler.getPccFilesLocation().toString();
				}
				if (!fileType.getSelectedItem().toString().equals("COMMENT"))
				{
					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(new File(d));
					int returnVal = fc.showOpenDialog(SourceFilesPanel.this);
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
				}
				fileModel.addFileAndLocation(fileType.getSelectedItem().toString(), loc);
				fileTable.updateUI();
			}
		});
		aPanel.add(aButton);
		aButton = new JButton("Remove");
		aButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int row = SourceFilesPanel.this.fileTable.getSelectedRow();
				fileModel.removeItemsAtIndex(row);
				fileTable.updateUI();
			}
		});
		aPanel.add(aButton);
		add(aPanel, BorderLayout.SOUTH);

		fileTable.setModel(fileModel);
		fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileTable.setDoubleBuffered(false);
		scrollPane.setViewportView(fileTable);
		add(scrollPane, BorderLayout.CENTER);
	}

	public void updateView(PObject thisPObject)
	{
		if (!(thisPObject instanceof Campaign))
		{
			return;
		}
		theCampaign = (Campaign) thisPObject;
		List aList = theCampaign.getLines();
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			String a = (String) i.next();
			if (a.startsWith("#"))
			{
				fileModel.addFileAndLocation("COMMENT", a.substring(1));
			}
			else
			{
				String b = a.substring(0, a.indexOf(":"));
				fileModel.addFileAndLocation(b, a.substring(b.length() + 1));
			}
		}
		fileTable.updateUI();
	}

	public void updateData(PObject thisPObject)
	{
		List fList = fileModel.getFileList();
		List lList = fileModel.getLocationList();
		Iterator j = lList.iterator();
		theCampaign.addLine(".CLEAR");
		for (Iterator i = fList.iterator(); i.hasNext();)
		{
			String name = (String) i.next();
			String val = (String) j.next();
			if (name.equals("COMMENT"))
			{
				theCampaign.addLine("#" + val);
			}
			else
			{
				theCampaign.addLine(name + ":" + val);
			}
		}
	}

	final class SourceFileModel extends AbstractTableModel
	{
		List fileList = new ArrayList();
		List locationList = new ArrayList();

		public int getColumnCount()
		{
			return 2;
		}

		public List getFileList()
		{
			return fileList;
		}

		public List getLocationList()
		{
			return locationList;
		}

		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		public int getRowCount()
		{
			return fileList.size();
		}

		public boolean isCellEditable(int rowIndex, int colIndex)
		{
			return (colIndex == 1);
		}

		public void addFileAndLocation(String filetype, String filelocation)
		{
			int row = SourceFilesPanel.this.fileTable.getSelectedRow();
			if (fileList.size() == 0)
			{
				row = 0;
			}
			else if (row < 0)
			{
				row = fileList.size() - 1;
			}
			fileList.add(row, filetype);
			locationList.add(row, filelocation);
		}

		public void removeItemsAtIndex(int loc)
		{
			if (loc >= 0 && loc < fileList.size())
			{
				fileList.remove(loc);
				locationList.remove(loc);
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			String rc = null;
			switch (columnIndex)
			{
				case 0:
					if (rowIndex < fileList.size())
					{
						rc = fileList.get(rowIndex).toString();
					}
					else
					{
						rc = "";
					}
					break;

				case 1:
					if (rowIndex < locationList.size())
					{
						rc = locationList.get(rowIndex).toString();
					}
					else
					{
						rc = "";
					}
					break;
				default:
					Logging.errorPrint("Unhandled column " + columnIndex + " at SourceFilesPanel.getValueAt");
					break;
			}
			return rc;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (columnIndex == 1 && rowIndex < locationList.size())
			{
				String vString = (String) aValue;
				locationList.set(rowIndex, vString);
			}
		}
	}
}
