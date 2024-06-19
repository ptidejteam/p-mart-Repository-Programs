package net.suberic.util.gui.propedit;
import javax.swing.*;
import java.util.List;
import java.util.LinkedList;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.DefaultHelpBroker;
import java.awt.*;

/**
 * This is a top-level editor for properties.  It includes buttons for
 * activating changes, accepting and closing, and cancelling the action.
 */
public class PropertyEditorPane extends JPanel {
  SwingPropertyEditor editor;
  PropertyEditorManager manager;
  Container container;
  boolean doCommit;
  JButton defaultButton = null;

  /**
   * Constructor for subclasses.
   */
  PropertyEditorPane() {

  }

  /**
   * This contructor creates a PropertyEditor using the given
   * SwingPropertyEditor.
   */
  public PropertyEditorPane(PropertyEditorManager newManager,
                            SwingPropertyEditor newEditor,
                            Container newContainer) {
    this(newManager, newEditor, newContainer, true);
  }

  /**
   * This contructor creates a PropertyEditor using the given
   * SwingPropertyEditor.
   */
  public PropertyEditorPane(PropertyEditorManager newManager,
                            SwingPropertyEditor newEditor,
                            Container newContainer,
                            boolean newCommit) {
    manager = newManager;
    container = newContainer;
    editor = newEditor;
    doCommit = newCommit;

    Component editorComponent = editor;

    if (editor instanceof LabelValuePropertyEditor) {
      JPanel editorPanel = new JPanel();
      SpringLayout editorPanelLayout = new SpringLayout();
      editorPanel.setLayout(editorPanelLayout);

      LabelValuePropertyEditor lvEditor = (LabelValuePropertyEditor) editor;
      editorPanel.add(lvEditor.getLabelComponent());
      editorPanel.add(lvEditor.getValueComponent());

      editorPanelLayout.putConstraint(SpringLayout.WEST, lvEditor.getLabelComponent(), 5, SpringLayout.WEST, editorPanel);
      editorPanelLayout.putConstraint(SpringLayout.NORTH, lvEditor.getLabelComponent(), 5, SpringLayout.NORTH, editorPanel);
      editorPanelLayout.putConstraint(SpringLayout.SOUTH, editorPanel, 5 ,SpringLayout.SOUTH, lvEditor.getLabelComponent());
      //editorPanelLayout.putConstraint(SpringLayout.SOUTH, lvEditor.getLabelComponent(), -5, SpringLayout.SOUTH, editorPanel);

      editorPanelLayout.putConstraint(SpringLayout.WEST, lvEditor.getValueComponent(), 5 ,SpringLayout.EAST, lvEditor.getLabelComponent());

      editorPanelLayout.putConstraint(SpringLayout.NORTH, lvEditor.getValueComponent(), 5 ,SpringLayout.NORTH, editorPanel);
      //editorPanelLayout.putConstraint(SpringLayout.SOUTH, editorPanel, 5 ,SpringLayout.SOUTH, lvEditor.getValueComponent());
      editorPanelLayout.putConstraint(SpringLayout.EAST, editorPanel, 5 ,SpringLayout.EAST, lvEditor.getValueComponent());

      editorComponent = editorPanel;
    }

    JPanel buttonPanel = createButtonPanel();

    pepLayout(editorComponent, buttonPanel);

    editor.acceptDefaultFocus();

  }

  /**
   * Does the layout for the PropertyEditorPane.
   */
  void pepLayout(Component editorPanel, Component buttonPanel) {
    SpringLayout layout = new SpringLayout();
    this.setLayout(layout);

    this.add(editorPanel);

    layout.putConstraint(SpringLayout.WEST, editorPanel, 5, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.NORTH, editorPanel, 5, SpringLayout.NORTH, this);
    layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, editorPanel);
    //layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST, editorPanel);

    this.add(buttonPanel);
    layout.putConstraint(SpringLayout.NORTH, buttonPanel, 5, SpringLayout.SOUTH, editorPanel);

