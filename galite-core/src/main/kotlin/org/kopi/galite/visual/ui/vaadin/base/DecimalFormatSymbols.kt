package org.kopi.galite.visual.ui.vaadin.base

class DecimalFormatSymbols(var currencySymbol: String?,
  var internationalCurrencySymbol: String?,
  var decimalSeparator: Char,
  var digit: Char,
  var exponentSeparator: String?,
  var groupingSeparator: Char,
  var infinity: String?,
  var minusSign: Char,
  var monetaryDecimalSeparator: Char,
  nan: String?,
  var patternSeparator: Char,
  var percent: Char,
  var perMill: Char,
  var zeroDigit: Char
) {

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun equals(other: Any?): Boolean {
    if (other !is DecimalFormatSymbols) {
      return false
    }

    val dfs: DecimalFormatSymbols = other
    return (nullEquals(currencySymbol, dfs.currencySymbol)
            && nullEquals(internationalCurrencySymbol, dfs.internationalCurrencySymbol)
            && decimalSeparator == dfs.decimalSeparator
            && digit == dfs.digit
            && nullEquals(exponentSeparator, dfs.exponentSeparator)
            && groupingSeparator == dfs.groupingSeparator
            && nullEquals(infinity, dfs.infinity)
            && minusSign == dfs.minusSign
            && monetaryDecimalSeparator == dfs.monetaryDecimalSeparator
            && nullEquals(naN, dfs.naN)
            && patternSeparator == dfs.patternSeparator
            && percent == dfs.percent
            && perMill == dfs.perMill && zeroDigit == dfs.zeroDigit)
  }

  private fun nullEquals(obj1: Any?, obj2: Any?): Boolean {
    return if (obj1 == null) obj2 == null else obj1 == obj2
  }

  private fun nullHash(obj: Any?): Int {
    return obj?.hashCode() ?: 0
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------

  override fun hashCode(): Int {
    return nullHash(currencySymbol) * 3 + nullHash(internationalCurrencySymbol) * 5 + decimalSeparator.toInt() * 7 + digit.toInt() * 11
    + nullHash(exponentSeparator) * 13
    + groupingSeparator.toInt() * 17
    + nullHash(infinity) * 19
    + minusSign.toInt() * 23
    + monetaryDecimalSeparator.toInt() * 29
    + nullHash(naN) * 31
    + patternSeparator.toInt() * 37 + percent.toInt() * 41
    + perMill.toInt() * 43
    + zeroDigit.toInt() * 53
  }


  var naN: String? = nan

  companion object {

    /**
     * Returns the decimal symbols instance for a given locale.
     * @param locale The locale string value.
     * @return The deciaml format symbols instance.
     */
    fun get(locale: String) = if (cache.containsKey(locale)) {
      cache[locale]
    } else {
      DEFAULT
    }

    val FR = DecimalFormatSymbols( "€",
                                  "EUR",
                                  ',',
                                  '#',
                                  "E",
                                  ' ',
                                  "∞",
                                  '-',
                                  ',',
                                  "�",
                                  ';',
                                  '%',
                                  '‰',
                                  '0')

    val AT = DecimalFormatSymbols( "€",
                                  "EUR",
                                  ',',
                                  '#',
                                  "E",
                                  '.',
                                  "∞",
                                  '-',
                                  ',',
                                  "�",
                                  ';',
                                  '%',
                                  '‰',
                                  '0')

    val GB = DecimalFormatSymbols( "£",
                                  "GBP",
                                  '.',
                                  '#',
                                  "E",
                                  ',',
                                  "∞",
                                  '-',
                                  '.',
                                  "�",
                                  ';',
                                  '%',
                                  '‰',
                                  '0')

    val TN = DecimalFormatSymbols( "د.ت.",
                                  "TND",
                                  '.',
                                  '#',
                                  "E",
                                  ',',
                                  "∞",
                                  '-',
                                  '.',
                                  "�",
                                  ';',
                                  '%',
                                  '‰',
                                  '0')

    val DEFAULT = AT
    val cache = mutableMapOf("de_AT" to AT, "fr_FR" to FR, "en_GB" to GB, "ar_TN" to TN)
  }
}
