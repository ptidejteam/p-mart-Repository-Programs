/*
 * (c) Copyright 2001-2004 Yann-Gaël Guéhéneuc,
 * University of Montréal.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package dpl.test;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import dpl.XMLConstants;
import util.xml.DOMVisitor;
import util.xml.DOMVisitorAdapter;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since  2004/04/21
 */
public class DOMVisitorExample {
	public static void main(final String[] args) {
		final DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document =
				builder.parse(new File(XMLConstants.ORIGIN_XML_FILE));
			new DOMVisitorAdapter(document).accept(new DOMVisitor() {
				public void open(Document aDocument) {
					System.out.println("Opening document");
				}
				public void close(final Document aDocument) {
					System.out.println("Closing document");
				}
				public void open(final Node aNode) {
					System.out.print("Opening node (");
					System.out.print(aNode.getNodeName());
					System.out.print(", ");
					System.out.print(aNode.getNodeValue());
					System.out.println(')');
				}
				public void close(final Node aNode) {
					System.out.println("Closing node");
				}
			});
		}
		catch (final ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		catch (final SAXException saxe) {
			saxe.printStackTrace();
		}
		catch (final IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
