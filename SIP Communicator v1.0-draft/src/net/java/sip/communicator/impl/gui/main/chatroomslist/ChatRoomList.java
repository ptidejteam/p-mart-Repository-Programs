/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.chatroomslist;

import java.util.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.impl.gui.main.chat.conference.*;
import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.service.configuration.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;

import org.osgi.framework.*;

/**
 * The <tt>ChatRoomsList</tt> is the list containing all chat rooms.
 *
 * @author Yana Stamcheva
 */
public class ChatRoomList
{
    private static final Logger logger = Logger.getLogger(ChatRoomList.class);

    /**
     * The list containing all chat servers and rooms.
     */
    private final List<ChatRoomProviderWrapper> providersList
        = new Vector<ChatRoomProviderWrapper>();

    /**
     * Initializes the list of chat rooms.
     */
    public void loadList()
    {
        try
        {
            ServiceReference[] serRefs
                = GuiActivator.bundleContext.getServiceReferences(
                                        ProtocolProviderService.class.getName(),
                                        null);

            // If we don't have providers at this stage we just return.
            if (serRefs == null)
                return;

            for (ServiceReference serRef : serRefs)
            {
                ProtocolProviderService protocolProvider
                    = (ProtocolProviderService)
                            GuiActivator.bundleContext.getService(serRef);

                Object multiUserChatOpSet
                    = protocolProvider
                        .getOperationSet(OperationSetMultiUserChat.class);

                if (multiUserChatOpSet != null)
                {
                    this.addChatProvider(protocolProvider);
                }
            }
        }
        catch (InvalidSyntaxException e)
        {
            logger.error("Failed to obtain service references.", e);
        }
    }

    /**
     * Adds a chat server and all its existing chat rooms.
     *
     * @param pps the <tt>ProtocolProviderService</tt> corresponding to the chat
     * server
     */
    public void addChatProvider(ProtocolProviderService pps)
    {
        ChatRoomProviderWrapper chatRoomProvider
            = new ChatRoomProviderWrapper(pps);

        providersList.add(chatRoomProvider);

        ConfigurationService configService
            = GuiActivator.getConfigurationService();

        String prefix = "net.java.sip.communicator.impl.gui.accounts";

        List<String> accounts =
            configService.getPropertyNamesByPrefix(prefix, true);

        for (String accountRootPropName : accounts) {
            String accountUID
                = configService.getString(accountRootPropName);

            if(accountUID.equals(pps
                    .getAccountID().getAccountUniqueID()))
            {
                List<String> chatRooms = configService
                    .getPropertyNamesByPrefix(
                        accountRootPropName + ".chatRooms", true);

                for (String chatRoomPropName : chatRooms)
                {
                    String chatRoomID
                        = configService.getString(chatRoomPropName);

                    String chatRoomName = configService.getString(
                        chatRoomPropName + ".chatRoomName");

                    ChatRoomWrapper chatRoomWrapper
                        = new ChatRoomWrapper(  chatRoomProvider,
                                                chatRoomID,
                                                chatRoomName);

                    chatRoomProvider.addChatRoom(chatRoomWrapper);
                }
            }
        }
    }

    /**
     * Removes the corresponding server and all related chat rooms from this
     * list.
     * 
     * @param pps the <tt>ProtocolProviderService</tt> corresponding to the
     *            server to remove
     */
    public void removeChatProvider(ProtocolProviderService pps)
    {
        ChatRoomProviderWrapper wrapper = findServerWrapperFromProvider(pps);

        if (wrapper != null)
            removeChatProvider(wrapper);
    }

    /**
     * Removes the corresponding server and all related chat rooms from this
     * list.
     * 
     * @param chatRoomProvider the <tt>ChatRoomProviderWrapper</tt>
     *            corresponding to the server to remove
     */
    private void removeChatProvider(ChatRoomProviderWrapper chatRoomProvider)
    {
        providersList.remove(chatRoomProvider);

        ConfigurationService configService
            = GuiActivator.getConfigurationService();
        String prefix = "net.java.sip.communicator.impl.gui.accounts";
        String providerAccountUID
            = chatRoomProvider
                    .getProtocolProvider().getAccountID().getAccountUniqueID();

        for (String accountRootPropName
                : configService.getPropertyNamesByPrefix(prefix, true))
        {
            String accountUID
                = configService.getString(accountRootPropName);

            if(accountUID.equals(providerAccountUID))
            {
                List<String> chatRooms
                    = configService.getPropertyNamesByPrefix(
                            accountRootPropName + ".chatRooms",
                            true);

                for (String chatRoomPropName : chatRooms)
                {
                    configService.setProperty(
                        chatRoomPropName + ".chatRoomName",
                        null);
                }

                configService.setProperty(accountRootPropName, null);
            }
        }
    }

