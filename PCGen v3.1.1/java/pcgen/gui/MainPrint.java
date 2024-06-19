/*
 * MainPrint.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.*;
import pcgen.core.Globals;
import pcgen.core.Party;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.util.FOPHandler;

/**
 * Title:        MainPrint.java
 * Description:  New GUI implementation for printing PCs and Parties via templates
 *               Basically, this is a copy of MainExport
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

class MainPrint extends JPanel
{
	private GridBagLayout gridBagLayout = new GridBagLayout();
	private JList pcList;
	private JList templateList;
	private JLabel lblPCs = new JLabel();
	private JLabel lblTemplates = new JLabel();
	private boolean partyMode = false;
	private JButton templatePathButton = new JButton();
	private JPanel buttonPanel = new JPanel();
	private JButton exportButton = new JButton();
	private JButton printButton = new JButton();
	private JButton closeButton = new JButton();
	private JCheckBox cboxParty = new JCheckBox();
	private JScrollPane pcScroll;
	private JScrollPane templateScroll;

	private int mode;

	private JFrame parentFrame;
	private Timer timer;

	private TemplateListModel templateModel;

	private ButtonListener bl = new ButtonListener();
	private FOPHandler fh = new FOPHandler();
	private JTextField progressField = new JTextField();
	private JProgressBar progressBar = new JProgressBar();
//  	private PrinterJob printerJob = PrinterJob.getPrinterJob();

	private static final String MSG = "Please standby while rendering ...";

	public static int EXPORT_MODE = 0;
	public static int PRINT_MODE = 1;

	public MainPrint(JFrame pf, int mode)
	{
		this.mode = mode;
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		this.parentFrame = pf;
		this.timer = new Timer(5, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int value = progressBar.getValue();
				if (value == progressBar.getMaximum())
				{
					value = -1;
				}
				progressBar.setValue(value + 1);
			}
		});
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
			tempSel = SettingsHandler.getSelectedPartyTemplate();
		}
		else
		{
			tempSel = SettingsHandler.getSelectedTemplate();
		}
		tempSel = tempSel.substring(tempSel.lastIndexOf(File.separator) + 1);
//  		templateList.setSelectedValue(tempSel, true);
		templateList.setSelectedIndex(Math.max(0, templateModel.indexOf(tempSel)));
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
		templateList = new JList(templateModel = new TemplateListModel());
		templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		templateList.setVisibleRowCount(128);
		templateScroll = new JScrollPane();
		templateScroll.getViewport().setView(templateList);
		templateScroll.setPreferredSize(new Dimension(200, 200));

		templatePathButton.setText("Find Templates...");
		templatePathButton.setMnemonic(KeyEvent.VK_F);
		templatePathButton.addActionListener(bl);

		exportButton.setText("Export");
		exportButton.setMnemonic(KeyEvent.VK_E);
		exportButton.addActionListener(bl);

		/**
		 * Thomas Behr
		 * 18-12-01
		 *
		 * this is just a quick and dirty hack
		 */
		printButton.setText("Print");
		printButton.setMnemonic(KeyEvent.VK_P);
		printButton.addActionListener(bl);

		closeButton.setText("Close");
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(bl);
		// Remove the Template Path button for now, functionality has moved to the preferences menu.
		// I'll leave all the code intact, for now, since there are some people who hop around looking for templates
		// They may demand the return of this button, but it'll need a shorter name...
