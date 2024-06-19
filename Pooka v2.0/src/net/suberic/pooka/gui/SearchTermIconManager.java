package net.suberic.pooka.gui;
import java.awt.Component;
import javax.swing.*;
import javax.mail.Message;
import javax.mail.search.SearchTerm;
import java.util.MissingResourceException;
import java.util.Vector;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.SearchTermManager;

/**
 * This defines a set of icons and values that can apply to a given Message.
 *
 * An example:
 * FolderTable.Status.type=SearchTerm
 * FolderTable.Status.size=15
 * FolderTable.Status.value=Deleted:New:Answered
 * FolderTable.Status.Deleted.searchTerm=Deleted
 * FolderTable.Status.Deleted.icon=net/suberic/pooka/gui/images/SmallRedCircle.gif
 * FolderTable.Status.New.searchTerm=Recent:and:Not:Seen
 * FolderTable.Status.New.icon=net/suberic/pooka/gui/images/SmallGreenCircle.gif
 * FolderTable.Status.Answered.searchTerm=Answered
 * FolderTable.Status.Answered.icon=net/suberic/pooka/gui/images/SmallBlueCircle.gif
 */
public class SearchTermIconManager {
    SearchTerm[] terms;
    Component[] icons;
    protected Component blankImage = new JLabel();
    
    public SearchTermIconManager(String definitionProperty) {
	SearchTermManager manager = Pooka.getSearchManager();
	createTermsAndIcons(definitionProperty, manager);
        ((JLabel)blankImage).setOpaque(true);
    }

  /**
   * Populates the terms and icons arrays.
   */
  private void createTermsAndIcons(String property, SearchTermManager manager) {
    // i'm lazy.
    Vector iconVector = new Vector();
    Vector termVector = new Vector(); 
    
    Vector items = Pooka.getResources().getPropertyAsVector(property + ".value", "");
    for (int i = 0; i < items.size(); i++) {
      String subProperty = property + "." + (String) items.elementAt(i);
      Component currentIcon = loadImage(Pooka.getProperty(subProperty + ".icon", ""));
      if (currentIcon != null) {
	SearchTerm currentTerm = null;
	try {
	  currentTerm = createSearchTerm(subProperty , manager); 
	} catch (java.text.ParseException pe) {
	  
	}
	if (currentTerm != null) {
	  iconVector.add(currentIcon);
	  termVector.add(currentTerm);
	}
      }
    }
    
    terms = new SearchTerm[termVector.size()];
    icons = new Component[iconVector.size()];
    for (int i = 0; i < termVector.size() ; i++) {
      terms[i] = (SearchTerm)termVector.elementAt(i);
      icons[i] = (Component)iconVector.elementAt(i);
    }
  }
  
  /**
   * This returns the icon for the given value.
   */
  public Component getIcon(int value) {
    if (value < 0 || value >= icons.length || icons[value] == null) {
      return blankImage;
    } else 
      return icons[value];
  }
  
    /**
     * This calculates the int value for the given Message.  It does this
     * by running the SearchTerm on each message.  The value of the first
     * match is returned.  If no matches are found, then the next value
     * (number of SearchTerms + 1) is returned.
     */
    public int getValue(Message m) {
	if (terms != null) {
	    for (int i = 0; i < terms.length; i++) {
		if (terms[i] != null && terms[i].match(m))
		    return i;
	    }
	    return terms.length;
	} else
	    return -1;
    }
    
    /**
     * Creates an appropriate SearchTerm from the given property.
     */
    public SearchTerm createSearchTerm(String propertyName, SearchTermManager manager) throws java.text.ParseException {
	return manager.generateSearchTermFromProperty(propertyName);
    }

    /**
     * This attempts to load an image from the given ImageFile.
     */
    public Component loadImage(String imageKey) {
      Component returnValue = null;
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon(imageKey);
      if (icon != null) {
	returnValue = new JLabel(icon);
	((JLabel)returnValue).setOpaque(true);
	
      } else {
	returnValue = null;
      }
      
      return returnValue;
    }
    
}
