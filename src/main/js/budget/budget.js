import React from 'react'; 
import { render } from 'react-dom';
import { Provider } from 'react-redux'; 
import store from '../store/store'; 
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import Budget from './BudgetView'

const renderApp = () => {
  render(
    <Provider store={store}>
      <LoginDialog title='Budgets'>
        <AppHeader title='Budget' home='../wiki' />
        <Budget />
      </LoginDialog>
    </Provider>,
	document.getElementById('react')
  );
};
renderApp();