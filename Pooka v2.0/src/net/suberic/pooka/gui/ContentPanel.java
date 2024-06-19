package net.suberic.pooka.gui;

public interface ContentPanel extends net.suberic.pooka.UserProfileContainer, ActionContainer {
  /**
   * Returns the JComponent for this ContentPanel.
   */
  public javax.swing.JComponent getUIComponent();
  
  /**
   * Sets the JComponent for this ContentPanel.
   */
  public void setUIComponent(javax.swing.JComponent comp);
  
  /**
   * Shows a Help Screen with the given title and URL.
   */
  public void showHelpScreen(String title, java.net.URL url);
  
  /**
   * Opens the given Vector of FolderInfos.
   */
  public void openSavedFolders(java.util.Vector folderList);
  
  public void saveOpenFolders();
  
  public void savePanelSize();
    
  public boolean isSavingOpenFolders();

  /**
   * Refreshes any submenus that need to be refreshed.
   */
  public void refreshActiveMenus();
}
