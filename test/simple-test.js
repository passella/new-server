import http from 'k6/http';
import {check, fail} from 'k6';
import {uuidv4} from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';
import {Trend} from 'k6/metrics';

const paymentDuration = new Trend('payment_duration', true);

export const options = {
    vus: 1,
    iterations: 1,
    thresholds: {
        'http_req_duration': ['p(95)<500'],
        'http_req_failed': ['rate<0.01'],
    },
};

export default function () {
    const url = 'http://localhost:8080/payments';
    const correlationId = uuidv4();
    const requestedAt = new Date().toISOString();
    const amount = 100;

    const payload = JSON.stringify({
        correlationId: correlationId,
        amount: amount,
        requestedAt: requestedAt,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Rinha-Token': '123',
        },
    };

    let response;
    try {
        response = http.post(url, payload, params);
        paymentDuration.add(response.timings.duration);

        const checkResult = check(response, {
            'status is 200 (Created)': (r) => r.status === 200
        });

        if (!checkResult) {
            console.error(`Request failed with status: ${response.status}`);
            console.error(`Response body: ${response.body || 'No response body available'}`);
            console.error(`Request URL: ${url}`);
            console.error(`Request payload: ${payload}`);
            console.error(`Response headers: ${JSON.stringify(response.headers, null, 2)}`);
            console.error(`Timings: ${JSON.stringify(response.timings, null, 2)}`);
            fail(`Check failed: Expected status 200 but got ${response.status}`);
        } else {
            console.info(`Request successful with status: ${response.status}`);
            console.info(`Duration: ${response.timings.duration.toFixed(2)}ms`);
            console.info(`Response body: ${response.body}`);
        }
    } catch (error) {
        console.error(`Unexpected error during request to ${url}: ${error.message}`);
        console.error(`Request payload: ${payload}`);
        if (response) {
            console.error(`Response status: ${response.status || 'N/A'}`);
            console.error(`Response body: ${response.body || 'No response body available'}`);
            console.error(`Response headers: ${JSON.stringify(response.headers || {}, null, 2)}`);
        }
        fail(`Request failed due to unexpected error: ${error.message}`);
    }
}