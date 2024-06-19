package net.suberic.util.swing;
import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.Method;

/**
 * <p>
 * This is a DesktopManager for a JDesktopPane which dynamically resizes
 * when a JInternalFrame is moved out of the visible portion of the
 * desktop.  This means that all parts of your JInteralFrames will be
 * available via the ScrollBars at all time.
 * </p>
 *
 * <p>
 * Currently, to use this class you need to set it as the DesktopManager
 * of your JDesktopPane, and also register the JDesktopPane and its
 * JScrollPane with this ScrollingDesktopManager:
 * </p>
 *
 * <pre>
 * JDesktopPane desktopPane = new JDesktopPane();
 * JScrollPane scrollPane = new JScrollPane(desktopPane,
 *   JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,
 *   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
 * ScrollingDesktopManager manager = new ScrollingDesktopManager(desktopPane,
 *   scrollPane);
 * desktopPane.setDesktopManager(manager);
 * </pre>
 *
 * <p>
 * Note that it only makes sense to use this class with the
 * SCROLLBAR_AS_NEEDED and SCROLLBAR_ALWAYS options on the JScrollPane.
 * </p>
 *
 * @see javax.swing.JDesktopPane
 * @see javax.swing.JScrollBar
 * @see javax.swing.DefaultDesktopManager
 * @version 1.0 6/28/2000
 * @author Allen Petersen
 */

