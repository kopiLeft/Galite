/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.kopi.galite.visual.ui.vaadin.base

/**
 * Localized messages. Each message has it own unique key.
 */
object LocalizedMessages {
  //---------------------------------------------------
  // LOCALIZED MESSAGES
  //---------------------------------------------------
  /**
   * Returns the localized value of the given key.
   * @param locale The value locale.
   * @param key The value key.
   * @return The localized value of the given key.
   */
  fun getMessage(locale: String, key: String): String {
    return format(messages[locale]!![key])
  }

  /**
   * Returns the localized value of the given key.
   * @param locale The value locale.
   * @param key The value key.
   * @param params The message parameters.
   * @return The localized value of the given key.
   */
  fun getMessage(locale: String, key: String, vararg params: Any): String {
    return format(messages[locale]!![key], *params)
  }

  /**
   * Properties initialization.
   */
  private fun initProperties() {
    messages["fr_FR"] = frenchMessages
    messages["en_GB"] = englishMessages
    messages["de_AT"] = germanMessages
    messages["ar_TN"] = arabicMessages
  }

  /**
   * Messages initialization.
   */
  private val frenchMessages: Map<String, String>
    get() = mapOf(
            "00001" to "Erreur de saisie: Aucune valeur appropriée.",
            "00002" to "Erreur de saisie: Valeurs multiples.",
            "00003" to "Erreur de saisie: Ce n''est pas une date valide.",
            "00004" to "Erreur de saisie: Ce n''est pas un entier.",
            "00005" to "Erreur de saisie: Ce n''est pas un mois valide.",
            "00006" to "Erreur de saisie: Ce nombre n''est pas valide.",
            "00007" to "Erreur de saisie: Ce n''est pas une heure valide.",
            "00008" to "Erreur de saisie: Ce n''est pas une semaine valide.",
            "00009" to "Erreur de saisie: Ce nombre est trop grand (max {0}).",
            "00010" to "Erreur de saisie: Ce nombre est trop grand.",
            "00011" to "Erreur de saisie: Trop de chiffres après la virgule (max. {0}).",
            "00012" to "Ce nombre est trop petit (min {0}).",
            "00013" to "Erreur de saisie: texte trop long.",
            "00015" to "Pas (plus) de données.",
            "00016" to "Aucune valeur appropriée dans {0}.",
            "00017" to "La valeur de l''enregistrement a changé.",
            "00018" to "L''enregistrement a été éffacé.",
            "00019" to "Cet enregistrement ne peut pas etre effacé.",
            "00022" to "Aucune valeur appropriée trouvée.",
            "00023" to "Ce champ doit être rempli.",
            "00024" to "Cette page n'est pas accessible.",
            "00025" to "Commande non autorisée."
    )

  /**
   * Messages initialization.
   */
  private val englishMessages: Map<String, String>
    get() = mapOf(
            "00001" to "Data entry error: No matching value.",
            "00002" to "Data entry error: More than one matching value.",
            "00003" to "Data entry error: This is not a valid date value.",
            "00004" to "Data entry error: This is not an integer.",
            "00005" to "Data entry error: This is not a valid month value.",
            "00006" to "Data entry error: This in not a number.",
            "00007" to "Data entry error: This is not a valid time value.",
            "00008" to "Data entry error: This is not a valid week value.",
            "00009" to "Data entry error: This number is to big (max {0}).",
            "00010" to "Data entry error: This number is to big",
            "00011" to "Data entry error: Too many digit after comma (max. {0}).",
            "00012" to "Data entry error: This number is to small (min. {0}).",
            "00013" to "Data entry error: This text is too long.",
            "00015" to "No (more) data.",
            "00016" to "No matching value in {0}",
            "00017" to "The value of the record has changed.",
            "00018" to "The record was deleted.",
            "00019" to "This record can''t be deleted.",
            "00022" to "No matching value found.",
            "00023" to "This field must be filled.",
            "00024" to "This page is not accessible.",
            "00025" to "Command not accessible."
    )

