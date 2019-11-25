import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

import LinkGrid from './LinkGrid'

const routes = (
  <div>
    <Switch>
      <Route path="/lf/" exact component={LinkGrid} />
      <Route path="/lf/:pageName" component={LinkGrid} />
      <Route render={() => (<h1>Error</h1>)} />
    </Switch>
  </div>
);

const LinkRouter = () => <Router basename="/">{routes}</Router>;

export default LinkRouter;