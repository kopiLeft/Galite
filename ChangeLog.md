# 1.5.7
## What's Changed
* Fix : Implement ImageHandler in Vaadin Flow to fix fatal error when loading an image list and show images on the list by @sebai-dhia in [#642](https://github.com/kopiLeft/Galite/pull/642)
* Fix abnormal behavior of the list button and menu query in the ProductForm by @sebai-dhia in [#644](https://github.com/kopiLeft/Galite/pull/644)

**Full Changelog**: [1.5.6 ... 1.5.7](https://github.com/kopiLeft/Galite/compare/1.5.6...1.5.7)

# 1.5.6
## What's Changed
* Fix : Allow LEFT JOIN for nullable fields with non nullable fields in Galite forms by @mgrati in [#640](https://github.com/kopiLeft/Galite/pull/640)
* Fix : Fix autocomplete and popup navigation issues using Vaadin Flow ComboBox by @sebai-dhia in [#638](https://github.com/kopiLeft/Galite/pull/638)

**Full Changelog**: [1.5.5 ... 1.5.6](https://github.com/kopiLeft/Galite/compare/1.5.5...1.5.6)

# 1.5.5
## What's Changed
* Feat: Add the possibility to keep empty strings in the generated xml using the Factory generation task by Galite by @yahiaoui97 in [#637](https://github.com/kopiLeft/Galite/pull/637)
* Fix: Display color content in a no detail block by @achraf-dridi in [#630](https://github.com/kopiLeft/Galite/pull/630)
* Fix: Ensure field validated when going from text field to boolean field in Galite forms by @achraf-dridi in [#633](https://github.com/kopiLeft/Galite/pull/630)

**Full Changelog**: [1.5.4 ... 1.5.5](https://github.com/kopiLeft/Galite/compare/1.5.4...1.5.5)

# 1.5.4
## What's Changed
* Feat : Add color picker field to Galite : 
  * Add a new field type "Color" to Galite by @achraf-dridi in [#625](https://github.com/kopiLeft/Galite/pull/625)
  * Convert the value type of the color field to an integer instead of blob by @achraf-dridi in [#631](https://github.com/kopiLeft/Galite/pull/631)
* Fix : Fix the found bugs in the module "Factory Generator" of Galite Utils 
  * Fix generated classes in the event when the xsd contains attributes that are named as one of the hard keywords of Kotlin by @achraf-dridi in [#629](https://github.com/kopiLeft/Galite/pull/629)
  * Avoid creating the schemaorg_apache_xmlbeans package containing copies of the *.xsd files by @yahiaoui97 [#628](https://github.com/kopiLeft/Galite/pull/628)
  * Add toCalendar method in the generated factory classes by @achraf-dridi [#627](https://github.com/kopiLeft/Galite/pull/627)
  * Add NullOrBlank verification on String parameters in the generated factory classes by @achraf-dridi [#627](https://github.com/kopiLeft/Galite/pull/627)
  * Fix generated simple types attributes in the generated factory classes by @achraf-dridi [#626](https://github.com/kopiLeft/Galite/pull/626)

**Full Changelog**: [1.5.3 ... 1.5.4](https://github.com/kopiLeft/Galite/compare/1.5.3...1.5.4)

# 1.5.3
## What's Changed
* Generalize the galite factory generator by @MayssenGharbi in [#623](https://github.com/kopiLeft/Galite/pull/623)
* Add SqlLogger to galite-data / Migration constructor by @mgrati in [#624](https://github.com/kopiLeft/Galite/pull/624)

**Full Changelog**: [1.5.2 ... 1.5.3](https://github.com/kopiLeft/Galite/compare/1.5.2...1.5.3)

# 1.5.2
## What's Changed
* Add factory generator using .xsd files by @MayssenGharbi [#622](https://github.com/kopiLeft/Galite/pull/622)
* Make the option driver no longuer required when calling Migration.kt of galite-data by @sebai-dhia (APPS-0226)

**Full Changelog**: [1.5.1 ... 1.5.2](https://github.com/kopiLeft/Galite/compare/1.5.1...1.5.2)

# 1.5.1
## What's Changed
* Fix fatal error linked to the management of temporal fields in Full Calendar component by @Iyedchaabane [#611](https://github.com/kopiLeft/Galite/pull/611)
* Fix fields of type CodeDomain in pivot table by @Iyedchaabane [#615](https://github.com/kopiLeft/Galite/pull/615)
* Use java.time.LocalDate / java.time.LocalDateTime for all fields of type date / timestamp by @Iyedchaabane [#619](https://github.com/kopiLeft/Galite/pull/619)
* Fix chart implementation by @Iyedchaabane in [#620](https://github.com/kopiLeft/Galite/pull/620)

**Full Changelog**: [1.5.0 ... 1.5.1](https://github.com/kopiLeft/Galite/compare/1.5.0...1.5.1)

# 1.5.0
## What's Changed
* Upgrade Exposed version to 0.42.1 and Kotlin version to 1.9.0 by @mgrati and @achraf-dridi in [#603](https://github.com/kopiLeft/Galite/pull/603)
* Implement waitMin and waitMax params for retrying aborted transactions by @mgrati and @achraf-dridi in [#603](https://github.com/kopiLeft/Galite/pull/603)
* Use HikariCP pool connection for galite-data connections by @mgrati in [#613](https://github.com/kopiLeft/Galite/pull/613)
* Fix : Fix unusual behaviour for applications with multiple connections by @mgrati in [#613](https://github.com/kopiLeft/Galite/pull/613)
* Allow display of multi-line report rows by @MedAzizTousli in [#597](https://github.com/kopiLeft/Galite/pull/597)
* Implement automated testing for pivot table module by @RiadhCherni in [#607](https://github.com/kopiLeft/Galite/pull/607)

**Full Changelog**: [1.4.3 ... 1.5.0](https://github.com/kopiLeft/Galite/compare/1.4.3...1.5.0)

# 1.4.3
## What's Changed
* Fix : Allow creating Galite forms with database tables with a sequence name different from "<TABLE8NAME>ID" @mgrati [#604](https://github.com/kopiLeft/Galite/pull/604)
* Feature: Add pivot table component implementation @RiadhCherni [#606](https://github.com/kopiLeft/Galite/pull/606)

**Full Changelog**: [1.4.2 ... 1.4.3](https://github.com/kopiLeft/Galite/compare/1.4.2...1.4.3)

# 1.4.2
## What's Changed
* Fix: Allow creating Galite forms with database tables without a column named "ID" @mgrati [#605](https://github.com/kopiLeft/Galite/pull/605)

**Full Changelog**: [1.4.1 ... 1.4.2](https://github.com/kopiLeft/Galite/compare/1.4.1...1.4.2)

# 1.4.1
## What's Changed
* Fix: Bug in DATETIME domain type: Change the return type to LocalDateTime for columns of type LocalDateTime when calling retrieveQuery by @achraf-dridi [#602](https://github.com/kopiLeft/Galite/pull/602)

**Full Changelog**: [1.4.0 ... 1.4.1](https://github.com/kopiLeft/Galite/compare/1.4.0...1.4.1)

# 1.4.0
## What's Changed
* Upgrade Vaadin version from 22.0.14 to 23.3.8 by @MedAzizTousli in [#601](https://github.com/kopiLeft/Galite/pull/601)
* Configure field height in dynamic report fields by @MedAzizTousli in [#599](https://github.com/kopiLeft/Galite/pull/599)
* Configure defaultRepetitionAttempts when connecting to database by @achraf-dridi [#595](https://github.com/kopiLeft/Galite/pull/595)

**Full Changelog**: [1.3.8 ... 1.4.0](https://github.com/kopiLeft/Galite/compare/1.3.8...1.4.0)

# 1.3.8
## What's Changed
* Add use of expressions with keyOf function in ListDomain by @yahiaoui97 [APPS-01LB]
* Fix: Add support for java.time.LocalDateTime in addition to java.time.Instant in GaliteDomain timestamp fields by @achraf-dridi [#593](https://github.com/kopiLeft/Galite/pull/593)

**Full Changelog**: [1.3.7 ... 1.3.8](https://github.com/kopiLeft/Galite/compare/1.3.7...1.3.8)

# 1.3.7
## What's Changed
* Fix: Change SQL query logging to depend on traceLevel value by @mgrati [#594](https://github.com/kopiLeft/Galite/pull/594)

**Full Changelog**: [1.3.6 ... 1.3.7](https://github.com/kopiLeft/Galite/compare/1.3.6...1.3.7)

# 1.3.6
## What's Changed
* Fix: Mark default actors defined in org.kopi.galite.visual.dsl.form.Form as non user actor to allow the application to localize these actors by @mgrati [#590](https://github.com/kopiLeft/Galite/pull/590)

**Full Changelog**: [1.3.5 ... 1.3.6](https://github.com/kopiLeft/Galite/compare/1.3.5...1.3.6)

# 1.3.5
## What's Changed
* Remove dependency of ReportSelectionForm to DictionaryForm by @mgrati [APPS-01CJ]
* Add default actors and commands to Galite Form and adjust compatibility with the default Galite Locales : fr_FR, en_GB, de_AT and ar_TN by @mgrati [APPS-01H0]

**Full Changelog**: [1.3.4 ... 1.3.5](https://github.com/kopiLeft/Galite/compare/1.3.4...1.3.5)

# 1.3.4
## What's Changed
* Add support for types BigDecimal and Month in Galite ListDomain by @achraf-dridi [#589](https://github.com/kopiLeft/Galite/pull/589)
* Add ReportConfigurations table to be able to setup dynamic reports configuration by @achraf-dridi [APPS-01F2]
* Remove multi-block assertion when calling VBlock.setMode by @yahiaoui97 [APPS-01KN]

**Full Changelog**: [1.3.3 ... 1.3.4](https://github.com/kopiLeft/Galite/compare/1.3.3...1.3.4)

# 1.3.3
## What's Changed
* Adjust and add the possibility to execute automatically the creation of tables related to the framework in the database. [#588](https://github.com/kopiLeft/Galite/pull/588)
* Add the possibility to define trace level and isolation level when establishing a database connection with Galite. [#588](https://github.com/kopiLeft/Galite/pull/588)
* Add the possibility to get connection info from path conf/config.properties file. [#588](https://github.com/kopiLeft/Galite/pull/588)
* Fix : Enable click on Quit button in MenuTree by @yahiaoui97 [APPS-01EQ]

**Full Changelog**: [1.3.2 ... 1.3.3](https://github.com/kopiLeft/Galite/compare/1.3.2...1.3.3)

# 1.3.2
## What's Changed
* No changes
# 1.3.1
## What's Changed
* Fix: Set RoundingMode parameter when calling BigDecimal setScale function by @mgrati [#587](https://github.com/kopiLeft/Galite/pull/587)
* Add constraints to Galite domains by @hfazai [#559](https://github.com/kopiLeft/Galite/pull/559)
* VaadinUI: workaround to avoid closing editor on application errors by @hfazai
* Fix: Multi block UI : keep the record open when entering an invalid value by @hfazai
* Implement text transformation feature by @hfazai
* Vaadin: Move up to version 22.0.14 by @hfazai
* VaadinUI: set navigation listeners on the notification dialogs by @hfazai
* VaadinUI: Set width to 100% input text fields in aligned blocks by @hfazai
* Fix: VaadinUI : Stay on the active field when validating boolean field value by @hfazai

**Full Changelog**: [1.3.0 ... 1.3.1](https://github.com/kopiLeft/Galite/compare/1.3.0...1.3.1)

# 1.3.0
## What's Changed
* DSL/Model: code clean and refactoring by @hfazai in [#581](https://github.com/kopiLeft/Galite/pull/581)
* Adding PivotTable UI implementation by @hfazai in [#583](https://github.com/kopiLeft/Galite/pull/583)
* Block: remove duplicated maxRowPos, maxColumnPos, displayedFields by @hfazai
* Remove VDefaultActor in favor to DefaultActor by @hfazai
* Change mustfill form field generic type, it shouldn't be nullable by @hfazai
* Adding Block.clear() API by @hfazai
* Externalize galite-data and galite-util modules by @hfazai
* Nullable columns could be assigned to mustfill form fields by @hfazai
* Report: Create a number of fields with one API by @hfazai
* Implement DGridEditorImageField.createConverter() by @hfazai
* Vaadin22 workaround: allow navigation to password field when clicking on TAB by @hfazai
* VaadinUI: Droppable blocks should allow to upload files by @hfazai
* DSL: Adding FormField.isID API by @hfazai
* Field list table should be lazily evaluated by @hfazai

**Full Changelog**: [1.2.0 ... 1.3.0](https://github.com/kopiLeft/Galite/compare/1.2.0...1.3.0)

# 1.2.0
## What's Changed
* DSL/DB: Adding WeekColumnType and MonthColumnType by @hfazai in [#575](https://github.com/kopiLeft/Galite/pull/575)
* Fix: VField.retrieveQuery shouldn't convert value to Week and Month by @hfazai in [#576](https://github.com/kopiLeft/Galite/pull/576)
* DB: Adding Query extension function allowing to get single row or throw custom DB Exception by @hfazai in [#579](https://github.com/kopiLeft/Galite/pull/579)
* Style: Add padding between fields in multi block by @h-haddad in [#582](https://github.com/kopiLeft/Galite/pull/582)
* UI: Avoid refreshing filtered rows in grid block by @hfazai
* DSL: Adding the feature of defining an alias of a form field by @hfazai
* DSL: Adding setPageTitle method to Report by @hfazai
* Implement Pivot Table model using Kotlin Dataframe by @hfazai in [#580](https://github.com/kopiLeft/Galite/pull/580)
* DSL: MustFill field value should be nullable by @hfazai in [#577](https://github.com/kopiLeft/Galite/pull/577)
* VaadinUI: Filter deleted records from multiblock by @hfazai in [#578](https://github.com/kopiLeft/Galite/pull/578)
* VBump: Vaadin 22.0.6 by @h-haddad in [#564](https://github.com/kopiLeft/Galite/pull/564) and [#584](https://github.com/kopiLeft/Galite/pull/584)
* VaadinUI: Auto select is done via autoselect property instead of focus listener by @hfazai
* VBump: Vaadin 22.0.13 by @hfazai

**Full Changelog**: [1.1.0 ... 1.2.0](https://github.com/kopiLeft/Galite/compare/1.1.0...1.2.0)

# 1.1.0
## What's Changed
* DateField Rework: Replace vaadin-date-picker-light with TextField by @hfazai in [#565](https://github.com/kopiLeft/Galite/pull/565)
* DSL: Form, chart and report models are not built lazily anymore. This allows calling DSL APIs in all scopes by @hfazai in [#548](https://github.com/kopiLeft/Galite/pull/548)
* Feature: Add the feature of opening programmatically an URL from a window by @hfazai in [#563](https://github.com/kopiLeft/Galite/pull/563)
* DSL: Adding more APIs to Actor and Command DSL by @hfazai in [#569](https://github.com/kopiLeft/Galite/pull/569)
* Remove Timestamp in favor to java Instant by @hfazai in [#566](https://github.com/kopiLeft/Galite/pull/566)
* VaadinUI/Multiblock: Do not close editor when trying to edit inaccessible records by @hfazai in [#574](https://github.com/kopiLeft/Galite/pull/574)
* Adding test for form and report localization by @h-haddad in [#570](https://github.com/kopiLeft/Galite/pull/570)
* UI/Firefox: Remove space between page menu and form block by @hfazaiby @hfazai in [#571](https://github.com/kopiLeft/Galite/pull/571)
* Adding galite-domain module by @hfazai
* DSL: Add more APIs to FormField, ReportField, Window and Report by @hfazai
* Refactoring of the code publishing releases to maven by @hfazai
* Stop passing DBConnection between forms as Exposed handles Connections by @hfazai
* Cleanup deprecations and fix kdoc comments by @hfazai
* Entering an invalid date should throw VFieldException by @hfazai
* Implement Month and Week operators by @hfazai
* DSL: Adding a method to get report row value at a specific column by @hfazai

**Full Changelog**: [1.0.1 ... 1.1.0](https://github.com/kopiLeft/Galite/compare/1.0.1...1.1.0)

# 1.0.1
## What's Changed
* Adding the feature of setting custom colors to the user interface by @hfazai
* Gradle: Add exposed-jdbc dependency to galite-core by @hfazai
* Code cleaning and removing unused code by @hfazai
* Add ability to change CSS colors via variables from Vapplication by @h-haddad in [#560](https://github.com/kopiLeft/Galite/pull/560)
* Set default align to right for numeric fields in multi block by @h-haddad in [#561](https://github.com/kopiLeft/Galite/pull/561)
* VaadinUI: Use setTimeout to set date field value only after the element is attached by @hfazai in [#562](https://github.com/kopiLeft/Galite/pull/562)

**Full Changelog**: [1.0.0 ... 1.0.1](https://github.com/kopiLeft/Galite/compare/1.0.0...1.0.1)

# 1.0.0
## What's Changed
* Domain: Move min, max properties from Field to Domain by @hfazai
* Remove useless DBContextHandler as Connection is fully handled by Exposed by @hfazai
* DSL: Chart dimension can accept lambda function to format values by @hfazai
* VBump: Exposed 0.37.3 by @hfazai
* VaadinUI: Date fields shouldn't accept invalid values by @hfazai in [#538](https://github.com/kopiLeft/Galite/pull/538)
* Generate a report (pdf, xls, xlsx, csv) containing only visible column by @h-haddad in [#537](https://github.com/kopiLeft/Galite/pull/537)
* Fix incorrect title in the help viewer by @hfazai in [#540](https://github.com/kopiLeft/Galite/pull/540)
* VaadinUI: Delete added spaces to decimal field by @hfazai in [#542](https://github.com/kopiLeft/Galite/pull/542)
* Improvement in style of textarea by @h-haddad in [#544](https://github.com/kopiLeft/Galite/pull/544)
* Change the color and the bottom border of the skipped field by @h-haddad in [#543](https://github.com/kopiLeft/Galite/pull/543)
* Remove Decimal class in favor to BigDecimal by @hfazai in [#541](https://github.com/kopiLeft/Galite/pull/541)
* Associate window elements to localization files based on class name by @hfazai in [#545](https://github.com/kopiLeft/Galite/pull/545)
* Icrement the report height to fit the window size by @h-haddad in [#546](https://github.com/kopiLeft/Galite/pull/546) and [#551](https://github.com/kopiLeft/Galite/pull/551)
* Remove DBContext class in favor to Connection by @hfazai in [#549](https://github.com/kopiLeft/Galite/pull/549)
* Adding test to check the order of execution of triggers by @h-haddad in [#550](https://github.com/kopiLeft/Galite/pull/550)
* Adding test for block, label, actor and menu localization by @h-haddad in [#547](https://github.com/kopiLeft/Galite/pull/547)
* Display icons for actors in Navigation Menu by @h-haddad in [#552](https://github.com/kopiLeft/Galite/pull/552)
* Fix the contents align for Decimal and int fields by @h-haddad in [#557](https://github.com/kopiLeft/Galite/pull/557)
* Improvement in report style when reordering and resizing columns by @h-haddad in [#554](https://github.com/kopiLeft/Galite/pull/554)
* Fix show and hide filter behaviour in multi block by @h-haddad in [#555](https://github.com/kopiLeft/Galite/pull/555)
* Adding a background color for report cell when focus by @h-haddad in [#553](https://github.com/kopiLeft/Galite/pull/553)
* Remove Date type in favor to java LocalDate by @hfazai in [#556](https://github.com/kopiLeft/Galite/pull/556)
* Remove Time type in favor to LocalTime by @hfazai in [#558](https://github.com/kopiLeft/Galite/pull/558)
* Full Calendar: Fix conversion of Date to Timestamp by @hfazai
* Gradle: Remove jvmTarget from options of kotlin compile task by @hfazai

**Full Changelog**: [1.0.0-beta.4 ... 1.0.0](https://github.com/kopiLeft/Galite/compare/1.0.0-beta.4...1.0.0)

# 1.0.0-beta.4
## What's Changed
* Full calendar block should be in insert mode when inserting new entry
* VaadinUI: Feature of changing the foreground and background of a field by @hfazai in [#531](https://github.com/kopiLeft/Galite/pull/531)
* DSL: Supporting key columns in form fields
* Do not loose field's color on blur by @h-haddad in [#532](https://github.com/kopiLeft/Galite/pull/532)
* VaadinUI: Parse time field value to accept many formats like 1000 => 10:00
* DSL: Adding more APIs to report DSL
* Adding shift left and shift right to Decimal by @hfazai in [#533](https://github.com/kopiLeft/Galite/pull/533)
* Enhancement in style of VActorsRootNavigationItem in actors panel by @h-haddad in [#528](https://github.com/kopiLeft/Galite/pull/528)
* Improvement in style of checkbox by @h-haddad in [#535](https://github.com/kopiLeft/Galite/pull/535)
* DSL: Remove ident parameter from Actor declaration and add predefined commands
* DSL: title, help and locale parameters will be passed to Window's constructor and reorder Block constructor parameters by @hfazai in [#534](https://github.com/kopiLeft/Galite/pull/534)

**Full Changelog**: [1.0.0-beta.3 ... 1.0.0-beta.4](https://github.com/kopiLeft/Galite/compare/1.0.0-beta.3...1.0.0-beta.4)

# 1.0.0-beta.3
## What's Changed
* VaadinUI: focus on a field when a form is opened in a dialog by @hfazai in [#522](https://github.com/kopiLeft/Galite/pull/522)
* DSL: simplify DSL API for commands by @hfazai in [#521](https://github.com/kopiLeft/Galite/pull/521)
* DSL: more APIs to manipulate a form block by @hfazai in [commit](https://github.com/kopiLeft/Galite/commit/a628c3b34ba84c64367e7d2f660d545d988e3e3e)
* Fix: field's shortcuts should be fired only when all modifiers are combined by @hfazai in [#523](https://github.com/kopiLeft/Galite/pull/523)
* Full Calendar performance: refresh only changed entries by @hfazai in [commit](https://github.com/kopiLeft/Galite/commit/3e0f07d0196164a693307fef399c9da6cf8dfdf2)
* DSL: API for setting the width of a list description in ListDomain by @hfazai in [commit](https://github.com/kopiLeft/Galite/commit/9a6c91aceb778274f82a6119af0bf600e54b3236)
* DSL: Create report without having to override a method from ReportSelectionForm by @hfazai in [#524](https://github.com/kopiLeft/Galite/pull/524)
* Code clean for grid block: Do not update model many times by @hfazai in [#526](https://github.com/kopiLeft/Galite/pull/526)
* DSL: Add enum class for actor icons by @hfazai in [#527](https://github.com/kopiLeft/Galite/pull/527)

**Full Changelog**: [1.0.0-beta.2 ... 1.0.0-beta.3](https://github.com/kopiLeft/Galite/compare/1.0.0-beta.2...1.0.0-beta.3)

# 1.0.0-beta.2
## What's Changed
* VaadinUI: More precise method to calculate multiblock height by @hfazai in [commit](https://github.com/kopiLeft/Galite/commit/bdb1ddf4ae97a1d8554bd93b8b6c8af4242788e1)
* VaadinUI: Sort list dialog when reordering grid columns by @hfazai in [commit](https://github.com/kopiLeft/Galite/commit/2087563bc555dfbf2631317faf2a637b5db64408)
* Add Unit Test for DSL components by @MedAliMarz in [#497](https://github.com/kopiLeft/Galite/pull/497)
* Add the ability to display and hide the report's columns by @h-haddad in [#516](https://github.com/kopiLeft/Galite/pull/516)
* UITests: Adding tests for block and report by @h-haddad in [#481](https://github.com/kopiLeft/Galite/pull/481)
* VBump: Vaadin 21.0.9 by @hfazai in [#518](https://github.com/kopiLeft/Galite/pull/518)
* VBump: Exposed 0.36.2 by @hfazai in [commit](https://github.com/kopiLeft/Galite/commit/4f27fee07478a63a5cbd3105fa618de31b73f88f)
* Fix: Records of the last day of the week should be fetched in full calendar by @hfazai in [commit](https://github.com/kopiLeft/Galite/commit/85389ba69c1cc91d20561042a9ffad49860a39ea)
* Improvement in Css style by @h-haddad in [#517](https://github.com/kopiLeft/Galite/pull/517)
* FIX: Refresh data after deleting record from full calendar block by @hfazai in [#519](https://github.com/kopiLeft/Galite/pull/519)
* Add UI Tests for Block, Form, Report and List by @MedAliMarz in [#503](https://github.com/kopiLeft/Galite/pull/503)
* Add Unit tests for VForm, VField and VBlock by @MedAliMarz in [#500](https://github.com/kopiLeft/Galite/pull/500)

**Full Changelog**: [1.0.0-beta.1 ... 1.0.0-beta.2](https://github.com/kopiLeft/Galite/compare/1.0.0-beta.1...1.0.0-beta.2)

# 1.0.0-beta.1
## What's Changed
* creation of complete form, report and chart that contains all behaviors by @h-haddad in [#486](https://github.com/kopiLeft/Galite/pull/486)
* VaadinUI : Calculate the list table width based on its content by @hfazai in [#487](https://github.com/kopiLeft/Galite/pull/487)
* DSL: Allow to define a combination of indices on a form field by @hfazai in [#490](https://github.com/kopiLeft/Galite/pull/490)
* VaadinUI: Support sortable form fields by @hfazai in [#492](https://github.com/kopiLeft/Galite/pull/492)
* VaadinUI: Rework the feature of filtering data on list dialog and grid by @hfazai in [#494](https://github.com/kopiLeft/Galite/pull/494)
* VaadinUI: Lazily load layout components when user navigates to a page by @hfazai in [#498](https://github.com/kopiLeft/Galite/pull/498)
* Adding full calendar component by @h-haddad in [#499](https://github.com/kopiLeft/Galite/pull/499)
* Feature: Support droppable blocks by @hfazai in [#502](https://github.com/kopiLeft/Galite/pull/502)
* Unit Tests : adding tests for VDimension, VMeasure, Domain, ExtendedChoiceFormat and getSearchTable by @MedAliMarz in [#495](https://github.com/kopiLeft/Galite/pull/495)
* Adding UITests for all cases existing in the documentations by @h-haddad in [#496](https://github.com/kopiLeft/Galite/pull/496)
* Feature: Support Full calendar by @hfazai in [#504](https://github.com/kopiLeft/Galite/pull/504)
* Feature: Implement sending mail and fix error reporting by @hfazai in [#505](https://github.com/kopiLeft/Galite/pull/505)
* VaadinUI/JS: Handle autofill button visibility from JS by @hfazai in [#507](https://github.com/kopiLeft/Galite/pull/507)
* Fix produced file name, impove file downloading feature and format integer and decimal fields by @h-haddad in [#508](https://github.com/kopiLeft/Galite/pull/508)
* VaadinUI/JS: Handle navigation shortcuts in client side by @hfazai in [#506](https://github.com/kopiLeft/Galite/pull/506)
* Remove column view of form fields and clean code by @hfazai in [#509](https://github.com/kopiLeft/Galite/pull/509)
* Feature: Allow adding and editing full calendar items by @hfazai in [#511](https://github.com/kopiLeft/Galite/pull/511)
* VaadinUI: Parse date and timestamp fields on value change by @hfazai in [#512](https://github.com/kopiLeft/Galite/pull/512)
* Externalize methods of building models from DSL APIs by @hfazai in [#513](https://github.com/kopiLeft/Galite/pull/513)
* Rename FormBlock class to Block
* VaadinUI: download produced file in server without showing a dialog by @hfazai in [#514](https://github.com/kopiLeft/Galite/pull/514)
* Rename FullCalendarBlock class to FullCalendar

**Full Changelog**: [1.0.0-beta...1.0.0-beta.1](https://github.com/kopiLeft/Galite/compare/1.0.0-beta...1.0.0-beta.1)

# 1.0.0-beta
## What's Changed
* VaadinUI : show all rows in list dialog by @hfazai in [#478](https://github.com/kopiLeft/Galite/pull/478)
* Fixing bug in field triggers: only the last field trigger is called and the other ones are not working by @h-haddad in [#470](https://github.com/kopiLeft/Galite/pull/470)
* Support code domains in reports and charts by @hfazai in [#483](https://github.com/kopiLeft/Galite/pull/483)
* VaadinUI: Enable autoselection on focus on text fields by @hfazai in [#484](https://github.com/kopiLeft/Galite/pull/484)
* Feature: Supporting selecting a schema during connection database by @hfazai in [#485](https://github.com/kopiLeft/Galite/pull/485) Added after contributing to Exposed in [JetBrains/Exposed#1367](https://github.com/JetBrains/Exposed/pull/1367)


**Full Changelog**: [1.0.0-alpha...1.0.0-beta](https://github.com/kopiLeft/Galite/compare/1.0.0-alpha...1.0.0-beta)
