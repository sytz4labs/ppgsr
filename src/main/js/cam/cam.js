import React from 'react'; 
import { render } from 'react-dom';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import CamGrid from './CamGrid'

const renderApp = () => {
  render(
      <LoginDialog>
        <AppHeader title='Camera' home='../wiki' />
        <CamGrid/>
      </LoginDialog>,
	document.getElementById('react')
  );
};
renderApp();