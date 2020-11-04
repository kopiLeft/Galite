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
 * $Id: KopiButtonUI.java 35283 2018-01-05 09:00:51Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.metal.MetalButtonUI;

import org.kopi.galite.base.Utils;
import org.kopi.vkopi.lib.ui.swing.base.JActorFieldButton;
import org.kopi.vkopi.lib.ui.swing.base.JFieldButton;
import org.kopi.vkopi.lib.ui.swing.base.JMenuButton;
import org.kopi.vkopi.lib.ui.swing.base.KnownBugs;

public class KopiButtonUI extends MetalButtonUI {
  protected static KopiButtonUI kopiButtonUI = new KopiButtonUI();

  public static ComponentUI createUI(JComponent x ) {
    return kopiButtonUI;
  }

  public void installDefaults(AbstractButton b) {
    super.installDefaults(b);
    b.setRolloverEnabled(true);
    if (b instanceof JMenuButton || b instanceof JActorFieldButton) {
      b.setBorder(border_empty);
      b.setFont(font);
      b.setIconTextGap(textIconGap);
      b.setAlignmentY(0);
    }
  }

  public void paint(Graphics g, JComponent c) {
    paintBackground(g,c);
    super.paint(g, c);
    if (c instanceof JMenuButton) {
      paintAcceleratorTip(g, c);
    }
  }

