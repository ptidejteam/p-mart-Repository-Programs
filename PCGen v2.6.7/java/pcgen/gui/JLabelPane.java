/*
 * JLabelPane.java
 * Copyright 2001 (C) Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on June 4, 2002
 */

package pcgen.gui;

import javax.swing.JEditorPane;

import pcgen.core.Globals;

/**
 *  <code>JLabelPane</code> extends <code>JEditorPane</code> for PCGen
 *  Label panes
 *
 * @author     B. K. Oxley (binkley) <binkley@bigfoot.com>
 * @version    $Revision: 1.1 $
 */

public class JLabelPane extends JEditorPane
{
	/**
	 * Create default HTML "label", an HTML <code>JEditorPane</code>
	 */
	public JLabelPane ()
	{
		super("text/html","<html></html>");
		setText();
	}

	/**
	 * Update the text in a non-editable HTML "label" to the default
	 * for non-selected labels
	 */
	public void setText()
	{
		/*
		 * this fixes a bug which causes NPEs to be thrown on updateUI()
		 * with no HTML tags present
		 *
		 * author: Thomas Behr 13-03-03
		 */
		setText(Globals.html_NONESELECTED);
	}

	/**
	 * Update the text in a non-editable HTML "label". If the label
	 * is <code>null</code>, use a default, blank HTML string
	 *
	 * @param s String new contents of pane or <code>null</code>
	 */
	public void setText(String s)
	{
		if (s == null)
		{
			s = Globals.html_NONESELECTED;
		}
		setEditable(true);
		super.setText(s);
		setCaretPosition(0);
		setEditable(false);
	}
}
