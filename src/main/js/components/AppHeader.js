import React from 'react';
import { connect } from "react-redux";
import { readCookie } from  '../lib/s4lib'

class AppHeader extends React.Component{

	constructor(props) {
        super(props);
		this.logoutForm = React.createRef();
	}

	render() {
		const { userName } = this.props.userR;

		return <div>
				<div id="headerText" style={{cursor: "pointer"}} onClick={() => {top.location=this.props.home}}>{this.props.title}</div>
				<div id="headline">&nbsp;</div>
				<div id="headerLogin" style={{position: 'absolute', top: '0px', right: '10px'}}>{userName == null
					? ''
					: <span>{userName} <button onClick={() => {this.logoutForm.current.submit()}}>logout</button>
						<form id='logoutForm' action='/logout' method='post' ref={this.logoutForm}>
							<input type='hidden' name='_csrf' value={readCookie('XSRF-TOKEN')} />
						</form>
					</span>}</div>
			</div>
	}
};

const mapStateToProps = state => {
	return {
		userR: state.userReducer
	}
};

const mapDispatchToProps = dispatch => {
	return {
	}
};

export default connect(mapStateToProps, mapDispatchToProps)(AppHeader);