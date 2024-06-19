package src.DECORATOR;


public class LoggerDecorator implements Logger {

  Logger logger;

  public LoggerDecorator(Logger inp_logger) {
    logger = inp_logger;
  }

  public void log(String DataLine) {
    /*
     	Default implementation
     	to be overriden by subclasses.
     */
    logger.log(DataLine);
  }

}// end of class

