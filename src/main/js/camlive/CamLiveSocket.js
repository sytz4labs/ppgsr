import React, { useContext, useEffect, useRef } from "react";

const CamLiveSocketContext = React.createContext({ socket: null });

export const useCamLiveSocket = (aListener) => {
    const { socket } = useContext(CamLiveSocketContext);

    useEffect(() => {
        socket.onmessage = e => aListener(JSON.parse(e.data));
    }, [aListener])
}

export default function CamLiveSocket(props) {

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

    return <CamLiveSocketContext.Provider value={{socket: socketR.current}}>
        {props.children}
    </CamLiveSocketContext.Provider>
}
