/**
 * TestServletRequest - partial implementation of ServletRequest for testing
 *
 * Copyright (c) 2001
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
package com.taursys.servlet.test;

import javax.servlet.ServletRequest;
import java.util.Enumeration;
import javax.servlet.ServletInputStream;
import java.util.Locale;
import java.io.BufferedReader;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.BufferedReader;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * ServletRequest object partial implementation for testing purposes
 */
public class TestHttpServletRequest implements HttpServletRequest {
  private java.util.Hashtable parameters = new java.util.Hashtable();
  private String contentType = "";
  private BufferedReader reader;
  private ServletInputStream inputStream;

  public TestHttpServletRequest() {
  }

  /**
   * Testing setup method to add simulated parameters to request
   */
  public void addParameter(String parmName, Object value) {
    parameters.put(parmName, value);
  }

  public Object getAttribute(String name) {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getAttribute() not yet implemented.");
  }
  public Enumeration getAttributeNames() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getAttributeNames() not yet implemented.");
  }
  public String getCharacterEncoding() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getCharacterEncoding() not yet implemented.");
  }
  public int getContentLength() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getContentLength() not yet implemented.");
  }
  public String getContentType() {
    return contentType;
  }
  public ServletInputStream getInputStream() throws IOException {
    return inputStream;
  }
  public String getParameter(String name) {
    return (String)parameters.get(name);
  }
  public Enumeration getParameterNames() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getParameterNames() not yet implemented.");
  }
  public String[] getParameterValues(String name) {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getParameterValues() not yet implemented.");
  }
  public String getProtocol() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getProtocol() not yet implemented.");
  }
  public String getScheme() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getScheme() not yet implemented.");
  }
  public String getServerName() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getServerName() not yet implemented.");
  }
  public int getServerPort() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getServerPort() not yet implemented.");
  }
  public BufferedReader getReader() throws IOException {
    return reader;
  }
  public String getRemoteAddr() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getRemoteAddr() not yet implemented.");
  }
  public String getRemoteHost() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getRemoteHost() not yet implemented.");
  }
  public void setAttribute(String name, Object o) {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method setAttribute() not yet implemented.");
  }
  public void removeAttribute(String name) {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method removeAttribute() not yet implemented.");
  }
  public Locale getLocale() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getLocale() not yet implemented.");
  }
  public Enumeration getLocales() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getLocales() not yet implemented.");
  }
  public boolean isSecure() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method isSecure() not yet implemented.");
  }
  public RequestDispatcher getRequestDispatcher(String path) {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getRequestDispatcher() not yet implemented.");
  }
  public String getRealPath(String path) {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getRealPath() not yet implemented.");
  }
  public void setContentType(String newContentType) {
    contentType = newContentType;
  }
  public void setReader(BufferedReader newReader) {
    reader = newReader;
  }
  public void setInputStream(ServletInputStream newInputStream) {
    inputStream = newInputStream;
  }
  public String getAuthType() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getAuthType() not yet implemented.");
  }
  public Cookie[] getCookies() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getCookies() not yet implemented.");
  }
  public long getDateHeader(String name) {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getDateHeader() not yet implemented.");
  }
  public String getHeader(String name) {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getHeader() not yet implemented.");
  }
  public Enumeration getHeaders(String name) {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getHeaders() not yet implemented.");
  }
  public Enumeration getHeaderNames() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getHeaderNames() not yet implemented.");
  }
  public int getIntHeader(String name) {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getIntHeader() not yet implemented.");
  }
  public String getMethod() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getMethod() not yet implemented.");
  }
  public String getPathInfo() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getPathInfo() not yet implemented.");
  }
  public String getPathTranslated() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getPathTranslated() not yet implemented.");
  }
  public String getContextPath() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getContextPath() not yet implemented.");
  }
  public String getQueryString() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getQueryString() not yet implemented.");
  }
  public String getRemoteUser() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getRemoteUser() not yet implemented.");
  }
  public boolean isUserInRole(String role) {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method isUserInRole() not yet implemented.");
  }
  public Principal getUserPrincipal() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getUserPrincipal() not yet implemented.");
  }
  public String getRequestedSessionId() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getRequestedSessionId() not yet implemented.");
  }
  public String getRequestURI() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getRequestURI() not yet implemented.");
  }
  public String getServletPath() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getServletPath() not yet implemented.");
  }
  public HttpSession getSession(boolean create) {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getSession() not yet implemented.");
  }
  public HttpSession getSession() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getSession() not yet implemented.");
  }
  public boolean isRequestedSessionIdValid() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method isRequestedSessionIdValid() not yet implemented.");
  }
  public boolean isRequestedSessionIdFromCookie() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method isRequestedSessionIdFromCookie() not yet implemented.");
  }
  public boolean isRequestedSessionIdFromURL() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method isRequestedSessionIdFromURL() not yet implemented.");
  }
  public boolean isRequestedSessionIdFromUrl() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method isRequestedSessionIdFromUrl() not yet implemented.");
  }
  public StringBuffer getRequestURL() {
    /**@todo: Implement this javax.servlet.http.HttpServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getRequestURL() not yet implemented.");
  }
  public void setCharacterEncoding(String encoding) throws java.io.UnsupportedEncodingException {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method setCharacterEncoding() not yet implemented.");
  }
  public Map getParameterMap() {
    /**@todo: Implement this javax.servlet.ServletRequest method*/
    throw new java.lang.UnsupportedOperationException("Method getParameterMap() not yet implemented.");
  }
}
