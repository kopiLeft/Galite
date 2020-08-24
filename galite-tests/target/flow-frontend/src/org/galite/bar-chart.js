import '@google-web-components/google-chart';
import {PolymerElement, html} from '@polymer/polymer';

export class BarChart extends PolymerElement {

    static get template() {
        return html`
            <div>
                <google-chart
                    type='bar' data='[[userdata]]'
                    options='{"title": "Distribution of days"}'
                    selection="{{chartselection::google-chart-select}}">
                </google-chart>
            </div>`;
    }
}

customElements.define('bar-chart', BarChart);
