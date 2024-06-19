package net.suberic.pooka.filter;
import javax.mail.*;
import javax.mail.search.*;
import net.suberic.pooka.*;

/**
 * This is a SearchTerm which checks for duplicate messages.
 */
public class DuplicateSearchTerm extends SearchTerm {

    /**
     * Creates the given DuplicateSearchTerm.  Note that you have to
     * have a FolderInfo in which to check for duplicates.
     */
    public DuplicateSearchTerm (FolderInfo sourceFolder) {

    }

    /**
     * Checks to see if the given Message is a duplicate of one that 
     * already exists in the FolderInfo.
     */
    public boolean match(Message m) {
	return false;
    }
}