    /**
     * Adds a chat room to this list.
     *
     * @param chatRoomWrapper the <tt>ChatRoom</tt> to add
     */
    public void addChatRoom(ChatRoomWrapper chatRoomWrapper)
    {
        ChatRoomProviderWrapper chatRoomProvider
            = chatRoomWrapper.getParentProvider();

        if (!chatRoomProvider.containsChatRoom(chatRoomWrapper))
            chatRoomProvider.addChatRoom(chatRoomWrapper);

        if (chatRoomWrapper.isPersistent())
        {
            ConfigurationManager.saveChatRoom(
                chatRoomProvider.getProtocolProvider(),
                chatRoomWrapper.getChatRoomID(),
                chatRoomWrapper.getChatRoomID(),
                chatRoomWrapper.getChatRoomName());
        }
    }

    /**
     * Removes the given <tt>ChatRoom</tt> from the list of all chat rooms.
     * 
     * @param chatRoomWrapper the <tt>ChatRoomWrapper</tt> to remove
     */
    public void removeChatRoom(ChatRoomWrapper chatRoomWrapper)
    {
        ChatRoomProviderWrapper chatRoomProvider
            = chatRoomWrapper.getParentProvider();

        if (providersList.contains(chatRoomProvider))
        {
            chatRoomProvider.removeChatRoom(chatRoomWrapper);

            if (chatRoomWrapper.isPersistent())
            {
                ConfigurationManager.saveChatRoom(
                    chatRoomProvider.getProtocolProvider(),
                    chatRoomWrapper.getChatRoomID(),
                    null,   // The new identifier.
                    null);   // The name of the chat room.
            }
        }
    }

    /**
     * Returns the <tt>ChatRoomWrapper</tt> that correspond to the given
     * <tt>ChatRoom</tt>. If the list of chat rooms doesn't contain a
     * corresponding wrapper - returns null.
     *  
     * @param chatRoom the <tt>ChatRoom</tt> that we're looking for
     * @return the <tt>ChatRoomWrapper</tt> object corresponding to the given
     * <tt>ChatRoom</tt>
     */
    public ChatRoomWrapper findChatRoomWrapperFromChatRoom(ChatRoom chatRoom)
    {
        for (ChatRoomProviderWrapper provider : providersList)
        {
            ChatRoomWrapper systemRoomWrapper = provider.getSystemRoomWrapper();
            ChatRoom systemRoom = systemRoomWrapper.getChatRoom();

            if ((systemRoom != null) && systemRoom.equals(chatRoom))
            {
                return systemRoomWrapper;
            }
            else
            {
                ChatRoomWrapper chatRoomWrapper
                    = provider.findChatRoomWrapperForChatRoom(chatRoom);

                if (chatRoomWrapper != null)
                {
                    // stored chatrooms has no chatroom, but their
                    // id is the same as the chatroom we are searching wrapper
                    // for
                    if(chatRoomWrapper.getChatRoom() == null)
                    {
                        chatRoomWrapper.setChatRoom(chatRoom);
                    }

                    return chatRoomWrapper;
                }
            }
        }

        return null;
    }

    /**
     * Returns the <tt>ChatRoomProviderWrapper</tt> that correspond to the
     * given <tt>ProtocolProviderService</tt>. If the list doesn't contain a
     * corresponding wrapper - returns null.
     *  
     * @param protocolProvider the protocol provider that we're looking for
     * @return the <tt>ChatRoomProvider</tt> object corresponding to
     * the given <tt>ProtocolProviderService</tt>
     */
    public ChatRoomProviderWrapper findServerWrapperFromProvider(
        ProtocolProviderService protocolProvider)
    {
        for(ChatRoomProviderWrapper chatRoomProvider : providersList)
        {
            if(chatRoomProvider.getProtocolProvider().equals(protocolProvider))
            {
                return chatRoomProvider;
            }
        }

        return null;
    }

    /**
     * Goes through the locally stored chat rooms list and for each
     * {@link ChatRoomWrapper} tries to find the corresponding server stored
     * {@link ChatRoom} in the specified operation set. Joins automatically all
     * found chat rooms.
     *
     * @param protocolProvider the protocol provider for the account to
     * synchronize
     * @param opSet the multi user chat operation set, which give us access to
     * chat room server 
     */
    public void synchronizeOpSetWithLocalContactList(
        ProtocolProviderService protocolProvider,
        final OperationSetMultiUserChat opSet)
    {
        ChatRoomProviderWrapper chatRoomProvider
            = findServerWrapperFromProvider(protocolProvider);

        if (chatRoomProvider != null)
        {
            chatRoomProvider.synchronizeProvider();
        }
    }

    /**
     * Returns an iterator to the list of chat room providers.
     * 
     * @return an iterator to the list of chat room providers.
     */
    public Iterator<ChatRoomProviderWrapper> getChatRoomProviders()
    {
        return providersList.iterator();
    }
}
