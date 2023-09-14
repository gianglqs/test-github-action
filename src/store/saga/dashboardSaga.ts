import { takeEvery, put } from "redux-saga/effects"
import { dashboardStore } from "../reducers"
import { call } from "typed-redux-saga"
import dashboardApi from "@/api/dashboard.api"

function* fetchUserList() {
  try {
    const { data } = yield* call(dashboardApi.getUser, { search: "" })
    yield put(dashboardStore.actions.setUserList(JSON.parse(data)?.userList))
  } catch (error) {}
}

function* dashboardSaga() {
  yield takeEvery(dashboardStore.sagaGetList, fetchUserList)
}

export default dashboardSaga
