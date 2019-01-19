import { combineReducers } from 'redux';

import budgetReducer from './budgetReducer';
import userReducer from '../../reducers/user'

export default combineReducers({
  budgetReducer,
  userReducer,
})