//		buttonPanel.add(templatePathButton);

		/**
		 * Thomas Behr
		 * 18-12-01
		 *
		 * this is just a quick and dirty hack
		 */
		if (mode == EXPORT_MODE)
		{
			buttonPanel.add(exportButton);
		}
		else if (mode == PRINT_MODE)
		{
			buttonPanel.add(printButton);
		}

		buttonPanel.add(closeButton);

		cboxParty.setText("Entire Party");
		cboxParty.addActionListener(bl);

		progressField.setBackground(UIManager.getColor("Panel.background"));
		progressField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		progressField.setEditable(false);
		progressField.setText("");
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setValue(0);

		JPanel progressPanel = new JPanel(new GridLayout(2, 1));
		progressPanel.add(progressField);
		progressPanel.add(progressBar);

		this.add(pcScroll,
		  new GridBagConstraints(0, 1, 1, 5, 0.0, 0.0,
		    GridBagConstraints.CENTER,
		    GridBagConstraints.BOTH,
		    new Insets(2, 2, 2, 2), 0, 0));
		this.add(templateScroll,
		  new GridBagConstraints(1, 1, 1, 5, 0.0, 0.0,
		    GridBagConstraints.CENTER,
		    GridBagConstraints.BOTH,
		    new Insets(2, 2, 2, 2), 0, 0));
		this.add(lblPCs,
		  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST,
		    GridBagConstraints.NONE,
		    new Insets(0, 0, 0, 0), 0, 0));
		this.add(lblTemplates,
		  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST,
		    GridBagConstraints.NONE,
		    new Insets(0, 0, 0, 0), 0, 0));
		this.add(cboxParty,
		  new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
		    GridBagConstraints.CENTER,
		    GridBagConstraints.NONE,
		    new Insets(0, 0, 0, 0), 0, 0));
		this.add(buttonPanel,
		  new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
		    GridBagConstraints.CENTER,
		    GridBagConstraints.NONE,
		    new Insets(0, 0, 0, 0), 0, 0));
		this.add(progressPanel,
		  new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0,
		    GridBagConstraints.CENTER,
		    GridBagConstraints.BOTH,
		    new Insets(0, 0, 0, 0), 0, 0));
	}

	private void getTemplatePath()
	{
		JFileChooser fcTemplates = new JFileChooser();
		fcTemplates.setCurrentDirectory(SettingsHandler.getTemplatePath());
		if (fcTemplates.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File newTemplatePath = fcTemplates.getSelectedFile();
			if (!newTemplatePath.isDirectory())
				newTemplatePath = newTemplatePath.getParentFile();
			SettingsHandler.setTemplatePath(newTemplatePath);
			final TemplateListModel tlModel = (TemplateListModel)templateList.getModel();
			tlModel.updateTemplateList(); //reload template names
			templateList.updateUI(); //refresh the list
			setDefaultTemplateSelection();  //just in case we've moved back to where the default is
		}
	}

	/**
	 * Thomas Behr
	 * 23-12-01
	 */
	private void block()
	{
		closeButton.setEnabled(false);
		closeButton.update(closeButton.getGraphics());
		exportButton.setEnabled(false);
		exportButton.update(exportButton.getGraphics());
		printButton.setEnabled(false);
		printButton.update(printButton.getGraphics());
		progressField.setText(MSG);
		progressField.update(progressField.getGraphics());
		progressBar.setValue(0);
	}

	/**
	 * Thomas Behr
	 * 23-12-01
	 */
	private void unblock()
	{
		progressBar.setValue(0);
		progressField.setText("");
		progressField.update(progressField.getGraphics());
		printButton.setEnabled(true);
		printButton.update(printButton.getGraphics());
		exportButton.setEnabled(true);
		exportButton.update(exportButton.getGraphics());
		closeButton.setEnabled(true);
		closeButton.update(closeButton.getGraphics());
	}

	/**
	 * Thomas Behr
	 * 18-12-01
	 *
	 * this is just a quick and dirty hack
	 */
	private void export()
	{
		int[] pcExports;

		final String extension = ".pdf";

		JFileChooser fcExport = new JFileChooser();
		fcExport.setCurrentDirectory(SettingsHandler.getHtmlOutputPath());
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
			final String pcName = partyMode
			  ? "Entire Party"
			  : (String)pcList.getModel().getElementAt(pcExports[loop]);
			fcExport.setSelectedFile(new File(SettingsHandler.getHtmlOutputPath() + File.separator +
			  pcName + extension));
			fcExport.setDialogTitle("Export " + pcName);
			if (fcExport.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
				continue;
			final String aFileName = fcExport.getSelectedFile().getAbsolutePath();
			SettingsHandler.setHtmlOutputPath(fcExport.getSelectedFile().getParentFile());
			if (aFileName.length() < 1)
			{
				JOptionPane.showMessageDialog(null,
				  "You must set a filename.",
				  "PCGen",
				  JOptionPane.ERROR_MESSAGE);
				continue;
			}

			block();
			try
			{
				final File outFile = new File(aFileName);
				if (outFile.isDirectory())
				{
					JOptionPane.showMessageDialog(null,
					  "You cannot overwrite a directory with a file.",
					  "PCGen",
					  JOptionPane.ERROR_MESSAGE);
					continue;
				}
				if (outFile.exists())
				{
					int reallyClose = JOptionPane.showConfirmDialog(this,
					  "The file " + outFile.getName() +
					  " already exists, " +
					  "are you sure you want " +
					  "to overwrite it?",
					  "Confirm overwriting " +
					  outFile.getName(),
					  JOptionPane.YES_NO_OPTION);
					if (reallyClose != JOptionPane.YES_OPTION)
					{
						continue;
					}
				}

				/**
				 * Thomas Behr
				 * 18-12-01
				 */
//  				block();
				File tmpFile = File.createTempFile("currentPC_", ".fo");

				// pcgen standard export
				printToFile(tmpFile, pcExports[loop]);

				// setting up pdf renderer
				fh.setMode(FOPHandler.PDF_MODE);
				fh.setInputFile(tmpFile);
				fh.setOutputFile(outFile);

				// render to pdf
//                                  block();
				timer.start();
				fh.run();
				timer.stop();
//                                  unblock();

				tmpFile.deleteOnExit();

				String errMessage = fh.getErrorMessage();
				if (errMessage.length() > 0)
				{
					JOptionPane.showMessageDialog(null,
					  errMessage,
					  "PCGen",
					  JOptionPane.ERROR_MESSAGE);
				}
//  				unblock();
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(null,
				  "Could not export " + pcName + ". Try another filename.",
				  "PCGen",
				  JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
			unblock();
		}
	}

	/**
	 * Thomas Behr
	 * 16-12-01
	 *
	 * this is just a quick and dirty hack
	 */
	private void print()
	{
		int[] pcExports;

		//final String templateName = (String)templateList.getSelectedValue();

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

			final String pcName = partyMode
			  ? "Entire Party"
			  : (String)pcList.getModel().getElementAt(pcExports[loop]);

			block();
			try
			{
//  				block();
				File tmpFile = File.createTempFile("currentPC_", ".fo");

				// pcgen standard export
				printToFile(tmpFile, pcExports[loop]);

				// setting up pdf renderer
				fh.setMode(FOPHandler.AWT_MODE);
				fh.setInputFile(tmpFile);

				// render to awt
//                                  block();
				timer.start();
				fh.run();
				timer.stop();
//                                  unblock();

				tmpFile.deleteOnExit();

				String errMessage = fh.getErrorMessage();
				if (errMessage.length() > 0)
				{
					JOptionPane.showMessageDialog(null,
					  errMessage,
					  "PCGen",
					  JOptionPane.ERROR_MESSAGE);
				}
				else
				{

					// standard print stuff
					org.apache.fop.render.awt.AWTRenderer awtRenderer =
					  (org.apache.fop.render.awt.AWTRenderer)fh.getRenderer();

					PrinterJob printerJob = PrinterJob.getPrinterJob();
					printerJob.setPrintable(awtRenderer);
					printerJob.setPageable(awtRenderer);

					if (printerJob.printDialog())
					{
						printerJob.print();
					}

				}
//  				unblock();
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(null,
				  "Could not print " + pcName + ". Try again.",
				  "PCGen",
				  JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
			unblock();
		}

	}

	private void printToFile(File outFile, int pcIndex) throws IOException
	{
		final BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		final File template = new File(SettingsHandler.getTemplatePath().getAbsolutePath() + File.separator +
		  (String)templateList.getSelectedValue());
		if (partyMode)
		{
			SettingsHandler.setSelectedPartyTemplate(template.getAbsolutePath());
			Party.print(template, bw);
		}
		else
		{
			SettingsHandler.setSelectedTemplate(template.getAbsolutePath());
			final PlayerCharacter aPC = (PlayerCharacter)Globals.getPCList().get(pcIndex);
			final PlayerCharacter oldPC = Globals.getCurrentPC();
			Globals.setCurrentPC(aPC);
			(new pcgen.io.ExportHandler(template)).write(aPC, bw);
//  			aPC.print(template, bw);
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
				final PlayerCharacter aPC = (PlayerCharacter)Globals.getPCList().get(index);
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
			final File templateFolder = SettingsHandler.getTemplatePath();

			if (templateFolder.exists())
			{
				cSheets = templateFolder.list(new FilenameFilter()
				{
					String tmp = "";

					public boolean accept(File dir, String name)
					{
						/**
						 * Thomas Behr
						 * 16-12-01
						 *
						 * this is just a quick and dirty hack
						 */
						tmp = name.toLowerCase();
						if (tmp.startsWith("csheet") &&
						  (tmp.endsWith(".fo") || tmp.endsWith(".xsl")))
							return true;
						return false;
					}
				});
				pSheets = templateFolder.list(new FilenameFilter()
				{
					String tmp = "";

					public boolean accept(File dir, String name)
					{
						/**
						 * Thomas Behr
						 * 16-12-01
						 *
						 * this is just a quick and dirty hack
						 */
						tmp = name.toLowerCase();
						if (tmp.startsWith("psheet") &&
						  (tmp.endsWith(".fo") || tmp.endsWith(".xsl")))
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

				JOptionPane.showMessageDialog(null,
				  "Directory: " + templateFolder.getPath() + " does not exist.",
				  "PCGen",
				  JOptionPane.INFORMATION_MESSAGE);
			}
			Arrays.sort(pSheets);
			Arrays.sort(cSheets);
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

	/**
	 * Thomas Behr
	 * 23-12-01
	 */
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();
			if (src.equals(exportButton))
			{
				(new Thread(new Runnable()
				{
					public void run()
					{
						export();
					}
				})).start();
			}
			else if (src.equals(printButton))
			{
				(new Thread(new Runnable()
				{
					public void run()
					{
						print();
					}
				})).start();
			}
			else if (src.equals(closeButton))
			{
				parentFrame.dispose();
			}
			else if (src.equals(cboxParty))
			{
				setPartyMode(cboxParty.isSelected());
				pcList.setEnabled(!cboxParty.isSelected());
				templateList.updateUI();
				setDefaultTemplateSelection();
			}
			else if (src.equals(templatePathButton))
			{
				getTemplatePath();
			}
		}
	}
}














