import { useState, useEffect } from "react";
import axios from "axios";

const useFetchPost = (url, req, initialValue) => {
    const [data, setData] = useState(initialValue);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('')
    useEffect(() => {
        if (req != null) {
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
            }
    }, [url, req]);
    return { loading, data, error };
};

export default useFetchPost;