public class ScrollingDesktopManager extends DefaultDesktopManager
  implements ContainerListener, AdjustmentListener {

  private JScrollPane scrollPane = null;

  private JDesktopPane pane = null;

  private boolean updating = false;

  private static Integer SIMPLE_SCROLL_MODE;

  /**
   * <p>
   * This creates a ScrollingDesktopManager for JDesktopPane pane
   * in JScrollPane scrollPane.
   * </p>
   */
  public ScrollingDesktopManager(JDesktopPane pane, JScrollPane scrollPane) {
    super();
    setDesktopPane(pane);
    setScrollPane(scrollPane);
  }

  /**
   * <p>This extends the behaviour of DefaultDesktopManager by
   * calling <code>updateDesktopSize()</code> after
   * completing its action.
   *
   * Overrides
   * <code>javax.swing.DefaultDesktopManager.closeFrame(JInternalFrame f).
   * </code>
   */
  public void closeFrame(JInternalFrame f) {
    super.closeFrame(f);
    updateDesktopSize();

    // workaround for bug in jdk 1.5 (sigh)
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {

          java.awt.KeyboardFocusManager mgr = java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager();
          pane.requestFocusInWindow();
        }
      });
  }

  /**
   * <p>This extends the behaviour of DefaultDesktopManager by
   * calling <code>updateDesktopSize()</code> after
   * completing its action.
   *
   * Overrides
   * <code>javax.swing.DefaultDesktopManager.minimizeFrame(JInternalFrame f).
   * </code>
   */
  public void minimizeFrame(JInternalFrame f) {
    super.minimizeFrame(f);
    updateDesktopSize();

  }

  /**
   * <p>This extends the behaviour of DefaultDesktopManager by
   * calling <code>updateDesktopSize()</code> after
   * completing its action.
   *
   * Overrides
   * <code>javax.swing.DefaultDesktopManager.iconifyFrame(JInternalFrame f).
   * </code>
   */
  public void iconifyFrame(JInternalFrame f) {
    super.iconifyFrame(f);
    updateDesktopSize();
  }

  /**
   * <p>This extends the behaviour of DefaultDesktopManager by
   * calling <code>updateDesktopSize()</code> after
   * completing its action.
   *
   * Overrides
   * <code>javax.swing.DefaultDesktopManager.deiconifyFrame(JInternalFrame f).
   * </code>
   */
  public void deiconifyFrame(JInternalFrame f) {
    super.deiconifyFrame(f);
    updateDesktopSize();
  }

  /**
   * <p>This extends the behaviour of DefaultDesktopManager by
   * calling <code>updateDesktopSize()</code> after
   * completing its action.
   *
   * Overrides
   * <code>javax.swing.DefaultDesktopManager.endDraggingFrame(JComponent f).
   * </code>
   */
  public void endDraggingFrame(JComponent f) {
    super.endDraggingFrame(f);

    updateDesktopSize();
  }

  /**
   * <p>This extends the behaviour of DefaultDesktopManager by
   * calling <code>updateDesktopSize()</code> after
   * completing its action.
   *
   * Overrides
   * <code>javax.swing.DefaultDesktopManager.endResizingFrame(JComponent f).
   * </code>
   */

  public void endResizingFrame(JComponent f) {
    super.endResizingFrame(f);
    updateDesktopSize();
  }

  /**
   * <p>This overrides maximizeFrame() such that it only maximizes to the
   * size of the Viewport, rather than to the entire size of the
   * JDesktopPane.</p>
   *
   * Overrides
   * <code>javax.swing.DefaultDesktopManager.maximizeFrame(JInternalFrame f)
   * </code>
   */
  public void maximizeFrame(JInternalFrame f) {
    if (scrollPane != null) {
      java.awt.Dimension newSize = scrollPane.getViewport().getSize();
      pane.setSize(newSize);
      pane.setPreferredSize(newSize);
    }

    super.maximizeFrame(f);

  }

  /**
   * Implements componentRemoved() as an empty method.  This is necessary
   * because, in order to move components between layers, JLayeredPane
   * removes the component from one layer and then adds it to the other
   * layer.  This can lead to problems, as the desktop may resize and
   * remove the area where the pane was going to be replaced.
   *
   * Fortunately, closeFrame() is called when a JInternalFrame is
   * actually removed from the JDesktopPane, so it should be safe to
   * ignore the componentRemoved events.
   */
  public void componentRemoved(java.awt.event.ContainerEvent e) {
    //updateDesktopSize();
  }

  /**
   * Implements componentAdded() to call updateDesktopSize().
   */
  public void componentAdded(java.awt.event.ContainerEvent e) {
    updateDesktopSize();
  }

  /**
   * Implements adjustmentValueChanged() to call updateDesktopSize().
   */
  public void adjustmentValueChanged(java.awt.event.AdjustmentEvent e) {
    updateDesktopSize();
  }

  /**
   * This actually does the updating of the parent JDesktopPane.
   */
  public void updateDesktopSize() {
    if (!updating && scrollPane != null && scrollPane.isShowing()) {
      updating = true;

      JScrollBar hsb = scrollPane.getHorizontalScrollBar();
      JScrollBar vsb = scrollPane.getVerticalScrollBar();

      // calculate the min and max locations for all the frames.
      JInternalFrame[] allFrames = pane.getAllFrames();
      int min_x = 0, min_y = 0, max_x = 0, max_y = 0;
      java.awt.Rectangle bounds = null;
      // add to this the current viewable area.

      if (allFrames.length > 0) {
        bounds = allFrames[0].getBounds(bounds);
        min_x = bounds.x;
        min_y = bounds.y;
        max_x = bounds.width + bounds.x;
        max_y = bounds.height + bounds.y;
        for (int i = 1; i < allFrames.length; i++) {
          bounds = allFrames[i].getBounds(bounds);
          min_x = Math.min(min_x, bounds.x);
          min_y = Math.min(min_y, bounds.y);
          max_x = Math.max(max_x, bounds.width + bounds.x);
          max_y = Math.max(max_y, bounds.height + bounds.y);
        }
      }

      int windowsWidth = max_x;
      int windowsHeight = max_y;

      bounds = scrollPane.getViewport().getViewRect();
      min_x = Math.min(min_x, bounds.x);
      min_y = Math.min(min_y, bounds.y);
      max_x = Math.max(max_x, bounds.width + bounds.x);
      max_y = Math.max(max_y, bounds.height + bounds.y);


      if (min_x != 0 || min_y != 0) {
        for (int i = 0; i < allFrames.length; i++) {
          allFrames[i].setLocation(allFrames[i].getX() - min_x, allFrames[i].getY() - min_y);

        }

        windowsWidth = windowsWidth - min_x;
        windowsHeight = windowsHeight - min_y;
      }

      int hval = hsb.getValue();
      int vval = vsb.getValue();

      bounds = scrollPane.getViewport().getViewRect();
      int oldViewWidth = bounds.width + hval;
      int oldViewHeight = bounds.height + vval;

      int portWidth = scrollPane.getViewport().getSize().width;
      int portHeight = scrollPane.getViewport().getSize().height;

      java.awt.Dimension dim = pane.getSize();
      int oldWidth = dim.width;
      int oldHeight = dim.height;

      pane.setSize(max_x - min_x, max_y - min_y);

      /*********************************/

      int prefWidth = max_x - min_x;
      int prefHeight = max_y - min_y;


      boolean hasVsb = false, needsVsb = false, hasHsb = false, needsHsb = false;
      // if a scrollbar is added, check to see if the space covered
      // by the scrollbar is whitespace or not.  if not, remove that
      // whitespace from the preferredsize.

      if (vsb.isVisible()) {
        hasVsb = true;
      } else {
        hasVsb = false;
      }

      if (hsb.isVisible()) {
        hasHsb = true;
      } else {
        hasHsb = false;
      }

      if (max_x - min_x > scrollPane.getViewport().getViewRect().width)
        needsHsb = true;
      else
        needsHsb = false;

      if (max_y - min_y > scrollPane.getViewport().getViewRect().height)
        needsVsb = true;
      else
        needsVsb = false;

      if (hasVsb == false && needsVsb == true) {
        if (windowsWidth < bounds.width + bounds.x - vsb.getPreferredSize().width) {
          prefWidth-=vsb.getPreferredSize().width;
        }
      } else if (hasVsb == true && needsVsb == false) {
        if (windowsWidth <= bounds.width + bounds.x) {
          prefWidth+= vsb.getPreferredSize().width;
        }
      }

      if (hasHsb == false && needsHsb == true) {
        if (windowsHeight < bounds.height + bounds.y - hsb.getPreferredSize().height) {
          prefHeight-=hsb.getPreferredSize().height;
        }
      } else if (hasHsb == true && needsHsb == false) {
        if (windowsHeight <= bounds.height + bounds.y) {
          prefHeight+= hsb.getPreferredSize().height;
        }
      }

      /**************************************/

      pane.setPreferredSize(new java.awt.Dimension(prefWidth, prefHeight));
      scrollPane.validate();

      hsb = scrollPane.getHorizontalScrollBar();
      vsb = scrollPane.getVerticalScrollBar();

      if (min_x != 0 && hval - min_x + hsb.getModel().getExtent() > hsb.getMaximum()) {
        hsb.setMaximum(hval - min_x + hsb.getModel().getExtent());
      }

      if (min_y != 0 && vval - min_y + vsb.getModel().getExtent() > vsb.getMaximum()) {
        vsb.setMaximum(vval - min_y + vsb.getModel().getExtent());
      }

      hsb.setValue(hval - min_x);

      vsb.setValue(vval - min_y);

      updating = false;
    }
  }

  /**
   * This sets the scrollPane object.  It also removes this as a
   * listener on the previous scrollPane object (if any), and then sets
   * it as an adjustmentListener on the scrollPane's JScrollBars.
   */
  public void setScrollPane(JScrollPane newScrollPane) {
    if (scrollPane != null) {
      scrollPane.getHorizontalScrollBar().removeAdjustmentListener(this);
      scrollPane.getVerticalScrollBar().removeAdjustmentListener(this);
    }
    scrollPane = newScrollPane;
    scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
    scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
  }

  public JScrollPane getScrollPane() {
    return scrollPane;
  }

  /**
   * This sets the desktopPane object.  It also removes this as a
   * listener on the previous desktopPane object (if any), and then sets
   * itself as a ContainerListener on the new JDesktopPane.
   */
  public void setDesktopPane(JDesktopPane newDesktopPane) {
    if (pane != null)
      pane.removeContainerListener(this);
    pane = newDesktopPane;
    pane.addContainerListener(this);
  }

  public JDesktopPane getDesktopPane() {
    return pane;
  }
}


