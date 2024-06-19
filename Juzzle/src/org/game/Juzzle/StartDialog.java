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
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import org.game.Juzzle.images.*;

/**
 * StartDialog shows a select dialog for the image choose. It is called for every game start.
 *
 * @see     org.game.Juzzle.JuzzlePanel
 */
public class StartDialog
{
 /**
  * the panel that contains the list and the divide text fields
  */
 protected JPanel panel = null;
 /**
  * the list with images
  */
 protected JList list = null;

 /**
  * value field for x division
  */
 protected JTextField x_div = null;
 /**
  * value field for y division
  */
 protected JTextField y_div = null;

 /**
  * array with ImageDescription's, from JuzzleImages
  *
  * @see     org.game.Juzzle.ImageDescription
  * @see     org.game.Juzzle.images.JuzzleImages
  */
 protected Vector imagesList = null;

 /**
  * creates the panel only, but don't show it.
  */
 public StartDialog()
  {
  super();

  // create panel
  panel = new JPanel(new BorderLayout());
  panel.setBorder(new EtchedBorder());
  panel.setPreferredSize(new Dimension(400, 400));

  JLabel caption = new JLabel("Please select a image:");
  caption.setBorder(new EmptyBorder(2, 10, 2, 2));
  panel.add(BorderLayout.NORTH, caption);

  // get the images
  imagesList = JuzzleImages.getImages();

  // create list
  list = new JList(imagesList);
  list.setCellRenderer(new ImageListRenderer());
  JScrollPane jsp_list = new JScrollPane(list);
  jsp_list.setBorder(new CompoundBorder(new EmptyBorder(2, 10, 2, 10), new BevelBorder(BevelBorder.LOWERED)));
  panel.add(BorderLayout.CENTER, jsp_list);
  list.setSelectedIndex(0);

  // create divide fields
  JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
  sizePanel.add(new JLabel("Divide:"));
  sizePanel.add(x_div = new JTextField("4"));
  sizePanel.add(new JLabel("x"));
  sizePanel.add(y_div = new JTextField("4"));

  x_div.setPreferredSize(new Dimension(50, 20));
  y_div.setPreferredSize(new Dimension(50, 20));

  panel.add(BorderLayout.SOUTH, sizePanel);
  }

 /**
  * add new images to list
  *
  * @param id image to add
  * @see      org.game.Juzzle.ImageDescription
  */
 public void addToImageList(ImageDescription id)
  {
  imagesList.addElement(id);
  list.setListData(imagesList);
  }

 /**
  * create a modal dialog and show the panel
  *
  * @param parent the parent Component of this dialog
  * @return true if user select ok or false if cancel
  */
 public boolean showDialog(Component parent)
  {
  int ret = JOptionPane.showOptionDialog(parent,
                                         panel,
                                         "Please select a image",
                                         JOptionPane.OK_CANCEL_OPTION,
                                         JOptionPane.QUESTION_MESSAGE,
                                         null,
                                         null,
                                         null);
  return (ret == JOptionPane.OK_OPTION);
  }

 /**
  * after the dialog is closed return the selected image
  *
  * @return the selected image
  * @see     org.game.Juzzle.ImageDescription
  */
 public ImageDescription getSelectedImage()
  {
  return (ImageDescription)list.getSelectedValue();
  }

 /**
  * after the dialog is closed return deivision of the image, it is always x > 1 and y > 1
  *
  * @return the division of the selected image
  * @see     org.game.Juzzle.ImageDescription
  */
 public Dimension getSelectedDivision()
  {
  int dx = 2;
  int dy = 2;
  try
     {
     dx = Integer.parseInt(x_div.getText());
     dy = Integer.parseInt(x_div.getText());

     if(dx < 2) dx = 2;
     if(dy < 2) dy = 2;
     }
  catch(Exception e) {}

  return new Dimension(dx, dy);
  }

 /**
  * this class renders the image with text line in the selection dialog
  *
  * @see     org.game.Juzzle.StartDialog
  */
 protected class ImageListRenderer implements ListCellRenderer
 {
  /**
   * for image name
   */
  protected JLabel text  = null;
  /**
   * for image itself (scaled version)
   */
  protected JLabel image = null;
  /**
   * panel for image and text panels
   */
  protected JPanel jpanel = null;

  /**
   * selected border of panel
   */
  protected Border selBorder  = new LineBorder(new Color(128, 0, 0), 5);
  /**
   * normal border of panel
   */
  protected Border normBorder = new EmptyBorder(5, 5, 5, 5);

  /**
   * selected background color of panel
   */
//  protected Color selColor  = new Color(204, 204, 255);
  protected Color selColor  = new Color(255, 255, 204);
  /**
   * normal background color of panel
   */
  protected Color normColor = Color.white;

  /**
   * simply constructor
   */
  public ImageListRenderer()
   {
   // creates text label
   text = new JLabel();
   text.setOpaque(false);
   text.setBorder(new EmptyBorder(2, 10, 2, 2));
   // creates image label
   image = new JLabel();
   image.setHorizontalAlignment(JLabel.CENTER);
   image.setOpaque(false);
   image.setBorder(new EmptyBorder(5, 5, 5, 5));
   image.setPreferredSize(new Dimension(100, 0));

   // creates panel
   jpanel = new JPanel(new BorderLayout());
   jpanel.setBackground(normColor);
   jpanel.setBorder(normBorder);

   jpanel.add(BorderLayout.WEST, image);
   jpanel.add(BorderLayout.CENTER, text);
   jpanel.setPreferredSize(new Dimension(0, 100));
   }

  /**
   * set image and set text, set borders and background colors and return the main panel
   *
   * @return the main panel with image and text
   */
  public Component getListCellRendererComponent(JList jlist,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus)
   {
   // background color
   if(isSelected)   jpanel.setBackground(selColor);
   else             jpanel.setBackground(normColor);
   // border
   if(cellHasFocus) jpanel.setBorder(selBorder);
   else             jpanel.setBorder(normBorder);
/*
   if(isSelected && cellHasFocus)
     jpanel.setPreferredSize(new Dimension(0, 120));
   else
     jpanel.setPreferredSize(new Dimension(0, 70));
*/
   // set the image eith text
   ImageDescription id = (ImageDescription)value;
   image.setIcon(id.imageIcon_scaled);
   text.setText(id.name + " (" + id.imageIcon.getIconWidth() + "x" + id.imageIcon.getIconHeight() + " Pixel)");

   return jpanel;
   }
 }
}