  /**
   * Properties initialization.
   */
  private val germanMessages: Map<String, String>
    get() = mapOf(
            "00001" to "Eingabefehler: kein entsprechender Wert.",
            "00002" to "Eingabefehler: Eingabe nicht eindeutig.",
            "00003" to "Eingabefehler: Eingabe ist kein gültiges Datum.",
            "00004" to "Eingabefehler: Eingabe ist nicht ganzzahlig.",
            "00005" to "Eingabefehler: Eingabe ist kein gültiges Monat.",
            "00006" to "Eingabefehler: Formatfehler bei Eingabe.",
            "00007" to "Eingabefehler: Eingabe ist keine gültige Uhrzeit.",
            "00008" to "Eingabefehler: Eingabe ist keine gültige Woche.",
            "00009" to "Eingabefehler: Wert ist zu groß (max {0}).",
            "00010" to "Eingabefehler: Wert ist zu groß",
            "00011" to "Eingabefehler: zu viele Nachkommastellen (max {0}).",
            "00012" to "Eingabefehler: Wert ist zu klein (min {0}).",
            "00013" to "Eingabefehler: Text ist zu lang.",
            "00015" to "Keine (weiteren) Datensätze.",
            "00016" to "Kein entsprechender Eintrag in {0}",
            "00017" to "Datensatz wurde in der Zwischenzeit verändert.",
            "00018" to "Datensatz wurde in der Zwischenzeit gelöscht.",
            "00019" to "Dieser Datensatz kann nicht gelöscht werden.",
            "00022" to "Kein entsprechender Eintrag gefunden.",
            "00023" to "Dieses Feld muß gefüllt werden.",
            "00024" to "Auf diese Seite kann nicht zugegriffen werden.",
            "00025" to "Kommando nicht erlaubt."
    )

  /**
   * Properties initialization.
   */
  private val arabicMessages: Map<String, String>
    get() = mapOf(
            "00001" to "خطأ تسجيل : لا توجد أية قيمة مناسبة",
            "00002" to "خطأ تسجيل : القيم متعددة",
            "00003" to "خطأ تسجيل : هذا التاريخ غير صحيح",
            "00004" to "خطأ تسجيل : انه ليس بعدد صحيح",
            "00005" to "خطأ تسجيل : هذا الشهر غير صحيح",
            "00006" to "خطأ تسجيل :هذا العددغير صحيح",
            "00007" to "خطأ تسجيل :هذه الساعة غير صحيحة",
            "00008" to "خطأ تسجبل : هذا الاأسبوع غير صحيح",
            "00009" to "خطأ تسجيل : هذا  الرقم كبير جدا (الأقصى{0})",
            "00010" to "خطأ تسجيل : هذا االرقم كبير جدا.",
            "00011" to "خطأ تسجيل :الأرقام كثيرة بعد الفاصلة(الأقصى{0})",
            "00012" to "هذا العدد صغير جدا(الأدنى {0})",
            "00013" to "خطأ تسجيل : النص طويل جدا",
            "00015" to "لا يوجد (أكثر) بيانات",
            "00016" to "لا توجد القيمة مناسبة في {0}.",
            "00017" to "قيمة التسجيل تغيرت",
            "00018" to "وقع شطب  التسجيل.",
            "00019" to "لا يمكن  شطب  هذا التسجيل",
            "00022" to "لا توجد أية قيمة مناسبة",
            "00023" to "يجب ملء هذا الموقع.",
            "00024" to "لا يمكن الدخول الى هذه الصفحة",
            "00025" to "الطلبية  غير مسموح بها."
    )

  /**
   * A replacement for java.text.MessageFormat.format().
   * @param format The text to be formatted.
   * @param args The format parameters.
   * @return The formatted string.
   */
  private fun format(format: String?, vararg args: Any): String {
    val sb = StringBuilder()
    var cur = 0
    val len = format!!.length
    while (cur < len) {
      val fi = format.indexOf('{', cur)
      cur = if (fi != -1) {
        sb.append(format.substring(cur, fi))
        val si = format.indexOf('}', fi)
        if (si != -1) {
          val nStr = format.substring(fi + 1, si)
          val i = nStr.toInt()
          sb.append(args[i])
          si + 1
        } else {
          sb.append(format.substring(fi))
          break
        }
      } else {
        sb.append(format.substring(cur, len))
        break
      }
    }
    return sb.toString()
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var messages = mutableMapOf<String, Map<String, String>>()

  init {
    initProperties()
  }
}
