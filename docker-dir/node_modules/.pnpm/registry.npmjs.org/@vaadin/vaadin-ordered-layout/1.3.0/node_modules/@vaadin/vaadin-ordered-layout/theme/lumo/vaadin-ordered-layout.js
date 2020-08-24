import '@vaadin/vaadin-lumo-styles/spacing.js';
const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="lumo-ordered-layout">
  <template>
    <style>
      :host([theme~="margin"]) {
        margin: var(--lumo-space-m);
      }

      :host([theme~="padding"]) {
        padding: var(--lumo-space-m);
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
