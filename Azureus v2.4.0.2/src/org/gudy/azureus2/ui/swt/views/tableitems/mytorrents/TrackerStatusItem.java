/*
 * File    : TrackerStatusItem.java
 * Created : 24 nov. 2003
 * By      : Olivier
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

package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
import org.gudy.azureus2.core3.tracker.client.impl.bt.TRTrackerBTScraperResponseImpl;
import org.gudy.azureus2.plugins.ui.tables.TableCell;
import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
import org.gudy.azureus2.ui.swt.mainwindow.Colors;
import org.gudy.azureus2.ui.swt.views.table.TableCellCore;
import org.gudy.azureus2.ui.swt.views.table.utils.CoreTableColumn;

/**
 * @author Olivier
 *
 */
public class TrackerStatusItem extends CoreTableColumn implements
		TableCellAddedListener, TableCellToolTipListener
{
	public TrackerStatusItem(String sTableID) {
		super("tracker", POSITION_LAST, 90, sTableID);
		setRefreshInterval(10); // Slow update while no responses from tracker
	}

	public void cellAdded(TableCell cell) {
		new Cell(cell);
	}

	private class Cell extends AbstractTrackerCell {
		public Cell(TableCell cell) {
			super(cell);
		}

		public void refresh(TableCell cell) {
			super.refresh(cell);
			
			String status = dm == null ? "" : dm.getTrackerStatus();

			// status sometimes contains multiline text (e.g. HTML) on failure
			// - trim to end of first line break if present (see bug 1337563)

			int nl_pos = status.indexOf('\n');
			if (nl_pos >= 0)
				status = status.substring(0, nl_pos);

	    if (cell.setText(status) || !cell.isValid()) {
	    	TrackerCellUtils.updateColor(cell, dm);
	    }
		}

		public void scrapeResult(TRTrackerScraperResponse response) {
			checkScrapeResult(response);
		}

		public void announceResult(TRTrackerAnnouncerResponse response) {
			cell.invalidate();
		}
	}

	public void cellHover(TableCell cell) {
		DownloadManager dm = (DownloadManager) cell.getDataSource();
		cell.setToolTip(TrackerCellUtils.getTooltipText(cell, dm));
	}

	public void cellHoverComplete(TableCell cell) {
		cell.setToolTip(null);
	}
}
