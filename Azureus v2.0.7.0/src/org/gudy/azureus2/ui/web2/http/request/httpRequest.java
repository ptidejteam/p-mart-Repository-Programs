/*
 * Copyright (c) 2001 by Matt Welsh and The Regents of the University of 
 * California. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 * Author: Matt Welsh <mdw@cs.berkeley.edu>
 * Code to parse HTTP query strings by Eric Wagner.
 * 
 */

package org.gudy.azureus2.ui.web2.http.request;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import seda.sandStorm.api.ClassQueueElementIF;
import seda.sandStorm.api.SinkIF;
import seda.sandStorm.core.TimeStampedEvent;

/**
 * This class represents a single HTTP client request.
 * 
 * @author Matt Welsh
 */
public class httpRequest extends TimeStampedEvent implements ClassQueueElementIF {

  private static final boolean DEBUG = false;

  /** getRequest() code corresponding to a GET request. */
  public static final int REQUEST_GET = 0;
  /** getRequest() code corresponding to a POST request. */
  public static final int REQUEST_POST = 1;
  /** getRequest() code corresponding to a HEAD request. */
  public static final int REQUEST_HEAD = 2;
  /** getRequest() code corresponding to a CONNECT request. */
  public static final int REQUEST_CONNECT = 3;
  /** getRequest() code corresponding to a DELETE request. */
  public static final int REQUEST_DELETE = 4;
  /** getRequest() code corresponding to a OPTIONS request. */
  public static final int REQUEST_OPTIONS = 5;
  /** getRequest() code corresponding to a PUT request. */
  public static final int REQUEST_PUT = 6;
  /** getRequest() code corresponding to a TRACE request. */
  public static final int REQUEST_TRACE = 7;
  /** getRequest() code corresponding to a unknown request. */
  public static final int REQUEST_UNKNOWN = 99;

  /** getHttpVer() code corresponding to HTTP/0.9. */
  public static final int HTTPVER_09 = 0;
  /** getHttpVer() code corresponding to HTTP/1.0. */
  public static final int HTTPVER_10 = 1;
  /** getHttpVer() code corresponding to HTTP/1.1. */
  public static final int HTTPVER_11 = 2;

  /** Default value for a query key. */
  public static final String QUERY_KEY_SET = "true";

  private int request;
  private String url;
  private byte[] content;
  private int httpver;
  private int user_class = -2;
  
  private SinkIF compQ;
  private Object tag;

  private Vector rawHeader;
  private Hashtable header;
  private Hashtable get;
  private Hashtable post;
  private Hashtable query;

  /**
   * Package-internal: Create an httpRequest from the given connection,
   * request string, URL, HTTP version, and header.
   */
  httpRequest(SinkIF compQ, Object tag, String requestStr, String url, int httpver, Vector header) throws IOException {
    this.compQ = compQ;
    this.tag = tag;
    this.httpver = httpver;
    this.rawHeader = header;
    this.header = null;

    // Check to see if there is a query string
    int question = url.indexOf('?');
    if (question != -1) {
      get = new Hashtable();
      this.url = url.substring(0, question);
      StringTokenizer st = new StringTokenizer(url.substring(question + 1), ";&");
      while (st.hasMoreTokens()) {
        String name_value_pair = decodeURL(st.nextToken());
        int equals = name_value_pair.indexOf('=');

        if (equals == -1) {
          putVal(name_value_pair, QUERY_KEY_SET, get);
        } else {
          putVal(name_value_pair.substring(0, equals), name_value_pair.substring(equals + 1), get);
        }
      }
    } else {
      this.url = url;
    }

    if (requestStr.equalsIgnoreCase("get")) {
      request = REQUEST_GET;
    } else if (requestStr.equalsIgnoreCase("post")) {
      request = REQUEST_POST;
    } else if (requestStr.equalsIgnoreCase("options")) {
      request = REQUEST_OPTIONS;
    } else if (requestStr.equalsIgnoreCase("head")) {
      request = REQUEST_HEAD;
    } else if (requestStr.equalsIgnoreCase("put")) {
      request = REQUEST_PUT;
    } else if (requestStr.equalsIgnoreCase("delete")) {
      request = REQUEST_DELETE;
    } else if (requestStr.equalsIgnoreCase("trace")) {
      request = REQUEST_TRACE;
    } else if (requestStr.equalsIgnoreCase("connect")) {
      request = REQUEST_CONNECT;
    } else {
      request = REQUEST_UNKNOWN;
    }
  }

  httpRequest(SinkIF compQ, Object tag, String requestStr, String url, int httpver, Vector header, byte[] content) throws IOException {
    this(compQ, tag, requestStr, url, httpver, header);
    this.content = content;
    if (this.request == REQUEST_POST) {
      post = new Hashtable();
      StringTokenizer st = new StringTokenizer(new String(this.content), ";&");
      while (st.hasMoreTokens()) {
        String name_value_pair = decodeURL(st.nextToken());
        int equals = name_value_pair.indexOf('=');

        if (equals == -1) {
          putVal(name_value_pair, QUERY_KEY_SET, post);
        } else {
          putVal(name_value_pair.substring(0, equals), name_value_pair.substring(equals + 1), post);
        }
      }
    }

  }

  // Decode special characters in URLs 
  private String decodeURL(String encoded) {
    StringBuffer out = new StringBuffer(encoded.length());
    int i = 0;
    int j = 0;

    while (i < encoded.length()) {
      char ch = encoded.charAt(i);
      i++;
      if (ch == '+')
        ch = ' ';
      else if (ch == '%') {
        try {
          ch = (char) Integer.parseInt(encoded.substring(i, i + 2), 16);
          i += 2;
        } catch (StringIndexOutOfBoundsException se) {
          // If nothing's there, just ignore it
        }
      }
      out.append(ch);
      j++;
    }
    return out.toString();
  }

