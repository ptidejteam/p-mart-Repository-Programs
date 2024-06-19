package net.suberic.util.gui.propedit;
import javax.swing.*;
import java.awt.Container;
import java.awt.Component;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import net.suberic.util.VariableBundle;

/**
 * This is a Property Editor which displays a group of properties.
 * These properties should all be defined by a single property.
 *
 * An example:
 *
 * Configuration=foo:bar
 * Configuration.propertyType=Composite
 * Configuration.scoped=false
 * foo=zork
 * bar=frobozz
 *
 * Options:
 *
 * See CompositeSwingPropertyEditor for options.
 *
 */
public class CompositeEditorPane extends CompositeSwingPropertyEditor {

  /**
   * Creates a CompositeEditorPane.
   */
  public CompositeEditorPane() {

  }

  /**
   * This configures this editor with the following values.
   *
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {

    configureBasic(propertyName, template, propertyBaseName, newManager);

    getLogger().fine("creating CompositeEditorPane for " + property + " with template " + editorTemplate);

    //this.setBorder(BorderFactory.createEtchedBorder());

    String borderLabel = manager.getProperty(editorTemplate + ".label.border", "");
    if (borderLabel.length() > 0) {
      this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), borderLabel));
    }

    List<String> properties = new ArrayList<String>();
    List<String> templates = new ArrayList<String>();

    getLogger().fine("testing for template " + template);

    List<String> templateNames = manager.getPropertyAsList(template, "");
    getLogger().fine("templateNames = getProp(" + template + ") = " + manager.getProperty(template, ""));

    for (int i = 0; i < templateNames.size() ; i++) {
      String subTemplateString = templateNames.get(i);
      properties.add(createSubProperty(subTemplateString));
      templates.add(createSubTemplate(subTemplateString));
    }

    addEditors(properties, templates);
  }

  public void addEditors(List<String> properties, List<String> templates) {
    SwingPropertyEditor currentEditor;

    editors = new Vector();

    SpringLayout layout = new SpringLayout();

    this.setLayout(new SpringLayout());
    Component[] labelComponents = new Component[properties.size()];
    Component[] valueComponents = new Component[properties.size()];
    for (int i = 0; i < properties.size(); i++) {
      getLogger().fine("creating editor for " + properties.get(i) + ", template " + templates.get(i) + ", propertyBase " + propertyBase);
      currentEditor = (SwingPropertyEditor) manager.createEditor(properties.get(i), templates.get(i), propertyBase);
      getLogger().fine("got " + currentEditor.getClass().getName());
      editors.add(currentEditor);

      if (currentEditor instanceof LabelValuePropertyEditor) {
        LabelValuePropertyEditor lvEditor = (LabelValuePropertyEditor) currentEditor;
        this.add(lvEditor.labelComponent);
        labelComponents[i] = lvEditor.labelComponent;
        this.add(lvEditor.valueComponent);
        valueComponents[i] = lvEditor.valueComponent;
      } else {
        this.add(currentEditor);
        labelComponents[i] = currentEditor;
      }
    }
    //makeCompactGrid(this, labelComponents, valueComponents, 5, 5, 5, 5);
    layoutGrid(this, labelComponents, valueComponents, 5, 5, 5, 5, manager.getProperty(editorTemplate + ".nested", "false").equalsIgnoreCase("true"));
    manager.registerPropertyEditor(property, this);
  }


}
