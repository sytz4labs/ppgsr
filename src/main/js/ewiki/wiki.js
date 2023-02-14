import React from 'react';
import { createRoot } from 'react-dom/client';
import LoginDialog from '../login/LoginDialog';
import WikiRouter from './WikiRouter';

createRoot(document.getElementById('react'))
  .render(<LoginDialog loginOptional={true}>
      <WikiRouter />
    </LoginDialog>);