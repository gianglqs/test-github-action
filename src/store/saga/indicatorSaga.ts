import { takeEvery, put } from 'redux-saga/effects';
import { indicatorStore, commonStore } from '../reducers';
import { select, call, all } from 'typed-redux-saga';
import indicatorApi from '@/api/indicators.api';

function* fetchIndicator() {
   try {
      const { tableState } = yield* all({
         tableState: select(commonStore.selectTableState),
      });

      const { defaultValueFilterIndicator } = yield* all({
         defaultValueFilterIndicator: select(indicatorStore.selectDefaultValueFilterIndicator),
      });

      const dataForLineChartRegion = yield* call(
         indicatorApi.getDataLineChartRegion,
         defaultValueFilterIndicator
      );

      const dataForLineChartPlant = yield* call(
         indicatorApi.getDataLineChartRegion,
         defaultValueFilterIndicator
      );

      console.log(dataForLineChartRegion);

      const initDataFilter = yield* call(indicatorApi.getInitDataFilter);

      yield put(indicatorStore.actions.setInitDataFilter(initDataFilter.data));

      // get data for Line Chart Region
      const initDataForLineChartRegion = yield* call(indicatorApi.getDataLineChartRegion);

      const lineChartRegionData = JSON.parse(String(initDataForLineChartRegion)).lineChartRegion;

      yield put(indicatorStore.actions.setInitDataForLineChartRegion(lineChartRegionData));

      // get data for Line Chart Plant
      const initDataForLineChartPLant = yield* call(indicatorApi.getDataLineChartPlant);

      const lineChartPlantData = JSON.parse(String(initDataForLineChartPLant)).lineChartPlant;

      yield put(indicatorStore.actions.setInitDataForLineChartPlant(lineChartPlantData));

      yield put(
         commonStore.actions.setTableState({
            totalItems: JSON.parse(String()).totalItems,
         })
      );
   } catch (error) {}
}

function* dashboardSaga() {
   yield takeEvery(indicatorStore.sagaGetList, fetchIndicator);
}

export default dashboardSaga;
