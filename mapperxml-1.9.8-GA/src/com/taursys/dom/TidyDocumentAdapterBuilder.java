/**
 * TidyDocumentAdapterBuilder
 *
 * Copyright (c) 2005
 *      Marty Phelan, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.taursys.dom;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;

import com.taursys.debug.Debug;

/**
 * TidyDocumentAdapterBuilder is used to create a
 * DOM_2_20001113_DocumentAdapter. It provides a variety of build methods with
 * options to validate the document source. It uses the JTidy 04Aug2000r7-dev
 * DOMParser to create the DocumentAdapter.
 * 
 * @author marty
 * @version $Revision: 1.2 $
 */
public class TidyDocumentAdapterBuilder extends DocumentAdapterBuilder {
  public static final String FEATURE_CORRECT_COMMENTS = "com.taursys.dom.TidyDocumentAdapter.correctComments";
  public static final String FEATURE_INDENT_DOCUMENT = "com.taursys.dom.TidyDocumentAdapter.indentDocument";

  public static final String FEATURE_ONLY_ERRORS = "org.w3c.tidy.Tidy.onlyErrors";
  public static final String FEATURE_QUIET = "org.w3c.tidy.Tidy.quiet";
  public static final String FEATURE_TIDY_MARK = "org.w3c.tidy.Tidy.tidyMark";

  public static final String PROPERTY_ERROR_OUT = "org.w3c.tidy.Tidy.errorOut";

  private static final String MESSAGE_PARSED_DOCUMENT_IS_NULL = "Parsed document is null";

  private Tidy parser;
  private boolean correctComments = true;
  private boolean indentDocument = true;

