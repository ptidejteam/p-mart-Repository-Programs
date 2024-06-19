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

import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Business Delegate is responsible for obtaining needed data and performing transactions.
 * It may do this by delegating the work to other local or remote objects.
 */
public class BusinessDelegate {
  DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
  private ArrayList people = new ArrayList();

  /**
   * Constructs a new BusinessDelagate and adds some test records.
   */
  public BusinessDelegate() {
    try {
    people.add(new Person(0, "Picard", "Jean Luc", df.parse("05/05/1955")));
    people.add(new Person(1, "Riker", "Will", df.parse("07/02/1916")));
    people.add(new Person(2, "Crusher", "Beverly", df.parse("10/28/1924")));
    } catch (ParseException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Get the Person who how has the given id.
   */
  public Person getPerson(int id) throws Exception {
    return (Person)people.get(id);
  }

  /**
   * Get a collection of all the Person's in the system.
   */
  public Collection getAllPeople() throws Exception {
    return people;
  }
}