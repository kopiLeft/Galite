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
 * Localized properties.
 */
object LocalizedProperties {
  //---------------------------------------------------
  // LOCALIZED STRINGS
  //---------------------------------------------------
  /**
   * Returns the localized value of the given key.
   * @param locale The value locale.
   * @param key The value key.
   * @return The localized value of the given key.
   */
  fun getString(locale: String?, key: String): String? {
    if (locale == null) {
      return ""
    }
    if (properties == null) {
      properties = HashMap()
    }
    if (properties!![locale] == null) {
      initProperties()
    }
    return properties!![locale]!![key]
  }

  /**
   * Properties initialization.
   */
  private fun initProperties() {
    properties!!["fr_FR"] = frenchProperties
    properties!!["en_GB"] = englishProperties
    properties!!["de_AT"] = germanProperties
    properties!!["ar_TN"] = arabicProperties
  }

  /**
   * Properties initialization.
   */
  private val frenchProperties: HashMap<String, String>
    get() {
      val properties: HashMap<String, String> = HashMap()
      properties["OK"] = "Oui"
      properties["CLOSE"] = "Fermer"
      properties["CANCEL"] = "Annuler"
      properties["NO"] = "Non"
      properties["position-number"] = "Numéro de position"
      properties["TO"] = "à"
      properties["welcomeText"] = "Bienvenue"
      properties["downloadLabel"] = "Télécharger"
      properties["downloadText"] = "Le fichier est généré pour"
      properties["windowsText"] = "Changer de fenêtre"
      properties["informationText"] = "Veuillez saisir votre nom d'utilisateur et votre mot de passe."
      properties["usernameLabel"] = "Nom d'utilisateur:"
      properties["passwordLabel"] = "Mot de passe:"
      properties["languageLabel"] = "Langue:"
      properties["loginText"] = "Connexion"
      properties["logoutText"] = "Déconnexion"
      properties["admin"] = "Admin"
      properties["support"] = "Support"
      properties["help"] = "Aide"
      properties["about"] = " A propos"
      properties["BROWSE"] = "Parcourir"
      properties["UPLOAD"] = "Télécharger"
      properties["UPTITLE"] = "Choisir un fichier"
      properties["UPHELP"] = "Cliquez sur Parcourir pour choisir un fichier de votre ordinateur"
      properties["Error"] = "Erreur"
      properties["Warning"] = "Attention"
      properties["Notice"] = "Information"
      properties["Question"] = "Question"
      properties["showErrorDetails"] = "Afficher les détails"
      // modifiers
      properties["alt"] = "Alt"
      properties["control"] = "Ctrl"
      properties["meta"] = "Méta"
      properties["shift"] = "Maj"
      // key codes
      properties["enter"] = "Entrer"
      properties["pgdn"] = "Page suivante"
      properties["pgup"] = "Page précédente"
      properties["home"] = "Origine"
      properties["end"] = "Fin"
      properties["escape"] = "Echappe"
      // actors menu tooltip
      properties["actorsMenuHelp"] = "Affiche le menu associé à ce formulaire"
      return properties
    }

  /**
   * Properties initialization.
   */
  private val englishProperties: HashMap<String, String>
    get() {
      val properties: HashMap<String, String> = HashMap()
      properties["OK"] = "Yes"
      properties["CLOSE"] = "Close"
      properties["CANCEL"] = "Cancel"
      properties["NO"] = "No"
      properties["position-number"] = "Position number"
      properties["TO"] = "to"
      properties["welcomeText"] = "Welcome to"
      properties["downloadText"] = "File is generated to"
      properties["downloadLabel"] = "Download"
      properties["windowsText"] = "Switch window"
      properties["informationText"] = "Please enter your user name and password."
      properties["usernameLabel"] = "User Name:"
      properties["passwordLabel"] = "Password:"
      properties["languageLabel"] = "Language:"
      properties["loginText"] = "Log In"
      properties["logoutText"] = "Log Out"
      properties["admin"] = "Admin"
      properties["support"] = "Support"
      properties["help"] = "Help"
      properties["about"] = " About"
      properties["BROWSE"] = "browse"
      properties["UPLOAD"] = "Upload"
      properties["UPTITLE"] = "Choose a file"
      properties["UPHELP"] = "Click Browse to choose a file from your computer"
      properties["Error"] = "Error"
      properties["Warning"] = "Warning"
      properties["Notice"] = "Notice"
      properties["Question"] = "Question"
      properties["showErrorDetails"] = "Show details"
      // modifiers
      properties["alt"] = "Alt"
      properties["control"] = "Ctrl"
      properties["meta"] = "Meta"
      properties["shift"] = "Shift"
      // key codes
      properties["enter"] = "Enter"
      properties["pgdn"] = "Page down"
      properties["pgup"] = "Page up"
      properties["home"] = "Home"
      properties["end"] = "End"
      properties["escape"] = "Escape"
      // actors menu tooltip
      properties["actorsMenuHelp"] = "Displays the menu associated with this form"
      return properties
    }

