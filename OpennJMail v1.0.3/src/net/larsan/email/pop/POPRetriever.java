/*
*  Code: POPRetriever.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2000 Lars J. Nilsson
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

package net.larsan.email.pop;

import java.io.*;
import java.net.*;
import java.util.*;

import net.larsan.email.*;
import net.larsan.email.util.*;

/**
* This class connects to and retrieves EmailMessages from a POP server. The class
* is not threaded and thus only provides a method for retrieving messages
* one at the time.<p>
*
* The connection to the server is created with a read timeout of 30 seconds. Users
* can set their own timeout with the <code>setTimeout</code> method. The timeout is
* disabled if set to zero.<p>
*
* <b>Important!</b> The class does not connect / disconnect automaticly. So first
* <code>connect</code>, then send any other commands, end with <code>quit</code>.<p>
*
* The sender can log every command and answer. Enable logging with the 
* <code>enableLog</code> method and retreive it with <code>getLog</code> method.<p>
*
* Implementors can set a default character set. This character set will be used by the 
* message parser only if no character set is specified in the message. Default set for
* this retriever is "ISO-8859-1" which should be sufficient for most implementors dealing
* with western languages.<p>
*
* The SMTP protocoll is descibed in RFC 1725.
*
* @author Lars J. Nilsson
* @version 1.2 31/10/00
*/

public class POPRetriever {
    
    // instance data
    private PushbackInputStream in;
    private MessageParser parser;
    private BufferedWriter out;
    private Socket socket;
    private String host, defaultCharset;
	private int timeout, port;
	private StringBuffer log;
	
	/**
	* Construct a new POPRetriever on specified port and host.
	*/
    
    public POPRetriever(String host, int port) {
    
        in = null;
        out = null;
        socket = null;
        
        defaultCharset = "ISO-8859-1";
        
        parser = new MessageParser();
        parser.setDefaultCharset(defaultCharset);
        
        this.host = host;
        this.port = port;
        
        timeout = 30;
    }

	/**
	* Construct a new POPRetriever on specified host and default port (110).
	*/

    public POPRetriever(String host) {
        this(host, 110);
    }



    /**
    * Set the default character set. This character set will be used wherever the 
    * message parser encounter textual content which it cannot determine the character set on.<p>
    *
    * Default setting for this retriever is "ISO-8859-1".
    */

    public void setDefaultCharset(String charset) {
        defaultCharset = charset;
        parser = new MessageParser();
        parser.setDefaultCharset(charset);
    }



    /**
    * Get the current default character set.
    */

    public String getDefaultCharset() {
        return defaultCharset;
    }



    /**
    * Test if the sender log is enabled.
    */

    public boolean isLogEnabled() {
        return log == null ? false : true;
    }



    /**
    * Enable logging.
    */
    
    public void enableLog() {
        if(log == null) {
            log = new StringBuffer();
            log.append("SMTPSender log started - ");
            log.append(new Date().toString());
            log.append("\r\n");
        }
    }



    /**
    * Disable logging.
    */
    
    public void disableLog() {
        log = null;
    }



    /**
    * Get sender log. Will return an empty string if the logging
    * is not enabled.
    */
    
    public String getLog() {
        return log == null ? new String() : log.toString();
    }



    /**
    * Reset log. If logging is not enabled this method <b>will not</b>
    * automaticly enable the it.
    */

    public void resetLog() {
        if(log != null) {
            log = new StringBuffer();
            log.append("POPRetriever log started - ");
            log.append(new Date().toString());
            log.append("\r\n");
        }
    }



    /**
    * Set server socket read timeout in seconds. 
    */

    public void setTimeout(int seconds) {
        if(seconds < 0) seconds = 0; // check for -1, just in case...
        this.timeout = seconds;
    }



    /**
    * Get server socket read timeout in seconds.
    */

    public int getTimeout() {
        return timeout;
    }



    /**
    * Connect to POP server.
    */

    public void connect(String user, String pass) throws POPException, IOException {
        
        if(socket != null) throw new IOException("POPRetriever in use, disconnect and try again.");
        
        socket = new Socket(host, port);
        socket.setSoTimeout(timeout * 1000); // set read timeout
        
        in = new PushbackInputStream(new BufferedInputStream(socket.getInputStream()), 512);
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        
        // consume "+OK" and authorize
        
        consume();
            
        write("USER ");
        writeln(user);
            
        consume();
        
        // send pass directly to stream so it does not get logged
        
        out.write("PASS ");
        out.write(pass);
        out.write("\r\n");
        out.flush();
        
        // now: log a pass dummy...
        
        log("PASS ");
        
        for(int i = 0; i < pass.length(); i++) log("*");
        
        logln("");
        
        // ... and consume
        
        consume();
            
    }



    /**
    * Quit this pop 3 session. This method disconnects from the server.
    */

    public void quit() {
        
        try {
            
            writeln("QUIT");
            
        } catch(IOException e) { 
        } finally {
            
            disconnect();
            
        }
    }



    /**
    * Disconnect from server. This method is called by the <code>quit</code> methods which
    * also send a "QUIT" command.
    */

    protected void disconnect() {
        
        try {
            
            in.close();
            out.close();
            socket.close();
            
        } catch(IOException e) {
        } finally {
            
            in = null;
            out = null;
            socket = null;
        }
    }


    /**
    * Send "STAT" command. Returns a MessageMark where the public variable
    * "number" contains the number of messages in the maildrop and "size" their
    * total size in bytes.
    */

