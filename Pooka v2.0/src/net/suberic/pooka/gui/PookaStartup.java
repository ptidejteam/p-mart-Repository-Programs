package net.suberic.pooka.gui;

import javax.swing.*;
import java.awt.*;

import net.suberic.pooka.Pooka;

/**
 * Shows a Pooka startup screen.  Very simple.
 */
public class PookaStartup {

  JLabel mStatusField;
  JProgressBar mProgressBar;

  JFrame mFrame;

  /**
   * Shows the startup screen.
   */
  public void show() {
    mFrame = new JFrame();
    JPanel fullPanel = new JPanel();
    fullPanel.setLayout(new BorderLayout());
    
    ImagePanel imagePanel = null;
    try {
      java.net.URL sourceUrl = this.getClass().getResource(Pooka.getProperty("Pooka.startupImage", "images/PookaSplashscreen.jpg"));
      Image image = Toolkit.getDefaultToolkit().getImage(sourceUrl);
      
      imagePanel = new ImagePanel(image);
      imagePanel.setSize(300,300);
      imagePanel.setPreferredSize(new java.awt.Dimension(300,300));
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    mStatusField = new JLabel("Loading Pooka...", SwingConstants.CENTER);
    
    mProgressBar = new JProgressBar(0, 100);
    
    Box progressBox = Box.createVerticalBox();
    progressBox.add(mStatusField);
    progressBox.add(mProgressBar);
    
    fullPanel.add(imagePanel, BorderLayout.CENTER);
    fullPanel.add(progressBox, BorderLayout.SOUTH);
    
    fullPanel.setBorder(BorderFactory.createEtchedBorder());
    
    mFrame.getContentPane().add(fullPanel);
    
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          mFrame.setUndecorated(true);
          mFrame.pack();
          
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          Dimension frameSize = mFrame.getSize();
          int x = Math.max(0, (screenSize.width - frameSize.width) / 2);
          int y = Math.max(0, (screenSize.height - frameSize.height) / 2);
          
          mFrame.setLocation(x, y);
          //mFrame.setVisible(true);
          mFrame.setLocation(x, y);
        }
      });
  }
  
  /**
   * Sets the current status.  Note that this actually takes a resource string
   * from
   */
  public void setStatus(String pStatus) {
    final String fStatus = pStatus;
    Runnable runMe = new Runnable() {
        public void run() {
          String text = Pooka.getProperty(fStatus + ".label", fStatus);
          int weight = 10;
          try {
            weight = Integer.parseInt(Pooka.getProperty(fStatus + ".weight", "10"));
          } catch (NumberFormatException nfe) {
          }
          mStatusField.setText(text);
          mStatusField.repaint();
          
          try {
            mProgressBar.setValue(mProgressBar.getValue() + weight);
          } catch (Exception e) {
            // if we get an error setting the value, just set to
            // indeterminate.
            mProgressBar.setIndeterminate(true);
          }
        }
      };
    
    if (SwingUtilities.isEventDispatchThread()) {
      runMe.run();
    } else {
      SwingUtilities.invokeLater(runMe);
    }
  }
  
  /**
   * Removes/destroys the startup screen.
   */
  public void hide() {
    /*
    Runnable runMe = new Runnable() {
        public void run() {
          mFrame.setVisible(false);
          mFrame.dispose();
        }
      };
    
    if (SwingUtilities.isEventDispatchThread()) {
      runMe.run();
    } else {
      SwingUtilities.invokeLater(runMe);
    }
    */
  }
  
  class ImagePanel extends JPanel {
    Image mImage;
    
    ImagePanel(Image pImage) {
      mImage = pImage;
    }
    
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      int x = (getSize().width - mImage.getWidth(this)) / 2;
      int y = (getSize().height - mImage.getHeight(this)) / 2;
      
      g.drawImage(mImage, x, y, this);
      
    }
  }
  
}
