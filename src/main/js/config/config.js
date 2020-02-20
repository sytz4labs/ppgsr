import React from 'react'; 
import { render } from 'react-dom';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import ConfigGrid from './ConfigGrid';

const renderApp = () => {
  render(
      <LoginDialog>
        <AppHeader title='Configuration' home='../wiki' />
			  <ConfigGrid />
      </LoginDialog>,
	document.getElementById('react')
  );
};
renderApp();