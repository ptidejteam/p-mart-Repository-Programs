package net.suberic.pooka;

public class NoTrashFolderException extends javax.mail.MessagingException {
    public NoTrashFolderException(String message, javax.mail.MessagingException sourceEx) {
	super(message, sourceEx);
    }
}
