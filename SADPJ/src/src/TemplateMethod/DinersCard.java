package src.TemplateMethod;

public class DinersCard extends CreditCard {

  public DinersCard(String num, int expMonth, int expYear) {
    super(num, expMonth, expYear);
  }

  public boolean isNumOfDigitsValid() {
    if (cardNum.length() == 14) {
      return true;
    } else {
      return false;
    }
  }
  public boolean isValidPrefix() {
    String prefix = cardNum.substring(0, 1);
    String nextChar = cardNum.substring(1, 2);
    String validChars = "068";

    //51-55
    if ((prefix.equals("3")) &&
        (validChars.indexOf(nextChar) >= 0)) {
      return true;
    } else {
      return false;
    }
  }
  public boolean isAccountInGoodStand() {
    /*
     	Make necessary DINERS CARD API calls to
     	perform other checks.
     */
    return true;
  }

}
