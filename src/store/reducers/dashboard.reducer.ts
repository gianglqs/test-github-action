import { createSlice, PayloadAction, createSelector, createAsyncThunk, createAction } from '@reduxjs/toolkit'

import type { RootReducerType } from './rootReducer'
import dashboardApi from '@/api/dashboard.api';

export const name = 'dashboard'

export const initialState = {
  userList: [] as any[]
}

const dashboardSlice = createSlice({
  name,
  initialState,
  reducers: {
    setUserList(state, { payload }: PayloadAction<any[]>) {
      state.userList = payload
    }
  }
})

export const sagaGetList = createAction(`${name}/users`)
// Selectors
export const selectState = (state: RootReducerType) => state[name]
export const selectUserList = createSelector(selectState, (state) => state.userList)

export const { actions } = dashboardSlice

export default dashboardSlice
