import { combineReducers } from "redux"
import dashboard from "./dashboard.reducer"

const rootReducers = combineReducers({
  [dashboard.name]: dashboard.reducer,
})

export type RootReducerType = ReturnType<typeof rootReducers>

export default rootReducers
