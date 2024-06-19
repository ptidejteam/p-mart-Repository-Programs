/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006-2009  Bruno Ranschaert, S.D.I.-Consulting BVBA.

For more information contact: nospam@sdi-consulting.com
Visit our website: http://www.sdi-consulting.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.sdi.pws.generator;

/**
 * A generator can generate random strings based on a number of user modifiable preferences.
 */
public interface Generator
{
    String generate();

    int getQualityCategory();

    int getLength();
    void setLength(int length);

    boolean isReadable();
    void setReadable(boolean readable);

    boolean isMixedCase();
    void setMixedCase(boolean mixedCase);

    boolean isNumbersIncluded();
    void setNumbersIncluded(boolean numbersIncluded);

    boolean isPunctuationIncluded();
    void setPunctuationIncluded(boolean symbolsIncluded);

    byte[] getEntropy();
    void setEntropy(byte[] entropy);
}
