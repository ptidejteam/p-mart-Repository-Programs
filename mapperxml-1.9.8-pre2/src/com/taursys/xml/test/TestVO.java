/*
 * Created on Jul 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.taursys.xml.test;

import java.util.Date;

/**
 * @author Marty
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestVO {
  private String lastName;
  private String firstName;
  private Date birthdate;
  private int personId;

  /**
   * 
   */
  public TestVO() {
  }

  public TestVO(String lastName, String firstName) {
    this.lastName = lastName;
    this.firstName = firstName;
  }

  /**
   * @return Returns the birthdate.
   */
  public Date getBirthdate() {
    return birthdate;
  }
  /**
   * @param birthdate The birthdate to set.
   */
  public void setBirthdate(Date birthdate) {
    this.birthdate = birthdate;
  }
  /**
   * @return Returns the firstName.
   */
  public String getFirstName() {
    return firstName;
  }
  /**
   * @param firstName The firstName to set.
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  /**
   * @return Returns the lastName.
   */
  public String getLastName() {
    return lastName;
  }
  /**
   * @param lastName The lastName to set.
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  /**
   * @return Returns the personId.
   */
  public int getPersonId() {
    return personId;
  }
  /**
   * @param personId The personId to set.
   */
  public void setPersonId(int personId) {
    this.personId = personId;
  }

}
