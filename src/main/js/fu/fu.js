import React from 'react'; 
import { createRoot } from 'react-dom/client';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import FileUpload from './FileUpload'

createRoot(document.getElementById('react'))
  .render(<LoginDialog>
            <AppHeader title='FileUpload' home='../wiki' />
            <FileUpload />
          </LoginDialog>);