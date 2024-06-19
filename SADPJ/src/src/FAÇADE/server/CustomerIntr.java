package src.FAÇADE.server;
import java.rmi.RemoteException;

public interface CustomerIntr extends java.rmi.Remote {

  void setAddress(String inAddress) throws RemoteException;
  void setCity(String inCity) throws RemoteException;
  void setState(String inState) throws RemoteException;
  void setFName(String inFName) throws RemoteException;
  void setLName(String inLName) throws RemoteException;
  void setCardType(String inCardType) throws RemoteException;
  void setCardNumber(String inCardNumber) 
    throws RemoteException;
  void setCardExpDate(String inCardExpDate) 
    throws RemoteException;
  boolean saveCustomerData() throws RemoteException;
}
