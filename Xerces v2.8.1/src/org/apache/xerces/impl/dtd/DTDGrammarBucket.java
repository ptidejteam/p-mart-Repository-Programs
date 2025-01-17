/*
 * Copyright 1999-2002,2004 The Apache Software Foundation.
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

package org.apache.xerces.impl.dtd;

import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import java.util.Hashtable;

/**
 * This very simple class is the skeleton of what the DTDValidator could use
 * to store various grammars that it gets from the GrammarPool.  As in the
 * case of XSGrammarBucket, one thinks of this object as being closely
 * associated with its validator; when fully mature, this class will be
 * filled from the GrammarPool when the DTDValidator is invoked on a
 * document, and, if a new DTD grammar is parsed, the new set will be
 * offered back to the GrammarPool for possible inclusion.
 * 
 * @xerces.internal
 *
 * @author Neil Graham, IBM
 *
 * @version $Id: DTDGrammarBucket.java 320090 2004-10-04 22:00:42Z mrglavas $
 */
public class DTDGrammarBucket {

    // REVISIT:  make this class smarter and *way* more complete!

    //
    // Data
    //

    /** Grammars associated with element root name. */
    protected Hashtable fGrammars;

    // the unique grammar from fGrammars (or that we're
    // building) that is used in validation.
    protected DTDGrammar fActiveGrammar;

    // is the "active" grammar standalone?
    protected boolean fIsStandalone;

    //
    // Constructors
    //

    /** Default constructor. */
    public DTDGrammarBucket() {
        fGrammars = new Hashtable();
    } // <init>()

    //
    // Public methods
    //

    /**
     * Puts the specified grammar into the grammar pool and associate it to
     * a root element name (this being internal, the lack of generality is irrelevant).
     * 
     * @param grammar     The grammar.
     */
    public void putGrammar(DTDGrammar grammar) {
        XMLDTDDescription desc = (XMLDTDDescription)grammar.getGrammarDescription();
        fGrammars.put(desc, grammar);
    } // putGrammar(DTDGrammar)

    // retrieve a DTDGrammar given an XMLDTDDescription
    public DTDGrammar getGrammar(XMLGrammarDescription desc) {
        return (DTDGrammar)(fGrammars.get((XMLDTDDescription)desc));
    } // putGrammar(DTDGrammar)

    public void clear() {
        fGrammars.clear();
        fActiveGrammar = null;
        fIsStandalone = false;
    } // clear()

    // is the active grammar standalone?  This must live here because
    // at the time the validator discovers this we don't yet know
    // what the active grammar should be (no info about root)
    void setStandalone(boolean standalone) {
        fIsStandalone = standalone;
    }

    boolean getStandalone() {
        return fIsStandalone;
    }

    // set the "active" grammar:
    void setActiveGrammar (DTDGrammar grammar) {
        fActiveGrammar = grammar;
    }
    DTDGrammar getActiveGrammar () {
        return fActiveGrammar;
    }
} // class DTDGrammarBucket