  /**
   * Properties initialization.
   */
  private val germanProperties: HashMap<String, String>
    get() {
      val properties = HashMap<String, String>()
      properties["OK"] = "Ja"
      properties["CLOSE"] = "Schließen"
      properties["CANCEL"] = "Abbrechen"
      properties["NO"] = "Nein"
      properties["position-number"] = "Positionszahl"
      properties["TO"] = "von"
      properties["welcomeText"] = "Willkommen"
      properties["downloadText"] = "Datei wird generiert zu"
      properties["downloadLabel"] = "Herunterladen"
      properties["windowsText"] = "Fenster wechseln"
      properties["informationText"] = "Bitte geben Sie Ihren Benutzer und Ihr kennwort ein."
      properties["usernameLabel"] = "Benutzer:"
      properties["passwordLabel"] = "Kennwort:"
      properties["languageLabel"] = "Sprache:"
      properties["loginText"] = "Einloggen"
      properties["logoutText"] = "Austragen"
      properties["admin"] = "Verwaltung"
      properties["support"] = "Unterstützung"
      properties["help"] = "Hilfe"
      properties["about"] = " Über"
      properties["BROWSE"] = "Blättern"
      properties["UPLOAD"] = "Hochladen"
      properties["UPTITLE"] = "Datei auswählen"
      properties["UPHELP"] = "Klicken Sie auf Durchsuchen, um eine Datei von Ihrem Computer zu wählen"
      properties["Error"] = "Fehler"
      properties["Warning"] = "Warnung"
      properties["Notice"] = "Achtung"
      properties["Question"] = "Frage"
      properties["showErrorDetails"] = "Zeige details"
      // modifiers
      properties["alt"] = "Alt"
      properties["control"] = "Strg"
      properties["meta"] = "Meta"
      properties["shift"] = "Umschalt"
      // key codes
      properties["enter"] = "Eingabe"
      properties["pgdn"] = "Bild ab"
      properties["pgup"] = "Bild auf"
      properties["home"] = "Pos 1"
      properties["end"] = "Ende"
      properties["escape"] = "ESC"
      // actors menu tooltip
      properties["actorsMenuHelp"] = "Zeigt das Menü mit diesem Formular verknüpft"
      return properties
    }

  /**
   * Properties initialization.
   */
  private val arabicProperties: HashMap<String, String>
    get() {
      val properties: HashMap<String, String> = HashMap()
      properties["OK"] = "نعم"
      properties["CLOSE"] = "غلق"
      properties["CANCEL"] = "الغاء"
      properties["NO"] = "لا"
      properties["position-number"] = "رقم الموضع"
      properties["TO"] = "إلى"
      properties["welcomeText"] = "مرحباً"
      properties["downloadText"] = "تم إنشاء الملف ل"
      properties["downloadLabel"] = "تحميل"
      properties["windowsText"] = "تبديل نافذة"
      properties["informationText"] = "الرجاء إدخال إسم المستخدم وكلمة السر."
      properties["usernameLabel"] = "إسم المستخدم:"
      properties["passwordLabel"] = "كلمة السر:"
      properties["languageLabel"] = "الغة:"
      properties["loginText"] = "تسجيل الدخول"
      properties["logoutText"] = "تسجيل الخروج"
      properties["admin"] = "الإدارة"
      properties["support"] = "دعم"
      properties["help"] = "مساعدة"
      properties["about"] = " حول"
      properties["BROWSE"] = "تصفح"
      properties["UPLOAD"] = "تحميل"
      properties["UPTITLE"] = " اختيار ملف"
      properties["UPHELP"] = "انقر على تصفح لاختيار ملف من جهاز الكمبيوتر الخاص بك"
      properties["Error"] = "خطأ"
      properties["Warning"] = "انتباه"
      properties["Notice"] = "معلومة"
      properties["Question"] = "سؤال"
      properties["showErrorDetails"] = "عرض التفاصيل"
      //!!! not translated
      // modifiers
      properties["alt"] = "Alt"
      properties["control"] = "Ctrl"
      properties["meta"] = "Meta"
      properties["shift"] = "Shift"
      // key codes
      properties["enter"] = "Enter"
      properties["pgdn"] = "Page down"
      properties["pgup"] = "Page up"
      properties["home"] = "Home"
      properties["end"] = "End"
      properties["escape"] = "Escape"
      // actors menu tooltip
      properties["actorsMenuHelp"] = "يعرض القائمة المقترنة مع هذه النافذة"
      return properties
    }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var properties: HashMap<String, HashMap<String, String>?>? = null
}
