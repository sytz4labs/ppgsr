import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

import AppHeader from '../components/AppHeader';
import WikiPage from './WikiPage';

const routes = (
  <div>
    <AppHeader title='PP&G' home='../wiki' />
    <Switch>
      <Route path="/wiki/" exact component={WikiPage} />
      <Route path="/wiki/:pageName" component={WikiPage} />
      <Route render={() => (<h1>Error</h1>)} />
    </Switch>
  </div>
);

const WikiRouter = () => <Router basename="/">{routes}</Router>;

export default WikiRouter;