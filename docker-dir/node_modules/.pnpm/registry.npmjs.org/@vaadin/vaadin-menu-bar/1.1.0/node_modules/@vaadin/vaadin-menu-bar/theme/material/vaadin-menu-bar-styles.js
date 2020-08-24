import { html } from '@polymer/polymer/lib/utils/html-tag.js';

const $_documentContainer = html`<dom-module id="material-menu-bar" theme-for="vaadin-menu-bar">
  <template>
    <style>
      [part="container"] {
        /* To retain the box-shadow */
        padding-bottom: 5px;
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
