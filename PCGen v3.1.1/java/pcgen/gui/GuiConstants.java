/*
 * Constants.java
 * Copyright 2002 (C) Jonas Karlsson
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
 * $Id: GuiConstants.java,v 1.1 2006/02/21 00:05:32 vauchers Exp $
 */

package pcgen.gui;

/**
 * This interface holds a few gui constants.
 * These constants were moved from assorted GUI classes in order to reduce connections between
 * the core and gui packages.
 *
 * @author     Jonas Karlsson
 * @version    $Revision: 1.1 $
 */

public interface GuiConstants
{

	// view modes for tables
	int INFOEQUIPPING_VIEW_TYPE = 0;
	int INFOEQUIPPING_VIEW_LOCATION = 1;
	int INFOEQUIPPING_VIEW_NAME = 2;

	int INFOFEATS_VIEW_TYPENAME = 0;		// view mode for Type->Name
	int INFOFEATS_VIEW_NAMEONLY = 1;		// view mode for Name (essentially a JTable)
	int INFOFEATS_VIEW_PREREQTREE = 2;		// view in requirement tree mode
	//public final static int VIEW_SOURCENAME = 3;		// view mode for Source->Name
	//public final static int VIEW_SOURCETYPENAME = 4;	// view mode for Source->Type->Name
	//public final static int VIEW_TYPESOURCENAME = 5;	// view mode for Type->Source->Name

	int INFOINVENTORY_VIEW_TYPE_SUBTYPE_NAME = 0;		// Type/SubType/Name
	int INFOINVENTORY_VIEW_TYPE_NAME = 1;			// Type/Name
	int INFOINVENTORY_VIEW_NAME = 2;				// Name
	int INFOINVENTORY_VIEW_ALL_TYPES = 3;			// All Types
	int INFORACE_VIEW_NAME = 0;
	int INFORACE_VIEW_TYPE = 1;
	int INFORACE_VIEW_SOURCE = 2;
	//view modes for tables
	int INFOSKILLS_VIEW_STAT_TYPE_NAME = 0;
	int INFOSKILLS_VIEW_STAT_NAME = 1;
	int INFOSKILLS_VIEW_TYPE_NAME = 2;
	int INFOSKILLS_VIEW_COST_NAME = 3;
	int INFOSKILLS_VIEW_NAME = 4;
	//view modes for tables
	int INFOSPELLS_VIEW_CLASS = 0;
	int INFOSPELLS_VIEW_LEVEL = 1;
	int INFOSPELLS_VIEW_TYPE = 1;
}
