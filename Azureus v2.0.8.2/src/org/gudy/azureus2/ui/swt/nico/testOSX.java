/*
 * Created on 25 juin 2003
 *  
 */
package org.gudy.azureus2.ui.swt.nico;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Olivier
 *  
 */
public class testOSX extends  Object{
  
  private Display display;
  private Shell mainWindow;
 

  public testOSX() {

    //The Main Window
    display = new Display();
    mainWindow = new Shell(display, SWT.RESIZE | SWT.BORDER | SWT.CLOSE | SWT.MAX | SWT.MIN);
    mainWindow.setText("Test OSX"); //$NON-NLS-1$
    
    /*Listener printer = new Listener() { 
    	public void handleEvent(Event evt) { 
    		System.out.println("-->" + evt.type); 
    	}
    };
    mainWindow.addListener(SWT.Close,printer);
    mainWindow.addListener(SWT.Dispose,printer);
    mainWindow.addListener(SWT.KeyDown,printer);
    mainWindow.addListener(SWT.KeyUp,printer);
    */
      
    mainWindow.open();
    //mainWindow.forceActive();
    
    /*
    mainWindow.addDisposeListener(new DisposeListener() {
    	public void widgetDisposed(DisposeEvent arg0) {
    		System.out.println("NICO disposelistener 002\n");
    		if (mainWindow != null) {
    			System.out.println("NICO disposelistener 002a\n");
    			mainWindow.removeDisposeListener(this);
    			System.out.println("NICO disposelistener 002b\n");
    			dispose();
    			System.out.println("NICO disposelistener 002c\n");
    		}
    		System.out.println("NICO disposelistener 003\n");
    	}      
    });
    */

    mainWindow.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent event) {
        if(getExitConfirmation()) {
          dispose();
        } else {
          event.doit = false;
        }
      }
    });
  }


  public void waitForClose() {
  	while (!mainWindow.isDisposed()) {
  		try {
  			if (!display.readAndDispatch())
  				display.sleep();
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  		}
  	}
  	display.dispose();
  }
  
  public static void main(String args[]) {	
    testOSX mw = new testOSX();
    mw.waitForClose();
  }

	

  public void dispose() {
    if(mainWindow != null && ! mainWindow.isDisposed())
      mainWindow.dispose();
  }

  /**
   * @return true, if the user choosed OK in the exit dialog
   *
   * @author Rene Leonhardt
   */
  private boolean getExitConfirmation() {
    MessageBox mb = new MessageBox(mainWindow, SWT.ICON_WARNING | SWT.YES | SWT.NO);
    mb.setText("Confirm");
    mb.setMessage("Do you really want to exit?");
    if(mb.open() == SWT.YES)
      return true;
    return false;
  }
  
   


}
