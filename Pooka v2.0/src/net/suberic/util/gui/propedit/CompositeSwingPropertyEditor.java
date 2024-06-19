package net.suberic.util.gui.propedit;
import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This will make an editor for a list of properties.</p>
 *
 * <p>Note that CompositeSwingPropertyEditors generally will append
 * subProperties that start with "." to the source template and property.
 * In addition, CompositeSwingPropertyEditors have two properties
 * that can vary this behavior:  if "propertyScoped" is set to true and
 * the subproperty does not start with ".", then the original property is
 * passed through.  If "addSubProperty" is set to false, then the original
 * property will be passed through even if the subProperty starts with "."
 * </p>
 *
 * <p>For template properties, the value 'templateBase" can be set
 * to indicate the template base to use for scoped subtemplates, so that
 * you can use the same set of subproperty definitions for multiple templated
 * locations (i.e. using Store.editor.main.server as the key for .server
 * for both Store.editor.main.imap and Store.editor.main.pop3).</p>
 *
 * <p>In addition, for a templated subproperty you can set
 * '.appendProperty=false', which will result in that subproperty not
 * being added to the property itself.</p>
 */
public abstract class CompositeSwingPropertyEditor extends SwingPropertyEditor {
  protected List<SwingPropertyEditor> editors;
  protected Logger mLogger = Logger.getLogger("editors.debug");

  /**
   * This writes the currently configured values in the PropertyEditorUI
   * to the source VariableBundle.
   */
  public void setValue() throws PropertyValueVetoException {
    if (isEditorEnabled()) {
      List<PropertyValueVetoException> exceptionList = new ArrayList<PropertyValueVetoException>();
      for (int i = 0; i < editors.size() ; i++) {
        try {
          editors.get(i).setValue();
        } catch (PropertyValueVetoException pvve) {
          exceptionList.add(pvve);
        }
      }
      if (exceptionList.size() > 0) {
        StringBuilder builder = new StringBuilder();
        Iterator<PropertyValueVetoException> iter = exceptionList.iterator();
        while (iter.hasNext()) {
          PropertyValueVetoException pvve = iter.next();
          builder.append(pvve.getMessage());
          if (iter.hasNext())
            builder.append("\r\n");
        }
        throw new PropertyValueVetoException(builder.toString());
      }
    }
  }

  public void validateProperty() throws PropertyValueVetoException {

    if (isEditorEnabled()) {
      List<PropertyValueVetoException> exceptionList = new ArrayList<PropertyValueVetoException>();
      for (int i = 0; i < editors.size() ; i++) {
        try {
          editors.get(i).validateProperty();
        } catch (PropertyValueVetoException pvve) {
          exceptionList.add(pvve);
        }
      }
      if (exceptionList.size() > 0) {
        StringBuilder builder = new StringBuilder();
        Iterator<PropertyValueVetoException> iter = exceptionList.iterator();
        while (iter.hasNext()) {
          PropertyValueVetoException pvve = iter.next();
          builder.append(pvve.getMessage());
          if (iter.hasNext())
            builder.append("\r\n");
        }
        throw new PropertyValueVetoException(builder.toString());
      }
    }
  }

  /**
   * This resets the editor to the original (or latest set, if setValue()
   * has been called) value of the edited property.
   */
    public void resetDefaultValue() throws PropertyValueVetoException {
    if (isEditorEnabled()) {
      for (int i = 0; i < editors.size() ; i++) {
        editors.get(i).resetDefaultValue();
      }
    }
  }

  /**
   * Returns the current values of the edited properties as a
   * java.util.Properties object.
   */
  public java.util.Properties getValue() {
    java.util.Properties currentRetValue = new java.util.Properties();
    java.util.Iterator<SwingPropertyEditor> iter = editors.iterator();
    while (iter.hasNext()) {
      currentRetValue.putAll(iter.next().getValue());
    }

    return currentRetValue;
  }

