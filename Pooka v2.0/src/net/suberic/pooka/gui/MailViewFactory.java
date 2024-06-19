package net.suberic.pooka.gui;
import javax.swing.text.ViewFactory;
import javax.swing.text.View;
import javax.swing.text.Element;

public class MailViewFactory implements ViewFactory {
    public MailViewFactory() {
    }

    public View create(Element elem) {
	return new MailWrappedView(elem);
    }
}
