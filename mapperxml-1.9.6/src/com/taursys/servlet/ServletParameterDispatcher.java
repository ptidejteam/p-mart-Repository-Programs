/**
 * ServletParameterDispatcher - Component responsible for receiving and dispatching Servlet ParameterEvents
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
import com.taursys.xml.event.ParameterEvent;
import javax.servlet.ServletRequest;
import java.util.Iterator;
import com.taursys.debug.Debug;

/**
 * Component responsible for receiving and dispatching Servlet ParameterEvents.
 * Parameters should be registered with this component by their parent container
 * to be notified whenever their inputs arrive.
 * <p>
 * This component will dispatch any inputs present in the ServletRequest
 * to registered Parameters.  The dispatch method provides this behavior.
 */
public class ServletParameterDispatcher extends Dispatcher {

  /**
   * Constructs a new dispatcher
   */
  public ServletParameterDispatcher() {
  }

  /**
   * Dispatches a ParameterEvent to each registered component.
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
      Parameter pc = (Parameter)iter.next();
      // fetch input value if parameter is not null or blank
      if (pc.getParameter() != null && pc.getParameter().length() > 0) {
        String value = request.getParameter(pc.getParameter());
        // Get defaultValue if input value is null
        if (value == null)
          value = pc.getDefaultValue();
        // Dispatch event only if NOT null
        if (value != null) {
          ParameterEvent e = new ParameterEvent(pc, pc.getParameter(), value);
          pc.dispatchEvent(e);
        }
      }
    }
  }
}
