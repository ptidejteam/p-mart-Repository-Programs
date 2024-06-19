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
 * this model is using to realise the DummyPuzzlePartList to create a game with no function.
 * This is needed to write this code in the PuzzlePanel:<br>
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
 * @see org.game.Juzzle.PuzzlePartList
 * @see org.game.Juzzle.DummyPuzzlePartList
 * @see org.game.Juzzle.PuzzlePanel
 */
public interface IPuzzlePartList
{
 /**
  * return the puzzle part count
  *
  * @return the puzzle part count
  */
 public int getPartsCount();
 /**
  * return the solved puzzle part count
  *
  * @return the solved puzzle part count
  */
 public int getSolvedPartsCount();
 /**
  * return the first node
  *
  * @return the first node
  */
 public PuzzlePartList.PuzzlePart getFirstNode();
 /**
  * get the array with all parts, the array is never changing for a game, only the list is changing
  *
  * @return a array with all parts
  */
 public PuzzlePartList.PuzzlePart [] getPartArray();
 /**
  * is the part the selected part or not
  *
  * @param part the part to compare
  * @return true if the same part or false if not
  */
 public boolean isSelectedPart(PuzzlePartList.PuzzlePart part);
 /**
  * select part at the point
  *
  * @param point point for selection
  * @return true if selected or false if not
  */
 public boolean selectPart(Point point);
 /**
  * deselect the selected part if one selected
  */
 public void deselectPart();
 /**
  * ask the model for the part that has changed and should be repainted
  *
  * @param repaintRect reference to the Rectangle, that get the repaint rectangle
  */
 public void setRedrawRect(Rectangle repaintRect);
 /**
  * move the selected part
  *
  * @param dx new x position
  * @param dy new y position
  */
 public void moveLocation(int dx, int dy);
 /**
  * if the piece is releasing, test if it pass to another part and if true create a new part
  */
 public void updatePart();
}
