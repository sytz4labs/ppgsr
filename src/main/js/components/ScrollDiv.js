import React from 'react';

const scrollDiv1 = {display: 'flex', flexDirection: 'column', flexGrow: 1, position: 'relative'};
const scrollDiv2 = {display: 'flex', flexDirection: 'column', position: 'absolute', top: 0, bottom: 0, left: 0, right: 0}
const scrollDiv3 = {display: 'flex', flexDirection: 'column', overflow: 'auto', alignItems: 'baseline'};

export default function ScrollDiv(props) {
    return <div style={scrollDiv1}>
            <div style={scrollDiv2}>
                <div style={scrollDiv3}>
                    {props.children}
                </div>
            </div>
        </div>
}