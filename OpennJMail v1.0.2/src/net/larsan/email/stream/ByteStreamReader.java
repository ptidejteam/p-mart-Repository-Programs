package net.larsan.email.stream;

import java.io.*;
import sun.io.*;

public class ByteStreamReader extends Reader {

    public static final int DEFAULT_BUFFER_SIZE = 4096;

    private ByteArrayPushbackStream in;
    
    private ByteToCharConverter converter;
    
    private byte[] byteBuffer;
    
    private int bufferIndex, bufferLength;
    
    private boolean streamMode, isReversed;
    
    
    private ByteStreamReader(ByteArrayPushbackStream in, ByteToCharConverter converter, int bufferSize) {
        
        super(in);
        
        this.in = in;
        
        if(bufferSize > 0) byteBuffer = new byte[bufferSize];
        else byteBuffer = new byte[DEFAULT_BUFFER_SIZE];
        
        this.converter = converter;
        
        bufferIndex = 0;
        bufferLength = 0;
        
        streamMode = false;
        isReversed = true;
        
    }
        
    public ByteStreamReader(ByteArrayPushbackStream in, int bufferSize) {
        this(in, ByteToCharConverter.getDefault(), bufferSize);
    }
    
    public ByteStreamReader(ByteArrayPushbackStream in) {
        this(in, ByteToCharConverter.getDefault(), DEFAULT_BUFFER_SIZE);
    }
    
    public ByteStreamReader(ByteArrayPushbackStream in, String encoding, int bufferSize) throws UnsupportedEncodingException {
        this(in, ByteToCharConverter.getConverter(encoding), bufferSize);
    }
    
    public ByteStreamReader(ByteArrayPushbackStream in, String encoding) throws UnsupportedEncodingException {
        this(in, ByteToCharConverter.getConverter(encoding), DEFAULT_BUFFER_SIZE);
    }
    
    public String getEncoding() {
        return converter.getCharacterEncoding();
    }
    
    public InputStream checkoutStream() throws IOException {
        if(!isReversed) reverseBuffers();
        streamMode = true;
        return in;
    }
    
    public void checkinStream(InputStream in) throws IOException {
        if(in != this.in) throw new IllegalStateException("Returned stream does not belong to reader");
        if(!streamMode) throw new IllegalStateException("Mismatched stream mode use, check in stream before using reader");
        streamMode = false;
    }
    
    public synchronized void close() throws IOException {
        
        if(in == null) return;
        
        try {
            
            in.close();
            
        } finally {

            converter = null;
            byteBuffer = null;
            
            in = null;
            
        }
    }
    
    public int read() throws IOException {
        char[] tmp = new char[1];
        if(read(tmp, 0, 1) == -1) return -1;
        else return tmp[0];
    }
    
    public int read(char[] buff) throws IOException {
        return read(buff, 0, buff.length);
    }
    
    /*public synchronized int read(char cbuf[], int off, int len) throws IOException {
        
        checkStream();
        checkState();
        
            if(len == 0) return 0;

            return fill(cbuf, off, len);

    }*/

    
    public synchronized int read(char[] buff, int off, int len) throws IOException { 
    
        checkState();
        
        checkStream();
        
        if(len == 0) return 0;
        else {
            
            int read = 0;
            
            if(bufferIndex < bufferLength) read = convertTo(buff, off, len);
            
            while(read < len) {

                if(bufferLength != -1) {
            
                    if((read > 0) && !checkEnd()) return read;
                    bufferLength = in.read(byteBuffer, 0, byteBuffer.length);
                    //System.out.println(bufferLength);
                    isReversed = false;
            
                }
    
                if(bufferLength == -1) {
    
                    bufferLength = 0;
                    read += flushTo(buff, off + read, len - read);
            
                    if(read == 0) return -1;
                    else break;
    
                } else {
    
                    bufferIndex = 0;
                    read += convertTo(buff, off + read, len - read);
                    
                    //if(read == len) break;
    
                }
            } 
                    
            return read;
            
        }
    }
    
    private int flushTo(char[] buff, int off, int len) throws IOException { 
    
        try {
            
            return converter.flush(buff, off, off + len);
            
        } catch(ConversionBufferFullException e) { return len; }
    }
    
    private int convertTo(char[] buff, int off, int len) throws IOException { 
    
        if(bufferIndex < bufferLength) {
            
            try {

                int read = converter.convert(byteBuffer, bufferIndex, bufferLength, buff, off, off + len);
                
                bufferIndex = bufferLength;

                if(converter.nextByteIndex() != bufferIndex) throw new InternalError("Mismatched buffer sizes");;
            
                return read;
            
            } catch(ConversionBufferFullException e) {

                bufferIndex = converter.nextByteIndex();
                return len;
            
            }
            
        } else return 0;
    } 
    
    private synchronized void reverseBuffers() throws IOException { 
 
        checkStream();
    
        if(isReversed) return;
    
    
        CharArrayWriter wr = new CharArrayWriter(byteBuffer.length);
        
        while(true) {
            
            char[] tmp = new char[byteBuffer.length];
            
            int len = 0;
        
            try { 
            
                len = converter.flush(tmp, 0, tmp.length);
                
                wr.write(tmp, 0, len);
                
                break;
                
            } catch(ConversionBufferFullException e) {
                
                wr.write(tmp, 0, len);
            
            }
        }
        
        if(bufferIndex < bufferLength) {
            
            byte[] tmp = new byte[bufferLength - bufferIndex];
            
            System.arraycopy(byteBuffer, bufferIndex, tmp, 0, bufferLength - bufferIndex);
            
            in.unread(tmp);
            
            //System.out.println(tmp.length);

            bufferIndex = 0;
            bufferLength = 0;
            
            converter.reset();
            
        }
        
        in.unread(new String(wr.toCharArray()).getBytes(getEncoding()));
        
        isReversed = true;
    
    }
    
    private void checkState() {
        if(streamMode) throw new IllegalStateException("Mismatched stream mode use, check in stream before using reader");
    }
    
    private void checkStream() throws IOException {
        if(in == null || converter == null || byteBuffer == null) throw new IOException("Closed stream");
    }
    
    private boolean checkEnd() throws IOException {
        
        int tmp = in.read();
        
        try {
                
            if(tmp == -1) return false;
            else return true;
        
        } finally { 
        
            if(tmp != -1) in.unread(tmp); 
            
        } 
    }
}