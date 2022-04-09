import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import LinkGrid from './LinkGrid'

const routes = (
  <div>
    <Routes>
      <Route path="/lf/" exact element={<LinkGrid/>} />
      <Route path="/lf/:pageName" element={<LinkGrid/>} />
      <Route render={() => (<h1>Error</h1>)} />
    </Routes>
  </div>
);

const LinkRouter = () => <Router basename="/">{routes}</Router>;

export default LinkRouter;