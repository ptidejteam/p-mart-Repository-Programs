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
 * ControlPanel is the tool window in the main frame that contain the menu, the scaled image and the time panel
 *
 * @see     org.game.Juzzle.JuzzlePanel
 * @see     org.game.Juzzle.MainFrame
 */
public class ControlPanel extends JPanel
{
 /**
  * background color for time and solved labels
  */
 protected static final Color textBackground = new Color(128, 0, 0);
 /**
  * text color for time and solved labels
  */
 protected static final Color textForeground = new Color(255, 255, 0);
 /**
  * the maximal image side length to fit it into the image label
  */
 protected static final int imageSize = 200;

 /**
  * image label
  */
 protected JLabel imagePanel  = null;
 /**
  * all pieces label
  */
 protected JLabel piecesLabel = null;
 /**
  * solved pieces label
  */
 protected JLabel solvedLabel = null;
 /**
  * time label
  */
 protected JLabel timeLabel   = null;

 /**
  * counter for time
  */
 protected int   gameTime = 0;
 /**
  * timer for the game time. It is a thread.
  */
 protected Timer gameTimeCounter = new Timer(1000, new TimeCounterListener());
 /**
  * help variables for time calculation.
  */
 protected int sec, min, hour, time;
 /**
  * help variables for time calculation.
  */
 protected String ssec, smin;

 /**
  * simply constructor
  */
 public ControlPanel()
  {
  super(new BorderLayout());

  // image label
  imagePanel = new JLabel();
  imagePanel.setHorizontalAlignment(JLabel.CENTER);

//  setImage(new ImageIcon("doco2-05.jpg").getImage());

//  JScrollPane imagePanel_jsp = new JScrollPane(imagePanel);
//  imagePanel_jsp.setPreferredSize(new Dimension(200, 200));

  // panel for centering of the image label
  JPanel center = new JPanel(new GridLayout(1, 1));
  center.setPreferredSize(new Dimension(220, 220));
  CompoundBorder border1 = new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new EtchedBorder());
  center.setBorder(new CompoundBorder(border1, new EmptyBorder(5, 5, 5, 5)));
  center.add(imagePanel);

  // panel pieces an time labels
  JPanel statistic = new JPanel(new GridBagLayout());
  statistic.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new BevelBorder(BevelBorder.LOWERED)));

  GridBagConstraints gbc = new GridBagConstraints();

  gbc.gridx = 0; gbc.gridy = 0;
  gbc.fill = GridBagConstraints.BOTH;
  statistic.add(createLabel("Pieces:", JLabel.RIGHT), gbc);

  gbc.gridx = 0; gbc.gridy = 1;
  gbc.fill = GridBagConstraints.BOTH;
  statistic.add(createLabel("Solved:", JLabel.RIGHT), gbc);

  gbc.gridx = 0; gbc.gridy = 2;
  gbc.fill = GridBagConstraints.BOTH;
  statistic.add(createLabel("Time:", JLabel.RIGHT), gbc);

  gbc.gridx = 1; gbc.gridy = 0;
  gbc.fill = GridBagConstraints.BOTH;
  gbc.weightx = 1.0;
  statistic.add(piecesLabel = createLabel("", JLabel.LEFT), gbc);

  gbc.gridx = 1; gbc.gridy = 1;
  gbc.fill = GridBagConstraints.BOTH;
  gbc.weightx = 1.0;
  statistic.add(solvedLabel = createLabel("", JLabel.LEFT), gbc);

  gbc.gridx = 1; gbc.gridy = 2;
  gbc.fill = GridBagConstraints.BOTH;
  gbc.weightx = 1.0;
  statistic.add(timeLabel   = createLabel("", JLabel.LEFT), gbc);

  add(BorderLayout.CENTER, center);
  add(BorderLayout.SOUTH, statistic);
  }

 /**
  * set the current game image, create a scaled version and set it into the image label 
  *
  * @param image current game image
  */
 public void setImage(Image image)
  {
  // gets the image dimension
  int iw = image.getWidth(null);
  int ih = image.getHeight(null);

  int niw = 0;
  int nih = 0;

  // the image must fit into the 200x200 rectangle
  if(iw >= ih)
    {
    niw = imageSize;
    nih = (int)((double)ih*((double)imageSize/(double)iw));
    }
  else
    {
    niw = (int)((double)iw*((double)imageSize/(double)ih));
    nih = imageSize;
    }

  // scaled and set
  Image image_scaled = image.getScaledInstance(niw, nih, Image.SCALE_FAST);
  imagePanel.setIcon(new ImageIcon(image_scaled));
  }

 /**
  * help function. Creates label and set all properties
  *
  * @param text   label text
  * @param layout JLabel.LEFT or JLabel.RIGHT
  */
 protected JLabel createLabel(String text, int layout)
  {
  JLabel jl = new JLabel(text, layout);
  jl.setBorder(new EmptyBorder(0, 5, 0, 2));
  jl.setOpaque(true);
  jl.setBackground(textBackground);
  jl.setForeground(textForeground); 
  jl.setFont(new Font("SansSerif", Font.PLAIN, 11));
  return jl;
  }

 /**
  * start the counter at game begin
  */
 public void startCounter()
  {
  gameTime = 0;
  printTime("00:00");
  if(gameTimeCounter.isRunning())
    gameTimeCounter.restart();
  else
    gameTimeCounter.start();
  }

 /**
  * start the counter at game end
  */
 public void stopCounter()
  {
  gameTimeCounter.stop();
  }

 /**
  * convert the millisecond time into a string
  *
  * @return the game time as string
  */
 public String getGameTime()
  {
  time = gameTime;

  sec = time%60;
  ssec = (sec < 10 ? "0"+sec : ""+sec);
  time = time/60;
  if(time < 1) return "00:"+ssec;

  min = time%60;
  smin = (min < 10 ? "0"+min : ""+min);
  time = time/60;
  if(time < 1) return smin+":"+ssec;

  hour = time;
  return hour+":"+smin+":"+ssec;
  }

 /**
  * set the text for all pieces label
  *
  * @param s text for label
  */
 public void printPieces(String s)
  {
  if(piecesLabel != null)
    piecesLabel.setText(s);
  }

 /**
  * set the text for solved pieces label
  *
  * @param s text for label
  */
 public void printSolved(String s)
  {
  if(solvedLabel != null)
    solvedLabel.setText(s);
  }

 /**
  * set the text for time label
  *
  * @param s text for label
  */
 public void printTime(String s)
  {
  if(timeLabel != null)
    timeLabel.setText(s);
  }

 /**
  * callback function for Timer. It is called every second.
  */
 protected class TimeCounterListener implements ActionListener
 {
  public void actionPerformed(ActionEvent e)
   {
   gameTime++;
   printTime(getGameTime());
   } 
 }
}
