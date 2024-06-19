/*
 * JOpenRecentMenu.java
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
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
 * Created on February 7th, 2002.
 */

package pcgen.gui.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import pcgen.core.Constants;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 *  <code>JOpenRecentMenu</code> extends JMenu with one special coded
 *  for opening recent files
 *
 * @author     B. K. Oxley (binkley) <a href="mailto:binkley@bigfoot.com">&ltbinkley@bigfoot.com&gt;</a>
 * @version    $Revision: 1.1 $
 */

public class JOpenRecentMenu extends JMenu
{

	static final long serialVersionUID = -1385714650728604115L;
	private static final class FixedArrayList extends ArrayList
	{
		// A rather incomplement implementation, I admit.  XXX --bko
		private int max = 0;

		FixedArrayList()
		{
			this(Constants.MAX_OPEN_RECENT_ENTRIES);
		}

		FixedArrayList(int max)
		{
			super(max);
			this.max = max;
		}

		public boolean add(Object element)
		{
			//
			// Insert to the top and remove excess from the bottom
			//
			super.add(0, element);
			int size = FixedArrayList.this.size();
			while (size > max)
			{
				this.remove(--size);
			}
			return true;
		}
	}

	/**
	 * <code>OpenRecentCallback</code> is an
	 * <code>ActionListener</code> for menu items in the recently
	 * opened menu.
	 */
	public interface OpenRecentCallback
	{
		/**
		 * Handle the reopening of a recently opened file.
		 *
		 * @param e ActionEvent the menu selection event
		 * @param file File the recently opened file
		 */
		void openRecentPerformed(ActionEvent e, File file);
	}

	private final class OpenRecentActionListener implements ActionListener
	{
		private JOpenRecentMenu menu = null;
		private OpenRecentEntry entry = null;
		private OpenRecentCallback cb = null;

		OpenRecentActionListener(JOpenRecentMenu aMenu, OpenRecentEntry anEntry, OpenRecentCallback aCb)
		{
			menu = aMenu;
			entry = anEntry;
			cb = aCb;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (entry.file.exists())
			{
				menu.doAddUpdateEntry(entry); // move to top
				cb.openRecentPerformed(e, entry.file);
			}
			else
			{
				menu.doRemoveUpdateEntry(entry);
			}
		}
	}

	private OpenRecentCallback cb = null;
	private FixedArrayList entries = null;

	public final String[] getEntriesAsStrings()
	{
		ArrayList strings = new ArrayList();

		if (entries != null)
		{
			for (int i = entries.size() - 1; i >= 0; --i)
			{
				final OpenRecentEntry entry = (OpenRecentEntry) entries.get(i);
				strings.add(entry.displayAs);
				strings.add(entry.file.getAbsolutePath());
			}
		}

		return (String[]) strings.toArray(new String[0]);
	}

	public final void setEntriesAsStrings(String[] strings)
	{
		for (int i = 0; i < strings.length; i += 2)
		{
			try
			{
				add(strings[i], new File(strings[i + 1]));
			}
			catch (Exception e)
			{
				Logging.errorPrint(
					"Error setting old character "
					+ strings[i]
					+ ".",
					e);
			}
		}
	}

	private void standardMenuFeatures()
	{
		setText(PropertyFactory.getString("in_mnuOpenRecent"));
		setMnemonic(PropertyFactory.getMnemonic("in_mn_mnuOpenRecent"));
		Utility.setDescription(this, PropertyFactory.getString("in_mnuOpenRecentTip"));
	}

	public JOpenRecentMenu(OpenRecentCallback aCb, int aMax)
	{
		standardMenuFeatures();
		setEnabled(false);
		cb = aCb;
		entries = new FixedArrayList(aMax);
	}

	public JOpenRecentMenu(OpenRecentCallback aCb)
	{
		standardMenuFeatures();
		setEnabled(false);
		cb = aCb;
		entries = new FixedArrayList();
	}

	private final class OpenRecentEntry
	{
		private String displayAs;
		private File file;

		OpenRecentEntry(final String displayAs, final File file)
		{
			this.displayAs = displayAs;
			this.file = file;
		}

		public boolean equals(final Object obj)
		{
			if (obj == null)
			{
				return false;
			}
			if (!(obj instanceof OpenRecentEntry))
			{
				return false;
			}
			final OpenRecentEntry entry = (OpenRecentEntry) obj;
			//if (!displayAs.equals(entry.displayAs))
			//{
			//	return false;
			//}
			return file.equals(entry.file);
		}

		/**
		 * As I have no idea what to do here, I simply return super.hashCode()
		 * TODO: Is that right?
		 * @return super.hashCode()
		 */
		public int hashCode()
		{
			return super.hashCode();
		}

	}

	private final void updateMenu()
	{
		setEnabled(false);
		removeAll();

		final int x = entries.size();

		// Load in reverse order so most recent is at the top
		for (int i = 0; i < x; ++i)
		{
			add(createMenuItem((OpenRecentEntry) entries.get(i)));
		}

		if (x != 0)
		{
			setEnabled(true);
		}
	}

	private JMenuItem createMenuItem(OpenRecentEntry entry)
	{
		return Utility.createMenuItem(entry.displayAs, new OpenRecentActionListener(this, entry, cb), null, (char) 0, null, entry.file.getAbsolutePath(), null, true);
	}

	private void doAddEntry(OpenRecentEntry entry)
	{
		doRemoveEntry(entry); // move to top if possible
		entries.add(entry);
	}

	private void doRemoveEntry(OpenRecentEntry entry)
	{
		for (int i = 0; i < entries.size(); ++i)
		{
			if (!(entries.get(i)).equals(entry))
			{
				continue;
			}
			entries.remove(i);
			break;
		}
	}

	private void doAddUpdateEntry(OpenRecentEntry entry)
	{
		doAddEntry(entry);
		updateMenu();
	}

	private void doRemoveUpdateEntry(OpenRecentEntry entry)
	{
		doRemoveEntry(entry);
		updateMenu();
	}

	/**
	 * Add a new entry to the open recent menu.  If the entry is a
	 * duplicate, move it to the top.
	 *
	 * @param displayAs String the menu item label
	 * @param file File the <code>File</code> object
	 */
	public final void add(String displayAs, File file)
	{
		doAddUpdateEntry(new OpenRecentEntry(displayAs, file));
	}

	/**
	 * Remove an entry from the open recent menu.
	 *
	 * @param displayAs String the menu item label
	 * @param file File the <code>File</code> object
	 */
	//public final void remove(String displayAs, File file)
	//{
	//	doRemoveUpdateEntry(new OpenRecentEntry(displayAs, file));
	//}
}
