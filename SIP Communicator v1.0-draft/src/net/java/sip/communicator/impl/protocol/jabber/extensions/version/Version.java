package net.java.sip.communicator.impl.protocol.jabber.extensions.version;

import org.jivesoftware.smack.packet.*;

/**
 * EXtension providing application version
 *
 * @author Damian Minkov
 */
public class Version
    implements PacketExtension
{
    public static final String ELEMENT_NAME = "c";
    public static final String NAMESPACE = "http://jabber.org/protocol/caps";

    /**
     * Returns the root element name.
     *
     * @return the element name.
     */
    public String getElementName()
    {
        return ELEMENT_NAME;
    }

    /**
     * Returns the root element XML namespace.
     *
     * @return the namespace.
     */
    public String getNamespace()
    {
        return NAMESPACE;
    }

    /**
     * Returns the XML reppresentation of the PacketExtension.
     *
     * @return the packet extension as XML.
     */
    public String toXML()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<").append(getElementName()).
            append(" xmlns=\"").append(getNamespace()).append("\"").
            append(" node=\"http://sip-communicator.org/caps\"").
            append(" ver=\"").
            append(System.getProperty("sip-communicator.version")).
            append("\"/>");

        return buf.toString();
    }
}
