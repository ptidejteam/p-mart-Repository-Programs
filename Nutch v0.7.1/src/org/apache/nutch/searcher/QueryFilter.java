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

import org.apache.lucene.search.BooleanQuery;

/** Extension point for query translation.  Permits one to add metadata to a
 * query.  All plugins found which implement this extension point are run
 * sequentially on the query.
 */
public interface QueryFilter {
  /** The name of the extension point. */
  final static String X_POINT_ID = QueryFilter.class.getName();

  /** Adds clauses or otherwise modifies the BooleanQuery that will be
   * searched. */
  BooleanQuery filter(Query input, BooleanQuery translation)
    throws QueryException;
}
