package org.apache.lucene.analysis;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A filter that replaces accented characters in the ISO Latin 1 character set 
 * (ISO-8859-1) by their unaccented equivalent. The case will not be altered.
 * <p>
 * For instance, '&agrave;' will be replaced by 'a'.
 * <p>
 */
public class ISOLatin1AccentFilter extends TokenFilter {
  public ISOLatin1AccentFilter(TokenStream input) {
    super(input);
  }

  private char[] output = new char[256];
  private int outputPos;

  public final Token next(Token result) throws java.io.IOException {
    result = input.next(result);
    if (result != null) {
      final char[] buffer = result.termBuffer();
      final int length = result.termLength();
      // If no characters actually require rewriting then we
      // just return token as-is:
      for(int i=0;i<length;i++) {
        final char c = buffer[i];
        if (c >= '\u00c0' && c <= '\u0178') {
          removeAccents(buffer, length);
          result.setTermBuffer(output, 0, outputPos);
          break;
        }
      }
      return result;
    } else
      return null;
  }

  /**
   * To replace accented characters in a String by unaccented equivalents.
   */
  public final void removeAccents(char[] input, int length) {

    // Worst-case length required:
    final int maxSizeNeeded = 2*length;

    int size = output.length;
    while (size < maxSizeNeeded)
      size *= 2;

    if (size != output.length)
      output = new char[size];

    outputPos = 0;

    int pos = 0;

    for (int i=0; i<length; i++, pos++) {
      final char c = input[pos];

      // Quick test: if it's not in range then just keep
      // current character
      if (c < '\u00c0')
        output[outputPos++] = c;
      else {
        switch (c) {
        case '\u00C0' : // À
        case '\u00C1' : // Á
        case '\u00C2' : // Â
        case '\u00C3' : // Ã
        case '\u00C4' : // Ä
        case '\u00C5' : // Å
          output[outputPos++] = 'A';
          break;
        case '\u00C6' : // Æ
          output[outputPos++] = 'A';
          output[outputPos++] = 'E';
          break;
        case '\u00C7' : // Ç
          output[outputPos++] = 'C';
          break;
        case '\u00C8' : // È
        case '\u00C9' : // É
        case '\u00CA' : // Ê
        case '\u00CB' : // Ë
          output[outputPos++] = 'E';
          break;
        case '\u00CC' : // Ì
        case '\u00CD' : // Í
        case '\u00CE' : // Î
        case '\u00CF' : // Ï
          output[outputPos++] = 'I';
          break;
        case '\u00D0' : // Ð
          output[outputPos++] = 'D';
          break;
        case '\u00D1' : // Ñ
          output[outputPos++] = 'N';
          break;
        case '\u00D2' : // Ò
        case '\u00D3' : // Ó
        case '\u00D4' : // Ô
        case '\u00D5' : // Õ
        case '\u00D6' : // Ö
        case '\u00D8' : // Ø
          output[outputPos++] = 'O';
          break;
        case '\u0152' : // Œ
          output[outputPos++] = 'O';
          output[outputPos++] = 'E';
          break;
        case '\u00DE' : // Þ
          output[outputPos++] = 'T';
          output[outputPos++] = 'H';
          break;
        case '\u00D9' : // Ù
        case '\u00DA' : // Ú
        case '\u00DB' : // Û
        case '\u00DC' : // Ü
          output[outputPos++] = 'U';
          break;
        case '\u00DD' : // Ý
        case '\u0178' : // Ÿ
          output[outputPos++] = 'Y';
          break;
        case '\u00E0' : // à
        case '\u00E1' : // á
        case '\u00E2' : // â
        case '\u00E3' : // ã
        case '\u00E4' : // ä
        case '\u00E5' : // å
          output[outputPos++] = 'a';
          break;
        case '\u00E6' : // æ
          output[outputPos++] = 'a';
          output[outputPos++] = 'e';
          break;
        case '\u00E7' : // ç
          output[outputPos++] = 'c';
          break;
        case '\u00E8' : // è
        case '\u00E9' : // é
        case '\u00EA' : // ê
        case '\u00EB' : // ë
          output[outputPos++] = 'e';
          break;
        case '\u00EC' : // ì
        case '\u00ED' : // í
        case '\u00EE' : // î
        case '\u00EF' : // ï
          output[outputPos++] = 'i';
          break;
        case '\u00F0' : // ð
          output[outputPos++] = 'd';
          break;
        case '\u00F1' : // ñ
          output[outputPos++] = 'n';
          break;
        case '\u00F2' : // ò
        case '\u00F3' : // ó
        case '\u00F4' : // ô
        case '\u00F5' : // õ
        case '\u00F6' : // ö
        case '\u00F8' : // ø
          output[outputPos++] = 'o';
          break;
        case '\u0153' : // œ
          output[outputPos++] = 'o';
          output[outputPos++] = 'e';
          break;
        case '\u00DF' : // ß
          output[outputPos++] = 's';
          output[outputPos++] = 's';
          break;
        case '\u00FE' : // þ
          output[outputPos++] = 't';
          output[outputPos++] = 'h';
          break;
        case '\u00F9' : // ù
        case '\u00FA' : // ú
        case '\u00FB' : // û
        case '\u00FC' : // ü
          output[outputPos++] = 'u';
          break;
        case '\u00FD' : // ý
        case '\u00FF' : // ÿ
          output[outputPos++] = 'y';
          break;
        default :
          output[outputPos++] = c;
          break;
        }
      }
    }
  }
}
