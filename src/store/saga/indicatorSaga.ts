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
      // get data for Line Chart Region
      const dataForLineChartRegion = yield* call(
         indicatorApi.getDataLineChartRegion,
         defaultValueFilterIndicator
      );
      const lineChartRegionData = JSON.parse(String(dataForLineChartRegion.data)).lineChartRegion;
      yield put(indicatorStore.actions.setInitDataForLineChartRegion(lineChartRegionData));

      // get data for Line Chart Plant
      const dataForLineChartPlant = yield* call(
         indicatorApi.getDataLineChartPlant,
         defaultValueFilterIndicator
      );
      const lineChartPlantData = JSON.parse(String(dataForLineChartPlant.data)).lineChartPlant;
      yield put(indicatorStore.actions.setInitDataForLineChartPlant(lineChartPlantData));

      // get data for filter
      const initDataFilter = yield* call(indicatorApi.getInitDataFilter);
      yield put(indicatorStore.actions.setInitDataFilter(JSON.parse(initDataFilter.data)));

      // get data for table
      const dataListIndicator = yield* call(
         indicatorApi.getIndicators,
         defaultValueFilterIndicator,
         {
            pageNo: tableState.pageNo,
            perPage: tableState.perPage,
         }
      );

      const dataListIndicatorObject = JSON.parse(String(dataListIndicator.data)).listCompetitor;
      yield put(indicatorStore.actions.setIndicatorList(dataListIndicatorObject));

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
