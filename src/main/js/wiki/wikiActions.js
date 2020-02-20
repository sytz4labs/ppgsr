import axios from "axios";

export function postWiki(dispatch, url, data, affirmFunc) {
	dispatch({
		type: 'FETCH_WIKI',
		payload: null
	});
	axios.post("/wiki/" + url, data)
		.then(function (response) {
			dispatch({
				type: 'FETCH_WIKI_FULFILLED',
				payload: response.data
			});
			affirmFunc(response.data.affirm0, response.data.affirm1)
		})
		.catch(function (error) {
			dispatch({
				type: 'FETCH_WIKI_REJECTED',
				payload: error
			});
		});
}