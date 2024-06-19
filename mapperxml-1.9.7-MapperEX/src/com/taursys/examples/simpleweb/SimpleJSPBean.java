package com.taursys.examples.simpleweb;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class SimpleJSPBean {
  private String sample = "Start value (version 0.3)";

  /**Access sample property*/
  public String getSample() {
    return sample;
  }

  /**Access sample property*/
  public void setSample(String newValue) {
    if (newValue!=null) {
      sample = newValue;
    }
  }
}