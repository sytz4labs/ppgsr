import React from 'react';
import { connect } from "react-redux"

import logout from "logoutActions"

class Logout extends React.Component{

    componentWillMount() {
		this.props.logout();
    }


    render() {
		return <h1>Logged off</h1>
	}
}

const mapStateToProps = state => {
	return {
		loginR: state.loginReducer
	}
};

const mapDispatchToProps = dispatch => {
	return {
		logout: () => {
            dispatch(logout());
		}
	}
};

export default connect(mapStateToProps, mapDispatchToProps)(Logout);
