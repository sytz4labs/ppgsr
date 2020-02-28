import React, { useContext, useEffect, useReducer, useRef } from "react";

import { useLoginReducer } from './loginReducer'
import axios from "axios";
import { logout, getUserInfo, signIn, submitLogin } from './loginActions'

const LoginDialogContext = React.createContext();

// properties
// loginOptional = boolean

export const useLoginDialog = () => {
    const loginDialogContext = useContext(LoginDialogContext);
    return { state: loginDialogContext.state, signIn: loginDialogContext.signIn, logout: loginDialogContext.logout };
}

function showLogin(context, props) {
    if (context.state.userInfo == null) {
        if (props.loginOptional) {
            return context.state.userInfoRequested
        }
        else {
            return context.state.userInfoCalled;
        }
    }
    else {
        return false;
    }
}

export default function LoginDialog(props) {
    const [loginState, dispatch] = useLoginReducer();
    const usernameInput = useRef();
    const passwordInput = useRef();

    useEffect(() => {
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
    }, []);

    useEffect(() => {
        getUserInfo(dispatch)
    }, [])

    useEffect(() => {
        if (usernameInput.current != null) {
            usernameInput.current.focus();
        }
    });

    const loginDialogContext = {
        state: loginState,
        signIn: () => signIn(dispatch),
        logout: () => logout(dispatch)
        };

    const login = () => {
        var formData = new FormData();
        formData.set('username', usernameInput.current.value);
        formData.set('password', passwordInput.current.value);
        formData.set('remember-me', 'true');
        submitLogin(dispatch, formData);
    };

    const keyDown = (e) => {
        if (e.keyCode == 13) {
            login();
        }
    }

    const signInCancel = () => {
        dispatch({
            type: 'USER_INFO_REQUEST_CANCEL',
            payload: null
        });
    }

    return <LoginDialogContext.Provider value={loginDialogContext}>
        {showLogin(loginDialogContext, props)
            ?   <span>
                    <div style={{position: 'absolute', background: '#fff', zIndex: 200, border: '2px solid #37b', top: '50%', left: '50%', transform: 'translate(-50%, -50%)' /*margin: '180px', padding: '20px', width: '400px', height: '150px'*/}}>
                        <table style={{margin: '25px', width: '250px'}}>
                            <tbody>
                                <tr>
                                    <td colSpan='2'>PPGS</td>
                                </tr>
                                {state.loginFailed > 0 &&
                                    <tr><td colSpan='2' style={{color: 'red'}}>Login failed {state.loginFailed}</td></tr>
                                }
                                <tr>
                                    <td>User:</td>
                                    <td><input style={{padding: '5px', margin: '10px', border: '1px solid #000'}} ref={usernameInput} onKeyDown={(e) => keyDown(e, this)} type='text' name='username'/></td>
                                </tr>
                                <tr>
                                    <td>Password:</td>
                                    <td><input style={{padding: '5px', margin: '10px', border: '1px solid #000'}} ref={passwordInput} onKeyDown={(e) => keyDown(e, this)} type='password' name='password' /></td>
                                </tr>
                                <tr>
                                    <td colSpan='2'><button onClick={() => login()}>Login</button>
                                    {props.loginOptional && <button onClick={() => signInCancel()}>Cancel</button>}
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div style={{position: 'fixed', zIndex: 99, opacity: .3, background: '#aac', top: 0, left: 0, width: '100%', height: '100%'}}></div>
                </span>
            : <div> {props.children} </div>
        }
    </LoginDialogContext.Provider>
}
