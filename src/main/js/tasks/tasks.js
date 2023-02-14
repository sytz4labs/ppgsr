import React from 'react';
import { createRoot } from 'react-dom/client';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import TasksView from './tasksView'

createRoot(document.getElementById('react'))
  .render(<LoginDialog loginOptional={true}>
              <AppHeader title='Tasks' home='../wiki' />
              <TasksView />
            </LoginDialog>); 