/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
/*
 * $Id: XResources_cy.java,v 1.1 2006/03/09 00:07:40 vauchers Exp $
 */
package org.apache.xml.utils.res;


/**
 * The Cyrillic resource bundle.
 * @xsl.usage internal
 */
public class XResources_cy extends XResourceBundle
{

  /**
   * Get the association list.
   *
   * @return The association list.
   */
  public Object[][] getContents()
  {
    return contents;
  }

 /** The association list. */
  static final Object[][] contents =
  {
    { "ui_language", "cy" }, { "help_language", "cy" }, { "language", "cy" },
    { "alphabet",
      new char[]{ 0x0430, 0x0432, 0x0433, 0x0434, 0x0435, 0x0437, 0x0438,
                  0x0439, 0x04A9, 0x0457, 0x043A, 0x043B, 0x043C, 0x043D,
                  0x046F, 0x043E, 0x043F, 0x0447, 0x0440, 0x0441, 0x0442,
                  0x0443, 0x0444, 0x0445, 0x0470, 0x0460, 0x0446 } },
    { "tradAlphabet",
      new char[]{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                  'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                  'Y', 'Z' } },

    //language orientation
    { "orientation", "LeftToRight" },

    //language numbering 
    //{"numbering", "additive"},
    { "numbering", "multiplicative-additive" },
    { "multiplierOrder", "precedes" },

    // largest numerical value
    //{"MaxNumericalValue", new Integer(10000000000)},
    //These would not be used for EN. Only used for traditional numbering   
    { "numberGroups", new int[]{ 100, 10, 1 } },

    //These only used for mutiplicative-additive numbering
    { "multiplier", new long[]{ 1000 } },
    { "multiplierChar", new char[]{ 0x03D9 } },

    // chinese only??
    { "zero", new char[0] },

    //{"digits", new char[]{'a','b','c','d','e','f','g','h','i'}},
    { "digits",
      new char[]{ 0x0430, 0x0432, 0x0433, 0x0434, 0x0435, 0x0437, 0x0438,
                  0x0439, 0x04A9 } },
    { "tens",
      new char[]{ 0x0457, 0x043A, 0x043B, 0x043C, 0x043D, 0x046F, 0x043E,
                  0x043F, 0x0447 } },
    { "hundreds",
      new char[]{ 0x0440, 0x0441, 0x0442, 0x0443, 0x0444, 0x0445, 0x0470,
                  0x0460, 0x0446 } },
    { "tables", new String[]{ "hundreds", "tens", "digits" } }
  };
}
