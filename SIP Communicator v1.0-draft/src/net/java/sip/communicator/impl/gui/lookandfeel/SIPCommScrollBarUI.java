/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.lookandfeel;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

/**
 * The SIPCommScrollBarUI implementation.
 * 
 * @author Yana Stamcheva
 */
public class SIPCommScrollBarUI extends MetalScrollBarUI {
    
    private BufferedImage horizontalThumb;
    private BufferedImage verticalThumb;
    private BufferedImage horizontalThumbHandle;
    private BufferedImage verticalThumbHandle;
    
    public SIPCommScrollBarUI(){
        horizontalThumb         = (BufferedImage)UIManager
                                    .get("ScrollBar.horizontalThumbIcon");
        verticalThumb           = (BufferedImage)UIManager
                                    .get("ScrollBar.verticalThumbIcon");
        horizontalThumbHandle   = (BufferedImage)UIManager
                                    .get("ScrollBar.horizontalThumbHandleIcon");
        verticalThumbHandle     = (BufferedImage)UIManager
                                    .get("ScrollBar.verticalThumbHandleIcon");
    }
    
    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
        return new SIPCommScrollBarUI();
    }
    
    protected void paintTrack( Graphics g, JComponent c, Rectangle trackBounds )
    {
        g.translate( trackBounds.x, trackBounds.y );

        boolean leftToRight = c.getComponentOrientation().isLeftToRight();

        if ( scrollbar.getOrientation() == JScrollBar.VERTICAL )
        {
            if ( !isFreeStanding ) {
                trackBounds.width += 2;
                if ( !leftToRight ) {
                    g.translate( -1, 0 );
                }
            }          
            
            g.setColor(this.trackColor);
            g.fillRect(0, 0, trackBounds.width-2, trackBounds.height);
            
            g.setColor(this.trackHighlightColor);
            g.drawRect(0, 0, trackBounds.width-2, trackBounds.height);
            
            if ( !isFreeStanding ) {
                trackBounds.width -= 2;
                if ( !leftToRight ) {
                    g.translate( 1, 0 );
                }
            }
        }
        else  // HORIZONTAL
        {
            if ( !isFreeStanding ) {
                trackBounds.height += 2;
            }

            g.setColor(this.trackColor);
            g.fillRect(0, 0, trackBounds.width, trackBounds.height-2);
            
            g.setColor(this.trackHighlightColor);
            g.drawRect(0, 0, trackBounds.width, trackBounds.height-2);

            if ( !isFreeStanding ) {
                trackBounds.height -= 2;
            }
        }
        g.translate( -trackBounds.x, -trackBounds.y );
    }

    /**
     * Paints the bar of the scroll bar.
     */
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
    {
        if (!c.isEnabled()) {
            return;
        }

        boolean leftToRight = c.getComponentOrientation().isLeftToRight();
        
        g.translate( thumbBounds.x, thumbBounds.y );

        int imgWidth;
        int imgHeight;
        int indentWidth  = 10;
        
        if(scrollbar.getOrientation() == JScrollBar.VERTICAL)
        {            
            if(!isFreeStanding) {
                thumbBounds.width += 2;
                
                if ( !leftToRight ) {
                    g.translate( -1, 0 );
                }
            }            
            imgWidth = verticalThumb.getWidth();
            imgHeight = verticalThumb.getHeight();
            
            Image topImage 
                = verticalThumb.getSubimage(0, 0, 
                                            imgWidth, 
                                            indentWidth);
            Image middleImage 
                = verticalThumb.getSubimage(0, indentWidth, 
                                            imgWidth, 
                                            imgHeight-2*indentWidth);
            Image bottomImage 
                = verticalThumb.getSubimage(0, imgHeight-indentWidth, 
                                            imgWidth, indentWidth);
            
            g.drawImage(topImage, 0, 0, 
                    thumbBounds.width-2, indentWidth , null);
            
            g.drawImage(middleImage, thumbBounds.x, indentWidth, 
                    thumbBounds.width-2, 
                    thumbBounds.height-indentWidth , null);
            
            g.drawImage(bottomImage, thumbBounds.x, thumbBounds.height-indentWidth,
                    thumbBounds.width-2, indentWidth, null);

            
            g.drawImage(verticalThumbHandle, 
                        thumbBounds.width/2-verticalThumbHandle.getWidth()/2,
                        thumbBounds.height/2-verticalThumbHandle.getHeight()/2,
                        verticalThumbHandle.getWidth(),
                        verticalThumbHandle.getHeight(), null);
            
            if (!isFreeStanding) {
                thumbBounds.width -= 2;
                if(!leftToRight) {
                    g.translate( 1, 0 );
                }
            }
        }
        else  // HORIZONTAL
        {
            if (!isFreeStanding) {
                thumbBounds.height += 2;
            }
            imgWidth = horizontalThumb.getWidth();
            imgHeight = horizontalThumb.getHeight();
            
            Image leftImage 
                = horizontalThumb.getSubimage(0, 0,
                                            indentWidth, imgHeight);
            Image middleImage 
                = horizontalThumb.getSubimage(indentWidth, 0, 
                                            imgWidth-2*indentWidth, 
                                            imgHeight);
            Image rightImage 
                = horizontalThumb.getSubimage(imgWidth-indentWidth, 0, 
                                            indentWidth, 
                                            imgHeight);
            
            g.drawImage(leftImage, 0, 0, 
                    indentWidth, thumbBounds.height-2, null);
            
            g.drawImage(middleImage, indentWidth, thumbBounds.y, 
                    thumbBounds.width-indentWidth, 
                    thumbBounds.height-2 , null);
            
            g.drawImage(rightImage, thumbBounds.width-indentWidth, thumbBounds.y,
                    indentWidth, thumbBounds.height-2, null);
            
            g.drawImage(horizontalThumbHandle, 
                    thumbBounds.width/2-horizontalThumbHandle.getWidth()/2,
                    thumbBounds.height/2-horizontalThumbHandle.getHeight()/2,
                    horizontalThumbHandle.getWidth(),
                    horizontalThumbHandle.getHeight(), null);
            
            if (!isFreeStanding) {
                thumbBounds.height -= 2;
            }
        }
        g.translate(-thumbBounds.x, -thumbBounds.y);
    }
    
    protected Dimension getMinimumThumbSize()
    {        
        if(scrollbar.getOrientation() == JScrollBar.VERTICAL)
            return new Dimension(scrollBarWidth, verticalThumbHandle.getHeight()+4);
        else
            return new Dimension(horizontalThumbHandle.getWidth()+4, scrollBarWidth);
    }       

}
