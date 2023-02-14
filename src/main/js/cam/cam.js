import React from 'react'; 
import { createRoot } from 'react-dom/client';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import CamGrid from './CamGrid'

createRoot(document.getElementById('react'))
  .render(<LoginDialog>
            <AppHeader title='Camera' home='../wiki' />
            <CamGrid/>
          </LoginDialog>);