package net.suberic.util.gui.propedit;
import java.awt.CardLayout;
import java.lang.reflect.Constructor;
import java.util.*;
import javax.swing.*;

/**
 * A SwingEditorPane that implements Wizard functionality.
 */
public class WizardEditorPane extends CompositeSwingPropertyEditor {

  Map<String, SwingPropertyEditor> layoutMap = new HashMap<String, SwingPropertyEditor>();
  CardLayout layout;
  WizardPropertyEditor wizardContainer = null;
  WizardController controller = null;

  /**
   * Configures the editor.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);

    editors = new ArrayList<SwingPropertyEditor>();

    layout = new CardLayout();
    this.setLayout(layout);

    // see about loading the WizardController
    String controllerClassString = manager.getProperty(template + ".controllerClass", "");
    if (controllerClassString.length() > 0) {
      try {
        Class controllerClass = Class.forName(controllerClassString);
        Constructor constructor = controllerClass.getConstructor(Class.forName("java.lang.String"), Class.forName("net.suberic.util.gui.propedit.WizardEditorPane"));
        controller = (WizardController) constructor.newInstance(template, this);
      } catch (Exception e) {
        getLogger().log(java.util.logging.Level.SEVERE, "Error loading controller class " + controllerClassString, e);
        controller = new WizardController(template, this);
      }
    } else {
      controller = new WizardController(template, this);
    }
    controller.initialize();
  }

  /**
   * Creates the editors for each state.
   */
  public void createEditors(List<String> stateList) {
    for (String stateString: stateList) {
      String subProperty = createSubProperty(manager.getProperty(editorTemplate + "._states." + stateString + ".editor", ""));
      String subTemplate = createSubTemplate(manager.getProperty(editorTemplate + "._states." + stateString + ".editor", ""));
      SwingPropertyEditor newEditor = (SwingPropertyEditor) manager.getFactory().createEditor(subProperty, subTemplate, subTemplate, manager);
      layoutMap.put(stateString, newEditor);
      this.add(stateString, newEditor);
      editors.add(newEditor);
    }
  }

  /**
   * Commits the value for the given state.
   */
  public void setValue(String state) throws PropertyValueVetoException {
    SwingPropertyEditor editor = layoutMap.get(state);
    editor.setValue();
  }

  /**
   * Validates the given state.
   */
  public void validateProperty(String state) throws PropertyValueVetoException {
    SwingPropertyEditor editor = layoutMap.get(state);
    editor.validateProperty();
  }

  /**
   * Loads the current state.
   */
  public void loadState(String state) {
    layout.show(this, state);
    loadContainerState();
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          acceptDefaultFocus();
        }
      });
  }

  /**
   * Loads the state info into the container.
   */
  public void loadContainerState() {
    if (getWizardContainer() != null) {
      getWizardContainer().setBeginningState(inBeginningState());
      getWizardContainer().setEndState(inEndState());
    }
  }

  /**
   * Returns the controller.
   */
  public WizardController getController() {
    return controller;
  }


  /**
   * Returns the current Wizard state.
   */
  public String getState() {
    return controller.getState();
  }

  /**
   * Returns if this is the beginning state.
   */
  public boolean inBeginningState() {
    return controller.inBeginningState();
  }

  /**
   * Returns if this is in a valid end state.
   */
  public boolean inEndState() {
    return controller.inEndState();
  }

  /**
   * Goes back a state.
   */
  public void back() {
    controller.back();
  }

  /**
   * Goes forward a state.
   */
  public void next() throws PropertyValueVetoException {
    controller.next();
  }

  /**
   * Sets the WizardPropertyEditor container.
   */
  public void setWizardContainer(WizardPropertyEditor wpe) {
    wizardContainer = wpe;
  }

  /**
   * Gets the WizardPropertyEditor container.
   */
  public WizardPropertyEditor getWizardContainer() {
    return wizardContainer;
  }

  /**
   * Accepts or rejects the initial focus for this component.
   */
  public boolean acceptDefaultFocus() {
    SwingPropertyEditor currentEditor = layoutMap.get(getState());
    return currentEditor.acceptDefaultFocus();
  }

}
