package org.apache.lucene.analysis;

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

import java.io.StringReader;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

public class TestStopAnalyzer extends TestCase {
  private StopAnalyzer stop = new StopAnalyzer();

  private Set inValidTokens = new HashSet();
  public TestStopAnalyzer(String s) {
    super(s);
  }

  protected void setUp() {
    for (int i = 0; i < StopAnalyzer.ENGLISH_STOP_WORDS.length; i++) {
      inValidTokens.add(StopAnalyzer.ENGLISH_STOP_WORDS[i]);
    }
  }

  public void testDefaults() {
    assertTrue(stop != null);
    StringReader reader = new StringReader("This is a test of the english stop analyzer");
    TokenStream stream = stop.tokenStream("test", reader);
    assertTrue(stream != null);
    Token token = null;
    try {
      while ((token = stream.next()) != null)
      {
        assertTrue(inValidTokens.contains(token.termText()) == false);
      }
    } catch (IOException e) {
      assertTrue(false);
    }
  }

  public void testStopList() {
    Set stopWordsSet = new HashSet();
    stopWordsSet.add("good");
    stopWordsSet.add("test");
    stopWordsSet.add("analyzer");
    StopAnalyzer newStop = new StopAnalyzer((String[])stopWordsSet.toArray(new String[3]));
    StringReader reader = new StringReader("This is a good test of the english stop analyzer");
    TokenStream stream = newStop.tokenStream("test", reader);
    assertTrue(stream != null);
    Token token = null;
    try {
      while ((token = stream.next()) != null)
      {
        String text = token.termText();
        assertTrue(stopWordsSet.contains(text) == false);
      }
    } catch (IOException e) {
      assertTrue(false);
    }
  }
}
