/*
 * Created on 30-Jul-2004
 * Created by Paul Gardner
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

package org.gudy.azureus2.ui.swt.views.tableitems.mytracker;

import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
import org.gudy.azureus2.plugins.ui.tables.TableCell;
import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
import org.gudy.azureus2.plugins.ui.tables.TableManager;
import org.gudy.azureus2.ui.swt.views.table.utils.CoreTableColumn;

/**
 * @author parg
 *
 */

public class 
BadNATCountItem 
	extends CoreTableColumn 
	implements TableCellRefreshListener
{
	/** Default Constructor */
	public BadNATCountItem() 
	{
		super("badnat", ALIGN_TRAIL, POSITION_LAST, 60, TableManager.TABLE_MYTRACKER);
		setRefreshInterval(INTERVAL_LIVE);
	}

	public void 
	refresh(TableCell cell) 
	{
		TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
		long value = 0;
		if (item != null) {
			Long longObject = (Long)item.getData("GUI_BadNATCount");
			if (longObject != null)
				value = longObject.longValue();
		}

		cell.setSortValue(value);
		cell.setText(""+value);
	}
}

