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
package dpl.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import dpl.ARFFConstants;
import dpl.XMLConstants;
import util.xml.DOMVisitorAdapter;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since  2004/05/06
 */
public class ARFFGenerator {
	public static void main(final String[] args) {
		final DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document =
				builder.parse(new File(XMLConstants.TARGET_XML_FILE));

			// Yann 2005/05/05: XSLT.
			// I should be able to use a XSLT stylesheet to convert the
			// data from the design pattern list to tabbed text. However,
			// XSLT seems rather complex to handle and to master, so I
			// prefer to do that with a Visitor in Java!
			//	final TransformerFactory transformerFactory =
			//		TransformerFactory.newInstance();
			//	final Transformer transformer =
			//		transformerFactory.newTransformer();
			//
			//	final DOMSource source = new DOMSource(document);
			//	final StreamResult result =
			//		new StreamResult(new File(Constants.TARGETTABFILE));
			//	transformer.transform(source, result);

			// This visitor prints out a ARFF version of the data.
			new DOMVisitorAdapter(document).accept(
				new ARFFVisitor(
					new FileWriter(ARFFConstants.TARGET_ARFF_FILE),
					new FileWriter(ARFFConstants.TARGET_ROLES_FILE)));
			//	new DOMVisitorAdapter(document).accept(
			//		new ARFFVisitor(new OutputStreamWriter(System.out)));
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
		//	catch (final TransformerConfigurationException tce) {
		//		tce.printStackTrace();
		//	}
		//	catch (final TransformerFactoryConfigurationError tfce) {
		//		tfce.printStackTrace();
		//	}
		//	catch (final TransformerException te) {
		//		te.printStackTrace();
		//	}
	}
}
