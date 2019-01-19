import axios from "axios";
import { setUserName } from '../../actions/userActions'

export function getTransactions(url) {
    return (dispatch) => {
			dispatch({
				type: 'FETCH_TRANS',
				payload: null
			});
			axios.get("/budget/" + url)
				.then(function (response) {
					dispatch(setUserName(response.data.user))
					dispatch({
						type: 'FETCH_TRANS_FULFILLED',
						payload: response.data
					});
				})
				.catch(function (error) {
					dispatch({
						type: 'FETCH_TRANS_REJECTED',
						payload: error
					});
				});
    }
}

export function postBudget(url, data) {
    return (dispatch) => {
			dispatch({
				type: 'FETCH_TRANS',
				payload: null
			});
			axios.post("/budget/" + url, data)
				.then(function (response) {
					dispatch({
						type: 'FETCH_TRANS_FULFILLED',
						payload: response.data
					});
				})
				.catch(function (error) {
					dispatch({
						type: 'FETCH_TRANS_REJECTED',
						payload: error
					});
				});
    }
}
