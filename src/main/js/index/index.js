import React from 'react'; 
import ReactDOM from 'react-dom';

class HelloMessage extends React.Component {
	render() {
	  return (
			<div>
				Nothing happens here
			</div>
	  );
	}
  }
  
ReactDOM.render(
	<HelloMessage />, document.getElementById('react')
);