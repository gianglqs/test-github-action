import { takeEvery,put } from 'redux-saga/effects'
import { dashboardStore } from '../reducers'
import { call } from 'typed-redux-saga'
import dashboardApi from '@/api/dashboard.api'


function* getRFOsList() {
    try {
        // const { data } = yield* call(dashboardApi.getUser())
        yield put(dashboardStore.actions.setUserList([1212]))
        
    } catch (error) {
      
    }
    
  }

function* rfosSaga() {
    yield takeEvery(dashboardStore.sagaGetList, getRFOsList)
  }
  
  export default rfosSaga