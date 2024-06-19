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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import pcgen.gui.filter.FilterDialogFactory;
import pcgen.gui.filter.Filterable;
import pcgen.util.PropertyFactory;

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

	JButton helpButton;
	public static JFrame helpFrame = new JFrame("Help");
	private static JEditorPane helpPane = new JEditorPane("text/html", "");

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
		final FrameActionListener frameActionListener = new FrameActionListener(main);

		newButton = Utility.createButton(main.frameActionListener.newActionListener, "file.new", PropertyFactory.getString("in_newChaTip"), "New16.gif", false);
		add(newButton);

		openButton = Utility.createButton(main.frameActionListener.openActionListener, "file.open", PropertyFactory.getString("in_openChaTip"), "Open16.gif", true);
		add(openButton);

		saveButton = Utility.createButton(main.frameActionListener.saveActionListener, "file.save", PropertyFactory.getString("in_saveChaTip"), "Save16.gif", false);
		add(saveButton);

		addSeparator();

		printPreviewButton = Utility.createButton(main.frameActionListener.printPreviewActionListener, "file.printpreview", PropertyFactory.getString("in_printPreviewTip"), "PrintPreview16.gif", false);
		add(printPreviewButton);

		printButton = Utility.createButton(main.frameActionListener.printActionListener, "file.print", PropertyFactory.getString("in_printChaTip"), "Print16.gif", false);
		add(printButton);

		addSeparator();

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

		clearFilters = Utility.createButton(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FilterDialogFactory.clearSelectedFiltersForSelectedFilterable();
			}
		},
			"filter.clear",
			PropertyFactory.getString("in_removeFilters"),
			"RemoveZoom16.gif",
			false);
		add(clearFilters);

		customFilters = Utility.createButton(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FilterDialogFactory.showHideFilterCustomDialog();
			}
		},
			"filter.custom",
			PropertyFactory.getString("in_customFilters"),
			"CustomZoom16.gif",
			false);
		add(customFilters);

		editorFilters = Utility.createButton(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FilterDialogFactory.showHideFilterEditorDialog();
			}
		},
			"filter.editor",
			PropertyFactory.getString("in_compoundFilters"),
			"EditZoom16.gif",
			false);
		add(editorFilters);

		addSeparator();

		helpButton = Utility.createButton(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				displayHelpPanel(true);
			}
		},
			"help.context",
			PropertyFactory.getString("in_contextHelpTip"),
			"ContextualHelp16.gif",
			true);
		add(helpButton);

		helpFrame.setSize(new Dimension(400, 400));
		helpFrame.getContentPane().add(new JScrollPane(helpPane));
		helpPane.setEditable(false);
	}

	public static void displayHelpPanel(boolean forceDisplay)
	{
		int currentPanel = PCGen_Frame1.getBaseTabbedPanel().getSelectedIndex();
		try
		{
			File aFile;
			JTabbedPane aPane;
			switch (currentPanel)
			{
				default:
					aFile = new File("doc" + File.separator + "SourceMaterialTab.htm");
					break;
				case 1:
					aPane = (JTabbedPane)PCGen_Frame1.getBaseTabbedPanel().getComponent(1);
					switch (aPane.getSelectedIndex())
					{
						default:
							aFile = new File("doc" + File.separator + "CombatTab.htm");
							break;
						case 1:
							aFile = new File("doc" + File.separator + "QuickStatsTab.htm");
							break;
						case 2:
							aFile = new File("doc" + File.separator + "XPTrackerTab.htm");
							break;
						case 3:
							aFile = new File("doc" + File.separator + "NotesTab.htm");
							break;
					}
					break;
				case 2:
					aPane = (JTabbedPane)PCGen_Frame1.getCharacterTab().getComponent(0);
					switch (aPane.getSelectedIndex())
					{
						default:
							aFile = new File("doc" + File.separator + "RaceTab.htm");
							break;
						case 1:
							aFile = new File("doc" + File.separator + "AbilitiesTab.htm");
							break;
						case 2:
							aFile = new File("doc" + File.separator + "ClassesTab.htm");
							break;
						case 3:
							aFile = new File("doc" + File.separator + "SkillsTab.htm");
							break;
						case 4:
							aFile = new File("doc" + File.separator + "FeatsTab.htm");
							break;
						case 5:
							aFile = new File("doc" + File.separator + "DomainsTab.htm");
							break;
						case 6:
							aFile = new File("doc" + File.separator + "SpellsTab.htm");
							break;
						case 7:
							aFile = new File("doc" + File.separator + "InventoryTab.htm");
							break;
						case 8:
							aFile = new File("doc" + File.separator + "DescriptionTab.htm");
							break;
						case 9:
							aFile = new File("doc" + File.separator + "MiscTab.htm");
							break;
						case 10:
							aFile = new File("doc" + File.separator + "PreviewTab.htm");
							break;
						case 11:
							aFile = new File("doc" + File.separator + "NotesTab.htm");
							break;
						case 12:
							aFile = new File("doc" + File.separator + "EquippingTab.htm");
							break;
					}
					break;
			}
			FileInputStream fis = new FileInputStream(aFile);
			int length = (int)aFile.length();
			byte[] inputLine = new byte[length];
			fis.read(inputLine, 0, length);
			String aString = new String(inputLine);
			int i = aString.indexOf("<img ");
			if (i > 0)
			{
				int k = aString.substring(i).indexOf(">");
				helpPane.setText(aString.substring(0, i) + " " + aString.substring(i + k + 1));
			}
			else
			{
				URL aurl = aFile.toURL();
				helpPane.setPage(aurl);
			}
		}
		catch (IOException exc)
		{
			System.err.println("huh " + exc.getMessage());
		}
		if (helpFrame.isVisible() || forceDisplay)
			helpFrame.show();
		helpPane.setCaretPosition(0);
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
			PToolBar.displayHelpPanel(false);
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
			Utility.setDescription(this, PropertyFactory.getString("in_filterIcon"));

			// Work around old JDK bug on Windows
			this.setMargin(new Insets(0, 0, 0, 0));

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