    public MessageMark status() throws POPException, IOException {
        
        writeln("STAT");
        
        String response = consume();
        
        return parseMarkString(response);
    }


    /**
    * Send "LIST" command on a single message. Returns a MessageMark where the public variable
    * "number" contains the number of the message and "size" it's size in bytes.
    */

    public MessageMark list(int number) throws POPException, IOException {
        
        write("LIST ");
        writeln(number);
    
        String response = consume();
        
        return parseMarkString(response);
    }


    /**
    * Send "LIST" command. Returns a MessageMark array where each member represents
    * a single message and it's public variable "number" contains the number of the message 
    * and "size" it's size in bytes.
    */

    public MessageMark[] list() throws POPException, IOException {
        
        // use a vector as buffer
        
        ArrayList list = new ArrayList();
        
        writeln("LIST"); 
        
        // get the response and feed it into a reader
        
        String response = consume(true);
        BufferedReader br = new BufferedReader(new StringReader(response));
        
        // each line contains a single message, but not the first - so will
        // start with dropping that
        
        String tmp = br.readLine();
        
        while((tmp = br.readLine()) != null) {
        
            try {
                
                // parse the line and add to buffer
            
                list.add(parseMarkString(tmp));
            
            } catch(POPException e) { }
        }
    
        // set references to array and return
    
        MessageMark[] answer = new MessageMark[list.size()];
        list.toArray(answer);
        return answer;
    }



    /**
    * Retrieve a single message from the server.
    */

    public Message retrieve(int number) throws POPException, IOException {
    
        write("RETR ");
        writeln(number);
        
        consume();
        
        Message answer = parser.parseMessage(in);
        
        cleanUpStream();
        
        return answer;
    }



    /**
    * Delete a single message from the server. This action will not be completed until
    * the "QUIT" command is send and can be halted before that by the "RESET" command.
    */

    public void delete(int number) throws POPException, IOException {
        
        write("DELE ");
        writeln(number);
        
        consume();
    }

    
    
    /**
    * Reset maildrop. Message marked as deleted will be restored.
    */

    public void reset() throws POPException, IOException {
        
        writeln("RSET");
        consume();
    }



    /**
    * Test if the POPRetriever is connected to a server.
    */

    public boolean isConnected() {
        return socket == null ? false : true;
    }



    // send a string to the output stream

    private void write(String s) throws IOException {
        
        log(s);
        
        out.write(s);
    }



    // send a string to the output stream followed by a canonical new line sequence

    private void writeln(String s) throws IOException {
        
        logln(s);
        
        StringBuffer string = new StringBuffer(s);
        
        string.append("\r\n");
        
        out.write(string.toString());

        out.flush();
    }



    // send an int to the stream followed by a canonical new line sequence

    private void writeln(int string) throws IOException {
        writeln(Integer.toString(string));
    }



    // consume a single line answer from the server

    private String consume() throws POPException, IOException {
        return consume(false);
    }



    // consume answer from the server, boolean parameter determines if the
    // answer can be on multiple lines

    private String consume(boolean multiline) throws POPException, IOException {
        
        // setup answer buffer and read to first carriage return
        
        StringBuffer answer = new StringBuffer();
        
        int tmp = 0;
        
        while((tmp = in.read()) != '\r') answer.append((char)tmp);
        
        // log the first line
        
        logln(answer.toString());
        
        // discard the following line feed character
        
        in.read();
        
        // is this a valid "+OK" answer ?
        
        if(answer.charAt(0) != '+') throw new POPException(host, "Negative response from pop server: " + answer.toString());
        
        if(multiline) {
            
            // add new line to answer
            
            answer.append("\r\n");
            
            int last = '\n';
            tmp = in.read();
            
            while(true) {
                
                // we'll continue reading until we find a non.transparent dot
                // on a single line
                
                if(tmp == '.' && last == '\n') {
                    
                    tmp = in.read();
                    
                    if(tmp != '\r') { 
                    
                        // wasn't the and so unread and continue
                        
                        in.unread(tmp);
                        answer.append('.');
                        
                    } else {
                        
                        // finished - discard line feed and return
                        
                        in.read();
                        break;
                    }
                
                } else answer.append((char)tmp);
            
                last = tmp;
                tmp = in.read();
            }
        
        }
    
        // delete the first four charcters, ie: "+OK " - we already know that
    
        return (answer.substring(4)).toString();
        
    }



    // log string

    private void log(String s) {
        if(log != null) log.append(s);
    }



    // log string followed by a new line

    private void logln(String s) {
        log(s);
        log("\r\n");
    }



    // check stream after a message retrieval

    private void cleanUpStream() throws IOException {
        
        // first check for trailing spaces, returns and line feeds
        
        int tmp = in.read();
        
        while(tmp == '\r' || tmp == '\n' || tmp == ' ') tmp = in.read();
        
        // if there's a dot we'll discard the following two characters (the canonical
        // new line), otherwise we'll unread the character (shouldn't really happend)
        
        if(tmp == '.') {
            
            in.read();
            in.read();
            
        } else if(tmp != -1) in.unread(tmp);
        
    }



    // tokenize this string into to numbers and return a MessageMark

    private MessageMark parseMarkString(String tmp) throws POPException {
        
        StringTokenizer st = new StringTokenizer(tmp, " ");
        
        try {
            
            int n = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            
            return new MessageMark(n, s);
            
        } catch(NoSuchElementException e) {
            
            throw new POPException(host, "Invalid pop server response format: " + tmp);
            
        }
    }
}