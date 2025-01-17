/*
 * Created on Sep 27, 2004
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

/**
 * Interface designation for rate-limited entities handled by a write controller.
 */
public interface RateControlledWriteEntity {
  /**
   * Uses fair round-robin scheduling of write ops.
   */
  public static final int PRIORITY_NORMAL = 0;
  
  /**
   * Guaranteed scheduling of write ops, with preference
   * over normal-priority entities.
   */
  public static final int PRIORITY_HIGH   = 1;
  
  /**
   * Is ready for a write op.
   * @return true if it can write >0 bytes, false if not ready
   */
  public boolean canWrite();
  
  /**
   * Attempt to do a write operation.
   * @return true if >0 bytes were written (success), false if 0 bytes were written (failure)
   */
  public boolean doWrite();
  
  /**
   * Get this entity's priority level.
   * @return priority
   */
  public int getPriority();
}
