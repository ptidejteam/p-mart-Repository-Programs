/*
 * File    : TRTrackerServerTorrent.java
 * Created : 26-Oct-2003
 * By      : stuff
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

package org.gudy.azureus2.core3.tracker.server.impl;

/**
 * @author parg
 *
 */

import java.util.*;
import java.io.*;

import org.gudy.azureus2.core3.logging.LGLogger;
import org.gudy.azureus2.core3.tracker.server.*;
import org.gudy.azureus2.core3.util.*;


public class 
TRTrackerServerTorrentImpl 
	implements TRTrackerServerTorrent
{
		// no point in caching replies smaller than that below
	
	public static final int	MIN_CACHE_ENTRY_SIZE		= 10;
	
	public static final int MAX_UPLOAD_BYTES_PER_SEC	= 3*1024*1024;  //3MBs
	public static final int MAX_DOWNLOAD_BYTES_PER_SEC	= MAX_UPLOAD_BYTES_PER_SEC;
	
	public static final boolean	USE_LIGHTWEIGHT_SEEDS	= true;
	
	
	protected HashWrapper			hash;

	protected Map				peer_map 		= new HashMap();
	protected Map				peer_reuse_map	= new HashMap();
	
	protected List				peer_list		= new ArrayList();
	protected int				peer_list_hole_count;
	protected boolean			peer_list_compaction_suspended;
	
	protected Map				lightweight_seed_map	= new HashMap();
	
	protected int				seed_count;
	protected int				removed_count;
	
	protected int				bad_NAT_count;	// calculated periodically
	
	protected Random			random		= new Random( SystemTime.getCurrentTime());
	
	protected long				last_scrape_calc_time;
	protected Map				last_scrape;
	
	protected LinkedHashMap		announce_cache	= new LinkedHashMap();
	
	protected TRTrackerServerTorrentStatsImpl	stats;
		
	protected List				listeners	= new ArrayList();
	protected boolean			deleted;
	
	protected boolean			map_size_diff_reported;
	
	protected byte				duplicate_peer_checker_index	= 0;
	protected byte[]			duplicate_peer_checker			= new byte[0];
	
	protected boolean			caching_enabled	= true;
	
	protected AEMonitor this_mon 	= new AEMonitor( "TRTrackerServerTorrent" );

	public
	TRTrackerServerTorrentImpl(
		HashWrapper				_hash )
	{
		hash		= _hash;
		
		stats		= new TRTrackerServerTorrentStatsImpl( this );
	}
	
	
	public TRTrackerServerPeerImpl
	peerContact(
		String		event,
		HashWrapper	peer_id,
		int			port,
		String		ip_address,
		boolean		loopback,
		String		tracker_key,
		long		uploaded,
		long		downloaded,
		long		left,
		long		interval_requested )
	
		throws TRTrackerServerException
	{
		try{
			this_mon.enter();
		
			// System.out.println( "TRTrackerServerTorrent: peerContact, ip = " + ip_address );
					
			boolean	stopped 	= event != null && event.equalsIgnoreCase("stopped");
			boolean	completed 	= event != null && event.equalsIgnoreCase("completed");
			
			long	now = SystemTime.getCurrentTime();
			
			int		tracker_key_hash_code	= tracker_key==null?0:tracker_key.hashCode();
			
			TRTrackerServerPeerImpl	peer = (TRTrackerServerPeerImpl)peer_map.get( peer_id );
	
			boolean		new_peer 			= false;
			
			boolean		already_completed	= false;
			long		last_contact_time	= 0;
			
			long	ul_diff = 0;
			long	dl_diff	= 0;
			long	le_diff = 0;
			
			byte[]	ip_address_bytes = ip_address.getBytes( Constants.BYTE_ENCODING );
			
			if ( peer == null ){
				
				String	reuse_key = new String( ip_address_bytes, Constants.BYTE_ENCODING ) + ":" + port;
				
				byte	last_NAT_status	= loopback?TRTrackerServerPeer.NAT_CHECK_OK:TRTrackerServerPeer.NAT_CHECK_UNKNOWN;
				
				new_peer	= true;
				
				// check to see if this peer already has an entry against this torrent
				// and if so delete it (assumption is that the client has quit and
				// restarted with new peer id
				
				//System.out.println( "new peer" );
				
				
				TRTrackerServerPeerImpl old_peer	= (TRTrackerServerPeerImpl)peer_reuse_map.get( reuse_key );
								
				if ( old_peer != null ){
									
					last_contact_time	= old_peer.getLastContactTime();
					
					already_completed	= old_peer.getDownloadCompleted();
					
					removePeer( old_peer );
					
					lightweight_seed_map.remove( old_peer.getPeerId());
					
				}else{
					
					lightweightSeed lws = (lightweightSeed)lightweight_seed_map.remove( peer_id );
					
					if ( lws != null ){
						
						last_contact_time	= lws.getLastContactTime();
						
						ul_diff	= uploaded - lws.getUploaded();
						
						if ( ul_diff < 0 ){
							
							ul_diff	= 0;
						}
						
						last_NAT_status = lws.getNATStatus();
						
					}else{
					
						last_contact_time	= now;
					}
				}
				
				if ( !stopped ){			
							
					peer = new TRTrackerServerPeerImpl( 
									peer_id, 
									tracker_key_hash_code, 
									ip_address_bytes,
									port,
									last_contact_time,
									already_completed,
									last_NAT_status );
					
					peer_map.put( peer_id, peer );
					
					peer_list.add( peer );
									
					peer_reuse_map.put( reuse_key, peer );
				}
			}else{
				
				int	existing_tracker_key_hash_code = peer.getKeyHashCode();
		
				// System.out.println( "tracker_key:" + existing_tracker_key + "/" + tracker_key );
					
				if ( existing_tracker_key_hash_code != tracker_key_hash_code ){
			
					throw( new TRTrackerServerException( "Unauthorised: key mismatch "));
					
				}
				
				already_completed	= peer.getDownloadCompleted();
				
				last_contact_time	= peer.getLastContactTime();
				
				if ( stopped ){
					
					removePeer( peer );
					
				}else{
					
						// IP may have changed - update if required
		
						// it is possible for two az clients to have the same peer id. Unlikely but possible
						// or indeed some hacked versions could do it on purpose. If this is the case then all we
						// will see here is address/port changes as each peer announces
	
					byte[]	old_ip 		= peer.getIPAsRead();
					int		old_port	= peer.getPort();
					
					if ( peer.checkForIPOrPortChange( ip_address_bytes, port )){
						
							// same peer id so same port
						
						String 	old_key = new String( old_ip, Constants.BYTE_ENCODING ) + ":" + old_port;
						
						String	new_key = new String(ip_address_bytes, Constants.BYTE_ENCODING ) + ":" + port;
						
							// it is possible, on address change, that the target address already exists and is
							// (was) being used by another peer. Given that this peer has taken over its address
							// the assumption is that the other peer has also had an address change and has yet
							// to report it. The only action here is to delete the other peer
						
						TRTrackerServerPeerImpl old_peer = (TRTrackerServerPeerImpl)peer_reuse_map.get( new_key );
						
						if ( old_peer != null ){
						
							removePeer( old_peer );
						}
	
							// now swap the keys
						
						if ( peer_reuse_map.remove( old_key ) == null ){
							
							Debug.out( "TRTrackerServerTorrent: IP address change: '" + old_key + "' -> '" + new_key + "': old key not found" );
						}
	
						peer_reuse_map.put( new_key, peer );
					}
				}
			}
			
				// a null peer here signifies a new peer whose first state was "stopped"
			
			long	new_timeout = now + ( interval_requested * 1000 * TRTrackerServerImpl.CLIENT_TIMEOUT_MULTIPLIER );
			
			if ( peer != null ){
				
				peer.setTimeout( now, new_timeout );
							
					// if this is the first time we've heard from this peer then we don't want to
					// use existing ul/dl value diffs as they will have been reported previously
					// (either the client's changed peer id by stop/start (in which case the values 
					// should be 0 anyway as its a per-session total), or the tracker's been 
					// stopped and started).
				
				if ( !new_peer ){	
			
					ul_diff = uploaded 		- peer.getUploaded();
					dl_diff = downloaded 	- peer.getDownloaded();
				}
				
					// simple rate control
				
				long	elapsed_time	= now - last_contact_time;
				
				if ( elapsed_time == 0 ){
					
					elapsed_time = SystemTime.TIME_GRANULARITY_MILLIS;
				}
				
				long	ul_rate = (ul_diff*1000)/elapsed_time;	// bytes per second
				long	dl_rate	= (dl_diff*1000)/elapsed_time;
					
				if ( ul_rate > MAX_UPLOAD_BYTES_PER_SEC ){
					
					LGLogger.log( "TRTrackerPeer: peer " + peer.getIPRaw() + "/" +
									new String(peer.getPeerId().getHash()) + 
									" reported an upload rate of " + ul_rate/1024 + " KiB/s per second" );
					
					ul_diff	= 0;
				}
				
				if ( dl_rate > MAX_DOWNLOAD_BYTES_PER_SEC ){
					
          LGLogger.log( "TRTrackerPeer: peer " + peer.getIPRaw() + "/" +
									new String(peer.getPeerId().getHash()) + 
									" reported a download rate of " + dl_rate/1024 + " KiB/s per second" );
					
					dl_diff	= 0;
				}
						// when the peer is removed its "left" amount will dealt with
					
				le_diff = stopped?0:(left - peer.getAmountLeft());
				
				boolean	was_seed 	= new_peer?false:peer.isSeed();
				
				peer.setStats( uploaded, downloaded, left );
				
				boolean	is_seed		= peer.isSeed();
				
				if (!(stopped || was_seed || !is_seed )){
					
					seed_count++;
				}
			}
			
			stats.addAnnounce( ul_diff, dl_diff, le_diff );
			
			if ( completed && !already_completed ){
				
				peer.setDownloadCompleted();
				
				stats.addCompleted();
			}
			
			if ( peer != null && peer.isSeed()){
				
				int	seed_retention = TRTrackerServerImpl.getMaxSeedRetention();
			
				if ( seed_retention != 0 && seed_count > seed_retention ){
					
						// remove 5% of the seeds
					
					int	to_remove = (seed_retention/20)+1;
					
					try{
						peer_list_compaction_suspended	= true;
					
							// remove bad NAT ones in preference to others
						
						for (int bad_nat_loop=TRTrackerServerNATChecker.getSingleton().isEnabled()?0:1;bad_nat_loop<2;bad_nat_loop++){
							
							for (int i=0;i<peer_list.size();i++){
								
								TRTrackerServerPeerImpl	this_peer = (TRTrackerServerPeerImpl)peer_list.get(i);
								
								if ( this_peer != null && this_peer.isSeed()){
							
									boolean	bad_nat = this_peer.isNATStatusBad();
									
									if ( 	( bad_nat_loop == 0 && bad_nat ) ||
											( bad_nat_loop == 1 )){
										
										if ( USE_LIGHTWEIGHT_SEEDS ){
																			
											lightweight_seed_map.put( 
													this_peer.getPeerId(), 
													new lightweightSeed( 
															now, 
															new_timeout, 
															this_peer.getUploaded(),
															this_peer.getNATStatus()));
										}
										
										removePeer( this_peer, i );
			
										if ( --to_remove == 0 ){
											
											break;
										}
									}
								}
							}
							
							if ( to_remove == 0 ){
								
								break;
							}
						}
					}finally{
						
						peer_list_compaction_suspended	= false;
					}
					
					checkForPeerListCompaction( false );
				}
			}
			
			return( peer );
			
		}catch( UnsupportedEncodingException e ){
			
			throw( new TRTrackerServerException( "Encoding fails", e ));
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	protected void
	removePeer(
		TRTrackerServerPeerImpl	peer )
	{
		removePeer( peer, -1 );
	}
		
	protected void
	removePeer(
		TRTrackerServerPeerImpl	peer,
		int						peer_list_index )	// -1 if not known
	{
		try{
			this_mon.enter();
		
			stats.removeLeft( peer.getAmountLeft());
			
			if ( peer_map.size() != peer_reuse_map.size()){
		
				if ( !map_size_diff_reported ){
					
					map_size_diff_reported	= true;
					
					Debug.out( "TRTrackerServerTorrent::removePeer: maps size different ( " + peer_map.size() + "/" + peer_reuse_map.size() +")");
				}
			}
			
			{
				Object o = peer_map.remove( peer.getPeerId());
				
				if ( o == null ){
					
					Debug.out(" TRTrackerServerTorrent::removePeer: peer_map doesn't contain peer");
				}
			}
			
			if ( peer_list_index == -1 ){
				
				int	peer_index = peer_list.indexOf( peer );
				
				if ( peer_index == -1){
					
					Debug.out(" TRTrackerServerTorrent::removePeer: peer_list doesn't contain peer");
				}else{
					
					peer_list.set( peer_index, null );
				}
			}else{
				
				if ( peer_list.get( peer_list_index ) == peer ){
					
					peer_list.set( peer_list_index, null );
					
				}else{
					
					Debug.out(" TRTrackerServerTorrent::removePeer: peer_list doesn't contain peer at index");
					
				}
			}
			
			peer_list_hole_count++;
				
			checkForPeerListCompaction( false );
			
			try{
				Object o = peer_reuse_map.remove( new String( peer.getIPAsRead(), Constants.BYTE_ENCODING ) + ":" + peer.getPort());
			
				if ( o == null ){
					
					Debug.out(" TRTrackerServerTorrent::removePeer: peer_reuse_map doesn't contain peer");
				}
				
			}catch( UnsupportedEncodingException e ){
			}	
			
			if ( peer.isSeed()){
				
				seed_count--;
			}
			
			removed_count++;
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	public Map
	exportAnnounceToMap(
		HashMap						preprocess_map,
		TRTrackerServerPeerImpl		requesting_peer,		// maybe null for an initial announce from a stopped peer
		boolean						include_seeds,
		int							num_want,
		long						interval,
		long						min_interval,
		boolean						no_peer_id,
		boolean						compact )
	{
		try{
			this_mon.enter();
		
			long	now = SystemTime.getCurrentTime();
			
			// we have to force non-caching for nat_warnings responses as they include
			// peer-specific data
	
			boolean	nat_warning = requesting_peer != null && requesting_peer.getNATStatus() == TRTrackerServerPeerImpl.NAT_CHECK_FAILED;
			
			int		total_peers			= peer_map.size();
			int		cache_millis	 	= TRTrackerServerImpl.getAnnounceCachePeriod();
			
			boolean	send_peer_ids 		= TRTrackerServerImpl.getSendPeerIds();
			
				// override if client has explicitly not requested them
			
			if ( no_peer_id || compact ){
				
				send_peer_ids	= false;
			}
			
			boolean	add_to_cache	= false;
			
			int		max_peers	= TRTrackerServerImpl.getMaxPeersToSend();
			
				// num_want < 0 -> not supplied so give them max
			
			if ( num_want < 0 ){
				
				num_want = total_peers;
			}
			
				// trim back to max_peers if specified
			
			if ( max_peers > 0 && num_want > max_peers ){
				
				num_want	= max_peers;
			}
					
			if ( 	caching_enabled &&
					(!nat_warning) &&
					preprocess_map.size() == 0 &&	// don't cache if we've got pre-process stuff to add
					cache_millis > 0 &&
					num_want >= MIN_CACHE_ENTRY_SIZE &&
					total_peers >= TRTrackerServerImpl.getAnnounceCachePeerThreshold()){
							
					// note that we've got to select a cache entry that is somewhat 
					// relevant to the num_want param (but NOT greater than it)
				
					// remove stuff that's too old
							
				Iterator	it = announce_cache.keySet().iterator();
				
				while( it.hasNext() ){
					
					Integer	key = (Integer)it.next();
					
					announceCacheEntry	entry = (announceCacheEntry)announce_cache.get( key );
					
					if ( now - entry.getTime() > cache_millis ){
											
						it.remove();	
					}
				}
				
					// look for an entry with a reasonable num_want
					// e.g. for 100 look between 50 and 100
				
				for (int i=num_want/10;i>num_want/20;i--){
									
					announceCacheEntry	entry = (announceCacheEntry)announce_cache.get(new Integer(i));
					
					if( entry != null ){
				
						if ( now - entry.getTime() > cache_millis ){
							
							announce_cache.remove( new Integer(i));
							
						}else{
						
								// make sure this is compatible
							
							if ( 	entry.getSendPeerIds() == send_peer_ids &&
									entry.getCompact() == compact ){
							
								return( entry.getData());
							}
						}
					}
				}
			
				add_to_cache	= true;
			}
			
			
			List	rep_peers = new ArrayList();
			
	
			// System.out.println( "exportPeersToMap: num_want = " + num_want + ", max = " + max_peers );
			
				// if they want them all simply give them the set
			
			if ( num_want > 0 ){
							
				if ( num_want >= total_peers){
			
						// if they want them all simply give them the set
					
					for (int i=0;i<peer_list.size();i++){
									
						TRTrackerServerPeerImpl	peer = (TRTrackerServerPeerImpl)peer_list.get(i);
										
						if ( peer == null ){
													
						}else if ( now > peer.getTimeout()){
										
								// System.out.println( "removing timed out client '" + peer.getString());
							
							removePeer( peer, i );									
							
						}else if ( peer.getPort() == 0 ){
							
								// a port of 0 means that the peer definitely can't accept incoming connections
							
						}else if ( include_seeds || !peer.isSeed()){
											
							Map rep_peer = new HashMap(3);
				
							if ( send_peer_ids ){
								
								rep_peer.put( "peer id", peer.getPeerId().getHash());
							}
							
							if ( compact ){
								
								byte[]	peer_bytes = peer.getIPBytes();
								
								if ( peer_bytes == null ){
									
									continue;
								}
								
								rep_peer.put( "ip", peer_bytes );
							}else{
								rep_peer.put( "ip", peer.getIPAsRead() );
							}
							
							rep_peer.put( "port", new Long( peer.getPort()));
							
							rep_peers.add( rep_peer );
						}
					}
				}else{
					
					if ( num_want < total_peers*3 ){
						
						int	peer_list_size	= peer_list.size();
				
							// to avoid returning duplicates when doing the two-loop check
							// for nat selection we maintain an array of markers
						
						if ( duplicate_peer_checker.length < peer_list_size ){
							
							duplicate_peer_checker	= new byte[peer_list_size*2];
							
							duplicate_peer_checker_index	= 1;
							
						}else if ( duplicate_peer_checker.length > (peer_list_size*2)){
							
							duplicate_peer_checker	= new byte[(3*peer_list_size)/2];
							
							duplicate_peer_checker_index	= 1;
							
						}else{
							
							duplicate_peer_checker_index++;
							
							if ( duplicate_peer_checker_index == 0 ){
								
								Arrays.fill( duplicate_peer_checker, (byte)0);
								
								duplicate_peer_checker_index	= 1;
							}
						}
						
						boolean	peer_removed	= false;
								
						try{
								// got to suspend peer list compaction as we rely on the
								// list staying the same size during processing below
							
							peer_list_compaction_suspended	= true;
				
								// too costly to randomise as below. use more efficient but slightly less accurate
								// approach
							
								// two pass process if bad nat detection enabled
						
							int	added			= 0;
							//int	bad_nat_added	= 0;
	
							for (int bad_nat_loop=TRTrackerServerNATChecker.getSingleton().isEnabled()?0:1;bad_nat_loop<2;bad_nat_loop++){
		
								int	limit 	= num_want*2;	// some entries we find might not be usable
															// so in the limit search for more
								
								for (int i=0;i<limit && added < num_want;i++){
									
									int	index = random.nextInt(peer_list_size);
									
									TRTrackerServerPeerImpl	peer = (TRTrackerServerPeerImpl)peer_list.get(index);
					
									if ( peer == null ){
										
									}else if ( now > peer.getTimeout()){
										
										removePeer( peer );
										
										peer_removed	= true;
										
									}else if ( peer.getPort() == 0 ){
										
											// a port of 0 means that the peer definitely can't accept incoming connections
								
									}else if ( include_seeds || !peer.isSeed()){
								
										boolean	bad_nat = peer.isNATStatusBad();
										
										if ( 	( bad_nat_loop == 0 && !bad_nat ) ||
												( bad_nat_loop == 1 )){
											
											if ( duplicate_peer_checker[index] != duplicate_peer_checker_index ){
												
												duplicate_peer_checker[index] = duplicate_peer_checker_index;
										
												//if ( bad_nat ){
												//	
												//	bad_nat_added++;
												//}
												
												added++;
												
												Map rep_peer = new HashMap(3);
												
												if ( send_peer_ids ){
													
													rep_peer.put( "peer id", peer.getPeerId().getHash());
												}
												
												if ( compact ){
													
													byte[]	peer_bytes = peer.getIPBytes();
													
													if ( peer_bytes == null ){
																							
														continue;
													}
													
													rep_peer.put( "ip", peer_bytes );
													
												}else{
													
													rep_peer.put( "ip", peer.getIPAsRead() );
												}
												
												rep_peer.put( "port", new Long( peer.getPort()));	
												
												rep_peers.add( rep_peer );
											}
										}
									}
								}
							}
							
							// System.out.println( "num_want = " + num_want + ", added = " + added + ", bad_nat = " + bad_nat_added );
							
						}finally{
								
							peer_list_compaction_suspended	= false;
								
							if ( peer_removed ){
									
								checkForPeerListCompaction( false );
							}
						}
						
					}else{
						
							// randomly select the peers to return
						
						LinkedList	peers = new LinkedList( peer_map.keySet());
						
						int	added = 0;
						
						while( added < num_want && peers.size() > 0 ){
							
							String	key = (String)peers.remove(random.nextInt(peers.size()));
											
							TRTrackerServerPeerImpl	peer = (TRTrackerServerPeerImpl)peer_map.get(key);
							
							if ( now > peer.getTimeout()){
								
								removePeer( peer );
								
							}else if ( peer.getPort() == 0 ){
								
									// a port of 0 means that the peer definitely can't accept incoming connections
								
							}else if ( include_seeds || !peer.isSeed()){
								
								added++;
								
								Map rep_peer = new HashMap(3);	// don't use TreeMap as default is "compact"
																// so we never actually encode anyway
								
								if ( send_peer_ids ){
									
									rep_peer.put( "peer id", peer.getPeerId().getHash());
								}
								
								if ( compact ){
									
									byte[]	peer_bytes = peer.getIPBytes();
									
									if ( peer_bytes == null ){
										
										continue;
									}
									
									rep_peer.put( "ip", peer_bytes );
								}else{
									rep_peer.put( "ip", peer.getIPAsRead() );
								}
								
								rep_peer.put( "port", new Long( peer.getPort()));
								
								rep_peers.add( rep_peer );
							
							}
						}
					}
				}
			}
			
			Map	root = new TreeMap();	// user TreeMap to pre-sort so encoding quicker
			
			if ( preprocess_map.size() > 0 ){
				
				root.putAll( preprocess_map );
			}
			
			int	num_peers_returned	= rep_peers.size();
			
			if ( compact ){
				
				byte[]	compact_peers = new byte[rep_peers.size()*6];
							
				for ( int i=0;i<num_peers_returned;i++){
					
					Map	rep_peer = (Map)rep_peers.get(i);
					
					byte[] 	ip 		= (byte[])rep_peer.get( "ip" );
					int		port	= ((Long)rep_peer.get( "port" )).intValue();
					
					int	pos = i*6;
					
					System.arraycopy( ip, 0, compact_peers, pos, 4 );
					
					pos += 4;
					
					compact_peers[pos++] = (byte)(port>>8);
					compact_peers[pos++] = (byte)(port&0xff);
				}
									
				root.put( "peers", compact_peers );
			}else{
				
				root.put( "peers", rep_peers );
			}
			
			root.put( "interval", new Long( interval ));
		
			root.put( "min interval", new Long( min_interval ));
			
			if ( nat_warning ){
				
				requesting_peer.setNATStatus( TRTrackerServerPeerImpl.NAT_CHECK_FAILED_AND_REPORTED );
				
				root.put( 
						"warning message", 
						("Unable to connect to your incoming data port (" + requesting_peer.getIP() + ":" + requesting_peer.getPort() +"). " +
						 "This will result in slow downloads. Please check your firewall/router settings").getBytes());
			}
			
				// also include scrape details
			
			root.put( "complete", new Long( getSeedCount() ));
			root.put( "incomplete", new Long( getLeecherCount() ));
			root.put( "downloaded", new Long(stats.getCompletedCount()));
			
			if ( add_to_cache ){
					
				announce_cache.put( new Integer((num_peers_returned+9)/10), new announceCacheEntry( root, send_peer_ids, compact ));
			}
			
			return( root );
			
		}finally{
			
			this_mon.exit();
		}
	}
		
	public Map
	exportScrapeToMap(
		boolean		allow_cache )
	{
		try{
			this_mon.enter();
		
			stats.addScrape();
			
			long	now = SystemTime.getCurrentTime();
			
			if ( 	allow_cache && !SystemTime.isErrorLast1min() &&
					last_scrape != null && 
					now - last_scrape_calc_time < TRTrackerServerImpl.getScrapeCachePeriod()){
				
				return( last_scrape );
			}
			
			last_scrape 			= new TreeMap();
			last_scrape_calc_time	= now;
			
			last_scrape.put( "complete", new Long( getSeedCount()));
			last_scrape.put( "incomplete", new Long( getLeecherCount()));
			last_scrape.put( "downloaded", new Long(stats.getCompletedCount()));
			
			return( last_scrape );
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	protected void
	checkTimeouts()
	{
		try{
			this_mon.enter();
		
			long	now = SystemTime.getCurrentTime();
			
			int	new_bad_NAT_count	= 0;
			
			try{
				peer_list_compaction_suspended	= true;
				
				for (int i=0;i<peer_list.size();i++){
									
					TRTrackerServerPeerImpl	peer = (TRTrackerServerPeerImpl)peer_list.get(i);
					
					if ( peer == null ){
						
						continue;
					}
					
					if ( now > peer.getTimeout()){
						
						removePeer( peer, i );
						
					}else{
						
						if ( peer.isNATStatusBad()){
							
							new_bad_NAT_count++;
						}
					}
				}
			}finally{
				
				peer_list_compaction_suspended	= false;
			}
			
			bad_NAT_count	= new_bad_NAT_count;
			
			if ( removed_count > 1000 ){
				
				removed_count = 0;
				
				checkForPeerListCompaction( true );
				
					// rehash
				
				HashMap	new_peer_map 		= new HashMap(peer_map);
				HashMap	new_peer_reuse_map	= new HashMap(peer_reuse_map);
				
				peer_map 		= new_peer_map;
				peer_reuse_map	= new_peer_reuse_map;
				
			}else{
				
				checkForPeerListCompaction( false );
			}
			
			Iterator	it = lightweight_seed_map.values().iterator();
			
			while( it.hasNext()){
				
				lightweightSeed	lws = (lightweightSeed)it.next();
				
				if ( now > lws.getTimeout()){
				
					it.remove();
				}
			}
		}finally{
			
			this_mon.exit();
		}
	}
	
	protected void
	checkForPeerListCompaction(
		boolean	force )
	{
		if ( peer_list_hole_count > 0 && !peer_list_compaction_suspended ){
			
			if ( force || peer_list_hole_count > peer_map.size()/10 ){
								
				ArrayList	new_peer_list = new ArrayList( peer_list.size() - (peer_list_hole_count/2));
				
				int	holes_found = 0;
				
				for (int i=0;i<peer_list.size();i++){
					
					Object	obj = peer_list.get(i);
					
					if ( obj == null ){
						
						holes_found++;
					}else{
						
						new_peer_list.add( obj );
					}
				}
				
				if( holes_found != peer_list_hole_count ){
					
					Debug.out( "TRTrackerTorrent:compactHoles: count mismatch" );
				}
				
				peer_list	= new_peer_list;
				
				peer_list_hole_count	= 0;
			}
		}
	}
	
	protected void
	updateXferStats(
		int		bytes_in,
		int		bytes_out )
	{
		stats.addXferStats( bytes_in, bytes_out );
	}
	
	public TRTrackerServerTorrentStats
	getStats()
	{
		return( stats );
	}
	
	public int
	getPeerCount()
	{
		return( peer_map.size() + lightweight_seed_map.size());
	}
	
	public int
	getSeedCount()
	{
		if ( seed_count < 0 ){
			
			Debug.out( "seed count negative" );
		}
				
		return( seed_count + lightweight_seed_map.size());
	}
	
	public int
	getLeecherCount()
	{
			// this isn't synchronised so could possible end up negative
		
		int	res = peer_map.size() - seed_count;
		
		return( res<0?0:res );
	}
	
	public TRTrackerServerPeer[]
	getPeers()
	{
		try{
			this_mon.enter();
		
			TRTrackerServerPeer[]	res = new TRTrackerServerPeer[peer_map.size()];
		
			peer_map.values().toArray( res );
		
			return( res );
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	public HashWrapper
	getHash()
	{
		return( hash );
	}
		
	public void
	addListener(
		TRTrackerServerTorrentListener	l )
	{
		listeners.add(l);
		
		if ( deleted ){
			
			l.deleted(this);
		}
	}
	
	public void
	removeListener(
		TRTrackerServerTorrentListener	l )
	{
		listeners.remove(l);
	}
	
	public void
	disableCaching()
	{
		caching_enabled	= false;
	}
	
	public boolean
	isCachingEnabled()
	{
		return( caching_enabled );
	}
	
	public int
	getBadNATPeerCount()
	{
		return( bad_NAT_count );
	}
	
	protected void
	delete()
	{
		deleted	= true;
		
		for (int i=0;i<listeners.size();i++){
			
			((TRTrackerServerTorrentListener)listeners.get(i)).deleted(this);
		}
	}
	
	static class
	announceCacheEntry
	{
		protected Map		data;
		protected boolean	send_peer_ids;
		protected boolean	compact;
		protected long		time;
		
		protected
		announceCacheEntry(
			Map		_data,
			boolean	_send_peer_ids,
			boolean	_compact )
		{
			data			= _data;
			send_peer_ids	= _send_peer_ids;
			compact			= _compact;
			time			= SystemTime.getCurrentTime();
		}
		
		protected boolean
		getSendPeerIds()
		{
			return( send_peer_ids );
		}
		
		protected boolean
		getCompact()
		{
			return( compact );
		}
		
		protected long
		getTime()
		{
			return( time );
		}
		
		protected Map
		getData()
		{
			return( data );
		}
	}
	
	protected static class
	lightweightSeed
	{
		long	timeout;
		long	last_contact_time;
		long	uploaded;
		byte	nat_status;
		
		protected
		lightweightSeed(
			long	_now,
			long	_timeout,
			long	_uploaded,
			byte	_nat_status )
		{
			last_contact_time	= _now;
			timeout				= _timeout;
			uploaded			= _uploaded;
			nat_status			= _nat_status;
		}
		
		protected long
		getTimeout()
		{
			return( timeout );
		}
		protected long
		getLastContactTime()
		{
			return( last_contact_time );
		}
		
		protected long
		getUploaded()
		{
			return( uploaded );
		}
		
		protected byte
		getNATStatus()
		{
			return( nat_status );
		}
	}
}
