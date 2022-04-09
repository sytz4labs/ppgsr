import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import AppHeader from '../components/AppHeader';
import WikiPage from './WikiPage';

export default function WikiRouter(props) {
    return <Router basename="/">
                <AppHeader title='PP&G' home='../wiki'>
                    <Routes>
                        <Route path="/wiki/" exact element={<WikiPage/>} />
                        <Route path="/wiki/:pageName" element={<WikiPage/>} />
                        <Route render={() => (<h1>Error</h1>)} />
                    </Routes>
                </AppHeader>
        </Router>
}
