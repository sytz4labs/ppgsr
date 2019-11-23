import { combineReducers } from 'redux';

import loginReducer from '../login/loginReducer'
import budgetReducer from './budgetReducer';
import configsReducer from './configsReducer';
import linksReducer from './linksReducer';
import wikiReducer from './wikiReducer';

export default combineReducers({
  loginReducer,
  budgetReducer,
  configsReducer,
  linksReducer,
  wikiReducer,
})
