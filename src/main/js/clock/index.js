import React from 'react'; 
import { render } from 'react-dom';
import 'react-clock/dist/Clock.css';

import 'bootstrap/dist/css/bootstrap.min.css'
import ClocksRouter from './ClocksRouter';

const renderApp = () => {
    render(
        <div style={{backgroundColor: '#222'}}>
            <ClocksRouter />
        </div>,
    document.getElementById('react')
    );
};
renderApp();