/*
 * File    : SeedsItem.java
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

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.config.ParameterListener;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
import org.gudy.azureus2.plugins.ui.tables.*;
import org.gudy.azureus2.ui.swt.views.table.utils.CoreTableColumn;

/**
 *
 * @author Olivier<br>
 * @author TuxPaper 2004/Apr/17: modified to TableCellAdapter<br>
 * @author TuxPaper 2005/Oct/13: Full Copy text & Scrape listener 
 */
public class SeedsItem extends CoreTableColumn implements
		TableCellAddedListener, ParameterListener {
	private static final String CFG_FC_SEEDSTART = "StartStopManager_iFakeFullCopySeedStart";

	private static final String CFG_FC_NUMPEERS = "StartStopManager_iNumPeersAsFullCopy";

	// don't count x peers as a full copy if seeds below
	private int iFC_MinSeeds;

	// count x peers as a full copy, but..
	private int iFC_NumPeers;

	private boolean bCompleteTorrent;

	/** Default Constructor */
	public SeedsItem(String sTableID) {
		super("seeds", ALIGN_CENTER, POSITION_LAST, 60, sTableID);
		setRefreshInterval(INTERVAL_LIVE);

		bCompleteTorrent = sTableID == TableManager.TABLE_MYTORRENTS_COMPLETE;
		if (bCompleteTorrent) {
			iFC_MinSeeds = COConfigurationManager.getIntParameter(CFG_FC_SEEDSTART);
			iFC_NumPeers = COConfigurationManager.getIntParameter(CFG_FC_NUMPEERS);
			COConfigurationManager.addParameterListener(CFG_FC_SEEDSTART, this);
			COConfigurationManager.addParameterListener(CFG_FC_NUMPEERS, this);
		}
	}

	protected void finalize() throws Throwable {
		super.finalize();
		if (bCompleteTorrent) {
			COConfigurationManager.removeParameterListener(CFG_FC_SEEDSTART, this);
			COConfigurationManager.removeParameterListener(CFG_FC_NUMPEERS, this);
		}
	}

	public void cellAdded(TableCell cell) {
		new Cell(cell);
	}

	public void parameterChanged(String parameterName) {
		iFC_MinSeeds = COConfigurationManager.getIntParameter(CFG_FC_SEEDSTART);
		iFC_NumPeers = COConfigurationManager.getIntParameter(CFG_FC_NUMPEERS);
	}

	private class Cell extends AbstractTrackerCell {
		private long lTotalPeers = 0;

		private long lTotalSeeds = -1;

		/**
		 * Initialize
		 * 
		 * @param cell
		 */
		public Cell(TableCell cell) {
			super(cell);
		}

		public void scrapeResult(final TRTrackerScraperResponse response) {
			if (checkScrapeResult(response)) {
				lTotalSeeds = response.getSeeds();
				lTotalPeers = response.getPeers();
			}
		}

		public void refresh(TableCell cell) {
			super.refresh(cell);

			long lConnectedSeeds = 0;
			if (dm != null) {
				lConnectedSeeds = dm.getNbSeeds();

				if (lTotalSeeds == -1) {
					scrapeResult(dm.getTrackerScrapeResponse());
				}
			}

			// Allows for 2097151 of each type (connected seeds, seeds, peers)
			long value = (lConnectedSeeds << 42);
			if (lTotalSeeds > 0)
				value += (lTotalSeeds << 21);
			if (lTotalPeers > 0)
				value += lTotalPeers;

			if (!cell.setSortValue(value) && cell.isValid())
				return;

			String tmp = String.valueOf(lConnectedSeeds);
			if (lTotalSeeds != -1) {
				tmp += " (" + lTotalSeeds;
				if (bCompleteTorrent && iFC_NumPeers > 0 && lTotalSeeds >= iFC_MinSeeds
						&& lTotalPeers > 0) {
					long lSeedsToAdd = lTotalPeers / iFC_NumPeers;
					if (lSeedsToAdd > 0)
						tmp += "+" + lSeedsToAdd;
				}
				tmp += ")";
			}
			cell.setText(tmp);
		}

		public void cellHover(TableCell cell) {
			super.cellHover(cell);

			long lConnectedSeeds = 0;
			DownloadManager dm = (DownloadManager) cell.getDataSource();
			if (dm != null) {
				lConnectedSeeds = dm.getNbSeeds();
			}

			String sToolTip = lConnectedSeeds + " "
					+ MessageText.getString("GeneralView.label.connected") + "\n";
			if (lTotalSeeds != -1) {
				sToolTip += lTotalSeeds + " "
						+ MessageText.getString("GeneralView.label.in_swarm");
			} else {
				TRTrackerScraperResponse response = dm.getTrackerScrapeResponse();
				sToolTip += "?? " + MessageText.getString("GeneralView.label.in_swarm");
				if (response != null)
					sToolTip += "(" + response.getStatusString() + ")";
			}
			if (bCompleteTorrent && iFC_NumPeers > 0 && lTotalSeeds >= iFC_MinSeeds
					&& lTotalPeers > 0) {
				long lSeedsToAdd = lTotalPeers / iFC_NumPeers;
				sToolTip += "\n"
						+ MessageText.getString("MyTorrentsView.seeds.fullcopycalc",
								new String[] { "" + lTotalPeers, "" + lSeedsToAdd });
			}
			cell.setToolTip(sToolTip);
		}
	}
}
