package src.STATE;
public class TransactionFeeState extends State {

  public TransactionFeeState(BusinessAccount account) {
    super(account);
  }
  public TransactionFeeState(State source) {
    super(source);
  }

  public State transitionState() {
    double balance = getContext().getBalance();
    if (balance < 0) {
      getContext().setState(new OverDrawnState(this));
    } else {
      if (balance >= BusinessAccount.MIN_BALANCE) {
        getContext().setState(
          new NoTransactionFeeState(this));
      }
    }
    return getContext().getState();
  }
  public boolean deposit(double amount) {
    double balance = getContext().getBalance();

    getContext().setBalance(balance -
        BusinessAccount.TRANS_FEE_NORMAL);
    System.out.println(
      "Transaction Fee was charged due to " +
      "account status " +
      "(less than minimum balance)");
    return super.deposit(amount);
  }
  public boolean withdraw(double amount) {
    double balance = getContext().getBalance();

    if ((balance - BusinessAccount.TRANS_FEE_NORMAL -
         amount) > BusinessAccount.OVERDRAW_LIMIT) {
      getContext().setBalance(balance -
          BusinessAccount.TRANS_FEE_NORMAL);
      System.out.println(
        "Transaction Fee was charged due to " +
        "account status " +
        "(less than minimum balance)");
      return super.withdraw(amount);
    } else {
      System.out.println(
        BusinessAccount.ERR_OVERDRAW_LIMIT_EXCEED);
      return false;
    }

  }


}
