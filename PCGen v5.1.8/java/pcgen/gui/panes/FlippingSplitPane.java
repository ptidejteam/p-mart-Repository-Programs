/*
 * FlippingSplitPane.java
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import pcgen.util.PropertyFactory;

/**
 * <code>FlippingSplitPane</code> is an improved version of
 * <code>JSplitPane</code> featuring a popup menu accesses by
 * right-clicking on the divider.
 *
 * (<code>JSplitPane</code> is used to divide two (and only two)
 * <code>Component</code>s.  The two <code>Component</code>s are
 * graphically divided based on the look and feel implementation, and
 * the two <code>Component</code>s can then be interactively resized
 * by the user.  Information on using <code>JSplitPane</code> is in <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/components/splitpane.html">How
 * to Use Split Panes</a> in <em>The Java Tutorial</em>.)
 *
 * In addition to the standard keyboard keys used by
 * <code>JSplitPane</code>, <code>FlippingSplitPane</code> will flip
 * the panes orientation on <code>SHIFT-BUTTON1</code>.
 *
 * (For the keyboard keys used by <code>JSplitPane</code> in the
 * standard Look and Feel (L&F) renditions, see the <a
 * href="doc-files/Key-Index.html#JSplitPane"><code>JSplitPane</code>
 * key assignments</a>.)
 *
 * <code>FlippingSplitPane</code> treats many of the methods of
 * <code>JSplitPane</code> recursively, calling the same method on the
 * left and right components (or top and bottom for
 * <code>VERTICAL_ORIENTATION</code>) if they are also
 * <code>FlippingSplitPane<code>s.  You can defeat this behavior by
 * using <code>JSplitPane</code> components instead.
 *
 * <code>FlippingSplitPane</code> also supports "locking": a locked
 * pane renders the divider unmovable, and the popup menu only has an
 * "Unlocked" item.  Locking is also recursive for
 * <code>FlippingSplitPane</code> components.
 *
 * @version $Revision: 1.1 $
 * @author B. K. Oxley (binkley) <a href="mailto:binkley@alumni.rice.edu">binkley@alumni.rice.edu</a>
 */
public final class FlippingSplitPane extends JSplitPane
{
	static final long serialVersionUID = -6343545558990369582L;
	/** Icon for Center item in popup menu. */
	private static final ImageIcon CENTER_ICON = Utilities.getImageIcon("resources/MediaStop16.gif");
	/** Icon for Flip item in popup menu. */
	private static final ImageIcon FLIP_ICON = Utilities.getImageIcon("resources/Refresh16.gif");
	/** Icon for Reset item in popup menu. */
	private static final ImageIcon RESET_ICON = Utilities.getImageIcon("resources/Redo16.gif");
	/** Icon for Lock/Unlock item in popup menu */
	private static final ImageIcon LOCK_ICON = Utilities.getImageIcon("resources/Bookmarks16.gif");

	/** Is the split pane locked? */
	private boolean locked = false;
	/**
	 * Workaround for bug with locking panes; this is easier that big
	 * surgery on BasicSplitPaneDivider.
	 */
	private boolean wasContinuousLayout = false;

