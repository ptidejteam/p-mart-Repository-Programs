/**
 * TemplateTriggerDispatcher - Dispatches TriggerEvents for Templates
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
package com.taursys.xml.event;

import java.util.Map;

import com.taursys.debug.Debug;
import com.taursys.model.CollectionValueHolder;
import com.taursys.model.ModelParseException;
import com.taursys.model.MultiModelParseException;
import com.taursys.xml.Template;

/**
 * TemplateTriggerDispatcher is Dispatcher of TriggerEvents for Templates
 * @author Marty Phelan
 * @version 1.0
 */
public class TemplateTriggerDispatcher extends TriggerDispatcher {
  private Template template;

  /**
   * Constructs a new TemplateTriggerDispatcher
   */
  public TemplateTriggerDispatcher(Template template) {
    this.template = template;
  }

  /**
   * Dispatches an Event to each registered component with the given key/value
   * <code>Map</code>. This method invokes the <code>dispatchToComponent</code>
   * for each registered component.
   * @param map a Map containing message key/values for dispatching
   */
  public void dispatch(Map map) throws Exception {
    MultiModelParseException ex = null;
    CollectionValueHolder collectionValueHolder =
        template.getCollectionValueHolder();
    if (collectionValueHolder != null) {
      resetIndex();
      collectionValueHolder.reset();
      while (collectionValueHolder.hasNext()) {
        collectionValueHolder.next();
        try {
          super.dispatch(map);
        } catch (MultiModelParseException e) {
          Debug.debug("Processing MultiModelParseException");
          if (ex == null) {
            ex = e;
          } else {
            ex.add(e);
          }
        } catch (ModelParseException e) {
          Debug.debug("Processing ModelParseException");
          if (ex == null) {
            ex = new MultiModelParseException(e);
          } else {
            ex.add(e);
          }
        }
        incrementIndex();
      }
    } else {
      Debug.warn("Template with id=" + template.getId()
          + " has a null CollectionValueHolder during trigger processing.");
    }
    if (ex != null) {
      throw ex;
    }
  }
}
