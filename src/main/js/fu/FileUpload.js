import React, { Fragment, useState, useEffect } from 'react';

import { useFuReducer } from './fuReducer'
import { getFiles, postFile } from './fuActions';

export default function FileUpload() {
    const [file, setFile] = useState('');
    const [ fuR, fuDispatch ] = useFuReducer();

    useEffect(() => {
        getFiles(fuDispatch);
    }, []);

    const onChange = e => {
        setFile(e.target.files[0]);
    };

    const onSubmit = e => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('file', file);
        postFile(fuDispatch, formData)
    };

    return (
        <Fragment>
            <form onSubmit={onSubmit}>
                <div>
                    <input type='file' onChange={onChange} />
                </div>

                <div>Progress {fuR.progress}</div>

                <input type='submit' value='Upload' />
            </form>
            {fuR.fu != null  && <table>
                <thead>
                    <tr>
                        <th>File</th>
                        <th>Bytes</th>
                    </tr>
                </thead>
                <tbody>
                    {fuR.fu.info.map((file, i) =>
                         <tr key={i}>
                             <td>{file.name}</td>
                             <td style={{textAlign: 'right'}}>{file.length}</td></tr>)}
                </tbody>
            </table>}
        </Fragment>
    );
}