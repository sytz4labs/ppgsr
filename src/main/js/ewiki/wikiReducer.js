import { useReducer } from "react";

export function useWikiReducer() {
    return useReducer(reducer, initialState);
}

const initialState = {
    wiki: null,
    fetching: false,
    fetched: false,
    error: null,
}

function reducer(state, action) {

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

