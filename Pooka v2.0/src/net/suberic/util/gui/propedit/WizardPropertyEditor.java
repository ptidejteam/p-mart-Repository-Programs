package net.suberic.util.gui.propedit;
import java.awt.Component;
import java.awt.Container;
import javax.swing.*;


/**
 * A top-level editor for wizard properties.  Instead of having a single
 * panel with properties and a set of 'help', 'apply', 'ok', and 'cancel'
 * buttons, this has a series of panels with 'help', 'cancel', 'back',
 * and 'next' buttons.  You must go through the workflow for the Wizard
 * before you reach an 'ok' stage.
 */
public class WizardPropertyEditor extends PropertyEditorPane {
  WizardEditorPane wizard = null;
  JButton backButton;
  JButton nextButton;


  /**
   * This contructor creates a PropertyEditor using the given
   * SwingPropertyEditor.
   */
  public WizardPropertyEditor(PropertyEditorManager newManager,
                            SwingPropertyEditor newEditor,
                            Container newContainer,
                            boolean newCommit) {
    manager = newManager;
    container = newContainer;
    editor = newEditor;
    wizard = (WizardEditorPane) newEditor;
    wizard.setWizardContainer(this);
    doCommit = newCommit;

    Component editorComponent = editor;

    JPanel buttonPanel = createButtonPanel();

    pepLayout(editorComponent, buttonPanel);

    wizard.loadContainerState();

    wizard.acceptDefaultFocus();

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
          //System.err.println("showing help for " + editor.getHelpID());
          //manager.getFactory().getHelpBroker().showID(editor.getHelpID(), null, null);
          //new CSH.DisplayHelpFromSource(manager.getFactory().getHelpBroker()).actionPerformed(e);
          manager.getFactory().getHelpBroker().setCurrentID(editor.getHelpID());
          manager.getFactory().getHelpBroker().setDisplayed(true);

        }
      });

    //CSH.setHelpIDString(helpButton, "UserProfile");
    buttonPanel.add(helpButton);

    backButton = createButton("Wizard.button.back", new AbstractAction() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          wizard.back();
        }
      });


    nextButton = createButton("Wizard.button.next", new AbstractAction() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          try {
            wizard.next();
          } catch (PropertyValueVetoException pvve) {
            manager.getFactory().showError(WizardPropertyEditor.this, pvve.getMessage());
          }
        }
      });

    JButton cancelButton = createButton("PropertyEditor.button.cancel", new AbstractAction() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          closeWizard();
        }
      });

    buttonPanel.add(helpButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(backButton);
    buttonPanel.add(nextButton);

    Spring buttonWidth = Spring.constant(0);
    buttonWidth = Spring.max(buttonWidth, buttonLayout.getConstraints(helpButton).getWidth());
    buttonWidth = Spring.max(buttonWidth, buttonLayout.getConstraints(cancelButton).getWidth());
    buttonWidth = Spring.max(buttonWidth, buttonLayout.getConstraints(backButton).getWidth());
    buttonWidth = Spring.max(buttonWidth, buttonLayout.getConstraints(nextButton).getWidth());

    buttonLayout.getConstraints(helpButton).setWidth(buttonWidth);
    buttonLayout.getConstraints(cancelButton).setWidth(buttonWidth);
    buttonLayout.getConstraints(backButton).setWidth(buttonWidth);
    buttonLayout.getConstraints(nextButton).setWidth(buttonWidth);

    buttonLayout.putConstraint(SpringLayout.WEST, helpButton, 5, SpringLayout.WEST, buttonPanel);
    buttonLayout.putConstraint(SpringLayout.NORTH, helpButton, 5, SpringLayout.NORTH, buttonPanel);
    buttonLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, 5, SpringLayout.SOUTH, helpButton);

    buttonLayout.putConstraint(SpringLayout.WEST, cancelButton, Spring.constant(5, 5, 32000), SpringLayout.EAST, helpButton);
    buttonLayout.putConstraint(SpringLayout.NORTH, cancelButton, 5, SpringLayout.NORTH, buttonPanel);

    buttonLayout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.EAST, cancelButton);
    buttonLayout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, buttonPanel);

    buttonLayout.putConstraint(SpringLayout.WEST, nextButton, 5, SpringLayout.EAST, backButton);
    buttonLayout.putConstraint(SpringLayout.NORTH, nextButton, 5, SpringLayout.NORTH, buttonPanel);
    buttonLayout.putConstraint(SpringLayout.EAST, buttonPanel, 5, SpringLayout.EAST, nextButton);

    return buttonPanel;
  }

  /**
   * Sets the propertyEditor into beginning state.
   */
  public void setBeginningState(boolean beginningState) {
    if (beginningState) {
      backButton.setEnabled(false);
    } else {
      backButton.setEnabled(true);
    }
  }

  /**
   * Sets the propertyEditor into end state.
   */
  public void setEndState(boolean endState) {
    if (endState) {
      if (! nextButton.getText().equals(manager.getProperty("Wizard.button.end.label", "Finish"))) {
        nextButton.setText(manager.getProperty("Wizard.button.end.label", "Finish"));
        String mnemonic = manager.getProperty("Wizard.button.end.keyBinding", "");
        if (mnemonic.length() > 0) {
          nextButton.setMnemonic(mnemonic.charAt(0));
        }
      }
    } else {
      if (! nextButton.getText().equals(manager.getProperty("Wizard.button.next", "Next"))) {
        nextButton.setText(manager.getProperty("Wizard.button.next.label", "Finish"));
        String mnemonic = manager.getProperty("Wizard.button.next.keyBinding", "");
        if (mnemonic.length() > 0) {
          nextButton.setMnemonic(mnemonic.charAt(0));
        }
      }
    }
  }

  /**
   * Closes the editor.
   */
  public void closeWizard() {
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
}
