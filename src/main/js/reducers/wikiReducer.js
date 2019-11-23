export default function reducer(state={
        wiki: null,
        fetching: false,
        fetched: false,
        error: null,
    }, action) {

    switch (action.type) {
        case "FETCH_WIKI_PENDING": {
            return {...state, fetching: true}
        }
        case "FETCH_WIKI_REJECTED": {
            return {...state, fetching: false, error: action.payload}
        }
        case "FETCH_WIKI_FULFILLED": {
            return {
                ...state,
                fetching: false,
                fetched: true,
                wiki: action.payload,
            }
        }
    }

    return state
}
