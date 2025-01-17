/*
 * Created on Feb 24, 2005
 * Created by Alon Rohter
 * Copyright (C) 2004-2005 Aelitis, All Rights Reserved.
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

package org.gudy.azureus2.plugins.messaging;

import org.gudy.azureus2.plugins.PluginInterface;


/**
 * Manages peer message handling.
 */
public interface MessageManager {

  /**
   * Register the given message type with the manager for processing.
   * NOTE: A message type needs to be registered in order for support to be
   * advertised to other peers.
   * @param message instance to use for decoding
   * @throws MessageException if this message type has already been registered
   */
  public void registerMessageType( Message message ) throws MessageException;
  
  
  /**
   * Remove registration of given message type from manager.
   * @param message type to remove
   */
  public void deregisterMessageType( Message message );
  
  
  /**
   * Globally register for notification of peers that support the given message type.
   * @param plug_interface to get the download manager
   * @param message to match
   * @param listener to notify
   */
  public void locateCompatiblePeers( PluginInterface plug_interface, Message message, MessageManagerListener listener );
}
