/*
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: FieldStates.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.base;

public interface FieldStates {
  /* bit 0-2: availability */
  int   UNKOWN      = 0;
  int   HIDDEN      = 1;
  int   SKIPPED     = 2; 
  int   VISIT       = 3;
  int   MUSTFILL    = 4; 

  int   FLD_MASK    = 7;

  /* bit 3: focused */
  int   FOCUSED     = 8;

  /* bit 4: in chart */
  int   CHART       = 16;

  /* bit 5: mouse rollover */
  int   ROLLOVER    = 32;

  /* bit 6: is the active row in the chart */
  int   ACTIVE      = 64;

  /* bit 7: a NOEDT field */
  int   NOEDIT      = 128;

  /* bit 8: a NOBORDER field */
  int   NOBORDER      = 256;
}
