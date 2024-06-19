package src.STATE;
public class BusinessAccount {

  public static final double MIN_BALANCE = 2000.00;
  public static final double OVERDRAW_LIMIT = -1000.00;

  public static final double TRANS_FEE_NORMAL = 2.00;
  public static final double TRANS_FEE_OVERDRAW = 5.00;

  public static final String ERR_OVERDRAW_LIMIT_EXCEED =
    "Error: Transaction cannot be processed. " + 
      "Overdraw limit exceeded.";


  private State objState;
  private String accountNumber;
  private double balance;

  public void setState(State newState) {
    objState = newState;
  }
  public State getState() {
    return objState;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public boolean deposit(double amount) {
    return getState().deposit(amount);
  }
  public boolean withdraw(double amount) {
    return getState().withdraw(amount);
  }
  public BusinessAccount(String accountNum) {
    accountNumber = accountNum;
    objState = State.InitialState(this);
  }
  public double getBalance() {
    return balance;
  }

  public void setBalance(double newBalance) {
    balance = newBalance;
  }

}
