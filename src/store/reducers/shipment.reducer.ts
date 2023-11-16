import { createSlice, PayloadAction, createSelector, createAction } from '@reduxjs/toolkit';

import type { RootReducerType } from './rootReducer';
import { defaultValueFilterShipment } from '@/utils/defaultValues';

export const name = 'shipment';
export const resetState = createAction(`${name}/RESET_STATE`);

export const initialState = {
   shipmentList: [] as any[],
   initDataFilter: {} as any,
   defaultValueFilterShipment: defaultValueFilterShipment as any,
};

const shipmentSlice = createSlice({
   name,
   initialState,
   reducers: {
      setShipmentList(state, { payload }: PayloadAction<any[]>) {
         state.shipmentList = payload;
      },
      setInitDataFilter(state, { payload }: PayloadAction<any[]>) {
         state.initDataFilter = payload;
      },
      setDefaultValueFilterShipment(state, { payload }: PayloadAction<Partial<any>>) {
         state.defaultValueFilterShipment = {
            ...state.defaultValueFilterShipment,
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
export const selectShipmentList = createSelector(selectState, (state) => state.shipmentList);
export const selectInitDataFilter = createSelector(selectState, (state) => state.initDataFilter);

export const selectDefaultValueFilterShipment = createSelector(
   selectState,
   (state) => state.defaultValueFilterShipment
);

export const { actions } = shipmentSlice;

export default shipmentSlice;
