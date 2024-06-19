/*
*  Code: MessageParser.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2001 Lars J. Nilsson
*
*       This program is free software; you can redistribute it and/or
*       modify it under the terms of the GNU General Public License
*       as published by the Free Software Foundation; either version 2
*       of the License, or (at your option) any later version.
*
*       This program is distributed in the hope that it will be useful,
*       but WITHOUT ANY WARRANTY; without even the implied warranty of
*       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*       GNU General Public License for more details.
*
*       You should have received a copy of the GNU General Public License
*       along with this program; if not, write to the Free Software
*       Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
*/

package net.larsan.email.util;

import java.io.*;
import java.util.*;

import net.larsan.email.*;
import net.larsan.email.stream.*;
import net.larsan.email.encoder.*;

/**
* This class provides a parser which attempts to create en email message from an 
* input stream. The parser works in a read ahead manner which makes it fast
* but also requires a lot of the environment when it comes to protocoll exactness and
* other implementations. In specific there are a couple requirements that must be met for
* the parser to work correctly and those will be discussed below.<p>
*
* The parser conforms to the email message and transport protocolls as defined in 
* RFC 821, 822, 1521, 1522 and 1725 with a couple of exceptions. The parser does
* not fully support:<p>
*
* 1) Header field comments.<br>
* 2) Text between MIME body parts.<br>
* 3) MIME content type "message".<p>
*
* A header field comment will be treated as plain text by the parser, all text between
* body parts - such as the frequently used "this is a multipart mime message" warning -
* will be discarded and the mime content type "message" with all it's subtypes will be treated
* as a plain text content type.<p>
*
* Implementations can set a "default character set" with the <code>setDefaultCharset</code>
* method. This character set will be used by the parser if it encounters textual content
* without a valid content type character set value. By default the parser will use the 
* platform specific character set so the <code>setDefaultCharset</code> method should only
* be called if the implementation knows the incomming message will be of another character set.<p>
*
* The parser supports the following content encodings: "Q", "B", "Base64", "Quoted-Printable" and the
* textual content flags "7bit", "8bit" and "binary". If an implementation needs customized
* encodings it can provided parser with Encoder and Decoder objects for the encoding through
* a CoderTable to the contructor.<p>
*
* <b>Environmental requirements</b>: The parser is made as flexible as possible while still
* working direct on an input stream. However: there are some situations described below
* where the parser might have troubles.<p>
*
* The parser will leave the stream when it encounters one of the following "stop tokens":<p>
*
* 1) End of stream reached.<br>
* 2) End message boundary detected.<br>
* 3) Delimiting non-transparent dot detected.<p>
*
* The parser will leave the stream directly after the last character belonging to the message. 
* Thus the next byte in the stream depends on how the message ended. If the message ended
* by an end of stream byte the next byte would be -1, and if the parser stopped for a
* delimiting dot that dot itself would be the next byte on the stream. But, if the parser stops for
* an end boundary it is up to the environment to decide what to expect next in the stream.<p>
*
* The parser works on an pushback input stream. When preparing the environment for that stream
* there are two thing to consider:<p>
*
* 1) Never unread an end of stream byte (-1) to the stream, the internal pushback
* mechanism will convert it to a positive int (255) and the parser will enter an 
* infinite loop.<br><br>
*
* 2) Suply a sufficient buffer upon creation of the stream, 256 bytes or more are 
* recomended.<p>
*
* Implementors should be very careful with delimiting the message of an incoming
* stream to avoid the points above.
*
* @author Lars J. Nilsson
* @version 1.1 01/04/2001
*/


public class MessageParser {
    
    // original buffer for errors
    private StringBuffer originalMessage;
    
    // main header, also for errors
    private Header mainHeader;
    
    // default charset
    private String defaultCharset;
    
    // last read byte 
    private int lastByte;
    
    // encoders / decoders
    private CoderTable coderTable;
    
    
    /**
    * Create a new message parser capable of parsing the default "Q", "B", "Base64",
    * "8Bit" and "Quoted-Printable " encodings.
    */
    
    public MessageParser() {
        this(null);
    }

    /**
    * Create a new message parser using the specified encoders / decoders.
    */

    public MessageParser(CoderTable table) {
        
        this.defaultCharset = "";
        
        if(table == null) {
            
            coderTable = new CoderTable();
            coderTable.addCoderPair(new CoderPair("Q", new Q_Encoder(defaultCharset), new Q_Decoder(defaultCharset)));
            coderTable.addCoderPair(new CoderPair("B", new B_Encoder(defaultCharset), new B_Decoder(defaultCharset)));
            coderTable.addCoderPair(new CoderPair("Base64", new Base64Encoder(defaultCharset), new Base64Decoder(defaultCharset)));
            coderTable.addCoderPair(new CoderPair("Quoted-Printable", new QuotedPrintableEncoder(defaultCharset), new QuotedPrintableDecoder(defaultCharset)));
            coderTable.addCoderPair(new CoderPair("8Bit", new Bit8Encoder(defaultCharset), new Bit8Decoder(defaultCharset)));

        } else {
            
            coderTable = table;
        
        }
        
        reset();
    }


    /**
    * Set the default character set. This character set will be used wherever the parser
    * encounter textual content which it cannot determine the character set on.<p>
    *
    * Default setting for the parser charset is platform dependend.
    */

    public void setDefaultCharset(String charset) {
        defaultCharset = charset;
    }


    /**
    * Get the current default character set.
    */

    public String getDefaultCharset() {
        return defaultCharset;
    }


