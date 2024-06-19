/**
 * ServletTriggerDispatcher - Component responsible for receiving and dispatching Servlet TriggerEvents
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
import com.taursys.xml.Trigger;
import com.taursys.xml.event.TriggerEvent;
import javax.servlet.ServletRequest;
import java.util.Iterator;


/**
 * Component responsible for receiving and dispatching Servlet TriggerEvents.
 * Triggers should be registered with this component by their parent container
 * to be notified whenever their trigger value arrives.
 * <p>
 * This component will dispatch any matching trigger values present in the ServletRequest
 * to registered Triggers.  The dispatch method provides this behavior.
 */
public class ServletTriggerDispatcher extends Dispatcher {

  /**
   * Constructs a new dispatcher
   */
  public ServletTriggerDispatcher() {
  }

  /**
   * Dispatches a TriggerEvent to each registered component if a matching input value is present in request.
   * Triggers will only be dispatched an event if their specified name/value pair
   * if present in the ServletRequest.
   */
  public void dispatch(ServletRequest request) throws Exception {
    // iterate through each registered component and fetch
    Iterator iter = components.iterator();
    while (iter.hasNext()) {
      Trigger button = (Trigger)iter.next();
      // Get input parameter name -- skip if blank or null
      String pname = button.getParameter();
      if (pname != null && pname.length()>0) {
        // fetch input value
        String value = request.getParameter(pname);
        // Dispatch if default trigger or value received
        if (value == null && button.isDefaultTrigger()) {
          value = button.getText();
          TriggerEvent e = new TriggerEvent(button, button.getParameter(), value);
          button.dispatchEvent(e);
        } else if (value != null && value.equals(button.getText())) {
          TriggerEvent e = new TriggerEvent(button, button.getParameter(), value);
          button.dispatchEvent(e);
        }
      }
    }
  }
}
