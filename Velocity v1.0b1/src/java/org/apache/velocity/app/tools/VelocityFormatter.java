package org.apache.velocity.app.tools;

/*
 * Copyright (c) 2001 The Java Apache Project.  All rights reserved.
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
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by the Java Apache
 *    Project for use in the Apache JServ servlet engine project
 *    <http://java.apache.org/>."
 *
 * 4. The names "Apache JServ", "Apache JServ Servlet Engine", "Turbine",
 *    "Apache Turbine", "Turbine Project", "Apache Turbine Project" and
 *    "Java Apache Project" must not be used to endorse or promote products
 *    derived from this software without prior written permission.
 *
 * 5. Products derived from this software may not be called "Apache JServ"
 *    nor may "Apache" nor "Apache JServ" appear in their names without
 *    prior written permission of the Java Apache Project.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the Java Apache
 *    Project for use in the Apache JServ servlet engine project
 *    <http://java.apache.org/>."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JAVA APACHE PROJECT "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JAVA APACHE PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Java Apache Group. For more information
 * on the Java Apache Project and the Apache JServ Servlet Engine project,
 * please see <http://java.apache.org/>.
 *
 */

// Java Core Classes
import java.util.*;
import java.text.*;
import java.lang.reflect.Array;

// Veclocity classes
import org.apache.velocity.*;
import org.apache.velocity.context.*;

/**
 * Formatting tool for inserting into the Velocity WebContext.  Can
 * format dates or lists of objects.
 *
 * <p>Here's an example of some uses:
 *
 * <code><pre>
 * $formatter.formatShortDate($object.Date)
 * $formatter.formatLongDate($db.getRecord(232).getDate())
 * $formatter.formatArray($array)
 * $formatter.limitLen(30, $object.Description)
 * </pre></code>
 * 
 * @author <a href="sean@somacity.com">Sean Legassick</a>
 * @version $Id: VelocityFormatter.java,v 1.2 2001/03/05 11:44:54 jvanzyl Exp $
 */
public class VelocityFormatter
{
    Context context = null;
    NumberFormat nf = NumberFormat.getInstance();

    /**
     * Constructor needs a backpointer to the context.
     *
     * @param context A Context.
     */
    public VelocityFormatter(Context context)
    {
        this.context = context;
    }

    /**
     * Formats a date in 'short' style.
     *
     * @param date A Date.
     * @return A String.
     */
    public String formatShortDate(Date date)
    {
        return DateFormat
            .getDateInstance(DateFormat.SHORT).format(date);
    }

    /**
     * Formats a date in 'long' style.
     *
     * @param date A Date.
     * @return A String.
     */
    public String formatLongDate(Date date)
    {
        return DateFormat
            .getDateInstance(DateFormat.LONG).format(date);
    }

    /**
     * Formats a date/time in 'short' style.
     *
     * @param date A Date.
     * @return A String.
     */
    public String formatShortDateTime(Date date)
    {
        return DateFormat
            .getDateTimeInstance(DateFormat.SHORT,
                                 DateFormat.SHORT).format(date);
    }

    /**
     * Formats a date/time in 'long' style.
     *
     * @param date A Date.
     * @return A String.
     */
    public String formatLongDateTime(Date date)
    {
        return DateFormat.getDateTimeInstance(
                DateFormat.LONG, DateFormat.LONG).format(date);
    }

    /**
     * Formats an array into the form "A, B and C".
     *
     * @param array An Object.
     * @return A String.
     */
    public String formatArray(Object array)
    {
        return formatArray(array, ", ", " and ");
    }

    /**
     * Formats an array into the form
     * "A&lt;delim&gt;B&lt;delim&gt;C".
     *
     * @param array An Object.
     * @param delim A String.
     * @return A String.
     */
    public String formatArray(Object array,
                              String delim)
    {
        return formatArray(array, delim, delim);
    }

    /**
     * Formats an array into the form
     * "A&lt;delim&gt;B&lt;finaldelim&gt;C".
     *
     * @param array An Object.
     * @param delim A String.
     * @param finalDelim A String.
     * @return A String.
     */
    public String formatArray(Object array,
                              String delim,
                              String finaldelim)
    {
        StringBuffer sb = new StringBuffer();
        int arrayLen = Array.getLength(array);
        for (int i = 0; i < arrayLen; i++)
        {
            // Use the Array.get method as this will automatically
            // wrap primitive types in a suitable Object-derived
            // wrapper if necessary.
            sb.append(Array.get(array, i).toString());
            if (i  < arrayLen - 2)
            {
                sb.append(delim);
            }
            else if (i < arrayLen - 1)
            {
                sb.append(finaldelim);
            }
        }
        return sb.toString();
    }

    /**
     * Formats a vector into the form "A, B and C".
     *
     * @param vector A Vector.
     * @return A String.
     */
    public String formatVector(Vector vector)
    {
        return formatVector(vector, ", ", " and ");
    }