	/**
	* Attempt to parse a message from the input stream. This method will return a 
	* Message instance if parsing succeded, null if there was no content in the
	* stream or no message could be parsed or throw an InvalidFormatException or an
	* IOException if needed. The parser will read from the input stream and declare
	* the message finished when one of the following "stop tokens" are encountered:<p>
	* 
	* 1) End of stream is reached.<br>
	* 2) End of multipart message is reached.<br>
	* 3) Delimiter dot encountered.<p>
	*
	* This parser tries to be as lenient as possible regarding the line breaks,
	* but it should be stressed that anything other than the canonical form of line
	* break - ie: a carriage return followed by a line feed ("\r\n") - is highly
	* discouraged.<p>
	*
	* The parser will leave the stream at the position of the "stop token", so in
	* a stream reading from a storage where the messages are separated by delimiting
	* dots the parser will return and the next character in the stream will be
	* a '.'. <p>
	*
	* When preparing the pushback stream care should be taken about the following
	* steps:<p>
	*
	* 1) Never unread an end of stream byte (-1) to the stream, the internal pushback
	* mechanism will convert it to a positive int (255) and the parser will enter an 
	* infinite loop.<br><br>
	*
	* 2) Suply a sufficient buffer upon creation of the stream, 256 bytes are recomended.<p>
	*
	* Step number 1 above is can occur if an storage implementatation parses messages from a
	* loop that only looks for delimiters after the messages - sush a loop <b>must</b>
	* check for the end of the storage stream too.
	*/
	
	public Message parseMessage(PushbackInputStream in) throws InvalidFormatException, IOException {
	   
	    reset();
	
	    /*
	    * This is how we'll do this: We'll attempt to parse the incomming
	    * input stream with as little read ahead as possible. Since the message
	    * is linear in it's composition we can achieve that by examining the
	    * part we already have read to be able to anticipate the correct next part.
	    *
	    * Since no body multipart is finished before every of it's content bodies
	    * heave been fully read we can store the bodies in a List instance
	    * where the last element in the list - kept track of by the "currentDepth"
	    * counter - is the part to be parsed at the moment. And when the part at
	    * the "currentDepth" position is finished we'll simply add it to the underlying
	    * multipart body (if one exist) and subtract 1 from "currentDepth".
	    *
	    * There are three distinct message parts / parser modes we want
	    * to deal with, and those are:
	    *
	    * 1) HEADER - We expect a header containing of zero or more header fields.
	    *     This Header is a standalone object which determines what to do next, so
	    *     we'll read it and then try to create a Body object which corresponds to
	    *     the header content type. After this the state of the parser can be either
	    *     NEXT or BODY depending on if we expect a boundary or not.
	    *
	    * 2) BODY - This is where we'll want to read a actual body content. This part
	    *     will also finish of the current body, since it is not a multipart but a
	    *     textual / binary content body. If there is a multipart body at current
	    *     depth - 1 the next state will be NEXT, otherwise we're done.
	    *
	    * 3) NEXT - Here we anticipate a message boundary. If we read an end boundary
	    *     we can finish of the body at the current depth - then, if the're is another
	    *     multipart body underneath the current one we'll expect another boundary
	    *     so the state is NEXT otherwise we're done. Should the boundary not be
	    *     an end boundary we'll anticipate a header again so state will be HEADER.
	    *
	    * We need a ArrayList to keep the bodies in, a "currentDepth" counter, a 
	    * variable keeping track if the parser state and a reference to the current 
	    * body header.
	    *
	    * We'll keep a reference to the first header and it's eventual boundary 
	    * seperately, so if an I/O error occurs or there's an invalid format in the
	    * message we can still read the stream to an end and return it as a text
	    * message.
	    */
	
	    ArrayList bodies = new ArrayList();
	    String currentLine = "";
	    int currentDepth = -1;
	    int state = HEADER;
	    
	    String firstBoundary = ""; // for errors only - see "readToEnd()"
	    
	    EmailHeader tmp_header = null;
	    
	    try {
	       
	       // we'll break this loop when we're finished
	    
	       while(true) {   
	        
      	        if(state == HEADER) { 
      	        
      	            // first we'll just check the stream - it might be that the parser is set
      	            // up in a loop parsing text from a storage in which case there could be
      	            // a -1 byte first that we must check for
      	            
      	            int firstByte = in.read();
      	            
      	            if(firstByte == -1) break; 
      	            else in.unread(firstByte);
      	           
      	            tmp_header = new EmailHeader();
      	            
      	            // the "readHeaderField method will return null when it does not
      	            // find any more header fields
      	            
      	            HeaderField field = readHeaderField(in, true);
      	            
      	            if(field != null) {
      	             
      	                tmp_header.add(field);
      	            
      	                while((field = readHeaderField(in, false)) != null) tmp_header.add(field);
      	                
      	            }

      	            // if this is the first header in a message we'll call it
      	            // a main header and keep it's boundary for error use
      	            
      	            if(mainHeader == null) {
      	                mainHeader = tmp_header;
      	                firstBoundary = getBoundary(tmp_header);
      	            }    
      	            
      	            // examine the boundary and parser state - should there
      	            // be a boundary after this header ?
      	            
      	            if(bodies.size() > 0 && getBoundary(tmp_header).length() > 0) state = NEXT;
      	            else if(bodies.size() == 0 && firstBoundary.length() > 0) state = NEXT;
      	            else state = BODY;
      	            
      	            // place a new body in the Vector and add depth
      	            
      	            bodies.add(getNewBody(tmp_header));
      	            currentDepth++;
      
      	        } else if(state == BODY) {
      	           
      	            // get the boundary we anticipate in the read method - if the current
      	            // body does not have a boundary we'll see in there's a multipart
      	            // body "below" it that does
      	            
      	            String boundary = getBoundary(((Body)bodies.get(currentDepth)).getHeader());
      	            
      	            if(boundary.length() == 0 && bodies.size() > 1) boundary = getBoundary(((Body)bodies.get(currentDepth - 1)).getHeader());
      	            
      	            // now read the body and set it in list
      	            
      	            bodies.set(currentDepth, readBody((Body)bodies.get(currentDepth), boundary, in));
      	         
      	            // if current depth > 0 we've got a multipart body at current depth - 1
      	         
      	            if(currentDepth > 0) {
      	               
      	               if(bodies.get(currentDepth - 1) instanceof MixedBody) {
      	                   
      	                   if(bodies.get(currentDepth - 1) instanceof AlternativeBody) ((MixedBody)bodies.get(currentDepth - 1)).addHighOrderBodyPart((Body)bodies.get(currentDepth));
      	                   else ((MixedBody)bodies.get(currentDepth - 1)).addLowOrderBodyPart((Body)bodies.get(currentDepth));
      	                   
      	               } else if(bodies.get(currentDepth - 1) instanceof ParallelBody) {
      
      	                   ((ParallelBody)bodies.get(currentDepth - 1)).addBodyPart((Body)bodies.get(currentDepth));
      	                   
      	               } 
      	               
      	               // this body is now a part of the body at currentDepth - 1, so
      	               // delete it and subtract current depth
      	           
      	               bodies.remove(currentDepth);
      	               currentDepth--;
      	               state = NEXT;
      	               
      	           } else break; // finished
      	            
      	        } else if(state == NEXT) {
      	           
      	            // get the boundary to anticipate - if the current body does not 
      	            // have a boundary we'll see in there's a multipart body "below" 
      	            //it that does
      	           
      	            String boundary = getBoundary(((Body)bodies.get(currentDepth)).getHeader());
      	            
      	            if(boundary.length() == 0 && bodies.size() > 0) boundary = getBoundary(((Body)bodies.get(currentDepth - 1)).getHeader());
      	            
      	            // read the next boundary and see if it is an end boundary or not
      	            
      	            String current_boundary = readBoundary(in, boundary);
      	           
      	            if(current_boundary.equals("--" + boundary + "--")) {
      	               
      	                // if current depth > 0 we've got a multipart body at current depth -1
      	               
          	            if(currentDepth > 0) {
          	               
          	               if(bodies.get(currentDepth - 1) instanceof MixedBody) {
          	                   
          	                   if(bodies.get(currentDepth - 1) instanceof AlternativeBody) ((MixedBody)bodies.get(currentDepth - 1)).addHighOrderBodyPart((Body)bodies.get(currentDepth));
      	                       else ((MixedBody)bodies.get(currentDepth - 1)).addLowOrderBodyPart((Body)bodies.get(currentDepth));
      	                   
          	               } else if(bodies.get(currentDepth - 1) instanceof ParallelBody) {
          
          	                   ((ParallelBody)bodies.get(currentDepth - 1)).addBodyPart((Body)bodies.get(currentDepth));
          	                   
          	               } 
          	               
          	               // this body is now a part of the body at currentDepth - 1, so
      	                   // delete it and subtract current depth
          	           
          	               bodies.remove(currentDepth);
          	               currentDepth--;
          	               state = NEXT;
          	               
          	           } else break; // finished
      	                
      	            } else state = HEADER; // no end boundary, so anticipate a header again
      	        }
	        }
	     
	        // well - if we don't have any bodies or the current depth is - 1 there
	        // was nothing on this stream or there've been a serious error, so we'll
	        // throw an exception
        	
            if(bodies.size() == 0 || currentDepth == -1) return null;
            else return new DefaultMessage((Body)bodies.get(currentDepth), defaultCharset);

	    } catch(IOException e) {
	       
	         System.err.println(e.toString());
	       
	         if(!(e instanceof InvalidFormatException)) throw e;
      	     
      	     readToEnd(in, firstBoundary); 
      	     
      	     if(originalMessage.length() == 0) return null;
      	     
      	     if(mainHeader != null) {
      	         
      	         TextBody body = new TextBody(originalMessage.toString());
      	         body.setHeader(mainHeader);
      	         
      	         return new DefaultMessage(body, defaultCharset);
      	         
      	     } else return new DefaultMessage(new TextBody(originalMessage.toString()), defaultCharset);
      	}
	}


