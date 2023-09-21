import { fork } from "redux-saga/effects"
import dashboardSaga from "./dashboardSaga"
import bookingSaga from "./bookingSaga"

function* rootSaga() {
  yield fork(dashboardSaga)
  yield fork(bookingSaga)
}

export default rootSaga
