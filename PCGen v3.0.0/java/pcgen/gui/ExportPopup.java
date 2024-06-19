/*
 * ExportPopup.java
 * Copyright 2002 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
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
 * Created on February 14th, 2002.
 */

package pcgen.gui;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JTabbedPane;
import pcgen.core.Globals;

/**
 * Export popup dialog.  The real work goes on in the panel.
 *
 * @author B. K. Oxley (binkley) <a href="mailto:binkley@bigfoot.com">&lt;binkley@bigfoot.com&gt;</a>
 * @version    $Revision: 1.1 $
 */
public class ExportPopup extends PCGenPopup
{
	MainExport mainExport = null;
	JTabbedPane baseTabbedPanel = null;

	private static String in_exportPCParty;
	/**
	 * Resrouce bundles
	 */
	static
	{
		ResourceBundle pdfProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			pdfProperties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
			in_exportPCParty = pdfProperties.getString("in_exportPCParty");
		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			pdfProperties = null;
		}
	}


	public ExportPopup(JTabbedPane aPanel)
	{
		super(in_exportPCParty);
		mainExport = new MainExport();
		setPanel(mainExport);
		baseTabbedPanel = aPanel;
	}

	public void refreshTemplates()
	{
		if (mainExport != null)
			mainExport.refreshTemplates();
	}

	public void setCurrentPCSelectionByTab()
	{
		if (mainExport != null)
		{
			mainExport.setCurrentPCSelection(baseTabbedPanel.getSelectedIndex());
			pack();
			setVisible(true);
		}
	}
}
