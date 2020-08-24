/**
@license
Copyright (c) 2019 Vaadin Ltd.
This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
*/
import '@polymer/polymer/lib/elements/dom-module.js';

import { ButtonElement } from '@vaadin/vaadin-button/src/vaadin-button.js';
const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="vaadin-menu-bar-button-styles" theme-for="vaadin-menu-bar-button">
  <template>
    <style>
      [part="label"] ::slotted(vaadin-context-menu-item) {
        position: relative;
        z-index: 1;
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
/**
 * @extends PolymerElement
 */
class MenuBarButtonElement extends ButtonElement {
  static get is() {
    return 'vaadin-menu-bar-button';
  }
}

customElements.define(MenuBarButtonElement.is, MenuBarButtonElement);
