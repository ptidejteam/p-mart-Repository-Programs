/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.chat;

/**
 * The <tt>ChatMessage</tt> class encapsulates message information in order to
 * provide a single object containing all data needed to display a chat message.
 * 
 * @author Yana Stamcheva
 */
public class ChatMessage
{
    /**
     * The name of the contact sending the message.
     */
    private final String contactName;

    /**
     * The date and time of the message.
     */
    private final long date;

    /**
     * The type of the message.
     */
    private final String messageType;

    /**
     * The title of the message. This property is optional and could be used
     * to show a title for error messages.
     */
    private String messageTitle;

    /**
     * The content of the message.
     */
    private String message;

    /**
     * The content type of the message.
     */
    private final String contentType;

    /**
     * Creates a <tt>ChatMessage</tt> by specifying all parameters of the
     * message.
     * @param contactName the name of the contact
     * @param date the date and time
     * @param messageType the type (INCOMING or OUTGOING)
     * @param message the content
     * @param contentType the content type (e.g. "text", "text/html", etc.)
     */
    public ChatMessage( String contactName,
                        long date,
                        String messageType,
                        String message,
                        String contentType)
    {
        this.contactName = contactName;
        this.date = date;
        this.messageType = messageType;
        this.message = message;
        this.contentType = contentType;
    }

    /**
     * Creates a <tt>ChatMessage</tt> by specifying all parameters of the
     * message.
     * @param contactName the name of the contact
     * @param date the date and time
     * @param messageType the type (INCOMING or OUTGOING)
     * @param message the content
     * @param contentType the content type (e.g. "text", "text/html", etc.)
     */
    public ChatMessage( String contactName,
                        long date,
                        String messageType,
                        String messageTitle,
                        String message,
                        String contentType)
    {
        this.contactName = contactName;
        this.date = date;
        this.messageType = messageType;
        this.messageTitle = messageTitle;
        this.message = message;
        this.contentType = contentType;
    }

    /**
     * Returns the name of the contact sending the message.
     * 
     * @return the name of the contact sending the message.
     */
    public String getContactName()
    {
        return contactName;
    }

    /**
     * Returns the date and time of the message.
     * 
     * @return the date and time of the message.
     */
    public long getDate()
    {
        return date;
    }

    /**
     * Returns the type of the message.
     * 
     * @return the type of the message.
     */
    public String getMessageType()
    {
        return messageType;
    }

    /**
     * Returns the title of the message.
     * 
     * @return the title of the message.
     */
    public String getMessageTitle()
    {
        return messageTitle;
    }

    /**
     * Returns the content of the message.
     * 
     * @return the content of the message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the content of the message.
     * 
     * @param message the new content
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Returns the content type (e.g. "text", "text/html", etc.).
     * 
     * @return the content type
     */
    public String getContentType()
    {
        return contentType;
    }
}
