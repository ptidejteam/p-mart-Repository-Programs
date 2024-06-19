package net.larsan.email.stream;

import java.io.*;
import java.util.*;

public class ByteArrayPushbackStream extends FilterInputStream {    

    private LinkedList bucketStack;
    
    private byte[] singleBytes;
    
    private int singleByteIndex;
    
    public ByteArrayPushbackStream(InputStream in, int singleByteBufferSize) {
        
        super(in);
        
        bucketStack = new LinkedList();
        
        singleBytes = new byte[singleByteBufferSize];
        singleByteIndex = singleByteBufferSize - 1;

    }
    
    public ByteArrayPushbackStream(InputStream in) {
        this(in, 512);
    }
    
    public int read() throws IOException {
        
        if(singleByteIndex < (singleBytes.length - 1)) return singleBytes[++singleByteIndex];
        else if(bucketStack.size() == 0) return super.read();
        else {
            
            ByteArrayIterator i = (ByteArrayIterator)bucketStack.getFirst();
            
            if(i.hasNext()) return (i.next() & 0xFF);
            else {
                
                bucketStack.removeFirst();
                
                return read();
                
            }
        }
    }
    
    public int read(byte[] buff) throws IOException {
        return read(buff, 0, buff.length);
    }

    public int read(byte[] buff, int off, int len) throws IOException {
        
        if(len == 0) return 0;
        
        if(singleByteIndex < (singleBytes.length - 1)) dropSingleBytes();
            
        if(bucketStack.size() == 0) return super.read(buff, off, len);
        else {
            
            ByteArrayIterator it = (ByteArrayIterator)bucketStack.getFirst();
            
            if(it.remaining() > len) return it.fill(buff, off, len);
            else {
                
                int read = it.fill(buff, off, len);
                
                bucketStack.removeFirst();
                
                if(read == len) return len;
                else if(checkEnd()) return read + read(buff, off + read, len - read);
                else return read;

            }           
        }
    }
    
    public void unread(int i) {
        if(singleByteIndex < (singleBytes.length - 1)) dropSingleBytes();
        singleBytes[singleByteIndex--] = (byte)i;
    }

    public void unread(byte[] bytes) {
        
        if(singleByteIndex < (singleBytes.length - 1)) {
            
            byte[] tmp = new byte[bytes.length + (singleBytes.length - (singleByteIndex + 1))];
            
            System.arraycopy(singleBytes, singleByteIndex + 1, tmp, 0, singleBytes.length - (singleByteIndex + 1));
            System.arraycopy(bytes, 0, tmp, singleBytes.length - (singleByteIndex + 1), bytes.length);
            
            bucketStack.addFirst(new ByteArrayIterator(tmp));
            
            singleByteIndex = singleBytes.length - 1;
            
        } else bucketStack.addFirst(new ByteArrayIterator(bytes));
    }
    
    public void unread(InputStream in) throws IOException {
        
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        
        BufferedOutputStream buff = new BufferedOutputStream(ba);
        
        if(singleByteIndex < (singleBytes.length - 1)) {
            
            buff.write(singleBytes, singleByteIndex + 1, singleBytes.length - (singleByteIndex + 1));
            singleByteIndex = singleBytes.length - 1;
            
        }
        
        byte[] tmp = new byte[256];
        
        int len = 0;
        
        while((len = in.read(tmp)) != -1) {
            
            buff.write(tmp, 0, len);
            
        }
        
        buff.flush();
        
        unread(ba.toByteArray());
        
    }
    
    private boolean checkEnd() {
        try {
            return (bucketStack.size() > 0 || in.available() > 0);
        } catch(IOException e) { return false; }
    }
    
    protected void dropBuffer(OutputStream out) throws IOException {
        dropBufferImpl(out);
    }
    
    protected byte[] dropBuffer() {
        try {
            return dropBufferImpl(null);
        } catch(IOException e) { 
            return new byte[0]; 
        }
    }
    
    private void dropSingleBytes() {
        
        if(singleByteIndex == (singleBytes.length - 1)) return;
        
        byte[] tmp = new byte[singleBytes.length - (singleByteIndex + 1)];
        
        System.arraycopy(singleBytes, singleByteIndex + 1, tmp, 0, singleBytes.length - (singleByteIndex + 1));
        
        bucketStack.addFirst(new ByteArrayIterator(tmp));
        
        singleByteIndex = singleBytes.length - 1;
        
    }

    private byte[] dropBufferImpl(OutputStream out) throws IOException {
        
        dropSingleBytes();
        
        if(bucketStack.size() == 0) return new byte[0];
        
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        BufferedOutputStream buff = new BufferedOutputStream(ba);
        
        for(Iterator i = bucketStack.iterator(); i.hasNext();) {
            
            ByteArrayIterator it = (ByteArrayIterator)i.next();
            
            byte[] tmp = new byte[it.remaining()];
            
            it.fill(tmp);

            buff.write(tmp);
            
        }
        
        buff.flush();
        
        if(out != null) ba.writeTo(out);
        
        return ba.toByteArray();
        
    }
    
    protected static class ByteArrayIterator {
        
        private byte[] bytes;
        
        private int index;
        
        public ByteArrayIterator(byte[] bytes) {
            this.bytes = bytes;
            index = 0;
        }
        
        public boolean hasNext() {
            return (index < bytes.length);
        }
        
        public byte next() {
            if(index >= bytes.length) throw new NoSuchElementException();
            else return bytes[index++];
        }
        
        public int remaining() {
            return (bytes.length - index);
        }
        
        public int fill(byte[] buffer) {
            return fill(buffer, 0, buffer.length);
        }
        
        public int fill(byte[] buffer, int off, int len) {
            
            if(remaining() == 0) return 0;
            
            if(remaining() < len) len = remaining();
           
            System.arraycopy(bytes, index, buffer, off, len);
            
            index += len;
            
            return len;
            
        }
    }
}