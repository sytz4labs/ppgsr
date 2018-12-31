import React from 'react'; 
import { render } from 'react-dom';
import { Provider } from 'react-redux'; 
import store from './store/store'; 
import AppHeader from '../components/AppHeader';
import ConfigGrid from './components/ConfigGrid';

const renderApp = () => {
  render(
    <Provider store={store}>
      <AppHeader title='Configuration' home='../wiki' />
			<ConfigGrid />
    </Provider>,
	document.getElementById('react')
  );
};
renderApp();