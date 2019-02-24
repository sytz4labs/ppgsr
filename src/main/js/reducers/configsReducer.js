export default function reducer(state={
        configs: [],
        fetching: false,
        fetched: false,
        error: null,
    }, action) {

    switch (action.type) {
        case "FETCH_CONFIGS_PENDING": {
            return {...state, fetching: true}
        }
        case "FETCH_CONFIGS_REJECTED": {
            return {...state, fetching: false, error: action.payload}
        }
        case "FETCH_CONFIGS_FULFILLED": {
            return {
                ...state,
                fetching: false,
                fetched: true,
                configs: action.payload,
            }
        }
    }

    return state
}
