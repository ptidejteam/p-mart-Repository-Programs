/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.protocol.event;

import java.util.*;

import net.java.sip.communicator.service.protocol.*;

/**
 * <tt>ChatRoomMessageDeliveredEvent</tt>s confirm successful delivery of an
 * instant message.
 *
 * @author Emil Ivov
 */
public class ChatRoomMessageDeliveryFailedEvent
    extends EventObject
{
    /**
      * The chat room member that this message has been sent to.
      */
     private ChatRoomMember to = null;

     /**
      * Set when no other error code can describe the exception that occurred.
      */
     public static final int UNKNOWN_ERROR = 1;

     /**
      * Set when delivery fails due to a failure in network communications or
      * a transport error.
      */
     public static final int NETWORK_FAILURE = 2;

     /**
      * Set to indicate that delivery has failed because the provider was not
      * registered.
      */
     public static final int PROVIDER_NOT_REGISTERED = 3;

     /**
      * Set when delivery fails for implementation specific reasons.
      */
     public static final int INTERNAL_ERROR = 4;

     /**
      * Set when delivery fails because we're trying to send a message to a
      * contact that is currently offline and the server does not support
      * offline messages.
      */
     public static final int OFFLINE_MESSAGES_NOT_SUPPORTED = 5;

     /**
      * An error code indicating the reason for the failure of this delivery.
      */
     private int errorCode = UNKNOWN_ERROR;

     /**
      * A timestamp indicating the exact date when the event occurred.
      */
     private Date timestamp = null;

     /**
      * The received <tt>Message</tt>.
      */
     private Message message = null;

     /**
      * Creates a <tt>ChatRoomMessageDeliveryFailedEvent</tt> indicating failure
      * of delivery of a message to the specified <tt>ChatRoomMember</tt> in the
      * specified <tt>ChatRoom</tt>.
      *
      * @param source the <tt>ChatRoom</tt> in which the message was sent
      * @param to the <tt>ChatRoomMember</tt> that this message was sent to.
      * @param errorCode an errorCode indicating the reason of the failure.
      * @param timestamp the exacte Date when it was determined that delivery
      * had failed.
      * @param message the received <tt>Message</tt>.
      */
     public ChatRoomMessageDeliveryFailedEvent(ChatRoom source,
                                               ChatRoomMember to,
                                               int errorCode,
                                               Date timestamp,
                                               Message message)
     {
         super(source);

         this.to = to;
         this.errorCode = errorCode;
         this.timestamp = timestamp;
         this.message = message;
     }

     /**
      * Returns a reference to the <tt>ChatRoomMember</tt> that the source
      * (failed) <tt>Message</tt> was sent to.
      *
      * @return a reference to the <tt>ChatRoomMember</tt> that the source failed
      * <tt>Message</tt> was sent to.
      */
     public ChatRoomMember getDestinationChatRoomMember()
     {
         return to;
     }
     
     /**
      * Returns the received message.
      * @return the <tt>Message</tt> that triggered this event.
      */
     public Message getMessage()
     {
         return message;
     }

     /**
      * Returns an error code descibing the reason for the failure of the
      * message delivery.
      * @return an error code descibing the reason for the failure of the
      * message delivery.
      */
     public int getErrorCode()
     {
        return errorCode;
     }


     /**
      * A timestamp indicating the exact date when the event ocurred (in this
      * case it is the moment when it was determined that message delivery
      * has failed).
      * @return a Date indicating when the event ocurred.
      */
     public Date getTimestamp()
     {
         return timestamp;
     }

     /**
      * Returns the <tt>ChatRoom</tt> that triggered this event.
      * @return the <tt>ChatRoom</tt> that triggered this event.
      */
     public ChatRoom getSourceChatRoom()
     {
         return (ChatRoom) getSource();
     }
}
