import React from 'react'; 
import { render } from 'react-dom';
import { Provider } from 'react-redux'; 
import LoginDialog from '../login/LoginDialog'
import store from '../store/store'; 
import AppHeader from '../components/AppHeader';
import ConfigGrid from './ConfigGrid';

const renderApp = () => {
  render(
    <Provider store={store}>
      <LoginDialog title='Configuration'>
        <AppHeader title='Configuration' home='../wiki' />
			  <ConfigGrid />
      </LoginDialog>
    </Provider>,
	document.getElementById('react')
  );
};
renderApp();