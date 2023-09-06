import { fork } from "redux-saga/effects";
import dashboardSaga from "./dashboardSaga";

function* rootSaga() {
  yield fork(dashboardSaga);
}

export default rootSaga;
