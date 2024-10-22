package org.apache.lucene.search;

/**
 * Copyright 2004 The Apache Software Foundation
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

import java.util.BitSet;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;

/** Abstract base class providing a mechanism to restrict searches to a subset
 of an index. */
public abstract class Filter implements java.io.Serializable {
  /** Returns a BitSet with true for documents which should be permitted in
    search results, and false for those that should not. */
  public abstract BitSet bits(IndexReader reader) throws IOException;
}
