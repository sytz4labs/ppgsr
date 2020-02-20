import axios from "axios";

export function getConfigs(dispatch) {
	dispatch({
		type: 'FETCH_CONFIGS',
		payload: null
	});
	axios.get("/config/configs")
		.then(function (response) {
			dispatch({
				type: 'FETCH_CONFIGS_FULFILLED',
				payload: response.data
			});
		})
		.catch(function (error) {
			dispatch({
				type: 'FETCH_CONFIGS_REJECTED',
				payload: error
			});
		});
}

export function saveConfig(dispatch, id, type, value) {
	dispatch({
		type: 'FETCH_CONFIGS',
		payload: null
	});
	axios.post('/config/save', { 'id': id, 'type': type, 'value': value})
		.then(function (response) {
			dispatch({
				type: 'FETCH_CONFIGS_FULFILLED',
				payload: response.data
			});
		})
		.catch(function (error) {
			dispatch({
				type: 'FETCH_CONFIGS_REJECTED',
				payload: error
			});
		});
}
