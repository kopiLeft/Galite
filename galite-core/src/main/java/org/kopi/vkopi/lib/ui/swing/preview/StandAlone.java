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
 * $Id: StandAlone.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.preview;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;

import javax.swing.UIManager;

import org.kopi.galite.preview.VPreviewWindow;
import org.kopi.vkopi.lib.ui.swing.base.Utils;
import org.kopi.galite.ui.swing.visual.JApplication;
import org.kopi.galite.util.PrintJob;
import org.kopi.galite.visual.VException;
import org.kopi.vkopi.lib.ui.swing.plaf.KopiLookAndFeel;
import org.kopi.xkopi.lib.base.DBContext;

import com.lowagie.text.Rectangle;

/**
 * Starts PreviewWindow (for testing) without an Application
 */
public class StandAlone {

  @SuppressWarnings("serial")
  public StandAlone(String inFile) {
    title = inFile;

    try {
      FileInputStream   fileIn = new FileInputStream(inFile);
      File		file = Utils.getTempFile("PREVIEW", "PS");

      BufferedWriter	ous = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
      // USE A TEMP FILE !!!

      // READ HEADER
      LineNumberReader    reader = new LineNumberReader(new InputStreamReader(fileIn));
      String              s;
      int                 currentPage = -1;

      while ((s = reader.readLine()) != null) {
        if (s.equals("/toprinter {true} def")) {
          ous.write("/toprinter {false} def");
          ous.write("\n");
        } else if (numberOfPages == -1 && s.startsWith("%%Page: ")) {
          currentPage = readCurrentPageNumber(s);
          ous.write(s);
          ous.write("\n");
        } else {
          ous.write(s);
          ous.write("\n");
        }
      }
      reader.close();
      ous.close();

      if (numberOfPages == -1 && currentPage != -1) {
        numberOfPages = currentPage;
      }

      try {
        VPreviewWindow    preview = new VPreviewWindow() {
            public void close(int type) {
              System.exit(0);
            }
          };

        PrintJob        job = new PrintJob(file, false, PrintJob.FORMAT_A4);

        job.setPrintInformation(title, format, numberOfPages);
        preview.preview(job, command);
      } catch (VException e) {
        e.printStackTrace();
        System.exit(1);
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private int readCurrentPageNumber(String line) {
    StringBuffer    buffer = new StringBuffer();

    // skip "%%Page: "
    for (int i = 8; i < line.length() && Character.isDigit(line.charAt(i)); i++) {
      buffer.append(line.charAt(i));
    }
    if (buffer.length() == 0) {
      return -1;
    } else {
      try {
        return Integer.parseInt(buffer.toString());
      } catch (NumberFormatException e) {
        return -1;
      }
    }
  }

  public static void main(String[] argv) {
    if (argv.length != 1) {
      System.out.println("usage: java org.kopi.galite.preview.StandAlone filename");
      System.exit(1);
    }

    try {
      UIManager.setLookAndFeel(new KopiLookAndFeel());
    } catch (Exception e) {
      System.err.println("Undefined look and feel: Kopi Look & Feel must be installed!");
      System.exit(1);
    }

    new JApplication(null) {

      public DBContext login(String database, String driver, String username, String password, String schema) {
        return null;
      }
    };
    
    new StandAlone(argv[0]);
  }

  private String		command = "gs ";
  private String		title = "Editor";

  private Rectangle             format = PrintJob.FORMAT_A4;
  private int			numberOfPages = -1;
}
