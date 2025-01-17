/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.xerces.impl.xs.util;

import java.util.Vector;

import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNamespaceItemList;
/**
 * Containts a list of Object's.
 *
 * @xerces.internal 
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id: NSItemListImpl.java 446723 2006-09-15 20:37:45Z mrglavas $
 */
public class NSItemListImpl implements XSNamespaceItemList {

    // The array to hold all data
    private XSNamespaceItem[] fArray = null;
    // Number of elements in this list
    private int fLength = 0;

    // REVISIT: this is temp solution. In general we need to use this class
    //          instead of the Vector.
    private Vector fVector;

    public NSItemListImpl(Vector v) {
        fVector = v;        
        fLength = v.size();
    }

    /**
     * Construct an XSNamespaceItemList implementation
     * 
     * @param array     the data array
     * @param length    the number of elements
     */
    public NSItemListImpl(XSNamespaceItem[] array, int length) {
        fArray = array;
        fLength = length;
    }

    /**
     * The number of <code>Objects</code> in the list. The range of valid
     * child node indices is 0 to <code>length-1</code> inclusive.
     */
    public int getLength() {
        return fLength;
    }

    public XSNamespaceItem item(int index) {
        if (index < 0 || index >= fLength)
            return null;
        if (fVector != null) {
            return (XSNamespaceItem)fVector.elementAt(index);
        }
        return fArray[index];
    }

} // class XSParticle
