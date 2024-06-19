package net.suberic.pooka;
import java.util.*;
import java.text.SimpleDateFormat;


/**
 * This is a little utility class which stores all of the DateFormat classes
 * that we use.
 */
public class DateFormatter {

  // the full date.  for reply headers such as 'on Friday, Jan 01, 2000,
  // Greyface <greyface@corporation.co.thud> wrote...'
  public SimpleDateFormat fullDateFormat;

  // a time today, used in the message table.
  public SimpleDateFormat todayFormat;

  // a date this past week, so we can just say the day rather than the
  // full date if we wish.
  public SimpleDateFormat thisWeekFormat;

  // a short format which shows the full date, but doesn't take up much
  // room.
  public SimpleDateFormat shortFormat;

  public DateFormatter() {
    fullDateFormat = new java.text.SimpleDateFormat(Pooka.getProperty("DateFormat", "EEE, MMM dd, yyyy, hh:mm"));
    todayFormat = new java.text.SimpleDateFormat(Pooka.getProperty("FolderTable.TodayDateFormat", "HH:mm"));
    thisWeekFormat = new java.text.SimpleDateFormat(Pooka.getProperty("FolderTable.ThisWeekDateFormat", "EEE HH:mm"));
    shortFormat = new java.text.SimpleDateFormat(Pooka.getProperty("FolderTable.DefaultDateFormat", "MM/dd/yy HH:mm"));

  }


}
