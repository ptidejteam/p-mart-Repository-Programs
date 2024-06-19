package net.suberic.pooka.gui.propedit;
import net.suberic.util.gui.propedit.*;
import net.suberic.util.VariableBundle;
import net.suberic.pooka.gui.filechooser.*;
import net.suberic.pooka.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import javax.swing.filechooser.FileSystemView;

/**
 * This displays the currently selected folder (if any), along with a
 * button which will bring up a dialog to select another folder.
 */

public class FolderSelectorPane extends LabelValuePropertyEditor {

  JLabel label;
  JTextField valueDisplay;
  JButton inputButton;

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);
    getLogger().fine("property is " + property + "; editorTemplate is " + editorTemplate);

    label = createLabel();

    valueDisplay = new JTextField(originalValue);

    inputButton = createInputButton();

    valueDisplay.setPreferredSize(new java.awt.Dimension(150 - inputButton.getPreferredSize().width, valueDisplay.getMinimumSize().height));

    this.add(label);
    labelComponent = label;
    JPanel tmpPanel = new JPanel();
    SpringLayout layout = new SpringLayout();
    tmpPanel.setLayout(layout);

    tmpPanel.add(valueDisplay);
    tmpPanel.add(inputButton);

    layout.putConstraint(SpringLayout.NORTH, valueDisplay, 0, SpringLayout.NORTH, tmpPanel);
    layout.putConstraint(SpringLayout.WEST, valueDisplay, 0, SpringLayout.WEST, tmpPanel);
    layout.putConstraint(SpringLayout.SOUTH, tmpPanel, 0, SpringLayout.SOUTH, valueDisplay);
    layout.putConstraint(SpringLayout.WEST, inputButton, 5, SpringLayout.EAST, valueDisplay);
    layout.putConstraint(SpringLayout.EAST, tmpPanel, 5, SpringLayout.EAST, inputButton);

    tmpPanel.setPreferredSize(new java.awt.Dimension(150, valueDisplay.getMinimumSize().height));
    tmpPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, valueDisplay.getMinimumSize().height));

    valueComponent = tmpPanel;

    this.add(tmpPanel);

    manager.registerPropertyEditor(property, this);

    updateEditorEnabled();
  }

  /**
   * Creates a button that will bring up a way to select a folder.
   */
  public JButton createInputButton() {
    getLogger().fine("creating an input button.");
    try {
      java.net.URL url = this.getClass().getResource(manager.getProperty("FolderSelectorPane.inputButton.image", "/net/suberic/util/gui/images/More.gif"));
      if (url != null) {
        getLogger().fine("url isn't null.");

        ImageIcon icon = new ImageIcon(url);

        JButton newButton = new JButton(icon);
        getLogger().fine("new button is created.");

        newButton.setPreferredSize(new java.awt.Dimension(icon.getIconHeight(), icon.getIconWidth()));
        newButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
              selectNewFolder();
            }
          });

        getLogger().fine("returning button.");

        return newButton;
      }
    } catch (java.util.MissingResourceException mre) {
    }

    getLogger().fine("error - creating a blank button.");

    JButton newButton = new JButton();
    newButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          selectNewFolder();
        }
      });

    return newButton;
  }

  /**
   * This actually brings up a JFileChooser to select a new Folder for
   * the value of the property.
   */
  public void selectNewFolder() {

    FileSystemView mfsv = createFileSystemView();

    String defaultRoot = valueDisplay.getText();
    if (defaultRoot.equals("")) {
      defaultRoot = "/";
      try {
        String storeName = property.substring(property.indexOf('.') + 1, property.indexOf('.', property.indexOf('.') + 1));
        StoreInfo si = Pooka.getStoreManager().getStoreInfo(storeName);
        if (si != null) {
          defaultRoot = storeName;
        }
      } catch (Exception e) {
      }
    } else {

      if (defaultRoot.lastIndexOf('/') > -1) {
        defaultRoot = defaultRoot.substring(0, defaultRoot.lastIndexOf('/'));
      }
    }

    JFileChooser jfc =
      new JFileChooser(defaultRoot, mfsv);
    jfc.setMultiSelectionEnabled(false);
    jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    // workaround for bug in jdk 1.4
    jfc.setCurrentDirectory(mfsv.createFileObject(defaultRoot));

    int returnValue =
      jfc.showDialog(Pooka.getMainPanel(),
                     Pooka.getProperty("FolderEditorPane.Select",
                                       "Select"));

    if (returnValue == JFileChooser.APPROVE_OPTION) {
      java.io.File wrapper = jfc.getSelectedFile();
      valueDisplay.setText(wrapper.getAbsolutePath());
    }

  }

  /**
   * Creates the FileSystemView appropriate for this file chooser.  This
   * can either be a view of all the stores and their corresponding
   * folders, or just the folders of a single store.  This is determined
   * by the 'selectionRoot' subproperty of the edited property's template.
   */
  public FileSystemView createFileSystemView() {

    FileSystemView returnValue = null;
    boolean justSubscribed = manager.getProperty(editorTemplate + ".onlySubscribed", "true").equalsIgnoreCase("true");

    if (manager.getProperty(editorTemplate + ".selectionRoot", "allStores").equals("allStores")) {
      if (justSubscribed)
        returnValue = new PookaFileSystemView();
      else
        returnValue = new MailFileSystemView();
    } else {
      int prefixSize = manager.getProperty(editorTemplate + ".namePrefix", "Store.").length();
      int suffixSize = manager.getProperty(editorTemplate + ".nameSuffix", ".trashFolder").length();
      String currentStoreName = property.substring(prefixSize, property.length() - suffixSize);
      net.suberic.pooka.StoreInfo currentStore = Pooka.getStoreManager().getStoreInfo(currentStoreName);
      if (currentStore != null) {
        if (justSubscribed)
          returnValue = new PookaFileSystemView(currentStore);
        else
          returnValue = new MailFileSystemView(currentStore);
      }
    }

    return returnValue;
  }

  //  as defined in net.suberic.util.gui.PropertyEditorUI

  public void setValue() throws PropertyValueVetoException {
    validateProperty();
    getLogger().fine("calling fsp.setValue.  isEnabled() = " + isEnabled() + "; isChanged() = " + isChanged());
    if (isEditorEnabled() && isChanged())
      manager.setProperty(property, (String)valueDisplay.getText());
  }

  public void validateProperty() throws PropertyValueVetoException {
    getLogger().fine("calling fsp.validateProperty().  isEnabled() = " + isEditorEnabled() + "; isChanged() = " + isChanged());
    if (isEditorEnabled())
      firePropertyCommittingEvent((String)valueDisplay.getText());
  }

  public java.util.Properties getValue() {
    java.util.Properties retProps = new java.util.Properties();

    retProps.setProperty(property, (String)valueDisplay.getText());

    return retProps;
  }

  public void resetDefaultValue() {
    valueDisplay.setText(originalValue);
  }

  public boolean isChanged() {
    return (!(originalValue.equals(valueDisplay.getText())));
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    getLogger().fine("calling fsp.updateEditorEnabled().  isEditorEnabled() = " + isEditorEnabled());
    if (inputButton != null) {
      inputButton.setEnabled(isEditorEnabled());
    }
    if (valueDisplay != null) {
      valueDisplay.setEnabled(isEditorEnabled());
    }
    if (label != null) {
      label.setEnabled(isEditorEnabled());
    }
    getLogger().fine("set enabled to " + isEditorEnabled());

  }

}
