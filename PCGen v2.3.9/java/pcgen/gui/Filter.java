/**
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
 * Created on December 29, 2001, 7:15 PM
 * Bryan McRoberts (merton_monk@yahoo.com)
 */
package pcgen.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import pcgen.core.Globals;

public class Filter extends JDialog
{
	// by default show all objects, if true then show only qualfieid items
	public static boolean showQualifiedOnly = false;

	public Filter()
	{
		super(Globals.getRootFrame());
		initComponents();
	}

	public Filter(java.awt.Dialog owner)
	{
		super(owner);
		initComponents();
	}

	public Filter(java.awt.Dialog owner, String title)
	{
		super(owner, title);
		initComponents();
	}

	public Filter(java.awt.Dialog owner, String title, boolean modal)
	{
		super(owner, title, modal);
		initComponents();
	}

	public Filter(java.awt.Dialog owner, boolean modal)
	{
		super(owner, modal);
		initComponents();
	}

	public Filter(java.awt.Frame owner)
	{
		super(owner);
		initComponents();
	}

	public Filter(java.awt.Frame owner, String title)
	{
		super(owner, title);
		initComponents();
	}

	public Filter(java.awt.Frame owner, String title, boolean modal)
	{
		super(owner, title, modal);
		initComponents();
	}

	public Filter(java.awt.Frame owner, boolean modal)
	{
		super(owner, modal);
		initComponents();
	}

	private void initComponents()
	{
		// Initialize basic dialog settings
		setModal(false);
		setSize(new Dimension(200, 50));
		setTitle("Filter");
		final JCheckBox aBox = new JCheckBox("Show Qualified items Only", false);
		aBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showQualifiedOnly = !showQualifiedOnly;
				aBox.setSelected(showQualifiedOnly);
                        InfoFeats.needsUpdate=true;
                        InfoDomain.needsUpdate=true;
			}
		});
		Container contentPane = getContentPane();
		contentPane.add(aBox);
	}
}


