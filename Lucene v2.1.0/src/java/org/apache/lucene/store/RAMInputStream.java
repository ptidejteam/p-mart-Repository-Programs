package org.apache.lucene.store;

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
 * A memory-resident {@link IndexInput} implementation.
 *
 * @version $Id: RAMInputStream.java 478014 2006-11-22 02:47:49Z yonik $
 */

class RAMInputStream extends BufferedIndexInput implements Cloneable {
  private RAMFile file;
  private long pointer = 0;
  private long length;

  public RAMInputStream(RAMFile f) {
    file = f;
    length = file.length;
  }

  public void readInternal(byte[] dest, int destOffset, int len) {
    int remainder = len;
    long start = pointer;
    while (remainder != 0) {
      int bufferNumber = (int)(start/BUFFER_SIZE);
      int bufferOffset = (int)(start%BUFFER_SIZE);
      int bytesInBuffer = BUFFER_SIZE - bufferOffset;
      int bytesToCopy = bytesInBuffer >= remainder ? remainder : bytesInBuffer;
      byte[] buffer = (byte[])file.buffers.get(bufferNumber);
      System.arraycopy(buffer, bufferOffset, dest, destOffset, bytesToCopy);
      destOffset += bytesToCopy;
      start += bytesToCopy;
      remainder -= bytesToCopy;
    }
    pointer += len;
  }

  public void close() {
  }

  public void seekInternal(long pos) {
    pointer = pos;
  }

  public long length() {
    return length;
  }

}
