/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * An instance of <code>SymbolTable</code> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of <i>buckets</i> in the SymbolTable, and the
 * <i>initial capacity</i> is simply the capacity at the time the SymbolTable
 * is created.  Note that the SymbolTable is <i>open</i>: in the case of a "hash
 * collision", a single bucket stores multiple entries, which must be searched
 * sequentially.  The <i>load factor</i> is a measure of how full the SymbolTable
 * is allowed to get before its capacity is automatically increased.
 * When the number of entries in the SymbolTable exceeds the product of the load
 * factor and the current capacity, the capacity is increased by calling the
 * <code>rehash</code> method.<p>
 *
 * Generally, the default load factor (.75) offers a good tradeoff between
 * time and space costs.  Higher values decrease the space overhead but
 * increase the time cost to look up an entry (which is reflected in most
 * <tt>SymbolTable</tt> operations, including <tt>addSymbol</tt> and <tt>containsSymbol</tt>).<p>
 *
 * The initial capacity controls a tradeoff between wasted space and the
 * need for <code>rehash</code> operations, which are time-consuming.
 * No <code>rehash</code> operations will <i>ever</i> occur if the initial
 * capacity is greater than the maximum number of entries the
 * <tt>Hashtable</tt> will contain divided by its load factor.  However,
 * setting the initial capacity too high can waste space.<p>
 *
 * If many entries are to be made into a <code>SymbolTable</code>, 
 * creating it with a sufficiently large capacity may allow the 
 * entries to be inserted more efficiently than letting it perform 
 * automatic rehashing as needed to grow the table. <p>

 * @see SymbolHash
 *
 * @author Andy Clark
 * @author John Kim, IBM
 *
 * @version $Id: SymbolTable.java 447241 2006-09-18 05:12:57Z mrglavas $
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

    /** actual table size **/
    protected int fTableSize;

    /** The total number of entries in the hash table. */
    protected transient int fCount;

    /** The table is rehashed when its size exceeds this threshold.  (The
     * value of this field is (int)(capacity * loadFactor).) */
    protected int fThreshold;
							 
    /** The load factor for the SymbolTable. */
    protected float fLoadFactor;

    //
    // Constructors
    //
    
    /**
     * Constructs a new, empty SymbolTable with the specified initial 
     * capacity and the specified load factor.
     *
     * @param      initialCapacity   the initial capacity of the SymbolTable.
     * @param      loadFactor        the load factor of the SymbolTable.
     * @throws     IllegalArgumentException  if the initial capacity is less
     *             than zero, or if the load factor is nonpositive.
     */
    public SymbolTable(int initialCapacity, float loadFactor) {
        
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal Load: " + loadFactor);
        }
        
        if (initialCapacity == 0) {
            initialCapacity = 1;
        }
        
        fLoadFactor = loadFactor;
        fTableSize = initialCapacity;
        fBuckets = new Entry[fTableSize];
        fThreshold = (int)(fTableSize * loadFactor);
        fCount = 0;
    }

    /**
     * Constructs a new, empty SymbolTable with the specified initial capacity
     * and default load factor, which is <tt>0.75</tt>.
     *
     * @param     initialCapacity   the initial capacity of the hashtable.
     * @throws    IllegalArgumentException if the initial capacity is less
     *            than zero.
     */
    public SymbolTable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }
    
    /**
     * Constructs a new, empty SymbolTable with a default initial capacity (101)
     * and load factor, which is <tt>0.75</tt>. 
     */
    public SymbolTable() {
        this(TABLE_SIZE, 0.75f);
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
        for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            if (entry.symbol.equals(symbol)) {
                return entry.symbol;
            }
        }
        
        if (fCount >= fThreshold) {
            // Rehash the table if the threshold is exceeded
            rehash();
            bucket = hash(symbol) % fTableSize;
        } 
        
        // create new entry
        Entry entry = new Entry(symbol, fBuckets[bucket]);
        fBuckets[bucket] = entry;
        ++fCount;
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
        
        if (fCount >= fThreshold) {
            // Rehash the table if the threshold is exceeded
            rehash();
            bucket = hash(buffer, offset, length) % fTableSize;
        } 
        
        // add new entry
        Entry entry = new Entry(buffer, offset, length, fBuckets[bucket]);
        fBuckets[bucket] = entry;
        ++fCount;
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
        return symbol.hashCode() & 0x7FFFFFF;
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
        for (int i = 0; i < length; ++i) {
            code = code * 31 + buffer[offset + i];
        }
        return code & 0x7FFFFFF;

    } // hash(char[],int,int):int

    /**
     * Increases the capacity of and internally reorganizes this 
     * SymbolTable, in order to accommodate and access its entries more 
     * efficiently.  This method is called automatically when the 
     * number of keys in the SymbolTable exceeds this hashtable's capacity 
     * and load factor. 
     */
    protected void rehash() {

        int oldCapacity = fBuckets.length;
        Entry[] oldTable = fBuckets;

        int newCapacity = oldCapacity * 2 + 1;
        Entry[] newTable = new Entry[newCapacity];

        fThreshold = (int)(newCapacity * fLoadFactor);
        fBuckets = newTable;
        fTableSize = fBuckets.length;

        for (int i = oldCapacity ; i-- > 0 ;) {
            for (Entry old = oldTable[i] ; old != null ; ) {
                Entry e = old;
                old = old.next;

                int index = hash(e.characters, 0, e.characters.length) % newCapacity;
                e.next = newTable[index];
                newTable[index] = e;
            }
        }
    }

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
