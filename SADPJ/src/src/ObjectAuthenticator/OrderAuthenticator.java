package src.ObjectAuthenticator;
public class OrderAuthenticator implements OrderIF {
//  private OrderManager client;
  private String accessCode;
  private String clientCode;

  public OrderAuthenticator(String aCode, String cCode) {
    accessCode = aCode;
    clientCode = cCode;
  }

  public void create(String item,
                     int qty) throws UnAuthorizedUserException {
    if (clientCode.equals(accessCode)) {
      Order ord = new Order();
      ord.create(item, qty);
    } else {
      throw new UnAuthorizedUserException();
    }
  }

}
