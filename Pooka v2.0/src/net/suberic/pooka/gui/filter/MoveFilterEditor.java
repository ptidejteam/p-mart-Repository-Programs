package net.suberic.pooka.gui.filter;
import net.suberic.pooka.gui.propedit.FolderSelectorPane;
import net.suberic.util.gui.propedit.PropertyValueVetoException;
import java.util.Properties;
import javax.swing.SpringLayout;


/**
 * This is a class that lets you choose your filter actions.
 */
public class MoveFilterEditor extends FilterEditor {
  String originalFolderName;

  FolderSelectorPane fsp;

  public static String FILTER_CLASS = "net.suberic.pooka.filter.MoveFilterAction";

  /**
   * Configures the given FilterEditor from the given VariableBundle and
   * property.
   */
  public void configureEditor(net.suberic.util.gui.propedit.PropertyEditorManager newManager, String propertyName) {
    property = propertyName;
    manager = newManager;

    fsp = new FolderSelectorPane();
    fsp.configureEditor(propertyName + ".targetFolder", manager);

    SpringLayout layout = new SpringLayout();
    this.setLayout(layout);

    this.add(fsp.getLabelComponent());
    this.add(fsp.getValueComponent());

    layout.putConstraint(SpringLayout.NORTH, fsp.getLabelComponent(), 0, SpringLayout.NORTH, this);
    layout.putConstraint(SpringLayout.WEST, fsp.getLabelComponent(), 0, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, fsp.getLabelComponent());
    layout.putConstraint(SpringLayout.WEST, fsp.getValueComponent(), 5, SpringLayout.EAST, fsp.getLabelComponent());
    layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST, fsp.getValueComponent());

    this.setPreferredSize(new java.awt.Dimension(150, fsp.getLabelComponent().getMinimumSize().height));
    this.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, fsp.getLabelComponent().getMinimumSize().height));

  }

  /**
   * Gets the values that would be set by this FilterEditor.
   */
  public java.util.Properties getValue() {
    Properties props = fsp.getValue();

    String oldClassName = manager.getProperty(property + ".class", "");
    if (!oldClassName.equals(FILTER_CLASS))
      props.setProperty(property + ".class", FILTER_CLASS);

    return props;
  }

  /**
   * Sets the values represented by this FilterEditor in the manager.
   */
  public void setValue() throws PropertyValueVetoException {

    fsp.setValue();

    String oldClassName = manager.getProperty(property + ".class", "");
    if (!oldClassName.equals(FILTER_CLASS))
      manager.setProperty(property + ".class", FILTER_CLASS);
  }

  /**
   * Returns the class that will be set for this FilterEditor.
   */
  public String getFilterClassValue() {
    return FILTER_CLASS;
  }

}
