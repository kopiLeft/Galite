/*
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH
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
 * $Id$
 */

package org.kopi.vkopi.lib.ui.swing.visual;

import java.io.IOException;

import org.kopi.galite.visual.preview.VPreviewWindow;
import org.kopi.galite.visual.print.PSPrintException;
import org.kopi.galite.visual.util.AbstractPrinter;
import org.kopi.galite.visual.util.PrintException;
import org.kopi.galite.visual.util.PrintJob;
import org.kopi.galite.visual.visual.ApplicationConfiguration;
import org.kopi.galite.visual.visual.PreviewRunner;

public class JPreviewRunner implements PreviewRunner {

  /**
   * Launch document preview
   */
  public void run(PrintJob data, String command) throws IOException, PrintException {
    if (ApplicationConfiguration.Companion.getConfiguration().useAcroread()) {
      try {
	if (System.getProperty("os.name").startsWith("Linux")) {
	  Runtime.getRuntime().exec("acroread " + data.getDataFile());
	} else if (System.getProperty("os.name").startsWith("Windows")) {
	  Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " +  data.getDataFile());
	}
      } catch (IOException e) {
	System.out.println("Acroread failed: " + e.getMessage());
      }
    } else {
      try {
	new VPreviewWindow().preview((data.getDataType() != PrintJob.DAT_PS) ? data : AbstractPrinter.Companion.convertToGhostscript(data), command);
      } catch (Exception e) {
	throw new PSPrintException("PreviewPrinter.PrintTaskImpl::print()", e);
      }
    }
  }
}
