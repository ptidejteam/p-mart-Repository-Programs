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

package org.game.Juzzle.images;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import org.game.Juzzle.*;

/**
 * static class for loading of the images
 */
public final class JuzzleImages
{
 /**
  * load a game image
  *
  * @param name name of the image
  * @return the image
  */
 public static Image loadImage(String name)
  {
  Class thisClass = JuzzleImages.class;
  return new ImageIcon(thisClass.getResource(name)).getImage();
  }

 /**
  * load all game images.
  *
  * @return the array with images
  */
 public static Vector getImages()
  {
  Vector vector = new Vector();
  try
     {
     Class thisClass = JuzzleImages.class;
     ImageIcon ii = null;
     ImageIcon iis = null;

     // the list of the images to load
     // is stored in the puzzleImages.txt file
     // in the format
     // [image] [name]
     LineNumberReader lnr = new LineNumberReader(new InputStreamReader(thisClass.getResourceAsStream("puzzleImages.txt")));
     String line = null;
     String file = null;
     String name = null;
     int sep = -1;
     while((line = lnr.readLine()) != null)
          {
          // get the name of image
          sep = line.indexOf(' ');
          if(sep < 0)
            {
            file = line;
            name = "";
            }
          else
            {
            file = line.substring(0, sep);
            name = line.substring(sep + 1, line.length());
            }
//          System.out.println(file + ":" + name);

          // create the image and store it in the ImageDescription
          ii = new ImageIcon(thisClass.getResource(file));
          iis = scaleIcon(ii, 80);
          vector.addElement(new ImageDescription(ii, iis, name));
          }
        }
  catch(IOException e)
       {
       e.printStackTrace();
       }
  return vector;
  }

 /**
  * scale the image to the scaled version
  *
  * @param ii        original image
  * @param imageSize the scaled image must fit into the (imageSize x imageSize) rectangle
  * @return the scaled image
  */
 public static ImageIcon scaleIcon(ImageIcon ii, int imageSize)
  {
  int iw = ii.getIconWidth();
  int ih = ii.getIconHeight();

  int niw = 0;
  int nih = 0;

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

  Image image_scaled = ii.getImage().getScaledInstance(niw, nih, Image.SCALE_FAST);
  return new ImageIcon(image_scaled);
  }

 /**
  * load a image as ImageIcon
  *
  * @param name name of the image
  * @return the image as ImageIcon
  */
 public static ImageIcon getIcon(String name)
  {
  Class thisClass = JuzzleImages.class;
  return new ImageIcon(thisClass.getResource(name));
  }
}
