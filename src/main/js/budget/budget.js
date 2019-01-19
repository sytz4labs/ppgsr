import React from 'react'; 
import { render } from 'react-dom';
import { Provider } from 'react-redux'; 
import store from './store/store'; 
import AppHeader from '../components/AppHeader';
import Budget from './components/Budget'

const renderApp = () => {
  render(
    <Provider store={store}>
      <AppHeader title='Budget' home='../wiki' />
			<Budget />
    </Provider>,
	document.getElementById('react')
  );
};
renderApp();