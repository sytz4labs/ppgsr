import React from 'react'; 
import { render } from 'react-dom';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import LinkRouter from './LinkRouter';

const renderApp = () => {
  render(
      <LoginDialog title='Link Farm' loginOptional={true}>
        <AppHeader title='Link Farm' home='../wiki' />
        <LinkRouter />
      </LoginDialog>,
	document.getElementById('react')
  );
};
renderApp();