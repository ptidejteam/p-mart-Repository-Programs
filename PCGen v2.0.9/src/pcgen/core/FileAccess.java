/*
 * FileAccess.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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

package pcgen.core;

import java.util.*;
import java.util.Iterator;
import java.io.*;
import javax.swing.JOptionPane;

/**
 * <code>FileAccess</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class FileAccess
{
  static void write(Writer output, String aString)
  {
    try
    {
      output.write(aString);
    }
    catch (Exception exception)
    {
      JOptionPane.showMessageDialog(null, exception.getMessage());
    }
  }

  static void newLine(BufferedWriter output)
  {
    try {
      output.newLine();
    }
    catch (Exception exception)
    {
      JOptionPane.showMessageDialog(null, exception.getMessage());
    }
  }

  static String readLine(BufferedReader input)
  {
    try {
      return input.readLine();
    }
    catch (Exception exception)
    {
      JOptionPane.showMessageDialog(null, exception.getMessage());
    }
    return null;
  }
  
  static String readWholeLine(BufferedReader input)
  {
    try {
      char[] c = new char[5000];
      for(int i=0;i<5000;i++)
      {
        char d=(char)input.read();
        c[i]=d;
        if (d=='\r' || d=='\n')
          break;
      }
      String aString = new String(c);
      return aString;
    }
    catch (Exception exception)
    {
      JOptionPane.showMessageDialog(null, exception.getMessage());
    }
    return null;
  }

  static int read(BufferedReader input)
  {
    try
    {
      return input.read();
    }
    catch (Exception exception)
    {
      JOptionPane.showMessageDialog(null, exception.getMessage());
    }
    return 0;
  }
}
