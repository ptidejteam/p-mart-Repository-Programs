/*
 * The contents of this file are subject to the Netscape Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/NPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1997-1999 Netscape Communications Corporation. All
 * Rights Reserved.
 *
 * Contributor(s):
 * Norris Boyd
 * Roger Lawrence
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the GNU Public License (the "GPL"), in which case the
 * provisions of the GPL are applicable instead of those above.
 * If you wish to allow use of your version of this file only
 * under the terms of the GPL and not to allow others to use your
 * version of this file under the NPL, indicate your decision by
 * deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL.  If you do not delete
 * the provisions above, a recipient may use your version of this
 * file under either the NPL or the GPL.
 */



package org.mozilla.javascript.optimizer;

class DataFlowBitSet {

    private int itsBits[];
    private int itsSize;

    DataFlowBitSet(int size)
    {
        itsSize = size;
        itsBits = new int[(size + 31) >> 5];
    }

    int size()
    {
        return itsSize;
    }

    void set(int n)
    {
        if (!(0 <= n && n < itsSize)) badIndex(n);
        itsBits[n >> 5] |= 1 << (n & 31);
    }

    boolean test(int n)
    {
        if (!(0 <= n && n < itsSize)) badIndex(n);
        return ((itsBits[n >> 5] & (1 << (n & 31))) != 0);
    }

    void not()
    {
        int bitsLength = itsBits.length;
        for (int i = 0; i < bitsLength; i++)
            itsBits[i] = ~itsBits[i];
    }

    void clear(int n)
    {
        if (!(0 <= n && n < itsSize)) badIndex(n);
        itsBits[n >> 5] &= ~(1 << (n & 31));
    }

    void clear()
    {
        int bitsLength = itsBits.length;
        for (int i = 0; i < bitsLength; i++)
            itsBits[i] = 0;
    }

    void or(DataFlowBitSet b)
    {
        int bitsLength = itsBits.length;
        for (int i = 0; i < bitsLength; i++)
            itsBits[i] |= b.itsBits[i];
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("DataFlowBitSet, size = ");
        sb.append(itsSize);
        sb.append('\n');
        int bitsLength = itsBits.length;
        for (int i = 0; i < bitsLength; i++) {
            sb.append(Integer.toHexString(itsBits[i]));
            sb.append(' ');
        }
        return sb.toString();
    }

    boolean df(DataFlowBitSet in, DataFlowBitSet gen, DataFlowBitSet notKill)
    {
        int bitsLength = itsBits.length;
        boolean changed = false;
        for (int i = 0; i < bitsLength; i++) {
            int oldBits = itsBits[i];
            itsBits[i] = (in.itsBits[i] | gen.itsBits[i]) & notKill.itsBits[i];
            changed |= (oldBits != itsBits[i]);
        }
        return changed;
    }

    boolean df2(DataFlowBitSet in, DataFlowBitSet gen, DataFlowBitSet notKill)
    {
        int bitsLength = itsBits.length;
        boolean changed = false;
        for (int i = 0; i < bitsLength; i++) {
            int oldBits = itsBits[i];
            itsBits[i] = (in.itsBits[i] & notKill.itsBits[i]) | gen.itsBits[i];
            changed |= (oldBits != itsBits[i]);
        }
        return changed;
    }

    private void badIndex(int n)
    {
        throw new RuntimeException("DataFlowBitSet bad index " + n);
    }
}
