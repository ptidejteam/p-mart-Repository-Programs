// SAXNotRecognizedException.java - unrecognized feature or value.
// http://www.saxproject.org
// Written by David Megginson
// NO WARRANTY!  This class is in the Public Domain.
// $Id: SAXNotRecognizedException.java,v 1.1 2007/03/12 16:15:08 guehene Exp $

package org.xml.sax;


/**
 * Exception class for an unrecognized identifier.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * See <a href='http://www.saxproject.org'>http://www.saxproject.org</a>
 * for further information.
 * </blockquote>
 *
 * <p>An XMLReader will throw this exception when it finds an
 * unrecognized feature or property identifier; SAX applications and
 * extensions may use this class for other, similar purposes.</p>
 *
 * @since SAX 2.0
 * @author David Megginson
 * @version 2.0.1 (sax2r2)
 * @see org.xml.sax.SAXNotSupportedException
 */
public class SAXNotRecognizedException extends SAXException
{

    /**
     * Default constructor.
     */
    public SAXNotRecognizedException ()
    {
	super();
    }


    /**
     * Construct a new exception with the given message.
     *
     * @param message The text message of the exception.
     */
    public SAXNotRecognizedException (String message)
    {
	super(message);
    }

    // Added serialVersionUID to preserve binary compatibility 
    static final long serialVersionUID = 5440506620509557213L;
}

// end of SAXNotRecognizedException.java
