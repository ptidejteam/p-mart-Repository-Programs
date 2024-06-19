package net.suberic.pooka.cache;
import javax.mail.MessagingException;

public class NotCachedException extends MessagingException {
    public NotCachedException(String msg) {
	super(msg);
    }
}
