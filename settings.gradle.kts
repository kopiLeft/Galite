/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

rootProject.name = "galite"
include("galite-core")
include("galite-data")
include("galite-domain")
include("galite-localizer")
include("galite-util")
include("galite-testing")
include("galite-tests")
include("galite-demo:galite-vaadin")
include("galite-demo:galite-vaadin-spring")
include("Plugins")
include("plugins")
include("galite-plugins")
include("galite-plugins:src:main")
findProject(":galite-plugins:src:main")?.name = "main"
include("galite-plugins")
