/*
 * Created on Feb 28, 2005
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

package org.gudy.azureus2.plugins.messaging.bittorrent;

import org.gudy.azureus2.pluginsimpl.local.messaging.MessageAdapter;

import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTCancel;

/**
 *
 */
public class BTMessageCancel extends MessageAdapter {
  private final BTCancel cancel;
  
  protected BTMessageCancel( com.aelitis.azureus.core.peermanager.messaging.Message core_msg ) {
    super( core_msg );
    cancel = (BTCancel)core_msg;
  }
  
  
  public int getPieceNumber() {  return cancel.getPieceNumber();  }
  
  public int getPieceOffset() {  return cancel.getPieceOffset();  }
  
  public int getLength() {  return cancel.getLength();  }
  
}
