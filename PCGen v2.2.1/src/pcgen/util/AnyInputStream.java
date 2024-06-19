/*
 * AnyInputStream.java
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.util;

import java.net.URL;
import java.util.Collection;

/**
 * Open streams by hook or by crook.
 *
 * @author B. K. Oxley (binkley) <binkley@bigfoot.com>
 * @version $Revision: 1.1 $
 * @see java.net.JarURLConnection
 * @see java.net.URL
 */
public class AnyInputStream
{
  /**
   * The magic incantation for whence we were loaded.
   */
  private static String location = null;

  /**
   * For making <code>Location</code>s.  Keep a class static rather
   * than creating a fresh copy of ourselves each time someone uses
   * @see #createLocation .
   */
  private static final AnyInputStream THIS = new AnyInputStream();

  /**
   * Where to search for jar files?
   */
  public static final String SEARCH_JAR_PATHS_PROPERTY = "pcgen.jar.paths";

  /**
   * Where to search for jar files.  This MUST be a package variable
   * so that @see pcgen.util.Boot can set it up after the properties
   * files is loaded.
   */
  static Collection searchJarPaths = null;

  /**
   * To support resolution of chicken-egg problem with Boot calling
   * calling Boot.
   */
  static void handCraftSearchJarPaths()
  {
    java.util.Collection rawSearchJarPaths = Boot.getPropertyStrings
      (SEARCH_JAR_PATHS_PROPERTY);
    searchJarPaths = (java.util.Collection)new java.util.Vector();

    java.util.Iterator i = rawSearchJarPaths.iterator();

    while (i.hasNext())
    {
      String prefix = (String)i.next();

      // If not absolute, then relative to the loading jar.
      if (prefix.equals("."))
        if (location.endsWith(".jar"))
          searchJarPaths.add(location + "!/");

        else
        // Ignore . if we were not loaded from a jar.
          continue;

      else if (location.endsWith(".jar"))
      // Replace original jar with ourselves.
        searchJarPaths.add
          (location.substring
          (0, location.lastIndexOf('/') + 1)
          + prefix + "!/");

      else
        searchJarPaths.add(location + prefix + "!/");
    }
  }

  static
  {
    try
    {
      location = Class.forName("pcgen.util.AnyInputStream")
        .getProtectionDomain().getCodeSource()
        .getLocation().toString();
    }

    catch (Exception e)
    {
      // impossible
      System.err.println("IMPOSSIBLE");
      e.printStackTrace();
      System.exit(99);
    }

    if (searchJarPaths == null)
      handCraftSearchJarPaths();
  }

  /**
   * Open a stream by hook or by crook.
   *
   * @param path The path to open
   *
   * @return The opened InputStream
   *
   * @exception java.io.IOException Cannot open path
   *
   * @see java.net.URL#openStream()
   */
  public static java.io.InputStream createInputStream(String path)
    throws java.io.IOException
  {
    return createLocation(path).inputStream;
  }

  /**
   * Open a stream by hook or by crook.  Search
   * through the default list of jar files from @see
   * #SEARCH_JAR_PATHS_PROPERTY.
   *
   * @param path The path to open as a file or jar entry
   *
   * @return The opened <code>InputStream</code>
   *
   * @exception java.io.IOException Cannot open path
   *
   * @see java.net.JarURLConnection
   * @see java.net.URL#openStream()
   */
  public static java.io.InputStream createInputStreamFromJar(String path)
    throws java.io.IOException
  {
    return createLocationFromJar(path).inputStream;
  }

  /**
   * Open a stream by hook or by crook.  If <code>null</code>
   * search through the default list of jar files from @see
   * #SEARCH_JAR_PATHS_PROPERTY.
   *
   * @param jar The jar file to look in
   * @param path The path to open as a file or jar entry
   *
   * @return The opened <code>InputStream</code>
   *
   * @exception java.io.IOException Cannot open path
   *
   * @see java.net.JarURLConnection
   * @see java.net.URL#openStream()
   */
  public static java.io.InputStream createInputStream(String jar,
    String path)
    throws java.io.IOException
  {
    return createLocation(jar, path).inputStream;
  }

  /**
   * Open a stream by hook or by crook for Booting.
   *
   * @param jar The jar file to look in; no searching properties
   *            since the property file loading code calls this
   * @param path The path to open as a file or jar entry
   *
   * @return The opened InputStream
   *
   * @exception java.io.IOException Cannot open path
   *
   * @see java.net.URL#openStream()
   */
  static java.io.InputStream createInputStream(String jar, String path,
    boolean booting)
    throws java.io.IOException
  {
    return createLocation(jar, path, booting).inputStream;
  }

