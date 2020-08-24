import '@vaadin/vaadin-material-styles/mixins/overlay.js';
import '@vaadin/vaadin-material-styles/shadow.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';

const $_documentContainer = html`<dom-module id="material-dialog" theme-for="vaadin-dialog-overlay">
  <template>
    <style include="material-overlay">
      [part="overlay"] {
        box-shadow: var(--material-shadow-elevation-24dp);
        outline: none;
        max-width: 560px;
        min-width: 280px;
        -webkit-tap-highlight-color: transparent;
      }

      [part="content"] {
        padding: 24px;
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
