/*
 * Copyright (c) 1999 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package org.w3c.dom.html;

/**
 * A selectable choice. See the OPTION element definition in HTML 4.0.
 */
public interface HTMLOptionElement extends HTMLElement {
  /**
   * Returns the <code>FORM</code> element containing this control. Returns 
   * <code>null</code> if this control is not within the context of a form. 
   */
  public HTMLFormElement    getForm();
  /**
   * Represents the value of the HTML selected attribute. The value of this 
   * attribute does not change if the state of the corresponding form 
   * control, in an interactive user agent, changes. Changing 
   * <code>defaultSelected</code>, however, resets the state of the form 
   * control. See the selected attribute definition in HTML 4.0.
   */
  public boolean            getDefaultSelected();
  public void               setDefaultSelected(boolean defaultSelected);
  /**
   * The text contained within the option element. 
   */
  public String             getText();
  /**
   * The index of this <code>OPTION</code> in its parent <code>SELECT</code>. 
   */
  public int                getIndex();
  /**
   * The control is unavailable in this context. See the disabled attribute 
   * definition in HTML 4.0.
   */
  public boolean            getDisabled();
  public void               setDisabled(boolean disabled);
  /**
   * Option label for use in hierarchical menus. See the label attribute 
   * definition in HTML 4.0.
   */
  public String             getLabel();
  public void               setLabel(String label);
  /**
   * Represents the current state of the corresponding form control, in an 
   * interactive user agent. Changing this attribute changes the state of the 
   * form control, but does not change the value of the HTML selected 
   * attribute of the element.
   */
  public boolean            getSelected();
  public void               setSelected(boolean selected);
  /**
   * The current form control value. See the value attribute definition in 
   * HTML 4.0.
   */
  public String             getValue();
  public void               setValue(String value);
}

