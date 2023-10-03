import {
  createSlice,
  PayloadAction,
  createSelector,
  createAction,
} from "@reduxjs/toolkit"

import type { RootReducerType } from "./rootReducer"
import { defaultValueFilterBooking } from "@/utils/defaultValues"

export const name = "margin_analysis"
export const resetState = createAction(`${name}/RESET_STATE`)

export const initialState = {
  marginAnalystData: [] as any[],
}

const marginAnalysisSlice = createSlice({
  name,
  initialState,
  reducers: {
    setMarginAnalystData(state, { payload }: PayloadAction<any[]>) {
      state.marginAnalystData = payload
    },
  },
  extraReducers: {
    [resetState.type]() {
      return initialState
    },
  },
})

export const sagaGetList = createAction(`${name}/GET_LIST`)
// Selectors
export const selectState = (state: RootReducerType) => state[name]
export const selectMarginAnalystData = createSelector(
  selectState,
  (state) => state.marginAnalystData
)

export const { actions } = marginAnalysisSlice

export default marginAnalysisSlice
