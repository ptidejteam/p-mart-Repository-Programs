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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */


package org.apache.xml.serialize;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.Writer;

import org.w3c.dom.*;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;


/**
 * Implements a text serializer supporting both DOM and SAX
 * serializing. For usage instructions see {@link Serializer}.
 * <p>
 * If an output stream is used, the encoding is taken from the
 * output format (defaults to <tt>UTF-8</tt>). If a writer is
 * used, make sure the writer uses the same encoding (if applies)
 * as specified in the output format.
 * <p>
 * The serializer supports both DOM and SAX. DOM serializing is done
 * by calling {@link #serialize} and SAX serializing is done by firing
 * SAX events and using the serializer as a document handler.
 * <p>
 * If an I/O exception occurs while serializing, the serializer
 * will not throw an exception directly, but only throw it
 * at the end of serializing (either DOM or SAX's {@link
 * org.xml.sax.DocumentHandler#endDocument}.
 *
 *
 * @version
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see Serializer
 */
public final class TextSerializer
    extends BaseMarkupSerializer
{


    /**
     * Constructs a new serializer. The serializer cannot be used without
     * calling {@link #setOutputCharStream} or {@link #setOutputByteStream}
     * first.
     */
    public TextSerializer()
    {
        setOutputFormat( null );
    }


    /**
     * Constructs a new serializer. The serializer cannot be used without
     * calling {@link #setOutputCharStream} or {@link #setOutputByteStream}
     * first.
     */
    public TextSerializer( OutputFormat format )
    {
        setOutputFormat( format );
    }


    /**
     * Constructs a new serializer that writes to the specified writer
     * using the specified output format. If <tt>format</tt> is null,
     * will use a default output format.
     *
     * @param writer The writer to use
     * @param format The output format to use, null for the default
     */
    public TextSerializer( Writer writer, OutputFormat format )
    {
        setOutputFormat( format );
        setOutputCharStream( writer );
    }


    /**
     * Constructs a new serializer that writes to the specified output
     * stream using the specified output format. If <tt>format</tt>
     * is null, will use a default output format.
     *
     * @param output The output stream to use
     * @param format The output format to use, null for the default
     */
    public TextSerializer( OutputStream output, OutputFormat format )
    {
        setOutputFormat( format );
        try {
            setOutputByteStream( output );
        } catch ( UnsupportedEncodingException except ) {
            // Should never happend
        }
    }


    public void setOutputFormat( OutputFormat format )
    {
        if ( format == null )
            super.setOutputFormat( new OutputFormat( Method.TEXT, null, false ) );
        else
            super.setOutputFormat( format );
    }


    //-----------------------------------------//
    // SAX content handler serializing methods //
    //-----------------------------------------//


    public void startElement( String namespaceURI, String localName,
                              String rawName, Attributes attrs )
    {
        startElement( rawName == null ? localName : rawName, null );
    }


    public void endElement( String namespaceURI, String localName,
                            String rawName )
    {
        endElement( rawName == null ? localName : rawName );
    }


    //------------------------------------------//
    // SAX document handler serializing methods //
    //------------------------------000---------//

    
    public void startDocument()
    {
        // Nothing to do here. All the magic happens in startDocument(String)
    }
    
    
    public void startElement( String tagName, AttributeList attrs )
    {
        boolean      preserveSpace;
        ElementState state;
        
        state = getElementState();
        if ( state == null ) {
            // If this is the root element handle it differently.
            // If the first root element in the document, serialize
            // the document's DOCTYPE. Space preserving defaults
            // to that of the output format.
            if ( ! _started )
                startDocument( tagName );
            preserveSpace = _format.getPreserveSpace();
        } else {
            // For any other element, if first in parent, then
            // use the parnet's space preserving.
            preserveSpace = state.preserveSpace;
        }
        // Do not change the current element state yet.
        // This only happens in endElement().
        
        // Ignore all other attributes of the element, only printing
        // its contents.
        
        // Now it's time to enter a new element state
        // with the tag name and space preserving.
        // We still do not change the curent element state.
        state = enterElementState( null, null, tagName, preserveSpace );
    }
    
    
    public void endElement( String tagName )
    {
        ElementState state;
        
        // Works much like content() with additions for closing
        // an element. Note the different checks for the closed
        // element's state and the parent element's state.
        state = getElementState();
        // Leave the element state and update that of the parent
        // (if we're not root) to not empty and after element.
        state = leaveElementState();
        if ( state != null ) {
            state.afterElement = true;
            state.empty = false;
        } else {
            // [keith] If we're done printing the document but don't
            // get to call endDocument(), the buffer should be flushed.
            flush();
        }
    }


    public void processingInstruction( String target, String code )
    {
    }


    public void comment( String text )
    {
    }


    public void comment( char[] chars, int start, int length )
    {
    }


    public void characters( char[] chars, int start, int length )
    {
        characters( new String( chars, start, length ), false );
    }


    protected void characters( String text, boolean unescaped )
    {
        ElementState state;
        
        state = content();
        if ( state != null ) {
            state.doCData = state.inCData = false;
        }
        printText( text, true );
    }


    //------------------------------------------//
    // Generic node serializing methods methods //
    //------------------------------------------//


    /**
     * Called to serialize the document's DOCTYPE by the root element.
     * <p>
     * This method will check if it has not been called before ({@link #_started}),
     * will serialize the document type declaration, and will serialize all
     * pre-root comments and PIs that were accumulated in the document
     * (see {@link #serializePreRoot}). Pre-root will be serialized even if
     * this is not the first root element of the document.
     */
    protected void startDocument( String rootTagName )
    {
        // Required to stop processing the DTD, even though the DTD
        // is not printed.
        leaveDTD();
        
        _started = true;
        // Always serialize these, even if not te first root element.
        serializePreRoot();
    }


    /**
     * Called to serialize a DOM element. Equivalent to calling {@link
     * #startElement}, {@link #endElement} and serializing everything
     * inbetween, but better optimized.
     */
    protected void serializeElement( Element elem )
    {
        Node         child;
        ElementState state;
        boolean      preserveSpace;
        String       tagName;
        
        tagName = elem.getTagName();
        state = getElementState();
        if ( state == null ) {
            // If this is the root element handle it differently.
            // If the first root element in the document, serialize
            // the document's DOCTYPE. Space preserving defaults
            // to that of the output format.
            if ( ! _started )
                startDocument( tagName );
            preserveSpace = _format.getPreserveSpace();
        } else {
            // For any other element, if first in parent, then
            // use the parnet's space preserving.
            preserveSpace = state.preserveSpace;
        }
        // Do not change the current element state yet.
        // This only happens in endElement().
        
        // Ignore all other attributes of the element, only printing
        // its contents.
        
        // If element has children, then serialize them, otherwise
        // serialize en empty tag.
        if ( elem.hasChildNodes() ) {
            // Enter an element state, and serialize the children
            // one by one. Finally, end the element.
            state = enterElementState( null, null, tagName, preserveSpace );
            child = elem.getFirstChild();
            while ( child != null ) {
                serializeNode( child );
                child = child.getNextSibling();
            }
            endElement( tagName );
        } else {
            if ( state != null ) {
                // After element but parent element is no longer empty.
                state.afterElement = true;
                state.empty = false;
            }
        }
    }


    /**
     * Serialize the DOM node. This method is unique to the Text serializer.
     *
     * @param node The node to serialize
     */
    protected void serializeNode( Node node )
    {
        // Based on the node type call the suitable SAX handler.
        // Only comments entities and documents which are not
        // handled by SAX are serialized directly.
        switch ( node.getNodeType() ) {
        case Node.TEXT_NODE :
            characters( node.getNodeValue(), true );
            break;
            
        case Node.CDATA_SECTION_NODE :
            characters( node.getNodeValue(), true );
            break;
            
        case Node.COMMENT_NODE :
            break;
            
        case Node.ENTITY_REFERENCE_NODE :
            // Ignore.
            break;
            
        case Node.PROCESSING_INSTRUCTION_NODE :
            break;
            
        case Node.ELEMENT_NODE :
            serializeElement( (Element) node );
            break;
            
        case Node.DOCUMENT_NODE :
        case Node.DOCUMENT_FRAGMENT_NODE : {
            Node         child;
            
            // By definition this will happen if the node is a document,
            // document fragment, etc. Just serialize its contents. It will
            // work well for other nodes that we do not know how to serialize.
            child = node.getFirstChild();
            while ( child != null ) {
                serializeNode( child );
                child = child.getNextSibling();
            }
            break;
        }
        
        default:
            break;
        }
    }
    
    
    protected ElementState content()
    {
        ElementState state;
        
        state = getElementState();
        if ( state != null ) {
            // If this is the first content in the element,
            // change the state to not-empty.
            if ( state.empty ) {
                state.empty = false;
            }
            // Except for one content type, all of them
            // are not last element. That one content
            // type will take care of itself.
            state.afterElement = false;
        }
        return state;
    }
    
    
    protected String getEntityRef( char ch )
    {
        return null;
    }


}


