import React from 'react'; 
import { createRoot } from 'react-dom/client';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import ConfigGrid from './ConfigGrid';

createRoot(document.getElementById('react'))
  .render(<LoginDialog>
        <AppHeader title='Configuration' home='../wiki' />
			  <ConfigGrid />
      </LoginDialog>);