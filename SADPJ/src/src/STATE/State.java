package src.STATE;
public class State {
  private BusinessAccount context;

  public BusinessAccount getContext() {
    return context;
  }

  public void setContext(BusinessAccount newAccount) {
    context = newAccount;
  }

  public State transitionState() {
    return null;
  }

  public State(BusinessAccount account) {
    setContext(account);
  }

  public State(State source) {
    setContext(source.getContext());
  }

  public static State InitialState(BusinessAccount account) {
    return new NoTransactionFeeState(account);
  }
  public boolean deposit(double amount) {
    double balance = getContext().getBalance();

    getContext().setBalance(balance + amount);
    transitionState();
    System.out.println("An amount " + amount +
                       " is deposited ");
    return true;
  }
  public boolean withdraw(double amount) {
    double balance = getContext().getBalance();

    getContext().setBalance(balance - amount);
    transitionState();
    System.out.println("An amount " + amount +
                       " is withdrawn ");
    return true;
  }

}
