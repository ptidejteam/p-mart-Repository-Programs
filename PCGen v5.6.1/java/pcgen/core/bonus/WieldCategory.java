/*
 * WieldCategory.java
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:33:36 $
 * 
 */

package pcgen.core.bonus;

/**
 * <code>WieldCategory</code>
 *
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 */
final class WieldCategory extends MultiTagBonusObj
{
	WieldCategory()
	{
		super();
		setBonusTags(new String[]{"LIGHT", "ONEHANDED", "TWOHANDED"});
	}

}

