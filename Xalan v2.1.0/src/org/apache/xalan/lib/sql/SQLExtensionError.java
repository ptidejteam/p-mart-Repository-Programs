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



package org.apache.xalan.lib.sql;

import org.w3c.dom.Node;
import java.sql.SQLException;

import java.lang.Integer;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;

/**
 * <p>
 * An Extension of the Extension Error Class that adds information
 * about the SQL Exception. {@link ExtensionError}
 * </p>
 * <pre>
 * <p>
 * This class adds the following information to the Document
 *
 * &lt;ext-error&gt;
 *    &lt;sql-error&gt;
 *        &lt;error-code&gt; The SQL Error Code returned by the driver &lt;/error-code&gt;
 *        &lt;state&gt; The Current SQL Connection State &lt;/state&gt;
 *    &lt;/sql-error&gt;
 * &lt;ext-error&gt;
 * </p>
 * </pre>
 *
 */
public class SQLExtensionError extends ExtensionError {

  private SQLException m_sql_ex = null;

  public SQLExtensionError(SQLException e)
  {
    m_sql_ex = e;
    super.processBaseError(e);
    // dump();
  }

  protected void populateSpecificData(Document doc, Node n)
  {
    Element etmp = null;
    Text text = null;
    CDATASection cdata = null;

    Element root = doc.createElement("sql-error");
    n.appendChild(root);


    Element code = doc.createElement("error-code");
    root.appendChild(code);

    int ecode = m_sql_ex.getErrorCode();
    Integer i = new Integer(ecode);

    text = doc.createTextNode(i.toString());
    code.appendChild(text);

    Element state = doc.createElement("state");
    root.appendChild(state);
    text = doc.createTextNode(m_sql_ex.getSQLState());
    state.appendChild(text);

    // m_sql_ex = m_sql_ex.getNextException();
    // if ( null != m_sql_ex ) populateSpecificData(doc, n);
  }

}