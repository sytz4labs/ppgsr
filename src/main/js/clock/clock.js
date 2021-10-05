import React from 'react'; 
import { render } from 'react-dom';
import 'react-clock/dist/Clock.css';


import Clocks from './Clocks'

const renderApp = () => {
  render(
      <div style={{backgroundColor: 'gray'}}>
        <Clocks />
      </div>,
	document.getElementById('react')
  );
};
renderApp();