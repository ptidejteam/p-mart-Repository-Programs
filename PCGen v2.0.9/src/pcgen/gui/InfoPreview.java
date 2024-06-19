/*
 * CharacterInfo.java
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
 * Created on May 14, 2001, 16:35 PM
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import pcgen.core.Globals;
import pcgen.core.Party;

/**
 *  <code>InfoPreview</code> creates a new tabbed panel.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */
public class InfoPreview extends JPanel
{
	BorderLayout infoPreviewLayout = new BorderLayout();
	JScrollPane previewCenter = new JScrollPane();
	JPanel previewBottom = new JPanel();
	JButton btnBrowseTemplate = new JButton();
	JTextField txtSelectedTemplate = new JTextField();
	BorderLayout previewBottomLayout = new BorderLayout();
	JPanel previewBottomCenter = new JPanel();
	JButton btnExport = new JButton();
	GridLayout previewBottomCenterLayout = new GridLayout();
	JTextField txtExportTo = new JTextField();
	JButton btnExportTo = new JButton();
	JEditorPane templateViewer = new JEditorPane();
	final JFileChooser fc = new JFileChooser();
	final CsheetFilter csheetFilter = new CsheetFilter();
	final PsheetFilter psheetFilter = new PsheetFilter();
	JButton btnPreview = new JButton();
	JButton btnPreviewInBrowser = new JButton();

	/**
	 *  Constructor for the InfoPreview object
	 *
	 * @since
	 */
	public InfoPreview()
	{
		setLayout(infoPreviewLayout);

		previewBottom.setLayout(previewBottomLayout);

		txtSelectedTemplate.setPreferredSize(new Dimension(150, 21));
		txtSelectedTemplate.setEditable(false);
		txtSelectedTemplate.setToolTipText("Non-editable, press button to change.");
		txtSelectedTemplate.setBackground(Color.lightGray);

		btnBrowseTemplate.setText("Select a template...");
		btnBrowseTemplate.setToolTipText("Select a charactersheet template");
		btnBrowseTemplate.addActionListener(
			new java.awt.event.ActionListener()
			{
				/**
				 *  Anonymous event handler
				 *
				 * @param  e  The ActionEvent
				 * @since
				 */
				public void actionPerformed(ActionEvent e)
				{
					btnBrowseTemplate_actionPerformed(e);
				}
			});

		btnExportTo.setText("Export To...");
		btnExportTo.setToolTipText("Select file to export to");
		btnExportTo.addActionListener(
			new java.awt.event.ActionListener()
			{
				/**
				 *  Anonymous event handler
				 *
				 * @param  e  The ActionEvent
				 * @since
				 */
				public void actionPerformed(ActionEvent e)
				{
					btnBrowse_actionPerformed(e);
				}
			});

		txtExportTo.setPreferredSize(new Dimension(150, 21));
		txtExportTo.setEditable(false);
		txtExportTo.setToolTipText("Non-editable, press button to change.");
		txtExportTo.setBackground(Color.lightGray);

		btnPreview.setText("Preview");
		btnPreview.setToolTipText("Previews in tab");
		btnPreview.addActionListener(
			new java.awt.event.ActionListener()
			{
				/**
				 *  Anonymous event handler
				 *
				 * @param  e  The ActionEvent
				 * @since
				 */
				public void actionPerformed(ActionEvent e)
				{
					btnPreview_actionPerformed(e);
				}
			});

		btnPreviewInBrowser.setText("Preview in browser");
		btnPreviewInBrowser.setToolTipText("Launches a browser and preview in that");
		btnPreviewInBrowser.addActionListener(
			new java.awt.event.ActionListener()
			{
				/**
				 *  Anonymous event handler
				 *
				 * @param  e  The ActionEvent
				 * @since
				 */
				public void actionPerformed(ActionEvent e)
				{
					btnPreviewInBrowser_actionPerformed(e);
				}
			});

		btnExport.setText("Export");
		btnExport.setToolTipText("Exports to file");
		btnExport.addActionListener(
			new java.awt.event.ActionListener()
			{
				/**
				 *  Anonymous event handler
				 *
				 * @param  e  The ActionEvent
				 * @since
				 */
				public void actionPerformed(ActionEvent e)
				{
					btnExport_actionPerformed(e);
				}
			});
		previewBottomCenter.setLayout(previewBottomCenterLayout);
		previewBottomCenterLayout.setHgap(10);
		previewCenter.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		previewCenter.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		previewCenter.setDoubleBuffered(true);
		templateViewer.setDoubleBuffered(true);
		templateViewer.setEditable(false);
		templateViewer.setToolTipText("If it looks bad, try Preview in browser instead. This previewer doesn't work too well yet. Sorry...");
		add(previewCenter, BorderLayout.CENTER);
		previewCenter.getViewport().add(templateViewer, null);
		add(previewBottom, BorderLayout.SOUTH);
		previewBottom.add(previewBottomCenter, BorderLayout.CENTER);
		previewBottomCenter.add(btnBrowseTemplate, null);
		previewBottomCenter.add(txtSelectedTemplate, null);
		previewBottomCenter.add(btnExportTo, null);
		previewBottomCenter.add(txtExportTo, null);
		previewBottomCenter.add(btnPreview, null);
		previewBottomCenter.add(btnPreviewInBrowser, null);
		previewBottomCenter.add(btnExport, null);

		HTMLEditorKit htmlEditor = new HTMLEditorKit();
		templateViewer.setEditorKit(htmlEditor);

		addComponentListener(new java.awt.event.ComponentAdapter()
		{
			public void componentShown(java.awt.event.ComponentEvent evt)
			{
				// run when the panel becomes visible
				requestDefaultFocus();
				String templateName = Globals.getSelectedTemplate();
				updateExportFilename(templateName);
				txtSelectedTemplate.setText(templateName);
			}

		});

		//If the template selected at start exists, enable export &c.
		if (new File(Globals.getSelectedTemplate()).exists())
		{
			btnPreview.setEnabled(true);
			btnPreviewInBrowser.setEnabled(true);
			btnExport.setEnabled(true);
		}
		else
		{
			btnPreview.setEnabled(false);
			btnPreviewInBrowser.setEnabled(false);
			btnExport.setEnabled(false);
		}
	}

