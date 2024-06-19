/**
 * Example Mapper Application
 * by: Marty Phelan
 *
 * This example is free software; you can redistribute it and/or
 * modify it as you wish.  It is released to the public domain.
 *
 * This example is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.taursys.examples.simpleweb;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ValueObject for a Person.  Also contains a collection of Deductions.
 */
public class Person {
  private long personId;
  private String lastName;
  private String firstName;
  private java.util.Date birthdate;
  private Collection deductions = new ArrayList();
  private String favoriteColor;
  public static final String LAST_NAME = "lastName";
  public static final String FIRST_NAME = "firstName";
  public static final String BIRTHDATE = "birthdate";
  public static final String DEDUCTIONS = "deductions";
  public static final String FAVORITE_COLOR = "favoriteColor";
  public static final String FULL_NAME = "fullName";

  public Person() {
  }
  public Person(long pid, String ln, String fn, Date bd) {
    personId = pid;
    lastName = ln;
    firstName = fn;
    birthdate = bd;
  }
  public long getPersonId() {
    return personId;
  }
  public void setPersonId(long newPersonId) {
    personId = newPersonId;
  }
  public void setLastName(String newLastName) {
    lastName = newLastName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setFirstName(String newFirstName) {
    firstName = newFirstName;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setBirthdate(java.util.Date newBirthdate) {
    birthdate = newBirthdate;
  }
  public java.util.Date getBirthdate() {
    return birthdate;
  }
  public Collection getDeductions() {
    return deductions;
  }
  public void setDeductions(Collection c) {
    deductions = c;
  }
  public void addDeduction(Deduction d) {
    deductions.add(d);
  }
  public void setFavoriteColor(String newFavoriteColor) {
    favoriteColor = newFavoriteColor;
  }
  public String getFavoriteColor() {
    return favoriteColor;
  }
  public String getFullName() {
    return firstName + " " + lastName;
  }
}