package pcgen.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
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
import pcgen.core.Party;
import pcgen.core.PlayerCharacter;

/**
 * Title:        MainExport.java
 * Description:  New GUI implementation for exporting PCs and Parties via templates
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Jason Buchanan
 * @version $Revision: 1.1 $
 */

public class MainExport extends JPanel
{
	private GridBagLayout gridBagLayout = new GridBagLayout();
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
	private JFileChooser fcExport = new JFileChooser();
	private JFileChooser fcTemplates = new JFileChooser();

	public MainExport()
	{
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setPartyMode(boolean party)
	{
		partyMode = party;
		templateList.updateUI();
	}

	public void setCurrentPCSelection(int curSel)
	{
		pcList.updateUI();
		if (curSel > 1 && curSel - 2 < pcList.getModel().getSize()) //an individual PC is selected
		{
			setPartyMode(false);
			pcList.setSelectedIndex(curSel - 2);
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
			tempSel = Globals.getSelectedPartyTemplate();
		}
		else
		{
			tempSel = Globals.getSelectedTemplate();
		}
		tempSel = tempSel.substring(tempSel.lastIndexOf(File.separator) + 1);
		templateList.setSelectedValue(tempSel, true);
	}

	private void jbInit() throws Exception
	{
		this.setLayout(gridBagLayout);
		lblPCs.setText("Select a Character:");
		pcList = new JList(new PCListModel());
		pcList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		pcList.setVisibleRowCount(128);
		pcScroll = new JScrollPane();
		pcScroll.getViewport().setView(pcList);
		pcScroll.setPreferredSize(new Dimension(200, 200));
		lblTemplates.setText("Select a Template:");
		templateList = new JList(new TemplateListModel());
		templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		templateList.setVisibleRowCount(128);
		templateScroll = new JScrollPane();
		templateScroll.getViewport().setView(templateList);
		templateScroll.setPreferredSize(new Dimension(200, 200));
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
				export();
			}
		});

		closeButton.setText("Close");
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFrame parentFrame = (JFrame)getParent().getParent().getParent().getParent();  //ugly, but effective...
				parentFrame.dispose();
			}
		});
		// Remove the Template Path button for now, functionality has moved to the preferences menu.
		// I'll leave all the code intact, for now, since there are some people who hop around looking for tmeplates
		// They may demand the return of this button, but it'll need a shorter name...
//		buttonPanel.add(templatePathButton);
		buttonPanel.add(exportButton);
		buttonPanel.add(closeButton);
		cboxParty.setText("Entire Party");
		cboxParty.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setPartyMode(cboxParty.isSelected());
				pcList.setEnabled(!cboxParty.isSelected());
				templateList.updateUI();
				setDefaultTemplateSelection();
			}
		});
		this.add(pcScroll, new GridBagConstraints(0, 1, 1, 5, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		this.add(templateScroll, new GridBagConstraints(1, 1, 1, 5, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		this.add(lblPCs, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(lblTemplates, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(cboxParty, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(buttonPanel, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	private void getTemplatePath()
	{
		fcTemplates.setCurrentDirectory(Globals.getTemplatePath());
		if (fcTemplates.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File newTemplatePath = fcTemplates.getSelectedFile();
			if (!newTemplatePath.isDirectory())
				newTemplatePath = newTemplatePath.getParentFile();
			Globals.setTemplatePath(newTemplatePath);
			final TemplateListModel tlModel = (TemplateListModel)templateList.getModel();
			tlModel.updateTemplateList(); //reload template names
			templateList.updateUI(); //refresh the list
			setDefaultTemplateSelection();  //just in case we've moved back to where the default is
		}
	}

	private void export()
	{
		int[] pcExports;
		final String templateName = (String)templateList.getSelectedValue();
		final String extension = templateName.substring(templateName.lastIndexOf('.'));
		fcExport.setCurrentDirectory(Globals.getHtmlOutputPath());
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
			final String pcName = partyMode ? "Entire Party" : (String)pcList.getModel().getElementAt(pcExports[loop]);
			fcExport.setSelectedFile(new File(Globals.getHtmlOutputPath() + File.separator + pcName + extension));
			fcExport.setDialogTitle("Export " + pcName);
			if (fcExport.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
				continue;
			final String aFileName = fcExport.getSelectedFile().getAbsolutePath();
			Globals.setHtmlOutputPath(fcExport.getSelectedFile().getParentFile());
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
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(null, "Could not export " + pcName + ". Try another filename.", "PCGen", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	private void printToFile(File outFile, int pcIndex) throws IOException
	{
		final BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		final File template = new File(Globals.getTemplatePath().getAbsolutePath() + File.separator + (String)templateList.getSelectedValue());
		if (partyMode)
		{
			Globals.setSelectedPartyTemplate(template.getAbsolutePath());
			Party.print(template, bw);
		}
		else
		{
			Globals.setSelectedTemplate(template.getAbsolutePath());
			final PlayerCharacter aPC = (PlayerCharacter)Globals.getPcList().get(pcIndex);
			final PlayerCharacter oldPC = Globals.getCurrentPC();
			Globals.setCurrentPC(aPC);
			aPC.print(template, bw);
			Globals.setCurrentPC(oldPC);
		}
		bw.close();
	}

	private class PCListModel extends AbstractListModel
	{
		public Object getElementAt(int index)
		{
			if (index < Globals.getPcList().size())
			{
				final PlayerCharacter aPC = (PlayerCharacter)Globals.getPcList().get(index);
				return aPC.getName();
			}
			else
			{
				return null;
			}
		}

		public int getSize()
		{
			return Globals.getPcList().size();
		}
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
			final File templateFolder = Globals.getTemplatePath();

			if (templateFolder.exists())
			{
				final int iTest = templateFolder.list().length;
				cSheets = templateFolder.list(new FilenameFilter()
				{
					public boolean accept(File dir, String name)
					{
						if (name.toLowerCase().startsWith("csheet"))
							return true;
						return false;
					}
				});
				pSheets = templateFolder.list(new FilenameFilter()
				{
					public boolean accept(File dir, String name)
					{
						if (name.toLowerCase().startsWith("psheet"))
							return true;
						return false;
					}
				});
			}
			else
			{
				// Initialize these so this case appears the same as the case in which the
				// directory is empty
				cSheets = new String[0];
				pSheets = new String[0];

				JOptionPane.showMessageDialog(null, "Directory: " + templateFolder.getPath() + " does not exist."
					, "PCGen", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		public Object getElementAt(int index)
		{
			if (partyMode)
			{
				if (index >= pSheets.length)
					return null;
				else
					return pSheets[index];
			}
			else
			{
				if (index >= cSheets.length)
					return null;
				else
					return cSheets[index];
			}
		}

		public int getSize()
		{
			if (partyMode)
				return pSheets.length;
			else
				return cSheets.length;
		}
	}
}