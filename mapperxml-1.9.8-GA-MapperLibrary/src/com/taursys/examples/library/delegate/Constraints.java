package com.taursys.examples.library.delegate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author marty
 */
public class Constraints {
  private static final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

  /**
   * Check to ensure that a value is within the given range (or null)
   * 
   * @param valueName
   * @param value
   * @param minimum
   * @param maximum
   * @throws ValidationException
   *           if out of range
   */
  public static void checkRange(String valueName, BigDecimal value, String minimum,
      String maximum) throws ValidationException {
    if (value != null) {
      if (value.compareTo(new BigDecimal(minimum)) == -1
          || value.compareTo(new BigDecimal(maximum)) == 1) {
        throw new ValidationException("Value for " + valueName
            + " is out of range. Range is from " + minimum + " to " + maximum);
      }
    }
  }

  /**
   * Checks to ensure that given value is in given set of acceptable values
   * 
   * @param valueName
   * @param value
   * @param set
   * @throws ValidationException
   */
  public static void checkIn(String valueName, int value, int[] set, String setNames) throws ValidationException {
    for (int i = 0; i < set.length; i++) {
      if (value == set[i]) {
        return;
      }
    }
    throw new ValidationException("Value for " + valueName
        + " is not valid. Must be one of: " + setNames);
  }

  /**
   * Check to ensure that a value is within the given range (or null)
   * 
   * @param valueName
   * @param value
   * @param minimum
   * @param maximum
   * @throws ValidationException
   *           if out of range
   */
  public static void checkRange(String valueName, int value, String minimum,
      String maximum) throws ValidationException {
    checkRange(valueName, new BigDecimal(value), minimum, maximum);
  }

  /**
   * Check to ensure that a value is within the given range (or null)
   * 
   * @param valueName
   * @param value
   * @param minimum
   * @param maximum
   * @throws ValidationException
   *           if out of range
   */
  public static void checkRange(String valueName, Date value, Date minimum,
      Date maximum) throws ValidationException {
    if (value != null) {
      if (minimum != null && maximum != null) {
        if (value.compareTo(minimum) == -1
            || value.compareTo(maximum) == 1) {
          throw new ValidationException("Value for " + valueName
              + " is out of range. Range is from " + df.format(minimum) 
              + " to " + df.format(maximum));
        }
      } else {
        throw new ValidationException("Cannot validate " + valueName 
            + ". Validation range is invalid. " 
            + (minimum == null ? "Minimum value is null. " : "")
            + (maximum == null ? "Maximum value is null." : "")
            );
      }
    }
  }

  /**
   * Check to ensure that the two given values are not reversed in magnitude
   * 
   * @param valueName
   * @param begin
   * @param end
   * @throws ValidationException
   */
  public static void checkNotReversed(String valueName, Comparable begin,
      Comparable end) throws ValidationException {
    if (begin != null && end != null) {
      if (begin.compareTo(end) == 1) {
        throw new ValidationException("Values for " + valueName
            + " are reversed");
      }
    }
  }

  /**
   * Check to ensure that a value is within the given range (or null)
   * 
   * @param valueName
   * @param value
   * @param minimum
   * @param maximum
   * @throws ValidationException
   *           if out of range
   */
  public static void checkRange(String valueName, Integer value, String minimum,
      String maximum) throws ValidationException {
    if (value != null) {
      checkRange(valueName, new BigDecimal(value.toString()), minimum, maximum);
    }
  }

  /**
   * Check to ensure that value is not null.
   * 
   * @param valueName
   * @param value
   * @throws ValidationException
   *           if null
   */
  public static void checkNotNull(String valueName, Object value)
      throws ValidationException {
    if (value == null) {
      throw new ValidationException("Value for " + valueName
          + " cannot be blank.");
    }
  }
  
  /**
   * Check to ensure that value is not null or blank.
   * 
   * @param valueName
   * @param value
   * @throws ValidationException
   *           if null
   */
  public static void checkNotNullOrBlank(String valueName, String value)
      throws ValidationException {
    if (value == null || value.length() == 0) {
      throw new ValidationException("Value for " + valueName
          + " cannot be blank.");
    }
  }

  /**
   * Checks to ensure String is within size range. If minSize > 0,
   * then also checks to ensure not null.
   * 
   * @param valueName
   * @param value
   * @param minSize
   * @param maxSize
   * @throws ValidationException
   */
  public static void checkSizeRange(String valueName, String value,
      int minSize, int maxSize) throws ValidationException {
    if (minSize > 0) {
      checkNotNull(valueName, value);
    }
    checkMinSize(valueName, value, minSize);
    checkMaxSize(valueName, value, maxSize);
  }
  
  /**
   * Check to ensure that value is not too long.
   * @param valueName
   * @param value
   * @param maxSize
   * @throws ValidationException if too long
   */
  public static void checkMaxSize(String valueName, String value, int maxSize) throws ValidationException {
    if (value != null) {
      if (value.length() > maxSize) {
        throw new ValidationException("Value for " + valueName + " is too long. Maximum allowed length is " + maxSize);
      }
    }
  }
  
  /**
   * Check to ensure that value is not too short.
   * @param valueName
   * @param value
   * @param minSize
   * @throws ValidationException if too short
   */
  public static void checkMinSize(String valueName, String value, int minSize) throws ValidationException {
    if (value != null) {
      if (value.length() < minSize) {
        throw new ValidationException("Value for " + valueName + " is too short. Minimum allowed length is " + minSize);
      }
    }
  }  

  /**
   * Check to ensure that value is not a negative number
   * 
   * @param valueName
   * @param value
   * @throws ValidationException
   */
  public static void checkNotNegative(String valueName, BigDecimal value)
      throws ValidationException {
    if (value != null) {
      if (value.compareTo(new BigDecimal("0")) == -1) {
        throw new ValidationException("Value for " + valueName
            + " cannot be negative");
      }
    }
  }

}
