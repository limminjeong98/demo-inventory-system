import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  // A number specifying the number of VUs to run concurrently.
  vus: 1,
  // A string specifying the total duration of the test run.
  duration: '15s',
};

const stockMap = {}

export default function() {
  const data = {
    quantity: 1
  }

  const resp = http.post('http://localhost:8080/api/v1/inventory/1/decrease', JSON.stringify(data), {
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });

  check(resp, {
    'response status should be 200': (r) => r.status === 200,
    'response body should hava item_id "1"': (r) => r.json('data.item_id') === '1',
    'response body should have stock gte 0': (r) => r.json('data.stock') >= 0,
  });

  const stock = resp.json('data.stock')
  if (stockMap[stock]) {
    console.log(`Duplicates found: ${stock}`)
  } else {
    stockMap[stock] = true
  }

  sleep(0.25);
}
