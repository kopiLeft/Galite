<img src="docs/logo_galite.png" alt="Galite" width="315" />

# Galite
Galite Framework

![license](http://img.shields.io/badge/license-LGPL_v2.1-lightgrey.svg?style=flat)
![build](https://github.com/kopiLeft/Galite/workflows/Build/badge.svg)

Welcome to **Galite**, the framework of [kopiLeft](https://github.com/kopiLeft) with an expressive elegant syntax based on Kotlin DSL to create great applications.

## Purpose
Since development must be as creative and enjoyable as doing common tasks, Galite provides all the needed tools for developers to take the pain out of designing and implementing applications.

Galite allows users to build business applications with forms, reports and charts using a simple, creative and specific syntax that will allow you to put all the verbosity aside and concentrate on the business logic with its simple domain specific language.

## Advantages
Galite offers many advantages:
* Kotlin-based DSL: using Type-Safe builders for building the application components through the simple declaration of the forms, reports or charts in the DSL.
* Database-backed application: it provides you with the feature of connecting forms with databases and getting queries from different database dialects. You declare the database tables using [Exposed](https://github.com/JetBrains/Exposed) framework.
* Strongly typed fields: you have to declare the data types of fields explicitly, which allows to check the type of data assigned to a field at compile-time.

## Configuration
To define your application configurations (logo, locales,..etc) you need to implement the `VApplication` class.

Exemple:
````KOTLIN
class MyApp : VApplication(Registry(domain = "GALITE", parent = null)) {

  override val sologanImage get() = "galite-slogan.png"
  
  override val logoImage get() = "galite-logo.png"
  
  override val logoHref get() = "http://[mywebsite]"
  
  override val alternateLocale get() = Locale("de", "AT")
  
  override val supportedLocales
    get() =
      arrayOf(Locale.FRANCE,
              Locale("de", "AT"),
              Locale("ar", "TN"))

  override fun login(
    database: String,
    driver: String,
    username: String,
    password: String,
    schema: String?
  ): DBContext? {
    return try {
      DBContext().apply {
        this.defaultConnection = this.createConnection(driver, database, username, password, true, schema)
      }
    } catch (exception: Throwable) {
      null
    }
  }
  
  init {
    ApplicationConfiguration.setConfiguration(
      object : ApplicationConfiguration() {
        override val version get(): String = "1.0"
        override val applicationName get(): String = "MyApp"
        override val informationText get(): String = "info"
        override val logFile get(): String = "log.txt"
        override val debugMailRecipient get(): String = "mail@adress"
        override fun getSMTPServer(): String = "smtp.server"
        override val faxServer get(): String = "fax.server"
        override val dictionaryServer get(): String = "c:/aspell"
        override fun mailErrors(): Boolean = false
        override fun logErrors(): Boolean = true
        override fun debugMessageInTransaction(): Boolean = true
        
        /** And many other configurations. See ApplicationConfiguration.kt */
      }
    )
  }
}
````

When starting the application, the login page is displayed. It is provided by Galite by default and you don't need to create it.
![login page](docs/login-page.png)

## Form
You should be able to build custom forms fast and efficiently.

Here is an example:
````KOTLIN
class ClientForm : Form() {
  
  override val locale = Locale.UK
  
  override val title = "form-title"

  val action = menu("Action")

  val helpForm = actor(
    ident = "helpForm",
    menu = action,
    label = "Help",
    help = " Help"
  ) {
    key = Key.F1   // key is optional here
    icon = "help"  // icon is optional here
  }
 
  val helpCmd = command(item = helpForm) {
    action = {
      showHelp()
    }
  }

  val block = insertBlock(Clients()) 
}

class Clients : FormBlock(1, 1, "Clients") {
  val u = table(Client)

  val idClt = visit(domain = Domain<Int>(30), position = at(1, 1..2)) {
    label = "ID"
    help = "The client id"
    columns(u.idClt)
  }
  val fstnameClt = visit(domain = Domain<String>(25), position = at(2, 1)) {
    label = "First Name"
    help = "The client first name"
    columns(u.firstNameClt)
  }
  val nameClt = visit(domain = Domain<String>(25), position = at(2, 2)) {
    label = "Last name"
    help = "The client last name"
    columns(u.lastNameClt)
  }
  val ageClt = visit(domain = Domain<Int>(3), position = at(3, 1)) {
    label = "Age"
    help = "The client age"
    columns(u.ageClt)
  }
  val addressClt = visit(domain = Domain<String>(20), position = at(3, 2)) {
    label = "Address"
    help = "The client address"
    columns(u.addressClt)
  }
  val countryClt = visit(domain = Domain<String>(12), position = at(4, 1)) {
    label = "Country"
    help = "The client country"
    columns(u.countryClt)
  }
  val cityClt = visit(domain = Domain<String>(12), position = at(4, 2)) {
    label = "City"
    help = "The client city"
    columns(u.cityClt)
  }
  val zipCodeClt = visit(domain = Domain<Int>(12), position = follow(cityClt)) {
    label = "Zip code"
    help = "The client zip code"
    columns(u.zipCodeClt)
  }
  val active = visit(domain = Domain<Boolean>(), position = at(5, 1)) {
    label = "City"
    help = "The client city"
  }
}
````
## Reports
The report consists of an instance that gets consistently injected with the data that is inserted by the user. The required data is iteratively fetched from the table to then be formatted within a customizable report.
Reports can be grouped together according to certain attributes (collapse, computations)
The user can generate a file from a report. The file can be in one of the following formats: csv, pdf or excel.
````KOTLIN
class ClientR : Report() {
  override val locale = Locale.UK

  override val title = "report-title"

  val action = menu("Action")

  val csv = actor(
          ident = "CSV",
          menu = action,
          label = "CSV",
          help = "CSV Format",
  ) {
    key = Key.F8          // key is optional here
    icon = "exportCsv"  // icon is optional here
  }

  val pdf = actor(
          ident = "PDF",
          menu = action,
          label = "PDF",
          help = "PDF Format",
  ) {
    key = Key.F9          // key is optional here
    icon = "exportPdf"  // icon is optional here
  }
  
  val cmdCSV = command(item = csv) {
    action = {
      model.export(VReport.TYP_CSV)
    }
  }

  val cmdPDF = command(item = pdf) {
    action = {
      model.export(VReport.TYP_PDF)
    }
  }

  val firstName = field(Domain<String>(25)) {
    label = "First Name"
    help = "The client first name"
    align = FieldAlignment.LEFT
    group = ageClt
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val lastName = field(Domain<String>(25)) {
    label = "Last Name"
    help = "The client last name"
    align = FieldAlignment.LEFT
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val addressClt = field(Domain<String>(50)) {
    label = "Address"
    help = "The client address"
    align = FieldAlignment.LEFT
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val ageClt = field(Domain<Int>(2)) {
    label = "Age"
    help = "The client age"
    align = FieldAlignment.LEFT
  }

  val countryClt = field(Domain<String>(50)) {
    label = "City"
    help = "The client country"
    align = FieldAlignment.LEFT
  }

  val cityClt = field(Domain<String>(50)) {
    label = "City"
    help = "The client city"
    align = FieldAlignment.LEFT
  }

  val zipCodeClt = field(Domain<Int>(2)) {
    label = "Zip code"
    help = "The client zip code"
    align = FieldAlignment.LEFT
  }

  val clients = Client.selectAll()

  init {
    transaction {
      clients.forEach { result ->
        add {
          this[firstName] = result[Client.firstNameClt]
          this[lastName] = result[Client.lastNameClt]
          this[addressClt] = result[Client.addressClt]
          this[ageClt] = result[Client.ageClt]
          this[countryClt] = result[Client.countryClt]
          this[cityClt] = result[Client.cityClt]
          this[zipCodeClt] = result[Client.zipCodeClt]
        }
      }
    }
  }
}
````

## Charts
If further analysis is needed, the user can generate charts to visualize data and draw conclusions from the observed patterns.
There exist different types of charts such as pie, bar, column, line and area charts.
Charts are essential to the decision-making process and for speculation.
The user will be able to select the required data along with the chart’s dimensions and measures.

````KOTLIN
object ChartSample: Chart() {
  override val locale = Locale.UK
  override val title = "area/population per city"
  override val help = "This chart presents the area/population per city"

  val action = menu("Action")

  val area = measure(Domain<Decimal?>(width = 10, scale = 5)) {
    label = "area (ha)"

    color {
      VColor.GREEN
    }
  }

  val population = measure(Domain<Int?>(10)) {
    label = "population"
  }

  val city = dimension(Domain<String>(10)) {
    label = "dimension"

    format {
      object : VColumnFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val type = trigger(CHARTTYPE) {
    VChartType.BAR
  }

  init {
    city.add("Tunis") {
      this[area] = Decimal("34600")
      this[population] = 1056247
    }

    city.add("Kasserine") {
      this[area] = Decimal("806600")
      this[population] = 439243
    }

    city.add("Bizerte") {
      this[area] = Decimal("568219")
      this[population] = 368500
    }
  }
}
````

## Contributing
All contributions are welcome.

If the idea excites you and you want to add a new patch or feature, feel free to submit a pull request.

If you have any questions, you can create a question issue.

## License

Galite framework is licensed under the LGPLv2.1. See [COPYING](COPYING)
