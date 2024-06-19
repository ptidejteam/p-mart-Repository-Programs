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

import javax.swing.*;
import javax.swing.border.*;

/**
 * Frame class for the Juzzle. Juzzle can be a applet, so MainFrame is not important and has no important code.
 *
 * @see     org.game.Juzzle.JuzzlePanel
 */
public class MainFrame extends JFrame
{
 /**
  * the main and only panel in this frame
  *
  * @see     org.game.Juzzle.JuzzlePanel
  */
 protected JuzzlePanel juzzlePanel = null;

 /**
  * Main constructor for MainFrame. Simply call it to create the GUI and start the game.
  */
 public MainFrame()
  {
  super("Juzzle [version " + JuzzlePanel.version + "]");

  // get the screen resolution
  Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

  int w = screen.width  - 50;
  int h = screen.height - 50;

  // set the frame on center of the screen
  setSize(w, h);
  setLocation((screen.width - w)/2, (screen.height - h)/2);

  // create the main panel
  juzzlePanel = new JuzzlePanel();

  Container contentPane = getContentPane();
  contentPane.add(juzzlePanel);

  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setVisible(true);

  // resize
  juzzlePanel.revalidate();
  // start the first game
  juzzlePanel.startGame();
  }
}
