/*
 * Copyright 2005 The Apache Software Foundation.
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

package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

/**
 * <p>Filter {@link XMLGrammarPool} that exposes a 
 * read-only view of the underlying pool.</p>
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 * @version $Id: ReadOnlyGrammarPool.java,v 1.1 2006/02/02 01:00:07 vauchers Exp $
 */
final class ReadOnlyGrammarPool implements XMLGrammarPool {
    
    private final XMLGrammarPool core;
    
    public ReadOnlyGrammarPool( XMLGrammarPool pool ) {
        this.core = pool;
    }
    
    public void cacheGrammars(String grammarType, Grammar[] grammars) {
        // noop. don't let caching to happen
    }

    public void clear() {
        // noop. cache is read-only.
    }

    public void lockPool() {
        // noop. this pool is always read-only
    }

    public Grammar retrieveGrammar(XMLGrammarDescription desc) {
        return core.retrieveGrammar(desc);
    }

    public Grammar[] retrieveInitialGrammarSet(String grammarType) {
        return core.retrieveInitialGrammarSet(grammarType);
    }

    public void unlockPool() {
        // noop. this pool is always read-only.
    }

} // ReadOnlyGrammarPool