  /**
   * Constructs new builder with FEATURE_TIDY_MARK set false,
   * FEATURE_CORRECT_COMMENTS set true and PROPERTY_ERROR_OUT set to a dummy print
   * stream.
   */
  public TidyDocumentAdapterBuilder() {
    this.parser = new Tidy();
    setFeature(FEATURE_TIDY_MARK, false);
    setFeature(FEATURE_CORRECT_COMMENTS, true);
    setProperty(PROPERTY_ERROR_OUT,
        new PrintWriter(new ByteArrayOutputStream()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#setFeature(java.lang.String,
   *      boolean)
   */
  public void setFeature(String featureName, boolean value) {
    if (featureName.equals(FEATURE_CORRECT_COMMENTS)) {
      correctComments = value;
    } else if (featureName.equals(FEATURE_INDENT_DOCUMENT)) {
      indentDocument = value;
    } else if (featureName.equals(FEATURE_ONLY_ERRORS)) {
      parser.setOnlyErrors(value);
    } else if (featureName.equals(FEATURE_QUIET)) {
      parser.setQuiet(value);
    } else if (featureName.equals(FEATURE_TIDY_MARK)) {
      parser.setTidyMark(value);
    } else {
      Debug.debug("Feature " + featureName + " not recognized");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#getFeature(java.lang.String)
   */
  public boolean getFeature(String featureName) {
    if (featureName.equals(FEATURE_CORRECT_COMMENTS)) {
      return correctComments;
    } else if (featureName.equals(FEATURE_INDENT_DOCUMENT)) {
      return indentDocument;
    } else if (featureName.equals(FEATURE_ONLY_ERRORS)) {
      return parser.getOnlyErrors();
    } else if (featureName.equals(FEATURE_QUIET)) {
      return parser.getQuiet();
    } else if (featureName.equals(FEATURE_TIDY_MARK)) {
      return parser.getTidyMark();
    } else {
      Debug.debug("Feature " + featureName + " not recognized");
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#setProperty(java.lang.String,
   *      java.lang.Object)
   */
  public void setProperty(String propertyName, Object value) {
    if (propertyName.equals(PROPERTY_ERROR_OUT)) {
      parser.setErrout((PrintWriter) value);
    } else {
      Debug.debug("Property " + propertyName + " not recognized");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#getProperty(java.lang.String)
   */
  public Object getProperty(String propertyName) {
    if (propertyName.equals(PROPERTY_ERROR_OUT)) {
      return parser.getErrout();
    } else {
      Debug.debug("Property " + propertyName + " not recognized");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#build(java.io.InputStream)
   */
  public DocumentAdapter build(InputStream is)
      throws DocumentAdapterBuilderException {
    Document doc = null;

    if (is == null) {
      throw new DocumentAdapterBuilderException(MESSAGE_NULL_INPUT_STREAM);
    }
    try {
      doc = parser.parseDOM(is, null);
      if (doc == null) {
        throw new DocumentAdapterBuilderException(
            MESSAGE_PARSED_DOCUMENT_IS_NULL);
      }
      if (correctComments) {
        correctComments(doc);
      }
      if (indentDocument) {
        indentDocument(doc.getDocumentElement(), "");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new DocumentAdapterBuilderException(e.getMessage());
    }
    return DOMDocumentAdapterFactory.getInstance().newDOMDocumentAdapter(doc);
  }

  /**
   * Moves comments from Text node to Comment node
   * 
   * @param node
   *          Starting Node
   */
  private void correctComments(Node node) {
    String value = node.getNodeValue();
    if (value != null) {
      value = value.trim();
      if (value.startsWith("<!--") && value.endsWith("-->")) {
        value = value.substring(4, value.length() - 3);
        Comment comment = node.getOwnerDocument().createComment(value);
        node.getParentNode().appendChild(comment);
        node.setNodeValue("");
      }
    }
    Node child = node.getFirstChild();
    while (child != null) {
      correctComments(child);
      child = child.getNextSibling();
    }
  }

  /**
   * Adds text nodes to document elements to provide indentation format
   * 
   * @param node
   *          Starting Node
   * @param level
   *          Indentation level
   */
  private void indentDocument(Node node, String level) {
    if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
      Node child = node.getFirstChild();
      while (child != null) {
        if (child.getNodeType() == Node.ELEMENT_NODE) {
          node.insertBefore(node.getOwnerDocument().createTextNode(
              "\n" + level + "  "), child);
          indentDocument(child, level + "  ");
        } else if (child.getNodeType() == Node.COMMENT_NODE) {
          node.insertBefore(node.getOwnerDocument()
              .createTextNode("\n" + level), child);
        } else if (child.getNodeType() == Node.TEXT_NODE) {
          child.setNodeValue("\n" + level + "  " + child.getNodeValue().trim());
          //          node.insertBefore(node.getOwnerDocument().createTextNode("\n" +
          // level + " "), child);
        }
        child = child.getNextSibling();
      }
      if (node.hasChildNodes()) {
        node.appendChild(node.getOwnerDocument().createTextNode("\n" + level));
      }
    }
  }

  public boolean isCorrectComments() {
    return correctComments;
  }

  public void setCorrectComments(boolean correctComments) {
    this.correctComments = correctComments;
  }

  public boolean isIndentDocument() {
    return indentDocument;
  }

  public void setIndentDocument(boolean indentDocument) {
    this.indentDocument = indentDocument;
  }

  public boolean isOnlyErrors() {
    return parser.getOnlyErrors();
  }

  public boolean isQuiet() {
    return parser.getQuiet();
  }

  public boolean isTidyMark() {
    return parser.getTidyMark();
  }

  public void setOnlyErrors(boolean arg0) {
    parser.setOnlyErrors(arg0);
  }

  public void setQuiet(boolean arg0) {
    parser.setQuiet(arg0);
  }

  public void setTidyMark(boolean arg0) {
    parser.setTidyMark(arg0);
  }

  public PrintWriter getErrout() {
    return parser.getErrout();
  }

  public void setErrout(PrintWriter arg0) {
    parser.setErrout(arg0);
  }
}