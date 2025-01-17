/*
 * File    : TrackerWebPageRequestImpl.java
 * Created : 08-Dec-2003
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

package org.gudy.azureus2.pluginsimpl.tracker;

/**
 * @author parg
 *
 */

import java.io.InputStream;
import org.gudy.azureus2.plugins.tracker.*;
import org.gudy.azureus2.plugins.tracker.web.*;

public class 
TrackerWebPageRequestImpl
	implements TrackerWebPageRequest
{
	protected Tracker		tracker;
	protected String		client_address;
	protected String		url;
	protected String		header;
	protected InputStream	is;
	
	protected
	TrackerWebPageRequestImpl(
		Tracker		_tracker,
		String		_client_address,
		String		_url,
		String		_header,
		InputStream	_is )
	{
		tracker			= _tracker;
		client_address	= _client_address;
		url				= _url;
		header			= _header;
		is				= _is;
	}
	
	public Tracker
	getTracker()
	{
		return( tracker );
	}
	
	public String
	getURL()
	{
		return( url );
	}
	
	public String
	getClientAddress()
	{
		return( client_address );
	}
	

	public InputStream
	getInputStream()
	{
		return( is );
	}
}	
