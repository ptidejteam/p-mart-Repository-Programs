/*
 * Created on 29-Apr-2004
 * Created by Paul Gardner
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package org.gudy.azureus2.pluginsimpl.local.utils;

/**
 * @author parg
 *
 */

import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.plugins.utils.*;

import org.gudy.azureus2.core3.util.*;

public class 
UTTimerImpl
	implements UTTimer
{
	private PluginInterface		plugin_interface;
	private Timer				timer;

	private boolean				destroyed;
	
	protected
	UTTimerImpl(
		PluginInterface		pi,
		String				name,
		boolean				lightweight )
	{
		plugin_interface	= pi;
		
		if ( !lightweight ){
			
			timer = new Timer( "Plugin " + pi.getPluginID() + ":" + name );
		}
	}
	
	public UTTimerEvent
	addEvent(
		long						when,
		final UTTimerEventPerformer	ext_performer )
	{
		if ( destroyed ){
			
			throw( new RuntimeException( "Timer has been destroyed" ));	
		}
		
		final timerEvent	res = new timerEvent();
		
		TimerEventPerformer	performer = 
			new TimerEventPerformer()
			{	
				public void
				perform(
					TimerEvent		ev )
				{
					UtilitiesImpl.setPluginThreadContext( plugin_interface );
					
					res.perform( ext_performer );
				}
			};
			
		if ( timer == null ){
			
			res.setEvent( SimpleTimer.addEvent( when, performer ));
			
		}else{
			
			res.setEvent( timer.addEvent( when, performer ));
		
		}
			
		return( res );
	}
	
	public UTTimerEvent
	addPeriodicEvent(
		long						periodic_millis,
		final UTTimerEventPerformer	ext_performer )
	{
		if ( destroyed ){
			
			throw( new RuntimeException( "Timer has been destroyed" ));	
		}
		
		final timerEvent	res = new timerEvent();
		
		TimerEventPerformer	performer = 
			new TimerEventPerformer()
			{	
				public void
				perform(
					TimerEvent		ev )
				{
					UtilitiesImpl.setPluginThreadContext( plugin_interface );
					
					res.perform( ext_performer );
				}
			};
			
		if ( timer == null ){
			
			res.setEvent( SimpleTimer.addPeriodicEvent( periodic_millis, performer ));
			
		}else{
			
			res.setEvent( timer.addPeriodicEvent( periodic_millis, performer ));
		
		}
			
		return( res );
	}
	
	public void
	destroy()
	{
		destroyed	= true;
		
		if ( timer != null ){
			
			timer.destroy();
		}
	}
	
	protected class
	timerEvent
		implements UTTimerEvent
	{
		protected TimerEvent				ev;
		protected TimerEventPeriodic		pev;
		
		protected void
		setEvent(
			TimerEventPeriodic	_ev )
		{
			pev		= _ev;
		}
		
		protected void
		setEvent(
			TimerEvent	_ev )
		{
			ev		= _ev;
		}
		
		protected void
		perform(
			UTTimerEventPerformer	p )
		{
			p.perform( this );
		}
		
		public void
		cancel()
		{
			if ( ev != null ){
				
				ev.cancel();
				
			}else{
				
				pev.cancel();
			}
		}
	}
}
