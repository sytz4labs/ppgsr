import React from 'react'; 
import { createRoot } from 'react-dom/client';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import LinkRouter from './LinkRouter';

createRoot(document.getElementById('react'))
  .render(<LoginDialog loginOptional={true}>
            <AppHeader title='Link Farm' home='../wiki' />
            <LinkRouter />
          </LoginDialog>);