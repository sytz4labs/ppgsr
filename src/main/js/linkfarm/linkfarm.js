import React from 'react'; 
import { render } from 'react-dom';
import { Provider } from 'react-redux'; 
import store from '../store/store'; 
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import LinkRouter from './LinkRouter';

const renderApp = () => {
  render(
    <Provider store={store}>
      <LoginDialog title='Link Farm' loginOptional={true}>
        <AppHeader title='Link Farm' home='../wiki' />
        <LinkRouter />
      </LoginDialog>
    </Provider>,
	document.getElementById('react')
  );
};
renderApp();