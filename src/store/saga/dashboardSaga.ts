import { takeEvery, put } from "redux-saga/effects"
import { dashboardStore } from "../reducers"
import { call } from "typed-redux-saga"
import dashboardApi from "@/api/dashboard.api"
import { useEffect } from "react"
import axios from "axios"
import nookies from "nookies"

function* fetchUserList() {
  try {
    const { data } = yield* call(dashboardApi.getUser)
    yield put(dashboardStore.actions.setUserList(JSON.parse(data)?.userList))
  } catch (error) {
    console.log("error")
  }
}

function* dashboardSaga() {
  yield takeEvery(dashboardStore.sagaGetList, fetchUserList)
}

export default dashboardSaga
