/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.customcontrols;

import java.io.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.util.*;

public class StyledHTMLEditorPane
    extends JEditorPane
{
    private static final long serialVersionUID = 1L;

    private final Logger logger = Logger.getLogger(StyledHTMLEditorPane.class);
    
    private final HTMLEditorKit editorKit;
    
    private final HTMLDocument document;
    
    public StyledHTMLEditorPane()
    {
        editorKit = new SIPCommHTMLEditorKit(this);

        this.document = (HTMLDocument) editorKit.createDefaultDocument();

        this.setContentType("text/html");
        this.setEditorKitForContentType("text/html", editorKit);
        this.setEditorKit(editorKit);
        this.setDocument(document);

        putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        Constants.loadSimpleStyle(document.getStyleSheet(), getFont());
    }

    public void appendToEnd(String text)
    {
        Element root = document.getDefaultRootElement();
        try
        {   
            document.insertAfterEnd(root
                .getElement(root.getElementCount() - 1), text);
        }
        catch (BadLocationException e)
        {
            logger.error("Insert in the HTMLDocument failed.", e);
        }
        catch (IOException e)
        {
            logger.error("Insert in the HTMLDocument failed.", e);
        }
    }
    
    public void insertAfterStart(String text)
    {
        Element root = this.document.getDefaultRootElement();
        
        try {
            this.document.insertBeforeStart(root
                    .getElement(0), text);            
        } catch (BadLocationException e) {
            logger.error("Insert in the HTMLDocument failed.", e);
        } catch (IOException e) {
            logger.error("Insert in the HTMLDocument failed.", e);
        }
    }
}
