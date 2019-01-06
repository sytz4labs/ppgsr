import { combineReducers } from 'redux';

import linksReducer from './linksReducer';
import userReducer from '../../reducers/user'

export default combineReducers({
  linksReducer,
  userReducer,
})
