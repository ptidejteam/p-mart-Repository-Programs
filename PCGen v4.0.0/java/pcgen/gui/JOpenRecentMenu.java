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

package pcgen.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import pcgen.core.Constants;

/**
 *  <code>JOpenRecentMenu</code> extends JMenu with one special coded
 *  for opening recent files
 *
 * @author     B. K. Oxley (binkley) <a href="mailto:binkley@bigfoot.com">&ltbinkley@bigfoot.com&gt;</a>
 * @version    $Revision: 1.1 $
 */

class JOpenRecentMenu extends JMenu
{
	int max = Constants.MAX_OPEN_RECENT_ENTRIES;

	private class FixedArrayList extends ArrayList
	{
		// A rather incomplement implementation, I admit.  XXX --bko
		private int max = 0;

		FixedArrayList()
		{
			super(Constants.MAX_OPEN_RECENT_ENTRIES);
			max = Constants.MAX_OPEN_RECENT_ENTRIES;
		}

		FixedArrayList(int max)
		{
			super(max);
			this.max = max;
		}

		public boolean add(Object element)
		{
			while (FixedArrayList.this.size() > max - 1)
				this.remove(0);

			return super.add(element);
		}
	}

	/**
	 * <code>OpenRecentCallback</code> is an
	 * <code>ActionListener</code> for menu items in the recently
	 * opened menu.
	 */
	interface OpenRecentCallback
	{
		/**
		 * Handle the reopening of a recently opened file.
		 *
		 * @param e ActionEvent the menu selection event
		 * @param file File the recently opened file
		 */
		void openRecentPerformed(ActionEvent e, File file);
	}

	private class OpenRecentActionListener implements ActionListener
	{
		JOpenRecentMenu menu = null;
		OpenRecentEntry entry = null;
		OpenRecentCallback cb = null;

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
				menu.doRemoveUpdateEntry(entry);
		}
	}

	OpenRecentCallback cb = null;
	FixedArrayList entries = null;

	public String[] getEntriesAsStrings()
	{
		ArrayList strings = new ArrayList();

		for (Iterator it = entries.iterator(); it.hasNext();)
		{
			OpenRecentEntry entry = (OpenRecentEntry)it.next();
			strings.add(entry.displayAs);
			strings.add(entry.file.getAbsolutePath());
		}

		return (String[])strings.toArray(new String[0]);
	}

	public void setEntriesAsStrings(String[] strings)
	{
		for (int i = 0; i < strings.length; i += 2)
		{
			add(strings[i], new File(strings[i + 1]));
		}
	}

	private void standardMenuFeatures()
	{
		setText("Open Recent");
		setMnemonic('R');
		String description = "Open a recently closed file";
		Utility.setDescription(this, description);
	}

	public JOpenRecentMenu(OpenRecentCallback aCb)
	{
		standardMenuFeatures();
		setEnabled(false);
		cb = aCb;
		entries = new FixedArrayList();
	}

	public JOpenRecentMenu(OpenRecentCallback aCb, int aMax)
	{
		standardMenuFeatures();
		setEnabled(false);
		cb = aCb;
		entries = new FixedArrayList(aMax);
	}

	private class OpenRecentEntry
	{
		String displayAs;
		File file;

		OpenRecentEntry(String displayAs, File file)
		{
			this.displayAs = displayAs;
			this.file = file;
		}

		public boolean equals(Object obj)
		{
			if (obj == null)
				return false;
			if (!(obj instanceof OpenRecentEntry))
				return false;
			OpenRecentEntry entry = (OpenRecentEntry)obj;
			if (!displayAs.equals(entry.displayAs))
				return false;
			if (!file.equals(entry.file))
				return false;
			return true;
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

	private void updateMenu()
	{
		setEnabled(false);
		removeAll();

		int x = entries.size();

		// Load in reverse order so most recent is at the top
		for (int i = x - 1; i >= 0; --i)
			add(createMenuItem((OpenRecentEntry)entries.get(i)));

		if (x > 0)
			setEnabled(true);
	}

	private JMenuItem createMenuItem(OpenRecentEntry entry)
	{
		return Utility.createMenuItem(entry.displayAs,
		  new OpenRecentActionListener(this, entry, cb),
		  null, (char)0, null, entry.file.getAbsolutePath(),
		  null, true);
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
			if (!((OpenRecentEntry)entries.get(i)).equals(entry))
				continue;
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
	public void add(String displayAs, File file)
	{
		doAddUpdateEntry(new OpenRecentEntry(displayAs, file));
	}

	/**
	 * Remove an entry from the open recent menu.
	 *
	 * @param displayAs String the menu item label
	 * @param file File the <code>File</code> object
	 */
	public void remove(String displayAs, File file)
	{
		doRemoveUpdateEntry(new OpenRecentEntry(displayAs, file));
	}
}
