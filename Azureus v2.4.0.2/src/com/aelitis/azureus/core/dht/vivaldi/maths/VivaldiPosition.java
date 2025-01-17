/*
 * Created on 22 juin 2005
 * Created by Olivier Chalouhi
 * 
 * Copyright (C) 2004, 2005, 2006 Aelitis SAS, All rights Reserved
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
 * 
 * AELITIS, SAS au capital de 46,603.30 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package com.aelitis.azureus.core.dht.vivaldi.maths;

public interface VivaldiPosition {
  
  final static int CONVERGE_EVERY = 5;
  final static float CONVERGE_FACTOR = 50f;
  
  // controlling parameters
  public final static float ERROR_MIN = 0.1f;
  
  public Coordinates getCoordinates();
  
  public float getErrorEstimate();
  
  public void  setErrorEstimate(float error);
  
  public void update(float rtt,Coordinates coordinates,float error);
  
  public void update(float rtt, float[] serialised_data );
  
  public float estimateRTT(Coordinates coordinates);
  
  	// serialisation stuff

  public static final int	FLOAT_ARRAY_SIZE	= 4;	// size of float-serialisation array size
  
  public float[] toFloatArray();
  
  public void fromFloatArray( float[] data );
}
