import { useState, useEffect } from "react";
import axios from "axios";

const useFetchPost = (url, req, initialValue) => {
    const [data, setData] = useState(initialValue);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('')
    useEffect(() => {
        setLoading(true);
        axios.post(url, req)
            .then(function (response) {
                setData(response.data);
                setLoading(false);
             })
            .catch(function (error) {
                setError(error.message);
                setLoading(false);
            });
        return () => {console.log('fetchPost unload')}
    }, [url, req]);
    return { loading, data, error };
};

export default useFetchPost;