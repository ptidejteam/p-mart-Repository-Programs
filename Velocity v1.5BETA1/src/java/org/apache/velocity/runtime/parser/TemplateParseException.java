package org.apache.velocity.runtime.parser;

import org.apache.velocity.exception.ExtendedParseException;

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

/**
 * This is an extension of the ParseException, which also takes a
 * template name.
 *
 * @see org.apache.velocity.runtime.parser.ParseException
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id: TemplateParseException.java 440842 2006-09-06 19:39:15Z henning $
 */
public class TemplateParseException
        extends ParseException
        implements ExtendedParseException
{
    private static final long serialVersionUID = -3146323135623083918L;
    
    /**
     * This is the name of the template which contains the parsing error, or
     * null if not defined.
     */
    private final String templateName;

    /**
     * This constructor is used to add a template name
     * to info cribbed from a ParseException generated in the parser.
     */
    public TemplateParseException(Token currentTokenVal, int [][] expectedTokenSequencesVal, String [] tokenImageVal,
        String templateNameVal)
    {
        super(currentTokenVal, expectedTokenSequencesVal, tokenImageVal);
        this.templateName = templateNameVal;
    }

    /**
     * This constructor is used by the method "generateParseException"
     * in the generated parser.  Calling this constructor generates
     * a new object of this type with the fields "currentToken",
     * "expectedTokenSequences", and "tokenImage" set.  The boolean
     * flag "specialConstructor" is also set to true to indicate that
     * this constructor was used to create this object.
     * This constructor calls its super class with the empty string
     * to force the "toString" method of parent class "Throwable" to
     * print the error message in the form:
     *     ParseException: <result of getMessage>
     */
    public TemplateParseException(Token currentTokenVal, int [][] expectedTokenSequencesVal, String [] tokenImageVal)
    {
        super(currentTokenVal, expectedTokenSequencesVal, tokenImageVal);
        templateName = "*unset*";
    }

    /**
     * The following constructors are for use by you for whatever
     * purpose you can think of.  Constructing the exception in this
     * manner makes the exception behave in the normal way - i.e., as
     * documented in the class "Throwable".  The fields "errorToken",
     * "expectedTokenSequences", and "tokenImage" do not contain
     * relevant information.  The JavaCC generated code does not use
     * these constructors.
     */
    public TemplateParseException()
    {
        super();
        templateName = "*unset*";
    }

    /**
     * Creates a new TemplateParseException object.
     *
     * @param message TODO: DOCUMENT ME!
     */
    public TemplateParseException(String message)
    {
        super(message);
        templateName = "*unset*";
    }

    /**
     * returns the Template name where this exception occured.
     */
    public String getTemplateName()
    {
        return templateName;
    }

    /**
     * returns the line number where this exception occured.
     */
    public int getLineNumber()
    {
        if ((currentToken != null) && (currentToken.next != null))
        {
            return currentToken.next.beginLine;
        }
        else
        {
            return -1;
        }
    }

    /**
     * returns the column number where this exception occured.
     */
    public int getColumnNumber()
    {
        if ((currentToken != null) && (currentToken.next != null))
        {
            return currentToken.next.beginColumn;
        }
        else
        {
            return -1;
        }
    }

    /**
     * This method has the standard behavior when this object has been
     * created using the standard constructors.  Otherwise, it uses
     * "currentToken" and "expectedTokenSequences" to generate a parse
     * error message and returns it.  If this object has been created
     * due to a parse error, and you do not catch it (it gets thrown
     * from the parser), then this method is called during the printing
     * of the final stack trace, and hence the correct error message
     * gets displayed.
     */
    public String getMessage()
    {
        if (!specialConstructor)
        {
            StringBuffer sb = new StringBuffer(super.getMessage());
            appendTemplateInfo(sb);
            return sb.toString();
        }

        int maxSize = 0;

        StringBuffer expected = new StringBuffer();

        for (int i = 0; i < expectedTokenSequences.length; i++)
        {
            if (maxSize < expectedTokenSequences[i].length)
            {
                maxSize = expectedTokenSequences[i].length;
            }

            for (int j = 0; j < expectedTokenSequences[i].length; j++)
            {
                expected.append(tokenImage[expectedTokenSequences[i][j]]).append(" ");
            }

            if (expectedTokenSequences[i][expectedTokenSequences[i].length - 1] != 0)
            {
                expected.append("...");
            }

            expected.append(eol).append("    ");
        }

        StringBuffer retval = new StringBuffer("Encountered \"");
        Token tok = currentToken.next;

        for (int i = 0; i < maxSize; i++)
        {
            if (i != 0)
            {
                retval.append(" ");
            }

            if (tok.kind == 0)
            {
                retval.append(tokenImage[0]);
                break;
            }

            retval.append(add_escapes(tok.image));
            tok = tok.next;
        }

        retval.append("\"");
        appendTemplateInfo(retval);

        if (expectedTokenSequences.length == 1)
        {
            retval.append("Was expecting:").append(eol).append("    ");
        }
        else
        {
            retval.append("Was expecting one of:").append(eol).append("    ");
        }

        // avoid JDK 1.3 StringBuffer.append(Object o) vs 1.4 StringBuffer.append(StringBuffer sb) gotcha.
        retval.append(expected.toString());
        return retval.toString();
    }

    protected void appendTemplateInfo(final StringBuffer sb)
    {
        sb.append(" at line ").append(getLineNumber())
          .append(", column ").append(getColumnNumber());

        if (getTemplateName() != null)
        {
            sb.append(" of ").append(getTemplateName());
        }
        else
        {
            sb.append(".");
        }
        sb.append(eol);
    }
}
