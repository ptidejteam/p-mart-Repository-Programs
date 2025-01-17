package org.apache.lucene.index;

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

import java.io.IOException;

/** Abstract class for enumerating terms.

  <p>Term enumerations are always ordered by Term.compareTo().  Each term in
  the enumeration is greater than all that precede it.  */

public abstract class TermEnum {
  /** Increments the enumeration to the next element.  True if one exists.*/
  public abstract boolean next() throws IOException;

  /** Returns the current Term in the enumeration.*/
  public abstract Term term();

  /** Returns the docFreq of the current Term in the enumeration.*/
  public abstract int docFreq();

  /** Closes the enumeration to further activity, freeing resources. */
  public abstract void close() throws IOException;
  
// Term Vector support
  
  /** Skips terms to the first beyond the current whose value is
   * greater or equal to <i>target</i>. <p>Returns true iff there is such
   * an entry.  <p>Behaves as if written: <pre>
   *   public boolean skipTo(Term target) {
   *     do {
   *       if (!next())
   * 	     return false;
   *     } while (target > term());
   *     return true;
   *   }
   * </pre>
   * Some implementations are considerably more efficient than that.
   */
  public boolean skipTo(Term target) throws IOException {
     do {
        if (!next())
  	        return false;
     } while (target.compareTo(term()) > 0);
     return true;
  }
}
