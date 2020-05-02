import React from 'react';
import { useState, useEffect, useRef } from "react";

import useFetch from "../lib/useFetch";

function CamGrid() {
    const canvas = useRef(null);

    const [path, setPath] = useState('')
    const [time, setTime] = useState('')
    const { loading, data, error } = useFetch("goSubDir?subDir=" + path, { subDir: '', breadCrumbs: [] });

    useEffect(() => {
        CamWs.initialize(path);
    }, [data.subDir]);

    useEffect(() => {
        var myInterval = setInterval(() => {
            if (canvas.current == null) {
                return;
            }
            if (head == null) {
                return;
            }

            if (next == null) {
                next = head;
            }
            else {
                if (toggle) {
                    if (forward) {
                        goNext();
                    }
                    else {
                        goPrev();
                    }
                }
            }

            var cv = canvas.current;
            var ctx = cv.getContext('2d');
            ctx.drawImage(next.img, 0, 0);
            setTime(next.time);
        }, 100);

        return () => {
            clearInterval(myInterval)
        }
    }, [data.subDir]);

    return (
        <div style={{ display: 'flex', flexDirection: 'column' }}>
            <div style={{ display: 'flex', flexDirection: 'row' }}>

                <button onClick={() => { next = head; forward = true }}>Restart</button>
                <button onClick={() => forward = false}>Backward</button>
                <button onClick={() => goPrev()}>Previous</button>
                <button onClick={() => toggle = !toggle}>Toggle</button>
                <button onClick={() => goNext()}>Next</button>
                <button onClick={() => forward = true}>Forward</button>
                <button onClick={() => { next = tail; forward = false }}>Tailstart</button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'row' }}>
                {data.breadCrumbs.map((crumb, i1) => <span key={i1}>/<span style={{ padding: '4px', cursor: 'pointer' }} onClick={() => setPath(crumb.path)}>{crumb.name}</span></span>)}
            </div>
            <div style={{ display: 'flex', flexDirection: 'row' }}>
                {data.breadCrumbs.map((crumb, i2) => <div key={i2} style={{ display: 'flex', flexDirection: 'column' }}>
                    {crumb.dirs.map((dir, i3) => <div key={i3} style={{backgroundColor: (data.subDir.startsWith(crumb.path == '' ? dir : crumb.path + "/" + dir) ? 'yellow' : 'white')}}>
                        <span style={{margin: '2px', cursor: 'pointer'}} onClick={() => setPath((crumb.path == '' ? '' : crumb.path + '/') + dir)}>{dir}</span>
                        </div>)}
                </div>)}
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <div id="myTime">{time}</div>
                        <canvas id="myCv" height="720" width="960" ref={canvas}></canvas>
                </div>
            </div>
            <div style={{ display: 'flex', flexDirection: 'row' }} id="console-container">
                    <div id="console"></div>
                </div>
        </div>
    );
}

export default CamGrid;
