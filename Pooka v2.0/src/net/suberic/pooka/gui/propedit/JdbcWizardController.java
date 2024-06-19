package net.suberic.pooka.gui.propedit;
import java.util.*;
import java.util.prefs.Preferences;
import net.suberic.util.gui.propedit.*;
import net.suberic.util.VariableBundle;

/**
 * The controller class for the JdbcWizard.
 */
public class JdbcWizardController extends WizardController {

  /**
   * Creates a JdbcWizardController.
   */
  public JdbcWizardController(String sourceTemplate, WizardEditorPane wep) {
    super(sourceTemplate, wep);
  }

  /**
   * Saves all of the properties for this wizard.
   */
  protected void saveProperties() throws PropertyValueVetoException {
    try {

      /*
        System.out.println("driver " + getManager().getCurrentProperty("Pooka._jdbcWizard.selection.driver", ""));
        System.out.println("url " + getManager().getCurrentProperty("Pooka._jdbcWizard.selection.url", ""));
        System.out.println("user " +   getManager().getCurrentProperty("Pooka._jdbcWizard.selection.user", ""));
        System.out.println("pw " +   net.suberic.util.gui.propedit.PasswordEditorPane.descrambleString(getManager().getCurrentProperty("Pooka._jdbcWizard.selection.password", "")));
      */

      System.setProperty("JDBCPreferences.driverName", getManager().getCurrentProperty("Pooka._jdbcWizard.selection.driver", ""));
      System.setProperty("JDBCPreferences.url",  getManager().getCurrentProperty("Pooka._jdbcWizard.selection.url", ""));
      System.setProperty("JDBCPreferences.user",  getManager().getCurrentProperty("Pooka._jdbcWizard.selection.user", ""));
      System.setProperty("JDBCPreferences.password",  net.suberic.util.gui.propedit.PasswordEditorPane.descrambleString(getManager().getCurrentProperty("Pooka._jdbcWizard.selection.password", "")));
      Preferences p = Preferences.userNodeForPackage(this.getClass());
      p.keys();
      System.setProperty("useJdbcConnection", "true");
    } catch (Exception e) {
      throw new PropertyValueVetoException("Pooka._jdbcWizard.selection.driver", getManager().getCurrentProperty("Pooka._jdbcWizard.selection.driver", ""), e.getMessage(), null);
    }
  }

  /**
   * Finsihes the wizard.
   */
  public void finishWizard() throws PropertyValueVetoException {
    saveProperties();
    getEditorPane().getWizardContainer().closeWizard();
  }

}
