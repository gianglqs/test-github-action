import { takeEvery, put } from 'redux-saga/effects';
import { competitorColorStore, commonStore } from '../reducers';
import { select, call, all } from 'typed-redux-saga';
import competitorColorApi from '@/api/competitorColor.api';

function* fetchCompetitorColor() {
   try {
      const { tableState } = yield* all({
         tableState: select(commonStore.selectTableState),
      });

      const { competitorColorSearch } = yield* all({
         competitorColorSearch: select(competitorColorStore.selectCompetitorColorSearch),
      });

      const listCompetitorColorResponse = yield* call(competitorColorApi.getCompetitorColor, {
         search: competitorColorSearch,
         pageNo: tableState.pageNo,
         perPage: tableState.perPage,
      });
      const dataForTable = JSON.parse(String(listCompetitorColorResponse.data));
      yield put(competitorColorStore.actions.setCompetitorColorList(dataForTable.competitorColors));

      yield put(
         commonStore.actions.setTableState({
            totalItems: dataForTable.totalItems,
         })
      );
   } catch (error) {
      console.log(error);
   }
}

function* competitorColorSaga() {
   yield takeEvery(competitorColorStore.sagaGetList, fetchCompetitorColor);
}
export default competitorColorSaga;
