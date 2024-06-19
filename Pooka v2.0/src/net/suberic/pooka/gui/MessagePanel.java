package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.util.*;
import net.suberic.util.swing.*;
import net.suberic.util.gui.ConfigurableKeyBinding;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.MetalTheme;

/**
 * The message panel.
 *
 */

public class MessagePanel extends JDesktopPane implements ContentPanel, ThemeSupporter, ThemeListener {
  /**
   * ExtendedDesktopManager is just a Desktop Manager which also
   * calls refreshActiveMenus() and refreshCurrentUser()  when the
   * focus is changed.  It also selects the last window selected when
   * the currently selected window closes.
   *
   */
  class ExtendedDesktopManager extends net.suberic.util.swing.ScrollingDesktopManager {

    public ExtendedDesktopManager(JDesktopPane pane, JScrollPane scrollPane) {
      super(pane, scrollPane);
    }

    /**
     * This refreshes the active menus and the current user to match
     * the newly selected frame.
     *
     * Overrides DefaultDesktopManager.activateFrame(JInternalFrame f).
     */
    public void activateFrame(JInternalFrame f) {
      super.activateFrame(f);
    }

    /**
     * In addition to closing the current Frame, this also activates
     * another InternalFrame and updates the active menus.
     *
     * Overrides DefaultDesktopManager.closeFrame(JInternalFrame f).
     */
    public void closeFrame(JInternalFrame f) {
      super.closeFrame(f);
      JInternalFrame allFrames[] = getAllFrames();
      if (allFrames.length > 0 && allFrames[0] != null)
        try {
          allFrames[0].setSelected(true);
        } catch (java.beans.PropertyVetoException pve) {
        }
    }

  }

  // end internal class ExtendedDesktopManager

  JComponent UIComponent;
  ConfigurableKeyBinding keyBindings;
  boolean savingWindowLocations = false;
  boolean savingOpenFolders = false;
  MetalTheme currentTheme = null;

