/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

import { WysiwygE } from 'wysiwyg-e-fork/wysiwyg-e.js';

/**
 * This text editor class extends WysiwygE component.
 */
export class RichText extends WysiwygE {
  connectedCallback() {
    if (!this._pasteHandler) {
      this._pasteHandler = function (event) {
      	event.preventDefault();
      	var data = event.clipboardData.getData('text/html');
      	if (!data.length) {
      	  // If paste does not contain HTML, fall back to plain text
      	  data = event.clipboardData.getData('text');
      	  document.execCommand('insertText', false, data);
      	} else {
      	  document.execCommand('insertHTML', false, data);
      	}
      }.bind(this);
    }

    this.addEventListener('paste', this._pasteHandler);

    super.connectedCallback();
  }
}

customElements.define('wysiwyg-e-rich-text', RichText);