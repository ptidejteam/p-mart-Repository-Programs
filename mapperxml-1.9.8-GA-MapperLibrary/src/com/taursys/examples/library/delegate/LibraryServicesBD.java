package com.taursys.examples.library.delegate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;


public class LibraryServicesBD {
  private static LibraryServicesBD bd;
  private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
  private Hashtable books = new Hashtable();
  private Hashtable locations = new Hashtable();

  // =========================================================================
  // Constructors
  // =========================================================================

  /**
   * Singleton constructor
   */
  private LibraryServicesBD() {
    // Initialize library with locations and books
    locations.put(new Integer(1), new LocationVO(1, "Downtown Main Branch"));
    locations.put(new Integer(2), new LocationVO(2, "Valley Branch"));
    locations.put(new Integer(3), new LocationVO(3, "Douglas Branch"));
    try {
      putBook(new BookVO("156592262X", "Java in a Nutshell",
          "java, programming", df.parse("2000-11-30"), new BigDecimal("50"), 1));
      putBook(new BookVO("1565924940", "Dynamic HTML",
          "javascript, programming, html", df.parse("2000-10-05"),
          new BigDecimal("39.95"), 3));
      putBook(new BookVO("0078820979",
          "Oracle The Complete Reference 3rd Edition",
          "oracle, programing, database", df.parse("2000-11-05"),
          new BigDecimal("88.99"), 2));
      putBook(new BookVO("1565921275",
          "Essential System Administration 2nd Edition",
          "unix, administration", df.parse("2000-12-31"), new BigDecimal(
              "45.96"), 1));
      putBook(new BookVO("0789719932", "Using StarOffice for the Beginner",
          "office, applications", df.parse("2000-12-05"), new BigDecimal(
              "39.95"), 2));
      putBook(new BookVO("0201702673", "Applying Enterprise JavaBeans",
          "java, j2ee, programming", df.parse("2001-07-05"), new BigDecimal(
              "39.95"), 3));
      putBook(new BookVO("0201705028", "JNDI API Tutorial and Reference",
          "jndi, java", df.parse("2001-07-03"), new BigDecimal("42.95"), 1));
      putBook(new BookVO("013127604",
          "Essential CSS and DHTML for Web Professionals", "html, css, dhtml",
          df.parse("2001-07-05"), new BigDecimal("29.99"), 2));
      books.put("",
          new BookVO("0471332291",
              "Mastering Enterprise JavaBeans and the Java 2 Plat",
              "java, j2ee, ejb", df.parse("2001-07-05"),
              new BigDecimal("49.99"), 3));
      putBook(new BookVO("0782121802", "Mastering Java 2", "java, programming",
          df.parse("2001-07-14"), new BigDecimal("49.95"), 3));
      putBook(new BookVO("1565927443", "SQL in a Nutshell", "sql, database", df
          .parse("2001-07-22"), new BigDecimal("29.95"), 2));
      putBook(new BookVO("1565924495", "Using Samba", "samba, network, server",
          df.parse("2002-11-06"), new BigDecimal("34.95"), 2));
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Utility method for constructor for books
   * 
   * @param vo
   *          BookVO to add to Hashtable
   */
  private void putBook(BookVO vo) {
    books.put(vo.getCatalogNo(), vo);
  }

  /**
   * Obtain access to singleton instance of LibraryServicesBD
   * 
   * @return singleton instance of LibraryServicesBD
   */
  public static LibraryServicesBD getInstance() {
    if (bd == null) {
      bd = new LibraryServicesBD();
    }
    return bd;
  }

  // =========================================================================
  // Public Services
  // =========================================================================

  /**
   * Get all books in the library collection.
   * 
   * @return Collection of all BookVO's in library collection
   */
  public Collection getAllBooks() {
    // Wrap list so does not get altered
    return new ArrayList(books.values());
  }

  /**
   * Get all books in the library collection that match searchKey by keyword.
   * 
   * @param searchKey -
   *          keyword to search for
   * 
   * @return Collection of BookVO's
   */
  public Collection getAllMatchingBooks(String searchKey) {
    if (searchKey == null || searchKey.length() < 1) {
      return getAllBooks();
    }
    ArrayList results = new ArrayList();
    for (Iterator iter = books.values().iterator(); iter.hasNext();) {
      BookVO vo = (BookVO) iter.next();
      if (vo.getKeywords().toLowerCase().indexOf(searchKey.toLowerCase()) != -1) {
        results.add(vo);
      }
    }
    return results;
  }

  /**
   * Get spcific book in the library collection by catalogNo
   * 
   * @param catalogNo
   *          to lookup
   * 
   * @return the BookVO
   */
  public BookVO getBook(String catalogNo) throws ApplicationException {
    BookVO vo = (BookVO) books.get(catalogNo);
    if (vo == null) {
      throw new ApplicationException("Book not found for catalogNo: "
          + catalogNo);
    }
    return vo;
  }

  /**
   * Validates book against business rules
   * 
   * @param vo
   * @throws ValidationException
   *           if fails any business rules
   */
  private void validateBook(BookVO vo) throws ValidationException {
    Constraints.checkNotNull("Catalog No", vo.getCatalogNo());
    Constraints.checkNotNull("Title", vo.getTitle());
    Constraints.checkNotNull("Cost", vo.getCost());
    Constraints.checkNotNull("Date added", vo.getDateAdded());
    Constraints.checkNotNegative("Cost", vo.getCost());
  }

  /**
   * Adds a new book to the library collection
   */
  public void addBook(BookVO vo) throws ValidationException {
    validateBook(vo);
    putBook(vo);
  }

  /**
   * Updates the given book
   * 
   * @param vo
   *          the book to update
   */
  public void updateBook(BookVO vo) throws ValidationException {
    validateBook(vo);
    putBook(vo);
  }

  /**
   * Deletes the book for the given key
   * 
   * @return Collection of BookVO's
   */
  public void deleteBook(String catalogNo) throws ValidationException {
    books.remove(catalogNo);
  }

  /**
   * Create
   * 
   * @return
   */
  public BookVO createDefaultBook() {
    BookVO vo = new BookVO();
    vo.setLocationId(1);
    vo.setDateAdded(new Date());
    return vo;
  }

  /**
   * Get all library locations.
   * 
   * @return Collection of all library LocationVO's
   */
  public Collection getAllLocations() {
    return locations.values();
  }

}
