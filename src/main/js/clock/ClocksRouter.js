import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

import Clocks from './Clocks'

const routes = (
    <div>
        <Switch>
            <Route path="/clock/:clockSetId" component={Clocks} />
            <Route render={() => (<h1>Error</h1>)} />
        </Switch>
    </div>
);

const ClocksRouter = () => <Router basename="/">{routes}</Router>;

export default ClocksRouter;