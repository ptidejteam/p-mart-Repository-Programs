package org.apache.velocity.demo.action;

/* 
 * The Apache Software License, Version 1.1
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Jakarta-Regexp", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

// Java
import java.util.*;

// Java Servlet
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Velocity
import org.apache.velocity.context.Context;

// Demo
import org.apache.velocity.demo.om.*;

/**
 * Handles posting new messages to the Forum
 * 
 * @author <a href="mailto:daveb@miceda-data.com">Dave Bryson</a>
 * @version $Revision: 1.2 $
 * $Id: PostCommand.java,v 1.2 2001/01/03 06:08:06 geirm Exp $
 */
public class PostCommand extends Command
{
    public PostCommand( HttpServletRequest req, HttpServletResponse resp )
    {
        super( req, resp );
    }

    /**
     * Collects the parameters from the Form.
     * Creates a new message and stores it then
     * gets a fresh list and calls the list template
     */
    public String exec( Context ctx )
    {
        String name = request.getParameter("name");
        String subject = request.getParameter("subject");
        String email = request.getParameter("email");
        String content = request.getParameter("content");
        
        Message message = new Message();
        message.setName( name );
        message.setSubject( subject );
        message.setEmail( email );
        message.setContents( content );
        
        ForumDatabase.postMessage( message );
        
        Object[] list = ForumDatabase.listAll();
        
        ctx.put("listall", list );
        ctx.put("hasMessages", Boolean.TRUE );
        
        return ListCommand.LIST;
    }
}

