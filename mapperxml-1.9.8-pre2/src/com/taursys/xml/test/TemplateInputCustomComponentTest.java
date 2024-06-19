/**
 * TemplateInputCustomComponentTest
 *
 * Copyright (c) 2002
 *      Marty Phelan, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.taursys.xml.test;

import java.util.HashMap;

import junit.framework.TestCase;

import com.taursys.html.HTMLInputText;
import com.taursys.model.ModelException;
import com.taursys.model.ObjectArrayValueHolder;
import com.taursys.model.ValueHolder;
import com.taursys.xml.DocumentElement;
import com.taursys.xml.Form;
import com.taursys.xml.Template;
import com.taursys.xml.event.InputEvent;
import com.taursys.xml.event.InputListener;

/**
 * Tests input processing of a custom component (DocumentElement)
 * when used in a Template. An example would be a custom date field
 * which is made up of three separate fields: month, day, year, that
 * function as a single field returning a Date. This custom component
 * could then be used as part of a Template in a ServletForm to
 * allow input of multiple dates and store values in a Collection.
 * 
 * This test was created to solve a bug which caused the first
 * input value from the user to be stored in all members of the
 * collection. This was caused by a design defect in the Dispatcher.
 * When nesting components, each gets their own Dispatchers. The
 * Dispatcher contains an 'index' used for processing multi-value
 * inputs. Nesting caused the nested component's dispatcher to
 * only process the first element, rather than the current 'index'
 * of the parent component. 
 * 
 * @author marty
 */
public class TemplateInputCustomComponentTest extends TestCase {
  private TestForm form;
  private ObjectArrayValueHolder holder;
  
  /**
   * Form to use for testing
   * @author marty
   */
  class TestForm extends Form {
    public void execute() throws Exception {
      dispatchInput();
    }
  }
  
  /**
   * Custom component for use in test
   * @author marty
   */
  class CustomComponent extends DocumentElement {
    private HTMLInputText letter1 = new HTMLInputText();
    private HTMLInputText letter2 = new HTMLInputText();
    private ValueHolder holder;

    public CustomComponent() {
      letter1.setParameter("letter1");
      letter2.setParameter("letter2");
      letter2.addInputListener(new InputListener() {
        public void inputReceived(InputEvent e) {
          try {
            System.out.println("letter1=" + letter1.getText());
            System.out.println("letter2=" + letter2.getText());
            // Set holder directly instead of through a model
            holder.setPropertyValue("value",letter1.getText() + letter2.getText());
          } catch (ModelException e1) {
            e1.printStackTrace();
          }
        }
      });
    }
    
    public void addNotify() {
      add(letter1);
      add(letter2);
      // add ourselves to be notified after children
      super.addNotify();
    }

    public void removeNotify() {
      super.removeNotify();
      remove(letter1);
      remove(letter2);
    }
    
    public void setValueHolder(ValueHolder hold) {
      holder = hold;
    }
  }

  /**
   * @param arg0
   */
  public TemplateInputCustomComponentTest(String arg0) {
    super(arg0);
  }

  /* setUp method for test case */
  protected void setUp() {
    // Create and assemble the form for testing
    form = new TestForm();
    Template template = new Template();
    holder = new ObjectArrayValueHolder(new String[] {"A","B","C"});
    template.setCollectionValueHolder(holder);
    CustomComponent custom = new CustomComponent();
    custom.setValueHolder(holder);
    template.add(custom);
    form.add(template);
  }
  
  public void testProcessInput() throws Exception {
    HashMap map = new HashMap();
    map.put("letter1", new String[]{"X","Y","Z"});
    map.put("letter2", new String[]{"D","E","F"});
    form.setParameterMap(map);
    form.execute();
    Object[] values = holder.getArray();
    assertEquals("First value","XD", values[0]);
    assertEquals("First value","YE", values[1]);
    assertEquals("First value","ZF", values[2]);
  }
}