  /**
   * Returns the appropriate property for this source property.
   */
  public String createSubProperty(String pSource) {
    if (pSource.startsWith(".")) {
      if (manager.getProperty(editorTemplate + ".addSubProperty", "").equalsIgnoreCase("false") || manager.getProperty(createSubTemplate(pSource) + ".appendProperty", "true").equalsIgnoreCase("false")) {
        return property;
      } else {
        return property + pSource;
      }
    } else {
      if (manager.getProperty(editorTemplate + ".propertyScoped", "").equalsIgnoreCase("true")) {
        return property;
      } else {
        return pSource;
      }
    }
  }

  /**
   * Returns the appropriate tempate for this source property.
   */
  public String createSubTemplate(String pSource) {
    if (pSource.startsWith(".")) {
      return manager.getProperty(editorTemplate + ".templateBase", editorTemplate) + pSource;
    } else {
      return pSource;
    }
  }

  /**
   * Returns the appropriate propertyBase for this source property.
   */
  public String createSubPropertyBase(String pSource) {
    if (! pSource.startsWith(".") && ! manager.getProperty(editorTemplate + ".propertyScoped", "").equalsIgnoreCase("true")) {
      return pSource;
    } else {
      return propertyBase;
    }
  }

  /**
   * Lays out the composite property editor in a grid.
   */
  protected void layoutGrid(Container parent, Component[] labelComponents, Component[] valueComponents, int initialX, int initialY, int xPad, int yPad, boolean nested) {
    SpringLayout layout;
    try {
      layout = (SpringLayout)parent.getLayout();
    } catch (ClassCastException exc) {
      System.err.println("The first argument to layoutGrid must use SpringLayout.");
      return;
    }

    if (labelComponents == null || labelComponents.length < 1) {
      System.err.println("Attempt to layoutGrid with no components.");
      return;
    }

    // go through both columns.
    Spring labelWidth = Spring.constant(0);
    Spring valueWidth = Spring.constant(0);
    Spring fullWidth = Spring.constant(0);

    Spring labelValueXOffset = Spring.constant(initialX, initialX, 32000);
    Spring xOffset = Spring.constant(initialX, initialX, initialX);
    Spring fullXOffset = Spring.constant(initialX, initialX, 32000);

    for (int i = 0; i < labelComponents.length; i++) {
      // for components with a label and a value, add to labelWidth and
      // valueWidth.
      if (valueComponents[i] != null) {
        labelWidth = Spring.max(labelWidth, layout.getConstraints(labelComponents[i]).getWidth());
        valueWidth = Spring.max(valueWidth, layout.getConstraints(valueComponents[i]).getWidth());
      } else {
        // otherwise just add to fullWidth.
        fullWidth = Spring.max(fullWidth, layout.getConstraints(labelComponents[i]).getWidth());
      }
    }

    // make sure fullWidth and labelWidth + valueWidth match.
    if (fullWidth.getValue() <= labelWidth.getValue() + xPad + valueWidth.getValue()) {
      fullWidth = Spring.sum(labelWidth, Spring.sum(Spring.constant(xPad), valueWidth));
    } else {
      valueWidth = Spring.sum(fullWidth, Spring.minus(Spring.sum(Spring.constant(xPad), labelWidth)));
    }

    for (int i = 0; i < labelComponents.length; i++) {
      if (valueComponents[i] != null) {
        SpringLayout.Constraints constraints = layout.getConstraints(labelComponents[i]);
        //layout.putConstraint(SpringLayout.WEST, labelComponents[i], labelValueXOffset, SpringLayout.WEST, parent);
        layout.putConstraint(SpringLayout.WEST, labelComponents[i], xOffset, SpringLayout.WEST, parent);
        constraints.setWidth(labelWidth);

        constraints = layout.getConstraints(valueComponents[i]);
        layout.putConstraint(SpringLayout.WEST, valueComponents[i],  xPad, SpringLayout.EAST, labelComponents[i]);
        constraints.setWidth(valueWidth);
        if (i == 0) {
          layout.putConstraint(SpringLayout.EAST, parent, fullXOffset, SpringLayout.EAST, valueComponents[i]);
        }
      } else {
        // set for the full width.
        SpringLayout.Constraints constraints = layout.getConstraints(labelComponents[i]);
        //layout.putConstraint(SpringLayout.WEST, labelComponents[i], fullXOffset, SpringLayout.WEST, parent);
        layout.putConstraint(SpringLayout.WEST, labelComponents[i], xOffset, SpringLayout.WEST, parent);
        constraints.setWidth(fullWidth);
        if (i == 0) {
          layout.putConstraint(SpringLayout.EAST, parent, fullXOffset, SpringLayout.EAST, labelComponents[i]);
        }
      }
    }

    //Align all cells in each row and make them the same height.
    for (int i = 0; i < labelComponents.length; i++) {
      Spring height = Spring.constant(0);
      if (valueComponents[i] != null) {
        height = Spring.max(layout.getConstraints(labelComponents[i]).getHeight(), layout.getConstraints(valueComponents[i]).getHeight());
        if (i == 0) {
          layout.putConstraint(SpringLayout.NORTH, labelComponents[i], yPad, SpringLayout.NORTH, parent);
        } else {
          layout.putConstraint(SpringLayout.NORTH, labelComponents[i], yPad, SpringLayout.SOUTH, labelComponents[i - 1]);
        }
        layout.putConstraint(SpringLayout.NORTH, valueComponents[i], 0, SpringLayout.NORTH, labelComponents[i]);
        layout.putConstraint(SpringLayout.SOUTH, valueComponents[i], 0, SpringLayout.SOUTH, labelComponents[i]);

        layout.getConstraints(labelComponents[i]).setHeight(height);
        layout.getConstraints(valueComponents[i]).setHeight(height);
      } else {
        if (i == 0) {
          layout.putConstraint(SpringLayout.NORTH, labelComponents[i], yPad, SpringLayout.NORTH, parent);
        } else {
          layout.putConstraint(SpringLayout.NORTH, labelComponents[i], yPad, SpringLayout.SOUTH, labelComponents[i - 1]);
        }
      }
    }

    Spring southBoundary = Spring.constant(yPad, yPad, 32000);
    if (nested) {
      southBoundary = Spring.constant(yPad);
    }

    layout.putConstraint(SpringLayout.SOUTH, parent, southBoundary, SpringLayout.SOUTH, labelComponents[labelComponents.length - 1]);
    //Set the parent's size.
    //pCons.setConstraint(SpringLayout.EAST, Spring.sum(fullWidth, Spring.constant(initialX)));
  }