    // read body content - the parameters denotes th body containing the current 
    // header and the boundary to expect (if any) as well as the input stream
	
	private Body readBody(Body body, String boundary, PushbackInputStream in) throws IOException {
	     
	     /*
	     * This is what to do: We'll read into a byte array untill one
	     * of the following happends: 
	     * 
	     * 1) end of stream
	     * 2) boundary encountered
	     * 3) dot delimiter encountered
	     *
	     * We'll have to have a line buffer as well as a last character value
	     * to check for boundaries and to at least try to recover from "bare line
	     * breaks". However, if we encounter a "dot delimiter" we'll have to asume
	     * that the line break after it is canonical because our unread capability
	     * ends after that dot. If the line break isn't canonical, well...
	     * isn't that just too bad for the user...
	     *
	     * When the body is read we'll unread the boundary (if there is one) both
	     * from the input stream and the byte array.
	     *
	     * Then check if we need to decode the body.
	     *
	     * Then read the content into the appropriate body.
	     *
	     * That's all. Easy, huh?
	     */
	     
	     ByteArrayOutputStream ba = new ByteArrayOutputStream();
	    
	     StringBuffer line = new StringBuffer();
	     int last_char = 0;
         int tmp = 0;
         
         while(true) {
            
             tmp = read(in);

             if(tmp == -1) {
                
                 // end of stream - if we have a boundary we'll unread it and break loop
                
                 if(line.toString().startsWith("--" + boundary) && boundary.length() > 0) {
                     
                     char[] ch = line.toString().toCharArray();
                     
                     for(int i = 1; i <= ch.length; i++) unread(in, ch[ch.length - i]);
                     
                     break;
                     
                 } else break;
                 
             } else if(tmp == '.' && (last_char == '\r' || last_char == '\n')) {
                
                 // this might be the end of it...
                
                 tmp = read(in);
                 
                 if(tmp == '\r' || tmp == '\n' || tmp == -1) {
                     
                     if(tmp != -1) unread(in, tmp);
                     
                     unread(in, '.');
                     
                     break;
                     
                 } else {
                    
                     line.append('.').append((char)tmp);
                     
                     ba.write('.');
                     ba.write(tmp);
                    
                 }
              
             } else if(tmp == '\r') {
                
                 // new line - check for boundary: If we have one we'll unread it
                 // and break the loop, otherwise we'll reset the line buffer
                
                 if(line.toString().startsWith("--" + boundary) && boundary.length() > 0) {
                     
                     char[] ch = line.toString().toCharArray();
                     
                     for(int i = 1; i <= ch.length; i++) unread(in, ch[ch.length - i]);
                     
                     break;
                     
                 } else {
                    
                     ba.write(new byte[] { (byte)'\r', (byte)'\n' });
                     line = new StringBuffer();
                 
                 }
              
             } else if(tmp == '\n' && last_char != '\r') {
                
                 // we really shouldn't find outselves here, but if someone have not
                 // understood that emails must use a canonical line break 
                 // we'll be kind and try to parse his mail as well
                
                 if(line.toString().startsWith("--" + boundary) && boundary.length() > 0) {
                     
                     char[] ch = line.toString().toCharArray();
                     
                     for(int i = 1; i <= ch.length; i++) unread(in, ch[ch.length - i]);
                     
                     break;
                     
                 } else {
                    
                     ba.write(new byte[] { (byte)'\r', (byte)'\n' });
                     line = new StringBuffer();
                 
                 }
                 
             } else if(tmp != '\n') {
                ba.write(tmp);
                line.append((char)tmp);
             }
          
             last_char = tmp;
             
         }
      
      
         // now delete the boundary from byte array if needed
      
         int b_length = 0;
         
         if(boundary.length() > 0) b_length = line.length() + 2; // add 2 for last line break
      
         byte[] old_body = ba.toByteArray();
      
         byte[] new_body = new byte[old_body.length - b_length];
         
         for(int i = 0; i < new_body.length; i++) new_body[i] = old_body[i];
         
 
         // get encoding and create an input stream connected to the
         // byte array and decode
         
         ba = new ByteArrayOutputStream();
         
         Decoder dec = getDecoder(body.getHeader());
         byte[] last_body = dec.decode(new MailInputStream(new ByteArrayInputStream(new_body)));
         

	     // now set the bodies
	           
         if(body instanceof BinaryBody) ((BinaryBody)body).setBody(last_body);
         else if(body instanceof TextBody || body instanceof EncodedTextBody) {
             
             // get character set and convert
                          
             String charset = getCharset(body.getHeader());
             if(Decoder.validateCharset(charset)) dec.setCharset(charset);
             
             StringBuffer content = new StringBuffer();
             content.append(dec.toCharArray(last_body));
             
             if(body instanceof TextBody) ((TextBody)body).setBody(content.toString());
             else if(body instanceof EncodedTextBody) ((EncodedTextBody)body).setBody(content.toString());
             
         }       
        
         return body;
	}