	private void updateExportFilename(String templateName)
	{
		String extension;
		if (templateName != null && !templateName.equals("") && templateName.lastIndexOf('.') >= 0)
		{
			extension = templateName.substring(templateName.lastIndexOf('.'));
			txtExportTo.setText(pcgen.core.Globals.getHtmlOutputPath() + java.io.File.separator + pcgen.core.Globals.currentPC.name() + extension);
		}
	}

	/**
	 *  Select a filename for the charactersheet. Accepts anything...
	 *
	 * @param  e  The ActionEvent
	 * @since
	 */
	void btnBrowse_actionPerformed(ActionEvent e)
	{
		fc.setFileFilter(fc.getAcceptAllFileFilter());
		fc.setCurrentDirectory(Globals.getHtmlOutputPath());
		String fileName = txtExportTo.getText();
		if (fileName.length() > 0)
		{
			fc.setSelectedFile(new File(Globals.getHtmlOutputPath() + File.separator + fileName));
		}

		int returnVal = fc.showSaveDialog(InfoPreview.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			Globals.setHtmlOutputPath(file.getParentFile());
			if (file.isDirectory())
			{
				JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a file.");
				return;
			}

			txtExportTo.setText(file.getAbsolutePath());
		}
	}

	/**
	 *  Select a template for 'printing' the character.
	 *
	 * @param  e  The ActionEvent
	 * @since
	 */
	void btnBrowseTemplate_actionPerformed(ActionEvent e)
	{
		fc.setCurrentDirectory(Globals.getTemplatePath());
		fc.setFileFilter(psheetFilter);
		fc.addChoosableFileFilter(csheetFilter);
		int returnVal = fc.showOpenDialog(InfoPreview.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			Globals.setSelectedTemplate(fc.getSelectedFile().getPath());
			Globals.setTemplatePath(fc.getSelectedFile().getParentFile());
			txtSelectedTemplate.setText(Globals.getSelectedTemplate());
			updateExportFilename(Globals.getSelectedTemplate());
		}
		btnExport.setEnabled(true);
		btnPreview.setEnabled(true);
		btnPreviewInBrowser.setEnabled(true);
	}

	/**
	 *  Save the character to a html file.
	 *
	 * @param  e  The ActionEvent
	 * @since
	 */
	void btnExport_actionPerformed(ActionEvent e)
	{
		print(this, txtExportTo.getText());

	}

	/**
	 * Preview the character in the application's html viewer.
	 * Not all that likely to look good...
	 *
	 * @param  e  The ActionEvent
	 * @since
	 */
	void btnPreview_actionPerformed(ActionEvent e)
	{
		preview(this, Globals.getHtmlOutputPath() + File.separator + "currentPC.htm");
	}


	/**
	 * Preview the character in an external browser
	 *
	 * @param  e  The ActionEvent
	 * @since
	 */
	void btnPreviewInBrowser_actionPerformed(ActionEvent e)
	{
		previewInBrowser(this, txtExportTo.getText());
	}


	private void print(JComponent parent, String aFileName)
	{
		if (aFileName.length() < 1)
		{
			JOptionPane.showMessageDialog(null, "You must set a filename.");
			return;
		}

		try
		{
			File outFile = new File(aFileName);

			if (outFile.isDirectory())
			{
				JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a file.");
				return;
			}

			if (outFile.exists())
			{
				int reallyClose = JOptionPane.showConfirmDialog(parent,
					"The file " + outFile.getName() + " already exists, are you sure you want to overwrite it?",
					"Confirm overwriting " + outFile.getName(),
					JOptionPane.YES_NO_OPTION);
				if (reallyClose != JOptionPane.YES_OPTION)
				{
					return;
				}
			}

			printToFile(outFile);

		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "Could not export character. Try another filename.");
			ex.printStackTrace();
		}
	}

	private void printToFile(File outFile) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		File template = new File(Globals.getSelectedTemplate());
		String name = template.getName().toLowerCase();
		if (name.startsWith(Globals.s_CHARACTER_TEMPLATE_START))
		{
			Globals.currentPC.print(template, bw);
		}
		else if (name.startsWith(Globals.s_PARTY_TEMPLATE_START))
		{
			Party.print(template, bw);
		}
		else
		{
			throw new IOException(name + " is not a valid template file name.");
		}
		bw.close();
	}

	private void preview(JComponent parent, String aFileName)
	{
		try
		{
			File outFile = new File(aFileName);
			printToFile(outFile);

			BufferedReader br = new BufferedReader(new FileReader(outFile));
			templateViewer.read(br, new HTMLDocument());
			br.close();
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "Could not preview file. It probably looks ok in a browser though.");
			ex.printStackTrace();
		}
	}

	private void previewInBrowser(JComponent parent, String aFileName)
	{
		try
		{
			File outFile = new File(aFileName);
			printToFile(outFile);
			java.net.URL url = new java.net.URL("file://" + aFileName);
			try
			{
				BrowserLauncher.openURL(url.toString());
			}
			catch (IOException ioe)
			{
				System.out.println("IOException: " + ioe);
			}
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "Could not preview file in external browser. Sorry...");
			ex.printStackTrace();
		}
	}


}

