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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultipleTermPositions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Query;

/**
 * PhrasePrefixQuery is a generalized version of PhraseQuery, with an added
 * method {@link #add(Term[])}.
 * To use this class, to search for the phrase "Microsoft app*" first use
 * add(Term) on the term "Microsoft", then find all terms that has "app" as
 * prefix using IndexReader.terms(Term), and use PhrasePrefixQuery.add(Term[]
 * terms) to add them to the query.
 *
 * @author Anders Nielsen
 * @version 1.0
 */
public class PhrasePrefixQuery extends Query {
  private String field;
  private ArrayList termArrays = new ArrayList();

  private int slop = 0;

  /* Sets the phrase slop for this query.
   * @see PhraseQuery#setSlop(int)
   */
  public void setSlop(int s) { slop = s; }

  /* Sets the phrase slop for this query.
   * @see PhraseQuery#getSlop()
   */
  public int getSlop() { return slop; }

  /* Add a single term at the next position in the phrase.
   * @see PhraseQuery#add(Term)
   */
  public void add(Term term) { add(new Term[]{term}); }

  /* Add multiple terms at the next position in the phrase.  Any of the terms
   * may match.
   *
   * @see PhraseQuery#add(Term)
   */
  public void add(Term[] terms) {
    if (termArrays.size() == 0)
      field = terms[0].field();
    
    for (int i=0; i<terms.length; i++) {
      if (terms[i].field() != field) {
        throw new IllegalArgumentException
          ("All phrase terms must be in the same field (" + field + "): "
           + terms[i]);
      }
    }

    termArrays.add(terms);
  }

  private class PhrasePrefixWeight implements Weight {
    private Searcher searcher;
    private float value;
    private float idf;
    private float queryNorm;
    private float queryWeight;

    public PhrasePrefixWeight(Searcher searcher) {
      this.searcher = searcher;
    }

    public Query getQuery() { return PhrasePrefixQuery.this; }
    public float getValue() { return value; }

    public float sumOfSquaredWeights() throws IOException {
      Iterator i = termArrays.iterator();
      while (i.hasNext()) {
        Term[] terms = (Term[])i.next();
        for (int j=0; j<terms.length; j++)
          idf += getSimilarity(searcher).idf(terms[j], searcher);
      }

      queryWeight = idf * getBoost();             // compute query weight
      return queryWeight * queryWeight;           // square it
    }

    public void normalize(float queryNorm) {
      this.queryNorm = queryNorm;
      queryWeight *= queryNorm;                   // normalize query weight
      value = queryWeight * idf;                  // idf for document 
    }

    public Scorer scorer(IndexReader reader) throws IOException {
      if (termArrays.size() == 0)                  // optimize zero-term case
        return null;
    
      TermPositions[] tps = new TermPositions[termArrays.size()];
      for (int i=0; i<tps.length; i++) {
        Term[] terms = (Term[])termArrays.get(i);
      
        TermPositions p;
        if (terms.length > 1)
          p = new MultipleTermPositions(reader, terms);
        else
          p = reader.termPositions(terms[0]);
      
        if (p == null)
          return null;
      
        tps[i] = p;
      }
    
      if (slop == 0)
        return new ExactPhraseScorer(this, tps, getSimilarity(searcher),
                                     reader.norms(field));
      else
        return new SloppyPhraseScorer(this, tps, getSimilarity(searcher),
                                      slop, reader.norms(field));
    }
    
    public Explanation explain(IndexReader reader, int doc)
      throws IOException {
      Explanation result = new Explanation();
      result.setDescription("weight("+getQuery()+" in "+doc+"), product of:");

      Explanation idfExpl = new Explanation(idf, "idf("+getQuery()+")");
      
      // explain query weight
      Explanation queryExpl = new Explanation();
      queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");

      Explanation boostExpl = new Explanation(getBoost(), "boost");
      if (getBoost() != 1.0f)
        queryExpl.addDetail(boostExpl);

      queryExpl.addDetail(idfExpl);
      
      Explanation queryNormExpl = new Explanation(queryNorm,"queryNorm");
      queryExpl.addDetail(queryNormExpl);
      
      queryExpl.setValue(boostExpl.getValue() *
                         idfExpl.getValue() *
                         queryNormExpl.getValue());

      result.addDetail(queryExpl);
     
      // explain field weight
      Explanation fieldExpl = new Explanation();
      fieldExpl.setDescription("fieldWeight("+getQuery()+" in "+doc+
                               "), product of:");

      Explanation tfExpl = scorer(reader).explain(doc);
      fieldExpl.addDetail(tfExpl);
      fieldExpl.addDetail(idfExpl);

      Explanation fieldNormExpl = new Explanation();
      byte[] fieldNorms = reader.norms(field);
      float fieldNorm =
        fieldNorms!=null ? Similarity.decodeNorm(fieldNorms[doc]) : 0.0f;
      fieldNormExpl.setValue(fieldNorm);
      fieldNormExpl.setDescription("fieldNorm(field="+field+", doc="+doc+")");
      fieldExpl.addDetail(fieldNormExpl);

      fieldExpl.setValue(tfExpl.getValue() *
                         idfExpl.getValue() *
                         fieldNormExpl.getValue());
      
      result.addDetail(fieldExpl);

      // combine them
      result.setValue(queryExpl.getValue() * fieldExpl.getValue());

      if (queryExpl.getValue() == 1.0f)
        return fieldExpl;

      return result;
    }
  }

  protected Weight createWeight(Searcher searcher) {
    if (termArrays.size() == 1) {                 // optimize one-term case
      Term[] terms = (Term[])termArrays.get(0);
      BooleanQuery boq = new BooleanQuery();
      for (int i=0; i<terms.length; i++) {
        boq.add(new TermQuery(terms[i]), false, false);
      }
      boq.setBoost(getBoost());
      return boq.createWeight(searcher);
    }
    return new PhrasePrefixWeight(searcher);
  }

  /** Prints a user-readable version of this query. */
  public final String toString(String f) {
    StringBuffer buffer = new StringBuffer();
    if (!field.equals(f)) {
      buffer.append(field);
      buffer.append(":");
    }

    buffer.append("\"");
    Iterator i = termArrays.iterator();
    while (i.hasNext()) {
      Term[] terms = (Term[])i.next();
      buffer.append(terms[0].text() + (terms.length > 0 ? "*" : ""));
    }
    buffer.append("\"");

    if (slop != 0) {
      buffer.append("~");
      buffer.append(slop);
    }

    if (getBoost() != 1.0f) {
      buffer.append("^");
      buffer.append(Float.toString(getBoost()));
    }

    return buffer.toString();
  }
}
