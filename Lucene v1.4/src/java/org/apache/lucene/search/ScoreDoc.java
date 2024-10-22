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

/** Expert: Returned by low-level search implementations.
 * @see TopDocs */
public class ScoreDoc implements java.io.Serializable {
  /** Expert: The score of this document for the query. */
  public float score;

  /** Expert: A hit document's number.
   * @see Searcher#doc(int)
   */
  public int doc;

  /** Expert: Constructs a ScoreDoc. */
  public ScoreDoc(int doc, float score) {
    this.doc = doc;
    this.score = score;
  }
}
