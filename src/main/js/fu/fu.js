import React from 'react'; 
import { render } from 'react-dom';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import FileUpload from './FileUpload'

const renderApp = () => {
  render(
      <LoginDialog>
        <AppHeader title='FileUpload' home='../wiki' />
        <FileUpload />
      </LoginDialog>,
	document.getElementById('react')
  );
};
renderApp();