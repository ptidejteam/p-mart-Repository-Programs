package org.apache.lucene;

/**
 * Copyright 2004 The Apache Software Foundation
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

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.InputStream;
import org.apache.lucene.store.OutputStream;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.util.Date;
import java.util.Random;

class StoreTest {
  public static void main(String[] args) {
    try {
      test(1000, true);
    } catch (Exception e) {
      System.out.println(" caught a " + e.getClass() +
			 "\n with message: " + e.getMessage());
    }
  }

  public static void test(int count, boolean ram)
       throws Exception {
    Random gen = new Random(1251971);
    int i;
    
    Date veryStart = new Date();
    Date start = new Date();

    Directory store;
    if (ram)
      store = new RAMDirectory();
    else
      store = FSDirectory.getDirectory("test.store", true);

    final int LENGTH_MASK = 0xFFF;

    for (i = 0; i < count; i++) {
      String name = i + ".dat";
      int length = gen.nextInt() & LENGTH_MASK;
      byte b = (byte)(gen.nextInt() & 0x7F);
      //System.out.println("filling " + name + " with " + length + " of " + b);

      OutputStream file = store.createFile(name);

      for (int j = 0; j < length; j++)
	file.writeByte(b);
      
      file.close();
    }

    store.close();

    Date end = new Date();

    System.out.print(end.getTime() - start.getTime());
    System.out.println(" total milliseconds to create");

    gen = new Random(1251971);
    start = new Date();

    if (!ram)
      store = FSDirectory.getDirectory("test.store", false);

    for (i = 0; i < count; i++) {
      String name = i + ".dat";
      int length = gen.nextInt() & LENGTH_MASK;
      byte b = (byte)(gen.nextInt() & 0x7F);
      //System.out.println("reading " + name + " with " + length + " of " + b);

      InputStream file = store.openFile(name);

      if (file.length() != length)
	throw new Exception("length incorrect");

      for (int j = 0; j < length; j++)
	if (file.readByte() != b)
	  throw new Exception("contents incorrect");

      file.close();
    }

    end = new Date();

    System.out.print(end.getTime() - start.getTime());
    System.out.println(" total milliseconds to read");

    gen = new Random(1251971);
    start = new Date();

    for (i = 0; i < count; i++) {
      String name = i + ".dat";
      //System.out.println("deleting " + name);
      store.deleteFile(name);
    }

    end = new Date();

    System.out.print(end.getTime() - start.getTime());
    System.out.println(" total milliseconds to delete");

    System.out.print(end.getTime() - veryStart.getTime());
    System.out.println(" total milliseconds");

    store.close();
  }
}
