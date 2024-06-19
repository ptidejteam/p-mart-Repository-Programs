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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.Party;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

/**
 *  <code>InfoPreview</code> creates a new tabbed panel.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */
class InfoPreview extends JPanel
{
	private BorderLayout infoPreviewLayout = new BorderLayout();
	private JScrollPane previewCenter = new JScrollPane();
	private JPanel previewBottom = new JPanel();
	private JButton btnBrowseTemplate = new JButton();
	private JTextField txtSelectedTemplate = new JTextField();
	private BorderLayout previewBottomLayout = new BorderLayout();
	private JPanel previewBottomCenter = new JPanel();
	private GridLayout previewBottomCenterLayout = new GridLayout();
	private JEditorPane templateViewer = new JEditorPane();
	private final CsheetFilter csheetFilter = new CsheetFilter();
	private final PsheetFilter psheetFilter = new PsheetFilter();
	private JButton btnPreview = new JButton();
	private JButton btnPreviewInBrowser = new JButton();

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
		Utility.setDescription(txtSelectedTemplate, "Non-editable, press button to change.");
		txtSelectedTemplate.setBackground(Color.lightGray);

		btnBrowseTemplate.setText("Select a template...");
		Utility.setDescription(btnBrowseTemplate, "Select a charactersheet template");
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

		btnPreview.setText("Preview");
		Utility.setDescription(btnPreview, "Previews in tab");
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
		Utility.setDescription(btnPreviewInBrowser, "Launches a browser and preview in that");
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

		previewBottomCenter.setLayout(previewBottomCenterLayout);
		previewBottomCenterLayout.setHgap(10);
		previewCenter.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		previewCenter.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		previewCenter.setDoubleBuffered(true);
		templateViewer.setDoubleBuffered(true);
		templateViewer.setEditable(false);
		Utility.setDescription(templateViewer, "If it looks bad, try Preview in browser instead. This previewer doesn't work too well yet. Sorry...");
		add(previewCenter, BorderLayout.CENTER);
		previewCenter.getViewport().add(templateViewer, null);
		add(previewBottom, BorderLayout.SOUTH);
		previewBottom.add(previewBottomCenter, BorderLayout.CENTER);
		previewBottomCenter.add(btnBrowseTemplate, null);
		previewBottomCenter.add(txtSelectedTemplate, null);
		previewBottomCenter.add(btnPreview, null);
		previewBottomCenter.add(btnPreviewInBrowser, null);

		HTMLEditorKit htmlEditor = new HTMLEditorKit();
		templateViewer.setEditorKit(htmlEditor);

		addComponentListener(new java.awt.event.ComponentAdapter()
		{
			public void componentShown(java.awt.event.ComponentEvent evt)
			{
				// run when the panel becomes visible
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(true);
				aPC.setAutomaticFeatsStable(true);
				aPC.setVirtualFeatsStable(true);

				requestFocus();
				final String templateName = SettingsHandler.getSelectedTemplate();
				txtSelectedTemplate.setText(templateName);
			}

		});

		//If the template selected at start exists, enable preview
		if (new File(SettingsHandler.getSelectedTemplate()).exists())
		{
			btnPreview.setEnabled(true);
			btnPreviewInBrowser.setEnabled(true);
		}
		else
		{
			btnPreview.setEnabled(false);
			btnPreviewInBrowser.setEnabled(false);
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
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(SettingsHandler.getTemplatePath());
		fc.setFileFilter(psheetFilter);
		fc.addChoosableFileFilter(csheetFilter);
		int returnVal = fc.showOpenDialog(InfoPreview.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			SettingsHandler.setSelectedTemplate(fc.getSelectedFile().getPath());
			SettingsHandler.setTemplatePath(fc.getSelectedFile().getParentFile());
			txtSelectedTemplate.setText(SettingsHandler.getSelectedTemplate());
		}
		btnPreview.setEnabled(true);
		btnPreviewInBrowser.setEnabled(true);
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

		previewInternal();

	}

	/**
	 * Preview the character in an external browser
	 *
	 * @param  e  The ActionEvent
	 * @since
	 */
	void btnPreviewInBrowser_actionPerformed(ActionEvent e)
	{

		previewInBrowser();

	}

	/**
	 * Creates a temporary preview file for display.
	 */
	private static File getTempPreviewFile()
	{

		final String template = SettingsHandler.getSelectedTemplate();

		// include . in extension
		String extension = template.substring(template.lastIndexOf('.'));

		File tempFile = null;

		try
		{

			// create a temporary file to view the character output
			tempFile = File.createTempFile("currentPC", extension, SettingsHandler.getTempPath());

		}
		catch (IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Could not create temporary preview file.", "PCGen", JOptionPane.ERROR_MESSAGE);
			ioe.printStackTrace();
		}

		return tempFile;

	}


	/*
	private void print(JComponent parent, String aFileName)
	{
		if (aFileName.length() < 1)
		{
			JOptionPane.showMessageDialog(null, "You must set a filename.", "PCGen", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try
		{
			File outFile = new File(aFileName);

			if (outFile.isDirectory())
			{
				JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a file.", "PCGen", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(null, "Could not export character. Try another filename.", "PCGen", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
  */


	/**
	 * Previews the character internally
	 */
	private void previewInternal()
	{

		try
		{

			StringWriter sw = new StringWriter();
			printToWriter(sw);
			BufferedReader br = new BufferedReader(new StringReader(sw.toString()));
			templateViewer.read(br, new HTMLDocument());
			br.close();
			sw.close();

		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "Could not preview file. It probably looks ok in a browser though.", "PCGen", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}

	}

	/**
	 * Previews the character internally
	 */
	public static void previewInBrowser()
	{

		File outFile = getTempPreviewFile();

		// ensure we've got something
		if (outFile == null)
		{

			// message will have been displayed already
			return;

		}

		try
		{

			FileWriter w = new FileWriter(outFile);
			printToWriter(w);
			w.close();
			final String osName = System.getProperty("os.name");
			//
			// Windows tends to lock up or not actually display anything unless we've specified
			// a default browser, so at least make the user aware that (s)he needs one. If they
			// don't pick one and it doesn't work, at least they might know enough to try selecting
			// one the next time.
			//
			if (osName.startsWith("Windows ") && (SettingsHandler.getBrowserPath() == null))
			{
				Utility.selectDefaultBrowser(null);
			}

			if (osName.startsWith("Mac OS"))
				BrowserLauncher.openURL(outFile.toString());
			else
			{
				java.net.URL url = outFile.toURL();
				BrowserLauncher.openURL(url.toString());
			}
		}
		catch (Exception ex)
		{

			JOptionPane.showMessageDialog(null, "Could not preview file in external browser. Sorry...", "PCGen", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();

		}

	}

	/**
	 * Prints the character or party details to the writer specified.
	 *
	 * @param w  The writer to print the data to.
	 * @throws IOException  If any problems occur in writing the data
	 */
	private static void printToWriter(Writer w) throws IOException
	{

		BufferedWriter bw = new BufferedWriter(w);
		File template = new File(SettingsHandler.getSelectedTemplate());
		String name = template.getName().toLowerCase();
		if (name.startsWith(Constants.s_CHARACTER_TEMPLATE_START))
		{
			(new pcgen.io.ExportHandler(template)).write(Globals.getCurrentPC(), bw);
//  			Globals.getCurrentPC().print(template, bw);
		}
		else if (name.startsWith(Constants.s_PARTY_TEMPLATE_START))
		{
			Party.print(template, bw);
		}
		else
		{
			throw new IOException(name + " is not a valid template file name.");
		}
		bw.close();
	}

}

