/*
 * InfoSkillsSorter.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Created on Jan 13, 2003, 9:26 PM CST
 */

package pcgen.gui;

import pcgen.core.Skill;

/**
 * @author  B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 * @version $Revision: 1.1 $
 */

public interface InfoSkillsSorter
{
	boolean nodeGoHere(PObjectNode node, Skill skill);

	Object whatPart(boolean available, Skill skill);

	boolean nodeHaveNext();

	InfoSkillsSorter nextSorter();

	PObjectNode finalPass(PObjectNode node);
}
































