import { fork } from 'redux-saga/effects';
import dashboardSaga from './dashboardSaga';
import bookingSaga from './bookingSaga';
import marginAnalysisSaga from './analysisSaga';
import indicatorSaga from './indicatorSaga';
import shipmentSaga from './shipmentSaga';
import outlierSaga from './outlierSaga';

function* rootSaga() {
   yield fork(dashboardSaga);
   yield fork(bookingSaga);
   yield fork(marginAnalysisSaga);
   yield fork(indicatorSaga);
   yield fork(shipmentSaga);
   yield fork(outlierSaga);
}

export default rootSaga;