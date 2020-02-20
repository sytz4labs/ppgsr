export const initialState = {
    userInfo: null,
    loginFailed: 0,
    userInfoCalled: false,
    userInfoRequested: false,
};

export default function reducer(state=initialState, action) {

    switch (action.type) {
        case "USER_INFO_SUCCESSFUL": { // successful call to userInfo indicates we have a login and can get user info
            return {
                userInfo: action.payload === "" ? null : action.payload,
                loginFailed: 0,
                userInfoCalled: true,
                userInfoRequested: false,
            }
        }
        case "USER_INFO_FAILED": {
            return {...state, userInfoCalled: true }
        }
        case "USER_INFO_REQUESTED": {
            return {...state, userInfoRequested: true }
        }
        case "USER_INFO_REQUEST_CANCEL": {
            return {...state, userInfoRequested: false }
        }
        case "LOGIN_CLEAR": { // an API call resulted in a 401, everybody out of the pool.
            return {...state, userInfo: null, loginFailed: 0};
        }
        case "LOGIN_SUCCESSFUL": { // is this the same as user_info_successful?
            return {...state, loginFailed: 0}
        }
        case "LOGIN_FAILED": { // is this the same as user_info_successful?
            return {...state, loginFailed: state.loginFailed + 1}
        }
    }

    return state
}

