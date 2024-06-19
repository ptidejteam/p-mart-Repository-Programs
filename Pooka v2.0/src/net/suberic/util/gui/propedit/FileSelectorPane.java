package net.suberic.util.gui.propedit;
import java.io.*;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * This displays the currently selected file (if any), along with a
 * button which will bring up a JFileChooser to choose any other file(s).
 */

public class FileSelectorPane extends LabelValuePropertyEditor {

  protected JLabel label;
  protected JTextField valueDisplay;
  JButton inputButton;

  protected int fileSelection;

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName,  PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);

    String currentValue = manager.getProperty(property, "");

    getLogger().fine("property is " + property + "; editorTemplate is " + editorTemplate);

    label = createLabel();

    valueDisplay = new JTextField(currentValue);

    inputButton = createInputButton();

    valueDisplay.setPreferredSize(new java.awt.Dimension(150 - inputButton.getPreferredSize().width, valueDisplay.getMinimumSize().height));

    String selectionType = manager.getProperty(editorTemplate + ".propertyType", "File");
    if (selectionType.equalsIgnoreCase("Directory")) {
      fileSelection = JFileChooser.DIRECTORIES_ONLY;
    } else {
      fileSelection = JFileChooser.FILES_ONLY;
    }

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
   * Creates a button that will bring up a way to select a new File.
   */
  public JButton createInputButton() {
    try {
      java.net.URL url = this.getClass().getResource(manager.getProperty("FileSelectorPane.inputButton.image", "/net/suberic/util/gui/images/More.gif"));
      if (url != null) {
        ImageIcon icon = new ImageIcon(url);

        JButton newButton = new JButton(icon);

        newButton.setPreferredSize(new java.awt.Dimension(icon.getIconHeight(), icon.getIconWidth()));
        newButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
              selectNewFolder();
            }
          });

        return newButton;
      }
    } catch (java.util.MissingResourceException mre) {
    }

    JButton newButton = new JButton();
    newButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          selectNewFolder();
        }
      });

    return newButton;
  }

  /**
   * This actually brings up a JFileChooser to select a new File for
   * the value of the property.
   */
  public void selectNewFolder() {
    JFileChooser jfc =
      new JFileChooser((String)valueDisplay.getText());
    jfc.setMultiSelectionEnabled(false);
    jfc.setFileSelectionMode(fileSelection);
    jfc.setFileHidingEnabled(false);

    int returnValue =
      jfc.showDialog(this,
                     manager.getProperty("FolderEditorPane.Select",
                                         "Select"));

    if (returnValue == JFileChooser.APPROVE_OPTION) {
      File returnFile = jfc.getSelectedFile();
      String newValue = returnFile.getAbsolutePath();

      try {
        firePropertyChangingEvent(newValue);
        firePropertyChangedEvent(newValue);

        valueDisplay.setText(newValue);

      } catch (PropertyValueVetoException pvve) {
        manager.getFactory().showError(valueDisplay, "Error changing value " + label.getText() + " to " + newValue + ":  " + pvve.getReason());
      }
    }

  }

  //  as defined in net.suberic.util.gui.PropertyEditorUI

  /**
   * This writes the currently configured value in the PropertyEditorUI
   * to the source PropertyEditorManager.
   */
  public void setValue() throws PropertyValueVetoException {
    if (isEditorEnabled()) {
      validateProperty();
      if (isChanged()) {
        manager.setProperty(property, (String)valueDisplay.getText());
        originalValue = valueDisplay.getText();
      }
    }
  }

  /**
   * Validates the selected value.
   */
  public void validateProperty() throws PropertyValueVetoException {
    if (isEditorEnabled()) {
      firePropertyCommittingEvent((String)valueDisplay.getText());
    }
  }

  /**
   * Returns the current values of the edited properties as a
   * java.util.Properties object.
   */
  public java.util.Properties getValue() {
    java.util.Properties retProps = new java.util.Properties();

    retProps.setProperty(property, (String)valueDisplay.getText());

    return retProps;
  }

  /**
   * This resets the editor to the original (or latest set, if setValue()
   * has been called) value of the edited property.
   */
  public void resetDefaultValue() {
    valueDisplay.setText(originalValue);
  }

  /**
   * Returns whether or not this editor has its original value.
   */
  public boolean isChanged() {
    return (!(originalValue.equals(valueDisplay.getText())));
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    if (inputButton != null) {
      inputButton.setEnabled(isEditorEnabled());
    }
    if (valueDisplay != null) {
      valueDisplay.setEnabled(isEditorEnabled());
    }
    if (label != null) {
      label.setEnabled(isEditorEnabled());
    }
  }

  /**
   * Accepts or rejects the initial focus for this component.
   */
  public boolean acceptDefaultFocus() {
    if (isEditorEnabled()) {
      // for some reason this returns false in dialogs
      valueDisplay.requestFocusInWindow();
      return true;
    } else {
      return false;
    }
  }

}
