/*
 * File    : StatsStorage.java
 * Created : 2 mars 2004
 * By      : Olivier
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
package org.gudy.azureus2.core3.stats.transfer.impl;

import java.util.HashMap;
import java.util.Map;

import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.global.GlobalManagerStats;
import org.gudy.azureus2.core3.global.impl.GlobalManagerAdpater;
import org.gudy.azureus2.core3.stats.transfer.OverallStats;
import org.gudy.azureus2.core3.stats.transfer.YearStatsList;
import org.gudy.azureus2.core3.util.*;


/**
 * @author Olivier
 * 
 */
public class OverallStatsImpl extends GlobalManagerAdpater implements OverallStats, TimerEventPerformer{

  GlobalManager manager;
  Map statisticsMap;
  Map overallMap;
   
  long totalDownloaded;
  long totalUploaded;
  long totalUptime;
  
  long lastDownloaded;
  long lastUploaded;
  long lastUptime; 
  
  long session_start_time = SystemTime.getCurrentTime();
  
  protected AEMonitor	this_mon	= new AEMonitor( "OverallStats" );

  private void 
  load(String filename) 
  {
    statisticsMap = FileUtil.readResilientConfigFile( filename );
  }
  
  private void load() {
	  load("azureus.statistics");
	}
  
  private void 
  save(String filename) 
  {  	  
  	try{
  		this_mon.enter();
  	  		
  		FileUtil.writeResilientConfigFile( filename, statisticsMap );
  		
  	}finally{
  		
  		this_mon.exit();
  	}
  }
  
  private void save() {
	  save("azureus.statistics");
	}
  
  private void validateAndLoadValues() {
    overallMap = (Map) statisticsMap.get("all");
    if(overallMap == null) {
      overallMap = new HashMap();
      overallMap.put("downloaded",new Long(0));
      overallMap.put("uploaded",new Long(0));
      overallMap.put("uptime",new Long(0));
      statisticsMap.put("all",overallMap);
    }
    try{
	    totalDownloaded = ((Long)overallMap.get("downloaded")).longValue();
	    totalUploaded = ((Long)overallMap.get("uploaded")).longValue();
	    totalUptime = ((Long)overallMap.get("uptime")).longValue();
	    lastUptime = SystemTime.getCurrentTime() / 1000;
	    
    }catch( Throwable e ){
    	
    	Debug.out( "Stats invalid, resetting to 0" );
    	
    	save();
    }
  }
  
  public OverallStatsImpl(GlobalManager manager) {
    this.manager = manager;
    manager.addListener(this);
    load();
    validateAndLoadValues();

    SimpleTimer.addPeriodicEvent(1000 * 60,this);
  }
  
	public String getXMLExport() {
		// TODO Implement the XML export thing
		return null;
	}
	
	public YearStatsList getYearStats() {
		// TODO Implement granularity
		return null;
	}

	public void setLogLevel(int logLevel) {
		// TODO Auto-generated method stub
	}

  
	public int getAverageDownloadSpeed() {
		if(totalUptime > 1) {
      return (int)(totalDownloaded / totalUptime);
    }
    return 0;
	}

	public int getAverageUploadSpeed() {
    if(totalUptime > 1) {
      return (int)(totalUploaded / totalUptime);
    }
    return 0;
	}

	public long getDownloadedBytes() {
		return totalDownloaded;
	}

	public long getUploadedBytes() {
		return totalUploaded;
	}

	public long getTotalUpTime() {
		return totalUptime;
  }

  public long getSessionUpTime() {
    return (SystemTime.getCurrentTime() - session_start_time) / 1000;
  }
  
	public void perform(TimerEvent event) {
    updateStats();
	}
  
  public void destroyInitiated() {
    updateStats();
  }

  private void updateStats() 
  {
  	try{
  		this_mon.enter();
  	
	    long current_time = SystemTime.getCurrentTime() / 1000;
	    
	    if ( SystemTime.isErrorLast5min() ) {
	      lastUptime = current_time;
	      return;
	    }
	    
	    GlobalManagerStats stats = manager.getStats();
	    
	    long	current_total_received 	= stats.getTotalDataBytesReceived() + stats.getTotalProtocolBytesReceived();
	    long	current_total_sent		= stats.getTotalDataBytesSent() + stats.getTotalProtocolBytesSent();
	    
	    totalDownloaded +=  current_total_received - lastDownloaded;
	    lastDownloaded = current_total_received;
	    
	    totalUploaded +=  current_total_sent - lastUploaded;
	    lastUploaded = current_total_sent;
	    
	    long delta = current_time - lastUptime;
	    
	    if( delta > 100 || delta < 0 ) { //make sure the time diff isn't borked
	      lastUptime = current_time;
	      return;
	    }
	    
	    if( totalUptime > 60*60*24*365*10 ) {  //total uptime > 10years is an error, reset
	      totalUptime = 0;
	    }
	    
	    totalUptime += delta;
	    lastUptime = current_time;
	    
	    overallMap.put("downloaded",new Long(totalDownloaded));
	    overallMap.put("uploaded",new Long(totalUploaded));
	    overallMap.put("uptime",new Long(totalUptime));
	    
	    save();
  	}finally{
  	
  		this_mon.exit();
  	}
  }
}
