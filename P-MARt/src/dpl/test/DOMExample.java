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

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import dpl.XMLConstants;

public class DOMExample {
	public static void main(final String[] args) {
		final DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder =
				documentBuilderFactory.newDocumentBuilder();
			Document document = builder.newDocument();

			final Element root =
				(Element) document.createElement("rootElement");
			document.appendChild(root);
			root.appendChild(document.createTextNode("Some"));
			root.appendChild(document.createTextNode(" "));
			root.appendChild(document.createTextNode("text"));

			document = builder.parse(XMLConstants.ORIGIN_XML_FILE);

			XMLReader saxReader =
				XMLReaderFactory.createXMLReader(
					"org.apache.xerces.parsers.SAXParser");
			saxReader.setContentHandler(new ContentHandler() {
				public void setDocumentLocator(Locator locator) {
				}
				public void startDocument() throws SAXException {
					System.currentTimeMillis();
				}
				public void endDocument() throws SAXException {
				}
				public void startPrefixMapping(String prefix, String uri)
					throws SAXException {
				}
				public void endPrefixMapping(String prefix)
					throws SAXException {
				}
				public void startElement(
					String namespaceURI,
					String localName,
					String qName,
					Attributes atts)
					throws SAXException {
				}
				public void endElement(
					String namespaceURI,
					String localName,
					String qName)
					throws SAXException {
				}
				public void characters(char[] ch, int start, int length)
					throws SAXException {
				}
				public void ignorableWhitespace(
					char[] ch,
					int start,
					int length)
					throws SAXException {
				}
				public void processingInstruction(String target, String data)
					throws SAXException {
				}
				public void skippedEntity(String name) throws SAXException {
				}
			});
			saxReader.parse(XMLConstants.ORIGIN_XML_FILE);

			final TransformerFactory transformerFactory =
				TransformerFactory.newInstance();
			final Transformer transformer =
				transformerFactory.newTransformer();

			final DOMSource source = new DOMSource(document);
			final Node node0 = source.getNode();
			final NodeList nodeList0 = node0.getChildNodes();
			for (int i = 0; i < nodeList0.getLength(); i++) {
				final Node node1 = nodeList0.item(i);
				System.out.print('\t');
				System.out.print(node1.getNodeName());
				System.out.print(": ");
				System.out.print(node1.getNodeValue());
				System.out.print(": ");
				System.out.println(node1.getNodeType());
				final NodeList nodeList1 = node1.getChildNodes();
				for (int j = 0; j < nodeList1.getLength(); j++) {
					final Node node2 = nodeList1.item(j);
					System.out.print("\t\t");
					System.out.print(node2.getNodeName());
					System.out.print(": ");
					System.out.print(node2.getNodeValue());
					System.out.print(": ");
					System.out.println(node2.getNodeType());
					final NodeList nodeList2 = node2.getChildNodes();
					for (int k = 0; k < nodeList2.getLength(); k++) {
						final Node node3 = nodeList2.item(k);
						System.out.print("\t\t\t");
						System.out.print(node3.getNodeName());
						System.out.print(": ");
						System.out.print(node3.getNodeValue());
						System.out.print(": ");
						System.out.println(node3.getNodeType());
						final NodeList nodeList3 = node3.getChildNodes();
						for (int l = 0; l < nodeList3.getLength(); l++) {
							final Node node4 = nodeList3.item(l);
							System.out.print("\t\t\t\t");
							System.out.print(node4.getNodeName());
							System.out.print(": ");
							System.out.print(node4.getNodeValue());
							System.out.print(": ");
							System.out.println(node4.getNodeType());
						}
					}
				}
			}

			final StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
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
		catch (final TransformerConfigurationException tce) {
			tce.printStackTrace();

		}
		catch (final TransformerException te) {
			te.printStackTrace();

		}
	}
}
