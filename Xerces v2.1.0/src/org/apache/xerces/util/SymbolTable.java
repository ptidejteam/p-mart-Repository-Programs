/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.util;

/**
 * This class is a symbol table implementation that guarantees that
 * strings used as identifiers are unique references. Multiple calls
 * to <code>addSymbol</code> will always return the same string
 * reference.
 * <p>
 * The symbol table performs the same task as <code>String.intern()</code>
 * with the following differences:
 * <ul>
 *  <li>
 *   A new string object does not need to be created in order to
 *   retrieve a unique reference. Symbols can be added by using
 *   a series of characters in a character array.
 *  </li>
 *  <li>
 *   Users of the symbol table can provide their own symbol hashing
 *   implementation. For example, a simple string hashing algorithm
 *   may fail to produce a balanced set of hashcodes for symbols
 *   that are <em>mostly</em> unique. Strings with similar leading
 *   characters are especially prone to this poor hashing behavior.
 *  </li>
 * </ul>
 *
 * @see SymbolHash
 *
 * @author Andy Clark
 *
 * @version $Id: SymbolTable.java,v 1.1 2006/02/02 01:41:46 vauchers Exp $
 */
public class SymbolTable {

    //
    // Constants
    //

    /** Default table size. */
    protected static final int TABLE_SIZE = 101;

    //
    // Data
    //

    /** Buckets. */
    protected Entry[] fBuckets = null;

    // actual table size
    protected int fTableSize;

    //
    // Constructors
    //

    /** Constructs a symbol table with a default number of buckets. */
    public SymbolTable() {
        this(TABLE_SIZE);
    }

    /** Constructs a symbol table with a specified number of buckets. */
    public SymbolTable(int tableSize) {
        fTableSize = tableSize;
        fBuckets = new Entry[fTableSize];
    }

    //
    // Public methods
    //

    /**
     * Adds the specified symbol to the symbol table and returns a
     * reference to the unique symbol. If the symbol already exists,
     * the previous symbol reference is returned instead, in order
     * guarantee that symbol references remain unique.
     *
     * @param symbol The new symbol.
     */
    public String addSymbol(String symbol) {

        // search for identical symbol
        int bucket = hash(symbol) % fTableSize;
        int length = symbol.length();
        OUTER: for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            if (length == entry.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (symbol.charAt(i) != entry.characters[i]) {
                        continue OUTER;
                    }
                }
                return entry.symbol;
            }
        }

        // create new entry
        Entry entry = new Entry(symbol, fBuckets[bucket]);
        fBuckets[bucket] = entry;
        return entry.symbol;

    } // addSymbol(String):String

    /**
     * Adds the specified symbol to the symbol table and returns a
     * reference to the unique symbol. If the symbol already exists,
     * the previous symbol reference is returned instead, in order
     * guarantee that symbol references remain unique.
     *
     * @param buffer The buffer containing the new symbol.
     * @param offset The offset into the buffer of the new symbol.
     * @param length The length of the new symbol in the buffer.
     */
    public String addSymbol(char[] buffer, int offset, int length) {

        // search for identical symbol
        int bucket = hash(buffer, offset, length) % fTableSize;
        OUTER: for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            if (length == entry.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (buffer[offset + i] != entry.characters[i]) {
                        continue OUTER;
                    }
                }
                return entry.symbol;
            }
        }

        // add new entry
        Entry entry = new Entry(buffer, offset, length, fBuckets[bucket]);
        fBuckets[bucket] = entry;
        return entry.symbol;

    } // addSymbol(char[],int,int):String

    /**
     * Returns a hashcode value for the specified symbol. The value
     * returned by this method must be identical to the value returned
     * by the <code>hash(char[],int,int)</code> method when called
     * with the character array that comprises the symbol string.
     *
     * @param symbol The symbol to hash.
     */
    public int hash(String symbol) {

        int code = 0;
        int length = symbol.length();
        for (int i = 0; i < length; i++) {
            code = code * 37 + symbol.charAt(i);
        }
        return code & 0x7FFFFFF;

    } // hash(String):int

    /**
     * Returns a hashcode value for the specified symbol information.
     * The value returned by this method must be identical to the value
     * returned by the <code>hash(String)</code> method when called
     * with the string object created from the symbol information.
     *
     * @param buffer The character buffer containing the symbol.
     * @param offset The offset into the character buffer of the start
     *               of the symbol.
     * @param length The length of the symbol.
     */
    public int hash(char[] buffer, int offset, int length) {

        int code = 0;
        for (int i = 0; i < length; i++) {
            code = code * 37 + buffer[offset + i];
        }
        return code & 0x7FFFFFF;

    } // hash(char[],int,int):int

    /**
     * Returns true if the symbol table already contains the specified
     * symbol.
     *
     * @param symbol The symbol to look for.
     */
    public boolean containsSymbol(String symbol) {

        // search for identical symbol
        int bucket = hash(symbol) % fTableSize;
        int length = symbol.length();
        OUTER: for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            if (length == entry.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (symbol.charAt(i) != entry.characters[i]) {
                        continue OUTER;
                    }
                }
                return true;
            }
        }

        return false;

    } // containsSymbol(String):boolean

    /**
     * Returns true if the symbol table already contains the specified
     * symbol.
     *
     * @param buffer The buffer containing the symbol to look for.
     * @param offset The offset into the buffer.
     * @param length The length of the symbol in the buffer.
     */
    public boolean containsSymbol(char[] buffer, int offset, int length) {

        // search for identical symbol
        int bucket = hash(buffer, offset, length) % fTableSize;
        OUTER: for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            if (length == entry.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (buffer[offset + i] != entry.characters[i]) {
                        continue OUTER;
                    }
                }
                return true;
            }
        }

        return false;

    } // containsSymbol(char[],int,int):boolean

    //
    // Classes
    //

    /**
     * This class is a symbol table entry. Each entry acts as a node
     * in a linked list.
     */
    protected static final class Entry {

        //
        // Data
        //

        /** Symbol. */
        public String symbol;

        /**
         * Symbol characters. This information is duplicated here for
         * comparison performance.
         */
        public char[] characters;

        /** The next entry. */
        public Entry next;

        //
        // Constructors
        //

        /**
         * Constructs a new entry from the specified symbol and next entry
         * reference.
         */
        public Entry(String symbol, Entry next) {
            this.symbol = symbol.intern();
            characters = new char[symbol.length()];
            symbol.getChars(0, characters.length, characters, 0);
            this.next = next;
        }

        /**
         * Constructs a new entry from the specified symbol information and
         * next entry reference.
         */
        public Entry(char[] ch, int offset, int length, Entry next) {
            characters = new char[length];
            System.arraycopy(ch, offset, characters, 0, length);
            symbol = new String(characters).intern();
            this.next = next;
        }

    } // class Entry

} // class SymbolTable
