import '@vaadin/vaadin-material-styles/typography.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';

const $_documentContainer = html`<dom-module id="material-menu-bar-item" theme-for="vaadin-context-menu-item">
  <template>
    <style>
      :host([theme="menu-bar-item"]) [part="content"] {
        display: flex;
        /* tweak to inherit centering from menu bar button */
        align-items: inherit;
        justify-content: inherit;
        font-size: var(--material-button-font-size);
      }

      :host([theme="menu-bar-item"]) [part="content"] ::slotted(iron-icon[icon^="vaadin:"]) {
        display: inline-block;
        width: 18px;
        height: 18px;
        box-sizing: border-box !important;
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
