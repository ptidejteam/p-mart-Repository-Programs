package org.apache.lucene;

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

import java.util.GregorianCalendar;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.lucene.store.*;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.*;

/** JUnit adaptation of an older test case SearchTest.
 * @author dmitrys@earthlink.net
 * @version $Id: TestSearch.java,v 1.1 2006/01/31 01:29:28 vauchers Exp $
 */
public class TestSearch extends TestCase {

    /** Main for running test case by itself. */
    public static void main(String args[]) {
        TestRunner.run (new TestSuite(TestSearch.class));
    }

    /** This test performs a number of searches. It also compares output
     *  of searches using multi-file index segments with single-file
     *  index segments.
     *
     *  TODO: someone should check that the results of the searches are
     *        still correct by adding assert statements. Right now, the test
     *        passes if the results are the same between multi-file and
     *        single-file formats, even if the results are wrong.
     */
    public void testSearch() throws Exception {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      doTestSearch(pw, false);
      pw.close();
      sw.close();
      String multiFileOutput = sw.getBuffer().toString();
      //System.out.println(multiFileOutput);

      sw = new StringWriter();
      pw = new PrintWriter(sw, true);
      doTestSearch(pw, true);
      pw.close();
      sw.close();
      String singleFileOutput = sw.getBuffer().toString();

      assertEquals(multiFileOutput, singleFileOutput);
    }


    private void doTestSearch(PrintWriter out, boolean useCompoundFile)
    throws Exception
    {
      Directory directory = new RAMDirectory();
      Analyzer analyzer = new SimpleAnalyzer();
      IndexWriter writer = new IndexWriter(directory, analyzer, true);

      writer.setUseCompoundFile(useCompoundFile);

      String[] docs = {
        "a b c d e",
        "a b c d e a b c d e",
        "a b c d e f g h i j",
        "a c e",
        "e c a",
        "a c e a c e",
        "a c e a b c"
      };
      for (int j = 0; j < docs.length; j++) {
        Document d = new Document();
        d.add(Field.Text("contents", docs[j]));
        writer.addDocument(d);
      }
      writer.close();

      Searcher searcher = new IndexSearcher(directory);

      String[] queries = {
        "a b",
        "\"a b\"",
        "\"a b c\"",
        "a c",
        "\"a c\"",
        "\"a c e\"",
      };
      Hits hits = null;

      QueryParser parser = new QueryParser("contents", analyzer);
      parser.setPhraseSlop(4);
      for (int j = 0; j < queries.length; j++) {
        Query query = parser.parse(queries[j]);
        out.println("Query: " + query.toString("contents"));

      //DateFilter filter =
      //  new DateFilter("modified", Time(1997,0,1), Time(1998,0,1));
      //DateFilter filter = DateFilter.Before("modified", Time(1997,00,01));
      //System.out.println(filter);

        hits = searcher.search(query);

        out.println(hits.length() + " total results");
        for (int i = 0 ; i < hits.length() && i < 10; i++) {
          Document d = hits.doc(i);
          out.println(i + " " + hits.score(i)
// 			   + " " + DateField.stringToDate(d.get("modified"))
                             + " " + d.get("contents"));
        }
      }
      searcher.close();
  }

  static long Time(int year, int month, int day) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.set(year, month, day);
    return calendar.getTime().getTime();
  }
}
