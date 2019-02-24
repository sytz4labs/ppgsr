import axios from "axios";
import { setUserName } from '../actions/userActions'

export function getLinks(page) {
    return (dispatch) => {
			dispatch({
				type: 'FETCH_LINKS',
				payload: null
			});
			axios.get("/lf/links?page=" + page)
				.then(function (response) {
					dispatch(setUserName(response.data.user))
					dispatch({
						type: 'FETCH_LINKS_FULFILLED',
						payload: response.data
					});
				})
				.catch(function (error) {
					dispatch({
						type: 'FETCH_LINKS_REJECTED',
						payload: error
					});
				});
    }
}

export function postLinks(lfRequest) {
    return (dispatch) => {
		axios.post('/lf/update', lfRequest)
			.then(function (response) {
				dispatch({
					type: 'FETCH_LINKS_FULFILLED',
					payload: response.data
				});
			})
			.catch(function (error) {
				dispatch({
					type: 'FETCH_LINKS_REJECTED',
					payload: error
				});
			});
	}
}
