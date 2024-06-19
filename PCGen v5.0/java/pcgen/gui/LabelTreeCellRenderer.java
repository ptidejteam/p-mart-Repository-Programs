/*
 * LabelTreeCellRenderer.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import pcgen.core.Globals;

/**
 *  <code>LabelTreeCellRenderer</code>
 *
 * @author     ???
 * @version    $Revision: 1.1 $
 */
final class LabelTreeCellRenderer extends JLabel implements TreeCellRenderer
{

	/** Color to use for the background when selected. */
	protected static final Color SelectedBackgroundColor = Color.white;

	/**
	 * This is messaged from JTree whenever it needs to get the size
	 * of the component or it wants to draw it.
	 * This attempts to set the font based on value, which will be
	 * a TreeNode.
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{

		String stringValue;
		try
		{
			stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
		}
		catch (Exception exc)
		{
			stringValue = "";
			Globals.errorPrint("problem converting in treecellrenderer", exc);
		}

		if ((stringValue.length() > 0) && stringValue.charAt(1) == '|')
		{
			final Font aFont = getFont();
			final String fontName = aFont.getName();
			final int iSize = aFont.getSize();
			if (stringValue.charAt(0) == 'B')
			{
				stringValue = stringValue.substring(2, stringValue.length());
				final Font newFont = new Font(fontName, Font.BOLD, iSize);
				setFont(newFont);
			}
			else if (stringValue.charAt(0) == 'I')
			{
				stringValue = stringValue.substring(2, stringValue.length());
				final Font newFont = new Font(fontName, Font.ITALIC, iSize);
				setFont(newFont);
			}
		}
		else
		{
			final Font aFont = getFont();
			final String fontName = aFont.getName();
			final int iSize = aFont.getSize();
			final Font newFont = new Font(fontName, Font.PLAIN, iSize);
			setFont(newFont);
		}

		if (stringValue.length() > 0 && stringValue.charAt(0) == '|')
		{
			int x = Math.max(2, stringValue.lastIndexOf("|"));
			final String aString = stringValue.substring(1, x);
			myColor = new Color(Integer.parseInt(aString));
			stringValue = stringValue.substring(x + 1);
			if (selected)
			{
				setBackground(myColor);
				setForeground(Color.white);
			}
			else
			{
				setForeground(myColor);
				setBackground(Color.white);
			}
		}
		else
		{
			if (selected)
			{
				setForeground(Color.white);
				setBackground(Color.blue);
			}
			else
			{
				setForeground(Color.black);
				setBackground(Color.white);
			}
		}
		setText(stringValue);
		// Update the selected flag for the next paint
		this.selected = selected;
		return this;
	}

	/** Whether or not the item that was last configured is selected. */
	private boolean selected;
	private Color myColor = Color.white;

}
