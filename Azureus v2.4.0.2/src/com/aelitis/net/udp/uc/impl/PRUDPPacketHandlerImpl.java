/*
 * File    : PRUDPPacketReceiverImpl.java
 * Created : 20-Jan-2004
 * By      : parg
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

package com.aelitis.net.udp.uc.impl;

/**
 * @author parg
 *
 */

import java.util.*;
import java.io.*;
import java.net.*;

import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.util.*;

import com.aelitis.net.udp.uc.PRUDPPacket;
import com.aelitis.net.udp.uc.PRUDPPacketHandler;
import com.aelitis.net.udp.uc.PRUDPPacketHandlerException;
import com.aelitis.net.udp.uc.PRUDPPacketHandlerStats;
import com.aelitis.net.udp.uc.PRUDPPacketReceiver;
import com.aelitis.net.udp.uc.PRUDPPacketReply;
import com.aelitis.net.udp.uc.PRUDPPacketRequest;
import com.aelitis.net.udp.uc.PRUDPRequestHandler;

import org.bouncycastle.util.encoders.Base64;

public class 
PRUDPPacketHandlerImpl
	implements PRUDPPacketHandler
{	
	private static final LogIDs LOGID = LogIDs.NET;
	
	private boolean			TRACE_REQUESTS	= false;
	
	private static final long	MAX_SEND_QUEUE_DATA_SIZE	= 2*1024*1024;
	private static final long	MAX_RECV_QUEUE_DATA_SIZE	= 1*1024*1024;
	
	private int				port;
	private DatagramSocket	socket;
	
	private PRUDPRequestHandler	request_handler;
	
	private PRUDPPacketHandlerStatsImpl	stats = new PRUDPPacketHandlerStatsImpl( this );
	
	
	private Map			requests = new HashMap();
	private AEMonitor	requests_mon	= new AEMonitor( "PRUDPPH:req" );
	
	
	private AEMonitor	send_queue_mon	= new AEMonitor( "PRUDPPH:sd" );
	private long		send_queue_data_size;
	private List[]		send_queues		= new List[]{ new LinkedList(),new LinkedList(),new LinkedList()};
	private AESemaphore	send_queue_sem	= new AESemaphore( "PRUDPPH:sq" );
	private AEThread	send_thread;
	
	private AEMonitor	recv_queue_mon	= new AEMonitor( "PRUDPPH:rq" );
	private long		recv_queue_data_size;
	private List		recv_queue		= new ArrayList();
	private AESemaphore	recv_queue_sem	= new AESemaphore( "PRUDPPH:rq" );
	private AEThread	recv_thread;
	
	private int			send_delay				= 0;
	private int			receive_delay			= 0;
	private int			queued_request_timeout	= 0;
	
	private long		total_requests_received;
	private long		total_requests_processed;
	private long		total_replies;
	private long		last_error_report;
	
	protected
	PRUDPPacketHandlerImpl(
		int		_port )
	{
		port		= _port;
		
		final AESemaphore init_sem = new AESemaphore("PRUDPPacketHandler");
		
		Thread t = new AEThread( "PRUDPPacketReciever:".concat(String.valueOf(port)))
			{
				public void
				runSupport()
				{
					receiveLoop(init_sem);
				}
			};
		
		t.setDaemon(true);
		
		t.start();
		
		SimpleTimer.addPeriodicEvent(
				5000,
				new TimerEventPerformer()
				{
					public void
					perform(
						TimerEvent	event )
					{
						checkTimeouts();
					}
				});
		
		init_sem.reserve();
	}
	
	public void
	setRequestHandler(
		PRUDPRequestHandler		_request_handler )
	{
		if ( request_handler != null ){
		
			if ( _request_handler != null ){
				
					// if we need to support this then the handler will have to be associated
					// with a message type map, or we chain together and give each handler
					// a bite at processing the message
				
				throw( new RuntimeException( "Multiple handlers per endpoint not supported" ));
			}
		}
		
		request_handler	= _request_handler;
	}
	
	public PRUDPRequestHandler
	getRequestHandler()
	{
		return( request_handler );
	}
	
	public int
	getPort()
	{
		return( port );
	}
	
	protected void
	receiveLoop(
		AESemaphore	init_sem )
	{
		try{
			String bind_ip = COConfigurationManager.getStringParameter("Bind IP", "");
			
			InetSocketAddress	address;
			
			if ( bind_ip.length() == 0 ){
				
				address = new InetSocketAddress("127.0.0.1",port);
				
				socket = new DatagramSocket( port );
				
			}else{
				
				address = new InetSocketAddress(InetAddress.getByName(bind_ip), port);
				
				socket = new DatagramSocket( address );		
			}
					
			socket.setReuseAddress(true);
			
			socket.setSoTimeout( PRUDPPacket.DEFAULT_UDP_TIMEOUT );
			
			init_sem.release();
			
			if (Logger.isEnabled())
				Logger.log(new LogEvent(LOGID,
						"PRUDPPacketReceiver: receiver established on port " + port)); 
	
			byte[] buffer = new byte[PRUDPPacket.MAX_PACKET_SIZE];
			
			long	successful_accepts 	= 0;
			long	failed_accepts		= 0;
			
			while(true){
				
				try{
						
					DatagramPacket packet = new DatagramPacket( buffer, buffer.length, address );
					
					socket.receive( packet );
					
					successful_accepts++;
					
					failed_accepts = 0;
					
					process( packet );
				
				}catch( SocketTimeoutException e ){
										
				}catch( Throwable e ){
						
					failed_accepts++;
					
					if (Logger.isEnabled())
						Logger.log(new LogEvent(LOGID,
								"PRUDPPacketReceiver: receive failed on port " + port, e)); 

					if (( failed_accepts > 100 && successful_accepts == 0 ) || failed_accepts > 1000 ){						
		
						Logger.logTextResource(new LogAlert(LogAlert.UNREPEATABLE,
								LogAlert.AT_ERROR, "Network.alert.acceptfail"), new String[] {
								"" + port, "UDP" });
										
							// break, sometimes get a screaming loop. e.g.
						/*
						[2:01:55]  DEBUG::Tue Dec 07 02:01:55 EST 2004
						[2:01:55]    java.net.SocketException: Socket operation on nonsocket: timeout in datagram socket peek
						[2:01:55]  	at java.net.PlainDatagramSocketImpl.peekData(Native Method)
						[2:01:55]  	at java.net.DatagramSocket.receive(Unknown Source)
						[2:01:55]  	at org.gudy.azureus2.core3.tracker.server.impl.udp.TRTrackerServerUDP.recvLoop(TRTrackerServerUDP.java:118)
						[2:01:55]  	at org.gudy.azureus2.core3.tracker.server.impl.udp.TRTrackerServerUDP$1.runSupport(TRTrackerServerUDP.java:90)
						[2:01:55]  	at org.gudy.azureus2.core3.util.AEThread.run(AEThread.java:45)
						*/
						
						break;
					}					
				}
			}
		}catch( Throwable e ){
			Logger.logTextResource(new LogAlert(LogAlert.UNREPEATABLE,
					LogAlert.AT_ERROR, "Tracker.alert.listenfail"), new String[] { "UDP:"
					+ port });
			
			Logger.log(new LogEvent(LOGID, "PRUDPPacketReceiver: "
					+ "DatagramSocket bind failed on port " + port, e));
			
		}finally{
			
			init_sem.release();
		}
	}
	
	protected void
	checkTimeouts()
	{
		long	now = SystemTime.getCurrentTime();
			
		List	timed_out = new ArrayList();
		
		try{
			requests_mon.enter();
			
			Iterator it = requests.values().iterator();
			
			while( it.hasNext()){
				
				PRUDPPacketHandlerRequestImpl	request = (PRUDPPacketHandlerRequestImpl)it.next();
				
				long	sent_time = request.getSendTime();
				
				if ( 	sent_time != 0 &&
						now - sent_time >= request.getTimeout()){
				
					it.remove();

					stats.requestTimedOut();
					
					timed_out.add( request );
				}
			}
		}finally{
			
			requests_mon.exit();
		}
		
		for (int i=0;i<timed_out.size();i++){
			
			PRUDPPacketHandlerRequestImpl	request = (PRUDPPacketHandlerRequestImpl)timed_out.get(i);
			
			if ( TRACE_REQUESTS ){
				if (Logger.isEnabled())
					Logger.log(new LogEvent(LOGID, LogEvent.LT_ERROR,
							"PRUDPPacketHandler: request timeout")); 
			}
				// don't change the text of this message, it's used elsewhere
			
			try{
				request.setException(new PRUDPPacketHandlerException("timed out"));
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
		}
	}
	
	protected void
	process(
		DatagramPacket	dg_packet )
	{
		try{
				// HACK alert. Due to the form of the tracker UDP protocol (no common
				// header for requests and replies) we enforce a rule. All connection ids
				// must have their MSB set. As requests always start with the action, which
				// always has the MSB clear, we can use this to differentiate. 
			
			byte[]	packet_data = dg_packet.getData();
			int		packet_len	= dg_packet.getLength();
			
			// System.out.println( "received:" + packet_len );
			
			PRUDPPacket packet;
			
			boolean	request_packet;
						
			stats.packetReceived(packet_len);
			
			if ( ( packet_data[0]&0x80 ) == 0 ){
				
				request_packet	= false;
				
				packet = PRUDPPacketReply.deserialiseReply( 
					this,
					new DataInputStream(new ByteArrayInputStream( packet_data, 0, packet_len)));
				
			}else{
				
				request_packet	= true;
				
				packet = PRUDPPacketRequest.deserialiseRequest( 
						this,
						new DataInputStream(new ByteArrayInputStream( packet_data, 0, packet_len)));
		
			}
			
			packet.setSerialisedSize( packet_len );
			
			packet.setAddress( (InetSocketAddress)dg_packet.getSocketAddress());
			
			if ( request_packet ){
					
				total_requests_received++;
				
				// System.out.println( "Incoming from " + dg_packet.getAddress());
				
				if ( TRACE_REQUESTS ){
					Logger.log(new LogEvent(LOGID,
							"PRUDPPacketHandler: request packet received: "
									+ packet.getString())); 
				}
				
				if ( receive_delay > 0 ){
					
						// we take the processing offline so that these incoming requests don't
						// interfere with replies to outgoing requests
					
					try{
						recv_queue_mon.enter();
						
						if ( recv_queue_data_size > MAX_RECV_QUEUE_DATA_SIZE ){
							
							long	now = SystemTime.getCurrentTime();
							
							if ( now - last_error_report > 30000 ){
								
								last_error_report	= now;
								
								Debug.out( "Receive queue size limit exceeded (" + 
											MAX_RECV_QUEUE_DATA_SIZE + "), dropping request packet [" +
											total_requests_received + "/" + total_requests_processed + ":" + total_replies + "]");
							}
							
						}else if ( receive_delay * recv_queue.size() > queued_request_timeout ){
							
								// by the time this request gets processed it'll have timed out
								// in the caller anyway, so discard it
							
							long	now = SystemTime.getCurrentTime();
							
							if ( now - last_error_report > 30000 ){
								
								last_error_report	= now;

								Debug.out( "Receive queue entry limit exceeded (" + 
											recv_queue.size() + "), dropping request packet ]" +
											total_requests_received + "/" + total_requests_processed + ":" + total_replies + "]");
							}
							
						}else{
							
							recv_queue.add( new Object[]{ packet, new Integer( dg_packet.getLength()) });
											
							recv_queue_data_size	+= dg_packet.getLength();
														
							recv_queue_sem.release();
					
							if ( recv_thread == null ){
								
								recv_thread = 
									new AEThread( "PRUDPPacketHandler:receiver" )
									{
										public void
										runSupport()
										{
											while( true ){
												
												try{
													recv_queue_sem.reserve();
													
													Object[]	data;										
													
													try{
														recv_queue_mon.enter();
													
														data = (Object[])recv_queue.remove(0);
														
														total_requests_processed++;
														
													}finally{
														
														recv_queue_mon.exit();
													}
													
													PRUDPPacketRequest	p = (PRUDPPacketRequest)data[0];
													
													recv_queue_data_size -= ((Integer)data[1]).intValue();
													
													PRUDPRequestHandler	handler = request_handler;
													
													if ( handler != null ){
														
														handler.process( p );
													
														Thread.sleep( receive_delay );
													}
													
												}catch( Throwable e ){
													
													Debug.printStackTrace(e);
												}
											}
										}
									};
								
								recv_thread.setDaemon( true );
								
								recv_thread.start();
							}
						}
					}finally{
						
						recv_queue_mon.exit();
					}
				}else{
				
					PRUDPRequestHandler	handler = request_handler;
					
					if ( handler != null ){
						
						handler.process( (PRUDPPacketRequest)packet );
					}
				}
	
			}else{
				
				total_replies++;
				
				if ( TRACE_REQUESTS ){
					Logger.log(new LogEvent(LOGID,
							"PRUDPPacketHandler: reply packet received: "
									+ packet.getString())); 
				}
				
				PRUDPPacketHandlerRequestImpl	request;
				
				try{
					requests_mon.enter();
					
					if ( packet.hasContinuation()){
					
							// don't remove the request if there are more replies to come
						
						request = (PRUDPPacketHandlerRequestImpl)requests.get(new Integer(packet.getTransactionId()));

					}else{
					
						request = (PRUDPPacketHandlerRequestImpl)requests.remove(new Integer(packet.getTransactionId()));
					}

				}finally{
					
					requests_mon.exit();
				}
				
				if ( request == null ){
				
					if ( TRACE_REQUESTS ){
						Logger.log(new LogEvent(LOGID, LogEvent.LT_ERROR,
								"PRUDPPacketReceiver: unmatched reply received, discarding:"
										+ packet.getString()));
					}
				
				}else{
				
					request.setReply( packet, (InetSocketAddress)dg_packet.getSocketAddress());
				}
			}
		}catch( Throwable e ){
			
				// if someone's sending us junk we just log and continue
			
			if ( e instanceof IOException ){
			
					// generally uninteresting
				
			}else{
							
				Logger.log(new LogEvent(LOGID, "", e));
			}
		}
	}
	
	public PRUDPPacket
	sendAndReceive(
		PRUDPPacket				request_packet,
		InetSocketAddress		destination_address )
	
		throws PRUDPPacketHandlerException
	{
		return( sendAndReceive( null,request_packet, destination_address ));
	}
	
	public PRUDPPacket
	sendAndReceive(
		PasswordAuthentication	auth,
		PRUDPPacket				request_packet,
		InetSocketAddress		destination_address )
	
		throws PRUDPPacketHandlerException
	{
		PRUDPPacketHandlerRequestImpl	request = 
			sendAndReceive( auth, request_packet,destination_address, null, PRUDPPacket.DEFAULT_UDP_TIMEOUT, PRUDPPacketHandler.PRIORITY_MEDIUM );
		
		return( request.getReply());
	}
	
	public void
	sendAndReceive(
		PRUDPPacket					request_packet,
		InetSocketAddress			destination_address,
		PRUDPPacketReceiver			receiver,
		long						timeout,
		int							priority )
	
		throws PRUDPPacketHandlerException
	{
		sendAndReceive( null, request_packet, destination_address, receiver, timeout, priority );
	}
	
	public PRUDPPacketHandlerRequestImpl
	sendAndReceive(
		PasswordAuthentication		auth,
		PRUDPPacket					request_packet,
		InetSocketAddress			destination_address,
		PRUDPPacketReceiver			receiver,
		long						timeout,
		int							priority )
	
		throws PRUDPPacketHandlerException
	{
		if ( socket == null ){
			
			throw( new PRUDPPacketHandlerException( "Transport unavailable" ));
		}
		
		try{
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			
			DataOutputStream os = new DataOutputStream( baos );
					
			request_packet.serialise(os);
			
			byte[]	buffer = baos.toByteArray();
			
			request_packet.setSerialisedSize( buffer.length );
			
			if ( auth != null ){
				
				//<parg_home> so <new_packet> = <old_packet> + <user_padded_to_8_bytes> + <hash>
				//<parg_home> where <hash> = first 8 bytes of sha1(<old_packet> + <user_padded_to_8> + sha1(pass))
				//<XTF> Yes
				
				SHA1Hasher hasher = new SHA1Hasher();

				String	user_name 	= auth.getUserName();
				String	password	= new String(auth.getPassword());
				
				byte[]	sha1_password;
				
				if ( user_name.equals( "<internal>")){
					
					sha1_password = Base64.decode(password);

				}else{
					
					sha1_password = hasher.calculateHash(password.getBytes());			
				}
				
				byte[]	user_bytes = new byte[8];
				
				Arrays.fill( user_bytes, (byte)0);
				
				for (int i=0;i<user_bytes.length&&i<user_name.length();i++){
					
					user_bytes[i] = (byte)user_name.charAt(i);
				}
				
				hasher = new SHA1Hasher();
				
				hasher.update( buffer );
				hasher.update( user_bytes );
				hasher.update( sha1_password );
				
				byte[]	overall_hash = hasher.getDigest();
				
				//System.out.println("PRUDPHandler - auth = " + auth.getUserName() + "/" + new String(auth.getPassword()));
								
				baos.write( user_bytes );
				baos.write( overall_hash, 0, 8 );
				
				buffer = baos.toByteArray();
			}
			
			DatagramPacket dg_packet = new DatagramPacket(buffer, buffer.length, destination_address );
			
			PRUDPPacketHandlerRequestImpl	request = new PRUDPPacketHandlerRequestImpl( receiver, timeout );
		
			try{
				requests_mon.enter();
					
				requests.put( new Integer( request_packet.getTransactionId()), request );
				
			}finally{
				
				requests_mon.exit();
			}
			
			try{
				// System.out.println( "Outgoing to " + dg_packet.getAddress());

				if ( send_delay > 0 ){
									
					try{
						send_queue_mon.enter();
						
						if ( send_queue_data_size > MAX_SEND_QUEUE_DATA_SIZE ){
														
							request.sent();
							
								// synchronous write holding lock to block senders
							
							socket.send( dg_packet );
							
							stats.packetSent( buffer.length );
							
							if ( TRACE_REQUESTS ){
								Logger.log(new LogEvent(LOGID,
										"PRUDPPacketHandler: request packet sent to "
												+ destination_address + ": "
												+ request_packet.getString()));
							}
								
							Thread.sleep( send_delay );
							
						}else{
							
							send_queue_data_size	+= dg_packet.getLength();
								
							send_queues[priority].add( new Object[]{ dg_packet, request });
							
							if ( TRACE_REQUESTS ){
								
								String	str = "";
								
								for (int i=0;i<send_queues.length;i++){
									str += (i==0?"":",") + send_queues[i].size();
								}
								System.out.println( "send queue sizes: " + str );
							}
							
							send_queue_sem.release();
					
							if ( send_thread == null ){
								
								send_thread = 
									new AEThread( "PRUDPPacketHandler:sender" )
									{
										public void
										runSupport()
										{
											int[]		consecutive_sends = new int[send_queues.length];
											
											while( true ){
												
												try{
													send_queue_sem.reserve();
													
													Object[]	data;
													int			selected_priority	= 0;
													
													try{
														send_queue_mon.enter();
													
															// invariant: at least one queue must have an entry
														
														for (int i=0;i<send_queues.length;i++){
															
															List	queue = send_queues[i];
															
															int	queue_size = queue.size();
															
															if ( queue_size > 0 ){
																
																selected_priority	= i;
																
																if ( 	consecutive_sends[i] >= 4 ||
																		(	i < send_queues.length - 1 &&
																			send_queues[i+1].size() - queue_size > 500 )){	
																	
																		// too many consecutive or too imbalanced, see if there are
																		// lower priority queues with entries
																	
																	consecutive_sends[i]	= 0;
																	
																}else{
																	
																	consecutive_sends[i]++;
																	
																	break;
																}
															}else{
																
																consecutive_sends[i]	= 0;
															}
														}
														
														data = (Object[])send_queues[selected_priority].remove(0);
														
													}finally{
														
														send_queue_mon.exit();
													}
																									
													DatagramPacket				p	= (DatagramPacket)data[0];
													PRUDPPacketHandlerRequestImpl	r	= (PRUDPPacketHandlerRequestImpl)data[1];

														// mark as sent before sending in case send fails
														// and we then rely on timeout to pick this up
													
													send_queue_data_size	-= p.getLength();
													
													r.sent();
													
													socket.send( p );
													
													stats.packetSent( p.getLength() );
							
													if ( TRACE_REQUESTS ){
														Logger.log(new LogEvent(LOGID,
															"PRUDPPacketHandler: request packet sent to "
																	+ p.getAddress()));											
													}														
												
													long	delay = send_delay;
													
													if ( selected_priority == PRIORITY_HIGH ){
														
														delay	= delay/2;
													}
													
													Thread.sleep( delay );
													
												}catch( Throwable e ){
													// get occasional send fails, not very interesting
													Logger.log(
														new LogEvent(
															LOGID, 
															LogEvent.LT_WARNING,
															"PRUDPPacketHandler: send failed: " + Debug.getNestedExceptionMessage(e)));
												}
											}
										}
									};
								
									send_thread.setDaemon( true );
								
									send_thread.start();
							}
						}
					}finally{
						
						send_queue_mon.exit();
					}
				}else{
					
					request.sent();
					
					socket.send( dg_packet );
					
					// System.out.println( "sent:" + buffer.length );
					
					stats.packetSent( buffer.length );
					
					if ( TRACE_REQUESTS ){
						Logger.log(new LogEvent(LOGID, "PRUDPPacketHandler: "
								+ "request packet sent to " + destination_address + ": "
								+ request_packet.getString()));
					}
				}
					// if the send is ok then the request will be removed from the queue
					// either when a reply comes back or when it gets timed-out
				
				return( request );
				
			}catch( Throwable e ){
				
					// never got sent, remove it immediately
				
				try{
					requests_mon.enter();
					
					requests.remove( new Integer( request_packet.getTransactionId()));
					
				}finally{
					
					requests_mon.exit();
				}
				
				throw( e );
			}
		}catch( PRUDPPacketHandlerException e ){
			
			throw( e );
			
		}catch( Throwable e ){
			
			Logger.log(new LogEvent(LOGID,LogEvent.LT_ERROR,
					"PRUDPPacketHandler: sendAndReceive to " + destination_address + " failed: " + Debug.getNestedExceptionMessage(e))); 
			
			throw( new PRUDPPacketHandlerException( "PRUDPPacketHandler:sendAndReceive failed", e ));
		}
	}
	
	public void
	send(
		PRUDPPacket				request_packet,
		InetSocketAddress		destination_address )
	
		throws PRUDPPacketHandlerException
	{
		if ( socket == null ){
			
			throw( new PRUDPPacketHandlerException( "Transport unavailable" ));
		}
		
		try{
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			
			DataOutputStream os = new DataOutputStream( baos );
					
			request_packet.serialise(os);
			
			byte[]	buffer = baos.toByteArray();
			
			request_packet.setSerialisedSize( buffer.length );

			DatagramPacket dg_packet = new DatagramPacket(buffer, buffer.length, destination_address );
			
			// System.out.println( "Outgoing to " + dg_packet.getAddress());	
			
			if ( TRACE_REQUESTS ){
				Logger.log(new LogEvent(LOGID,
						"PRUDPPacketHandler: reply packet sent: "
								+ request_packet.getString()));
			}
			
			socket.send( dg_packet );
			
			stats.packetSent( buffer.length );
			
				// this is a reply to a request, no time delays considered here 
			
		}catch( Throwable e ){
			
			Logger.log(new LogEvent(LOGID, LogEvent.LT_ERROR, "PRUDPPacketHandler: send to " + destination_address + " failed: " + Debug.getNestedExceptionMessage(e)));
			
			throw( new PRUDPPacketHandlerException( "PRUDPPacketHandler:send failed", e ));
		}
	}
	
	public void
	setDelays(
		int		_send_delay,
		int		_receive_delay,
		int		_queued_request_timeout )
	{
		send_delay				= _send_delay;
		receive_delay			= _receive_delay;
		
			// trim a bit off this limit to include processing time
		
		queued_request_timeout	= _queued_request_timeout-5000;
		
		if ( queued_request_timeout < 5000 ){
			
			queued_request_timeout = 5000;
		}
	}
	
	public long
	getSendQueueLength()
	{
		int	res = 0;
		for (int i=0;i<send_queues.length;i++){
			res += send_queues[i].size();
		}
		return(res);
	}
	
	public long
	getReceiveQueueLength()
	{
		return( recv_queue.size());
		
	}
	public PRUDPPacketHandlerStats
	getStats()
	{
		return( stats );
	}
}
