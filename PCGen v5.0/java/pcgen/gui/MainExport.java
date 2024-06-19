/*
 * MainExport.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.util.PropertyFactory;

/**
 * Title:        MainExport.java
 * Description:  New GUI implementation for exporting PCs and Parties via templates
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Jason Buchanan
 * @version $Revision: 1.1 $
 */

final class MainExport extends JPanel
{
	private JList pcList;
	private JList templateList;
	private JButton exportButton = new JButton();
	private JLabel lblPCs = new JLabel();
	private JLabel lblTemplates = new JLabel();
	private boolean partyMode = false;
	private JButton templatePathButton = new JButton();
	private JPanel buttonPanel = new JPanel();
	private JButton closeButton = new JButton();
	private JCheckBox cboxParty = new JCheckBox();
	private JScrollPane pcScroll;
	private JScrollPane templateScroll;

	private TemplateListModel templateModel;

	private static final CsheetFilter csheetFilter = new CsheetFilter();
	private static final CsheetFilter psheetFilter = new CsheetFilter(1);
	private static int attempts = 0;

	public MainExport()
	{
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			Globals.errorPrint("Error while initing form", e);
		}
	}

	private void setPartyMode(boolean party)
	{
		partyMode = party;
		//templateList.updateUI();
		templateList.revalidate();
		cboxParty.setSelected(party);
	}

	public void setCurrentPCSelection(int curSel)
	{
		pcList.updateUI();
		//pcList.revalidate();
		if (curSel > 0 && curSel - 1 < pcList.getModel().getSize()) //an individual PC is selected
		{
			setPartyMode(false);
			pcList.setSelectedIndex(curSel - 1);
		}
		else
		{
			setPartyMode(true);
			pcList.setSelectedIndex(pcList.getModel().getSize() - 1); //select "Entire Party" if the user's on DM Tools or Campaign tab
		}
		setDefaultTemplateSelection();
	}

	private void setDefaultTemplateSelection()
	{
		String tempSel;
		if (partyMode)
		{
			tempSel = SettingsHandler.getSelectedPartyHTMLOutputSheet();
		}
		else
		{
			tempSel = SettingsHandler.getSelectedCharacterHTMLOutputSheet();
		}

		// Need to make sure to pick a safe item!
		// Bug #714808 sage_sam 04 April 2003

		final File templateDir = SettingsHandler.getTemplatePath(); 
		if( templateDir!=null ) 
		{ 
			final int templatePathEnd=templateDir.getAbsolutePath().length()+1; 
			if( (templatePathEnd>1) && (templatePathEnd<tempSel.length()) ) 
			{ 
				tempSel=tempSel.substring(templatePathEnd); 
			} 
		} 
		templateList.setSelectedIndex(Math.max(0, templateModel.indexOf(tempSel)));
	}

	private void jbInit() throws Exception
	{
		lblPCs.setText("Select a Character:");
		pcList = new JList(new PCListModel());
		pcList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		pcScroll = new JScrollPane();
		pcScroll.getViewport().setView(pcList);
		lblTemplates.setText("Select a Template:");
		templateList = new JList(templateModel = new TemplateListModel());		templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		templateScroll = new JScrollPane();
		templateScroll.getViewport().setView(templateList);
		templatePathButton.setText("Find Templates...");
		templatePathButton.setMnemonic(KeyEvent.VK_F);
		templatePathButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				getTemplatePath();
			}
		});
		exportButton.setText("Export");
		exportButton.setMnemonic(KeyEvent.VK_E);
		exportButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (templateList.getSelectedValue() == null)
				{
					JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_exportNoTemplate"), "PCGen", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					export();
				}
			}
		});

		closeButton.setText("Close");
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFrame parentFrame = (JFrame) getParent().getParent().getParent().getParent();  //ugly, but effective...
				parentFrame.dispose();
			}
		});
		buttonPanel.add(exportButton);
		buttonPanel.add(closeButton);
		cboxParty.setText("Entire Party");
		cboxParty.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setPartyMode(cboxParty.isSelected());
				pcList.setEnabled(!cboxParty.isSelected());
				//templateList.updateUI();
				templateList.revalidate();
				setDefaultTemplateSelection();
			}
		});
		this.setLayout(new BorderLayout());
		JPanel aPanel = new JPanel();
		aPanel.setLayout(new BorderLayout());
		aPanel.add(lblPCs, BorderLayout.NORTH);
		aPanel.add(pcScroll, BorderLayout.CENTER);
		aPanel.add(cboxParty, BorderLayout.SOUTH);
		aPanel.setMinimumSize(new Dimension(200, 200));
		this.add(aPanel, BorderLayout.WEST);

		JPanel bPanel = new JPanel();
		bPanel.setLayout(new BorderLayout());
		JPanel cPanel = new JPanel();
		cPanel.add(lblTemplates);
		bPanel.add(cPanel, BorderLayout.NORTH);
		bPanel.add(templateScroll, BorderLayout.CENTER);
		bPanel.add(buttonPanel, BorderLayout.SOUTH);
		this.add(bPanel, BorderLayout.CENTER);

		this.setSize(new Dimension(500, 400));
	}

	private void getTemplatePath()
	{
		JFileChooser fcTemplates = new JFileChooser();
		fcTemplates.setCurrentDirectory(new File(SettingsHandler.getHTMLOutputSheetPath()));
		if (fcTemplates.showOpenDialog(MainExport.this) == JFileChooser.APPROVE_OPTION)
		{
			File newTemplatePath = fcTemplates.getSelectedFile();
			if (!newTemplatePath.isDirectory())
			{
				newTemplatePath = newTemplatePath.getParentFile();
			}
			SettingsHandler.setTemplatePath(newTemplatePath.getParentFile());
			final TemplateListModel tlModel = (TemplateListModel) templateList.getModel();
			tlModel.updateTemplateList(); //reload template names
			//templateList.updateUI(); //refresh the list
			templateList.revalidate(); //refresh the list
			setDefaultTemplateSelection();  //just in case we've moved back to where the default is
		}
	}

	private void export()
	{
		int[] pcExports;

		final String templateName = (String) templateList.getSelectedValue();
		final int idx = templateName.lastIndexOf('.');
		String extension = "";
		if (idx >= 0)
		{
			extension = templateName.substring(idx + 1);
		}

		final PFileChooser fcExport = new PFileChooser();
		fcExport.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fcExport.setCurrentDirectory(new File(SettingsHandler.getHTMLOutputSheetPath()));
		String desc;
		if ("htm".equalsIgnoreCase(extension))
		{
			desc = "HTML Documents";
		}
		else if ("xml".equalsIgnoreCase(extension))
		{
			desc = "XML Documents";
		}
		else
		{
			desc = extension + " Files";
		}
		fcExport.addChoosableFileFilter(extension, desc + " (*." + extension + ")");

		if (!partyMode)
		{
			pcExports = pcList.getSelectedIndices();
		}
		else
		{
			pcExports = new int[]{-2}; //this value should never happen with getSelectedIndices()
		}
		for (int loop = 0; loop < pcExports.length; loop++)
		{
			final String pcName = partyMode ? "Entire Party" : (String) pcList.getModel().getElementAt(pcExports[loop]);
			fcExport.setSelectedFile(new File(SettingsHandler.getPcgPath().toString() + File.separator + pcName + "." + extension));
			fcExport.setDialogTitle("Export " + pcName);
			if (fcExport.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			{
				continue;
			}
			final String aFileName = fcExport.getSelectedFile().getAbsolutePath();

			if (aFileName.length() < 1)
			{
				JOptionPane.showMessageDialog(null, "You must set a filename.", "PCGen", JOptionPane.ERROR_MESSAGE);
				continue;
			}
			try
			{
				final File outFile = new File(aFileName);
				if (outFile.isDirectory())
				{
					JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a file.", "PCGen", JOptionPane.ERROR_MESSAGE);
					continue;
				}
				if (outFile.exists())
				{
					int reallyClose = JOptionPane.showConfirmDialog(this, "The file " + outFile.getName() + " already exists, are you sure you want to overwrite it?", "Confirm overwriting " + outFile.getName(), JOptionPane.YES_NO_OPTION);
					if (reallyClose != JOptionPane.YES_OPTION)
					{
						continue;
					}
				}
				printToFile(outFile, pcExports[loop]);
				Globals.executePostExportCommand(aFileName);
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(null, "Could not export " + pcName + ". Try another filename.", "PCGen", JOptionPane.ERROR_MESSAGE);
				Globals.errorPrint("Could not export " + pcName, ex);
			}
		}
	}

	private void printToFile(File outFile, int pcIndex) throws IOException
	{
		//final BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
		final File template = new File(SettingsHandler.getTemplatePath() + File.separator + (String) templateList.getSelectedValue());
		if (partyMode)
		{
			SettingsHandler.setSelectedPartyHTMLOutputSheet(template.getAbsolutePath());
			(new ExportHandler(template)).write(Globals.getPCList(), bw);
//			Party.print(template, bw);
			bw.close();
		}
		else
		{
			SettingsHandler.setSelectedCharacterHTMLOutputSheet(template.getAbsolutePath());
			final PlayerCharacter aPC = (PlayerCharacter) Globals.getPCList().get(pcIndex);
			final PlayerCharacter oldPC = Globals.getCurrentPC();
			Globals.setCurrentPC(aPC);
			(new ExportHandler(template)).write(aPC, bw);
//                          aPC.print(template, bw);
			Globals.setCurrentPC(oldPC);
		}
		bw.close();
	}

	private class PCListModel extends AbstractListModel
	{
		public Object getElementAt(int index)
		{
			if (index < Globals.getPCList().size())
			{
				final PlayerCharacter aPC = (PlayerCharacter) Globals.getPCList().get(index);
				return aPC.getDisplayName();
			}
			else
			{
				return null;
			}
		}

		public int getSize()
		{
			return Globals.getPCList().size();
		}
	}

	void refreshTemplates()
	{
		((TemplateListModel) templateList.getModel()).updateTemplateList();
	}

	//when first created, this class will cache the contents of the "Templates" directory
	private class TemplateListModel extends AbstractListModel
	{
		private String[] cSheets;
		private String[] pSheets;

		public TemplateListModel()
		{
			super();
			updateTemplateList();
		}

		public void updateTemplateList()
		{
			csheetFilter.setDirFilter("htmlxml");
			ArrayList aList = csheetFilter.getAccepted();

			if (aList.size() == 0 && attempts == 0)
			{
				Object[] options = {"OK", "CANCEL"};
				if (JOptionPane.showOptionDialog(null, "No templates found. Attempt to change to " + Globals.getDefaultPath() + File.separator + "outputsheets ?", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]) == JOptionPane.YES_OPTION)
				{
					SettingsHandler.setTemplatePath(new File(Globals.getDefaultPath() + File.separator + "outputsheets"));
					attempts = 1;
					aList = csheetFilter.getAccepted();
				}
			}

			cSheets = new String[aList.size()];
			for (int i = 0; i < aList.size(); i++)
			{
				cSheets[i] = aList.get(i).toString();
			}

			psheetFilter.setDirFilter("htmlxml");
			aList = psheetFilter.getAccepted();
			pSheets = new String[aList.size()];
			for (int i = 0; i < aList.size(); i++)
			{
				pSheets[i] = aList.get(i).toString();
			}

			Arrays.sort(pSheets);
			Arrays.sort(cSheets);
		}

		public Object getElementAt(int index)
		{
			if (partyMode)
			{
				if (index >= pSheets.length)
				{
					return null;
				}
				else
				{
					return pSheets[index];
				}
			}
			else
			{
				if (index >= cSheets.length)
				{
					return null;
				}
				else
				{
					return cSheets[index];
				}
			}
		}

		public int getSize()
		{
			if (partyMode)
			{
				return pSheets.length;
			}
			else
			{
				return cSheets.length;
			}
		}

		public int indexOf(Object o)
		{
			if (partyMode)
			{
				return Arrays.binarySearch(pSheets, o);
			}
			else
			{
				return Arrays.binarySearch(cSheets, o);
			}
		}
	}
}
