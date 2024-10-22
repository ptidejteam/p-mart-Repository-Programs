/*
 * Created by Joseph Bridgewater
 * Created on Jan 26, 2006
 * Copyright (C) 2005, 2006 Aelitis, All Rights Reserved.
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
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package com.aelitis.azureus.core.peermanager.piecepicker.priority.impl;

import com.aelitis.azureus.core.peermanager.piecepicker.priority.PriorityShape;
import com.aelitis.azureus.core.util.HashCodeUtils;


/**
 * @author MjrTom Jan 26, 2006
 */
public class PriorityShapeRangedImpl
	extends PriorityShapeImpl
	implements PriorityShape, Cloneable
{
    /** the first piece # for the range selection criteria */
	public int     start =0;
    /** the last piece # for the range selection criteria */
	public int     end =0;
	
    public PriorityShapeRangedImpl(final long m, final int p, final int s, final int e)
    {
        super(m, p);
        start =s;
        end =e;
    }
    
    public int hashCode()
    {
        int result =HashCodeUtils.hashMore(super.hashCode(), end);
        return HashCodeUtils.hashMore(result, start);
    }
    
    public boolean equals(final Object other)
    {
        if (!super.equals(other))
            return false;
        final PriorityShapeRangedImpl priorityShape =(PriorityShapeRangedImpl)other;
        if (this.start !=priorityShape.start)
            return false;
        if (this.end !=priorityShape.end)
            return false;
        return true;
    }
    

    public boolean isSelected(final int pieceNumber)
    {
        return start <=pieceNumber &&pieceNumber <=end; 
    }
    
    
	public int getStart()
	{
		return start;
	}
	
	public void setStart(int i)
	{
		start =i;
	}
	
	
	public int getEnd()
	{
		return end;
	}

    public void setEnd(int i)
    {
        end =i;
    }
    
    
    
    public boolean isNoRandom(final int pieceNumber)
    {
        return (mode &PRIORITY_MODE_NO_RANDOM) ==PRIORITY_MODE_NO_RANDOM
            &&start <=pieceNumber &&end >=pieceNumber;
    }
    
    public boolean isNoRandom(final int s, final int e)
    {
        return (mode &PRIORITY_MODE_NO_RANDOM) ==PRIORITY_MODE_NO_RANDOM
            &&this.start <=s &&this.end >=e;
    }
    
    
    public boolean isIgnoreRarity(final int pieceNumber)
    {
        return (mode &PRIORITY_MODE_IGNORE_RARITY) ==PRIORITY_MODE_IGNORE_RARITY
            &&start <=pieceNumber &&end >=pieceNumber;
    }
    
    public boolean isIgnoreRarity(final int s, final int e)
    {
        return (mode &PRIORITY_MODE_IGNORE_RARITY) ==PRIORITY_MODE_IGNORE_RARITY
            &&this.start <=s &&this.end >=e;
    }
    
    
    public boolean isFullPieces(final int pieceNumber)
    {
        return (mode &PRIORITY_MODE_FULL_PIECES) ==PRIORITY_MODE_FULL_PIECES
            &&start <=pieceNumber &&end >=pieceNumber;
    }
    
    public boolean isFullPieces(final int s, final int e)
    {
        return (mode &PRIORITY_MODE_FULL_PIECES) ==PRIORITY_MODE_FULL_PIECES
            &&this.start <=s &&this.end >=e;
    }
    
    
    public boolean isAutoReserve(final int pieceNumber)
    {
        return (mode &PRIORITY_MODE_AUTO_RESERVE) ==PRIORITY_MODE_AUTO_RESERVE
            &&start <=pieceNumber &&end >=pieceNumber;
    }
    
    public boolean isAutoReserve(final int s, final int e)
    {
        return (mode &PRIORITY_MODE_AUTO_RESERVE) ==PRIORITY_MODE_AUTO_RESERVE
            &&this.start <=s &&this.end >=e;
    }
    
    
    public boolean isReverse(final int pieceNumber)
    {
        return (mode &PRIORITY_MODE_REVERSE_ORDER) ==PRIORITY_MODE_REVERSE_ORDER
            &&start <=pieceNumber &&end >=pieceNumber;
    }
    
    public boolean isReverse(final int s, final int e)
    {
        return (mode &PRIORITY_MODE_REVERSE_ORDER) ==PRIORITY_MODE_REVERSE_ORDER
            &&this.start <=s &&this.end >=e;
    }
    
    
    public boolean isAutoSlide(final int pieceNumber)
    {
        return (mode &PRIORITY_MODE_AUTO_SLIDE) ==PRIORITY_MODE_AUTO_SLIDE
            &&start <=pieceNumber &&end >=pieceNumber;
    }
    
    public boolean isAutoSlide(final int s, final int e)
    {
        return (mode &PRIORITY_MODE_AUTO_SLIDE) ==PRIORITY_MODE_AUTO_SLIDE
            &&this.start <=s &&this.end >=e;
    }
    
    
    public boolean isRamp(final int pieceNumber)
    {
        return (mode &PRIORITY_MODE_RAMP) ==PRIORITY_MODE_RAMP
            &&start <=pieceNumber &&end >=pieceNumber;
    }
    
    public boolean isRamp(final int s, final int e)
    {
        return (mode &PRIORITY_MODE_RAMP) ==PRIORITY_MODE_RAMP
            &&this.start <=s &&this.end >=e;
    }
    
    
    public boolean isStaticPriority(final int pieceNumber)
    {
        return (mode &PRIORITY_MODE_STATIC_PRIORITY) ==PRIORITY_MODE_STATIC_PRIORITY
            &&start <=pieceNumber &&end >=pieceNumber;
    }
    
    public boolean isStaticPriority(final int s, final int e)
    {
        return (mode &PRIORITY_MODE_STATIC_PRIORITY) ==PRIORITY_MODE_STATIC_PRIORITY
            &&this.start <=s &&this.end >=e;
    }
    
    
}