    // query the header and create a new body which matches it
	
	private Body getNewBody(Header header) {
	    
	    // get content type and charachter set
	    
	    int type = getContentType(header);
	    String charset = getCharset(header);
	    
	    // we'll need an encoder too
	    
	    Encoder encoder = getEncoder(header);
	    
	    if(Encoder.validateCharset(charset)) encoder.setCharset(charset);
	 
	    // now we're ready to create the body, so switch the type
	 
	    Body body = null;
	    
	    switch(type) {
	       
            case MIXED : 
            
                body = new MixedBody(getBoundary(header));
                break;
                
            case PARALLEL : 
                
                body = new ParallelBody(getBoundary(header));
                break;
                
            case ALTERNATIVE : 
            
                body = new AlternativeBody(getBoundary(header));
                break;
                
            case DIGEST : 
            
                body = new DigestBody(getBoundary(header));
                break;
                
            /*
            
            At the moment we don't parse message content types
            
            case MESSAGE : 
            
                body = new MessageBody(new EmailMessage(new TextBody()));
                break;
                
            */
                
            case JPG_IMAGE : 
            
                body = new ImageBody(ImageBody.JPEG_IMAGE, new byte[0]);
                break;
                
            case GIF_IMAGE : 
            
                body = new ImageBody(ImageBody.GIF_IMAGE, new byte[0]);
                break;
                
            case HTML : 
            
                body = new HTMLBody("", charset, encoder);
                break;
                
            case TEXT : 
            
                body = new EncodedTextBody("", charset, encoder);
                break;
                
            case TEXT_ATT :
            
                body = new TextAttachment("", getAttName(header), charset, encoder);
                break;
                
            case BINARY : 
            
                body = new BinaryBody(new byte[0], encoder);
                break;
                
            case BINARY_ATT :
            
                body = new BinaryAttachment(new byte[0], getAttName(header), encoder);
                break;

            default : body = new TextBody("", charset);

	    }
	 
	    // done - insert header and return
	 
	    body.setHeader(header);
	    
	    return body;
    }



    // read boundary from input stream
	
	private String readBoundary(PushbackInputStream in, String boundary) throws IOException {

        // We'll read lines and compare them to the boundary and return
        // the read boundary; so setup an answer string and a line buffer

	    String answer = "";
	    
	    StringBuffer line = new StringBuffer();
	    
	    int tmp = 0;
	    int last = 0;
	    
	    while(true) { 
	    
	        tmp = read(in);
	        
	        if(tmp == -1) {
	           
	             // if we have an end boundary the stream end is ok
	             // otherwise the stream end is not expected and we'll have
	             // to throw an exception
	            
                 if(line.toString().equals("--" + boundary + "--")  && boundary.length() > 0) {
                     
                     answer = line.toString();
                     
                     break;
                     
                 } else throw new InvalidFormatException("Premature end of stream detected: -1");
                 
            } else if(tmp == '.' && (last == '\r' || last == '\n')) {
                
                 // this might be the end...
                
                 tmp = read(in);
                 
                 if(tmp == '\r' || tmp == '\n') {
                    
                     unread(in, '.');
                     unread(in, tmp);
                     
                     throw new InvalidFormatException("Premature end of stream detected: \'.\'");
                 
                 } else line.append('.').append((char)tmp);
            
            } else if(tmp == '\r') {
                
                // so, here's an end of a line
                
                if(line.toString().startsWith("--" + boundary) && boundary.length() > 0) {

                     // be kind: some fool may be trying this parser without
                     // using canonical line breaks
                     
                     tmp = read(in);
                     
                     if(tmp != '\n') unread(in, tmp);
                  
                     // return answer
                     
                     answer = line.toString();
                     
                     break;
                     
                } else line = new StringBuffer();
                 
            } else if(tmp =='\n' && last != '\r') {
                
                 // we shouldn't be here but... ahh, well - it's so simple
                 // so I might as well permit it
                 
                 if(line.toString().startsWith("--" + boundary) && boundary.length() > 0) {
                     
                     answer = line.toString();
                     
                     break;
                     
                 } else line = new StringBuffer();
                 
            } else if(tmp != '\n') line.append((char)tmp);
            
            last = tmp;
	    
	    }
	
	    return answer;
	    
	}



