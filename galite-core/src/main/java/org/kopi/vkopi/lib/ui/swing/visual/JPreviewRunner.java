/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

import org.kopi.vkopi.lib.preview.VPreviewWindow;
import org.kopi.vkopi.lib.print.PSPrintException;
import org.kopi.vkopi.lib.util.AbstractPrinter;
import org.kopi.vkopi.lib.util.PrintException;
import org.kopi.vkopi.lib.util.PrintJob;
import org.kopi.vkopi.lib.visual.ApplicationConfiguration;
import org.kopi.vkopi.lib.visual.PreviewRunner;
import org.kopi.vkopi.lib.visual.VException;

import java.io.IOException;

public class JPreviewRunner implements PreviewRunner {

  /**
   * Launch document preview
   */
  public void run(PrintJob data, String command) throws IOException, PrintException {
    if (ApplicationConfiguration.getConfiguration().useAcroread()) {
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
	new VPreviewWindow().preview((data.getDataType() != PrintJob.DAT_PS) ? data : AbstractPrinter.convertToGhostscript(data), command);
      } catch (VException e) {
	throw new PSPrintException("PreviewPrinter.PrintTaskImpl::print()", e);
      }
    }
  }
}
