/*
 * File    : PRUDPPacketRequestAnnounce.java
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

package org.gudy.azureus2.core3.tracker.protocol.udp;

/**
 * @author parg
 *
 */

import java.io.*;

import org.gudy.azureus2.core3.util.*;

import com.aelitis.net.udp.PRUDPPacketRequest;

public class 
PRUDPPacketRequestAnnounce2
	extends PRUDPPacketRequest
{
	/*
	char m_info_hash[20];
	char m_peer_id[20];
<XTF> 	__int64 m_downloaded;
<XTF> 	__int64 m_left;
<XTF> 	__int64 m_uploaded;
<XTF> 	int m_event;
<XTF> 	int m_ipa;
		INT KEY		// V2
<XTF> 	int m_num_want;
<XTF> 	short m_port;
	*/
	
	/*
	enum t_event
	<XTF> 	{
		<XTF> 		e_none,
		<XTF> 		e_completed,
		<XTF> 		e_started,
		<XTF> 		e_stopped,
		<XTF> 	};
	<pargzzz> heh
	<XTF> 0, 1, 2, 3
	*/
	
	public static final int	EV_STARTED		= 2;
	public static final int	EV_STOPPED		= 3;
	public static final int	EV_COMPLETED	= 1;
	public static final int	EV_UPDATE		= 0;
	
	protected byte[]		hash;
	protected byte[]		peer_id;
	protected long			downloaded;
	protected int			event;
	protected int			key;
	protected int			num_want;
	protected long			left;
	protected short			port;
	protected long			uploaded;
	protected int			ip_address;
	
	public
	PRUDPPacketRequestAnnounce2(
		long				con_id )
	{
		super( PRUDPPacketTracker.ACT_REQUEST_ANNOUNCE, con_id );
	}
	
	protected
	PRUDPPacketRequestAnnounce2(
		DataInputStream		is,
		long				con_id,
		int					trans_id )
	
		throws IOException
	{
		super( PRUDPPacketTracker.ACT_REQUEST_ANNOUNCE, con_id, trans_id );
		
		hash 	= new byte[20];
		peer_id	= new byte[20];
		
		is.read( hash );
		is.read( peer_id );
		
		downloaded 	= is.readLong();
		left		= is.readLong();
		uploaded	= is.readLong();
		event 		= is.readInt();
		ip_address	= is.readInt();
		key			= is.readInt();
		num_want	= is.readInt();
		port		= is.readShort();
	}
	
	public byte[]
	getHash()
	{
		return( hash );
	}
	
	public byte[]
	getPeerId()
	{
		return( peer_id );
	}
	
	public long
	getDownloaded()
	{
		return( downloaded );
	}
	
	public int
	getEvent()
	{
		return( event );
	}
	
	public int
	getNumWant()
	{
		return( num_want );
	}
	
	public long
	getLeft()
	{
		return( left );
	}
	
	public short
	getPort()
	{
		return( port );
	}
	
	public long
	getUploaded()
	{
		return( uploaded );
	}
	
	public int
	getIPAddress()
	{
		return( ip_address );
	}
	
	public int
	getKey()
	{
		return( key );
	}
	
	public void
	setDetails(
		byte[]		_hash,
		byte[]		_peer_id,
		long		_downloaded,
		int			_event,
		int			_ip_address,
		int			_key,
		int			_num_want,
		long		_left,
		short		_port,
		long		_uploaded )
	{
		hash		= _hash;
		peer_id		= _peer_id;
		downloaded	= _downloaded;
		event		= _event;
		ip_address	= _ip_address;
		key			= _key;
		num_want	= _num_want;
		left		= _left;
		port		= _port;
		uploaded	= _uploaded;
	}
	
	public void
	serialise(
		DataOutputStream	os )
	
		throws IOException
	{
		super.serialise(os);
		
		os.write( hash );
		os.write( peer_id );
		os.writeLong( downloaded );
		os.writeLong( left );
		os.writeLong( uploaded );
		os.writeInt( event );
		os.writeInt( ip_address );
		os.writeInt( key );
		os.writeInt( num_want );
		os.writeShort( port );
	}
	
	public String
	getString()
	{
		return( super.getString()).concat("[").concat(
					"hash="+ByteFormatter.nicePrint( hash, true )+
					"peer="+ByteFormatter.nicePrint( peer_id, true )+
					"dl="+downloaded+
					"ev="+event+
					"ip="+ip_address+
					"key="+key+
					"nw="+num_want+
					"left="+left+
					"port="+port+
					"ul="+uploaded+"]");
	}
}
