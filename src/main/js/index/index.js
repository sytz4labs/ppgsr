import React from 'react'; 
import ReactDOM from 'react-dom';

class HelloMessage extends React.Component {
	render() {
	  return (
			<div>
				Hello a i {this.props.name}
			</div>
	  );
	}
  }
  
ReactDOM.render(
	<HelloMessage name="Index" />, document.getElementById('react')
);