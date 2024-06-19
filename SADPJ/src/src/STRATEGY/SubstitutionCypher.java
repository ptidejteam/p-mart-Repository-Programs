package src.STRATEGY;
public class SubstitutionCypher implements EncryptionStrategy {

  char[] source = {'a','b','c','d','e','f','g','h','i','j','k',
                   'l','m','n','o','p','q','r','s','t','u','v',
                   'w','x','y','z'};

  char[] dest = {'m','n','o','p','q','r','a','b','c','d','e',
                 'f','g','h','i','j','k','l','y','z','s','t',
                 'u','v','w', 'x'};

  public String encrypt(String inputData) {

    char[] chars = inputData.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      for (int j = 0; j < source.length; j++) {
        if (source[j] == chars[i]) {
          c = dest[j];
        }
      }
      chars[i] = c;
    }

    return new String(chars);
  }

}
