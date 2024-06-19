package pcgen.gui;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class MainPopupMenu extends JPopupMenu
{
	JMenuItem pleaseLoadItem;
	JMenuItem newItem;

	public MainPopupMenu(FrameActionListener frameActionListener)
	{
		add(pleaseLoadItem = Utility.createMenuItem("Please load campaigns", null, "mainPopupMenu.pleaseLoad", (char)0, null, "You must load one or more campaigns before creating new characters", null, false));
		newItem = Utility.createMenuItem("New", frameActionListener.newPopupActionListener, "mainPopupMenu.new", 'N', null, "Create a new character", "New16.gif", true);
	}

	public void setLoaded(boolean loaded)
	{
		if (loaded)
		{
			remove(pleaseLoadItem);
			add(newItem);
		}
		else
		{
			remove(newItem);
			add(pleaseLoadItem);
		}
	}
}
