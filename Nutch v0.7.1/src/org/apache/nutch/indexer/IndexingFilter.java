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

package org.apache.nutch.indexer;

import org.apache.lucene.document.Document;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.fetcher.FetcherOutput;

/** Extension point for indexing.  Permits one to add metadata to the indexed
 * fields.  All plugins found which implement this extension point are run
 * sequentially on the parse.
 */
public interface IndexingFilter {
  /** The name of the extension point. */
  final static String X_POINT_ID = IndexingFilter.class.getName();

  /** Adds fields or otherwise modifies the document that will be indexed for a
   * parse. */
  Document filter(Document doc, Parse parse, FetcherOutput fo)
    throws IndexingException;
}
