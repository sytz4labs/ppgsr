import React from 'react'; 
import { render } from 'react-dom';
import { Provider } from 'react-redux'; 
import store from '../store/store'; 
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import CamGrid from './CamGrid'

const renderApp = () => {
  render(
    <Provider store={store}>
      <LoginDialog title='Camera'>
        <AppHeader title='Camera' home='../wiki' />
        <CamGrid/>
      </LoginDialog>
    </Provider>,
	document.getElementById('react')
  );
};
renderApp();