  /**
   * Adds a disable mask to the PropertyEditorUI.
   */
  public void addDisableMask(Object key) {
    disableMaskSet.add(key);
    updateEditorEnabled();
  }

  /**
   * Removes the disable mask keyed by this Object.
   */
  public void removeDisableMask(Object key) {
    disableMaskSet.remove(key);
    updateEditorEnabled();
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    if (isEditorEnabled()) {
      for(PropertyEditorUI editor: editors) {
        editor.removeDisableMask(this);
      }
    } else {
      for(PropertyEditorUI editor: editors) {
        editor.addDisableMask(this);
      }
    }
  }

  /**
   * Gets the parent PropertyEditorPane for the given component.
   */
  public PropertyEditorPane getPropertyEditorPane() {
    return getPropertyEditorPane(this);
  }

  /**
   * Returns the helpId for this editor.
   */
  public String getHelpID() {
    String subProperty = manager.getProperty(editorTemplate + ".helpController", "");
    if (subProperty.length() == 0)
      return getEditorTemplate();
    else {
      String controllerProperty = createSubTemplate(subProperty);
      Iterator<SwingPropertyEditor> iter = editors.iterator();
      while(iter.hasNext()) {
        PropertyEditorUI ui = iter.next();
        if (ui.getEditorTemplate().equals(controllerProperty)) {
          return ui.getHelpID();
        }
      }
    }

    return getEditorTemplate();
  }

  /**
   * Returns the display value for this property.
   */
  public String getDisplayValue() {
    return getProperty();
  }

  /**
   * Removes the PropertyEditor.
   */
  public void remove() {
    manager.removePropertyEditorListeners(getProperty());
    for (PropertyEditorUI editor: editors) {
      editor.remove();
    }
  }

  /**
   * Accepts or rejects the initial focus for this component.
   */
  public boolean acceptDefaultFocus() {
    if (editors != null) {
      for (SwingPropertyEditor editor: editors) {
        if (editor.acceptDefaultFocus()) {
          return true;
        }
      }
      return false;
    } else {
      return false;
    }
  }


}



