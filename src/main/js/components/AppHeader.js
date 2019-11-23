import React from 'react';
import { connect } from "react-redux";

import { logout, signIn } from "../login/logoutActions"

class AppHeader extends React.Component{

	signIn() {
		window.location = 'login'
	}

	render() {
		const { loginR, wikiR } = this.props;

		return <div>
				<div id="headerText" style={{cursor: "pointer"}} onClick={() => {top.location=this.props.home}}>{this.props.title}</div>
				<div                 style={{position: 'absolute', top: '18px', left: '120px', fontSize: '16px', lineHeight: '16px'}}><b>{wikiR == null ? '' : wikiR.affirm0}</b><span>{wikiR == null ? '' : wikiR.affirm1}</span></div>

				<div id="headline">&nbsp;</div>
				<div id="headerLogin" style={{position: 'absolute', top: '0px', right: '10px'}}>{loginR.userInfo == null
					? <button onClick={() => {this.props.signIn()}}>Sign In</button>
					: <span>{loginR.userInfo.userId} <button onClick={() => this.props.logout()} >logout</button></span>}
				</div>
			</div>
	}
};

const mapStateToProps = state => {
	return {
		loginR: state.loginReducer,
		wikiR: state.wikiReducer
	}
};

const mapDispatchToProps = dispatch => {
	return {
		logout: () => {
            dispatch(logout());
		},
		signIn: () => {
            dispatch(signIn());
		}
	}
};

export default connect(mapStateToProps, mapDispatchToProps)(AppHeader);