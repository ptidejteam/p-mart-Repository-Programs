/*
 * File    : PRUDPPacketRequest.java
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

package com.aelitis.net.udp.uc;

/**
 * @author parg
 *
 */

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.gudy.azureus2.core3.util.AEMonitor;

public abstract class 
PRUDPPacketRequest
	extends PRUDPPacket
{	
	public static final int	PR_HEADER_SIZE	= 16;
	
	private static AEMonitor				class_mon = new AEMonitor( "PRUDPPacketRequest:class" );

	private static Map	packet_decoders	= new HashMap();

	protected long		connection_id;
	
	public static void
	registerDecoders(
		Map		_decoders )
	{
		try{
			class_mon.enter();
		
			Map	new_decoders = new HashMap( packet_decoders );
			
			new_decoders.putAll( _decoders );
			
			packet_decoders	= new_decoders;
			
		}finally{
			
			class_mon.exit();
		}
	}
	
	public 
	PRUDPPacketRequest(
		int		_action,
		long	_con_id )
	{
		super( _action );
		
		connection_id	= _con_id;
	}
	
	protected 
	PRUDPPacketRequest(
		int		_action,
		long	_con_id,
		int		_trans_id )
	{
		super( _action, _trans_id );
		
		connection_id	= _con_id;
	}
	
	public long
	getConnectionId()
	{
		return( connection_id );
	}
	
	public void
	serialise(
		DataOutputStream	os )
	
		throws IOException
	{
			// add to this and you need to adjust HEADER_SIZE above
		
		os.writeLong( connection_id );
		
		os.writeInt( getAction() );
		
		os.writeInt( getTransactionId() );
	}
	
	public static PRUDPPacketRequest
	deserialiseRequest(
		PRUDPPacketHandler	handler,
		DataInputStream		is )
	
		throws IOException
	{
		long		connection_id 	= is.readLong();
		int			action			= is.readInt();
		int			transaction_id	= is.readInt();
		
		PRUDPPacketRequestDecoder	decoder = (PRUDPPacketRequestDecoder)packet_decoders.get( new Integer( action ));
		
		if ( decoder == null ){
			
			throw( new IOException( "No decoder registered for action '" + action + "'" ));
		}

		return( decoder.decode( handler, is, connection_id, action, transaction_id ));
	}
	
	public String
	getString()
	{
		return( super.getString() + ":request[con=" + connection_id + ",trans=" + getTransactionId() + "]");
	}
}
