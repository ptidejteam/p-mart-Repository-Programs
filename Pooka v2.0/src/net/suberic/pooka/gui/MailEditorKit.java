package net.suberic.pooka.gui;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

/**
 * This class is an EditorKit for editing new email messages.
 */

public class MailEditorKit extends StyledEditorKit {

    public ViewFactory getViewFactory() {
	return new MailViewFactory();
    }

    
} 
