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
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * this panel paints the pieces of the puzzle
 *
 * @see     org.game.Juzzle.PuzzlePanel
 * @see     org.game.Juzzle.PuzzlePartList
 */
public class PuzzlePanel extends JPanel implements MouseListener, MouseMotionListener
{
 /**
  * thicknes of the outline for the piece for repaint rect
  */
 protected static final int PAINT_EXTEND = 2;
 /**
  * thicknes of the shadow while dragging and for repaint rect
  */
 protected static final int PAINT_SHADOW = 10;
 /**
  * cursor while dragging
  */
 protected static final Cursor CURSOR_HAND    = new Cursor(Cursor.HAND_CURSOR);
 /**
  * cursor normal
  */
 protected static final Cursor CURSOR_DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);
 /**
  * outline color
  */
 protected static final Color outlineColor = new Color(0, 0, 0, 128);
 /**
  * shadow color
  */
 protected static final Color shadowColor = new Color(0, 0, 0, 128);

 /**
  * reference to the image
  */
 protected BufferedImage bimage = null;
 /**
  * x division
  */
 protected int x_parts = 0;
 /**
  * y division
  */
 protected int y_parts = 0;

 /**
  * reference to the ControlPanel to set sthe solved pieces count
  *
  * @see     org.game.Juzzle.ControlPanel
  */
 protected ControlPanel controlPanel = null;

 /**
  * drag modus 1 mean that the user clicks the mouse button and then move the piece and clicks again to release.<br>
  * <br>
  * <br>
  * drag modus 2 mean that the user press the mouse button and then drag the piece and release the button.<br>
  * <br>
  * <br>
  */
 protected boolean dragModus1, dragModus2 = false;
 /**
  * last drag location for difference calculation
  */
 protected Point lastDragLocation = new Point();
 /**
  * source rect to repaint
  */
 protected Rectangle repaintRect1 = new Rectangle();
 /**
  * target rect to repaint
  */
 protected Rectangle repaintRect2 = new Rectangle();
 /**
  * needed to show only one time per game the win message
  */
 protected boolean endMessagePrinted = false;

 /**
  * reference to the pieces list model
  *
  * @see     org.game.Juzzle.IPuzzlePartList
  */
 protected IPuzzlePartList puzzlePartList = null;

 /**
  * antialiasing on/off
  */
 protected boolean antialiasing = true;
 /**
  * outline on/off
  */
 protected boolean outline = true;
 /**
  * shadow on/off
  */
 protected boolean shadow = true;

 /**
  * simply constructor
  *
  * @param controlPanel the reference to the ControlPanel
  */
 public PuzzlePanel(ControlPanel controlPanel)
  {
  super(null);

  this.controlPanel = controlPanel;

  setOpaque(false);

  addMouseListener(this);
  addMouseMotionListener(this);

//  setPreferredSize(new Dimension(2000, 2000));

  // set the vatiables
  emptyGame();
  }

 /**
  * reset the current game
  */
 public void resetGame()
  {
  if(bimage != null)
    {
    emptyGame();
    puzzlePartList = new PuzzlePartList(bimage, x_parts, y_parts, getSize());
    controlPanel.printPieces("" + puzzlePartList.getPartsCount());
    controlPanel.printSolved("0");
    controlPanel.startCounter();
    repaint();
    }
  }

 /**
  * clear all variables
  */
 public void emptyGame()
  {
  dragModus1 = dragModus2 = false;
  lastDragLocation = new Point();
  repaintRect1 = new Rectangle();
  repaintRect2 = new Rectangle();
  puzzlePartList = new DummyPuzzlePartList();
  endMessagePrinted = false;

  controlPanel.printPieces("");
  controlPanel.printSolved("");
  controlPanel.printTime("");

  repaint();
  }

 /**
  * set the new parameters to play the game
  */
 public void setGame(BufferedImage bimage, int x_parts, int y_parts)
  {
  // clear all variables
  emptyGame();

  this.bimage = bimage;
  this.x_parts = x_parts;
  this.y_parts = y_parts;

  puzzlePartList = new PuzzlePartList(bimage, x_parts, y_parts, getSize());
  controlPanel.printPieces("" + puzzlePartList.getPartsCount());
  controlPanel.printSolved("0");
  controlPanel.startCounter();

  repaint();
  }

 /**
  * set antialiasing on/off
  */
 public void setAntialiasing(boolean b)
  {
  antialiasing = b;
  repaint();
  }
 /**
  * set outline on/off
  */
 public void setOutline(boolean b)
  {
  outline = b;
  repaint();
  }
 /**
  * set shadow on/off
  */
 public void setShadow(boolean b)
  {
  shadow = b;
  repaint();
  }

 /**
  * overwrite paintComponent to paint the pieces
  */
 public void paintComponent(Graphics g)
  {
  super.paintComponent(g);

  Graphics2D g2 = (Graphics2D)g;

  // enable antialiasing
  if(antialiasing)
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

  // get the first part for enumeration
  PuzzlePartList.PuzzlePart part = puzzlePartList.getFirstNode();

  int tx, ty;

  // outline size
  g2.setStroke(new BasicStroke(3));

  while(part != null)
       {
//       g2.setColor(Color.black);
//       g2.fillOval(part.locationIn.x-5, part.locationIn.y-5, 10, 10);
//       g2.setColor(Color.black);
//       g2.drawOval(part.locationOut.x-5, part.locationOut.y-5, 10, 10);

       tx = part.locationIn.x - part.boundsIn.x;
       ty = part.locationIn.y - part.boundsIn.y;

       // translate the graphics context to the piece location
       g2.translate(tx, ty);

//       PuzzlePaint pp = new PuzzlePaint(bimage, part.boundsImage);

       // paint the shadow
       if(shadow && puzzlePartList.isSelectedPart(part))
         {
         g2.translate(PAINT_SHADOW, PAINT_SHADOW);
         g2.setPaint(shadowColor);
         g2.fill(part.generalPath);
         g2.translate(-PAINT_SHADOW, -PAINT_SHADOW);
         }

//       g2.setPaint(pp);

       // set the paint
       g2.setPaint(part.puzzlePaint);
       // fill the shape of the piece with the piece image part
       g2.fill(part.generalPath);

       // paint the outline
       if(outline)
         {
         g2.setColor(outlineColor);
         g2.draw(part.generalPath);
         }

//  g2.setStroke(new BasicStroke(1));
//  g2.setColor(Color.blue);
//  g2.draw(part.boundsIn);

//       g2.setStroke(new BasicStroke(1));
//       g2.setColor(Color.black);
//       g2.draw(s.getBounds());

       // translate the graphics context back
       g2.translate(-tx, -ty);

       // next in list
       part = part.next;
       }

//  g2.setStroke(new BasicStroke(1));
//  g2.setColor(Color.red);
//  g2.draw(repaintRect1);
//  g2.setColor(Color.green);
//  g2.draw(repaintRect2);
  }

 /**
  * if the user clicks one of the parts, start dragging
  */
 protected void startDragging(MouseEvent e)
  {
  if(puzzlePartList.selectPart(e.getPoint()))
    {
    setCursor(CURSOR_HAND);
    lastDragLocation.setLocation(e.getPoint());
    dragModus1 = true;
    mouseMoved(e);
    }
  }

 /**
  * the user put the piece on the desctop back
  */
 protected void stopDragging(MouseEvent e)
  {
  // ask the model for the part that has changed and should be repainted
  puzzlePartList.setRedrawRect(repaintRect1);
  // move selected part
  puzzlePartList.updatePart();
  // ask again
  puzzlePartList.setRedrawRect(repaintRect2);

  // deselect the selected part
  puzzlePartList.deselectPart();

  // if the repaintRect1 and repaintRect2 intersects repaint only one rect
  if(repaintRect1.intersects(repaintRect2))
    {
    repaintRect1.add(repaintRect2);
    repaint(repaintRect1.x - PAINT_EXTEND,
            repaintRect1.y - PAINT_EXTEND,
            repaintRect1.width  + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND,
            repaintRect1.height + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND);
    }
  // if the repaintRect1 and repaintRect2 don't intersects repaint two rects
  else
    {
    repaint(repaintRect1.x - PAINT_EXTEND,
            repaintRect1.y - PAINT_EXTEND,
            repaintRect1.width  + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND,
            repaintRect1.height + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND);
    repaint(repaintRect2.x - PAINT_EXTEND,
            repaintRect2.y - PAINT_EXTEND,
            repaintRect2.width  + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND,
            repaintRect2.height + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND);
    }

  // update the solved count in ControlPanel
  controlPanel.printSolved("" + puzzlePartList.getSolvedPartsCount());

  // dragging end
  dragModus1 = false;
  setCursor(CURSOR_DEFAULT);

  // if solved the whole image, show the message one time
  if(!endMessagePrinted && puzzlePartList.getSolvedPartsCount() >= puzzlePartList.getPartsCount())
    {
    controlPanel.stopCounter();
    JOptionPane.showMessageDialog(this, "Congratulation! You solved the puzzle in " + controlPanel.getGameTime() + ".");
    endMessagePrinted = true;
    }
  }

 /**
  * from MouseListener
  */
 public void mouseClicked (MouseEvent e)
  {
  // drag modus 1 on if not on
  if(dragModus1 == false)
    {
    startDragging(e);
    }
  // drag modus 1 off if on
  else
    {
    stopDragging(e);
    }
  }
 /**
  * from MouseListener, not used
  */
 public void mouseEntered (MouseEvent e)
  {
  }
 /**
  * from MouseListener, not used
  */
 public void mouseExited  (MouseEvent e)
  {
  }
 /**
  * from MouseListener
  */
 public void mousePressed (MouseEvent e)
  {
  // drag modus 2 on if no mode is on
  if(!dragModus2 && !dragModus1)
    {
    dragModus2 = true;
    }
  }
 /**
  * from MouseListener
  */
 public void mouseReleased(MouseEvent e)
  {
  // if only mode 2 on do nothing
  // mouseClicked is called
  if(dragModus2 && !dragModus1)
    {
    dragModus2 = false;
    }
  // if both modes on stop dragging
  else if(dragModus2 && dragModus1)
    {
    dragModus2 = false;
    stopDragging(e);
    }
  }

 /**
  * from MouseMotionListener
  */
 public void mouseDragged(MouseEvent e)
  {
  // user use the modus 2
  if(dragModus2 && !dragModus1)
    {
    dragModus1 = true;
    startDragging(e);
    }
  mouseMoved(e);
  }
 /**
  * from MouseMotionListener
  */
 public void mouseMoved(MouseEvent e)
  {
  // user dragged or moved the piece
  if(dragModus1)
    {
    Point point = e.getPoint();

    int dx = point.x - lastDragLocation.x;
    int dy = point.y - lastDragLocation.y;

    // ask the model for the part that has changed and should be repainted
    puzzlePartList.setRedrawRect(repaintRect1);
    // move selected part
    puzzlePartList.moveLocation(dx, dy);
    // ask again
    puzzlePartList.setRedrawRect(repaintRect2);

    // if the repaintRect1 and repaintRect2 intersects repaint only one rect
    if(repaintRect1.intersects(repaintRect2))
      {
      repaintRect1.add(repaintRect2);
      repaint(repaintRect1.x - PAINT_EXTEND,
              repaintRect1.y - PAINT_EXTEND,
              repaintRect1.width  + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND,
              repaintRect1.height + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND);
      }
    // if the repaintRect1 and repaintRect2 don't intersects repaint two rects
    else
      {
      repaint(repaintRect1.x - PAINT_EXTEND,
              repaintRect1.y - PAINT_EXTEND,
              repaintRect1.width  + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND,
              repaintRect1.height + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND);
      repaint(repaintRect2.x - PAINT_EXTEND,
              repaintRect2.y - PAINT_EXTEND,
              repaintRect2.width  + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND,
              repaintRect2.height + PAINT_SHADOW + PAINT_EXTEND + PAINT_EXTEND);
      }

    lastDragLocation.setLocation(point);
    }
  }
}
