import axios from "axios";

export function signIn(dispatch) {
    dispatch({
        type: 'USER_INFO_REQUESTED',
        payload: null
    });
}

export function submitLogin(dispatch, formData) {
    axios.post("/login", formData)
        .then(function (response) {
            dispatch({
                type: 'LOGIN_SUCCESSFUL',
                payload: response.data
            });
            getUserInfo(dispatch);
        })
        .catch(function (error) {
            dispatch({
                type: 'LOGIN_FAILED',
                payload: error
            });
        });
}

export function getUserInfo(dispatch) {
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
}

export function logout(dispatch) {
    axios.get("/logout")
        .then(function (response) {
            dispatch({
                type: 'LOGIN_CLEAR',
                payload: response.data
            });
            window.location.href = window.location.href;
        })
        .catch(function (error) {
            dispatch({
                type: 'LOGOUT_FAILED',
                payload: error
            });
        });
}
