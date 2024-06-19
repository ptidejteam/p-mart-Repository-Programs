package src.FAÇADE.server;
public class CreditCard {

  String cardType;
  String cardNumber;
  String cardExpDate;

  public static final String CC_DATA_FILE = "CC.txt";

  public static final String DISCOVER = "Discover";
  public static final String MASTER = "Master";
  public static final String VISA = "Visa";
  public static final String VALIDCARD = "Valid Card";

  public CreditCard(String ccType, String ccNumber,
                    String ccExpDate) {
    cardType = ccType;
    cardNumber = ccNumber;
    cardExpDate = ccExpDate;
  }

  public boolean isValid() {
    /*
     	Let's go with simpler validation
     	here to keep the example simpler.
     */
    if (getCardType().equals(CreditCard.VISA)) {
      return (getCardNumber().trim().length() == 16);
    }
    if (getCardType().equals(CreditCard.DISCOVER)) {
      return (getCardNumber().trim().length() == 15);
    }
    if (getCardType().equals(CreditCard.MASTER)) {
      return (getCardNumber().trim().length() == 16);
    }

    return false;
  }
  public boolean save() {
    FileUtil futil = new FileUtil();
    String dataLine =
      getCardType() + "," + getCardNumber() + "," +
      getCardExpDate();
    return futil.writeToFile(CC_DATA_FILE, dataLine, true,
           true);
  }
  public String getCardType() {
    return cardType;
  }
  public String getCardNumber() {
    return cardNumber;
  }
  public String getCardExpDate() {
    return cardExpDate;
  }

}

