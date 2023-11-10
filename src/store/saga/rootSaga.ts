import { fork } from 'redux-saga/effects';
import dashboardSaga from './dashboardSaga';
import bookingSaga from './bookingSaga';
import marginAnalysisSaga from './analysisSaga';
import indicatorSaga from './indicatorSaga';

function* rootSaga() {
   yield fork(dashboardSaga);
   yield fork(bookingSaga);
   yield fork(marginAnalysisSaga);
   yield fork(indicatorSaga);
}

export default rootSaga;
