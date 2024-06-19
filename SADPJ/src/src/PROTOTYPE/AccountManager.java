package src.PROTOTYPE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

public class AccountManager {

  public static void main(String[] args) {
    /*
     	Create Prototypical Objects
     */
    Vector supervisorPermissions =
      getPermissionsFromFile("supervisor.txt");
    UserAccount supervisor = new UserAccount();
    supervisor.setPermissions(supervisorPermissions);

    Vector accountRepPermissions =
      getPermissionsFromFile("accountrep.txt");
    UserAccount accountRep = new UserAccount();
    accountRep.setPermissions(accountRepPermissions);

    AccountPrototypeFactory factory =
      new AccountPrototypeFactory(supervisor,
          accountRep);

    /* 	Using protype objects to create other user accounts */

    UserAccount newSupervisor = factory.getSupervisor();
    newSupervisor.setUserName("PKuchana");
    newSupervisor.setPassword("Everest");
    System.out.println(newSupervisor);

    UserAccount anotherSupervisor = factory.getSupervisor();
    anotherSupervisor.setUserName("SKuchana");
    anotherSupervisor.setPassword("Everest");
    System.out.println(anotherSupervisor);

    UserAccount newAccountRep = factory.getAccountRep();
    newAccountRep.setUserName("VKuchana");
    newAccountRep.setPassword("Vishal");
    System.out.println(newAccountRep);
  }

  public static Vector getPermissionsFromFile(String fileName) {
    Vector v = new Vector();
    String inputLine;
    try {
      File inFile = new File(fileName);
      BufferedReader br = new BufferedReader(
                            new InputStreamReader(
                              new FileInputStream(inFile)));

      while ((inputLine = br.readLine()) != null) {
        StringTokenizer st =
          new StringTokenizer(inputLine, ",");
        String resource = st.nextToken();
        String permission = st.nextToken();
        v.addElement(
          new Permission(resource, permission));
      }
      br.close();
    } // Try
    catch (FileNotFoundException ex) {
      //
    }
    catch (IOException ex) {
      //
    }
    return (v);
  }

}
