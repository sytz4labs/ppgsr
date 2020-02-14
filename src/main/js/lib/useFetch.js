import { useState, useEffect } from "react";
import axios from "axios";

const useFetch = (url, initialValue) => {
    const [data, setData] = useState(initialValue);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('')
    useEffect(() => {
        setLoading(true);
        axios.get(url)
            .then(function (response) {
                setData(response.data);
                setLoading(false);
             })
            .catch(function (error) {
                setError(error.message);
                setLoading(false);
            });
    }, [url]);
    return { loading, data, error };
};

export default useFetch;