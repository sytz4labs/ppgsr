import { useReducer } from "react";

export function useFuReducer() {
    return useReducer(reducer, initialState);
}

const initialState = {
    fu: null,
    fetching: false,
    fetched: false,
    progress: 0,
    error: null,
}

function reducer(state, action) {
console.log(action.type)
    switch (action.type) {
        case 'FETCH_FU_PROGRESS': {
            return {...state, progress: action.payload}
        }
        case "FETCH_FU_PENDING": {
            return {...state, fetching: true}
        }
        case "FETCH_FU_REJECTED": {
            return {...state, fetching: false, error: action.payload}
        }
        case "FETCH_FU_FULFILLED": {
            return {
                ...state,
                fetching: false,
                fetched: true,
                fu: action.payload,
            }
        }
    }

    return state
}
