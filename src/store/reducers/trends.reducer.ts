import { createSlice, PayloadAction, createSelector, createAction } from '@reduxjs/toolkit';

import type { RootReducerType } from './rootReducer';
import { defaultValueFilterTrends } from '@/utils/defaultValues';

export const name = 'trends';
export const resetState = createAction(`${name}/RESET_STATE`);

export const initialState = {
   initDataFilter: {} as any,
   defaultValueFilterTrends: defaultValueFilterTrends as any,
   initDataForMarginVsCost: [] as any[],
   initDataForMarginVsDN: [] as any[],
};

const trendsSlice = createSlice({
   name,
   initialState,
   reducers: {
      setInitDataFilter(state, { payload }: PayloadAction<any[]>) {
         state.initDataFilter = payload;
      },

      setDefaultValueFilterTrends(state, { payload }: PayloadAction<Partial<any>>) {
         state.defaultValueFilterTrends = {
            ...state.defaultValueFilterTrends,
            ...payload,
         };
      },

      setInitDataForMarginVsCost(state, { payload }: PayloadAction<any[]>) {
         state.initDataForMarginVsCost = payload;
      },
      setInitDataForMarginVsDN(state, { payload }: PayloadAction<any[]>) {
         state.initDataForMarginVsDN = payload;
      },
   },
   extraReducers: {
      [resetState.type]() {
         return initialState;
      },
   },
});

export const sagaGetList = createAction(`${name}/GET_LIST`);

export const selectState = (state: RootReducerType) => state[name];
export const selectInitDataFilter = createSelector(selectState, (state) => state.initDataFilter);
export const selectDefaultValueFilterTrends = createSelector(
   selectState,
   (state) => state.defaultValueFilterTrends
);
export const selectDataForMarginVsCost = createSelector(
   selectState,
   (state) => state.initDataForMarginVsCost
);
export const selectDataForMarginVsDN = createSelector(
   selectState,
   (state) => state.initDataForMarginVsDN
);

export const { actions } = trendsSlice;
export default trendsSlice;
