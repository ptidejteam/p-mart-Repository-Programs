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
package org.apache.xalan.xpath.xml;

import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import java.util.Hashtable;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xalan.xpath.res.XPATHErrorResources;

/**
 * <meta name="usage" content="general"/>
 * FormatterToXML formats SAX-style events into XML.
 * Warning: this class will be replaced by the Xerces Serializer classes.
 */
public class FormatterToXML implements DocumentHandler, LexicalHandler, RawCharacterHandler
{
  /** 
   * The writer where the XML will be written.
   */
  protected  Writer m_writer      =   null;
  
  boolean m_shouldFlush = true;
  
  protected OutputStream m_outputStream = null;
  
  private boolean m_bytesEqualChars = false;

  /**
   * Return the Writer.
   */
  public java.io.Writer getWriter()
  {
    return m_writer;
  }

  /**
   * If true, cdata sections are simply stripped of their 
   * CDATA brackets, without escaping.
   */
  public boolean m_stripCData = false;

  /**
   * If true, characters in cdata sections are 
   * escaped, instead of being writted out as 
   * cdata sections.
   */
  public boolean m_escapeCData = false;

  /**
   * The character encoding.  Must match the encoding used for the printWriter.
   */
  protected  String m_encoding    =   null;
  
  /**
   * Assume java encoding names are the same as the ISO encoding names if this is true.
   */
  static boolean javaEncodingIsISO = false; 
  
  /**
   * Tells if we should write the XML declaration.
   */
  public boolean m_shouldNotWriteXMLHeader = false;
  
  /**
   * Tells the XML version, for writing out to the XML decl.
   */
  public String m_version = null;
  
  /**
   * A stack of Boolean objects that tell if the given element 
   * has children.
   */
  protected  BoolStack m_elemStack = new BoolStack();
  
  /**
   * Use the system line seperator to write line breaks.
   */
  protected final String m_lineSep = System.getProperty("line.separator");
  
  /**
   * The length of the line seperator, since the write is done 
   * one character at a time.
   */
  protected final int m_lineSepLen = m_lineSep.length();
  
  /**
   * Output a system-dependent line break.
   */
  protected final void outputLineSep()
    throws SAXException
  {
    for(int z = 0; z < m_lineSepLen; z++)
    {
      accum(m_lineSep.charAt(z));
    }
  }
  
  /**
   * State flag to tell if preservation of whitespace 
   * is important.
   */
  protected boolean m_ispreserve    =   false;
  
  /**
   * Stack to keep track of whether or not we need to 
   * preserve whitespace.
   */
  protected BoolStack   m_preserves     =   new BoolStack();
  
  /**
   * State flag that tells if the previous node processed 
   * was text, so we can tell if we should preserve whitespace.
   */
  protected boolean m_isprevtext    =   false;
  
  /**
   * Flag to tell if indenting (pretty-printing) is on.
   */
  protected boolean m_doIndent =   false;
  
  /**
   * Flag to keep track of the indent amount.
   */
  protected int     m_currentIndent =   0;
  
  /**
   * Amount to indent.
   */
  public int     indent        =   0; 
  
  /**
   * Current level of indent.
   */
  protected   int     level   =   0;
  
  /**
   * Flag to signal that a newline should be added.
   */
  boolean m_startNewLine;
  
  /**
   * Flag to tell that we need to add the doctype decl, 
   * which we can't do until the first element is 
   * encountered.
   */
  boolean m_needToOutputDocTypeDecl = true;
  
  /**
   * Tell if the next text should be raw.
   */
  boolean m_nextIsRaw = false;
  
  /**
   * The System ID for the doc type.
   */
  String m_doctypeSystem;
  
  /**
   * The public ID for the doc type.
   */
  String m_doctypePublic;
  
  /**
   * The standalone value for the doctype.
   */
  boolean m_standalone;
  
  /**
   * The mediatype.  Not used right now.
   */
  String m_mediatype;

  /**
   * Tells if we're in an EntityRef event.
   */
  protected boolean m_inEntityRef = false;

  
  /**
   * These are characters that will be escaped in the output.
   */
  // public char[] m_attrSpecialChars = {'<', '>', '&', '\"', '\r', '\n'};
  public char[] m_attrSpecialChars = {'<', '>', '&', '\"'};
  
  static final int SPECIALSSIZE = 256;
  
  public char[] m_attrCharsMap = new char[SPECIALSSIZE];
  
  public char[] m_charsMap = new char[SPECIALSSIZE];
  
  /**
   * Set the attribute characters what will require special mapping.
   */
  protected void initAttrCharsMap()
  {
    int n = (m_maxCharacter > SPECIALSSIZE) ? SPECIALSSIZE : m_maxCharacter;
    for(int i = 0; i < n; i++)
    {
      m_attrCharsMap[i] = '\0';
    }
    int nSpecials = m_attrSpecialChars.length;
    for(int i = 0; i < nSpecials; i++)
    {
      m_attrCharsMap[(int)m_attrSpecialChars[i]] = 'S';
    }
    m_attrCharsMap[0x0A] = 'S';
    m_attrCharsMap[0x0D] = 'S';
  }

  /**
   * Set the characters what will require special mapping.
   */
  protected void initCharsMap()
  {
    initAttrCharsMap();
    int n = (m_maxCharacter > SPECIALSSIZE) ? SPECIALSSIZE : m_maxCharacter;
    for(int i = 0; i < n; i++)
    {
      m_charsMap[i] = '\0';
    }
    m_charsMap[(int)'\n'] = 'S';
    m_charsMap[(int)'<'] = 'S';
    m_charsMap[(int)'>'] = 'S';
    m_charsMap[(int)'&'] = 'S';
    for(int i = 0; i < 20; i++)
    {
      m_charsMap[i] = 'S';
    }
    m_charsMap[0x0A] = 'S';
    m_charsMap[0x0D] = 'S';
    m_charsMap[9] = '\0';
    for(int i = m_maxCharacter; i < SPECIALSSIZE; i++)
    {
      m_charsMap[i] = 'S';
    }
  }
  
  /**
   * Flag to quickly tell if the encoding is UTF8.
   */
  boolean m_isUTF8;
  
  /**
   * The maximum character size before we have to resort 
   * to escaping.
   */
  char m_maxCharacter = '\u007F';
  
  /**
   * Add space before '/>' for XHTML.
   */
  public boolean     m_spaceBeforeClose        =   false; 
  
  static final String DEFAULT_MIME_ENCODING = "UTF-8";
  
  /**
   * Default constructor.
   */
  public FormatterToXML() 
  {
    initEncodings();
  }

