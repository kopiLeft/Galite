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
 * $Id: SpellChecker.java 34997 2016-12-01 09:51:43Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.spellchecker;

import java.awt.Frame;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.kopi.galite.visual.visual.VlibProperties;
import org.kopi.vkopi.lib.ui.swing.base.Utils;

public class SpellChecker {

  public SpellChecker(String aspell, Frame parent, Document document) {
    this.aspell = aspell;
    this.parent = parent;
    this.document = document;
  }

  @SuppressWarnings("deprecation")
  public void check() throws SpellException {
    AspellProcess       	spellChecker = new AspellProcess(aspell);
    List<Suggestions>           results = null;

    try {
      results = spellChecker.checkText(document.getText(0, document.getLength()));
    } catch(BadLocationException e) {
    }
    resultIterator = results.iterator();

    if (checkNext()) {
      SpellcheckerDialog  spellDialog = new SpellcheckerDialog(parent,
              VlibProperties.getString("aspell-dialog-title"),
              this);
      spellDialog.show();
    }

    Object[]    options = { VlibProperties.getString("CLOSE")};

    JOptionPane.showOptionDialog(parent,
            VlibProperties.getString("aspell-finished"),
            VlibProperties.getString("aspell-notice"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            ICN_NOTICE,
            options,
            options[0]);

    //    return check(text, results);
  }

  protected boolean checkNext() {
    if (! resultIterator.hasNext()) {
      return false;
    } else {
      result = resultIterator.next();

      if(result.getType() != Suggestions.RLT_OK) {
        String          replacementWord;

        if(changeAllMap.containsKey(result.getOriginalWord())) {
          replacementWord = (String)changeAllMap.get(result.getOriginalWord());
          replaceWord(replacementWord);
          return checkNext();
        } else if(ignoreAllSet.contains(result.getOriginalWord())) {
          return checkNext();
        } else {
//           replacementWord = spellCheck(result);
//           if(replacementWord == null) {
//             checkedLine = null;
//             break;
//           }
//         }

//         if(replacementWord != null) {
//             replaceWord(checkedDoc,
//                         result.getOriginalWord(),
//                         result.getOffset(),
//                         replacementWord );
          return true;
        }
      } else {
        return checkNext();
      }
    }
  }

  Suggestions getSuggestions() {
    return result;
  }

  void change (String word) {
    if(changeAllMap.containsKey( result.getOriginalWord())) {
      System.err.println( "Change  all: same word: " +
              result.getOriginalWord());
    }
    changeAllMap.put(result.getOriginalWord(),
            word);
    replaceWord(word);
  }

  void changeAll(String word) {
    replaceWord(word);
  }

  void ignore(String word) {
  }

  void ignoreAll(String word) {
    if(ignoreAllSet.contains( result.getOriginalWord())) {
      System.err.println("Ignore all: same word: " +
              result.getOriginalWord());
    }
    ignoreAllSet.add(result.getOriginalWord());
  }


  protected void replaceWord(String replacementWord) {
    String              originalWord;
    int                 originalIndex;

    originalWord = result.getOriginalWord();
    originalIndex = result.getOffset();
    try {
      document.remove(originalIndex-1, originalWord.length());
      document.insertString(originalIndex-1, replacementWord, null);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  private Suggestions           		result;
  private Iterator<Suggestions>              	resultIterator;

  private final Document        		document;
  private final String          		aspell;
  private final Frame           		parent;
  private final HashMap<String, String> 	changeAllMap = new HashMap<String, String>();
  private final HashSet<String>         	ignoreAllSet = new HashSet<String>();

  public static final ImageIcon		ICN_NOTICE = Utils.getImage("notice.gif");
}
