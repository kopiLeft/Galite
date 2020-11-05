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
 * $Id: ImageFileChooser.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.form;

import org.kopi.vkopi.lib.ui.swing.base.Utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

public class ImageFileChooser {

    // ----------------------------------------------------------------------
    // FILE CHOOSER
    // ----------------------------------------------------------------------

    public static File chooseFile(Component parent) {
        JFileChooser filechooser = new JFileChooser();
        filechooser.addChoosableFileFilter(new ImageFilter());
        filechooser.setFileView(new ImageFileView());
        filechooser.setAccessory(new ImagePreview(filechooser));

        int returnVal = filechooser.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return filechooser.getSelectedFile();
        } else {
            return null;
        }
    }

    // ----------------------------------------------------------------------
    // INNER CLASSES
    // ----------------------------------------------------------------------

    private static class ImageFilter extends FileFilter {
        static final String jpeg = "jpeg";
        static final String jpg = "jpg";
        static final String gif = "gif";

        // Accept all directories and all gif, jpg, or tiff files.
        public boolean accept(File f) {

            if (f.isDirectory()) {
                return true;
            }

            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                String extension = s.substring(i + 1).toLowerCase();
                if (gif.equals(extension) ||
                        jpeg.equals(extension) ||
                        jpg.equals(extension)) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        // The description of this filter
        public String getDescription() {
            return "Just Images";
        }
    }

    // ----------------------------------------------------------------------

    private static class ImagePreview extends JComponent implements PropertyChangeListener {


        ImageIcon thumbnail = null;
        File f = null;

        public ImagePreview(JFileChooser fc) {
            setPreferredSize(new Dimension(100, 50));
            fc.addPropertyChangeListener(this);
        }

        public void loadImage() {
            if (f == null) {
                return;
            }

            ImageIcon tmpIcon = new ImageIcon(f.getPath());
            if (tmpIcon.getIconWidth() > 90) {
                thumbnail = new ImageIcon(tmpIcon.getImage().
                        getScaledInstance(90, -1, Image.SCALE_DEFAULT));
            } else {
                thumbnail = tmpIcon;
            }
        }

        public void propertyChange(PropertyChangeEvent e) {
            String prop = e.getPropertyName();
            if (prop == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
                f = (File) e.getNewValue();
                if (isShowing()) {
                    loadImage();
                    repaint();
                }
            }
        }

        public void paint(Graphics g) {
            if (thumbnail == null) {
                loadImage();
            }
            if (thumbnail != null) {
                int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
                int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;

                if (y < 0) {
                    y = 0;
                }

                if (x < 5) {
                    x = 5;
                }
                thumbnail.paintIcon(this, g, x, y);
            }
        }

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = -253909169278804396L;
    }

    // ----------------------------------------------------------------------

    private static class ImageFileView extends FileView {
        ImageIcon jpgIcon = Utils.getImage("jpgIcon.gif");
        ImageIcon gifIcon = Utils.getImage("gifIcon.gif");

        public String getName(File f) {
            return null; // let the L&F FileView figure this out
        }

        public String getDescription(File f) {
            return null; // let the L&F FileView figure this out
        }

        public Boolean isTraversable(File f) {
            return null; // let the L&F FileView figure this out
        }

        public String getTypeDescription(File f) {
            String extension = getExtension(f);
            String type = null;

            if (extension != null) {
                if (extension.equals("jpeg") || extension.equals("jpg")) {
                    type = "JPEG Image";
                } else if (extension.equals("gif")) {
                    type = "GIF Image";
                }
            }

            return type;
        }

        public Icon getIcon(File f) {
            String extension = getExtension(f);
            Icon icon = null;
            if (extension != null) {
                if (extension.equals("jpeg") || extension.equals("jpg")) {
                    icon = jpgIcon;
                } else if (extension.equals("gif")) {
                    icon = gifIcon;
                }
            }
            return icon;
        }

        // Get the extension of this file. Code is factored out
        // because we use this in both getIcon and getTypeDescription
        private String getExtension(File f) {

            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        }
    }
}
