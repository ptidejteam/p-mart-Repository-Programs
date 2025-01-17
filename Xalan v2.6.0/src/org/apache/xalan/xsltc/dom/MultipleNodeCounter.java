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
 * $Id: MultipleNodeCounter.java,v 1.1 2006/03/09 00:07:41 vauchers Exp $
 */

package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xml.dtm.DTMAxisIterator;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
public abstract class MultipleNodeCounter extends NodeCounter {
    private DTMAxisIterator _precSiblings = null;

    public MultipleNodeCounter(Translet translet,
			       DOM document, DTMAxisIterator iterator) {
	super(translet, document, iterator);
    }
	
    public NodeCounter setStartNode(int node) {
	_node = node;
	_nodeType = _document.getExpandedTypeID(node);
	_precSiblings = _document.getAxisIterator(PRECEDINGSIBLING);
	return this;
    }

    public String getCounter() {
	if (_value != Integer.MIN_VALUE) {
	    return formatNumbers(_value);
	}

	IntegerArray ancestors = new IntegerArray();

	// Gather all ancestors that do not match from pattern
	int next = _node;
	ancestors.add(next);		// include self
	while ((next = _document.getParent(next)) > END && 
	       !matchesFrom(next)) {
	    ancestors.add(next);
	}

	// Create an array of counters
	final int nAncestors = ancestors.cardinality();
	final int[] counters = new int[nAncestors]; 
	for (int i = 0; i < nAncestors; i++) {
	    counters[i] = Integer.MIN_VALUE;
	}

	// Increment array of counters according to semantics
	for (int j = 0, i = nAncestors - 1; i >= 0 ; i--, j++) {
	    final int counter = counters[j];
	    final int ancestor = ancestors.at(i);

	    if (matchesCount(ancestor)) {
		_precSiblings.setStartNode(ancestor);
		while ((next = _precSiblings.next()) != END) {
		    if (matchesCount(next)) {
			counters[j] = (counters[j] == Integer.MIN_VALUE) ? 1 
			    : counters[j] + 1;		
		    }
		}
		// Count the node itself
		counters[j] = counters[j] == Integer.MIN_VALUE
		    ? 1 
		    : counters[j] + 1;	
	    }
	}
	return formatNumbers(counters);
    }

    public static NodeCounter getDefaultNodeCounter(Translet translet,
						    DOM document,
						    DTMAxisIterator iterator) {
	return new DefaultMultipleNodeCounter(translet, document, iterator);
    }

    static class DefaultMultipleNodeCounter extends MultipleNodeCounter {
	public DefaultMultipleNodeCounter(Translet translet,
					  DOM document,
					  DTMAxisIterator iterator) {
	    super(translet, document, iterator);
	}
    }
}
