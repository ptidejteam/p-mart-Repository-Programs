/**
 * Copyright 2005 The Apache Software Foundation
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

package org.apache.nutch.searcher;

import java.io.IOException;

/** Service that searches. */
public interface Searcher {
  /** Return the top-scoring hits for a query. */
  Hits search(Query query, int numHits,
              String dedupField, String sortField, boolean reverse)
    throws IOException;

  /** Return an HTML-formatted explanation of how a query scored. */
  String getExplanation(Query query, Hit hit) throws IOException;
}