    // read header field from stream

    private HeaderField readHeaderField(PushbackInputStream in, boolean isFirst) throws IOException {
        
        StringBuffer name = new StringBuffer();
        StringBuffer value = new StringBuffer();
        
        StringBuffer charset = new StringBuffer();

        int tmp = read(in);
        
        // try to read header name, ie: read until a ':' sign or a
        // line break - ignore emtpy lines
        
        while(tmp != ':') {
            
            if(tmp == -1) break;
            
            if(tmp == '\r' || tmp == '\n') {
                if(isFirst) {
                    if((name.toString().trim()).length() > 0) break;
                } else break;
            } else name.append((char)tmp);
            
            tmp = read(in);
        }
    
        // if we broke out of the loop above for any other reason than a ':'
        // character we should unread and return null
    
        if(tmp != ':' || name.length() == 0) {
            
            if(tmp == '\r') {
                
                tmp = read(in);
                
                if(tmp != '\n') unread(in, tmp);
            
            } else unread(in, tmp);
            
            if(name.length() > 0) {
                
                char[] ch = name.toString().toCharArray();
                for(int i = 1; i <= ch.length; i++) unread(in, ch[ch.length - i]);
            }
            
            return null;
        
        } else {
            
            // ok, we got a ':' charcter, now the question is if it is a
            // header field name we recognise: if it is not, return null
                
            if(!isHeaderField(name.toString())) {
                
                unread(in, tmp);
                
                if(name.length() > 0) {
                
                    char[] ch = name.toString().toCharArray();
                    for(int i = 1; i <= ch.length; i++) unread(in, ch[ch.length - i]);
                
                }
            
                return null;
                
            } else {
                
                // we recognised the header name but if the character that broke the
                // first loop was any form of a new line we'll have to unread it
                // to make sure it get's parsed correctly below
                
                if(tmp == '\r' || tmp == '\n') unread(in, tmp);
            }
        }
    
        // continue read header field value
        
        boolean finished = false;
        String encoding = "";
        
        while(!finished) {
            
            tmp = read(in);
            
            // minus one == stream ended
            
            if(tmp == -1) finished = true;
            
            // end of line - now read ahead a bit, if the new line is followed
            // by spaces it might be a folded header field, but if the spaces are
            // followed by a new line again we'll count it as a complete empty line
            // preceeding a body or a boundary
            
            else if(tmp == '\r' || tmp == '\n') {
                
                if(tmp == '\r') {
                    
                    tmp = read(in);
                
                    if(tmp == '\n') tmp = read(in);
                    
                } else tmp = read(in);
                
                
                if(tmp == ' ' || tmp == '\t') {
                    
                    // read spaces
                    
                    while(tmp == ' ' || tmp == '\t') tmp = read(in);
                
                    if(tmp != '\r' && tmp != '\n') { // folding
                    
                        unread(in, tmp);
                        unread(in, ' '); 
                    
                    } else {
                    
                        // not folded, read one more to get the complete line break,
                        // should the line break don't be valid, be kind and forgiving
                        
                        tmp = read(in);
                        
                        if(tmp != '\n') unread(in, tmp);
                        
                        // we're done
                        
                        finished = true;
                    }
                    
                } else {
                    
                    unread(in, tmp);
                    finished = true;
                }
            
            // a '=' character might be the start of an encoded partion of the field, so...
            
            } else if(tmp == '=') { 
                
                // create a buffer holding the question mark positions, a position
                // counter and a general counter
                
                int[] q_marks = new int[4];
                int pos = 0;
                int counter = 1;
                
                // create a byte array buffer to push back later if this wasn't a 
                // encoded field
                
                ByteArrayOutputStream ba = new ByteArrayOutputStream();
                
                ba.write(tmp);
                
                // the next byte should be a '?' for encodings
                
                tmp = read(in);
                
                if(tmp != '?') value.append("=").append((char)tmp); // no encoding
                else {
                    
                    // now loop forward - add counter, read byte, and if byte is
                    // a question mark, remember it's position - we'll do this until
                    // we encounter a space or a new line 
                
                    while(tmp != ' ' && tmp != '\r' && tmp != '\n' && tmp != -1) {
                            
                        ba.write(tmp);
                            
                        if(tmp == '?' && pos < 4) q_marks[pos++] = counter;
                        
                        tmp = read(in);
        
                        counter++;
    
                    } 
                    
                    ba.writeTo(new FileOutputStream("header.txt"));
                    
                    // unread the character that made us stop the loop above
                    
                    unread(in, tmp); 
    
                    // create byte array
            
                    byte[] read = ba.toByteArray();
                    
                    // if the signs are correct - ie: we got four '?' characters at
                    // positions which is very likely to indicate an encoding -
                    // we'll attempt to decode the correct part of the byte array;
                    // otherwise we'll just unread
                    
                    if(read.length > 0 && read[read.length - 1] == '=' && pos == 4) {
                        
                        if(q_marks[0] == 1 && q_marks[2] == q_marks[1] + 2 && q_marks[3] == read.length - 2) {
                            
                            // get charset name, it should be located between the first
                            // and the second question mark, try if we can use that charset
                            // and unread if we can't
    
                            charset = new StringBuffer();
                            
                            for(int i = q_marks[0] + 1; i < q_marks[1]; i++) charset.append((char)read[i]);
                            
                            if(!Decoder.validateCharset(charset.toString())) for(int i = 0; i < read.length; i++) value.append((char)read[i]);
                            else {
                                
                                // let's create a new byte[] called body containing the byte's
                                // we think we're about to decode
                                
                                byte[] body = new byte[q_marks[3] - q_marks[2]];
                                
                                for(int i = q_marks[2] + 1, j = 0; i < q_marks[3]; i++) body[j++] = read[i];
                                
                                // let's see if we can find an encoding name too, shall we ?
                                // then fetch a decoder and do it - however, default decoder is a Bit8Decoder
                                // in which case we didn't support the encoding
                                
                                encoding = "" + (char)read[q_marks[1] + 1];
                                Decoder dec = getDecoder(encoding);
                                dec.setCharset(charset.toString());
                                
                                if(!(dec instanceof Bit8Decoder)) value.append(dec.toCharArray(dec.decode(new ByteArrayInputStream(body))));
                                else {
                                    
                                    // after all: it wasn't an encoding so just append it to the value
                                    
                                    for(int i = 0; i < read.length; i++) value.append((char)read[i]);
                                    
                                    encoding = "";
                                }
                            }
                            
                        } else for(int i = 0; i < read.length; i++) value.append((char)read[i]);
                        
                    } else for(int i = 0; i < read.length; i++) value.append((char)read[i]);   
                }
                
            } else value.append((char)tmp); // an ordinary character - just append to value
        }
    
        // get an encoder object for the header field
    
        Encoder enc = getEncoder(encoding);
        if(Encoder.validateCharset(charset.toString())) enc.setCharset(charset.toString());
        
        // if the header field is a known address or parameter field we'll handle that
        // otherwise we'll just return a new EmailHeaderField object
        
        if(isAddressField(name.toString())) {
            
            HeaderField answer = null;
            
            // adresses can be undisclosed, in which case the parsing will
            // throw an InvalidFormatException - we'll catch it and return
            // an EmailHeaderField instead
            
            try {
            
                answer = new AddressField(name.toString().trim(), enc);
            
                EmailAddress[] adr = parseAddresses(value.toString().trim());
            
                for(int i = 0; i < adr.length; i++) ((AddressField)answer).addAddress(adr[i]);
                
            } catch(InvalidFormatException e) {
                
                answer = new EmailHeaderField(name.toString().trim(), value.toString().trim(), enc);
                
            }
            
            return answer;
            
        } else if(isParameterField(name.toString())) {
        
            // here we'll attempt to parse parameters
        
            String v = value.toString().trim();
            ParameterField pf = null;
            
            if(v.indexOf(';') > -1) {
                
                pf = new ParameterField(new EmailHeaderField(name.toString().trim(), v.substring(0, v.indexOf(';'))));
                
                // jump to the first ';' and subtract it if possible
                
                v = v.substring(v.indexOf(';'));
                
                if(v.length() > 1) v = v.substring(1);
                else v = "";
                
                // parse the parameters
                
                Hashtable ht = parseParameters(v.trim());
                
                // add them to the parameter field
                
                for(Enumeration e = ht.keys(); e.hasMoreElements();) {
                    
                    String pn = (String)e.nextElement();
                    
                    pf.addParameter(pn, (String)ht.get(pn));
                }
            
            } else pf = new ParameterField(new EmailHeaderField(name.toString().trim(), v));
            
            return pf;
            
        } else return new EmailHeaderField(name.toString().trim(), value.toString().trim(), enc);
    }



