import axios from "axios";

export function logout() {
    return (dispatch) => {
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
}

export function signIn() {
    return (dispatch) => {
        dispatch({
            type: 'USER_INFO_REQUESTED',
            payload: null
        });
    }
}
