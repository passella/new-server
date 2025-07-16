import http from 'k6/http';
import {check, fail, sleep} from 'k6';
import {uuidv4} from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
    scenarios: {
        ramp_up_down: {
            executor: 'ramping-vus',
            startVUs: 1,
            stages: [
                {target: 1000, duration: '60s'}
            ],
            gracefulRampDown: '5s',
        },
    },
};

export default function () {
    const url = 'http://localhost:8080/payments';
    const correlationId = uuidv4();
    const requestedAt = new Date().toISOString();
    const payload = JSON.stringify({
        correlationId: correlationId,
        amount: 1,
        requestedAt: requestedAt
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Rinha-Token': '123',
        },
    };

    const response = http.post(url, payload, params);
    check(response, {'status is 200': (r) => r.status === 200});

    sleep(0.5);
}