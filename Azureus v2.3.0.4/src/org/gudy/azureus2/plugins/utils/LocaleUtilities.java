/*
 * File    : LocaleUtilities.java
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

package org.gudy.azureus2.plugins.utils;

/**
 * @author parg
 *
 */
public interface 
LocaleUtilities 
{
	/**
	 * Allows programatic registration of plugin messages, as opposed to using the
	 * plugin.langfile property in plugin.properties
	 * If you message base file is, say, a.b.c.Messages.properties, pass a.b.c.Messages
	 * @param resource_bundle_prefix
	 */
	
	public void
	integrateLocalisedMessageBundle(
		String		resource_bundle_prefix );
	
	public String
	getLocalisedMessageText(
		String		key );
	
	public String
	getLocalisedMessageText(
		String		key,
		String[]	params );
	
	public LocaleDecoder[]
	getDecoders();
}