  /**
   * Constructor using a writer.
   * @param writer        The character output stream to use.
   */
  public FormatterToXML(Writer writer) 
  {
    m_shouldFlush = false;
    m_writer = writer;
    initEncodings();
  }
  
  /**
   * Constructor using an output stream, and a simple OutputFormat.
   * @param writer        The character output stream to use.
   */
  public FormatterToXML(java.io.OutputStream os) 
    throws UnsupportedEncodingException
  {
    initEncodings();
    this.init(os, new org.apache.xml.serialize.OutputFormat( "xml", "UTF-8", false ));
  }
  
  /**
   * Constructor using a writer.
   * @param writer        The character output stream to use.
   */
  public FormatterToXML(FormatterToXML xmlListener) 
  {
    m_writer = xmlListener.m_writer;
    m_outputStream = xmlListener.m_outputStream;
    m_bytesEqualChars = xmlListener.m_bytesEqualChars;
    m_stripCData = xmlListener.m_stripCData;
    m_escapeCData = xmlListener.m_escapeCData;
    m_encoding = xmlListener.m_encoding;
    javaEncodingIsISO = xmlListener.javaEncodingIsISO;
    m_shouldNotWriteXMLHeader = xmlListener.m_shouldNotWriteXMLHeader;
    m_shouldNotWriteXMLHeader = xmlListener.m_shouldNotWriteXMLHeader;
    m_elemStack = xmlListener.m_elemStack;
    // m_lineSep = xmlListener.m_lineSep;
    // m_lineSepLen = xmlListener.m_lineSepLen;
    m_ispreserve = xmlListener.m_ispreserve;
    m_preserves = xmlListener.m_preserves;
    m_isprevtext = xmlListener.m_isprevtext;
    m_doIndent = xmlListener.m_doIndent;
    m_currentIndent = xmlListener.m_currentIndent;
    indent = xmlListener.indent;
    level = xmlListener.level;
    m_startNewLine = xmlListener.m_startNewLine;
    m_needToOutputDocTypeDecl = xmlListener.m_needToOutputDocTypeDecl;
    m_nextIsRaw = xmlListener.m_nextIsRaw;
    m_doctypeSystem = xmlListener.m_doctypeSystem;
    m_doctypePublic = xmlListener.m_doctypePublic;
    m_standalone = xmlListener.m_standalone;
    m_mediatype = xmlListener.m_mediatype;
    m_attrSpecialChars = xmlListener.m_attrSpecialChars;
    m_attrCharsMap = xmlListener.m_attrCharsMap;
    m_charsMap = xmlListener.m_charsMap;
    m_maxCharacter = xmlListener.m_maxCharacter;
    m_spaceBeforeClose = xmlListener.m_spaceBeforeClose;
    m_inCData = xmlListener.m_inCData;
    m_charBuf = xmlListener.m_charBuf;
    m_byteBuf = xmlListener.m_byteBuf;
    // m_pos = xmlListener.m_pos;
    m_pos = 0;
    initCharsMap();
  }
  
  /**
   * Initialize the serializer with the specified writer and output format.
   * Must be called before calling any of the serialize methods.
   *
   * @param writer The writer to use
   * @param format The output format
   */
  public synchronized void init( Writer writer, OutputFormat format )
  {
    init( writer, format, false );
  }

  
  /**
   * Initialize the serializer with the specified writer and output format.
   * Must be called before calling any of the serialize methods.
   *
   * @param writer The writer to use
   * @param format The output format
   */
  private synchronized void init( Writer writer, OutputFormat format, boolean shouldFlush )
  {
    this.m_shouldFlush = shouldFlush;
    this.m_writer   =   writer;
    // This is to get around differences between Xalan and Xerces.
    // Xalan uses -1 as default for no indenting, Xerces uses 0.
    // So we just adjust the indent value here because we bumped it
    // up previously ( in StylesheetRoot);
    this.indent = format.getIndent() - 1;
    this.m_doIndent = format.getIndenting();
    this.m_shouldNotWriteXMLHeader = format.getOmitXMLDeclaration();
    this.m_doctypeSystem = format.getDoctypeSystem();
    this.m_doctypePublic = format.getDoctypePublic();
    this.m_standalone = format.getStandalone();
    this.m_mediatype = format.getMediaType();
    
    if(null != m_doctypePublic)
    {
      if(m_doctypePublic.startsWith("-//W3C//DTD XHTML"))
        m_spaceBeforeClose = true;
    }
    // Determine the last printable character based on the output format
    m_maxCharacter = format.getLastPrintable();
    initCharsMap();
    
    if(null == this.m_encoding)
      this.m_encoding = getMimeEncoding(format.getEncoding());
    
    m_isUTF8 = this.m_encoding.equals(DEFAULT_MIME_ENCODING);
    
    Object maxCharObj = s_revsize.get(this.m_encoding.toUpperCase());
    if(null != maxCharObj)
    {
      Character maxChar = (Character)maxCharObj;
      m_maxCharacter = maxChar.charValue();
      initCharsMap();
    }

  }


  /**
   * Initialize the serializer with the specified output stream and output format.
   * Must be called before calling any of the serialize methods.
   *
   * @param output The output stream to use
   * @param format The output format
   * @throws UnsupportedEncodingException The encoding specified
   *   in the output format is not supported
   */
  public synchronized void init( OutputStream output, OutputFormat format )
    throws UnsupportedEncodingException
  {    
    this.m_encoding = getMimeEncoding(format.getEncoding());
    
    if(this.m_encoding.equals("WINDOWS-1250") ||
       this.m_encoding.equals("US-ASCII") ||
       this.m_encoding.equals("ASCII"))
    {
      m_bytesEqualChars = true;
      m_outputStream = output;
      init( (Writer)null, format, true );
    }
    else
    {
      String javaEncoding = this.convertMime2JavaEncoding(this.m_encoding);
      OutputStreamWriter osw = null;
      try
      {
        osw = new OutputStreamWriter(output, javaEncoding);
      }
      catch(Exception e)
      {
        try
        {
          if(javaEncoding.startsWith("ISO"))
          {
            javaEncoding = javaEncoding.substring(3);
            try
            {
              osw = new OutputStreamWriter(output, javaEncoding);
            }
            catch(Exception e2)
            {
              osw = new OutputStreamWriter(output, m_encoding);
            }
          }
          else
          {
            osw = new OutputStreamWriter(output, m_encoding);
          }
        }
        catch(Exception e3)
        {
          System.out.print("Java VM does not support encoding: "+m_encoding);
          if(null != javaEncoding)
            System.out.println(" or "+javaEncoding);
          else
            System.out.println();
          osw = new OutputStreamWriter(output);
        }
      }
      init( osw, format, true );
    }
  }
  
