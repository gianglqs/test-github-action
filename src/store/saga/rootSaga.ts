import { fork } from "redux-saga/effects"
import dashboardSaga from "./dashboardSaga"
import bookingSaga from "./bookingSaga"
import marginAnalysisSaga from "./analysisSaga"

function* rootSaga() {
  yield fork(dashboardSaga)
  yield fork(bookingSaga)
  yield fork(marginAnalysisSaga)
}

export default rootSaga
