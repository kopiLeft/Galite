import '@google-web-components/google-chart';
import {PolymerElement, html} from '@polymer/polymer';

export class AreaChart extends PolymerElement {

    static get template() {
        return html`
            <div>
                <google-chart
                    type='area' data='[[userdata]]'
                    options='{"title": "Distribution of days"}'
                    selection="{{chartselection::google-chart-select}}">
                </google-chart>
            </div>`;
    }
}

customElements.define('area-chart', AreaChart);
