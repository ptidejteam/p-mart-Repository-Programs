/**
 * ServletInputDispatcher - Component responsible for receiving and dispatching Servlet InputEvents
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
package com.taursys.servlet;

import com.taursys.xml.event.Dispatcher;
import com.taursys.xml.Parameter;
import com.taursys.xml.event.InputEvent;
import javax.servlet.ServletRequest;
import java.util.Iterator;

/**
 * Component responsible for receiving and dispatching Servlet InputEvents.
 * Fields should be registered with this component by their parent container
 * to be notified whenever their input arrives.
 * <p>
 * This component will dispatch any input present in the ServletRequest
 * to registered Fields.  The dispatch method provides this behavior.
 */
public class ServletInputDispatcher extends Dispatcher {

  /**
   * Constructs a new dispatcher
   */
  public ServletInputDispatcher() {
  }

  /**
   * Dispatches a InputEvent to each registered component.
   * Components will only be dispatched their own input as specified by
   * their parameter property.  If the component's parameter is present,
   * then the event will contain that value.  If the parameter is NOT present,
   * and the component's defaultValue is set, then the event will contain the
   * defaultValue.  If the parameter is NOT present and defaultValue
   * is NULL, no event will be dispatched.
   */
  public void dispatch(ServletRequest request) throws Exception {
    // iterate through each registered component and fetch
    Iterator iter = components.iterator();
    while (iter.hasNext()) {
      Parameter field = (Parameter)iter.next();
      // Get input parameter name -- skip if blank or null
      String pname = field.getParameter();
      if (pname != null && pname.length()>0) {
        // fetch input value
        String value = request.getParameter(pname);
        // Get defaultValue if input value is null
        if (value == null)
          value = field.getDefaultValue();
        // Dispatch event if present (not null)
        if (value != null) {
          InputEvent e = new InputEvent(field, field.getParameter(), value);
          field.dispatchEvent(e);
        }
      }
    }
  }
}
