package net.suberic.pooka;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import net.suberic.util.*;

/**
 * <p>An object which manages UserProfile resources.</p>
 *
 * @author Allen Petersen
 * @version $Revision$
 */
public class UserProfileManager implements ItemCreator, ItemListChangeListener {

  private ItemManager manager;
  private LinkedList listenerList = new LinkedList();

  private List mailPropertiesList = null;

  /**
   * <p>Creates a new UserProfileManager.</p>
   */
  public UserProfileManager(VariableBundle sourceBundle) {
    createMailPropertiesList(sourceBundle);
    createUserProfileList(sourceBundle);
  }

  //-----------------------
  // public interface.

  /**
   * This listens for ItemListChangeEvents, which result from changes to the
   * "UserProfile" property. The event is passed to listeners to this object.
   */
  public void itemListChanged(ItemListChangeEvent e) {
    fireItemListChanged(e);
  }

  /**
   * This returns a List with all the currently registered UserProfile
   * objects.
   */
  public java.util.List getUserProfileList() {
    return manager.getItems();
  }

  /**
   * This adds the UserProfile with the given UserProfileName to the
   * allUserProfiles list.
   */
  public void addUserProfile(String UserProfileName) {
    manager.addItem(UserProfileName);
  }

  /**
   * This adds the UserProfiles with the given UserProfileNames to the allUserProfiles list.
   */
  public void addUserProfile(String[] UserProfileName) {
    manager.addItem(UserProfileName);
  }

  /**
   * This removes the UserProfile with the given UserProfileName.
   */
  public void removeUserProfile(String UserProfileName) {
    manager.removeItem(UserProfileName);
  }

  /**
   * This removes the UserProfiles with the given UserProfileNames.
   */
  public void removeUserProfile(String[] UserProfileNames) {
    manager.removeItem(UserProfileNames);
  }

  /**
   * This removes the given UserProfile.
   */
  public void removeUserProfile(UserProfile UserProfile) {
    manager.removeItem(UserProfile);
  }

  /**
   * This removes the given UserProfiles.
   */
  public void removeUserProfile(UserProfile[] UserProfiles) {
    manager.removeItem(UserProfiles);
  }

  /**
   * This returns the UserProfile with the given UserProfileName if it
   * exists; otherwise, returns null.
   */
  public UserProfile getUserProfile(String UserProfileID) {
    return (UserProfile) manager.getItem(UserProfileID);
  }

  /**
   * This returns the UserProfile with the given UserProfileName if it
   * exists; otherwise, returns null.
   */
  public UserProfile getProfile(String UserProfileID) {
    return getUserProfile(UserProfileID);
  }

  /**
   * This returns the UserProfile with the given UserProfileName if it
   * exists; otherwise, returns null.
   */
  public UserProfile getDefaultProfile() {
    UserProfile defaultUser = getUserProfile(Pooka.getProperty("UserProfile.default", ""));
    if (defaultUser == null) {
      List profileList = manager.getItems();
      if (profileList != null && profileList.size() > 0) {
        defaultUser = (UserProfile) profileList.get(0);
      }
    }
    return defaultUser;
  }

  /**
   * This adds a ItemListChangeListener to the local listener list.
   */
  public void addItemListChangeListener(ItemListChangeListener ilcl) {
    if (! listenerList.contains(ilcl))
      listenerList.add(ilcl);
  }

  /**
   * This removes a ItemListChangeListener from the local listener list.
   */
  public void removeItemListChangeListener(ItemListChangeListener ilcl) {
    listenerList.remove(ilcl);
  }

  /**
   * This notifies all listeners that the UserProfileList has changed.
   */
  public void fireItemListChanged(ItemListChangeEvent e) {
    for (int i = 0; i < listenerList.size(); i++)
      ((ItemListChangeListener)listenerList.get(i)).itemListChanged(e);
  }


  /**
   * This creates a new UserProfile.
   */
  public Item createItem(VariableBundle sourceBundle, String resourceString, String itemID) {
    UserProfile newProfile = new UserProfile(itemID, sourceBundle);
    newProfile.initializeFromProperties(sourceBundle, mailPropertiesList);
    return newProfile;

  }

  //---------------------------
  // the background stuff.

  /**
   * This loads and creates all the UserProfiles using the "UserProfile"
   * property of the main Pooka VariableBundle.
   */
  private void createUserProfileList(VariableBundle sourceBundle) {
    manager = new ItemManager("UserProfile", sourceBundle, this);

    manager.addItemListChangeListener(this);
  }

  /**
   * This creates the profile map that we'll use to create new
   * Profile objects.
   */
  public void createMailPropertiesList(VariableBundle mainProperties) {
    mailPropertiesList = new ArrayList();

    // Initialize Profile Map

    StringTokenizer tokens = new StringTokenizer(mainProperties.getProperty("UserProfile.mailHeaders.fields", "From:FromPersonal:ReplyTo:ReplyToPersonal:Organization"), ":");
    while (tokens.hasMoreTokens()) {
      mailPropertiesList.add(tokens.nextToken());
    }

  }

  public List getMailPropertiesList() {
    return mailPropertiesList;
  }

  /**
   * Removes all UserProfiles from this Manager.
   */
  public void shutdownManager() {
    VariableBundle resources = Pooka.getResources();
    List profiles = getUserProfileList();
    for (int i = 0; i < profiles.size(); i++) {
      UserProfile up = (UserProfile) profiles.get(i);
      resources.removeValueChangeListener(up);
    }
    resources.removeValueChangeListener(manager);
  }

}