    layout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, buttonPanel);

    Spring widthSpring = Spring.constant(0);
    widthSpring = Spring.max(widthSpring, layout.getConstraints(buttonPanel).getWidth());
    widthSpring = Spring.max(widthSpring, layout.getConstraints(editorPanel).getWidth());

    layout.putConstraint(SpringLayout.EAST, this, Spring.sum(widthSpring, Spring.constant(10)), SpringLayout.WEST, this);

  }

  /**
   * Accepts the changes for the edited properties, and writes them to
   * the PropertyEditorManager.
   */
  public void setValue() throws PropertyValueVetoException {
    editor.setValue();
  }

  /**
   * Gets the currently selected values for the edited properties.
   */
  public java.util.Properties getValue() {
    return editor.getValue();
  }

  /**
   * Resets the original values for the edited properties.
   */
  public void resetDefaultValue() throws PropertyValueVetoException {
    editor.resetDefaultValue();
  }

  /**
   * Creates the appropriate buttons (Ok, Accept, Cancel) to this component.
   */
  public JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel();
    SpringLayout buttonLayout = new SpringLayout();
    buttonPanel.setLayout(buttonLayout);

    JButton helpButton = createButton("PropertyEditor.button.help", new AbstractAction() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          HelpBroker broker = manager.getFactory().getHelpBroker();

          if (container != null && container instanceof JDialog) {
            ((DefaultHelpBroker)broker).setActivationWindow((JDialog) container);
          }

          manager.getFactory().getHelpBroker().setCurrentID(editor.getHelpID());
          manager.getFactory().getHelpBroker().setDisplayed(true);
        }
      });

    //CSH.setHelpIDString(helpButton, "UserProfile");
    buttonPanel.add(helpButton);

    JButton okButton = createButton("PropertyEditor.button.ok", new AbstractAction() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          try {
            setValue();
            if (doCommit) {
              manager.commit();
            }
            if (container instanceof JInternalFrame) {
              try {
                ((JInternalFrame)container).setClosed(true);
              } catch (java.beans.PropertyVetoException pve) {
              }
            } else if (container instanceof JFrame) {
              ((JFrame)container).dispose();
            } else if (container instanceof JDialog) {
              ((JDialog)container).dispose();
            }
            editor.remove();
          } catch (PropertyValueVetoException pvve) {
            manager.getFactory().showError(PropertyEditorPane.this, pvve.getMessage());
          }
        }
      });

    JButton applyButton = createButton("PropertyEditor.button.apply", new AbstractAction() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          try {
            setValue();
            if (doCommit) {
              manager.commit();
            }
          } catch (PropertyValueVetoException pvve) {
            //manager.getFactory().showError(PropertyEditorPane.this, "Error changing value " + pvve.getProperty() + " to " + pvve.getRejectedValue() + ":  " + pvve.getReason());
            manager.getFactory().showError(PropertyEditorPane.this, pvve.getMessage());
          }
        }
      });

    JButton cancelButton = createButton("PropertyEditor.button.cancel", new AbstractAction() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (container instanceof JInternalFrame) {
            try {
              ((JInternalFrame)container).setClosed(true);
            } catch (java.beans.PropertyVetoException pve) {
            }
          } else if (container instanceof JFrame) {
            ((JFrame)container).dispose();
          } else if (container instanceof JDialog) {
            ((JDialog)container).dispose();
          }
          editor.remove();
        }
      });

    buttonPanel.add(helpButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(applyButton);
    buttonPanel.add(okButton);

    Spring buttonWidth = Spring.constant(0);
    buttonWidth = Spring.max(buttonWidth, buttonLayout.getConstraints(helpButton).getWidth());
    buttonWidth = Spring.max(buttonWidth, buttonLayout.getConstraints(cancelButton).getWidth());
    buttonWidth = Spring.max(buttonWidth, buttonLayout.getConstraints(applyButton).getWidth());
    buttonWidth = Spring.max(buttonWidth, buttonLayout.getConstraints(okButton).getWidth());

    buttonLayout.getConstraints(helpButton).setWidth(buttonWidth);
    buttonLayout.getConstraints(cancelButton).setWidth(buttonWidth);
    buttonLayout.getConstraints(applyButton).setWidth(buttonWidth);
    buttonLayout.getConstraints(okButton).setWidth(buttonWidth);

    buttonLayout.putConstraint(SpringLayout.WEST, helpButton, 5, SpringLayout.WEST, buttonPanel);
    buttonLayout.putConstraint(SpringLayout.NORTH, helpButton, 5, SpringLayout.NORTH, buttonPanel);
    buttonLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, 5, SpringLayout.SOUTH, helpButton);

    buttonLayout.putConstraint(SpringLayout.WEST, cancelButton, Spring.constant(5, 5, 32000), SpringLayout.EAST, helpButton);
    buttonLayout.putConstraint(SpringLayout.NORTH, cancelButton, 5, SpringLayout.NORTH, buttonPanel);

    buttonLayout.putConstraint(SpringLayout.WEST, applyButton, 5, SpringLayout.EAST, cancelButton);
    buttonLayout.putConstraint(SpringLayout.NORTH, applyButton, 5, SpringLayout.NORTH, buttonPanel);

    buttonLayout.putConstraint(SpringLayout.WEST, okButton, 5, SpringLayout.EAST, applyButton);
    buttonLayout.putConstraint(SpringLayout.NORTH, okButton, 5, SpringLayout.NORTH, buttonPanel);
    buttonLayout.putConstraint(SpringLayout.EAST, buttonPanel, 5, SpringLayout.EAST, okButton);

    return buttonPanel;
  }

  /**
   * Creates the appropriate Button.
   */
  JButton createButton(String key, Action e) {
    JButton thisButton;

    thisButton = new JButton(manager.getProperty(key +".label", key));
    String mnemonic = manager.getProperty(key + ".keyBinding", "");
    if (mnemonic.length() > 0) {
      thisButton.setMnemonic(mnemonic.charAt(0));
    }

    if (manager.getProperty(key + ".default", "false").equalsIgnoreCase("true")) {
      thisButton.setSelected(true);
      setDefaultButton(thisButton);
    }

    thisButton.addActionListener(e);

    return thisButton;
  }

  /**
   * Returns the Container for this PropertyEditorPane.
   */
  public Container getContainer() {
    return container;
  }

  /**
   * Gets the default Button for this PropertyEditorPane.
   */
  public JButton getDefaultButton() {
    return defaultButton;
  }

  /**
   * Sets the default Button for this PropertyEditorPane.
   */
  public void setDefaultButton(JButton pDefaultButton) {
    defaultButton = pDefaultButton;
  }
}
