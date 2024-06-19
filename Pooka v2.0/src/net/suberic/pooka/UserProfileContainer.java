package net.suberic.pooka;

/**
 * This interface may be implemented by any object which could have a
 * default UserProfile associated with it.
 *
 */

public interface UserProfileContainer {
  /**
   * If the object does have a UserProfile associated with it, this method
   * should return that UserProfile.  If the object does not, the method
   * should return null.
   */
  public UserProfile getDefaultProfile();

}

