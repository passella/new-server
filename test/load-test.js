import http from 'k6/http';
import {check, sleep} from 'k6';
import {randomIntBetween, uuidv4} from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';
import {Trend} from 'k6/metrics';

const paymentDuration = new Trend('payment_duration', true);
const queryDuration = new Trend('query_duration', true);

export const options = {
    scenarios: {
        ramp_up_stabilize_down_long: {
            executor: 'ramping-vus',
            startVUs: 1,
            stages: [
                {target: 1000, duration: '30s'},
                {target: 1000, duration: '60s'},
                {target: 0, duration: '30s'},
            ],
            gracefulRampDown: '5s',
        },
    },
    thresholds: {
        'http_req_duration': ['p(95)<500'],
        'http_req_failed': ['rate<0.01'],
    },
};

export default function () {
    const endpoints = [
        {method: 'POST', url: 'http://localhost:8080/payments', type: 'payment'},
    ];
    const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];

    let payload = null;
    let params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Rinha-Token': '123',
        },
    };

    if (endpoint.method === 'POST') {
        const correlationId = uuidv4();
        const requestedAt = new Date().toISOString();
        const amount = Math.random() > 0.5 ? 1 : 1000;
        payload = JSON.stringify({
            correlationId: correlationId,
            amount: amount,
            requestedAt: requestedAt,
        });
    }

    const response = http.request(endpoint.method, endpoint.url, payload, params);

    check(response, {
        'status is expected': (r) => r.status === 200 || r.status === 201,
        'response time acceptable': (r) => r.timings.duration < 500,
    });

    if (endpoint.type === 'payment') {
        paymentDuration.add(response.timings.duration);
    } else if (endpoint.type === 'query') {
        queryDuration.add(response.timings.duration);
    }

    sleep(randomIntBetween(100, 1000) / 1000);
}