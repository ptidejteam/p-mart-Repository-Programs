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

package org.apache.nutch.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.nutch.util.LogFormatter;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * Extractor to extract {@link org.apache.nutch.parse.Outlink}s 
 * / URLs from plain text using Regular Expressions.
 * 
 * @see <a
 *      href="http://wiki.java.net/bin/view/Javapedia/RegularExpressions">Comparison
 *      of different regexp-Implementations </a>
 * @see <a href="http://regex.info/java.html">Overview about Java Regexp APIs
 *      </a>
 * 
 * @author Stephan Strittmatter - http://www.sybit.de
 * @version 1.0
 * @since 0.7
 */
public class OutlinkExtractor {
  private static final Logger LOG = LogFormatter
      .getLogger(OutlinkExtractor.class.getName());

  /**
   * Regex pattern to get URLs within a plain text.
   * 
   * @see <a
   *      href="http://www.truerwords.net/articles/ut/urlactivation.html">http://www.truerwords.net/articles/ut/urlactivation.html
   *      </a>
   */
  private static final String URL_PATTERN = 
    "([A-Za-z][A-Za-z0-9+.-]+:[A-Za-z0-9/](([A-Za-z0-9$_.+!*,;/?:@&~=-])|%[A-Fa-f0-9]{2})+(#([a-zA-Z0-9][a-zA-Z0-9$_.+!*,;/?:@&~=%-]*))?)";

  /**
   * Extracts <code>Outlink</code> from given plain text.
   * 
   * @param plainText  the plain text from wich URLs should be extracted.
   * 
   * @return Array of <code>Outlink</code>s within found in plainText
   */
  public static Outlink[] getOutlinks(final String plainText) {
    return OutlinkExtractor.getOutlinks(plainText, "");
  }

  /**
   * Extracts <code>Outlink</code> from given plain text and adds anchor
   * to the extracted <code>Outlink</code>s
   * 
   * @param plainText the plain text from wich URLs should be extracted.
   * @param anchor    the anchor of the url
   * 
   * @return Array of <code>Outlink</code>s within found in plainText
   */
  public static Outlink[] getOutlinks(final String plainText, String anchor) {

    final List outlinks = new ArrayList();

    try {
      final PatternCompiler cp = new Perl5Compiler();
      final Pattern pattern = cp.compile(URL_PATTERN,
          Perl5Compiler.CASE_INSENSITIVE_MASK | Perl5Compiler.READ_ONLY_MASK
              | Perl5Compiler.MULTILINE_MASK);
      final PatternMatcher matcher = new Perl5Matcher();

      final PatternMatcherInput input = new PatternMatcherInput(plainText);

      MatchResult result;
      String url;

      //loop the matches
      while (matcher.contains(input, pattern)) {
        result = matcher.getMatch();
        url = result.group(0);
        outlinks.add(new Outlink(url, anchor));
      }
    } catch (Exception ex) {
      // if it is a malformed URL we just throw it away and continue with
      // extraction.
      LOG.throwing(OutlinkExtractor.class.getName(), "getOutlinks", ex);
    }

    final Outlink[] retval;

    //create array of the Outlinks
    if (outlinks != null && outlinks.size() > 0) {
      retval = (Outlink[]) outlinks.toArray(new Outlink[0]);
    } else {
      retval = new Outlink[0];
    }

    return retval;
  }
  

  /**
   * Extracts outlinks from a plain text. <br />
   * This Method takes the Jakarta Regexp API.
   * 
   * @param plainText
   * 
   * @return Array of <code>Outlink</code> s within found in plainText
   * @deprecated only for tests
   */
  private Outlink[] getOutlinksJakartaRegexpImpl(final String plainText) {

    throw new UnsupportedOperationException(
        "Implementation commented out. Please uncomment to use it.");

    // final List outlinks = new ArrayList();
    // String url;
    // Outlink link;
    //
    // RE re = new RE(URL_PATTERN);
    //
    // int pos = 0;
    //
    // while (re.match(plainText, pos)) {
    //
    // url = re.getParen(0);
    //
    // LOG.finest("Extracted url: " + url);
    //
    // try {
    //
    // link = new Outlink(url, null);
    // outlinks.add(link);
    //
    // } catch (MalformedURLException ex) {
    // // if it is a malformed URL we just throw it away and continue with
    // // extraction.
    // LOG.throwing(this.getClass().getName(), "getOutlinks", ex);
    // }
    //
    // pos = re.getParenEnd(0);
    // }
    //
    // final Outlink[] retval;
    //
    // if (pos > 0) {
    // retval = (Outlink[]) outlinks.toArray(new Outlink[0]);
    // } else {
    // retval = new Outlink[0];
    // }
    //
    // return retval;

  }

  /**
   * Extracts outlinks from a plain text.
   * </p>
   * This Method takes the JDK5 Regexp API.
   * 
   * @param plainText
   * 
   * @return Array of <code>Outlink</code> s within found in plainText
   * @deprecated only for tests
   */
  private Outlink[] getOutlinksJDK5Impl(final String plainText) {

    throw new UnsupportedOperationException(
        "Implementation commented out. Please uncomment to use it.");

    // final List outlinks = new ArrayList();
    // String url;
    // Outlink link;
    //
    // final Pattern urlPattern = Pattern.compile(URL_PATTERN);
    // final RE re = new RE(urlPattern);
    //
    // int pos = 0;
    //
    // while (re.match(plainText, pos)) {
    //
    // url = re.getParen(0);
    //
    // try {
    //
    // link = new Outlink(url, null);
    // outlinks.add(link);
    // } catch (MalformedURLException ex) {
    // // if it is a malformed URL we just throw it away and continue with
    // // extraction.
    // LOG.throwing(this.getClass().getName(), "getOutlinks", ex);
    // }
    //
    // pos = re.getParenEnd(0);
    // }
    //
    // final Outlink[] retval;
    //
    // if (pos > 0) {
    // retval = (Outlink[]) outlinks.toArray(new Outlink[0]);
    // } else {
    // retval = new Outlink[0];
    // }
    //
    // return retval;
  }
 
}
