import { takeEvery, put } from 'redux-saga/effects';
import { marginAnalysisStore } from '../reducers';
import { call } from 'typed-redux-saga';
import marginAnalysisApi from '@/api/marginAnalysis.api';

function* fetchMarginAnalysis() {
   try {
      const { data } = yield* call(marginAnalysisApi.getDealerList);

      yield put(marginAnalysisStore.actions.setDealerList(JSON.parse(data)?.dealers));
   } catch (error) {}
}

function* marginAnalysisSaga() {
   yield takeEvery(marginAnalysisStore.sagaGetList, fetchMarginAnalysis);
}

export default marginAnalysisSaga;
