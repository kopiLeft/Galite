/*
 * Copyright (c) 2013-2026 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.gradle

import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.exclude

fun ExternalModuleDependency.excludeWebJars() {
  listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
         "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
         "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
    .forEach { group -> exclude(group = group) }
}

/**
 * Returns the latest release name : Latest git tag.
 */
fun getLatestReleaseName(project: Project): String {
  return try {
    listOf("git", "describe", "--tags", "--abbrev=0").runCommand(project.rootDir).trim()
  } catch (_: Exception) {
    project.version.toString()
  }
}

/**
 * Returns the latest release date : Latest git tag commit date.
 */
fun getLatestReleaseDate(project: Project, tag: String): String {
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'").withZone(ZoneOffset.UTC)

  return try {
    // Step 1: get commit date of that tag
    val commitDate = listOf("git", "log", "-1", "--format=%aI", tag).runCommand(project.rootDir)
    // Step 2: parse and format with timezone as "yyyy-MM-dd HH:mm:ss UTC"
    val instant = Instant.parse(commitDate)
    val releaseDate = formatter.format(instant)

    releaseDate
  } catch (_: Exception) {
    formatter.format(Instant.now())
  }
}

/**
 * Runs a command line.
 */
private fun List<String>.runCommand(workingDir: File): String {
  val output = ByteArrayOutputStream()
  val process = ProcessBuilder(this)
    .directory(workingDir)
    .redirectErrorStream(true)
    .start()
  val exitCode = process.waitFor()

  process.inputStream.copyTo(output)

  val result = output.toString().trim()

  if (exitCode != 0) throw RuntimeException("Command failed with exit code $exitCode: $result")

  return result
}