  /**
   * For those interested in knowing where the stream came from.
   */
  public class Location
  {
    /**
     * <code>AnyInputStream</code> found the stream here.
     */
    public final java.net.URL url;

    /**
     * The stream
     */
    public final java.io.InputStream inputStream;

    /**
     * Construct a <code>Location</code>
     *
     * @param u The <code>URL</code>
     * @param is The <code>InputStream</code>
     */
    Location(java.net.URL u, java.io.InputStream is)
    {
      url = u;
      inputStream = is;
    }
  }

  /**
   * Open a location by hook or by crook.
   *
   * @param path The path to open
   *
   * @return The opened location
   *
   * @exception java.io.IOException Cannot open path
   *
   * @see java.net.URL#openStream()
   */
  public static Location createLocation(String path)
    throws java.io.IOException
  {
    try
    {
      java.net.URL url = new java.net.URL(new URL("file:"), path);

      return THIS.
      new Location(url, url.openStream());
    }

    catch (java.io.IOException ioe)
    {
      java.net.URL url = new java.net.URL(path);

      return THIS.
      new Location(url, url.openStream());
    }
  }

  /**
   * Open a location by hook or by crook.  Search
   * through the default list of jar files from @see
   * #SEARCH_JAR_PATHS_PROPERTY.
   *
   * @param path The path to open as a file or jar entry
   *
   * @return The opened location
   *
   * @exception java.io.IOException Cannot open path
   *
   * @see java.net.JarURLConnection
   * @see java.net.URL#openStream()
   */
  public static Location createLocationFromJar(String path)
    throws java.io.IOException
  {
    java.util.Iterator i = searchJarPaths.iterator();

    while (i.hasNext())
    {
      String next = (String)i.next();

      try
      {
        java.net.URL url = new java.net.URL
          (new URL("jar:file:" + next), path);

        return THIS.
        new Location(url, url.openStream());
      }

      catch (java.io.IOException ioe)
      {
        try
        {
          java.net.URL url = new java.net.URL
            (new URL("jar:" + next), path);

          return THIS.
          new Location
            (url, url.openStream());
        }

        catch (java.io.IOException ioe2)
        {
          continue;
        }
      }
    }

    return createLocation(path);
  }

  /**
   * Open a location by hook or by crook.  If <code>null</code>
   * search through the default list of jar files from @see
   * #SEARCH_JAR_PATHS_PROPERTY.
   *
   * @param jar The jar file to look in
   * @param path The path to open as a file or jar entry
   *
   * @return The opened location
   *
   * @exception java.io.IOException Cannot open path
   *
   * @see java.net.URL#openStream()
   * @see java.net.JarURLConnection
   */
  public static Location createLocation(String jar, String path)
    throws java.io.IOException
  {
    if (jar == null)
    {
      java.util.Iterator i = searchJarPaths.iterator();

      while (i.hasNext())
      {
        String next = (String)i.next();

        try
        {
          return createLocation(next, path);
        }

        catch (java.io.IOException ioe)
        {
          continue;
        }
      }

      return createLocation(path);
    }

    try
    {
      java.net.URL url = new java.net.URL
        (new URL("jar:file:" + jar + "!/"), path);

      return THIS.
      new Location(url, url.openStream());
    }

    catch (java.io.IOException ioe)
    {
      try
      {
        java.net.URL url = new java.net.URL
          (new URL("jar:" + jar + "!/"), path);

        return THIS.
        new Location(url, url.openStream());
      }

      catch (java.io.IOException ioe2)
      {
        return createLocation(path);
      }
    }
  }

  /**
   * Open a location by hook or by crook for Booting.  No searching
   * properties since the property file loading code calls this.
   *
   * @param jar The jar file to look in
   * @param path The path to open as a file or jar entry
   *
   * @return The opened location
   *
   * @exception java.io.IOException Cannot open path
   *
   * @see java.net.URL#openStream()
   * @see java.net.JarURLConnection
   */
  static Location createLocation(String jar, String path,
    boolean booting)
    throws java.io.IOException
  {
    if (jar == null)
      return createLocation(path);

    try
    {
      java.net.URL url = new java.net.URL
        (new URL("jar:file:" + jar + "!/"), path);

      return THIS.
      new Location(url, url.openStream());
    }

    catch (java.io.IOException ioe)
    {
      try
      {
        java.net.URL url = new java.net.URL
          (new URL("jar:" + jar + "!/"), path);

        return THIS.
        new Location(url, url.openStream());
      }

      catch (java.io.IOException ioe2)
      {
        return createLocation(path);
      }
    }
  }
}
