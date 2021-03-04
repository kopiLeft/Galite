package org.kopi.galite.ui.vaadin.grid

import java.io.Serializable

/**
 * The serialized for of an auto complete suggestion.
 */
class AutocompleteSuggestion : Serializable {
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  fun getDisplayStringAsHTML(col: Int): String {
    return if (col == 0) {
      "<span class=\"notMatch\">" + getNotMatchingLeftPortion(col) + "</span>" +
              "<span class=\"match\">" + getMatchingPortion(col) + "</span>" +
              "<span class=\"notMatch\">" + getNotMatchingRightPortion(col) + "</span>"
    } else {
      "<span class=\"infoCol\">" + getDisplayString(col) + "</span>"
    }
  }

  /**
   * Returns the display string.
   * @return The display string.
   */
  fun getDisplayString(col: Int): String {
    return displayStrings[col]
  }

  /**
   * Returns the column count.
   * @return The column count.
   */
  fun getColumnCount(): Int {
    return displayStrings.size
  }

  /**
   * Returns the query string.
   * @return the query string.
   */
  protected fun getQueryLowerCase(): String {
    return query!!.toLowerCase()
  }

  /**
   * Returns the display string.
   * @return The display string.
   */
  protected fun getDisplayStringLowerCase(col: Int): String {
    return displayStrings[col].toLowerCase()
  }
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  /**
   * Returns the matching portion of the suggestion.
   * @return The matching portion of the suggestion.
   */
  protected fun getMatchingPortion(col: Int): String {
    return displayStrings[col].substring(getDisplayStringLowerCase(col).indexOf(getQueryLowerCase()),
                                         getDisplayStringLowerCase(col).indexOf(getQueryLowerCase()) + query!!.length)
  }

  /**
   * Returns the non matching left portion of the suggestion.
   * @return The non matching left portion of the suggestion.
   */
  protected fun getNotMatchingLeftPortion(col: Int): String {
    return displayStrings[col].substring(0, getDisplayStringLowerCase(col).indexOf(getQueryLowerCase()))
  }

  /**
   * Returns the non matching right portion of the suggestion.
   * @return The non matching right portion of the suggestion.
   */
  protected fun getNotMatchingRightPortion(col: Int): String {
    return displayStrings[col].substring(getDisplayStringLowerCase(col).indexOf(getQueryLowerCase())
                                                 + query!!.length, displayStrings[col].length)
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /**
   * Sets the query string.
   * @param query The query string.
   */
  var query: String? = null

  /**
   * Sets the suggestion ID.
   * @param id The suggestion ID.
   */
  var id: Int? = null

  /**
   * Sets the display string.
   * @param displayStrings The display string.
   */
  lateinit var displayStrings: Array<String>
}
