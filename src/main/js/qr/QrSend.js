import React, { useState } from 'react';
import { useWebSocket } from '../lib/useWebSocketProvider';
import { useRef } from 'react';
import { useEffect } from 'react';

export default function QrSend() {

    const canvas = useRef(null);
	const [ d, setD ] = useState({cmd: 'blank', image: null})

	const sock = useWebSocket(e => {
		setD(e)
	})

	useEffect(() => {

        if (canvas.current != null && d.image != null) {
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
		<button onClick={() => {sock.send('start') }}>Go</button>{d.cmd}<br/>
		<canvas height="900" width="900" ref={canvas}></canvas>
	</div>
}
