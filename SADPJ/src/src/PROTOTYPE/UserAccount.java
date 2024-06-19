package src.PROTOTYPE;

import java.util.Vector;

public class UserAccount implements Cloneable {

  private String userName;
  private String password;
  private String fname;
  private String lname;
  private Vector permissions = new Vector();

  public void setUserName(String uName) {
    userName = uName;
  }
  public String getUserName() {
    return userName;
  }
  public void setPassword(String pwd) {
    password = pwd;
  }
  public String getPassword() {
    return password;
  }
  public void setFName(String name) {
    fname = name;
  }
  public String getFName() {
    return fname;
  }
  public void setLName(String name) {
    lname = name;
  }
  public String getLName() {
    return lname;
  }
  public void setPermissions(Vector rights) {
    permissions = rights;
  }
  public Vector getPermissions() {
    return permissions;
  }
  public void addPermission(Permission p) {
    permissions.add(p);
  }
  public Object clone() {

    //Shallow Copy

    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }

    /* Deep Copy

     UserAccount account=new UserAccount();

     account.setUserName(this.getUserName());
     account.setPassword(this.getPassword());
     account.setFName(this.getFName());
     account.setLName(this.getLName());

     Vector v=new Vector();
     for (int i=0;i<permissions.size();i++){
     	v.addElement(((Permission)permissions.elementAt(i))
     	  .clone());
     }
     account.setPermissions(v);

     //return the copy object
     return account;
     */
  }
  public String toString() {
    String str = "User: " + getUserName() + "\n" + 
      " Permissions : \n";

    for (int i = 0; i < permissions.size(); i++) {
      str = str + (Permission) permissions.elementAt(i) + "\n";

    }
    return str;
  }
}
