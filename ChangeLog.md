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
