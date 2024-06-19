/*
 * PToolBar.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 */
package pcgen.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.*;
import pcgen.core.Globals;
import pcgen.gui.filter.FilterDialogFactory;
import pcgen.gui.filter.Filterable;

/**
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
public class PToolBar extends JToolBar
{
	// need this to get access from FilterDialog
	private static PToolBar currentInstance = null;

	private FilterComponentListener fcl;

	private JButton clearFilters;
	private JButton customFilters;
	private JButton editorFilters;
	private FilterToolTipButton openFilters;

	JButton newButton;
	JButton openButton;
	JButton saveButton;
	JButton printPreviewButton;
	JButton printButton;

	private static String in_newChaTip;
	private static String in_openChaTip;
	private static String in_saveChaTip;
	private static String in_printPreviewTip;
	private static String in_printChaTip;
	private static String in_removeFilters;
	private static String in_customFilters;
	private static String in_compoundFilters;
	private static String in_filterIcon;
	/**
	 * Resrouce bundles
	 */
	static
	{
		ResourceBundle toolBarProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			toolBarProperties = ResourceBundle.getBundle("pcgen/gui/properities/LanguageBundle", currentLocale);
			in_newChaTip = toolBarProperties.getString("in_newChaTip");
			in_openChaTip = toolBarProperties.getString("in_openChaTip");
			in_saveChaTip = toolBarProperties.getString("in_saveChaTip");
			in_printPreviewTip = toolBarProperties.getString("in_printPreviewTip");
			in_printChaTip = toolBarProperties.getString("in_printChaTip");
			in_removeFilters = toolBarProperties.getString("in_removeFilters");
			in_customFilters = toolBarProperties.getString("in_customFilters");
			in_compoundFilters = toolBarProperties.getString("in_compoundFilters");
			in_filterIcon = toolBarProperties.getString("in_filterIcon");
		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			toolBarProperties = null;
		}
	}

	/**
	 * Constructor
	 */
	public PToolBar(PCGen_Frame1 main)
	{
		init(main);
		setFloatable(true);
		fcl = new FilterComponentListener();
	}

	private void init(PCGen_Frame1 main)
	{
		final Dimension buttonDimension = new Dimension(24, 24);
		final FrameActionListener frameActionListener = new FrameActionListener(main);

		newButton = Utility.createButton(main.frameActionListener.newActionListener, "file.new", in_newChaTip, "New16.gif", false);
		add(newButton);

		openButton = Utility.createButton(main.frameActionListener.openActionListener, "file.open", in_openChaTip, "Open16.gif", true);
		add(openButton);

		saveButton = Utility.createButton(main.frameActionListener.saveActionListener, "file.save", in_saveChaTip, "Save16.gif", false);
		add(saveButton);

		addSeparator();

		printPreviewButton = Utility.createButton(main.frameActionListener.printPreviewActionListener, "file.printpreview", in_printPreviewTip, "PrintPreview16.gif", false);
		add(printPreviewButton);

		printButton = Utility.createButton(main.frameActionListener.printActionListener, "file.print", in_printChaTip, "Print16.gif", false);
		add(printButton);

		addSeparator();

		//first button
		openFilters = new FilterToolTipButton(new ImageIcon(getClass().getResource("resource/Zoom16.gif")),
			new ImageIcon(getClass().getResource("resource/ZoomHighlightBlue16.gif")));
		openFilters.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FilterDialogFactory.showHideFilterSelectDialog();
			}
		});
		add(openFilters);

		//second button
		clearFilters = Utility.createButton(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FilterDialogFactory.clearSelectedFiltersForSelectedFilterable();
			}
		},
			"filter.clear",
			in_removeFilters,
			"RemoveZoom16.gif",
			false);
		add(clearFilters);

		// third button
		customFilters = Utility.createButton(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FilterDialogFactory.showHideFilterCustomDialog();
			}
		},
			"filter.custom",
			in_customFilters,
			"CustomZoom16.gif",
			false);
		add(customFilters);

		// fourth button
		editorFilters = Utility.createButton(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FilterDialogFactory.showHideFilterEditorDialog();
			}
		},
			"filter.editor",
			in_compoundFilters,
			"EditZoom16.gif",
			false);
		add(editorFilters);
	}

	/**
	 * switches the icon for the open filter button:
	 * set to "There are selected filters for the selected tab" icon
	 *
	 * author: Thomas Behr 11-02-02
	 */
	public void setFilterActive()
	{
		openFilters.setActiveIcon();
	}

	/**
	 * switches the icon for the open filter button:
	 * set to "There are no selected filters for the selected tab" icon
	 *
	 * author: Thomas Behr 11-02-02
	 */
	public void setFilterInactive()
	{
		openFilters.setInactiveIcon();
	}

	public void filterButtonsSetEnabled(boolean b)
	{
		openFilters.setEnabled(b);
		clearFilters.setEnabled(b);
		customFilters.setEnabled(b);
		editorFilters.setEnabled(b);
	}

	/**
	 * switches the icon for the open filter button:
	 * set to "There are no selected filters for the selected tab" icon
	 * Then the open filter and the clear filter button are disabled
	 * We use this for tabs like "Misc" which are no instance of Filterable
	 *
	 * author: Thomas Behr 11-02-02
	 */
	public void disableFilterButtons()
	{
		setFilterInactive();
		filterButtonsSetEnabled(false);
	}

	/**
	 * @return the component event listener associated to this PToolBar instance
	 */
	protected ComponentListener getComponentListener()
	{
		return fcl;
	}

	/**
	 * create a new PToolBar and registers it as current instance
	 *
	 * @return the PToolBar instance
	 *
	 * author: Thomas Behr 11-02-02
	 */
	public static PToolBar createToolBar(PCGen_Frame1 main)
	{
		if (currentInstance == null)
		{
			currentInstance = new PToolBar(main);
		}

		return currentInstance;
	}

	/**
	 * this method allows us to get the current instance of PToolBar
	 * we will use this in FilterDialog to get the appropriate instance
	 *
	 * @return the currently set PToolBar instance
	 *
	 * author: Thomas Behr 11-02-02
	 */
	public static PToolBar getCurrentInstance()
	{
		return currentInstance;
	}

	/**
	 * this method allows us to set the current instance of PToolBar
	 * we will use this in CharacterInfo to set the appropriate instance
	 *
	 * @param aInstance - the PToolBar instance
	 *
	 * author: Thomas Behr 11-02-02
	 */
	protected static void setCurrentInstance(PToolBar aInstance)
	{
		currentInstance = aInstance;
	}

	private class FilterComponentListener extends ComponentAdapter
	{
		public void componentShown(ComponentEvent e)
		{
			Component c = e.getComponent();
			if (c instanceof Filterable)
			{
				handleFilterableShown((Filterable)c);
			}
			else if (c instanceof CharacterInfo)
			{
				handleFilterableShown(((CharacterInfo)c).getSelectedFilterable());
			}
			else
			{
				disableFilterButtons();
			}
		}

		private void handleFilterableShown(Filterable f)
		{
			if ((f != null) && (f.getSelectionMode() != FilterDialogFactory.DISABLED_MODE))
			{
				filterButtonsSetEnabled(true);
				if (f.getSelectedFilters().size() > 0)
				{
					setFilterActive();
				}
				else
				{
					setFilterInactive();
				}
			}
			else
			{
				disableFilterButtons();
			}
		}
	}

	private class FilterIconButton extends JButton
	{
		private Icon activeIcon;
		private Icon inactiveIcon;

		public FilterIconButton(Icon inactiveIcon, Icon activeIcon)
		{
			super(inactiveIcon);
			this.activeIcon = activeIcon;
			this.inactiveIcon = inactiveIcon;
			this.setToolTipText(in_filterIcon);

			// Work around old JDK bug on Windows
			this.setMargin(new java.awt.Insets(0, 0, 0, 0));

		}

		public void setActiveIcon()
		{
			setIcon(activeIcon);
		}

		public void setInactiveIcon()
		{
			setIcon(inactiveIcon);

		}
	}

	private class FilterToolTipButton extends FilterIconButton
	{
		public FilterToolTipButton(Icon inactiveIcon, Icon activeIcon)
		{
			super(inactiveIcon, activeIcon);
		}

		public String getToolTipText()
		{
			return FilterDialogFactory.getSelectedFiltersToolTipText();
		}
	}
}
