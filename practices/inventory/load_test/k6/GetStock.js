import http from 'k6/http';
import {check, sleep} from 'k6';

const maxVus = 100
export const options = {
    // A number specifying the number of VUs to run concurrently.
    // vus: 10,
    // A string specifying the total duration of the test run.
    // duration: '15s',
    scenarios: {
        getStock: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                {duration: '5s', target: maxVus},
                {duration: '10s', target: maxVus},
                {duration: '5s', target: 0},
            ],
        }
    }
};

export default function () {
    const resp = http.get('http://localhost:8080/api/v1/inventory/1');

    check(resp, {
        'response status should be 200': (r) => r.status === 200,
        'response body should hava item_id "1"': (r) => r.json('data.item_id') === '1',
        'response body should have stock gte 0': (r) => r.json('data.stock') >= 0,
    });

    sleep(0.25); // 250ms
}
