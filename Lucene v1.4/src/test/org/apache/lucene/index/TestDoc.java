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
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.demo.FileDocument;

import java.io.*;
import java.util.*;


/** JUnit adaptation of an older test case DocTest.
 * @author dmitrys@earthlink.net
 * @version $Id: TestDoc.java,v 1.1 2006/01/31 01:29:25 vauchers Exp $
 */
public class TestDoc extends TestCase {

    /** Main for running test case by itself. */
    public static void main(String args[]) {
        TestRunner.run (new TestSuite(TestDoc.class));
    }


    private File workDir;
    private File indexDir;
    private LinkedList files;


    /** Set the test case. This test case needs
     *  a few text files created in the current working directory.
     */
    public void setUp() throws IOException {
        workDir = new File(System.getProperty("tempDir"),"TestDoc");
        workDir.mkdirs();

        indexDir = new File(workDir, "testIndex");
        indexDir.mkdirs();

        Directory directory = FSDirectory.getDirectory(indexDir, true);
        directory.close();

        files = new LinkedList();
        files.add(createFile("test.txt",
            "This is the first test file"
        ));

        files.add(createFile("test2.txt",
            "This is the second test file"
        ));
    }

    private File createFile(String name, String text) throws IOException {
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            File f = new File(workDir, name);
            if (f.exists()) f.delete();

            fw = new FileWriter(f);
            pw = new PrintWriter(fw);
            pw.println(text);
            return f;

        } finally {
            if (pw != null) pw.close();
            if (fw != null) fw.close();
        }
    }


    /** This test executes a number of merges and compares the contents of
     *  the segments created when using compound file or not using one.
     *
     *  TODO: the original test used to print the segment contents to System.out
     *        for visual validation. To have the same effect, a new method
     *        checkSegment(String name, ...) should be created that would
     *        assert various things about the segment.
     */
    public void testIndexAndMerge() throws Exception {
      StringWriter sw = new StringWriter();
      PrintWriter out = new PrintWriter(sw, true);

      Directory directory = FSDirectory.getDirectory(indexDir, true);
      directory.close();

      indexDoc("one", "test.txt");
      printSegment(out, "one");

      indexDoc("two", "test2.txt");
      printSegment(out, "two");

      merge("one", "two", "merge", false);
      printSegment(out, "merge");

      merge("one", "two", "merge2", false);
      printSegment(out, "merge2");

      merge("merge", "merge2", "merge3", false);
      printSegment(out, "merge3");

      out.close();
      sw.close();
      String multiFileOutput = sw.getBuffer().toString();
      //System.out.println(multiFileOutput);

      sw = new StringWriter();
      out = new PrintWriter(sw, true);

      directory = FSDirectory.getDirectory(indexDir, true);
      directory.close();

      indexDoc("one", "test.txt");
      printSegment(out, "one");

      indexDoc("two", "test2.txt");
      printSegment(out, "two");

      merge("one", "two", "merge", true);
      printSegment(out, "merge");

      merge("one", "two", "merge2", true);
      printSegment(out, "merge2");

      merge("merge", "merge2", "merge3", true);
      printSegment(out, "merge3");

      out.close();
      sw.close();
      String singleFileOutput = sw.getBuffer().toString();

      assertEquals(multiFileOutput, singleFileOutput);
   }


   private void indexDoc(String segment, String fileName)
   throws Exception
   {
      Directory directory = FSDirectory.getDirectory(indexDir, false);
      Analyzer analyzer = new SimpleAnalyzer();
      DocumentWriter writer =
         new DocumentWriter(directory, analyzer, Similarity.getDefault(), 1000);

      File file = new File(workDir, fileName);
      Document doc = FileDocument.Document(file);

      writer.addDocument(segment, doc);

      directory.close();
   }


   private void merge(String seg1, String seg2, String merged, boolean useCompoundFile)
   throws Exception {
      Directory directory = FSDirectory.getDirectory(indexDir, false);

      SegmentReader r1 = new SegmentReader(new SegmentInfo(seg1, 1, directory));
      SegmentReader r2 = new SegmentReader(new SegmentInfo(seg2, 1, directory));

      SegmentMerger merger =
        new SegmentMerger(directory, merged, useCompoundFile);

      merger.add(r1);
      merger.add(r2);
      merger.merge();
      merger.closeReaders();

      directory.close();
   }


   private void printSegment(PrintWriter out, String segment)
   throws Exception {
      Directory directory = FSDirectory.getDirectory(indexDir, false);
      SegmentReader reader =
        new SegmentReader(new SegmentInfo(segment, 1, directory));

      for (int i = 0; i < reader.numDocs(); i++)
        out.println(reader.document(i));

      TermEnum tis = reader.terms();
      while (tis.next()) {
        out.print(tis.term());
        out.println(" DF=" + tis.docFreq());

        TermPositions positions = reader.termPositions(tis.term());
        try {
          while (positions.next()) {
            out.print(" doc=" + positions.doc());
            out.print(" TF=" + positions.freq());
            out.print(" pos=");
            out.print(positions.nextPosition());
            for (int j = 1; j < positions.freq(); j++)
              out.print("," + positions.nextPosition());
            out.println("");
          }
        } finally {
          positions.close();
        }
      }
      tis.close();
      reader.close();
      directory.close();
    }
}
