package org.apache.velocity.runtime.parser.node;

/* Generated By:JJTree: Do not edit this line. ASTElseIfStatement.java */

/*
 * Copyright 2000-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import java.io.Writer;
import java.io.IOException;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.*;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * This class is responsible for handling the ElseIf VTL control statement.
 * 
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: ASTElseIfStatement.java 356581 2005-12-13 19:26:31Z henning $ 
*/
public class ASTElseIfStatement extends SimpleNode
{
    public ASTElseIfStatement(int id)
    {
        super(id);
    }

    public ASTElseIfStatement(Parser p, int id)
    {
        super(p, id);
    }

    /** Accept the visitor. **/
    public Object jjtAccept(ParserVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    /**
     * An ASTElseStatement is true if the expression
     * it contains evaluates to true. Expressions know
     * how to evaluate themselves, so we do that
     * here and return the value back to ASTIfStatement
     * where this node was originally asked to evaluate
     * itself.
     */
    public boolean evaluate ( InternalContextAdapter context)
        throws MethodInvocationException
    {
        return jjtGetChild(0).evaluate(context);
    }

    /**
     *  renders the block
     */
    public boolean render( InternalContextAdapter context, Writer writer)
        throws IOException,MethodInvocationException, 
        	ResourceNotFoundException, ParseErrorException
    {
        return jjtGetChild(1).render( context, writer );
    }
}
