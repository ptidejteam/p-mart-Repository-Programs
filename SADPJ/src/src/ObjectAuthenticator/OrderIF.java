package src.ObjectAuthenticator;
public interface OrderIF {
  public void create(String item,
                     int qty) throws UnAuthorizedUserException;
    }
