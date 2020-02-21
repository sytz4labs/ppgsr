import axios from "axios";

export const budgetActs = {
	getTransactions: (dispatch, url) => {
		dispatch({
			type: 'FETCH_TRANS',
			payload: null
		});
		axios.get("/budget/" + url)
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
	},
	postBudget: (dispatch, url, data) => {
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
