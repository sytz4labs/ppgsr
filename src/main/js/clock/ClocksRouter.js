import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import Clocks from './Clocks'

const routes = (
    <div>
        <Routes>
            <Route path="/clock/:clockSetId" element={<Clocks/>} />
            <Route render={() => (<h1>Error</h1>)} />
        </Routes>
    </div>
);

const ClocksRouter = () => <Router basename="/">{routes}</Router>;

export default ClocksRouter;