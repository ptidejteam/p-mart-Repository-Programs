/*
 * TabbedPane.java
 *
 * Copyright 2002 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Created on August 18th, 2002.
 */

package pcgen.gui.panes; // hm.binkley.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class TabbedPane extends JTabbedPane
{
    public interface PopupMenuPolicy
    {
	boolean canNew(int index);
	boolean canClose(int index);
	boolean canLock(int index);
	boolean canRename(int index);
	boolean canMove(int index);
	boolean hasMoveMenu(int index, MouseEvent e);
	boolean hasPlaceMenu(int index, MouseEvent e);
    }

    private HashSet locked = new HashSet();

    protected class DefaultPopupMenuPolicy
	implements PopupMenuPolicy
    {
	public boolean canNew(int index)
	{
	    return true;
	}

	public boolean canClose(int index)
	{
	    return true;
	}

	public boolean canLock(int index)
	{
	    return true;
	}

	public boolean canRename(int index)
	{
	    return true;
	}

	public boolean canMove(int index)
	{
	    return true;
	}

	public boolean hasMoveMenu(int index, MouseEvent e)
	{
	    return true;
	}

	public boolean hasPlaceMenu(int index, MouseEvent e)
	{
	    return true;
	}
    }

    protected PopupMenuPolicy policy = new DefaultPopupMenuPolicy();

    private static int offsetForPlacement(int placement)
    {
	return placement - 1;
    }

    private static int placementForSlot(int slot, int placement)
    {
	return (placement - 1 + slot) % 4 + 1;
    }

    private static final int PLACE_OFFSET = 0;
    private static final int MOVE_LEFT_RIGHT_OFFSET = 4;
    private static final int MOVE_UP_DOWN_OFFSET = 8;

    private static final String[] labels
	= { "Top", // place
	    "Left",
	    "Bottom",
	    "Right",
	    "Beginning", // move left/right
	    "Left",
	    "End",
	    "Right",
	    "Top", // move up/down
	    "Up",
	    "Bottom",
	    "Down" };

    private static final ImageIcon[] icons
	= { Utilities.UP_ICON, // place
	    Utilities.LEFT_ICON,
	    Utilities.DOWN_ICON,
	    Utilities.RIGHT_ICON,
	    Utilities.BEGINNING_ICON, // move left/right
	    Utilities.LEFT_ICON,
	    Utilities.END_ICON,
	    Utilities.RIGHT_ICON,
	    Utilities.TOP_ICON, // move up/down
	    Utilities.UP_ICON,
	    Utilities.BOTTOM_ICON,
	    Utilities.DOWN_ICON };

    private static final String[] tips
	= { "Place all tabs along the top",
	    "Place all tabs along the left",
	    "Place all tabs along the bottom",
	    "Place all tabs along the right",
	    "Move tab to the beginning",
	    "Move tab left",
	    "Move tab to the end",
	    "Move tab right",
	    "Move tab to the top",
	    "Move tab up",
	    "Move tab to the bottom",
	    "Move tab down" };

    private void setMenuItem(JMenuItem menuItem, int offset)
    {
	String label = labels[offset];

	menuItem.setText(label);

	if (label != null)
	    menuItem.setMnemonic(label.charAt(0));

	menuItem.setIcon(icons[offset]);
	menuItem.setToolTipText(tips[offset]);
    }

    private class NewActionListener
	implements ActionListener
    {
	int index;

	NewActionListener(int index)
	{
	    this.index = index;
	}

	public void actionPerformed(ActionEvent e)
	{
	    addNewTabAt(index, e);
	}
    }

    private class NewMenuItem extends JMenuItem
    {
	NewMenuItem(int index)
	{
	    super("New");

	    addActionListener(new NewActionListener(index));
	    setMnemonic('N');
	    setIcon(Utilities.NEW_ICON);
	}
    }

    private class CloseActionListener
	implements ActionListener
    {
	int index;

	CloseActionListener(int index)
	{
	    this.index = index;
	}

	public void actionPerformed(ActionEvent e)
	{
	    removeTabAt(index);
	}
    }

    private class CloseMenuItem extends JMenuItem
    {
	CloseMenuItem(int index)
	{
	    super("Close");

	    addActionListener(new CloseActionListener(index));
	    setMnemonic('C');
	    setIcon(Utilities.CLOSE_ICON);
	}
    }

    private class LockActionListener
	implements ActionListener
    {
	int index;

	LockActionListener(int index)
	{
	    this.index = index;
	}

	public void actionPerformed(ActionEvent e)
	{
	    lockTabAt(index);
	}
    }

    private class LockMenuItem extends JMenuItem
    {
	LockMenuItem(int index)
	{
	    super("Lock");

	    addActionListener(new LockActionListener(index));
	    setMnemonic('L');
	    setIcon(Utilities.LOCK_ICON);
	}
    }

    private class UnlockActionListener
	implements ActionListener
    {
	int index;

	UnlockActionListener(int index)
	{
	    this.index = index;
	}

	public void actionPerformed(ActionEvent e)
	{
	    unlockTabAt(index);
	}
    }

    private class UnlockMenuItem extends JMenuItem
    {
	UnlockMenuItem(int index)
	{
	    super("Unlock");

	    addActionListener(new UnlockActionListener(index));
	    setMnemonic('L');
	    setIcon(Utilities.LOCK_ICON);
	}
    }

    private class RenameActionListener
	implements ActionListener
    {
	int index;
	MouseEvent mouseEvent;

	RenameActionListener(int index, MouseEvent mouseEvent)
	{
	    this.index = index;
	    this.mouseEvent = mouseEvent;
	}

	private class RenameTextFieldActionListener
	    implements ActionListener
	{
	    int index;
	    JTextField textField;
	    JPopupMenu popupMenu;

	    RenameTextFieldActionListener(int index, JTextField textField,
					  JPopupMenu popupMenu)
	    {
		this.index = index;
		this.textField = textField;
		this.popupMenu = popupMenu;
	    }

	    public void actionPerformed(ActionEvent e)
	    {
		TabbedPane.this.setTitleAt
		    (index, textField.getText());
		popupMenu.setVisible(false); // why? XXX
	    }
	}

	public void actionPerformed(ActionEvent e)
	{
	    int x = mouseEvent.getX(), y = mouseEvent.getY();
	    JPopupMenu popupMenu = new JPopupMenu();
	    String title = getTitleAt(index);
	    JTextField textField = new JTextField(title);

	    System.err.println("document? " + textField.getDocument());

	    textField.addActionListener
		(new RenameTextFieldActionListener
		 (index, textField, popupMenu));
	    popupMenu.add(textField);

	    Component c = mouseEvent.getComponent();
	    // Because this doesn't have a width/height before being
	    // shown, need to show it them move it.
	    popupMenu.show(c, x, y);

	    // These don't seem to work. ?? XXX
	    textField.selectAll();
	    textField.setCaretPosition(title.length());

	    // Workaround bug in JDK1.4 (and earlier?): if JTextField
	    // slops past edge of the pane window, you can't stick the
	    // cursor in it.  XXX
	    Component pane = getComponentAt(index);
	    Point paneLocation = pane.getLocationOnScreen(),
		popupLocation = popupMenu.getLocationOnScreen();
	    Dimension paneSize = pane.getSize(),
		popupSize = popupMenu.getSize();
	    boolean reshow = false;

	    if (popupLocation.x + popupSize.width
		>= paneLocation.x + paneSize.width) {
		reshow = true;
		x = paneLocation.x + paneSize.width - popupSize.width - 1;
	    }

	    if (popupLocation.y + popupSize.height
		>= paneLocation.y + paneSize.height) {
		reshow = true;
		y = paneLocation.y + paneSize.height - popupSize.height - 1;
	    }

	    if (reshow) popupMenu.show(c, x, y);
	}
    }

    private class RenameMenuItem extends JMenuItem
    {
	RenameMenuItem(int index, MouseEvent e)
	{
	    super("Rename...");

	    addActionListener(new RenameActionListener(index, e));
	    setMnemonic('R');
	}
    }

    private class PlaceActionListener
	implements ActionListener
    {
	TabbedPane pane;
	int placement;

	PlaceActionListener(TabbedPane pane, int placement)
	{
	    this.pane = pane;
	    this.placement = placement;
	}

	public void actionPerformed(ActionEvent e)
	{
	    pane.setTabPlacement(placement);
	}
    }

    private class PlaceMenuItem extends JMenuItem
    {
	PlaceMenuItem(TabbedPane pane, int placement)
	{
	    int offset = offsetForPlacement(placement) + PLACE_OFFSET;

	    addActionListener(new PlaceActionListener(pane, placement));
	    setMenuItem(this, offset);
	}
    }

    private class PlaceMenu extends JMenu
    {
	PlaceMenu(int placement)
	{
	    super("Place Tabs");
	    setMnemonic('P');

	    // Add backwards to get clockwise choices
	    for (int j = 3; j > 0; --j)
		this.add(new PlaceMenuItem
			 (TabbedPane.this,
			  placementForSlot(j, placement)));
	}
    }

    private class MoveActionListener
	implements ActionListener
    {
	int index, placement;

	MoveActionListener(int index, int placement)
	{
	    this.index = index;
	    this.placement = placement;
	}

	private int previous(int current, int[] indices)
	{
	    for (int i = 1; i < indices.length; ++i)
		if (current == indices[i]) return indices[i - 1];

	    return -1;
	}

	private int next(int current, int[] indices)
	{
	    for (int i = 0, x = indices.length - 1; i < x; ++i)
		if (current == indices[i]) return indices[i + 1];

	    return -1;
	}

	public void actionPerformed(ActionEvent e)
	{
	    int[] indices = getMovableTabIndices();
	    int i = -1;

	    switch(placement) {
	    case TOP: i = indices[0]; break;
	    case LEFT: i = previous(index, indices); break;
	    case BOTTOM: i = indices[indices.length - 1]; break;
	    case RIGHT: i = next(index, indices); break;
	    }

	    moveTabAtTo(index, i, TabbedPane.this);
	    setSelectedIndex(i);
	}
    }

    private class MoveTabMenuItem extends JMenuItem
    {
	MoveTabMenuItem(int index, int placement)
	{
	    int offset = offsetForPlacement(placement);
	    switch (getTabPlacement()) {
	    case TOP: case BOTTOM: offset += MOVE_LEFT_RIGHT_OFFSET; break;
	    case LEFT: case RIGHT: offset += MOVE_UP_DOWN_OFFSET; break;
	    }

	    addActionListener(new MoveActionListener(index, placement));
	    setMenuItem(this, offset);
	}
    }

    private class MoveMenu extends JMenu
    {
	MoveMenu(int index)
	{
	    super("Move Tab");
	    setMnemonic('M');

	    int[] indices = getMovableTabIndices();

	    // Only you can prevent out of range errors.
	    int primum = -1, secundum = -1,
		penultimatum = -1, ultimatum = -1;
	    switch (indices.length) {
	    case 0: this.setEnabled(false); break;
	    case 1: this.setEnabled(false); break;
	    case 2:
		primum = indices[0];
		secundum = Integer.MAX_VALUE;
		penultimatum = Integer.MIN_VALUE;
		ultimatum = indices[1];
		break;
	    case 3:
		primum = indices[0];
		secundum = penultimatum = indices[1];
		ultimatum = indices[2];
	    default:
		primum = indices[0];
		secundum = indices[1];
		penultimatum = indices[indices.length - 2];
		ultimatum = indices[indices.length - 1];
	    }

	    for (int i = 0; i < indices.length; ++i) {
		if (index < indices[i]) continue;

		if (index > primum) {
		    if (index > secundum)
			this.add(new MoveTabMenuItem(index, TOP));

		    this.add(new MoveTabMenuItem(index, LEFT));
		}

		if (index < ultimatum) {
		    this.add(new MoveTabMenuItem(index, RIGHT));

		    if (index < penultimatum)
			this.add(new MoveTabMenuItem(index, BOTTOM));
		}

		break;
	    }
	}
    }

    // Need to use action events instead  XXX

    protected TabbedPane createPane()
    {
	return new TabbedPane();
    }

    protected void addPopupMenuItems(JPopupMenu popupMenu, int index,
				     MouseEvent e)
    {
    }

    protected void addNewTabAt(int index, ActionEvent e)
    {
	add(new JPanel());
    }

    private class PopupListener extends MouseAdapter
    {
	public void mousePressed(MouseEvent e)
	{
	    if (Utilities.isRightMouseButton(e)) {
		int x = e.getX(), y = e.getY();
		int index = indexAtLocation(x, y);
		int tabPlacement = getTabPlacement();

		JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem newMenuItem = null;
		JMenuItem closeMenuItem = null;
		JMenuItem lockMenuItem = null;
		JMenuItem renameMenuItem = null;
		JMenu moveMenu = null;
		JMenu placeMenu = null;

		if (policy.canNew(index))
		    newMenuItem = new NewMenuItem(index);

		if (index >= 0) {
		    if (policy.canClose(index)
			&& ! isTabLockedAt(index))
			closeMenuItem = new CloseMenuItem(index);

		    if (policy.canLock(index))
			lockMenuItem = isTabLockedAt(index)
			    ? (JMenuItem) new UnlockMenuItem(index)
			    : (JMenuItem) new LockMenuItem(index);

		    if (policy.canRename(index))
			renameMenuItem = new RenameMenuItem(index, e);

		    if (policy.hasMoveMenu(index, e)
			&& getMovableTabCount() > 1
			&& ! isTabLockedAt(index))
			moveMenu = new MoveMenu(index);
		}

		if (policy.hasPlaceMenu(index, e))
		    placeMenu = new PlaceMenu(tabPlacement);

		boolean useNewMenuItem = newMenuItem != null;
		boolean useCloseMenuItem = closeMenuItem != null;
		boolean useLockMenuItem = lockMenuItem != null;
		boolean useRenameMenuItem = renameMenuItem != null;
		boolean useMoveMenu = moveMenu != null
		    && moveMenu.getMenuComponentCount() > 0;
		boolean usePlaceMenu = placeMenu != null
		    && placeMenu.getMenuComponentCount() > 0;

		if (popupMenu.getComponentCount() > 0
		    && (useNewMenuItem || useCloseMenuItem))
		    popupMenu.addSeparator();

		if (useNewMenuItem) popupMenu.add(newMenuItem);
		if (useCloseMenuItem) popupMenu.add(closeMenuItem);

		if (popupMenu.getComponentCount() > 0
		    && (useLockMenuItem || useRenameMenuItem))
		    popupMenu.addSeparator();

		if (useLockMenuItem) popupMenu.add(lockMenuItem);
		if (useRenameMenuItem) popupMenu.add(renameMenuItem);

		if (popupMenu.getComponentCount() > 0
		    && (useMoveMenu || usePlaceMenu))
		    popupMenu.addSeparator();

		if (useMoveMenu) popupMenu.add(moveMenu);
		if (usePlaceMenu) popupMenu.add(placeMenu);

		addPopupMenuItems(popupMenu, index, e);

		popupMenu.show(e.getComponent(), x, y);
	    }
	}
    }

    public TabbedPane()
    {
	addMouseListener(new PopupListener());
    }

    public int getMovableTabCount()
    {
	int n = 0;

	for (int i = 0, x = getTabCount(); i < x; ++i)
	    if (policy.canMove(i) && ! isTabLockedAt(i)) ++n;

	return n;
    }

    public int[] getMovableTabIndices()
    {
	int x = getTabCount();
	int[] list1 = new int[x];
	int n = 0;

	for (int i = 0; i < x; ++i)
	    if (policy.canMove(i) && ! isTabLockedAt(i)) list1[n++] = i;

	int[] list2 = new int[n];

	for (int i = 0; i < n; ++i)
	    list2[i] = list1[i];

	return list2;
    }

    public void lockTabAt(int index)
    {
	locked.add(getComponentAt(index));
	setIconAt(index, Utilities.LOCK_ICON);
    }

    public void unlockTabAt(int index)
    {
	locked.remove(getComponentAt(index));
	setIconAt(index, null);
    }

    public boolean isTabLockedAt(int index)
    {
	return locked.contains(getComponentAt(index));
    }

    private void moveTabAtTo(int fromIndex, int toIndex, JTabbedPane to)
    {
	Component c = getComponentAt(fromIndex);

	Color background = getBackgroundAt(fromIndex);
	Icon disabledIcon = getDisabledIconAt(fromIndex);
	// JDK1.3 issue.  XXX
// 	int displayedMnemonicIndex = getDisplayedMnemonicIndexAt(fromIndex);
	Color foreground = getForegroundAt(fromIndex);
	Icon icon = getIconAt(fromIndex);
	// JDK1.3 issue.  XXX
// 	int mnemonic = getMnemonicAt(fromIndex);
	String title = getTitleAt(fromIndex);
	String tip = getToolTipTextAt(fromIndex);

	removeTabAt(fromIndex);
	if (toIndex == -1) toIndex = to.getTabCount();
	to.add(c, toIndex);

	to.setBackgroundAt(toIndex, background);
	to.setDisabledIconAt(toIndex, disabledIcon);
	to.setForegroundAt(toIndex, foreground);
	to.setIconAt(toIndex, icon);
	to.setTitleAt(toIndex, title);
	to.setToolTipTextAt(toIndex, tip);

	// JDK1.3 issue.  XXX
// 	if (mnemonic != -1)
// 	    to.setMnemonicAt(toIndex, mnemonic);
// 	if (displayedMnemonicIndex != -1)
// 	    to.setDisplayedMnemonicIndexAt(toIndex, displayedMnemonicIndex);
    }

    // JDK1.3 issue.  XXX
    /**
     * Returns the tab index corresponding to the tab whose bounds
     * intersect the specified location.  Returns -1 if no tab
     * intersects the location.
     *
     * @param x the x location relative to this tabbedpane
     * @param y the y location relative to this tabbedpane
     * @return the tab index which intersects the location, or
     *         -1 if no tab intersects the location
     * @since 1.4
     */
    public int indexAtLocation(int x, int y)
    {
	if (ui == null) return -1;

	return ((javax.swing.plaf.TabbedPaneUI) ui)
	    .tabForCoordinate(this, x, y);
    }
}
