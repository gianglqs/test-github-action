import { takeEvery, put } from 'redux-saga/effects';
import { adjustmentStore, commonStore } from '../reducers';
import { select, call, all } from 'typed-redux-saga';
import adjustmentApi from '@/api/adjustment.api';

function* fetchAdjustment() {
   try {
      const { tableState } = yield* all({
         tableState: select(commonStore.selectTableState),
      });

      const { defaultValueFilterAdjustment } = yield* all({
         defaultValueFilterAdjustment: select(adjustmentStore.selectDefaultValueFilterAdjustment),
      });
      const { defaultValueCalculator } = yield* all({
         defaultValueCalculator: select(adjustmentStore.selectDefaultValueCalculator),
      });

      const { data } = yield* call(
         adjustmentApi.getAdjustment,
         { dataFilter: defaultValueFilterAdjustment, dataCalculate: defaultValueCalculator },
         {
            pageNo: tableState.pageNo,
            perPage: tableState.perPage,
         }
      );

      const initDataFilter = yield* call(adjustmentApi.getInitDataFilter);

      const dataAdjustment = JSON.parse(String(data)).listAdjustment;
      const dataTotalRow = JSON.parse(String(data)).total;

      yield put(adjustmentStore.actions.setInitDataFilter(JSON.parse(String(initDataFilter.data))));
      yield put(adjustmentStore.actions.setAdjustmentList(dataAdjustment));
      yield put(adjustmentStore.actions.setTotalRow(dataTotalRow));
      yield put(
         commonStore.actions.setTableState({
            totalItems: JSON.parse(String(data)).totalItems,
         })
      );
   } catch (error) {}
}

function* dashboardSaga() {
   yield takeEvery(adjustmentStore.sagaGetList, fetchAdjustment);
}

export default dashboardSaga;
