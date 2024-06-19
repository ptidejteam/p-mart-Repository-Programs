package net.suberic.util.gui.propedit;
import javax.swing.*;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

/**
 * This will make an editor for a list of properties.  Each property, in
 * turn, should have a list of properties wihch it itself edits.  Each
 * top-level property will be a tab, with its values shown on the panel.
 *
 * So an example of a property definition for this would be:
 *
 * TabbedList=tabOne:tabTwo:tabThree
 * TabbedList.tabOne=prop1:prop2:prop3:prop4
 * TabbedList.tabTwo=prop5:prop6
 * TabbedList.tabThree=prop7:prop8:prop9
 *
 * Options:
 * TabbedList.templateScoped - add subproperty to the template.  for instance,
 *   if true, the example will edit using the template
 *   TabbedList.tabOne.prop1.  if false, it will use the template prop1.
 * TabbedList.propertyScoped - add the subproperty to the property instead
 *   of using it as its own property.  if true, this example would edit,
 *   for instance, MyProp.prop1 (since it would actually edit MyProp,
 *   TabbedList.tabOne, which would in turn probably edit MyProp.prop1,
 *   TabbedList.tabOne.prop1).
 *
 */
public class TabbedEditorPane extends CompositeSwingPropertyEditor {

  JTabbedPane tabbedPane;
  protected boolean templateScoped = false;
  protected boolean propertyScoped = false;

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

    debug = manager.getProperty("editors.debug", "false").equalsIgnoreCase("true");
    getLogger().fine("configuring editor with property " + propertyName + ", editorTemplate " + editorTemplate);

    tabbedPane = new JTabbedPane();

    // first, get the strings that we're going to edit.

    getLogger().fine("creating prop from " + template + "=" + manager.getProperty(template, ""));

    List<String> propsToEdit = manager.getPropertyAsList(template, "");

    editors = createEditors(propsToEdit);

    getLogger().fine("minimumSize for tabbedPane = " + tabbedPane.getMinimumSize());
    getLogger().fine("preferredSize for tabbedPane = " + tabbedPane.getPreferredSize());
    getLogger().fine("size for tabbedPane = " + tabbedPane.getSize());

    SpringLayout layout = new SpringLayout();
    this.setLayout(layout);
    this.add(tabbedPane);
    layout.putConstraint(SpringLayout.WEST, tabbedPane, 0, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.NORTH, tabbedPane, 0, SpringLayout.NORTH, this);
    layout.putConstraint(SpringLayout.SOUTH, this, 0 ,SpringLayout.SOUTH, tabbedPane);
    layout.putConstraint(SpringLayout.EAST, this, 0 ,SpringLayout.EAST, tabbedPane);

    manager.registerPropertyEditor(property, this);
  }

  /**
   * Creates the appropriate editors for the given properties.
   */
  public List createEditors(List<String> propsToEdit) {
    List editorList = new ArrayList();
    SwingPropertyEditor currentEditor;

    for (int i = 0; i < propsToEdit.size(); i++) {
      String subTemplateString = propsToEdit.get(i);
      String property = createSubProperty(subTemplateString);
      String currentTemplate = createSubTemplate(subTemplateString);
      currentEditor = createEditorPane(property, currentTemplate);

      getLogger().fine("adding " + currentEditor);
      getLogger().fine("currentEditor.getMinimumSize() = " + currentEditor.getMinimumSize());

      editorList.add(currentEditor);
      tabbedPane.add(manager.getProperty(currentTemplate + ".label", currentTemplate), currentEditor);
    }

    return editorList;
  }


  /**
   * Creates an editor pane for a group of values.
   */
  private SwingPropertyEditor createEditorPane(String subProperty, String subTemplate) {
    return (SwingPropertyEditor) manager.getFactory().createEditor(subProperty, subTemplate, subProperty, manager);

  }

  /**
   * Returns the helpId for this editor.
   */
  public String getHelpID() {
    String subProperty = manager.getProperty(editorTemplate + ".helpController", "");
    if (subProperty.length() == 0) {
      java.awt.Component selectedComponent = tabbedPane.getSelectedComponent();
      if (selectedComponent == null || ! (selectedComponent instanceof PropertyEditorUI)) {
        return super.getHelpID();
      } else {
        return ((PropertyEditorUI) selectedComponent).getHelpID();
      }
    } else {
      return super.getHelpID();
    }
  }

}



