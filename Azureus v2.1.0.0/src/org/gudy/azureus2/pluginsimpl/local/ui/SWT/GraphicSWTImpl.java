/*
 * Created on 2004/May/23
 * Created by TuxPaper
 * 
 * Copyright (C) 2004 Aelitis SARL, All rights Reserved
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

package org.gudy.azureus2.pluginsimpl.local.ui.SWT;

import org.gudy.azureus2.plugins.ui.SWT.GraphicSWT;
import org.eclipse.swt.graphics.Image;

/** An SWT image to be used in Azureus
 *
 * @see SWTManager.createGraphic
 */
public class GraphicSWTImpl implements GraphicSWT {
  Image img;
  
  public GraphicSWTImpl(Image newImage) {
    img = newImage;
  }

  public Image getImage() {
    return img;
  }

  public boolean setImage(Image newImage) {
    if (img == newImage)
      return false;
    img = newImage;
    return true;
  }
}
