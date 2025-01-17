/*
 * File    : ShareConfigImpl.java
 * Created : 31-Dec-2003
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

package org.gudy.azureus2.pluginsimpl.local.sharing;

/**
 * @author parg
 *
 */

import java.util.*;

import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.plugins.sharing.*;

public class 
ShareConfigImpl 
{
	protected ShareManagerImpl		manager;
	
	protected boolean				saving_suspended;
	protected boolean				save_outstanding;
	
	protected AEMonitor				this_mon	= new AEMonitor( "ShareConfig" );

	protected void
	loadConfig(
		ShareManagerImpl	_manager )
	{
		manager	= _manager;
				
		try{
						
			Map map = FileUtil.readResilientConfigFile("sharing.config");
			
			List resources = (List) map.get("resources");
			
			if (resources == null){
				
				return;
			}
			
			Iterator  iter = resources.iterator();
			
			while (iter.hasNext()) {
				
				Map r_map = (Map) iter.next();
				
				manager.deserialiseResource( r_map );
			}
			
		}catch (Exception e) {
			
			Debug.printStackTrace( e );
		}
	}

	protected void
	saveConfig()
	
		throws ShareException
	{
		try{
			this_mon.enter();
		
			if ( saving_suspended ){
				
				save_outstanding = true;
				
				return;
			}
			
			Map map = new HashMap();
			
			List list = new ArrayList();
			
			map.put("resources", list);
			
			ShareResource[]	shares = manager.getShares();
			
			for (int i=0;i<shares.length;i++){
				
				Map	m = new HashMap();
				
				((ShareResourceImpl)shares[i]).serialiseResource( m );
				
				list.add( m );
			}
			
			FileUtil.writeResilientConfigFile("sharing.config", map);
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	protected void
	suspendSaving()
	{
		try{
			this_mon.enter();
		
			saving_suspended	= true;
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	protected void
	resumeSaving()
		throws ShareException
	{
		try{
			this_mon.enter();
		
			saving_suspended	= false;
			
			if ( save_outstanding ){
				
				save_outstanding	= false;
				
				saveConfig();
			}
		}finally{
			
			this_mon.exit();
		}
	}
}

