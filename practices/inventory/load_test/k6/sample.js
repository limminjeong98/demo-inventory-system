import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  // A number specifying the number of VUs to run concurrently.
  /* virtual users. JMeter에서 number of threads */
  vus: 10,
  // A string specifying the total duration of the test run.
  /* JMeter에서 constant timer */
  // duration: '30s',
  duration: '10s',
  // The following section contains configuration options for execution of this
  // test script in Grafana Cloud.
  //
  // See https://grafana.com/docs/grafana-cloud/k6/get-started/run-cloud-tests-from-the-cli/
  // to learn about authoring and running k6 test scripts in Grafana k6 Cloud.
  //
  // cloud: {
  //   // The ID of the project to which the test is assigned in the k6 Cloud UI.
  //   // By default tests are executed in default project.
  //   projectID: "",
  //   // The name of the test in the k6 Cloud UI.
  //   // Test runs with the same name will be grouped.
  //   name: "sample.js"
  // },

  // Uncomment this section to enable the use of Browser API in your tests.
  //
  // See https://grafana.com/docs/k6/latest/using-k6-browser/running-browser-tests/ to learn more
  // about using Browser API in your test scripts.
  //
  // scenarios: {
  //   // The scenario name appears in the result summary, tags, and so on.
  //   // You can give the scenario any name, as long as each name in the script is unique.
  //   ui: {
  //     // Executor is a mandatory parameter for browser-based tests.
  //     // Shared iterations in this case tells k6 to reuse VUs to execute iterations.
  //     //
  //     // See https://grafana.com/docs/k6/latest/using-k6/scenarios/executors/ for other executor types.
  //     executor: 'shared-iterations',
  //     options: {
  //       browser: {
  //         // This is a mandatory parameter that instructs k6 to launch and
  //         // connect to a chromium-based browser, and use it to run UI-based
  //         // tests.
  //         type: 'chromium',
  //       },
  //     },
  //   },
  // }
};

// The function that defines VU logic.
//
// See https://grafana.com/docs/k6/latest/examples/get-started-with-k6/ to learn more
// about authoring k6 scripts.
//
export default function() {
  http.get('https://test.k6.io'); // http_req_duration
  sleep(0.25); // 4rps와 비슷
  // sleep(1); // 1rps와 비슷
}

// 1(1/sleep) rps * 10(vus) * 30s(duration) = 300번만큼 요청이 발생함

// sleep: 0.25, vus: 10, duration: 10s 으로 테스트할 경우
// 4(1/0.25) rps * 10 * 10 = 400
// 이론상으로는 10초동안 400번의 요청이 발생해야하지만 http.get('') 이 부분이 동기적으로 동작하기 때문에
// http_req_duration에 해당하는 시간이 매번 발생하고, 400번보다 적게 요청이 나갈 수 밖에 없음

// $ k6 new sample.js
// $ k6 run sample.js
// http_reqs: 전체 요청 수
// http_req_failed: 전체 실패 수와 퍼센티지
// http_req_duration: 요청을 받는데 걸린 시간. http_req_sending + http_req_waiting + http_req_receiving
// iterations: 전체 반복 수
// vus: 요청을 보낸 가상 사용자 수