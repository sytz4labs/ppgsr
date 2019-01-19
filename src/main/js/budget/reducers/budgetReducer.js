export default function reducer(state={
        transactions: { trans: [], transSpecs: [], transHist: [] },
        fetching: false,
        fetched: false,
        error: null,
    }, action) {

    switch (action.type) {
        case "FETCH_TRANS_PENDING": {
            return {...state, fetching: true}
        }
        case "FETCH_TRANS_REJECTED": {
            return {...state, fetching: false, error: action.payload}
        }
        case "FETCH_TRANS_FULFILLED": {
            return {
                ...state,
                fetching: false,
                fetched: true,
                transactions: { trans: action.payload.trans == null ? state.transactions.trans : action.payload.trans,
                                transSpecs: action.payload.transSpecs == null ? state.transactions.transSpecs : action.payload.transSpecs,
                                transHist: action.payload.transHist == null ? state.transactions.transHist : action.payload.transHist }
            }
        }
    }

    return state
}
