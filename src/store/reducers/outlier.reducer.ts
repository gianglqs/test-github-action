import { createSlice, PayloadAction, createSelector, createAction } from '@reduxjs/toolkit';

import type { RootReducerType } from './rootReducer';
import { defaultValueFilterOrder } from '@/utils/defaultValues';

export const name = 'outlier';
export const resetState = createAction(`${name}/RESET_STATE`);

export const initialState = {
   outlierList: [] as any[],
   initDataFilter: {} as any,
   defaultValueFilterOutlier: defaultValueFilterOrder as any,
};

const outlierSlice = createSlice({
   name,
   initialState,
   reducers: {
      setOutlierList(state, { payload }: PayloadAction<any[]>) {
         state.outlierList = payload;
      },
      setInitDataFilter(state, { payload }: PayloadAction<any[]>) {
         state.initDataFilter = payload;
      },
      setDefaultValueFilterOutlier(state, { payload }: PayloadAction<Partial<any>>) {
         state.defaultValueFilterOutlier = {
            ...state.defaultValueFilterOutlier,
            ...payload,
         };
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
export const selectOutlierList = createSelector(selectState, (state) => state.outlierList);
export const selectInitDataFilter = createSelector(selectState, (state) => state.initDataFilter);

export const selectDefaultValueFilterOutlier = createSelector(
   selectState,
   (state) => state.defaultValueFilterOutlier
);

export const { actions } = outlierSlice;

export default outlierSlice;
