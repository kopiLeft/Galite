const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="material-ordered-layout">
  <template>
    <style>
      [theme~="margin"] {
        margin: 16px;
      }

      [theme~="padding"] {
        padding: 16px;
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
