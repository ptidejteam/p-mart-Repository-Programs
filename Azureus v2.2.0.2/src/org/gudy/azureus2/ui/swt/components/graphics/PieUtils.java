/*
 * Created on 19 nov. 2004
 * Created by Olivier Chalouhi
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
package org.gudy.azureus2.ui.swt.components.graphics;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.gudy.azureus2.ui.swt.mainwindow.Colors;

/**
 * @author Olivier Chalouhi
 *
 */
public class PieUtils {
  
  public static Image computePie(Display display,int width,int height,int percent) {
    Image image = new Image(display,width,height);
    GC gcImage = new GC(image);
    gcImage.setForeground(Colors.blue);
    int angle = (percent * 360) / 100;
    gcImage.setBackground(Colors.blues[Colors.BLUES_MIDDARK]);
    gcImage.fillArc(0,0,width,height,90-angle,angle);
    gcImage.drawOval(0 , 0 , width-1, height-1);
    gcImage.dispose();
    return image;
  }
  
  public static void drawPie(GC gc,int x, int y,int width,int height,int percent) {
    gc.setForeground(Colors.blue);
    int angle = (percent * 360) / 100;
    gc.setBackground(Colors.white);
    gc.fillArc(x,y,width,height,0,360);
    gc.setBackground(Colors.blues[Colors.BLUES_MIDDARK]);
    gc.fillArc(x,y,width,height,90-angle,angle);
    gc.drawOval(x , y , width-1, height-1);
  }
  
}
