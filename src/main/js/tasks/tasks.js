import React from 'react'; 
import { render } from 'react-dom';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import TasksView from './tasksView'

const renderApp = () => {
  render(
      <LoginDialog>
        <AppHeader title='Tasks' home='../wiki' />
        <TasksView />
      </LoginDialog>,
	document.getElementById('react')
  );
};
renderApp();