import { configureStore, Store } from "@reduxjs/toolkit"
import { createWrapper } from "next-redux-wrapper"
import createSagaMiddleware from "redux-saga"

import rootSaga from "@/store/saga/rootSaga"
import rootReducer from "@/store/reducers/rootReducer"

export const makeStore = (): Store => {
  const sagaMiddleware = createSagaMiddleware()

  const store = configureStore({
    reducer: rootReducer,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({ thunk: false, immutableCheck: false }).concat(
        sagaMiddleware
      ),
    // devTools: process.env.NEXT_PUBLIC_MODE === 'develop'
  })

  sagaMiddleware.run(rootSaga)

  return store
}

export const wrapper = createWrapper(makeStore)
