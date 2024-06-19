package src.STRATEGY;
public class SimpleEncryption implements EncryptionStrategy {

  public String encrypt(String inputData) {

    inputData = inputData.substring(inputData.length() - 1) +
                inputData.substring(0, inputData.length() - 1);

    return inputData;
  }

}
