/**
 * Example Mapper Application
 * by: Marty Phelan
 *
 * This example is free software; you can redistribute it and/or
 * modify it as you wish.  It is released to the public domain.
 *
 * This example is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.taursys.examples.simpleweb;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.taursys.debug.Debug;
import com.taursys.html.HTMLSelect;
import com.taursys.model.DefaultTextModel;
import com.taursys.model.ModelException;
import com.taursys.model.ObjectListValueHolder;
import com.taursys.model.TextModel;
import com.taursys.model.ValueHolder;
import com.taursys.util.DataTypes;
import com.taursys.xml.DocumentElement;
import com.taursys.xml.event.InputEvent;

/**
 * This is an example of building a custom compound component.
 *
 * This component is actually a container that contains three components
 * which actually input the parts of the whole value (month, day and year).
 * They are combined whenever they are all received. This is done by overriding
 * the processInputEvent method.
 *
 * If the model value changes (the actual Date), the changes are propagated
 * to the individual components (month, day, and year).
 */
public class CustomDateComponent extends DocumentElement {
  private String dayId;
  private String monthId;
  private String yearId;
  private HTMLSelect month = new HTMLSelect(getMonths());
  private HTMLSelect day = new HTMLSelect(getDays());
  private ObjectListValueHolder yearListHolder = new ObjectListValueHolder(new String[] {
     "2002", "2003", "2004"
      });
  private HTMLSelect year = new HTMLSelect();
  private TextModel model = new DefaultTextModel(DataTypes.TYPE_DATE);
  private boolean ignoreChange = false;
  private ChangeListener dateChangeListener = new ChangeListener() {
    public void stateChanged(ChangeEvent e) {
      holder_stateChanged(e);
    }
  };

  private static String[] getMonths() {
    String[] months13 = new DateFormatSymbols().getMonths();
    String[] months12 = new String[12];
    System.arraycopy(months13, 0, months12, 0, 12);
    return months12;
  }

  private static String[] getDays() {
    String[] days = new String[31];
    for (int i = 0; i < days.length; i++) {
      days[i] = "" + (i + 1);
    }
    return days;
  }

  /**
   * Constructs a new custom date component with default id's and parameters.
   */
  public CustomDateComponent() {
    this("day", "month", "year");
  }

  /**
   * Constructs a new custom date component with custom id's which are also
   * used as parameters.
   */
  public CustomDateComponent(String dayId, String monthId, String yearId) {
    this.dayId = dayId;
    this.monthId = monthId;
    this.yearId = yearId;
    try {
      jbInit();
    } catch (Exception ex) {
      Debug.debug("Cannot initialize CustomDateComponent: "
          + ex.getMessage(), ex);
    }
  }

  /**
   * Set component properties here.  It is not recommended to put anything
   * other than property settings in here.
   */
  private void jbInit() throws Exception {
    month.setId(monthId);
    month.setParameter(monthId);
    month.setNullDisplay("Month");
    month.setNullAllowed(false);

    day.setId(dayId);
    day.setParameter(dayId);
    day.setNullDisplay("Day");
    day.setNullAllowed(false);

    year.setId(yearId);
    year.setList(yearListHolder);
    year.setParameter(yearId);
    year.setNullDisplay("Year");
    year.setNullAllowed(false);

    model.setFormat(new SimpleDateFormat());
    model.setFormatPattern("MMMM-d-yyyy");
    model.getValueHolder().addChangeListener(dateChangeListener);

    this.addInputListener(new com.taursys.xml.event.InputListener() {
      public void inputReceived(InputEvent e) throws Exception {
        this_inputReceived(e);
      }
    });
    add(month);
    add(day);
    add(year);
  }

  /**
   * Set the list of available years
   */
  public void setYearList(String[] yearList) {
    yearListHolder.setList(Arrays.asList(yearList));
  }

  /**
   * Get the list of available years
   */
  public String[] getYearList() {
    return (String[])yearListHolder.getList().toArray(new String[]{});
  }

 /**
   * Returns the valueHolder for the model.  The valueHolder is the object
   * which holds the Object where the model stores the value.  The
   * default valueHolder is a VariantValueHolder with a javaDataType of String.
   */
  public com.taursys.model.ValueHolder getValueHolder() {
    return model.getValueHolder();
  }

  /**
   * Sets the valueHolder for the model.  The valueHolder is the object
   * which holds the Object where the model stores the value.  The
   * default valueHolder is a VariantValueHolder with a javaDataType of String.
   */
  public void setValueHolder(ValueHolder holder) {
    // remove listener from old holder
    if (model.getValueHolder() != null)
      model.getValueHolder().removeChangeListener(dateChangeListener);
    model.setValueHolder(holder);
    // register listener with new holder
    if (holder != null)
      holder.addChangeListener(dateChangeListener);  }

  /**
   * Sets the propertyName in the valueHolder where the model stores the value.
   * This name is ignored if you are using the default model (A DefaultTextModel
   * with a VariantValueHolder).
   */
  public void setPropertyName(String newPropertyName) {
    model.setPropertyName(newPropertyName);
  }

  /**
   * Returns the propertyName in the valueHolder where the model stores the value.
   * This name is ignored if you are using the default model (A DefaultTextModel
   * with a VariantValueHolder).
   */
  public String getPropertyName() {
    return model.getPropertyName();
  }

  /**
   * Propagate current date to subcomponents whenever it changes unless
   * ignoreChange flag is true
   */
  private void holder_stateChanged(ChangeEvent e) {
    // propagate new value to subcomponents
    if (!ignoreChange) {
      try {
        String value = model.getText();
        if (value != null) {
          StringTokenizer tokens = new StringTokenizer(value, "-");
          month.setText(tokens.nextToken());
          day.setText(tokens.nextToken());
          year.setText(tokens.nextToken());
        } else {
          month.setValue(null);
          day.setValue(null);
          year.setValue(null);
        }
      } catch (ModelException ex) {
        com.taursys.debug.Debug.error(ex);
      }
    }
  }

  /**
   * Process the inputReceived event, then combining the input values.
   * This method is invoked AFTER the event has been processed by the
   * children of this component.
   */
  void this_inputReceived(InputEvent e) throws Exception {
    // Combine input
    try {
      if (month.getValue() == null
          || day.getValue() == null
          || year.getValue() == null) {
        ignoreChange = true;
        model.setText(""); // set to null
      } else {
        ignoreChange = true;
        model.setText(month.getText() + "-" + day.getText() + "-" + year.getText());
      }
    } finally {
      ignoreChange = false;
    }
  }
}
