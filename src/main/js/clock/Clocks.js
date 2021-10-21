import React, { useEffect, useState } from 'react';
import Spinner from 'react-bootstrap/Spinner';
import Button from 'react-bootstrap/Button'
import Modal from 'react-modal';

import useFetchPost from '../lib/useFetchPost'
import ZoneClock from './ZoneClock';
import ZoneModal from './ZoneModel';
import { useInterval } from '../lib/useInterval';

Modal.setAppElement('#react');

export default function Clocks(props) {

    const clockSetId = props.match.params.clockSetId;
    const [ clockReq, setClockReq ] = useState(null)
    const clockReqStatus = useFetchPost('/clock/api/clockReq', clockReq, null);

    const [ zoneEdit, setZoneEdit ] = useState(null);

    useEffect(() => {
        setClockReq({cmd: 'get', clockSetId: clockSetId})
    }, []);

    useInterval(() => {
        setClockReq({cmd: 'get', clockSetId: clockSetId})
    }, 60000);

    return (
        <div style={{height: '100%', display: 'flex', flexDirection: 'column', flexGrow: '1' }}>
            {clockReqStatus.data == null
                ? 'None found'
                : <>
                    <div style={{display: 'flex', flexDirection: 'row', flexWrap: 'wrap' }} >
                        {clockReqStatus.data.clocks.map((clock, i) => 
                            <ZoneClock key={i} clock={clock} onClick={() => setZoneEdit({zone: '', index: i})} />
                        )}
                    </div>
                    <div style={{display: 'flex', flexDirection: 'row', padding: '10px' }}>
                        <Button variant='light' size='medium' onClick={() => setClockReq({cmd: 'add', clockSetId: clockSetId, zone: 'US/Central'})}>Add</Button>      
                    </div>
                </> }
            <Modal isOpen={zoneEdit != null} onRequestClose={() => setZoneEdit(null)} >
                <div className='flexCol flexAbsolute'>
                    <div className='flexRow' style={{padding: '5px'}}>
                        <Button onClick={() => setZoneEdit(null)}>Close</Button>
                    </div>
                    <div className='flexRow' style={{height: '100%'}}>
                        <ZoneModal selectZoneFunc={(zone) => {
                            setClockReq({cmd: 'set', clockSetId: clockSetId, index: zoneEdit.index, zone});
                            setZoneEdit(null)}} />
                    </div>
                </div>
            </Modal>
        </div>
    )
}