/*
 * Created on Apr 5, 2005
 * Created by Alon Rohter
 * Copyright (C) 2005 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.core.peermanager.unchoker;

import java.util.*;

/**
 * Performs peer choke/unchoke calculations.
 */
public interface Unchoker {
  
  /**
   * Get any unchokes that should be performed immediately.
   * @param max_to_unchoke maximum number of peers allowed to be unchoked
   * @param all_peers list of peers to choose from
   * @return peers to unchoke
   */
  public ArrayList getImmediateUnchokes( int max_to_unchoke, ArrayList all_peers );

  /**
   * Perform peer choke, unchoke and optimistic calculations
   * @param max_to_unchoke maximum number of peers allowed to be unchoked
   * @param all_peers list of peers to choose from
   * @param force_refresh force a refresh of optimistic unchokes
   */
  public void calculateUnchokes( int max_to_unchoke, ArrayList all_peers, boolean force_refresh );
  
  /**
   * Get the list of peers calculated to be choked.
   * @return peers to choke
   */
  public ArrayList getChokes();
  
  /**
   * Get the list of peers calculated to be unchoked.
   * @return peers to unchoke
   */
  public ArrayList getUnchokes();
}
