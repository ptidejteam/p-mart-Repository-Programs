/*
 * MainPrint.java
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.GuiFacade;
import pcgen.io.ExportHandler;
import pcgen.util.FOPHandler;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * Title:        MainPrint.java
 * Description:  New GUI implementation for printing PCs and Parties via templates
 *               Basically, this is a copy of MainExport
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

final class MainPrint extends JPanel
{
	static final long serialVersionUID = 2322087772130893998L;
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
	private static final CsheetFilter csheetFilter = new CsheetFilter();
	private static final CsheetFilter psheetFilter = new CsheetFilter(1);

	public MainPrint(JFrame pf, int mode)
	{
		this.mode = mode;
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error while initing form", e);
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
		cboxParty.setSelected(party);
	}

	public void setCurrentPCSelection(int curSel)
	{
		pcList.updateUI();
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
			tempSel = SettingsHandler.getSelectedPartyPDFOutputSheet();
		}
		else
		{
			tempSel = SettingsHandler.getSelectedCharacterPDFOutputSheet();
		}

		// Need to make sure to pick a safe item!
		// Bug #714808 sage_sam 04 April 2003
		final File templateDir = SettingsHandler.getPcgenOutputSheetDir();
		if (templateDir != null)
		{
			final int templatePathEnd = templateDir.getAbsolutePath().length() + 1;
			if ((templatePathEnd > 1) && (templatePathEnd < tempSel.length()))
			{
				tempSel = tempSel.substring(templatePathEnd);
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
		templateList = new JList(templateModel = new TemplateListModel());
		templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		templateScroll = new JScrollPane();
		templateScroll.getViewport().setView(templateList);
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

		this.add(progressPanel, BorderLayout.SOUTH);
		this.setSize(new Dimension(500, 400));

	}

	private void getTemplatePath()
	{
		PFileChooser fcTemplates = new PFileChooser();
		fcTemplates.setCurrentDirectory(new File(SettingsHandler.getPDFOutputSheetPath()));
		if (fcTemplates.showOpenDialog(MainPrint.this) == PFileChooser.APPROVE_OPTION)
		{
			File newTemplatePath = fcTemplates.getSelectedFile();
			if (!newTemplatePath.isDirectory())
			{
				newTemplatePath = newTemplatePath.getParentFile().getParentFile();
			}
			final TemplateListModel tlModel = (TemplateListModel) templateList.getModel();
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

		PFileChooser fcExport = new PFileChooser();
		fcExport.setCurrentDirectory(new File(SettingsHandler.getPcgPath().toString()));
		fcExport.setFileSelectionMode(PFileChooser.FILES_ONLY);
		fcExport.addChoosableFileFilter(null, "All Files (*.*)");
		fcExport.addChoosableFileFilter("pdf", "PDF Documents (*.pdf)");

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
			fcExport.setSelectedFile(new File(SettingsHandler.getPcgPath().toString() + File.separator + pcName + extension));
			fcExport.setDialogTitle("Export " + pcName);

			try
			{
				if (fcExport.showSaveDialog(this) != PFileChooser.APPROVE_OPTION)
				{
					continue;
				}
			}
			catch (Exception ex)
			{
				Logging.errorPrint("Could not show Save Dialog for "+pcName);
				continue;
			}
			final String aFileName = fcExport.getSelectedFile().getAbsolutePath();
			SettingsHandler.setSelectedCharacterPDFOutputSheet(fcExport.getSelectedFile().getAbsolutePath());
			if (aFileName.length() < 1)
			{
				GuiFacade.showMessageDialog(null, "You must set a filename.", "PCGen", GuiFacade.ERROR_MESSAGE);
				continue;
			}

			try
			{
				final File outFile = new File(aFileName);
				if (outFile.isDirectory())
				{
					GuiFacade.showMessageDialog(null, "You cannot overwrite a directory with a file.", "PCGen", GuiFacade.ERROR_MESSAGE);
					continue;
				}
				if (outFile.exists())
				{
					int reallyClose = GuiFacade.showConfirmDialog(this, "The file " + outFile.getName() + " already exists, " + "are you sure you want " + "to overwrite it?", "Confirm overwriting " + outFile.getName(), GuiFacade.YES_NO_OPTION);
					if (reallyClose != GuiFacade.YES_OPTION)
					{
						continue;
					}
				}

				// can NOT block until after all
				// possible user input is done
				block();

				/**
				 * Dekker500
				 * Feb 1, 2003
				 *
				 * If user selected an XSLT template, perform export to base XML file
				 */
				File tmpFile = null;
				if (((String) templateList.getSelectedValue()).endsWith(".xslt"))
				{
					tmpFile = File.createTempFile("currentPC_", ".xml");
					printToXMLFile(tmpFile, pcExports[loop]);
					File template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator + (String) templateList.getSelectedValue());
					fh.setInputFile(tmpFile, template);
				}
				else
				{
					tmpFile = File.createTempFile("currentPC_", ".fo");
					printToFile(tmpFile, pcExports[loop]);
					fh.setInputFile(tmpFile);
				}

				// setting up pdf renderer
				fh.setMode(FOPHandler.PDF_MODE);
				fh.setOutputFile(outFile);

				// render to pdf
				Throwable throwable = null;
				timer.start();
				try
				{
					fh.run();
				}
				catch (Throwable t)
				{
					throwable = t;
				}
				timer.stop();

				// must unblock before can do user dialogs
				unblock();

				tmpFile.deleteOnExit();

				if (throwable != null)
				{
					GuiFacade.showMessageDialog(null, throwable.getClass().getName() + ": " + throwable.getMessage(), "PCGen", GuiFacade.ERROR_MESSAGE);
					throwable.printStackTrace();
				}

				String errMessage = fh.getErrorMessage();
				if (errMessage.length() > 0)
				{
					GuiFacade.showMessageDialog(null, errMessage, "PCGen", GuiFacade.ERROR_MESSAGE);
				}
				Globals.executePostExportCommand(aFileName);
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Could not export " + pcName, ex);
				GuiFacade.showMessageDialog(null, "Could not export " + pcName + ". Try another filename.", "PCGen", GuiFacade.ERROR_MESSAGE);
			}
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

			final String pcName = partyMode ? "Entire Party" : (String) pcList.getModel().getElementAt(pcExports[loop]);

			block();
			try
			{

				/*
				 * Dekker500
				 * Feb 1, 2003
				 *
				 * If user selected an XSLT template, perform export to base XML file
				 */
				File tmpFile = null;
				Logging.debugPrint((String) templateList.getSelectedValue());
				if (((String) templateList.getSelectedValue()).endsWith(".xslt"))
				{
					Logging.debugPrint("Printing using XML/XSLT");
					tmpFile = File.createTempFile("currentPC_", ".xml");
					printToXMLFile(tmpFile, pcExports[loop]);
					File template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator + (String) templateList.getSelectedValue());
					fh.setInputFile(tmpFile, template);
				}
				else
				{
					Logging.debugPrint("Printing using FO sheets");
					tmpFile = File.createTempFile("currentPC_", ".fo");
					printToFile(tmpFile, pcExports[loop]);
					fh.setInputFile(tmpFile);
				}


				// setting up pdf renderer
				fh.setMode(FOPHandler.AWT_MODE);
