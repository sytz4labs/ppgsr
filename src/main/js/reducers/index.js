import { combineReducers } from 'redux';

import budgetReducer from './budgetReducer';
import configsReducer from './configsReducer';
import linksReducer from './linksReducer';
import userReducer from './user';

export default combineReducers({
  budgetReducer,
  configsReducer,
  linksReducer,
  userReducer,
})
