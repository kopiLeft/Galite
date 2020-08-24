import { html } from '@polymer/polymer/lib/utils/html-tag.js';

const $_documentContainer = html`<dom-module id="lumo-login-form" theme-for="vaadin-login-form">
  <template>
    <style>
      vaadin-button[part="vaadin-login-submit"] {
        margin-top: var(--lumo-space-l);
        margin-bottom: var(--lumo-space-s);
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
