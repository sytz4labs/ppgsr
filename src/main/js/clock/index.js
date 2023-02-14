import React from 'react'; 
import { createRoot } from 'react-dom/client';
import 'react-clock/dist/Clock.css';

import 'bootstrap/dist/css/bootstrap.min.css'
import ClocksRouter from './ClocksRouter';

createRoot(document.getElementById('react'))
  .render(<div style={{backgroundColor: '#222'}}>
            <ClocksRouter />
        </div>);