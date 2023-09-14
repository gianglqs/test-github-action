import { takeEvery, put } from "redux-saga/effects"
import { bookingStore, dashboardStore } from "../reducers"
import { call } from "typed-redux-saga"
import bookingApi from "@/api/booking.api"

function* fetchBooking() {
  try {
    const { data } = yield* call(
      bookingApi.getListBookingOrder,
      {
        orderNo: "",
        regions: [],
        dealers: [],
        plants: [],
        metaSeries: [],
        classes: [],
        models: [],
        segments: [],
        fromDate: "",
        toDate: "",
      },
      { pageNo: 1, perPage: 50 }
    )

    const initDataFilter = yield* call(bookingApi.getInitDataFilter)

    yield put(
      bookingStore.actions.setInitDataFilter(
        JSON.parse(String(initDataFilter.data))
      )
    )
    yield put(
      bookingStore.actions.setBookingList(
        JSON.parse(String(data)).bookingOrdersList
      )
    )
  } catch (error) {}
}

function* dashboardSaga() {
  yield takeEvery(bookingStore.sagaGetList, fetchBooking)
}

export default dashboardSaga
