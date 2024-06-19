package net.suberic.pooka.gui.propedit;
import net.suberic.pooka.*;
import net.suberic.util.gui.propedit.*;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * A file selector that uses the ResourceManager to translate filenames.
 */
public class PookaFileSelector extends FileSelectorPane {

  /**
   * This actually brings up a JFileChooser to select a new File for
   * the value of the property.  In this case, it also translates the
   * selected file to use the pooka.root setting.
   */
  public void selectNewFolder() {
    JFileChooser jfc = new JFileChooser(Pooka.getResourceManager().translateName((String) valueDisplay.getText()));
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

        valueDisplay.setText(Pooka.getResourceManager().encodeFileName(newValue));

      } catch (PropertyValueVetoException pvve) {
        manager.getFactory().showError(valueDisplay, "Error changing value " + label.getText() + " to " + newValue + ":  " + pvve.getReason());
      }
    }

  }
}
