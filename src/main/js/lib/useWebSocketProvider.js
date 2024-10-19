import React, { useContext, useEffect, useRef } from "react";

const WebSocketContext = React.createContext({ socket: null });

function noop(f, e) {
    console.log(f + ' ' + JSON.stringify(e))
}

export const useWebSocket = (aListener, aError = (e) => noop('WS error ',e), aOpen = (e) => noop('WS open ',e), aClose = (e) => noop('WS close ',e)) => {
    const { socket } = useContext(WebSocketContext);

    useEffect(() => {
        socket.onmessage = e => aListener(JSON.parse(e.data));
        socket.onerror = e => aError(e);
        socket.onopen = e => aOpen(e);
        socket.onclose = e => aClose(e);
    }, [aListener])

    return socket
}

export default function WebSocketProvider(props) {

    const schema = window.location.protocol == "https:" ? "wss:" : "ws:";
    const url = schema + window.location.host
    const socketR = useRef(new WebSocket(url + props.uri));

    useEffect(() => {

        return () => {
            if (socketR && socketR.current) {
                socketR.current.close()
                console.log('cls close');
            }
        }
    }, []);

    return <WebSocketContext.Provider value={{socket: socketR.current}}>
        {props.children}
    </WebSocketContext.Provider>
}
