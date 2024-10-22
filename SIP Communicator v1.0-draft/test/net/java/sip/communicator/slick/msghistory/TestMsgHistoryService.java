/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.slick.msghistory;

import java.util.*;

import junit.framework.*;
import net.java.sip.communicator.impl.protocol.mock.*;
import net.java.sip.communicator.service.contactlist.*;
import net.java.sip.communicator.service.msghistory.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.util.*;

import org.osgi.framework.*;

/**
 * Tests message history.
 * First installs the MoxkProtocolProvider to be able to send some messages
 * The message history service stores them
 * and then tests the verious find methods - does they find the messsages we have
 * already sent
 *
 * @author Damian Minkov
 */
public class TestMsgHistoryService
    extends TestCase
{
    private static final Logger logger = Logger.getLogger(TestMsgHistoryService.class);

    static final String TEST_CONTACT_NAME_1 = "Mincho_Penchev_the_fisrt";
    static final String TEST_CONTACT_NAME_2 = "Mincho_Penchev_the_second";
    
    static final String TEST_ROOM_NAME = "test_room";

    /**
     * The provider that we use to make a dummy server-stored contactlist
     * used for testing. The mockProvider is instantiated and registered
     * by the metacontactlist slick activator.
     */
    public static MockProvider mockProvider = null;
    /**
     * The persistent presence operation set of the default mock provider.
     */
    public static MockPersistentPresenceOperationSet mockPresOpSet = null;
    public static MockBasicInstantMessaging mockBImOpSet = null;
    public static MockMultiUserChat mockMultiChat = null;

    private static ServiceReference msgHistoryServiceRef = null;
    public static MessageHistoryService msgHistoryService = null;

    private static MockContact testContact = null;

    private static ServiceReference metaCLref = null;
    private static MetaContactListService metaClService = null;

    private static MetaContact testMetaContact = null;

    /**
     * A reference to the registration of the first mock provider.
     */
    public static ServiceRegistration mockPrServiceRegistration = null;

    private static Message[] messagesToSend = null;

    private static Date controlDate1 = null;
    private static Date controlDate2 = null;

    public TestMsgHistoryService(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(
            new TestMsgHistoryService("setupContact"));
        suite.addTest(
            new TestMsgHistoryService("writeRecords"));
        suite.addTest(
            new TestMsgHistoryService("readRecords"));
        suite.addTest(
            new TestMsgHistoryService("writeRecordsToMultiChat"));
        suite.addTest(
            new TestMsgHistoryService("readRecordsFromMultiChat"));
        suite.addTest(
            new TestMsgHistoryService("testPurgeLocalContactListCopy"));

        return suite;
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void setupContact()
    {
        // changes the history service target derictory
        System.setProperty("HistoryServiceDirectory", "test-msghistory");

        mockProvider = new MockProvider("MessageHistoryMockUser");

        //store thre presence op set of the new provider into the fixture
        Map<String, OperationSet> supportedOperationSets =
            mockProvider.getSupportedOperationSets();

        //get the operation set presence here.
        mockPresOpSet =
            (MockPersistentPresenceOperationSet) supportedOperationSets.get(
                OperationSetPersistentPresence.class.getName());

        mockBImOpSet =
            (MockBasicInstantMessaging) supportedOperationSets.get(
                OperationSetBasicInstantMessaging.class.getName());
        
        mockMultiChat =
            (MockMultiUserChat) supportedOperationSets.get(
                OperationSetMultiUserChat.class.getName());

        msgHistoryServiceRef =
            MsgHistoryServiceLick.bc.
            getServiceReference(MessageHistoryService.class.getName());

        msgHistoryService =
            (MessageHistoryService)MsgHistoryServiceLick.bc.
                getService(msgHistoryServiceRef);

        // fill in a contact to comunicate with
        MockContactGroup root =
            (MockContactGroup)mockPresOpSet.getServerStoredContactListRoot();

        testContact = new MockContact(TEST_CONTACT_NAME_1, mockProvider);
        root.addContact(testContact);

        metaCLref = MsgHistoryServiceLick.bc.getServiceReference(
            MetaContactListService.class.getName());

        metaClService =
            (MetaContactListService)MsgHistoryServiceLick.bc.getService(metaCLref);

       System.setProperty(MetaContactListService.PROVIDER_MASK_PROPERTY, "1");

       Hashtable<String, String> mockProvProperties = new Hashtable<String, String>();
       mockProvProperties.put(ProtocolProviderFactory.PROTOCOL
                              , mockProvider.getProtocolName());
       mockProvProperties.put(MetaContactListService.PROVIDER_MASK_PROPERTY,
                              "1");

       mockPrServiceRegistration =
           MsgHistoryServiceLick.bc.registerService(
               ProtocolProviderService.class.getName(),
               mockProvider,
               mockProvProperties);
       logger.debug("Registered a mock protocol provider! ");

       testMetaContact = metaClService.getRoot().
            getMetaContact(mockProvider, TEST_CONTACT_NAME_1);
       
       // add one more contact as specific problems may happen only when 
       // more than one contact is in the metacontact
        metaClService.addNewContactToMetaContact(
            mockProvider, testMetaContact, TEST_CONTACT_NAME_2);

        messagesToSend = new Message[]
            {
                mockBImOpSet.createMessage("test message word1-" + Math.random()),
                mockBImOpSet.createMessage("test message word2" + Math.random()),
                mockBImOpSet.createMessage("test message word3" + Math.random()),
                mockBImOpSet.createMessage("test message word4" + Math.random()),
                mockBImOpSet.createMessage("test message word5" + Math.random())
            };
    }

    /**
     *  First send the messages
     */
    public void writeRecords()
    {

        logger.info("write records ");

        assertNotNull("No metacontact", testMetaContact);

        // First deliver message, so they are stored by the message history service
        mockBImOpSet.deliverMessage(TEST_CONTACT_NAME_1, messagesToSend[0]);
        mockBImOpSet.deliverMessage(TEST_CONTACT_NAME_2, messagesToSend[0]);

        this.controlDate1 = new Date();

        Object lock = new Object();
        synchronized (lock)
        {
            // wait a moment
            try
            {
                lock.wait(200);
            }
            catch (InterruptedException ex)
            {
            }
        }

        mockBImOpSet.deliverMessage(TEST_CONTACT_NAME_1, messagesToSend[1]);

        mockBImOpSet.deliverMessage(TEST_CONTACT_NAME_2, messagesToSend[2]);

        this.controlDate2 = new Date();
        synchronized (lock)
        {
            // wait a moment
            try
            {
                lock.wait(200);
            }
            catch (InterruptedException ex)
            {
            }
        }

        mockBImOpSet.deliverMessage(TEST_CONTACT_NAME_1, messagesToSend[3]);

        mockBImOpSet.deliverMessage(TEST_CONTACT_NAME_2, messagesToSend[4]);

    }

    /**
     * tests all read methods (finders)
     */
    public void readRecords()
    {
        /**
         * This matches all written messages, they are minimum 5
         */
        Collection<EventObject> rs
            = msgHistoryService.findByKeyword(testMetaContact, "test");

        assertTrue("Nothing found findByKeyword ", !rs.isEmpty());

        List<String> msgs = getMessages(rs);

        assertTrue("Messages too few - findByKeyword", msgs.size() >= 5);

        /**
         * Will test case sensitive and insensitive search
         */
        rs = msgHistoryService.findByKeyword(testMetaContact, "Test", false);

        assertTrue(
            "Nothing found findByKeyword caseINsensitive search",
            !rs.isEmpty());

        msgs = getMessages(rs);

        assertTrue("Messages too few - findByKeyword", msgs.size() >= 5);

        rs = msgHistoryService.findByKeyword(testMetaContact, "Test", true);

        assertFalse(
            "Something found by findByKeyword casesensitive search",
            !rs.isEmpty());

        /**
         * This must match also many messages, as tests are run many times
         * but the minimum is 3
         */
        rs = msgHistoryService.findByEndDate(testMetaContact, controlDate2);

        assertTrue("Nothing found findByEndDate", !rs.isEmpty());

        msgs = getMessages(rs);

        assertTrue("Messages too few - findByEndDate", msgs.size() >= 3);

        /**
         * This must find also many messages but atleast one
         */
        rs = msgHistoryService.findByKeywords(
            testMetaContact,
            new String[]{"test", "word2"});

        assertTrue("Nothing found findByKeywords", !rs.isEmpty());
        msgs = getMessages(rs);
        assertTrue("Messages too few - findByKeywords", msgs.size() >= 1);

        /**
         * Nothing to be found
         */
        rs = msgHistoryService.findByKeywords(
            testMetaContact,
            new String[]{"test1", "word2"});

        assertFalse("Something found findByKeywords", !rs.isEmpty());

        /**
         * must find 2 messages
         */
        rs = msgHistoryService.findByPeriod(
            testMetaContact, controlDate1, controlDate2);

        assertTrue("Nothing found findByPeriod", !rs.isEmpty());

        msgs = getMessages(rs);

        assertEquals("Messages must be 2", msgs.size(), 2);

        assertTrue("Message no found",
                   msgs.contains(messagesToSend[1].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[2].getContent()));

        /**
         * must find 1 record
         */
        rs = msgHistoryService.findByPeriod(
            testMetaContact, controlDate1, controlDate2, new String[]{"word2"});

        assertTrue("Nothing found findByPeriod", !rs.isEmpty());

        msgs = getMessages(rs);

        assertEquals("Messages must be 1", msgs.size(), 1);
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[1].getContent()));

        /**
         * must find 2 records
         */
        rs = msgHistoryService.findByStartDate(testMetaContact, controlDate2);

        assertTrue("Nothing found findByStartDate", !rs.isEmpty());
        msgs = getMessages(rs);
        assertEquals("Messages must be 2", msgs.size(), 2);
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[3].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[4].getContent()));

        /**
         * Must return exactly the last 3 messages
         */
        rs = msgHistoryService.findLast(testMetaContact, 3);

        assertTrue("Nothing found 8", !rs.isEmpty());
        msgs = getMessages(rs);
        assertEquals("Messages must be 3", msgs.size(), 3);
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[2].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[3].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[4].getContent()));

        /**
         * Must return exactly the 3 messages after controlDate1
         */
        rs = msgHistoryService.findFirstMessagesAfter(testMetaContact, controlDate1, 3);

        assertTrue("Nothing found 9", !rs.isEmpty());
        msgs = getMessages(rs);
        assertEquals("Messages must be 3", msgs.size(), 3);
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[1].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[2].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[3].getContent()));

        /**
         * Must return exactly the 3 messages before controlDate2
         */
        rs = msgHistoryService.findLastMessagesBefore(testMetaContact, controlDate2, 3);

        assertTrue("Nothing found 10", !rs.isEmpty());
        msgs = getMessages(rs);
        assertEquals("Messages must be 3", msgs.size(), 3);
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[0].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[1].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[2].getContent()));
    }
    
    public void writeRecordsToMultiChat()
    {
        try
        {
            ChatRoom room = mockMultiChat.createChatRoom("test_room", null);
            room.join();  

//            ChatRoom room = mockMultiChat.findRoom(TEST_ROOM_NAME);
//            room.joinAs(TEST_CONTACT_NAME);
            
            // First deliver message, so they are stored by the message history service
            room.sendMessage(messagesToSend[0]);

            this.controlDate1 = new Date();

            Object lock = new Object();
            synchronized (lock)
            {
                // wait a moment
                try
                {
                    lock.wait(200);
                }
                catch (InterruptedException ex)
                {
                }
            }

            room.sendMessage(messagesToSend[1]);

            room.sendMessage(messagesToSend[2]);

            this.controlDate2 = new Date();
            synchronized (lock)
            {
                // wait a moment
                try
                {
                    lock.wait(200);
                }
                catch (InterruptedException ex)
                {
                }
            }

            room.sendMessage(messagesToSend[3]);

            room.sendMessage(messagesToSend[4]);
        }
        catch(OperationFailedException ex)
        {
            fail("Failed to create room : " + ex.getMessage());
            logger.error("Failed to create room", ex);
        }
        catch(OperationNotSupportedException ex)
        {
            fail("Failed to create room : " + ex.getMessage());
            logger.error("Failed to create room", ex);
        }
    }
    
    /**
     * tests all read methods (finders)
     */
    public void readRecordsFromMultiChat()
    {
        ChatRoom room = null;
    
        try
        {
            room = mockMultiChat.findRoom(TEST_ROOM_NAME);
            
        }catch(Exception ex)
        {
            fail("Cannot find room!" + ex.getMessage());
        }

        /**
         * This matches all written messages, they are minimum 5
         */
        Collection<EventObject> rs
            = msgHistoryService.findByKeyword(room, "test");

        assertTrue("Nothing found findByKeyword ", !rs.isEmpty());

        List<String> msgs = getChatMessages(rs);

        assertTrue("Messages too few - findByKeyword", msgs.size() >= 5);

        /**
         * Will test case sensitive and insensitive search
         */
        rs = msgHistoryService.findByKeyword(room, "Test", false);

        assertTrue("Nothing found findByKeyword caseINsensitive search", !rs.isEmpty());

        msgs = getChatMessages(rs);

        assertTrue("Messages too few - findByKeyword", msgs.size() >= 5);

        rs = msgHistoryService.findByKeyword(room, "Test", true);

        assertFalse("Something found by findByKeyword casesensitive search", !rs.isEmpty());

        /**
         * This must match also many messages, as tests are run many times
         * but the minimum is 3
         */
        rs = msgHistoryService.findByEndDate(room, controlDate2);

        assertTrue("Nothing found findByEndDate", !rs.isEmpty());

        msgs = getChatMessages(rs);

        assertTrue("Messages too few - findByEndDate", msgs.size() >= 3);

        /**
         * This must find also many messages but atleast one
         */
        rs = msgHistoryService.findByKeywords(
            room,
            new String[]{"test", "word2"});

        assertTrue("Nothing found findByKeywords", !rs.isEmpty());
        msgs = getChatMessages(rs);
        assertTrue("Messages too few - findByKeywords", msgs.size() >= 1);

        /**
         * Nothing to be found
         */
        rs = msgHistoryService.findByKeywords(
            room,
            new String[]{"test1", "word2"});

        assertFalse("Something found findByKeywords", !rs.isEmpty());

        /**
         * must find 2 messages
         */
        rs = msgHistoryService.findByPeriod(
            room, controlDate1, controlDate2);

        assertTrue("Nothing found findByPeriod", !rs.isEmpty());

        msgs = getChatMessages(rs);

        assertEquals("Messages must be 2", msgs.size(), 2);

        assertTrue("Message no found",
                   msgs.contains(messagesToSend[1].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[2].getContent()));

        /**
         * must find 1 record
         */
        rs = msgHistoryService.findByPeriod(
            room, controlDate1, controlDate2, new String[]{"word2"});

        assertTrue("Nothing found findByPeriod", !rs.isEmpty());

        msgs = getChatMessages(rs);

        assertEquals("Messages must be 1", msgs.size(), 1);
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[1].getContent()));

        /**
         * must find 2 records
         */
        rs = msgHistoryService.findByStartDate(room, controlDate2);

        assertTrue("Nothing found findByStartDate", !rs.isEmpty());
        msgs = getChatMessages(rs);
        assertEquals("Messages must be 2", msgs.size(), 2);
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[3].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[4].getContent()));

        /**
         * Must return exactly the last 3 messages
         */
        rs = msgHistoryService.findLast(room, 3);

        assertTrue("Nothing found 8", !rs.isEmpty());
        msgs = getChatMessages(rs);
        assertEquals("Messages must be 3", msgs.size(), 3);
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[2].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[3].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[4].getContent()));

        /**
         * Must return exactly the 3 messages after controlDate1
         */
        rs = msgHistoryService.findFirstMessagesAfter(room, controlDate1, 3);

        assertTrue("Nothing found 9", !rs.isEmpty());
        msgs = getChatMessages(rs);
        assertEquals("Messages must be 3", msgs.size(), 3);
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[1].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[2].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[3].getContent()));

        /**
         * Must return exactly the 3 messages before controlDate2
         */
        rs = msgHistoryService.findLastMessagesBefore(room, controlDate2, 3);

        assertTrue("Nothing found 10", !rs.isEmpty());
        msgs = getChatMessages(rs);
        assertEquals("Messages must be 3", msgs.size(), 3);
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[0].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[1].getContent()));
        assertTrue("Message no found",
                   msgs.contains(messagesToSend[2].getContent()));
    }

    /**
     * Removes the locally stored contact list copy. The purpose of this is to
     * leave the local list empty for a next round of testing.
     */
    public void testPurgeLocalContactListCopy()
    {
        metaClService.purgeLocallyStoredContactListCopy();
    }

    private List<String> getMessages(Collection<EventObject> rs)
    {
        List<String> result = new Vector<String>();

        for (EventObject item : rs)
        {
            if(item instanceof MessageDeliveredEvent)
                result.add(
                        ((MessageDeliveredEvent)item)
                            .getSourceMessage().getContent());
            else if(item instanceof MessageReceivedEvent)
                result.add(
                        ((MessageReceivedEvent)item)
                            .getSourceMessage().getContent());
        }

        return result;
    }
    
    private List<String> getChatMessages(Collection<EventObject> rs)
    {
        List<String> result = new Vector<String>();

        for (EventObject item : rs)
        {
            if(item instanceof ChatRoomMessageDeliveredEvent)
                result.add(((ChatRoomMessageDeliveredEvent)item).
                    getMessage().getContent());
            else
                if(item instanceof ChatRoomMessageReceivedEvent)
                    result.add(((ChatRoomMessageReceivedEvent)item).
                        getMessage().getContent());
        }

        return result;
    }

//    private void dumpResult(QueryResultSet rs)
//    {
//        while (rs.hasNext())
//        {
//            HistoryRecord hr = (HistoryRecord)rs.next();
//            logger.info("----------------------");
//
//            for (int i = 0; i < hr.getPropertyNames().length; i++)
//            {
//                logger.info(hr.getPropertyNames()[i] + " => " + hr.getPropertyValues()[i]);
//            }
//
//            logger.info("----------------------");
//        }
//    }
}
