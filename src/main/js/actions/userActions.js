export function setUserName(userName) {
    return (dispatch) => {
			dispatch({
				type: 'SET_USER_NAME',
				payload: userName
			});
    }
}