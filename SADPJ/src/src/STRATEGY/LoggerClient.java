package src.STRATEGY;
class LoggerClient {

  public static void main(String[] args) {
    FileLogger logger = new FileLogger();

    EncryptLogger eLogger = new EncryptLogger(logger);
    eLogger.log("log1.txt",
                "this message is to be encrypted & logged");
    EncryptionStrategy strategy = new SubstitutionCypher();
    eLogger.setEncryptionStrategy(strategy);
    eLogger.log("log2.txt",
                "this message is to be encrypted & logged");
    strategy = new CodeBookCypher();
    eLogger.setEncryptionStrategy(strategy);
    eLogger.log("log3.txt","This is a true statement");

  }

} // End of class

