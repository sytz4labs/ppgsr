import React from 'react';
import { createRoot } from 'react-dom/client';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import BudgetView from './BudgetView'

createRoot(document.getElementById('react'))
  .render(<LoginDialog>
            <AppHeader title='Budget' home='../wiki' />
            <BudgetView />
          </LoginDialog>);