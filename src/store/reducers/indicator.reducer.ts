import { createSlice, PayloadAction, createSelector, createAction } from '@reduxjs/toolkit';

import type { RootReducerType } from './rootReducer';
import { defaultValueFilterIndicator } from '@/utils/defaultValues';

export const name = 'indicator';
export const resetState = createAction(`${name}/RESET_STATE`);

export const initialState = {
   indicatorList: [] as any[],
   initDataFilter: {} as any,
   defaultValueFilterIndicator: defaultValueFilterIndicator as any,
   initDataForLineChartPlant: [] as any[],
   initDataForLineChartRegion: [] as any[],
};

const indicatorSlice = createSlice({
   name,
   initialState,
   reducers: {
      setIndicatorList(state, { payload }: PayloadAction<any[]>) {
         state.indicatorList = payload;
      },
      setInitDataFilter(state, { payload }: PayloadAction<any[]>) {
         state.initDataFilter = payload;
      },
      setDefaultValueFilterIndicator(state, { payload }: PayloadAction<Partial<any>>) {
         state.defaultValueFilterIndicator = {
            ...state.defaultValueFilterIndicator,
            ...payload,
         };
      },
      setInitDataForLineChartPlant(state, { payload }: PayloadAction<any[]>) {
         state.initDataForLineChartPlant = payload;
      },
      setInitDataForLineChartRegion(state, { payload }: PayloadAction<any[]>) {
         state.initDataForLineChartRegion = payload;
      },
   },
   extraReducers: {
      [resetState.type]() {
         return initialState;
      },
   },
});

export const sagaGetList = createAction(`${name}/GET_LIST`);
// Selectors
export const selectState = (state: RootReducerType) => state[name];
export const selectIndicatorList = createSelector(selectState, (state) => state.indicatorList);
export const selectInitDataFilter = createSelector(selectState, (state) => state.initDataFilter);
export const selectDataForLineChartRegion = createSelector(
   selectState,
   (state) => state.initDataForLineChartRegion
);
export const selectDataForLineChartPLant = createSelector(
   selectState,
   (state) => state.initDataForLineChartPlant
);

export const selectDefaultValueFilterIndicator = createSelector(
   selectState,
   (state) => state.defaultValueFilterIndicator
);

export const { actions } = indicatorSlice;

export default indicatorSlice;
