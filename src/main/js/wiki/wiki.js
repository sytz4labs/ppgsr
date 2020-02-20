import React from 'react';
import { render } from 'react-dom';
import LoginDialog from '../login/LoginDialog';
import WikiRouter from './WikiRouter';

const renderApp = () => {
  render(
    <LoginDialog loginOptional={true}>
      <WikiRouter />
    </LoginDialog>,
    document.getElementById('react')
  );
};
renderApp();