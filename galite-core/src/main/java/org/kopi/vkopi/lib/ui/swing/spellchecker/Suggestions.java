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
 * $Id: Suggestions.java 34961 2016-11-04 17:20:49Z hacheni $
 */

package org.kopi.vkopi.lib.ui.swing.spellchecker;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class Suggestions {
  public Suggestions(String line) {
    if(line == null || line.length() <= 0) {
      processError(line);
    } else if(line.charAt(0) == '*') {
      processOk(line);
    } else if(line.charAt(0) == '&') {
      processSuggestion( line );
    } else if(line.charAt(0) == '#') {
      processNone(line);
    } else {
      processError(line);
    }
  }

  public int getOffset() {
    return offset;
  }

  public String getType() {
    return type;
  }

  public List<String> getSuggestions() {
    return suggestions;
  }

  public String getOriginalWord() {
    return originalWord;
  }

  public String toString() {
    StringBuffer        buff = new StringBuffer();

    buff.append("[type:");
    buff.append(type );
    buff.append(",originalWord:");
    buff.append(originalWord);
    buff.append(",offset:");
    buff.append(offset);
    buff.append(",suggestions:");
    buff.append(suggestions);
    return buff.toString();
  }

  private void processError(String line) {
    offset = 0;
    type = RLT_ERROR;
    suggestions = new ArrayList<String>();
    originalWord = "";
  }

  private void processOk(String line) {
    offset = 0;
    type = RLT_OK;
    suggestions = new ArrayList<String>();
    originalWord = "";
  }

  private void processNone(String line) {
    StringTokenizer     st = new StringTokenizer(line);

    type = RLT_NONE;
    suggestions = new ArrayList<String>();

    st.nextToken(); // skip '#'
    originalWord = st.nextToken();
    offset = Integer.parseInt( st.nextToken() );
  }

  private void processSuggestion(String line) {
    StringTokenizer     st = new StringTokenizer(line);

    type = RLT_SUGGESTION;
    st.nextToken(); // skip '#'
    originalWord = st.nextToken();

    int                 count = Integer.parseInt(st.nextToken().trim());

    suggestions = new ArrayList<String>(count);
    offset = Integer.parseInt(st.nextToken(":").trim());

    st = new StringTokenizer(st.nextToken(":"), ",");
    while(st.hasMoreTokens()) {
      suggestions.add(st.nextToken().trim());
    }
  }

  private  int     		offset;
  private  String  		type;
  private  List<String>    	suggestions;
  private  String  		originalWord;

  public static final String RLT_ERROR      = new String( "Error" );
  public static final String RLT_OK         = new String( "OK " );
  public static final String RLT_NONE       = new String( "None" );
  public static final String RLT_SUGGESTION = new String( "Suggestion" );
}
