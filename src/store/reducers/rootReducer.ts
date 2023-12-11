import { combineReducers } from 'redux';
import dashboard from './dashboard.reducer';
import common from './common.reducer';
import booking from './booking.reducer';
import marginAnalysis from './analysis.reducer';
import indicator from './indicator.reducer';
import shipment from './shipment.reducer';
import outlier from './outlier.reducer';
import trends from './trends.reducer';
import adjustment from './adjustment.reducer';
import competitorColor from './competitorColor.reducer';

const rootReducers = combineReducers({
   [common.name]: common.reducer,
   [dashboard.name]: dashboard.reducer,
   [booking.name]: booking.reducer,
   [marginAnalysis.name]: marginAnalysis.reducer,
   [indicator.name]: indicator.reducer,
   [shipment.name]: shipment.reducer,
   [outlier.name]: outlier.reducer,
   [trends.name]: trends.reducer,
   [adjustment.name]: adjustment.reducer,
   [competitorColor.name]: competitorColor.reducer,
});

export type RootReducerType = ReturnType<typeof rootReducers>;

export default rootReducers;
