import '@vaadin/vaadin-button/theme/material/vaadin-button-styles.js';
import '@vaadin/vaadin-material-styles/color.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';

const $_documentContainer = html`<dom-module id="material-drawer-toggle" theme-for="vaadin-drawer-toggle">
  <template>
    <style include="material-button">
      :host {
        min-width: 0 !important;
        width: 48px;
        height: 48px;
        padding: 0;
        border-radius: 50%;
      }

      :host(:not([dir="rtl"])) {
        margin-right: 1em;
      }

      :host([dir="rtl"]) {
        margin-left: 1em;
      }

      [part="icon"],
      [part="icon"]::after,
      [part="icon"]::before {
        background-color: currentColor;
      }

      [part='icon'] {
        top: 18px;
        left: 15px;
      }

      [part='icon'],
      [part='icon']::after,
      [part='icon']::before {
        height: 2px;
        width: 18px;
      }

      [part='icon']::after {
        top: 5px;
      }

      [part='icon']::before {
        top: 10px;
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
