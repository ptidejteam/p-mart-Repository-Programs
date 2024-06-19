package src.PROTOTYPE;

public class Permission {

  private String resource;
  private String rights;

  public Permission(String res, String access) {
    resource = res;
    rights = access;
  }
  public void setResource(String rName) {
    resource = rName;
  }
  public String getResource() {
    return resource;
  }
  public void setRights(String access) {
    rights = access;
  }
  public String getRights() {
    return rights;
  }
  public Object clone() {
    Permission permission =
      new Permission(this.getResource(),
                     this.getRights());

    return permission;
  }

  public String toString() {
    return getResource() + "::" + getRights();
  }
}
