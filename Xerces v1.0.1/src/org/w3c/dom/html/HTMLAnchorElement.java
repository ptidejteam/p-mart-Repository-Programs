/*
 * Copyright (c) 1999 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package org.w3c.dom.html;

/**
 * The anchor element. See the A element definition in HTML 4.0.
 */
public interface HTMLAnchorElement extends HTMLElement {
  /**
   * A single character access key to give access to the form control. See the 
   * accesskey attribute definition in HTML 4.0.
   */
  public String             getAccessKey();
  public void               setAccessKey(String accessKey);
  /**
   * The character encoding of the linked resource. See the charset attribute 
   * definition in HTML 4.0.
   */
  public String             getCharset();
  public void               setCharset(String charset);
  /**
   * Comma-separated list of lengths, defining an active region geometry.See 
   * also <code>shape</code> for the shape of the region. See the coords 
   * attribute definition in HTML 4.0.
   */
  public String             getCoords();
  public void               setCoords(String coords);
  /**
   * The URI of the linked resource. See the href attribute definition in HTML 
   * 4.0.
   */
  public String             getHref();
  public void               setHref(String href);
  /**
   * Language code of the linked resource. See the hreflang attribute 
   * definition in HTML 4.0.
   */
  public String             getHreflang();
  public void               setHreflang(String hreflang);
  /**
   * Anchor name. See the name attribute definition in HTML 4.0.
   */
  public String             getName();
  public void               setName(String name);
  /**
   * Forward link type. See the rel attribute definition in HTML 4.0.
   */
  public String             getRel();
  public void               setRel(String rel);
  /**
   * Reverse link type. See the rev attribute definition in HTML 4.0.
   */
  public String             getRev();
  public void               setRev(String rev);
  /**
   * The shape of the active area. The coordinates are givenby 
   * <code>coords</code>. See the shape attribute definition in HTML 4.0.
   */
  public String             getShape();
  public void               setShape(String shape);
  /**
   * Index that represents the element's position in the tabbing order. See 
   * the tabindex attribute definition in HTML 4.0.
   */
  public int                getTabIndex();
  public void               setTabIndex(int tabIndex);
  /**
   * Frame to render the resource in. See the target attribute definition in 
   * HTML 4.0.
   */
  public String             getTarget();
  public void               setTarget(String target);
  /**
   * Advisory content type. See the type attribute definition in HTML 4.0.
   */
  public String             getType();
  public void               setType(String type);
  /**
   * Removes keyboard focus from this element.
   */
  public void               blur();
  /**
   * Gives keyboard focus to this element.
   */
  public void               focus();
}

