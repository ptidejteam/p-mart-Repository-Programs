/**
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.nutch.ndfs;

import org.apache.nutch.io.*;
import org.apache.nutch.util.*;

import java.io.*;
import java.util.*;

/**************************************************
 * DatanodeInfo tracks stats on a given node
 *
 * @author Mike Cafarella
 **************************************************/
public class DatanodeInfo implements Writable, Comparable {
    UTF8 name;
    long capacity, remaining, lastUpdate, lastObsoleteCheck;
    volatile TreeSet blocks;

    /**
     */
    public DatanodeInfo() {
        this(new UTF8(), 0, 0);
    }

    public DatanodeInfo(UTF8 name) {
        this.name = name;
        int colon = name.toString().indexOf(":");
        this.blocks = new TreeSet();
        this.lastObsoleteCheck = System.currentTimeMillis();
        updateHeartbeat(0, 0);        
    }

    /**
     */
    public DatanodeInfo(UTF8 name, long capacity, long remaining) {
        this.name = name;
        this.blocks = new TreeSet();
        this.lastObsoleteCheck = System.currentTimeMillis();
        updateHeartbeat(capacity, remaining);
    }

    /**
     */
    public void updateBlocks(Block newBlocks[]) {
        blocks.clear();
        for (int i = 0; i < newBlocks.length; i++) {
            blocks.add(newBlocks[i]);
        }
    }

    /**
     */
    public void addBlock(Block b) {
        blocks.add(b);
    }

    /**
     */
    public void updateHeartbeat(long capacity, long remaining) {
        this.capacity = capacity;
        this.remaining = remaining;
        this.lastUpdate = System.currentTimeMillis();
    }
    public UTF8 getName() {
        return name;
    }
    public UTF8 getHost() {
        String nameStr = name.toString();
        int colon = nameStr.indexOf(":");
        if (colon < 0) {
            return name;
        } else {
            return new UTF8(nameStr.substring(0, colon));
        }
    }
    public String toString() {
        return name.toString();
    }
    public Block[] getBlocks() {
        return (Block[]) blocks.toArray(new Block[blocks.size()]);
    }
    public Iterator getBlockIterator() {
        return blocks.iterator();
    }
    public long getCapacity() {
        return capacity;
    }
    public long getRemaining() {
        return remaining;
    }
    public long lastUpdate() {
        return lastUpdate;
    }
    public void updateObsoleteCheck() {
        this.lastObsoleteCheck = System.currentTimeMillis();
    }
    public long lastObsoleteCheck() {
        return lastObsoleteCheck;
    }

    /////////////////////////////////////////////////
    // Comparable
    /////////////////////////////////////////////////
    public int compareTo(Object o) {
        DatanodeInfo d = (DatanodeInfo) o;
        return name.compareTo(d.getName());
    }

    /////////////////////////////////////////////////
    // Writable
    /////////////////////////////////////////////////
    /**
     */
    public void write(DataOutput out) throws IOException {
        name.write(out);
        out.writeLong(capacity);
        out.writeLong(remaining);
        out.writeLong(lastUpdate);

        /**
        out.writeInt(blocks.length);
        for (int i = 0; i < blocks.length; i++) {
            blocks[i].write(out);
        }
        **/
    }

    /**
     */
    public void readFields(DataInput in) throws IOException {
        this.name = new UTF8();
        this.name.readFields(in);
        this.capacity = in.readLong();
        this.remaining = in.readLong();
        this.lastUpdate = in.readLong();

        /**
        int numBlocks = in.readInt();
        this.blocks = new Block[numBlocks];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new Block();
            blocks[i].readFields(in);
        }
        **/
    }
}

