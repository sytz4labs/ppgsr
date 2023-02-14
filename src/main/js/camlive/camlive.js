import React from 'react'; 
import { createRoot } from 'react-dom/client';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import CamLiveSocket from './CamLiveSocket';
import CamLiveGrid from './camLiveGrid';

createRoot(document.getElementById('react'))
  .render(<LoginDialog>
                <AppHeader title='Camera Live' home='../wiki' />
                <CamLiveSocket uri='/camlivews'>
                    <CamLiveGrid/>
                </CamLiveSocket>
            </LoginDialog>);