package src.BRIDGE;

public class Client {
  public static void main(String[] args) {
    //Create an appropriate implementer object
    MessageLogger logger = new FileLogger();

    //Choose required interface object and
    // configure it with the implementer object
    Message msg = new EncryptedMessage(logger);

    msg.log("Test Message");

  }
}
