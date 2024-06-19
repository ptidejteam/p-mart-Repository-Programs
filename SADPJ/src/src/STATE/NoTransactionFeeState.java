
package src.STATE;
public class NoTransactionFeeState extends State {
  public NoTransactionFeeState(BusinessAccount account) {
    super(account);
  }

  public NoTransactionFeeState(State source) {
    super(source);
  }
  public boolean deposit(double amount) {
    return super.deposit(amount);
  }
  public boolean withdraw(double amount) {
    double balance = getContext().getBalance();

    if ((balance - amount) >
        BusinessAccount.OVERDRAW_LIMIT) {
      super.withdraw(amount);
      return true;
    } else {
      System.out.println(
        BusinessAccount.ERR_OVERDRAW_LIMIT_EXCEED);
      return false;
    }
  }

  public State transitionState() {
    double balance = getContext().getBalance();
    if (balance < 0) {
      getContext().setState(new OverDrawnState(this));
    } else {
      if (balance < BusinessAccount.MIN_BALANCE) {
        getContext().setState(
          new TransactionFeeState(this));
      }
    }
    return getContext().getState();
  }

}

