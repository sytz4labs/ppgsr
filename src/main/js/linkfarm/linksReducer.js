import { useReducer } from "react";

export function useLinksReducer() {
    return useReducer(reducer, initialState);
}

const initialState = {
    links: null,
    fetching: false,
    fetched: false,
    error: null,
}

function reducer(state, action) {

    switch (action.type) {
        case "FETCH_LINKS_PENDING": {
            return {...state, fetching: true}
        }
        case "FETCH_LINKS_REJECTED": {
            return {...state, fetching: false, error: action.payload}
        }
        case "FETCH_LINKS_FULFILLED": {
            return {
                ...state,
                fetching: false,
                fetched: true,
                links: action.payload,
            }
        }
    }

    return state
}
