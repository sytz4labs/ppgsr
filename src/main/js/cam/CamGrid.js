import React from 'react'; 
import { useState, useEffect, useRef } from "react";

import useFetch from "../lib/useFetch";

function CamGrid() {
    const canvas = useRef(null);

    const [ path, setPath ] = useState('')
    const [ time, setTime ] = useState('')
    const { loading, data, error } = useFetch("goSubDir?subDir=" + path, { subDir: '', dirs: [], breadCrumbs: []});

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
            ctx.drawImage(next.img,0,0);
            setTime(next.time);
        }, 100);

        return () => {
            clearInterval(myInterval)
        }
      }, [data.subDir]);

    return (
        <div>
            <table>
                <thead>
                    <tr><td>
                        <button onClick={() => {next = head;forward = true}}>Restart</button>
                        <button onClick={() => forward = false}>Backward</button>
                        <button onClick={() => goPrev()}>Previous</button>
                        <button onClick={() => toggle = !toggle}>Toggle</button>
                        <button onClick={() => goNext()}>Next</button>
                        <button onClick={() => forward = true}>Forward</button>
                        <button onClick={() => {next = tail;forward = false}}>Tailstart</button>
                    </td></tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            <table>
                                <tbody>
                                <tr>
                                    <td id="breadCrumbs" colSpan="2">
                                        {data.breadCrumbs.map((crumb, i) => <span key={i}>/<span style={{padding: '4px', cursor: 'pointer'}} onClick={() => setPath(crumb.key)}>{crumb.value}</span></span>)}
                                    </td>
                                </tr>
                                <tr>
                                    <td id="directories" style={{width: '100px', verticalAlign: 'top'}}>
                                        {data.dirs.map((dir, i) => <span key={i}><span style={{margin: '2px', cursor: 'pointer'}} onClick={() => setPath((path == '' ? '' : path + '/') + dir)}>{dir}</span><br/></span>)}
                                    </td>
                                    <td><div id="myTime">{time}</div>
                                        <canvas id="myCv" height="720" width="960" ref={canvas}></canvas>
                                    </td>
                                </tr>
                            </tbody>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div id="console-container">
                                <div id="console"></div>
                            </div>
                        </td>
                    </tr>
                </tbody>
          </table>
        </div>
    );
}

export default CamGrid;
