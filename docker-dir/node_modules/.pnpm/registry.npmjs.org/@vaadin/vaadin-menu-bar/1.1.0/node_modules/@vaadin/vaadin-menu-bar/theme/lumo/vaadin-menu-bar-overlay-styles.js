import { html } from '@polymer/polymer/lib/utils/html-tag.js';

const $_documentContainer = html`<dom-module id="lumo-menu-bar-overlay" theme-for="vaadin-context-menu-overlay">
  <template>
    <style>
      :host(:first-of-type) {
        padding-top: var(--lumo-space-xs);
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