	/** Action for Center item in popup menu. */
	private class CenterActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			centerDividerLocations();
		}
	}

	/** Menu item for Center item in popup menu. */
	private class CenterMenuItem extends JMenuItem
	{
		CenterMenuItem()
		{
			super(PropertyFactory.getString("in_center"));

			setMnemonic(PropertyFactory.getMnemonic("in_mn_center"));
			setIcon(CENTER_ICON);

			addActionListener(new CenterActionListener());
		}
	}

	/** Action for Flip item in popup menu. */
	private class FlipActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			flipOrientation();
		}
	}

	/** Menu item for Flip item in popup menu. */
	private class FlipMenuItem extends JMenuItem
	{
		FlipMenuItem()
		{
			super(PropertyFactory.getString("in_flip"));

			setMnemonic(PropertyFactory.getMnemonic("in_mn_flip"));
			setIcon(FLIP_ICON);

			addActionListener(new FlipActionListener());
		}
	}

	/** Action for Reset item in popup menu. */
	private class ResetActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			resetToPreferredSizes();
		}
	}

	/** Menu item for Reset item in popup menu. */
	private class ResetMenuItem extends JMenuItem
	{
		ResetMenuItem()
		{
			super(PropertyFactory.getString("in_reset"));

			setMnemonic(PropertyFactory.getMnemonic("in_mn_reset"));
			setIcon(RESET_ICON);

			addActionListener(new ResetActionListener());
		}
	}

	/** Action for Lock/Unlock item in popup menu. */
	private class LockActionListener implements ActionListener
	{
		private boolean locked;

		LockActionListener(boolean locked)
		{
			this.locked = locked;
		}

		public void actionPerformed(ActionEvent e)
		{
			setLocked(locked);
		}
	}

	/** Menu item for Lock/Unlock item in popup menu. */
	private class LockMenuItem extends JMenuItem
	{
		LockMenuItem()
		{
			final boolean locked = !isLocked();

			setText(locked ? PropertyFactory.getString("in_lock") : PropertyFactory.getString("in_unlock"));
			setMnemonic(PropertyFactory.getMnemonic("in_mn_lock"));
			setIcon(LOCK_ICON);

			addActionListener(new LockActionListener(locked));
		}
	}

	/** Action for Continuous layout item in options menu. */
	private class ContinuousLayoutActionListener implements ActionListener
	{
		boolean continuousLayout;

		ContinuousLayoutActionListener(boolean continuousLayout)
		{
			this.continuousLayout = continuousLayout;
		}

		public void actionPerformed(ActionEvent e)
		{
			setContinuousLayout(continuousLayout);
		}
	}

	/** Menu item for Continuous layout item in options menu. */
	private class ContinuousLayoutMenuItem extends JCheckBoxMenuItem
	{
		ContinuousLayoutMenuItem()
		{
			super(PropertyFactory.getString("in_smothRes"));

			boolean continuousLayout = isContinuousLayout();

			setMnemonic(PropertyFactory.getMnemonic("in_mn_smothRes"));
			setSelected(continuousLayout);

			addActionListener(new ContinuousLayoutActionListener(!continuousLayout));
		}
	}

	/** Action for One touch expandable item in options menu. */
	private class OneTouchExpandableActionListener implements ActionListener
	{
		private boolean oneTouchExpandable;

		OneTouchExpandableActionListener(boolean oneTouchExpandable)
		{
			this.oneTouchExpandable = oneTouchExpandable;
		}

		public void actionPerformed(ActionEvent e)
		{
			setOneTouchExpandable(oneTouchExpandable);
		}
	}

	/** Menu item for One touch expandable item in options menu. */
	private class OneTouchExpandableMenuItem extends JCheckBoxMenuItem
	{
		OneTouchExpandableMenuItem()
		{
			super(PropertyFactory.getString("in_oneTouchExp"));

			final boolean oneTouchExpandable = isOneTouchExpandable();

			setMnemonic(PropertyFactory.getMnemonic("in_mn_oneTouchExp"));
			setSelected(oneTouchExpandable);

			addActionListener(new OneTouchExpandableActionListener(!oneTouchExpandable));
		}
	}

	/** Menu for Options item in popup menu. */
	private class OptionsMenu extends JMenu
	{
		OptionsMenu()
		{
			super(PropertyFactory.getString("in_options"));

			setMnemonic(PropertyFactory.getMnemonic("in_mn_options"));

			this.add(new OneTouchExpandableMenuItem());
			this.add(new ContinuousLayoutMenuItem());
		}
	}

	/* *
	 * After <code>FlippingSplitPane</code> builds the basic popup
	 * menu, subclasses may modify it here before
	 * <code>FlippingSplitPane</code> displays it.
	 * /
	private static void addPopupMenuItems(JPopupMenu popupMenu, MouseEvent e)
	{
	}
	*/

	/** Mouse listener for popup menu. */
	private class PopupListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if (Utilities.isRightMouseButton(e))
			{
				final int x = e.getX(), y = e.getY();

				JPopupMenu popupMenu = new JPopupMenu();

				if (!isLocked())
				{
					popupMenu.add(new CenterMenuItem());
					popupMenu.add(new FlipMenuItem());
					popupMenu.add(new ResetMenuItem());
					popupMenu.addSeparator();
				}

				popupMenu.add(new LockMenuItem());

				if (!isLocked())
				{
					popupMenu.addSeparator();
					popupMenu.add(new OptionsMenu());
				}

				//Commented out as it's an unused empty function.
				//addPopupMenuItems(popupMenu, e);

				popupMenu.show(e.getComponent(), x, y);
			}
			// A handy shortcut
			else if (Utilities.isShiftLeftMouseButton(e))
			{
				if (!isLocked())
				{
					flipOrientation();
				}
			}
		}
	}