    // get attachment name
    
    private String getAttName(Header header) {
        
        String answer = "";
        
        if(header.exists("Content-Disposition")) answer = ((ParameterField)header.get("Content-Disposition")).getParameterValue("filename");
        
        if(answer.length() > 0) return answer;
        else {
            
            if(header.exists("Content-Type")) answer = ((ParameterField)header.get("Content-Type")).getParameterValue("name");
        
            return answer;
        }
    }


    // check header for content type
    
    private int getContentType(Header header) { 
    
        if(header.exists("Content-Type")) {
            
            String type = ((HeaderField)header.get("Content-Type")).getValue();
            String subtype = "";
            String att = "";
            
            if(header.exists("Content-Disposition")) {
                att = header.get("Content-Disposition").getValue();
                if(!att.equalsIgnoreCase("attachment")) att = "";
            }
            
            // check if there's a subtype
            
            if(type.indexOf('/') != -1) {
                
                subtype = type.substring(type.indexOf('/') + 1);
                type = type.substring(0, type.indexOf('/'));
            }
        
            // switch the type's and their subtypes
            
            if(type.equalsIgnoreCase("text")) {
                
                if(subtype.equalsIgnoreCase("html")) return HTML;
                else {
                    
                    if(att.length() > 0) return TEXT_ATT;
                    else return TEXT;
                }
                
            } else if(type.equalsIgnoreCase("image")) {
                
                if(subtype.equalsIgnoreCase("gif")) return GIF_IMAGE;
                else if(subtype.equalsIgnoreCase("jpeg")) return JPG_IMAGE;
                else if(subtype.equalsIgnoreCase("jpg")) return JPG_IMAGE;
                else return BINARY;
                
            } else if(type.equalsIgnoreCase("multipart")) {
            
                if(subtype.equalsIgnoreCase("alternative")) return ALTERNATIVE;
                else if(subtype.equalsIgnoreCase("parallel")) return PARALLEL;
                else if(subtype.equalsIgnoreCase("digest")) return DIGEST;
                else return MIXED;
                
            } else if(type.equalsIgnoreCase("message")) {
                
                // at the moment we don't handle the message content type
                
                //if(subtype.equalsIgnoreCase("rfc822")) return MESSAGE;
                //else return TEXT;
                
                return TEXT;
            
            } else {
                
                if(att.length() > 0) return BINARY_ATT;
                else return BINARY;
            }
            
        } else return TEXT;
    }



    // check header for encoding
        
    private Encoder getEncoder(Header header) { 
    
        if(header.exists("Content-Transfer-Encoding")) return getEncoder(((HeaderField)header.get("Content-Transfer-Encoding")).getValue());
        else return new Bit8Encoder();
    }

