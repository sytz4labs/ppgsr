import axios from "axios";

export function postWiki(url, data) {
    return (dispatch) => {
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
				})
				.catch(function (error) {
					dispatch({
						type: 'FETCH_WIKI_REJECTED',
						payload: error
					});
				});
    }
}