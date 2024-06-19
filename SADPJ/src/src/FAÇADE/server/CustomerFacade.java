package src.FAÇADE.server;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class CustomerFacade extends UnicastRemoteObject 
  implements CustomerIntr {

  private String address;
  private String city;
  private String state;
  private String cardType;
  private String cardNumber;
  private String cardExpDate;
  private String fname;
  private String lname;

  public CustomerFacade() throws RemoteException {
    super();
    System.out.println("Server object created");
  }

  public static void main(String[] args) throws Exception {
    String port = "1099";
    String host = "localhost";

    // Check for hostname argument
    if (args.length == 1) {
      host = args[0];
    }

    if (args.length == 2) {
      port = args[1];
    }

    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    //Create an instance of the server
    CustomerFacade facade = new CustomerFacade();

    //Bind it with the RMI Registry
    Naming.bind("//" + host + ":" + port + "/CustomerFacade",
                facade);

    System.out.println("Service Bound...");

  }
  public void setAddress(String inAddress)
  throws RemoteException {
    address = inAddress;
  }
  public void setCity(String inCity)
  throws RemoteException{ city = inCity;
  } public void setState(String inState)
  throws RemoteException{ state = inState;
  } public void setFName(String inFName)
  throws RemoteException{ fname = inFName;
  } public void setLName(String inLName)
  throws RemoteException{ lname = inLName;
  } public void setCardType(String inCardType)
  throws RemoteException {
    cardType = inCardType;
  }
  public void setCardNumber(String inCardNumber)
  throws RemoteException {
    cardNumber = inCardNumber;
  }
  public void setCardExpDate(String inCardExpDate)
  throws RemoteException {
    cardExpDate = inCardExpDate;
  }

  public boolean saveCustomerData() throws RemoteException{
    Address objAddress;
    Account objAccount;
    CreditCard objCreditCard;

    /*
    	client is transparent from the following
    	set of subsystem related operations.
    */

    boolean validData = true;
    String errorMessage = "";

    objAccount = new Account(fname, lname);
    if (objAccount.isValid() == false) {
      validData = false;
      errorMessage = "Invalid FirstName/LastName";
    }

    objAddress = new Address(address, city, state);
    if (objAddress.isValid() == false) {
      validData = false;
      errorMessage = "Invalid Address/City/State";
    }

    objCreditCard = new CreditCard(cardType, cardNumber,
                    cardExpDate);
    if (objCreditCard.isValid() == false) {
      validData = false;
      errorMessage = "Invalid CreditCard Info";
    }

    if (!validData) {
      System.out.println(errorMessage);
      return false;
    }

    if (objAddress.save() && objAccount.save() &&
        objCreditCard.save()) {

      return true;
    } else {
      return false;
    }

  }
}

