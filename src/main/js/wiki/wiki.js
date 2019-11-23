import React from 'react'; 
import { render } from 'react-dom';
import { Provider } from 'react-redux'; 
import store from '../store/store'; 
import LoginDialog from '../login/LoginDialog';
import WikiRouter from './WikiRouter';

const renderApp = () => {
  render(
    <Provider store={store}>
      <LoginDialog title='Wiki' loginOptional={true}>
        <WikiRouter />
      </LoginDialog>
    </Provider>,
	document.getElementById('react')
  );
};
renderApp();