  /**
   * Creates a new MessagePanel.
   */
  public MessagePanel() {
    //this.setAutoscrolls(true);
    this.setSize(1000, 1000);

    keyBindings = new ConfigurableKeyBinding(this, "ContentPanel.keyBindings", Pooka.getResources());
    //keyBindings.setCondition(JComponent.WHEN_IN_FOCUSED_WINDOW);
    keyBindings.setActive(getActions());

    Pooka.getHelpBroker().enableHelpKey(this, "ui.messagePanel", Pooka.getHelpBroker().getHelpSet());

    // if the MessagePanel itself ever gets focus, pass it on to the
    // selected JInternalFrame.

    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          JInternalFrame selectedFrame = getCurrentWindow();
          if (selectedFrame != null) {
            java.util.logging.Logger.getLogger("Pooka.debug.gui.focus").fine("sending focus from MessagePanel to " + selectedFrame);
            selectedFrame.requestFocusInWindow();
          }
        }
      });

    this.setSavingWindowLocations(Pooka.getProperty("Pooka.saveFolderWindowLocationsOnExit", "false").equalsIgnoreCase("true"));
    this.setSavingOpenFolders(Pooka.getProperty("Pooka.saveOpenFoldersOnExit", "false").equalsIgnoreCase("true"));

    // set up the colors
    configureInterfaceStyle();
  }

  /**
   * Creates a new MessagePanel from an already created
   * PreviewContentPanel.
   */
  public MessagePanel(PreviewContentPanel pcp) {
    this ();

    // go through each folder on the PreviewContentPanel.

    java.util.HashMap cardTable = pcp.getCardTable();
    Set keys = cardTable.keySet();
    Iterator keyIt = keys.iterator();
    while (keyIt.hasNext()) {
      String folderID = (String) keyIt.next();
      PreviewFolderPanel pfp = (PreviewFolderPanel) cardTable.get(folderID);
      FolderInternalFrame fif = new FolderInternalFrame(pfp, this);
      pfp.getFolderInfo().setFolderDisplayUI(fif);
      openFolderWindow(fif);
    }

    PreviewFolderPanel current = pcp.getCurrentPanel();
    if (current != null) {
      current.getFolderDisplay().getMessageTable().getSelectionModel().removeListSelectionListener(pcp.getSelectionListener());
    }
  }

  /**
   * Saves the panel size.  A no-op for this.
   */
  public void savePanelSize() {
    //
  }


  /**
   * This opens a new FolderWindow for the given FolderInfo, and sets
   * it as the selected window.
   */
  public void openFolderWindow(FolderInternalFrame f) {
    openFolderWindow(f, true);
  }

  /**
   * This opens a new FolderWindow for the given FolderInfo.  If
   * selectWindow is set to true, then the window is also automatically
   * selected; if set to false, the folderID.windowLocation.selected
   * property is used, if set.
   */
  public void openFolderWindow(FolderInternalFrame newFolderWindow, boolean selectWindow) {

    if (! SwingUtilities.isEventDispatchThread()) {
      System.err.println("running openFolderWindow while not on EventDispatchThread.");
      Thread.currentThread().dumpStack();
    }
    if (newFolderWindow.getDesktopPane() != this) {
      setLayer(newFolderWindow, JLayeredPane.DEFAULT_LAYER.intValue());
      String folderProperty = newFolderWindow.getFolderInfo().getFolderProperty();
      try {
        int x = Integer.parseInt(Pooka.getProperty(folderProperty + ".windowLocation.x"));
        int y = Integer.parseInt(Pooka.getProperty(folderProperty + ".windowLocation.y"));

        newFolderWindow.setLocation(x, y);
      } catch (Exception e) {
        newFolderWindow.setLocation(getNewWindowLocation(newFolderWindow, false));
      }

      if (!newFolderWindow.isVisible())
        newFolderWindow.setVisible(true);

      if (getComponentCount() == 0)
        selectWindow = true;

      this.add(newFolderWindow);
    } else {
      if (!newFolderWindow.isVisible())
        newFolderWindow.setVisible(true);
    }

    if (newFolderWindow.isIcon()) {
      try {
        newFolderWindow.setIcon(false);
      } catch (java.beans.PropertyVetoException e) {
      }
    }

    if (selectWindow) {
      try {
        newFolderWindow.setSelected(true);
      } catch (java.beans.PropertyVetoException e) {
      }
    }
  }


  /**
   * This returns an available location for JComponent c to be placed
   * in the MessageWindow.
   *
   * At the moment it just returns 0,0.  :)
   */
  public Point getNewWindowLocation(JComponent c, boolean center) {
    int baseDelta = 20;
    // first, figure out what the top left corner of the viewable area is.

    if (getUIComponent() != null) {
      JViewport viewport = ((JScrollPane)getUIComponent()).getViewport();

      Point p = viewport.getViewPosition();

      // second, see what the minimum/maximum x and y coordinate would be.

      Dimension componentSize = c.getSize();

      Dimension viewportSize = viewport.getViewSize();


      if (! center) {
        int maxX = (Math.max(0, viewportSize.width - componentSize.width) + p.x);
        int maxY = (Math.max(0, viewportSize.height - componentSize.height) + p.y);

        if (maxX - p.x <= baseDelta && maxY - p.y <= baseDelta)
          return p;

        Point returnValue = new Point(p.x, p.y);

        // get all of the locations of the frames.

        JInternalFrame[] allFrames = getAllFrames();

        if (allFrames.length > 0) {
          ArrayList problemPoints = new ArrayList();

          for (int i = 0; i < allFrames.length; i++) {
            if (allFrames[i] != c) {
              Point currentLoc = allFrames[i].getLocation();

              if (currentLoc.x <= maxX + baseDelta && currentLoc.x > p.x - baseDelta && currentLoc.y <= maxY + baseDelta && currentLoc.y > p.y - baseDelta) {
                problemPoints.add(currentLoc);
              }
            }
          }

          if (problemPoints.size() > 0) {
            // this means that we'll actually have to find a place to put this...

            boolean spotfound = false;
            // first run down the diagonals
            for (int delta = 0; ! spotfound && p.x + delta <= maxX && p.y + delta <= maxY; delta += baseDelta) {
              if (checkSpot(p.x + delta, p.y + delta, problemPoints)) {
                spotfound = true;
                returnValue = new Point(p.x + delta, p.y + delta);
              }
            }

            // if that didn't work, then go through the painstaking process of
            // trying out all available spots.

            if (!spotfound) {
              for (int xdelta = 0; ! spotfound && p.x + xdelta <= maxX; xdelta += baseDelta) {
                for (int ydelta = 0; ! spotfound && p.y + ydelta <= maxY; ydelta += baseDelta) {
                  if (checkSpot(p.x + xdelta, p.y + ydelta, problemPoints)) {
                    spotfound = true;
                    returnValue = new Point(p.x + xdelta, p.y + ydelta);
                  }
                }

              }
            }

            // if that didn't work, then try to find a place relative to the
            // top pane.

            if (! spotfound) {
              JInternalFrame jif = getSelectedFrame();
              if (jif != null) {
                Point selectedPoint = jif.getLocation();
                // check to make sure it's not off the screen
                if (selectedPoint.x >= p.x && selectedPoint.y >= p.y) {
                  // normalize the point.
                  int xdelta = selectedPoint.x - p.x;
                  int ydelta = selectedPoint.y - p.y;
                  int mindelta = Math.min(xdelta, ydelta);
                  Point basePoint = new Point(p.x + mindelta, p.y + mindelta);
                  if (basePoint.x + baseDelta <= maxX && basePoint.y + baseDelta <= maxY) {
                    returnValue = new Point(basePoint.x + baseDelta, basePoint.y + baseDelta);
                  } else if (basePoint.x + baseDelta <= maxX && basePoint.y <= maxY) {
                    returnValue = new Point(basePoint.x + baseDelta, basePoint.y);
                  } else if (basePoint.x <= maxX && basePoint.y + baseDelta <= maxY) {
                    returnValue = new Point(basePoint.x, basePoint.y + baseDelta);
                  }

                  // and if none of those work, screw it.

                }
              }
            }
          }
        }
        return returnValue;
      } else { // if center

        int diffWidth = Math.max(viewportSize.width - componentSize.width, 0);
        int diffHeight = Math.max(viewportSize.height - componentSize.height, 0);

        Point returnValue = new Point(p.x + (diffWidth / 2), p.y + (diffHeight / 2));

        return returnValue;
      }
    } else {
      return new Point(0, 0);
    }
  }

  /**
   * Checks to see if any other windows are with 5 of the given point.
   */
  private boolean checkSpot(int pX, int pY, java.util.List problemPoints) {
    boolean ok = true;
    int baseDelta = 20;
    for (int i = 0; ok && i < problemPoints.size(); i++) {
      Point currentPoint = (Point) problemPoints.get(i);
      if (currentPoint.x > pX - baseDelta && currentPoint.x < pX + baseDelta && currentPoint.y > pY - baseDelta && currentPoint.y < pY + baseDelta) {
        ok = false;
      }
    }

    return ok;
  }

  /**
   * This gets the FolderInfo associated with each name in the
   * folderList Vector, and attempts to open the FolderWindow for
   * each.
   *
   * Normally called at startup if Pooka.openSavedFoldersOnStartup
   * is set.
   */
  public void openSavedFolders(Vector folderList) {
    if (folderList != null) {
      Map<StoreInfo, java.util.List<FolderInfo>> storeFolderMap = new HashMap<StoreInfo, java.util.List<FolderInfo>>();
      for (int i = 0; i < folderList.size(); i++) {
        FolderInfo fInfo = Pooka.getStoreManager().getFolderById((String)folderList.elementAt(i));
        if (fInfo != null) {
          StoreInfo si = fInfo.getParentStore();
          if (si != null) {
            java.util.List<FolderInfo> fList = storeFolderMap.get(si);
            if (fList == null) {
              fList = new LinkedList<FolderInfo>();
              storeFolderMap.put(si, fList);
            }
            fList.add(fInfo);
            FolderNode fNode = fInfo.getFolderNode();
            if (fNode != null)
              fNode.makeVisible();
          }
        }
      }

      Iterator<StoreInfo> sIter = storeFolderMap.keySet().iterator();
      while (sIter.hasNext()) {
        final StoreInfo sInfo = sIter.next();
        final java.util.List<FolderInfo> fList = storeFolderMap.get(sInfo);
        sInfo.getStoreThread().addToQueue(new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent ae) {
              sInfo.openFolders(fList);
            }
          }, new java.awt.event.ActionEvent(this, 0, "folders-open"));
      }

    }
  }

  /**
   * This opens up the MessageWindow for MessageProxy m and then sets
   * it to being the selected window.
   *
   * If no MessageWindow exists for the MessageProxy, a new MessageWindow
   * for it is created.  If one does exist, then that window is
   * de-iconified (if necessary) and selected.
   */
  public void openMessageWindow(MessageProxy m, boolean newMessage) {
    JInternalFrame messageWindow = (JInternalFrame) m.getMessageUI();
    if (messageWindow == null) {
      try {
        MessageUI mui = Pooka.getUIFactory().createMessageUI(m);
        m.setMessageUI(mui);
        messageWindow = (JInternalFrame) mui;
        newMessage = true;
      } catch (OperationCancelledException oce) {
      } catch (MessagingException me) {
        Pooka.getUIFactory().showError(Pooka.getProperty("error.MessageInternalFrame.errorLoadingMessage", "Error loading Message:  ") + "\n" + me.getMessage(), Pooka.getProperty("error.MessageInternalFrame.errorLoadingMessage.title", "Error loading message."), me);
      }
    }

    if (messageWindow != null) {
      final JInternalFrame newMessageWindow = messageWindow;
      final boolean isNew = newMessage;

      Runnable openWindowCommand = new Runnable() {
          public void run() {
            if (isNew) {

              MessagePanel.this.add(newMessageWindow);
              Point p = getNewWindowLocation(newMessageWindow, false);
              newMessageWindow.setLocation(p);
              newMessageWindow.setVisible(true);
            } else {
              if (newMessageWindow.isIcon())
                try {
                  newMessageWindow.setIcon(false);
                } catch (java.beans.PropertyVetoException e) {
                }
            }

            try {
              newMessageWindow.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {
            }
          }
        };
      if (SwingUtilities.isEventDispatchThread())
        openWindowCommand.run();
      else
        try {
          SwingUtilities.invokeAndWait(openWindowCommand);
        } catch (Exception e) {
          // shouldn't happen.
        }
    }
  }

  /**
   * Calls createNewMessage(Message m) with a new MimeMessage object.
   */
  public void createNewMessage() {
    createNewMessage(new MimeMessage(Pooka.getMainPanel().getSession()));
  }

  /**
   * Creates a NewMessageProxy and NewMessageWindow for the given
   * Message object.  Also will open the NewMessageWindow on the
   * MessagePanel and set it as Active.
   */
  public void createNewMessage(javax.mail.Message m) {
    NewMessageProxy nmp = new NewMessageProxy(new NewMessageInfo(m));
    openMessageWindow(nmp, true);
  }

  /**
   * This saves the location of all FolderWindows, so that the next
   * time we start up, we can put the windows in the proper places.
   */

  public void saveWindowLocations() {
    JInternalFrame[] allFrames = getAllFrames();

    for(int i = 0; i < allFrames.length; i++) {
      if (allFrames[i] instanceof FolderInternalFrame) {
        FolderInternalFrame fif = (FolderInternalFrame)allFrames[i];
        //saveWindowLocation(fif);
        fif.saveWindowSettings();
      }
    }

  }

  /**
   * This saves a list of open folders, so that on future startup we
   * can automatically reopen them.
   */

  public void saveOpenFolders() {
    JInternalFrame[] allFrames = getAllFrames();
    boolean isFirst = true;

    StringBuffer savedFolderValues = new StringBuffer();
    for(int i = 0; i < allFrames.length; i++) {
      if (allFrames[i] instanceof FolderDisplayUI) {
        String folderID = ((FolderDisplayUI)allFrames[i]).getFolderInfo().getFolderID();
        if (! isFirst)
          savedFolderValues.append(":");

        isFirst = false;

        savedFolderValues.append(folderID);
      }
    }

    Pooka.setProperty("Pooka.openFolderList", savedFolderValues.toString());

  }

  /**
   * This returns the currently selected window for this JDesktopPane.
   */
  public JInternalFrame getCurrentWindow() {
    JInternalFrame[] allFrames = getAllFrames();

    for(int i = 0; i < allFrames.length; i++) {
      if (allFrames[i].isSelected())
        return allFrames[i];
    }

    return null;
  }

  /**
   * This makes the next JInternalFrame in the list be selected.
   */
  public void selectNextWindow() {
    JInternalFrame[] allFrames = getAllFrames();

    if (allFrames.length > 0) {
      for(int i = 0; i < allFrames.length; i++) {
        if (allFrames[i].isSelected()) {
          JInternalFrame selected = allFrames[i];
          JInternalFrame newSelected = allFrames[i + 1 % allFrames.length];
          try {
            setPosition(selected, allFrames.length -1);
            newSelected.setSelected(true);
          } catch (java.beans.PropertyVetoException e) {
          }

          return;
        }
      }

      // if we get to this point, it means that there are windows,
      // but none of them are selected.

      try {
        allFrames[0].setSelected(true);
      } catch (java.beans.PropertyVetoException e) {
      }
    }
  }

  /**
   * This makes the previous JInternalFrame in the list be selected.
   */
  public void selectPreviousWindow() {
    JInternalFrame[] allFrames = getAllFrames();

    if (allFrames.length > 0) {
      for(int i = 0; i < allFrames.length; i++) {
        if (allFrames[i].isSelected()) {
          int j;
          if (i > 0)
            j = i-1;
          else
            j = allFrames.length -1;
          try {
            allFrames[j].setSelected(true);
          } catch (java.beans.PropertyVetoException e) {
          }

          return;
        }
      }

      // if we get to this point, it means that there are windows,
      // but none of them are selected.

      try {
        allFrames[0].setSelected(true);
      } catch (java.beans.PropertyVetoException e) {
      }
    }
  }

  /**
   * This moves the current window either 1 or 10 spaces up, down,
   * left, or right, depending on the source of the event.
   */

  public void moveWindow(int modifiers, String cmd) {
    JInternalFrame current = getCurrentWindow();

    if (current != null) {
      int x = current.getX();
      int y = current.getY();

      int moveValue = 1;

      if ((modifiers & ActionEvent.SHIFT_MASK) != 0)
        moveValue = 10;

      if (cmd.equals("left"))
        x = x - moveValue;
      else if (cmd.equals("right"))
        x = x + moveValue;
      else if (cmd.equals("up"))
        y = y - moveValue;
      else if (cmd.equals("down"))
        y = y + moveValue;

      current.setLocation(x, y);
    }
  }

  /**
   * Unselects the given JInternalFrame and selects the frame now on
   * top.
   */
  public void unselectAndMoveToBack(JInternalFrame pFrame) {
    pFrame.moveToBack();
    if (pFrame.isSelected()) {
      try {
        pFrame.setSelected(false);
      } catch (Exception e) {
        java.util.logging.Logger.getLogger("Pooka.debug.gui").fine("error unselecting frame.");
      }
      int topLayer = highestLayer();
      JInternalFrame[] onTop = getAllFramesInLayer(topLayer);
      for (int i = 0; onTop != null && i < onTop.length; i++) {
        if (onTop[i] != null && onTop[i] != pFrame) {
          try {
            onTop[i].setSelected(true);
          } catch (Exception e) {
            java.util.logging.Logger.getLogger("Pooka.debug.gui").fine("selecting frame.");
          }
          break;
        }
      }
    }
  }


  /**
   * This method shows a help screen.  At the moment, it just takes the
   * given URL, creates a JInteralFrame and a JEditorPane, and then shows
   * the doc with those components.
   */
  public void showHelpScreen(String title, java.net.URL url) {
    JInternalFrame jif = new JInternalFrame(title, true, true, true);
    JEditorPane jep = new JEditorPane();
    try {
      jep.setPage(url);
    } catch (IOException ioe) {
      jep.setText(Pooka.getProperty("err.noHelpPage", "No help available."));
    }
    jep.setEditable(false);
    jif.setSize(500,500);
    jif.getContentPane().add(new JScrollPane(jep));
    this.add(jif);
    jif.setVisible(true);
    try {
      jif.setSelected(true);
    } catch (java.beans.PropertyVetoException e) {
    }

  }

  /**
   * Configures the interfaceStyle for this Pane.
   */
  public void configureInterfaceStyle() {
    Runnable runMe = new Runnable() {
        public void run() {
          try {
            Pooka.getUIFactory().getPookaThemeManager().updateUI(MessagePanel.this, MessagePanel.this);
          } catch (Exception e) {
          }
        }
      };

    if (! SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(runMe);
    } else {
      runMe.run();
    }
  }

  /**
   * Gets the Theme object from the ThemeManager which is appropriate
   * for this UI.
   */
  public MetalTheme getTheme(ThemeManager tm) {
    String id = Pooka.getProperty("Pooka.messagePanel.theme", "");
    if (id != null && ! id.equals("")) {
      return tm.getTheme(id);
    }

    return tm.getDefaultTheme();
  }

  /**
   * Gets the currently configured Theme.
   */
  public MetalTheme getCurrentTheme() {
    return currentTheme;
  }

  /**
   * Sets the Theme that this component is currently using.
   */
  public void setCurrentTheme(MetalTheme newTheme) {
    if (currentTheme != null && currentTheme instanceof ConfigurableMetalTheme) {
      ((ConfigurableMetalTheme)currentTheme).removeThemeListener(this);
    }
    currentTheme = newTheme;

    if (currentTheme != null && currentTheme instanceof ConfigurableMetalTheme) {
      ((ConfigurableMetalTheme)currentTheme).addThemeListener(this);
    }
  }

  /**
   * Called when the specifics of a Theme change.
   */
  public void themeChanged(ConfigurableMetalTheme theme) {
    // we should really only be getting messages from our own curren themes,
    // but, hey, it never hurts to check.
    if (currentTheme != null && currentTheme == theme) {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            try {
              Pooka.getUIFactory().getPookaThemeManager().updateUI(MessagePanel.this, MessagePanel.this);
            } catch (Exception e) {
            }
          }
        });
    }
  }

  /**
   * Refreshes any submenus that need to be refreshed.
   */
  public void refreshActiveMenus() {

  }

  /**
   * Returns the UIComponent for this ContentPanel.
   */
  public JComponent getUIComponent() {
    return UIComponent;
  }

  /**
   * Sets the UIComponent for this ContentPanel.
   */
  public void setUIComponent(JComponent newComp) {
    UIComponent = newComp;
  }

  /**
   * As defined in net.suberic.pooka.UserProfileContainer.
   */

  public UserProfile getDefaultProfile() {
    JInternalFrame cw = getCurrentWindow();
    if (cw != null && cw instanceof UserProfileContainer)
      return ((UserProfileContainer)cw).getDefaultProfile();
    else
      return null;
  }

  /**
   * This returns whether or not we want to save the location of windows
   * so we can use them again at startup.
   */

  public boolean isSavingWindowLocations() {
    return savingWindowLocations;
  }

  /**
   * This sets whether or not we want to save the locations of windows
   * for later use.
   */
  public void setSavingWindowLocations(boolean newValue) {
    savingWindowLocations = newValue;
  }

  /**
   * This returns whether or not we want to save which folders are open
   * so we can use them again at startup.
   */

  public boolean isSavingOpenFolders() {
    return savingOpenFolders;
  }

  /**
   * This sets whether or not we want to save which folders are open
   * for later use.
   */
  public void setSavingOpenFolders(boolean newValue) {
    savingOpenFolders = newValue;
  }

  public Action[] defaultActions = {
    new NextWindowAction(),
    new PreviousWindowAction(),
    new MoveWindowAction("move-window-left"),
    new MoveWindowAction("move-window-right"),
    new MoveWindowAction("move-window-up"),
    new MoveWindowAction("move-window-down")
  };

  public Action[] getDefaultActions() {
    return defaultActions;
  }

  public Action[] getActions() {
    JInternalFrame cw = getCurrentWindow();

    if (cw != null) {
      Action[] windowActions = null;
      if (cw instanceof ActionContainer)
        windowActions = ((ActionContainer)cw).getActions();

      if (windowActions != null)
        return TextAction.augmentList(windowActions, getDefaultActions());

    }
    return getDefaultActions();
  }

  public class NextWindowAction extends AbstractAction {
    NextWindowAction() {
      super("window-next");
    }

    public void actionPerformed(ActionEvent e) {
      selectNextWindow();
    }
  }

  public class PreviousWindowAction extends AbstractAction {
    PreviousWindowAction() {
      super("window-previous");
    }

    public void actionPerformed(ActionEvent e) {
      selectPreviousWindow();
    }
  }

  public class MoveWindowAction extends AbstractAction {
    MoveWindowAction() {
      super("move-window");
    }

    MoveWindowAction(String cmdString) {
      super(cmdString);
    }

    public void actionPerformed(ActionEvent e) {
      String cmdString = e.getActionCommand();
      moveWindow(e.getModifiers(), cmdString.substring(cmdString.lastIndexOf("-") +1));
    }
  }

}