  /**
   * Receive an object for locating the origin of SAX document events.
   *
   * @param locator An object that can return the location of
   *                any SAX document event.
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator (Locator locator)
  {
    // I don't do anything with this yet.
  }
  
  /**
   * Output the doc type declaration.
   */
  void outputDocTypeDecl(String name)
    throws SAXException
  {
      accum("<!DOCTYPE ");
      accum(name);
      
      if(null != m_doctypePublic)
      {
        accum(" PUBLIC \"");
        accum(m_doctypePublic);
        accum("\"");
      }
      if(null == m_doctypePublic)
        accum(" SYSTEM \"");
      else
        accum(" \"");
      accum(m_doctypeSystem);
      accum("\">");
      outputLineSep();
  }
  
  /**
   * Receive notification of the beginning of a document.
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void startDocument ()
    throws SAXException
  {
    if(m_inEntityRef)
      return;

    m_needToOutputDocTypeDecl = true;
    m_startNewLine = false;
    
    if(m_shouldNotWriteXMLHeader == false)
    {
      String encoding = getMimeEncoding(this.m_encoding);
      
      String version = (null == m_version) ? "1.0" : m_version;

      accum("<?xml version=\""+version+"\" encoding=\""+
            encoding + "\"?>");
      outputLineSep();
    }      
  }

  /**
   * Receive notification of the end of a document.
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void endDocument ()
    throws SAXException
  {
    if(m_doIndent && !m_isprevtext)
    {
      outputLineSep();
    }
    flush();
    flushWriter();
  }
  
  /**
   * Report the start of DTD declarations, if any.
   *
   * Any declarations are assumed to be in the internal subset
   * unless otherwise indicated.
   *
   * @param name The document type name.
   * @param publicId The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   * @exception SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD (String name, String publicId,
                                 String systemId)
    throws SAXException
  {
    // Do nothing for now.
  }

  /**
   * Report the end of DTD declarations.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD ()
    throws SAXException
  {
    // Do nothing for now.
  }

  /**
   * Receive notification of the beginning of an element.
   *
   * @param name The element type name.
   * @param atts The attributes attached to the element, if any.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #endElement
   * @see org.xml.sax.AttributeList 
   */
  public void startElement (String name, AttributeList atts)
    throws SAXException
  {
    if(m_inEntityRef)
      return;
    if((true == m_needToOutputDocTypeDecl) && (null != m_doctypeSystem))
    {
      outputDocTypeDecl(name);
    }  
    m_needToOutputDocTypeDecl = false;

    writeParentTagEnd();
    m_ispreserve = false;
    //  System.out.println(name+": m_doIndent = "+m_doIndent+", m_ispreserve = "+m_ispreserve+", m_isprevtext = "+m_isprevtext);
    if (shouldIndent() && m_startNewLine)
    {
      indent(m_currentIndent);
    }
    m_startNewLine = true;

    accum('<');
    accum(name);
        
    int nAttrs = atts.getLength();
    for (int i = 0;  i < nAttrs ;  i++)
    {
      processAttribute(atts.getName(i), atts.getValue(i));
    }
    // Flag the current element as not yet having any children.
    openElementForChildren();
    
    m_currentIndent += this.indent;
    
    m_isprevtext = false;
  }
  
  /**
   * Check to see if a parent's ">" has been written, and, if 
   * it has not, write it.
   */
  protected void writeParentTagEnd()
    throws SAXException
  {
    if(!m_elemStack.isEmpty())
    {
      // See if the parent element has already been flagged as having children.
      if((false == m_elemStack.peek()))
      {
        accum('>');
        m_isprevtext = false;
        m_elemStack.pop();
        m_elemStack.push(true);
        m_preserves.push(m_ispreserve);
      }
    }
  }
  
  /**
   * Flag the current element as not yet having any 
   * children.
   */
  protected void openElementForChildren()
  {
    // Flag the current element as not yet having any children.
    m_elemStack.push(false);
  }
  
  /**
   * Tell if child nodes have been added to the current
   * element.  Must be called in balance with openElementForChildren().
   */
  protected boolean childNodesWereAdded()
  { 
    return m_elemStack.isEmpty() ? 
           false : m_elemStack.pop();
  }

  /**
   * Receive notification of the end of an element.
   *
   * @param name The element type name
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void endElement (String name)
    throws SAXException
  {
    m_currentIndent -= this.indent;
    boolean hasChildNodes = childNodesWereAdded();
    if (hasChildNodes) 
    {
      if (shouldIndent()) 
        indent(m_currentIndent);
      accum('<');
      accum('/');
      accum(name);
      accum('>');
    }
    else
    {
      if(m_spaceBeforeClose)
        accum(" />");
      else
        accum("/>");
    }
    if (hasChildNodes) 
    {
      m_ispreserve = m_preserves.isEmpty() 
                     ? false : m_preserves.pop();
    }
    m_isprevtext = false;
  }

  /**
   * Process an attribute.
   * @param   name   The name of the attribute.
   * @param   value   The value of the attribute.
   */
  protected void processAttribute(String name, String value) 
    throws SAXException
  {
    accum(' ');
    accum(name);
    accum("=\"");
    writeAttrString(value, this.m_encoding);
    accum('\"');
  }

  /**
   * Receive notification of a processing instruction.
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *        none was supplied.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void processingInstruction (String target, String data)
    throws SAXException
  {
    if(m_inEntityRef)
      return;

    // Use a fairly nasty hack to tell if the next node is supposed to be 
    // unescaped text.
    if(target.equals("xslt-next-is-raw") && data.equals("formatter-to-dom"))
    {
      m_nextIsRaw = true;
    }
    else
    {
      writeParentTagEnd();
      if (shouldIndent())  
        indent(m_currentIndent);
      accum('<');
      accum('?');
      accum(target);
      if (data.length() > 0 && !Character.isSpaceChar(data.charAt(0)))
        accum(' ');
      accum(data);
      accum('?');
      accum('>');
      m_startNewLine = true;
    }
  }
  
  /**
   * Report an XML comment anywhere in the document.
   *
   * This callback will be used for comments inside or outside the
   * document element, including comments in the external DTD
   * subset (if read).
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   * @exception SAXException The application may raise an exception.
   */
  public void comment (char ch[], int start, int length)  
    throws SAXException
  {
    if(m_inEntityRef)
      return;

    writeParentTagEnd();
    if (shouldIndent())  
      indent(m_currentIndent);
    accum("<!--");
    accum(ch, start, length);
    accum("-->");
    m_startNewLine = true;
  }
  
