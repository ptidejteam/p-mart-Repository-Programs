/*
 * A puzzle game written in Java.
 *
 * Please read "http://juzzle.sourceforge.net/juzzle_licence.txt" for copyrights.
 * 
 * The sourcecode is designed and created with
 * Sun J2SDK 1.3 and Microsoft Visual J++ 6.0
 *
 * Juzzle homepage: http://juzzle.sourceforge.net
 *
 * autor: Slawa Weis
 * email: slawaweis@animatronik.net
 *
 */

package org.game.Juzzle;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 * puzzle paint return the part of the whole image to draw.
 *
 * @see java.awt.Paint
 * @see java.awt.PaintContext
 */
public class PuzzlePaint implements Paint, PaintContext
{
 /**
  * the reference to the whole image
  */
 protected BufferedImage bimage = null;
 /**
  * the part of the image rectangle
  */
 protected Rectangle viewRect   = new Rectangle();
 /**
  * help variable
  */
 protected Rectangle userBounds = new Rectangle();

 /**
  * simply constructor.
  *
  * @param textur   the puzzle image
  * @param viewRect the rectangle for this piece
  */
 public PuzzlePaint(BufferedImage textur, Rectangle viewRect)
  {
  this.bimage = textur;
  this.viewRect.setRect(viewRect);
  }

 /**
  * set a new rectangle for this piece, needed if two pieces comes together
  *
  * @param viewRect the new rectangle for this piece
  */
 public void setViewRect(Rectangle viewRect)
  {
  this.viewRect.setRect(viewRect);
  }

 /**
  * get the PaintContext of this Paint
  *
  * @see java.awt.Paint
  * @see java.awt.PaintContext
  */
 public PaintContext createContext(ColorModel cm,
                                   Rectangle deviceBounds,
                                   Rectangle2D userBounds,
                                   AffineTransform xform,
                                   RenderingHints hints)
  {
//  if(deviceBounds.equals(userBounds))
//    {
//    System.out.println("deviceBounds: " + deviceBounds);
//    System.out.println("userBounds:   " + userBounds);
//    }
  this.userBounds.setRect(deviceBounds);
  return this;
  }

 /**
  * has this Paint transparent parts or not
  *
  * @see java.awt.Paint
  */
 public int getTransparency()
  {
  return OPAQUE;
  }

 /**
  * destroy the Paint, not needed in Juzzle
  *
  * @see java.awt.PaintContext
  */
 public void dispose() {}

 /**
  * the ColorModel of this Paint, it's the same the puzzle image
  *
  * @see java.awt.PaintContext
  */
 public ColorModel getColorModel()
  {
  return bimage.getColorModel();
  }

 /**
  * return the part of the image to draw
  *
  * @see java.awt.PaintContext
  */
 public Raster getRaster(int x, int y, int w, int h)
  {
  int x2 = x - userBounds.x + viewRect.x;
  int y2 = y - userBounds.y + viewRect.y;
//  if((x2) < 0) x2 = 0;
//  if((y2) < 0) y2 = 0;
//  if((x2 + w) > bimage.getWidth())  x2 = 0;
//  if((y2 + h) > bimage.getHeight()) y2 = 0;
  return bimage.getRaster().createChild(x2, y2, w, h, 0, 0, null);
  }
}
