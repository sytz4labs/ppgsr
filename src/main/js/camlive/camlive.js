import React from 'react'; 
import { render } from 'react-dom';
import LoginDialog from '../login/LoginDialog'
import AppHeader from '../components/AppHeader';
import CamLiveSocket from './CamLiveSocket';
import CamLiveGrid from './camLiveGrid';

const renderApp = () => {
    render(
        <LoginDialog>
            <AppHeader title='Camera Live' home='../wiki' />
            <CamLiveSocket uri='/camlivews'>
                <CamLiveGrid/>
            </CamLiveSocket>
        </LoginDialog>,
        document.getElementById('react')
    );
};
renderApp();