import React from 'react';
import { connect } from "react-redux"

import axios from "axios";

// properties
// loginOptional = boolean

class LoginDialog extends React.Component{

    constructor(props) {
        super(props)
		this.usernameInput = React.createRef();
        this.passwordInput = React.createRef();
        axios.interceptors.response.use(function (response) {
            return response;
        }, function (error) {
            if (error.response.status == 401) {
                var url = error.request.responseURL;
                var ss = url.indexOf("//");
                var s = url.indexOf("/", ss + 2);
                if (url.substring(s) != "/login") {
                    props.asyncFailed();
                }
            }

            return Promise.reject(error);
        });
    }

    componentDidMount() {
        this.props.getUserInfo();
    }

	componentDidUpdate() {
		if (this.showLogin()) {
			this.usernameInput.current.focus();
		}
    }
    
    showLogin() {
        if (this.props.loginR.userInfo == null) {
            if (this.props.loginOptional) {
                return this.props.loginR.userInfoRequested
            }
            else {
                return this.props.loginR.userInfoCalled;
            }
        }
        else {
            return false;
        }
    }

    render() {
        const {	userInfo, loginFailed, userInfoCalled } = this.props.loginR;

		return <div>
                {this.showLogin()
                    ?   <span>
                            <div style={{position: 'absolute', background: '#fff', zIndex: 200, border: '2px solid #37b', top: '50%', left: '50%', transform: 'translate(-50%, -50%)' /*margin: '180px', padding: '20px', width: '400px', height: '150px'*/}}>
                                <table style={{margin: '25px', width: '250px'}}>
                                    <tbody>
                                        <tr>
                                            <td colSpan='2'>PPGS</td>
                                        </tr>
                                        {loginFailed > 0 &&
                                            <tr><td colSpan='2' style={{color: 'red'}}>Login failed {loginFailed}</td></tr>
                                        }
                                        <tr>
                                            <td>User:</td>
                                            <td><input style={{padding: '5px', margin: '10px', border: '1px solid #000'}} ref={this.usernameInput} onKeyDown={(e) => this.props.keyDown(e, this)} type='text' name='username'/></td>
                                        </tr>
                                        <tr>
                                            <td>Password:</td>
                                            <td><input style={{padding: '5px', margin: '10px', border: '1px solid #000'}} ref={this.passwordInput} onKeyDown={(e) => this.props.keyDown(e, this)} type='password' name='password' /></td>
                                        </tr>
                                        <tr>
                                            <td colSpan='2'><button onClick={() => this.props.login(this)}>Login</button>
                                            {this.props.loginOptional && <button onClick={() => this.props.signInCancel()}>Cancel</button>}
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div style={{position: 'fixed', zIndex: 99, opacity: .3, background: '#aac', top: 0, left: 0, width: '100%', height: '100%'}}></div>
                        </span>
                    : <div> {this.props.children} </div>
                }
			</div>
	}
}

const mapStateToProps = state => {
	return {
		loginR: state.loginReducer
	}
};

const mapDispatchToProps = dispatch => {
	return {
        getUserInfo: () => {
            axios.get("/userInfo")
                .then(function (response) {
                    dispatch({
                        type: 'USER_INFO_SUCCESSFUL',
                        payload: response.data
                    });
                })
                .catch(function(error) {
                    dispatch({
                        type: 'USER_INFO_FAILED',
                        payload: error
                    });
                });
        },
		asyncFailed: () => {
			dispatch({
                type: 'LOGIN_CLEAR',
                payload: null
            });
		},
		login: (aThis) => {
            var formData = new FormData();
            formData.set('username', aThis.usernameInput.current.value);
            formData.set('password', aThis.passwordInput.current.value);
            axios.post("/login", formData)
                .then(function (response) {
                    dispatch({
                        type: 'LOGIN_SUCCESSFUL',
                        payload: response.data
                    });
                    aThis.props.getUserInfo();
                })
                .catch(function (error) {
                    dispatch({
                        type: 'LOGIN_FAILED',
                        payload: error
                    });
                });
        },
        signInCancel: () => {
			dispatch({
                type: 'USER_INFO_REQUEST_CANCEL',
                payload: null
            });        },
        keyDown: (e, aThis) => {
            if (e.keyCode == 13) {
                aThis.props.login(aThis);
            }
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(LoginDialog);
