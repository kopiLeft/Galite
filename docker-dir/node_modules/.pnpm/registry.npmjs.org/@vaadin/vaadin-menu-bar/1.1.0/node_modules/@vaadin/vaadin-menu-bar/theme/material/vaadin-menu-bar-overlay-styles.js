import { html } from '@polymer/polymer/lib/utils/html-tag.js';

const $_documentContainer = html`<dom-module id="material-menu-bar-overlay" theme-for="vaadin-context-menu-overlay">
  <template>
    <style>
      :host(:first-of-type) {
        padding-top: 5px;
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
