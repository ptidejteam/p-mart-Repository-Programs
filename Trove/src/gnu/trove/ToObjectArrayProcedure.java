///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package gnu.trove;

/**
 * A procedure which stores each value it receives into a target array.
 *
 * Created: Sat Jan 12 10:13:42 2002
 *
 * @author Eric D. Friedman
 * @version $Id: ToObjectArrayProcedure.java,v 1.1 2007/06/13 13:55:22 kaczorol Exp $
 */

final class ToObjectArrayProcedure implements TObjectProcedure {
    private final Object[] target;
    private int pos = 0;
    
    public ToObjectArrayProcedure(final Object[] target) {
        this.target = target;
    }

    public final boolean execute(Object value) {
        target[pos++] = value;
        return true;
    }
} // ToObjectArrayProcedure
