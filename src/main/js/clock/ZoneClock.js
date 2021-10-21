import React from 'react';
import Clock from 'react-clock';


// props
// index is number in list
// selectZoneFunc = (index, newZone)
export default function ZoneClock(props) {

    const { clock, onClick } = props;

    return (
        <div style={{padding: '10px'}} onClick={onClick}>
            <Clock value={clock.timeShort} renderSecondHand={false} />
            <div style={{color: 'white', cursor: 'pointer'}} >
                {clock.zone}<br/>
                {clock.timeLong}
            </div>
        </div>
    )
}

