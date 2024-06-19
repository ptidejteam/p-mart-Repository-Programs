package src.ObjectAuthenticator;
public class OrderManager {
  public void createOrder(String item,
      int qty) throws UnAuthorizedUserException {
    AuthManager manager = new AuthManager();
    OrderIF authenticator =
      manager.getOrderAuthenticator("xYzAbC");
    authenticator.create(item, qty);
  }

}