  protected void paintBackground(Graphics g, JComponent c) {
    Insets              i = c.getInsets();
    Rectangle           viewRect = new Rectangle();
    AbstractButton      button = ((AbstractButton)c);
    ButtonModel         model = button.getModel();
    
    viewRect.x = i.left;
    viewRect.y = i.top;
    viewRect.width = c.getWidth() - (i.right + viewRect.x);
    viewRect.height = c.getHeight() - (i.bottom + viewRect.y);

    if (c instanceof JMenuButton || c instanceof JActorFieldButton) {
      g.setColor(backColorMenu);
      g.fillRect(0, 0, c.getWidth(), c.getHeight());

      if (model.isRollover()) {
        g.setColor(rollColor);
      } else {
        g.setColor(backColorMenu);
      }
      g.fillRoundRect(1, 1, viewRect.width-2, viewRect.height-2, border_arcMenu, border_arc);
      if (model.isRollover()) {
        Graphics2D        g2d = (Graphics2D) g;
        GradientPaint     gp;

        gp = new GradientPaint(viewRect.width-7,
                               0,
                               rollColor,
                               viewRect.width,
                               0,
                               borderColor);
        g2d.setPaint(gp);
        g.fillRoundRect(viewRect.width-7, 1, viewRect.width-2, viewRect.height-2, border_arcMenu*2, border_arcMenu*2);
        gp = new GradientPaint(0,
                               viewRect.height-7,
                               rollColor,
                               0,
                               viewRect.height,
                               borderColor);
        g2d.setPaint(gp);
        g.fillRoundRect(1, viewRect.height-7, viewRect.width-2, viewRect.height-2, border_arcMenu*2, border_arcMenu*2);
      }
      border.paintBorder(c, g, 0, 0, c.getWidth(), c.getHeight());
    } else if (c instanceof JFieldButton) {
      if (c.isEnabled()) {
        GradientPaint   gp;

        if (model.isPressed()) {
          gp = new GradientPaint(viewRect.x,
                                 viewRect.y,
                                 back_pressed,
                                 viewRect.x + viewRect.width,
                                 viewRect.y + viewRect.height,
                                 back_pressed.brighter().brighter());
        } else if (model.isRollover()) {
          gp = new GradientPaint(viewRect.x,
                                 viewRect.y,
                                 Color.white,
                                 viewRect.x + viewRect.width,
                                 viewRect.y + viewRect.height,
                                 back_roll);
        } else {
          gp = new GradientPaint(viewRect.x,
                                 viewRect.y,
                                 back,
                                 viewRect.x + viewRect.width,
                                 viewRect.y + viewRect.height,
                                 back.darker());
        }
        ((Graphics2D) g).setPaint(gp);
      } else {
        g.setColor(back_disabled);
      }
      g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), border_arc, border_arc);
    } else {
      g.setColor(backColor2);
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
      GradientPaint   gp;

      gp = new GradientPaint(viewRect.x,
                             viewRect.y,
                             backColor.brighter().brighter(),
                             viewRect.x + viewRect.width / 2,
                             viewRect.y + viewRect.height * 3,
                             backColor);
      ((Graphics2D) g).setPaint(gp);
      
      g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 7, 7);
    }
  }

  protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
    AbstractButton      b = (AbstractButton) c;                       
    ButtonModel         model = b.getModel();
    FontMetrics         fm = g.getFontMetrics();
    int                 mnemonicIndex = b.getDisplayedMnemonicIndex();
    int                 x = textRect.x + (model.isEnabled() ? getTextShiftOffset() : 0);
    int                 y = textRect.y + fm.getAscent() + (model.isEnabled() ? getTextShiftOffset() : 0);

    /* Draw the Text */
    if (model.isEnabled()) {
      g.setColor(b.getForeground());
      BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, x, y);
    } else {
      /*** paint the text disabled ***/
      g.setColor(b.getBackground().darker());
      BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemonicIndex, x, y);
    }
  }

  protected void paintButtonPressed(Graphics g, AbstractButton b){
    if (b instanceof JMenuButton || b instanceof JActorFieldButton) {
      if ( b.isContentAreaFilled() ) {
        Dimension size = b.getSize();
        
        g.setColor(selectColor);
        g.fillRoundRect(1, 1, size.width-2, size.height-2, border_arc, border_arc);
      }
    } else if (! (b instanceof JFieldButton)) {
      super.paintButtonPressed(g, b);
    }
  }

  protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect){
    try {
      KnownBugs.loadImage(((AbstractButton) c).getIcon());

      if (c instanceof JMenuButton) {
        super.paintIcon(g, c, new Rectangle(iconRect.x, iconRect.y+8, iconRect.width, iconRect.height));
      } else if (c instanceof JActorFieldButton) {
        super.paintIcon(g, c, new Rectangle(iconRect.x - 32, iconRect.y + 10, 28, 28));
      } else {
        super.paintIcon(g, c, iconRect);
      }
    } catch (NullPointerException npe) {
      Utils.freeMemory();
      // try again
      KnownBugs.paintIconFailure = true;
    }
  }

  private void paintAcceleratorTip(Graphics g, JComponent c) {
    // get Accelerator text
    AbstractButton    b = (AbstractButton) c;
    ButtonModel       m = b.getModel();
    Action            action = b.getAction();

    if (! m.isEnabled() || action == null) {
      return;
    }

    String            acceleratorText = "";
    KeyStroke         key = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);

    if (key == null) {
      // no accelerator key defined
      return;
    }

    int               modifiers = key.getModifiers();
    int               keyCode = key.getKeyCode();

    if (modifiers > 0) {
      acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
      //acceleratorText += "-";
      acceleratorText += acceleratorDelimiter;
    }
      
    if (keyCode != 0) {
      acceleratorText += KeyEvent.getKeyText(keyCode);
    }
    FontMetrics       fm = g.getFontMetrics();
    int               width = (int) fm.getStringBounds(acceleratorText, g).getWidth();      
    Font              font = g.getFont();
      
    g.setFont(keyTipFont);
    g.setColor(keyTipColor);
    g.drawString(acceleratorText, c.getWidth()-width-2, 10);
    g.setFont(font);
  }

  protected void paintFocus(Graphics g, AbstractButton b,
                            Rectangle viewRect, Rectangle textRect, Rectangle iconRect){
  }

  private static final Color    backColor = UIManager.getColor("Button.background");
  private static final Color    backColor2 = UIManager.getColor("control");

  private static final Color    back = UIManager.getColor("FieldButton.background");
  private static final Color    back_disabled = UIManager.getColor("FieldButton.background.disabled");
  private static final Color    back_roll = UIManager.getColor("FieldButton.background.rollover");
  private static final Color    back_pressed = UIManager.getColor("FieldButton.background.pressed");

  private static final int      border_arc = UIManager.getInt("FieldButton.border.arc");

  private static final String	acceleratorDelimiter =  UIManager.getString("MenuItem.acceleratorDelimiter");
  private static final Color    selectColor = UIManager.getColor("MenuButton.background");
  private static final Color    borderColor = UIManager.getColor("MenuButton.border");
  private static final Color    keyTipColor = UIManager.getColor("MenuButton.keytip.color");
  private static final Font     keyTipFont = UIManager.getFont("MenuButton.keytip.font");
  private static final Color    rollColor = UIManager.getColor("MenuButton.rollover");
  private static final Color    backColorMenu = UIManager.getColor("ButtonPanel.back");
  private static final int      border_arcMenu = UIManager.getInt("MenuButton.border.arc");
  private static final int      textIconGap = UIManager.getInt("MenuButton.textIconGap");
  private static final Font     font = UIManager.getFont("MenuButton.font");

  private static Border         border = new KopiUtils.KopiButtonBorder();
  private static Border         border_empty = new EmptyBorder(0,0,0,0);//new KopiUtils.KopiButtonBorder();

}
