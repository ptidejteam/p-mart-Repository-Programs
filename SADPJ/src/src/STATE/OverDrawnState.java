package src.STATE;
public class OverDrawnState extends State {

  public void sendMailToAccountHolder() {
    System.out.println (
      "Attention: Your Account is Overdrawn");
  }

  public OverDrawnState(BusinessAccount account) {
    super(account);
    sendMailToAccountHolder();
  }
  public OverDrawnState(State source) {
    super(source);
    sendMailToAccountHolder();
  }

  public State transitionState() {
    double balance = getContext().getBalance();
    if (balance >= BusinessAccount.MIN_BALANCE)
      getContext().setState(
        new NoTransactionFeeState(this));
    else if (balance >= 0)
      getContext().setState(new TransactionFeeState(this));


    return getContext().getState();
  }

  public boolean deposit(double amount) {
    double balance = getContext().getBalance();

    getContext().setBalance(balance -
        BusinessAccount.TRANS_FEE_OVERDRAW);
    System.out.println("Transaction Fee was charged " +
                       "due to account status(Overdrawn)");
    return super.deposit(amount);
  }

  public boolean withdraw(double amount) {
    double balance = getContext().getBalance();

    if ((balance - BusinessAccount.TRANS_FEE_OVERDRAW -
         amount) > BusinessAccount.OVERDRAW_LIMIT) {

      getContext().setBalance(balance -
          BusinessAccount.TRANS_FEE_OVERDRAW);
      System.out.println(
        "Transaction Fee was charged due to " +
        "account status(Overdrawn)");
      return super.withdraw(amount);

    } else {
      System.out.println(
        BusinessAccount.ERR_OVERDRAW_LIMIT_EXCEED);
      return false;
    }
  }

}
