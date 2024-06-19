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

/**
 * this class is used to write this code in the PuzzlePanel if no game is set:<br>
 * <br>
 * <code>
 * partModel.doAnythingWithPart();
 * </code>
 * <br>
 * <br>
 * instead of:<br>
 * <br>
 * <code>
 * if(part != null)
 *   {
 *   // do anything
 *   }
 * </code>
 * <br>
 * <br>
 * for faster performance.
 * <br>
 * <br>
 *
 * @see org.game.Juzzle.IPuzzlePartList
 * @see org.game.Juzzle.PuzzlePartList
 * @see org.game.Juzzle.PuzzlePanel
 */
public class DummyPuzzlePartList implements IPuzzlePartList
{
 /**
  * simply constructor.
  */
 public DummyPuzzlePartList()
  {
  }

 /**
  * return the puzzle part count
  *
  * @return always -1
  */
 public int getPartsCount()
  {
  return -1;
  }
 /**
  * return the solved puzzle part count
  *
  * @return always -1
  */
 public int getSolvedPartsCount()
  {
  return -1;
  }
 /**
  * return the first node
  *
  * @return always null
  */
 public PuzzlePartList.PuzzlePart getFirstNode()
  {
  return null;
  }
 /**
  * get the array with all parts, the array is never changing for a game, only the list is changing
  *
  * @return always null
  */
 public PuzzlePartList.PuzzlePart [] getPartArray()
  {
  return null;
  }
 /**
  * is the part the selected part or not
  *
  * @param part the part to compare
  * @return always false
  */
 public boolean isSelectedPart(PuzzlePartList.PuzzlePart part)
  {
  return false;
  }
 /**
  * select part at the point
  *
  * @param point point for selection
  * @return always false
  */
 public boolean selectPart(Point point)
  {
  return false;
  }
 /**
  * deselect the selected part if one selected
  */
 public void deselectPart() {}
 /**
  * ask the model for the part that has changed and should be repainted
  *
  * @param repaintRect reference to the Rectangle, that get the repaint rectangle
  */
 public void setRedrawRect(Rectangle repaintRect) {}
 /**
  * move the selected part
  *
  * @param dx new x position
  * @param dy new y position
  */
 public void moveLocation(int dx, int dy) {}
 /**
  * if the piece is releasing, test if it pass to another part and if true create a new part
  */
 public void updatePart() {}
}
