/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.chat;

import java.awt.datatransfer.*;
import java.awt.im.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.impl.gui.customcontrols.*;
import net.java.sip.communicator.impl.gui.main.contactlist.*;
import net.java.sip.communicator.service.contactlist.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;
import net.java.sip.communicator.util.swing.*;

/**
 * A TransferHandler that we use to handle copying, pasting and DnD operations
 * in our <tt>ChatPanel</tt>. The string handler is heavily inspired
 * by Sun's <tt>DefaultTransferHandler</tt> with the main difference being that
 * we only accept pasting of plain text. We do this in order to avoid html
 * support problems that appear when pasting formatted text into our editable
 * area.
 *
 * @author Emil Ivov
 * @author Yana Stamcheva
 */
public class ChatTransferHandler
    extends ExtendedTransferHandler
{
    /**
     * This class logger.
     */
    private final static Logger logger
        = Logger.getLogger(ChatTransferHandler.class);

    /**
     * The chat panel involved in the copy/paste/DnD operation.
     */
    private final ChatPanel chatPanel;

    /**
     * Constructs the <tt>ChatTransferHandler</tt> by specifying the
     * <tt>ChatPanel</tt> we're currently dealing with.
     *
     * @param chatPanel the <tt>ChatPanel</tt> we're currently dealing with
     */
    public ChatTransferHandler(ChatPanel chatPanel)
    {
        this.chatPanel = chatPanel;
    }

    /**
     * Indicates whether a component will accept an import of the given
     * set of data flavors prior to actually attempting to import it. We return
     * <tt>true</tt> to indicate that the transfer with at least one of the
     * given flavors would work and <tt>false</tt> to reject the transfer.
     * <p>
     * @param comp component
     * @param flavor the data formats available
     * @return  true if the data can be inserted into the component, false
     * otherwise
     * @throws NullPointerException if <code>support</code> is {@code null}
     */
    public boolean canImport(JComponent comp, DataFlavor flavor[])
    {
        for (int i = 0, n = flavor.length; i < n; i++)
        {
            if (flavor[i].equals(metaContactDataFlavor))
            {
                return true;
            }
        }

        return super.canImport(comp, flavor);
    }

    /**
     * Handles transfers to the chat panel from the clip board or a
     * DND drop operation. The <tt>Transferable</tt> parameter contains the
     * data that needs to be imported.
     * <p>
     * @param comp  the component to receive the transfer;
     * @param t the data to import
     * @return  true if the data was inserted into the component and false
     * otherwise
     */
    @SuppressWarnings("unchecked") //the case is taken care of
    public boolean importData(JComponent comp, Transferable t)
    {
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {
            try
            {
                Object o = t.getTransferData(DataFlavor.javaFileListFlavor);

                if (o instanceof java.util.Collection)
                {
                    Collection<File> files = (Collection<File>) o;

                    for(File file: files)
                    {
                        chatPanel.sendFile(file);
                    }

                    // Otherwise fire files dropped event.
                    return true;
                }
            }
            catch (UnsupportedFlavorException e)
            {
                logger.debug("Failed to drop files.", e);
            }
            catch (IOException e)
            {
                logger.debug("Failed to drop files.", e);
            }
        }
        else if (t.isDataFlavorSupported(metaContactDataFlavor))
        {
            Object o = null;

            try
            {
                o = t.getTransferData(metaContactDataFlavor);
            }
            catch (UnsupportedFlavorException e)
            {
                logger.debug("Failed to drop meta contact.", e);
            }
            catch (IOException e)
            {
                logger.debug("Failed to drop meta contact.", e);
            }

            if (o instanceof ContactNode)
            {
                MetaContact metaContact
                    = ((ContactNode) o).getMetaContact();

                ChatTransport currentChatTransport
                    = chatPanel.getChatSession().getCurrentChatTransport();

                Iterator<Contact> contacts = metaContact
                    .getContactsForProvider(
                        currentChatTransport.getProtocolProvider());

                String contact = null;
                if (contacts.hasNext())
                    contact = contacts.next().getAddress();

                if (contact != null)
                {
                    ArrayList inviteList = new ArrayList();
                    inviteList.add(contact);
                    chatPanel.inviteContacts(   currentChatTransport,
                                                inviteList, null);

                    return true;
                }
                else
                    new ErrorDialog(
                        null,
                        GuiActivator.getResources().getI18NString(
                            "service.gui.ERROR"),
                        GuiActivator.getResources().getI18NString(
                            "service.gui.CONTACT_NOT_SUPPORTING_CHAT_CONF",
                            new String[]{metaContact.getDisplayName()}))
                    .showDialog();
            }
        }
        else if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
        {
            InputContext inputContext = comp.getInputContext();
            if (inputContext != null)
            {
                inputContext.endComposition();
            }
            try
            {
                BufferedReader reader = new BufferedReader(
                    DataFlavor.stringFlavor.getReaderForText(t));

                StringBuffer buffToPaste = new StringBuffer();
                String line = reader.readLine();

                while(line != null)
                {
                    buffToPaste.append(line);

                    //read next line
                    line = reader.readLine();
                    if(line != null)
                        buffToPaste.append("\n");
                }

                ((JTextComponent)comp)
                    .replaceSelection(buffToPaste.toString());
                return true;
            }
            catch (UnsupportedFlavorException ufe)
            {
                logger.debug("Failed to drop string.", ufe);
            }
            catch (IOException ioe)
            {
                logger.debug("Failed to drop string.", ioe);
            }
        }
        return false;
    }
}
