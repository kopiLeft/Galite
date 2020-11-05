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
 * $Id: DBlockDropTargetHandler.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.kopi.galite.form.VBlock;
import org.kopi.galite.form.VField;
import org.kopi.galite.form.VImageField;
import org.kopi.galite.form.VStringField;
import org.kopi.galite.visual.VException;

public class DBlockDropTargetHandler implements DropTargetListener {

  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------

  public DBlockDropTargetHandler(VBlock block) {
    this.block = block;
  }

  //---------------------------------------------------------
  // DROPTARGETLISTENER IMPLEMENTATION
  //---------------------------------------------------------

  public void dragEnter(DropTargetDragEvent dtde) {
    dragOver(dtde);
  }

  public void dragOver(DropTargetDragEvent dtde) {
    dtde.acceptDrag(DnDConstants.ACTION_COPY);

    if (!isAccepted(dtde.getTransferable())) {
      dtde.rejectDrag();
    }
  }

  public void dropActionChanged(DropTargetDragEvent dtde) {}

  public void dragExit(DropTargetEvent dte) {}

  @SuppressWarnings("unchecked")
  public void drop(DropTargetDropEvent dtde) {
    dtde.acceptDrop(DnDConstants.ACTION_COPY);

    try {
      if (isChartBlockContext()) {
	dtde.dropComplete(handleDrop((List<File>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)));
      } else {
	File		flavor;

	flavor = (File)((List<File>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)).get(0);
	dtde.dropComplete(handleDrop(flavor, getExtension(flavor)));
      }
    } catch (UnsupportedFlavorException e) {
      dtde.dropComplete(false);
    } catch (IOException e) {
      dtde.dropComplete(false);
    } catch (VException e) {
      dtde.dropComplete(false);
    }
  }

  //---------------------------------------------------------
  // UTILS
  //---------------------------------------------------------

  @SuppressWarnings("unchecked")
  private boolean isAccepted(Transferable transferable) {
    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      try {
	List<File>		flavors;

	flavors = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);

	if (isChartBlockContext()) {
	  return isAccepted(flavors);
	} else {
	  if (flavors.size() > 1) {
	    return false;
	  } else {
	    return isAccepted(getExtension((File)flavors.get(0)));
	  }
	}
      } catch (UnsupportedFlavorException e) {
	return false;
      } catch (IOException e) {
	return false;
      }
    } else {
      return false;
    }
  }

  private boolean isAccepted(String flavor) {
    return flavor != null && flavor.length() > 0 && block.isAccepted(flavor);
  }

  /**
   * A List of flavors is accepted if all elements
   * of the list are accepted and have the same extension
   */
  private boolean isAccepted(List<File> flavors) {
    String		oldFlavor = null;

    for (int i = 0; i < flavors.size(); i++) {
      String	newFlavor = getExtension(flavors.get(i));

      if ((oldFlavor != null && !newFlavor.equals(oldFlavor))
	  || !isAccepted(newFlavor))
      {
	return false;
      }

      oldFlavor = newFlavor;
    }

    return true;
  }

  /**
   * Handles drop action for multiple files in a chart block
   */
  private boolean handleDrop(List<File> files) throws VException {
    for (int i = 0; i < files.size(); i++) {
      File	file = files.get(i);

      if (!handleDrop(file, getExtension(file))) {
	return false;
      }
    }

    return true;
  }

  private boolean handleDrop(File file, String flavor) throws VException {
    VField	target = block.getDropTarget(flavor);

    if (target == null) {
      return false;
    }

    target.onBeforeDrop();
    if (target instanceof VStringField) {
      if (target.getWidth() < file.getAbsolutePath().length()) {
	return false;
      } else {
        if (isChartBlockContext()) {
          int               rec;

          rec = getFirstUnfilledRecord(block, target);
          block.setActiveRecord(rec);
          ((VStringField)target).setString(rec, file.getAbsolutePath());
          target.onAfterDrop();
          block.setActiveRecord(rec + 1);
          block.gotoRecord(block.getActiveRecord());
          return true;
        } else {
	  ((VStringField)target).setString(file.getAbsolutePath());
	  target.onAfterDrop();
	  return true;
	}
      }
    } else if (target instanceof VImageField) {
      if (!target.isInternal()) {
	if (isImage(file)) {
	  return handleImage((VImageField)target, file);
	} else {
	  return false;
	}
      } else {
	return handleImage((VImageField)target, file);
      }
    } else {
      return false;
    }
  }

  private boolean handleImage(VImageField target, File file) throws VException {
    if (isChartBlockContext()) {
      int               rec;

      rec = getFirstUnfilledRecord(block, target);
      block.setActiveRecord(rec);
      target.setImage(rec, toByteArray(file));
      target.onAfterDrop();
      block.setActiveRecord(rec + 1);
      block.gotoRecord(block.getActiveRecord());
      return true;
    } else {
      target.setImage(toByteArray(file));
      target.onAfterDrop();
      return true;
    }
  }

  private boolean isChartBlockContext() {
    return block.noDetail() || (block.isMulti() && !block.isDetailMode());
  }

  private static String getExtension(File file) {
    String		extension = null;
    String 		name = file.getName();
    int 		index = name.lastIndexOf('.');

    if (index > 0 &&  index < name.length() - 1) {
      extension = name.substring(index + 1).toLowerCase();
    }

    return extension;
  }

  private static boolean isImage(File file) {
    String		mimeType;

    mimeType = MIMETYPES_FILE_TYPEMAP.getContentType(file);
    if(mimeType.split("/")[0].equals("image")) {
      return true;
    }

    return false;
  }

  private static byte[] toByteArray(File file) {
    try {
      ByteArrayOutputStream	baos = new ByteArrayOutputStream();

      copy(new FileInputStream(file), baos, 1024);
      return baos.toByteArray();
    } catch (IOException e) {
      return null;
    }
  }

  private static void copy(InputStream input,
                           OutputStream output,
                           int bufferSize)
    throws IOException
  {
    byte[]	buf = new byte[bufferSize];
    int 	bytesRead = input.read(buf);

    while (bytesRead != -1) {
      output.write(buf, 0, bytesRead);
      bytesRead = input.read(buf);
    }

    output.flush();
  }
  
  private static int getFirstUnfilledRecord(VBlock block, VField target) {
    for (int i = 0; i < block.getBufferSize(); i++) {
      if (target.isNull(i)) {
        return i;
      }
    }

    return 0;
  }

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private final VBlock				block;
  private static final MimetypesFileTypeMap	MIMETYPES_FILE_TYPEMAP = new MimetypesFileTypeMap();

  static {
    // missing PNG files in initial map
    MIMETYPES_FILE_TYPEMAP.addMimeTypes("image/png png");
  }
}
