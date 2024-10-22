/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xalan" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.xpath.dtm;
 
import org.w3c.dom.*;

import org.xml.sax.HandlerBase;
import org.xml.sax.AttributeList;

/**
 * <code>ChunkedIntArray</code> is an extensible array of blocks of integers.
 * (I'd consider Vector, but it's unable to handle integers except by
 * turning them into Objects.)
 * <P>
 * Making this a separate class means some call-and-return overhead. But
 * doing it all inline tends to be fragile and expensive in coder time,
 * not to mention driving up code size. If you want to inline it, feel free.
 * The Java text suggest that private and Final methods may be inlined, 
 * and one can argue that this beast need not be made subclassable...
 */
final class ChunkedIntArray
  extends org.xml.sax.HandlerBase
{
  final int slotsize=4; // Locked, MUST be power of two in current code
  // Debugging tip: Cranking lowbits down to 4 or so is a good
  // way to pound on the array addressing code.
  static final int lowbits=10; // How many bits address within chunks
  static final int chunkalloc=1<<lowbits;
  static final int lowmask=chunkalloc-1;
  
  ChunksVector chunks=new ChunksVector();
  final int fastArray[] = new int[chunkalloc];
  int lastUsed=0;

  /**
   * Create a new CIA with specified record size. Currently record size MUST
   * be a power of two... and in fact is hardcoded to 4.
   */
  ChunkedIntArray(int slotsize)
  {
    if(this.slotsize<slotsize)
      throw new ArrayIndexOutOfBoundsException("ChunkedIntArray("+slotsize+") not currently supported");
    else if (this.slotsize>slotsize)
      System.out.println("*****WARNING: ChunkedIntArray("+slotsize+") wasting "+(this.slotsize-slotsize)+" words per slot");
    chunks.addElement(fastArray);
  }
  /**
   * Append a 4-integer record to the CIA, starting with record 1. (Since
   * arrays are initialized to all-0, 0 has been reserved as the "unknown"
   * value in DTM.)
   * @return the index at which this record was inserted.
   */
  int appendSlot(int w0, int w1, int w2, int w3)
  {
    /*
    try
    {
      int newoffset = (lastUsed+1)*slotsize;
      fastArray[newoffset] = w0;
      fastArray[newoffset+1] = w1;
      fastArray[newoffset+2] = w2;
      fastArray[newoffset+3] = w3;
      return ++lastUsed;
    }
    catch(ArrayIndexOutOfBoundsException aioobe)
    */
    {
      final int slotsize=4;
      int newoffset = (lastUsed+1)*slotsize;
      int chunkpos = newoffset >> lowbits;
      int slotpos = (newoffset & lowmask);

      // Grow if needed
      if (chunkpos > chunks.size() - 1)
        chunks.addElement(new int[chunkalloc]);
      int[] chunk = chunks.elementAt(chunkpos);
      chunk[slotpos] = w0;
      chunk[slotpos+1] = w1;
      chunk[slotpos+2] = w2;
      chunk[slotpos+3] = w3;

      return ++lastUsed;
    }
  }
  /**
   * Retrieve an integer from the CIA by record number and column within
   * the record, both 0-based (though position 0 is reserved for special
   * purposes).
   * @param position int Record number
   * @param slotpos int Column number
   */
  int readEntry(int position, int offset) throws ArrayIndexOutOfBoundsException
  {
    /*
    try
    {
      return fastArray[(position*slotsize)+offset];
    }
    catch(ArrayIndexOutOfBoundsException aioobe)
    */
    {
      // System.out.println("Using slow read (1)");
      if (offset>=slotsize)
        throw new ArrayIndexOutOfBoundsException("Offset bigger than slot");
      position*=slotsize;
      int chunkpos = position >> lowbits;
      int slotpos = position & lowmask;
      int[] chunk = chunks.elementAt(chunkpos);
      return chunk[slotpos + offset];
    }
  }
  
  int specialFind(int startPos, int position)
  {
	  // We have to look all the way up the ancestor chain
	  // to make sure we don't have an ancestor.
	  int ancestor = startPos;
	  while(ancestor > 0)
	  {
		ancestor*=slotsize;
		int chunkpos = ancestor >> lowbits;
		int slotpos = ancestor & lowmask;
		int[] chunk = chunks.elementAt(chunkpos);
							
		ancestor = chunk[slotpos + 1];
		  
		if(ancestor == position)
			 break;
	  }

	  if (ancestor <= 0) 
	  {
		  return position;
	  }
	  return -1;
  }
  
  /**
   * @return int index of highest-numbered record currently in use
   */
  int slotsUsed()
  {
    return lastUsed;
  }

  /** Disard the highest-numbered record. This is used in the string-buffer
   CIA; when only a single characters() chunk has been recieved, its index
   is moved into the Text node rather than being referenced by indirection
   into the text accumulator.
   */
  void discardLast()
  {
    --lastUsed;
  }

  /**
   * Overwrite the integer found at a specific record and column.
   * Used to back-patch existing records, most often changing their
   * "next sibling" reference from 0 (unknown) to something meaningful
   * @param position int Record number
   * @param offset int Column number
   * @param value int New contents
   */
  void writeEntry(int position, int offset, int value) throws ArrayIndexOutOfBoundsException
  {
    /*
    try
    {
      fastArray[( position*slotsize)+offset] = value;
    }
    catch(ArrayIndexOutOfBoundsException aioobe)
    */
    {
      if (offset >= slotsize)
        throw new ArrayIndexOutOfBoundsException("Offset bigger than slot");
      position*=slotsize;
      int chunkpos = position >> lowbits;
      int slotpos = position & lowmask;
      int[] chunk = chunks.elementAt(chunkpos);
      chunk[slotpos + offset] = value; // ATOMIC!
    }
  }

  /**
   * Overwrite an entire (4-integer) record at the specified index.
   * Mostly used to create record 0, the Document node.
   * @param position integer Record number
   * @param w0 int 
   * @param w1 int
   * @param w2 int
   * @param w3 int
   */
  void writeSlot(int position, int w0, int w1, int w2, int w3)
  {
    int chunkpos = position >> lowbits;
    int slotpos = (position & lowmask) * slotsize;

    // Grow if needed
    if (chunkpos > chunks.size() - 1)
      chunks.addElement(new int[chunkalloc]);
    int[] chunk = chunks.elementAt(chunkpos);
    chunk[slotpos] = w0;
    chunk[slotpos + 1] = w1;
    chunk[slotpos + 2] = w2;
    chunk[slotpos + 3] = w3;
  }

  /**
   * Retrieve the contents of a record into a user-supplied buffer array.
   * Used to reduce addressing overhead when code will access several
   * columns of the record.
   * @param position int Record number
   * @param buffer int[] Integer array provided by user, must be large enough
   * to hold a complete record.
   */
  void readSlot(int position, int[] buffer)
  {
    /*
    try
    {
      System.arraycopy(fastArray, position*slotsize, buffer, 0, slotsize);
    }
    catch(ArrayIndexOutOfBoundsException aioobe)
    */
    {
      // System.out.println("Using slow read (2): "+position);
      position *= slotsize;
      int chunkpos = position >> lowbits;
      int slotpos = (position & lowmask);

      // Grow if needed
      if (chunkpos > chunks.size() - 1)
        chunks.addElement(new int[chunkalloc]);
      int[] chunk = chunks.elementAt(chunkpos);
      System.arraycopy(chunk,slotpos,buffer,0,slotsize);
    }
  }

  class ChunksVector
  {
    final int BLOCKSIZE = 64;
    int[] m_map[] = new int[BLOCKSIZE][];
    int m_mapSize = BLOCKSIZE;
    int pos = 0;
    
    ChunksVector()
    {
    }
    
    final int size()
    {
      return pos;
    }
    
    void addElement(int[] value)
    {
      if(pos >= m_mapSize)
      {
        int orgMapSize = m_mapSize;
        while(pos >= m_mapSize)
          m_mapSize+=BLOCKSIZE;
        int[] newMap[] = new int[m_mapSize][];
        System.arraycopy(m_map, 0, newMap, 0, orgMapSize);
        m_map = newMap;
      }
      // For now, just do a simple append.  A sorted insert only 
      // makes sense if we're doing an binary search or some such.
      m_map[pos] = value;
      pos++;
    }
    
    final int[] elementAt(int pos)
    {
      return m_map[pos];
    }
  }
}