  // Add a key to the query set
  private void putVal(String key, String val, Map addto) {
    Object oldval = addto.get(key);
    if (oldval == null) {
      addto.put(key, val);
    } else if (oldval instanceof String) {
      addto.remove(key);
      Vector vec = new Vector(2);
      vec.addElement(oldval);
      vec.addElement(val);
      addto.put(key, vec);
    } else {
      Vector vec = (Vector) oldval;
      vec.addElement(val);
    }
    if (query == null)
      query = new Hashtable();
    oldval = query.get(key);
    if (oldval == null) {
      query.put(key, val);
    } else if (oldval instanceof String) {
      query.remove(key);
      Vector vec = new Vector(2);
      vec.addElement(oldval);
      vec.addElement(val);
      query.put(key, vec);
    } else {
      Vector vec = (Vector) oldval;
      vec.addElement(val);
    }
  }

  /**
   * Return the code corresponding to the request. Each code has
   * one of the REQUEST_* values from this class.
   */
  public int getRequest() {
    return request;
  }
  /**
   * Return the request URL.
   */
  public String getURL() {
    return url;
  }

  /**
   * Return the code corresponding to the HTTP version. Each code has
   * one of the HTTPVER_* values from this class.
   */
  public int getHttpVer() {
    return httpver;
  }

  /**
   * Return the completion Sink.
   */
  public SinkIF getSink() {
    return this.compQ;
  }

	/**
	 * Return the tag.
	 */
	public Object getTag() {
		return this.tag;
	}

  /**
   * Return the header line corresponding to the given key.
   * For example, to get the 'User-Agent' field from the header,
   * use <tt>getHeader("User-Agent")</tt>.
   */
  public String getHeader(String key) {
    if (header == null) {
      parseHeader();
    }
    if (header == null)
      return null;
    return (String) header.get(key);
  }

  /**
   * Return an enumeration of keys in the query string, if any.
   */
  public Enumeration getQueryKeys() {
    if (query == null)
      return null;
    return query.keys();
  }

  /**
   * Return the value associated with the given query key.
   * If a key as more than one value then only the first value
   * will be returned.
   */
  public String getQuery(String key) {
    if (query == null)
      return null;
    Object val = query.get(key);
    if (val == null)
      return null;
    else if (val instanceof String)
      return (String) val;
    else {
      Vector vec = (Vector) val;
      return (String) vec.elementAt(0);
    }
  }

  /**
   * Retrun the query hashmap. 
   */
  public Hashtable getQuery() {
    if (this.query == null)
      return null;
    return (this.query.isEmpty()) ? null : this.query;
  }

  /**
   * Return the set of values associated with the given query key.
   */
  public String[] getQuerySet(String key) {
    if (query == null)
      return null;
    Object val = query.get(key);
    if (val == null)
      return null;
    else if (val instanceof String) {
      String ret[] = new String[1];
      ret[0] = (String) val;
      return ret;
    } else {
      Vector vec = (Vector) val;
      Object ret[] = vec.toArray();
      String sret[] = new String[ret.length];
      for (int i = 0; i < ret.length; i++) {
        sret[i] = (String) ret[i];
      }
      return sret;
    }
  }

  /**
   * Returns the requests contents.
   * @return
   */
  public byte[] getContent() {
    return this.content;
  }

  /**
   * Indicates whether this request requires a header to be sent
   * in the response (that is, whether this is HTTP/1.0 or later).
   */
  public boolean headerNeeded() {
    if (getHttpVer() > httpRequest.HTTPVER_09) {
      return true;
    } else {
      return false;
    }
  }

  // Convert rawHeader to header (key, value) pairs
  private void parseHeader() {
    if (rawHeader == null)
      return;
    header = new Hashtable(1);
    for (int i = 0; i < rawHeader.size(); i++) {
      String h = (String) rawHeader.elementAt(i);
      StringTokenizer s = new StringTokenizer(h);
      String k = s.nextToken(":").trim();
      String v = s.nextToken().trim();
      if (DEBUG)
        System.err.println("httpRequest: key=" + k + ", val=" + v);
      header.put(k, v);
    }
  }

  public String toString() {
    String s = "httpRequest[";
    switch (request) {
      case REQUEST_GET :
        s += "GET ";
        break;
      case REQUEST_POST :
        s += "POST ";
        break;
      default :
        s += "??? ";
        break;
    }
    s += url + " ";
    switch (httpver) {
      case HTTPVER_09 :
        s += "HTTP/0.9";
        break;
      case HTTPVER_10 :
        s += "HTTP/1.0";
        break;
      case HTTPVER_11 :
        s += "HTTP/1.1";
        break;
    }

    if (header == null)
      parseHeader();
    if (header != null) {
      Enumeration e = header.keys();
      while (e.hasMoreElements()) {
        String key = (String) e.nextElement();
        String val = (String) header.get(key);
        s += "\n\t" + key + " " + val;
      }
    }
    s += "]";
    return s;
  }

  /* For ClassQueueElementIF */

  public int getRequestClass() {
    if (user_class == -2) {
      String s = getHeader("User-Class");
      if (s != null) {
        try {
          user_class = Integer.parseInt(s);
        } catch (NumberFormatException e) {
          user_class = -1;
        }
      } else {
        user_class = -1;
      }
    }
    return user_class;
  }

  public void setRequestClass(int theclass) {
    this.user_class = theclass;
  }

}
