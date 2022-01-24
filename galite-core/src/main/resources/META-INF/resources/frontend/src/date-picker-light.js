/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
import {LitElement, html} from 'lit-element';
import '@vaadin/vaadin-date-picker/vaadin-date-picker-light.js';
import '@vaadin/vaadin-text-field/vaadin-text-field.js';
import '@vaadin/vaadin-lumo-styles/font-icons.js';
import { DatePickerLightElement } from '@vaadin/vaadin-date-picker/vaadin-date-picker-light.js';

class DatePickerLight extends DatePickerLightElement {
    constructor() {
        super();

        this.i18n.formatDate = (d) => {
          var day = d.day
          var month = d.month + 1

          if(day < 10) {
            day = "0" + day
          }

          if(month < 10) {
            month = "0" + month
          }

          const yearStr = String(d.year).replace(/\d+/, (y) => '0000'.substr(y.length) + y);
          return [day, month, yearStr].join('.');
        }

        this.i18n.parseDate = (text) => {
           const parts = text.split((/[./-]+/));
           const today = new Date();
           let date,
             month = today.getMonth(),
             year = today.getFullYear();

           if (parts.length === 3) {
             year = parseInt(parts[2]);
             if (parts[2].length < 3 && year >= 0) {
               year += year < 50 ? 2000 : 1900;
             }
             month = parseInt(parts[1]) - 1;
             date = parseInt(parts[0]);
           } else if (parts.length === 2) {
             month = parseInt(parts[1]) - 1;
             date = parseInt(parts[0]);
           } else if (parts.length === 1) {
             date = parseInt(parts[0]);
           }

           if (date !== undefined) {
             return { day: date, month, year };
           }
        }
    }
}

customElements.define('date-picker-light', DatePickerLight);
