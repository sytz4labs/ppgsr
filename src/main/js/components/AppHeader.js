import React from 'react';

export default class AppHeader extends React.Component{

	render() {
		return <div>
            <div id="headerText" style={{cursor: "pointer"}} onClick={() => {top.location=this.props.home}}>{this.props.title}</div>
            <div id="headline">&nbsp;</div>
            <div id="headerLogin" style={{position: 'absolute', top: '0px', right: '10px'}}></div>
			</div>
	}
};
