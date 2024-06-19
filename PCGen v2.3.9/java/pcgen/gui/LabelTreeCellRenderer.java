package pcgen.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

public class LabelTreeCellRenderer extends JLabel implements TreeCellRenderer
{


	/** Color to use for the background when selected. */
	static protected final Color SelectedBackgroundColor = Color.white;//new Color(0, 0, 128);

	/**
	 * This is messaged from JTree whenever it needs to get the size
	 * of the component or it wants to draw it.
	 * This attempts to set the font based on value, which will be
	 * a TreeNode.
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
		boolean selected, boolean expanded,
		boolean leaf, int row,
		boolean hasFocus)
	{
		String stringValue = tree.convertValueToText(value, selected,
			expanded, leaf, row, hasFocus);

		if (stringValue.startsWith("|"))
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
/* Update the selected flag for the next paint. */
		this.selected = selected;
		return this;
	}

	/** Whether or not the item that was last configured is selected. */
	protected boolean selected;
	protected Color myColor = Color.white;

	/**
	 * paint is subclassed to draw the background correctly.  JLabel
	 * currently does not allow backgrounds other than white, and it
	 * will also fill behind the icon.  Something that isn't desirable.
	 */
/*    public void paint(Graphics g) {
	Color            bColor;
	Icon             currentI = getIcon();

	if(selected && myColor==Color.white)
	    bColor = SelectedBackgroundColor;
	else
		bColor = myColor;

	g.setColor(bColor);
	if(currentI != null && getText() != null) {
	    int          offset = (currentI.getIconWidth() + getIconTextGap());

	    g.fillRect(offset, 0, getWidth() - 1 - offset,
		       getHeight() - 1);
	}
	else
	    g.fillRect(0, 0, getWidth()-1, getHeight()-1);
	super.paint(g);
    }
*/
}