    /**
     * Formats a vector into the form "A&lt;delim&gt;B&lt;delim&gt;C".
     *
     * @param vector A Vector.
     * @param delim A String.
     * @return A String.
     */
    public String formatVector(Vector vector,
                               String delim)
    {
        return formatVector(vector, delim, delim);
    }

    /**
     * Formats a vector into the form
     * "Adelim&gt;B&lt;finaldelim&gt;C".
     *
     * @param vector A Vector.
     * @param delim A String.
     * @param finalDelim A String.
     * @return A String.
     */
    public String formatVector(Vector vector,
                               String delim,
                               String finaldelim)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < vector.size(); i++)
        {
            sb.append(vector.elementAt(i).toString());
            if (i < vector.size() - 2)
            {
                sb.append(delim);
            }
            else if (i < vector.size() - 1)
            {
                sb.append(finaldelim);
            }
        }
        return sb.toString();
    }

    /**
     * Limits 'string' to 'maxlen' characters.  If the string gets
     * curtailed, "..." is appended to it.
     *
     * @param maxlen An int with the maximum length.
     * @param string A String.
     * @return A String.
     */
    public String limitLen(int maxlen,
                           String string)
    {
        return limitLen(maxlen, string, "...");
    }

    /**
     * Limits 'string' to 'maxlen' character.  If the string gets
     * curtailed, 'suffix' is appended to it.
     *
     * @param maxlen An int with the maximum length.
     * @param string A String.
     * @param suffix A String.
     * @return A String.
     */
    public String limitLen(int maxlen,
                           String string,
                           String suffix)
    {
        String ret = string;
        if (string.length() > maxlen)
        {
            ret = string.substring(0, maxlen - suffix.length()) + suffix;
        }
        return ret;
    }

    /**
     * Class that returns alternating values in a template.  It stores
     * a list of alternate Strings, whenever alternate() is called it
     * switches to the next in the list.  The current alternate is
     * retrieved through toString() - i.e. just by referencing the
     * object in a Velocity template.  For an example of usage see the
     * makeAlternator() method below.
     */
    public class VelocityAlternator
    {
        String[] alternates = null;
        int current = 0;

        /**
         * Constructor takes an array of Strings.
         *
         * @param alternates A String[].
         */
        public VelocityAlternator(String[] alternates)
        {
            this.alternates = alternates;
        }

        /**
         * Alternates to the next in the list.
         *
         * @return A String.
         */
        public String alternate()
        {
            current++;
            current %= alternates.length;
            return "";
        }

        /**
         * Returns the current alternate.
         *
         * @return A String.
         */
        public String toString()
        {
            return alternates[current];
        }
    }

    /**
     * Makes an alternator object that alternates between two values.
     *
     * <p>Example usage in a Velocity template:
     *
     * <code><pre>
     * &lt;table&gt;
     * $formatter.makeAlternator(rowcolor, "#c0c0c0", "#e0e0e0")
     * #foreach $item in $items
     * #begin
     * &lt;tr&gt;&lt;td bgcolor="$rowcolor"&gt;$item.Name&lt;/td&gt;&lt;/tr&gt;
     * $rowcolor.alternate()
     * #end
     * &lt;/table&gt;
     * </pre></code>
     *
     * @param name A String.
     * @param alt1 A String.
     * @param alt2 A String.
     * @return A String.
     */
    public String makeAlternator(String name,
                                 String alt1,
                                 String alt2)
    {
        String[] alternates = { alt1, alt2 };
        context.put(name, new VelocityAlternator(alternates));
        return "";
    }

    /**
     * Makes an alternator object that alternates between three
     * values.
     *
     * @param name A String.
     * @param alt1 A String.
     * @param alt2 A String.
     * @param alt3 A String.
     * @return A String.
     */
    public String makeAlternator(String name,
                                 String alt1,
                                 String alt2,
                                 String alt3)
    {
        String[] alternates = { alt1, alt2, alt3 };
        context.put(name, new VelocityAlternator(alternates));
        return "";
    }

    /**
     * Makes an alternator object that alternates between four values.
     *
     * @param name A String.
     * @param alt1 A String.
     * @param alt2 A String.
     * @param alt3 A String.
     * @param alt4 A String.
     * @return A String.
     */
    String makeAlternator(String name,
                          String alt1,
                          String alt2,
                          String alt3,
                          String alt4)
    {
        String[] alternates = { alt1, alt2, alt3, alt4 };
        context.put(name, new VelocityAlternator(alternates));
        return "";
    }

    /**
     * Returns a default value if the object passed is null.
     */
    public Object isNull(Object o, Object dflt)
    {
        if ( o == null )
        {
            return dflt;
        }
        else
        {
            return o;
        }
    }
}




