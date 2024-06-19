package net.suberic.util.gui.propedit;
import javax.swing.*;
import java.awt.event.*;
import net.suberic.util.*;
import java.awt.FlowLayout;

public class PasswordEditorPane extends StringEditorPane {
  String originalScrambledValue;

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);

    originalScrambledValue = manager.getProperty(property, "");
    if (!originalScrambledValue.equals(""))
      originalValue = descrambleString(originalScrambledValue);
    else
      originalValue = "";
    currentValue = originalValue;

    getLogger().fine("property is " + property + "; editorTemplate is " + editorTemplate);

    label = createLabel();

    inputField = new JPasswordField(originalValue);
    inputField.setPreferredSize(new java.awt.Dimension(150, inputField.getMinimumSize().height));
    inputField.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, inputField.getMinimumSize().height));
    inputField.addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent e) {
          String newPassword = new String(((JPasswordField)inputField).getPassword());
          if (! newPassword.equals(currentValue)) {
            try {
              firePropertyChangingEvent(newPassword);
              firePropertyChangedEvent(newPassword);
              currentValue = newPassword;
            } catch (PropertyValueVetoException pvve) {
              manager.getFactory().showError(inputField, "Error changing value " + label.getText() + ":  " + pvve.getReason());
              inputField.setText(currentValue);
            }
          }
        }

        public void focusGained(FocusEvent e) {
          if (inputField.getText() != null && inputField.getText().length() > 0) {
            inputField.setSelectionStart(0);
            inputField.setSelectionEnd(inputField.getText().length());
          }
        }

      });
    this.add(label);
    this.add(inputField);
    updateEditorEnabled();

    labelComponent = label;
    valueComponent = inputField;

    //manager.registerPropertyEditor(property, this);
  }

  /**
   * This writes the currently configured value in the PropertyEditorUI
   * to the source VariableBundle.
   */
  public void setValue() throws PropertyValueVetoException {
    String value = new String(((JPasswordField)inputField).getPassword());

    if (isEditorEnabled() && ! (value.equals(currentValue))) {
      firePropertyChangingEvent(value);
      firePropertyChangedEvent(value);
    }

    if (isEditorEnabled() && !(value.equals(originalValue))) {
      manager.setProperty(property, scrambleString(value));
      originalValue = value;
    }
  }

  /**
   * Returns the current values of the edited properties as a
   * java.util.Properties object.
   */
  public java.util.Properties getValue() {
    String value = new String(((JPasswordField)inputField).getPassword());
    java.util.Properties retProps = new java.util.Properties();
    if (value.equals(originalValue))
      retProps.setProperty(property, originalScrambledValue);
    else
      retProps.setProperty(property, scrambleString(value));
    return retProps;
  }

  /**
   * This resets the editor to the original (or latest set, if setValue()
   * has been called) value of the edited property.
   */
  public void resetDefaultValue() {
    String fieldValue = new String(((JPasswordField)inputField).getPassword());
    if (! (fieldValue.equals(currentValue) && fieldValue.equals(originalValue))) {
      // something has changed, so we'll have to deal with it.
      try {
        if (! currentValue.equals(originalValue)) {
          firePropertyChangingEvent(originalValue);
          firePropertyChangedEvent(originalValue);
          currentValue = originalValue;
        }
        inputField.setText(originalValue);
      } catch (PropertyValueVetoException pvve) {
        manager.getFactory().showError(inputField, "Error changing value " + label.getText() + " to " + originalValue + ":  " + pvve.getReason());
      }
    }
  }

  // the list of characters to use for scrambling.
  private static char[] scrambleChars = new char[] {'A', 'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f', 'G', 'g', 'H', 'h', 'I', 'i', 'J', 'j', 'K', 'k', 'L', 'l', 'M', 'm', 'N', 'n', 'O', 'o', 'P', 'p', 'Q', 'q', 'R', 'r', 'S', 's', 'T', 't', 'U', 'u', 'V', 'v', 'W', 'w', 'X', 'x', 'Y', 'y', 'Z', 'z'};

  /**
   * This is a simple scrambler.
   */
  public static String scrambleString(String key) {
    if (key == null || key.length() < 1) {
      return "";
    }
    int[] salt = new int[4];
    int keySize = key.length();
    long seed = System.currentTimeMillis();

    salt[0] = (int)((seed / 107) %  2704);
    salt[1] = (int)((seed / 19) % 2704);
    salt[2] = (int)((seed / 17) % 2704);
    salt[3] = (int)((seed / 91) % 2704);

    char [] scrambledString = new char[(keySize * 2) + 8];

    for (int i = 0; i < keySize; i++) {
      int numValue = (int)(key.charAt(i));
      numValue = (numValue + salt[i % 4]) % 2704;
      scrambledString[i * 2] = scrambleChars[numValue / 52];
      scrambledString[(i * 2) + 1] = scrambleChars[numValue % 52];
    }

    for (int i = 0; i  < 3; i++) {
      int numValue = (salt[i] + salt[i + 1]) % 2704;
      scrambledString[(keySize + i) * 2] = scrambleChars[numValue / 52];
      scrambledString[((keySize + i) * 2) + 1] = scrambleChars[numValue % 52];
    }

    scrambledString[(keySize + 3) * 2] = scrambleChars[salt[3] / 52];
    scrambledString[((keySize + 3) * 2) + 1] = scrambleChars[salt[3] % 52];

    return new String(scrambledString);
  }

  /**
   * And this is a simple descrambler.
   */
  public static String descrambleString(String value) {
    if (value == null || value.length() < 1) {
      return "";
    }
    int[] salt = new int[4];
    int scrambleSize = value.length();
    char[] key = new char[(scrambleSize - 8) / 2];
    salt[3] = (findCharValue(value.charAt(scrambleSize - 2)) * 52) + findCharValue(value.charAt(scrambleSize - 1));

    for (int i = 2; i >= 0; i--) {
      salt[i] = (2704 - salt[i + 1] + (findCharValue(value.charAt(scrambleSize - ((4 - i) * 2) )) * 52) + findCharValue(value.charAt(scrambleSize - ((4 - i) * 2) + 1))) % 2704;
    }

    for (int i = 0; i < (scrambleSize - 8) / 2; i++) {
      key[i] = (char)((2704 - salt[i % 4] + (findCharValue(value.charAt(i * 2)) * 52) + findCharValue(value.charAt((i * 2) + 1))) % 2704);
    }

    return new String(key);
  }

  /**
   * This very inefficiently finds a character value in the scrambleChars
   * array.
   */
  private static int findCharValue(char a) {
    for (int i = 0; i < scrambleChars.length; i++)
      if (a == scrambleChars[i])
        return i;

    return 0;
  }

}
