
package org.apache.velocity.runtime.directive;

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

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.app.event.EventHandlerUtil;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.resource.Resource;

/**
 * <p>Pluggable directive that handles the #include() statement in VTL. 
 * This #include() can take multiple arguments of either 
 * StringLiteral or Reference.</p>
 *
 * <p>Notes:</p>
 * <ol>
 * <li>For security reasons, the included source material can only come
 *    from somewhere within the template root tree.  If you want to include
 *    content from elsewhere on your disk, add extra template roots, or use
 *    a link from somwhere under template root to that content.</li>
 *
 *  <li>By default, there is no output to the render stream in the event of
 *    a problem.  You can override this behavior with two property values :
 *       include.output.errormsg.start
 *       include.output.errormsg.end
 *     If both are defined in velocity.properties, they will be used to
 *     in the render output to bracket the arg string that caused the 
 *     problem.
 *     Ex. : if you are working in html then
 *       include.output.errormsg.start=&lt;!-- #include error :
 *       include.output.errormsg.end= --&gt;
 *     might be an excellent way to start...</li>
 *
 *  <li>As noted above, #include() can take multiple arguments.
 *    Ex : #include('foo.vm' 'bar.vm' $foo)
 *    will include all three if valid to output without any
 *    special separator.</li>
 *  </ol>
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:kav@kav.dk">Kasper Nielsen</a>
 * @version $Id: Include.java 441235 2006-09-07 21:38:14Z dlr $
 */
public class Include extends InputBase
{
    private String outputMsgStart = "";
    private String outputMsgEnd = "";

    /**
     * Return name of this directive.
     */
    public String getName()
    {
        return "include";
    }        
    
    /**
     * Return type of this directive.
     */
    public int getType()
    {
        return LINE;
    }        

    /**
     *  simple init - init the tree and get the elementKey from
     *  the AST
     */
    public void init(RuntimeServices rs, InternalContextAdapter context,
                     Node node) 
        throws Exception
    {
        super.init( rs, context, node );

        /*
         *  get the msg, and add the space so we don't have to
         *  do it each time
         */
        outputMsgStart = rsvc.getString(RuntimeConstants.ERRORMSG_START);
        outputMsgStart = outputMsgStart + " ";
        
        outputMsgEnd = rsvc.getString(RuntimeConstants.ERRORMSG_END );
        outputMsgEnd = " " + outputMsgEnd;   
    }

    /**
     *  iterates through the argument list and renders every
     *  argument that is appropriate.  Any non appropriate
     *  arguments are logged, but render() continues.
     */
    public boolean render(InternalContextAdapter context, 
                           Writer writer, Node node)
        throws IOException, MethodInvocationException,
               ResourceNotFoundException
    {
        /*
         *  get our arguments and check them
         */

        int argCount = node.jjtGetNumChildren();

        for( int i = 0; i < argCount; i++)
        {
            /*
             *  we only handle StringLiterals and References right now
             */

            Node n = node.jjtGetChild(i);

            if ( n.getType() ==  ParserTreeConstants.JJTSTRINGLITERAL || 
                 n.getType() ==  ParserTreeConstants.JJTREFERENCE )
            {
                if (!renderOutput( n, context, writer ))
                    outputErrorToStream( writer, "error with arg " + i 
                        + " please see log.");
            }
            else
            {
                rsvc.getLog().error("#include() invalid argument type: "
                                    + n.toString());
                outputErrorToStream( writer, "error with arg " + i 
                    + " please see log.");
            }
        }
        
        return true;
    }

    /**
     *  does the actual rendering of the included file
     *
     *  @param node AST argument of type StringLiteral or Reference
     *  @param context valid context so we can render References
     *  @param writer output Writer
     *  @return boolean success or failure.  failures are logged
     */
    private boolean renderOutput( Node node, InternalContextAdapter context,
                                  Writer writer )
        throws IOException, MethodInvocationException,
               ResourceNotFoundException
    {
        if ( node == null )
        {
            rsvc.getLog().error("#include() null argument");
            return false;
        }

        /*
         *  does it have a value?  If you have a null reference, then no.
         */
        Object value = node.value( context );
        if ( value == null)
        {
            rsvc.getLog().error("#include()  null argument");
            return false;
        }

        /*
         *  get the path
         */
        String sourcearg = value.toString();

        /*
         *  check to see if the argument will be changed by the event handler
         */

        String arg = EventHandlerUtil.includeEvent( rsvc, context, sourcearg, context.getCurrentTemplateName(), getName() );

        /*
         *   a null return value from the event cartridge indicates we should not
         *   input a resource.
         */
        boolean blockinput = false;
        if (arg == null)
            blockinput = true;

        Resource resource = null;

        try
        {
            if (!blockinput)
                resource = rsvc.getContent(arg, getInputEncoding(context));
        }
        catch ( ResourceNotFoundException rnfe )
        {
            /*
             * the arg wasn't found.  Note it and throw
             */
            rsvc.getLog().error("#include(): cannot find resource '" + arg +
                                "', called from template " +
                                context.getCurrentTemplateName() + " at (" +
                                getLine() + ", " + getColumn() + ")" );
            throw rnfe;
        }

        /**
         * pass through application level runtime exceptions
         */
        catch( RuntimeException e )
        {
            throw e;
        }
        catch (Exception e)
        {
            rsvc.getLog().error("#include(): arg = '" + arg +
                                "', called from template " +
                                context.getCurrentTemplateName() + " at (" +
                                getLine() + ", " + getColumn() + ')', e);
        }


        /*
         *    note - a blocked input is still a successful operation as this is
         *    expected behavior.
         */

        if ( blockinput )
            return true;

        else if ( resource == null )
            return false;

        writer.write((String)resource.getData());
        return true;
    }

    /**
     *  Puts a message to the render output stream if ERRORMSG_START / END
     *  are valid property strings.  Mainly used for end-user template
     *  debugging.
     */
    private void outputErrorToStream( Writer writer, String msg )
        throws IOException
    {        
        if ( outputMsgStart != null  && outputMsgEnd != null)
        {
            writer.write(outputMsgStart);
            writer.write(msg);
            writer.write(outputMsgEnd);
        }
        return;
    }
}
