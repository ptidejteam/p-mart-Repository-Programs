package com.taursys.examples.simpleweb.delegate;

import java.io.Serializable;
import java.util.Date;

/**
 * Value Object
 * @author Marty Phelan
 * @version 1.0
 */
public class NewsItemVO implements Serializable {
  private long itemId;
  private Date releaseDate;
  private String headline;
  private String summary;
  private String fullText;

  /**
   * Constructs a new NewsItemVO with default values.
   */
  public NewsItemVO() {
  }

  /**
   * Constructs a new NewsItemVO with given values.
   * @param itemId Unique identifier for this news item.
   * @param releaseDate the date this item is released.
   * @param headline the headline or title for this news item.
   * @param summary the summary of this news item.
   * @param fullText the full text of this news item.
   */
  public NewsItemVO(
      long itemId
      ,Date releaseDate
      ,String headline
      ,String summary
      ,String fullText
      ) {
    this.itemId = itemId;
    this.releaseDate = releaseDate;
    this.headline = headline;
    this.summary = summary;
    this.fullText = fullText;
  }

  /**
   * Constructs a new NewsItemVO with given values.
   * @param itemId Unique identifier for this news item.
   * @param releaseDate the date this item is released.
   * @param headline the headline or title for this news item.
   * @param summary the summary of this news item which will also be used for
   *    the fullText.
   */
  public NewsItemVO(
      long itemId
      ,Date releaseDate
      ,String headline
      ,String summary
      ) {
    this.itemId = itemId;
    this.releaseDate = releaseDate;
    this.headline = headline;
    this.summary = summary;
    this.fullText = summary;
  }

  /**
   * Set Unique identifier for this news item.
   * @param itemId Unique identifier for this news item.
   */
  public void setItemId(long itemId) {
    this.itemId = itemId;
  }

  /**
   * Get Unique identifier for this news item.
   * @return Unique identifier for this news item.
   */
  public long getItemId() {
    return itemId;
  }

  /**
   * Set the date this item is released.
   * @param releaseDate the date this item is released.
   */
  public void setReleaseDate(Date releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * Get the date this item is released.
   * @return the date this item is released.
   */
  public Date getReleaseDate() {
    return releaseDate;
  }

  /**
   * Set the headline or title for this news item.
   * @param headline the headline or title for this news item.
   */
  public void setHeadline(String headline) {
    this.headline = headline;
  }

  /**
   * Get the headline or title for this news item.
   * @return the headline or title for this news item.
   */
  public String getHeadline() {
    return headline;
  }

  /**
   * Set the summary of this news item.
   * @param summary the summary of this news item.
   */
  public void setSummary(String summary) {
    this.summary = summary;
  }

  /**
   * Get the summary of this news item.
   * @return the summary of this news item.
   */
  public String getSummary() {
    return summary;
  }

  /**
   * Set the full text of this news item.
   * @param fullText the full text of this news item.
   */
  public void setFullText(String fullText) {
    this.fullText = fullText;
  }

  /**
   * Get the full text of this news item.
   * @return the full text of this news item.
   */
  public String getFullText() {
    return fullText;
  }

  // =====================================================================
  //        Override of hashCode and equals for Primary Key
  // =====================================================================

}
