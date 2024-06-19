package net.suberic.pooka.gui;
import java.awt.Component;

public class MultiValueIcon extends BooleanIcon {
    boolean seen, recent;
    String recentAndUnseenFile, justUnseenFile;

    public MultiValueIcon(boolean isSeen, boolean isRecent, String recentAndUnseen, String justUnseen ) {
	super (isSeen, recentAndUnseen, "MultiValue");
	seen = isSeen;
	recent = isRecent;
	recentAndUnseenFile = recentAndUnseen;
	justUnseenFile = justUnseen;
    }
    
    public Component getIcon() {
	if (seen) {
	    return BooleanIcon.blankImage;
	} else if (recent) {
	    return getIcon(recentAndUnseenFile);
	} else {
	    return getIcon(justUnseenFile);
	}
    }

    private int getIntValue() {
	if (seen)
	    return 0;
	else if (!recent)
	    return 1;
	else 
	    return 2;
    }

    public int compareTo(Object o) {
	if (o instanceof MultiValueIcon) {
	    int otherValue= ((MultiValueIcon) o).getIntValue();
	    if ( getIntValue() < otherValue)
		return -1;
	    else if (getIntValue() == otherValue)
		return 0;
	    else
		return 1;
	}

	throw new ClassCastException("object is not a MultiValueIcon.");
    }

    public String toString() {
	return "";
    }
}
