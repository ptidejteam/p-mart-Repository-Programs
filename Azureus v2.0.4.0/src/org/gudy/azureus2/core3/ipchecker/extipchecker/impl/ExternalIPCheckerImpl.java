/*
 * File    : ExternalIPCheckerImpl.java
 * Created : 09-Nov-2003
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

package org.gudy.azureus2.core3.ipchecker.extipchecker.impl;

/**
 * @author parg
 *
 */

import org.gudy.azureus2.core3.ipchecker.extipchecker.*;

public class 
ExternalIPCheckerImpl
	implements ExternalIPChecker 
{
	static ExternalIPCheckerService[]	services;
	
	static{
		services = new ExternalIPCheckerService[]{
							new ExternalIPCheckerServiceDynDNS(),
							new ExternalIPCheckerServiceDiscoveryVIP(),
						};
	}
	public ExternalIPCheckerService[]
	getServices()
	{
		return( services );
	}
}
