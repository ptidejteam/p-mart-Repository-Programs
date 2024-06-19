package src.ObjectAuthenticator;
public class AuthManager {
  public OrderIF getOrderAuthenticator(String clientCode) {
    return new OrderAuthenticator("xYzAbC", clientCode);
  }

}
