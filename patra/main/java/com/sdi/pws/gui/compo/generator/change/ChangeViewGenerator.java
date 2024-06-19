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

package com.sdi.pws.gui.compo.generator.change;

import com.sdi.pws.generator.Generator;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * Wrapper around a password generator that knows how to be an event throwing bean, it
 * supports the JavaBean event protocol. So listeners can be added.
 */
public class ChangeViewGenerator
implements Generator
{
    private Generator generator;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private boolean changed = false;

    public ChangeViewGenerator(Generator aGenerator)
    {
        generator = aGenerator;
    }

    // Change support.

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(listener);
    }

    // Delegation.

    public String generate()
    {
        return generator.generate();
    }

    public int getQualityCategory()
    {
        return generator.getQualityCategory();
    }

    public int getLength()
    {
        return generator.getLength();
    }

    public void setLength(int aLength)
    {
        final int lOldLength = getLength();
        final int lNewLength = aLength;
        generator.setLength(aLength);
        support.firePropertyChange("length", lOldLength, lNewLength);
        changed = true;
    }

    public boolean isReadable()
    {
        return generator.isReadable();
    }

    public void setReadable(boolean readable)
    {
        final boolean lOldReadable = isReadable();
        final boolean lNewReadable = readable;
        generator.setReadable(readable);
        support.firePropertyChange("readable", lOldReadable, lNewReadable);
        changed = true;
    }

    public boolean isMixedCase()
    {
        return generator.isMixedCase();
    }

    public void setMixedCase(boolean mixedCase)
    {
        final boolean lOldMixedCase = isMixedCase();
        final boolean lNewMixedCase = mixedCase;
        generator.setMixedCase(mixedCase);
        support.firePropertyChange("mixedCase", lOldMixedCase, lNewMixedCase);
        changed = true;
    }

    public boolean isNumbersIncluded()
    {
        return generator.isNumbersIncluded();
    }

    public void setNumbersIncluded(boolean numbersIncluded)
    {
        final boolean lOldNumbersIncluded = isNumbersIncluded();
        final boolean lNewNumbersIncluded = numbersIncluded;
        generator.setNumbersIncluded(numbersIncluded);
        support.firePropertyChange("numbersIncluded", lOldNumbersIncluded, lNewNumbersIncluded);
        changed = true;
    }

    public boolean isPunctuationIncluded()
    {
        return generator.isPunctuationIncluded();
    }

    public void setPunctuationIncluded(boolean symbolsIncluded)
    {
        final boolean lOldPunctuactionIncluded = isPunctuationIncluded();
        final boolean lNewPunctuationIncluded = symbolsIncluded;
        generator.setPunctuationIncluded(symbolsIncluded);
        support.firePropertyChange("punctuationIncluded", lOldPunctuactionIncluded, lNewPunctuationIncluded);
        changed = true;
    }

    public byte[] getEntropy()
    {
        return generator.getEntropy();
    }

    public void setEntropy(byte[] entropy)
    {
        final Object lOldEntropy = getEntropy();
        final Object lNewEntropy = entropy;
        generator.setEntropy(entropy);
        support.firePropertyChange("entropy", lOldEntropy, lNewEntropy);
        changed = true;
    }

    public boolean isChanged()
    {
        return changed;
    }
}
