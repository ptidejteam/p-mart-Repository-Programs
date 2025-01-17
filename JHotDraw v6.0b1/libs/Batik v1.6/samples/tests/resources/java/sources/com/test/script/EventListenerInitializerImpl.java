/*
 * Copyright 1999-2003,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.script;

import org.w3c.dom.Element;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import org.w3c.dom.svg.EventListenerInitializer;
import org.w3c.dom.svg.SVGDocument;

/**
 * This class implements the EventListenerInitializer interface.
 *
 * @author <a href="mailto:cjolif@apache.org">Christophe Jolif</a>
 * @version $Id: EventListenerInitializerImpl.java,v 1.1 2006/08/16 02:27:29 guehene Exp $
 */
public class EventListenerInitializerImpl implements EventListenerInitializer {

    /**
     * This method is called by the SVG viewer
     * when the scripts are loaded to register
     * the listener needed.
     * @param doc The current document.
     */
    public void initializeEventListeners(SVGDocument doc) {
        System.err.println(">>>>>>>>>>>>>>>>>>> SVGDocument : " + doc);
        ((EventTarget)doc.getElementById("testContent")).
            addEventListener("mousedown", new EventListener() {
                public void handleEvent(Event evt) {
                    ((Element)evt.getTarget()).setAttributeNS(null, "fill", "orange");
                }
            }, false);
    }
}

