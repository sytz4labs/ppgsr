import { combineReducers } from 'redux';

import configsReducer from './configsReducer';
import userReducer from '../../reducers/user'

export default combineReducers({
  configsReducer,
  userReducer,
})
