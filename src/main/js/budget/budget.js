import React from 'react'; 
import { render } from 'react-dom';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import BudgetView from './BudgetView'

const renderApp = () => {
  render(
      <LoginDialog>
        <AppHeader title='Budget' home='../wiki' />
        <BudgetView />
      </LoginDialog>,
	document.getElementById('react')
  );
};
renderApp();