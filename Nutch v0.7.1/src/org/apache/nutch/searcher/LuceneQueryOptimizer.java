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

import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.search.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.ChainedFilter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;

import java.io.IOException;

/** Utility which converts certain query clauses into {@link QueryFilter}s and
 * caches these.  Only required clauses whose boost is zero are converted to
 * cached filters.  Range queries are converted to range filters.  This
 * accellerates query constraints like date, language, document format, etc.,
 * which do not affect ranking but might otherwise slow search considerably. */
class LuceneQueryOptimizer {
  private LinkedHashMap cache;                   // an LRU cache of QueryFilter

  private float threshold;

  /** Construct an optimizer that caches and uses filters for required clauses
   * whose boost is zero.
   * @param cacheSize the number of QueryFilters to cache
   * @param threshold the fraction of documents which must contain a term
   */
  public LuceneQueryOptimizer(final int cacheSize, float threshold) {
    this.cache = new LinkedHashMap(cacheSize, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
          return size() > cacheSize;              // limit size of cache
        }
      };
    this.threshold = threshold;
  }

  public TopDocs optimize(BooleanQuery original,
                          Searcher searcher, int numHits,
                          String sortField, boolean reverse)
    throws IOException {

    BooleanQuery query = new BooleanQuery();
    BooleanQuery cacheQuery = new BooleanQuery();
    BooleanQuery filterQuery = new BooleanQuery();
    ArrayList filters = new ArrayList();

    BooleanClause[] clauses = original.getClauses();
    for (int i = 0; i < clauses.length; i++) {
      BooleanClause c = clauses[i];
      if (c.required                              // required
          && c.query.getBoost() == 0.0f) {        // boost is zero

        if (c.query instanceof TermQuery          // TermQuery
            && (searcher.docFreq(((TermQuery)c.query).getTerm())
                / (float)searcher.maxDoc()) < threshold) { // beneath threshold
          query.add(c);                           // don't filterize
          continue;
        }
          
        if (c.query instanceof RangeQuery) {      // RangeQuery
          RangeQuery range = (RangeQuery)c.query;
          boolean inclusive = range.isInclusive();// convert to RangeFilter
          Term lower = range.getLowerTerm();
          Term upper = range.getUpperTerm();
          filters.add(new RangeFilter(lower!=null?lower.field():upper.field(),
                                      lower != null ? lower.text() : null,
                                      upper != null ? upper.text() : null,
                                      inclusive, inclusive));
          cacheQuery.add(c.query, true, false);   // cache it
          continue;
        }

        // all other query types
        filterQuery.add(c.query, true, false);    // filter it
        cacheQuery.add(c.query, true, false);     // cache it
        continue;
      }

      query.add(c);                               // query it
    }

    Filter filter = null;

    if (cacheQuery.getClauses().length != 0) {
      synchronized (cache) {                      // check cache
        filter = (Filter)cache.get(cacheQuery);
      }
      if (filter == null) {                       // miss

        if (filterQuery.getClauses().length != 0) // add filterQuery to filters
          filters.add(new QueryFilter(filterQuery));

        if (filters.size() == 1) {                // convert filters to filter
          filter = (Filter)filters.get(0);
        } else {
          filter = new ChainedFilter((Filter[])filters.toArray
                                     (new Filter[filters.size()]),
                                     ChainedFilter.AND);
        }
        if (!(filter instanceof QueryFilter))     // make sure bits are cached
          filter = new CachingWrapperFilter(filter);
        
        synchronized (cache) {
          cache.put(cacheQuery, filter);          // cache the filter
        }
      }        
    }

    if (sortField == null && !reverse) {
      return searcher.search(query, filter, numHits);
    } else {
      return searcher.search(query, filter, numHits,
                             new Sort(sortField, reverse));
    }
  }
}
