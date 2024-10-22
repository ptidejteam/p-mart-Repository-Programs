/*
 * File    : PluginView.java
 * Created : Oct 12, 2005
 * By      : TuxPaper
 *
 * Copyright (C) 2005 Aelitis SARL, All rights Reserved
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
 * AELITIS, SARL au capital de 30,000 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */

package org.gudy.azureus2.plugins.ui;

/**
 * All plugin views should inherit from this interface so that we can always
 * check to see if they are a plugin view.
 * <p>
 * Any non generic UI plugin view functions are placed here, and every UI
 * should implement them.
 * 
 * @author TuxPaper
 * @since 2.3.0.5
 * 
 * @see org.gudy.azureus2.ui.swt.plugins.UISWTView
 */
public interface UIPluginView {
	/**
	 * Retrieve the data sources related to this view.
	 * 
	 * @return dependent upon subclasses implementation
	 */
	public Object getDataSource();

	/**
	 * ID of the view
	 * 
	 * @return ID of the view
	 * 
	 * @since 2.3.0.6
	 */
	public String getViewID();

	/** 
	 * Closes the view
	 * 
	 * @since 2.3.0.6
	 */
	public void closeView();
}
