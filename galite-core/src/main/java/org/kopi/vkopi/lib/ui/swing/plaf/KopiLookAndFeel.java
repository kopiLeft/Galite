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
 * * $Id: KopiLookAndFeel.java 35283 2018-01-05 09:00:51Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

public class KopiLookAndFeel extends MetalLookAndFeel {
  
public KopiLookAndFeel() {
    // install with the UIManager, if not done yet.
    if (!isInstalled) {
      UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo("Kopi", "org.kopi.vkopi.lib.ui.swing.plaf.KopiLookAndFeel"));
      isInstalled = true;
    }
  }

  public String getID() {
    return "Kopi";
  }

  public String getName() {
    return "Kopi";
  }

  public String getDescription() {
    return "Look and Feel giving a flat effect."
      + " Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH."
      + " Published under the GNU Lesser General Public Licence Version 2.1.";
  }

  public boolean isNativeLookAndFeel() {
    return false;
  }

  public boolean isSupportedLookAndFeel() {
    return true;
  }

  protected void initClassDefaults(UIDefaults table) {
    super.initClassDefaults(table);
    putDefault(table, "LabelUI");
    putDefault(table, "TextFieldUI");
    putDefault(table, "FieldLabelUI");
    putDefault(table, "PasswordFieldUI");
    putDefault(table, "TabbedPaneUI");
    putDefault(table, "TextAreaUI");
    putDefault(table, "TextPaneUI");
    putDefault(table, "ScrollBarUI");
    putDefault(table, "ProgressBarUI");
    putDefault(table, "ToggleButtonUI");
    putDefault(table, "ButtonUI");
    // non standard UI
    //putDefault(table, "FixTextUI");
    //    putDefault(table, "DBFieldUI");
    //putDefault(table, "MenuButtonUI");
    //    putDefault(table, "FieldButtonUI");
  }

  protected void initSystemColorDefaults(UIDefaults table) {
    super.initSystemColorDefaults(table);
  }

  protected void initComponentDefaults(UIDefaults table) {
    super.initComponentDefaults(table);

    Font        propFont = new Font("helvetica", Font.PLAIN, 12);
    Font        fixFont = new Font("dialoginput", Font.PLAIN, 12);
    Font        propFontSmall = new Font("helvetica", Font.PLAIN, 10);
    Font        keyTipFont = new Font("helvetica", Font.PLAIN, 9);
    Font        infoFont = new Font("dialoginput", Font.PLAIN, 10);

    table.put("KopiLabel.skipped.color",                LBL_COLOR_SKIPPED);  
    table.put("KopiLabel.mustfill.color",               LBL_COLOR_MUSTFILL);  
    table.put("KopiLabel.visit.color",                  LBL_COLOR_VISIT);  
    table.put("KopiLabel.focused.color",                LBL_COLOR_FOCUSED);
    table.put("KopiLabel.x.space",                      new Integer(8));

    table.put("KopiLabel.ul.skipped.color",             KopiTheme.USER_COLORS.COLOR_4);
    table.put("KopiLabel.ul.mustfill.color",            KopiTheme.USER_COLORS.COLOR_4);
    table.put("KopiLabel.ul.visit.color",               KopiTheme.USER_COLORS.COLOR_4);
    table.put("KopiLabel.ul.color",                     KopiTheme.USER_COLORS.COLOR_4);
    table.put("KopiLabel.ul.width",                     new Integer(1));
    table.put("KopiLabel.info.color",                   KopiTheme.USER_COLORS.COLOR_9);
    table.put("KopiLabel.info.font",                    infoFont);

    table.put("KopiField.skipped.color",                Color.black);
    table.put("KopiField.mustfill.color",               Color.black);
    table.put("KopiField.visit.color",                  Color.black);
    table.put("KopiField.focused.color",                Color.black);

    table.put("KopiField.background.color",             Color.white);
    table.put("KopiField.background.skipped.color",     KopiTheme.USER_COLORS.COLOR_1);
    table.put("KopiField.background.visit.color",       LBL_COLOR_BACK_VISIT);
    table.put("KopiField.background.mustfill.color",    LBL_COLOR_BACK_MUSTFILL);

    table.put("KopiField.ul.skipped.color",             KopiTheme.USER_COLORS.COLOR_4);
    table.put("KopiField.ul.mustfill.color",            KopiTheme.USER_COLORS.COLOR_4);
    table.put("KopiField.ul.visit.color",               KopiTheme.USER_COLORS.COLOR_4);
    table.put("KopiField.ul.color",                     KopiTheme.USER_COLORS.COLOR_4);
    table.put("KopiField.ul.chart",                     KopiTheme.USER_COLORS.COLOR_3);
    table.put("KopiField.ul.chart.active",              KopiTheme.USER_COLORS.COLOR_9);
    table.put("KopiField.index",                        KopiTheme.USER_COLORS.COLOR_3);
    table.put("KopiField.ul.width",                     new Integer(1));
    table.put("FieldText.y.space",                      new Integer(4));
    table.put("FieldText.x.space",                      new Integer(6));

    table.put("Button.border",                          BTN_BORDER);
    table.put("Button.background",                      KopiTheme.USER_COLORS.COLOR_3);
    table.put("Button.margin",                          new InsetsUIResource(1, 20, 1, 20));
    table.put("Button.focusInputMap",                   new UIDefaults.LazyInputMap(new Object[] {
                                                                   "SPACE", "pressed",
                                                          "released SPACE", "released",
                                                                   "ENTER", "pressed",
                                                          "released ENTER", "released"
                                                        }));
    table.put("ProgressBar.border",                     PGB_BORDER);
    table.put("ToggleButton.border",                    BTN_BORDER);
    table.put("ToggleButton.margin",                    new InsetsUIResource(0, 0, 0, 0));
    table.put("ToggleButton.select",                    KopiTheme.USER_COLORS.COLOR_9);
    table.put("ComboBox.background",                    Color.white);
    table.put("MenuItem.selectionForeground",           Color.white);
    table.put("MenuItem.acceleratorForeground",         KopiTheme.USER_COLORS.COLOR_9);
    table.put("MenuItem.acceleratorSelectionForeground",Color.white);
    table.put("Menu.selectionForeground",               Color.white);

    table.put("TabbedPaneUI.tabarea.background",        KopiTheme.USER_COLORS.COLOR_1);

    table.put("TextField.font", fixFont);
    table.put("TextArea.font", fixFont);
    table.put("TextPane.font", fixFont);
    table.put("PasswordField.font", fixFont);

    table.put("DateChooser.weekend", Color.red.darker());
    table.put("DateChooser.weeknumber", KopiTheme.USER_COLORS.COLOR_7);

    table.put("Menu.font", propFont);
    table.put("MenuItem.font", propFont);
    table.put("MenuBar.font", propFont);
    table.put("Label.font", propFont);
    table.put("Button.font", propFont);
    table.put("font", propFont);
    table.put("TabbedPane.font", propFont);

    table.put("KopiLayout.font", fixFont);

    table.put("TextField.background",           COLOR_SEL_FLD);
    table.put("TextArea.background",            COLOR_SEL_FLD);
    table.put("TextField.selectionBackground",  KopiTheme.USER_COLORS.COLOR_9);
    table.put("TextField.selectionForeground",  Color.white);
    table.put("TextPane.selectionBackground",  KopiTheme.USER_COLORS.COLOR_9);
    table.put("TextPane.selectionForeground",  Color.white);
    table.put("TextArea.selectionBackground",   KopiTheme.USER_COLORS.COLOR_9);
    table.put("TextArea.selectionForeground",   Color.white);
    table.put("PasswordField.selectionBackground",  KopiTheme.USER_COLORS.COLOR_9);
    table.put("PasswordField.selectionForeground",  Color.white);
    table.put("TabbedPane.background",          KopiTheme.USER_COLORS.COLOR_6);
    table.put("TabbedPane.highlight",           KopiTheme.USER_COLORS.COLOR_3);

    table.put("KopiField.alert",                Color.red);
    table.put("KopiField.critical",             Color.pink);
    table.put("KopiField.critical.skipped",     new Color(255, 192, 203)); // LIGHT PINK
    table.put("KopiField.action",               new Color(255, 230, 230)); // WHITE PINK
    table.put("KopiField.noedit",               Color.white);

    table.put("ScrollBar.thumb",                KopiTheme.USER_COLORS.COLOR_2);
    table.put("ScrollBar.thumbShadow",          KopiTheme.USER_COLORS.COLOR_4);
    table.put("ScrollBar.track",                KopiTheme.USER_COLORS.COLOR_6);
    table.put("ScrollBar.background",           table.get("control"));
    table.put("ScrollBar.highlight",            KopiTheme.USER_COLORS.COLOR_6);

    table.put("kopi.menuitem.parent",           new Color(0, 0, 153));
    table.put("kopi.menuitem.selected",         Color.black);
    table.put("kopi.menuitem.focus",            Color.white);
    table.put("kopi.menuitem.focus.background", KopiTheme.USER_COLORS.COLOR_9);

    table.put("snapshot.font.dialog",           fixFont);
    table.put("snapshot.background",            table.get("control"));
    table.put("menu.background",                table.get("control"));

    table.put("KopiTitleBorder.border",         KopiTheme.USER_COLORS.COLOR_8);
    table.put("KopiTitleBorder.background",     table.get("control"));
    table.put("KopiTitleBorder.foreground",     KopiTheme.USER_COLORS.COLOR_7);
    table.put("KopiTitleBorder.font",           propFont);
    table.put("KopiTitleBorder.border.empty",   blockBorder);
    table.put("KopiTitleBorder.border.line",    blockBorder);
    table.put("KopiTitleBorder.border.raised",  blockBorder);
    table.put("KopiTitleBorder.border.lowered", blockBorder);
    table.put("KopiTitleBorder.border.etched",  blockBorder);

    table.put("ButtonPanel.border",             KopiTheme.USER_COLORS.COLOR_3);
    table.put("ButtonPanel.back",               KopiTheme.USER_COLORS.COLOR_1);

    table.put("MenuButton.text.enabled",        new Boolean(true));
    table.put("MenuButton.border",              KopiTheme.USER_COLORS.COLOR_3);//COLOR_5);
    table.put("MenuButton.border.highlight",    KopiTheme.USER_COLORS.COLOR_11);
    table.put("MenuButton.border.darkshadow",   KopiTheme.USER_COLORS.COLOR_10);
    table.put("MenuButton.background",          getControlShadow());
    table.put("MenuButton.rollover",            Color.white);
    table.put("MenuButton.border.arc",          new Integer(7));//new Integer(24));
    table.put("MenuButton.font",                propFontSmall);
    table.put("MenuButton.margin",              new InsetsUIResource(1, 1, 1, 1));
    table.put("MenuButton.textIconGap",         new Integer(8));
    table.put("MenuButton.keytip.color",        KopiTheme.USER_COLORS.COLOR_9);
    table.put("MenuButton.keytip.font",         keyTipFont);

    table.put("FieldButton.border.disabled",    KopiTheme.USER_COLORS.COLOR_3);
    table.put("FieldButton.border.color",       KopiTheme.USER_COLORS.COLOR_9);
    table.put("FieldButton.border.arc",         new Integer(7));
    table.put("FieldButton.textIconGap",        table.get("Button.textIconGap"));
    table.put("FieldButton.textShiftOffset",    table.get("Button.textShiftOffset"));
    table.put("FieldButton.margin",             table.get("Button.margin"));
    table.put("FieldButton.background",         KopiTheme.USER_COLORS.COLOR_2);
    table.put("FieldButton.background.pressed", KopiTheme.USER_COLORS.COLOR_9);
    table.put("FieldButton.background.disabled",KopiTheme.USER_COLORS.COLOR_1);
    table.put("FieldButton.background.rollover",KopiTheme.USER_COLORS.COLOR_2);
    table.put("FieldButton.foreground",         table.get("Button.foreground"));
    table.put("FieldButton.font",               table.get("Button.font"));
    table.put("FieldButton.border",             table.get("Button.border"));
    table.put("FieldButton.select",             table.get("Button.select"));
    table.put("FieldButton.disabledText",       table.get("Button.disabledText"));
    table.put("FieldButton.focus",              table.get("Button.focus"));

    table.put("ListDialog.background",          Color.white);
    table.put("ListDialog.background.selected", LBL_COLOR_BACK_MUSTFILL);
    table.put("ListDialog.foreground",          Color.black);
    table.put("ListDialog.foreground.selected", Color.white);
    table.put("ListDialog.font",                fixFont);
    table.put("ListDialog.row.height",          new Integer(20)); 
  }


  protected void putDefault(UIDefaults table, String uiKey) {
    try {
      String className = "org.kopi.vkopi.lib.ui.swing.plaf.Kopi"+uiKey;
      table.put(uiKey, className);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Sets the current color theme. This works exactly as with the MetalLookAndFeel.
   * Note that for customizing the gradients the method setCurrentGradientTheme()
   * must be used.
   */
  public static void setCurrentTheme(MetalTheme theme) {
    MetalLookAndFeel.setCurrentTheme(theme);
    themeHasBeenSet = true;
  }

  protected void createDefaultTheme() {
    if (!themeHasBeenSet) {
      setCurrentTheme(new KopiTheme());
    }
  }

 
  static class KopiButtonBorder extends LineBorder {
    
	
	public KopiButtonBorder() {
      super(KopiTheme.USER_COLORS.COLOR_5, 4);
      
    }
    /**
     *
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      AbstractButton    button = (AbstractButton) c;
      ButtonModel       model = button.getModel();
      Rectangle         clipRect = new Rectangle(x+1, y+1, width-2, height-2);

      Color oldColor = g.getColor();
      if (model.isEnabled()) {
        if (model.isRollover()) {
          KopiUtils.drawActiveButtonBorder(g, c, clipRect, KopiTheme.USER_COLORS.COLOR_10, KopiTheme.USER_COLORS.COLOR_11, KopiTheme.USER_COLORS.COLOR_5);
 	} else if (button.isFocusPainted() && c.hasFocus()) {
          KopiUtils.drawActiveButtonBorder(g, c, clipRect, KopiTheme.USER_COLORS.COLOR_12, KopiTheme.USER_COLORS.COLOR_13, KopiTheme.USER_COLORS.COLOR_5);
        } else {
          g.setColor(lineColor);
          g.drawRoundRect(x+1, y+1, width-2, height-2, 7, 7);
        }
      }
      g.setColor(oldColor);
    }
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -530054692818299534L;
  }


  static class ButtonLineBorder extends LineBorder {

    
	public ButtonLineBorder(Color color)  {
      super(color, 3, true);
    }    

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      
      g.setColor(lineColor);
      g.drawRoundRect(x+1, y+1, width-2, height-2, 7, 7);
      g.setColor(oldColor);
    }
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4848831354770955851L;

  }

  public static class TopLineBorder extends LineBorder {
    
	public TopLineBorder()  {
      super(KopiTheme.USER_COLORS.COLOR_3, 3, true);
    }    

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      
      g.setColor(lineColor);
      g.drawLine(x, y, x+width, y);
      g.setColor(oldColor);
    }
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4029306344254390258L;
  }

//   private static Color COLOR_1  = new Color(248, 247, 241);      // light brown
//   private static Color COLOR_2  = new Color(214, 223, 247);      // light blue
//   private static Color COLOR_3  = new Color(173, 170, 156);      // dark brown
//   private static Color COLOR_4  = new Color(123, 158, 189);      // middle blue
//   private static Color COLOR_5  = new Color(  0,  60, 115);      // near black
//   private static Color COLOR_6  = new Color(239, 235, 222);      // middle brown
//   private static Color COLOR_7  = new Color(  0,  69, 214);      // dark blue
//   private static Color COLOR_8  = new Color( 66, 154, 255);      // middle blue (line)
//   private static Color COLOR_9  = new Color( 49, 105, 198);      // middle blue (selection)
//   private static Color COLOR_10 = new Color(231, 150,   0);      // dark orange (rollover)
//   private static Color COLOR_11 = new Color(255, 219, 140);      // bright orange (rollover)
//   private static Color COLOR_12 = new Color(107, 130, 239);      // dark blue (rollover)
//   private static Color COLOR_13 = new Color(189, 215, 247);      // bright blue (rollover)
//   private static Color COLOR_14 = new Color(255, 255, 231);      // light yellow (tooltip)

  private static Border BTN_BORDER = 
    new BorderUIResource.CompoundBorderUIResource(new KopiButtonBorder(),
                                                  new BasicBorders.MarginBorder());
  private static Border PGB_BORDER = new LineBorder(KopiTheme.USER_COLORS.COLOR_3, 1);

  private static Border         blockBorder = new EmptyBorder(5, 5, 5, 5);
    
  private static boolean themeHasBeenSet = false;
  private static boolean isInstalled = false;

  /* Colors for label */
  private static Color LBL_COLOR_MUSTFILL = new Color(0, 0, 153);
  private static Color LBL_COLOR_VISIT = new Color(0, 80, 0);
  private static Color LBL_COLOR_SKIPPED = Color.black;
  private static Color LBL_COLOR_FOCUSED = Color.red;

  private static Color LBL_COLOR_BACK_MUSTFILL =  new Color(156, 184, 231);
  private static Color LBL_COLOR_BACK_VISIT = new Color(198, 255, 198);
  private static Color COLOR_SEL_FLD = new Color(255, 255, 174);
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 5215994622108520908L;
}
