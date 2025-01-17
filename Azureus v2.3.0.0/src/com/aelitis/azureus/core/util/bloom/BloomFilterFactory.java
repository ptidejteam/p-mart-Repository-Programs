/*
 * Created on 29-Apr-2005
 * Created by Paul Gardner
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

package com.aelitis.azureus.core.util.bloom;

import com.aelitis.azureus.core.util.bloom.impl.BloomFilterImpl;

public class 
BloomFilterFactory 
{
		/**
		 * Creates a new bloom filter. 
		 * @param max_entries The filter size.
		 * 	a size of 10 * expected entries gives a false-positive of around 0.01%
		 *  17* -> 0.001
		 *  29* -> 0.0001
		 * Each entry takes 4 bits  
		 * So, if 0.01% is acceptable and expected max entries is 100, use a filter
		 * size of 1000.
		 * @return
		 */
	
	public static BloomFilter
	create(
		int		filter_size )
	{
		return( new BloomFilterImpl( filter_size ));
	}
}