//     private class KeyboardShiftHomeAction extends AbstractAction
//     {
//         public void actionPerformed(ActionEvent e)
// 	{
// 	    centerDividerLocations();
//         }
//     }

//     private class KeyboardShiftEndAction extends AbstractAction
//     {
//         public void actionPerformed(ActionEvent e)
// 	{
// 	    resetToPreferredSizes();
//         }
//     }

	/**
	 * <code>setupExtensions</code> installs the mouse listener for
	 * the popup menu, and fixes some egregious defaults in
	 * <code>JSplitPane</code>.
	 */
	private void setupExtensions()
	{
		SplitPaneUI ui = getUI();

		if (ui instanceof BasicSplitPaneUI)
		{
			((BasicSplitPaneUI) ui).getDivider().addMouseListener(new PopupListener());
		}

// 	// See source for JSplitPane for this junk.
// 	ActionMap map = (ActionMap) UIManager.get("SplitPane.actionMap");
// 	map.put("selectCenter", new KeyboardShiftHomeAction()); // XXX
// 	map.put("selectReset", new KeyboardShiftEndAction()); // XXX
// 	SwingUtilities.replaceUIActionMap(this, map);

		// This is *so* much better than squishing the top/left
		// component into oblivion.
		setResizeWeight(0.5);
	}

	/**
	 * Creates a new <code>FlippingSplitPane</code>.  Panes begin as
	 * unlocked, and otherwise take the defaults of {@link
	 * JSplitPane#JSplitPane}.
	 */
	public FlippingSplitPane()
	{
		setupExtensions();
	}

	/**
	 * Creates a new <code>FlippingSplitPane</code>.  Panes begin as
	 * unlocked, and otherwise take the defaults of {@link
	 * JSplitPane#JSplitPane(int)}.
	 */
	public FlippingSplitPane(int newOrientation)
	{
		super(newOrientation);

		setupExtensions();
	}

	/**
	 * Creates a new <code>FlippingSplitPane</code>.  Panes begin as
	 * unlocked, and otherwise take the defaults of {@link
	 * JSplitPane#JSplitPane(int,boolean)}.
	 */
	public FlippingSplitPane(int newOrientation, boolean newContinuousLayout)
	{
		super(newOrientation, newContinuousLayout);

		setupExtensions();
	}

	/**
	 * Creates a new <code>FlippingSplitPane</code>.  Panes begin as
	 * unlocked, and otherwise take the defaults of {@link
	 * JSplitPane#JSplitPane(int,Component,Component)}.
	 */
	public FlippingSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent)
	{
		super(newOrientation, newLeftComponent, newRightComponent);

		setupExtensions();
	}

	/**
	 * Creates a new <code>FlippingSplitPane</code>.  Panes begin as
	 * unlocked, and otherwise take the defaults of {@link
	 * JSplitPane#JSplitPane(int,boolean,Component,Component)}.
	 */
	public FlippingSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent, Component newRightComponent)
	{
		super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);

		setupExtensions();
	}

	/**
	 * Center <code>FlippingSplitPane</code> components; do nothing
	 * for other components.
	 *
	 * @param c <code>Component</code>, the component.
	 */
	private static void maybeCenterDividerLocationsComponent(Component c)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).centerDividerLocations();
		}
	}

	/**
	 * <code>centerDividerLocations</code> sets the divider location
	 * in the middle by recursively calling
	 * <code>setDividerLocation(0.5)</code>.
	 *
	 * @see #setDividerLocation(double)
	 */
	private void centerDividerLocations()
	{
		setDividerLocation(0.5);
		maybeCenterDividerLocationsComponent(getLeftComponent());
		maybeCenterDividerLocationsComponent(getRightComponent());
	}

	/**
	 * Reset <code>FlippingSplitPane</code> components; do nothing for
	 * other components (not even <code>JSplitPane</code> components).
	 *
	 * @param c <code>Component</code>, the component.
	 */
	private static void maybeResetToPreferredSizesComponent(Component c)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).resetToPreferredSizes();
		}
	}

	/**
	 * <code>fixedResetToPreferredSizes</code> fixes a bug whereby
	 * flipping a pane from vertical to horizontal sets the divider
	 * location to <code>1</code>, thereby hiding the left component.
	 */
	private void fixedResetToPreferredSizes()
	{
		setDividerLocation((int) ((getMinimumDividerLocation() + getMaximumDividerLocation()) / 2));
	}

	/**
	 * <code>resetToPreferredSizes</code> recursively calls {@link
	 * JSplitPane#resetToPreferredSizes} on
	 * <code>FlippingSplitPane</code> components.
	 */
	public void resetToPreferredSizes()
	{
		fixedResetToPreferredSizes();
		maybeResetToPreferredSizesComponent(getLeftComponent());
		maybeResetToPreferredSizesComponent(getRightComponent());
	}

	/**
	 * <code>invertOrientation</code> is a convenience function to
	 * turn horizontal into vertical orientations and the converse.
	 *
	 * @param orientation <code>int</code>, either
	 * <code>HORIZONTAL_ORIENTATION</code> or
	 * <code>VERTICAL_ORIENTATION</code>
	 *
	 * @return <code>int</code>, the inverse
	 */
	private static int invertOrientation(int orientation)
	{
		return orientation == HORIZONTAL_SPLIT ? VERTICAL_SPLIT : HORIZONTAL_SPLIT;
	}

	/**
	 * Flip <code>FlippingSplitPane</code> components; do nothing for
	 * other components.
	 *
	 * @param c <code>Component</code>, the component.
	 */
	private static void maybeFlipComponent(Component c)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).flipOrientation();
		}
	}

	/**
	 * <code>flipOrientation</code> inverts the current orientation of
	 * the panes, recursively flipping <code>FlippingSplitPane</code>
	 * components.
	 */
	private void flipOrientation()
	{
		super.setOrientation(invertOrientation(getOrientation()));
		maybeFlipComponent(getLeftComponent());
		maybeFlipComponent(getRightComponent());

		resetToPreferredSizes(); // gets munched anyway?  XXX
	}

	/**
	 * Set orientation for <code>FlippingSplitPane</code> components;
	 * do nothing for other components (not even
	 * <code>JSplitPane</code> components).
	 *
	 * @param c <code>Component</code>, the component
	 * @param newOrientation <code>int</code>, the orientation
	 */
	private static void maybeSetOrientationComponent(Component c, int newOrientation)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).setOrientation(newOrientation);
		}
	}

	/**
	 * <code>setOrientation</code> recursively calls {@link
	 * JSplitPane#setOrientation(int)} on
	 * <code>FlippingSplitPane</code> components, alternating the
	 * orietation so as to achieve a "criss-cross" affect.
	 *
	 * @param newOrientation <code>int</code>, the orientation
	 * @exception IllegalArgumentException if orientation is not one of:
	 *        HORIZONTAL_SPLIT or VERTICAL_SPLIT.
	 * @beaninfo
	 *        bound: true
	 *  description: The orientation, or how the splitter is divided.
	 *         enum: HORIZONTAL_SPLIT JSplitPane.HORIZONTAL_SPLIT
	 *               VERTICAL_SPLIT   JSplitPane.VERTICAL_SPLIT
	 *
	 * @see #getOrientation
	 */
	public void setOrientation(int newOrientation)
	{
		if (newOrientation == getOrientation())
		{
			return;
		}

		super.setOrientation(newOrientation);

		int subOrientation = invertOrientation(newOrientation);
		maybeSetOrientationComponent(getLeftComponent(), subOrientation);
		maybeSetOrientationComponent(getRightComponent(), subOrientation);
	}

	/**
	 * Set one touch expandable for <code>FlippingSplitPane</code>
	 * components; do nothing for other components (not even
	 * <code>JSplitPane</code> components).
	 *
	 * @param c <code>Component</code>, the component
	 * @param newOneTouchExpandable <code>boolean</code>, the setting
	 */
	private static void maybeSetOneTouchExpandableComponent(Component c, boolean newOneTouchExpandable)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).setOneTouchExpandable(newOneTouchExpandable);
		}
	}

	/**
	 * <code>setOneTouchExpandable</code> recursively calls {@link
	 * JSplitPane#setOneTouchExpandable(boolean)} on
	 * <code>FlippingSplitPane</code> components.
	 *
	 * @param newOneTouchExpandable <code>boolean</code>, the setting
	 *
	 * @beaninfo
	 *        bound: true
	 *  description: UI widget on the divider to quickly
	 *               expand/collapse the divider.
	 *
	 * @see #isOneTouchExpandable
	 */
	public void setOneTouchExpandable(boolean newOneTouchExpandable)
	{
		if (newOneTouchExpandable == isOneTouchExpandable())
		{
			return;
		}

		super.setOneTouchExpandable(newOneTouchExpandable);
		maybeSetOneTouchExpandableComponent(getLeftComponent(), newOneTouchExpandable);
		maybeSetOneTouchExpandableComponent(getRightComponent(), newOneTouchExpandable);
	}

	/**
	 * Set continuous layout for <code>FlippingSplitPane</code>
	 * components; do nothing for other components (not even
	 * <code>JSplitPane</code> components).
	 *
	 * @param c <code>Component</code>, the component
	 * @param newContinuousLayout <code>boolean</code>, the setting
	 */
	private static void maybeSetContinuousLayoutComponent(Component c, boolean newContinuousLayout)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).setContinuousLayout(newContinuousLayout);
		}
	}

	/**
	 * <code>setContinuousLayout</code> recursively calls {@link
	 * JSplitPane#setContinuousLayout(boolean)} on
	 * <code>FlippingSplitPane</code> components.
	 *
	 * @param newContinuousLayout <code>boolean</code>, the setting
	 *
	 * @beaninfo
	 *        bound: true
	 *  description: Whether the child components are
	 *               continuously redisplayed and laid out during
	 *               user intervention.
	 *
	 * @see #isContinuousLayout
	 */
	public void setContinuousLayout(boolean newContinuousLayout)
	{
		if (newContinuousLayout == isContinuousLayout())
		{
			return;
		}

		super.setContinuousLayout(newContinuousLayout);
		maybeSetContinuousLayoutComponent(getLeftComponent(), newContinuousLayout);
		maybeSetContinuousLayoutComponent(getRightComponent(), newContinuousLayout);
	}

	/**
	 * Gets the <code>locked</code> property.
	 *
	 * @return the value of the <code>locked</code> property
	 *
	 * @see #setLocked
	 */
	private boolean isLocked()
	{
		return locked;
	}

	/**
	 * Set locked for <code>FlippingSplitPane</code> components; do
	 * nothing for other components.
	 *
	 * @param c <code>Component</code>, the component
	 * @param locked <code>boolean</code>, the setting
	 */
	private static void maybeSetLockedComponent(Component c, boolean locked)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).setLocked(locked);
		}
	}

	/**
	 * Sets the value of the <code>locked</code> property, which must
	 * be <code>true</code> for the child components to be locked
	 * against changes.  The default value of this property is
	 * <code>false</code>.
	 *
	 * @param locked <code>int</code>, the setting
	 *
	 * @beaninfo
	 *        bound: true
	 *  description: Whether the child components are
	 *               locked against changes.
	 *
	 * @see #isLocked
	 */
	private void setLocked(boolean locked)
	{
		if (locked == isLocked())
		{
			return;
		}

		// Workaround so that you can't drag the divider when locked.
		if (this.locked = locked)
		{
			wasContinuousLayout = isContinuousLayout();
			setContinuousLayout(true);
		}
		else
		{
			setContinuousLayout(wasContinuousLayout);
		}

		maybeSetLockedComponent(getLeftComponent(), isLocked());
		maybeSetLockedComponent(getRightComponent(), isLocked());
	}

	/**
	 * <code>setDividerLocation</code> calls {@link
	 * JSplitPane#setDividerLocation(int)} unless the
	 * <code>FlippingSplitPane</code> is locked.
	 *
	 * @param location <code>int</code>, the location
	 *
	 * @beaninfo
	 *        bound: true
	 *  description: The location of the divider.
	 *
	 * @see #getDividerLocation
	 */
	public void setDividerLocation(int location)
	{
		if (isLocked())
		{
			super.setDividerLocation(getLastDividerLocation());
		}
		else
		{
			super.setDividerLocation(location);
		}
	}
}
