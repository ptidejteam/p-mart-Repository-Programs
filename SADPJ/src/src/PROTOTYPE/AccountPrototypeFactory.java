package src.PROTOTYPE;

public class AccountPrototypeFactory {

  private UserAccount accountRep;
  private UserAccount supervisor;

  public AccountPrototypeFactory(UserAccount supervisorAccount,
      UserAccount arep) {
    accountRep = arep;
    supervisor = supervisorAccount;
  }
  public UserAccount getAccountRep() {
    return (UserAccount) accountRep.clone();
  }
  public UserAccount getSupervisor() {
    return (UserAccount) supervisor.clone();
  }
}
