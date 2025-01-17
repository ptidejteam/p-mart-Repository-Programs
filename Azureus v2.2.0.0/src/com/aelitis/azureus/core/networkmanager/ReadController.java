/*
 * Created on Oct 16, 2004
 * Created by Alon Rohter
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

package com.aelitis.azureus.core.networkmanager;

import org.gudy.azureus2.core3.util.AEThread;

/**
 * Processes the read selector.
 */
public class ReadController {
  private final VirtualChannelSelector read_selector = new VirtualChannelSelector( VirtualChannelSelector.OP_READ );
  private static final int SELECT_TIME = 50;
  
  protected ReadController() {
    //start read processing
    Thread read_thread = new AEThread( "ReadController:ReadProcessor" ) {
      public void runSupport() {
        readLoop();
      }
    };
    read_thread.setDaemon( true );
    read_thread.setPriority( Thread.MAX_PRIORITY - 2 );
    read_thread.start();
  }
  
  private void readLoop() {
    while( true ) {
      read_selector.select( SELECT_TIME );
    }
  }
  
  /**
   * Get the virtual selector for socket channel read readiness.
   * @return selector
   */
  protected VirtualChannelSelector getReadSelector() {  return read_selector;  }
}
