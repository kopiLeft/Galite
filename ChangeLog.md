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
