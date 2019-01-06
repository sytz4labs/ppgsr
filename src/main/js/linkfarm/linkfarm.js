import React from 'react'; 
import { render } from 'react-dom';
import { Provider } from 'react-redux'; 
import store from './store/store'; 
import AppHeader from '../components/AppHeader';
import RootRouter from './routes/RootRouter';

const renderApp = () => {
  render(
    <Provider store={store}>
      <AppHeader title='Link Farm' home='../wiki' />
			<RootRouter />
    </Provider>,
	document.getElementById('react')
  );
};
renderApp();