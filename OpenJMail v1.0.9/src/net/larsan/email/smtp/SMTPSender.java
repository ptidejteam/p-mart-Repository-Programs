/*
*  Code: SMTPSender.java
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

package net.larsan.email.smtp;

import java.io.*;
import java.net.*;
import java.util.Date;

import net.larsan.email.*;
import net.larsan.email.util.*;
import net.larsan.email.stream.*;

/**
* This class connects to and sends Messages to an SMTP server. The class
* is not threaded and thus only provides a method for sending a single messages
* one at the time.<p>
*
* The connection to the server is created with a read timeout of 30 seconds. Users
* can set their own timeout with the <code>setTimeout</code> method. The timeout is
* disabled if set to zero.<p>
*
* <b>Important!</b> The class does not connect / disconnect automaticly. So first
* <code>connect</code>, then <code>send</code> and then <code>disconnect</code>.<p>
*
* The sender can log every command and answer. Enable logging with the 
* <code>enableLog</code> method and retreive it with <code>getLog</code> method.<p>
*
* The SMTP protocoll is descibed in RFC 821.
*
* @author Lars J. Nilsson
* @version 1.2.2 31/08/02
*/

public class SMTPSender {
    
    // instance data
    private BufferedReader in;
    private MailOutputStream out;
    private Socket socket;
    private String host;
	private int timeout, port;
	private StringBuffer log;
	
	/**
    * Contruct a new SMTPSender.
    */

	public SMTPSender(String host, int port){
	    this.host = host;
	    this.port = port;
	    
	    log = null;
	    in = null;
	    socket = null;
	    out = null;
	    timeout = 30;
	}

    /**
    * Contruct a new SMTPSender with a Server object using the
    * specified address on port 25 (default SMTP port).
    */

    public SMTPSender(String hostAddress) {
        this(hostAddress, 25);
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
        }
    }

    /**
    * Disable logging.
    */
    
    public void disableLog() {
        if(log != null) log = null;
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
            log.append("SMTPSender log started - ");
            log.append(new Date().toString());
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
    * Connect to SMTP server.
    */

    public void connect() throws SMTPException, IOException {
        
        if(socket != null) throw new IOException("SMTPSender in use, disconnect and try again.");
        
        socket = new Socket(host, port);
        socket.setSoTimeout(timeout * 1000); // set read timeout
        
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new MailOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        
        // check if SMTP server is ready (220) and then be polite
        // and say helo using your ip address
        
        consume(220);
        write("HELO ");
        writeln((InetAddress.getLocalHost()).getHostAddress());
        consume(250);
    }

    /**
    * Disconnect from server.
    */

    public void disconnect() {
        try {
            writeln("QUIT");
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
    * Send a Message to the SMTPServer.
    */
    
    public void send(Message message) throws SMTPException, IOException {
        EmailAddress user = message.getOriginator();
        if(user == null) throw new SMTPException(host, "No sender address provided in message."); // oops... no sender address
        sendFor(message, user);
    }

    /**
    * Send an Message to the SMTP server with an email address identifying the
    * sender to the SMTP server (as in SMTP 'MAIL FROM').
    */

    public void sendFor(Message message, EmailAddress user) throws SMTPException, IOException {
        
        // reset 
        
        writeln("RSET");
        consume(250);
        
        // get a reference to the header 
        Header header = message.getMessageBody().getHeader();
               
        // send user address 
        writeln("MAIL FROM: <" + user.getAddress() + ">");
        consume(250);
    
        // get reference to and send recipients
        Recipient[] recipients = message.getRecipientHandler().getRecipients();
        if(recipients.length == 0) throw new SMTPException(host, "No available recipients in message.");
        
        for(int i = 0; i < recipients.length; i++) {
            writeln("RCPT TO: <" + (recipients[i].getAddress()).getAddress() + ">");
            consume(new int[] { 250, 251 });
        }
    
        // start message
        writeln("DATA");
        consume(354);
        
        // if there is a BCC field in the message header, we don't want to send that
        if(header.exists("Bcc")) {
            HeaderField field = header.get("Bcc");
            header.removeAll("Bcc");
            message.write(out);
            header.set(field);
        } else message.write(out);
        
        writeDataEnd(); // non-transparent dot folowed by a new line
        consume(250);
    }

    /**
    * Test if the SMTPSender is connected to a server.
    */

    public boolean isConnected() {
        return socket == null ? false : true;
    }


    // send an non.transparent dot followed be a new line sequence
    
    private void writeDataEnd() throws IOException {
        writeln();
        out.writeDot();
        writeln();
    }


    // send a string to the output stream

    private void write(String s) throws IOException {
        write(s.toCharArray());
    }


    // send a char[] to the output stream

    private void write(char[] ch) throws IOException {
        if(out == null) throw new IOException("SMTPSender not connected to a server, connect and try again.");
        for(int i = 0; i < ch.length; i++) out.write(ch[i]);
        if(log != null) log.append(new String(ch));
        out.flush();
    }


    // send a string to the output stream followed by a canonical new line sequence

    private void writeln(String s) throws IOException {
        write(s.toCharArray());
        writeln();
    }


    // send a canonical new line sequence to the output stream

    private void writeln() throws IOException {
        if(log != null) log.append("\r\n");
        out.writeln();
        out.flush();
    }


    // read SMTP server answer, compare with expected answer

    private void consume(int expected) throws SMTPException, IOException {
        consume(new int[] { expected });
    }

    // read SMTP server answer, compare with expected answers

    private void consume(int[] expected) throws SMTPException, IOException {
        
        // BUGFIX ver 1.2.1: Now we must read check for multiple line response
        // so we'll move tho log function and buffer incoming lines
        
        String tmp = readLine();
        
        StringBuffer buff = new StringBuffer(tmp);
        boolean flag = false;

        for(int i = 0; i < expected.length; i++) {
            
            // SMTP answers will alway start with the answer code
            // and if it does we're finished and don't need to 
            // loop anymore
            
            if(tmp.startsWith(String.valueOf(expected[i]))) {
                flag = true;
                break;
            }
        }
        
        // check multiple lines
        if(tmp.length() > 3 && tmp.charAt(3) == '-') {
            do {
               tmp = readLine();  
               buff.append("\r\n").append(tmp);
            } while(tmp.length() > 3 && tmp.charAt(3) == '-');
        }
        
        
        if(log != null) { // log answer
            log.append(buff.toString());
            log.append("\r\n");
        }


        if(!flag) { // we didn't get the right answer
            int code = (buff.length() < 3 ? 0 : Integer.parseInt(buff.toString().substring(0, 3))); // parse code
            throw new SMTPException(host, "Unexpected SMTP server answer: " + buff.toString(), code);
        }
    }


    // Read line from server - if the connection is timed out 
    // disconnect and rethrow exception
    
    private String readLine() throws IOException {
        try {
            return in.readLine();
        } catch(InterruptedIOException e) {
            disconnect();
            throw e;    
        }
    }
}
