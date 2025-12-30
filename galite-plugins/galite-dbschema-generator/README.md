# galite-dbschema-generator Gradle Plugin

## Overview

The `galite-dbschema-generator` Gradle plugin is destined for Kotlin projects using JetBrains Exposed, in which the use of multiple database schemas is required. This plugin automates the creation of DBSchema.kt class, that copies already existing table declarations while applying a specific database schema name. The plugin dynamically compiles and integrates generated schema classes into the main source set, ensuring compatibility with existing application logic.

## Features

- **Automatic schema generation**: Generates `org.jetbrains.exposed.sql.Table` object definitions based on predefined table declarations.
- **Integration with Gradle build lifecycle**: Ensures that generated classes are compiled and available to the main source set.
- **Custom source set management**: Introduces a `generated` source set for schema classes.
- **Flexible cleanup operations**: Provides a `clean` extension function for managing generated files.

## Installation

To use `galite-dbschema-generator` in your Gradle project, add the following to your `build.gradle.kts`:

```kotlin
plugins {
    id("org.kopi.galite-dbschema-generator") version "1.5.12"
}
```

```kotlin
dependencies {
  implementation("org.kopi", "galite-dbschema-generator", "1.5.12")
}
```

Alternatively, apply the plugin in `settings.gradle.kts`:

```kotlin
pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
      url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
  }
}
```

## Configuration

By default, the plugin:

- Registers a `generated` source set.
- Adds dependencies from the `main` source set to `generated`.
- Ensures task `generateDBSchemas` runs before `compileKotlin` task.
- The generated classes are added in `./src/generated/kotlin`

To configure the `generateDBSchemas` task, we need to configure the project extension `dbSchemaGenerator` with the following information :
- **packageName**: The package name of the existing table declarations. This package should be a priorly compiled package in the project's `compileClasspath`.
- **schemaName**: The custom database schema name to be added to the table declarations.
- **subFolder**: A custom sub-folder name to be added to the original table declarations package for the generated class. If not specified, the default sub-folder name is "generated".

```kotlin
tasks {
  generateDBSchemas {
    doFirst {
      dbSchemaGenerator.data.set(listOf(
        Schema(packageName = "com.progmag.galite.dbschema", schemaName = "public"),                      // The generated class will be in the package "com.progmag.galite.dbschema.generated"
        Schema(packageName = "com.progmag.galite.dbschema", schemaName = "custom", subFolder = "custom") // The generated class will be in the package "com.progmag.galite.dbschema.custom"
      ))
    }
  }
}
```

## Usage

### Running the Schema Generation Task

To generate schema classes, run:

```sh
./gradlew generateDBSchemas
```

The generated class will have the following path:

```kotlin
"${packageName.replace(".", File.separator)}${File.separator}$subFolder${File.separator}DBSchema${schema.uppercase()}.kt"
```


### Compiling and Including Generated Classes

The generated classes are automatically compiled and included in the build.
