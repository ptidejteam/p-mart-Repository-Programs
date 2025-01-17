/*
 * Created on Feb 21, 2005
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

package com.aelitis.azureus.core.peermanager.utils;

import java.util.Random;

import org.gudy.azureus2.core3.config.COConfigurationManager;

/**
 *
 */
public class AZPeerIdentityManager {
  
  private static byte[] identity = COConfigurationManager.getByteParameter( "az_identity", null );
  static {
    if( identity == null || identity.length != 20 ) {
      identity = generateRandomBytes( 20 );
      COConfigurationManager.setParameter( "az_identity", identity );
    }
  }
  
  
  public static byte[] getAZPeerIdentity() {
    return identity;
  }
  
  
  
  private static byte[] generateRandomBytes( int num_to_generate ) {
    byte[] id = new byte[ num_to_generate ];
    
    Random rand = new Random( System.currentTimeMillis() );
    rand.nextBytes( id );
    
    return id;
  }
  
  

}
