/*
 * File    : LocaleUtilitiesImpl.java
 * Created : 30-Mar-2004
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

package org.gudy.azureus2.pluginsimpl.local.utils;

/**
 * @author parg
 *
 */

import org.gudy.azureus2.core3.internat.*;

import org.gudy.azureus2.plugins.utils.*;


public class 
LocaleUtilitiesImpl
	implements LocaleUtilities
{
	public String
	getLocalisedMessageText(
		String		key )
	{
		return( MessageText.getString( key ));
	}
	
	public LocaleDecoder[]
	getDecoders()
	{
		LocaleUtilDecoder[]	decs = LocaleUtil.getDecoders();
		
		LocaleDecoder[]	res = new LocaleDecoder[decs.length];
		
		for (int i=0;i<res.length;i++){
			
			res[i] = new LocaleDecoderImpl( decs[i] );
		}
		
		return( res );
	}
}
