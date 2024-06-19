package net.suberic.pooka;
import javax.mail.*;
import javax.mail.search.SearchTerm;
import net.suberic.pooka.filter.FilterAction;
import java.util.List;

/**
 * This represents a MessageFilter.  It contains a SearchTerm and an Action
 * which is done on any messages which match the SearchTerm.
 */
public class MessageFilter {
  private SearchTerm searchTerm;
  private FilterAction action;
  
  private String mProperty;

  /**
   * Create a MessageFilter from a SearchTerm and a FilterAction.
   */
  public MessageFilter(SearchTerm newSearchTerm, FilterAction newAction) {
    searchTerm = newSearchTerm;
    action = newAction;
  }
  
  /**
   * Create a MessageFilter from a String which represents a Pooka
   * property.  
   *
   */
  public MessageFilter(String sourceProperty) {
    mProperty = sourceProperty;

    try {
      searchTerm = Pooka.getSearchManager().generateSearchTermFromProperty(sourceProperty + ".searchTerm");
      
      action = generateFilterAction(sourceProperty + ".action");
    } catch (java.text.ParseException pe) {
      // FIXME:  we should actually handle this.
      
      pe.printStackTrace();
    } catch (InstantiationException ie) {
      ie.printStackTrace();
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
  }
  
  /**
   * Generates a FilterAction from the given property.
   */
  public static FilterAction generateFilterAction(String actionProperty) throws ClassNotFoundException, InstantiationException , IllegalAccessException {
    String className = Pooka.getProperty(actionProperty + ".class", "");
    Class filterClass = Class.forName(className);
    FilterAction newAction = (FilterAction)filterClass.newInstance();
    newAction.initializeFilter(actionProperty);
    return newAction;
  }
  
  // accessor methods.
  
  public SearchTerm getSearchTerm() {
    return searchTerm;
  }
  
  public void setSearchTerm(SearchTerm newTerm) {
    searchTerm = newTerm;
  }
  
  public FilterAction getAction() {
    return action;
  }
  
  public void setAction(FilterAction newAction) {
    action=newAction;
  }

  /**
   * Returns the source property used to create this.
   */
  public String getProperty() {
    return mProperty;
  }
}
