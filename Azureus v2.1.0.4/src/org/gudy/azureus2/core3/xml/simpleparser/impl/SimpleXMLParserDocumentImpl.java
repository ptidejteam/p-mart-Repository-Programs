/*
 * File    : SimpleXMLParserDocumentImpl.java
 * Created : 5 Oct. 2003
 * By      : Parg 
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 
package org.gudy.azureus2.core3.xml.simpleparser.impl;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.gudy.azureus2.core3.xml.simpleparser.*;
import org.gudy.azureus2.core3.util.Constants;
import org.w3c.dom.*;

import java.io.*;
import java.util.*;

public class 
SimpleXMLParserDocumentImpl
	implements SimpleXMLParserDocument
{
	protected Document				document;
	protected SimpleXMLParserDocumentNodeImpl	root_node;
	
	public
	SimpleXMLParserDocumentImpl(
		File		file )
		
		throws SimpleXMLParserDocumentException
	{
		try{
			
			create( new FileInputStream( file ));
			
		}catch( Throwable e ){
			
			throw( new SimpleXMLParserDocumentException( e ));
		}
	}
	
	public
	SimpleXMLParserDocumentImpl(
		String		data )
		
		throws SimpleXMLParserDocumentException
	{
		try{
			create( new ByteArrayInputStream( data.getBytes( Constants.DEFAULT_ENCODING )));
			
		}catch( UnsupportedEncodingException e ){
			
		}
	}

	public
	SimpleXMLParserDocumentImpl(
		InputStream		input_stream )
		
		throws SimpleXMLParserDocumentException
	{
		create( input_stream );
	}
	
	protected void
	create(
		InputStream		input_stream )
		
		throws SimpleXMLParserDocumentException
	{
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			// Set namespaceAware to true to get a DOM Level 2 tree with nodes
			// containing namesapce information.  This is necessary because the
			// default value from JAXP 1.0 was defined to be false.
						
			dbf.setNamespaceAware(true);

			// Set the validation mode to either: no validation, DTD
			// validation, or XSD validation
					
			dbf.setValidating( false );
					
			// Optional: set various configuration options
					
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setCoalescing(true);
					
			// The opposite of creating entity ref nodes is expanding them inline
			// NOTE that usage of, e.g. "&amp;" in text results in an entity ref. e.g.
			//	if ("BUY".equals (type) "
			//		ENT_REF: nodeName="amp"
			//		TEXT: nodeName="#text" nodeValue="&"
			
			dbf.setExpandEntityReferences(true);
					

			// Step 2: create a DocumentBuilder that satisfies the constraints
			// specified by the DocumentBuilderFactory
					
			DocumentBuilder db = dbf.newDocumentBuilder();

			// Set an ErrorHandler before parsing
					
			OutputStreamWriter errorWriter = new OutputStreamWriter(System.err);
					
			MyErrorHandler error_handler = new MyErrorHandler(new PrintWriter(errorWriter, true));

			db.setErrorHandler( error_handler );

			// Step 3: parse the input file
					
			document = db.parse( input_stream );
			
			// new SXPDocumentDomPrint( document );
			
			SimpleXMLParserDocumentNodeImpl[] root_nodes = parseNode( document, false );
			
			if ( root_nodes.length != 1 ){
				
				throw( new SimpleXMLParserDocumentException( "invalid document - " + root_nodes.length + " root elements" ));
			}
			
			root_node = root_nodes[0];
			
		}catch( Throwable e ){
			
			// e.printStackTrace();
			
			throw( new SimpleXMLParserDocumentException( e ));
		}
	}
	
	public String
	getName()
	{
		return( root_node.getName());
	}
	
	public String
	getValue()
	{
		return( root_node.getValue());
	}
	
	public SimpleXMLParserDocumentNode[]
	getChildren()
	{
		return( root_node.getChildren());
	}
	public SimpleXMLParserDocumentNode
	getChild(
		String	name )
	{
		return( root_node.getChild(name));
	}
	
	public SimpleXMLParserDocumentAttribute[]
	getAttributes()
	{
		return( root_node.getAttributes());
	}
	public SimpleXMLParserDocumentAttribute
	getAttribute(
		String		name )
	{
		return( root_node.getAttribute(name));
	}

	public void
	print()
	{
		root_node.print( "" );
	}
	
		// idea is to flatten out any unwanted structure. We just want the resultant
		// tree to have nodes for each nesting element and leaves denoting name/value bits
	
	protected SimpleXMLParserDocumentNodeImpl[]
	parseNode(
		Node		node,
		boolean		skip_this_node )
	{
        int type = node.getNodeType();
		
		if ( (	type == Node.ELEMENT_NODE ||
				type == Node.PROCESSING_INSTRUCTION_NODE )&& !skip_this_node ){
			
			return( new SimpleXMLParserDocumentNodeImpl[]{ new SimpleXMLParserDocumentNodeImpl( this, node )});
		}

		Vector	v = new Vector();
		
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()){
			
			SimpleXMLParserDocumentNodeImpl[] kids = parseNode( child, false );
			
			for (int i=0;i<kids.length;i++){
				
				v.addElement(kids[i]);
			}
        }
		
		SimpleXMLParserDocumentNodeImpl[]	res = new SimpleXMLParserDocumentNodeImpl[v.size()];
		
		v.copyInto( res );
		
		return( res );
	}
	
    private static class MyErrorHandler implements ErrorHandler {
        /** Error handler output goes here */
        private PrintWriter out;

        MyErrorHandler(PrintWriter out) {
            this.out = out;
        }

        /**
         * Returns a string describing parse exception details
         */
        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }
            String info = "URI=" + systemId +
                " Line=" + spe.getLineNumber() +
                ": " + spe.getMessage();
            return info;
        }

        // The following methods are standard SAX ErrorHandler methods.
        // See SAX documentation for more info.

        public void 
		warning(
			SAXParseException spe ) 
			
			throws SAXException 
		{
            out.println("Warning: " + getParseExceptionInfo(spe));
        }
        
        public void 
		error(
			SAXParseException spe )
			
			throws SAXException 
		{
            String message = "Error: " + getParseExceptionInfo(spe);
			
            throw new SAXException(message);
        }

        public void 
		fatalError(
			SAXParseException spe ) 
			
			throws SAXException 
		{
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
			
            throw new SAXException(message);
        }
    }
}
