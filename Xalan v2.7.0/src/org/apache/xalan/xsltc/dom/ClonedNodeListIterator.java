/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: ClonedNodeListIterator.java,v 1.1 2006/03/01 21:15:45 vauchers Exp $
 */

package org.apache.xalan.xsltc.dom;

import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

/**
 * A ClonedNodeListIterator is returned by the cloneIterator() method
 * of a CachedNodeListIterator. Its next() method retrieves the nodes from
 * the cache of the CachedNodeListIterator.
 */
public final class ClonedNodeListIterator extends DTMAxisIteratorBase {

    /**
     * Source for this iterator.
     */
    private CachedNodeListIterator _source;
    private int _index = 0;

    public ClonedNodeListIterator(CachedNodeListIterator source) {
	_source = source;
    }

    public void setRestartable(boolean isRestartable) {
	//_isRestartable = isRestartable;
	//_source.setRestartable(isRestartable);
    }

    public DTMAxisIterator setStartNode(int node) {
	return this;
    }

    public int next() {
        return _source.getNode(_index++);
    }
    
    public int getPosition() {
    	return _index == 0 ? 1 : _index;
    }

    public int getNodeByPosition(int pos) {
    	return _source.getNode(pos);
    }
    
    public DTMAxisIterator cloneIterator() {
	return _source.cloneIterator();
    }

    public DTMAxisIterator reset() {
    	_index = 0;
    	return this;
    }
    
    public void setMark() {
	_source.setMark();
    }

    public void gotoMark() {
	_source.gotoMark();
    }
}