    private Encoder getEncoder(String name) {
        if(coderTable.exists(name)) return coderTable.getEncoder(name);
        else return new Bit8Encoder();
    }



    // check header for encoding
        
    private Decoder getDecoder(Header header) { 
    
        if(header.exists("Content-Transfer-Encoding")) return getDecoder(((HeaderField)header.get("Content-Transfer-Encoding")).getValue());
        else return new Bit8Decoder(getCharset(header));
    }

    private Decoder getDecoder(String name) {
        
        if(coderTable.exists(name)) return coderTable.getDecoder(name);
        else return new Bit8Decoder();
    }


    // check header for charset
    
    private String getCharset(Header header) { 
    
        if(header.exists("Content-Type")) {
            
            HeaderField field = header.get("Content-Type");
            
            if(field instanceof ParameterField) return ((ParameterField)field).getParameterValue("charset");
            else return defaultCharset;
            
        } else return defaultCharset;
    }


    // check header for boundary
    
    private String getBoundary(Header header) { 
    
        if(header.exists("Content-Type")) {
            
            HeaderField field = header.get("Content-Type");
            
            if(field instanceof ParameterField) return ((ParameterField)field).getParameterValue("boundary");
            else return "";
            
        } else return "";
    }


    /**
    * This method checks if the "name" parameter is known as a header
    * field name through the <code>arrayContains</code> method. Sublasses can 
    * overide this method to provide support for their own custom header fields.<p>
    *
    * This method recognises the following header field names: from, resent-from,
    * sender, resent-sender, reply-to, resent-reply-to, to, resent-to, cc, resent-cc
    * bcc, resent-bcc, date, subject, comment, in-reply-to, message-id, references,
    * keywords, encrypted, mime-version, content-type, content-transfer-encoding,
    * content-id, content-desciption, content-disposition, received, return-path and
    * status.
    */

    protected boolean isHeaderField(String name) {
        
        // oh - it could be user defines so we'll start to check that
        
        if((name.startsWith("X-") || name.startsWith("x-")) && name.length() > 2) return true;
        else {
            
            String[] fieldnames = { "to", "from", "sender", "cc", "bcc", "date",
                "subject", "reply-to", "comment", "in-reply-to", "message-id",
                "resent-to", "resent-from", "resent-sender", "resent-reply-to",
                "resent-cc", "resent-bcc", "resent-message-id", "references",
                "keywords", "encrypted", "mime-version", "content-type",
                "content-transfer-encoding", "content-id", "content-description",
                "content-disposition", "received", "return-path", "status" };
                
            return arrayContains(fieldnames, name);
            
        }
    }



    /**
    * This method checks if the "name" parameter is known as a parameter header
    * field name through the <code>arrayContains</code> method. Sublasses can 
    * overide this method to provide support for their own custom parameter fields.<p>
    *
    * This method recognises two parameter field names: content-type and 
    * content-disposition.
    */

    protected boolean isParameterField(String name) {
        
        String[] fieldnames = { "content-type", "content-disposition" };
        
        return arrayContains(fieldnames, name);
    }


    
    /**
    * This method checks if the "name" parameter is known as a address header
    * field name through the <code>arrayContains</code> method. Sublasses can 
    * overide this method to provide support for their own custom address fields.<p>
    *
    * This method recognises the following address field names: from, resent-from,
    * sender, resent-sender, reply-to, resent-reply-to, to, resent-to, cc, resent-cc
    * bcc and resent-bcc.
    */

    protected boolean isAddressField(String name) {
        
        String[] fieldnames = { "from", "resent-from", "sender", "resent-sender", 
            "reply-to", "resent-reply-to", "to", "resent-to", "cc", "resent-cc", 
            "bcc", "resent-bcc" };
            
        return arrayContains(fieldnames, name);
    }



    /**
    * This simple method checks a String array for a certain String. 
    * The search is case insensitive and the method left protected
    * so it can be used by subclasses.
    */

    protected boolean arrayContains(String[] array, String name) {
        
        boolean answer = false;
        
        for(int i = 0; i < array.length; i++) {
            
            if(name.equalsIgnoreCase(array[i])) {
                
                answer = true;
                break;
            }
        }
    
        return answer;
    }



    // parse a header field value for single or multiple email addresses

    private EmailAddress[] parseAddresses(String value) throws InvalidFormatException {
        
        // first setup a list to hold the answer and break down the header
        // value to a character array for easier handling
        
        ArrayList list = new ArrayList();

        char[] ch = value.toCharArray();
        
        /*
        * Now, this is how it will work: An address can contain a 
        * address and a name or a single address. The field can contain
        * multiple addresses delimited by a comma. The name part of an adress
        * can be enclosed in quotes. 
        *
        * The EmailAddress class is able to parse the different forms of syntax 
        * but only for single addresses so we must divide the field into separate 
        * strings for the EmailAddress class to parse.
        *
        * To achieve that we must keep track of the eventual quotes in the name
        * partions since a comma AND a quote character MAY appear in the name. So:
        * we'll set a state parameter which can be either 1 or -1. -1 being outside
        * a quote and 1 being inside. 
        *
        * If we encounter a quote which is preceded by a backslash it is a correct 
        * "inline" quote and we'll simple add it to the buffer and not change state.
        * Otherwise we'll flip the state, add the quote character and continue.
        *
        * A comma character is allowed inside a quote but if we encounter it outside a 
        * it is a delimiter and thus we'll attempt to parse the address and flush the
        * buffer.
        */
                
        StringBuffer tmp = new StringBuffer();
                
        int QUOTE = 1;
        int state = -1;
                
        for(int i = 0; i < ch.length; i++) {
                    
            if(ch[i] == '\"') {
                
                // a quote character - check for backslash
                        
                if(tmp.length() > 0 && tmp.charAt(tmp.length() - 1) == '\\') {
                            
                    tmp.append('\"');
                            
                } else {
                    
                    // switch state
                
                    if(state == -1) state = QUOTE;
                    else state = -1;
                        
                    tmp.append('\"');
                }
                        
            } else if(ch[i] == ',') {
                        
                if(state == QUOTE) tmp.append(',');
                else {
                    
                    // a comma outside a quote is a delimiter - parse and flush
                    
                    list.add(EmailAddress.parseAddress(tmp.toString()));
                    tmp = new StringBuffer();
                }
            
            } else tmp.append(ch[i]);
        }
    
        // pick up the left over
    
        if(tmp.length() > 0) list.add(EmailAddress.parseAddress(tmp.toString()));
    
        // copy references to array and return
    
        EmailAddress[] answer = new EmailAddress[list.size()];
        list.toArray(answer);
        return answer;
    }   
    
    
    
