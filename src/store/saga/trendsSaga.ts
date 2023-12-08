import { takeEvery, put } from 'redux-saga/effects';
import { trendsStore } from '../reducers';
import { select, call, all } from 'typed-redux-saga';

import trendsApi from '@/api/trends.api';

function* fetchTrends() {
   const { defaultValueFilterTrends } = yield* all({
      defaultValueFilterTrends: select(trendsStore.selectDefaultValueFilterTrends),
   });

   const dataForMarginVsCost = yield* call(trendsApi.getMarginVsCostData, defaultValueFilterTrends);
   const dataForMarginVsDN = yield* call(trendsApi.getMarginVsDNData, defaultValueFilterTrends);

   const marginVsCostData = JSON.parse(String(dataForMarginVsCost.data)).marginVsCostData;
   yield put(trendsStore.actions.setInitDataForMarginVsCost(marginVsCostData));

   const marginVsDNData = JSON.parse(String(dataForMarginVsDN.data)).marginVsDNData;
   yield put(trendsStore.actions.setInitDataForMarginVsDN(marginVsDNData));

   const initDataFilter = yield* call(trendsApi.getInitDataFilter);
   yield put(trendsStore.actions.setInitDataFilter(JSON.parse(initDataFilter.data)));
}

function* dashboardSaga() {
   yield takeEvery(trendsStore.sagaGetList, fetchTrends);
}

export default dashboardSaga;
