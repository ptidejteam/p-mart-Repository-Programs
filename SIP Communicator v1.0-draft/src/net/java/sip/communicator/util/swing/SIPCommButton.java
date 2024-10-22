/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.util.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jvnet.lafwidget.animation.*;

/**
 * The <tt>SIPCommButton</tt> is a very flexible <tt>JButton</tt> that allows
 * to configure its background, its icon, the look when a mouse is over it, etc.
 *
 * @author Yana Stamcheva
 */
public class SIPCommButton
    extends JButton
{
    private Image bgImage;

    private final Image pressedImage;

    private final Image iconImage;

    /**
     * Creates a button with custom background image and icon image.
     *
     * @param bgImage       The background image.
     * @param pressedImage  The pressed image.
     * @param iconImage     The icon.
     */
    public SIPCommButton(   Image bgImage,
                            Image pressedImage,
                            Image iconImage)
    {
        MouseRolloverHandler mouseHandler = new MouseRolloverHandler();

        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);

        /*
         * Explicitly remove all borders that may be set from the current look
         * and feel.
         */
        this.setBorder(null);
        this.setContentAreaFilled(false);

        this.bgImage = bgImage;
        this.pressedImage = pressedImage;
        this.iconImage = iconImage;

        if (bgImage != null)
        {
            this.setPreferredSize(new Dimension(bgImage.getWidth(null),
                                                bgImage.getHeight(null)));

            this.setIcon(new ImageIcon(this.bgImage));
        }
    }

    /**
     * Creates a button with custom background image.
     *
     * @param bgImage the background button image
     * @param iconImage the icon of this button
     */
    public SIPCommButton(   Image bgImage,
                            Image iconImage)
    {
        this(bgImage, null, iconImage);
    }

    /**
     * Creates a button with custom background image.
     *
     * @param bgImage The background button image.
     */
    public SIPCommButton(Image bgImage)
    {
        this(bgImage, null);
    }

    /**
     * Resets the background image for this button.
     *
     * @param bgImage the new image to set.
     */
    public void setImage(Image bgImage)
    {
        this.bgImage = bgImage;

        this.repaint();
    }

    /**
     * Overrides the <code>paintComponent</code> method of <tt>JButton</tt> to
     * paint the button background and icon, and all additional effects of this
     * configurable button.
     *
     * @param g The Graphics object.
     */
    protected void paintComponent(Graphics g)
    {
        g = g.create();
        try
        {
            internalPaintComponent(g);
        }
        finally
        {
            g.dispose();
        }
    }

    /**
     * Paints this button.
     * @param g the <tt>Graphics</tt> object used for painting
     */
    private void internalPaintComponent(Graphics g)
    {
        AntialiasingManager.activateAntialiasing(g);
        /*
         * As JComponent#paintComponent says, if you do not invoke super's
         * implementation you must honor the opaque property, that is if this
         * component is opaque, you must completely fill in the background in a
         * non-opaque color. If you do not honor the opaque property you will
         * likely see visual artifacts.
         */
        if (isOpaque())
        {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (this.bgImage != null)
        {
            // If there's no icon, we make grey the backgroundImage
            // when disabled.
            Image paintBgImage;
            if (this.iconImage == null && !isEnabled())
            {
                paintBgImage = new ImageIcon(LightGrayFilter
                        .createDisabledImage(bgImage)).getImage();
            }
            else
                paintBgImage = bgImage;

            g.drawImage(paintBgImage,
                        this.getWidth()/2 - this.bgImage.getWidth(null)/2,
                        this.getHeight()/2 - this.bgImage.getHeight(null)/2,
                        this);
        }

        // Paint pressed state.
        if (this.getModel().isPressed())
        {
            if (this.pressedImage != null)
            {
                g.drawImage(this.pressedImage, 0, 0, this);
            }
            else if (this.iconImage != null)
            {
                g.drawImage(this.iconImage,
                    this.getWidth()/2 - this.iconImage.getWidth(null)/2 + 1,
                    this.getHeight()/2 - this.iconImage.getHeight(null)/2 + 1,
                    this);
            }
        }

        // Paint a roll over fade out.
        FadeTracker fadeTracker = FadeTracker.getInstance();

        float visibility = this.getModel().isRollover() ? 1.0f : 0.0f;
        if (fadeTracker.isTracked(this, FadeKind.ROLLOVER))
        {
            visibility = fadeTracker.getFade(this, FadeKind.ROLLOVER);
        }

        visibility /= 2;

        g.setColor(new Color(1.0f, 1.0f, 1.0f, visibility));

        if (this.bgImage != null)
        {
            g.fillRoundRect(
                this.getWidth() / 2 - this.bgImage.getWidth(null) / 2,
                this.getHeight() / 2 - this.bgImage.getHeight(null) / 2,
                bgImage.getWidth(null),
                bgImage.getHeight(null),
                10, 10);
        }
        else if (isContentAreaFilled() || (visibility != 0.0f))
        {
            g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
        }

        if (this.iconImage != null)
        {
            Image paintIconImage;
            if (!isEnabled())
            {
                paintIconImage = new ImageIcon(LightGrayFilter
                        .createDisabledImage(iconImage)).getImage();
            }
            else
                paintIconImage = iconImage;

            g.drawImage(paintIconImage,
                        this.getWidth()/2 - this.iconImage.getWidth(null)/2,
                        this.getHeight()/2 - this.iconImage.getHeight(null)/2,
                        this);
        }
    }

    /**
     * Returns the background image of this button.
     *
     * @return the background image of this button.
     */
    public Image getBackgroundImage()
    {
        return bgImage;
    }

    /**
     * Sets the background image of this button.
     *
     * @param bgImage the background image of this button.
     */
    public void setBackgroundImage(Image bgImage)
    {
        this.bgImage = bgImage;
    }

    /**
     * The <tt>ButtonRepaintCallback</tt> is charged to repaint this button
     * when the fade animation is performed.
     */
    private class ButtonRepaintCallback implements FadeTrackerCallback
    {
        public void fadeEnded(FadeKind arg0)
        {
            repaintLater();
        }

        public void fadePerformed(FadeKind arg0, float arg1)
        {
            repaintLater();
        }

        private void repaintLater()
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    SIPCommButton.this.repaint();
                }
            });
        }

        public void fadeReversed(FadeKind arg0, boolean arg1, float arg2)
        {
        }
    }

    /**
     * Perform a fade animation on mouse over.
     */
    private class MouseRolloverHandler
        implements  MouseListener,
                    MouseMotionListener
    {
        public void mouseMoved(MouseEvent e)
        {
        }

        public void mouseExited(MouseEvent e)
        {
            if (isEnabled())
            {
                getModel().setRollover(false);

                FadeTracker fadeTracker = FadeTracker.getInstance();

                fadeTracker.trackFadeOut(FadeKind.ROLLOVER,
                    SIPCommButton.this,
                    true,
                    new ButtonRepaintCallback());
            }
        }

        public void mouseClicked(MouseEvent e)
        {
        }

        public void mouseEntered(MouseEvent e)
        {
            if (isEnabled())
            {
                getModel().setRollover(true);

                FadeTracker fadeTracker = FadeTracker.getInstance();

                fadeTracker.trackFadeIn(FadeKind.ROLLOVER,
                    SIPCommButton.this,
                    true,
                    new ButtonRepaintCallback());
            }
        }

        public void mousePressed(MouseEvent e)
        {
        }

        public void mouseReleased(MouseEvent e)
        {
        }

        public void mouseDragged(MouseEvent e)
        {
        }
    }
}
