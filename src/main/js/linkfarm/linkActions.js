import axios from "axios";

export function getLinks(dispatch, page) {
	dispatch({
		type: 'FETCH_LINKS',
		payload: null
	});
	axios.get("/lf/links?page=" + page)
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

export function postLinks(dispatch, lfRequest) {
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
