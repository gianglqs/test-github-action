import { takeEvery, put } from "redux-saga/effects"
import { dashboardStore, marginAnalysisStore } from "../reducers"
import { call } from "typed-redux-saga"
import dashboardApi from "@/api/dashboard.api"
import marginAnalysisApi from "@/api/marginAnalysis.api"

function* fetchMarginAnalysis() {
  try {
    const { data } = yield* call(
      marginAnalysisApi.getListMarginAnalysisSummary,
      {
        modelCode: "GP30UX",
        currency: {
          currency: "USD",
        },
        monthYear: "2023-08-01",
      }
    )

    // yield put(dashboardStore.actions.setUserList(JSON.parse(data)?.userList))
  } catch (error) {}
}

function* marginAnalysisSaga() {
  // yield takeEvery(marginAnalysisStore.sagaGetList, fetchMarginAnalysis)
}

export default marginAnalysisSaga
