/*
 * Party.java
 * Copyright 2001 (C) Bryan McRoberts
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

public class PStringTokenizer
{
  String _forThisString = "";
  String _delimiter = "";
  String _ignoreBetweenThis = "";
  String _andThat = "";

  public String nextToken()
  {
    String aString = "";
    int ignores = 0;
    if (_forThisString.lastIndexOf(_delimiter) == -1)
    {
      aString = _forThisString;
      _forThisString = "";
    }
    else
    {
      int i = 0;
      for (i = 0; i < _forThisString.length(); i++)
      {
        if (_forThisString.substring(i).startsWith(_delimiter) && ignores == 0)
          break;
        if (_forThisString.substring(i).startsWith(_ignoreBetweenThis) && ignores == 0)
          ignores = 1;
        else if (_forThisString.substring(i).startsWith(_andThat))
          ignores = 0;
        aString = aString.concat(_forThisString.substring(i, i + 1));
      }
      _forThisString = _forThisString.substring(i + 1);
    }
    return aString;
  }

  public boolean hasMoreTokens()
  {
    return (_forThisString.length() > 0);
  }

  public PStringTokenizer(String forThisString, String delimiter, String ignoreBetweenThis, String andThat)
  {
    _forThisString = forThisString;
    _delimiter = delimiter;
    _ignoreBetweenThis = ignoreBetweenThis;
    _andThat = andThat;
  }
}