    // attempt to parse parameters to a hashtable - names ad keys and values as...
    
    private Hashtable parseParameters(String value) {
        
        // setup answer and get the value as a character array
        
        Hashtable table = new Hashtable();
        
        char[] ch = value.toCharArray();
        
        /*
        * This method works much like the "parseAddresses" above. We'll have to
        * keep track of the "quote" state and then check for delimiters
        * outside it.
        *
        * The delimiters are the '=' separating a name from a value and the ';'
        * separating the parameters.
        */
                
        StringBuffer tmp = new StringBuffer();
        String tmp_name = "";
                
        int QUOTE = 1;
        int state = -1;
        
        for(int i = 0; i < ch.length; i++) {
            
            if(ch[i] == '\"') {
                
                // a quote character - check from backslash which is allowed
                // in the parameter field or change state
                    
                if(tmp.length() > 0 && tmp.charAt(tmp.length() - 1) == '\\') {
                            
                    tmp.append('\"');
                            
                } else {
                
                    if(state == -1) state = QUOTE;
                    else state = -1;
                        
                    tmp.append('\"');
                }
                        
            } else if(ch[i] == '=') {
                
                // if we're in a quote just add the character - otherwise
                // we've got a name and will start to collect the value
                
                if(state != QUOTE) {
                
                    tmp_name = tmp.toString();
                    tmp = new StringBuffer();
                    
                } else tmp.append('=');
                
            } else if(ch[i] == ';') {
                
                // if we're in a quote ust add the character - otherwise
                // we'll trim the value and remove eventual surrounding
                // quote characters and put the name and the value into the
                // hashtable and start over again
                
                if(state != QUOTE) {
                    
                    String tmp_2 = (tmp.toString()).trim();
                    
                    if(tmp_2.startsWith("\"") && tmp_2.length() > 1) tmp_2 = tmp_2.substring(1);
                    if(tmp_2.endsWith("\"") && tmp_2.length() > 1) tmp_2 = tmp_2.substring(0, tmp_2.length() - 1);
                   
                    table.put(tmp_name, tmp_2);
                    
                    tmp = new StringBuffer();
                    tmp_name = "";
                    
                } else tmp.append(';');
                
            } else tmp.append(ch[i]);
        }
    
        // take care of the left over name and value
    
        if(tmp_name.length() > 0) {
            
            String tmp_2 = (tmp.toString()).trim();
            
            if(tmp_2.startsWith("\"") && tmp_2.length() > 1) tmp_2 = tmp_2.substring(1);
            if(tmp_2.endsWith("\"") && tmp_2.length() > 1) tmp_2 = tmp_2.substring(0, tmp_2.length() - 1);
 
            
            table.put(tmp_name, tmp_2);
        }
    
        // return table
        
        return table;
    }      
    
    
    
    // crude read to end of message
    
    private void readToEnd(PushbackInputStream in, String boundary) {
        
        try {
            
            int tmp = 0;
            int last_char = 0;
            
            StringBuffer line = new StringBuffer();
            
            while(true) {
                
                tmp = read(in);
                
                if(tmp == -1) break;
                else if(tmp == '.' && (last_char == '\n' || last_char == '\r')) {
                    
                    // check for dot delimiter
                    
                    tmp = read(in);
                    
                    if(tmp == '\r') {
                        
                        read(in);
                        trimOriginal(3);
                        break;
                        
                    } else line.append('.').append((char)tmp);
                
                } else if(tmp == '\r') {
                    
                    // check for end boundary
                
                    if(line.toString().equals("--" + boundary + "--")) break;
                    else line = new StringBuffer();
                 
                } else if(tmp =='\n' && last_char != '\r') {
                    
                    // check for end boundary
                
                    if(line.toString().equals("--" + boundary + "--")) break;
                    else line = new StringBuffer();
                 
                } else if(tmp != '\n') line.append((char)tmp);
                
                last_char = tmp;
            }
        
        } catch(IOException e) { }
    }

    
    
    // read byte from stream and write it to original
    
    private int read(InputStream in) throws IOException {
        
        lastByte = in.read();
        originalMessage.append((char)lastByte);
        return lastByte;

    }


    // unread character and add pushback count
    
    private void unread(PushbackInputStream in, int ch) throws IOException {
        
        if(ch != -1) in.unread(ch);
        originalMessage.deleteCharAt(originalMessage.length() - 1);
        
        if(originalMessage.length() > 0) lastByte = originalMessage.charAt(originalMessage.length() - 1);
        else lastByte = -1;
        
    }


    // delete characters from end of original (delimiting dot)

    private void trimOriginal(int i) {
        originalMessage = new StringBuffer(originalMessage.substring(0, originalMessage.length() - i));
    }
    
    
    
    // reset class message containers  
    
    private void reset() {
        
        originalMessage = new StringBuffer();
        mainHeader = null;
        lastByte = -1;

    }   
    
    // message recognition variables:
    
    // parser states
    private final int BODY = 9;
    private final int NEXT = 14;
    private final int HEADER = 10;
    
    // content types
    private final int MIXED = 5;
    private final int PARALLEL = 6;
    private final int ALTERNATIVE = 7;
    private final int DIGEST = 8;
    private final int MESSAGE = 15;
    private final int JPG_IMAGE = 16;
    private final int GIF_IMAGE = 17;
    private final int HTML = 11;
    private final int TEXT = 12;
    private final int BINARY = 13;
    private final int TEXT_ATT = 18;
    private final int BINARY_ATT = 19;
}