//				fh.setInputFile(tmpFile);

				// render to awt
//                                  block();
				Throwable throwable = null;
				timer.start();
				try
				{
					fh.run();
				}
				catch (Throwable t)
				{
					throwable = t;
				}
				timer.stop();
//                                  unblock();

				tmpFile.deleteOnExit();

				if (throwable != null)
				{
					GuiFacade.showMessageDialog(null, throwable.getClass().getName() + ": " + throwable.getMessage(), "PCGen", GuiFacade.ERROR_MESSAGE);
					throwable.printStackTrace();
				}

				String errMessage = fh.getErrorMessage();
				if (errMessage.length() > 0)
				{
					GuiFacade.showMessageDialog(null, errMessage, "PCGen", GuiFacade.ERROR_MESSAGE);
				}
				else
				{

					// standard print stuff
					org.apache.fop.render.awt.AWTRenderer awtRenderer = (org.apache.fop.render.awt.AWTRenderer) fh.getRenderer();

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
				Logging.errorPrint("Could not print " + pcName, ex);
				GuiFacade.showMessageDialog(null, "Could not print " + pcName + ". Try again.", "PCGen", GuiFacade.ERROR_MESSAGE);
			}
			unblock();
		}

	}

	private void printToFile(File outFile, int pcIndex) throws IOException
	{
		//final BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
		final File template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator + (String) templateList.getSelectedValue());
		if (partyMode)
		{
			SettingsHandler.setSelectedPartyPDFOutputSheet(template.getAbsolutePath());
			(new ExportHandler(template)).write(Globals.getPCList(), bw);
//			Party.print(template, bw);
		}
		else
		{
			SettingsHandler.setSelectedCharacterPDFOutputSheet(template.getAbsolutePath());
			final PlayerCharacter aPC = (PlayerCharacter) Globals.getPCList().get(pcIndex);
			final PlayerCharacter oldPC = Globals.getCurrentPC();
			Globals.setCurrentPC(aPC);
			(new ExportHandler(template)).write(aPC, bw);
//  			aPC.print(template, bw);
			Globals.setCurrentPC(oldPC);
		}
		bw.close();
	}

	/**
	 * Dekker500
	 * Feb 1, 2003
	 *
	 * If user selected an XSLT template, perform initial export to base XML file.
	 *
	 * In XML file mode, there is no such thing as party mode.
	 * Party mode simply means that the output file will contain
	 * each character, one after the other.  The XSLT sheet will extract the
	 * individual characters as required.
	 */
	private void printToXMLFile(File outFile, int pcIndex) throws IOException
	{	// In XML file mode, there is no such thing as party mode.
		// Party mode simply means that the output file will contain
		// each character, one after the other

		//final BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
		final File template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator + "base.xml");
		SettingsHandler.setSelectedCharacterPDFOutputSheet(template.getAbsolutePath());
		final PlayerCharacter oldPC = Globals.getCurrentPC();
		PlayerCharacter aPC;
		if (partyMode)
		{
			for (int i = 0; i < Globals.getPCList().size(); i++)
			{
				aPC = (PlayerCharacter) Globals.getPCList().get(i);
				Globals.setCurrentPC(aPC);
				(new ExportHandler(template)).write(aPC, bw);
			}
		}
		else
		{
			aPC = (PlayerCharacter) Globals.getPCList().get(pcIndex);
			Globals.setCurrentPC(aPC);
			(new ExportHandler(template)).write(aPC, bw);
		}
		Globals.setCurrentPC(oldPC);

		bw.close();
	}

	private static class PCListModel extends AbstractListModel
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
			csheetFilter.setDirFilter("pdf");
			csheetFilter.setIgnoreExtension(".");
			List aList = csheetFilter.getAccepted();
			cSheets = new String[aList.size()];
			for (int i = 0; i < aList.size(); i++)
			{
				cSheets[i] = aList.get(i).toString();
			}
			psheetFilter.setDirFilter("pdf");
			psheetFilter.setIgnoreExtension(".");
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
				if (templateList.getSelectedValue() == null)
				{
					GuiFacade.showMessageDialog(null, PropertyFactory.getString("in_exportNoTemplate"), "PCGen", GuiFacade.ERROR_MESSAGE);
				}
				else
				{
					(new Thread(new Runnable()
					{
						public void run()
						{
							export();
						}
					})).start();
				}
			}
			else if (src.equals(printButton))
			{
				if (templateList.getSelectedValue() == null)
				{
					GuiFacade.showMessageDialog(null, PropertyFactory.getString("in_printNoTemplate"), "PCGen", GuiFacade.ERROR_MESSAGE);
				}
				else
				{
					(new Thread(new Runnable()
					{
						public void run()
						{
							print();
						}
					})).start();
				}
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
