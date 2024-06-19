package pcgen.gui;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import pcgen.core.Globals;

public class PCPopupMenu extends JPopupMenu
{
	public static final int SHIFT_NONE = 0;
	public static final int SHIFT_LEFT = 1;
	public static final int SHIFT_RIGHT = 2;
	public static final int SHIFT_LEFT_RIGHT = 3;
	public static final int SHIFT_LEFT_BEGINNING = 4;
	public static final int SHIFT_END_RIGHT = 5;

	JMenuItem newItem;
	JMenuItem closeItem;
	JMenuItem saveItem;
	JMenuItem saveAsItem;
	JMenuItem revertToSavedItem;
	JSeparator shiftSeparator;
	JMenuItem shiftLeftItem;
	JMenuItem shiftRightItem;
	JMenuItem shiftBeginningItem;
	JMenuItem shiftEndItem;

	public PCPopupMenu(FrameActionListener frameActionListener)
	{
		add(newItem = Utility.createMenuItem("New", frameActionListener.newPopupActionListener, "pcPopupMenu.new", 'N', null, "Create a new character", "New16.gif", true));
		add(closeItem = Utility.createMenuItem("Close", frameActionListener.closePopupActionListener, "pcPopupMenu.close", 'C', null, "Close the current character", "Close16.gif", true));
		add(saveItem = Utility.createMenuItem("Save", frameActionListener.savePopupActionListener, "pcPopupMenu.save", 'S', null, "Save the current character to its .PCG file", "Save16.gif", true));
		add(saveAsItem = Utility.createMenuItem("Save as...", frameActionListener.saveAsPopupActionListener, "pcPopupMenu.saveas", 'A', null, "Save the current character to a new .PCG file", "SaveAs16.gif", true));
		// Special so that Save _A_s..., not S_a_ve As...
		//saveAsItem.setDisplayedMnemonicIndex(5); // JDK 1.4
		add(revertToSavedItem = Utility.createMenuItem("Revert to saved", frameActionListener.revertToSavedPopupActionListener, "pcPopupMenu.revert", 'T', null, "Reopen the current character from its .PCG file, discarding any changes", null, true));

		shiftSeparator = new JSeparator();
		shiftLeftItem = Utility.createMenuItem("Shift left", frameActionListener.shiftLeftPopupActionListener, "pcPopupMenu.shift.left", 'L', null, "Shift PC tab left one position", "Back16.gif", true);
		shiftRightItem = Utility.createMenuItem("Shift right", frameActionListener.shiftRightPopupActionListener, "pcPopupMenu.shift.right", 'R', null, "Shift PC tab right one position", "Forward16.gif", true);
		// The labels say beginning/end, but they really are just cyclicl right/left shift
		shiftBeginningItem = Utility.createMenuItem("Shift beginning", frameActionListener.shiftRightPopupActionListener, "pcPopupMenu.shift.beginning", 'B', null, "Shift PC tab left all the way", "Back16.gif", true);
		shiftEndItem = Utility.createMenuItem("Shift end", frameActionListener.shiftLeftPopupActionListener, "pcPopupMenu.shift.end", 'E', null, "Shift PC tab right all the way", "Forward16.gif", true);
	}

	public void setShiftType(int type)
	{
		remove(shiftSeparator);
		remove(shiftLeftItem);
		remove(shiftRightItem);
		remove(shiftBeginningItem);
		remove(shiftEndItem);

		switch(type)
		{
		case SHIFT_NONE:
		    break;
		case SHIFT_LEFT:
		    add(shiftSeparator);
		    add(shiftLeftItem);
		    break;
		case SHIFT_RIGHT:
		    add(shiftSeparator);
		    add(shiftRightItem);
		    break;
		case SHIFT_LEFT_RIGHT:
		    add(shiftSeparator);
		    add(shiftLeftItem);
		    add(shiftRightItem);
		    break;
		case SHIFT_LEFT_BEGINNING:
		    add(shiftSeparator);
		    add(shiftLeftItem);
		    add(shiftBeginningItem);
		    break;
		case SHIFT_END_RIGHT:
		    add(shiftSeparator);
		    add(shiftEndItem);
		    add(shiftRightItem);
		    break;
		default:
		    Globals.errorPrint("Invalid shift for PCPopupMenu: " + type);
		}
	}
}