  protected boolean m_inCData = false;
  
  /**
   * Report the start of a CDATA section.
   *
   * @exception SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA ()
    throws SAXException
  {
    m_inCData = true;
  }

  /**
   * Report the end of a CDATA section.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA ()
    throws SAXException
  {
    m_inCData = false;
  }

  /**
   * Receive notification of cdata.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity, so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Note that some parsers will report whitespace using the
   * ignorableWhitespace() method rather than this one (validating
   * parsers must do so).</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace 
   * @see org.xml.sax.Locator
   */
  public void cdata (char ch[], int start, int length)
    throws SAXException
  {
    try
    {
      if(m_nextIsRaw)
      {
        m_nextIsRaw = false;
        charactersRaw (ch, start, length);
        return;
      }
      if(m_escapeCData) // Should normally always be false.
      {
        characters(ch, start, length);
      }
      else
      {
        writeParentTagEnd();
        m_ispreserve = true;
        if (shouldIndent())
          indent(m_currentIndent);
        if(!m_stripCData)
        {
          if(((length >= 1) && (ch[start] <= m_maxCharacter)))
          {
            accum("<![CDATA[");
          }
        }
        // accum(ch, start, length);
        writeNormalizedChars(ch, start, length, !m_stripCData);
        if(!m_stripCData)
        {
          if(((length >= 1) && (ch[(start+length)-1] <= m_maxCharacter)))
          {
            accum("]]>");
          }
        }
      }
    }
    catch(IOException ioe) 
    { 
      throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_OIERROR, null),ioe); //"IO error", ioe);
    }
  }
  
  static final int MAXCHARBUF = (8*1024);
  protected char[] m_charBuf = new char[MAXCHARBUF];
  protected byte[] m_byteBuf = new byte[MAXCHARBUF];
  protected int m_pos = 0;
  
  /**
   * Append a byte to the buffer.
   */
  protected final void accum(byte b)
    throws SAXException
  {
    if(m_bytesEqualChars)
    {
      m_byteBuf[m_pos++] = b;
      if(m_pos >= MAXCHARBUF)
        flushBytes();
    }
    else
    {
      m_charBuf[m_pos++] = (char)b;
      if(m_pos >= MAXCHARBUF)
        flushChars();
    }
  }

  /**
   * Append a character to the buffer.
   */
  protected final void accum(char b)
    throws SAXException
  {
    if(m_bytesEqualChars)
    {
      m_byteBuf[m_pos++] = (byte)b;
      if(m_pos >= MAXCHARBUF)
        flushBytes();
    }
    else
    {
      m_charBuf[m_pos++] = b;
      if(m_pos >= MAXCHARBUF)
        flushChars();
    }
  }
  
  /**
   * Append a character to the buffer.
   */
  protected final void accum(char chars[], int start, int length)
    throws SAXException
  {
    int n = start+length;
    if(m_bytesEqualChars)
    {
      for(int i = start; i < n; i++)
      {
        m_byteBuf[m_pos++] = (byte)chars[i];
        if(m_pos >= MAXCHARBUF)
          flushBytes();
      }
    }
    else
    {
      for(int i = start; i < n; i++)
      {
        m_charBuf[m_pos++] = chars[i];
        if(m_pos >= MAXCHARBUF)
          flushChars();
      }
    }
  }

  /**
   * Append a character to the buffer.
   */
  protected final void accum(String s)
    throws SAXException
  {
    int n = s.length();
    
    if(m_bytesEqualChars)
    {
      char[] chars = s.toCharArray();
      for(int i = 0; i < n; i++)
      {
        m_byteBuf[m_pos++] = (byte)chars[i];;
        if(m_pos >= MAXCHARBUF)
          flushBytes();
      }
    }
    else
    {
      for(int i = 0; i < n; i++)
      {
        m_charBuf[m_pos++] = s.charAt(i);;
        if(m_pos >= MAXCHARBUF)
          flushChars();
      }
    }
  }

  private final void flushBytes()
    throws SAXException
  {
    try
    {
      this.m_outputStream.write(m_byteBuf, 0, m_pos);
      m_pos = 0;
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }
  
  /**
   * Flush the formatter's result stream.
   */
  public final void flushWriter()
    throws SAXException
  {
    if(m_shouldFlush && (null != this.m_writer))
    {
      try
      {
        this.m_writer.flush(); 
      }
      catch(IOException ioe)
      {
        throw new SAXException(ioe);
      }
    }
  }

  private final void flushChars()
    throws SAXException
  {
    try
    {
      this.m_writer.write(m_charBuf, 0, m_pos);
      m_pos = 0;
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }
  
  public final void flush()
    throws SAXException
  {
    if(m_bytesEqualChars)
    {
      flushBytes();
    }
    else
    {
      flushChars();
    }
  }

  /**
   * Receive notification of character data.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity, so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Note that some parsers will report whitespace using the
   * ignorableWhitespace() method rather than this one (validating
   * parsers must do so).</p>
   *
   * @param chars The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace 
   * @see org.xml.sax.Locator
   */
  public void characters (char chars[], int start, int length)
    throws SAXException
  {
    if(m_inEntityRef)
      return;

    if(0 == length)
      return;
    
    if(m_inCData)
    {
      cdata (chars, start, length);
      return;
    }
    
    if(m_nextIsRaw)
    {
      m_nextIsRaw = false;
      charactersRaw (chars, start, length);
      return;
    }
    
    writeParentTagEnd();
    m_ispreserve = true;

    int pos = 0;
    int end = start+length;
    for (int i = start;  i < end;  i ++) 
    {
      char ch = chars[i];
      if((ch < SPECIALSSIZE) && (m_charsMap[ch] != 'S'))
      {
        accum(ch);
      }
      else
        accumDefaultEscape(ch, i, chars, end, false);
    }
    
    m_isprevtext = true;
  }
  
  /**
   * If available, when the disable-output-escaping attribute is used, 
   * output raw text without escaping.
   */
  public void charactersRaw (char ch[], int start, int length)
    throws SAXException
  {
    if(m_inEntityRef)
      return;

    writeParentTagEnd();
    m_ispreserve = true;
    accum(ch, start, length);
  }
  
  /**
   * Normalize the characters, but don't escape.
   */
  void writeNormalizedChars(char ch[], int start, int length, boolean isCData)
    throws IOException, SAXException
  {
    int end = start+length;
    for(int i = start; i < end; i++)
    {
      char c = ch[i];
      if ((0x0D == c) && ((i+1) < end) && (0x0A==ch[i+1])) 
      {
        outputLineSep();
        i++;
      }
      else if ((0x0A == c) && ((i+1) < end) && (0x0D==ch[i+1])) 
      {
        outputLineSep();
        i++;
      }
      else if('\n' == c)
      {
        outputLineSep();
      }
      else if(isCData && (c > m_maxCharacter))
      {
        if(i != 0)
          accum("]]>");
        // This needs to go into a function... 
        if (0xd800 <= ((int)c) && ((int)c) < 0xdc00) 
        {
          // UTF-16 surrogate
          int next;
          if (i+1 >= end) 
          {
            throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INVALID_UTF16_SURROGATE, new Object[]{Integer.toHexString((int)c)})); //"Invalid UTF-16 surrogate detected: "
              //+Integer.toHexString((int)c)+ " ?");
          }
          else 
          {
            next = ch[++i];
            if (!(0xdc00 <= next && next < 0xe000))
              throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INVALID_UTF16_SURROGATE, new Object[]{Integer.toHexString((int)c)+" "+Integer.toHexString(next)})); //"Invalid UTF-16 surrogate detected: "
                //+Integer.toHexString((int)c)+" "+Integer.toHexString(next));
            next = ((c-0xd800)<<10)+next-0xdc00+0x00010000;
          }
          accum('&');
          accum('#');
          accum('x');
          accum(Integer.toHexString(next));
          accum(';');
        }
        else
        {
          accum("&#");
          String intStr = Integer.toString((int)c);
          accum(intStr);
          accum(';');
        }
        if((i != 0) && (i < (end-1)))
          accum("<![CDATA[");
      }
      else if(isCData && ((i < (end-2)) && (']' == c) && 
                          (']' == ch[i+1]) && ('>' == ch[i+2])))
      {
        accum("]]]]><![CDATA[>");
        i+=2;
      }
      else
      {
        if(c <= m_maxCharacter)
        {
          accum(c);
        }
        // This needs to go into a function... 
        else if (0xd800 <= ((int)c) && ((int)c) < 0xdc00) 
        {
          // UTF-16 surrogate
          int next;
          if (i+1 >= end) 
          {
            throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INVALID_UTF16_SURROGATE, new Object[]{Integer.toHexString((int)c)})); //"Invalid UTF-16 surrogate detected: "
              //+Integer.toHexString((int)c)+ " ?");
          }
          else 
          {
            next = ch[++i];
            if (!(0xdc00 <= next && next < 0xe000))
              throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INVALID_UTF16_SURROGATE, new Object[]{Integer.toHexString((int)c)+" "+Integer.toHexString(next)})); //"Invalid UTF-16 surrogate detected: "
                //+Integer.toHexString((int)c)+" "+Integer.toHexString(next));
            next = ((c-0xd800)<<10)+next-0xdc00+0x00010000;
          }
          accum("&#x");
          accum(Integer.toHexString(next));
          accum(";");
        }
        else
        {
          accum("&#");
          String intStr = Integer.toString((int)c);
          accum(intStr);
          accum(';');
        }
      }
    }
  }

  
  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * Not sure how to get this invoked quite yet.
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #characters
   */
  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException
  {
    if(0 == length)
      return;
    characters (ch, start, length);
  }

  /**
   * Report the beginning of an entity.
   *
   * The start and end of the document entity are not reported.
   * The start and end of the external DTD subset are reported
   * using the pseudo-name "[dtd]".  All other events must be
   * properly nested within start/end entity events.
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @exception SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.misc.DeclHandler#internalEntityDecl
   * @see org.xml.sax.misc.DeclHandler#externalEntityDecl
   */
  public void startEntity (String name)
    throws SAXException
  {
    entityReference(name);
    m_inEntityRef = true;
  }

  /**
   * Report the end of an entity.
   *
   * @param name The name of the entity that is ending.
   * @exception SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity (String name)
    throws SAXException
  {
    m_inEntityRef = false;
  }

  /**
   * Receive notivication of a entityReference.
   */
  public void entityReference(String name)
    throws SAXException
  {
    writeParentTagEnd();
    if (shouldIndent())  
      indent(m_currentIndent);
    accum("&");
    accum(name);
    accum(";");
  }
  
  /**
   * Handle one of the default entities, return false if it 
   * is not a default entity.
   */
  final boolean accumDefaultEntity(char ch, int i, char[] chars, int len, boolean escLF)
    throws SAXException
  {
    if (!escLF && (0x0D == ch) && ((i+1) < len) && (0x0A==chars[i+1])) 
    {
      outputLineSep();
      i++;
    }
    else if (!escLF && (0x0A == ch) && ((i+1) < len) && (0x0D==chars[i+1])) 
    {
      outputLineSep();
      i++;
    }
    else if (!escLF && 0x0D == ch) 
    {
      outputLineSep();
      i++;
    }
    else if (!escLF && '\n' == ch) 
    {
      outputLineSep();
    }
    else if ('<' == ch) 
    {
      accum('&');
      accum('l');
      accum('t');
      accum(';');
    }
    else if ('>' == ch) 
    {
      accum('&');
      accum('g');
      accum('t');
      accum(';');
    }
    else if ('&' == ch) 
    {
      accum('&');
      accum('a');
      accum('m');
      accum('p');
      accum(';');
    }
    else if ('"' == ch) 
    {
      accum('&');
      accum('q');
      accum('u');
      accum('o');
      accum('t');
      accum(';');
    }
    else if ('\'' == ch) 
    {
      accum('&');
      accum('a');
      accum('p');
      accum('o');
      accum('s');
      accum(';');
    }
    else
    {
      return false;
    }
    return true;
  }
  
  /**
   * Escape and accum a character.
   */
  final void accumDefaultEscape(char ch, int i, char[] chars, int len, boolean escLF)
    throws SAXException
  {
    if(!accumDefaultEntity(ch, i, chars, len, escLF))
    {
      if (0xd800 <= ch && ch < 0xdc00) 
      {
        // UTF-16 surrogate
        int next;
        if (i+1 >= len) 
        {
          throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INVALID_UTF16_SURROGATE, new Object[]{Integer.toHexString(ch)})); //"Invalid UTF-16 surrogate detected: "
            //+Integer.toHexString(ch)+ " ?");
        }
        else 
        {
          next = chars[++i];
          if (!(0xdc00 <= next && next < 0xe000))
            throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INVALID_UTF16_SURROGATE, new Object[]{Integer.toHexString(ch)+" "+Integer.toHexString(next)})); //"Invalid UTF-16 surrogate detected: "
              //+Integer.toHexString(ch)+" "+Integer.toHexString(next));
          next = ((ch-0xd800)<<10)+next-0xdc00+0x00010000;
        }
        accum("&#x");
        accum(Integer.toHexString(next));
        accum(";");
        /*} else if (null != ctbc && !ctbc.canConvert(ch)) {
        sb.append("&#x");
        sb.append(Integer.toString((int)ch, 16));
        sb.append(";");*/
      }
      else 
      {
        if(ch > m_maxCharacter || ((ch < SPECIALSSIZE) && (m_attrCharsMap[ch] == 'S')))
        {
          accum("&#");
          accum(Integer.toString(ch));
          accum(";");
        }
        else
        {
          accum(ch);
        }
      }
    }
  }

  /**
   * Returns the specified <var>string</var> after substituting <VAR>specials</VAR>,
   * and UTF-16 surrogates for chracter references <CODE>&amp;#xnn</CODE>.
   *
   * @param   string      String to convert to XML format.
   * @param   specials    Chracters, should be represeted in chracter referenfces.
   * @param   encoding    CURRENTLY NOT IMPLEMENTED.
   * @return              XML-formatted string.
   * @see #backReference
   */
  public void writeAttrString(String string, String encoding)
    throws SAXException
  {
    char[] stringChars = string.toCharArray();
    int len = stringChars.length;
    for (int i = 0;  i < len;  i ++) 
    {
      char ch = stringChars[i];
      if((ch < SPECIALSSIZE) && (m_attrCharsMap[ch] != 'S'))
        accum(ch);
      else 
        accumDefaultEscape(ch, i, stringChars, len, true);
    }
  }
  
  protected boolean shouldIndent()
  {
    return m_doIndent && (!m_ispreserve && !m_isprevtext);
  }
  
  /**
   * Prints <var>n</var> spaces.
   * @param pw        The character output stream to use.
   * @param n         Number of spaces to print.
   * @exception IOException   Thrown if <var>pw</var> is invalid.
   */
  public void printSpace(int n) throws SAXException 
  {
    for (int i = 0;  i < n;  i ++)
      accum(' ');
  }
 
  /**
   * Prints a newline character and <var>n</var> spaces.
   * @param pw        The character output stream to use.
   * @param n         Number of spaces to print.
   * @exception IOException   Thrown if <var>pw</var> is invalid.
   */
  public void indent(int n) throws SAXException 
  {
    if(m_startNewLine)
      outputLineSep();
    
    if(m_doIndent)
    {
      printSpace(n);
    }
  }
  
  static private Hashtable s_enchash = null;
  static private Hashtable s_revhash = null;
  static protected Hashtable s_revsize = new Hashtable();
  
  public static void initEncodings() 
  {    
    synchronized(s_revsize)
    {
      if(null != s_enchash)
        return;
      boolean useISOPrefix = true;
      try 
      {
        java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
        os.write(32);
        String s = os.toString("ISO8859_1");
        // Just in case it doesn't throw an exception...
        if(null == s)
          useISOPrefix = false;
        else
          useISOPrefix = true;
      }
      catch (java.io.UnsupportedEncodingException e) 
      {
        useISOPrefix = false;
      }
      
      // A bit of a hack for the blackdown VMs (and probably some others).
      try
      {
        String encoding = System.getProperty("file.encoding");

        int dashindex = (encoding != null ? encoding.indexOf('-') : -1);
        if(3 == dashindex)
        {
          String ISOprefix =  new String(encoding.toCharArray(), 0, 3);
          if (ISOprefix.equals("ISO") == true)
            javaEncodingIsISO = true;
        }
      }
      catch(SecurityException se)
      {
      }

      // Make a table to maximum character sizes before we 
      // need to resort to escaping in the XML.
      // TODO: To tell the truth, I'm guessing a bit here. 
      // s_revsize.put("CP1252",          new Character('\u00FF')); // Windows Latin-1 
      s_revsize.put("WINDOWS-1250",    new Character('\u00FF')); // Windows 1250 Peter Smolik
      s_revsize.put("UTF-8",           new Character('\uFFFF')); // Universal Transformation Format 8
      s_revsize.put("US-ASCII",        new Character('\u007F'));
      s_revsize.put("ISO-8859-1",      new Character('\u00FF'));
      s_revsize.put("ISO-8859-2",      new Character('\u00FF'));
      s_revsize.put("ISO-8859-3",      new Character('\u00FF'));
      s_revsize.put("ISO-8859-4",      new Character('\u00FF'));
      s_revsize.put("ISO-8859-5",      new Character('\u00FF'));
      s_revsize.put("ISO-8859-6",      new Character('\u00FF'));
      s_revsize.put("ISO-8859-7",      new Character('\u00FF'));
      s_revsize.put("ISO-8859-8",      new Character('\u00FF'));
      s_revsize.put("ISO-8859-9",      new Character('\u00FF'));
      s_revsize.put("ISO-2022-JP",     new Character('\uFFFF'));
      s_revsize.put("SHIFT_JIS",       new Character('\uFFFF'));
      s_revsize.put("EUC-JP",          new Character('\uFFFF'));
      s_revsize.put("GB2312",          new Character('\uFFFF'));
      s_revsize.put("BIG5",            new Character('\uFFFF'));
      s_revsize.put("EUC-KR",          new Character('\uFFFF'));
      s_revsize.put("ISO-2022-KR",     new Character('\uFFFF'));
      s_revsize.put("KOI8-R",          new Character('\uFFFF'));
      s_revsize.put("EBCDIC-CP-US",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-CA",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-NL",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-DK",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-NO",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-FI",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-SE",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-IT",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-ES",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-GB",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-FR",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-AR1",   new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-HE",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-CH",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-ROECE", new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-YU",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-IS",    new Character('\u00FF'));
      s_revsize.put("EBCDIC-CP-AR2",   new Character('\u00FF'));

      s_enchash = new Hashtable();

      //    <preferred MIME name>, <Java encoding name>
      // s_enchash.put("ISO 8859-1", "CP1252"); // Close enough, I guess
      s_enchash.put("WINDOWS-1250", "CP1250"); // Peter Smolik
      s_enchash.put("UTF-8", "UTF8");
      if(useISOPrefix)
      {
        s_enchash.put("US-ASCII",        "ISO8859_1");    // ?
        s_enchash.put("ISO-8859-1",      "ISO8859_1");
        s_enchash.put("ISO-8859-2",      "ISO8859_2");
        s_enchash.put("ISO-8859-3",      "ISO8859_3");
        s_enchash.put("ISO-8859-4",      "ISO8859_4");
        s_enchash.put("ISO-8859-5",      "ISO8859_5");
        s_enchash.put("ISO-8859-6",      "ISO8859_6");
        s_enchash.put("ISO-8859-7",      "ISO8859_7");
        s_enchash.put("ISO-8859-8",      "ISO8859_8");
        s_enchash.put("ISO-8859-9",      "ISO8859_9");
      }
      else
      {
        s_enchash.put("US-ASCII",        "8859_1");    // ?
        s_enchash.put("ISO-8859-1",      "8859_1");
        s_enchash.put("ISO-8859-2",      "8859_2");
        s_enchash.put("ISO-8859-3",      "8859_3");
        s_enchash.put("ISO-8859-4",      "8859_4");
        s_enchash.put("ISO-8859-5",      "8859_5");
        s_enchash.put("ISO-8859-6",      "8859_6");
        s_enchash.put("ISO-8859-7",      "8859_7");
        s_enchash.put("ISO-8859-8",      "8859_8");
        s_enchash.put("ISO-8859-9",      "8859_9");
      }
      s_enchash.put("ISO-2022-JP",     "JIS");
      s_enchash.put("SHIFT_JIS",       "SJIS");
      s_enchash.put("EUC-JP",          "EUCJIS");
      s_enchash.put("GB2312",          "GB2312");
      s_enchash.put("BIG5",            "Big5");
      s_enchash.put("EUC-KR",          "KSC5601");
      s_enchash.put("ISO-2022-KR",     "ISO2022KR");
      s_enchash.put("KOI8-R",          "KOI8_R");
      s_enchash.put("EBCDIC-CP-US",    "CP037");
      s_enchash.put("EBCDIC-CP-CA",    "CP037");
      s_enchash.put("EBCDIC-CP-NL",    "CP037");
      s_enchash.put("EBCDIC-CP-DK",    "CP277");
      s_enchash.put("EBCDIC-CP-NO",    "CP277");
      s_enchash.put("EBCDIC-CP-FI",    "CP278");
      s_enchash.put("EBCDIC-CP-SE",    "CP278");
      s_enchash.put("EBCDIC-CP-IT",    "CP280");
      s_enchash.put("EBCDIC-CP-ES",    "CP284");
      s_enchash.put("EBCDIC-CP-GB",    "CP285");
      s_enchash.put("EBCDIC-CP-FR",    "CP297");
      s_enchash.put("EBCDIC-CP-AR1",   "CP420");
      s_enchash.put("EBCDIC-CP-HE",    "CP424");
      s_enchash.put("EBCDIC-CP-CH",    "CP500");
      s_enchash.put("EBCDIC-CP-ROECE", "CP870");
      s_enchash.put("EBCDIC-CP-YU",    "CP870");
      s_enchash.put("EBCDIC-CP-IS",    "CP871");
      s_enchash.put("EBCDIC-CP-AR2",   "CP918");

      // j:CNS11643 -> EUC-TW?
      // ISO-2022-CN? ISO-2022-CN-EXT?
      
      s_revhash = new Hashtable();
      //    <Java encoding name>, <preferred MIME name>
      s_revhash.put("CP1252", "ISO-8859-1"); // Close enough, I guess
      s_revhash.put("CP1250", "WINDOWS-1250"); // Peter Smolik
      s_revhash.put("UTF8", "UTF-8");
      s_revhash.put("UTF-8", "UTF-8");  // as per Lewis Schoenberg, 01/17/00
      //s_revhash.put("ISO8859_1", "US-ASCII");    // ?
      if(useISOPrefix)
      {
        s_revhash.put("ISO8859_1", "ISO-8859-1");
        s_revhash.put("ISO8859_2", "ISO-8859-2");
        s_revhash.put("ISO8859_3", "ISO-8859-3");
        s_revhash.put("ISO8859_4", "ISO-8859-4");
        s_revhash.put("ISO8859_5", "ISO-8859-5");
        s_revhash.put("ISO8859_6", "ISO-8859-6");
        s_revhash.put("ISO8859_7", "ISO-8859-7");
        s_revhash.put("ISO8859_8", "ISO-8859-8");
        s_revhash.put("ISO8859_9", "ISO-8859-9");
      }
      else
      {
        s_revhash.put("8859_1", "ISO-8859-1");
        s_revhash.put("8859_2", "ISO-8859-2");
        s_revhash.put("8859_3", "ISO-8859-3");
        s_revhash.put("8859_4", "ISO-8859-4");
        s_revhash.put("8859_5", "ISO-8859-5");
        s_revhash.put("8859_6", "ISO-8859-6");
        s_revhash.put("8859_7", "ISO-8859-7");
        s_revhash.put("8859_8", "ISO-8859-8");
        s_revhash.put("8859_9", "ISO-8859-9");
      }
      s_revhash.put("JIS", "ISO-2022-JP");
      s_revhash.put("SJIS", "Shift_JIS");
      s_revhash.put("EUCJIS", "EUC-JP");
      s_revhash.put("GB2312", "GB2312");
      s_revhash.put("BIG5", "Big5");
      s_revhash.put("KSC5601", "EUC-KR");
      s_revhash.put("ISO2022KR", "ISO-2022-KR");
      s_revhash.put("KOI8_R", "KOI8-R");
      s_revhash.put("CP037", "EBCDIC-CP-US");
      s_revhash.put("CP037", "EBCDIC-CP-CA");
      s_revhash.put("CP037", "EBCDIC-CP-NL");
      s_revhash.put("CP277", "EBCDIC-CP-DK");
      s_revhash.put("CP277", "EBCDIC-CP-NO");
      s_revhash.put("CP278", "EBCDIC-CP-FI");
      s_revhash.put("CP278", "EBCDIC-CP-SE");
      s_revhash.put("CP280", "EBCDIC-CP-IT");
      s_revhash.put("CP284", "EBCDIC-CP-ES");
      s_revhash.put("CP285", "EBCDIC-CP-GB");
      s_revhash.put("CP297", "EBCDIC-CP-FR");
      s_revhash.put("CP420", "EBCDIC-CP-AR1");
      s_revhash.put("CP424", "EBCDIC-CP-HE");
      s_revhash.put("CP500", "EBCDIC-CP-CH");
      s_revhash.put("CP870", "EBCDIC-CP-ROECE");
      s_revhash.put("CP870", "EBCDIC-CP-YU");
      s_revhash.put("CP871", "EBCDIC-CP-IS");
      s_revhash.put("CP918", "EBCDIC-CP-AR2");
    }
  }
  
  /**
   * Try to determine if a given encoding is supported 
   * by the Java VM.  (If folks know of a better 
   * way to do this, then please do tell...)
   * @param enc Name of encoding (can not be null).
   * @return true if VM can support the encoding, false otherwise.
   */
  static boolean isSupportedJavaEncoding(String enc)
  {
    try 
    {
      java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
      os.write(32);
      String s = os.toString(enc);
      // Just in case it doesn't throw an exception...
      if(null != s)
        return true;
    }
    catch (java.io.UnsupportedEncodingException e) 
    {
    }
    return false;
  }
  
  /**
   * Convert a MIME charset name, also known as an XML encoding name, to a Java encoding name.
   * @param   mimeCharsetName Case insensitive MIME charset name: <code>UTF-8, US-ASCII, ISO-8859-1,
   *                          ISO-8859-2, ISO-8859-3, ISO-8859-4, ISO-8859-5, ISO-8859-6,
   *                          ISO-8859-7, ISO-8859-8, ISO-8859-9, ISO-2022-JP, Shift_JIS, 
   *                          EUC-JP, GB2312, Big5, EUC-KR, ISO-2022-KR, KOI8-R,
   *                          EBCDIC-CP-US, EBCDIC-CP-CA, EBCDIC-CP-NL, EBCDIC-CP-DK,
   *                          EBCDIC-CP-NO, EBCDIC-CP-FI, EBCDIC-CP-SE, EBCDIC-CP-IT,
   *                          EBCDIC-CP-ES, EBCDIC-CP-GB, EBCDIC-CP-FR, EBCDIC-CP-AR1,
   *                          EBCDIC-CP-HE, EBCDIC-CP-CH, EBCDIC-CP-ROECE, EBCDIC-CP-YU,
   *                          EBCDIC-CP-IS and EBCDIC-CP-AR2</code>.
   * @return                  Java encoding name, or <var>null</var> if <var>mimeCharsetName</var>
   *                          is unknown.
   * @see #reverse
   */
  public static String convertMime2JavaEncoding(String mimeCharsetName)
    throws UnsupportedEncodingException
  {
    if(null == s_enchash)
      initEncodings();      
    if(null == mimeCharsetName)
      return "UTF8";
    String encoding = javaEncodingIsISO ? mimeCharsetName : 
                               (String)s_enchash.get(mimeCharsetName.toUpperCase());
    
    // Since we're serializing out, if they hand in a Java encoding name 
    // for whatever reason, it's probably good and OK, so just use that.
    if((null == encoding) && isSupportedJavaEncoding(mimeCharsetName))
    {
      encoding = mimeCharsetName;
    }
    
    if (null == encoding)
      throw new UnsupportedEncodingException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_UNSUPPORTED_ENCODING, new Object[] {mimeCharsetName}));
    else 
      return encoding;
  }

  /**
   * Convert a Java encoding name to MIME charset name.
   * Available values of <i>encoding</i> are "UTF8", "ISO8859_1", "ISO8859_2", "ISO8859_3", "ISO8859_4",
   * "ISO8859_5", "ISO8859_6", "ISO8859_7", "ISO8859_8", "ISO8859_9", "JIS", "SJIS", "EUCJIS",
   * "GB2312", "BIG5", "KSC5601", "ISO2022KR",  "KOI8_R", "CP037", "CP277", "CP278",
   * "CP280", "CP284", "CP285", "CP297", "CP420", "CP424", "CP500", "CP870", "CP871" and "CP918".
   * @param   encoding    Case insensitive Java encoding name: <code>UTF8, ISO8859_1, ISO8859_2, ISO8859_3,
   *                      ISO8859_4, ISO8859_5, ISO8859_6, ISO8859_7, ISO8859_8, ISO8859_9, JIS, SJIS, EUCJIS,
   *                      GB2312, BIG5, KSC5601, ISO2022KR, KOI8_R, CP037, CP277, CP278,
   *                      CP280, CP284, CP285, CP297, CP420, CP424, CP500, CP870, CP871 
   *                      and CP918</code>.
   * @return              MIME charset name, or <var>null</var> if <var>encoding</var> is unknown.
   * @see #convert
   */
  public static String convertJava2MimeEncoding(String encoding) 
  {
    if(null == s_revhash)
      initEncodings();
    if(null == encoding)
      return "UTF-8";
    return javaEncodingIsISO ? encoding : 
                               (String)s_revhash.get(encoding.toUpperCase());
  }
  
  /**
   * Get the proper mime encoding.  From the XSLT recommendation: "The encoding 
   * attribute specifies the preferred encoding to use for outputting the result 
   * tree. XSLT processors are required to respect values of UTF-8 and UTF-16. 
   * For other values, if the XSLT processor does not support the specified 
   * encoding it may signal an error; if it does not signal an error it should 
   * use UTF-8 or UTF-16 instead. The XSLT processor must not use an encoding 
   * whose name does not match the EncName production of the XML Recommendation 
   * [XML]. If no encoding attribute is specified, then the XSLT processor should 
   * use either UTF-8 or UTF-16."
   */
  String getMimeEncoding(String encoding)
  {
    if(null == encoding)
    {
      try
      {
        // Get the default system character encoding.  This may be 
        // incorrect if they passed in a writer, but right now there 
        // seems to be no way to get the encoding from a writer.
        encoding = System.getProperty("file.encoding");
           
        if(null != encoding)
        {
          /*
          * See if the mime type is equal to UTF8.  If you don't 
          * do that, then  convertJava2MimeEncoding will convert 
          * 8859_1 to "ISO-8859-1", which is not what we want, 
          * I think, and I don't think I want to alter the tables 
          * to convert everything to UTF-8.
          */
          String jencoding = (encoding.equals("Cp1252") ||
                              encoding.equals("ISO8859_1") || 
                              encoding.equals("8859_1")  || 
                              encoding.equals("UTF8"))
                             ? DEFAULT_MIME_ENCODING : FormatterToXML.convertJava2MimeEncoding( encoding );
          encoding = (null != jencoding) ? jencoding : DEFAULT_MIME_ENCODING;
        }
        else
        {
          encoding = DEFAULT_MIME_ENCODING;
        }
      }
      catch(SecurityException se)
      {
        encoding = DEFAULT_MIME_ENCODING;
      }
    }
    return encoding;
  }


}  //ToXMLStringVisitor
