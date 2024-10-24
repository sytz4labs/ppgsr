import React, { useEffect, useRef, useState } from 'react';
import { useCamLiveSocket } from './CamLiveSocket';

export default function CamLiveGrid() {

    const canvas = useRef(null);
    const [ d, setD ] = useState({image: "ka"})
    useCamLiveSocket((m) => {
        if (m.image == "ka") {
            setD({...d, timek: m.time})
        }
        else {
            setD({...d, timei: m.time, image: m.image})
        }
    });
    useEffect(() => {

        if (canvas.current != null && d.image != "ka") {

            var img = new Image();
            img.src = d.image;
            img.onload = function () {
                var cv = canvas.current;
                var ctx = cv.getContext('2d');
                ctx.drawImage(img, 0, 0);
            }
        }
    }, [d])

    return <div>
        <div>{d.timek} keepAlive</div>
        <div>{d.timei} image</div>
        <canvas id="myCv" height="720" width="960" ref={canvas}></canvas>
    </div>
}