package com.taursys.examples.simpleweb;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class RBGColor {
  private String title;
  private String code;
  public static final String TITLE = "title";
  public static final String CODE = "code";

  public RBGColor() {
  }

  public RBGColor(String title, String code) {
    this.title = title;
    this.code = code;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String newTitle) {
    title = newTitle;
  }

  public void setCode(String newCode) {
    code = newCode;
  }

  public String getCode() {
    return code;
  }

  public String toString() {
    return title;
  }
}