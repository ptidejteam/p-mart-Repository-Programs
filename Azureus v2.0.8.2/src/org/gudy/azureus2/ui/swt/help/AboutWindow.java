/*
 * File    : AboutWindow.java
 * Created : 18 d�c. 2003}
 * By      : Olivier
 *
 * Azureus - a Java Bittorrent client
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
 */
package org.gudy.azureus2.ui.swt.help;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.util.Constants;
import org.gudy.azureus2.ui.swt.ImageRepository;
import org.gudy.azureus2.ui.swt.MainWindow;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.Utils;

/**
 * @author Olivier
 *
 */
public class AboutWindow {

  static Image image;
  
  public static void show(final Display display) {
    
    Properties properties = new Properties();
    try {
      properties.load(AboutWindow.class.getClassLoader().getResourceAsStream("org/gudy/azureus2/ui/swt/about.properties"));
    }
    catch (Exception e1) {
      e1.printStackTrace();
      return;
    }
        
    final Shell window = new Shell(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    window.setImage(ImageRepository.getImage("azureus")); //$NON-NLS-1$
    window.setText(MessageText.getString("MainWindow.about.title") + " " + Constants.AZUREUS_VERSION); //$NON-NLS-1$
    GridData gridData;
    window.setLayout(new GridLayout(3, false));
    
    disposeImage();
    
    image = new Image(display,ImageRepository.getImage("azureus_splash"),SWT.IMAGE_GRAY);
    
    Group gDevelopers = new Group(window, SWT.NULL);
    gDevelopers.setLayout(new GridLayout());
    Messages.setLanguageText(gDevelopers, "MainWindow.about.section.developers"); //$NON-NLS-1$
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gDevelopers.setLayoutData(gridData);
    
    Label label = new Label(gDevelopers, SWT.LEFT);
    label.setText(properties.getProperty("developers")); //$NON-NLS-1$ //$NON-NLS-2$
    label.setLayoutData(gridData = new GridData());
    
    final Label labelImage = new Label(window, SWT.NONE);
    labelImage.setImage(image);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
    labelImage.setLayoutData(gridData);
  
    Group gTranslators = new Group(window, SWT.NULL);
    gTranslators.setLayout(new GridLayout());
    Messages.setLanguageText(gTranslators, "MainWindow.about.section.translators"); //$NON-NLS-1$
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gTranslators.setLayoutData(gridData);
  
    label = new Label(gTranslators, SWT.LEFT);
    label.setText(properties.getProperty("translators")); //$NON-NLS-1$ //$NON-NLS-2$
    label.setLayoutData(gridData = new GridData());
  
    Group gInternet = new Group(window, SWT.NULL);
    gInternet.setLayout(new GridLayout());
    Messages.setLanguageText(gInternet, "MainWindow.about.section.internet"); //$NON-NLS-1$
    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gridData.horizontalSpan = 3;
    gInternet.setLayoutData(gridData);
  
    final String[][] link =
      { { "homepage", "sourceforge", "sourceforgedownloads", "bugreports", "featurerequests", "forumdiscussion" }, {
        "http://azureus.sourceforge.net/",
          "http://sourceforge.net/projects/azureus/",
          "http://sourceforge.net/project/showfiles.php?group_id=84122",
          "http://sourceforge.net/tracker/?atid=575154&group_id=84122&func=browse",
          "http://sourceforge.net/tracker/?atid=575157&group_id=84122&func=browse",
          "http://sourceforge.net/forum/forum.php?forum_id=291997" }
    };
  
    for (int i = 0; i < link[0].length; i++) {
      final Label linkLabel = new Label(gInternet, SWT.NULL);
      linkLabel.setText(MessageText.getString("MainWindow.about.internet." + link[0][i]));
      linkLabel.setData(link[1][i]);
      linkLabel.setCursor(MainWindow.handCursor);
      linkLabel.setForeground(MainWindow.blue);
      linkLabel.setLayoutData(gridData = new GridData());
      linkLabel.addMouseListener(new MouseAdapter() {
        public void mouseDoubleClick(MouseEvent arg0) {
          Program.launch((String) ((Label) arg0.widget).getData());
        }
        public void mouseDown(MouseEvent arg0) {
          Program.launch((String) ((Label) arg0.widget).getData());
        }
      });
    }
  
    window.pack();
    Utils.centreWindow(window);
    window.open();
    
    Thread updater =  new Thread("Splash Screen Updater") {
      public void run() {        
        if(image == null || image.isDisposed())
          return;
        
        final boolean finished[] = new boolean[1];
        final int[] x = new int[1];
        final int maxX = image.getBounds().width;
        final int maxY = image.getBounds().height;
        final Image imgSrc = ImageRepository.getImage("azureus_splash");
        while(!finished[0]) {
          if(image == null || image.isDisposed()) {
            finished[0] = true;
            break;
          }
          if(display == null || display.isDisposed()) {
            finished[0] = true;
            break;
          }
          display.asyncExec(new Runnable() {
            public void run() {
              if(labelImage.isDisposed())
                return;
              GC gcImage = new GC(labelImage);
              gcImage.setClipping(x[0],0,1,maxY);
              gcImage.drawImage(imgSrc,0,0);
              gcImage.dispose();
              x[0]++;
              if(x[0] >= maxX) {
                finished[0] = true;
                labelImage.setImage(imgSrc);
              }
            }
          });
          try {
            Thread.sleep(30);
          }catch(Exception e) {
            e.printStackTrace();
          }
      }
    }};
    updater.start();
  }
  
  public static synchronized void disposeImage() {
    if(image != null && ! image.isDisposed())
      image.dispose();
    image = null;
  }

}
