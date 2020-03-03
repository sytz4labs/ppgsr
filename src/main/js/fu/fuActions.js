import axios from "axios";

export function getFiles(dispatch) {
	dispatch({
		type: 'FETCH_FU',
		payload: null
	});
	axios.get("/fu/files")
		.then(function (response) {
			dispatch({
				type: 'FETCH_FU_FULFILLED',
				payload: response.data
			});
		})
		.catch(function (error) {
			dispatch({
				type: 'FETCH_FU_REJECTED',
				payload: error
			});
		});
}

export function postFile(dispatch, formData) {
	axios.post('/fu/go', formData, {
		headers: {
			'Content-Type': 'multipart/form-data'
		},
		onUploadProgress: progressEvent => {
			dispatch({
				type: 'FETCH_FU_PROGRESS',
				payload: Math.round((progressEvent.loaded * 100) / progressEvent.total)
			});
		}
	}).then(function (response) {
			dispatch({
				type: 'FETCH_FU_FULFILLED',
				payload: response.data
			});
		})
		.catch(function (error) {
			dispatch({
				type: 'FETCH_FU_REJECTED',
				payload: error
			});
		});
}
