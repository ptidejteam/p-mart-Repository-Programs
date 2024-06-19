package net.suberic.util.gui.propedit;
import java.util.*;

/**
 * This class handles things like
 */
public class WizardController {
  String template;
  protected String mState;
  protected List<String> mStateList = null;
  WizardEditorPane editorPane;
  PropertyEditorManager manager;

  /**
   * Creates a new WizardController using the given resource and
   * WizardEditorPane.
   */
  public WizardController(String sourceTemplate, WizardEditorPane wep) {
    template = sourceTemplate;
    editorPane = wep;
    manager = wep.getManager();

    mStateList = manager.getPropertyAsList(template + "._states", "");
    if (mStateList.size() > 0) {
      mState = mStateList.get(0);
    }

  }

  /**
   * Initializes the Controller and PropertyEditor.
   */
  void initialize() {
    editorPane.createEditors(mStateList);
    editorPane.loadState(mState);
  }

  /**
   * Returns the current Wizard state.
   */
  public String getState() {
    return mState;
  }

  /**
   * Returns if this is the beginning state.
   */
  public boolean inBeginningState() {
    if (mState == mStateList.get(0))
      return true;
    else
      return false;
  }

  /**
   * Returns if this is in a valid end state.
   */
  public boolean inEndState() {
    if (mState == mStateList.get(mStateList.size() - 1))
      return true;
    else
      return false;
  }

  /**
   * Goes back a state.
   */
  public void back() {
    if (inBeginningState())
      return;
    else {
      String newState = getBackState(mState);
      if (newState != null) {
        //checkStateTransition(mState, newState);
        mState = newState;
        editorPane.loadState(newState);
      }
    }

  }

  /**
   * Goes forward a state.
   */
  public void next() throws PropertyValueVetoException {
    if (inEndState()) {
      checkStateTransition(mState, "");
      finishWizard();
    } else {
      String newState = getNextState(mState);
      if (newState != null) {
        checkStateTransition(mState, newState);
        mState = newState;
        editorPane.loadState(newState);
      }
    }
  }

  /**
   * Checks the state transition to make sure that we can move from
   * state to state.
   */
  public void checkStateTransition(String oldState, String newState) throws PropertyValueVetoException {
    getEditorPane().validateProperty(oldState);
  }

  /**
   * Gets the next state.
   */
  public String getNextState(String currentState) {
    int current = mStateList.indexOf(mState);
    if (current > -1 && current < (mStateList.size() -1)) {
      String newState = mStateList.get(current + 1);
      return newState;
    } else {
      return null;
    }
  }
  /**
   * Gets the state that should be displayed next from a back request.
   */
  public String getBackState(String currentState) {
    int current = mStateList.indexOf(currentState);
    if (current >= 1) {
      String newState = mStateList.get(current - 1);
      return newState;
    } else {
      return null;
    }
  }

  /**
   * Finsihes the wizard.
   */
  public void finishWizard() throws PropertyValueVetoException {
    //getManager().commit();
    getEditorPane().getWizardContainer().closeWizard();
  }

  /**
   * Returns the PropertyEditorManager.
   */
  public PropertyEditorManager getManager() {
    return manager;
  }

  /**
   * Returns the WizardEditorPane.
   */
  public WizardEditorPane getEditorPane() {
    return editorPane;
  }
}

